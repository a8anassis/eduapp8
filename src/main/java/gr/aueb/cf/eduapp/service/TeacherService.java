package gr.aueb.cf.eduapp.service;

import gr.aueb.cf.eduapp.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.eduapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.eduapp.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.eduapp.core.filters.Paginated;
import gr.aueb.cf.eduapp.core.filters.TeacherFilters;
import gr.aueb.cf.eduapp.core.specifications.TeacherSpecification;
import gr.aueb.cf.eduapp.dto.TeacherInsertDTO;
import gr.aueb.cf.eduapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.eduapp.dto.TeacherUpdateDTO;
import gr.aueb.cf.eduapp.mapper.Mapper;
import gr.aueb.cf.eduapp.model.Attachment;
import gr.aueb.cf.eduapp.model.PersonalInfo;
import gr.aueb.cf.eduapp.model.Teacher;
import gr.aueb.cf.eduapp.repository.PersonalInfoRepository;
import gr.aueb.cf.eduapp.repository.TeacherRepository;
import gr.aueb.cf.eduapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
//@RequiredArgsConstructor
public class TeacherService implements ITeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final Mapper mapper;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository,
                          PersonalInfoRepository personalInfoRepository, Mapper mapper) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO teacherInsertDTO, MultipartFile amkaFile)
            throws AppObjectAlreadyExists, IOException {

        if (userRepository.findByVat(teacherInsertDTO.userInsertDTO().vat()).isPresent()) {
            throw new AppObjectAlreadyExists("VAT", "User with vat " + teacherInsertDTO.userInsertDTO().vat() + " already exists");
        }

        if (personalInfoRepository.findByAmka(teacherInsertDTO.personalInfoInsertDTO().amka()).isPresent()) {
            throw new AppObjectAlreadyExists("AMKA", "User with AMKA " + teacherInsertDTO.personalInfoInsertDTO().amka() + " already exists");
        }

        if (userRepository.findByUsername(teacherInsertDTO.userInsertDTO().username()).isPresent()) {
            throw new AppObjectAlreadyExists("Username", "User with username " + teacherInsertDTO.userInsertDTO().username() + " already exists");
        }

        if (personalInfoRepository.findByIdentityNumber(teacherInsertDTO.personalInfoInsertDTO().identityNumber()).isPresent()) {
            throw new AppObjectAlreadyExists("IdentityNumber", "User with identity number " + teacherInsertDTO.personalInfoInsertDTO().identityNumber() + " already exists");
        }

        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);

        if (amkaFile != null && !amkaFile.isEmpty()) {                  // Any non-database operation will NOT be rolled back
            saveAmkaFile(teacher.getPersonalInfo(), amkaFile);          // change to saveAmkaFileAndGetPath
        }

        // Register cleanup for rollback

        // TransactionSynchronizationManager.registerSynchronization(...) is a Spring transaction hook.
        // It lets you register a callback (a TransactionSynchronization) that Spring will call when the
        // current transaction finishes (afterCompletion(status)
        // either successfully (commit) or unsuccessfully (rollback).
        // If status == STATUS_COMMITTED → the transaction was committed.
        //If status == STATUS_ROLLED_BACK → the transaction was rolled back.

