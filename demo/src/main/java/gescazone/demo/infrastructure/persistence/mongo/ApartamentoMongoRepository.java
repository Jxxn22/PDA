package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.ApartamentoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ApartamentoMongoRepository extends MongoRepository<ApartamentoDocument, String> {
    Optional<ApartamentoDocument> findByNumero(String numero);
    boolean existsByNumero(String numero);
    void deleteByNumero(String numero);
    List<ApartamentoDocument> findByNombreTipoOcupacion(String nombreTipoOcupacion);
    List<ApartamentoDocument> findByNombreEstadoCuenta(String nombreEstadoCuenta);
}