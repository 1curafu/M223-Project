package ch.glauser.serviceauftrag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Arbeitsrapport zu genau einem Auftrag (1:1). Entsteht beim Uebergang nach AUSGEFUEHRT. */
@Entity
@Table(name = "rapport")
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "auftrag_id", nullable = false, unique = true)
    private Auftrag auftrag;

    @ManyToOne(optional = false)
    @JoinColumn(name = "erstellt_von", nullable = false)
    private Mitarbeiter erstelltVon;

    @Column(name = "arbeitszeit", precision = 5, scale = 2)
    private BigDecimal arbeitszeit;

    @Column(columnDefinition = "TEXT")
    private String material;

    @Column(columnDefinition = "TEXT")
    private String bemerkung;

    @Column(name = "erstellt_am", nullable = false)
    private LocalDateTime erstelltAm;

    protected Rapport() {
        // fuer JPA
    }

    public Rapport(Auftrag auftrag, Mitarbeiter erstelltVon, BigDecimal arbeitszeit,
                   String material, String bemerkung) {
        this.auftrag = auftrag;
        this.erstelltVon = erstelltVon;
        this.arbeitszeit = arbeitszeit;
        this.material = material;
        this.bemerkung = bemerkung;
        this.erstelltAm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Auftrag getAuftrag() {
        return auftrag;
    }

    public Mitarbeiter getErstelltVon() {
        return erstelltVon;
    }

    public BigDecimal getArbeitszeit() {
        return arbeitszeit;
    }

    public String getMaterial() {
        return material;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }
}
