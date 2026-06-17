package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.domain.Rolle;

public record LoginResponse(
        String token,
        Long mitarbeiterId,
        String name,
        String email,
        Rolle rolle
) {
}
