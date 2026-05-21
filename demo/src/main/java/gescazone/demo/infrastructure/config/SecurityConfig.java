package gescazone.demo.infrastructure.config;

import gescazone.demo.infrastructure.security.CustomOAuth2UserService;
import gescazone.demo.infrastructure.security.CustomUserDetailsService;
import gescazone.demo.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
// FIX: securedEnabled = false para evitar que @Secured en controllers
// entre en conflicto con las reglas de authorizeHttpRequests.
// Solo se usa prePostEnabled para @PreAuthorize donde sea estrictamente necesario.
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = false)
public class SecurityConfig {

    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private CustomOAuth2UserService  oAuth2UserService;
    @Autowired private JwtAuthenticationFilter  jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth

                // ── 1. Rutas públicas ─────────────────────────────────────────
                .requestMatchers(
                    "/login", "/registro", "/logout",
                    "/css/**", "/js/**", "/img/**", "/fonts/**",
                    "/favicon.ico", "/error"
                ).permitAll()

                // ── 2. OAuth2 interno (necesario para el flujo Google) ────────
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                // ── 3. Vistas MVC — reglas específicas primero ────────────────
                // IMPORTANTE: los patrones más específicos SIEMPRE antes que los
                // generales. Spring evalúa en orden y para en el primer match.

                // Solo administrador
                .requestMatchers("/gestionDeDatos")
                    .hasRole("ADMINISTRADOR")

                // Administrador + propietario
                .requestMatchers("/pagosYCartera")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO")

                // Administrador + funcionario
                .requestMatchers("/controlDeAccesos")
                    .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                // Todos los autenticados
                .requestMatchers("/inicio", "/reservas")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // ── 4. API REST — escritura (solo admin), específicos primero ──
                .requestMatchers(
                    "/api/apartamentos/**",
                    "/api/parqueaderos/**",
                    "/api/residentes/**"
                ).hasRole("ADMINISTRADOR")

                // Escritura de salones — solo admin
                .requestMatchers(
                    "/api/salones/crear",
                    "/api/salones/actualizar/**",
                    "/api/salones/eliminar/**",
                    "/api/salones/cambiar-estado"
                ).hasRole("ADMINISTRADOR")

                // Escritura de usuarios — solo admin
                .requestMatchers(
                    "/api/usuarios/crear",
                    "/api/usuarios/actualizar/**",
                    "/api/usuarios/eliminar/**",
                    "/api/usuarios/todos",
                    "/api/usuarios/cambiar-contrasena"
                ).hasRole("ADMINISTRADOR")

                // Lectura de salones y usuarios — todos (necesario para reservas)
                .requestMatchers("/api/salones/**", "/api/usuarios/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // Pagos — admin y propietario
                .requestMatchers("/api/pagos/**", "/api/metodos-pago/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO")

                // Accesos — admin y funcionario
                .requestMatchers("/api/accesos/**")
                    .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                // Reservas API — todos los autenticados
                // FIX DELETE: el endpoint DELETE /api/reservas/eliminar/{id}
                // estaba siendo bloqueado por la combinación de CSRF y sesión.
                // Al tener /api/** exento de CSRF (línea csrf más abajo) y esta
                // regla explícita, cualquier rol autenticado puede hacer DELETE.
                // El control fino (solo admin puede eliminar) lo hace el service.
                .requestMatchers("/api/reservas/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // OAuth2 callback interno
                .requestMatchers("/oauth2/callback/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // Cualquier otra ruta autenticada
                .anyRequest().authenticated()
            )

            // ── Form login ────────────────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(formSuccessHandler())
                .failureHandler(formFailureHandler())
                .permitAll()
            )

            // ── OAuth2 Google (solo para acciones internas) ───────────────────
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .authorizationEndpoint(ep -> ep
                    .authorizationRequestRepository(
                        new HttpSessionOAuth2AuthorizationRequestRepository()
                    )
                )
                .userInfoEndpoint(ui -> ui
                    .oidcUserService(oAuth2UserService)
                )
                .successHandler(oauth2SuccessHandler())
                .failureHandler(oauth2FailureHandler())
            )

