package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.domain.Rapport;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RapportRepository extends JpaRepository<Rapport, Long> {

    Optional<Rapport> findByAuftragId(Long auftragId);
}
