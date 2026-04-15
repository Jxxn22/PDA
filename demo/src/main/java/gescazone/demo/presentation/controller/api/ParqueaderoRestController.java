package gescazone.demo.presentation.controller.api;

import gescazone.demo.application.service.ParqueaderoService;
import gescazone.demo.domain.model.EstadoModel;
import gescazone.demo.domain.model.ParqueaderoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parqueaderos")
@CrossOrigin(origins = "*")
public class ParqueaderoRestController {

    @Autowired
    private ParqueaderoService parqueaderoService;

    @GetMapping("/todos")
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<ParqueaderoModel> parqueaderos = parqueaderoService.consultarTodos();

            List<Map<String, Object>> response = parqueaderos.stream().map(parq -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", parq.getId());
                map.put("numero", parq.getNumero());
                map.put("medidas", parq.getMedidas());
                map.put("telefono", parq.getTelefono());

                if (parq.getEstado() != null) {
                    map.put("estado", parq.getEstado().getNombreEstado());
                }

                return map;
            }).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar parqueaderos: " + e.getMessage());
        }
    }

    @GetMapping("/{numero}")
    public ResponseEntity<?> obtenerPorNumero(@PathVariable String numero) {
        try {
            ParqueaderoModel parqueadero = parqueaderoService.consultar(numero);

            if (parqueadero == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe un parqueadero con el número: " + numero);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", parqueadero.getId());
            response.put("numero", parqueadero.getNumero());
            response.put("medidas", parqueadero.getMedidas());
            response.put("telefono", parqueadero.getTelefono());

            if (parqueadero.getEstado() != null) {
                response.put("estado", parqueadero.getEstado().getNombreEstado());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar parqueadero: " + e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            String numero = (String) datos.get("numero");
            if (numero == null || numero.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número del parqueadero es obligatorio");

            if (!numero.matches("^P-\\d{2,3}$"))
                return ResponseEntity.badRequest().body("El número debe tener el formato P-01, P-02, etc.");

            String medidas = (String) datos.get("medidas");
            if (medidas == null || medidas.trim().isEmpty())
                return ResponseEntity.badRequest().body("Las medidas son obligatorias");

            try {
                double medidasNum = Double.parseDouble(medidas.trim());
                if (medidasNum <= 0)
                    return ResponseEntity.badRequest().body("Las medidas deben ser un número positivo");
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Las medidas deben ser un número válido");
            }

            if (!datos.containsKey("telefono") || datos.get("telefono") == null)
                return ResponseEntity.badRequest().body("El teléfono es obligatorio");

            Long telefono = null;
            Object telefonoObj = datos.get("telefono");
            if (telefonoObj instanceof Number) {
                telefono = ((Number) telefonoObj).longValue();
            } else if (telefonoObj instanceof String) {
                try {
                    telefono = Long.parseLong((String) telefonoObj);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("El teléfono debe ser un número válido");
                }
            }

            if (!String.valueOf(telefono).matches("^3\\d{9}$"))
                return ResponseEntity.badRequest().body("El teléfono debe iniciar con 3 y tener 10 dígitos");

            String nombreEstado = (String) datos.get("estado");
            if (nombreEstado == null || nombreEstado.trim().isEmpty())
                return ResponseEntity.badRequest().body("El estado es obligatorio");

            EstadoModel estado = new EstadoModel();
            estado.setNombreEstado(nombreEstado.trim());

            ParqueaderoModel parqueadero = new ParqueaderoModel();
            parqueadero.setNumero(numero.trim().toUpperCase());
            parqueadero.setMedidas(medidas.trim());
            parqueadero.setTelefono(telefono);
            parqueadero.setEstado(estado);

            String resultado = parqueaderoService.crear(parqueadero);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el parqueadero: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{numero}")
    public ResponseEntity<String> actualizar(@PathVariable String numero,
                                             @RequestBody Map<String, Object> datos) {
        try {
            ParqueaderoModel parqueadero = parqueaderoService.consultar(numero);
            if (parqueadero == null)
                return ResponseEntity.badRequest()
                        .body("No existe un parqueadero con el número: " + numero);

            String numeroNuevo = (String) datos.get("numero");
            if (numeroNuevo == null || numeroNuevo.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número del parqueadero es obligatorio");

            if (!numeroNuevo.matches("^P-\\d{2,3}$"))
                return ResponseEntity.badRequest().body("El número debe tener el formato P-01, P-02, etc.");

            parqueadero.setNumero(numeroNuevo.trim().toUpperCase());

            if (datos.containsKey("medidas") && datos.get("medidas") != null) {
                String medidas = (String) datos.get("medidas");
                if (!medidas.trim().isEmpty()) {
                    try {
                        double medidasNum = Double.parseDouble(medidas.trim());
                        if (medidasNum <= 0)
                            return ResponseEntity.badRequest().body("Las medidas deben ser un número positivo");
                        parqueadero.setMedidas(medidas.trim());
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body("Las medidas deben ser un número válido");
                    }
                }
            }

            if (datos.containsKey("telefono") && datos.get("telefono") != null) {
                Object telefonoObj = datos.get("telefono");
                Long telefono = null;
                if (telefonoObj instanceof Number) {
                    telefono = ((Number) telefonoObj).longValue();
                } else if (telefonoObj instanceof String) {
                    try {
                        telefono = Long.parseLong((String) telefonoObj);
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body("El teléfono debe ser un número válido");
                    }
                }
                if (!String.valueOf(telefono).matches("^3\\d{9}$"))
                    return ResponseEntity.badRequest().body("El teléfono debe iniciar con 3 y tener 10 dígitos");
                parqueadero.setTelefono(telefono);
            }

            if (datos.containsKey("estado") && datos.get("estado") != null) {
                EstadoModel estado = new EstadoModel();
                estado.setNombreEstado(((String) datos.get("estado")).trim());
                parqueadero.setEstado(estado);
            }

            String resultado = parqueaderoService.actualizar(parqueadero);
            return ResponseEntity.ok(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el parqueadero: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{numero}")
    public ResponseEntity<String> eliminar(@PathVariable String numero) {
        try {
            if (numero == null || numero.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número del parqueadero es obligatorio");

            String resultado = parqueaderoService.eliminar(numero.trim());
            return ResponseEntity.ok(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el parqueadero: " + e.getMessage());
        }
    }

    @PostMapping("/cambiar-estado")
    public ResponseEntity<String> cambiarEstado(@RequestBody Map<String, Object> datos) {
        try {
            String numero = (String) datos.get("numero");
            if (numero == null || numero.trim().isEmpty())
                return ResponseEntity.badRequest().body("El número del parqueadero es obligatorio");

            String nombreEstado = (String) datos.get("estado");
            if (nombreEstado == null || nombreEstado.trim().isEmpty())
                return ResponseEntity.badRequest().body("El estado es obligatorio");

            String resultado = parqueaderoService.setEstado(numero.trim(), nombreEstado.trim());
            return ResponseEntity.ok(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cambiar el estado: " + e.getMessage());
        }
    }

    @GetMapping("/estado/{nombreEstado}")
    public ResponseEntity<?> consultarPorEstado(@PathVariable String nombreEstado) {
        try {
            List<ParqueaderoModel> parqueaderos = parqueaderoService.consultarPorEstado(nombreEstado);
            return ResponseEntity.ok(parqueaderos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar: " + e.getMessage());
        }
    }

    @GetMapping("/medida/{medida}")
    public ResponseEntity<?> consultarPorMedida(@PathVariable String medida) {
        try {
            List<ParqueaderoModel> parqueaderos = parqueaderoService.consultarPorMedida(medida);
            return ResponseEntity.ok(parqueaderos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar: " + e.getMessage());
        }
    }
}