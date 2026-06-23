// Wiederverwendbare Tailwind-Klassen-Strings, damit sich Formulare/Buttons nicht wiederholen.

export const inputBase =
  'w-full rounded-lg border bg-white px-3 py-2.5 text-sm text-slate-900 outline-none';

/** Eingabefeld-Klassen; bei Fehler roter Rahmen. */
export function feldKlasse(fehlerhaft: boolean): string {
  return `${inputBase} ${fehlerhaft ? 'border-red-600' : 'border-slate-300'}`;
}

export const labelKlasse = 'block text-sm font-semibold mb-2';

export const btnPrimary =
  'inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white cursor-pointer hover:brightness-95 disabled:opacity-70 disabled:cursor-default';

export const btnSecondary =
  'inline-flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 cursor-pointer hover:bg-slate-50';

export const karte = 'rounded-2xl border border-slate-200 bg-white shadow-sm';

export const fehlerText = 'mt-1.5 text-[12.5px] text-red-600';
