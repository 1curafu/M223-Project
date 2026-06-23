# Serviceauftrag – Glauser Illnau AG

Interne Webanwendung für das Sanitärunternehmen **Glauser Illnau AG**. Sie bildet den
Ablauf eines Serviceauftrags von der Annahme bis zur Verrechnung ab (Modul Webanwendung,
BWZ Rapperswil-Jona).

- **Backend:** Java 25, Spring Boot 4.1 (Spring Framework 7), Spring Data JPA, REST
- **Frontend:** React 19 + TypeScript (Vite), Tailwind CSS v4
- **Datenbank:** PostgreSQL (gehostet bei Supabase)
- **Auth:** Spring Security mit JWT (zustandslos), BCrypt
- **Sprache:** Deutsch, Schweizer Schreibweise (immer „ss")

## Projektstruktur

```
/backend    Spring-Boot-Projekt (Maven)
              src/main/java/ch/glauser/serviceauftrag
                domain/      Entities + Enums (Status, Rolle)
                repository/  Spring-Data-JPA-Repositories
                dto/         Request/Response-DTOs
                service/     Geschäftslogik inkl. Status-Maschine
                security/    JWT, Spring Security, BCrypt
                web/         REST-Controller
                exception/   Globales Exception-Handling
                config/      LocalDataSeeder (nur Profil "local")
/frontend   React/TypeScript (Vite + Tailwind)
              src/
                pages/       Login, Liste, Erfassen, Detail, Druck
                components/  TopBar, StatusBadge, Modals, Toast
                api/         typisierter API-Client
                auth/        Auth-Context (Token, Rolle)
/db         schema.sql (Tabellen) + seed.sql (Testdaten)
README.md   diese Datei
```

## Voraussetzungen

| Werkzeug | Version | Hinweis |
|----------|---------|---------|
| JDK      | 25      | `java -version` |
| Maven    | 3.9+    | optional – der Wrapper `./mvnw` genügt |
| Node.js  | 20+     | inkl. npm, für das Frontend |
| Supabase | –       | nur für den Betrieb gegen die Cloud-DB nötig |

---

## Installation der Werkzeuge

> **Hinweis zu „Spring":** Spring bzw. Spring Boot wird **nicht separat installiert.**
> Es besteht aus Maven-Abhängigkeiten, die beim ersten Build automatisch aus dem Internet
> geladen werden. Man braucht daher nur ein **JDK** und ein **Build-Tool** (Maven – oder den
> mitgelieferten Wrapper `./mvnw`, der gar keine globale Maven-Installation verlangt).
> Analog wird **Tailwind** über `npm install` mitgezogen, nicht von Hand installiert.

### macOS (mit [Homebrew](https://brew.sh))

```bash
# JDK 25 (Temurin)
brew install --cask temurin@25

# Node.js (inkl. npm)
brew install node

# Maven – optional, der Wrapper ./mvnw genügt sonst
brew install maven
```

### Windows

```powershell
# mit winget (in PowerShell)
winget install EclipseAdoptium.Temurin.25.JDK
winget install OpenJS.NodeJS.LTS
winget install Apache.Maven        # optional
```

Alternativ die Installer von [adoptium.net](https://adoptium.net) (JDK) und
[nodejs.org](https://nodejs.org) herunterladen.

### Linux (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install -y openjdk-25-jdk maven
# Node.js 20 LTS über NodeSource
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
```

### Installation prüfen

```bash
java -version     # erwartet: 25.x
node -v           # erwartet: v20+ (oder neuer)
npm -v
mvn -version      # nur falls global installiert; sonst ./mvnw verwenden
```

> Falls `java -version` eine ältere Version zeigt, JDK 25 als Standard setzen
> (`JAVA_HOME` auf das JDK-25-Verzeichnis legen).

### Supabase-Projekt anlegen (nur für den Cloud-DB-Betrieb)

1. Kostenloses Konto auf <https://supabase.com> erstellen.
2. **New project** → Name vergeben, Region (z. B. *Zurich / eu-central-2*) und ein
   **Database-Passwort** wählen (notieren – wird für `.env` gebraucht).
3. Warten, bis das Projekt den Status *Active* hat.
4. Schema und Testdaten einspielen sowie `.env` ausfüllen – siehe
   [Betrieb gegen Supabase](#betrieb-gegen-supabase-echte-postgresql-db).

Eine lokale Installation von PostgreSQL ist **nicht** nötig – die Datenbank läuft in der
Supabase-Cloud. Zum schnellen Ausprobieren ganz ohne Supabase siehe Schnellstart unten.

---

## Schnellstart (ohne Supabase) – empfohlen zum Ausprobieren

Das Backend bringt ein **`local`-Profil** mit: In-Memory-DB (H2), die beim Start
automatisch mit Testdaten (inkl. Logins) befüllt wird. Es sind **keine** Zugangsdaten nötig.

```bash
# 1) Backend (Terminal 1)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 2) Frontend (Terminal 2)
cd frontend
npm install
npm run dev
```

- Frontend: <http://localhost:5173>
- Backend:  <http://localhost:8080>

---

## Betrieb gegen Supabase (echte PostgreSQL-DB)

### 1. Datenbank vorbereiten

Schema und Testdaten in das Supabase-Projekt einspielen – im **SQL Editor** des
Dashboards nacheinander `db/schema.sql` und `db/seed.sql` ausführen, oder per `psql`:

```bash
psql "<connection-string>" -f db/schema.sql
psql "<connection-string>" -f db/seed.sql
```

> Für das aktuelle Projekt ist das bereits geschehen.

### 2. Zugangsdaten setzen (`.env`)

Es liegen **keine** Secrets im Repo. Im Ordner `backend/` eine `.env` aus der Vorlage anlegen:

```bash
cd backend
cp .env.example .env
```

Dann in `.env` eintragen (Werte aus dem Supabase-Dashboard → **Settings → Database →
Connection string → Tab „Session pooler"**; DB-Passwort dort ggf. über
„Reset database password" setzen):

```bash
SUPABASE_DB_URL=jdbc:postgresql://<host>.pooler.supabase.com:5432/postgres?sslmode=require
SUPABASE_DB_USER=postgres.<project-ref>
SUPABASE_DB_PASSWORD=<dein-db-passwort>
JWT_SECRET=<langes-zufaelliges-geheimnis-min-32-zeichen>
```

> **Session pooler** verwenden (IPv4-tauglich, Port 5432). Der Benutzer lautet dann
> `postgres.<project-ref>`. Die direkte Verbindung (`db.<ref>.supabase.co`) ist auf dem
> Free-Tier nur über IPv6 erreichbar.

### 3. Starten

```bash
cd backend
set -a; source .env; set +a      # Variablen laden
./mvnw spring-boot:run            # ohne Profil -> nutzt Supabase
```

Frontend wie oben mit `npm run dev`.

---

## Test-Logins

Alle Testbenutzer haben das Passwort **`test1234`** (nur für die lokale Entwicklung):

| Rolle             | E-Mail            |
|-------------------|-------------------|
| Geschäftsleiter   | `gl@glauser.ch`   |
| Bereichsleiter    | `bl@glauser.ch`   |
| Mitarbeiter       | `ma@glauser.ch`   |
| Mitarbeiter       | `ma2@glauser.ch`  |

Login über die Login-Seite bzw. `POST /api/auth/login`
(`{ "email": "...", "passwort": "test1234" }`). Die Antwort enthält ein JWT, das bei allen
weiteren Aufrufen als Header `Authorization: Bearer <token>` mitgeschickt wird.

## Mitarbeiter / Logins anlegen

Die Anwendung hat **keine** offene Selbstregistrierung. Neue Benutzer (Mitarbeiter mit
Login und Rolle) werden auf einem von zwei Wegen angelegt – das Passwort wird in beiden
Fällen als **BCrypt-Hash** gespeichert, nie im Klartext.

**1. Über die Oberfläche (empfohlen):**
Als **Geschäftsleiter** einloggen → in der Kopfzeile auf **„Mitarbeiter"** → Formular
„Neuen Mitarbeiter anlegen" (Name, Rolle, E-Mail, Passwort). Das Backend hasht das
Passwort selbst. Andere Rollen sehen diesen Bereich nicht und werden serverseitig mit
HTTP 403 abgewiesen.

**2. Direkt in der Datenbank** (z. B. für den allerersten Geschäftsleiter):
Ein Passwort kann nicht im Klartext eingetragen werden – zuerst einen BCrypt-Hash
erzeugen, dann einfügen:

```bash
# BCrypt-Hash für ein Passwort erzeugen (Beispiel: test1234)
htpasswd -bnBC 10 "" "test1234" | tr -d ':\n'
```

```sql
INSERT INTO mitarbeiter (name, rolle, email, passwort_hash)
VALUES ('N. Neumann', 'MITARBEITER', 'nn@glauser.ch', '<bcrypt-hash>');
```

Die Spalte `rolle` (`GESCHAEFTSLEITER` | `BEREICHSLEITER` | `MITARBEITER`) ist zugleich die
Berechtigungsrolle – eine separate Rollen-Tabelle gibt es nicht.

## Tests ausführen

```bash
cd backend
./mvnw test
```

Enthalten: Unit-Tests der Statusübergangs-Logik (gültige/ungültige Übergänge, Rapport
ablehnen) und Integrationstests der REST-Endpunkte inkl. Zugriffskontrolle (401 ohne
Login, 403 bei fehlender Berechtigung). Die Tests nutzen eine In-Memory-DB (H2) und
benötigen **keine** Supabase-Zugangsdaten.

## Rollen & Berechtigungen (serverseitig durchgesetzt)

| Aktion                                   | Erlaubte Rolle(n) |
|------------------------------------------|-------------------|
| Auftrag erfassen                         | Geschäftsleiter |
| Auftrag disponieren (zuweisen + Termin)  | Bereichsleiter |
| Als ausgeführt markieren / rapportieren  | zugewiesener Mitarbeiter, Bereichsleiter |
| Rapport ablehnen (zurück auf disponiert) | Bereichsleiter |
| Als verrechnet markieren                 | Bereichsleiter, Geschäftsleiter |
| Mitarbeiter / Login anlegen              | Geschäftsleiter |
| Liste / Detail / Druckansicht ansehen    | alle |

Das Frontend blendet Buttons zusätzlich nach Rolle aus (UX), die echte Prüfung erfolgt
aber im Backend (`@PreAuthorize`, HTTP 403 bei Verstoss).

## REST-Endpunkte (Auszug)

| Methode | Pfad | Zweck |
|---------|------|-------|
| `POST`  | `/api/auth/login` | Login, JWT ausstellen |
| `GET`   | `/api/auftraege?status=...` | Liste, optional nach Status gefiltert |
| `GET`   | `/api/auftraege/{id}` | Detailansicht |
| `POST`  | `/api/auftraege` | Auftrag erfassen |
| `PUT`   | `/api/auftraege/{id}/disponieren` | Mitarbeiter zuweisen + Termin |
| `PUT`   | `/api/auftraege/{id}/ausgefuehrt` | Rapport erfassen, Status → ausgeführt |
| `PUT`   | `/api/auftraege/{id}/rapport-ablehnen` | zurück auf disponiert |
| `PUT`   | `/api/auftraege/{id}/verrechnet` | Status → verrechnet |
| `GET`   | `/api/kunden`, `POST /api/kunden` | Kunden lesen / anlegen |
| `GET`   | `/api/mitarbeiter` | Mitarbeiter (für Dispositions-Dropdown) |
| `POST`  | `/api/mitarbeiter` | Mitarbeiter mit Login anlegen (nur Geschäftsleiter) |

## Statuslogik (serverseitig)

```
ERFASST ──disponieren──▶ DISPONIERT ──ausgeführt──▶ AUSGEFUEHRT ──verrechnen──▶ VERRECHNET
                              ▲                          │
                              └──── Rapport ablehnen ────┘
```

Jeder Übergang prüft den genauen Quellzustand; ungültige Übergänge lehnt das Backend mit
HTTP 409 ab (nicht nur im UI ausgeblendet). Rapport-Daten dürfen nur beim Übergang nach
AUSGEFUEHRT gesetzt werden.

## Datenmodell

| Tabelle | Felder (Kurzform) |
|---------|-------------------|
| `kunde` | id, name, adresse, telefon, email |
| `mitarbeiter` | id, name, rolle (Enum), email (UNIQUE), passwort_hash (BCrypt) |
| `auftrag` | id, kunde_id (FK), zugewiesen_an (FK, NULL bis disponiert), titel, beschreibung, status (Enum), erfasst_am, termin_am |
| `rapport` | id, auftrag_id (FK, UNIQUE → 1:1), erstellt_von (FK), arbeitszeit, material, bemerkung, erstellt_am |

Ein Kunde hat mehrere Aufträge (1:n); ein Auftrag besitzt höchstens einen Rapport (1:1).
Die Anzeige-Auftragsnummer (z. B. `SA-2038`) wird aus der id abgeleitet.
