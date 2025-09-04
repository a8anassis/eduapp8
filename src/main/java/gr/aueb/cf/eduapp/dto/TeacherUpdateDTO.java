package gr.aueb.cf.eduapp.dto;

import jakarta.validation.Valid;
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

        @Valid
        @NotNull(message = "User details are required")
        UserUpdateDTO userUpdateDTO,

        @Valid
        @NotNull(message = "Personal Info is required")
        PersonalInfoUpdateDTO personalInfoUpdateDTO
) {}
