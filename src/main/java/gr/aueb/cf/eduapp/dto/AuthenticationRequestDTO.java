package gr.aueb.cf.eduapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
