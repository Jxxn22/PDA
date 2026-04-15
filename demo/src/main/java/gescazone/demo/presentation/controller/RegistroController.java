package gescazone.demo.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.repository.UsuarioRepository;
import jakarta.validation.Valid;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Muestra el formulario de registro
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioModel());
        return "registro"; // → templates/registro.html
    }

    /**
     * Procesa el formulario de registro
     */
    @PostMapping("/registro")
    public String procesarRegistro(
            @Valid @ModelAttribute("usuario") UsuarioModel usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 1. Mostrar errores de validación de campos
        if (result.hasErrors()) {
            return "registro";
        }

        // 2. Verificar que el número de documento no esté en uso
        if (usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento()).isPresent()) {
            model.addAttribute("errorDocumento", "Ya existe un usuario con ese número de documento.");
            return "registro";
        }

        // 3. Encriptar contraseña antes de guardar
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // 4. Guardar usuario
        usuarioRepository.save(usuario);

        // 5. Redirigir al login con mensaje de éxito
        redirectAttributes.addFlashAttribute("registroExitoso",
                "Registro exitoso. Por favor inicia sesión.");
        return "redirect:/login";
    }
}