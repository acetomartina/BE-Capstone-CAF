package com.martina.caf_fapi.utenti.repository;

import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    Optional<Utente> findByCodiceFiscale(String codiceFiscale);

    Optional<Utente> findByNumeroMatricola(String numeroMatricola);

    boolean existsByEmail(String email);

    boolean existsByCodiceFiscale(String codiceFiscale);

    boolean existsByNumeroMatricola(String numeroMatricola);

    List<Utente> findAllByRuolo(Ruolo ruolo);

    List<Utente> findAllByAttivoTrue();

    List<Utente> findAllByRuoloAndAttivoTrue(Ruolo ruolo);
}