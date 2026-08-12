package com.martina.caf_fapi.pratiche.repository;

import com.martina.caf_fapi.pratiche.entity.Sottopratica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SottopraticaRepository
        extends JpaRepository<Sottopratica, Long> {

    Page<Sottopratica> findByPraticaIdAndEliminatoFalse(
            Long praticaId,
            Pageable pageable
    );

    Optional<Sottopratica> findByIdAndEliminatoFalse(Long id);

    Page<Sottopratica> findByOperatoreAssegnatoIdAndEliminatoFalse(
            Long operatoreId,
            Pageable pageable
    );
}