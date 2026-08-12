package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.LoginRequest;
import com.martina.caf_fapi.auth.dto.LoginResponse;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    /**
     * Dati dell'utente collegato, ricavati dal token.
     * <p>
     * Serve al frontend per ricostruire la sessione dopo un ricaricamento:
     * il ruolo arriva dal database a ogni avvio, quindi una disattivazione
     * o un cambio di ruolo hanno effetto subito.
     */
    UtenteResponse utenteCorrente(String email);
}