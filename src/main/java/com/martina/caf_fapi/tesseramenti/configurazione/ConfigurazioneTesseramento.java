package com.martina.caf_fapi.tesseramenti.configurazione;

import com.martina.caf_fapi.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "configurazione_tesseramento")
@Getter
@Setter
@NoArgsConstructor
public class ConfigurazioneTesseramento
        extends BaseEntity {

    @Id
    private Long id;

    @Column(
            name = "quota_annuale",
            precision = 10,
            scale = 2
    )
    private BigDecimal quotaAnnuale;
}