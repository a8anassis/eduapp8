package gr.aueb.cf.eduapp.dto;

import lombok.Builder;

@Builder
public record PersonalInfoReadOnlyDTO(String amka, String identityNumber) {}


