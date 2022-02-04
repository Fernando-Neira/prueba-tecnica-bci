package cl.bci.prueba.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
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

/**
 * Filtro de request para autenticaciones
 */
@Component
public class RequestsFilterAuthentication extends OncePerRequestFilter {

    /**
     * Secreto para el token de acceso
     */
    @Value("${app.security.auth.secret:pruebaBci2021}")
    private String secret = "pruebaBci2021";

    /**
     * Prefijo del token de acceso
     */
    @Value("${app.security.auth.prefix:Bearer}")
    private String prefix = "Bearer";

    /**
     * Header donde viene el token
     */
    @Value("${app.security.auth.header:Authorization}")
    private String authHeader = "Authorization";

    /**
     * Metodo que permite interceptar los request y permitirlos o recharzarlos en base a la configuración de seguridad
     * @param request request
     * @param response response
     * @param chain chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            // Si tiene TokenJWT
            if (hasJWTToken(request)) {
                // Obtener los claims del token
                Claims claims = getClaimsJWT(request);
                if (claims.get("authorities") != null) {
                    // Si tiene authorities entonces los agrega al contexto de spring security
                    setSpringSecurityContext(claims);
                } else {
                    // Si no, los elimina el contexto de spring
                    SecurityContextHolder.clearContext();
                }
            } else {
                // si no tiene token JWT elimina el contexto de spring
                SecurityContextHolder.clearContext();
            }

            // devuelve la request
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e ) {
            // En caso de que ocurra un error escribe un mensaje de error y lo devuelve como un JSON
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Debes acceder para poder ingresar a este recurso.");

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            new ObjectMapper().writeValue(response.getWriter(), errorDetails);
        }
    }

    /**
     * Método que obtiene los claims de un token JWT
     * @param request req
     * @return
     */
    private Claims getClaimsJWT(HttpServletRequest request) {
        String jwtToken = request.getHeader(authHeader).replace(prefix, "");
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtToken).getBody();
    }

    /**
     * Método que setea claims en el contexto de spring security
     * @param claims
     */
    private void setSpringSecurityContext(Claims claims) {
        List<?> authorities = (List<?>) claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(String.class::cast).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    /**
     * Método para validar que un request tiene un token JWT (Contenido en header Authentication y comienza con Bearer)
     * @param request
     * @return
     */
    private boolean hasJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(authHeader);
        return authenticationHeader != null && authenticationHeader.startsWith(prefix);
    }

}
