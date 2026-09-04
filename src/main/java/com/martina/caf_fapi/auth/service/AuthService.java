package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.LoginRequest;
import com.martina.caf_fapi.auth.dto.LoginResponse;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UtenteResponse utenteCorrente(String email);
}