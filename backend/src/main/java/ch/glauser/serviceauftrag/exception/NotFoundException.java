package ch.glauser.serviceauftrag.exception;

/** Eine angeforderte Ressource existiert nicht (-> HTTP 404). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String meldung) {
        super(meldung);
    }
}
