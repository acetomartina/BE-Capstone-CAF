package com.martina.caf_fapi.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "creato_il", nullable = false, updatable = false)
    private LocalDateTime creatoIl;

    @LastModifiedDate
    @Column(name = "aggiornato_il", nullable =false)
    private LocalDateTime aggiornatoIl;

    @CreatedBy
    @Column(name = "creato_da")
    private Long creatoDa;

    @LastModifiedBy
    @Column(name = "aggiornato_da")
    private Long aggiornatoDa;

}