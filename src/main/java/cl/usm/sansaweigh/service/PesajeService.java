package cl.usm.sansaweigh.service;

import cl.usm.sansaweigh.model.CategoriaPeso;
import org.springframework.stereotype.Service;

@Service
public class PesajeService {


    private static final double SANSA_KG_RATIO = 1.337;

    public double convertirKilosASansas(double kilos) {
        return kilos / SANSA_KG_RATIO;
    }

    public CategoriaPeso clasificarPaquete(double pesoSansas) {
        if (pesoSansas <= 10.0) {
            return CategoriaPeso.LIVIANO;
        } else if (pesoSansas <= 50.0) {
            return CategoriaPeso.MEDIANO;
        } else {
            return CategoriaPeso.PESADO;
        }
    }
}