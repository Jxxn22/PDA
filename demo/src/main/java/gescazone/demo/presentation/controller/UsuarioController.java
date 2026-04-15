package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.application.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Mostrar lista de todos los usuarios
     */
    @GetMapping("/lista")
    public String consultarTodos(Model model, HttpSession session) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuarios", usuarioService.consultarTodos());
        return "usuarios/lista";
    }

    /**
     * Mostrar formulario para crear usuario
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuario", new UsuarioModel());
        return "usuarios/formulario";
    }

    /**
     * Crear un nuevo usuario
     */
    @PostMapping("/crear")
    public String crear(@ModelAttribute UsuarioModel usuario,
                        RedirectAttributes redirectAttributes) {
        try {
            String mensaje = usuarioService.crear(usuario);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/usuarios/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/usuarios/nuevo";
        }
    }

    /**
     * Mostrar formulario para editar usuario
     */
    @GetMapping("/editar/{documento}")
    public String mostrarFormularioEditar(@PathVariable String documento,
                                          Model model,
                                          RedirectAttributes redirectAttributes,
                                          HttpSession session) {
        // Verificar autenticación
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        try {
            UsuarioModel usuario = usuarioService.consultar(documento);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/usuarios/lista";
            }
            model.addAttribute("usuario", usuario);
            return "usuarios/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/lista";
        }
    }

    /**
     * Actualizar usuario
     */
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute UsuarioModel usuario,
                            RedirectAttributes redirectAttributes) {
        try {
            String mensaje = usuarioService.actualizar(usuario);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/usuarios/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/editar/" + usuario.getNumeroDocumento();
        }
    }

    /**
     * Eliminar usuario
     */
    @GetMapping("/eliminar/{documento}")
    public String eliminar(@PathVariable String documento,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        // Verificar autenticación
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        try {
            String mensaje = usuarioService.eliminar(documento);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios/lista";
    }

    /**
     * Consultar usuario por documento
     */
    @GetMapping("/consultar/{documento}")
    public String consultar(@PathVariable String documento,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {
        // Verificar autenticación
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        try {
            UsuarioModel usuario = usuarioService.consultar(documento);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/usuarios/lista";
            }
            model.addAttribute("usuario", usuario);
            return "usuarios/detalle";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/lista";
        }
    }

    /**
     * Consultar usuarios por tipo de documento (por nombre)
     */
    @GetMapping("/tipo-documento/{tipoDocumento}")
    public String consultarPorTipoDocumento(@PathVariable String tipoDocumento,
                                            Model model,
                                            RedirectAttributes redirectAttributes,
                                            HttpSession session) {
        // Verificar autenticación
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        try {
            model.addAttribute("usuarios", usuarioService.consultarPorTipoDocumento(tipoDocumento));
            model.addAttribute("filtro", "Tipo de documento: " + tipoDocumento);
            return "usuarios/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/lista";
        }
    }

    /**
     * Consultar usuarios por cargo (nombre del rol)
     */
    @GetMapping("/cargo/{cargo}")
    public String consultarPorCargo(@PathVariable String cargo,
                                    Model model,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        // Verificar autenticación
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        try {
            model.addAttribute("usuarios", usuarioService.consultarPorCargo(cargo));
            model.addAttribute("filtro", "Cargo: " + cargo);
            return "usuarios/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/lista";
        }
    }

    /**
     * Mostrar formulario de cambio de contraseña
     */
    @GetMapping("/cambiar-contrasena")
    public String mostrarFormularioCambioContrasena(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "usuarios/cambiar-contrasena";
    }

    /**
     * Cambiar contraseña
     */
    @PostMapping("/cambiar-contrasena")
    public String cambiarContrasena(@RequestParam String contrasenaActual,
                                    @RequestParam String contrasenaNueva,
                                    @RequestParam String confirmarContrasena,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        
        try {
            String documento = (String) session.getAttribute("usuarioLogueado");
            
            // Validar que las contraseñas coincidan
            if (!contrasenaNueva.equals(confirmarContrasena)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/usuarios/cambiar-contrasena";
            }
            
            String mensaje = usuarioService.cambiarContrasena(documento, contrasenaActual, contrasenaNueva);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/inicio";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/cambiar-contrasena";
        }
    }
}