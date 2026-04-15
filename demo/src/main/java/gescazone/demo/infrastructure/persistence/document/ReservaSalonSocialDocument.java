package gescazone.demo.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "reservas")
public class ReservaSalonSocialDocument {

    @Id
    private String id;
    private String idUsuario;
    private String idSalon;
    private LocalDateTime fechaYHoraReserva;

    public ReservaSalonSocialDocument() {}

    public ReservaSalonSocialDocument(String idUsuario, String idSalon, LocalDateTime fechaYHoraReserva) {
        this.idUsuario = idUsuario;
        this.idSalon = idSalon;
        this.fechaYHoraReserva = fechaYHoraReserva;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdSalon() { return idSalon; }
    public void setIdSalon(String idSalon) { this.idSalon = idSalon; }

    public LocalDateTime getFechaYHoraReserva() { return fechaYHoraReserva; }
    public void setFechaYHoraReserva(LocalDateTime fechaYHoraReserva) { this.fechaYHoraReserva = fechaYHoraReserva; }
}