package br.com.nexoimob.config;

import br.com.nexoimob.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, UsuarioRepository usuarios) throws Exception {
        http.authorizeHttpRequests(regras -> regras
                .requestMatchers("/css/**", "/js/**", "/login", "/cadastro",
                        "/recuperar", "/redefinir", "/error").permitAll()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        http.formLogin(login -> login.loginPage("/login")
                .defaultSuccessUrl("/", true).permitAll());
        http.logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        http.exceptionHandling(erros -> erros.accessDeniedPage("/acesso-negado"));
        http.addFilterBefore(new VerificarSessaoFilter(usuarios),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
