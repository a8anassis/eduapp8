package gr.aueb.cf.eduapp.api;

import gr.aueb.cf.eduapp.authentication.AuthenticationService;
import gr.aueb.cf.eduapp.dto.AuthenticationRequestDTO;
import gr.aueb.cf.eduapp.dto.AuthenticationResponseDTO;
import gr.aueb.cf.eduapp.dto.ResponseMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Authenticate user",
            description = "Returns JWT token for valid credentials",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponseDTO.class))),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized - Invalid credentials",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "400",  description = "Bad request - Missing/invalid parameters",
                            content = @Content)
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);
        return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
    }
}
