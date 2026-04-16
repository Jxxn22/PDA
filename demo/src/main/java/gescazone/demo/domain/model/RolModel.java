package gescazone.demo.domain.model;

import org.springframework.data.mongodb.core.mapping.Field;

// No lleva @Document porque se guarda embebido dentro de UsuarioModel
public class RolModel {

    @Field("nombreRol")
    private String nombreRol;

    public RolModel() {}
    public RolModel(String nombreRol) { this.nombreRol = nombreRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}