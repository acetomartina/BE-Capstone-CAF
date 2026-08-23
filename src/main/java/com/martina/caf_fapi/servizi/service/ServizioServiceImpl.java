package com.martina.caf_fapi.servizi.service;

import com.martina.caf_fapi.servizi.dto.MacroAreaResponse;
import com.martina.caf_fapi.servizi.dto.ServizioResponse;
import com.martina.caf_fapi.servizi.dto.UpdateServizioRequest;
import com.martina.caf_fapi.servizi.entity.MacroArea;
import com.martina.caf_fapi.servizi.entity.Servizio;
import com.martina.caf_fapi.servizi.repository.MacroAreaRepository;
import com.martina.caf_fapi.servizi.repository.ServizioRepository;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
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
    @Transactional
    public ServizioResponse aggiornaServizio(
            Long id,
            UpdateServizioRequest request
    ) {
        Servizio servizio = servizioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Servizio non trovato"
                        )
                );

        if (request.nome() != null) {
            servizio.setNome(request.nome());
        }

        if (request.descrizioneBreve() != null) {
            servizio.setDescrizioneBreve(
                    request.descrizioneBreve()
            );
        }

        if (request.descrizione() != null) {
            servizio.setDescrizione(
                    request.descrizione()
            );
        }

        if (request.destinatari() != null) {
            servizio.setDestinatari(
                    request.destinatari()
            );
        }

        if (request.requisiti() != null) {
            servizio.setRequisiti(
                    request.requisiti()
            );
        }

        if (request.comeFunziona() != null) {
            servizio.setComeFunziona(
                    request.comeFunziona()
            );
        }

        if (request.prezzo() != null) {
            servizio.setPrezzo(
                    request.prezzo()
            );
        }

        if (request.prezzoTesto() != null) {
            servizio.setPrezzoTesto(
                    request.prezzoTesto()
            );
        }

        if (request.notaPrezzo() != null) {
            servizio.setNotaPrezzo(
                    request.notaPrezzo()
            );
        }

        if (request.durataMinuti() != null) {
            servizio.setDurataMinuti(
                    request.durataMinuti()
            );
        }

        if (request.prenotabile() != null) {
            servizio.setPrenotabile(
                    request.prenotabile()
            );
        }

        if (request.richiedibileOnline() != null) {
            servizio.setRichiedibileOnline(
                    request.richiedibileOnline()
            );
        }

        if (request.inEvidenza() != null) {
            servizio.setInEvidenza(
                    request.inEvidenza()
            );
        }

        if (request.generaPratica() != null) {
            servizio.setGeneraPratica(
                    request.generaPratica()
            );
        }

        if (request.richiedeDocumenti() != null) {
            servizio.setRichiedeDocumenti(
                    request.richiedeDocumenti()
            );
        }

        if (request.ordineVisualizzazione() != null) {
            servizio.setOrdineVisualizzazione(
                    request.ordineVisualizzazione()
            );
        }

        if (request.attivo() != null) {
            servizio.setAttivo(
                    request.attivo()
            );
        }

        if (request.validoFinoAl() != null) {
            servizio.setValidoFinoAl(
                    request.validoFinoAl()
            );
        }

        Servizio aggiornato =
                servizioRepository.save(servizio);

        return toServizioResponse(aggiornato);
    }

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
                        new ResourceNotFoundException(
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
                        new ResourceNotFoundException(
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
                servizio.getPrezzo(),
                servizio.getPrezzoTesto(),
                servizio.getNotaPrezzo(),
                servizio.getDurataMinuti(),
                servizio.isPrenotabile(),
                servizio.isRichiedibileOnline(),
                servizio.isInEvidenza(),
                servizio.isGeneraPratica(),
                servizio.isRichiedeDocumenti(),
                servizio.getOrdineVisualizzazione(),
                servizio.isAttivo(),
                servizio.getValidoFinoAl()
        );
    }

    @Override
    public ServizioResponse trovaServizioPerSlug(
            String slug
    ) {
        Servizio servizio =
                servizioRepository
                        .findBySlugAndAttivoTrue(
                                slug
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Servizio non trovato"
                                )
                        );

        return toServizioResponse(
                servizio
        );
    }
}