package gr.aueb.cf.eduapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record TeacherInsertDTO(
        @NotNull(message = "isActive field is required")
        Boolean isActive,

        @NotNull(message = "User details are required")
        UserInsertDTO user,

        @NotNull(message = "Personal Info is required")
        PersonalInfoInsertDTO personalInfo
) {}
