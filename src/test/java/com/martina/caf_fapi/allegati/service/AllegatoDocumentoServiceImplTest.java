package com.martina.caf_fapi.allegati.service;

import com.martina.caf_fapi.allegati.entity.AllegatoDocumento;
import com.martina.caf_fapi.allegati.repository.AllegatoDocumentoRepository;
import com.martina.caf_fapi.allegati.storage.FileStorageService;
import com.martina.caf_fapi.auth.security.UtenteDetails;
import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.repository.DocumentoRichiestoPraticaRepository;
import com.martina.caf_fapi.exception.OperationNotAllowedException;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.repository.PraticaRepository;
import com.martina.caf_fapi.pratiche.service.StatoAutomaticoPraticaService;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Gli allegati sono documenti fiscali e di identita'.
 *
 * Il rischio concreto non e' che il codice sbagli a leggere un file, ma
 * che lo mostri alla persona sbagliata: qui si verifica che ogni via
 * d'accesso passi dal titolare della pratica.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AllegatoDocumentoServiceImplTest {

    private static final Long ID_CLIENTE_TITOLARE = 10L;
    private static final Long ID_ALTRO_CLIENTE = 11L;
    private static final Long ID_OPERATORE = 20L;

    private static final Long ID_ALLEGATO = 100L;
    private static final Long ID_DOCUMENTO = 200L;
    private static final Long ID_PRATICA = 300L;

    @Mock
    private AllegatoDocumentoRepository allegatoRepository;

    @Mock
    private DocumentoRichiestoPraticaRepository documentoPraticaRepository;

    @Mock
    private PraticaRepository praticaRepository;

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private StatoAutomaticoPraticaService statoAutomaticoPraticaService;

    @InjectMocks
    private AllegatoDocumentoServiceImpl service;

    /* ---------------------------------------------------------------- */

    private static Utente utente(Long id, Ruolo ruolo) {
        Utente utente = new Utente();

        utente.setId(id);
        utente.setNome("Nome" + id);
        utente.setCognome("Cognome" + id);
        utente.setEmail("utente" + id + "@esempio.it");
        utente.setPassword("hash-non-usato");
        utente.setRuolo(ruolo);
        utente.setAttivo(true);

        return utente;
    }

    private static UtenteDetails dettagli(Long id, Ruolo ruolo) {
        return new UtenteDetails(utente(id, ruolo));
    }

    private static Pratica praticaDi(Utente cliente) {
        Pratica pratica = new Pratica();

        pratica.setId(ID_PRATICA);
        pratica.setCliente(cliente);

        return pratica;
    }

    private static DocumentoRichiestoPratica documentoDi(
            Pratica pratica,
            StatoDocumentoPratica stato
    ) {
        DocumentoRichiestoPratica documento =
                new DocumentoRichiestoPratica();

        documento.setId(ID_DOCUMENTO);
        documento.setPratica(pratica);
        documento.setStato(stato);

        return documento;
    }

    private static AllegatoDocumento allegatoDi(
            DocumentoRichiestoPratica documento,
            Utente caricatoDa
    ) {
        AllegatoDocumento allegato = new AllegatoDocumento();

        allegato.setId(ID_ALLEGATO);
        allegato.setDocumentoPratica(documento);
        allegato.setCaricatoDa(caricatoDa);
        allegato.setNomeOriginale("isee.pdf");
        allegato.setNomeStorage("uuid-isee.pdf");
        allegato.setMimeType("application/pdf");
        allegato.setDimensione(1024L);

        return allegato;
    }

    /**
     * Scenario base: la pratica e il documento appartengono al cliente
     * titolare, e l'allegato lo ha caricato lui.
     */
    private AllegatoDocumento predisponiAllegatoDelTitolare(
            StatoDocumentoPratica statoDocumento
    ) {
        Utente titolare = utente(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE);

        Pratica pratica = praticaDi(titolare);

        DocumentoRichiestoPratica documento =
                documentoDi(pratica, statoDocumento);

        AllegatoDocumento allegato =
                allegatoDi(documento, titolare);

        when(allegatoRepository.findById(ID_ALLEGATO))
                .thenReturn(Optional.of(allegato));

        when(documentoPraticaRepository.findById(ID_DOCUMENTO))
                .thenReturn(Optional.of(documento));

        when(praticaRepository.findByIdAndEliminatoFalse(ID_PRATICA))
                .thenReturn(Optional.of(pratica));

        return allegato;
    }

    /* ---------------------------------------------------------------- */

    @Nested
    @DisplayName("Download")
    class Scarica {

        @Test
        @DisplayName("un cliente non raggiunge l'allegato di un altro")
        void clienteEstraneoRespinto() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            assertThatThrownBy(() ->
                    service.scarica(
                            ID_ALLEGATO,
                            dettagli(ID_ALTRO_CLIENTE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(ResourceNotFoundException.class);

            verify(fileStorageService, never()).carica(anyString());
        }

        @Test
        @DisplayName("il messaggio non rivela che l'allegato esiste")
        void messaggioIndistinguibile() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            when(allegatoRepository.findById(999L))
                    .thenReturn(Optional.empty());

            String suAllegatoAltrui =
                    catturaMessaggio(() ->
                            service.scarica(
                                    ID_ALLEGATO,
                                    dettagli(
                                            ID_ALTRO_CLIENTE,
                                            Ruolo.CLIENTE
                                    )
                            )
                    );

            String suAllegatoInesistente =
                    catturaMessaggio(() ->
                            service.scarica(
                                    999L,
                                    dettagli(
                                            ID_ALTRO_CLIENTE,
                                            Ruolo.CLIENTE
                                    )
                            )
                    );

            assertThat(suAllegatoAltrui)
                    .isEqualTo(suAllegatoInesistente);
        }

        @Test
        @DisplayName("il titolare scarica il proprio")
        void titolareAmmesso() {
            AllegatoDocumento allegato =
                    predisponiAllegatoDelTitolare(
                            StatoDocumentoPratica.DA_VERIFICARE
                    );

            Resource contenuto =
                    new ByteArrayResource(new byte[]{1, 2, 3});

            when(fileStorageService.carica(allegato.getNomeStorage()))
                    .thenReturn(contenuto);

            AllegatoDocumentoService.DownloadAllegato download =
                    service.scarica(
                            ID_ALLEGATO,
                            dettagli(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE)
                    );

            assertThat(download.nomeOriginale())
                    .isEqualTo("isee.pdf");
        }

        @Test
        @DisplayName("l'operatore scarica la pratica di chiunque")
        void operatoreAmmesso() {
            AllegatoDocumento allegato =
                    predisponiAllegatoDelTitolare(
                            StatoDocumentoPratica.DA_VERIFICARE
                    );

            when(fileStorageService.carica(allegato.getNomeStorage()))
                    .thenReturn(new ByteArrayResource(new byte[]{1}));

            assertThatCode(() ->
                    service.scarica(
                            ID_ALLEGATO,
                            dettagli(ID_OPERATORE, Ruolo.USER)
                    )
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Elenchi")
    class Elenchi {

        @Test
        @DisplayName("un cliente non elenca gli allegati di una pratica altrui")
        void perPraticaAltrui() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            assertThatThrownBy(() ->
                    service.trovaPerPratica(
                            ID_PRATICA,
                            dettagli(ID_ALTRO_CLIENTE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("un cliente non elenca gli allegati di un documento altrui")
        void perDocumentoAltrui() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            assertThatThrownBy(() ->
                    service.trovaPerDocumento(
                            ID_DOCUMENTO,
                            dettagli(ID_ALTRO_CLIENTE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("il titolare elenca i propri")
        void titolareAmmesso() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            when(allegatoRepository
                    .findByDocumentoPraticaIdOrderByCaricatoIlDesc(
                            ID_DOCUMENTO
                    )
            ).thenReturn(List.of());

            assertThatCode(() ->
                    service.trovaPerDocumento(
                            ID_DOCUMENTO,
                            dettagli(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE)
                    )
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Eliminazione")
    class Elimina {

        @Test
        @DisplayName("un cliente estraneo non elimina nulla")
        void clienteEstraneoRespinto() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.DA_VERIFICARE
            );

            assertThatThrownBy(() ->
                    service.elimina(
                            ID_ALLEGATO,
                            dettagli(ID_ALTRO_CLIENTE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(ResourceNotFoundException.class);

            verify(fileStorageService, never()).elimina(anyString());
        }

        @Test
        @DisplayName("il cliente non tocca cio' che ha caricato la sede")
        void allegatoDellaSedeProtetto() {
            Utente titolare =
                    utente(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE);

            Utente operatore =
                    utente(ID_OPERATORE, Ruolo.USER);

            Pratica pratica = praticaDi(titolare);

            DocumentoRichiestoPratica documento =
                    documentoDi(
                            pratica,
                            StatoDocumentoPratica.DA_VERIFICARE
                    );

            when(allegatoRepository.findById(ID_ALLEGATO))
                    .thenReturn(
                            Optional.of(
                                    allegatoDi(documento, operatore)
                            )
                    );

            assertThatThrownBy(() ->
                    service.elimina(
                            ID_ALLEGATO,
                            dettagli(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(OperationNotAllowedException.class);

            verify(fileStorageService, never()).elimina(anyString());
        }

        @Test
        @DisplayName("dopo la validazione il cliente non ritira piu' il documento")
        void documentoValidatoProtetto() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.VALIDATO
            );

            assertThatThrownBy(() ->
                    service.elimina(
                            ID_ALLEGATO,
                            dettagli(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(OperationNotAllowedException.class);

            verify(fileStorageService, never()).elimina(anyString());
        }

        @Test
        @DisplayName("il cliente ritira il proprio caricamento non ancora validato")
        void ritiroConsentito() {
            AllegatoDocumento allegato =
                    predisponiAllegatoDelTitolare(
                            StatoDocumentoPratica.DA_VERIFICARE
                    );

            when(allegatoRepository
                    .existsByDocumentoPraticaIdAndIdNot(
                            ID_DOCUMENTO,
                            ID_ALLEGATO
                    )
            ).thenReturn(false);

            service.elimina(
                    ID_ALLEGATO,
                    dettagli(ID_CLIENTE_TITOLARE, Ruolo.CLIENTE)
            );

            verify(fileStorageService)
                    .elimina(allegato.getNomeStorage());

            verify(allegatoRepository).delete(allegato);
        }

        @Test
        @DisplayName("l'operatore elimina anche un documento validato")
        void operatoreNonVincolato() {
            AllegatoDocumento allegato =
                    predisponiAllegatoDelTitolare(
                            StatoDocumentoPratica.VALIDATO
                    );

            when(allegatoRepository
                    .existsByDocumentoPraticaIdAndIdNot(
                            ID_DOCUMENTO,
                            ID_ALLEGATO
                    )
            ).thenReturn(true);

            assertThatCode(() ->
                    service.elimina(
                            ID_ALLEGATO,
                            dettagli(ID_OPERATORE, Ruolo.ADMIN)
                    )
            ).doesNotThrowAnyException();

            verify(fileStorageService)
                    .elimina(allegato.getNomeStorage());
        }
    }

    @Nested
    @DisplayName("Caricamento")
    class Carica {

        @Test
        @DisplayName("un cliente non carica su una pratica altrui")
        void suPraticaAltrui() {
            predisponiAllegatoDelTitolare(
                    StatoDocumentoPratica.MANCANTE
            );

            assertThatThrownBy(() ->
                    service.carica(
                            ID_DOCUMENTO,
                            null,
                            dettagli(ID_ALTRO_CLIENTE, Ruolo.CLIENTE)
                    )
            ).isInstanceOf(ResourceNotFoundException.class);

            /* Il controllo precede la scrittura su disco: un file di un
               estraneo non deve nemmeno essere salvato e poi rimosso. */
            verify(fileStorageService, never()).salva(null);
        }
    }

    /* ---------------------------------------------------------------- */

    private static String catturaMessaggio(Runnable azione) {
        try {
            azione.run();
        } catch (RuntimeException eccezione) {
            return eccezione.getMessage();
        }

        throw new AssertionError(
                "L'operazione doveva essere rifiutata."
        );
    }
}
