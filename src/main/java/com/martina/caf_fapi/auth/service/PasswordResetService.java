package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.RecuperoPasswordRequest;
import com.martina.caf_fapi.auth.dto.ResetPasswordRequest;

public interface PasswordResetService {

    void richiediRecupero(RecuperoPasswordRequest request);

    void reimpostaPassword(ResetPasswordRequest request);
}
