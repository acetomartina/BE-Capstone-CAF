package com.martina.caf_fapi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Risposta di sola conferma. Il messaggio non deve mai rivelare se un
 * account esiste: il frontend mostra comunque un testo fisso proprio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessaggioResponse {

    private String messaggio;
}
