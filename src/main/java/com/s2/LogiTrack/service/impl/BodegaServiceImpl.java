package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.request.BodegaRequest;
import com.s2.LogiTrack.dto.response.BodegaResponse;
import com.s2.LogiTrack.exception.ReglaNegocioException;
import com.s2.LogiTrack.exception.ResourceNotFoundException;
import com.s2.LogiTrack.mapper.BodegaMapper;
import com.s2.LogiTrack.model.Bodega;
import com.s2.LogiTrack.model.Existencia;
import com.s2.LogiTrack.model.Municipio;
import com.s2.LogiTrack.model.Producto;
import com.s2.LogiTrack.repository.BodegaRepository;
import com.s2.LogiTrack.repository.ExistenciaRepository;
import com.s2.LogiTrack.repository.MunicipioRepository;
import com.s2.LogiTrack.repository.ProductoRepository;
import com.s2.LogiTrack.service.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final MunicipioRepository municipioRepository;
    private final ExistenciaRepository existenciaRepository;
    private final BodegaMapper bodegaMapper;

    // --- NUEVA INYECCIÓN ---
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public BodegaResponse crear(BodegaRequest request) {
        if (bodegaRepository.existsByNombre(request.nombre())) {
            throw new ReglaNegocioException("Ya existe una bodega con ese nombre.");
        }
        Municipio municipio = municipioRepository.findById(request.municipioId())
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado"));

        Bodega bodega = bodegaMapper.toEntity(request, municipio);
        Bodega guardada = bodegaRepository.save(bodega);

        // --- NUEVA LÓGICA: Crear existencia en 0 para todos los productos ---
        List<Producto> productos = productoRepository.findAll();
        for (Producto producto : productos) {
            Existencia existencia = new Existencia();
            existencia.setBodega(guardada);
            existencia.setProducto(producto);
            existencia.setCantidad(0);
            existencia.setVersion(0);
            existenciaRepository.save(existencia);
        }

        return bodegaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaResponse> listar() {
        return bodegaRepository.findAll().stream()
                .map(bodegaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BodegaResponse buscarPorId(Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));
        return bodegaMapper.toResponse(bodega);
    }

    @Override
    @Transactional
    public BodegaResponse actualizar(Long id, BodegaRequest request) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));

        Municipio municipio = municipioRepository.findById(request.municipioId())
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado"));

        bodega.setNombre(request.nombre());
        bodega.setUbicacion(request.ubicacion());
        bodega.setMunicipio(municipio);
        bodega.setCapacidad(request.capacidad());
        bodega.setEncargado(request.encargado());

        return bodegaMapper.toResponse(bodegaRepository.save(bodega));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));

        // Validación: no borrar si tiene existencias
        boolean tieneExistencias = existenciaRepository.findAll().stream()
                .anyMatch(e -> e.getBodega().getId().equals(id) && e.getCantidad() > 0);

        if (tieneExistencias) {
            throw new ReglaNegocioException("No se puede eliminar la bodega porque tiene productos en existencia.");
        }

        // Si la bodega está vacía, borramos primero sus registros en existencia
        List<Existencia> existenciasVacias = existenciaRepository.findAll().stream()
                .filter(e -> e.getBodega().getId().equals(id))
                .collect(Collectors.toList());
        existenciaRepository.deleteAll(existenciasVacias);

        bodegaRepository.delete(bodega);
    }
}