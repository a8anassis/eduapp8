package gr.aueb.cf.eduapp.core.specifications;

import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

// Utility Class
public class TeacherSpecification {

    private TeacherSpecification() {

    }

    public static Specification<Teacher> teacherUserVatIs(String  vat) {
        return ((root, query, criteriaBuilder) -> {
           if (vat == null || vat.isBlank())
               return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            Join<Teacher, User> user = root.join("user");
            return criteriaBuilder.equal(user.get("vat"), vat);
        });
    }

    public static Specification<Teacher> trUserIsActive(Boolean isActive) {
        return (root, query, builder) -> {
            if (isActive == null) {
                return builder.isTrue(builder.literal(true));
            }

            // Join the User entity related to the Teacher entity
            Join<Teacher, User> user = root.join("user");

            // Return the condition where the user's isActive matches the input isActive
            return builder.equal(user.get("isActive"), isActive);
        };
    }

    public static Specification<Teacher> trPersonalInfoAmkaIs(String amka) {
        return (root, query, builder) -> {
            // If AMKA is null or blank, return a condition that is always true
            if (amka == null || amka.isBlank()) {
                return builder.isTrue(builder.literal(true));
            }

            // Join the PersonalInfo entity related to the Teacher entity
            Join<Teacher, PersonalInfo> personalInfo = root.join("personalInfo");

            // Return the condition where the personalInfo's AMKA matches the input AMKA
            return builder.equal(personalInfo.get("amka"), amka);
        };
    }

    public static Specification<Teacher> trStringFieldLike(String field, String value) {
        return (root, query, builder) -> {
            if (value == null || value.trim().isEmpty()) return builder.isTrue(builder.literal(true));
            return builder.like(builder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
        };
    }
}
