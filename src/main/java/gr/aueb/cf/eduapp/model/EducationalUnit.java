package gr.aueb.cf.eduapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "educational_units")
public class EducationalUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(mappedBy = "eduUnits", fetch = FetchType.LAZY)
    private Set<Employee> employees = new HashSet<>();

    public Set<Employee> getAllEmployees() {
        return Collections.unmodifiableSet(employees);
    }

    public void addEmployee(Employee employee) {
        if (employees == null) employees = new HashSet<>();
        employees.add(employee);
        employee.getEduUnits().add(this);
    }

    public void removeEmployee(Employee employee) {
        if (employees == null) return;
        employees.remove(employee);
        employee.getEduUnits().remove(this);
    }
}
