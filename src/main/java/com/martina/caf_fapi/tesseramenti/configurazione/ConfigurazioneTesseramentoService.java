package com.martina.caf_fapi.tesseramenti.configurazione;

import com.martina.caf_fapi.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ConfigurazioneTesseramentoService {

    private static final Long
            CONFIGURAZIONE_ID = 1L;

    private final ConfigurazioneTesseramentoRepository
            repository;

    @Transactional(readOnly = true)
    public ConfigurazioneTesseramentoResponse trova() {
        return toResponse(trovaEntita());
    }

    @Transactional
    public ConfigurazioneTesseramentoResponse aggiorna(
            AggiornaConfigurazioneTesseramentoRequest request
    ) {
        ConfigurazioneTesseramento configurazione =
                trovaEntita();

        configurazione.setQuotaAnnuale(
                request.quotaAnnuale()
        );

        return toResponse(configurazione);
    }

    @Transactional(readOnly = true)
    public BigDecimal trovaQuotaAnnualeObbligatoria() {
        BigDecimal quota =
                trovaEntita().getQuotaAnnuale();

        if (quota == null) {
            throw new IllegalStateException(
                    "La quota annuale del tesseramento non è stata ancora configurata."
            );
        }

        return quota;
    }

    private ConfigurazioneTesseramento trovaEntita() {
        return repository
                .findById(CONFIGURAZIONE_ID)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Configurazione tesseramento non trovata."
                        )
                );
    }

    private ConfigurazioneTesseramentoResponse toResponse(
            ConfigurazioneTesseramento configurazione
    ) {
        return new ConfigurazioneTesseramentoResponse(
                configurazione.getQuotaAnnuale(),
                configurazione.getAggiornatoIl()
        );
    }
}