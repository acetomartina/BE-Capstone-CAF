package com.martina.caf_fapi.tesseramenti.configurazione;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurazioneTesseramentoRepository
        extends JpaRepository<
        ConfigurazioneTesseramento,
        Long
        > {
}