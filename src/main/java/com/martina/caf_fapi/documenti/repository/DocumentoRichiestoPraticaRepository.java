package com.martina.caf_fapi.documenti.repository;

import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentoRichiestoPraticaRepository
        extends JpaRepository<
        DocumentoRichiestoPratica,
        Long
        > {

    List<DocumentoRichiestoPratica>
    findByPraticaIdOrderByIdAsc(
            Long praticaId
    );

    @EntityGraph(
            attributePaths = {
                    "pratica",
                    "pratica.cliente",
                    "pratica.servizio"
            }
    )
    @Query(
            value = """
                    SELECT documento
                    FROM DocumentoRichiestoPratica documento
                    JOIN documento.pratica pratica
                    JOIN pratica.cliente cliente
                    JOIN pratica.servizio servizio
                    WHERE pratica.eliminato = false
                      AND (
                            :termine = ''
                            OR LOWER(documento.etichetta)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(pratica.numeroPratica)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(pratica.oggetto)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(cliente.nome)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(cliente.cognome)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(
                                CONCAT(
                                    cliente.nome,
                                    ' ',
                                    cliente.cognome
                                )
                            )
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(cliente.codiceFiscale)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(servizio.nome)
                                LIKE CONCAT('%', :termine, '%')
                      )
                      AND (
                            :stato IS NULL
                            OR documento.stato = :stato
                      )
                      AND (
                            :tipoObbligatorieta IS NULL
                            OR documento.tipoObbligatorieta =
                                :tipoObbligatorieta
                      )
                    """,
            countQuery = """
                    SELECT COUNT(documento)
                    FROM DocumentoRichiestoPratica documento
                    JOIN documento.pratica pratica
                    JOIN pratica.cliente cliente
                    JOIN pratica.servizio servizio
                    WHERE pratica.eliminato = false
                      AND (
                            :termine = ''
                            OR LOWER(documento.etichetta)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(pratica.numeroPratica)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(pratica.oggetto)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(cliente.nome)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(cliente.cognome)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(
                                CONCAT(
                                    cliente.nome,
                                    ' ',
                                    cliente.cognome
                                )
                            )
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(cliente.codiceFiscale)
                                LIKE CONCAT('%', :termine, '%')
                            OR LOWER(servizio.nome)
                                LIKE CONCAT('%', :termine, '%')
                      )
                      AND (
                            :stato IS NULL
                            OR documento.stato = :stato
                      )
                      AND (
                            :tipoObbligatorieta IS NULL
                            OR documento.tipoObbligatorieta =
                                :tipoObbligatorieta
                      )
                    """
    )
    Page<DocumentoRichiestoPratica>
    cercaPerAmministrazione(
            @Param("termine")
            String termine,

            @Param("stato")
            StatoDocumentoPratica stato,

            @Param("tipoObbligatorieta")
            TipoObbligatorietaDocumento
                    tipoObbligatorieta,

            Pageable pageable
    );

    long countByPraticaEliminatoFalse();

    long countByStatoAndPraticaEliminatoFalse(
            StatoDocumentoPratica stato
    );
}