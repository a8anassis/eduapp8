package gr.aueb.cf.eduapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonalInfoReadOnlyDTO {
    private String amka;
    private String identityNumber;
}
