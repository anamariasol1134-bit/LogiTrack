package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.request.MunicipioRequest;
import com.s2.LogiTrack.dto.response.MunicipioResponse;
import java.util.List;

public interface MunicipioService {
    MunicipioResponse crear(MunicipioRequest request);
    List<MunicipioResponse> listar();
}