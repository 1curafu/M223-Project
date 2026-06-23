export type Rolle = 'GESCHAEFTSLEITER' | 'BEREICHSLEITER' | 'MITARBEITER';

export type Status = 'ERFASST' | 'DISPONIERT' | 'AUSGEFUEHRT' | 'VERRECHNET';

export interface LoginResponse {
  token: string;
  mitarbeiterId: number;
  name: string;
  email: string;
  rolle: Rolle;
}

export interface Kunde {
  id: number;
  name: string;
  adresse: string | null;
  telefon: string | null;
  email: string | null;
}

export interface Mitarbeiter {
  id: number;
  name: string;
  rolle: Rolle;
}

export interface MitarbeiterErstellenRequest {
  name: string;
  rolle: Rolle;
  email: string;
  passwort: string;
}

export interface Rapport {
  id: number;
  arbeitszeit: number | null;
  material: string | null;
  bemerkung: string | null;
  erstelltVon: Mitarbeiter;
  erstelltAm: string;
}

export interface Auftrag {
  id: number;
  anzeigeNr: string;
  titel: string;
  beschreibung: string | null;
  status: Status;
  erfasstAm: string;
  terminAm: string | null;
  kunde: Kunde;
  zugewiesenAn: Mitarbeiter | null;
  rapport: Rapport | null;
}

export interface AuftragErfassenRequest {
  kundeId: number;
  titel: string;
  beschreibung: string;
  terminAm: string | null;
}

export interface DisponierenRequest {
  mitarbeiterId: number;
  terminAm: string;
}

export interface RapportRequest {
  arbeitszeit: number;
  material: string;
  bemerkung: string;
}

export interface KundeRequest {
  name: string;
  adresse?: string;
  telefon?: string;
  email?: string;
}

export interface ApiError {
  zeitpunkt: string;
  status: number;
  fehler: string;
  meldung: string;
  feldFehler?: Record<string, string>;
}
