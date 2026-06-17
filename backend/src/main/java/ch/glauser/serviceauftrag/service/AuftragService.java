package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.domain.Auftrag;
import ch.glauser.serviceauftrag.domain.Kunde;
import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.domain.Rapport;
import ch.glauser.serviceauftrag.domain.Rolle;
import ch.glauser.serviceauftrag.domain.Status;
import ch.glauser.serviceauftrag.dto.AuftragErfassenRequest;
import ch.glauser.serviceauftrag.dto.AuftragResponse;
import ch.glauser.serviceauftrag.dto.DisponierenRequest;
import ch.glauser.serviceauftrag.dto.RapportRequest;
import ch.glauser.serviceauftrag.exception.NotFoundException;
import ch.glauser.serviceauftrag.exception.UngueltigerStatusUebergangException;
import ch.glauser.serviceauftrag.repository.AuftragRepository;
import ch.glauser.serviceauftrag.repository.KundeRepository;
import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import ch.glauser.serviceauftrag.repository.RapportRepository;
import ch.glauser.serviceauftrag.security.BenutzerPrincipal;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Geschaeftslogik fuer Serviceauftraege. Enthaelt die serverseitig durchgesetzte
 * Status-Maschine und die rollenbasierte Zugriffskontrolle (@PreAuthorize).
 */
@Service
@Transactional
public class AuftragService {

    private final AuftragRepository auftragRepository;
    private final RapportRepository rapportRepository;
    private final KundeRepository kundeRepository;
    private final MitarbeiterRepository mitarbeiterRepository;

    public AuftragService(AuftragRepository auftragRepository,
                          RapportRepository rapportRepository,
                          KundeRepository kundeRepository,
                          MitarbeiterRepository mitarbeiterRepository) {
        this.auftragRepository = auftragRepository;
        this.rapportRepository = rapportRepository;
        this.kundeRepository = kundeRepository;
        this.mitarbeiterRepository = mitarbeiterRepository;
    }

