package ch.glauser.serviceauftrag.domain;

import java.util.Set;

/**
 * Lebenszyklus eines Auftrags.
 * <p>
 * Erlaubte Uebergaenge (serverseitig im Service durchgesetzt):
 * <pre>
 *   ERFASST     -> DISPONIERT
 *   DISPONIERT  -> AUSGEFUEHRT
 *   AUSGEFUEHRT -> VERRECHNET           (Rapport freigegeben)
 *   AUSGEFUEHRT -> DISPONIERT           (Rapport abgelehnt, zurueck zur Disposition)
 * </pre>
 * Jeder andere Uebergang ist ungueltig.
 */
public enum Status {
    ERFASST,
    DISPONIERT,
    AUSGEFUEHRT,
    VERRECHNET;

    /**
     * Liefert die Zielzustaende, in die aus dem aktuellen Status gewechselt werden darf.
     */
    public Set<Status> erlaubteUebergaenge() {
        return switch (this) {
            case ERFASST     -> Set.of(DISPONIERT);
            case DISPONIERT  -> Set.of(AUSGEFUEHRT);
            case AUSGEFUEHRT -> Set.of(VERRECHNET, DISPONIERT);
            case VERRECHNET  -> Set.of();
        };
    }

    /**
     * Prueft, ob der Uebergang von diesem Status nach {@code ziel} fachlich erlaubt ist.
     */
    public boolean darfWechselnNach(Status ziel) {
        return erlaubteUebergaenge().contains(ziel);
    }
}
