package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.RegistroAccesoPiscinaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistroAccesoPiscinaMongoRepository extends MongoRepository<RegistroAccesoPiscinaDocument, String> {
    List<RegistroAccesoPiscinaDocument> findByIdApartamento(String idApartamento);
    List<RegistroAccesoPiscinaDocument> findByIdResidente(String idResidente);
    List<RegistroAccesoPiscinaDocument> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query(value = "{}", sort = "{'fechaHora': -1}")
    List<RegistroAccesoPiscinaDocument> findAllOrderByFechaHoraDesc();
}