package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.ParqueaderoModel;
import gescazone.demo.domain.model.EstadoModel;
import gescazone.demo.domain.repository.ParqueaderoRepository;
import gescazone.demo.infrastructure.persistence.document.ParqueaderoDocument;
import gescazone.demo.infrastructure.persistence.mongo.ParqueaderoMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParqueaderoRepositoryImpl implements ParqueaderoRepository {

    @Autowired
    private ParqueaderoMongoRepository mongoRepository;

    private ParqueaderoModel toModel(ParqueaderoDocument doc) {
        ParqueaderoModel model = new ParqueaderoModel();
        model.setId(doc.getId());
        model.setNumero(doc.getNumero());
        model.setMedidas(doc.getMedidas());
        model.setTelefono(doc.getTelefono());

        if (doc.getNombreEstado() != null) {
            EstadoModel estado = new EstadoModel();
            estado.setNombreEstado(doc.getNombreEstado());
            model.setEstado(estado);
        }

        return model;
    }

    private ParqueaderoDocument toDocument(ParqueaderoModel model) {
        ParqueaderoDocument doc = new ParqueaderoDocument();
        doc.setId(model.getId());
        doc.setNumero(model.getNumero());
        doc.setMedidas(model.getMedidas());
        doc.setTelefono(model.getTelefono());

        if (model.getEstado() != null)
            doc.setNombreEstado(model.getEstado().getNombreEstado());

        return doc;
    }

    @Override
    public Optional<ParqueaderoModel> findByNumero(String numero) {
        return mongoRepository.findByNumero(numero).map(this::toModel);
    }

    @Override
    public boolean existsByNumero(String numero) {
        return mongoRepository.existsByNumero(numero);
    }

    @Override
    public void deleteByNumero(String numero) {
        mongoRepository.deleteByNumero(numero);
    }

    @Override
    public List<ParqueaderoModel> findByNombreEstado(String nombreEstado) {
        return mongoRepository.findByNombreEstado(nombreEstado)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ParqueaderoModel> findByMedidas(String medidas) {
        return mongoRepository.findByMedidas(medidas)
                .stream().map(this::toModel).toList();
    }

    @Override
    public ParqueaderoModel save(ParqueaderoModel parqueadero) {
        return toModel(mongoRepository.save(toDocument(parqueadero)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<ParqueaderoModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<ParqueaderoModel> findAll() {
        return mongoRepository.findAll().stream().map(this::toModel).toList();
    }
}