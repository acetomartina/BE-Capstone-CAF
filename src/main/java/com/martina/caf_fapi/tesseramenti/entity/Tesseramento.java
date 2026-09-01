package com.martina.caf_fapi.tesseramenti.entity;

import com.martina.caf_fapi.common.entity.BaseEntity;
import com.martina.caf_fapi.utenti.entity.Utente;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tesseramenti",
        indexes = {
                @Index(
                        name = "idx_tesseramenti_cliente",
                        columnList = "cliente_id"
                ),
                @Index(
                        name = "idx_tesseramenti_scadenza",
                        columnList = "data_scadenza"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tesseramento extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "cliente_id",
            nullable = false
    )
    private Utente cliente;

    @Column(
            name = "data_tesseramento",
            nullable = false
    )
    private LocalDate dataTesseramento;

    @Column(
            name = "data_scadenza",
            nullable = false
    )
    private LocalDate dataScadenza;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal quota;

    @Column(length = 500)
    private String note;

    @Column(nullable = false)
    private boolean annullato = false;

    @Column(name = "annullato_il")
    private LocalDateTime annullatoIl;
}