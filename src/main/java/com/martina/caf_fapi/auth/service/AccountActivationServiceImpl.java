package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.entity.TokenAttivazioneAccount;
import com.martina.caf_fapi.auth.repository.TokenAttivazioneAccountRepository;
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

@Service
@RequiredArgsConstructor
public class AccountActivationServiceImpl
        implements AccountActivationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    AccountActivationServiceImpl.class
            );

    private static final int BYTE_TOKEN = 32;

    private static final SecureRandom CASUALE =
            new SecureRandom();

    private static final String TOKEN_NON_VALIDO =
            "Il link di attivazione non è valido, è scaduto oppure è già stato usato.";

    private final TokenAttivazioneAccountRepository tokenRepository;
    private final UtenteRepository utenteRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.account-activation.durata-minuti}")
    private int durataMinuti;

    @Override
    @Transactional
    public void inviaInvito(Utente utente) {

        if (utente == null) {
            throw new InvalidDataException(
                    "L'utente da attivare è obbligatorio."
            );
        }

        if (utente.isAttivo() && utente.isEmailVerificata()) {
            throw new InvalidDataException(
                    "L'account risulta già attivato."
            );
        }

        /*
         * Un nuovo invito rende inutilizzabili
         * eventuali link precedenti.
         */
        tokenRepository.eliminaTokenNonUsati(
                utente.getId()
        );

        String token = generaToken();

        tokenRepository.save(
                TokenAttivazioneAccount.builder()
                        .tokenHash(impronta(token))
                        .utente(utente)
                        .scadenza(
                                LocalDateTime.now()
                                        .plusMinutes(
                                                durataMinuti
                                        )
                        )
                        .build()
        );

        /*
         * Qui, diversamente dal recupero password,
         * vogliamo sapere se l'invio fallisce:
         * l'operatore sta creando davvero un cliente
         * e deve poter essere informato del problema.
         */
        emailService.inviaInvitoAttivazioneAccount(
                utente.getEmail(),
                utente.getNome(),
                token
        );

        LOGGER.info(
                "Invito di attivazione account inviato."
        );
    }

    @Override
    @Transactional
    public void attivaAccount(
            String tokenInChiaro,
            String nuovaPassword
    ) {

        if (
                tokenInChiaro == null
                        || tokenInChiaro.isBlank()
        ) {
            throw new InvalidDataException(
                    TOKEN_NON_VALIDO
            );
        }

        if (
                nuovaPassword == null
                        || nuovaPassword.isBlank()
        ) {
            throw new InvalidDataException(
                    "La nuova password è obbligatoria."
            );
        }

        LocalDateTime adesso =
                LocalDateTime.now();

        TokenAttivazioneAccount token =
                tokenRepository
                        .findByTokenHash(
                                impronta(tokenInChiaro)
                        )
                        .orElseThrow(() ->
                                new InvalidDataException(
                                        TOKEN_NON_VALIDO
                                )
                        );

        if (!token.isUtilizzabile(adesso)) {
            throw new InvalidDataException(
                    TOKEN_NON_VALIDO
            );
        }

        Utente utente = token.getUtente();

        if (
                utente.isAttivo()
                        && utente.isEmailVerificata()
        ) {
            throw new InvalidDataException(
                    "L'account risulta già attivato."
            );
        }

        utente.setPassword(
                passwordEncoder.encode(
                        nuovaPassword
                )
        );

        utente.setPasswordModificataIl(
                adesso
        );

        utente.setAttivo(true);
        utente.setEmailVerificata(true);
        utente.setAccountBloccato(false);
        utente.setTentativiAccessoFalliti(0);

        utenteRepository.save(utente);

        /*
         * Il token viene consumato soltanto
         * dopo l'attivazione completata.
         */
        token.setUsatoIl(adesso);

        tokenRepository.save(token);

        LOGGER.info(
                "Account cliente attivato correttamente."
        );
    }

    private String generaToken() {

        byte[] byteCasuali =
                new byte[BYTE_TOKEN];

        CASUALE.nextBytes(byteCasuali);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(byteCasuali);
    }

    private String impronta(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            return HexFormat
                    .of()
                    .formatHex(
                            digest.digest(
                                    token.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            )
                    );

        } catch (
                NoSuchAlgorithmException ex
        ) {

            throw new IllegalStateException(
                    "SHA-256 non disponibile su questa JVM.",
                    ex
            );
        }
    }
}