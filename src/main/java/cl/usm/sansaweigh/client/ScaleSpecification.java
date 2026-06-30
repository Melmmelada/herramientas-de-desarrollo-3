package cl.usm.sansaweigh.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

//es el modelo del JSON que devuelve la API externa (el que está en el PDF)
public class ScaleSpecification implements Serializable {
    private String id;
    private String name;
    private String brand;
    private Double maxCapacity;
    private Double precision;
    private Double lastCalibrationOffset;
}