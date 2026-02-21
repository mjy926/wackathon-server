package com.wafflestudio.areucoming.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Service
public class AuthService {
    private final SecretKey secretKey;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public AuthService(@Value("${JWT_SECRET}") String secret){
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        this.refreshTokenExpirationTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 15;
    }
    public String createToken(String sub, String token_type, long expiresMs){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiresMs);

        return Jwts.builder()
                .claim("sub", sub)
                .claim("iat", now)
                .claim("exp", expiration)
                .claim("typ",  token_type)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String createAccessToken(String email){
        return createToken(email, "access", accessTokenExpirationTime);
    }

    public String createRefreshToken(String email){
        return createToken(email, "refresh", refreshTokenExpirationTime);
    }

    public boolean validateAccessToken(String token){
        String typ = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("typ").toString();

        return isValidToken(token) && Objects.equals(typ, "access");
    }

    public boolean validateRefreshToken(String token){
        String typ = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("typ").toString();

        return isValidToken(token) && Objects.equals(typ, "refresh");
    }

    public boolean isValidToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch(SecurityException | MalformedJwtException |
              ExpiredJwtException | UnsupportedJwtException |
              IllegalArgumentException e){
            return false;
        }
    }

    public String getSub(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
