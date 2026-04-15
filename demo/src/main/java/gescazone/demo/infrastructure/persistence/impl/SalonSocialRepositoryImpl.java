package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.SalonSocialModel;
import gescazone.demo.domain.model.EstadoModel;
import gescazone.demo.domain.repository.SalonSocialRepository;
import gescazone.demo.infrastructure.persistence.document.SalonSocialDocument;
import gescazone.demo.infrastructure.persistence.mongo.SalonSocialMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SalonSocialRepositoryImpl implements SalonSocialRepository {

    @Autowired
    private SalonSocialMongoRepository mongoRepository;

    private SalonSocialModel toModel(SalonSocialDocument doc) {
        SalonSocialModel model = new SalonSocialModel();
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

    private SalonSocialDocument toDocument(SalonSocialModel model) {
        SalonSocialDocument doc = new SalonSocialDocument();
        doc.setId(model.getId());
        doc.setNumero(model.getNumero());
        doc.setMedidas(model.getMedidas());
        doc.setTelefono(model.getTelefono());

        if (model.getEstado() != null)
            doc.setNombreEstado(model.getEstado().getNombreEstado());

        return doc;
    }

    @Override
    public Optional<SalonSocialModel> findByNumero(String numero) {
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
    public List<SalonSocialModel> findByNombreEstado(String nombreEstado) {
        return mongoRepository.findByNombreEstado(nombreEstado)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<SalonSocialModel> findByMedidas(String medidas) {
        return mongoRepository.findByMedidas(medidas)
                .stream().map(this::toModel).toList();
    }

    @Override
    public SalonSocialModel save(SalonSocialModel salon) {
        return toModel(mongoRepository.save(toDocument(salon)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<SalonSocialModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<SalonSocialModel> findAll() {
        return mongoRepository.findAll().stream().map(this::toModel).toList();
    }
}