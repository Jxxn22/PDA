package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.ResidenteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ResidenteMongoRepository extends MongoRepository<ResidenteDocument, String> {
    Optional<ResidenteDocument> findByNumeroDocumento(Integer numeroDocumento);
    boolean existsByNumeroDocumento(Integer numeroDocumento);
    void deleteByNumeroDocumento(Integer numeroDocumento);
    List<ResidenteDocument> findByNombreTipoResidente(String nombreTipoResidente);
    List<ResidenteDocument> findByNombreTipoDocumento(String nombreTipoDocumento);
    List<ResidenteDocument> findByNombreContainingIgnoreCase(String nombre);
    List<ResidenteDocument> findByApellidoContainingIgnoreCase(String apellido);
    List<ResidenteDocument> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
}