package gescazone.demo.presentation.controller;

import gescazone.demo.domain.model.ResidenteModel;
import gescazone.demo.domain.model.TipoDocumentoModel;
import gescazone.demo.domain.model.TipoResidenteModel;
import gescazone.demo.application.service.ResidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/residentes")
public class ResidenteController {

    @Autowired
    private ResidenteService residenteService;

    @GetMapping("/lista")
    public String consultarTodos(Model model) {
        model.addAttribute("residentes", residenteService.consultarTodos());
        return "residentes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("residente", new ResidenteModel());
        return "residentes/formulario";
    }

    @PostMapping("/crear")
    public String crear(@ModelAttribute ResidenteModel residente,
                        @RequestParam String nombreTipoDocumento,
                        @RequestParam String nombreTipoResidente,
                        RedirectAttributes redirectAttributes) {
        try {
            residente.setTipoDocumento(new TipoDocumentoModel(nombreTipoDocumento));
            residente.setTipoResidente(new TipoResidenteModel(nombreTipoResidente));
            String mensaje = residenteService.crear(residente);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/residentes/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("residente", residente);
            return "redirect:/residentes/nuevo";
        }
    }

    @GetMapping("/editar/{numeroDocumento}")
    public String mostrarFormularioEditar(@PathVariable Integer numeroDocumento,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        try {
            ResidenteModel residente = residenteService.consultar(numeroDocumento);
            if (residente == null) {
                redirectAttributes.addFlashAttribute("error", "Residente no encontrado");
                return "redirect:/residentes/lista";
            }
            model.addAttribute("residente", residente);
            return "residentes/formulario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/lista";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute ResidenteModel residente,
                             @RequestParam String nombreTipoDocumento,
                             @RequestParam String nombreTipoResidente,
                             RedirectAttributes redirectAttributes) {
        try {
            residente.setTipoDocumento(new TipoDocumentoModel(nombreTipoDocumento));
            residente.setTipoResidente(new TipoResidenteModel(nombreTipoResidente));
            String mensaje = residenteService.actualizar(residente);
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/residentes/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/editar/" + residente.getNumeroDocumento();
        }
    }

    @GetMapping("/eliminar/{numeroDocumento}")
    public String eliminar(@PathVariable Integer numeroDocumento,
                           RedirectAttributes redirectAttributes) {
        try {
            String mensaje = residenteService.eliminar(numeroDocumento);
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/residentes/lista";
    }

    @GetMapping("/consultar/{numeroDocumento}")
    public String consultar(@PathVariable Integer numeroDocumento,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            ResidenteModel residente = residenteService.consultar(numeroDocumento);
            if (residente == null) {
                redirectAttributes.addFlashAttribute("error", "Residente no encontrado");
                return "redirect:/residentes/lista";
            }
            model.addAttribute("residente", residente);
            return "residentes/detalle";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/lista";
        }
    }

    @GetMapping("/tipo/{nombreTipoResidente}")
    public String consultarPorTipo(@PathVariable String nombreTipoResidente,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("residentes", residenteService.consultarPorTipo(nombreTipoResidente));
            model.addAttribute("filtro", "Tipo de residente: " + nombreTipoResidente);
            return "residentes/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/lista";
        }
    }

    @GetMapping("/tipo-documento/{nombreTipoDocumento}")
    public String consultarPorTipoDeDocumento(@PathVariable String nombreTipoDocumento,
                                              Model model,
                                              RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("residentes", residenteService.consultarPorTipoDeDocumento(nombreTipoDocumento));
            model.addAttribute("filtro", "Tipo de documento: " + nombreTipoDocumento);
            return "residentes/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/lista";
        }
    }

    @GetMapping("/buscar/nombre")
    public String buscarPorNombre(@RequestParam String nombre,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("residentes", residenteService.buscarPorNombre(nombre));
            model.addAttribute("filtro", "Nombre: " + nombre);
            return "residentes/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/lista";
        }
    }

    @GetMapping("/buscar/apellido")
    public String buscarPorApellido(@RequestParam String apellido,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("residentes", residenteService.buscarPorApellido(apellido));
            model.addAttribute("filtro", "Apellido: " + apellido);
            return "residentes/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/residentes/lista";
        }
    }
}