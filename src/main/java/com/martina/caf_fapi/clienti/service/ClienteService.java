package com.martina.caf_fapi.clienti.service;

import com.martina.caf_fapi.clienti.dto.AggiornaClienteRequest;
import com.martina.caf_fapi.clienti.dto.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.martina.caf_fapi.clienti.dto.CreaClienteRequest;

public interface ClienteService {

    ClienteResponse trovaPerId(Long id);

    ClienteResponse creaCliente(CreaClienteRequest request);

    ClienteResponse aggiornaCliente(
            Long id,
            AggiornaClienteRequest request
    );

    Page<ClienteResponse> trovaTutti(
            Boolean attivo,
            Pageable pageable
    );

    Page<ClienteResponse> cercaPerCognome(
            String cognome,
            Pageable pageable
    );

    Page<ClienteResponse> cercaPerCodiceFiscale(
            String codiceFiscale,
            Pageable pageable
    );

    Page<ClienteResponse> cerca(
            String termine,
            Boolean attivo,
            Pageable pageable
    );

    void eliminaCliente(Long id);

    ClienteResponse ripristinaCliente(Long id);

    void reinviaAttivazione(Long id);

}