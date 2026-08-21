package com.martina.caf_fapi.documenti.entity;

import com.martina.caf_fapi.documenti.enums.StatoDocumentoPratica;
import com.martina.caf_fapi.documenti.enums.TipoObbligatorietaDocumento;
import com.martina.caf_fapi.pratiche.entity.Pratica;
import com.martina.caf_fapi.utenti.entity.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "documenti_richiesti_pratica",
        indexes = {
                @Index(
                        name = "documenti_richiesti_pratica_pratica_id_stato_index",
                        columnList = "pratica_id, stato"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoRichiestoPratica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "pratica_id",
            nullable = false
    )
    private Pratica pratica;

    @Column(
            nullable = false,
            length = 150
    )
    private String etichetta;

    @Column(columnDefinition = "TEXT")
    private String suggerimento;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_obbligatorieta",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private TipoObbligatorietaDocumento tipoObbligatorieta =
            TipoObbligatorietaDocumento.OBBLIGATORIO;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private StatoDocumentoPratica stato =
            StatoDocumentoPratica.MANCANTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "richiesto_da_id")
    private Utente richiestoDa;

    @Column(
            name = "creato_il",
            nullable = false,
            updatable = false
    )
    private LocalDateTime creatoIl;

    @Column(
            name = "aggiornato_il",
            nullable = false
    )
    private LocalDateTime aggiornatoIl;

    @PrePersist
    protected void prePersist() {
        LocalDateTime adesso = LocalDateTime.now();

        creatoIl = adesso;
        aggiornatoIl = adesso;
    }

    @PreUpdate
    protected void preUpdate() {
        aggiornatoIl = LocalDateTime.now();
    }
}