package gescazone.demo.controller.api;

import gescazone.demo.model.RegistroVisitanteModel;
import gescazone.demo.service.RegistroVisitanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registro-visitante")
@CrossOrigin(origins = "*")
public class RegistroVisitanteRestController {

    @Autowired
    private RegistroVisitanteService registroVisitanteService;

    @GetMapping("/todos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        try {
            List<RegistroVisitanteModel> registros = registroVisitanteService.consultarTodos();
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{residenteId}/{apartamentoId}")
    public ResponseEntity<Map<String, Object>> obtenerRegistro(@PathVariable Integer residenteId,
                                                                @PathVariable Integer apartamentoId) {
        try {
            RegistroVisitanteModel registro = registroVisitanteService.consultar(residenteId, apartamentoId);
            
            if (registro == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(convertirAMap(registro));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/visitante/{residenteId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorVisitante(@PathVariable Integer residenteId) {
        try {
            List<RegistroVisitanteModel> registros = registroVisitanteService.consultarPorIdResidente(residenteId);
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/apartamento/{apartamentoId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorApartamento(@PathVariable Integer apartamentoId) {
        try {
            List<RegistroVisitanteModel> registros = registroVisitanteService.consultarPorIdApartamento(apartamentoId);
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Map<String, Object>>> obtenerActivos() {
        try {
            List<RegistroVisitanteModel> registros = registroVisitanteService.consultarVisitantesActivos();
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/apartamento/{apartamentoId}/activos")
    public ResponseEntity<List<Map<String, Object>>> obtenerActivosPorApartamento(
            @PathVariable Integer apartamentoId) {
        try {
            List<RegistroVisitanteModel> registros = registroVisitanteService
                .consultarVisitantesActivosPorApartamento(apartamentoId);
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            // Validar y convertir IDs
            Integer residenteId = convertirAInteger(datos.get("residenteId"));
            Integer apartamentoId = convertirAInteger(datos.get("apartamentoId"));
            
            if (residenteId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del residente es obligatorio");
            }
            
            if (apartamentoId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del apartamento es obligatorio");
            }
            
            // Verificar que no exista ya este registro
            RegistroVisitanteModel existente = registroVisitanteService.consultar(residenteId, apartamentoId);
            if (existente != null) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un registro para este visitante en este apartamento");
            }
            
            RegistroVisitanteModel registro = new RegistroVisitanteModel();
            registro.setFechaHoraEntrada((String) datos.get("fechaHoraEntrada"));
            
            String resultado = registroVisitanteService.crear(registro, residenteId, apartamentoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el registro: " + e.getMessage());
        }
    }

    @PostMapping("/registrar-salida")
    public ResponseEntity<String> registrarSalida(@RequestBody Map<String, Object> datos) {
        try {
            Integer residenteId = convertirAInteger(datos.get("residenteId"));
            Integer apartamentoId = convertirAInteger(datos.get("apartamentoId"));
            String fechaHoraSalida = (String) datos.get("fechaHoraSalida");
            
            if (residenteId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del residente es obligatorio");
            }
            
            if (apartamentoId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del apartamento es obligatorio");
            }
            
            if (fechaHoraSalida == null || fechaHoraSalida.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La fecha y hora de salida es obligatoria");
            }
            
            // Verificar que exista el registro
            RegistroVisitanteModel existente = registroVisitanteService.consultar(residenteId, apartamentoId);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un registro para este visitante en este apartamento");
            }
            
            String resultado = registroVisitanteService.registrarSalida(residenteId, apartamentoId, fechaHoraSalida);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar la salida: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{residenteId}/{apartamentoId}")
    public ResponseEntity<String> actualizar(@PathVariable Integer residenteId,
                                            @PathVariable Integer apartamentoId,
                                            @RequestBody Map<String, Object> datos) {
        try {
            // Verificar que exista el registro
            RegistroVisitanteModel existente = registroVisitanteService.consultar(residenteId, apartamentoId);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un registro con los IDs proporcionados");
            }
            
            RegistroVisitanteModel registro = new RegistroVisitanteModel();
            registro.setFechaHoraEntrada((String) datos.get("fechaHoraEntrada"));
            
            if (datos.containsKey("fechaHoraSalida") && datos.get("fechaHoraSalida") != null) {
                registro.setFechaHoraSalida((String) datos.get("fechaHoraSalida"));
            }
            
            String resultado = registroVisitanteService.actualizar(registro, residenteId, apartamentoId);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el registro: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{residenteId}/{apartamentoId}")
    public ResponseEntity<String> eliminar(@PathVariable Integer residenteId,
                                          @PathVariable Integer apartamentoId) {
        try {
            // Verificar que exista el registro
            RegistroVisitanteModel existente = registroVisitanteService.consultar(residenteId, apartamentoId);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un registro con los IDs proporcionados");
            }
            
            String resultado = registroVisitanteService.eliminar(residenteId, apartamentoId);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el registro: " + e.getMessage());
        }
    }
    
    /**
     * Método auxiliar para convertir el modelo a Map
     */
    private Map<String, Object> convertirAMap(RegistroVisitanteModel reg) {
        Map<String, Object> map = new HashMap<>();
        map.put("residenteId", reg.getResidente().getIdResidente());
        map.put("residenteNombre", reg.getResidente().getNombre() + " " + reg.getResidente().getApellido());
        map.put("residenteDocumento", reg.getResidente().getNumeroDocumento());
        map.put("apartamentoId", reg.getApartamento().getIdApartamento());
        map.put("apartamentoNumero", reg.getApartamento().getNumero());
        map.put("fechaHoraEntrada", reg.getFechaHoraEntrada());
        map.put("fechaHoraSalida", reg.getFechaHoraSalida());
        map.put("activo", reg.isActivo());
        return map;
    }
    
    /**
     * Método auxiliar para convertir Object a Integer
     */
    private Integer convertirAInteger(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof Integer) {
            return (Integer) valor;
        }
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }
        if (valor instanceof String) {
            try {
                return Integer.parseInt((String) valor);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}