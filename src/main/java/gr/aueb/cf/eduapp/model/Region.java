package gr.aueb.cf.eduapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "regions")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Getter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "region")
    private Set<EducationalUnit> educationalUnits = new HashSet<>();

    public Set<EducationalUnit> getAllEducationalUnits() {
        if (educationalUnits == null) educationalUnits = new HashSet<>();
        return Collections.unmodifiableSet(educationalUnits);
    }

    @Override
    public String toString() {
        return name;
    }
}
