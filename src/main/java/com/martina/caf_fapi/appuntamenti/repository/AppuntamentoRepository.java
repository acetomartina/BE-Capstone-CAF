package com.martina.caf_fapi.appuntamenti.repository;

import com.martina.caf_fapi.appuntamenti.entity.Appuntamento;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppuntamentoRepository
        extends JpaRepository<Appuntamento, Long> {

    @EntityGraph(
            attributePaths = {
                    "cliente",
                    "responsabile",
                    "pratica",
                    "servizio"
            }
    )
    @Query("""
            SELECT appuntamento
            FROM Appuntamento appuntamento
            WHERE appuntamento.eliminato = false
              AND appuntamento.inizio < :al
              AND appuntamento.fine > :dal
            ORDER BY appuntamento.inizio ASC
            """)
    List<Appuntamento> trovaNelPeriodo(
            @Param("dal")
            LocalDateTime dal,

            @Param("al")
            LocalDateTime al
    );

    @EntityGraph(
            attributePaths = {
                    "cliente",
                    "responsabile",
                    "pratica",
                    "servizio"
            }
    )
    Optional<Appuntamento>
    findByIdAndEliminatoFalse(
            Long id
    );

    @Query("""
            SELECT appuntamento
            FROM Appuntamento appuntamento
            WHERE appuntamento.eliminato = false
              AND appuntamento.responsabile.id = :responsabileId
              AND appuntamento.stato <> com.martina.caf_fapi.appuntamenti.enums.StatoAppuntamento.ANNULLATO
              AND appuntamento.inizio < :fine
              AND appuntamento.fine > :inizio
              AND (:idDaEscludere IS NULL OR appuntamento.id <> :idDaEscludere)
            ORDER BY appuntamento.inizio ASC
            """)
    List<Appuntamento> trovaSovrapposti(
            @Param("responsabileId")
            Long responsabileId,

            @Param("inizio")
            LocalDateTime inizio,

            @Param("fine")
            LocalDateTime fine,

            @Param("idDaEscludere")
            Long idDaEscludere
    );
}