package gescazone.demo.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT — actúa SOLO sobre rutas /api/**.
 *
 * Las rutas de vistas MVC usan sesión HTTP (form login).
 * Las rutas OAuth2 (/oauth2/**, /login/oauth2/**) usan la sesión HTTP también.
 * Por eso shouldNotFilter excluye todo lo que no sea /api/.
 *
 * Si hay sesión HTTP activa (form login), el filtro respeta esa autenticación
 * y no la sobreescribe (SecurityContextHolder ya tendrá un Authentication).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extraerTokenDeCookie(request);

            // Solo autenticar si hay token, es válido, y no hay autenticación previa
            if (token != null
                    && jwtTokenProvider.validateToken(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                String numeroDocumento = jwtTokenProvider.getNumeroDocumentoFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(numeroDocumento);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.out.println("[JWT] No se pudo autenticar: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * El filtro JWT solo interviene en /api/**.
     * Vistas MVC, OAuth2 y recursos estáticos se excluyen.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/");
    }

    private String extraerTokenDeCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("JWT-TOKEN".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}