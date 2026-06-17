package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.exception.NotFoundException;
import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MitarbeiterService {

    private final MitarbeiterRepository mitarbeiterRepository;

    public MitarbeiterService(MitarbeiterRepository mitarbeiterRepository) {
        this.mitarbeiterRepository = mitarbeiterRepository;
    }

    public List<Mitarbeiter> alle() {
        return mitarbeiterRepository.findAll();
    }

    public Mitarbeiter finden(Long id) {
        return mitarbeiterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mitarbeiter " + id + " nicht gefunden"));
    }
}
