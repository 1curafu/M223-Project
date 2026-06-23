package ch.glauser.serviceauftrag.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Reine Tests der erlaubten/ungueltigen Statusuebergaenge auf dem Enum. */
class StatusTest {

    @Test
    @DisplayName("Erlaubte Vorwaerts-Uebergaenge")
    void erlaubteVorwaertsUebergaenge() {
        assertThat(Status.ERFASST.darfWechselnNach(Status.DISPONIERT)).isTrue();
        assertThat(Status.DISPONIERT.darfWechselnNach(Status.AUSGEFUEHRT)).isTrue();
        assertThat(Status.AUSGEFUEHRT.darfWechselnNach(Status.VERRECHNET)).isTrue();
    }

    @Test
    @DisplayName("Rapport ablehnen: AUSGEFUEHRT zurueck nach DISPONIERT erlaubt")
    void rueckwaertsUebergangErlaubt() {
        assertThat(Status.AUSGEFUEHRT.darfWechselnNach(Status.DISPONIERT)).isTrue();
    }

    @Test
    @DisplayName("Uebersprungene Stufen sind ungueltig")
    void uebersprungeneStufenUngueltig() {
        assertThat(Status.ERFASST.darfWechselnNach(Status.AUSGEFUEHRT)).isFalse();
        assertThat(Status.ERFASST.darfWechselnNach(Status.VERRECHNET)).isFalse();
        assertThat(Status.DISPONIERT.darfWechselnNach(Status.VERRECHNET)).isFalse();
    }

    @Test
    @DisplayName("Verkehrte Rueckwaerts-Uebergaenge sind ungueltig")
    void falscheRueckwaertsUebergaengeUngueltig() {
        assertThat(Status.DISPONIERT.darfWechselnNach(Status.ERFASST)).isFalse();
        assertThat(Status.VERRECHNET.darfWechselnNach(Status.AUSGEFUEHRT)).isFalse();
    }

    @Test
    @DisplayName("VERRECHNET ist ein Endzustand")
    void verrechnetIstEndzustand() {
        assertThat(Status.VERRECHNET.erlaubteUebergaenge()).isEmpty();
    }

    @Test
    @DisplayName("Kein Uebergang auf sich selbst")
    void keinSelbstUebergang() {
        for (Status s : Status.values()) {
            assertThat(s.darfWechselnNach(s)).isFalse();
        }
    }
}
