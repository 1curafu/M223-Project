package ch.glauser.serviceauftrag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.glauser.serviceauftrag.domain.Auftrag;
import ch.glauser.serviceauftrag.domain.Kunde;
import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.domain.Rapport;
import ch.glauser.serviceauftrag.domain.Rolle;
import ch.glauser.serviceauftrag.domain.Status;
import ch.glauser.serviceauftrag.dto.DisponierenRequest;
import ch.glauser.serviceauftrag.dto.RapportRequest;
import ch.glauser.serviceauftrag.exception.UngueltigerStatusUebergangException;
import ch.glauser.serviceauftrag.repository.AuftragRepository;
import ch.glauser.serviceauftrag.repository.KundeRepository;
import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import ch.glauser.serviceauftrag.repository.RapportRepository;
import ch.glauser.serviceauftrag.security.BenutzerPrincipal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuftragServiceTest {

    @Mock private AuftragRepository auftragRepository;
    @Mock private RapportRepository rapportRepository;
    @Mock private KundeRepository kundeRepository;
    @Mock private MitarbeiterRepository mitarbeiterRepository;

    @InjectMocks private AuftragService auftragService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ---------- Hilfsmethoden ----------

    private Mitarbeiter mitarbeiter(long id, Rolle rolle) {
        Mitarbeiter m = new Mitarbeiter("Test " + id, rolle, "u" + id + "@glauser.ch", "hash");
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    private Auftrag auftragMit(long id, Status status, Mitarbeiter zugewiesen) {
        Kunde kunde = new Kunde("Kunde", "Adresse", "012", "k@example.ch");
        ReflectionTestUtils.setField(kunde, "id", 1L);
        Auftrag a = new Auftrag(kunde, "Titel", "Beschreibung", null);
        ReflectionTestUtils.setField(a, "id", id);
        a.setStatus(status);
        a.setZugewiesenAn(zugewiesen);
        return a;
    }

    private void alsBenutzer(Mitarbeiter m) {
        BenutzerPrincipal principal = new BenutzerPrincipal(m);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    // ---------- Disponieren ----------

    @Test
    @DisplayName("Disponieren: ERFASST -> DISPONIERT setzt Mitarbeiter und Termin")
    void disponierenGueltig() {
        Auftrag auftrag = auftragMit(10L, Status.ERFASST, null);
        Mitarbeiter ma = mitarbeiter(3L, Rolle.MITARBEITER);
        LocalDateTime termin = LocalDateTime.of(2026, 6, 20, 9, 0);

        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));
        when(mitarbeiterRepository.findById(3L)).thenReturn(Optional.of(ma));
        when(rapportRepository.findByAuftragId(10L)).thenReturn(Optional.empty());

        var antwort = auftragService.disponieren(10L, new DisponierenRequest(3L, termin));

        assertThat(antwort.status()).isEqualTo(Status.DISPONIERT);
        assertThat(auftrag.getZugewiesenAn()).isEqualTo(ma);
        assertThat(auftrag.getTerminAm()).isEqualTo(termin);
    }

    @Test
    @DisplayName("Disponieren auf bereits ausgefuehrtem Auftrag wird abgelehnt")
    void disponierenUngueltig() {
        Auftrag auftrag = auftragMit(10L, Status.AUSGEFUEHRT, mitarbeiter(3L, Rolle.MITARBEITER));
        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));

        assertThatThrownBy(() ->
                auftragService.disponieren(10L, new DisponierenRequest(3L, LocalDateTime.now())))
                .isInstanceOf(UngueltigerStatusUebergangException.class);

        assertThat(auftrag.getStatus()).isEqualTo(Status.AUSGEFUEHRT);
        verify(mitarbeiterRepository, never()).findById(any());
    }

    // ---------- Ausgefuehrt markieren + Rapport ----------

    @Test
    @DisplayName("Ausgefuehrt: zugewiesener Mitarbeiter rapportiert -> AUSGEFUEHRT, Rapport gespeichert")
    void ausgefuehrtDurchZugewiesenenMitarbeiter() {
        Mitarbeiter ma = mitarbeiter(3L, Rolle.MITARBEITER);
        Auftrag auftrag = auftragMit(10L, Status.DISPONIERT, ma);
        alsBenutzer(ma);

        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));
        when(mitarbeiterRepository.findById(3L)).thenReturn(Optional.of(ma));
        when(rapportRepository.findByAuftragId(10L)).thenReturn(Optional.empty());

        var antwort = auftragService.alsAusgefuehrtMarkieren(10L,
                new RapportRequest(new BigDecimal("2.5"), "Material", "Bemerkung"));

        assertThat(antwort.status()).isEqualTo(Status.AUSGEFUEHRT);
        verify(rapportRepository).save(any(Rapport.class));
    }

    @Test
    @DisplayName("Ausgefuehrt: fremder Mitarbeiter wird mit 403 (AccessDenied) abgewiesen")
    void ausgefuehrtDurchFremdenMitarbeiter() {
        Mitarbeiter zugewiesen = mitarbeiter(3L, Rolle.MITARBEITER);
        Mitarbeiter fremder = mitarbeiter(99L, Rolle.MITARBEITER);
        Auftrag auftrag = auftragMit(10L, Status.DISPONIERT, zugewiesen);
        alsBenutzer(fremder);

        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));

        assertThatThrownBy(() -> auftragService.alsAusgefuehrtMarkieren(10L,
                new RapportRequest(new BigDecimal("1.0"), null, null)))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(auftrag.getStatus()).isEqualTo(Status.DISPONIERT);
        verify(rapportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ausgefuehrt: Bereichsleiter darf auch fuer fremde Auftraege rapportieren")
    void ausgefuehrtDurchBereichsleiter() {
        Mitarbeiter zugewiesen = mitarbeiter(3L, Rolle.MITARBEITER);
        Mitarbeiter bl = mitarbeiter(2L, Rolle.BEREICHSLEITER);
        Auftrag auftrag = auftragMit(10L, Status.DISPONIERT, zugewiesen);
        alsBenutzer(bl);

        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));
        when(mitarbeiterRepository.findById(2L)).thenReturn(Optional.of(bl));
        when(rapportRepository.findByAuftragId(10L)).thenReturn(Optional.empty());

        var antwort = auftragService.alsAusgefuehrtMarkieren(10L,
                new RapportRequest(new BigDecimal("3.0"), "Material", null));

        assertThat(antwort.status()).isEqualTo(Status.AUSGEFUEHRT);
        verify(rapportRepository).save(any(Rapport.class));
    }

    // ---------- Rapport ablehnen (Rueckwaerts) ----------

    @Test
    @DisplayName("Rapport ablehnen: AUSGEFUEHRT -> DISPONIERT, Rapport wird geloescht")
    void rapportAblehnen() {
        Auftrag auftrag = auftragMit(10L, Status.AUSGEFUEHRT, mitarbeiter(3L, Rolle.MITARBEITER));
        Rapport rapport = new Rapport(auftrag, mitarbeiter(3L, Rolle.MITARBEITER),
                new BigDecimal("2.0"), "m", "b");

        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));
        when(rapportRepository.findByAuftragId(10L)).thenReturn(Optional.of(rapport), Optional.empty());

        var antwort = auftragService.rapportAblehnen(10L);

        assertThat(antwort.status()).isEqualTo(Status.DISPONIERT);
        verify(rapportRepository).delete(rapport);
    }

    // ---------- Verrechnen ----------

    @Test
    @DisplayName("Verrechnen: AUSGEFUEHRT -> VERRECHNET")
    void verrechnenGueltig() {
        Auftrag auftrag = auftragMit(10L, Status.AUSGEFUEHRT, mitarbeiter(3L, Rolle.MITARBEITER));
        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));
        when(rapportRepository.findByAuftragId(10L)).thenReturn(Optional.empty());

        var antwort = auftragService.alsVerrechnetMarkieren(10L);

        assertThat(antwort.status()).isEqualTo(Status.VERRECHNET);
    }

    @Test
    @DisplayName("Verrechnen eines erst erfassten Auftrags wird abgelehnt")
    void verrechnenUngueltig() {
        Auftrag auftrag = auftragMit(10L, Status.ERFASST, null);
        when(auftragRepository.findById(10L)).thenReturn(Optional.of(auftrag));

        assertThatThrownBy(() -> auftragService.alsVerrechnetMarkieren(10L))
                .isInstanceOf(UngueltigerStatusUebergangException.class);

        assertThat(auftrag.getStatus()).isEqualTo(Status.ERFASST);
    }
}
