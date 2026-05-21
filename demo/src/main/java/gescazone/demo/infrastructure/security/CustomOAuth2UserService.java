package gescazone.demo.infrastructure.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.repository.UsuarioRepository;

/**
 * Servicio OIDC para Google.
 *
 * IMPORTANTE — este flujo NO es para iniciar sesión.
 * El login del sistema usa usuario/contraseña (form login).
 *
 * Google OAuth2 se usa SOLO para verificar la identidad del usuario
 * logueado en dos acciones internas:
 *   1. Agregar un método de pago en /pagosYCartera
 *   2. Confirmar una reserva de salón en /reservas
 *
 * Por eso esta clase:
 *   - NO bloquea si el correo no está registrado (solo construye el OidcUser)
 *   - El correo verificado queda en sesión para que OAuth2ActionController
 *     lo use al ejecutar la acción pendiente
 *
 * El bloqueo real ocurre en OAuth2ActionController, que compara el correo
 * de Google con el correo del usuario de sesión (session.usuarioLogueado).
 */
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    // Delega la llamada real al UserInfo Endpoint de Google
    private final OidcUserService delegate = new OidcUserService();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. Obtener los atributos OIDC desde Google
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String correo = oidcUser.getEmail();

        if (correo == null || correo.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"),
                    "Google no retornó el correo del usuario."
            );
        }

        // 2. Buscar el usuario en MongoDB por su correo para obtener su rol real.
        //    Si no existe, asignamos un rol temporal de solo lectura.
        //    OAuth2ActionController validará que el correo coincida con la sesión activa.
        String authority;
        try {
            UsuarioModel usuario = usuarioRepository.findByCorreo(correo)
                    .orElse(null);
            authority = (usuario != null)
                    ? "ROLE_" + usuario.getRol().getNombreRol().toUpperCase()
                    : "ROLE_OAUTH2_TEMP";
        } catch (Exception e) {
            // Si falla la consulta a MongoDB, no bloqueamos el flujo;
            // OAuth2ActionController rechazará si el correo no coincide
            authority = "ROLE_OAUTH2_TEMP";
        }

        // 3. Retornar DefaultOidcUser con el rol y los claims de Google.
        //    "email" como nameAttributeKey → getName() retorna el correo.
        return new DefaultOidcUser(
                Collections.singletonList(new SimpleGrantedAuthority(authority)),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"
        );
    }
}