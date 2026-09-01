package com.martina.caf_fapi.tesseramenti.mapper;

import com.martina.caf_fapi.tesseramenti.dto.TesseramentoResponse;
import com.martina.caf_fapi.tesseramenti.entity.StatoTesseramento;
import com.martina.caf_fapi.tesseramenti.entity.Tesseramento;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TesseramentoMapper {

    private static final int
            GIORNI_AVVISO_SCADENZA = 30;

    public TesseramentoResponse toResponse(
            Tesseramento tesseramento
    ) {
        return new TesseramentoResponse(
                tesseramento.getId(),
                tesseramento
                        .getCliente()
                        .getId(),
                tesseramento
                        .getDataTesseramento(),
                tesseramento
                        .getDataScadenza(),
                tesseramento.getQuota(),
                tesseramento.getNote(),
                tesseramento.isAnnullato(),
                calcolaStato(tesseramento),
                tesseramento.getCreatoIl(),
                tesseramento.getAggiornatoIl()
        );
    }

    private StatoTesseramento calcolaStato(
            Tesseramento tesseramento
    ) {
        if (tesseramento.isAnnullato()) {
            return StatoTesseramento.ANNULLATA;
        }

        LocalDate oggi = LocalDate.now();

        if (
                oggi.isAfter(
                        tesseramento
                                .getDataScadenza()
                )
        ) {
            return StatoTesseramento.SCADUTA;
        }

        if (
                !oggi.isBefore(
                        tesseramento
                                .getDataScadenza()
                                .minusDays(
                                        GIORNI_AVVISO_SCADENZA
                                )
                )
        ) {
            return StatoTesseramento.IN_SCADENZA;
        }

        return StatoTesseramento.VALIDA;
    }
}