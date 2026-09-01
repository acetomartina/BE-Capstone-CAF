package com.martina.caf_fapi.utenti.entity;

import com.martina.caf_fapi.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "utenti",
        indexes = {
                @Index(name = "idx_utenti_ruolo", columnList = "ruolo")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dati anagrafici
    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 80)
    private String cognome;

    @Column(
            name = "codice_fiscale",
            nullable = false,
            unique = true,
            length = 16
    )
    private String codiceFiscale;

    @Column(name = "data_nascita")
    private LocalDate dataNascita;

    @Column(name = "luogo_nascita", length = 100)
    private String luogoNascita;

    // Contatti
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(name = "telefono_secondario", length = 20)
    private String telefonoSecondario;

    // Indirizzo
    @Column(length = 150)
    private String indirizzo;

    @Column(length = 100)
    private String comune;

    @Column(length = 2)
    private String provincia;

    @Column(length = 5)
    private String cap;

    // Domicilio

    @Column(
            name = "domicilio_diverso_dalla_residenza",
            nullable = false
    )
    @Builder.Default
    private boolean domicilioDiversoDallaResidenza = false;

    @Column(name = "domicilio_indirizzo", length = 150)
    private String domicilioIndirizzo;

    @Column(name = "domicilio_comune", length = 100)
    private String domicilioComune;

    @Column(name = "domicilio_provincia", length = 2)
    private String domicilioProvincia;

    @Column(name = "domicilio_cap", length = 5)
    private String domicilioCap;

    // Autenticazione e autorizzazione
    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Ruolo ruolo;

    @Column(nullable = false)
    @Builder.Default
    private boolean attivo = true;

    @Column(name = "email_verificata", nullable = false)
    @Builder.Default
    private boolean emailVerificata = false;

    @Column(name = "account_bloccato", nullable = false)
    @Builder.Default
    private boolean accountBloccato = false;

    @Column(name = "tentativi_accesso_falliti", nullable = false)
    @Builder.Default
    private int tentativiAccessoFalliti = 0;

    // Informazioni professionali
    @Column(length = 100)
    private String mansione;

    @Column(name = "numero_matricola", unique = true, length = 50)
    private String numeroMatricola;

    // Immagine profilo
    @Column(name = "url_immagine_profilo", length = 500)
    private String urlImmagineProfilo;

    // Tracciamento accessi e password
    @Column(name = "ultimo_accesso")
    private LocalDateTime ultimoAccesso;

    @Column(name = "password_modificata_il")
    private LocalDateTime passwordModificataIl;

}
