package gr.aueb.cf.eduapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record PersonalInfoUpdateDTO(
        @NotNull
        Long id,

        @NotEmpty(message = "AMKA is required")
        @Pattern(regexp = "\\d{11}", message = "AMKA must be an 11-digit number")
        String amka,

        @NotEmpty(message = "Identity number is required")
        String identityNumber,

        @NotEmpty(message = "Place of birth is required")
        String placeOfBirth,

        @NotEmpty(message = "Municipality of registration is required")
        String municipalityOfRegistration
) {}
