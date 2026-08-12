package com.martina.caf_fapi.pratiche.repository;

import com.martina.caf_fapi.pratiche.entity.Pratica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PraticaRepository
        extends JpaRepository<Pratica, Long> {

    Page<Pratica> findByEliminatoFalse(Pageable pageable);

    Optional<Pratica> findByIdAndEliminatoFalse(Long id);

    Page<Pratica> findByClienteIdAndEliminatoFalse(
            Long clienteId,
            Pageable pageable
    );

    Page<Pratica> findByResponsabileIdAndEliminatoFalse(
            Long responsabileId,
            Pageable pageable
    );

    boolean existsByNumeroPratica(String numeroPratica);
}