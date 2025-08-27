package gr.aueb.cf.eduapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TeacherUpdateDTO(
        @NotNull(message = "id field is required")
        Long id,

        @NotNull(message = "isActive field is required")
        Boolean isActive,

        @NotNull(message = "uuid field is required")
        String uuid,

        @NotNull(message = "User details are required")
        UserUpdateDTO userUpdateDTO,

        @NotNull(message = "Personal Info is required")
        PersonalInfoUpdateDTO personalInfoUpdateDTO
) {}
