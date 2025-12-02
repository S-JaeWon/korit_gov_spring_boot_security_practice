package com.korit.security_practice.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key KEY;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(String id) {
        return Jwts.builder()
                .subject("AccessToken")
                .id(id)
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 24L * 30L)))
                .signWith(KEY)
                .compact();
    }
    public String generateVerifyToken(String id) {
        return Jwts.builder()
                .subject("VerifyToken")
                .id(id)
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 3L)))
                .signWith(KEY)
                .compact();
    }

    public Claims getClaims(String token) throws JwtException {
        JwtParserBuilder jwtParserBuilder = Jwts.parser();

        jwtParserBuilder.setSigningKey(KEY);
        JwtParser jwtParser = jwtParserBuilder.build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public boolean isBearer(String token) {
        if (token == null) {
            return false;
        }
        if (!token.startsWith("Bearer ")) {
            return false;
        }
        return true;
    }

    public String removeBearer(String bearerToken) {
        return bearerToken.replaceFirst("Bearer ", "");
    }

}
