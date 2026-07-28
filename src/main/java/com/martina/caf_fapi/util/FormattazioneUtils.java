package com.martina.caf_fapi.util;

import java.util.Locale;

public final class FormattazioneUtils {

    private FormattazioneUtils() {
        throw new IllegalStateException("Classe di utilità non istanziabile.");
    }

    public static String normalizzaTitleCase(String testo) {
        String valore = normalizzaTestoBase(testo);

        if (valore == null) {
            return null;
        }

        StringBuilder risultato = new StringBuilder();
        boolean capitalizza = true;

        for (char carattere : valore.toLowerCase(Locale.ITALIAN).toCharArray()) {
            if (Character.isLetter(carattere)) {
                risultato.append(
                        capitalizza
                                ? Character.toUpperCase(carattere)
                                : carattere
                );
                capitalizza = false;
            } else {
                risultato.append(carattere);
                capitalizza = carattere == ' '
                        || carattere == '-'
                        || carattere == '\'';
            }
        }

        return risultato.toString();
    }

    public static String normalizzaTesto(String testo) {
        return normalizzaTestoBase(testo);
    }

    public static String normalizzaProvincia(String provincia) {
        String valore = normalizzaTestoBase(provincia);

        return valore == null
                ? null
                : valore.toUpperCase(Locale.ROOT);
    }

    public static String normalizzaCodiceFiscale(String codiceFiscale) {
        String valore = normalizzaTestoBase(codiceFiscale);

        return valore == null
                ? null
                : valore.replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
    }

    public static String normalizzaEmail(String email) {
        String valore = normalizzaTestoBase(email);

        return valore == null
                ? null
                : valore.toLowerCase(Locale.ROOT);
    }

    public static String normalizzaTelefono(String telefono) {
        String valore = normalizzaTestoBase(telefono);

        return valore == null
                ? null
                : valore.replaceAll("[\\s-]+", "");
    }

    public static String normalizzaCap(String cap) {
        String valore = normalizzaTestoBase(cap);

        return valore == null
                ? null
                : valore.replaceAll("\\s+", "");
    }

    public static String normalizzaUrl(String url) {
        return normalizzaTestoBase(url);
    }

    private static String normalizzaTestoBase(String testo) {
        if (testo == null) {
            return null;
        }

        String valore = testo
                .replaceAll("[\\p{Cf}\\p{Cc}&&[^\\r\\n\\t]]", "")
                .trim()
                .replaceAll("\\s+", " ");

        return valore.isBlank() ? null : valore;
    }
}