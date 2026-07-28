package com.martina.caf_fapi.utenti.mapper;

import com.martina.caf_fapi.common.mapper.Mapper;
import com.martina.caf_fapi.utenti.dto.UtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UtenteMapper
        implements Mapper<Utente, UtenteRequest, UtenteResponse> {

    @Override
    public Utente toEntity(UtenteRequest request) {
        if (request == null) {
            return null;
        }

        return Utente.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .codiceFiscale(request.getCodiceFiscale())
                .dataNascita(request.getDataNascita())
                .luogoNascita(request.getLuogoNascita())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .indirizzo(request.getIndirizzo())
                .comune(request.getComune())
                .provincia(request.getProvincia())
                .cap(request.getCap())
                .password(request.getPassword())
                .mansione(request.getMansione())
                .numeroMatricola(request.getNumeroMatricola())
                .urlImmagineProfilo(request.getUrlImmagineProfilo())
                .build();
    }

    @Override
    public UtenteResponse toResponse(Utente utente) {
        if (utente == null) {
            return null;
        }

        return UtenteResponse.builder()
                .id(utente.getId())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .codiceFiscale(utente.getCodiceFiscale())
                .dataNascita(utente.getDataNascita())
                .luogoNascita(utente.getLuogoNascita())
                .email(utente.getEmail())
                .telefono(utente.getTelefono())
                .indirizzo(utente.getIndirizzo())
                .comune(utente.getComune())
                .provincia(utente.getProvincia())
                .cap(utente.getCap())
                .ruolo(utente.getRuolo())
                .attivo(utente.isAttivo())
                .emailVerificata(utente.isEmailVerificata())
                .mansione(utente.getMansione())
                .numeroMatricola(utente.getNumeroMatricola())
                .urlImmagineProfilo(utente.getUrlImmagineProfilo())
                .ultimoAccesso(utente.getUltimoAccesso())
                .creatoIl(utente.getCreatoIl())
                .aggiornatoIl(utente.getAggiornatoIl())
                .build();
    }

    public void updateEntity(
            Utente utente,
            UtenteUpdateRequest request
    ) {
        if (utente == null || request == null) {
            return;
        }

        utente.setNome(request.getNome());
        utente.setCognome(request.getCognome());
        utente.setEmail(request.getEmail());
        utente.setTelefono(request.getTelefono());
        utente.setIndirizzo(request.getIndirizzo());
        utente.setComune(request.getComune());
        utente.setProvincia(request.getProvincia());
        utente.setCap(request.getCap());
        utente.setMansione(request.getMansione());
        utente.setUrlImmagineProfilo(
                request.getUrlImmagineProfilo()
        );
    }

    public List<UtenteResponse> toResponseList(
            List<Utente> utenti
    ) {
        if (utenti == null || utenti.isEmpty()) {
            return Collections.emptyList();
        }

        return utenti.stream()
                .map(this::toResponse)
                .toList();
    }
}