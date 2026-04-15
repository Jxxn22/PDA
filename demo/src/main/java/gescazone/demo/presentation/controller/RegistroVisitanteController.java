package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.RegistroVisitanteModel;
import gescazone.demo.application.service.RegistroVisitanteService;
import gescazone.demo.application.service.ResidenteService;
import gescazone.demo.application.service.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro-visitante")
public class RegistroVisitanteController {

    @Autowired
    private RegistroVisitanteService registroVisitanteService;

    @Autowired
    private ResidenteService residenteService;

    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping("/lista")
    public String consultarTodos(Model model) {
        model.addAttribute("registros", registroVisitanteService.consultarTodos());
        return "registro-visitante/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("registro", new RegistroVisitanteModel());
        model.addAttribute("residentes", residenteService.consultarTodos());
        model.addAttribute("apartamentos", apartamentoService.consultarTodos());
        return "registro-visitante/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute RegistroVisitanteModel registro,
                        @RequestParam String residenteId,
                        @RequestParam String apartamentoId,
                        RedirectAttributes redirectAttributes) {
        try {
            String mensaje = registroVisitanteService.crear(registro, residenteId, apartamentoId);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/registro-visitante/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registro", registro);
            return "redirect:/registro-visitante/nuevo";
        }
    }

    @GetMapping("/editar/{residenteId}/{apartamentoId}")
    public String mostrarFormularioEditar(@PathVariable String residenteId,
                                          @PathVariable String apartamentoId,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            RegistroVisitanteModel registro = registroVisitanteService.consultar(residenteId, apartamentoId);
            if (registro == null) {
                redirectAttributes.addFlashAttribute("error", "Registro no encontrado");
                return "redirect:/registro-visitante/lista";
            }
            model.addAttribute("registro", registro);
            model.addAttribute("residentes", residenteService.consultarTodos());
            model.addAttribute("apartamentos", apartamentoService.consultarTodos());
            return "registro-visitante/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro-visitante/lista";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute RegistroVisitanteModel registro,
                             @RequestParam String residenteId,
                             @RequestParam String apartamentoId,
                             RedirectAttributes redirectAttributes) {
        try {
            String mensaje = registroVisitanteService.actualizar(registro, residenteId, apartamentoId);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/registro-visitante/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro-visitante/editar/" + residenteId + "/" + apartamentoId;
        }
    }

    @PostMapping("/registrar-salida")
    public String registrarSalida(@RequestParam String residenteId,
                                  @RequestParam String apartamentoId,
                                  @RequestParam String fechaHoraSalida,
                                  RedirectAttributes redirectAttributes) {
        try {
            String mensaje = registroVisitanteService.registrarSalida(residenteId, apartamentoId, fechaHoraSalida);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registro-visitante/lista";
    }

    @GetMapping("/eliminar/{residenteId}/{apartamentoId}")
    public String eliminar(@PathVariable String residenteId,
                           @PathVariable String apartamentoId,
                           RedirectAttributes redirectAttributes) {
        try {
            String mensaje = registroVisitanteService.eliminar(residenteId, apartamentoId);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registro-visitante/lista";
    }

    @GetMapping("/residente/{residenteId}")
    public String consultarPorResidente(@PathVariable String residenteId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("registros", registroVisitanteService.consultarPorIdResidente(residenteId));
            model.addAttribute("filtro", "Residente ID: " + residenteId);
            return "registro-visitante/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro-visitante/lista";
        }
    }

    @GetMapping("/apartamento/{apartamentoId}")
    public String consultarPorApartamento(@PathVariable String apartamentoId,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("registros", registroVisitanteService.consultarPorIdApartamento(apartamentoId));
            model.addAttribute("filtro", "Apartamento ID: " + apartamentoId);
            return "registro-visitante/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro-visitante/lista";
        }
    }

    @GetMapping("/apartamento/numero/{numero}")
    public String consultarPorNumeroApartamento(@PathVariable String numero,
                                                Model model,
                                                RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("registros", registroVisitanteService.consultarPorNumeroApartamento(numero));
            model.addAttribute("filtro", "Apartamento Número: " + numero);
            return "registro-visitante/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro-visitante/lista";
        }
    }

    @GetMapping("/activos")
    public String consultarVisitantesActivos(Model model) {
        model.addAttribute("registros", registroVisitanteService.consultarVisitantesActivos());
        model.addAttribute("filtro", "Visitantes activos (sin salida registrada)");
        return "registro-visitante/lista";
    }
}