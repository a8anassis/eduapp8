package gr.aueb.cf.eduapp.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TeacherFilters extends GenericFilters {

    @Nullable
    private String uuid;

    @Nullable
    private String userVat;

    @Nullable
    private String userAmka;

    @Nullable
    private Boolean active;
}
