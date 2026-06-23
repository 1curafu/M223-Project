package ch.glauser.serviceauftrag.web;

import ch.glauser.serviceauftrag.dto.MitarbeiterErstellenRequest;
import ch.glauser.serviceauftrag.dto.MitarbeiterResponse;
import ch.glauser.serviceauftrag.service.MitarbeiterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mitarbeiter")
public class MitarbeiterController {

    private final MitarbeiterService mitarbeiterService;

    public MitarbeiterController(MitarbeiterService mitarbeiterService) {
        this.mitarbeiterService = mitarbeiterService;
    }

    /** Fuer das Dispositions-Dropdown im Frontend. */
    @GetMapping
    public List<MitarbeiterResponse> liste() {
        return mitarbeiterService.alle().stream().map(MitarbeiterResponse::von).toList();
    }

    /** Neuen Mitarbeiter mit Login anlegen (nur Geschaeftsleiter, Rollenpruefung im Service). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MitarbeiterResponse erstellen(@Valid @RequestBody MitarbeiterErstellenRequest request) {
        return MitarbeiterResponse.von(mitarbeiterService.erstellen(request));
    }
}
