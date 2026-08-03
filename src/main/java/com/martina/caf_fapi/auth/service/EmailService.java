package com.martina.caf_fapi.auth.service;

public interface EmailService {

    /**
     * Invia il link per reimpostare la password.
     * <p>
     * Propaga l'eccezione se l'invio fallisce: decidere se un guasto SMTP
     * debba interrompere l'operazione spetta a chi chiama, non a questo
     * servizio.
     *
     * @param destinatario indirizzo dell'utente
     * @param token        token in chiaro, da inserire nel link
     */
    void inviaLinkRecuperoPassword(String destinatario, String token);
}
