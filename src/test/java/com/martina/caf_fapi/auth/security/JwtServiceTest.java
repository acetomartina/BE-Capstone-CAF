package com.martina.caf_fapi.auth.security;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SEGRETO =
            Base64.getEncoder().encodeToString(
                    "segreto-di-collaudo-lungo-almeno-32-byte!".getBytes()
            );

    private static final long DURATA_MS = 3_600_000L;

    private static final String EMAIL = "mario.rossi@email.it";

    private JwtService jwtService;

    @BeforeEach
    void inizializza() {
        jwtService = new JwtService(SEGRETO, DURATA_MS);
    }

    private UtenteDetails dettagli(LocalDateTime passwordModificataIl) {
        return new UtenteDetails(
                Utente.builder()
                        .id(1L)
                        .email(EMAIL)
                        .password("hash-password")
                        .ruolo(Ruolo.CLIENTE)
                        .attivo(true)
                        .accountBloccato(false)
                        .passwordModificataIl(passwordModificataIl)
                        .build()
        );
    }

    @Test
    @DisplayName("token appena emesso: valido")
    void tokenAppenaEmesso() {
        UtenteDetails utente = dettagli(LocalDateTime.now().minusDays(3));

        String token = jwtService.generaToken(utente);

        assertThat(jwtService.tokenValido(token, utente)).isTrue();
    }

    @Test
    @DisplayName("password cambiata dopo l'emissione: token rifiutato")
    void passwordCambiataDopoEmissione() {
        LocalDateTime prima = LocalDateTime.now().minusDays(3);

        String token = jwtService.generaToken(dettagli(prima));

        /* Stesso utente, stessa email: cambia solo il momento del
           cambio password, come dopo un reset. */
        UtenteDetails dopoIlReset = dettagli(LocalDateTime.now());

        assertThat(jwtService.tokenValido(token, dopoIlReset)).isFalse();
    }

    @Test
    @DisplayName("basta un secondo di differenza per invalidare")
    void differenzaDiUnSecondo() {
        LocalDateTime momento = LocalDateTime.now().minusHours(1);

        String token = jwtService.generaToken(dettagli(momento));

        assertThat(jwtService.tokenValido(token, dettagli(momento.plusSeconds(1))))
                .isFalse();
    }

    @Test
    @DisplayName("utente che non ha mai cambiato password: token valido")
    void passwordMaiCambiata() {
        UtenteDetails utente = dettagli(null);

        String token = jwtService.generaToken(utente);

        assertThat(jwtService.tokenValido(token, utente)).isTrue();
    }

    @Test
    @DisplayName("primo cambio password: i token precedenti decadono")
    void primoCambioPassword() {
        String token = jwtService.generaToken(dettagli(null));

        assertThat(jwtService.tokenValido(token, dettagli(LocalDateTime.now())))
                .isFalse();
    }

    @Test
    @DisplayName("token firmato ma senza la claim: rifiutato")
    void tokenSenzaClaim() {
        long adesso = System.currentTimeMillis();

        /* Come i token emessi prima di questa funzionalità: firma valida,
           scadenza valida, ma nessuna informazione sulla password. */
        String tokenVecchio = Jwts.builder()
                .subject(EMAIL)
                .issuedAt(new Date(adesso))
                .expiration(new Date(adesso + DURATA_MS))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SEGRETO)))
                .compact();

        assertThat(jwtService.tokenValido(
                tokenVecchio,
                dettagli(LocalDateTime.now().minusDays(3))
        )).isFalse();
    }

    @Test
    @DisplayName("UserDetails non nostro: rifiutato senza guardare il token")
    void userDetailsEstraneo() {
        String token = jwtService.generaToken(dettagli(null));

        UserDetails estraneo = new User(
                EMAIL,
                "hash-password",
                List.of()
        );

        assertThat(jwtService.tokenValido(token, estraneo)).isFalse();
    }

    @Test
    @DisplayName("token di un altro utente: rifiutato")
    void tokenDiAltroUtente() {
        LocalDateTime momento = LocalDateTime.now().minusDays(1);

        String token = jwtService.generaToken(dettagli(momento));

        UtenteDetails altro = new UtenteDetails(
                Utente.builder()
                        .id(2L)
                        .email("altra.persona@email.it")
                        .password("hash-password")
                        .ruolo(Ruolo.CLIENTE)
                        .attivo(true)
                        .passwordModificataIl(momento)
                        .build()
        );

        assertThat(jwtService.tokenValido(token, altro)).isFalse();
    }

    @Test
    @DisplayName("la claim finisce davvero nel token emesso")
    void claimPresenteNelToken() {
        String token = jwtService.generaToken(
                dettagli(LocalDateTime.now().minusDays(3))
        );

        String payload = new String(Base64.getUrlDecoder().decode(
                token.split("\\.")[1]
        ));

        assertThat(payload).contains("\"pwd\"");
    }
}
