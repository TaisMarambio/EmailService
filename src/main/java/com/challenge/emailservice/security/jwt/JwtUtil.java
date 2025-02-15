package com.challenge.emailservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil { //va a tener metodos necesarios para generar, validar y extraer informacion del token

    private final String secretKey = "secret"; //clave secreta para firmar el token

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody(); //firmar el token con la clave secreta
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); //expiracion antes de la fecha que esta ahí
    }

    public String generateToken(String username, String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username); //el claim tiene el rol, el username y la fecha de expiracion

    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())) //fecha de creacion
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //fecha de expiracion
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secretKey).compact(); //signature con el algoritmo y la clave secreta
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
