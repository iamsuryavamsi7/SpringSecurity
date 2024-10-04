package com.connekt.SpringSecurity_V_01.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.security.jwt.secretKey}")
    private String secretKey;

    @Value("${application.security.jwt.accessToken.expiration}")
    private int accessTokenExpiration;

    @Value("${application.security.jwt.refreshToken.expiration}")
    private int refreshTokenExpiration;

    private SecretKey getSignInKey() {

        return Keys.hmacShaKeyFor(secretKey.getBytes());

    }

    private Object populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        HashSet<String> authoritySet = new HashSet<>();

        for ( GrantedAuthority authority: authorities ) {

            authoritySet.add(authority.getAuthority());

        }

        return String.join(",", authoritySet);

    }

    public String generateToken(UserDetails userDetails){

        return Jwts.builder()
                .claim("Authorities", populateAuthorities(userDetails.getAuthorities()))
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSignInKey())
                .compact();

    }

    public String generateRefreshToken(UserDetails userDetails){

        return Jwts.builder()
                .claim("Authorities", populateAuthorities(userDetails.getAuthorities()))
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSignInKey())
                .compact();

    }

    private Claims extractAllClaims(String jwtToken){

        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

    }

    public <T> T extractClaims(String jwtToken, Function<Claims, T> tClaimsFunction){

        Claims extractedClaims = extractAllClaims(jwtToken);

        return tClaimsFunction.apply(extractedClaims);

    }

    public String extractUserEmail(String jwtToken){

        return extractClaims(jwtToken, Claims::getSubject);

    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails){

        Date extractExpiration = extractClaims(jwtToken, Claims::getExpiration);

        String userName = extractUserEmail(jwtToken);

        return userName.equals(userDetails.getUsername()) && extractExpiration.after(new Date(System.currentTimeMillis()));

    }

}
