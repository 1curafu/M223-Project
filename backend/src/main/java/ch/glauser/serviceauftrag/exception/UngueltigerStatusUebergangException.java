package ch.glauser.serviceauftrag.exception;

import ch.glauser.serviceauftrag.domain.Status;

/** Ein fachlich nicht erlaubter Statuswechsel wurde versucht (-> HTTP 409). */
public class UngueltigerStatusUebergangException extends RuntimeException {
    public UngueltigerStatusUebergangException(Status von, Status nach) {
        super("Ungueltiger Statuswechsel: " + von + " -> " + nach);
    }

    public UngueltigerStatusUebergangException(String meldung) {
        super(meldung);
    }
}
