package gr.aueb.cf.eduapp.dto;

import jakarta.validation.constraints.NotNull;

public record TeacherInsertDTO(
        @NotNull(message = "isActive field is required")
        Boolean isActive,

        @NotNull(message = "User details are required")
        UserInsertDTO userInsertDTO,

        @NotNull(message = "Personal Info is required")
        PersonalInfoInsertDTO personalInfoInsertDTO
) {}
