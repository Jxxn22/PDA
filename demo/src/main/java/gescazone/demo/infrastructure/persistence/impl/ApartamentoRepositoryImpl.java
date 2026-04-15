package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.ApartamentoModel;
import gescazone.demo.domain.model.TipoOcupacionModel;
import gescazone.demo.domain.model.EstadoCuentaModel;
import gescazone.demo.domain.repository.ApartamentoRepository;
import gescazone.demo.infrastructure.persistence.document.ApartamentoDocument;
import gescazone.demo.infrastructure.persistence.mongo.ApartamentoMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ApartamentoRepositoryImpl implements ApartamentoRepository {

    @Autowired
    private ApartamentoMongoRepository mongoRepository;

    private ApartamentoModel toModel(ApartamentoDocument doc) {
        ApartamentoModel model = new ApartamentoModel();
        model.setId(doc.getId());
        model.setNumero(doc.getNumero());
        model.setMedidas(doc.getMedidas());
        model.setTelefono(doc.getTelefono());

        if (doc.getNombreTipoOcupacion() != null) {
            TipoOcupacionModel to = new TipoOcupacionModel();
            to.setNombreTipoOcupacion(doc.getNombreTipoOcupacion());
            model.setTipoOcupacion(to);
        }

        if (doc.getNombreEstadoCuenta() != null) {
            EstadoCuentaModel ec = new EstadoCuentaModel();
            ec.setNombreEstadoCuenta(doc.getNombreEstadoCuenta());
            model.setEstadoCuenta(ec);
        }

        return model;
    }

    private ApartamentoDocument toDocument(ApartamentoModel model) {
        ApartamentoDocument doc = new ApartamentoDocument();
        doc.setId(model.getId());
        doc.setNumero(model.getNumero());
        doc.setMedidas(model.getMedidas());
        doc.setTelefono(model.getTelefono());

        if (model.getTipoOcupacion() != null)
            doc.setNombreTipoOcupacion(model.getTipoOcupacion().getNombreTipoOcupacion());

        if (model.getEstadoCuenta() != null)
            doc.setNombreEstadoCuenta(model.getEstadoCuenta().getNombreEstadoCuenta());

        return doc;
    }

    @Override
    public Optional<ApartamentoModel> findByNumero(String numero) {
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
    public List<ApartamentoModel> findByNombreTipoOcupacion(String nombreTipoOcupacion) {
        return mongoRepository.findByNombreTipoOcupacion(nombreTipoOcupacion)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ApartamentoModel> findByNombreEstadoCuenta(String nombreEstadoCuenta) {
        return mongoRepository.findByNombreEstadoCuenta(nombreEstadoCuenta)
                .stream().map(this::toModel).toList();
    }

    @Override
    public ApartamentoModel save(ApartamentoModel apartamento) {
        return toModel(mongoRepository.save(toDocument(apartamento)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<ApartamentoModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<ApartamentoModel> findAll() {
        return mongoRepository.findAll().stream().map(this::toModel).toList();
    }
}