package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.dto.LoginRequest;
import ch.glauser.serviceauftrag.dto.LoginResponse;
import ch.glauser.serviceauftrag.security.BenutzerPrincipal;
import ch.glauser.serviceauftrag.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/** Authentifiziert Login-Daten und stellt bei Erfolg ein JWT aus. */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        // Wirft BadCredentialsException bei falschen Daten -> 401 im GlobalExceptionHandler.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.passwort()));

        BenutzerPrincipal principal = (BenutzerPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new LoginResponse(
                token,
                principal.getMitarbeiterId(),
                principal.getName(),
                principal.getEmail(),
                principal.getRolle());
    }
}
