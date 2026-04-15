package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.RegistroVisitanteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RegistroVisitanteMongoRepository extends MongoRepository<RegistroVisitanteDocument, String> {
    List<RegistroVisitanteDocument> findByIdResidente(String idResidente);
    List<RegistroVisitanteDocument> findByIdApartamento(String idApartamento);
    boolean existsByIdResidenteAndIdApartamento(String idResidente, String idApartamento);
    Optional<RegistroVisitanteDocument> findByIdResidenteAndIdApartamento(String idResidente, String idApartamento);
    List<RegistroVisitanteDocument> findByFechaHoraSalidaIsNull();
    List<RegistroVisitanteDocument> findByIdResidenteAndFechaHoraSalidaIsNull(String idResidente);
    List<RegistroVisitanteDocument> findByIdApartamentoAndFechaHoraSalidaIsNull(String idApartamento);
    long countByIdApartamentoAndFechaHoraSalidaIsNull(String idApartamento);
    long countByIdResidente(String idResidente);

    @Query(value = "{}", sort = "{'fechaHoraEntrada': -1}")
    List<RegistroVisitanteDocument> findLatestRegistros();
}