package gescazone.demo.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registro_visitante")
public class RegistroVisitanteDocument {

    @Id
    private String id;
    private String idResidente;
    private String idApartamento;
    private String fechaHoraEntrada;
    private String fechaHoraSalida;

    public RegistroVisitanteDocument() {}

    public RegistroVisitanteDocument(String idResidente, String idApartamento,
                                     String fechaHoraEntrada, String fechaHoraSalida) {
        this.idResidente = idResidente;
        this.idApartamento = idApartamento;
        this.fechaHoraEntrada = fechaHoraEntrada;
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdResidente() { return idResidente; }
    public void setIdResidente(String idResidente) { this.idResidente = idResidente; }

    public String getIdApartamento() { return idApartamento; }
    public void setIdApartamento(String idApartamento) { this.idApartamento = idApartamento; }

    public String getFechaHoraEntrada() { return fechaHoraEntrada; }
    public void setFechaHoraEntrada(String fechaHoraEntrada) { this.fechaHoraEntrada = fechaHoraEntrada; }

    public String getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(String fechaHoraSalida) { this.fechaHoraSalida = fechaHoraSalida; }

    public boolean isActivo() {
        return fechaHoraSalida == null || fechaHoraSalida.trim().isEmpty();
    }
}