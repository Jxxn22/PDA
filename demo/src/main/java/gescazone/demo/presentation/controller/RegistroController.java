package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.RolModel;
import gescazone.demo.domain.model.TipoDocumentoModel;
import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String[] TIPOS_DOCUMENTO = {
        "Cédula de Ciudadanía",
        "Cédula de Extranjería",
        "Pasaporte",
        "Tarjeta de Identidad"
    };

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new UsuarioModel());
        model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @Valid @ModelAttribute("usuario") UsuarioModel usuario,
            BindingResult result,
            @RequestParam(required = false) String nombreTipoDocumento,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
            return "registro";
        }

        if (nombreTipoDocumento == null || nombreTipoDocumento.trim().isEmpty()) {
            model.addAttribute("errorDocumento", "Debe seleccionar un tipo de documento.");
            model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
            return "registro";
        }

        if (usuarioRepository.existsByNumeroDocumento(usuario.getNumeroDocumento().trim())) {
            model.addAttribute("errorDocumento", "Ya existe un usuario con ese número de documento.");
            model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
            return "registro";
        }

        if (usuarioRepository.existsByCorreo(usuario.getCorreo().trim())) {
            model.addAttribute("errorDocumento", "Ya existe un usuario con ese correo.");
            model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
            return "registro";
        }

        try {
            // Asignar tipo de documento
            TipoDocumentoModel tipoDoc = new TipoDocumentoModel();
            tipoDoc.setNombreTipoDocumento(nombreTipoDocumento.trim());
            usuario.setTipoDocumento(tipoDoc);

            // Asignar rol PROPIETARIO por defecto
            RolModel rol = new RolModel();
            rol.setNombreRol("PROPIETARIO");
            usuario.setRol(rol);

            // Encriptar contraseña
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute("registroExitoso",
                "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorDocumento", "Error al registrar. Intente más tarde.");
            model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
            return "registro";
        }
    }
}