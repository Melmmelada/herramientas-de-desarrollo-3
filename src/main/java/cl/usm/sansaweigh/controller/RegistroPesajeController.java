package cl.usm.sansaweigh.controller;

import cl.usm.sansaweigh.model.EstadoPesaje;
import cl.usm.sansaweigh.model.CategoriaPeso;
import cl.usm.sansaweigh.model.RegistroPesaje;
import cl.usm.sansaweigh.repository.RegistroPesajeRepository;
import cl.usm.sansaweigh.service.PesajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/pesajes")
@RequiredArgsConstructor
public class RegistroPesajeController {

    private final RegistroPesajeRepository repository;
    private final PesajeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroPesaje crear(@RequestBody RegistroPesaje registro) {
        registro.setCategoriaPeso(service.clasificarPaquete(registro.getPesoSansas()).name());
        service.validarRestricciones(
                CategoriaPeso.valueOf(registro.getCategoriaPeso()),
                registro.getBalanzaId(),
                LocalDateTime.now()
        );
        registro.setEstadoActual(EstadoPesaje.INGRESADO.name());
        return repository.save(registro);
    }

    @PutMapping("/{id}/estado")
    public RegistroPesaje actualizarEstado(@PathVariable String id, @RequestParam EstadoPesaje nuevoEstado) {
        RegistroPesaje registro = repository.findById(id).orElseThrow(() -> new RuntimeException("Error 404"));
        service.validarTransicion(EstadoPesaje.valueOf(registro.getEstadoActual()), nuevoEstado);
        registro.setEstadoActual(nuevoEstado.name());
        return repository.save(registro);
    }

    @GetMapping
    public List<RegistroPesaje> obtenerPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return repository.findByCreatedAtBetween(fecha.atStartOfDay(), fecha.atTime(LocalTime.MAX));
    }
}