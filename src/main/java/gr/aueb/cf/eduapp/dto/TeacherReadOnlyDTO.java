package gr.aueb.cf.eduapp.dto;

import lombok.Builder;

@Builder
public record TeacherReadOnlyDTO(
        Long id,
        String uuid,
        Boolean isActive,
        UserReadOnlyDTO userReadOnlyDTO,
        PersonalInfoReadOnlyDTO personalInfoReadOnlyDTO) {}
