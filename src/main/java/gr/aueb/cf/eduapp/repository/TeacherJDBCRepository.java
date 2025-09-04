package gr.aueb.cf.eduapp.repository;

import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TeacherJDBCRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Teacher> findAllTeachersWithDetails() {
        String sql = """
            SELECT 
                t.id AS teacher_id, 
                t.uuid AS teacher_uuid, 
                t.is_active AS teacher_is_active,
                u.id AS user_id, 
                u.firstname AS user_firstname, 
                u.lastname AS user_lastname, 
                u.vat AS user_vat,
                p.id AS personal_info_id, 
                p.amka AS personal_info_amka, 
                p.identity_number AS personal_info_identity_number,
                p.place_of_birth AS personal_info_place_of_birth
            FROM 
                teachers t
            JOIN            
                users u ON t.user_id = u.id
            LEFT JOIN 
                personal_information p ON t.personal_info_id = p.id
            """;

        // Uses JOIN for users since user can NOT be nullable
        // Uses LEFT JOIN for personal_info since personal_info can be null
        // Java17+ uses text blocks """ for multiline text without + or \n

        return jdbcTemplate.query(sql, (rs, rowNum) -> {        // implements the RowMapper Interface with rs and row num
            // Build User
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFirstname(rs.getString("user_firstname"));
            user.setLastname(rs.getString("user_lastname"));
            user.setVat(rs.getString("user_vat"));

            // Build PersonalInfo (nullable)
            PersonalInfo personalInfo = null;
            Long personalInfoId = rs.getObject("personal_info_id", Long.class);
            if (personalInfoId != null) {
                personalInfo = new PersonalInfo();
                personalInfo.setId(rs.getLong("personal_info_id"));
                personalInfo.setAmka(rs.getString("personal_info_amka"));
                personalInfo.setIdentityNumber(rs.getString("personal_info_identity_number"));
                personalInfo.setPlaceOfBirth(rs.getString("personal_info_place_of_birth"));
            }

            // Build Teacher
            Teacher teacher = new Teacher();
            teacher.setId(rs.getLong("teacher_id"));
            teacher.setUuid(rs.getString("teacher_uuid"));
            teacher.setIsActive(rs.getBoolean("teacher_is_active"));

            teacher.setUser(user);
            teacher.setPersonalInfo(personalInfo);
            return teacher;
        });
    }

    public List<Teacher> findAllTeachersWithStatus(boolean isActive) {
        String sql = """
            SELECT 
                t.id AS teacher_id, 
                t.uuid AS teacher_uuid, 
                t.is_active AS teacher_is_active,
                u.id AS user_id, 
                u.firstname AS user_firstname, 
                u.lastname AS user_lastname, 
                u.vat AS user_vat,
                p.id AS personal_info_id, 
                p.amka AS personal_info_amka, 
                p.identity_number AS personal_info_identity_number,
                p.place_of_birth AS personal_info_place_of_birth
            FROM 
                teachers t
            JOIN            
                users u ON t.user_id = u.id
            LEFT JOIN 
                personal_information p ON t.personal_info_id = p.id
            WHERE 
                t.is_active = ?
            """;

        // Uses JOIN for users since user can NOT be nullable
        // Uses LEFT JOIN for personal_info since personal_info can be null
        // Java17+ uses text blocks """ for multiline text without + or \n

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
            // Build User
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFirstname(rs.getString("user_firstname"));
            user.setLastname(rs.getString("user_lastname"));
            user.setVat(rs.getString("user_vat"));

            // Build PersonalInfo (nullable)
            PersonalInfo personalInfo = null;
            Long personalInfoId = rs.getObject("personal_info_id", Long.class);
            if (personalInfoId != null) {
                personalInfo = new PersonalInfo();
                personalInfo.setId(rs.getLong("personal_info_id"));
                personalInfo.setAmka(rs.getString("personal_info_amka"));
                personalInfo.setIdentityNumber(rs.getString("personal_info_identity_number"));
                personalInfo.setPlaceOfBirth(rs.getString("personal_info_place_of_birth"));
            }

            // Build Teacher
            Teacher teacher = new Teacher();
            teacher.setId(rs.getLong("teacher_id"));
            teacher.setUuid(rs.getString("teacher_uuid"));
            teacher.setIsActive(rs.getBoolean("teacher_is_active"));

            teacher.setUser(user);
            teacher.setPersonalInfo(personalInfo);
            return teacher;
        }, isActive);
    }

    public List<Teacher> filterTeachers(Boolean isActive,
                                        String placeOfBirth,
                                        String userLastName) {
        // Base query with 1=1 for easy WHERE clause concatenation
        StringBuilder sql = new StringBuilder("""
        SELECT 
            t.id AS teacher_id,
            t.uuid AS teacher_uuid,
            t.is_active AS teacher_is_active,
            u.id AS user_id,
            u.firstname AS user_firstname,
            u.lastname AS user_lastname,
            u.vat AS user_vat,
            p.id AS personal_info_id,
            p.place_of_birth AS personal_info_place_of_birth
        FROM 
            teachers t
        JOIN 
            users u ON t.user_id = u.id
        LEFT JOIN 
            personal_information p ON t.personal_info_id = p.id
        WHERE 1=1
        """);

        // Parameter map
        Map<String, Object> params = new HashMap<>();

        // Add filters dynamically
        if (isActive != null) {
            sql.append(" AND t.is_active = :isActive");
            params.put("isActive", isActive);
        }
        if (placeOfBirth != null && !placeOfBirth.isEmpty()) {
            sql.append(" AND p.place_of_birth = :placeOfBirth");
            params.put("placeOfBirth", placeOfBirth);
        }
        if (userLastName != null && !userLastName.isEmpty()) {
            sql.append(" AND u.lastname = :userLastName");
            params.put("userLastName", userLastName);
        }

        // Execute query
        return namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> {
                    Teacher teacher = new Teacher();
                    teacher.setId(rs.getLong("teacher_id"));
                    teacher.setUuid(rs.getString("teacher_uuid"));
                    teacher.setIsActive(rs.getBoolean("teacher_is_active"));

                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setFirstname(rs.getString("user_firstname"));
                    user.setLastname(rs.getString("user_lastname"));
                    user.setVat(rs.getString("user_vat"));
                    teacher.setUser(user);

                    Long personalInfoId = rs.getObject("personal_info_id", Long.class);
                    if (personalInfoId != null) {
                        PersonalInfo personalInfo = new PersonalInfo();
                        personalInfo.setId(rs.getLong("personal_info_id"));
                        personalInfo.setPlaceOfBirth(rs.getString("personal_info_place_of_birth"));
                        teacher.setPersonalInfo(personalInfo);
                    }

                    return teacher;
                }
        );
    }
}
