package gescazone.demo.infrastructure.config;

import gescazone.demo.infrastructure.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Dentro de SecurityConfig:
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            boolean isFuncionario = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_FUNCIONARIO"));

            if (isAdmin) {
                response.sendRedirect("/inicio");
            } else if (isFuncionario) {
                response.sendRedirect("/controlDeAccesos");
            } else {
                response.sendRedirect("/inicio"); // PROPIETARIO u otros
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String error = switch (exception.getClass().getSimpleName()) {
                case "BadCredentialsException"        -> "bad_credentials";
                case "UsernameNotFoundException"      -> "user_not_found";
                case "DisabledException"              -> "disabled";
                case "SessionAuthenticationException" -> "session_limit";
                default                               -> "true";
            };
            response.sendRedirect("/login?error=" + error);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

                // ── Rutas públicas ──────────────────────────────────────────
                .requestMatchers(
                    "/login",
                    "/registro",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/favicon.ico",
                    "/error"          // evita loops en páginas de error
                ).permitAll()

                // ── Rutas compartidas (todos los roles autenticados) ────────
                // FIX: /inicio estaba cayendo en anyRequest() sin estar
                // explícitamente permitido, causando el redirect loop
                .requestMatchers("/inicio").hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // ── Solo ADMINISTRADOR ──────────────────────────────────────
                .requestMatchers(
                    "/gestionDeDatos",
                    "/api/apartamentos/**",
                    "/api/parqueaderos/**",
                    "/api/salones/**",
                    "/api/usuarios/**",
                    "/api/residentes/**"
                ).hasRole("ADMINISTRADOR")

                // ── ADMINISTRADOR + PROPIETARIO ─────────────────────────────
                .requestMatchers("/pagosYCartera", "/api/pagos/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO")

                // ── ADMINISTRADOR + FUNCIONARIO ─────────────────────────────
                .requestMatchers("/controlDeAccesos", "/api/accesos/**")
                    .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                // ── Cualquier otra ruta requiere autenticación ──────────────
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") // URL donde Spring procesa el POST
                .successHandler(customSuccessHandler())
                .failureHandler(customFailureHandler())
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )

            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )

            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?error=session_expired")
            );

        return http.build();
    }
}