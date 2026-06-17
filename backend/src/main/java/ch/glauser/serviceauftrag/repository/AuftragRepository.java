package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.domain.Auftrag;
import ch.glauser.serviceauftrag.domain.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuftragRepository extends JpaRepository<Auftrag, Long> {

    /** Auftraege nach Status filtern (sortiert nach Erfassungszeitpunkt, neueste zuerst). */
    List<Auftrag> findByStatusOrderByErfasstAmDesc(Status status);

    List<Auftrag> findAllByOrderByErfasstAmDesc();
}
