package com.martina.caf_fapi.documenti.service;

import com.martina.caf_fapi.pratiche.entity.Pratica;

public interface DocumentoPraticaService {

    void generaChecklistDaServizio(
            Pratica pratica
    );
}