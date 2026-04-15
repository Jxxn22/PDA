package gescazone.demo.controller.api;

import gescazone.demo.model.SalonSocialModel;
import gescazone.demo.model.EstadoModel;
import gescazone.demo.service.SalonSocialService;
import gescazone.demo.service.EstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salones")
@CrossOrigin(origins = "*")
public class SalonSocialRestController {

    @Autowired
    private SalonSocialService salonSocialService;
    
    @Autowired
    private EstadoService estadoService;

    /**
     * GET /api/salones/todos
     * Obtener todos los salones sociales
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        try {
            List<SalonSocialModel> salones = salonSocialService.consultarTodos();
            
            List<Map<String, Object>> response = salones.stream()
                .map(salon -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", salon.getIdSalonSocial());
                    map.put("numero", salon.getNumero());
                    map.put("medidas", salon.getMedidas());
                    map.put("telefono", salon.getTelefono());
                    
                    // Información del estado
                    if (salon.getEstado() != null) {
                        Map<String, Object> estadoMap = new HashMap<>();
                        estadoMap.put("id", salon.getEstado().getIdEstado());
                        estadoMap.put("nombre", salon.getEstado().getNombreEstado());
                        map.put("estado", estadoMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/salones/{numero}
     * Obtener salón social por número
     */
    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> obtenerPorNumero(@PathVariable String numero) {
        try {
            SalonSocialModel salon = salonSocialService.consultar(numero);
            
            if (salon == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", salon.getIdSalonSocial());
            response.put("numero", salon.getNumero());
            response.put("medidas", salon.getMedidas());
            response.put("telefono", salon.getTelefono());
            
            // Información del estado
            if (salon.getEstado() != null) {
                Map<String, Object> estadoMap = new HashMap<>();
                estadoMap.put("id", salon.getEstado().getIdEstado());
                estadoMap.put("nombre", salon.getEstado().getNombreEstado());
                response.put("estado", estadoMap);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/salones/crear
     * Crear un nuevo salón social
     */
    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            String numero = (String) datos.get("numero");
            
            if (numero == null || numero.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número del salón social es obligatorio");
            }
            
            // Verificar que no exista
            SalonSocialModel existente = salonSocialService.consultar(numero);
            if (existente != null) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un salón social con el número: " + numero);
            }
            
            // Validar y obtener el estado
            if (!datos.containsKey("idEstado")) {
                return ResponseEntity.badRequest()
                    .body("El estado es obligatorio");
            }
            
            Integer idEstado = ((Number) datos.get("idEstado")).intValue();
            EstadoModel estado = estadoService.consultar(idEstado);
            if (estado == null) {
                return ResponseEntity.badRequest()
                    .body("El estado especificado no existe");
            }
            
            SalonSocialModel salon = new SalonSocialModel();
            salon.setNumero(numero);
            salon.setMedidas((String) datos.get("medidas"));

            if (datos.containsKey("telefono") && datos.get("telefono") != null) {
                Object telefonoObj = datos.get("telefono");
                if (telefonoObj instanceof Number) {
                    salon.setTelefono(((Number) telefonoObj).longValue()); // ← CAMBIO
                } else if (telefonoObj instanceof String) {
                    try {
                        salon.setTelefono(Long.parseLong((String) telefonoObj)); // ← CAMBIO
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest()
                            .body("El teléfono debe ser un número válido");
                    }
                }
            }
            salon.setEstado(estado);
            
            String resultado = salonSocialService.crear(salon);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el salón social: " + e.getMessage());
        }
    }

    /**
     * PUT /api/salones/actualizar/{numero}
     * Actualizar un salón social
     */
    @PutMapping("/actualizar/{numero}")
    public ResponseEntity<String> actualizar(@PathVariable String numero,
                                            @RequestBody Map<String, Object> datos) {
        try {
            SalonSocialModel salon = salonSocialService.consultar(numero);
            if (salon == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un salón social con el número: " + numero);
            }
            
            String numeroNuevo = (String) datos.get("numero");
            if (numeroNuevo == null || numeroNuevo.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número del salón social es obligatorio");
            }
            
            // Si cambió el número, validar que el nuevo no exista
            if (!numero.equals(numeroNuevo)) {
                SalonSocialModel existente = salonSocialService.consultar(numeroNuevo);
                if (existente != null) {
                    return ResponseEntity.badRequest()
                        .body("Ya existe un salón social con el número: " + numeroNuevo);
                }
            }
            
            // Actualizar estado si viene
            if (datos.containsKey("idEstado")) {
                Integer idEstado = ((Number) datos.get("idEstado")).intValue();
                EstadoModel estado = estadoService.consultar(idEstado);
                if (estado == null) {
                    return ResponseEntity.badRequest()
                        .body("El estado especificado no existe");
                }
                salon.setEstado(estado);
            }
            
            // Actualizar campos básicos
            salon.setNumero(numeroNuevo);
            if (datos.containsKey("medidas")) {
                salon.setMedidas((String) datos.get("medidas"));
            }
            if (datos.containsKey("telefono") && datos.get("telefono") != null) {
                Object telefonoObj = datos.get("telefono");
                if (telefonoObj instanceof Number) {
                    salon.setTelefono(((Number) telefonoObj).longValue()); // ← CAMBIO
                } else if (telefonoObj instanceof String) {
                    try {
                        salon.setTelefono(Long.parseLong((String) telefonoObj)); // ← CAMBIO
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest()
                            .body("El teléfono debe ser un número válido");
                    }
                }
            }
            
            String resultado = salonSocialService.actualizar(salon);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el salón social: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/salones/eliminar/{numero}
     * Eliminar un salón social
     */
    @DeleteMapping("/eliminar/{numero}")
    public ResponseEntity<String> eliminar(@PathVariable String numero) {
        try {
            if (numero == null || numero.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número del salón social es obligatorio");
            }
            
            SalonSocialModel existente = salonSocialService.consultar(numero);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un salón social con el número: " + numero);
            }
            
            String resultado = salonSocialService.eliminar(numero);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el salón social: " + e.getMessage());
        }
    }

    /**
     * POST /api/salones/cambiar-estado
     * Cambiar el estado de un salón social
     */
    @PostMapping("/cambiar-estado")
    public ResponseEntity<String> cambiarEstado(@RequestBody Map<String, Object> datos) {
        try {
            String numero = (String) datos.get("numero");
            
            if (numero == null || numero.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número del salón social es obligatorio");
            }
            
            if (!datos.containsKey("idEstado")) {
                return ResponseEntity.badRequest()
                    .body("El ID del estado es obligatorio");
            }
            
            Integer idEstado = ((Number) datos.get("idEstado")).intValue();
            String resultado = salonSocialService.setEstado(numero, idEstado);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cambiar el estado: " + e.getMessage());
        }
    }

    /**
     * GET /api/salones/estado-actual/{numero}
     * Obtener el estado actual de un salón social
     */
    @GetMapping("/estado-actual/{numero}")
    public ResponseEntity<Map<String, Object>> obtenerEstado(@PathVariable String numero) {
        try {
            if (numero == null || numero.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            SalonSocialModel existente = salonSocialService.consultar(numero);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
            
            EstadoModel estado = salonSocialService.getEstado(numero);
            
            Map<String, Object> response = new HashMap<>();
            response.put("numero", numero);
            response.put("idEstado", estado.getIdEstado());
            response.put("nombreEstado", estado.getNombreEstado());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/salones/estado/{idEstado}
     * Consultar salones sociales por estado
     */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<Map<String, Object>>> consultarPorEstado(
            @PathVariable Integer idEstado) {
        try {
            List<SalonSocialModel> salones = salonSocialService.consultarPorEstado(idEstado);
            
            List<Map<String, Object>> response = salones.stream()
                .map(salon -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", salon.getIdSalonSocial());
                    map.put("numero", salon.getNumero());
                    map.put("medidas", salon.getMedidas());
                    map.put("telefono", salon.getTelefono());
                    
                    if (salon.getEstado() != null) {
                        Map<String, Object> estadoMap = new HashMap<>();
                        estadoMap.put("id", salon.getEstado().getIdEstado());
                        estadoMap.put("nombre", salon.getEstado().getNombreEstado());
                        map.put("estado", estadoMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/salones/medida/{medida}
     * Consultar salones sociales por medida
     */
    @GetMapping("/medida/{medida}")
    public ResponseEntity<List<Map<String, Object>>> consultarPorMedida(
            @PathVariable String medida) {
        try {
            List<SalonSocialModel> salones = salonSocialService.consultarPorMedida(medida);
            
            List<Map<String, Object>> response = salones.stream()
                .map(salon -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", salon.getIdSalonSocial());
                    map.put("numero", salon.getNumero());
                    map.put("medidas", salon.getMedidas());
                    map.put("telefono", salon.getTelefono());
                    
                    if (salon.getEstado() != null) {
                        Map<String, Object> estadoMap = new HashMap<>();
                        estadoMap.put("id", salon.getEstado().getIdEstado());
                        estadoMap.put("nombre", salon.getEstado().getNombreEstado());
                        map.put("estado", estadoMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}