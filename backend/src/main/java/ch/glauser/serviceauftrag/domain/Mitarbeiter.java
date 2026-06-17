package ch.glauser.serviceauftrag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Person mit Rolle und Login. Die Spalte {@code rolle} dient zugleich als
 * Berechtigungsrolle fuer Spring Security.
 */
@Entity
@Table(name = "mitarbeiter")
public class Mitarbeiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rolle rolle;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "passwort_hash", nullable = false, length = 100)
    private String passwortHash;

    protected Mitarbeiter() {
        // fuer JPA
    }

    public Mitarbeiter(String name, Rolle rolle, String email, String passwortHash) {
        this.name = name;
        this.rolle = rolle;
        this.email = email;
        this.passwortHash = passwortHash;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswortHash() {
        return passwortHash;
    }

    public void setPasswortHash(String passwortHash) {
        this.passwortHash = passwortHash;
    }
}
