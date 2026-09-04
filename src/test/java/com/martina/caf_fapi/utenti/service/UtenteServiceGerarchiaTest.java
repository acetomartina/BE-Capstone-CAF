package com.martina.caf_fapi.utenti.service;

import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.mapper.UtenteMapper;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.validation.CodiceFiscaleValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UtenteServiceGerarchiaTest {

    private static final Long ID_SUPER_ADMIN = 1L;
    private static final Long ID_ADMIN = 2L;
    private static final Long ID_ALTRO_ADMIN = 3L;
    private static final Long ID_USER = 4L;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private UtenteMapper utenteMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CodiceFiscaleValidator codiceFiscaleValidator;

    @InjectMocks
    private UtenteServiceImpl service;

    @AfterEach
    void pulisciContesto() {
        SecurityContextHolder.clearContext();
    }

    private static Utente utente(Long id, Ruolo ruolo) {
        Utente utente = new Utente();

        utente.setId(id);
        utente.setNome("Nome" + id);
        utente.setCognome("Cognome" + id);
        utente.setEmail("utente" + id + "@esempio.it");
        utente.setPassword("hash");
        utente.setRuolo(ruolo);
        utente.setAttivo(true);

        return utente;
    }

    private void autenticatoCome(Long id, Ruolo ruolo) {
        UtenteDetails dettagli =
                new UtenteDetails(utente(id, ruolo));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        dettagli,
                        null,
                        dettagli.getAuthorities()
                )
        );
    }

    private void esisteUtente(Utente utente) {
        when(utenteRepository.findById(utente.getId()))
                .thenReturn(Optional.of(utente));
    }

    private static UtenteUpdateRequest richiestaConEmail(String email) {
        return UtenteUpdateRequest.builder()
                .nome("Nuovo")
                .cognome("Nome")
                .email(email)
                .build();
    }

    @Nested
    @DisplayName("Modifica anagrafica")
    class Aggiorna {

        @Test
        @DisplayName("un ADMIN non cambia l'email del SUPER_ADMIN")
        void adminNonTocchiIlSuperAdmin() {
            esisteUtente(utente(ID_SUPER_ADMIN, Ruolo.SUPER_ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.aggiornaUtente(
                            ID_SUPER_ADMIN,
                            richiestaConEmail("attaccante@esempio.it")
                    )
            ).isInstanceOf(AccessDeniedException.class);

            verify(utenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("un ADMIN non modifica un altro ADMIN")
        void adminNonTocchiUnPari() {
            esisteUtente(utente(ID_ALTRO_ADMIN, Ruolo.ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.aggiornaUtente(
                            ID_ALTRO_ADMIN,
                            richiestaConEmail("nuova@esempio.it")
                    )
            ).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("un ADMIN modifica un USER")
        void adminSuUtenteSubordinato() {
            Utente bersaglio = utente(ID_USER, Ruolo.USER);

            esisteUtente(bersaglio);
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            when(utenteRepository.save(any()))
                    .thenReturn(bersaglio);

            assertThatCode(() ->
                    service.aggiornaUtente(
                            ID_USER,
                            richiestaConEmail("utente4@esempio.it")
                    )
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("nemmeno il SUPER_ADMIN si modifica tramite questa via")
        void superAdminIntoccabileAncheDaSeStesso() {
            esisteUtente(utente(ID_SUPER_ADMIN, Ruolo.SUPER_ADMIN));
            autenticatoCome(ID_SUPER_ADMIN, Ruolo.SUPER_ADMIN);

            assertThatThrownBy(() ->
                    service.aggiornaUtente(
                            ID_SUPER_ADMIN,
                            richiestaConEmail("nuova@esempio.it")
                    )
            ).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("Cambio ruolo")
    class CambiaRuolo {

        @Test
        @DisplayName("un ADMIN non declassa il SUPER_ADMIN")
        void superAdminNonDeclassabile() {
            esisteUtente(utente(ID_SUPER_ADMIN, Ruolo.SUPER_ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.cambiaRuolo(ID_SUPER_ADMIN, Ruolo.USER)
            ).isInstanceOf(AccessDeniedException.class);

            verify(utenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("un ADMIN non declassa un altro ADMIN")
        void adminNonDeclassaUnPari() {
            esisteUtente(utente(ID_ALTRO_ADMIN, Ruolo.ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.cambiaRuolo(ID_ALTRO_ADMIN, Ruolo.USER)
            ).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("nessuno cambia il ruolo a se stesso")
        void autodeclassamentoImpedito() {
            esisteUtente(utente(ID_ADMIN, Ruolo.ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.cambiaRuolo(ID_ADMIN, Ruolo.USER)
            ).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("Attivazione e disattivazione")
    class Stato {

        @Test
        @DisplayName("un ADMIN non disattiva il SUPER_ADMIN")
        void superAdminNonDisattivabile() {
            esisteUtente(utente(ID_SUPER_ADMIN, Ruolo.SUPER_ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.disattivaUtente(ID_SUPER_ADMIN)
            ).isInstanceOf(AccessDeniedException.class);

            verify(utenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("nessuno si chiude fuori da solo")
        void autodisattivazioneImpedita() {
            esisteUtente(utente(ID_ADMIN, Ruolo.ADMIN));
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            assertThatThrownBy(() ->
                    service.disattivaUtente(ID_ADMIN)
            ).isInstanceOf(AccessDeniedException.class);

            verify(utenteRepository, never()).save(any());
        }

        @Test
        @DisplayName("un ADMIN disattiva un USER")
        void adminDisattivaSubordinato() {
            Utente bersaglio = utente(ID_USER, Ruolo.USER);

            esisteUtente(bersaglio);
            autenticatoCome(ID_ADMIN, Ruolo.ADMIN);

            when(utenteRepository.save(any()))
                    .thenReturn(bersaglio);

            assertThatCode(() ->
                    service.disattivaUtente(ID_USER)
            ).doesNotThrowAnyException();
        }
    }
}
