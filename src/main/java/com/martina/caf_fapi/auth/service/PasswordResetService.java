package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.RecuperoPasswordRequest;
import com.martina.caf_fapi.auth.dto.ResetPasswordRequest;

public interface PasswordResetService {

    /**
     * Genera un token e invia il link di recupero.
     * <p>
     * Non segnala in alcun modo se l'indirizzo corrisponde a un account:
     * termina senza errori sia che l'utente esista sia che non esista.
     * È la difesa contro l'enumerazione degli account.
     */
    void richiediRecupero(RecuperoPasswordRequest request);

    /**
     * Reimposta la password consumando il token.
     *
     * @throws com.martina.caf_fapi.exception.InvalidDataException
     *         se il token è sconosciuto, scaduto o già usato
     */
    void reimpostaPassword(ResetPasswordRequest request);
}
