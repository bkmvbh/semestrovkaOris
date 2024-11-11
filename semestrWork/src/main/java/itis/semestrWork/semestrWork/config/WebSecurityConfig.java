package itis.semestrWork.semestrWork.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled=true)
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers( "/static/**","/usercheck","/registration","/registration/new","/static/**").permitAll()
//                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
//                        .anyRequest().authenticated()
//                );
//        http.formLogin(formLogin ->
//                        formLogin
//                                .loginPage("/login")
//                                .loginProcessingUrl("/usercheck")// /login (POST)
//                                .usernameParameter("username")
//                                .passwordParameter("password")
//                                .successHandler((request, response, authentication) -> {
//                                    if (authentication.getAuthorities().stream()
//                                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
//                                        response.sendRedirect("/admin/");
//                                    } else {
//                                        response.sendRedirect("/");
//                                    }
//                                })
//                                .failureUrl("/login?error=true")
//                                .permitAll())
//                .logout((logout) ->
//                        logout.permitAll()
//                );
//
//        http.csrf(customizer -> customizer.disable());
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers( "/usercheck").permitAll()
                        .requestMatchers( "/registration/**").permitAll()
                        .requestMatchers( "/static/**").permitAll()
                        .anyRequest().authenticated()
                );
        http.formLogin(formLogin ->
                        formLogin
                                .loginPage("/login").permitAll()
                                .loginProcessingUrl("/usercheck")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/main")
                                .failureUrl("/login?error")
                                .permitAll())
                .logout(LogoutConfigurer::permitAll
                );

        return http.build();
    }
}