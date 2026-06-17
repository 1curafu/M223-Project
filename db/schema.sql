-- ============================================================
--  Serviceauftrag - Glauser Illnau AG
--  PostgreSQL-Schema (Supabase)
--  Entspricht dem ERM aus der Projektdokumentation Teil 1.
--  Erweiterung: mitarbeiter.email + mitarbeiter.passwort_hash
--  fuer die Authentifizierung (Spring Security / JWT).
-- ============================================================

-- Bei Bedarf sauber neu aufsetzen (Reihenfolge wegen Fremdschluesseln)
DROP TABLE IF EXISTS rapport CASCADE;
DROP TABLE IF EXISTS auftrag CASCADE;
DROP TABLE IF EXISTS mitarbeiter CASCADE;
DROP TABLE IF EXISTS kunde CASCADE;

-- ------------------------------------------------------------
-- kunde : Auftraggeber
-- ------------------------------------------------------------
CREATE TABLE kunde (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    adresse VARCHAR(200),
    telefon VARCHAR(30),
    email   VARCHAR(120)
);

-- ------------------------------------------------------------
-- mitarbeiter : Personen mit Rolle + Login
-- ------------------------------------------------------------
CREATE TABLE mitarbeiter (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    rolle         VARCHAR(20)  NOT NULL
                  CHECK (rolle IN ('GESCHAEFTSLEITER', 'BEREICHSLEITER', 'MITARBEITER')),
    email         VARCHAR(120) NOT NULL UNIQUE,   -- Login-Kennung
    passwort_hash VARCHAR(100) NOT NULL           -- BCrypt-Hash, nie Klartext
);

-- ------------------------------------------------------------
-- auftrag : Serviceauftrag, durchlaeuft die Status-Maschine
-- ------------------------------------------------------------
CREATE TABLE auftrag (
    id            BIGSERIAL PRIMARY KEY,
    kunde_id      BIGINT       NOT NULL REFERENCES kunde(id),
    zugewiesen_an BIGINT       REFERENCES mitarbeiter(id),   -- NULL bis disponiert
    titel         VARCHAR(150) NOT NULL,
    beschreibung  TEXT,
    status        VARCHAR(20)  NOT NULL
                  CHECK (status IN ('ERFASST', 'DISPONIERT', 'AUSGEFUEHRT', 'VERRECHNET')),
    erfasst_am    TIMESTAMP    NOT NULL,
    termin_am     TIMESTAMP
);

CREATE INDEX idx_auftrag_status ON auftrag(status);
CREATE INDEX idx_auftrag_kunde  ON auftrag(kunde_id);

-- ------------------------------------------------------------
-- rapport : genau ein Rapport pro Auftrag (1:1)
-- ------------------------------------------------------------
CREATE TABLE rapport (
    id           BIGSERIAL PRIMARY KEY,
    auftrag_id   BIGINT        NOT NULL UNIQUE REFERENCES auftrag(id),   -- 1:1
    erstellt_von BIGINT        NOT NULL REFERENCES mitarbeiter(id),
    arbeitszeit  NUMERIC(5,2)  CHECK (arbeitszeit >= 0),
    material     TEXT,
    bemerkung    TEXT,
    erstellt_am  TIMESTAMP     NOT NULL
);
