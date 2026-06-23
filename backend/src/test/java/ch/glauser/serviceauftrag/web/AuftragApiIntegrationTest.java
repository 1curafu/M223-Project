package ch.glauser.serviceauftrag.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.glauser.serviceauftrag.domain.Auftrag;
import ch.glauser.serviceauftrag.domain.Kunde;
import ch.glauser.serviceauftrag.domain.Mitarbeiter;
import ch.glauser.serviceauftrag.domain.Rolle;
import ch.glauser.serviceauftrag.repository.AuftragRepository;
import ch.glauser.serviceauftrag.repository.KundeRepository;
import ch.glauser.serviceauftrag.repository.MitarbeiterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.web.FilterChainProxy;

@SpringBootTest
class AuftragApiIntegrationTest {

    @Autowired private WebApplicationContext context;
    @Autowired private FilterChainProxy springSecurityFilterChain;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private KundeRepository kundeRepository;
    @Autowired private MitarbeiterRepository mitarbeiterRepository;
    @Autowired private AuftragRepository auftragRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private Long erfassterAuftragId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();

        auftragRepository.deleteAll();
        mitarbeiterRepository.deleteAll();
        kundeRepository.deleteAll();

        String hash = passwordEncoder.encode("test1234");
        mitarbeiterRepository.save(new Mitarbeiter("A. Glauser", Rolle.GESCHAEFTSLEITER, "gl@glauser.ch", hash));
        mitarbeiterRepository.save(new Mitarbeiter("R. Frei", Rolle.BEREICHSLEITER, "bl@glauser.ch", hash));
        mitarbeiterRepository.save(new Mitarbeiter("P. Steiner", Rolle.MITARBEITER, "ma@glauser.ch", hash));

        Kunde kunde = kundeRepository.save(new Kunde("Testkunde", "Adresse", "012", "k@example.ch"));
        Auftrag auftrag = auftragRepository.save(new Auftrag(kunde, "Testauftrag", "Beschreibung", null));
        erfassterAuftragId = auftrag.getId();
    }

    private String login(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "passwort", "test1234"))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @DisplayName("Ohne Token wird /api/auftraege mit 401 abgelehnt")
    void ohneTokenAbgelehnt() throws Exception {
        mockMvc.perform(get("/api/auftraege"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login mit falschem Passwort -> 401")
    void loginFalschesPasswort() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "gl@glauser.ch", "passwort", "falsch"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Mit gueltigem Token darf die Liste gelesen werden")
    void listeMitToken() throws Exception {
        String token = login("ma@glauser.ch");
        mockMvc.perform(get("/api/auftraege").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Mitarbeiter darf nicht disponieren -> 403")
    void mitarbeiterDarfNichtDisponieren() throws Exception {
        String token = login("ma@glauser.ch");
        Long blId = mitarbeiterRepository.findByEmail("bl@glauser.ch").orElseThrow().getId();

        mockMvc.perform(put("/api/auftraege/" + erfassterAuftragId + "/disponieren")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("mitarbeiterId", blId, "terminAm", "2026-06-20T09:00:00"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Geschaeftsleiter darf einen Auftrag erfassen -> 201")
    void geschaeftsleiterDarfErfassen() throws Exception {
        String token = login("gl@glauser.ch");
        Long kundeId = kundeRepository.findAll().get(0).getId();

        mockMvc.perform(post("/api/auftraege")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "kundeId", kundeId,
                                "titel", "Neuer Auftrag",
                                "beschreibung", "Beschreibung"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ERFASST"));
    }

    @Test
    @DisplayName("Mitarbeiter darf nicht erfassen -> 403")
    void mitarbeiterDarfNichtErfassen() throws Exception {
        String token = login("ma@glauser.ch");
        Long kundeId = kundeRepository.findAll().get(0).getId();

        mockMvc.perform(post("/api/auftraege")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "kundeId", kundeId,
                                "titel", "Neuer Auftrag",
                                "beschreibung", "Beschreibung"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Geschaeftsleiter darf einen Mitarbeiter anlegen -> 201")
    void geschaeftsleiterDarfMitarbeiterAnlegen() throws Exception {
        String token = login("gl@glauser.ch");

        mockMvc.perform(post("/api/mitarbeiter")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "N. Neumann",
                                "rolle", "MITARBEITER",
                                "email", "nn@glauser.ch",
                                "passwort", "geheim12"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rolle").value("MITARBEITER"));

        assertThat(mitarbeiterRepository.findByEmail("nn@glauser.ch")).isPresent();
        // Passwort muss gehasht sein, nicht im Klartext
        assertThat(mitarbeiterRepository.findByEmail("nn@glauser.ch").orElseThrow().getPasswortHash())
                .isNotEqualTo("geheim12");
    }

    @Test
    @DisplayName("Mitarbeiter darf keinen Mitarbeiter anlegen -> 403")
    void mitarbeiterDarfKeinenMitarbeiterAnlegen() throws Exception {
        String token = login("ma@glauser.ch");

        mockMvc.perform(post("/api/mitarbeiter")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "X. Test",
                                "rolle", "MITARBEITER",
                                "email", "x@glauser.ch",
                                "passwort", "geheim12"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Datenbestand ist nach Setup vorhanden")
    void setupVorhanden() {
        assertThat(mitarbeiterRepository.count()).isEqualTo(3);
        assertThat(auftragRepository.count()).isEqualTo(1);
    }
}
