package com.martina.caf_fapi.auth.service;

import com.martina.caf_fapi.auth.dto.RecuperoPasswordRequest;
import com.martina.caf_fapi.auth.dto.ResetPasswordRequest;
import com.martina.caf_fapi.auth.entity.TokenResetPassword;
import com.martina.caf_fapi.auth.repository.TokenResetPasswordRepository;
import com.martina.caf_fapi.exception.InvalidDataException;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    private static final int DURATA_MINUTI = 30;
    private static final String EMAIL = "mario.rossi@email.it";
    private static final String PASSWORD_NUOVA = "Password1!";

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private TokenResetPasswordRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl service;

    @Captor
    private ArgumentCaptor<TokenResetPassword> tokenSalvato;

    @Captor
    private ArgumentCaptor<String> tokenInviato;

    @BeforeEach
    void impostaDurata() {
        ReflectionTestUtils.setField(service, "durataMinuti", DURATA_MINUTI);
    }

    private Utente utenteAttivo() {
        return Utente.builder()
                .id(7L)
                .nome("Mario")
                .cognome("Rossi")
                .email(EMAIL)
                .password("vecchia-password-codificata")
                .ruolo(Ruolo.CLIENTE)
                .attivo(true)
                .accountBloccato(false)
                .build();
    }

    private RecuperoPasswordRequest richiesta(String email) {
        return RecuperoPasswordRequest.builder().email(email).build();
    }

    /** Ricalcola l'impronta come fa il servizio, per confrontarla. */
    private String sha256(String valore) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(valore.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Nested
    @DisplayName("richiediRecupero")
    class RichiediRecupero {

        @Test
        @DisplayName("email sconosciuta: nessuna mail, nessun errore")
        void emailSconosciuta() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.empty());

            assertThatCode(() -> service.richiediRecupero(richiesta(EMAIL)))
                    .doesNotThrowAnyException();

            verifyNoInteractions(emailService);
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("account disattivato: si comporta come se non esistesse")
        void accountDisattivato() {
            Utente utente = utenteAttivo();
            utente.setAttivo(false);

            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utente));

            service.richiediRecupero(richiesta(EMAIL));

            verifyNoInteractions(emailService);
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("account bloccato: si comporta come se non esistesse")
        void accountBloccato() {
            Utente utente = utenteAttivo();
            utente.setAccountBloccato(true);

            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utente));

            service.richiediRecupero(richiesta(EMAIL));

            verifyNoInteractions(emailService);
            verify(tokenRepository, never()).save(any());
        }

        @Test
        @DisplayName("normalizza l'email prima di cercare l'utente")
        void normalizzaEmail() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.empty());

            service.richiediRecupero(richiesta("  MARIO.ROSSI@Email.IT  "));

            verify(utenteRepository).findByEmailIgnoreCase(EMAIL);
        }

        @Test
        @DisplayName("nel database salva l'hash, per mail manda il token in chiaro")
        void salvaSoloImpronta() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utenteAttivo()));

            service.richiediRecupero(richiesta(EMAIL));

            verify(tokenRepository).save(tokenSalvato.capture());
            verify(emailService)
                    .inviaLinkRecuperoPassword(anyString(), tokenInviato.capture());

            String inChiaro = tokenInviato.getValue();
            TokenResetPassword salvato = tokenSalvato.getValue();

            assertThat(salvato.getTokenHash())
                    .isNotEqualTo(inChiaro)
                    .hasSize(64)
                    .isEqualTo(sha256(inChiaro));
        }

        @Test
        @DisplayName("il token in chiaro ha entropia da 32 byte ed è usabile in URL")
        void tokenRobusto() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utenteAttivo()));

            service.richiediRecupero(richiesta(EMAIL));

            verify(emailService)
                    .inviaLinkRecuperoPassword(anyString(), tokenInviato.capture());

            assertThat(tokenInviato.getValue())
                    .hasSizeGreaterThanOrEqualTo(43)
                    .matches("[A-Za-z0-9_-]+");
        }

        @Test
        @DisplayName("invalida i token precedenti dello stesso utente")
        void invalidaPrecedenti() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utenteAttivo()));

            service.richiediRecupero(richiesta(EMAIL));

            verify(tokenRepository).eliminaTokenNonUsati(7L);
        }

        @Test
        @DisplayName("la scadenza rispetta i minuti configurati")
        void scadenzaConfigurata() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utenteAttivo()));

            LocalDateTime prima = LocalDateTime.now();

            service.richiediRecupero(richiesta(EMAIL));

            verify(tokenRepository).save(tokenSalvato.capture());

            assertThat(tokenSalvato.getValue().getScadenza())
                    .isAfter(prima.plusMinutes(DURATA_MINUTI).minusSeconds(30))
                    .isBefore(prima.plusMinutes(DURATA_MINUTI).plusSeconds(30));
        }

        @Test
        @DisplayName("un guasto SMTP non diventa un errore per il chiamante")
        void guastoSmtpNonPropaga() {
            when(utenteRepository.findByEmailIgnoreCase(EMAIL))
                    .thenReturn(Optional.of(utenteAttivo()));

            doThrow(new MailSendException("SMTP irraggiungibile"))
                    .when(emailService)
                    .inviaLinkRecuperoPassword(anyString(), anyString());

            assertThatCode(() -> service.richiediRecupero(richiesta(EMAIL)))
                    .doesNotThrowAnyException();

            verify(tokenRepository).save(any());
        }
    }

    @Nested
    @DisplayName("reimpostaPassword")
    class ReimpostaPassword {

        private ResetPasswordRequest richiestaReset(String token) {
            return ResetPasswordRequest.builder()
                    .token(token)
                    .nuovaPassword(PASSWORD_NUOVA)
                    .build();
        }

        private TokenResetPassword tokenConScadenza(LocalDateTime scadenza) {
            return TokenResetPassword.builder()
                    .id(1L)
                    .tokenHash(sha256("token-in-chiaro"))
                    .utente(utenteAttivo())
                    .scadenza(scadenza)
                    .build();
        }

        @Test
        @DisplayName("token sconosciuto: errore generico, nessuna modifica")
        void tokenSconosciuto() {
            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.reimpostaPassword(richiestaReset("inventato")))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("non è valido");

            verify(utenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("token scaduto: errore, password invariata")
        void tokenScaduto() {
            TokenResetPassword token =
                    tokenConScadenza(LocalDateTime.now().minusMinutes(1));

            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(token));

            assertThatThrownBy(() ->
                    service.reimpostaPassword(richiestaReset("token-in-chiaro")))
                    .isInstanceOf(InvalidDataException.class);

            verify(utenteRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("token già usato: errore, password invariata")
        void tokenGiaUsato() {
            TokenResetPassword token =
                    tokenConScadenza(LocalDateTime.now().plusMinutes(10));
            token.setUsatoIl(LocalDateTime.now().minusMinutes(2));

            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(token));

            assertThatThrownBy(() ->
                    service.reimpostaPassword(richiestaReset("token-in-chiaro")))
                    .isInstanceOf(InvalidDataException.class);

            verify(utenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("i tre casi di token danno lo stesso messaggio")
        void messaggioIndistinguibile() {
            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            String messaggioSconosciuto = messaggioDi("inventato");

            TokenResetPassword scaduto =
                    tokenConScadenza(LocalDateTime.now().minusMinutes(1));

            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(scaduto));

            String messaggioScaduto = messaggioDi("token-in-chiaro");

            assertThat(messaggioScaduto).isEqualTo(messaggioSconosciuto);
        }

        private String messaggioDi(String token) {
            try {
                service.reimpostaPassword(richiestaReset(token));

                throw new AssertionError("Doveva fallire.");
            } catch (InvalidDataException ex) {
                return ex.getMessage();
            }
        }

        @Test
        @DisplayName("token valido: codifica la password e consuma il token")
        void resetRiuscito() {
            TokenResetPassword token =
                    tokenConScadenza(LocalDateTime.now().plusMinutes(10));

            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.of(token));
            when(passwordEncoder.encode(PASSWORD_NUOVA))
                    .thenReturn("nuova-password-codificata");

            service.reimpostaPassword(richiestaReset("token-in-chiaro"));

            ArgumentCaptor<Utente> utenteSalvato =
                    ArgumentCaptor.forClass(Utente.class);

            verify(utenteRepository).save(utenteSalvato.capture());

            assertThat(utenteSalvato.getValue().getPassword())
                    .isEqualTo("nuova-password-codificata")
                    .isNotEqualTo(PASSWORD_NUOVA);

            assertThat(utenteSalvato.getValue().getPasswordModificataIl())
                    .isNotNull();

            assertThat(token.getUsatoIl()).isNotNull();
            assertThat(token.isUtilizzabile(LocalDateTime.now())).isFalse();
        }

        @Test
        @DisplayName("cerca il token per hash, mai per valore in chiaro")
        void cercaPerImpronta() {
            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.reimpostaPassword(richiestaReset("token-in-chiaro")))
                    .isInstanceOf(InvalidDataException.class);

            verify(tokenRepository)
                    .findByTokenHash(sha256("token-in-chiaro"));
        }
    }

    @Test
    @DisplayName("nessuna richiesta tocca il repository dei token senza utente")
    void nessunTokenSenzaUtente() {
        when(utenteRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.empty());

        service.richiediRecupero(richiesta("sconosciuto@email.it"));

        verify(tokenRepository, never()).eliminaTokenNonUsati(anyLong());
    }
}
