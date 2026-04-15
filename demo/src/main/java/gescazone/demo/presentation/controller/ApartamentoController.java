package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.ApartamentoModel;
import gescazone.demo.application.service.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/apartamentos")
public class ApartamentoController {

    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping("/lista")
    public String consultarTodos(Model model) {
        model.addAttribute("apartamentos", apartamentoService.consultarTodos());
        return "apartamentos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("apartamento", new ApartamentoModel());
        return "apartamentos/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute ApartamentoModel apartamento, 
                        RedirectAttributes redirectAttributes) {
        try {
            String mensaje = apartamentoService.crear(apartamento);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/apartamentos/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("apartamento", apartamento);
            return "redirect:/apartamentos/nuevo";
        }
    }

    @GetMapping("/editar/{numeroApto}")
    public String mostrarFormularioEditar(@PathVariable String numeroApto, 
                                          Model model, 
                                          RedirectAttributes redirectAttributes) {
        try {
            ApartamentoModel apartamento = apartamentoService.consultar(numeroApto);
            if (apartamento == null) {
                redirectAttributes.addFlashAttribute("error", "Apartamento no encontrado");
                return "redirect:/apartamentos/lista";
            }
            model.addAttribute("apartamento", apartamento);
            return "apartamentos/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/apartamentos/lista";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute ApartamentoModel apartamento, 
                            RedirectAttributes redirectAttributes) {
        try {
            String mensaje = apartamentoService.actualizar(apartamento);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/apartamentos/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/apartamentos/editar/" + apartamento.getNumero();
        }
    }

    @GetMapping("/eliminar/{numeroApto}")
    public String eliminar(@PathVariable String numeroApto, 
                          RedirectAttributes redirectAttributes) {
        try {
            String mensaje = apartamentoService.eliminar(numeroApto);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/apartamentos/lista";
    }
}