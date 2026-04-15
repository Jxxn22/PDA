package gescazone.demo.presentation.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        // Si ya está autenticado, redirigir a inicio
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/inicio";
        }
        
        // Mensajes de error o logout
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("logout", "Has cerrado sesión correctamente");
        }
        
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio";
    }
    
    @GetMapping("/gestionDeDatos")
    public String gestionDeDatos() {
        return "gestionDeDatos";
    }
    
    @GetMapping("/pagosYCartera")
    public String pagosYCartera() {
        return "pagosYCartera";
    }
    
    @GetMapping("/reservas")
    public String reservas() {
        return "reservas";
    }
    
    @GetMapping("/controlDeAccesos")
    public String controlDeAccesos() {
        return "controlDeAccesos";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}