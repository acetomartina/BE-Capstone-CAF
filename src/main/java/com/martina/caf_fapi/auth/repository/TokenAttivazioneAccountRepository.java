package com.martina.caf_fapi.auth.repository;

import com.martina.caf_fapi.auth.entity.TokenAttivazioneAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenAttivazioneAccountRepository
        extends JpaRepository<TokenAttivazioneAccount, Long> {

    Optional<TokenAttivazioneAccount> findByTokenHash(
            String tokenHash
    );

    @Modifying
    @Query("""
            delete from TokenAttivazioneAccount token
            where token.utente.id = :utenteId
            and token.usatoIl is null
            """)
    void eliminaTokenNonUsati(
            @Param("utenteId") Long utenteId
    );
}