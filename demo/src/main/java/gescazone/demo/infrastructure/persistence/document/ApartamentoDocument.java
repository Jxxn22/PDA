package gescazone.demo.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "apartamentos")
public class ApartamentoDocument {

    @Id
    private String id;
    private String numero;
    private String medidas;
    private Long telefono;
    private String nombreTipoOcupacion;
    private String nombreEstadoCuenta;

    public ApartamentoDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getMedidas() { return medidas; }
    public void setMedidas(String medidas) { this.medidas = medidas; }

    public Long getTelefono() { return telefono; }
    public void setTelefono(Long telefono) { this.telefono = telefono; }

    public String getNombreTipoOcupacion() { return nombreTipoOcupacion; }
    public void setNombreTipoOcupacion(String nombreTipoOcupacion) { this.nombreTipoOcupacion = nombreTipoOcupacion; }

    public String getNombreEstadoCuenta() { return nombreEstadoCuenta; }
    public void setNombreEstadoCuenta(String nombreEstadoCuenta) { this.nombreEstadoCuenta = nombreEstadoCuenta; }
}