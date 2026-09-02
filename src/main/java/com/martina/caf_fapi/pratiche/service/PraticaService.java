package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.pratiche.dto.AggiornaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.CambiaStatoPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.CreaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PraticaService {

    Page<PraticaResponse> trovaTutte(
            Pageable pageable
    );

    Page<PraticaResponse> cerca(
            String query,
            StatoPratica stato,
            Long servizioId,
            Long responsabileId,
            Pageable pageable
    );

    Page<PraticaResponse> trovaPerCliente(
            Long clienteId,
            Pageable pageable
    );

    PraticaResponse trovaPerIdDelCliente(
            Long praticaId,
            Long clienteId
    );

    PraticaResponse trovaPerId(
            Long id
    );

    PraticaResponse creaPratica(
            CreaPraticaRequest request
    );

    PraticaResponse aggiornaPratica(
            Long id,
            AggiornaPraticaRequest request
    );

    PraticaResponse cambiaStato(
            Long id,
            CambiaStatoPraticaRequest request
    );
}