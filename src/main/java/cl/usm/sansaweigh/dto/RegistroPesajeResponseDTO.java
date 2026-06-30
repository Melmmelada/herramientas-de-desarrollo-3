package cl.usm.sansaweigh.dto;

import cl.usm.sansaweigh.model.CategoriaPeso;
import cl.usm.sansaweigh.model.EstadoPesaje;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RegistroPesajeResponseDTO {
    private String id;
    private String balanzaId;
    private String paqueteId;
    private Double pesoSansas;
    private Double pesoKg;
    private CategoriaPeso categoria;
    private EstadoPesaje estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> historialTransiciones;
}