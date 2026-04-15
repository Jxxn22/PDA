package gescazone.demo.presentation.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CerrarSesionController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión
        session.invalidate();
        
        // Redirigir al login
        return "redirect:/login?logout";
    }
}