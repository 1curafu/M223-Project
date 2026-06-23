import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/Toast';
import StatusBadge from '../components/StatusBadge';
import DisponierenModal from '../components/DisponierenModal';
import RapportModal from '../components/RapportModal';
import { formatDatum } from '../theme';
import { btnPrimary } from '../ui';
import type { Auftrag, DisponierenRequest, Mitarbeiter, RapportRequest } from '../types';

const karte = 'rounded-2xl border border-slate-200 bg-white p-6 shadow-sm';
const sektionsTitel = 'mb-4 text-[11.5px] font-bold uppercase tracking-wider text-slate-400';
const labelZelle = 'text-slate-400';

export default function DetailPage() {
  const { id } = useParams();
  const auftragId = Number(id);
  const navigate = useNavigate();
  const { benutzer } = useAuth();
  const { showToast } = useToast();

  const [auftrag, setAuftrag] = useState<Auftrag | null>(null);
  const [mitarbeiter, setMitarbeiter] = useState<Mitarbeiter[]>([]);
  const [fehler, setFehler] = useState('');
  const [modal, setModal] = useState<null | 'dispo' | 'rapport'>(null);

  useEffect(() => {
    let aktiv = true;
    api
      .auftrag(auftragId)
      .then((a) => { if (aktiv) setAuftrag(a); })
      .catch(() => { if (aktiv) setFehler('Auftrag konnte nicht geladen werden.'); });
    api.mitarbeiter().then((m) => { if (aktiv) setMitarbeiter(m); }).catch(() => {});
    return () => { aktiv = false; };
  }, [auftragId]);

  if (fehler) {
    return <div className="mx-auto max-w-[920px] px-6 pt-7 text-red-600">{fehler}</div>;
  }
  if (!auftrag) {
    return <div className="mx-auto max-w-[920px] px-6 pt-7 text-slate-400">Lädt…</div>;
  }

  const rolle = benutzer?.rolle;
  const istZugewiesen = auftrag.zugewiesenAn?.id === benutzer?.mitarbeiterId;

  const canDisponieren = auftrag.status === 'ERFASST' && rolle === 'BEREICHSLEITER';
  const canAusfuehren =
    auftrag.status === 'DISPONIERT' && (rolle === 'BEREICHSLEITER' || (rolle === 'MITARBEITER' && istZugewiesen));
  const canAblehnen = auftrag.status === 'AUSGEFUEHRT' && rolle === 'BEREICHSLEITER';
  const canVerrechnen =
    auftrag.status === 'AUSGEFUEHRT' && (rolle === 'BEREICHSLEITER' || rolle === 'GESCHAEFTSLEITER');

  const zeigeRapport = auftrag.status === 'AUSGEFUEHRT' || auftrag.status === 'VERRECHNET';

  async function disponieren(req: DisponierenRequest) {
    const aktualisiert = await api.disponieren(auftragId, req);
    setAuftrag(aktualisiert);
    setModal(null);
    showToast(`${aktualisiert.anzeigeNr} wurde disponiert.`);
  }

  async function rapportieren(req: RapportRequest) {
    const aktualisiert = await api.alsAusgefuehrt(auftragId, req);
    setAuftrag(aktualisiert);
    setModal(null);
    showToast(`${aktualisiert.anzeigeNr} wurde als ausgeführt markiert.`);
  }

  async function ablehnen() {
    const aktualisiert = await api.rapportAblehnen(auftragId);
    setAuftrag(aktualisiert);
    showToast(`Rapport zu ${aktualisiert.anzeigeNr} abgelehnt – zurück auf disponiert.`);
  }

  async function verrechnen() {
    const aktualisiert = await api.alsVerrechnet(auftragId);
    setAuftrag(aktualisiert);
    showToast(`${aktualisiert.anzeigeNr} wurde als verrechnet markiert.`);
  }

  const r = auftrag.rapport;

  return (
    <div className="mx-auto max-w-[920px] px-6 pb-28 pt-7">
      <button
        onClick={() => navigate('/')}
        className="mb-4 inline-flex items-center gap-1.5 border-none bg-transparent py-1.5 text-sm font-semibold text-slate-500 hover:text-slate-900 cursor-pointer"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
        Zurück zur Liste
      </button>

      <div className="mb-1.5 flex items-center gap-3.5">
        <span className="font-mono text-[15px] font-semibold text-slate-500">{auftrag.anzeigeNr}</span>
        <StatusBadge status={auftrag.status} />
      </div>
      <h1 className="mb-[26px] text-[26px] font-bold tracking-tight">{auftrag.titel}</h1>

      <div className="mb-[18px] grid grid-cols-2 gap-[18px]">
        <div className={karte}>
          <div className={sektionsTitel}>Kundendaten</div>
          <div className="grid grid-cols-[84px_1fr] gap-x-3 gap-y-3 text-sm">
            <div className={labelZelle}>Name</div><div className="font-medium text-slate-900">{auftrag.kunde.name}</div>
            <div className={labelZelle}>Adresse</div><div className="text-slate-700">{auftrag.kunde.adresse || '—'}</div>
            <div className={labelZelle}>Telefon</div><div className="text-slate-700">{auftrag.kunde.telefon || '—'}</div>
            <div className={labelZelle}>E-Mail</div><div className="text-slate-700">{auftrag.kunde.email || '—'}</div>
          </div>
        </div>
        <div className={karte}>
          <div className={sektionsTitel}>Auftragsdetails</div>
          <div className="grid grid-cols-[96px_1fr] gap-x-3 gap-y-3 text-sm">
            <div className={labelZelle}>Beschreibung</div><div className="leading-relaxed text-slate-700">{auftrag.beschreibung || '—'}</div>
            <div className={labelZelle}>Erfasst am</div><div className="text-slate-700">{formatDatum(auftrag.erfasstAm)}</div>
            <div className={labelZelle}>Termin</div><div className="text-slate-700">{auftrag.terminAm ? formatDatum(auftrag.terminAm) : '—'}</div>
            <div className={labelZelle}>Mitarbeiter</div><div className="font-medium text-slate-900">{auftrag.zugewiesenAn?.name ?? 'noch nicht zugewiesen'}</div>
          </div>
        </div>
      </div>

      {zeigeRapport && (
        <div className={`${karte} mb-[18px]`}>
          <div className={sektionsTitel}>Rapport</div>
          <div className="grid grid-cols-3 gap-[18px] text-sm">
            <div>
              <div className={`${labelZelle} mb-1.5`}>Arbeitszeit</div>
              <div className="font-medium text-slate-900">{r?.arbeitszeit != null ? `${r.arbeitszeit} h` : '—'}</div>
            </div>
            <div>
              <div className={`${labelZelle} mb-1.5`}>Material</div>
              <div className="text-slate-700">{r?.material || '—'}</div>
            </div>
            <div>
              <div className={`${labelZelle} mb-1.5`}>Bemerkung</div>
              <div className="leading-relaxed text-slate-700">{r?.bemerkung || '—'}</div>
            </div>
          </div>
        </div>
      )}

      <div className="sticky bottom-0 mt-1.5 bg-gradient-to-t from-slate-100 from-70% to-transparent pt-[18px]">
        <div className="flex flex-wrap items-center gap-3 rounded-2xl border border-slate-200 bg-white p-[18px] shadow-lg">
          <span className="mr-0.5 text-[12.5px] font-semibold text-slate-500">Aktionen:</span>
          {canDisponieren && <button onClick={() => setModal('dispo')} className={btnPrimary}>Disponieren</button>}
          {canAusfuehren && <button onClick={() => setModal('rapport')} className={btnPrimary}>Als ausgeführt markieren</button>}
          {canAblehnen && (
            <button
              onClick={ablehnen}
              className="inline-flex items-center gap-2 rounded-lg border border-orange-200 bg-white px-[18px] py-2.5 text-[13.5px] font-semibold text-orange-700 hover:bg-orange-50 cursor-pointer"
            >
              Rapport ablehnen
            </button>
          )}
          {canVerrechnen && <button onClick={verrechnen} className={btnPrimary}>Als verrechnet markieren</button>}
          <button
            onClick={() => navigate(`/auftraege/${auftragId}/druck`)}
            className="ml-auto inline-flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-[18px] py-2.5 text-[13.5px] font-semibold text-slate-700 hover:bg-slate-50 cursor-pointer"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M6 9V3h12v6M6 18H4v-7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v7h-2M8 14h8v7H8v-7Z" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
            Auftragsdokument drucken
          </button>
        </div>
      </div>

      {modal === 'dispo' && (
        <DisponierenModal mitarbeiter={mitarbeiter} onClose={() => setModal(null)} onSubmit={disponieren} />
      )}
      {modal === 'rapport' && <RapportModal onClose={() => setModal(null)} onSubmit={rapportieren} />}
    </div>
  );
}
