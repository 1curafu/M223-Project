-- ============================================================
--  Seed-Daten (Testdaten) - Serviceauftrag Glauser Illnau AG
--  Nur fuer lokale Entwicklung / Demo.
--  Passwort aller Test-Logins: test1234   (BCrypt-Hash unten)
-- ============================================================

-- ---------- Kunden ----------
INSERT INTO kunde (id, name, adresse, telefon, email) VALUES
    (1, 'M. Müller',   'Dorfstrasse 12, 8308 Illnau',        '052 000 00 00', 'mueller@example.ch'),
    (2, 'Hauser GmbH', 'Industriestrasse 5, 8307 Effretikon','052 111 11 11', 'info@hauser.ch'),
    (3, 'K. Brunner',  'Bahnhofweg 3, 8400 Winterthur',      '052 222 22 22', 'k.brunner@example.ch'),
    (4, 'Schmid AG',   'Werkstrasse 20, 8404 Winterthur',    '052 333 33 33', 'kontakt@schmid-ag.ch'),
    (5, 'L. Weber',    'Seeweg 8, 8610 Uster',               '052 444 44 44', 'l.weber@example.ch');

-- ---------- Mitarbeiter (mit Login) ----------
-- Alle nutzen das Testpasswort "test1234".
INSERT INTO mitarbeiter (id, name, rolle, email, passwort_hash) VALUES
    (1, 'A. Glauser', 'GESCHAEFTSLEITER', 'gl@glauser.ch',  '$2y$10$KcP/dFTgx7H.0dI4wbVWM.I9wlhYHXaDAFJV04IwHZYDE7j6APZFW'),
    (2, 'R. Frei',    'BEREICHSLEITER',   'bl@glauser.ch',  '$2y$10$KcP/dFTgx7H.0dI4wbVWM.I9wlhYHXaDAFJV04IwHZYDE7j6APZFW'),
    (3, 'P. Steiner', 'MITARBEITER',      'ma@glauser.ch',  '$2y$10$KcP/dFTgx7H.0dI4wbVWM.I9wlhYHXaDAFJV04IwHZYDE7j6APZFW'),
    (4, 'M. Keller',  'MITARBEITER',      'ma2@glauser.ch', '$2y$10$KcP/dFTgx7H.0dI4wbVWM.I9wlhYHXaDAFJV04IwHZYDE7j6APZFW');

-- ---------- Auftraege (verschiedene Status) ----------
-- Anzeige-Nr. im Frontend = "SA-" + (2037 + id)
INSERT INTO auftrag (id, kunde_id, zugewiesen_an, titel, beschreibung, status, erfasst_am, termin_am) VALUES
    (1, 1, 3, 'Wasserhahn ersetzen',     'Alten Mischer in der Küche demontieren, neuen montieren.',        'AUSGEFUEHRT', TIMESTAMP '2026-06-12 09:15:00', TIMESTAMP '2026-06-18 08:00:00'),
    (2, 2, 4, 'Heizung entlüften',       'Heizkörper im 1. OG entlüften, Druck prüfen.',                    'DISPONIERT',  TIMESTAMP '2026-06-13 10:30:00', TIMESTAMP '2026-06-19 13:00:00'),
    (3, 3, NULL, 'WC-Spülung defekt',    'Spülkasten läuft nach, Kunde meldet tropfendes Geräusch.',        'ERFASST',     TIMESTAMP '2026-06-16 14:45:00', NULL),
    (4, 4, 3, 'Boiler-Service jährlich', 'Jahreswartung Boiler inkl. Entkalkung und Funktionsprüfung.',     'VERRECHNET',  TIMESTAMP '2026-06-05 08:00:00', TIMESTAMP '2026-06-10 09:00:00'),
    (5, 5, 4, 'Rohrbruch Keller',        'Wasseraustritt an Steigleitung im Keller, dringend.',             'DISPONIERT',  TIMESTAMP '2026-06-14 07:50:00', TIMESTAMP '2026-06-17 07:30:00'),
    (6, 1, 3, 'Spülkasten justieren',    'Schwimmer neu einstellen, Dichtung ersetzen.',                    'AUSGEFUEHRT', TIMESTAMP '2026-06-11 11:00:00', TIMESTAMP '2026-06-16 10:00:00');

-- ---------- Rapporte (nur fuer ausgefuehrte/verrechnete Auftraege) ----------
INSERT INTO rapport (id, auftrag_id, erstellt_von, arbeitszeit, material, bemerkung, erstellt_am) VALUES
    (1, 1, 3, 2.50, '1× Mischbatterie, Dichtungen',  'Eckventil leicht verkalkt, gereinigt.', TIMESTAMP '2026-06-18 10:30:00'),
    (2, 4, 3, 1.50, 'Service-Kit Boiler, Entkalker', 'Jahreswartung ohne Befund, Anlage ok.', TIMESTAMP '2026-06-10 10:15:00'),
    (3, 6, 3, 0.75, 'Spülkasten-Dichtung',           'Schwimmer justiert, dicht.',            TIMESTAMP '2026-06-16 11:40:00');

-- ---------- Sequenzen auf den naechsten freien Wert setzen ----------
SELECT setval('kunde_id_seq',       (SELECT MAX(id) FROM kunde));
SELECT setval('mitarbeiter_id_seq', (SELECT MAX(id) FROM mitarbeiter));
SELECT setval('auftrag_id_seq',     (SELECT MAX(id) FROM auftrag));
SELECT setval('rapport_id_seq',     (SELECT MAX(id) FROM rapport));
