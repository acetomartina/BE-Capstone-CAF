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
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String jwtSecret;
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    public String generaToken(UserDetails userDetails) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userDetails.getUsername())
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

        String username = estraiUsername(token);

        return username.equals(userDetails.getUsername())
                && !tokenScaduto(token);
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