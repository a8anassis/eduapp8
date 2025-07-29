package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long>,
        JpaSpecificationExecutor<Teacher> {

    // UserId by convention looks for the @Id field of User entity since
    // we have a @OneToOne relation
    Optional<Teacher> findByUserId(Long id);

    Optional<Teacher> findByUuid(String uuid);
}
