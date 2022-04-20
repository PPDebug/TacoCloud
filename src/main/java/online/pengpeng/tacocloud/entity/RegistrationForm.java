package online.pengpeng.tacocloud.entity;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author pengpeng
 * @date 2022/4/19
 */
@Data
public class RegistrationForm {
    @NotNull
    @Size(min=5, message = "user name length at least 5")
    private String username;
    @NotNull(message = "this field can't be empty")
    private String password;
    @NotNull(message = "this field can't be empty")
    private String fullName;
    @NotNull(message = "this field can't be empty")

    private String street;
    @NotNull(message = "this field can't be empty")
    private String city;
    @NotNull(message = "this field can't be empty")
    private String state;
    @NotNull(message = "this field can't be empty")
    private String zip;
    @NotNull(message = "this field can't be empty")
    private String phone;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username,
                passwordEncoder.encode(password),
                fullName,
                street,
                city,
                state,
                zip,
                phone
        );
    }
}
