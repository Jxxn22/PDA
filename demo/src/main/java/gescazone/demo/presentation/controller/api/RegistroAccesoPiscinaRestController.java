package gescazone.demo.controller.api;

import gescazone.demo.model.RegistroAccesoPiscinaModel;
import gescazone.demo.service.RegistroAccesoPiscinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/piscina")
@CrossOrigin(origins = "*")
public class RegistroAccesoPiscinaRestController {

    @Autowired
    private RegistroAccesoPiscinaService registroService;

    /**
     * GET /api/piscina/todos
     * Obtener todos los registros
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        try {
            List<RegistroAccesoPiscinaModel> registros = registroService.consultarTodos();
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::createRegistroMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/piscina/{id}
     * Obtener un registro por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        try {
            List<RegistroAccesoPiscinaModel> registros = registroService.consultarTodos();
            RegistroAccesoPiscinaModel registro = registros.stream()
                .filter(r -> r.getIdRegistroAccesoPiscina().equals(id))
                .findFirst()
                .orElse(null);
            
            if (registro == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(createRegistroMap(registro));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/piscina/hoy
     * Obtener registros de hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<Map<String, Object>>> obtenerRegistrosHoy() {
        try {
            List<RegistroAccesoPiscinaModel> registros = registroService.consultarRegistrosHoy();
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::createRegistroMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/piscina/apartamento/{numero}
     * Obtener registros por número de apartamento
     */
    @GetMapping("/apartamento/{numero}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorApartamento(@PathVariable String numero) {
        try {
            List<RegistroAccesoPiscinaModel> registros = registroService.consultarPorApartamento(numero);
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::createRegistroMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/piscina/residente/{numeroDocumento}
     * Obtener registros por documento de residente
     */
    @GetMapping("/residente/{numeroDocumento}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorResidente(@PathVariable Integer numeroDocumento) {
        try {
            List<RegistroAccesoPiscinaModel> registros = registroService.consultarPorResidente(numeroDocumento);
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::createRegistroMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/piscina/verificar-acceso/{numeroApartamento}
     * Verificar si un apartamento puede acceder
     */
    @GetMapping("/verificar-acceso/{numeroApartamento}")
    public ResponseEntity<Map<String, Object>> verificarAcceso(@PathVariable String numeroApartamento) {
        try {
            boolean puedeAcceder = registroService.verificarAcceso(numeroApartamento);
            
            Map<String, Object> response = new HashMap<>();
            response.put("numeroApartamento", numeroApartamento);
            response.put("puedeAcceder", puedeAcceder);
            response.put("mensaje", puedeAcceder ? 
                "Apartamento autorizado para acceder" : 
                "Apartamento NO autorizado (verificar pagos)");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/piscina/registrar-ingreso
     * Registrar ingreso a la piscina
     */
    @PostMapping("/registrar-ingreso")
    public ResponseEntity<String> registrarIngreso(@RequestBody Map<String, Object> datos) {
        try {
            // Validar que los datos existen
            if (!datos.containsKey("numeroApartamento") || datos.get("numeroApartamento") == null) {
                return ResponseEntity.badRequest()
                    .body("El número del apartamento es obligatorio");
            }
            
            if (!datos.containsKey("numeroDocumento") || datos.get("numeroDocumento") == null) {
                return ResponseEntity.badRequest()
                    .body("El número de documento es obligatorio");
            }
            
            String numeroApartamento = (String) datos.get("numeroApartamento");
            Integer numeroDocumento = null;
            
            // Convertir numeroDocumento
            Object docObj = datos.get("numeroDocumento");
            if (docObj instanceof String) {
                numeroDocumento = Integer.parseInt((String) docObj);
            } else if (docObj instanceof Integer) {
                numeroDocumento = (Integer) docObj;
            } else if (docObj instanceof Number) {
                numeroDocumento = ((Number) docObj).intValue();
            }
            
            if (numeroApartamento.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número del apartamento no puede estar vacío");
            }
            
            String resultado = registroService.registrarIngreso(numeroApartamento, numeroDocumento);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar ingreso: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/piscina/eliminar/{id}
     * Eliminar un registro
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            String resultado = registroService.eliminar(id);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el registro: " + e.getMessage());
        }
    }
    
    /**
     * Método helper para crear el Map de respuesta
     */
    private Map<String, Object> createRegistroMap(RegistroAccesoPiscinaModel reg) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id_acceso_piscina", reg.getIdRegistroAccesoPiscina());
        map.put("numero_documento", reg.getResidenteNumeroDocumento());
        map.put("numero_apartamento", reg.getApartamentoNumero());
        map.put("fecha_hora", reg.getFechaHora().toString());
        
        return map;
    }
}