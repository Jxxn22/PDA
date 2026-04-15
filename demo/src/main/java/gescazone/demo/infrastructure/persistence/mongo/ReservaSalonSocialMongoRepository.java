package gescazone.demo.infrastructure.persistence.mongo;

import gescazone.demo.infrastructure.persistence.document.ReservaSalonSocialDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaSalonSocialMongoRepository extends MongoRepository<ReservaSalonSocialDocument, String> {
    List<ReservaSalonSocialDocument> findByIdSalon(String idSalon);
    List<ReservaSalonSocialDocument> findByIdUsuario(String idUsuario);
    boolean existsByIdSalonAndFechaYHoraReserva(String idSalon, LocalDateTime fechaYHora);
    List<ReservaSalonSocialDocument> findByFechaYHoraReservaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("{'idSalon': ?0, 'fechaYHoraReserva': {$gt: ?1}}")
    List<ReservaSalonSocialDocument> findReservasFuturasPorSalon(String idSalon, LocalDateTime ahora);

    @Query("{'idUsuario': ?0, 'fechaYHoraReserva': {$gt: ?1}}")
    List<ReservaSalonSocialDocument> findReservasFuturasPorUsuario(String idUsuario, LocalDateTime ahora);

    @Query("{'idSalon': ?0, 'fechaYHoraReserva': {$gte: ?1, $lt: ?2}}")
    List<ReservaSalonSocialDocument> findReservasEnMismoDia(String idSalon, LocalDateTime inicioDia, LocalDateTime finDia);
}