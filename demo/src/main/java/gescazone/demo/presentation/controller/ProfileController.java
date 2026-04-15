package gescazone.demo.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.application.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String showProfile(HttpSession session, Model model) {
        // Obtener documento del usuario de la sesión
        String documentoUsuario = (String) session.getAttribute("usuarioLogueado");
        
        if (documentoUsuario == null) {
            return "redirect:/login";
        }
        
        // Cargar datos del usuario desde la base de datos
        UsuarioModel usuario = usuarioService.consultar(documentoUsuario);
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Agregar datos del usuario al modelo
        model.addAttribute("usuario", usuario);
        
        return "profile";
    }
}