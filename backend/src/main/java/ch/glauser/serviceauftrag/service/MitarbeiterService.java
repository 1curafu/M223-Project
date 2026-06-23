package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.dto.MitarbeiterErstellenRequest;
import ch.glauser.serviceauftrag.exception.NotFoundException;
import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MitarbeiterService {

    private final MitarbeiterRepository mitarbeiterRepository;
    private final PasswordEncoder passwordEncoder;

    public MitarbeiterService(MitarbeiterRepository mitarbeiterRepository, PasswordEncoder passwordEncoder) {
        this.mitarbeiterRepository = mitarbeiterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Mitarbeiter> alle() {
        return mitarbeiterRepository.findAll();
    }

    public Mitarbeiter finden(Long id) {
        return mitarbeiterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mitarbeiter " + id + " nicht gefunden"));
    }

    /**
     * Legt einen neuen Mitarbeiter mit Login an. Das Passwort wird mit BCrypt gehasht,
     * niemals im Klartext gespeichert. Nur der Geschaeftsleiter darf Benutzer anlegen.
     */
    @Transactional
    @PreAuthorize("hasRole('GESCHAEFTSLEITER')")
    public Mitarbeiter erstellen(MitarbeiterErstellenRequest request) {
        String email = request.email().trim().toLowerCase();
        if (mitarbeiterRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-Mail " + email + " ist bereits vergeben.");
        }
        Mitarbeiter mitarbeiter = new Mitarbeiter(
                request.name().trim(),
                request.rolle(),
                email,
                passwordEncoder.encode(request.passwort()));
        return mitarbeiterRepository.save(mitarbeiter);
    }
}
