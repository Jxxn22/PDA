package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.RegistroParqueaderoVisitanteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RegistroParqueaderoVisitanteMongoRepository extends MongoRepository<RegistroParqueaderoVisitanteDocument, String> {
    List<RegistroParqueaderoVisitanteDocument> findByIdResidente(String idResidente);
    List<RegistroParqueaderoVisitanteDocument> findByIdParqueadero(String idParqueadero);
    List<RegistroParqueaderoVisitanteDocument> findByIdApartamento(String idApartamento);
    boolean existsByIdResidenteAndIdParqueadero(String idResidente, String idParqueadero);
    Optional<RegistroParqueaderoVisitanteDocument> findByIdResidenteAndIdParqueadero(String idResidente, String idParqueadero);
    List<RegistroParqueaderoVisitanteDocument> findByFechaHoraSalidaIsNull();
    List<RegistroParqueaderoVisitanteDocument> findByIdResidenteAndFechaHoraSalidaIsNull(String idResidente);
    List<RegistroParqueaderoVisitanteDocument> findByIdParqueaderoAndFechaHoraSalidaIsNull(String idParqueadero);
    List<RegistroParqueaderoVisitanteDocument> findByIdApartamentoAndFechaHoraSalidaIsNull(String idApartamento);
    List<RegistroParqueaderoVisitanteDocument> findByPlacaContainingIgnoreCase(String placa);
    long countByIdParqueaderoAndFechaHoraSalidaIsNull(String idParqueadero);
    long countByIdApartamentoAndFechaHoraSalidaIsNull(String idApartamento);
    long countByIdResidente(String idResidente);

    @Query(value = "{}", sort = "{'fechaHoraEntrada': -1}")
    List<RegistroParqueaderoVisitanteDocument> findLatestRegistros();
}