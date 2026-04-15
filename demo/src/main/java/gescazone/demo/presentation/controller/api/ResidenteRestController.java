package gescazone.demo.presentation.controller.api;

import gescazone.demo.domain.model.ResidenteModel;
import gescazone.demo.application.service.ResidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/residentes")
@CrossOrigin(origins = "*")
public class ResidenteRestController {

    @Autowired
    private ResidenteService residenteService;

    /**
     * GET /api/residentes/todos
     * Obtener todos los residentes
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        try {
            List<ResidenteModel> residentes = residenteService.consultarTodos();
            
            List<Map<String, Object>> response = residentes.stream()
                .map(res -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idResidente", res.getIdResidente());
                    map.put("numeroDocumento", res.getNumeroDocumento());
                    map.put("nombre", res.getNombre());
                    map.put("apellido", res.getApellido());
                    map.put("celular", res.getCelular());
                    
                    // Información del tipo de documento
                    if (res.getTipoDocumento() != null) {
                        Map<String, Object> tipoDocMap = new HashMap<>();
                        tipoDocMap.put("id", res.getTipoDocumento().getIdTipoDocumento());
                        tipoDocMap.put("nombre", res.getTipoDocumento().getNombreTipoDocumento());
                        map.put("tipoDocumento", tipoDocMap);
                    }
                    
                    // Información del tipo de residente
                    if (res.getTipoResidente() != null) {
                        Map<String, Object> tipoResMap = new HashMap<>();
                        tipoResMap.put("id", res.getTipoResidente().getIdTipoResidente());
                        tipoResMap.put("nombre", res.getTipoResidente().getNombreTipoResidente());
                        map.put("tipoResidente", tipoResMap);
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
     * GET /api/residentes/{numeroDocumento}
     * Obtener residente por número de documento
     */
    @GetMapping("/{numeroDocumento}")
    public ResponseEntity<Map<String, Object>> obtenerPorDocumento(@PathVariable Integer numeroDocumento) {
        try {
            ResidenteModel residente = residenteService.consultar(numeroDocumento);
            
            if (residente == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("idResidente", residente.getIdResidente());
            response.put("numeroDocumento", residente.getNumeroDocumento());
            response.put("nombre", residente.getNombre());
            response.put("apellido", residente.getApellido());
            response.put("celular", residente.getCelular());
            
            // Información del tipo de documento
            if (residente.getTipoDocumento() != null) {
                Map<String, Object> tipoDocMap = new HashMap<>();
                tipoDocMap.put("id", residente.getTipoDocumento().getIdTipoDocumento());
                tipoDocMap.put("nombre", residente.getTipoDocumento().getNombreTipoDocumento());
                response.put("tipoDocumento", tipoDocMap);
            }
            
            // Información del tipo de residente
            if (residente.getTipoResidente() != null) {
                Map<String, Object> tipoResMap = new HashMap<>();
                tipoResMap.put("id", residente.getTipoResidente().getIdTipoResidente());
                tipoResMap.put("nombre", residente.getTipoResidente().getNombreTipoResidente());
                response.put("tipoResidente", tipoResMap);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/residentes/tipo/{idTipoResidente}
     * Obtener residentes por tipo
     */
    @GetMapping("/tipo/{idTipoResidente}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorTipo(@PathVariable Integer idTipoResidente) {
        try {
            List<ResidenteModel> residentes = residenteService.consultarPorTipo(idTipoResidente);
            
            List<Map<String, Object>> response = residentes.stream()
                .map(res -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idResidente", res.getIdResidente());
                    map.put("numeroDocumento", res.getNumeroDocumento());
                    map.put("nombre", res.getNombre());
                    map.put("apellido", res.getApellido());
                    map.put("celular", res.getCelular());
                    
                    if (res.getTipoDocumento() != null) {
                        Map<String, Object> tipoDocMap = new HashMap<>();
                        tipoDocMap.put("id", res.getTipoDocumento().getIdTipoDocumento());
                        tipoDocMap.put("nombre", res.getTipoDocumento().getNombreTipoDocumento());
                        map.put("tipoDocumento", tipoDocMap);
                    }
                    
                    if (res.getTipoResidente() != null) {
                        Map<String, Object> tipoResMap = new HashMap<>();
                        tipoResMap.put("id", res.getTipoResidente().getIdTipoResidente());
                        tipoResMap.put("nombre", res.getTipoResidente().getNombreTipoResidente());
                        map.put("tipoResidente", tipoResMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/residentes/tipo-documento/{idTipoDocumento}
     * Obtener residentes por tipo de documento
     */
    @GetMapping("/tipo-documento/{idTipoDocumento}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorTipoDocumento(@PathVariable Integer idTipoDocumento) {
        try {
            List<ResidenteModel> residentes = residenteService.consultarPorTipoDeDocumento(idTipoDocumento);
            
            List<Map<String, Object>> response = residentes.stream()
                .map(res -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idResidente", res.getIdResidente());
                    map.put("numeroDocumento", res.getNumeroDocumento());
                    map.put("nombre", res.getNombre());
                    map.put("apellido", res.getApellido());
                    map.put("celular", res.getCelular());
                    
                    if (res.getTipoDocumento() != null) {
                        Map<String, Object> tipoDocMap = new HashMap<>();
                        tipoDocMap.put("id", res.getTipoDocumento().getIdTipoDocumento());
                        tipoDocMap.put("nombre", res.getTipoDocumento().getNombreTipoDocumento());
                        map.put("tipoDocumento", tipoDocMap);
                    }
                    
                    if (res.getTipoResidente() != null) {
                        Map<String, Object> tipoResMap = new HashMap<>();
                        tipoResMap.put("id", res.getTipoResidente().getIdTipoResidente());
                        tipoResMap.put("nombre", res.getTipoResidente().getNombreTipoResidente());
                        map.put("tipoResidente", tipoResMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/residentes/buscar/nombre
     * Buscar residentes por nombre
     */
    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<Map<String, Object>>> buscarPorNombre(@RequestParam String nombre) {
        try {
            List<ResidenteModel> residentes = residenteService.buscarPorNombre(nombre);
            
            List<Map<String, Object>> response = residentes.stream()
                .map(res -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idResidente", res.getIdResidente());
                    map.put("numeroDocumento", res.getNumeroDocumento());
                    map.put("nombre", res.getNombre());
                    map.put("apellido", res.getApellido());
                    map.put("celular", res.getCelular());
                    
                    if (res.getTipoDocumento() != null) {
                        Map<String, Object> tipoDocMap = new HashMap<>();
                        tipoDocMap.put("id", res.getTipoDocumento().getIdTipoDocumento());
                        tipoDocMap.put("nombre", res.getTipoDocumento().getNombreTipoDocumento());
                        map.put("tipoDocumento", tipoDocMap);
                    }
                    
                    if (res.getTipoResidente() != null) {
                        Map<String, Object> tipoResMap = new HashMap<>();
                        tipoResMap.put("id", res.getTipoResidente().getIdTipoResidente());
                        tipoResMap.put("nombre", res.getTipoResidente().getNombreTipoResidente());
                        map.put("tipoResidente", tipoResMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/residentes/buscar/apellido
     * Buscar residentes por apellido
     */
    @GetMapping("/buscar/apellido")
    public ResponseEntity<List<Map<String, Object>>> buscarPorApellido(@RequestParam String apellido) {
        try {
            List<ResidenteModel> residentes = residenteService.buscarPorApellido(apellido);
            
            List<Map<String, Object>> response = residentes.stream()
                .map(res -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idResidente", res.getIdResidente());
                    map.put("numeroDocumento", res.getNumeroDocumento());
                    map.put("nombre", res.getNombre());
                    map.put("apellido", res.getApellido());
                    map.put("celular", res.getCelular());
                    
                    if (res.getTipoDocumento() != null) {
                        Map<String, Object> tipoDocMap = new HashMap<>();
                        tipoDocMap.put("id", res.getTipoDocumento().getIdTipoDocumento());
                        tipoDocMap.put("nombre", res.getTipoDocumento().getNombreTipoDocumento());
                        map.put("tipoDocumento", tipoDocMap);
                    }
                    
                    if (res.getTipoResidente() != null) {
                        Map<String, Object> tipoResMap = new HashMap<>();
                        tipoResMap.put("id", res.getTipoResidente().getIdTipoResidente());
                        tipoResMap.put("nombre", res.getTipoResidente().getNombreTipoResidente());
                        map.put("tipoResidente", tipoResMap);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/residentes/crear
     * Crear un nuevo residente
     */
    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            // Validar y obtener numeroDocumento
            if (!datos.containsKey("numeroDocumento")) {
                return ResponseEntity.badRequest()
                    .body("El número de documento es obligatorio");
            }
            
            Integer numeroDocumento = ((Number) datos.get("numeroDocumento")).intValue();
            
            // Verificar que no exista un residente con ese documento
            ResidenteModel existente = residenteService.consultar(numeroDocumento);
            if (existente != null) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un residente con el documento: " + numeroDocumento);
            }
            
            ResidenteModel residente = new ResidenteModel();
            residente.setNumeroDocumento(numeroDocumento);
            residente.setNombre((String) datos.get("nombre"));
            residente.setApellido((String) datos.get("apellido"));
            
            if (datos.containsKey("celular") && datos.get("celular") != null) {
                residente.setCelular(((Number) datos.get("celular")).longValue());
            }
            
            Integer idTipoDocumento = ((Number) datos.get("idTipoDocumento")).intValue();
            Integer idTipoResidente = ((Number) datos.get("idTipoResidente")).intValue();
            
            String resultado = residenteService.crear(residente, idTipoDocumento, idTipoResidente);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el residente: " + e.getMessage());
        }
    }

    /**
     * PUT /api/residentes/actualizar/{numeroDocumento}
     * Actualizar un residente
     */
    @PutMapping("/actualizar/{numeroDocumento}")
    public ResponseEntity<String> actualizar(@PathVariable Integer numeroDocumento,
                                            @RequestBody Map<String, Object> datos) {
        try {
            // Verificar que exista el residente
            ResidenteModel residente = residenteService.consultar(numeroDocumento);
            if (residente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un residente con el documento: " + numeroDocumento);
            }
            
            // Obtener nuevo número de documento si cambió
            Integer numeroDocumentoNuevo = numeroDocumento;
            if (datos.containsKey("numeroDocumento")) {
                numeroDocumentoNuevo = ((Number) datos.get("numeroDocumento")).intValue();
                
                // Si cambió el documento, validar que el nuevo no exista
                if (!numeroDocumento.equals(numeroDocumentoNuevo)) {
                    ResidenteModel existente = residenteService.consultar(numeroDocumentoNuevo);
                    if (existente != null && !existente.getIdResidente().equals(residente.getIdResidente())) {
                        return ResponseEntity.badRequest()
                            .body("Ya existe un residente con el documento: " + numeroDocumentoNuevo);
                    }
                }
            }
            
            // Actualizar datos
            residente.setNumeroDocumento(numeroDocumentoNuevo);
            
            if (datos.containsKey("nombre")) {
                residente.setNombre((String) datos.get("nombre"));
            }
            if (datos.containsKey("apellido")) {
                residente.setApellido((String) datos.get("apellido"));
            }
            if (datos.containsKey("celular")) {
                residente.setCelular(((Number) datos.get("celular")).longValue());
            }
            
            Integer idTipoDocumento = ((Number) datos.get("idTipoDocumento")).intValue();
            Integer idTipoResidente = ((Number) datos.get("idTipoResidente")).intValue();
            
            String resultado = residenteService.actualizar(residente, idTipoDocumento, idTipoResidente);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el residente: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/residentes/eliminar/{numeroDocumento}
     * Eliminar un residente
     * ✅ MÉTODO MEJORADO CON MANEJO DE ERRORES DE INTEGRIDAD REFERENCIAL
     */
    @DeleteMapping("/eliminar/{numeroDocumento}")
    public ResponseEntity<String> eliminar(@PathVariable Integer numeroDocumento) {
        try {
            if (numeroDocumento == null) {
                return ResponseEntity.badRequest()
                    .body("El número de documento es obligatorio");
            }
            
            // Verificar que exista el residente
            ResidenteModel existente = residenteService.consultar(numeroDocumento);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un residente con el documento: " + numeroDocumento);
            }
            
            String resultado = residenteService.eliminar(numeroDocumento);
            return ResponseEntity.ok(resultado);
            
        } catch (DataIntegrityViolationException e) {
            // ✅ Error específico para restricciones de clave foránea
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No se puede eliminar el residente porque tiene registros relacionados (parqueaderos, visitantes, etc.). Elimine primero esos registros.");
                
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el residente: " + e.getMessage());
        }
    }
}