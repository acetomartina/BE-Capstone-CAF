package com.martina.caf_fapi.ricerca.service;

import com.martina.caf_fapi.ricerca.dto.RicercaGlobaleResponse;
import com.martina.caf_fapi.ricerca.dto.RisultatoClienteRicercaResponse;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RicercaGlobaleService {

    private final UtenteRepository utenteRepository;

    @Transactional(readOnly = true)
    public RicercaGlobaleResponse cerca(String query) {

        String queryNormalizzata = query.trim();

        if (queryNormalizzata.length() < 2) {
            return rispostaVuota();
        }

        List<RisultatoClienteRicercaResponse> clienti =
                utenteRepository
                        .ricercaGlobaleClienti(
                                Ruolo.CLIENTE,
                                queryNormalizzata
                        )
                        .stream()
                        .map(this::mappaCliente)
                        .toList();

        return RicercaGlobaleResponse.builder()
                .clienti(clienti)
                .pratiche(List.of())
                .documenti(List.of())
                .build();
    }

    private RisultatoClienteRicercaResponse mappaCliente(
            Utente utente
    ) {
        return RisultatoClienteRicercaResponse.builder()
                .id(utente.getId())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .codiceFiscale(utente.getCodiceFiscale())
                .email(utente.getEmail())
                .telefono(utente.getTelefono())
                .attivo(utente.isAttivo())
                .build();
    }

    private RicercaGlobaleResponse rispostaVuota() {
        return RicercaGlobaleResponse.builder()
                .clienti(List.of())
                .pratiche(List.of())
                .documenti(List.of())
                .build();
    }
}