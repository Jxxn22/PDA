package gescazone.demo.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "residentes")
public class ResidenteDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private Integer numeroDocumento;

    private String nombre;
    private String apellido;
    private Long celular;
    private String nombreTipoDocumento;
    private String nombreTipoResidente;

    public ResidenteDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(Integer numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Long getCelular() { return celular; }
    public void setCelular(Long celular) { this.celular = celular; }

    public String getNombreTipoDocumento() { return nombreTipoDocumento; }
    public void setNombreTipoDocumento(String nombreTipoDocumento) { this.nombreTipoDocumento = nombreTipoDocumento; }

    public String getNombreTipoResidente() { return nombreTipoResidente; }
    public void setNombreTipoResidente(String nombreTipoResidente) { this.nombreTipoResidente = nombreTipoResidente; }
}