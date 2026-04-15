package gescazone.demo.presentation.controller.api;

import gescazone.demo.model.*;
import gescazone.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RestController para obtener todos los catálogos de la aplicación
 * Estos endpoints serán usados para llenar ComboBox y Selects en el frontend
 */
@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin(origins = "*")
public class CatalogosRestController {

    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;
    
    @Autowired
    private TipoOcupacionRepository tipoOcupacionRepository;
    
    @Autowired
    private TipoResidenteRepository tipoResidenteRepository;
    
    @Autowired
    private EstadoCuentaRepository estadoCuentaRepository;
    
    @Autowired
    private EstadoRepository estadoRepository;

    /**
     * GET /api/catalogos/todos
     * Obtener todos los catálogos en una sola petición (RECOMENDADO)
     * Esto reduce el número de llamadas HTTP desde el frontend
     */
    @GetMapping("/todos")
    public ResponseEntity<Map<String, Object>> obtenerTodosCatalogos() {
        try {
            Map<String, Object> catalogos = new HashMap<>();
            catalogos.put("roles", rolRepository.findAll());
            catalogos.put("tiposDocumento", tipoDocumentoRepository.findAll());
            catalogos.put("tiposOcupacion", tipoOcupacionRepository.findAll());
            catalogos.put("tiposResidente", tipoResidenteRepository.findAll());
            catalogos.put("estadosCuenta", estadoCuentaRepository.findAll());
            catalogos.put("estados", estadoRepository.findAll());
            
            return ResponseEntity.ok(catalogos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cargar catálogos: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/roles
     * Obtener todos los roles disponibles
     */
    @GetMapping("/roles")
    public ResponseEntity<?> obtenerRoles() {
        try {
            List<RolModel> roles = rolRepository.findAll();
            
            // Validación: verificar si hay datos
            if (roles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensaje", "No hay roles registrados"));
            }
            
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener roles: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/tipos-documento
     * Obtener todos los tipos de documento (CC, TI, Pasaporte, etc.)
     */
    @GetMapping("/tipos-documento")
    public ResponseEntity<?> obtenerTiposDocumento() {
        try {
            List<TipoDocumentoModel> tipos = tipoDocumentoRepository.findAll();
            
            if (tipos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensaje", "No hay tipos de documento registrados"));
            }
            
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener tipos de documento: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/tipos-ocupacion
     * Obtener todos los tipos de ocupación
     */
    @GetMapping("/tipos-ocupacion")
    public ResponseEntity<?> obtenerTiposOcupacion() {
        try {
            List<TipoOcupacionModel> tipos = tipoOcupacionRepository.findAll();
            
            if (tipos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensaje", "No hay tipos de ocupación registrados"));
            }
            
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener tipos de ocupación: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/tipos-residente
     * Obtener todos los tipos de residente
     */
    @GetMapping("/tipos-residente")
    public ResponseEntity<?> obtenerTiposResidente() {
        try {
            List<TipoResidenteModel> tipos = tipoResidenteRepository.findAll();
            
            if (tipos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensaje", "No hay tipos de residente registrados"));
            }
            
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener tipos de residente: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/estados-cuenta
     * Obtener todos los estados de cuenta
     */
    @GetMapping("/estados-cuenta")
    public ResponseEntity<?> obtenerEstadosCuenta() {
        try {
            List<EstadoCuentaModel> estados = estadoCuentaRepository.findAll();
            
            if (estados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensaje", "No hay estados de cuenta registrados"));
            }
            
            return ResponseEntity.ok(estados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener estados de cuenta: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/estados
     * Obtener todos los estados generales
     */
    @GetMapping("/estados")
    public ResponseEntity<?> obtenerEstados() {
        try {
            List<EstadoModel> estados = estadoRepository.findAll();
            
            if (estados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensaje", "No hay estados registrados"));
            }
            
            return ResponseEntity.ok(estados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener estados: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/rol/{id}
     * Obtener un rol específico por ID
     */
    @GetMapping("/rol/{id}")
    public ResponseEntity<?> obtenerRolPorId(@PathVariable Integer id) {
        try {
            return rolRepository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Rol no encontrado con ID: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener rol: " + e.getMessage()));
        }
    }

    /**
     * GET /api/catalogos/tipo-documento/{id}
     * Obtener un tipo de documento específico por ID
     */
    @GetMapping("/tipo-documento/{id}")
    public ResponseEntity<?> obtenerTipoDocumentoPorId(@PathVariable Integer id) {
        try {
            return tipoDocumentoRepository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Tipo de documento no encontrado con ID: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener tipo de documento: " + e.getMessage()));
        }
    }
}