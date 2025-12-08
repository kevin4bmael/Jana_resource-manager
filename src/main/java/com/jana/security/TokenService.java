package com.jana.security;

import com.jana.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class TokenService {

    private static final String SECRET_STRING = System.getenv("SECRET_KEY") != null ?
            System.getenv("SECRET_KEY") : "minha_chave_secreta_super_segura_123456";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generateToken(Usuario usuario){
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getUserId())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(3, ChronoUnit.HOURS)))
                .signWith(KEY)
                .compact();
    }

    public String verifyAndExtractToken(String token){
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject(); // Retorna o email
        } catch (Exception e) {
            throw new RuntimeException("Token inválido");
        }
    }

    public static Long extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


            return claims.get("id", Long.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair ID: " + e.getMessage());
        }
    }
}