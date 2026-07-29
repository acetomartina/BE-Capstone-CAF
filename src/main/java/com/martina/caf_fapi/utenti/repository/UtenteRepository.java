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

    Optional<Utente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCodiceFiscale(String codiceFiscale);

    boolean existsByNumeroMatricola(String numeroMatricola);

    boolean existsByRuolo(Ruolo ruolo);

    Page<Utente> findByRuolo(
            Ruolo ruolo,
            Pageable pageable
    );
}