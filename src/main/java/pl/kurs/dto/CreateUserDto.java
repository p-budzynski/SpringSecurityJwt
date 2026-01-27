package pl.kurs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.validation.Create;
import pl.kurs.validation.Password;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserDto {

    @NotBlank(message = "User name is required", groups = Create.class)
    private String username;

    @Email(message = "Email must be valid", groups = Create.class)
    @NotBlank(message = "Email is required", groups = Create.class)
    private String email;

    @Password(groups = Create.class)
    @NotBlank(message = "Password is required", groups = Create.class)
    private String password;

}
