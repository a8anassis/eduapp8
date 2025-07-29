package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByVat(String vat);
    Optional<User> findByUsername(String username);
}
