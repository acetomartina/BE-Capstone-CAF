package com.martina.caf_fapi.validation;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class CodiceFiscaleValidator {

    private static final String FORMATO_CODICE_FISCALE =
            "^[A-Z]{6}"
                    + "[0-9LMNPQRSTUV]{2}"
                    + "[ABCDEHLMPRST]"
                    + "[0-9LMNPQRSTUV]{2}"
                    + "[A-Z]"
                    + "[0-9LMNPQRSTUV]{3}"
                    + "[A-Z]$";

    private static final Map<Character, Integer> VALORI_POSIZIONI_DISPARI = Map.ofEntries(
            Map.entry('0', 1),
            Map.entry('1', 0),
            Map.entry('2', 5),
            Map.entry('3', 7),
            Map.entry('4', 9),
            Map.entry('5', 13),
            Map.entry('6', 15),
            Map.entry('7', 17),
            Map.entry('8', 19),
            Map.entry('9', 21),
            Map.entry('A', 1),
            Map.entry('B', 0),
            Map.entry('C', 5),
            Map.entry('D', 7),
            Map.entry('E', 9),
            Map.entry('F', 13),
            Map.entry('G', 15),
            Map.entry('H', 17),
            Map.entry('I', 19),
            Map.entry('J', 21),
            Map.entry('K', 2),
            Map.entry('L', 4),
            Map.entry('M', 18),
            Map.entry('N', 20),
            Map.entry('O', 11),
            Map.entry('P', 3),
            Map.entry('Q', 6),
            Map.entry('R', 8),
            Map.entry('S', 12),
            Map.entry('T', 14),
            Map.entry('U', 16),
            Map.entry('V', 10),
            Map.entry('W', 22),
            Map.entry('X', 25),
            Map.entry('Y', 24),
            Map.entry('Z', 23)
    );

    public boolean isValido(String codiceFiscale) {
        if (codiceFiscale == null || codiceFiscale.isBlank()) {
            return false;
        }

        String valore = codiceFiscale.toUpperCase(Locale.ROOT);

        if (!valore.matches(FORMATO_CODICE_FISCALE)) {
            return false;
        }

        int somma = 0;

        for (int indice = 0; indice < 15; indice++) {
            char carattere = valore.charAt(indice);

            if (indice % 2 == 0) {
                Integer valoreDispari = VALORI_POSIZIONI_DISPARI.get(carattere);

                if (valoreDispari == null) {
                    return false;
                }

                somma += valoreDispari;
            } else {
                somma += valorePosizionePari(carattere);
            }
        }

        char carattereDiControlloAtteso =
                (char) ('A' + somma % 26);

        return valore.charAt(15) == carattereDiControlloAtteso;
    }

    private int valorePosizionePari(char carattere) {
        if (Character.isDigit(carattere)) {
            return carattere - '0';
        }

        return carattere - 'A';
    }
}