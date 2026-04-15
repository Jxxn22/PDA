package gescazone.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos (CSS, JS, imágenes)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/assets/**").permitAll()
                
                // Página de login pública
                .requestMatchers("/login", "/logout").permitAll()
                
                // API de login pública
                .requestMatchers("/api/usuarios/login").permitAll()
                
                // Gestión de Datos - Solo Administrador
                .requestMatchers("/gestionDeDatos", "/api/apartamentos/**", "/api/parqueaderos/**", 
                                "/api/salones/**", "/api/usuarios/**", "/api/residentes/**", 
                                "/api/catalogos/**").hasRole("ADMINISTRADOR")
                
                // Pagos y Cartera - Administrador y Propietario
                .requestMatchers("/pagosYCartera", "/api/pagos/**").hasAnyRole("ADMINISTRADOR", "PROPIETARIO")
                
                // Reservas - Todos los roles autenticados
                .requestMatchers("/reservas", "/api/reservas/**").authenticated()
                
                // Control de Accesos - Administrador y Funcionario
                .requestMatchers("/controlDeAccesos", "/api/accesos/**").hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")
                
                // Inicio y Perfil - Todos los autenticados
                .requestMatchers("/inicio", "/profile").authenticated()
                
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Desactivar CSRF solo para API
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }
}
