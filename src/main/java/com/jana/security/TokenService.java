package main.java.com.jana.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import main.java.com.jana.model.Usuario;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TokenService {
    private final String secret = System.getenv("SECRET_KEY");
    private final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(Usuario usuario){
        //3 horas de duracao
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id",usuario.getUserId())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(3, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public String verifyAndExtractToken(String token){
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token inválido: " + e.getMessage());
        }
    }
    public static Long extractUserIdFromToken(String token) {
        try {
            TokenService tokenService = new TokenService();
            Claims claims = Jwts.parser()
                    .verifyWith(tokenService.key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("id", Long.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair ID do token: " + e.getMessage());
        }
    }
}