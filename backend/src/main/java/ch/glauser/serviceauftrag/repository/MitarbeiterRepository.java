package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MitarbeiterRepository extends JpaRepository<Mitarbeiter, Long> {

    /** Fuer die Authentifizierung: Mitarbeiter anhand der Login-E-Mail laden. */
    Optional<Mitarbeiter> findByEmail(String email);
}
