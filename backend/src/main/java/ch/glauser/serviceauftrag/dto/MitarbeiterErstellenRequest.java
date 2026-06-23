package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.domain.Rolle;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Eingabe fuer das Anlegen eines neuen Mitarbeiters mit Login (nur Geschaeftsleiter). */
public record MitarbeiterErstellenRequest(
        @NotBlank String name,
        @NotNull Rolle rolle,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein") String passwort
) {
}
