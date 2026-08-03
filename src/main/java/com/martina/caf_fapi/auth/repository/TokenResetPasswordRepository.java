package com.martina.caf_fapi.auth.repository;

import com.martina.caf_fapi.auth.entity.TokenResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenResetPasswordRepository
        extends JpaRepository<TokenResetPassword, Long> {

    /** Il token in chiaro non e' mai salvato: si cerca sempre per hash. */
    Optional<TokenResetPassword> findByTokenHash(String tokenHash);

    /**
     * Elimina i token ancora aperti di un utente, cosi' che una nuova
     * richiesta renda inutilizzabili i link inviati in precedenza.
     * <p>
     * Cancella invece di marcare: {@code usatoIl} deve continuare a
     * significare "qualcuno ha aperto questo link", non "e' decaduto".
     * I token gia' consumati restano in tabella come traccia.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM TokenResetPassword t
             WHERE t.utente.id = :utenteId
               AND t.usatoIl IS NULL
            """)
    int eliminaTokenNonUsati(@Param("utenteId") Long utenteId);

    /** Pulizia dei token scaduti, per non far crescere la tabella. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM TokenResetPassword t
             WHERE t.scadenza < :limite
            """)
    int eliminaScadutiPrimaDi(@Param("limite") LocalDateTime limite);
}
