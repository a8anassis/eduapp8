package gr.aueb.cf.eduapp.service;

import gr.aueb.cf.eduapp.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.eduapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.eduapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.eduapp.core.filters.Paginated;
import gr.aueb.cf.eduapp.core.filters.TeacherFilters;
import gr.aueb.cf.eduapp.dto.TeacherInsertDTO;
import gr.aueb.cf.eduapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.eduapp.dto.TeacherUpdateDTO;
import gr.aueb.cf.eduapp.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ITeacherService {

    TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO teacherInsertDTO, MultipartFile amkaFile)
            throws AppObjectAlreadyExists, IOException;

    TeacherReadOnlyDTO updateTeacher(TeacherUpdateDTO teacherUpdateDTO, MultipartFile amkaFile)
            throws AppObjectAlreadyExists, IOException, AppObjectNotFoundException;

    TeacherReadOnlyDTO getOneTeacher(String uuid) throws AppObjectNotFoundException;

    Paginated<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size);
//    Page<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size);

    Paginated<TeacherReadOnlyDTO> getTeachersFilteredPaginated(TeacherFilters teacherFilters);

//    List<Teacher> findAllTeachersWithDetails();
//    List<Teacher> findAllTeachersWithStatus(boolean isActive);
//    List<Teacher> filterTeachers(Boolean isActive, String placeOfBirth, String userLastName);
}
