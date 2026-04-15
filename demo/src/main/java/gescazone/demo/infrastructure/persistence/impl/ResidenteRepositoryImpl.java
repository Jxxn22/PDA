package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.ResidenteModel;
import gescazone.demo.domain.model.TipoDocumentoModel;
import gescazone.demo.domain.model.TipoResidenteModel;
import gescazone.demo.domain.repository.ResidenteRepository;
import gescazone.demo.infrastructure.persistence.document.ResidenteDocument;
import gescazone.demo.infrastructure.persistence.mongo.ResidenteMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResidenteRepositoryImpl implements ResidenteRepository {

    @Autowired
    private ResidenteMongoRepository mongoRepository;

    private ResidenteModel toModel(ResidenteDocument doc) {
        ResidenteModel model = new ResidenteModel();
        model.setId(doc.getId());
        model.setNumeroDocumento(doc.getNumeroDocumento());
        model.setNombre(doc.getNombre());
        model.setApellido(doc.getApellido());
        model.setCelular(doc.getCelular());

        if (doc.getNombreTipoDocumento() != null) {
            TipoDocumentoModel td = new TipoDocumentoModel();
            td.setNombreTipoDocumento(doc.getNombreTipoDocumento());
            model.setTipoDocumento(td);
        }

        if (doc.getNombreTipoResidente() != null) {
            TipoResidenteModel tr = new TipoResidenteModel();
            tr.setNombreTipoResidente(doc.getNombreTipoResidente());
            model.setTipoResidente(tr);
        }

        return model;
    }

    private ResidenteDocument toDocument(ResidenteModel model) {
        ResidenteDocument doc = new ResidenteDocument();
        doc.setId(model.getId());
        doc.setNumeroDocumento(model.getNumeroDocumento());
        doc.setNombre(model.getNombre());
        doc.setApellido(model.getApellido());
        doc.setCelular(model.getCelular());

        if (model.getTipoDocumento() != null)
            doc.setNombreTipoDocumento(model.getTipoDocumento().getNombreTipoDocumento());

        if (model.getTipoResidente() != null)
            doc.setNombreTipoResidente(model.getTipoResidente().getNombreTipoResidente());

        return doc;
    }

    @Override
    public Optional<ResidenteModel> findByNumeroDocumento(Integer numeroDocumento) {
        return mongoRepository.findByNumeroDocumento(numeroDocumento).map(this::toModel);
    }

    @Override
    public boolean existsByNumeroDocumento(Integer numeroDocumento) {
        return mongoRepository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public void deleteByNumeroDocumento(Integer numeroDocumento) {
        mongoRepository.deleteByNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<ResidenteModel> findByNombreTipoResidente(String nombreTipoResidente) {
        return mongoRepository.findByNombreTipoResidente(nombreTipoResidente)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ResidenteModel> findByNombreTipoDocumento(String nombreTipoDocumento) {
        return mongoRepository.findByNombreTipoDocumento(nombreTipoDocumento)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ResidenteModel> findByNombreContainingIgnoreCase(String nombre) {
        return mongoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ResidenteModel> findByApellidoContainingIgnoreCase(String apellido) {
        return mongoRepository.findByApellidoContainingIgnoreCase(apellido)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<ResidenteModel> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido) {
        return mongoRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, apellido)
                .stream().map(this::toModel).toList();
    }

    @Override
    public ResidenteModel save(ResidenteModel residente) {
        return toModel(mongoRepository.save(toDocument(residente)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<ResidenteModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<ResidenteModel> findAll() {
        return mongoRepository.findAll().stream().map(this::toModel).toList();
    }
}