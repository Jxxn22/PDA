package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.RegistroParqueaderoVisitanteModel;
import gescazone.demo.domain.repository.RegistroParqueaderoVisitanteRepository;
import gescazone.demo.infrastructure.persistence.document.RegistroParqueaderoVisitanteDocument;
import gescazone.demo.infrastructure.persistence.mongo.RegistroParqueaderoVisitanteMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RegistroParqueaderoVisitanteRepositoryImpl implements RegistroParqueaderoVisitanteRepository {

    @Autowired
    private RegistroParqueaderoVisitanteMongoRepository mongoRepository;

    private RegistroParqueaderoVisitanteModel toModel(RegistroParqueaderoVisitanteDocument doc) {
        RegistroParqueaderoVisitanteModel model = new RegistroParqueaderoVisitanteModel();
        model.setId(doc.getId());
        model.setIdResidente(doc.getIdResidente());
        model.setIdParqueadero(doc.getIdParqueadero());
        model.setIdApartamento(doc.getIdApartamento());
        model.setFechaHoraEntrada(doc.getFechaHoraEntrada());
        model.setFechaHoraSalida(doc.getFechaHoraSalida());
        model.setPlaca(doc.getPlaca());
        return model;
    }

    private RegistroParqueaderoVisitanteDocument toDocument(RegistroParqueaderoVisitanteModel model) {
        RegistroParqueaderoVisitanteDocument doc = new RegistroParqueaderoVisitanteDocument();
        doc.setId(model.getId());
        doc.setIdResidente(model.getIdResidente());
        doc.setIdParqueadero(model.getIdParqueadero());
        doc.setIdApartamento(model.getIdApartamento());
        doc.setFechaHoraEntrada(model.getFechaHoraEntrada());
        doc.setFechaHoraSalida(model.getFechaHoraSalida());
        doc.setPlaca(model.getPlaca());
        return doc;
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByIdResidente(String idResidente) {
        return mongoRepository.findByIdResidente(idResidente).stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByIdParqueadero(String idParqueadero) {
        return mongoRepository.findByIdParqueadero(idParqueadero).stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByIdApartamento(String idApartamento) {
        return mongoRepository.findByIdApartamento(idApartamento).stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByParqueaderoNumero(String numero) {
        return mongoRepository.findAll().stream()
                .map(this::toModel).toList();
    }

    @Override
    public boolean existsByIdResidenteAndIdParqueadero(String idResidente, String idParqueadero) {
        return mongoRepository.existsByIdResidenteAndIdParqueadero(idResidente, idParqueadero);
    }

    @Override
    public Optional<RegistroParqueaderoVisitanteModel> findByIdResidenteAndIdParqueadero(String idResidente, String idParqueadero) {
        return mongoRepository.findByIdResidenteAndIdParqueadero(idResidente, idParqueadero).map(this::toModel);
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByFechaHoraSalidaIsNull() {
        return mongoRepository.findByFechaHoraSalidaIsNull().stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByIdResidenteAndFechaHoraSalidaIsNull(String idResidente) {
        return mongoRepository.findByIdResidenteAndFechaHoraSalidaIsNull(idResidente).stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findActivosByIdParqueadero(String idParqueadero) {
        return mongoRepository.findByIdParqueaderoAndFechaHoraSalidaIsNull(idParqueadero).stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findActivosByIdApartamento(String idApartamento) {
        return mongoRepository.findByIdApartamentoAndFechaHoraSalidaIsNull(idApartamento).stream().map(this::toModel).toList();
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findByPlacaContainingIgnoreCase(String placa) {
        return mongoRepository.findByPlacaContainingIgnoreCase(placa).stream().map(this::toModel).toList();
    }

    @Override
    public void deleteByIdResidente(String idResidente) {
        mongoRepository.findByIdResidente(idResidente)
                .forEach(doc -> mongoRepository.deleteById(doc.getId()));
    }

    @Override
    public void deleteByIdParqueadero(String idParqueadero) {
        mongoRepository.findByIdParqueadero(idParqueadero)
                .forEach(doc -> mongoRepository.deleteById(doc.getId()));
    }

    @Override
    public void deleteByIdApartamento(String idApartamento) {
        mongoRepository.findByIdApartamento(idApartamento)
                .forEach(doc -> mongoRepository.deleteById(doc.getId()));
    }

    @Override
    public long countActivosByIdParqueadero(String idParqueadero) {
        return mongoRepository.countByIdParqueaderoAndFechaHoraSalidaIsNull(idParqueadero);
    }

    @Override
    public long countActivosByIdApartamento(String idApartamento) {
        return mongoRepository.countByIdApartamentoAndFechaHoraSalidaIsNull(idApartamento);
    }

    @Override
    public long countByIdResidente(String idResidente) {
        return mongoRepository.countByIdResidente(idResidente);
    }

    @Override
    public List<RegistroParqueaderoVisitanteModel> findLatestRegistros() {
        return mongoRepository.findLatestRegistros().stream().map(this::toModel).toList();
    }

    @Override
    public RegistroParqueaderoVisitanteModel save(RegistroParqueaderoVisitanteModel registro) {
        return toModel(mongoRepository.save(toDocument(registro)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<RegistroParqueaderoVisitanteModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }
}