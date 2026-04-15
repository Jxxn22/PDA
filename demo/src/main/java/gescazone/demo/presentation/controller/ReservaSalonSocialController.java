package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.ReservaSalonSocialModel;
import gescazone.demo.application.service.ReservaSalonSocialService;
import gescazone.demo.application.service.SalonSocialService;
import gescazone.demo.application.service.ResidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reservas")
public class ReservaSalonSocialController {

    @Autowired
    private ReservaSalonSocialService reservaService;

    @Autowired
    private SalonSocialService salonService;

    @Autowired
    private ResidenteService habitanteService;

    @GetMapping("/lista")
    public String listarReservas(Model model) {
        model.addAttribute("reservas", reservaService.listarReservas());
        return "reservas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("reserva", new ReservaSalonSocialModel());
        model.addAttribute("salones", salonService.consultarTodos());
        model.addAttribute("habitantes", habitanteService.consultarTodos());
        return "reservas/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute ReservaSalonSocialModel reserva,
                        RedirectAttributes redirectAttributes) {
        try {
            String mensaje = reservaService.guardarReserva(reserva);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/nueva";
        }
    }

    // línea 73 — id cambia de Long a String
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable String id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            ReservaSalonSocialModel reserva = reservaService.buscarPorId(id);
            if (reserva == null) {
                redirectAttributes.addFlashAttribute("error", "Reserva no encontrada");
                return "redirect:/reservas/lista";
            }
            model.addAttribute("reserva", reserva);
            model.addAttribute("salones", salonService.consultarTodos());
            model.addAttribute("habitantes", habitanteService.consultarTodos());
            return "reservas/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    // línea 100 — getIdReserva() reemplazado por getId()
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute ReservaSalonSocialModel reserva,
                             RedirectAttributes redirectAttributes) {
        try {
            String mensaje = reservaService.guardarReserva(reserva);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/editar/" + reserva.getId();
        }
    }

    // línea 111 — id cambia de Long a String
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id,
                           RedirectAttributes redirectAttributes) {
        try {
            String mensaje = reservaService.eliminarReserva(id);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas/lista";
    }

    // línea 127 — id cambia de Long a String
    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable String id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            ReservaSalonSocialModel reserva = reservaService.buscarPorId(id);
            if (reserva == null) {
                redirectAttributes.addFlashAttribute("error", "Reserva no encontrada");
                return "redirect:/reservas/lista";
            }
            model.addAttribute("reserva", reserva);
            return "reservas/detalle";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    // línea 148 — idSalon cambia de Integer a String
    @GetMapping("/salon/{idSalon}")
    public String buscarPorSalon(@PathVariable String idSalon,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("reservas", reservaService.buscarPorIdSalon(idSalon));
            model.addAttribute("filtro", "Salón ID: " + idSalon);
            return "reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    @GetMapping("/salon/numero/{numero}")
    public String buscarPorNumeroSalon(@PathVariable String numero,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("reservas", reservaService.buscarPorNumeroSalon(numero));
            model.addAttribute("filtro", "Salón Número: " + numero);
            return "reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    // línea 182 — idHabitante cambia de Long a String
    @GetMapping("/habitante/{idHabitante}")
    public String buscarPorHabitante(@PathVariable String idHabitante,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("reservas", reservaService.buscarPorIdHabitante(idHabitante));
            model.addAttribute("filtro", "Habitante ID: " + idHabitante);
            return "reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    @GetMapping("/habitante/documento/{documento}")
    public String buscarPorDocumentoHabitante(@PathVariable String documento,
                                              Model model,
                                              RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("reservas", reservaService.buscarPorDocumentoHabitante(documento));
            model.addAttribute("filtro", "Documento: " + documento);
            return "reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    @GetMapping("/buscar-por-fechas")
    public String buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("reservas", reservaService.buscarPorRangoFechas(inicio, fin));
            model.addAttribute("filtro", "Desde " + inicio + " hasta " + fin);
            return "reservas/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservas/lista";
        }
    }

    @GetMapping("/buscar-fechas")
    public String mostrarFormularioBusquedaFechas() {
        return "reservas/buscar-fechas";
    }
}