package cl.usm.sansaweigh.dto;

import cl.usm.sansaweigh.model.EstadoPesaje;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransicionEstadoDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPesaje nuevoEstado;
}