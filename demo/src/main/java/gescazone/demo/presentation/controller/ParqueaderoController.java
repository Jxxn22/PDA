package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.ParqueaderoModel;
import gescazone.demo.domain.model.EstadoModel;
import gescazone.demo.application.service.ParqueaderoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parqueaderos")
public class ParqueaderoController {

    @Autowired
    private ParqueaderoService parqueaderoService;

    @GetMapping("/lista")
    public String consultarTodos(Model model) {
        model.addAttribute("parqueaderos", parqueaderoService.consultarTodos());
        return "parqueaderos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("parqueadero", new ParqueaderoModel());
        return "parqueaderos/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute ParqueaderoModel parqueadero,
                        @RequestParam String nombreEstado,
                        RedirectAttributes redirectAttributes) {
        try {
            parqueadero.setEstado(new EstadoModel(nombreEstado.trim()));
            String mensaje = parqueaderoService.crear(parqueadero);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/parqueaderos/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("parqueadero", parqueadero);
            return "redirect:/parqueaderos/nuevo";
        }
    }

    @GetMapping("/editar/{numero}")
    public String mostrarFormularioEditar(@PathVariable String numero,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            ParqueaderoModel parqueadero = parqueaderoService.consultar(numero);
            if (parqueadero == null) {
                redirectAttributes.addFlashAttribute("error", "Parqueadero no encontrado");
                return "redirect:/parqueaderos/lista";
            }
            model.addAttribute("parqueadero", parqueadero);
            return "parqueaderos/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parqueaderos/lista";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute ParqueaderoModel parqueadero,
                             @RequestParam String nombreEstado,
                             RedirectAttributes redirectAttributes) {
        try {
            parqueadero.setEstado(new EstadoModel(nombreEstado.trim()));
            String mensaje = parqueaderoService.actualizar(parqueadero);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/parqueaderos/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parqueaderos/editar/" + parqueadero.getNumero();
        }
    }

    @GetMapping("/eliminar/{numero}")
    public String eliminar(@PathVariable String numero,
                           RedirectAttributes redirectAttributes) {
        try {
            String mensaje = parqueaderoService.eliminar(numero);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/parqueaderos/lista";
    }

    @GetMapping("/consultar/{numero}")
    public String consultar(@PathVariable String numero,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            ParqueaderoModel parqueadero = parqueaderoService.consultar(numero);
            if (parqueadero == null) {
                redirectAttributes.addFlashAttribute("error", "Parqueadero no encontrado");
                return "redirect:/parqueaderos/lista";
            }
            model.addAttribute("parqueadero", parqueadero);
            return "parqueaderos/detalle";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parqueaderos/lista";
        }
    }

    @GetMapping("/estado/{nombreEstado}")
    public String consultarPorEstado(@PathVariable String nombreEstado,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("parqueaderos", parqueaderoService.consultarPorEstado(nombreEstado));
            model.addAttribute("filtro", "Estado: " + nombreEstado);
            return "parqueaderos/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parqueaderos/lista";
        }
    }

    @GetMapping("/medida/{medida}")
    public String consultarPorMedida(@PathVariable String medida,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("parqueaderos", parqueaderoService.consultarPorMedida(medida));
            model.addAttribute("filtro", "Medida: " + medida);
            return "parqueaderos/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parqueaderos/lista";
        }
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam String numero,
                                @RequestParam String nombreEstado,
                                RedirectAttributes redirectAttributes) {
        try {
            String mensaje = parqueaderoService.setEstado(numero, nombreEstado);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/parqueaderos/lista";
    }
}