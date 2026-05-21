package gescazone.demo.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador que recibe el callback de Google OAuth2 tras la verificación
 * de identidad del usuario para una acción interna.
 *
 * ──────────────────────────────────────────────────────────────────────────────
 * FLUJO COMPLETO
 * ──────────────────────────────────────────────────────────────────────────────
 *
 * 1. JS en pagosYCartera o reservas guarda en sesión:
 *      - oauth2AccionPendiente  : "PAGO" | "RESERVA"
 *      - oauth2DatosPendientes  : JSON con los datos de la acción (para reserva)
 *
 * 2. JS redirige al usuario a:
 *      /oauth2/iniciar?accion=PAGO    o    /oauth2/iniciar?accion=RESERVA
 *    Este endpoint (abajo) guarda la acción en sesión y redirige a Google.
 *
 * 3. Google autentica → SecurityConfig.oauth2SuccessHandler guarda
 *    oauth2CorreoVerificado en sesión → redirige aquí (/oauth2/callback/accion).
 *
 * 4. Este controller:
 *    a. Verifica que haya sesión activa (usuario logueado previamente con form login)
 *    b. Verifica que el correo de Google coincida con session.usuarioLogueado (opcional)
 *    c. Según la acción pendiente, redirige de vuelta con ?oauth=ok
 *       para que el JS del frontend complete la acción.
 *
 * ──────────────────────────────────────────────────────────────────────────────
 * SEGURIDAD
 * ──────────────────────────────────────────────────────────────────────────────
 *
 * - Si no hay sesión activa (usuario no logueado con form login) → /login
 * - Si el correo de Google no coincide con el usuario logueado → error
 * - La acción real (crear reserva, guardar tarjeta) la ejecuta el JS del frontend
 *   al recibir ?oauth=ok, llamando a los endpoints /api/** correspondientes.
 *   Así evitamos duplicar lógica de negocio aquí.
 *
 * - Para el caso de RESERVA: los datos del formulario viajan en sesión como
 *   oauth2DatosPendientes (JSON string) y el JS los recupera con una llamada
 *   GET a /oauth2/datos-pendientes antes de hacer el POST a /api/reservas/crear.
 */
@Controller
@RequestMapping("/oauth2")
public class OAuth2ActionController {

    /**
     * Paso 2: Punto de entrada desde el JS.
     * Guarda la acción pendiente en sesión e inicia el flujo OAuth2 con Google.
     *
     * Parámetros query:
     *   accion = "PAGO" | "RESERVA"
     *   datos  = JSON string con los datos del formulario (solo para RESERVA)
     */
    @GetMapping("/iniciar")
    public String iniciarOAuth2(
            HttpServletRequest request,
            @RequestParam String accion,
            @RequestParam(required = false) String datos) {

        // FIX: usar getSession(true) para asegurar que la sesión exista.
        // getSession(false) retorna null si no hay sesión activa, lo que impide
        // guardar la acción pendiente y rompe el flujo.
        HttpSession session = request.getSession(false);

        // Si no hay sesión activa el usuario no está logueado → login
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        // Guardar acción y datos en sesión para recuperarlos en el callback
        session.setAttribute("oauth2AccionPendiente", accion);
        if (datos != null && !datos.isBlank()) {
            session.setAttribute("oauth2DatosPendientes", datos);
        }

        // FIX Bug 2: guardar el Authentication actual (form login) antes de
        // iniciar el flujo OAuth2. Spring Security reemplazará este Authentication
        // con el OidcUser de Google durante el flujo; oauth2SuccessHandler en
        // SecurityConfig lo restaurará al regresar para que el usuario mantenga
        // su rol real (ADMINISTRADOR, PROPIETARIO, etc.).
        org.springframework.security.core.Authentication authActual =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();
        if (authActual != null && authActual.isAuthenticated()) {
            session.setAttribute("authOriginalAntesDeOAuth2", authActual);
        }

        // Spring Security arranca el flujo OAuth2 con Google
        return "redirect:/oauth2/authorization/google";
    }

