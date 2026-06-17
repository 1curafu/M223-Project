package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.domain.Auftrag;
import ch.glauser.serviceauftrag.domain.Rapport;
import ch.glauser.serviceauftrag.domain.Status;
import java.time.LocalDateTime;

/** Vollstaendige Auftrags-Detailansicht inkl. Kunde, zugewiesenem Mitarbeiter und Rapport. */
public record AuftragResponse(
        Long id,
        String anzeigeNr,
        String titel,
        String beschreibung,
        Status status,
        LocalDateTime erfasstAm,
        LocalDateTime terminAm,
        KundeResponse kunde,
        MitarbeiterResponse zugewiesenAn,
        RapportResponse rapport
) {
    /** Anzeige-Auftragsnummer aus der id abgeleitet (z. B. SA-2038). */
    public static String anzeigeNr(Long id) {
        return "SA-" + (2037 + id);
    }

    public static AuftragResponse von(Auftrag a, Rapport rapport) {
        return new AuftragResponse(
                a.getId(),
                anzeigeNr(a.getId()),
                a.getTitel(),
                a.getBeschreibung(),
                a.getStatus(),
                a.getErfasstAm(),
                a.getTerminAm(),
                KundeResponse.von(a.getKunde()),
                a.getZugewiesenAn() == null ? null : MitarbeiterResponse.von(a.getZugewiesenAn()),
                rapport == null ? null : RapportResponse.von(rapport)
        );
    }
}
