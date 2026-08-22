package com.martina.caf_fapi.allegati.entity;

import com.martina.caf_fapi.documenti.entity.DocumentoRichiestoPratica;
import com.martina.caf_fapi.utenti.entity.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "allegati_documento",
        indexes = {
                @Index(
                        name = "idx_allegati_documento_pratica",
                        columnList = "documento_pratica_id"
                ),
                @Index(
                        name = "idx_allegati_documento_caricato_da",
                        columnList = "caricato_da_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllegatoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "documento_pratica_id",
            nullable = false
    )
    private DocumentoRichiestoPratica documentoPratica;

    @Column(
            name = "nome_originale",
            nullable = false,
            length = 255
    )
    private String nomeOriginale;

    @Column(
            name = "nome_storage",
            nullable = false,
            unique = true,
            length = 255
    )
    private String nomeStorage;

    @Column(
            name = "mime_type",
            nullable = false,
            length = 100
    )
    private String mimeType;

    @Column(
            nullable = false
    )
    private Long dimensione;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "caricato_da_id",
            nullable = false
    )
    private Utente caricatoDa;

    @Column(
            name = "caricato_il",
            nullable = false,
            updatable = false
    )
    private LocalDateTime caricatoIl;

    @PrePersist
    protected void prePersist() {
        caricatoIl = LocalDateTime.now();
    }
}