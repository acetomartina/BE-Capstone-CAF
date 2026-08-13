package com.martina.caf_fapi.servizi.repository;

import com.martina.caf_fapi.servizi.entity.MacroArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MacroAreaRepository
        extends JpaRepository<MacroArea, Long> {

    List<MacroArea>
    findByAttivaTrueOrderByOrdineVisualizzazioneAsc();

    Optional<MacroArea>
    findByIdAndAttivaTrue(Long id);

    Optional<MacroArea>
    findBySlugAndAttivaTrue(String slug);
}