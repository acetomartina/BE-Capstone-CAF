package com.martina.caf_fapi.documenti.entity;

import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
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
    private boolean attivo = true;

    @Column(
            name = "visibile_al_cliente",
            nullable = false
    )
    @Builder.Default
    private boolean visibileAlCliente = true;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_obbligatorieta",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private TipoObbligatorietaDocumento tipoObbligatorieta =
            TipoObbligatorietaDocumento.OBBLIGATORIO;


    @Column(
            name = "ordine_visualizzazione",
            nullable = false
    )
    private Integer ordineVisualizzazione;


}