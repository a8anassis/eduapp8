package gr.aueb.cf.eduapp.model;

import gr.aueb.cf.eduapp.core.enums.Role;
import gr.aueb.cf.eduapp.core.enums.GenderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String vat;

    private String fatherName;
    private String motherName;
    private String fatherLastname;
    private String motherLastname;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(mappedBy = "userInsertDTO")
    private Employee employee;

    @OneToOne(mappedBy = "userInsertDTO")
    private Teacher teacher;

    public boolean isEmployee() {
        return getEmployee() != null;
    }

    public boolean isTeacher() {
        return getTeacher() != null;
    }

    public String getFullName(){
        return firstname + " " + lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getIsActive() == null || this.getIsActive();
    }
}
