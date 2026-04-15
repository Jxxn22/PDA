package gescazone.demo.controller.api;

import gescazone.demo.model.ReservaSalonSocialModel;
import gescazone.demo.service.ReservaSalonSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaSalonSocialRestController {

    @Autowired
    private ReservaSalonSocialService reservaService;

    /**
     * GET /api/reservas/todas
     * Obtener todas las reservas
     */
    @GetMapping("/todas")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodas() {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.listarReservas();
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/{id}
     * Obtener una reserva por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        try {
            ReservaSalonSocialModel reserva = reservaService.buscarPorId(id);
            
            if (reserva == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(createReservaMap(reserva));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/salon/{idSalon}
     * Obtener reservas por ID de salón
     */
    @GetMapping("/salon/{idSalon}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorSalon(@PathVariable Integer idSalon) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarPorIdSalon(idSalon);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/usuario/{idUsuario}
     * Obtener reservas por ID de usuario
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarPorIdUsuario(idUsuario);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/salon/numero/{numero}
     * Obtener reservas por número de salón
     */
    @GetMapping("/salon/numero/{numero}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorNumeroSalon(@PathVariable String numero) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarPorNumeroSalon(numero);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/usuario/documento/{documento}
     * Obtener reservas por documento de usuario
     */
    @GetMapping("/usuario/documento/{documento}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorDocumentoUsuario(@PathVariable String documento) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarPorDocumentoUsuario(documento);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/fechas?inicio={inicio}&fin={fin}
     * Buscar reservas por rango de fechas
     */
    @GetMapping("/fechas")
    public ResponseEntity<List<Map<String, Object>>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarPorRangoFechas(inicio, fin);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/salon/{idSalon}/futuras
     * Obtener reservas futuras de un salón
     */
    @GetMapping("/salon/{idSalon}/futuras")
    public ResponseEntity<List<Map<String, Object>>> obtenerFuturasSalon(@PathVariable Integer idSalon) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarReservasFuturasSalon(idSalon);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/reservas/usuario/{idUsuario}/futuras
     * Obtener reservas futuras de un usuario
     */
    @GetMapping("/usuario/{idUsuario}/futuras")
    public ResponseEntity<List<Map<String, Object>>> obtenerFuturasUsuario(@PathVariable Integer idUsuario) {
        try {
            List<ReservaSalonSocialModel> reservas = reservaService.buscarReservasFuturasUsuario(idUsuario);
            
            List<Map<String, Object>> response = reservas.stream()
                .map(this::createReservaMap)
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/reservas/crear
     * Crear una nueva reserva
     */
    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            // Validar que los datos existen ANTES de convertir
            if (!datos.containsKey("idUsuario") || datos.get("idUsuario") == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del usuario es obligatorio");
            }
            
            if (!datos.containsKey("idSalon") || datos.get("idSalon") == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del salón es obligatorio");
            }
            
            if (!datos.containsKey("fechaYHoraReserva") || datos.get("fechaYHoraReserva") == null) {
                return ResponseEntity.badRequest()
                    .body("La fecha y hora de la reserva son obligatorias");
            }
            
            // Ahora sí convertir los valores
            Integer idUsuario = ((Number) datos.get("idUsuario")).intValue();
            Integer idSalon = ((Number) datos.get("idSalon")).intValue();
            String fechaHoraStr = (String) datos.get("fechaYHoraReserva");
            
            if (fechaHoraStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La fecha y hora de la reserva no pueden estar vacías");
            }
            
            // Parsear fecha (formato: "2024-11-13T14:30:00")
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr);
            
            ReservaSalonSocialModel reserva = new ReservaSalonSocialModel();
            reserva.setFechaYHoraReserva(fechaHora);
            
            String resultado = reservaService.guardarReserva(reserva, idUsuario, idSalon);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la reserva: " + e.getMessage());
        }
    }

    /**
     * PUT /api/reservas/actualizar/{id}
     * Actualizar una reserva
     */
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Long id,
                                            @RequestBody Map<String, Object> datos) {
        try {
            ReservaSalonSocialModel reservaExistente = reservaService.buscarPorId(id);
            if (reservaExistente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe una reserva con el ID: " + id);
            }
            
            // Validar que los datos existen ANTES de convertir
            if (!datos.containsKey("idUsuario") || datos.get("idUsuario") == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del usuario es obligatorio");
            }
            
            if (!datos.containsKey("idSalon") || datos.get("idSalon") == null) {
                return ResponseEntity.badRequest()
                    .body("El ID del salón es obligatorio");
            }
            
            if (!datos.containsKey("fechaYHoraReserva") || datos.get("fechaYHoraReserva") == null) {
                return ResponseEntity.badRequest()
                    .body("La fecha y hora de la reserva son obligatorias");
            }
            
            Integer idUsuario = ((Number) datos.get("idUsuario")).intValue();
            Integer idSalon = ((Number) datos.get("idSalon")).intValue();
            String fechaHoraStr = (String) datos.get("fechaYHoraReserva");
            
            if (fechaHoraStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La fecha y hora de la reserva no pueden estar vacías");
            }
            
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr);
            
            ReservaSalonSocialModel reserva = new ReservaSalonSocialModel();
            reserva.setIdReserva(id);
            reserva.setFechaYHoraReserva(fechaHora);
            
            String resultado = reservaService.guardarReserva(reserva, idUsuario, idSalon);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la reserva: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/reservas/eliminar/{id}
     * Eliminar una reserva
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            ReservaSalonSocialModel existente = reservaService.buscarPorId(id);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe una reserva con el ID: " + id);
            }
            
            String resultado = reservaService.eliminarReserva(id);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la reserva: " + e.getMessage());
        }
    }
    
    /**
     * Método helper para crear el Map de respuesta
     */
    private Map<String, Object> createReservaMap(ReservaSalonSocialModel res) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("idReserva", res.getIdReserva());
        map.put("fechaYHoraReserva", res.getFechaYHoraReserva().toString());
        
        // Información del usuario
        if (res.getUsuario() != null) {
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("id", res.getUsuario().getIdUsuario());
            usuarioMap.put("numeroDocumento", res.getUsuario().getNumeroDocumento());
            usuarioMap.put("nombres", res.getUsuario().getNombre());
            usuarioMap.put("apellidos", res.getUsuario().getApellido());
            usuarioMap.put("nombreCompleto", res.getUsuario().getNombre() + " " + res.getUsuario().getApellido());
            map.put("usuario", usuarioMap);
        }
        
        // Información del salón
        if (res.getSalon() != null) {
            Map<String, Object> salonMap = new HashMap<>();
            salonMap.put("id", res.getSalon().getIdSalonSocial());
            salonMap.put("numero", res.getSalon().getNumero());
            
            // Estado del salón
            if (res.getSalon().getEstado() != null) {
                salonMap.put("estado", res.getSalon().getEstado().getNombreEstado());
            }
            
            map.put("salon", salonMap);
        }
        
        return map;
    }
}