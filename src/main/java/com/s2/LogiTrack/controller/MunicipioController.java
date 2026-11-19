package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.dto.request.MunicipioRequest;
import com.s2.LogiTrack.dto.response.MunicipioResponse;
import com.s2.LogiTrack.service.MunicipioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/municipios")
@RequiredArgsConstructor
@Tag(name = "Municipios", description = "Endpoints para la consulta y creación de municipios")
public class MunicipioController {

    private final MunicipioService municipioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Listar municipios", description = "Obtiene todos los municipios registrados en el sistema.")
    public ResponseEntity<List<MunicipioResponse>> listar() {
        return ResponseEntity.ok(municipioService.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un municipio", description = "Registra un nuevo municipio con su departamento. Solo ADMIN.")
    public ResponseEntity<MunicipioResponse> crear(@Valid @RequestBody MunicipioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(municipioService.crear(request));
    }
}