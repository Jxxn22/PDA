package gescazone.demo.application.service;

import gescazone.demo.domain.model.RegistroVisitanteModel;
import gescazone.demo.domain.repository.RegistroVisitanteRepository;
import gescazone.demo.domain.repository.ResidenteRepository;
import gescazone.demo.domain.repository.ApartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroVisitanteService {

    @Autowired
    private RegistroVisitanteRepository registroVisitanteRepository;
    @Autowired
    private ResidenteRepository residenteRepository;
    @Autowired
    private ApartamentoRepository apartamentoRepository;

    public String crear(RegistroVisitanteModel registro, String residenteId, String apartamentoId) {
        if (registro == null)
            throw new IllegalArgumentException("El registro no puede ser nulo");
        if (residenteId == null)
            throw new IllegalArgumentException("El ID del residente es obligatorio");
        if (apartamentoId == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio");

        residenteRepository.findById(residenteId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el residente con ID: " + residenteId));
        apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el apartamento con ID: " + apartamentoId));

        if (registroVisitanteRepository.existsByIdResidenteAndIdApartamento(residenteId, apartamentoId))
            throw new IllegalArgumentException("Ya existe un registro para este residente en este apartamento");

        if (registro.getFechaHoraEntrada() == null || registro.getFechaHoraEntrada().trim().isEmpty())
            throw new IllegalArgumentException("La fecha y hora de entrada es obligatoria");

        registro.setIdResidente(residenteId);
        registro.setIdApartamento(apartamentoId);
        registro.setFechaHoraEntrada(registro.getFechaHoraEntrada().trim());

        registroVisitanteRepository.save(registro);
        return "Registro de visitante creado con éxito";
    }

    public RegistroVisitanteModel consultar(String residenteId, String apartamentoId) {
        if (residenteId == null)
            throw new IllegalArgumentException("El ID del residente es obligatorio para la consulta");
        if (apartamentoId == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio para la consulta");
        return registroVisitanteRepository.findByIdResidenteAndIdApartamento(residenteId, apartamentoId).orElse(null);
    }

    public List<RegistroVisitanteModel> consultarTodos() {
        return registroVisitanteRepository.findLatestRegistros();
    }

    public List<RegistroVisitanteModel> consultarPorIdResidente(String idResidente) {
        if (idResidente == null)
            throw new IllegalArgumentException("El ID del residente es obligatorio para la consulta");
        return registroVisitanteRepository.findByIdResidente(idResidente);
    }

    public List<RegistroVisitanteModel> consultarPorIdApartamento(String idApartamento) {
        if (idApartamento == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio para la consulta");
        return registroVisitanteRepository.findByIdApartamento(idApartamento);
    }

    public List<RegistroVisitanteModel> consultarVisitantesActivos() {
        return registroVisitanteRepository.findByFechaHoraSalidaIsNull();
    }

    public List<RegistroVisitanteModel> consultarVisitantesActivosPorApartamento(String idApartamento) {
        if (idApartamento == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio");
        return registroVisitanteRepository.findVisitantesActivosByIdApartamento(idApartamento);
    }

    public String registrarSalida(String residenteId, String apartamentoId, String fechaHoraSalida) {
        if (residenteId == null)
            throw new IllegalArgumentException("El ID del residente es obligatorio");
        if (apartamentoId == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio");
        if (fechaHoraSalida == null || fechaHoraSalida.trim().isEmpty())
            throw new IllegalArgumentException("La fecha y hora de salida es obligatoria");

        RegistroVisitanteModel registro = registroVisitanteRepository
                .findByIdResidenteAndIdApartamento(residenteId, apartamentoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el registro del visitante"));

        registro.registrarSalida(fechaHoraSalida);
        registroVisitanteRepository.save(registro);
        return "Salida registrada con éxito";
    }

    public String actualizar(RegistroVisitanteModel registro, String residenteId, String apartamentoId) {
        if (registro == null)
            throw new IllegalArgumentException("El registro no puede ser nulo");
        if (residenteId == null)
            throw new IllegalArgumentException("El ID del residente es obligatorio");
        if (apartamentoId == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio");

        if (!registroVisitanteRepository.existsByIdResidenteAndIdApartamento(residenteId, apartamentoId))
            throw new IllegalArgumentException("No existe un registro con los IDs proporcionados");

        if (registro.getFechaHoraEntrada() == null || registro.getFechaHoraEntrada().trim().isEmpty())
            throw new IllegalArgumentException("La fecha y hora de entrada es obligatoria");

        residenteRepository.findById(residenteId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el residente con ID: " + residenteId));
        apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el apartamento con ID: " + apartamentoId));

        registro.setIdResidente(residenteId);
        registro.setIdApartamento(apartamentoId);
        registro.setFechaHoraEntrada(registro.getFechaHoraEntrada().trim());

        if (registro.getFechaHoraSalida() != null)
            registro.setFechaHoraSalida(registro.getFechaHoraSalida().trim());

        registroVisitanteRepository.save(registro);
        return "Registro de visitante actualizado con éxito";
    }

    public String eliminar(String residenteId, String apartamentoId) {
        if (residenteId == null)
            throw new IllegalArgumentException("El ID del residente es obligatorio para eliminar");
        if (apartamentoId == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio para eliminar");

        RegistroVisitanteModel registro = registroVisitanteRepository
                .findByIdResidenteAndIdApartamento(residenteId, apartamentoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el registro con los IDs proporcionados"));

        registroVisitanteRepository.deleteById(registro.getId());
        return "Registro eliminado con éxito";
    }

    public long contarVisitantesActivosEnApartamento(String idApartamento) {
        if (idApartamento == null)
            throw new IllegalArgumentException("El ID del apartamento es obligatorio");
        return registroVisitanteRepository.countVisitantesActivosByIdApartamento(idApartamento);
    }

    public List<RegistroVisitanteModel> consultarPorNumeroApartamento(String numero) {
        if (numero == null || numero.trim().isEmpty())
            throw new IllegalArgumentException("El número del apartamento es obligatorio");

        var apartamento = apartamentoRepository.findByNumero(numero)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el apartamento con número: " + numero));

        return registroVisitanteRepository.findByIdApartamento(apartamento.getId());
    }
}