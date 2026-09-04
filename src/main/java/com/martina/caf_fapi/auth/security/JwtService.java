package com.martina.caf_fapi.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    /**
     * Istante dell'ultimo cambio password, in secondi epoch. Se la password
     * cambia dopo l'emissione, il token smette di valere: e' cosi' che un
     * reset chiude le sessioni gia' aperte altrove.
     */
    private static final String CLAIM_PASSWORD = "pwd";

    private final String jwtSecret;
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    public String generaToken(UtenteDetails userDetails) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(
                        CLAIM_PASSWORD,
                        marcaturaPassword(userDetails.getPasswordModificataIl())
                )
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public LocalDateTime calcolaScadenza() {
        return LocalDateTime.now()
                .plusSeconds(jwtExpiration / 1000);
    }

    public String estraiUsername(String token) {
        return estraiClaim(token, Claims::getSubject);
    }

    public boolean tokenValido(
            String token,
            UserDetails userDetails
    ) {

        /* Fallire in direzione del rifiuto: senza il dato sul cambio
           password non si puo' stabilire se il token sia ancora legittimo. */
        if (!(userDetails instanceof UtenteDetails dettagli)) {
            return false;
        }

        String username = estraiUsername(token);

        return username.equals(userDetails.getUsername())
                && !tokenScaduto(token)
                && passwordNonCambiata(token, dettagli);
    }

    /**
     * Confronta la marcatura contenuta nel token con quella attuale
     * dell'utente. I token emessi prima dell'introduzione della claim non
     * la contengono e vengono rifiutati: comporta un nuovo accesso una
     * tantum, ma non lascia in giro sessioni non verificabili.
     */
    private boolean passwordNonCambiata(
            String token,
            UtenteDetails dettagli
    ) {

        Object valore = estraiClaim(
                token,
                claims -> claims.get(CLAIM_PASSWORD)
        );

        if (!(valore instanceof Number marcatura)) {
            return false;
        }

        return marcatura.longValue()
                == marcaturaPassword(dettagli.getPasswordModificataIl());
    }

    private long marcaturaPassword(LocalDateTime passwordModificataIl) {

        if (passwordModificataIl == null) {
            return 0L;
        }

        return passwordModificataIl
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
    }

    private boolean tokenScaduto(String token) {
        return estraiScadenza(token).before(new Date());
    }

    private Date estraiScadenza(String token) {
        return estraiClaim(token, Claims::getExpiration);
    }

    private <T> T estraiClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims = estraiTuttiIClaims(token);

        return resolver.apply(claims);
    }

    private Claims estraiTuttiIClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}