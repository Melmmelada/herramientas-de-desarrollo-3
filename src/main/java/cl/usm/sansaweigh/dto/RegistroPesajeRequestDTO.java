package cl.usm.sansaweigh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RegistroPesajeRequestDTO {

    @NotBlank(message = "El ID de balanza es obligatorio")
    private String balanzaId;

    @NotBlank(message = "El ID de paquete es obligatorio")
    private String paqueteId;

    @NotNull(message = "El peso en kg es obligatorio")
    @Positive(message = "El peso debe ser positivo")
    private Double pesoKg;
}