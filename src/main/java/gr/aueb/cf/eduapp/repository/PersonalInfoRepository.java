package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, Long>,
        JpaSpecificationExecutor<PersonalInfo> {

    Optional<PersonalInfo> findByAmka(String amka);
    Optional<PersonalInfo> findByIdentityNumber(String identityNumber);
}
