package ch.glauser.serviceauftrag.domain;

/**
 * Berechtigungsrolle eines Mitarbeiters. Entspricht den Aktoren aus der
 * Use-Case-Analyse: Geschaeftsleiter/Administration, Bereichsleiter, Mitarbeiter.
 */
public enum Rolle {
    GESCHAEFTSLEITER,
    BEREICHSLEITER,
    MITARBEITER
}
