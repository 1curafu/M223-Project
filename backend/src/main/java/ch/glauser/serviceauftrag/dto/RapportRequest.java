package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RapportRequest(
        @NotNull @DecimalMin(value = "0.0", message = "Arbeitszeit darf nicht negativ sein") BigDecimal arbeitszeit,
        String material,
        String bemerkung
) {
}
