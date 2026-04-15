package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.SalonSocialModel;
import gescazone.demo.domain.model.EstadoModel;
import gescazone.demo.application.service.SalonSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/salones")
public class SalonSocialController {

    @Autowired
    private SalonSocialService salonService;

    @GetMapping("/lista")
    public String consultarTodos(Model model) {
        model.addAttribute("salones", salonService.consultarTodos());
        return "salones/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("salon", new SalonSocialModel());
        return "salones/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute SalonSocialModel salon,
                        @RequestParam String nombreEstado,
                        RedirectAttributes redirectAttributes) {
        try {
            salon.setEstado(new EstadoModel(nombreEstado));
            String mensaje = salonService.crear(salon);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/salones/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("salon", salon);
            return "redirect:/salones/nuevo";
        }
    }

    @GetMapping("/editar/{numero}")
    public String mostrarFormularioEditar(@PathVariable String numero,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            SalonSocialModel salon = salonService.consultar(numero);
            if (salon == null) {
                redirectAttributes.addFlashAttribute("error", "Salón social no encontrado");
                return "redirect:/salones/lista";
            }
            model.addAttribute("salon", salon);
            return "salones/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salones/lista";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute SalonSocialModel salon,
                             @RequestParam String nombreEstado,
                             RedirectAttributes redirectAttributes) {
        try {
            salon.setEstado(new EstadoModel(nombreEstado));
            String mensaje = salonService.actualizar(salon);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/salones/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salones/editar/" + salon.getNumero();
        }
    }

    @GetMapping("/eliminar/{numero}")
    public String eliminar(@PathVariable String numero,
                           RedirectAttributes redirectAttributes) {
        try {
            String mensaje = salonService.eliminar(numero);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/salones/lista";
    }

    @GetMapping("/consultar/{numero}")
    public String consultar(@PathVariable String numero,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            SalonSocialModel salon = salonService.consultar(numero);
            if (salon == null) {
                redirectAttributes.addFlashAttribute("error", "Salón social no encontrado");
                return "redirect:/salones/lista";
            }
            model.addAttribute("salon", salon);
            return "salones/detalle";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salones/lista";
        }
    }

    @GetMapping("/estado/{nombreEstado}")
    public String consultarPorEstado(@PathVariable String nombreEstado,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("salones", salonService.consultarPorEstado(nombreEstado));
            model.addAttribute("filtro", "Estado: " + nombreEstado);
            return "salones/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salones/lista";
        }
    }

    @GetMapping("/medida/{medida}")
    public String consultarPorMedida(@PathVariable String medida,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("salones", salonService.consultarPorMedida(medida));
            model.addAttribute("filtro", "Medida: " + medida);
            return "salones/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salones/lista";
        }
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam String numero,
                                @RequestParam String nombreEstado,
                                RedirectAttributes redirectAttributes) {
        try {
            String mensaje = salonService.setEstado(numero, nombreEstado);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/salones/lista";
    }

    @GetMapping("/estado-actual/{numero}")
    public String obtenerEstado(@PathVariable String numero,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            EstadoModel estado = salonService.getEstado(numero);
            model.addAttribute("numero", numero);
            model.addAttribute("estado", estado);
            return "salones/estado";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/salones/lista";
        }
    }
}