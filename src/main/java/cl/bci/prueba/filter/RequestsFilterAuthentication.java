package cl.bci.prueba.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RequestsFilterAuthentication extends OncePerRequestFilter {

    @Value("${app.security.auth.secret:pruebaBci2021}")
    private String secret = "pruebaBci2021";

    @Value("${app.security.auth.prefix:Bearer}")
    private String prefix = "Bearer";

    @Value("${app.security.auth.header:Authorization}")
    private String authHeader = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            if (hasJWTToken(request)) {
                Claims claims = getClaimsJWT(request);
                if (claims.get("authorities") != null) {
                    setSpringSecurityContext(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Debes acceder para poder ingresar a este recurso.");

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            new ObjectMapper().writeValue(response.getWriter(), errorDetails);
        }
    }

    private Claims getClaimsJWT(HttpServletRequest request) {
        String jwtToken = request.getHeader(authHeader).replace(prefix, "");
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    private void setSpringSecurityContext(Claims claims) {
        List<?> authorities = (List<?>) claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(String.class::cast).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private boolean hasJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(authHeader);
        return authenticationHeader != null && authenticationHeader.startsWith(prefix);
    }

}
