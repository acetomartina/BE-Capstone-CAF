package com.martina.caf_fapi.appuntamenti.service;

import com.martina.caf_fapi.appuntamenti.dto.CreaAppuntamentoRequest;
import com.martina.caf_fapi.appuntamenti.entity.Appuntamento;
import com.martina.caf_fapi.appuntamenti.enums.ModalitaAppuntamento;
import com.martina.caf_fapi.appuntamenti.enums.TipologiaAppuntamento;
import com.martina.caf_fapi.appuntamenti.mapper.AppuntamentoMapper;
import com.martina.caf_fapi.appuntamenti.repository.AppuntamentoRepository;
import com.martina.caf_fapi.exception.InvalidDataException;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppuntamentoServiceImplTest {

    private static final Long ID_CLIENTE = 10L;
    private static final Long ID_RESPONSABILE = 20L;

    private static final LocalDateTime INIZIO =
            LocalDateTime.of(2026, 9, 10, 10, 0);

    private static final LocalDateTime FINE =
            LocalDateTime.of(2026, 9, 10, 11, 0);

    @Mock
    private AppuntamentoRepository appuntamentoRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private PraticaRepository praticaRepository;

    @Mock
    private AppuntamentoMapper appuntamentoMapper;

    @InjectMocks
    private AppuntamentoServiceImpl service;

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

    private CreaAppuntamentoRequest richiesta(Long responsabileId) {
        return new CreaAppuntamentoRequest(
                ID_CLIENTE,
                null,
                responsabileId,
                "Consulenza ISEE",
                null,
                TipologiaAppuntamento.values()[0],
                ModalitaAppuntamento.values()[0],
                INIZIO,
                FINE,
                null,
                null,
                null
        );
    }

    private void clienteEResponsabileEsistono() {
        when(utenteRepository.findById(ID_CLIENTE))
                .thenReturn(
                        Optional.of(
                                utente(ID_CLIENTE, Ruolo.CLIENTE)
                        )
                );

        when(utenteRepository.findById(ID_RESPONSABILE))
                .thenReturn(
                        Optional.of(
                                utente(ID_RESPONSABILE, Ruolo.USER)
                        )
                );
    }

    private Appuntamento appuntamentoEsistente() {
        Appuntamento esistente = new Appuntamento();

        esistente.setId(999L);
        esistente.setTitolo("Dichiarazione redditi");
        esistente.setInizio(INIZIO);
        esistente.setFine(FINE);

        return esistente;
    }

    @Test
    @DisplayName("rifiuta un secondo appuntamento sullo stesso operatore")
    void sovrapposizioneRifiutata() {
        clienteEResponsabileEsistono();

        when(appuntamentoRepository.trovaSovrapposti(
                eq(ID_RESPONSABILE),
                eq(INIZIO),
                eq(FINE),
                isNull()
        )).thenReturn(
                List.of(appuntamentoEsistente())
        );

        assertThatThrownBy(() ->
                service.crea(richiesta(ID_RESPONSABILE))
        )
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Dichiarazione redditi");

        verify(appuntamentoRepository, never())
                .save(any(Appuntamento.class));
    }

    @Test
    @DisplayName("accetta quando l'agenda dell'operatore e' libera")
    void agendaLiberaAccettata() {
        clienteEResponsabileEsistono();

        when(appuntamentoRepository.trovaSovrapposti(
                eq(ID_RESPONSABILE),
                eq(INIZIO),
                eq(FINE),
                isNull()
        )).thenReturn(List.of());

        when(appuntamentoRepository.save(any(Appuntamento.class)))
                .thenAnswer(invocazione -> invocazione.getArgument(0));

        assertThatCode(() ->
                service.crea(richiesta(ID_RESPONSABILE))
        ).doesNotThrowAnyException();

        verify(appuntamentoRepository)
                .save(any(Appuntamento.class));
    }

    @Test
    @DisplayName("senza responsabile non interroga nemmeno l'agenda")
    void senzaResponsabileNessunControllo() {
        when(utenteRepository.findById(ID_CLIENTE))
                .thenReturn(
                        Optional.of(
                                utente(ID_CLIENTE, Ruolo.CLIENTE)
                        )
                );

        when(appuntamentoRepository.save(any(Appuntamento.class)))
                .thenAnswer(invocazione -> invocazione.getArgument(0));

        service.crea(richiesta(null));

        verify(appuntamentoRepository, never())
                .trovaSovrapposti(any(), any(), any(), any());
    }

    @Test
    @DisplayName("una fine non successiva all'inizio e' un errore di richiesta")
    void intervalloInvalido() {
        CreaAppuntamentoRequest capovolta =
                new CreaAppuntamentoRequest(
                        ID_CLIENTE,
                        null,
                        ID_RESPONSABILE,
                        "Consulenza",
                        null,
                        TipologiaAppuntamento.values()[0],
                        ModalitaAppuntamento.values()[0],
                        FINE,
                        INIZIO,
                        null,
                        null,
                        null
                );

        assertThatThrownBy(() -> service.crea(capovolta))
                .isInstanceOf(InvalidDataException.class);
    }
}
