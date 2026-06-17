package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.domain.Kunde;
import ch.glauser.serviceauftrag.dto.KundeRequest;
import ch.glauser.serviceauftrag.exception.NotFoundException;
import ch.glauser.serviceauftrag.repository.KundeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KundeService {

    private final KundeRepository kundeRepository;

    public KundeService(KundeRepository kundeRepository) {
        this.kundeRepository = kundeRepository;
    }

    @Transactional(readOnly = true)
    public List<Kunde> alle() {
        return kundeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Kunde finden(Long id) {
        return kundeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kunde " + id + " nicht gefunden"));
    }

    public Kunde erstellen(KundeRequest request) {
        Kunde kunde = new Kunde(request.name(), request.adresse(), request.telefon(), request.email());
        return kundeRepository.save(kunde);
    }
}
