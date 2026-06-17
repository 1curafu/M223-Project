package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.domain.Kunde;

public record KundeResponse(
        Long id,
        String name,
        String adresse,
        String telefon,
        String email
) {
    public static KundeResponse von(Kunde k) {
        return new KundeResponse(k.getId(), k.getName(), k.getAdresse(), k.getTelefon(), k.getEmail());
    }
}
