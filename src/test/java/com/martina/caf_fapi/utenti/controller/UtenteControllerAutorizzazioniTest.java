package com.martina.caf_fapi.utenti.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * La gestione utenti e' riservata ad ADMIN e SUPER_ADMIN.
 * <p>
 * Solo endpoint di lettura: finche' l'autorizzazione non c'e', una POST
 * di prova creerebbe davvero un utente sul database.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UtenteControllerAutorizzazioniTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("un CLIENTE non puo' elencare gli utenti")
    @WithMockUser(roles = "CLIENTE")
    void clienteNonElenca() throws Exception {
        mockMvc.perform(get("/api/utenti"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un CLIENTE non puo' leggere la scheda di un utente")
    @WithMockUser(roles = "CLIENTE")
    void clienteNonLegge() throws Exception {
        mockMvc.perform(get("/api/utenti/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un CLIENTE non puo' filtrare gli utenti per ruolo")
    @WithMockUser(roles = "CLIENTE")
    void clienteNonFiltraPerRuolo() throws Exception {
        mockMvc.perform(get("/api/utenti/ruolo/ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un dipendente (USER) non puo' elencare gli utenti")
    @WithMockUser(roles = "USER")
    void dipendenteNonElenca() throws Exception {
        mockMvc.perform(get("/api/utenti"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("chi non e' autenticato non entra")
    void anonimoNonEntra() throws Exception {
        mockMvc.perform(get("/api/utenti"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("un ADMIN elenca gli utenti")
    @WithMockUser(roles = "ADMIN")
    void adminElenca() throws Exception {
        mockMvc.perform(get("/api/utenti"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("un SUPER_ADMIN elenca gli utenti")
    @WithMockUser(roles = "SUPER_ADMIN")
    void superAdminElenca() throws Exception {
        mockMvc.perform(get("/api/utenti"))
                .andExpect(status().isOk());
    }
}
