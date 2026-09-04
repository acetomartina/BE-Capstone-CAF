package com.martina.caf_fapi.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerMeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("senza token non si sa chi sono")
    void senzaToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("con un token malformato non si entra")
    void tokenMalformato() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer non-e-un-token"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("il login pubblico continua a non richiedere token")
    void loginRestaPubblico() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(risultato -> {
                    int stato = risultato.getResponse().getStatus();

                    if (stato == 401 || stato == 403) {
                        throw new AssertionError(
                                "Il login non deve richiedere autenticazione, "
                                        + "ricevuto " + stato
                        );
                    }
                });
    }
}
