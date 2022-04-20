package online.pengpeng.tacocloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.sql.DataSource;

/**
 * @author pengpeng
 * @date 2022/4/19
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user1")
//                    .password("password2")
//                    .authorities("ROLE_USER")
//                .and()
//                .withUser("user2")
//                    .password("password")
//                    .authorities("ROLE_USER");

//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery(
//                "select username, password, enabled form Users " +
//                        "where username=?"
//                )
//                .authoritiesByUsernameQuery(
//                        "select username, authority form UserAuthorities " +
//                                "where username=?"
//                )
//                .passwordEncoder(new BCryptPasswordEncoder())
//        ;

        // ldap is not working, just let it go
//        auth.ldapAuthentication()
//                .userSearchBase("ou=people")
//                .userSearchFilter("(uid={0})")
//                .groupSearchBase("ou=groups")
//                .groupSearchFilter("member={0}")
//                .passwordCompare()
//                .passwordAttribute("userPassword")
////                .passwordEncoder(new BCryptPasswordEncoder())
//                .and()
//                .contextSource()
////                .url("ldap://127.0.0.1.com:33389/dc=tacocloud, dc=com");
//                .root("dc=tacocloud,dc=com")
//                .ldif("users.ldif");

        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder())
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/design", "/orders")
                .hasRole("USER")
            .antMatchers("/", "/**").permitAll()

            .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/design")
        ;
    }
}
