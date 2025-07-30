package gr.aueb.cf.eduapp.mapper;

import gr.aueb.cf.eduapp.dto.*;
import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public TeacherReadOnlyDTO mapToTeacherReadOnlyDTO(Teacher teacher) {
        TeacherReadOnlyDTO teacherReadOnlyDTO = new TeacherReadOnlyDTO();
        teacherReadOnlyDTO.setId(teacher.getId());
        teacherReadOnlyDTO.setUuid(teacher.getUuid());
        teacherReadOnlyDTO.setIsActive(teacher.getIsActive());

        // Map User to UserReadOnlyDTO
        UserReadOnlyDTO userDTO = new UserReadOnlyDTO();
        userDTO.setFirstname(teacher.getUser().getFirstname());
        userDTO.setLastname(teacher.getUser().getLastname());
        userDTO.setVat(teacher.getUser().getVat());
        teacherReadOnlyDTO.setUserReadOnlyDTO(userDTO);

        // Map PersonalInfo to PersonalInfoReadOnlyDTO
        PersonalInfoReadOnlyDTO personalInfoDTO = new PersonalInfoReadOnlyDTO();
        personalInfoDTO.setAmka(teacher.getPersonalInfo().getAmka());
        personalInfoDTO.setIdentityNumber(teacher.getPersonalInfo().getIdentityNumber());
        teacherReadOnlyDTO.setPersonalInfoReadOnlyDTO(personalInfoDTO);

        return teacherReadOnlyDTO;
    }


    public Teacher mapToTeacherEntity(TeacherInsertDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setIsActive(dto.isActive());

        // Map fields from UserDTO
        UserInsertDTO userDTO = dto.userInsertDTO();
        User user = new User();
        user.setFirstname(userDTO.firstname());
        user.setLastname(userDTO.lastname());
        user.setUsername(userDTO.username());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setVat(userDTO.vat());
        user.setFatherName(userDTO.fatherName());
        user.setFatherLastname(userDTO.fatherLastname());
        user.setMotherName(userDTO.motherName());
        user.setMotherLastname(userDTO.motherLastname());
        user.setDateOfBirth(userDTO.dateOfBirth());
        user.setGender(userDTO.gender());
        user.setRole(userDTO.role());
        user.setIsActive(dto.isActive());
        teacher.setUser(user);  // Set User entity to Teacher

        // Map fields from PersonalInfoDTO
        PersonalInfoInsertDTO personalInfoDTO = dto.personalInfoInsertDTO();
        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setAmka(personalInfoDTO.amka());
        personalInfo.setIdentityNumber(personalInfoDTO.identityNumber());
        personalInfo.setPlaceOfBirth(personalInfoDTO.placeOfBirth());
        personalInfo.setMunicipalityOfRegistration(personalInfoDTO
                .municipalityOfRegistration());
        teacher.setPersonalInfo(personalInfo);  // Set PersonalInfo entity to Teacher

        return teacher;
    }
}
