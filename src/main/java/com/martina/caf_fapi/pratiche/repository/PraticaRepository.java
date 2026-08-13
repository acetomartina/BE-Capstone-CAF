package com.martina.caf_fapi.pratiche.repository;

import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.pratiche.enums.StatoPratica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PraticaRepository
        extends JpaRepository<Pratica, Long> {

    Page<Pratica> findByEliminatoFalse(
            Pageable pageable
    );

    Optional<Pratica> findByIdAndEliminatoFalse(
            Long id
    );

    Page<Pratica> findByClienteIdAndEliminatoFalse(
            Long clienteId,
            Pageable pageable
    );

    Page<Pratica> findByResponsabileIdAndEliminatoFalse(
            Long responsabileId,
            Pageable pageable
    );

    boolean existsByNumeroPratica(
            String numeroPratica
    );

    @Query("""
            SELECT p
            FROM Pratica p
            WHERE p.eliminato = false
              AND (
                    :query = ''
                    OR LOWER(p.numeroPratica)
                        LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.oggetto)
                        LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.cliente.nome)
                        LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.cliente.cognome)
                        LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(
                        CONCAT(
                            CONCAT(p.cliente.nome, ' '),
                            p.cliente.cognome
                        )
                    )
                        LIKE LOWER(CONCAT('%', :query, '%'))
              )
              AND (
                    :stato IS NULL
                    OR p.stato = :stato
              )
              AND (
                    :servizioId IS NULL
                    OR p.servizio.id = :servizioId
              )
              AND (
                    :responsabileId IS NULL
                    OR p.responsabile.id = :responsabileId
              )
            """)
    Page<Pratica> cerca(
            @Param("query")
            String query,

            @Param("stato")
            StatoPratica stato,

            @Param("servizioId")
            Long servizioId,

            @Param("responsabileId")
            Long responsabileId,

            Pageable pageable
    );
}