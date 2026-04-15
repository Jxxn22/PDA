package gescazone.demo.application.service;

import gescazone.demo.domain.model.ReservaSalonSocialModel;
import gescazone.demo.domain.repository.ReservaSalonSocialRepository;
import gescazone.demo.domain.repository.SalonSocialRepository;
import gescazone.demo.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaSalonSocialService {

    @Autowired
    private ReservaSalonSocialRepository reservaRepository;
    @Autowired
    private SalonSocialRepository salonRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ReservaSalonSocialModel> listarReservas() {
        return reservaRepository.findAll();
    }

    public ReservaSalonSocialModel buscarPorId(String id) {
        if (id == null)
            throw new IllegalArgumentException("El ID no puede ser nulo");
        return reservaRepository.findById(id).orElse(null);
    }

    public String guardarReserva(ReservaSalonSocialModel reserva) {
        if (reserva == null)
            throw new IllegalArgumentException("La reserva no puede ser nula");
        if (reserva.getFechaYHoraReserva() == null)
            throw new IllegalArgumentException("La fecha y hora de la reserva son obligatorias");
        if (reserva.getIdSalon() == null)
            throw new IllegalArgumentException("El salón es obligatorio");
        if (reserva.getIdUsuario() == null)
            throw new IllegalArgumentException("El usuario es obligatorio");
        if (reserva.getFechaYHoraReserva().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("No se puede reservar en una fecha pasada");

        salonRepository.findById(reserva.getIdSalon())
                .orElseThrow(() -> new IllegalArgumentException("El salón especificado no existe"));
        usuarioRepository.findById(reserva.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario especificado no existe"));

        boolean esNuevaReserva = reserva.getId() == null;
        boolean debeValidarDisponibilidad = esNuevaReserva;

        if (!esNuevaReserva) {
            ReservaSalonSocialModel reservaExistente = reservaRepository.findById(reserva.getId())
                    .orElseThrow(() -> new IllegalArgumentException("La reserva a actualizar no existe"));

            boolean cambioSalon = !reservaExistente.getIdSalon().equals(reserva.getIdSalon());
            boolean cambioFecha = !reservaExistente.getFechaYHoraReserva().toLocalDate()
                    .equals(reserva.getFechaYHoraReserva().toLocalDate());

            debeValidarDisponibilidad = cambioSalon || cambioFecha;
        }

        if (debeValidarDisponibilidad) {
            if (reservaRepository.existsReservaEnMismoDia(reserva.getIdSalon(), reserva.getFechaYHoraReserva())) {
                throw new IllegalArgumentException(
                        "El salón ya está reservado para el día " +
                        reserva.getFechaYHoraReserva().toLocalDate() +
                        ". Solo se permite una reserva por salón por día.");
            }
        }

        reservaRepository.save(reserva);
        return esNuevaReserva ? "Reserva creada exitosamente" : "Reserva actualizada exitosamente";
    }

    public String guardarReserva(ReservaSalonSocialModel reserva, String idUsuario, String idSalon) {
        if (reserva == null)
            throw new IllegalArgumentException("La reserva no puede ser nula");
        if (idUsuario == null)
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        if (idSalon == null)
            throw new IllegalArgumentException("El ID del salón es obligatorio");

        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con el ID: " + idUsuario));
        salonRepository.findById(idSalon)
                .orElseThrow(() -> new IllegalArgumentException("No existe un salón con el ID: " + idSalon));

        reserva.setIdUsuario(idUsuario);
        reserva.setIdSalon(idSalon);
        return guardarReserva(reserva);
    }

    public String eliminarReserva(String id) {
        if (id == null)
            throw new IllegalArgumentException("El ID no puede ser nulo");
        reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una reserva con el ID: " + id));
        reservaRepository.deleteById(id);
        return "Reserva eliminada exitosamente";
    }

    public List<ReservaSalonSocialModel> buscarPorIdSalon(String idSalon) {
        if (idSalon == null)
            throw new IllegalArgumentException("El ID del salón no puede ser nulo");
        return reservaRepository.findByIdSalon(idSalon);
    }

    public List<ReservaSalonSocialModel> buscarPorNumeroSalon(String numero) {
        if (numero == null || numero.trim().isEmpty())
            throw new IllegalArgumentException("El número de salón no puede estar vacío");
        return reservaRepository.findBySalonNumero(numero);
    }

    public List<ReservaSalonSocialModel> buscarPorIdUsuario(String idUsuario) {
        if (idUsuario == null)
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        return reservaRepository.findByIdUsuario(idUsuario);
    }

    public List<ReservaSalonSocialModel> buscarPorIdHabitante(String idHabitante) {
        if (idHabitante == null)
            throw new IllegalArgumentException("El ID del habitante no puede ser nulo");
        return reservaRepository.findByIdUsuario(idHabitante);
    }

    public List<ReservaSalonSocialModel> buscarPorDocumentoUsuario(String documento) {
        if (documento == null || documento.trim().isEmpty())
            throw new IllegalArgumentException("El documento no puede estar vacío");
        return reservaRepository.findByUsuarioNumeroDocumento(documento);
    }

    public List<ReservaSalonSocialModel> buscarPorDocumentoHabitante(String documento) {
        return buscarPorDocumentoUsuario(documento);
    }

    public List<ReservaSalonSocialModel> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null)
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        if (inicio.isAfter(fin))
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        return reservaRepository.findByFechaYHoraReservaBetween(inicio, fin);
    }

    public List<ReservaSalonSocialModel> buscarReservasFuturasSalon(String idSalon) {
        if (idSalon == null)
            throw new IllegalArgumentException("El ID del salón no puede ser nulo");
        return reservaRepository.findReservasFuturasPorSalon(idSalon, LocalDateTime.now());
    }

    public List<ReservaSalonSocialModel> buscarReservasFuturasUsuario(String idUsuario) {
        if (idUsuario == null)
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        return reservaRepository.findReservasFuturasPorUsuario(idUsuario, LocalDateTime.now());
    }

    public boolean verificarDisponibilidad(String idSalon, LocalDateTime fechaHora) {
        if (idSalon == null)
            throw new IllegalArgumentException("El ID del salón no puede ser nulo");
        if (fechaHora == null)
            throw new IllegalArgumentException("La fecha y hora no pueden ser nulas");
        salonRepository.findById(idSalon)
                .orElseThrow(() -> new IllegalArgumentException("El salón especificado no existe"));
        return !reservaRepository.existsByIdSalonAndFechaYHoraReserva(idSalon, fechaHora);
    }
}