import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import StatusBadge from '../components/StatusBadge';
import { STATUS_LABEL, formatDatum } from '../theme';
import type { Auftrag, Status } from '../types';

type Filter = 'ALLE' | Status;

const FILTER: Filter[] = ['ALLE', 'ERFASST', 'DISPONIERT', 'AUSGEFUEHRT', 'VERRECHNET'];

const thKlasse = 'px-4 py-3 text-left text-[11.5px] font-semibold uppercase tracking-wider text-slate-500';
const tdKlasse = 'px-4 py-4 text-sm';

export default function ListPage() {
  const navigate = useNavigate();
  const { benutzer } = useAuth();
  const [filter, setFilter] = useState<Filter>('ALLE');
  const [auftraege, setAuftraege] = useState<Auftrag[]>([]);
  const [laeuft, setLaeuft] = useState(true);
  const [fehler, setFehler] = useState('');

  useEffect(() => {
    let aktiv = true;
    setLaeuft(true);
    api
      .auftraege(filter === 'ALLE' ? undefined : filter)
      .then((data) => { if (aktiv) { setAuftraege(data); setFehler(''); } })
      .catch(() => { if (aktiv) setFehler('Aufträge konnten nicht geladen werden.'); })
      .finally(() => { if (aktiv) setLaeuft(false); });
    return () => { aktiv = false; };
  }, [filter]);

  const darfErfassen = benutzer?.rolle === 'GESCHAEFTSLEITER';

  return (
    <div className="mx-auto max-w-[1180px] px-6 pb-20 pt-8">
      <div className="mb-6 flex items-end justify-between gap-4">
        <div>
          <h1 className="text-[26px] font-bold tracking-tight">Serviceaufträge</h1>
          <p className="mt-1.5 text-sm text-slate-500">
            Übersicht aller erfassten Aufträge und ihres Bearbeitungsstands.
          </p>
        </div>
        {darfErfassen && (
          <button
            onClick={() => navigate('/auftraege/neu')}
            className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-[18px] py-2.5 text-sm font-semibold text-white shadow-sm hover:brightness-95 cursor-pointer"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M12 5v14M5 12h14" stroke="#fff" strokeWidth="2.2" strokeLinecap="round" />
            </svg>
            Neuer Auftrag
          </button>
        )}
      </div>

      <div className="mb-[18px] flex flex-wrap items-center gap-2.5">
        <span className="mr-0.5 text-sm font-semibold text-slate-500">Zustand:</span>
        {FILTER.map((f) => {
          const aktiv = filter === f;
          const beschriftung = f === 'ALLE' ? 'Alle' : STATUS_LABEL[f];
          return (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`inline-flex items-center rounded-full border px-3.5 py-1.5 text-sm font-semibold transition cursor-pointer ${
                aktiv ? 'border-blue-600 bg-blue-600 text-white' : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'
              }`}
            >
              {beschriftung}
            </button>
          );
        })}
      </div>

      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <table className="w-full border-collapse">
          <thead>
            <tr className="bg-slate-50">
              <th className={thKlasse}>Auftrags-Nr.</th>
              <th className={thKlasse}>Kunde</th>
              <th className={thKlasse}>Titel</th>
              <th className={thKlasse}>Status</th>
              <th className={thKlasse}>Zugewiesen an</th>
              <th className={thKlasse}>Termin</th>
            </tr>
          </thead>
          <tbody>
            {auftraege.map((a) => (
              <tr
                key={a.id}
                onClick={() => navigate(`/auftraege/${a.id}`)}
                className="cursor-pointer border-t border-slate-100 hover:bg-blue-50/40"
              >
                <td className={`${tdKlasse} whitespace-nowrap font-mono text-[13px] font-semibold`}>{a.anzeigeNr}</td>
                <td className={`${tdKlasse} text-slate-700`}>{a.kunde.name}</td>
                <td className={`${tdKlasse} font-medium text-slate-900`}>{a.titel}</td>
                <td className={tdKlasse}><StatusBadge status={a.status} /></td>
                <td className={`${tdKlasse} text-slate-600`}>{a.zugewiesenAn?.name ?? '—'}</td>
                <td className={`${tdKlasse} whitespace-nowrap text-slate-600`}>{a.terminAm ? formatDatum(a.terminAm) : '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {!laeuft && auftraege.length === 0 && !fehler && (
          <div className="px-4 py-12 text-center text-sm text-slate-400">Keine Aufträge in diesem Zustand.</div>
        )}
        {laeuft && <div className="px-4 py-12 text-center text-sm text-slate-400">Lädt…</div>}
        {fehler && <div className="px-4 py-12 text-center text-sm text-red-600">{fehler}</div>}
      </div>
      <p className="mt-3.5 px-0.5 text-[12.5px] text-slate-400">Tipp: Zeile anklicken öffnet die Detailansicht.</p>
    </div>
  );
}
