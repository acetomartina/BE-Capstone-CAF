package com.martina.caf_fapi.servizi.repository;

import com.martina.caf_fapi.servizi.entity.Servizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServizioRepository
        extends JpaRepository<Servizio, Long> {

    List<Servizio>
    findByAttivoTrueOrderByOrdineVisualizzazioneAsc();

    List<Servizio>
    findByMacroAreaIdAndAttivoTrueOrderByOrdineVisualizzazioneAsc(
            Long macroAreaId
    );

    Optional<Servizio>
    findByIdAndAttivoTrue(Long id);

    Optional<Servizio>
    findBySlugAndAttivoTrue(String slug);


}