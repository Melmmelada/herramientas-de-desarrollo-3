package cl.usm.sansaweigh.service;

import cl.usm.sansaweigh.exception.IllegalWeighingStateException;
import cl.usm.sansaweigh.model.CategoriaPeso;
import cl.usm.sansaweigh.model.EstadoPesaje;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void validarTransicion(EstadoPesaje estadoActual, EstadoPesaje nuevoEstado) {
        if (estadoActual == null && nuevoEstado == EstadoPesaje.INGRESADO) return;
        if (estadoActual == EstadoPesaje.INGRESADO && nuevoEstado == EstadoPesaje.PESADO) return;
        if (estadoActual == EstadoPesaje.PESADO && (nuevoEstado == EstadoPesaje.APROBADO || nuevoEstado == EstadoPesaje.RECHAZADO)) return;
        if ((estadoActual == EstadoPesaje.APROBADO || estadoActual == EstadoPesaje.RECHAZADO) && nuevoEstado == EstadoPesaje.DESPACHADO) return;

        throw new IllegalWeighingStateException("Transicion invalida");
    }

    public void validarRestricciones(CategoriaPeso categoria, String balanzaIdStr, LocalDateTime fechaHora) {
        if (categoria == CategoriaPeso.PESADO) {
            int hora = fechaHora.getHour();
            if (hora >= 20 || hora < 6) {
                throw new IllegalArgumentException("Horario nocturno no permitido");
            }

            int dia = fechaHora.getDayOfMonth();
            int balanzaId = Integer.parseInt(balanzaIdStr);

            if (dia % 2 != 0 && esPrimo(balanzaId)) {
                throw new IllegalArgumentException("Restriccion de balanza prima activa");
            }
        }
    }

    private boolean esPrimo(int numero) {
        if (numero <= 1) return false;
        for (int i = 2; i <= Math.sqrt(numero); i++) {
            if (numero % i == 0) return false;
        }
        return true;
    }
}