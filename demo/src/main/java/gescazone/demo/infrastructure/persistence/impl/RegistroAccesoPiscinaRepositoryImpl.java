package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.RegistroAccesoPiscinaModel;
import gescazone.demo.domain.repository.RegistroAccesoPiscinaRepository;
import gescazone.demo.infrastructure.persistence.document.RegistroAccesoPiscinaDocument;
import gescazone.demo.infrastructure.persistence.mongo.RegistroAccesoPiscinaMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class RegistroAccesoPiscinaRepositoryImpl implements RegistroAccesoPiscinaRepository {

    @Autowired
    private RegistroAccesoPiscinaMongoRepository mongoRepository;

    private RegistroAccesoPiscinaModel toModel(RegistroAccesoPiscinaDocument doc) {
        RegistroAccesoPiscinaModel model = new RegistroAccesoPiscinaModel();
        model.setId(doc.getId());
        model.setIdApartamento(doc.getIdApartamento());
        model.setIdResidente(doc.getIdResidente());
        model.setFechaHora(doc.getFechaHora());
        return model;
    }

    private RegistroAccesoPiscinaDocument toDocument(RegistroAccesoPiscinaModel model) {
        RegistroAccesoPiscinaDocument doc = new RegistroAccesoPiscinaDocument();
        doc.setId(model.getId());
        doc.setIdApartamento(model.getIdApartamento());
        doc.setIdResidente(model.getIdResidente());
        doc.setFechaHora(model.getFechaHora());
        return doc;
    }

    @Override
    public List<RegistroAccesoPiscinaModel> findByApartamentoNumero(String numero) {
        return mongoRepository.findByIdApartamento(numero)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroAccesoPiscinaModel> findByResidenteNumeroDocumento(Integer numeroDocumento) {
        return mongoRepository.findByIdResidente(numeroDocumento.toString())
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroAccesoPiscinaModel> findRegistrosHoy() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);
        return mongoRepository.findByFechaHoraBetween(inicioDia, finDia)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroAccesoPiscinaModel> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin) {
        return mongoRepository.findByFechaHoraBetween(inicio, fin)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroAccesoPiscinaModel> findAllOrderByFechaHoraDesc() {
        return mongoRepository.findAllOrderByFechaHoraDesc()
                .stream().map(this::toModel).toList();
    }

    @Override
    public RegistroAccesoPiscinaModel save(RegistroAccesoPiscinaModel registro) {
        return toModel(mongoRepository.save(toDocument(registro)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<RegistroAccesoPiscinaModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public boolean existsById(String id) {
        return mongoRepository.existsById(id);
    }
}