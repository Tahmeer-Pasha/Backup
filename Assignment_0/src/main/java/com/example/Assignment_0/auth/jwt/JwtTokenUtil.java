package com.example.Assignment_0.auth.jwt;

import com.example.Assignment_0.entity.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Map<String, String> generateToken(UserDetails userDetails, Map<String, Object> claims) {
        // 10 hours
        long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 10;
        String accessToken = generateToken(claims, userDetails, ACCESS_TOKEN_EXPIRATION);
        // 7 days
        long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
        String refreshToken = generateToken(claims, userDetails, REFRESH_TOKEN_EXPIRATION);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        log.info(userDetails.getUsername());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractRoles(String token) {
        return extractClaims(token, claims -> (String) claims.get("role"));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
