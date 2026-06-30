package cl.usm.sansaweigh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "registros_pesaje")
public class RegistroPesaje {

    @Id
    private String id;

    private String balanzaId;
    private String paqueteId;
    private Double pesoSansas;
    private CategoriaPeso categoria;
    private EstadoPesaje estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<String> historialTransiciones = new ArrayList<>();
}