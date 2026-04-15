package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.RegistroAccesoPiscinaModel;
import gescazone.demo.application.service.RegistroAccesoPiscinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/piscina")
public class RegistroAccesoPiscinaController {
    
    @Autowired
    private RegistroAccesoPiscinaService registroService;
    
    /**
     * Mostrar formulario de registro de acceso
     */
    @GetMapping("/acceso")
    public String mostrarFormularioAcceso() {
        return "piscina/registro-acceso";
    }
    
    /**
     * Procesar registro de ingreso
     */
    @PostMapping("/registrar-ingreso")
    public String registrarIngreso(
            @RequestParam String numeroApartamento,
            @RequestParam Integer numeroDocumento,
            RedirectAttributes redirectAttributes) {
        try {
            String mensaje = registroService.registrarIngreso(numeroApartamento, numeroDocumento);
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/piscina/acceso";
    }
    
    
    /**
     * Listar todos los registros
     */
    @GetMapping("/registros")
    public String listarRegistros(Model model) {
        List<RegistroAccesoPiscinaModel> registros = registroService.consultarTodos();
        model.addAttribute("registros", registros);
        model.addAttribute("titulo", "Todos los Registros");
        return "piscina/lista-registros";
    }

    /**
     * Listar registros de hoy
     */
    @GetMapping("/hoy")
    public String listarRegistrosHoy(Model model) {
        List<RegistroAccesoPiscinaModel> registros = registroService.consultarRegistrosHoy();
        model.addAttribute("registros", registros);
        model.addAttribute("titulo", "Registros de Hoy");
        return "piscina/lista-registros";
    }
    
    /**
     * Eliminar registro
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarRegistro(
        @PathVariable String id,
        RedirectAttributes redirectAttributes) {
            try {
                String mensaje = registroService.eliminar(id);
                redirectAttributes.addFlashAttribute("mensaje", mensaje);
                redirectAttributes.addFlashAttribute("tipo", "success");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
                redirectAttributes.addFlashAttribute("tipo", "danger");
            }
            return "redirect:/piscina/registros";
            }
    
    /**
     * Verificar si un apartamento puede acceder
     */
    @GetMapping("/verificar-acceso")
    public String verificarAcceso(
            @RequestParam String numeroApartamento,
            Model model) {
        try {
            boolean puedeAcceder = registroService.verificarAcceso(numeroApartamento);
            model.addAttribute("numeroApartamento", numeroApartamento);
            model.addAttribute("puedeAcceder", puedeAcceder);
            model.addAttribute("mensaje", puedeAcceder ? 
                "El apartamento puede acceder a la piscina" : 
                "El apartamento NO puede acceder (pagos pendientes)");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            model.addAttribute("tipo", "danger");
        }
        return "piscina/verificar-acceso";
    }
}