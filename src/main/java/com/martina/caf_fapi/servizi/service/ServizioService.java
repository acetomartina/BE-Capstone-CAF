package com.martina.caf_fapi.servizi.service;

import com.martina.caf_fapi.servizi.dto.MacroAreaResponse;
import com.martina.caf_fapi.servizi.dto.ServizioResponse;

import java.util.List;

public interface ServizioService {

    List<MacroAreaResponse> trovaMacroAreeAttive();

    List<ServizioResponse> trovaServiziAttivi();

    List<ServizioResponse> trovaServiziPerMacroArea(
            Long macroAreaId
    );

    ServizioResponse trovaServizioPerId(
            Long id
    );
}