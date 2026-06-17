package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record KundeRequest(
        @NotBlank String name,
        String adresse,
        String telefon,
        @Email String email
) {
}
