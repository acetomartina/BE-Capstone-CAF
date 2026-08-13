package com.martina.caf_fapi.documenti.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documenti_richiesti_servizio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRichiestoServizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "servizio_id",
            nullable = false
    )
    private Long servizioId;

    @Column(
            nullable = false,
            length = 150
    )
    private String etichetta;

    @Column(columnDefinition = "TEXT")
    private String suggerimento;

    @Column(nullable = false)
    @Builder.Default
    private boolean obbligatorio = true;

    @Column(
            name = "ordine_visualizzazione",
            nullable = false
    )
    private Integer ordineVisualizzazione;
}