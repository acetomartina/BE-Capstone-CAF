package com.martina.caf_fapi.allegati.repository;

import com.martina.caf_fapi.allegati.entity.AllegatoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllegatoDocumentoRepository
        extends JpaRepository<AllegatoDocumento, Long> {

    List<AllegatoDocumento>
    findByDocumentoPraticaIdOrderByCaricatoIlDesc(
            Long documentoPraticaId
    );

    long countByDocumentoPraticaId(
            Long documentoPraticaId
    );

    boolean existsByDocumentoPraticaIdAndIdNot(
            Long documentoPraticaId,
            Long allegatoId
    );

    List<AllegatoDocumento>
    findByDocumentoPraticaPraticaIdOrderByCaricatoIlDesc(
            Long praticaId
    );
}