package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.request.MunicipioRequest;
import com.s2.LogiTrack.dto.response.MunicipioResponse;
import com.s2.LogiTrack.exception.ReglaNegocioException;
import com.s2.LogiTrack.mapper.MunicipioMapper;
import com.s2.LogiTrack.model.Municipio;
import com.s2.LogiTrack.repository.MunicipioRepository;
import com.s2.LogiTrack.service.MunicipioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MunicipioServiceImpl implements MunicipioService {

    private final MunicipioRepository municipioRepository;
    private final MunicipioMapper municipioMapper;

    @Override
    @Transactional
    public MunicipioResponse crear(MunicipioRequest request) {
        if (municipioRepository.existsByNombreAndDepartamento(request.nombre(), request.departamento())) {
            throw new ReglaNegocioException("Ya existe un municipio con ese nombre en ese departamento.");
        }
        Municipio municipio = municipioMapper.toEntity(request);
        return municipioMapper.toResponse(municipioRepository.save(municipio));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MunicipioResponse> listar() {
        return municipioRepository.findAll().stream()
                .map(municipioMapper::toResponse)
                .collect(Collectors.toList());
    }
}