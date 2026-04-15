package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.ReservaSalonSocialModel;
import gescazone.demo.domain.repository.ReservaSalonSocialRepository;
import gescazone.demo.infrastructure.persistence.document.ReservaSalonSocialDocument;
import gescazone.demo.infrastructure.persistence.mongo.ReservaSalonSocialMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaSalonSocialRepositoryImpl implements ReservaSalonSocialRepository {

    @Autowired
    private ReservaSalonSocialMongoRepository mongoRepository;

    private ReservaSalonSocialModel toModel(ReservaSalonSocialDocument doc) {
        ReservaSalonSocialModel model = new ReservaSalonSocialModel();
        model.setId(doc.getId());
        model.setIdUsuario(doc.getIdUsuario());
        model.setIdSalon(doc.getIdSalon());
        model.setFechaYHoraReserva(doc.getFechaYHoraReserva());
        return model;
    }

    private ReservaSalonSocialDocument toDocument(ReservaSalonSocialModel model) {
        ReservaSalonSocialDocument doc = new ReservaSalonSocialDocument();
        doc.setId(model.getId());
        doc.setIdUsuario(model.getIdUsuario());
        doc.setIdSalon(model.getIdSalon());
        doc.setFechaYHoraReserva(model.getFechaYHoraReserva());
        return doc;
    }

    @Override
    public List<ReservaSalonSocialModel> findByIdSalon(String idSalon) {
        return mongoRepository.findByIdSalon(idSalon).stream().map(this::toModel).toList();
    }

    @Override
    public List<ReservaSalonSocialModel> findBySalonNumero(String numero) {
        return mongoRepository.findAll().stream()
                .map(this::toModel).toList();
    }

    @Override
    public List<ReservaSalonSocialModel> findByIdUsuario(String idUsuario) {
        return mongoRepository.findByIdUsuario(idUsuario).stream().map(this::toModel).toList();
    }

    @Override
    public List<ReservaSalonSocialModel> findByUsuarioNumeroDocumento(String documento) {
        return mongoRepository.findAll().stream()
                .map(this::toModel).toList();
    }

    @Override
    public boolean existsByIdSalonAndFechaYHoraReserva(String idSalon, LocalDateTime fechaYHora) {
        return mongoRepository.existsByIdSalonAndFechaYHoraReserva(idSalon, fechaYHora);
    }

    @Override
    public List<ReservaSalonSocialModel> findByFechaYHoraReservaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return mongoRepository.findByFechaYHoraReservaBetween(inicio, fin)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ReservaSalonSocialModel> findReservasFuturasPorSalon(String idSalon, LocalDateTime ahora) {
        return mongoRepository.findReservasFuturasPorSalon(idSalon, ahora)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ReservaSalonSocialModel> findReservasFuturasPorUsuario(String idUsuario, LocalDateTime ahora) {
        return mongoRepository.findReservasFuturasPorUsuario(idUsuario, ahora)
                .stream().map(this::toModel).toList();
    }

    @Override
    public boolean existsReservaEnMismoDia(String idSalon, LocalDateTime fecha) {
        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = fecha.toLocalDate().atTime(23, 59, 59);
        return !mongoRepository.findReservasEnMismoDia(idSalon, inicioDia, finDia).isEmpty();
    }

    @Override
    public ReservaSalonSocialModel save(ReservaSalonSocialModel reserva) {
        return toModel(mongoRepository.save(toDocument(reserva)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<ReservaSalonSocialModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<ReservaSalonSocialModel> findAll() {
        return mongoRepository.findAll().stream().map(this::toModel).toList();
    }
}