            // ── Logout ────────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("GESCAZONE-SESSION", "GESCAZONE-REMEMBER", "JWT-TOKEN")
                .clearAuthentication(true)
                .permitAll()
            )

            // ── CSRF ──────────────────────────────────────────────────────────
            // FIX DELETE: /api/** completamente exento de CSRF.
            // El fetch DELETE desde reservas.js no envía token CSRF y no debe
            // necesitarlo — las APIs REST se protegen con autenticación, no CSRF.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/logout", "/oauth2/**")
            )

            // ── Sesiones ──────────────────────────────────────────────────────
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?error=session_expired")
            )

            // ── Errores de acceso ─────────────────────────────────────────────
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, e) -> {
                    request.setAttribute("codigo",  403);
                    request.setAttribute("mensaje", "No tienes permisos para acceder a esta sección.");
                    request.setAttribute("ruta",    request.getRequestURI());
                    request.getRequestDispatcher("/error").forward(request, response);
                })
            );

        return http.build();
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private org.springframework.security.web.authentication.AuthenticationSuccessHandler
            formSuccessHandler() {
        return (request, response, authentication) -> {
            String rolCompleto = authentication.getAuthorities().stream()
                    .findFirst().map(a -> a.getAuthority()).orElse("");

            String rolVista = switch (rolCompleto) {
                case "ROLE_ADMINISTRADOR" -> "Administrador";
                case "ROLE_PROPIETARIO"   -> "Propietario";
                case "ROLE_FUNCIONARIO"   -> "Funcionario";
                default                   -> "Desconocido";
            };

            HttpSession session = request.getSession();
            session.setAttribute("rolUsuario",      rolVista);
            session.setAttribute("usuarioLogueado", authentication.getName());

            response.sendRedirect(
                "ROLE_FUNCIONARIO".equals(rolCompleto) ? "/controlDeAccesos" : "/inicio"
            );
        };
    }

    private org.springframework.security.web.authentication.AuthenticationFailureHandler
            formFailureHandler() {
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

    private org.springframework.security.web.authentication.AuthenticationSuccessHandler
            oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Guardar correo verificado para OAuth2ActionController
                session.setAttribute("oauth2CorreoVerificado", oidcUser.getEmail());

                // FIX Bug 2: restaurar la autenticación original (form login)
                // que estaba en sesión antes de iniciar el flujo OAuth2.
                // Sin esto, Spring reemplaza el Authentication con OidcUser
                // (ROLE_OAUTH2_TEMP) y las rutas protegidas devuelven 403.
                org.springframework.security.core.Authentication authOriginal =
                        (org.springframework.security.core.Authentication)
                        session.getAttribute("authOriginalAntesDeOAuth2");
                if (authOriginal != null) {
                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext().setAuthentication(authOriginal);
                    session.setAttribute(
                        org.springframework.security.web.context.HttpSessionSecurityContextRepository
                            .SPRING_SECURITY_CONTEXT_KEY,
                        org.springframework.security.core.context.SecurityContextHolder.getContext()
                    );
                    session.removeAttribute("authOriginalAntesDeOAuth2");
                }
            }
            response.sendRedirect("/oauth2/callback/accion");
        };
    }

    private org.springframework.security.web.authentication.AuthenticationFailureHandler
            oauth2FailureHandler() {
        return (request, response, exception) -> {
            HttpSession session = request.getSession(false);
            String accion = (session != null)
                    ? (String) session.getAttribute("oauth2AccionPendiente")
                    : null;
            response.sendRedirect(
                "RESERVA".equals(accion) ? "/reservas?oauth=error" : "/pagosYCartera?oauth=error"
            );
        };
    }
}