package ch.glauser.serviceauftrag.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime zeitpunkt,
        int status,
        String fehler,
        String meldung,
        Map<String, String> feldFehler
) {
    public static ErrorResponse of(int status, String fehler, String meldung) {
        return new ErrorResponse(LocalDateTime.now(), status, fehler, meldung, null);
    }

    public static ErrorResponse of(int status, String fehler, String meldung, Map<String, String> feldFehler) {
        return new ErrorResponse(LocalDateTime.now(), status, fehler, meldung, feldFehler);
    }
}
