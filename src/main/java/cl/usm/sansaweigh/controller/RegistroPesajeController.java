package cl.usm.sansaweigh.controller;

import cl.usm.sansaweigh.dto.RegistroPesajeRequestDTO;
import cl.usm.sansaweigh.dto.RegistroPesajeResponseDTO;
import cl.usm.sansaweigh.dto.TransicionEstadoDTO;
import cl.usm.sansaweigh.service.PesajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/registros")
@RequiredArgsConstructor
@Tag(name = "Registros de Pesaje", description = "Gestión de pesaje SansaWeigh")
public class RegistroPesajeController {

    private final PesajeService pesajeService;

    @PostMapping
    @Operation(summary = "Crear un nuevo registro de pesaje")
    public ResponseEntity<RegistroPesajeResponseDTO> crear(
            @Valid @RequestBody RegistroPesajeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pesajeService.crear(dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del registro")
    public ResponseEntity<RegistroPesajeResponseDTO> actualizarEstado(
            @PathVariable String id,
            @Valid @RequestBody TransicionEstadoDTO dto) {
        return ResponseEntity.ok(pesajeService.actualizarEstado(id, dto));
    }

    @GetMapping
    @Operation(summary = "Obtener registros con filtro opcional por fecha")
    public ResponseEntity<List<RegistroPesajeResponseDTO>> obtener(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        if (desde != null && hasta != null) {
            return ResponseEntity.ok(pesajeService.obtenerPorFecha(desde, hasta));
        }
        return ResponseEntity.ok(pesajeService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro por su ID")
    public ResponseEntity<RegistroPesajeResponseDTO> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(pesajeService.obtenerPorId(id));
    }
}