package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.RecuperoPasswordRequest;
import com.martina.caf_fapi.auth.dto.ResetPasswordRequest;
import com.martina.caf_fapi.auth.entity.TokenResetPassword;
import com.martina.caf_fapi.auth.repository.TokenResetPasswordRepository;
import com.martina.caf_fapi.exception.InvalidDataException;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    /** 32 byte = 256 bit di entropia: non attaccabile per tentativi. */
    private static final int BYTE_TOKEN = 32;

    private static final SecureRandom CASUALE = new SecureRandom();

    /* Un solo messaggio per token sconosciuto, scaduto o gia' usato:
       distinguerli direbbe a un attaccante quali token sono esistiti. */
    private static final String TOKEN_NON_VALIDO =
            "Il link di recupero non è valido, è scaduto oppure è già stato usato.";

    private final UtenteRepository utenteRepository;
    private final TokenResetPasswordRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.reset-password.durata-minuti}")
    private int durataMinuti;

    @Override
    @Transactional
    public void richiediRecupero(RecuperoPasswordRequest request) {

        String emailNormalizzata = request.getEmail()
                .strip()
                .toLowerCase(Locale.ROOT);

        Optional<Utente> eventualeUtente =
                utenteRepository.findByEmailIgnoreCase(emailNormalizzata);

        if (eventualeUtente.isEmpty()) {
            LOGGER.info(
                    "Recupero password richiesto per un indirizzo "
                            + "senza account: nessuna azione."
            );

            return;
        }

        Utente utente = eventualeUtente.get();

        if (!utente.isAttivo() || utente.isAccountBloccato()) {
            LOGGER.warn(
                    "Recupero password richiesto per un account "
                            + "disattivato o bloccato: nessuna azione."
            );

            return;
        }

        /* Una nuova richiesta rende inutilizzabili i link precedenti. */
        tokenRepository.eliminaTokenNonUsati(utente.getId());

        String token = generaToken();

        tokenRepository.save(
                TokenResetPassword.builder()
                        .tokenHash(impronta(token))
                        .utente(utente)
                        .scadenza(
                                LocalDateTime.now().plusMinutes(durataMinuti)
                        )
                        .build()
        );

        /* Un guasto SMTP non deve trasformarsi in un errore visibile:
           risponderemmo 200 alle mail inesistenti e 500 a quelle vere,
           cioe' esattamente l'enumerazione che stiamo evitando. */
        try {
            emailService.inviaLinkRecuperoPassword(utente.getEmail(), token);
        } catch (RuntimeException ex) {
            LOGGER.error("Invio della mail di recupero fallito.", ex);
        }
    }

    @Override
    @Transactional
    public void reimpostaPassword(ResetPasswordRequest request) {

        LocalDateTime adesso = LocalDateTime.now();

        TokenResetPassword token = tokenRepository
                .findByTokenHash(impronta(request.getToken()))
                .orElseThrow(() ->
                        new InvalidDataException(TOKEN_NON_VALIDO)
                );

        if (!token.isUtilizzabile(adesso)) {
            throw new InvalidDataException(TOKEN_NON_VALIDO);
        }

        Utente utente = token.getUtente();

        utente.setPassword(
                passwordEncoder.encode(request.getNuovaPassword())
        );
        utente.setPasswordModificataIl(adesso);
        utenteRepository.save(utente);

        /* Consumato: da qui in poi lo stesso link non funziona piu'. */
        token.setUsatoIl(adesso);
        tokenRepository.save(token);

        LOGGER.info("Password reimpostata tramite token di recupero.");
    }

    private String generaToken() {
        byte[] byteCasuali = new byte[BYTE_TOKEN];

        CASUALE.nextBytes(byteCasuali);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(byteCasuali);
    }

    /*
     * SHA-256 e non bcrypt: il token e' generato da noi con 256 bit di
     * entropia, quindi non serve un algoritmo lento contro la forza bruta.
     * Serve invece un hash deterministico, per poter cercare il token nel
     * database senza scorrere tutta la tabella.
     */
    private String impronta(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(
                    digest.digest(token.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "SHA-256 non disponibile su questa JVM.", ex
            );
        }
    }
}
