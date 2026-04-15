package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.ParqueaderoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ParqueaderoMongoRepository extends MongoRepository<ParqueaderoDocument, String> {
    Optional<ParqueaderoDocument> findByNumero(String numero);
    boolean existsByNumero(String numero);
    void deleteByNumero(String numero);
    List<ParqueaderoDocument> findByNombreEstado(String nombreEstado);
    List<ParqueaderoDocument> findByMedidas(String medidas);
}