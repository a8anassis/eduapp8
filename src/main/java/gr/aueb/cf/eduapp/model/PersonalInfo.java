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

    @Column(unique = true)
    private String amka;

    @Column(unique = true)
    private String identityNumber;
    private String placeOfBirth;
    private String municipalityOfRegistration;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "amka_file_id")
    private Attachment amkaFile;
}
