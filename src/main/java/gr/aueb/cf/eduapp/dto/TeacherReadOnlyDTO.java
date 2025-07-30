package gr.aueb.cf.eduapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeacherReadOnlyDTO {

    private Long id;
    private String uuid;
    private Boolean isActive;

    private UserReadOnlyDTO userReadOnlyDTO;

    private PersonalInfoReadOnlyDTO personalInfoReadOnlyDTO;
}