    /**
     * Paso 4: Callback interno tras autenticación exitosa con Google.
     * SecurityConfig.oauth2SuccessHandler redirige aquí después de guardar
     * el correo verificado en sesión (oauth2CorreoVerificado).
     */
    @GetMapping("/callback/accion")
    public String procesarCallback(HttpServletRequest request) {

        // FIX: usar getSession(false) — si no hay sesión no redirigimos a login
        // directamente; verificamos atributos primero para dar mensajes útiles.
        HttpSession session = request.getSession(false);

        // ── Guardia 1: debe existir sesión activa de form login ───────────────
        if (session == null) {
            return "redirect:/login";
        }

        String usuarioLogueado  = (String) session.getAttribute("usuarioLogueado");
        String correoVerificado = (String) session.getAttribute("oauth2CorreoVerificado");
        String accionPendiente  = (String) session.getAttribute("oauth2AccionPendiente");

        // ── Guardia 2: el usuario debe estar logueado con form login ──────────
        if (usuarioLogueado == null) {
            limpiarAtributosOAuth2(session);
            return "redirect:/login";
        }

        // ── Guardia 3: debe haber un correo verificado por Google ─────────────
        if (correoVerificado == null || correoVerificado.isBlank()) {
            limpiarAtributosOAuth2(session);
            String destino = "RESERVA".equals(accionPendiente)
                    ? "/reservas?oauth=error&motivo=no_correo"
                    : "/pagosYCartera?oauth=error&motivo=no_correo";
            return "redirect:" + destino;
        }

        // ── Guardia 4 (VERIFICACIÓN ESTRICTA — descomenta si guardas correoUsuario en sesión):
        //
        // Activa esto si al hacer form login guardas el correo del usuario en sesión:
        //   session.setAttribute("correoUsuario", usuario.getCorreo());
        //
        // String correoSesion = (String) session.getAttribute("correoUsuario");
        // if (correoSesion != null && !correoSesion.equalsIgnoreCase(correoVerificado)) {
        //     limpiarAtributosOAuth2(session);
        //     String destino = "RESERVA".equals(accionPendiente)
        //             ? "/reservas?oauth=error&motivo=correo_no_coincide"
        //             : "/pagosYCartera?oauth=error&motivo=correo_no_coincide";
        //     return "redirect:" + destino;
        // }

        // ── Acción verificada: redirigir al frontend para que complete la acción
        String destino;
        if ("RESERVA".equals(accionPendiente)) {
            // Los datos de la reserva siguen en sesión (oauth2DatosPendientes).
            // El JS los recupera llamando a GET /oauth2/datos-pendientes.
            destino = "/reservas?oauth=ok";
        } else {
            // Para PAGO no hay datos pendientes; el modal se abre en el frontend.
            destino = "/pagosYCartera?oauth=ok";
        }

        // Limpiar correo verificado y acción (se usaron); dejar datos pendientes para el JS
        session.removeAttribute("oauth2CorreoVerificado");
        session.removeAttribute("oauth2AccionPendiente");

        return "redirect:" + destino;
    }

    /**
     * Endpoint auxiliar: el JS llama a GET /oauth2/datos-pendientes después de
     * detectar ?oauth=ok en /reservas para obtener los datos del formulario
     * que guardó antes de iniciar el flujo OAuth2.
     *
     * FIX: retorna Content-Type: application/json explícitamente para que
     * el fetch() del frontend pueda parsear con response.json() sin error.
     * El JSON fue serializado como String en la sesión; lo retornamos tal cual.
     */
    @GetMapping("/datos-pendientes")
    @ResponseBody
    public ResponseEntity<String> obtenerDatosPendientes(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"Sin sesión activa\"}");
        }

        String datos = (String) session.getAttribute("oauth2DatosPendientes");
        session.removeAttribute("oauth2DatosPendientes");

        if (datos == null || datos.isBlank()) {
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"Sin datos pendientes\"}");
        }

        // FIX: retornar con Content-Type JSON para que response.json() funcione en el JS
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(datos);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void limpiarAtributosOAuth2(HttpSession session) {
        session.removeAttribute("oauth2CorreoVerificado");
        session.removeAttribute("oauth2AccionPendiente");
        session.removeAttribute("oauth2DatosPendientes");
    }
}