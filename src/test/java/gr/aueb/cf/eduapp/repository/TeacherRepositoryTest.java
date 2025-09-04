package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import lombok.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TeacherRepositoryTest {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    @Autowired
    public TeacherRepositoryTest(TeacherRepository teacherRepository, UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
    }

    private Teacher existingTeacher = new Teacher();
    private User existingUser = new User();

    @BeforeEach
    public void setup() {
        createDummyData();
    }

    @Test
    void persistAndGetTeacher() {
        // Create User first
        User user = new User();
        user.setFirstname("Λαμπρινή");
        user.setLastname("Παπαδοπούλου");
        user.setUsername("lamp_" + UUID.randomUUID());
        user.setPassword("secret");
        user.setVat("1078563411");
        userRepository.save(user);

        // Create Teacher and associate User
        Teacher teacher = new Teacher();
        teacher.setUuid(UUID.randomUUID().toString());
        teacher.setIsActive(true);
        teacher.setUser(user);
        teacherRepository.save(teacher);

        List<Teacher> teachers = teacherRepository.findByUserLastname("Παπαδοπούλου");
        assertEquals(1, teachers.size());   // JUnit
//        assertThat(teachers).hasSize(1);  // AssertJ
    }

    @Test
    void updateTeacher() {
        // Retrieve teacher from DB
        Teacher teacherToUpdate = teacherRepository.findById(existingTeacher.getId()).orElseThrow();

        // Update fields in the user
        User user = teacherToUpdate.getUser();
        user.setFatherName("Κώστας");
        user.setVat("144445679");

        // Save teacher (cascades to user)
        teacherRepository.save(teacherToUpdate);

        // Reload to verify
        Teacher updatedTeacher = teacherRepository.findById(existingTeacher.getId()).orElseThrow();
        assertEquals("Κώστας", updatedTeacher.getUser().getFatherName());
        assertEquals("144445679", updatedTeacher.getUser().getVat());

//        assertThat(updatedTeacher.getUser().getFatherName()).isEqualTo("Κώστας");
//        assertThat(updatedTeacher.getUser().getVat()).isEqualTo("144445679");
    }

    @Test
    void deleteTeacher() {
        teacherRepository.deleteById(existingTeacher.getId());
        Teacher deletedTeacher = teacherRepository.findById(existingTeacher.getId()).orElse(null);
        assertNull(deletedTeacher);                 // JUnit
//        assertThat(deletedTeacher).isNull();      // AssertJ
    }

    @Test
    void getTeacherByIdPositive() {
        Teacher teacher = teacherRepository.findById(existingTeacher.getId()).orElse(null);
        assertNotNull(teacher);
        assertEquals("Γιαννούτσου", teacher.getUser().getLastname());
    }

    @Test
    void getTeacherByIdNegative() {
        Teacher teacher = teacherRepository.findById(999L).orElse(null);
        assertNull(teacher);
    }

    @Test
    void getTeacherByLastname() {
        List<Teacher> teachers = teacherRepository.findByUserLastname("Γιαννούτσου");
        assertEquals(1, teachers.size());
    }

    @Test
    void testFindByUserId() {
        Optional<Teacher> teacherOpt = teacherRepository.findByUserId(existingUser.getId());
        assertTrue(teacherOpt.isPresent());
        assertEquals(existingTeacher.getId(), teacherOpt.get().getId());
    }

    @Test
    void testFindByUuid() {
        Optional<Teacher> teacherOpt = teacherRepository.findByUuid(existingTeacher.getUuid());
        assertTrue(teacherOpt.isPresent());
        assertEquals(existingUser.getId(), teacherOpt.get().getUser().getId());
    }


    private void createDummyData() {
        // Create a User for the teacher
        existingUser = new User();
        existingUser.setFirstname("Άννα");
        existingUser.setLastname("Γιαννούτσου");
        existingUser.setUsername("anna_" + UUID.randomUUID());
        existingUser.setPassword("secret");
        existingUser.setFatherName("Νίκος");
        existingUser.setVat("144445678");
        existingUser.setIsActive(true);
        userRepository.save(existingUser);

        // Create initial Teacher
        existingTeacher = new Teacher();
        existingTeacher.setUuid(UUID.randomUUID().toString());
        existingTeacher.setIsActive(true);
        existingTeacher.setUser(existingUser);  // link the user

        teacherRepository.save(existingTeacher);
    }
}

