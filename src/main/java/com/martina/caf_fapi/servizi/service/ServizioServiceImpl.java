package com.martina.caf_fapi.servizi.service;

import com.martina.caf_fapi.servizi.dto.MacroAreaResponse;
import com.martina.caf_fapi.servizi.dto.ServizioResponse;
import com.martina.caf_fapi.servizi.entity.MacroArea;
import com.martina.caf_fapi.servizi.entity.Servizio;
import com.martina.caf_fapi.servizi.repository.MacroAreaRepository;
import com.martina.caf_fapi.servizi.repository.ServizioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServizioServiceImpl
        implements ServizioService {

    private final MacroAreaRepository macroAreaRepository;
    private final ServizioRepository servizioRepository;

    @Override
    public List<MacroAreaResponse> trovaMacroAreeAttive() {
        return macroAreaRepository
                .findByAttivaTrueOrderByOrdineVisualizzazioneAsc()
                .stream()
                .map(this::toMacroAreaResponse)
                .toList();
    }

    @Override
    public List<ServizioResponse> trovaServiziAttivi() {
        return servizioRepository
                .findByAttivoTrueOrderByOrdineVisualizzazioneAsc()
                .stream()
                .map(this::toServizioResponse)
                .toList();
    }

    @Override
    public List<ServizioResponse> trovaServiziPerMacroArea(
            Long macroAreaId
    ) {
        macroAreaRepository
                .findByIdAndAttivaTrue(macroAreaId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Macro-area non trovata"
                        )
                );

        return servizioRepository
                .findByMacroAreaIdAndAttivoTrueOrderByOrdineVisualizzazioneAsc(
                        macroAreaId
                )
                .stream()
                .map(this::toServizioResponse)
                .toList();
    }

    @Override
    public ServizioResponse trovaServizioPerId(
            Long id
    ) {
        Servizio servizio = servizioRepository
                .findByIdAndAttivoTrue(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Servizio non trovato"
                        )
                );

        return toServizioResponse(servizio);
    }

    private MacroAreaResponse toMacroAreaResponse(
            MacroArea macroArea
    ) {
        return new MacroAreaResponse(
                macroArea.getId(),
                macroArea.getNome(),
                macroArea.getSlug(),
                macroArea.getDescrizioneBreve(),
                macroArea.getChiaveIcona(),
                macroArea.getChiaveColore(),
                macroArea.getOrdineVisualizzazione()
        );
    }

    private ServizioResponse toServizioResponse(
            Servizio servizio
    ) {
        return new ServizioResponse(
                servizio.getId(),
                servizio.getMacroArea().getId(),
                servizio.getMacroArea().getNome(),
                servizio.getPartnerId(),
                servizio.getNome(),
                servizio.getSlug(),
                servizio.getDescrizioneBreve(),
                servizio.getDescrizione(),
                servizio.getDestinatari(),
                servizio.getRequisiti(),
                servizio.getComeFunziona(),
                servizio.getPrezzoTesto(),
                servizio.getNotaPrezzo(),
                servizio.getDurataMinuti(),
                servizio.isPrenotabile(),
                servizio.isRichiedibileOnline(),
                servizio.isInEvidenza(),
                servizio.getOrdineVisualizzazione()
        );
    }
}