package gescazone.demo.presentation.controller.api;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.model.RolModel;
import gescazone.demo.domain.model.TipoDocumentoModel;
import gescazone.demo.application.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/todos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        try {
            List<UsuarioModel> usuarios = usuarioService.consultarTodos();
            
            List<Map<String, Object>> response = usuarios.stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idUsuario", user.getIdUsuario());
                    map.put("numeroDocumento", user.getNumeroDocumento());
                    map.put("nombre", user.getNombre());
                    map.put("apellido", user.getApellido());
                    map.put("correo", user.getCorreo());
                    
                    // Incluir información del tipo de documento
                    if (user.getTipoDocumento() != null) {
                        Map<String, Object> tipoDoc = new HashMap<>();
                        tipoDoc.put("id", user.getTipoDocumento().getIdTipoDocumento());
                        tipoDoc.put("nombre", user.getTipoDocumento().getNombreTipoDocumento());
                        map.put("tipoDocumento", tipoDoc);
                    }
                    
                    // Incluir información del rol
                    if (user.getRol() != null) {
                        Map<String, Object> rol = new HashMap<>();
                        rol.put("id", user.getRol().getIdRol());
                        rol.put("nombre", user.getRol().getNombreRol());
                        map.put("rol", rol);
                    }
                    
                    return map;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{numeroDocumento}")
    public ResponseEntity<Map<String, Object>> obtenerPorDocumento(@PathVariable String numeroDocumento) {
        try {
            UsuarioModel usuario = usuarioService.consultar(numeroDocumento);
            
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("idUsuario", usuario.getIdUsuario());
            response.put("numeroDocumento", usuario.getNumeroDocumento());
            response.put("nombre", usuario.getNombre());
            response.put("apellido", usuario.getApellido());
            response.put("correo", usuario.getCorreo());
            
            if (usuario.getTipoDocumento() != null) {
                Map<String, Object> tipoDoc = new HashMap<>();
                tipoDoc.put("id", usuario.getTipoDocumento().getIdTipoDocumento());
                tipoDoc.put("nombre", usuario.getTipoDocumento().getNombreTipoDocumento());
                response.put("tipoDocumento", tipoDoc);
            }
            
            if (usuario.getRol() != null) {
                Map<String, Object> rol = new HashMap<>();
                rol.put("id", usuario.getRol().getIdRol());
                rol.put("nombre", usuario.getRol().getNombreRol());
                response.put("rol", rol);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody Map<String, Object> datos) {
        try {
            String numeroDocumento = (String) datos.get("numeroDocumento");
            
            if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número de documento es obligatorio");
            }
            
            UsuarioModel existente = usuarioService.consultar(numeroDocumento);
            if (existente != null) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un usuario con el documento: " + numeroDocumento);
            }
            
            String contrasena = (String) datos.get("contrasena");
            if (contrasena == null || contrasena.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La contraseña es obligatoria");
            }
            
            UsuarioModel usuario = new UsuarioModel();
            usuario.setNumeroDocumento(numeroDocumento);
            usuario.setNombre((String) datos.get("nombre"));
            usuario.setApellido((String) datos.get("apellido"));
            usuario.setCorreo((String) datos.get("correo"));
            usuario.setContrasena(contrasena);
            
            // Configurar tipo de documento
            if (datos.containsKey("idTipoDocumento")) {
                TipoDocumentoModel tipoDoc = new TipoDocumentoModel();
                tipoDoc.setIdTipoDocumento(((Number) datos.get("idTipoDocumento")).intValue());
                usuario.setTipoDocumento(tipoDoc);
            }
            
            // Configurar rol
            if (datos.containsKey("idRol")) {
                RolModel rol = new RolModel();
                rol.setIdRol(((Number) datos.get("idRol")).intValue());
                usuario.setRol(rol);
            }
            
            String resultado = usuarioService.crear(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el usuario: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{numeroDocumento}")
    public ResponseEntity<String> actualizar(
            @PathVariable String numeroDocumento,
            @RequestBody Map<String, Object> datos) {
        try {
            UsuarioModel usuario = usuarioService.consultar(numeroDocumento);
            if (usuario == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un usuario con el documento: " + numeroDocumento);
            }
            
            // Actualizar campos básicos
            if (datos.containsKey("nombre")) {
                usuario.setNombre((String) datos.get("nombre"));
            }
            if (datos.containsKey("apellido")) {
                usuario.setApellido((String) datos.get("apellido"));
            }
            if (datos.containsKey("correo")) {
                usuario.setCorreo((String) datos.get("correo"));
            }
            
            // Actualizar tipo de documento si viene
            if (datos.containsKey("idTipoDocumento")) {
                TipoDocumentoModel tipoDoc = new TipoDocumentoModel();
                tipoDoc.setIdTipoDocumento(((Number) datos.get("idTipoDocumento")).intValue());
                usuario.setTipoDocumento(tipoDoc);
            }
            
            // Actualizar rol si viene
            if (datos.containsKey("idRol")) {
                RolModel rol = new RolModel();
                rol.setIdRol(((Number) datos.get("idRol")).intValue());
                usuario.setRol(rol);
            }
            
            String resultado = usuarioService.actualizar(usuario);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{numeroDocumento}")
    public ResponseEntity<String> eliminar(@PathVariable String numeroDocumento) {
        try {
            if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número de documento es obligatorio");
            }
            
            UsuarioModel existente = usuarioService.consultar(numeroDocumento);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un usuario con el documento: " + numeroDocumento);
            }
            
            String resultado = usuarioService.eliminar(numeroDocumento);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<String> cambiarContrasena(@RequestBody Map<String, String> datos) {
        try {
            String numeroDocumento = datos.get("numeroDocumento");
            String contrasenaActual = datos.get("contrasenaActual");
            String contrasenaNueva = datos.get("contrasenaNueva");
            String confirmarContrasena = datos.get("confirmarContrasena");
            
            if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("El número de documento es obligatorio");
            }
            
            if (contrasenaActual == null || contrasenaActual.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La contraseña actual es obligatoria");
            }
            
            if (contrasenaNueva == null || contrasenaNueva.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("La contraseña nueva es obligatoria");
            }
            
            if (!contrasenaNueva.equals(confirmarContrasena)) {
                return ResponseEntity.badRequest()
                    .body("Las contraseñas nuevas no coinciden");
            }
            
            UsuarioModel existente = usuarioService.consultar(numeroDocumento);
            if (existente == null) {
                return ResponseEntity.badRequest()
                    .body("No existe un usuario con el documento: " + numeroDocumento);
            }
            
            String resultado = usuarioService.cambiarContrasena(numeroDocumento, contrasenaActual, contrasenaNueva);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cambiar la contraseña: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> datos) {
        try {
            String numeroDocumento = datos.get("numeroDocumento");
            String contrasena = datos.get("contrasena");
            
            UsuarioModel usuario = usuarioService.validarLogin(numeroDocumento, contrasena);
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales inválidas"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Login exitoso");
            response.put("numeroDocumento", usuario.getNumeroDocumento());
            response.put("nombre", usuario.getNombre());
            response.put("apellido", usuario.getApellido());
            
            if (usuario.getRol() != null) {
                response.put("rol", usuario.getRol().getNombreRol());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error en el login"));
        }
    }
}