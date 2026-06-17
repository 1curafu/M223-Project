package ch.glauser.serviceauftrag.web;

import ch.glauser.serviceauftrag.dto.KundeRequest;
import ch.glauser.serviceauftrag.dto.KundeResponse;
import ch.glauser.serviceauftrag.service.KundeService;
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
@RequestMapping("/api/kunden")
public class KundeController {

    private final KundeService kundeService;

    public KundeController(KundeService kundeService) {
        this.kundeService = kundeService;
    }

    @GetMapping
    public List<KundeResponse> liste() {
        return kundeService.alle().stream().map(KundeResponse::von).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KundeResponse erstellen(@Valid @RequestBody KundeRequest request) {
        return KundeResponse.von(kundeService.erstellen(request));
    }
}
