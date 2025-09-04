package gr.aueb.cf.eduapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aueb.cf.eduapp.core.ErrorHandler;
import gr.aueb.cf.eduapp.core.enums.GenderType;
import gr.aueb.cf.eduapp.core.enums.Role;
import gr.aueb.cf.eduapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.eduapp.dto.*;

import gr.aueb.cf.eduapp.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
//@Import(ErrorHandler.class)
@AutoConfigureMockMvc(addFilters = false) // disable JWT/security filters
class TeacherRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TeacherService teacherService;

    @Test
    void getTeacherByUuid_shouldReturnOk() throws Exception {

        String uuid = "uuid-123";
        UserReadOnlyDTO userReadOnlyDTO = new UserReadOnlyDTO("Άννα", "Γιαννούτσου", "123456789");
        PersonalInfoReadOnlyDTO personalDto = new PersonalInfoReadOnlyDTO("AMKA123", "ID123");
        TeacherReadOnlyDTO teacherReadOnlyDTO = new TeacherReadOnlyDTO(1L, uuid, true, userReadOnlyDTO, personalDto);

        when(teacherService.getOneTeacher(uuid)).thenReturn(teacherReadOnlyDTO);

        // Assert
        mockMvc.perform(get("/api/teachers/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid))   // $ is the root JSON
                .andExpect(jsonPath("$.userReadOnlyDTO.firstname").value("Άννα"));
    }

    @Test
    void getTeacherByUuid_shouldReturnNotFound() throws Exception {

        String uuid = "non-existent-uuid";
        when(teacherService.getOneTeacher(uuid))
                .thenThrow(new AppObjectNotFoundException("Teacher", "Teacher not found"));

        // Act & Assert
        mockMvc.perform(get("/teachers/{uuid}", uuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveTeacher_shouldReturnCreated() throws Exception {
//        UserInsertDTO userInsertDTO = UserInsertDTO.builder()
//                .firstname("Αθανάσιος").lastname("Ανδρούτσος")
//                .vat("123456789")
//                .build();
        UserInsertDTO userInsertDTO = UserInsertDTO.builder()
                .firstname("Αθανάσιος")
                .lastname("Ανδρούτσος")
                .username("ath.an@gmail.com")
                .password("Valid@123") // meets regex
                .vat("123456789")
                .fatherName("Ανδρέας")
                .fatherLastname("Ανδρούτσος")
                .motherName("Μαρία")
                .motherLastname("Ανδρούτσου")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(GenderType.MALE)
                .role(Role.TEACHER)
                .build();

//        PersonalInfoInsertDTO personalInfoInsertDTO = PersonalInfoInsertDTO.builder()
//                .amka("12345678987").identityNumber("ID123")
//                .build();

        PersonalInfoInsertDTO personalInfoInsertDTO = PersonalInfoInsertDTO.builder()
                .amka("12345678987")
                .identityNumber("ID123")
                .placeOfBirth("Athens")
                .municipalityOfRegistration("Athens Municipality")
                .build();

        TeacherInsertDTO teacherInsertDTO = TeacherInsertDTO.builder()
                .isActive(true).userInsertDTO(userInsertDTO)
                .personalInfoInsertDTO(personalInfoInsertDTO)
                .build();
        String uuid = "uuid-123";
        UserReadOnlyDTO userReadOnlyDTO = new UserReadOnlyDTO("Αθανάσιος", "Ανδρούτσος", "123456789");
        PersonalInfoReadOnlyDTO personalInfoReadOnlyDTO = new PersonalInfoReadOnlyDTO("12345678987", "ID123");
        TeacherReadOnlyDTO teacherReadOnlyDTO = new TeacherReadOnlyDTO(1L, uuid, true, userReadOnlyDTO, personalInfoReadOnlyDTO);

        // Create the JSON part using objectMapper for consistency
        MockMultipartFile teacherJson = new MockMultipartFile(
                "teacher",
                "", "application/json",
                objectMapper.writeValueAsBytes(teacherInsertDTO)
        );

        // Optional AMKA file part
        MockMultipartFile amkaFile = new MockMultipartFile(
                "amkaFile",
                "amka.txt",
                "text/plain",
                "dummy content".getBytes()
        );

        when(teacherService.saveTeacher(any(TeacherInsertDTO.class), eq(amkaFile)))
                .thenReturn(teacherReadOnlyDTO);

        // Assert - Include both parts in the request
        mockMvc.perform(multipart("/api/teachers")
                        .file(teacherJson)
                        .file(amkaFile)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userReadOnlyDTO.firstname").value("Αθανάσιος"));
    }

    @Test
    void saveTeacher_shouldReturnBadRequest_whenValidationFails() throws Exception {
        String invalidJson = """
            {
              "isActive": true,
              "userInsertDTO": {
                "username": "invalid-email",
                "password": "simple",
                "vat": "123",
                "gender": "MALE",
                "role": "TEACHER",
                "dateOfBirth": "2000-01-01"
                // Missing required fields...
              },
              "personalInfoInsertDTO": {
              // should be 11
                "amka": "AMKA123",            
                "identityNumber": "ID123"
              }
            }
        """;

        MockMultipartFile invalidTeacherJson = new MockMultipartFile(
                "teacher", "", "application/json", invalidJson.getBytes()
        );

        mockMvc.perform(multipart("/api/teachers")
                        .file(invalidTeacherJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
