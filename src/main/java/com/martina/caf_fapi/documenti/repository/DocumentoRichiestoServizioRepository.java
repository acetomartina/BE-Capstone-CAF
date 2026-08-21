package com.martina.caf_fapi.documenti.repository;

import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoServizio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRichiestoServizioRepository
        extends JpaRepository<DocumentoRichiestoServizio, Long> {

    List<DocumentoRichiestoServizio>
    findByServizioIdAndAttivoTrueOrderByOrdineVisualizzazioneAsc(
            Long servizioId
    );
}