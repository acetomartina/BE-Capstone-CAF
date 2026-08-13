package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.pratiche.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SottopraticaService {

    Page<SottopraticaResponse> trovaPerPratica(
            Long praticaId,
            Pageable pageable
    );

    SottopraticaResponse trovaPerId(Long id);

    SottopraticaResponse crea(
            Long praticaId,
            CreaSottopraticaRequest request
    );

    SottopraticaResponse aggiorna(
            Long id,
            AggiornaSottopraticaRequest request
    );

    SottopraticaResponse cambiaStato(
            Long id,
            CambiaStatoSottopraticaRequest request
    );
}