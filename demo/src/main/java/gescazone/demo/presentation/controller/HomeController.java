package gescazone.demo.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index(HttpSession session) {
        // Si ya está logueado, redirigir al inicio
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/inicio";
        }
        return "login"; // Mostrar login si no está autenticado
    }
    
    @GetMapping("/inicio")
    public String inicio(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "inicio";
    }
    
    @GetMapping("/gestionDeDatos")
    public String gestionDeDatos(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "gestionDeDatos";
    }
    
    @GetMapping("/pagosYCartera")
    public String pagosYCartera(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "pagosYCartera";
    }
    
    @GetMapping("/reservas")
    public String reservas(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "reservas";
    }
    
    @GetMapping("/controlDeAccesos")
    public String controlDeAccesos(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "controlDeAccesos";
    }
    
    /*
@GetMapping("/profile")
public String profile(HttpSession session) {
    if (session.getAttribute("usuarioLogueado") == null) {
        return "redirect:/login";
    }
    return "profile";
}
*/
}