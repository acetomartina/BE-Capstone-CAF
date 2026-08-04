package com.martina.caf_fapi.utenti.repository;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCodiceFiscale(String codiceFiscale);

    boolean existsByNumeroMatricola(String numeroMatricola);

    boolean existsByRuolo(Ruolo ruolo);

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

    Page<Utente> findByRuoloAndEliminatoFalseAndCognomeContainingIgnoreCase(
            Ruolo ruolo,
            String cognome,
            Pageable pageable
    );

    Page<Utente> findByRuoloAndEliminatoFalseAndCodiceFiscaleContainingIgnoreCase(
            Ruolo ruolo,
            String codiceFiscale,
            Pageable pageable
    );
}