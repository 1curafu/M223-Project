import type { Status } from './types';

export const STATUS_LABEL: Record<Status, string> = {
  ERFASST: 'Erfasst',
  DISPONIERT: 'Disponiert',
  AUSGEFUEHRT: 'Ausgeführt',
  VERRECHNET: 'Verrechnet',
};

export const STATUS_BADGE_CLASS: Record<Status, string> = {
  ERFASST: 'bg-slate-100 text-slate-600 border-slate-200',
  DISPONIERT: 'bg-blue-100 text-blue-700 border-blue-200',
  AUSGEFUEHRT: 'bg-orange-100 text-orange-700 border-orange-200',
  VERRECHNET: 'bg-green-100 text-green-700 border-green-200',
};

export function formatDatum(iso: string | null | undefined): string {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '—';
  const tag = String(d.getDate()).padStart(2, '0');
  const monat = String(d.getMonth() + 1).padStart(2, '0');
  return `${tag}.${monat}.${d.getFullYear()}`;
}

export function dateInputToIso(value: string): string {
  return `${value}T08:00:00`;
}
