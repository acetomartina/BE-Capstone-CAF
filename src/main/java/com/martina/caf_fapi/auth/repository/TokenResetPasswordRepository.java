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

    Optional<TokenResetPassword> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM TokenResetPassword t
             WHERE t.utente.id = :utenteId
               AND t.usatoIl IS NULL
            """)
    int eliminaTokenNonUsati(@Param("utenteId") Long utenteId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM TokenResetPassword t
             WHERE t.scadenza < :limite
            """)
    int eliminaScadutiPrimaDi(@Param("limite") LocalDateTime limite);
}
