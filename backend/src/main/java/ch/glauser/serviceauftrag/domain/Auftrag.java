package ch.glauser.serviceauftrag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Serviceauftrag. Durchlaeuft die Status-Maschine von ERFASST bis VERRECHNET. */
@Entity
@Table(name = "auftrag")
public class Auftrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;

    @ManyToOne
    @JoinColumn(name = "zugewiesen_an")
    private Mitarbeiter zugewiesenAn;

    @Column(nullable = false, length = 150)
    private String titel;

    @Column(columnDefinition = "TEXT")
    private String beschreibung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(name = "erfasst_am", nullable = false)
    private LocalDateTime erfasstAm;

    @Column(name = "termin_am")
    private LocalDateTime terminAm;

    protected Auftrag() {
        // fuer JPA
    }

    public Auftrag(Kunde kunde, String titel, String beschreibung, LocalDateTime terminAm) {
        this.kunde = kunde;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.terminAm = terminAm;
        this.status = Status.ERFASST;
        this.erfasstAm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Mitarbeiter getZugewiesenAn() {
        return zugewiesenAn;
    }

    public void setZugewiesenAn(Mitarbeiter zugewiesenAn) {
        this.zugewiesenAn = zugewiesenAn;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getErfasstAm() {
        return erfasstAm;
    }

    public void setErfasstAm(LocalDateTime erfasstAm) {
        this.erfasstAm = erfasstAm;
    }

    public LocalDateTime getTerminAm() {
        return terminAm;
    }

    public void setTerminAm(LocalDateTime terminAm) {
        this.terminAm = terminAm;
    }
}
