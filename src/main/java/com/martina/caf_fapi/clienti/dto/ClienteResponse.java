package com.martina.caf_fapi.clienti.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponse(

        Long id,
        String nome,
        String cognome,
        String codiceFiscale,
        LocalDate dataNascita,
        String luogoNascita,

        String email,
        String telefono,
        String telefonoSecondario,

        String indirizzo,
        String comune,
        String provincia,
        String cap,

        boolean domicilioDiversoDallaResidenza,
        String domicilioIndirizzo,
        String domicilioComune,
        String domicilioProvincia,
        String domicilioCap,

        boolean attivo,
        boolean emailVerificata,
        String urlImmagineProfilo,
        LocalDateTime creatoIl,
        LocalDateTime aggiornatoIl

) {
}