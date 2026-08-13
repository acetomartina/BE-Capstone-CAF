package com.martina.caf_fapi.documenti.repository;

import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRichiestoPraticaRepository
        extends JpaRepository<DocumentoRichiestoPratica, Long> {

    List<DocumentoRichiestoPratica>
    findByPraticaIdOrderByIdAsc(
            Long praticaId
    );

    long countByPraticaIdAndStato(
            Long praticaId,
            StatoDocumentoPratica stato
    );
}