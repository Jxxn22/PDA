package gescazone.demo.controller.api;

import gescazone.demo.model.RegistroParqueaderoVisitanteModel;
import gescazone.demo.service.RegistroParqueaderoVisitanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registro-parqueadero")
@CrossOrigin(origins = "*")
public class RegistroParqueaderoVisitanteRestController {

    @Autowired
    private RegistroParqueaderoVisitanteService registroService;

    @GetMapping("/todos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        try {
            List<RegistroParqueaderoVisitanteModel> registros = registroService.consultarTodos();
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{residenteId}/{parqueaderoId}")
    public ResponseEntity<Map<String, Object>> obtenerRegistro(@PathVariable Integer residenteId,
                                                                @PathVariable Integer parqueaderoId) {
        try {
            RegistroParqueaderoVisitanteModel registro = registroService.consultar(residenteId, parqueaderoId);
            
            if (registro == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(convertirAMap(registro));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/residente/{residenteId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorResidente(@PathVariable Integer residenteId) {
        try {
            List<RegistroParqueaderoVisitanteModel> registros = registroService.consultarPorResidente(residenteId);
            
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

    @GetMapping("/parqueadero/{parqueaderoId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorParqueadero(@PathVariable Integer parqueaderoId) {
        try {
            List<RegistroParqueaderoVisitanteModel> registros = registroService.consultarPorParqueadero(parqueaderoId);
            
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
            List<RegistroParqueaderoVisitanteModel> registros = registroService.consultarPorApartamento(apartamentoId);
            
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
            List<RegistroParqueaderoVisitanteModel> registros = registroService.consultarActivos();
            
            List<Map<String, Object>> response = registros.stream()
                .map(this::convertirAMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/parqueadero/{parqueaderoId}/activos")
    public ResponseEntity<List<Map<String, Object>>> obtenerActivosPorParqueadero(
            @PathVariable Integer parqueaderoId) {
        try {
            List<RegistroParqueaderoVisitanteModel> registros = registroService
                .consultarActivosPorParqueadero(parqueaderoId);
            
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
    
    @GetMapping("/apartamento/{apartamentoId}/activos")
    public ResponseEntity<List<Map<String, Object>>> obtenerActivosPorApartamento(
            @PathVariable Integer apartamentoId) {
        try {
            List<RegistroParqueaderoVisitanteModel> registros = registroService
                .consultarActivosPorApartamento(apartamentoId);
            
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
    
    @GetMapping("/placa/{placa}")
    public ResponseEntity<List<Map<String, Object>>> buscarPorPlaca(@PathVariable String placa) {
        try {
            List<RegistroParqueaderoVisitanteModel> registros = registroService.buscarPorPlaca(placa);
            
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
            Integer parqueaderoId = convertirAInteger(datos.get("parqueaderoId"));
            Integer apartamentoId = convertirAInteger(datos.get("apartamentoId"));
            
            if (residenteId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del residente es obligatorio");
            }
            
            if (parqueaderoId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del parqueadero es obligatorio");
            }
            
            // Verificar que no exista ya este registro
            RegistroParqueaderoVisitanteModel existente = registroService.consultar(residenteId, parqueaderoId);
            if (existente != null) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un registro para este residente en este parqueadero");
            }
            
            RegistroParqueaderoVisitanteModel registro = new RegistroParqueaderoVisitanteModel();
            registro.setFechaHoraEntrada((String) datos.get("fechaHoraEntrada"));
            registro.setPlaca((String) datos.get("placa"));
            
            String resultado = registroService.crear(registro, residenteId, parqueaderoId, apartamentoId);
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
            Integer parqueaderoId = convertirAInteger(datos.get("parqueaderoId"));
            String fechaHoraSalida = (String) datos.get("fechaHoraSalida");
            
            if (residenteId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del residente es obligatorio");
            }
            
            if (parqueaderoId == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del parqueadero es obligatorio");
            }
            
            if (fechaHoraSalida == null || fechaHoraSalida.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La fecha y hora de salida es obligatoria");
            }
            
            // Verificar que exista el registro
            RegistroParqueaderoVisitanteModel existente = registroService.consultar(residenteId, parqueaderoId);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un registro para este residente en este parqueadero");
            }
            
            String resultado = registroService.registrarSalida(residenteId, parqueaderoId, fechaHoraSalida);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar la salida: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{residenteId}/{parqueaderoId}")
    public ResponseEntity<String> actualizar(@PathVariable Integer residenteId,
                                            @PathVariable Integer parqueaderoId,
                                            @RequestBody Map<String, Object> datos) {
        try {
            // Verificar que exista el registro
            RegistroParqueaderoVisitanteModel existente = registroService.consultar(residenteId, parqueaderoId);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un registro con los IDs proporcionados");
            }
            
            Integer apartamentoId = convertirAInteger(datos.get("apartamentoId"));
            
            RegistroParqueaderoVisitanteModel registro = new RegistroParqueaderoVisitanteModel();
            registro.setFechaHoraEntrada((String) datos.get("fechaHoraEntrada"));
            registro.setPlaca((String) datos.get("placa"));
            
            if (datos.containsKey("fechaHoraSalida") && datos.get("fechaHoraSalida") != null) {
                registro.setFechaHoraSalida((String) datos.get("fechaHoraSalida"));
            }
            
            String resultado = registroService.actualizar(registro, residenteId, parqueaderoId, apartamentoId);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el registro: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{residenteId}/{parqueaderoId}")
    public ResponseEntity<String> eliminar(@PathVariable Integer residenteId,
                                          @PathVariable Integer parqueaderoId) {
        try {
            // Verificar que exista el registro
            RegistroParqueaderoVisitanteModel existente = registroService.consultar(residenteId, parqueaderoId);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un registro con los IDs proporcionados");
            }
            
            String resultado = registroService.eliminar(residenteId, parqueaderoId);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el registro: " + e.getMessage());
        }
    }
    
    @GetMapping("/parqueadero/{parqueaderoId}/count-activos")
    public ResponseEntity<Map<String, Object>> contarActivosPorParqueadero(@PathVariable Integer parqueaderoId) {
        try {
            long count = registroService.contarActivosPorParqueadero(parqueaderoId);
            Map<String, Object> response = new HashMap<>();
            response.put("parqueaderoId", parqueaderoId);
            response.put("visitantesActivos", count);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Método auxiliar para convertir el modelo a Map
     */
    private Map<String, Object> convertirAMap(RegistroParqueaderoVisitanteModel reg) {
        Map<String, Object> map = new HashMap<>();
        map.put("residenteId", reg.getResidente().getIdResidente());
        map.put("residenteNombre", reg.getResidente().getNombre() + " " + reg.getResidente().getApellido());
        map.put("residenteDocumento", reg.getResidente().getNumeroDocumento());
        map.put("parqueaderoId", reg.getParqueadero().getIdParqueadero());
        map.put("parqueaderoNumero", reg.getParqueadero().getNumero());
        
        if (reg.getApartamento() != null) {
            map.put("apartamentoId", reg.getApartamento().getIdApartamento());
            map.put("apartamentoNumero", reg.getApartamento().getNumero());
        } else {
            map.put("apartamentoId", null);
            map.put("apartamentoNumero", null);
        }
        
        map.put("placa", reg.getPlaca());
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