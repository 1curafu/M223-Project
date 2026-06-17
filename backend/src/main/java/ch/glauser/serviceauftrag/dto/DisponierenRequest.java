package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Eingabe fuer das Disponieren: Mitarbeiter zuweisen und Termin setzen. */
public record DisponierenRequest(
        @NotNull Long mitarbeiterId,
        @NotNull LocalDateTime terminAm
) {
}
