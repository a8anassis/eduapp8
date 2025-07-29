package gr.aueb.cf.eduapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "personal_information")
public class PersonalInfo extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String amka;
    private String identityNumber;
    private String placeOfBirth;
    private String municipalityOfRegistration;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "amka_file_id")
    private Attachment amkaFile;

    public PersonalInfo(Long id, String  amka, String identityNumber,
                        String placeOfBirth, String municipalityOfRegistration) {
        this.id = id;
        this.amka = amka;
        this.identityNumber = identityNumber;
        this.placeOfBirth = placeOfBirth;
        this.municipalityOfRegistration = municipalityOfRegistration;
    }
}
