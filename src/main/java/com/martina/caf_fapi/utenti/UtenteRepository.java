package com.martina.caf_fapi.utenti;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    Optional<Utente> findByCodiceFiscale(String codiceFiscale);

    Optional<Utente> findByIdAndAttivoTrue(Long id);

    boolean existsByEmail(String email);

    boolean existsByCodiceFiscale(String codiceFiscale);
}