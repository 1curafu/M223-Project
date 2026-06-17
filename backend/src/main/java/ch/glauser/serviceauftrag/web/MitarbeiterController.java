package ch.glauser.serviceauftrag.web;

import ch.glauser.serviceauftrag.dto.MitarbeiterResponse;
import ch.glauser.serviceauftrag.service.MitarbeiterService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
