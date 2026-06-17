package ch.glauser.serviceauftrag.web;

import ch.glauser.serviceauftrag.domain.Status;
import ch.glauser.serviceauftrag.dto.AuftragErfassenRequest;
import ch.glauser.serviceauftrag.dto.AuftragResponse;
import ch.glauser.serviceauftrag.dto.DisponierenRequest;
import ch.glauser.serviceauftrag.dto.RapportRequest;
import ch.glauser.serviceauftrag.service.AuftragService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auftraege")
public class AuftragController {

    private final AuftragService auftragService;

    public AuftragController(AuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /** Liste, optional gefiltert: GET /api/auftraege?status=DISPONIERT */
    @GetMapping
    public List<AuftragResponse> liste(@RequestParam(required = false) Status status) {
        return auftragService.liste(status);
    }

    @GetMapping("/{id}")
    public AuftragResponse detail(@PathVariable Long id) {
        return auftragService.detail(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuftragResponse erfassen(@Valid @RequestBody AuftragErfassenRequest request) {
        return auftragService.erfassen(request);
    }

    @PutMapping("/{id}/disponieren")
    public AuftragResponse disponieren(@PathVariable Long id,
                                       @Valid @RequestBody DisponierenRequest request) {
        return auftragService.disponieren(id, request);
    }

    @PutMapping("/{id}/ausgefuehrt")
    public AuftragResponse alsAusgefuehrt(@PathVariable Long id,
                                          @Valid @RequestBody RapportRequest request) {
        return auftragService.alsAusgefuehrtMarkieren(id, request);
    }

    @PutMapping("/{id}/rapport-ablehnen")
    public AuftragResponse rapportAblehnen(@PathVariable Long id) {
        return auftragService.rapportAblehnen(id);
    }

    @PutMapping("/{id}/verrechnet")
    public AuftragResponse alsVerrechnet(@PathVariable Long id) {
        return auftragService.alsVerrechnetMarkieren(id);
    }
}
