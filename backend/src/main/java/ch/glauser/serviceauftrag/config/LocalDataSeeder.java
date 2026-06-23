package ch.glauser.serviceauftrag.config;

import ch.glauser.serviceauftrag.domain.Auftrag;
import ch.glauser.serviceauftrag.domain.Kunde;
import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.domain.Rapport;
import ch.glauser.serviceauftrag.domain.Rolle;
import ch.glauser.serviceauftrag.domain.Status;
import ch.glauser.serviceauftrag.repository.AuftragRepository;
import ch.glauser.serviceauftrag.repository.KundeRepository;
import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import ch.glauser.serviceauftrag.repository.RapportRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spielt beim Start im Profil "local" Beispieldaten in die In-Memory-DB ein,
 * damit die Anwendung ohne Supabase ausprobiert werden kann.
 */
@Component
@Profile("local")
public class LocalDataSeeder implements CommandLineRunner {

    private final KundeRepository kundeRepository;
    private final MitarbeiterRepository mitarbeiterRepository;
    private final AuftragRepository auftragRepository;
    private final RapportRepository rapportRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalDataSeeder(KundeRepository kundeRepository,
                           MitarbeiterRepository mitarbeiterRepository,
                           AuftragRepository auftragRepository,
                           RapportRepository rapportRepository,
                           PasswordEncoder passwordEncoder) {
        this.kundeRepository = kundeRepository;
        this.mitarbeiterRepository = mitarbeiterRepository;
        this.auftragRepository = auftragRepository;
        this.rapportRepository = rapportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (mitarbeiterRepository.count() > 0) return;

        String hash = passwordEncoder.encode("test1234");
        mitarbeiterRepository.save(new Mitarbeiter("A. Glauser", Rolle.GESCHAEFTSLEITER, "gl@glauser.ch", hash));
        Mitarbeiter frei = mitarbeiterRepository.save(new Mitarbeiter("R. Frei", Rolle.BEREICHSLEITER, "bl@glauser.ch", hash));
        Mitarbeiter steiner = mitarbeiterRepository.save(new Mitarbeiter("P. Steiner", Rolle.MITARBEITER, "ma@glauser.ch", hash));
        Mitarbeiter keller = mitarbeiterRepository.save(new Mitarbeiter("M. Keller", Rolle.MITARBEITER, "ma2@glauser.ch", hash));

        Kunde mueller = kundeRepository.save(new Kunde("M. Müller", "Dorfstrasse 12, 8308 Illnau", "052 000 00 00", "mueller@example.ch"));
        Kunde hauser = kundeRepository.save(new Kunde("Hauser GmbH", "Industriestrasse 5, 8307 Effretikon", "052 111 11 11", "info@hauser.ch"));
        Kunde brunner = kundeRepository.save(new Kunde("K. Brunner", "Bahnhofweg 3, 8400 Winterthur", "052 222 22 22", "k.brunner@example.ch"));
        Kunde schmid = kundeRepository.save(new Kunde("Schmid AG", "Werkstrasse 20, 8404 Winterthur", "052 333 33 33", "kontakt@schmid-ag.ch"));
        Kunde weber = kundeRepository.save(new Kunde("L. Weber", "Seeweg 8, 8610 Uster", "052 444 44 44", "l.weber@example.ch"));

        Auftrag a1 = auftrag(mueller, "Wasserhahn ersetzen", "Alten Mischer in der Küche demontieren, neuen montieren.",
                Status.AUSGEFUEHRT, steiner, LocalDateTime.of(2026, 6, 18, 8, 0));
        rapportRepository.save(new Rapport(a1, steiner, new BigDecimal("2.50"), "1× Mischbatterie, Dichtungen", "Eckventil leicht verkalkt, gereinigt."));

        auftrag(hauser, "Heizung entlüften", "Heizkörper im 1. OG entlüften, Druck prüfen.",
                Status.DISPONIERT, keller, LocalDateTime.of(2026, 6, 19, 13, 0));

        auftrag(brunner, "WC-Spülung defekt", "Spülkasten läuft nach, Kunde meldet tropfendes Geräusch.",
                Status.ERFASST, null, null);

        Auftrag a4 = auftrag(schmid, "Boiler-Service jährlich", "Jahreswartung Boiler inkl. Entkalkung und Funktionsprüfung.",
                Status.VERRECHNET, steiner, LocalDateTime.of(2026, 6, 10, 9, 0));
        rapportRepository.save(new Rapport(a4, steiner, new BigDecimal("1.50"), "Service-Kit Boiler", "Jahreswartung ohne Befund, Anlage ok."));

        auftrag(weber, "Rohrbruch Keller", "Wasseraustritt an Steigleitung im Keller, dringend.",
                Status.DISPONIERT, keller, LocalDateTime.of(2026, 6, 17, 7, 30));
    }

    private Auftrag auftrag(Kunde kunde, String titel, String beschreibung,
                            Status status, Mitarbeiter zugewiesen, LocalDateTime termin) {
        Auftrag a = new Auftrag(kunde, titel, beschreibung, termin);
        a.setStatus(status);
        a.setZugewiesenAn(zugewiesen);
        return auftragRepository.save(a);
    }
}
