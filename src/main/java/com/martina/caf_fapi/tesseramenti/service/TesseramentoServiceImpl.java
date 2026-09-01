package com.martina.caf_fapi.tesseramenti.service;

import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.tesseramenti.dto.CreaTesseramentoRequest;
import com.martina.caf_fapi.tesseramenti.dto.TesseramentoResponse;
import com.martina.caf_fapi.tesseramenti.entity.Tesseramento;
import com.martina.caf_fapi.tesseramenti.mapper.TesseramentoMapper;
import com.martina.caf_fapi.tesseramenti.repository.TesseramentoRepository;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.tesseramenti.configurazione.ConfigurazioneTesseramentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TesseramentoServiceImpl
        implements TesseramentoService {

    private final UtenteRepository utenteRepository;

    private final TesseramentoRepository
            tesseramentoRepository;

    private final TesseramentoMapper
            tesseramentoMapper;

    private final ConfigurazioneTesseramentoService
            configurazioneTesseramentoService;

    @Override
    @Transactional
    public TesseramentoResponse crea(
            Long clienteId,
            CreaTesseramentoRequest request
    ) {
        Utente cliente = trovaCliente(clienteId);

        Tesseramento tesseramento =
                new Tesseramento();

        tesseramento.setCliente(cliente);

        tesseramento.setDataTesseramento(
                request.dataTesseramento()
        );

        tesseramento.setDataScadenza(
                request.dataTesseramento()
                        .plusYears(1)
        );

        tesseramento.setQuota(
                configurazioneTesseramentoService
                        .trovaQuotaAnnualeObbligatoria()
        );

        tesseramento.setNote(
                normalizzaNote(request.note())
        );

        Tesseramento salvato =
                tesseramentoRepository.save(
                        tesseramento
                );

        return tesseramentoMapper.toResponse(
                salvato
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TesseramentoResponse>
    trovaStoricoCliente(Long clienteId) {
        trovaCliente(clienteId);

        return tesseramentoRepository
                .findByClienteIdAndEliminatoFalseOrderByDataTesseramentoDesc(
                        clienteId
                )
                .stream()
                .map(
                        tesseramentoMapper::toResponse
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TesseramentoResponse>
    trovaCorrenteCliente(Long clienteId) {
        trovaCliente(clienteId);

        return tesseramentoRepository
                .findFirstByClienteIdAndEliminatoFalseAndAnnullatoFalseAndDataScadenzaGreaterThanEqualOrderByDataScadenzaAsc(
                        clienteId,
                        LocalDate.now()
                )
                .map(
                        tesseramentoMapper::toResponse
                );
    }

    private Utente trovaCliente(Long clienteId) {
        return utenteRepository
                .findByIdAndRuoloAndEliminatoFalse(
                        clienteId,
                        Ruolo.CLIENTE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente non trovato con id: "
                                        + clienteId
                        )
                );
    }

    private String normalizzaNote(
            String note
    ) {
        if (
                note == null ||
                        note.trim().isEmpty()
        ) {
            return null;
        }

        return note.trim();
    }
}