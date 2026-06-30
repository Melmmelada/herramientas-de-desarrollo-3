package cl.usm.sansaweigh.model;

import lombok.Data;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "registros_pesaje")
public class RegistroPesaje {

    @Id
    private String id;
    private String balanzaId;
    private String paqueteId;
    private Double pesoSansas;
    private String categoriaPeso;
    private String estadoActual;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
