package com.martina.caf_fapi.tesseramenti.service;

import com.martina.caf_fapi.tesseramenti.dto.CreaTesseramentoRequest;
import com.martina.caf_fapi.tesseramenti.dto.TesseramentoResponse;

import java.util.List;
import java.util.Optional;

public interface TesseramentoService {

    TesseramentoResponse crea(
            Long clienteId,
            CreaTesseramentoRequest request
    );

    List<TesseramentoResponse> trovaStoricoCliente(
            Long clienteId
    );

    Optional<TesseramentoResponse> trovaCorrenteCliente(
            Long clienteId
    );
}