package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.pratiche.dto.CreaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PraticaService {

    Page<PraticaResponse> trovaTutte(Pageable pageable);

    PraticaResponse trovaPerId(Long id);

    PraticaResponse creaPratica(CreaPraticaRequest request);
}