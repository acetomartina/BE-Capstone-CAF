package com.martina.caf_fapi.utenti.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UtenteControllerAutorizzazioniTest {

    private static final long ID_INESISTENTE = 999_999L;

    private static final String UTENTE_VALIDO = """
            {
              "nome": "Prova",
              "cognome": "Permessi",
              "codiceFiscale": "CLLRCP80A01F205W",
              "email": "prova-permessi@example.invalid",
              "password": "Password1!",
              "ruolo": "CLIENTE"
            }
            """;

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

    @Test
    @DisplayName("un CLIENTE non puo' creare utenti")
    @WithMockUser(roles = "CLIENTE")
    void clienteNonCrea() throws Exception {
        mockMvc.perform(post("/api/utenti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UTENTE_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un dipendente (USER) non puo' creare utenti")
    @WithMockUser(roles = "USER")
    void dipendenteNonCrea() throws Exception {
        mockMvc.perform(post("/api/utenti")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UTENTE_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un CLIENTE non puo' disattivare nessuno")
    @WithMockUser(roles = "CLIENTE")
    void clienteNonDisattiva() throws Exception {
        mockMvc.perform(patch("/api/utenti/{id}/disattiva", ID_INESISTENTE))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un dipendente (USER) non puo' attivare nessuno")
    @WithMockUser(roles = "USER")
    void dipendenteNonAttiva() throws Exception {
        mockMvc.perform(patch("/api/utenti/{id}/attiva", ID_INESISTENTE))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un ADMIN non puo' cambiare i ruoli")
    @WithMockUser(roles = "ADMIN")
    void adminNonCambiaRuoli() throws Exception {
        mockMvc.perform(patch("/api/utenti/{id}/ruolo", ID_INESISTENTE)
                        .param("ruolo", "SUPER_ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un CLIENTE non puo' cambiare i ruoli")
    @WithMockUser(roles = "CLIENTE")
    void clienteNonCambiaRuoli() throws Exception {
        mockMvc.perform(patch("/api/utenti/{id}/ruolo", ID_INESISTENTE)
                        .param("ruolo", "SUPER_ADMIN"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("un SUPER_ADMIN arriva al cambio ruolo")
    @WithMockUser(roles = "SUPER_ADMIN")
    void superAdminCambiaRuoli() throws Exception {
        mockMvc.perform(patch("/api/utenti/{id}/ruolo", ID_INESISTENTE)
                        .param("ruolo", "CLIENTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("un ADMIN arriva alla disattivazione")
    @WithMockUser(roles = "ADMIN")
    void adminDisattiva() throws Exception {
        mockMvc.perform(patch("/api/utenti/{id}/disattiva", ID_INESISTENTE))
                .andExpect(status().isNotFound());
    }
}
