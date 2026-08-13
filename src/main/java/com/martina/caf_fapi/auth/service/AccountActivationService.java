package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.utenti.entity.Utente;

public interface AccountActivationService {

    void inviaInvito(Utente utente);

    void attivaAccount(
            String token,
            String nuovaPassword
    );
}