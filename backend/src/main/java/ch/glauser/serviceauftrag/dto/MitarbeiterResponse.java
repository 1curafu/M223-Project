package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.domain.Rolle;

public record MitarbeiterResponse(
        Long id,
        String name,
        Rolle rolle
) {
    public static MitarbeiterResponse von(Mitarbeiter m) {
        return new MitarbeiterResponse(m.getId(), m.getName(), m.getRolle());
    }
}
