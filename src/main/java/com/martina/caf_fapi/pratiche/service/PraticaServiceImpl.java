package com.martina.caf_fapi.pratiche.service;

import com.martina.caf_fapi.pratiche.dto.CreaPraticaRequest;
import com.martina.caf_fapi.pratiche.dto.PraticaResponse;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.enums.PrioritaPratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import com.martina.caf_fapi.pratiche.mapper.PraticaMapper;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PraticaServiceImpl implements PraticaService {

    private final PraticaRepository praticaRepository;
    private final UtenteRepository utenteRepository;
    private final PraticaMapper praticaMapper;
    private final EntityManager entityManager;

    @Override
    public Page<PraticaResponse> trovaTutte(Pageable pageable) {
        return praticaRepository
                .findByEliminatoFalse(pageable)
                .map(praticaMapper::toResponse);
    }

    @Override
    public PraticaResponse trovaPerId(Long id) {
        Pratica pratica = praticaRepository
                .findByIdAndEliminatoFalse(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Pratica non trovata"
                        )
                );

        return praticaMapper.toResponse(pratica);
    }

    @Override
    @Transactional
    public PraticaResponse creaPratica(
            CreaPraticaRequest request
    ) {
        Utente cliente = trovaCliente(request.clienteId());

        Utente responsabile = null;

        if (request.responsabileId() != null) {
            responsabile =
                    trovaResponsabile(request.responsabileId());
        }

        verificaEsistenzaServizio(request.servizioId());

        Pratica pratica = Pratica.builder()
                .numeroPratica(generaNumeroPratica())
                .cliente(cliente)
                .servizioId(request.servizioId())
                .responsabile(responsabile)
                .oggetto(request.oggetto().trim())
                .descrizione(normalizza(request.descrizione()))
                .stato(StatoPratica.DA_AVVIARE)
                .priorita(
                        request.priorita() != null
                                ? request.priorita()
                                : PrioritaPratica.NORMALE
                )
                .dataScadenza(request.dataScadenza())
                .note(normalizza(request.note()))
                .build();

        Pratica salvata = praticaRepository.save(pratica);

        return praticaMapper.toResponse(salvata);
    }

    private Utente trovaCliente(Long clienteId) {
        Utente cliente = utenteRepository
                .findByIdAndRuoloAndEliminatoFalse(
                        clienteId,
                        Ruolo.CLIENTE
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cliente non trovato"
                        )
                );

        if (!cliente.isAttivo()) {
            throw new IllegalArgumentException(
                    "Il cliente selezionato non è attivo"
            );
        }

        return cliente;
    }

    private Utente trovaResponsabile(Long responsabileId) {
        Utente utente = utenteRepository
                .findById(responsabileId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Responsabile non trovato"
                        )
                );

        if (Boolean.TRUE.equals(utente.getEliminato())) {
            throw new IllegalArgumentException(
                    "Il responsabile selezionato è stato eliminato"
            );
        }

        if (!utente.isAttivo()) {
            throw new IllegalArgumentException(
                    "Il responsabile selezionato non è attivo"
            );
        }

        if (!ruoloOperativo(utente.getRuolo())) {
            throw new IllegalArgumentException(
                    "L'utente selezionato non può essere responsabile di una pratica"
            );
        }

        return utente;
    }

    private boolean ruoloOperativo(Ruolo ruolo) {
        return ruolo == Ruolo.SUPER_ADMIN
                || ruolo == Ruolo.ADMIN
                || ruolo == Ruolo.USER;
    }

    /*
     * Per ora il modulo Java "servizi" non esiste ancora.
     * Verifichiamo quindi la FK senza introdurre query SQL manuali:
     * EntityManager controlla l'esistenza della riga nella tabella servizi.
     *
     * Appena creeremo Servizio.java + ServizioRepository,
     * questa parte verrà sostituita dal repository dedicato.
     */
    private void verificaEsistenzaServizio(Long servizioId) {
        Number conteggio = (Number) entityManager
                .createNativeQuery(
                        """
                        SELECT COUNT(*)
                        FROM servizi
                        WHERE id = :servizioId
                          AND attivo = true
                        """
                )
                .setParameter("servizioId", servizioId)
                .getSingleResult();

        if (conteggio.longValue() == 0) {
            throw new EntityNotFoundException(
                    "Servizio non trovato o non attivo"
            );
        }
    }

    /*
     * Formato iniziale:
     * CAF-2026-000001
     *
     * La generazione definitiva diventerà più robusta quando
     * introduciamo una sequence/codice dedicato.
     */
    private String generaNumeroPratica() {
        int anno = Year.now().getValue();

        long progressivo =
                praticaRepository.count() + 1;

        String numero;

        do {
            numero = "CAF-%d-%06d"
                    .formatted(anno, progressivo);

            progressivo++;
        } while (
                praticaRepository.existsByNumeroPratica(numero)
        );

        return numero;
    }

    private String normalizza(String valore) {
        if (valore == null) {
            return null;
        }

        String pulito = valore.trim();

        return pulito.isEmpty() ? null : pulito;
    }
}