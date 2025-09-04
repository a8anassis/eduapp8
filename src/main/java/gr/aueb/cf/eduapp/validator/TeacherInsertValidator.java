package gr.aueb.cf.eduapp.validator;

import gr.aueb.cf.eduapp.dto.TeacherInsertDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class TeacherInsertValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return TeacherInsertDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TeacherInsertDTO teacherInsertDTO = (TeacherInsertDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "userInsertDTO.firstname",
                "empty",
                "Firstname cannot be blank"
        );
        if (teacherInsertDTO.userInsertDTO().firstname().length() < 3 || teacherInsertDTO.userInsertDTO().firstname().length() > 50) {
            errors.rejectValue("userInsertDTO.firstname", "size", null, "Firstname must be between 3 and 50 characters");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "userInsertDTO.lastname",
                "empty",
                "Lastname cannot be blank"
        );
        if (teacherInsertDTO.userInsertDTO().lastname().length() < 3 || teacherInsertDTO.userInsertDTO().lastname().length() > 50) {
            errors.rejectValue("userInsertDTO.lastname", "size", null, "Lastname must be between 3 and 50 characters");
        }

    }
}
