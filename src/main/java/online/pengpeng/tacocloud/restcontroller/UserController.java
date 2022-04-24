package online.pengpeng.tacocloud.restcontroller;

import lombok.extern.slf4j.Slf4j;
import online.pengpeng.tacocloud.entity.RegistrationForm;
import online.pengpeng.tacocloud.entity.User;
import online.pengpeng.tacocloud.service.UserRepositoryUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author pengpeng
 * @date 2022/4/24
 */
@Slf4j
@RestController("restUserController")
@RequestMapping(path = "/rest/user", produces = "application/json")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepositoryUserDetailsService userService;

    public UserController(UserRepositoryUserDetailsService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody @Valid RegistrationForm form){
        return userService.register(form);
    }

}
