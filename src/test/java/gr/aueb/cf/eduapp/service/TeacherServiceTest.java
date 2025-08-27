package gr.aueb.cf.eduapp.service;

import gr.aueb.cf.eduapp.core.enums.GenderType;
import gr.aueb.cf.eduapp.core.enums.Role;
import gr.aueb.cf.eduapp.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.eduapp.dto.*;
import gr.aueb.cf.eduapp.mapper.Mapper;
import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.model.User;
import gr.aueb.cf.eduapp.repository.PersonalInfoRepository;
import gr.aueb.cf.eduapp.repository.TeacherRepository;
import gr.aueb.cf.eduapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TeacherServiceTest {

    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final Mapper mapper;

    @Autowired
    public TeacherServiceTest(TeacherService teacherService, TeacherRepository teacherRepository,
                              UserRepository userRepository, PersonalInfoRepository personalInfoRepository, Mapper mapper) {
        this.teacherService = teacherService;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.mapper = mapper;
    }

    private TeacherInsertDTO teacherInsertDTO;
    private Teacher existingTeacher = new Teacher();

    @BeforeEach
    void setup() {
        createDummyData();
    }

    private void createDummyData() {
        User existingUser = new User();
        existingUser.setFirstname("Άννα");
        existingUser.setLastname("Γιαννούτσου");
        existingUser.setUsername("anna_" + UUID.randomUUID());
        existingUser.setPassword("secret");  // encode if needed
        existingUser.setFatherName("Νίκος");
        existingUser.setVat("11111111111");
        existingUser.setIsActive(true);
        userRepository.save(existingUser);

        PersonalInfo existingPersonalInfo = new PersonalInfo();
        existingPersonalInfo.setAmka("1111111111");
        existingPersonalInfo.setIdentityNumber("A1111111");
        existingPersonalInfo.setPlaceOfBirth("Athens");
        existingPersonalInfo.setMunicipalityOfRegistration("Kypseli");
        personalInfoRepository.save(existingPersonalInfo);

        existingTeacher = new Teacher();
        existingTeacher.setUuid(UUID.randomUUID().toString());
        existingTeacher.setIsActive(true);
        existingTeacher.setUser(existingUser);              // link the user
        existingTeacher.setPersonalInfo(existingPersonalInfo); // link personal info

        teacherRepository.save(existingTeacher);
    }

    @Test
    void saveTeacher_successWithFile() throws Exception {
        UserInsertDTO userInsertDTO = UserInsertDTO.builder()
                .firstname("Αθανάσιος").lastname("Ανδρούτσος")
                .username("athanassios" + UUID.randomUUID() + "@aueb.gr")
                .password("C0d1ngF@").vat("222222222222")
                .fatherName("Κωνσταντίνος").fatherLastname("Ανδρούτσος")
                .motherName("Νίκη").motherLastname("Σαμαρά")
                .dateOfBirth(LocalDate.of(1990, 3, 2))
                .gender(GenderType.MALE).role(Role.TEACHER)
                .build();
        PersonalInfoInsertDTO personalInfoInsertDTO = PersonalInfoInsertDTO.builder()
                .amka("22222222222").identityNumber("D1234567")
                .placeOfBirth("Athens").municipalityOfRegistration("Kypseli")
                .build();
        teacherInsertDTO = TeacherInsertDTO.builder()
                .isActive(true).userInsertDTO(userInsertDTO)
                .personalInfoInsertDTO(personalInfoInsertDTO)
                .build();
        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);
        MultipartFile normalFile = new MockMultipartFile(
                "file",                 // form field name
                "test.pdf",             // original filename
                "application/pdf",      // content type
                new byte[100]           // file content
        );
        TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.saveTeacher(teacherInsertDTO, normalFile);
        assertNotNull(teacherReadOnlyDTO);
        Teacher foundTeacher = teacherRepository.findByUserLastname("Ανδρούτσος").get(0);
        assertEquals(teacherReadOnlyDTO.userReadOnlyDTO().firstname(), foundTeacher.getUser().getFirstname());
        assertNotNull(foundTeacher.getPersonalInfo().getAmkaFile());
    }

    @Test
    void saveTeacher_successWithoutFile() throws Exception {
        UserInsertDTO userInsertDTO = UserInsertDTO.builder()
                .firstname("Αθανάσιος").lastname("Ανδρούτσος")
                .username("than" + UUID.randomUUID() + "@aueb.gr")
                .password("C0d1ngF@").vat("333333333333")
                .fatherName("Κωνσταντίνος").fatherLastname("Ανδρούτσος")
                .motherName("Νίκη").motherLastname("Σαμαρά")
                .dateOfBirth(LocalDate.of(1990, 3, 2)).gender(GenderType.MALE)
                .role(Role.TEACHER)
                .build();
        PersonalInfoInsertDTO personalInfoInsertDTO = PersonalInfoInsertDTO.builder()
                .amka("33333333333").identityNumber("K1234567")
                .placeOfBirth("Athens").municipalityOfRegistration("Kypseli")
                .build();
        teacherInsertDTO = TeacherInsertDTO.builder()
                .isActive(true).userInsertDTO(userInsertDTO)
                .personalInfoInsertDTO(personalInfoInsertDTO)
                .build();
        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);
        MultipartFile emptyFile = new MockMultipartFile(
                "file",                 // form field name
                "test.pdf",             // original filename
                "application/pdf",      // content type
                new byte[0]           // file content
        );
        TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.saveTeacher(teacherInsertDTO, emptyFile);
        assertNotNull(teacherReadOnlyDTO);
        Teacher foundTeacher = teacherRepository.findByUserLastname("Ανδρούτσος").get(0);
        assertEquals(teacherReadOnlyDTO.userReadOnlyDTO().firstname(), foundTeacher.getUser().getFirstname());
        assertNull(foundTeacher.getPersonalInfo().getAmkaFile());
    }

    @Test
    void saveTeacher_duplicateVat_throwsException() {
        // Create UserInsertDTO
        UserInsertDTO userInsertDTO = UserInsertDTO.builder()
                .firstname("Αθανάσιος").lastname("Ανδρούτσος")
                .username("thanos" + UUID.randomUUID() + "@aueb.gr")
                .password("C0d1ngF@").vat("11111111111")                    // same
                .fatherName("Κωνσταντίνος").fatherLastname("Ανδρούτσος")
                .motherName("Νίκη")
                .motherLastname("Σαμαρά")
                .dateOfBirth(LocalDate.of(1990, 3, 2))
                .gender(GenderType.MALE)
                .role(Role.TEACHER)
                .build();
        PersonalInfoInsertDTO personalInfoInsertDTO = PersonalInfoInsertDTO.builder()
                .amka("44444444444").identityNumber("W1200567")
                .placeOfBirth("Athens").municipalityOfRegistration("Kypseli")
                .build();
        teacherInsertDTO = TeacherInsertDTO.builder()
                .isActive(true).userInsertDTO(userInsertDTO)
                .personalInfoInsertDTO(personalInfoInsertDTO)
                .build();
        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);
        MultipartFile emptyFile = new MockMultipartFile(
                "file",                 // form field name
                "test.pdf",             // original filename
                "application/pdf",      // content type
                new byte[0]           // file content
        );
        assertThrows(AppObjectAlreadyExists.class,
                () -> teacherService.saveTeacher(teacherInsertDTO, emptyFile));
    }

    @Test
    void updateTeacher_successWithoutFile() throws Exception {

        Teacher before = teacherRepository.findById(existingTeacher.getId()).orElseThrow();

        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .id(before.getUser().getId())
                .firstname("Λαμπρινή")   // <-- changed
                .lastname(before.getUser().getLastname())
                .username(before.getUser().getUsername())
                .password(before.getUser().getPassword()) // keep same
                .vat(before.getUser().getVat())           // unchanged -> avoids duplicate VAT check
                .fatherName(before.getUser().getFatherName())
                .fatherLastname(before.getUser().getFatherLastname())
                .motherName(before.getUser().getMotherName())
                .motherLastname(before.getUser().getMotherLastname())
                .dateOfBirth(before.getUser().getDateOfBirth())
                .gender(before.getUser().getGender())
                .role(before.getUser().getRole())
                .build();
        PersonalInfoUpdateDTO personalInfoUpdateDTO = PersonalInfoUpdateDTO.builder()
                .id(before.getPersonalInfo().getId())
                .amka(before.getPersonalInfo().getAmka())
                .identityNumber("B7654321") // <-- changed (ensure this value doesn't already exist)
                .placeOfBirth(before.getPersonalInfo().getPlaceOfBirth())
                .municipalityOfRegistration(before.getPersonalInfo().getMunicipalityOfRegistration())
                .build();
        TeacherUpdateDTO updateDTO = TeacherUpdateDTO.builder()
                .id(before.getId()).isActive(before.getIsActive())
                .uuid(before.getUuid()).userUpdateDTO(userUpdateDTO)
                .personalInfoUpdateDTO(personalInfoUpdateDTO)
                .build();

        // Act: no file -> AMKA file URL is null
        TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.updateTeacher(updateDTO, null);

        // Assert: reload and verify only the intended fields changed
        Teacher after = teacherRepository.findById(before.getId()).orElseThrow();

        // TeacherReadOnlyDTO corresponds to the same teacher id
        assertNotNull(teacherReadOnlyDTO);
        assertEquals(before.getId(), teacherReadOnlyDTO.id());

        assertEquals("Λαμπρινή", after.getUser().getFirstname());
        assertEquals("B7654321", after.getPersonalInfo().getIdentityNumber());

        // Assert unchanged values remain the same
        assertEquals(before.getUser().getVat(), after.getUser().getVat());
        assertEquals(before.getUuid(), after.getUuid());

        // file should be unchanged because we passed null (no new file)
        assertEquals(before.getPersonalInfo().getAmkaFile(),
                after.getPersonalInfo().getAmkaFile());
    }
}
