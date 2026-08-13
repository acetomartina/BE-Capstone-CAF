package com.martina.caf_fapi.auth.service;

public interface EmailService {

    void inviaLinkRecuperoPassword(
            String destinatario,
            String token
    );

    void inviaInvitoAttivazioneAccount(
            String destinatario,
            String nome,
            String token
    );
}