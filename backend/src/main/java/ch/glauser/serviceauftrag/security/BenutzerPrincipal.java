package ch.glauser.serviceauftrag.security;

import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.domain.Rolle;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Eingeloggter Benutzer als Spring-Security-Principal.
 * Haelt zusaetzlich id und Rolle, damit die Geschaeftslogik die Eigentuemerschaft
 * (z. B. "der zugewiesene Mitarbeiter") pruefen kann.
 */
public class BenutzerPrincipal implements UserDetails {

    private final Long mitarbeiterId;
    private final String name;
    private final String email;
    private final String passwortHash;
    private final Rolle rolle;

    public BenutzerPrincipal(Mitarbeiter m) {
        this.mitarbeiterId = m.getId();
        this.name = m.getName();
        this.email = m.getEmail();
        this.passwortHash = m.getPasswortHash();
        this.rolle = m.getRolle();
    }

    public Long getMitarbeiterId() {
        return mitarbeiterId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Rolle getRolle() {
        return rolle;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rolle.name()));
    }

    @Override
    public String getPassword() {
        return passwortHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
