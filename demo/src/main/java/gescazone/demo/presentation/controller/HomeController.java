package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.application.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String index() {
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
    public String profile(HttpSession session, Model model) {
        String documentoUsuario = (String) session.getAttribute("usuarioLogueado");
        if (documentoUsuario == null)
            return "redirect:/login";

        UsuarioModel usuario = usuarioService.consultar(documentoUsuario);
        if (usuario == null)
            return "redirect:/login";

        model.addAttribute("usuario", usuario);
        return "profile";
    }
}