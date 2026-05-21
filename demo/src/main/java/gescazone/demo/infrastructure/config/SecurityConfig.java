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

/**
 * Configuración central de seguridad.
 *
 * ──────────────────────────────────────────────────────────────────────────────
 * ARQUITECTURA DE AUTENTICACIÓN
 * ──────────────────────────────────────────────────────────────────────────────
 *
 * Login del sistema → SOLO usuario/contraseña (form login).
 *   Google OAuth2 NO aparece en el login; los propietarios y funcionarios
 *   acceden exclusivamente con su número de documento y contraseña.
 *
 * Google OAuth2 → se usa SOLO para verificar identidad en 2 acciones internas:
 *   1. Agregar método de pago   (/pagosYCartera → botón "Agregar Método")
 *   2. Confirmar reserva de salón (/reservas    → botón "Confirmar Reserva")
 *
 *   Flujo:
 *     JS guarda datos pendientes en sesión
 *     → redirige a /oauth2/authorization/google?accion=PAGO|RESERVA
 *     → Google autentica
 *     → callback a /oauth2/callback/accion   (OAuth2ActionController)
 *     → el controller valida que el correo de Google coincida con session.usuarioLogueado
 *     → ejecuta la acción y redirige de vuelta con ?oauth=ok
 *
 * ──────────────────────────────────────────────────────────────────────────────
 * CSRF
 * ──────────────────────────────────────────────────────────────────────────────
 *   /api/** exento (clientes REST manejan sus propios tokens).
 *   /oauth2/** exento (Spring Security lo gestiona internamente).
 *
 * ──────────────────────────────────────────────────────────────────────────────
 * SESIONES
 * ──────────────────────────────────────────────────────────────────────────────
 *   Máximo 1 sesión activa por usuario.
 *   HttpSessionOAuth2AuthorizationRequestRepository asegura que el state/nonce
 *   de OAuth2 sobreviva el redirect cross-site a Google y de vuelta.
 *
 * ──────────────────────────────────────────────────────────────────────────────
 * JWT
 * ──────────────────────────────────────────────────────────────────────────────
 *   JwtAuthenticationFilter actúa SOLO sobre /api/**.
 *   Las vistas MVC usan sesión HTTP normal.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private CustomOAuth2UserService  oAuth2UserService;
    @Autowired private JwtAuthenticationFilter  jwtFilter;

    // ── Beans de infraestructura ──────────────────────────────────────────────

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

    // ── Cadena de filtros principal ───────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ── Proveedor de autenticación ────────────────────────────────────
            .authenticationProvider(authenticationProvider())

            // ── Filtro JWT (solo /api/**) ─────────────────────────────────────
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // ── Autorización por rutas ────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth

                // Públicas — incluye /logout para que Spring Security
                // pueda procesarlo aunque la sesión haya expirado
                .requestMatchers(
                    "/login", "/registro", "/logout",
                    "/css/**", "/js/**", "/img/**", "/fonts/**",
                    "/favicon.ico", "/error"
                ).permitAll()

                // OAuth2: authorize y callback son accesibles solo para usuarios
                // ya autenticados con sesión activa (form login previo).
                // Spring Security requiere que estas rutas sean accesibles
                // durante el flujo OAuth2; la validación de sesión activa la
                // hace OAuth2ActionController antes de ejecutar la acción.
                .requestMatchers(
                    "/oauth2/**",
                    "/login/oauth2/**"
                ).permitAll()

                // Acceso general autenticado
                .requestMatchers("/inicio")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // Solo administrador — gestión completa (escritura)
                .requestMatchers(
                    "/gestionDeDatos",
                    "/api/apartamentos/**", "/api/parqueaderos/**",
                    "/api/residentes/**",
                    // Escritura de salones y usuarios: solo admin
                    "/api/salones/crear", "/api/salones/actualizar/**",
                    "/api/salones/eliminar/**", "/api/salones/cambiar-estado",
                    "/api/usuarios/crear", "/api/usuarios/actualizar/**",
                    "/api/usuarios/eliminar/**", "/api/usuarios/todos",
                    "/api/usuarios/cambiar-contrasena"
                ).hasRole("ADMINISTRADOR")

                // Lectura de salones y usuarios: necesaria para validar el
                // formulario de reservas (todos los roles autenticados)
                .requestMatchers("/api/salones/**", "/api/usuarios/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // Administrador y propietario
                .requestMatchers("/pagosYCartera", "/api/pagos/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO")

                // Administrador y funcionario
                .requestMatchers("/controlDeAccesos", "/api/accesos/**")
                    .hasAnyRole("ADMINISTRADOR", "FUNCIONARIO")

                // Reservas: todos los roles autenticados pueden reservar
                .requestMatchers("/reservas", "/api/reservas/**")
                    .hasAnyRole("ADMINISTRADOR", "PROPIETARIO", "FUNCIONARIO")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // ── Form login (ÚNICO punto de entrada al sistema) ────────────────
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(formSuccessHandler())
                .failureHandler(formFailureHandler())
                .permitAll()
            )

            // ── OAuth2 / Google (SOLO para acciones internas, no para login) ──
            // El loginPage apunta a /login para que Spring sepa cuál es la página
            // de autenticación, pero NO se muestra el botón de Google en el HTML.
            // El flujo lo inicia JS desde dentro de la app, no el usuario en /login.
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                // Persiste state/nonce en sesión HTTP entre redirect a Google y callback
                .authorizationEndpoint(ep -> ep
                    .authorizationRequestRepository(
                        new HttpSessionOAuth2AuthorizationRequestRepository()
                    )
                )
                // Google usa OIDC (no OAuth2 puro), por eso oidcUserService
                .userInfoEndpoint(ui -> ui
                    .oidcUserService(oAuth2UserService)
                )
                // Tras autenticar con Google → OAuth2ActionController toma el control
                .successHandler(oauth2SuccessHandler())
                .failureHandler(oauth2FailureHandler())
            )

            // ── Logout ────────────────────────────────────────────────────────
            // FIX: el HTML usa <a href="/logout"> que genera un GET.
            // Spring Security 6 por defecto solo acepta POST para logout (CSRF).
            // Se exime /logout del CSRF para que funcione con GET desde los links.
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("GESCAZONE-SESSION", "GESCAZONE-REMEMBER", "JWT-TOKEN")
                .clearAuthentication(true)
                .permitAll()
            )

            // ── CSRF ──────────────────────────────────────────────────────────
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/logout")
            )

            // ── Gestión de sesiones ───────────────────────────────────────────
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?error=session_expired")
            )

            // ── Manejo de errores de acceso ───────────────────────────────────
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

    // ── Handlers: Form login ──────────────────────────────────────────────────

    /**
     * Tras login exitoso con usuario/contraseña:
     * guarda rol y nombre en sesión y redirige según rol.
     */
    private org.springframework.security.web.authentication.AuthenticationSuccessHandler
            formSuccessHandler() {
        return (request, response, authentication) -> {
            String rolCompleto = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("");

            String rolVista = switch (rolCompleto) {
                case "ROLE_ADMINISTRADOR" -> "Administrador";
                case "ROLE_PROPIETARIO"   -> "Propietario";
                case "ROLE_FUNCIONARIO"   -> "Funcionario";
                default                   -> "Desconocido";
            };

            HttpSession session = request.getSession();
            session.setAttribute("rolUsuario",      rolVista);
            session.setAttribute("usuarioLogueado", authentication.getName());

            String destino = "ROLE_FUNCIONARIO".equals(rolCompleto)
                    ? "/controlDeAccesos"
                    : "/inicio";
            response.sendRedirect(destino);
        };
    }

    /**
     * Tras login fallido con usuario/contraseña.
     */
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

    // ── Handlers: OAuth2 / Google ─────────────────────────────────────────────

    /**
     * Tras autenticación exitosa con Google:
     * redirige a OAuth2ActionController para ejecutar la acción pendiente.
     * NO crea una sesión de sistema; solo verifica identidad puntualmente.
     */
    private org.springframework.security.web.authentication.AuthenticationSuccessHandler
            oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            // El correo verificado por Google llega en el OidcUser
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String correoGoogle = oidcUser.getEmail();

            // Guardarlo temporalmente en sesión para que OAuth2ActionController lo use
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("oauth2CorreoVerificado", correoGoogle);
            }

            // Delegar al controller que ejecuta la acción pendiente
            response.sendRedirect("/oauth2/callback/accion");
        };
    }

    /**
     * Tras fallo en la autenticación con Google:
     * redirige con error a la página que inició el flujo.
     */
    private org.springframework.security.web.authentication.AuthenticationFailureHandler
            oauth2FailureHandler() {
        return (request, response, exception) -> {
            HttpSession session = request.getSession(false);
            String accionPendiente = (session != null)
                    ? (String) session.getAttribute("oauth2AccionPendiente")
                    : null;

            String destino = "RESERVA".equals(accionPendiente)
                    ? "/reservas?oauth=error"
                    : "/pagosYCartera?oauth=error";

            response.sendRedirect(destino);
        };
    }
}