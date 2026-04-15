package gescazone.demo.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registro_parqueadero_visitante")
public class RegistroParqueaderoVisitanteDocument {

    @Id
    private String id;
    private String idResidente;
    private String idParqueadero;
    private String idApartamento;
    private String fechaHoraEntrada;
    private String fechaHoraSalida;
    private String placa;

    public RegistroParqueaderoVisitanteDocument() {}

    public RegistroParqueaderoVisitanteDocument(String idResidente, String idParqueadero,
                                                String fechaHoraEntrada, String placa) {
        this.idResidente = idResidente;
        this.idParqueadero = idParqueadero;
        this.fechaHoraEntrada = fechaHoraEntrada;
        this.placa = placa;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdResidente() { return idResidente; }
    public void setIdResidente(String idResidente) { this.idResidente = idResidente; }

    public String getIdParqueadero() { return idParqueadero; }
    public void setIdParqueadero(String idParqueadero) { this.idParqueadero = idParqueadero; }

    public String getIdApartamento() { return idApartamento; }
    public void setIdApartamento(String idApartamento) { this.idApartamento = idApartamento; }

    public String getFechaHoraEntrada() { return fechaHoraEntrada; }
    public void setFechaHoraEntrada(String fechaHoraEntrada) { this.fechaHoraEntrada = fechaHoraEntrada; }

    public String getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(String fechaHoraSalida) { this.fechaHoraSalida = fechaHoraSalida; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public boolean isActivo() {
        return fechaHoraSalida == null || fechaHoraSalida.trim().isEmpty();
    }
}