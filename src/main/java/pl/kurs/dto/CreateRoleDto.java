package pl.kurs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateRoleDto {

    @NotBlank(message = "Role name is required", groups = {Create.class, Update.class})
    private String roleName;

}
