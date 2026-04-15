package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.UsuarioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioMongoRepository extends MongoRepository<UsuarioDocument, String> {
    Optional<UsuarioDocument> findByNumeroDocumento(String numeroDocumento);
    boolean existsByNumeroDocumento(String numeroDocumento);
    Optional<UsuarioDocument> findByNumeroDocumentoAndContrasena(String numeroDocumento, String contrasena);
    void deleteByNumeroDocumento(String numeroDocumento);
    List<UsuarioDocument> findByNombreRol(String nombreRol);
    List<UsuarioDocument> findByNombreTipoDocumento(String nombreTipoDocumento);
    Optional<UsuarioDocument> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}