package com.martina.caf_fapi.tesseramenti.repository;

import com.martina.caf_fapi.tesseramenti.entity.Tesseramento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TesseramentoRepository
        extends JpaRepository<Tesseramento, Long> {

    List<Tesseramento>
    findByClienteIdAndEliminatoFalseOrderByDataTesseramentoDesc(
            Long clienteId
    );

    Optional<Tesseramento>
    findFirstByClienteIdAndEliminatoFalseAndAnnullatoFalseAndDataScadenzaGreaterThanEqualOrderByDataScadenzaAsc(
            Long clienteId,
            LocalDate dataOdierna
    );
}