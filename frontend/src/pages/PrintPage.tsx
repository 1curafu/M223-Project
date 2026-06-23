import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api/client';
import StatusBadge from '../components/StatusBadge';
import { formatDatum } from '../theme';
import type { Auftrag } from '../types';

const sektion =
  'mb-2 border-b border-slate-200 pb-1.5 text-[10.5px] font-bold uppercase tracking-wider text-slate-400';
const td = 'border border-slate-200 px-2.5 py-2';
const tdLabel = `${td} w-[140px] bg-slate-50 font-semibold`;

export default function PrintPage() {
  const { id } = useParams();
  const auftragId = Number(id);
  const navigate = useNavigate();
  const [auftrag, setAuftrag] = useState<Auftrag | null>(null);

  useEffect(() => {
    let aktiv = true;
    api.auftrag(auftragId).then((a) => { if (aktiv) setAuftrag(a); }).catch(() => {});
    return () => { aktiv = false; };
  }, [auftragId]);

  if (!auftrag) {
    return <div className="p-7 text-slate-400">Lädt…</div>;
  }

  const r = auftrag.rapport;
  const druckDatum = formatDatum(new Date().toISOString());

  return (
    <div className="print-wrap min-h-screen bg-slate-200 px-5 pb-[60px] pt-7">
      <div className="no-print mx-auto mb-4 flex w-[210mm] max-w-full items-center justify-between gap-3">
        <button
          onClick={() => navigate(`/auftraege/${auftragId}`)}
          className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M15 18l-6-6 6-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          Zurück
        </button>
        <button
          onClick={() => window.print()}
          className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white hover:brightness-95 cursor-pointer"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path d="M6 9V3h12v6M6 18H4v-7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v7h-2M8 14h8v7H8v-7Z" stroke="#fff" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          Drucken
        </button>
      </div>

      <div className="a4-sheet mx-auto min-h-[297mm] w-[210mm] max-w-full bg-white p-[20mm_18mm] text-xs leading-relaxed text-slate-900 shadow-2xl">
        <div className="flex items-start justify-between border-b-2 border-slate-900 pb-4">
          <div className="flex items-start gap-3">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" className="text-blue-600">
              <path d="M12 2.5S5 9.6 5 14.4a7 7 0 0 0 14 0C19 9.6 12 2.5 12 2.5Z" fill="currentColor" />
            </svg>
            <div>
              <div className="text-[17px] font-bold tracking-tight">Glauser Illnau AG</div>
              <div className="mt-0.5 text-[11px] text-slate-600">Sanitär · Heizung · Service</div>
              <div className="mt-0.5 text-[11px] text-slate-600">Dorfstrasse 1, 8308 Illnau · 052 000 00 00 · info@glauser-illnau.ch</div>
            </div>
          </div>
          <div className="text-right">
            <div className="font-mono text-[18px] font-bold">{auftrag.anzeigeNr}</div>
            <div className="mt-1 text-[11px] text-slate-600">Datum: {druckDatum}</div>
            <div className="mt-2"><StatusBadge status={auftrag.status} schlicht /></div>
          </div>
        </div>

        <h2 className="my-[18px] mt-[22px] text-base font-bold">Serviceauftrag: {auftrag.titel}</h2>

        <div className="mb-[22px] grid grid-cols-2 gap-6">
          <div>
            <div className={sektion}>Kunde</div>
            <div className="mb-0.5 font-semibold">{auftrag.kunde.name}</div>
            <div className="text-slate-700">{auftrag.kunde.adresse || '—'}</div>
            <div className="text-slate-700">{auftrag.kunde.telefon || '—'}</div>
            <div className="text-slate-700">{auftrag.kunde.email || '—'}</div>
          </div>
          <div>
            <div className={sektion}>Ausführung</div>
            <div className="grid grid-cols-[auto_1fr] gap-x-3.5 gap-y-1.5">
              <div className="text-slate-400">Mitarbeiter</div><div className="font-medium">{auftrag.zugewiesenAn?.name ?? '—'}</div>
              <div className="text-slate-400">Termin</div><div>{auftrag.terminAm ? formatDatum(auftrag.terminAm) : '—'}</div>
              <div className="text-slate-400">Erfasst am</div><div>{formatDatum(auftrag.erfasstAm)}</div>
            </div>
          </div>
        </div>

        <div className="mb-[22px]">
          <div className={sektion}>Auftragsbeschreibung</div>
          <div className="leading-relaxed text-slate-700">{auftrag.beschreibung || '—'}</div>
        </div>

        <div className="mb-[30px]">
          <div className={sektion}>Rapport</div>
          <table className="w-full border-collapse text-xs">
            <tbody>
              <tr><td className={tdLabel}>Arbeitszeit</td><td className={td}>{r?.arbeitszeit != null ? `${r.arbeitszeit} h` : '—'}</td></tr>
              <tr><td className={tdLabel}>Material</td><td className={td}>{r?.material || '—'}</td></tr>
              <tr><td className={tdLabel}>Bemerkung</td><td className={td}>{r?.bemerkung || '—'}</td></tr>
            </tbody>
          </table>
        </div>

        <div className="mt-11 grid grid-cols-2 gap-10">
          <div><div className="border-t border-slate-900 pt-1.5 text-[11px] text-slate-600">Unterschrift Kunde</div></div>
          <div><div className="border-t border-slate-900 pt-1.5 text-[11px] text-slate-600">Unterschrift Monteur</div></div>
        </div>
      </div>
    </div>
  );
}