//        TransactionSynchronizationManager.registerSynchronization(
//                (TransactionSynchronization) status -> {
//                    if (status == STATUS_ROLLED_BACK) {
//                        try {
//                            Files.deleteIfExists(filePath);
//                        } catch (IOException e) {
//                            throw new UncheckedIOException("Failed to delete file after rollback", e);
//                        }
//                    }
//                }
//        );

        // Saves teacher (cascades to User and PersonalInfo)
        Teacher savedTeacher = teacherRepository.save(teacher);     // Exception


        log.info("Teacher with amka={} saved.", teacherInsertDTO.personalInfoInsertDTO().amka());
        return mapper.mapToTeacherReadOnlyDTO(savedTeacher);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TeacherReadOnlyDTO updateTeacher(TeacherUpdateDTO teacherUpdateDTO, MultipartFile amkaFile)
            throws AppObjectAlreadyExists, IOException, AppObjectNotFoundException {

        if (teacherRepository.findById(teacherUpdateDTO.id()).isEmpty()) {
            throw new AppObjectNotFoundException("Teacher", "Teacher with id " + teacherUpdateDTO.id() + " not found");
        }

        Teacher existingTeacher = teacherRepository.findById(teacherUpdateDTO.id()).orElse(null);
        if (existingTeacher == null) throw new AppObjectNotFoundException("Teacher", "Teacher with id=" + teacherUpdateDTO.id() + " not found");

        if (!existingTeacher.getUser().getVat().equals(teacherUpdateDTO.userUpdateDTO().vat()) &&
                userRepository.findByVat(teacherUpdateDTO.userUpdateDTO().vat()).isPresent()) {
            throw new AppObjectAlreadyExists("Teacher", "Teacher with vat " + teacherUpdateDTO.userUpdateDTO().vat() + " already exists");
        }

        if (!existingTeacher.getPersonalInfo().getIdentityNumber().equals(teacherUpdateDTO.personalInfoUpdateDTO().identityNumber()) &&
                personalInfoRepository.findByIdentityNumber(teacherUpdateDTO.personalInfoUpdateDTO().identityNumber()).isPresent()) {
            throw new AppObjectAlreadyExists("Teacher", "Teacher with identity number " + teacherUpdateDTO.personalInfoUpdateDTO().identityNumber() + " already exists");
        }

        Teacher teacherToUpdate = mapper.mapToTeacherEntity(teacherUpdateDTO);
        if (amkaFile != null && !amkaFile.isEmpty()) {
            Files.deleteIfExists(Paths.get(existingTeacher.getPersonalInfo().getAmkaFile().getFilePath()));
            saveAmkaFile(teacherToUpdate.getPersonalInfo(), amkaFile);          // change to saveAmkaFileAndGetPath
        }

        // Register cleanup for rollback
//        TransactionSynchronizationManager.registerSynchronization(
//                (TransactionSynchronization) status -> {
//                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
//                        try {
//                            Files.deleteIfExists(filePath);                   // get file path from above
//                        } catch (IOException e) {
//                            throw new UncheckedIOException("Failed to delete file after rollback", e);
//                        }
//                    }
//                }
//        );

        // Saves teacher (cascades to User and PersonalInfo)
        Teacher updatedTeacher = teacherRepository.save(teacherToUpdate);
        log.info("Teacher with id={} saved.", teacherUpdateDTO.personalInfoUpdateDTO().id());
        return mapper.mapToTeacherReadOnlyDTO(updatedTeacher);
    }

    @Override
    public TeacherReadOnlyDTO getOneTeacher(String uuid) throws AppObjectNotFoundException {
        return teacherRepository
                .findByUuid(uuid)
                .map(mapper::mapToTeacherReadOnlyDTO)
                .orElseThrow(() ->
                    new AppObjectNotFoundException("Teacher", "Teacher with uuid:" + uuid + " not found"));
    }

    @Override
//    public Page<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size) {
    public Paginated<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size) {
        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        log.debug("Paginated teachers were returned successfully with page={} and size={}", page, size);
//        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
        var paginatedTeachers = teacherRepository.findAll(pageable);
        return Paginated.fromPage(paginatedTeachers.map(mapper::mapToTeacherReadOnlyDTO));
    }

    @Override
    public Paginated<TeacherReadOnlyDTO> getTeachersFilteredPaginated(TeacherFilters teacherFilters) {
        var filtered = teacherRepository.findAll(getSpecsFromFilters(teacherFilters), teacherFilters.getPageable());
        log.debug("Filtered and paginated teachers were returned successfully with page={} and size={}", teacherFilters.getPage(),
                teacherFilters.getPageSize());
//        return new Paginated<>(filtered.map(mapper::mapToTeacherReadOnlyDTO));
        return Paginated.fromPage(filtered.map(mapper::mapToTeacherReadOnlyDTO));
    }


    private void saveAmkaFile(PersonalInfo personalInfo, MultipartFile amkaFile)
            throws IOException {

        String originalFilename = amkaFile.getOriginalFilename();
        String savedName = UUID.randomUUID().toString() + getFileExtension(originalFilename);

        String uploadDirectory = "uploads/";
        Path filePath = Paths.get(uploadDirectory + savedName);

        Files.createDirectories(filePath.getParent());
//        Files.write(filePath, amkaFile.getBytes());
        amkaFile.transferTo(filePath);  // safe for large files, more efficient

        Attachment attachment = new Attachment();
        attachment.setFilename(originalFilename);
        attachment.setSavedName(savedName);
        attachment.setFilePath(filePath.toString());
        attachment.setContentType(amkaFile.getContentType());
        attachment.setExtension(getFileExtension(originalFilename));

        personalInfo.setAmkaFile(attachment);
        log.info("Attachment for teacher with amka={} saved", personalInfo.getAmka());
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }


    private Specification<Teacher> getSpecsFromFilters(TeacherFilters teacherFilters) {
        return TeacherSpecification.trStringFieldLike("uuid", teacherFilters.getUuid())
                .and(TeacherSpecification.teacherUserVatIs(teacherFilters.getUserVat()))
                .and(TeacherSpecification.trPersonalInfoAmkaIs(teacherFilters.getUserAmka()))
                .and(TeacherSpecification.trUserIsActive(teacherFilters.getActive()));
    }
}
