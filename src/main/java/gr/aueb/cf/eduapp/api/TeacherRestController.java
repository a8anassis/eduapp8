package gr.aueb.cf.eduapp.api;

import gr.aueb.cf.eduapp.core.exceptions.*;
import gr.aueb.cf.eduapp.core.filters.Paginated;
import gr.aueb.cf.eduapp.core.filters.TeacherFilters;
import gr.aueb.cf.eduapp.dto.ResponseMessageDTO;
import gr.aueb.cf.eduapp.dto.TeacherInsertDTO;
import gr.aueb.cf.eduapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.eduapp.dto.TeacherUpdateDTO;
import gr.aueb.cf.eduapp.service.ITeacherService;
import gr.aueb.cf.eduapp.validator.TeacherInsertValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeacherRestController {

    private final ITeacherService teacherService;

    @Operation(
            summary = "Save a teacher",
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Teacher created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Teacher already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
//                    ,
//                    @ApiResponse(
//                            responseCode = "401", description = "Unauthorized",
//                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)
//                            )
//                    ),
//                    @ApiResponse(
//                            responseCode = "403", description = "Access Denied",
//                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
//                    )
            }
    )
    @PostMapping(value = "/teachers")
    public ResponseEntity<TeacherReadOnlyDTO> saveTeacher(
            @Valid @RequestPart(name = "teacher") TeacherInsertDTO teacherInsertDTO,
            BindingResult bindingResult,
            @Nullable @RequestPart(value = "amkaFile", required = false) MultipartFile amkaFile)
            throws AppObjectAlreadyExists, IOException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.saveTeacher(teacherInsertDTO, amkaFile);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()                       // request URI is /teachers
                .path("/{uuid}")                            // Appends "/{uuid}"
                .buildAndExpand(teacherReadOnlyDTO.uuid())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(teacherReadOnlyDTO);
    }

    @Operation(
            summary = "Get all teachers paginated",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Teachers returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Paginated.class,
                                            example = """
                                                {
                                                  "data": [
                                                    { "id": 1, "uuid": "...", ... },
                                                    { "id": 2, "uuid": "...", ... }
                                                  ],
                                                  "currentPage": 4,
                                                  "pageSize": 10,
                                                  "totalPages": 5,
                                                  "numberOfElements": 8,
                                                  "totalElements": 48
                                                }"""
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)))
            }
    )
    @GetMapping("/teachers")
    public ResponseEntity<Paginated<TeacherReadOnlyDTO>> getPaginatedTeachers(
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size
    ) {
//        Page<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page, size);
        Paginated<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page, size);
        return ResponseEntity.ok(teachersPage);
    }

    @Operation(
            summary = "Get all teachers paginated and filtered",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Teachers returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Paginated.class,
                                            example = """
                                                {
                                                  "data": [
                                                    { "id": 1, "uuid": "...", ... },
                                                    { "id": 2, "uuid": "...", ... }
                                                  ],
                                                  "currentPage": 4,
                                                  "pageSize": 10,
                                                  "totalPages": 5,
                                                  "numberOfElements": 8,
                                                  "totalElements": 48
                                                }"""
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)))
            }
    )
    @PostMapping("/teachers/search")
    public ResponseEntity<Paginated<TeacherReadOnlyDTO>> getFilteredAndPaginatedTeachers(
            @Nullable @RequestBody TeacherFilters filters)  {

        if (filters == null) filters = TeacherFilters.builder().build();
        Paginated<TeacherReadOnlyDTO> dtoPaginated = teacherService.getTeachersFilteredPaginated(filters);
        return ResponseEntity.ok(dtoPaginated);
    }

    @Operation(
            summary = "Get one teacher by uuid",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Teacher returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Teacher not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/teachers/{uuid}")
    public ResponseEntity<TeacherReadOnlyDTO> getTeacherByUuid(@PathVariable String uuid)
            throws AppObjectNotFoundException {
        return ResponseEntity.ok(teacherService.getOneTeacher(uuid));
    }


    @Operation(
            summary = "Update a teacher",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Teacher updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Teacher already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Teacher not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PutMapping(value = "/teachers/{uuid}")
    @PreAuthorize("#uuid == #teacherUpdateDTO.uuid()")  // AccessDeniedException
    public ResponseEntity<TeacherReadOnlyDTO> updateTeacher(@PathVariable String uuid,
                                                            @Valid @RequestPart(name = "teacher") TeacherUpdateDTO teacherUpdateDTO,
                                                            @Nullable @RequestPart(value = "amkaFile", required = false) MultipartFile amkaFile,
                                                            BindingResult bindingResult)
            throws AppObjectNotFoundException, AppObjectAlreadyExists, IOException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.updateTeacher(teacherUpdateDTO, amkaFile);

        return ResponseEntity.ok(teacherReadOnlyDTO);
    }
}
