package ch.glauser.serviceauftrag.exception;

import ch.glauser.serviceauftrag.dto.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Bean-Validation auf @Valid-Requests -> 400 mit Feldfehlern. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> feldFehler = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            feldFehler.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(400, "Validierungsfehler", "Eingaben sind ungueltig.", feldFehler));
    }

    /** Ungueltiger Statuswechsel -> 409 Conflict. */
    @ExceptionHandler(UngueltigerStatusUebergangException.class)
    public ResponseEntity<ErrorResponse> handleStatus(UngueltigerStatusUebergangException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(409, "Ungueltiger Statuswechsel", ex.getMessage()));
    }

    /** Ressource nicht gefunden -> 404. */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(404, "Nicht gefunden", ex.getMessage()));
    }

    /** Sonstige fachliche Fehler (z. B. fehlerhafte Argumente) -> 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(400, "Ungueltige Anfrage", ex.getMessage()));
    }

    /** Falsche Login-Daten -> 401. */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(401, "Nicht autorisiert", "E-Mail oder Passwort ist falsch."));
    }

    /** Fehlende Berechtigung (@PreAuthorize) -> 403. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(403, "Zugriff verweigert", "Fuer diese Aktion fehlt die Berechtigung."));
    }
}
