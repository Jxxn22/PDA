package gescazone.demo.infrastructure.persistence.impl;

import gescazone.demo.domain.model.UsuarioModel;
import gescazone.demo.domain.model.RolModel;
import gescazone.demo.domain.model.TipoDocumentoModel;
import gescazone.demo.domain.repository.UsuarioRepository;
import gescazone.demo.infrastructure.persistence.document.UsuarioDocument;
import gescazone.demo.infrastructure.persistence.mongo.UsuarioMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Autowired
    private UsuarioMongoRepository mongoRepository;

    private UsuarioModel toModel(UsuarioDocument doc) {
        UsuarioModel model = new UsuarioModel();
        model.setId(doc.getId());
        model.setNumeroDocumento(doc.getNumeroDocumento());
        model.setNombre(doc.getNombre());
        model.setApellido(doc.getApellido());
        model.setCorreo(doc.getCorreo());
        model.setContrasena(doc.getContrasena());

        if (doc.getNombreRol() != null) {
            RolModel rol = new RolModel();
            rol.setNombreRol(doc.getNombreRol());
            model.setRol(rol);
        }

        if (doc.getNombreTipoDocumento() != null) {
            TipoDocumentoModel td = new TipoDocumentoModel();
            td.setNombreTipoDocumento(doc.getNombreTipoDocumento());
            model.setTipoDocumento(td);
        }

        return model;
    }

    private UsuarioDocument toDocument(UsuarioModel model) {
        UsuarioDocument doc = new UsuarioDocument();
        doc.setId(model.getId());
        doc.setNumeroDocumento(model.getNumeroDocumento());
        doc.setNombre(model.getNombre());
        doc.setApellido(model.getApellido());
        doc.setCorreo(model.getCorreo());
        doc.setContrasena(model.getContrasena());

        if (model.getRol() != null)
            doc.setNombreRol(model.getRol().getNombreRol());

        if (model.getTipoDocumento() != null)
            doc.setNombreTipoDocumento(model.getTipoDocumento().getNombreTipoDocumento());

        return doc;
    }

    @Override
    public Optional<UsuarioModel> findByNumeroDocumento(String numeroDocumento) {
        return mongoRepository.findByNumeroDocumento(numeroDocumento).map(this::toModel);
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return mongoRepository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public Optional<UsuarioModel> findByNumeroDocumentoAndContrasena(String numeroDocumento, String contrasena) {
        return mongoRepository.findByNumeroDocumentoAndContrasena(numeroDocumento, contrasena).map(this::toModel);
    }

    @Override
    public void deleteByNumeroDocumento(String numeroDocumento) {
        mongoRepository.deleteByNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<UsuarioModel> findByNombreRol(String nombreRol) {
        return mongoRepository.findByNombreRol(nombreRol)
                .stream().map(this::toModel).toList();
    }

    @Override
    public List<UsuarioModel> findByNombreTipoDocumento(String nombreTipoDocumento) {
        return mongoRepository.findByNombreTipoDocumento(nombreTipoDocumento)
                .stream().map(this::toModel).toList();
    }

    @Override
    public Optional<UsuarioModel> findByCorreo(String correo) {
        return mongoRepository.findByCorreo(correo).map(this::toModel);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return mongoRepository.existsByCorreo(correo);
    }

    @Override
    public UsuarioModel save(UsuarioModel usuario) {
        return toModel(mongoRepository.save(toDocument(usuario)));
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<UsuarioModel> findById(String id) {
        return mongoRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<UsuarioModel> findAll() {
        return mongoRepository.findAll().stream().map(this::toModel).toList();
    }
}