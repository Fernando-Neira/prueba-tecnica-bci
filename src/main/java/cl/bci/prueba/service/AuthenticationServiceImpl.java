package cl.bci.prueba.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${app.security.auth.secret}")
    private String secret;

    @Value("${app.security.auth.prefix}")
    private String prefix;

    @Override
    public String generateAccessToken(String email) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("app-test")
                .setSubject(email)
                .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes()).compact();

        return prefix + " " + token;
    }

}
