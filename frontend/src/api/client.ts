import type {
  ApiError,
  Auftrag,
  AuftragErfassenRequest,
  DisponierenRequest,
  Kunde,
  KundeRequest,
  LoginResponse,
  Mitarbeiter,
  MitarbeiterErstellenRequest,
  RapportRequest,
  Status,
} from '../types';

const TOKEN_KEY = 'sc_token';

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

export class HttpError extends Error {
  status: number;
  body?: ApiError;

  constructor(status: number, body?: ApiError) {
    super(body?.meldung ?? `HTTP ${status}`);
    this.status = status;
    this.body = body;
  }
}

async function request<T>(
  method: string,
  pfad: string,
  body?: unknown,
): Promise<T> {
  const headers: Record<string, string> = {};
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;
  if (body !== undefined) headers['Content-Type'] = 'application/json';

  const res = await fetch(`/api${pfad}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    let fehler: ApiError | undefined;
    try {
      fehler = (await res.json()) as ApiError;
    } catch {
    }
    throw new HttpError(res.status, fehler);
  }

  if (res.status === 204) return undefined as T;
  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}

export const api = {
  login: (email: string, passwort: string) =>
    request<LoginResponse>('POST', '/auth/login', { email, passwort }),

  auftraege: (status?: Status) =>
    request<Auftrag[]>('GET', status ? `/auftraege?status=${status}` : '/auftraege'),

  auftrag: (id: number) => request<Auftrag>('GET', `/auftraege/${id}`),

  erfassen: (req: AuftragErfassenRequest) =>
    request<Auftrag>('POST', '/auftraege', req),

  disponieren: (id: number, req: DisponierenRequest) =>
    request<Auftrag>('PUT', `/auftraege/${id}/disponieren`, req),

  alsAusgefuehrt: (id: number, req: RapportRequest) =>
    request<Auftrag>('PUT', `/auftraege/${id}/ausgefuehrt`, req),

  rapportAblehnen: (id: number) =>
    request<Auftrag>('PUT', `/auftraege/${id}/rapport-ablehnen`),

  alsVerrechnet: (id: number) =>
    request<Auftrag>('PUT', `/auftraege/${id}/verrechnet`),

  kunden: () => request<Kunde[]>('GET', '/kunden'),

  kundeErstellen: (req: KundeRequest) => request<Kunde>('POST', '/kunden', req),

  mitarbeiter: () => request<Mitarbeiter[]>('GET', '/mitarbeiter'),

  mitarbeiterErstellen: (req: MitarbeiterErstellenRequest) =>
    request<Mitarbeiter>('POST', '/mitarbeiter', req),
};
