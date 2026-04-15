package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.SalonSocialDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface SalonSocialMongoRepository extends MongoRepository<SalonSocialDocument, String> {
    Optional<SalonSocialDocument> findByNumero(String numero);
    boolean existsByNumero(String numero);
    void deleteByNumero(String numero);
    List<SalonSocialDocument> findByNombreEstado(String nombreEstado);
    List<SalonSocialDocument> findByMedidas(String medidas);
}