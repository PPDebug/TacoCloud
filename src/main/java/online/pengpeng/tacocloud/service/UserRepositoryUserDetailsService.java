package online.pengpeng.tacocloud.service;

import online.pengpeng.tacocloud.entity.RegistrationForm;
import online.pengpeng.tacocloud.entity.User;
import online.pengpeng.tacocloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author pengpeng
 * @date 2022/4/19
 */
@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    public User register(RegistrationForm form) {
        return userRepo.save(form.toUser(passwordEncoder));
    }
}
