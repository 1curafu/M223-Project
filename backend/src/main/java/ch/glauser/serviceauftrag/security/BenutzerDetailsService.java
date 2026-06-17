package ch.glauser.serviceauftrag.security;

import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Laedt einen Benutzer fuer Spring Security anhand der Login-E-Mail. */
@Service
public class BenutzerDetailsService implements UserDetailsService {

    private final MitarbeiterRepository mitarbeiterRepository;

    public BenutzerDetailsService(MitarbeiterRepository mitarbeiterRepository) {
        this.mitarbeiterRepository = mitarbeiterRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return mitarbeiterRepository.findByEmail(email)
                .map(BenutzerPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Kein Benutzer mit E-Mail " + email));
    }
}
