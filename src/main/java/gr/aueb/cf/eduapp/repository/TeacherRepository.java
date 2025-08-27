package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long>,
        JpaSpecificationExecutor<Teacher> {

    Optional<Teacher> findByUserId(Long id);

    Optional<Teacher> findByUuid(String uuid);

    List<Teacher> findByUserLastname(String lastname);
}
