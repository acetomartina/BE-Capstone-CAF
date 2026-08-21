package com.martina.caf_fapi.documenti.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RiordinaDocumentiServizioRequest(

        @NotEmpty
        List<@NotNull Long> documentoIds

) {
}