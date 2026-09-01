package com.martina.caf_fapi.utenti.repository;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCodiceFiscale(String codiceFiscale);

    boolean existsByNumeroMatricola(String numeroMatricola);

    boolean existsByRuolo(Ruolo ruolo);

    boolean existsByCodiceFiscaleIgnoreCaseAndIdNot(
            String codiceFiscale,
            Long id
    );

    Optional<Utente> findByEmailIgnoreCase(String email);

    Page<Utente> findByRuolo(
            Ruolo ruolo,
            Pageable pageable
    );

    Optional<Utente> findByIdAndRuoloAndEliminatoFalse(
            Long id,
            Ruolo ruolo
    );

    Page<Utente> findByRuoloAndEliminatoFalse(
            Ruolo ruolo,
            Pageable pageable
    );

    Page<Utente>
    findByRuoloAndEliminatoFalseAndCognomeContainingIgnoreCase(
            Ruolo ruolo,
            String cognome,
            Pageable pageable
    );

    Page<Utente>
    findByRuoloAndEliminatoFalseAndCodiceFiscaleContainingIgnoreCase(
            Ruolo ruolo,
            String codiceFiscale,
            Pageable pageable
    );

    Page<Utente>
    findByRuoloInAndEliminatoFalseAndAttivoTrue(
            List<Ruolo> ruoli,
            Pageable pageable
    );

    @Query("""
            SELECT u
            FROM Utente u
            WHERE u.ruolo = :ruolo
              AND u.eliminato = false
              AND (
                  LOWER(u.nome) LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(u.cognome) LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(CONCAT(u.nome, ' ', u.cognome))
                      LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(u.codiceFiscale)
                      LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(u.email)
                      LIKE LOWER(CONCAT('%', :query, '%'))
                  OR LOWER(u.telefono)
                      LIKE LOWER(CONCAT('%', :query, '%'))
              )
            ORDER BY u.cognome ASC, u.nome ASC
            """)
    List<Utente> ricercaGlobaleClienti(
            @Param("ruolo") Ruolo ruolo,
            @Param("query") String query
    );

    Page<Utente> findByRuoloAndEliminatoFalseAndAttivo(
            Ruolo ruolo,
            boolean attivo,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT u
                FROM Utente u
                WHERE u.ruolo = :ruolo
                  AND u.eliminato = false
                  AND (:attivo IS NULL OR u.attivo = :attivo)
                  AND (
                      LOWER(u.nome) LIKE LOWER(CONCAT('%', :termine, '%'))
                      OR LOWER(u.cognome) LIKE LOWER(CONCAT('%', :termine, '%'))
                      OR LOWER(CONCAT(u.nome, ' ', u.cognome))
                          LIKE LOWER(CONCAT('%', :termine, '%'))
                      OR LOWER(u.codiceFiscale)
                          LIKE LOWER(CONCAT('%', :termine, '%'))
                  )
                """,
            countQuery = """
                SELECT COUNT(u)
                FROM Utente u
                WHERE u.ruolo = :ruolo
                  AND u.eliminato = false
                  AND (:attivo IS NULL OR u.attivo = :attivo)
                  AND (
                      LOWER(u.nome) LIKE LOWER(CONCAT('%', :termine, '%'))
                      OR LOWER(u.cognome) LIKE LOWER(CONCAT('%', :termine, '%'))
                      OR LOWER(CONCAT(u.nome, ' ', u.cognome))
                          LIKE LOWER(CONCAT('%', :termine, '%'))
                      OR LOWER(u.codiceFiscale)
                          LIKE LOWER(CONCAT('%', :termine, '%'))
                  )
                """
    )
    Page<Utente> cercaClienti(
            @Param("ruolo") Ruolo ruolo,
            @Param("termine") String termine,
            @Param("attivo") Boolean attivo,
            Pageable pageable
    );
}