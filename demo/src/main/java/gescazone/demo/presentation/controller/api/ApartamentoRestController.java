package gescazone.demo.presentation.controller.api;

import gescazone.demo.application.service.ApartamentoService;
import gescazone.demo.domain.model.ApartamentoModel;
import gescazone.demo.domain.model.TipoOcupacionModel;
import gescazone.demo.domain.model.EstadoCuentaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apartamentos")
@CrossOrigin(origins = "*")
public class ApartamentoRestController {

    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping("/todos")
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<ApartamentoModel> apartamentos = apartamentoService.consultarTodos();

            List<Map<String, Object>> response = apartamentos.stream().map(apt -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", apt.getId());
                map.put("numero", apt.getNumero());
                map.put("medidas", apt.getMedidas());
                map.put("telefono", apt.getTelefono());

                if (apt.getTipoOcupacion() != null) {
                    map.put("tipoOcupacion", apt.getTipoOcupacion().getNombreTipoOcupacion());
                }

                if (apt.getEstadoCuenta() != null) {
                    map.put("estadoCuenta", apt.getEstadoCuenta().getNombreEstadoCuenta());
                }

                return map;
            }).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar apartamentos: " + e.getMessage());
        }
    }

    @GetMapping("/{numero}")
    public ResponseEntity<?> obtenerPorNumero(@PathVariable String numero) {
        try {
            ApartamentoModel apartamento = apartamentoService.consultar(numero);

            if (apartamento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe un apartamento con el número: " + numero);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", apartamento.getId());
            response.put("numero", apartamento.getNumero());
            response.put("medidas", apartamento.getMedidas());
            response.put("telefono", apartamento.getTelefono());

            if (apartamento.getTipoOcupacion() != null) {
                response.put("tipoOcupacion", apartamento.getTipoOcupacion().getNombreTipoOcupacion());
            }

            if (apartamento.getEstadoCuenta() != null) {
                response.put("estadoCuenta", apartamento.getEstadoCuenta().getNombreEstadoCuenta());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar apartamento: " + e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            String numero = (String) datos.get("numero");
            if (numero == null || numero.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número de apartamento es obligatorio");

            String nombreTipoOcupacion = (String) datos.get("tipoOcupacion");
            if (nombreTipoOcupacion == null || nombreTipoOcupacion.trim().isEmpty())
                return ResponseEntity.badRequest().body("El tipo de ocupación es obligatorio");

            String nombreEstadoCuenta = (String) datos.get("estadoCuenta");
            if (nombreEstadoCuenta == null || nombreEstadoCuenta.trim().isEmpty())
                return ResponseEntity.badRequest().body("El estado de cuenta es obligatorio");

            TipoOcupacionModel tipoOcupacion = new TipoOcupacionModel();
            tipoOcupacion.setNombreTipoOcupacion(nombreTipoOcupacion.trim());

            EstadoCuentaModel estadoCuenta = new EstadoCuentaModel();
            estadoCuenta.setNombreEstadoCuenta(nombreEstadoCuenta.trim());

            ApartamentoModel apartamento = new ApartamentoModel();
            apartamento.setNumero(numero.trim());
            apartamento.setMedidas((String) datos.get("medidas"));
            apartamento.setTipoOcupacion(tipoOcupacion);
            apartamento.setEstadoCuenta(estadoCuenta);

            if (datos.containsKey("telefono") && datos.get("telefono") != null) {
                Object telefonoObj = datos.get("telefono");
                if (telefonoObj instanceof Number) {
                    apartamento.setTelefono(((Number) telefonoObj).longValue());
                } else if (telefonoObj instanceof String) {
                    try {
                        apartamento.setTelefono(Long.parseLong((String) telefonoObj));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body("El teléfono debe ser un número válido");
                    }
                }
            }

            String resultado = apartamentoService.crear(apartamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el apartamento: " + e.getMessage());
        }
    }

    @PostMapping("/actualizar")
    public ResponseEntity<String> actualizar(@RequestBody Map<String, Object> datos) {
        try {
            String numeroActual = (String) datos.get("numeroActual");
            if (numeroActual == null || numeroActual.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número actual del apartamento es obligatorio");

            ApartamentoModel apartamento = apartamentoService.consultar(numeroActual.trim());
            if (apartamento == null)
                return ResponseEntity.badRequest()
                        .body("No existe un apartamento con el número: " + numeroActual);

            String numeroNuevo = (String) datos.get("numero");
            if (numeroNuevo == null || numeroNuevo.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número de apartamento es obligatorio");

            apartamento.setNumero(numeroNuevo.trim());

            if (datos.containsKey("medidas")) {
                apartamento.setMedidas((String) datos.get("medidas"));
            }

            if (datos.containsKey("tipoOcupacion") && datos.get("tipoOcupacion") != null) {
                TipoOcupacionModel tipoOcupacion = new TipoOcupacionModel();
                tipoOcupacion.setNombreTipoOcupacion(((String) datos.get("tipoOcupacion")).trim());
                apartamento.setTipoOcupacion(tipoOcupacion);
            }

            if (datos.containsKey("estadoCuenta") && datos.get("estadoCuenta") != null) {
                EstadoCuentaModel estadoCuenta = new EstadoCuentaModel();
                estadoCuenta.setNombreEstadoCuenta(((String) datos.get("estadoCuenta")).trim());
                apartamento.setEstadoCuenta(estadoCuenta);
            }

            if (datos.containsKey("telefono") && datos.get("telefono") != null) {
                Object telefonoObj = datos.get("telefono");
                if (telefonoObj instanceof Number) {
                    apartamento.setTelefono(((Number) telefonoObj).longValue());
                } else if (telefonoObj instanceof String) {
                    try {
                        apartamento.setTelefono(Long.parseLong((String) telefonoObj));
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body("El teléfono debe ser un número válido");
                    }
                }
            }

            String resultado = apartamentoService.actualizar(apartamento);
            return ResponseEntity.ok(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el apartamento: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{numero}")
    public ResponseEntity<String> eliminar(@PathVariable String numero) {
        try {
            if (numero == null || numero.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número de apartamento es obligatorio");

            String resultado = apartamentoService.eliminar(numero.trim());
            return ResponseEntity.ok(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el apartamento: " + e.getMessage());
        }
    }

    @GetMapping("/tipo-ocupacion/{nombre}")
    public ResponseEntity<?> consultarPorTipoOcupacion(@PathVariable String nombre) {
        try {
            List<ApartamentoModel> apartamentos = apartamentoService.consultarPorTipoOcupacion(nombre);
            return ResponseEntity.ok(apartamentos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar: " + e.getMessage());
        }
    }

    @GetMapping("/estado-cuenta/{nombre}")
    public ResponseEntity<?> consultarPorEstadoCuenta(@PathVariable String nombre) {
        try {
            List<ApartamentoModel> apartamentos = apartamentoService.consultarPorEstadoCuenta(nombre);
            return ResponseEntity.ok(apartamentos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar: " + e.getMessage());
        }
    }
}