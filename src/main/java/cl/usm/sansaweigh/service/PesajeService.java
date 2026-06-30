package cl.usm.sansaweigh.service;

import cl.usm.sansaweigh.client.ExternalScaleClient;
import cl.usm.sansaweigh.dto.RegistroPesajeRequestDTO;
import cl.usm.sansaweigh.dto.RegistroPesajeResponseDTO;
import cl.usm.sansaweigh.dto.TransicionEstadoDTO;
import cl.usm.sansaweigh.exception.BusinessRuleException;
import cl.usm.sansaweigh.exception.IllegalWeighingStateException;
import cl.usm.sansaweigh.model.CategoriaPeso;
import cl.usm.sansaweigh.model.EstadoPesaje;
import cl.usm.sansaweigh.model.RegistroPesaje;
import cl.usm.sansaweigh.repository.RegistroPesajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PesajeService {

    private static final double SANSA_KG_RATIO = 1.337;

    private final RegistroPesajeRepository repository;
    private final ExternalScaleClient scaleClient;

    // ── Conversión ──────────────────────────────────────────────────────────────

    public double convertirKilosASansas(double kilos) {
        return kilos / SANSA_KG_RATIO;
    }

    public double convertirSansasAKilos(double sansas) {
        return sansas * SANSA_KG_RATIO;
    }

    // ── Clasificación ───────────────────────────────────────────────────────────

    public CategoriaPeso clasificarPaquete(double pesoSansas) {
        if (pesoSansas <= 10.0) return CategoriaPeso.LIVIANO;
        if (pesoSansas <= 50.0) return CategoriaPeso.MEDIANO;
        return CategoriaPeso.PESADO;
    }

    // ── Validación de estado ─────────────────────────────────────────────────────

    public void validarTransicion(EstadoPesaje estadoActual, EstadoPesaje nuevoEstado) {
        if (estadoActual == EstadoPesaje.INGRESADO && nuevoEstado == EstadoPesaje.PESADO) return;
        if (estadoActual == EstadoPesaje.PESADO &&
                (nuevoEstado == EstadoPesaje.APROBADO || nuevoEstado == EstadoPesaje.RECHAZADO)) return;
        if ((estadoActual == EstadoPesaje.APROBADO || estadoActual == EstadoPesaje.RECHAZADO)
                && nuevoEstado == EstadoPesaje.DESPACHADO) return;

        throw new IllegalWeighingStateException(
                "Transición inválida: " + estadoActual + " → " + nuevoEstado);
    }

    // ── Reglas de negocio ────────────────────────────────────────────────────────

    public void validarRestricciones(CategoriaPeso categoria, String balanzaIdStr, LocalDateTime fechaHora) {
        if (categoria != CategoriaPeso.PESADO) return;

        // Restricción horaria nocturna
        int hora = fechaHora.getHour();
        if (hora >= 20 || hora < 6) {
            throw new BusinessRuleException(
                    "No se permite procesar paquetes PESADOS en horario nocturno (20:00 - 06:00)");
        }

        // Restricción balanza prima en días impares
        try {
            int balanzaId = Integer.parseInt(balanzaIdStr);
            int dia = fechaHora.getDayOfMonth();
            if (dia % 2 != 0 && esPrimo(balanzaId)) {
                throw new BusinessRuleException(
                        "Balanza prima (" + balanzaId + ") no puede registrar paquetes PESADOS en días impares");
            }
        } catch (NumberFormatException e) {
            log.debug("ID de balanza no numérico, regla prima omitida: {}", balanzaIdStr);
        }
    }

    public boolean esPrimo(int numero) {
        if (numero <= 1) return false;
        for (int i = 2; i <= Math.sqrt(numero); i++) {
            if (numero % i == 0) return false;
        }
        return true;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────────

    public RegistroPesajeResponseDTO crear(RegistroPesajeRequestDTO dto) {
        double sansas = convertirKilosASansas(dto.getPesoKg());
        CategoriaPeso categoria = clasificarPaquete(sansas);
        LocalDateTime ahora = LocalDateTime.now();

        validarRestricciones(categoria, dto.getBalanzaId(), ahora);
        scaleClient.getScaleSpecifications(dto.getBalanzaId());

        RegistroPesaje registro = RegistroPesaje.builder()
                .balanzaId(dto.getBalanzaId())
                .paqueteId(dto.getPaqueteId())
                .pesoSansas(sansas)
                .categoria(categoria)
                .estado(EstadoPesaje.INGRESADO)
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        registro.getHistorialTransiciones().add(ahora + " → INGRESADO");

        return toDTO(repository.save(registro));
    }

    public RegistroPesajeResponseDTO actualizarEstado(String id, TransicionEstadoDTO dto) {
        RegistroPesaje registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado: " + id));

        validarTransicion(registro.getEstado(), dto.getNuevoEstado());

        LocalDateTime ahora = LocalDateTime.now();
        registro.setEstado(dto.getNuevoEstado());
        registro.setUpdatedAt(ahora);
        registro.getHistorialTransiciones().add(ahora + " → " + dto.getNuevoEstado());

        return toDTO(repository.save(registro));
    }

    public List<RegistroPesajeResponseDTO> obtenerPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByCreatedAtBetween(desde, hasta)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<RegistroPesajeResponseDTO> obtenerTodos() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RegistroPesajeResponseDTO obtenerPorId(String id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado: " + id));
    }

    // ── Mapper ───────────────────────────────────────────────────────────────────

    private RegistroPesajeResponseDTO toDTO(RegistroPesaje r) {
        return RegistroPesajeResponseDTO.builder()
                .id(r.getId())
                .balanzaId(r.getBalanzaId())
                .paqueteId(r.getPaqueteId())
                .pesoSansas(r.getPesoSansas())
                .pesoKg(convertirSansasAKilos(r.getPesoSansas()))
                .categoria(r.getCategoria())
                .estado(r.getEstado())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .historialTransiciones(r.getHistorialTransiciones())
                .build();
    }
}