    // ---------------------------------------------------------------
    //  Lesen (alle Rollen)
    // ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<AuftragResponse> liste(Status status) {
        List<Auftrag> auftraege = (status == null)
                ? auftragRepository.findAllByOrderByErfasstAmDesc()
                : auftragRepository.findByStatusOrderByErfasstAmDesc(status);
        return auftraege.stream().map(this::zuResponse).toList();
    }

    @Transactional(readOnly = true)
    public AuftragResponse detail(Long id) {
        return zuResponse(ladeAuftrag(id));
    }

    // ---------------------------------------------------------------
    //  1. Auftrag erfassen -> ERFASST  (Geschaeftsleiter)
    // ---------------------------------------------------------------

    @PreAuthorize("hasRole('GESCHAEFTSLEITER')")
    public AuftragResponse erfassen(AuftragErfassenRequest request) {
        Kunde kunde = kundeRepository.findById(request.kundeId())
                .orElseThrow(() -> new NotFoundException("Kunde " + request.kundeId() + " nicht gefunden"));
        Auftrag auftrag = new Auftrag(kunde, request.titel(), request.beschreibung(), request.terminAm());
        return zuResponse(auftragRepository.save(auftrag));
    }

    // ---------------------------------------------------------------
    //  4. Disponieren -> DISPONIERT  (Bereichsleiter)
    // ---------------------------------------------------------------

    @PreAuthorize("hasRole('BEREICHSLEITER')")
    public AuftragResponse disponieren(Long id, DisponierenRequest request) {
        Auftrag auftrag = ladeAuftrag(id);
        pruefeUebergang(auftrag, Status.DISPONIERT);

        Mitarbeiter mitarbeiter = mitarbeiterRepository.findById(request.mitarbeiterId())
                .orElseThrow(() -> new NotFoundException(
                        "Mitarbeiter " + request.mitarbeiterId() + " nicht gefunden"));

        auftrag.setZugewiesenAn(mitarbeiter);
        auftrag.setTerminAm(request.terminAm());
        auftrag.setStatus(Status.DISPONIERT);
        return zuResponse(auftrag);
    }

    // ---------------------------------------------------------------
    //  5. Als ausgefuehrt markieren + Rapport erfassen -> AUSGEFUEHRT
    //     (zugewiesener Mitarbeiter oder Bereichsleiter)
    // ---------------------------------------------------------------

    @PreAuthorize("hasAnyRole('MITARBEITER', 'BEREICHSLEITER')")
    public AuftragResponse alsAusgefuehrtMarkieren(Long id, RapportRequest request) {
        Auftrag auftrag = ladeAuftrag(id);
        pruefeUebergang(auftrag, Status.AUSGEFUEHRT);

        BenutzerPrincipal aktuell = aktuellerBenutzer();
        // Ein Mitarbeiter darf nur den ihm zugewiesenen Auftrag rapportieren.
        if (aktuell.getRolle() == Rolle.MITARBEITER) {
            boolean zugewiesen = auftrag.getZugewiesenAn() != null
                    && auftrag.getZugewiesenAn().getId().equals(aktuell.getMitarbeiterId());
            if (!zugewiesen) {
                throw new AccessDeniedException("Nur der zugewiesene Mitarbeiter darf diesen Auftrag rapportieren.");
            }
        }

        Mitarbeiter ersteller = mitarbeiterRepository.findById(aktuell.getMitarbeiterId())
                .orElseThrow(() -> new NotFoundException("Eingeloggter Mitarbeiter nicht gefunden"));

        Rapport rapport = new Rapport(auftrag, ersteller, request.arbeitszeit(),
                request.material(), request.bemerkung());
        rapportRepository.save(rapport);

        auftrag.setStatus(Status.AUSGEFUEHRT);
        return zuResponse(auftrag);
    }

    // ---------------------------------------------------------------
    //  Rapport ablehnen -> zurueck auf DISPONIERT  (Bereichsleiter)
    //  (Rueckwaerts-Uebergang aus dem Zustandsdiagramm)
    // ---------------------------------------------------------------

    @PreAuthorize("hasRole('BEREICHSLEITER')")
    public AuftragResponse rapportAblehnen(Long id) {
        Auftrag auftrag = ladeAuftrag(id);
        pruefeUebergang(auftrag, Status.DISPONIERT);

        rapportRepository.findByAuftragId(id).ifPresent(rapportRepository::delete);
        auftrag.setStatus(Status.DISPONIERT);
        return zuResponse(auftrag);
    }

    // ---------------------------------------------------------------
    //  6. Als verrechnet markieren -> VERRECHNET
    //     (Bereichsleiter oder Geschaeftsleiter)
    // ---------------------------------------------------------------

    @PreAuthorize("hasAnyRole('BEREICHSLEITER', 'GESCHAEFTSLEITER')")
    public AuftragResponse alsVerrechnetMarkieren(Long id) {
        Auftrag auftrag = ladeAuftrag(id);
        pruefeUebergang(auftrag, Status.VERRECHNET);
        auftrag.setStatus(Status.VERRECHNET);
        return zuResponse(auftrag);
    }

    // ---------------------------------------------------------------
    //  Hilfsmethoden
    // ---------------------------------------------------------------

    /** Prueft den Statuswechsel und wirft bei Verstoss eine fachliche Exception. */
    private void pruefeUebergang(Auftrag auftrag, Status ziel) {
        if (!auftrag.getStatus().darfWechselnNach(ziel)) {
            throw new UngueltigerStatusUebergangException(auftrag.getStatus(), ziel);
        }
    }

    private Auftrag ladeAuftrag(Long id) {
        return auftragRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Auftrag " + id + " nicht gefunden"));
    }

    private AuftragResponse zuResponse(Auftrag auftrag) {
        Rapport rapport = rapportRepository.findByAuftragId(auftrag.getId()).orElse(null);
        return AuftragResponse.von(auftrag, rapport);
    }

    private BenutzerPrincipal aktuellerBenutzer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof BenutzerPrincipal principal)) {
            throw new AccessDeniedException("Kein authentifizierter Benutzer im Kontext.");
        }
        return principal;
    }
}
