package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Eingabe fuer das Erfassen eines neuen Auftrags. */
public record AuftragErfassenRequest(
        @NotNull Long kundeId,
        @NotBlank String titel,
        @NotBlank String beschreibung,
        LocalDateTime terminAm
) {
}
