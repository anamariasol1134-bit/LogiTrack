package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.request.ProveedorRequest;
import com.s2.LogiTrack.dto.response.ProveedorResponse;
import com.s2.LogiTrack.exception.ReglaNegocioException;
import com.s2.LogiTrack.exception.ResourceNotFoundException;
import com.s2.LogiTrack.mapper.ProveedorMapper;
import com.s2.LogiTrack.model.Municipio;
import com.s2.LogiTrack.model.Proveedor;
import com.s2.LogiTrack.repository.MunicipioRepository;
import com.s2.LogiTrack.repository.ProveedorRepository;
import com.s2.LogiTrack.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final MunicipioRepository municipioRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        if (proveedorRepository.existsByNit(request.nit())) {
            throw new ReglaNegocioException("Ya existe un proveedor con ese NIT.");
        }
        Municipio municipio = municipioRepository.findById(request.municipioId())
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado"));

        Proveedor proveedor = proveedorMapper.toEntity(request, municipio);
        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return proveedorRepository.findAll().stream()
                .map(proveedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse buscarPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        return proveedorMapper.toResponse(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        Municipio municipio = municipioRepository.findById(request.municipioId())
                .orElseThrow(() -> new ResourceNotFoundException("Municipio no encontrado"));

        proveedor.setNombre(request.nombre());
        proveedor.setNit(request.nit());
        proveedor.setTelefono(request.telefono());
        proveedor.setCorreo(request.correo());
        proveedor.setMunicipio(municipio);

        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }
}