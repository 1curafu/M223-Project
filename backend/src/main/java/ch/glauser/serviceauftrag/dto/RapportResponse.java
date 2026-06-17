package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.domain.Rapport;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RapportResponse(
        Long id,
        BigDecimal arbeitszeit,
        String material,
        String bemerkung,
        MitarbeiterResponse erstelltVon,
        LocalDateTime erstelltAm
) {
    public static RapportResponse von(Rapport r) {
        return new RapportResponse(
                r.getId(),
                r.getArbeitszeit(),
                r.getMaterial(),
                r.getBemerkung(),
                MitarbeiterResponse.von(r.getErstelltVon()),
                r.getErstelltAm()
        );
    }
}
