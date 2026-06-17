package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.domain.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KundeRepository extends JpaRepository<Kunde, Long> {
}
