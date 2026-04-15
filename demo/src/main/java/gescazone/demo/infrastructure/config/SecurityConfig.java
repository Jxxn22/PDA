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

    // ─────────────────────────────────────────────
    // 1. ENCODER DE CONTRASEÑAS
    // ─────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ─────────────────────────────────────────────
    // 2. PROVEEDOR DE AUTENTICACIÓN
    //    Conecta UserDetailsService + PasswordEncoder
    // ─────────────────────────────────────────────
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ─────────────────────────────────────────────
    // 3. AUTHENTICATION MANAGER
    //    Necesario para autenticar manualmente desde controladores
    // ─────────────────────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ─────────────────────────────────────────────
    // 4. PUBLICADOR DE EVENTOS DE SESIÓN
    //    Requerido para que maximumSessions funcione correctamente
    // ─────────────────────────────────────────────
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // ─────────────────────────────────────────────
    // 5. HANDLER DE ÉXITO: redirige según el rol
    // ─────────────────────────────────────────────
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
                // PROPIETARIO u otro rol autenticado
                response.sendRedirect("/inicio");
            }
        };
    }

    // ─────────────────────────────────────────────
    // 6. HANDLER DE FALLO: distingue tipo de error
    // ─────────────────────────────────────────────
    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String errorParam;
            String exceptionName = exception.getClass().getSimpleName();

            switch (exceptionName) {
                case "BadCredentialsException"       -> errorParam = "bad_credentials";
                case "UsernameNotFoundException"     -> errorParam = "user_not_found";
                case "DisabledException"             -> errorParam = "disabled";
                case "SessionAuthenticationException"-> errorParam = "session_limit";
                default                              -> errorParam = "true";
            }

            response.sendRedirect("/login?error=" + errorParam);
        };
    }

    // ─────────────────────────────────────────────
    // 7. CADENA DE FILTROS DE SEGURIDAD
    // ─────────────────────────────────────────────
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ── Registrar el proveedor de autenticación ──────────────────────
            .authenticationProvider(authenticationProvider())

            // ── Autorización por rutas ────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth

                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/img/**", "/assets/**", "/favicon.ico").permitAll()

                // Páginas públicas
                .requestMatchers("/login", "/registro", "/logout").permitAll()

                // API de registro/login pública
                .requestMatchers("/api/registro", "/api/usuarios/login").permitAll()

                // ── ADMINISTRADOR ─────────────────────────────────────────────
                .requestMatchers(
                    "/gestionDeDatos",
                    "/api/apartamentos/**",
                    "/api/parqueaderos/**",
                    "/api/salones/**",
                    "/api/usuarios/**",
                    "/api/residentes/**",
                    "/api/catalogos/**"
                ).hasRole("ADMINISTRADOR")

                // ── ADMINISTRADOR + PROPIETARIO ───────────────────────────────
                .requestMatchers("/pagosYCartera", "/api/pagos/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO")

                // ── ADMINISTRADOR + FUNCIONARIO ───────────────────────────────
                .requestMatchers("/controlDeAccesos", "/api/accesos/**")
                    .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                // ── TODOS LOS ROLES AUTENTICADOS ──────────────────────────────
                .requestMatchers("/reservas", "/api/reservas/**").authenticated()
                .requestMatchers("/inicio", "/profile").authenticated()

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // ── Formulario de login ──────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customSuccessHandler())   // redirección por rol
                .failureHandler(customFailureHandler())   // mensajes de error precisos
                .permitAll()
            )

            // ── Logout ───────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )

            // ── CSRF: desactivado solo para la API REST ───────────────────────
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )

            // ── Manejo de sesiones ───────────────────────────────────────────
            .sessionManagement(session -> session
                .maximumSessions(1)                   // un solo dispositivo a la vez
                .maxSessionsPreventsLogin(false)       // expira sesión anterior en vez de bloquear
                .expiredUrl("/login?error=session_expired")
            )

            // ── Página de acceso denegado (403) ──────────────────────────────
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/login?error=access_denied")
            );

        return http.build();
    }
}