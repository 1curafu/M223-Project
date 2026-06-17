package ch.glauser.serviceauftrag.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Liest das Bearer-Token aus dem Authorization-Header und setzt den Security-Context. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final BenutzerDetailsService benutzerDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, BenutzerDetailsService benutzerDetailsService) {
        this.jwtService = jwtService;
        this.benutzerDetailsService = benutzerDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (jwtService.isValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtService.extractUsername(token);
            UserDetails benutzer = benutzerDetailsService.loadUserByUsername(email);
            var auth = new UsernamePasswordAuthenticationToken(
                    benutzer, null, benutzer.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
