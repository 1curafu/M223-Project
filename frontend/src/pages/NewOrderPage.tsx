import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { useToast } from '../components/Toast';
import KundeModal from '../components/KundeModal';
import { dateInputToIso } from '../theme';
import { btnPrimary, btnSecondary, feldKlasse, inputBase, labelKlasse } from '../ui';
import type { Kunde } from '../types';

export default function NewOrderPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [kunden, setKunden] = useState<Kunde[]>([]);
  const [kundeId, setKundeId] = useState('');
  const [titel, setTitel] = useState('');
  const [beschreibung, setBeschreibung] = useState('');
  const [termin, setTermin] = useState('');
  const [fehler, setFehler] = useState<{ kunde?: string; titel?: string; beschreibung?: string }>({});
  const [kundeModal, setKundeModal] = useState(false);
  const [laeuft, setLaeuft] = useState(false);

  useEffect(() => {
    api.kunden().then(setKunden).catch(() => setKunden([]));
  }, []);

  async function speichern() {
    const f: typeof fehler = {};
    if (!kundeId) f.kunde = 'Bitte einen Kunden auswählen.';
    if (!titel.trim()) f.titel = 'Bitte einen Titel eingeben.';
    if (!beschreibung.trim()) f.beschreibung = 'Bitte eine Beschreibung eingeben.';
    if (Object.keys(f).length) { setFehler(f); return; }

    setLaeuft(true);
    try {
      const neu = await api.erfassen({
        kundeId: Number(kundeId),
        titel: titel.trim(),
        beschreibung: beschreibung.trim(),
        terminAm: termin ? dateInputToIso(termin) : null,
      });
      showToast(`Auftrag ${neu.anzeigeNr} wurde erfasst.`);
      navigate('/');
    } catch {
      setFehler({ titel: 'Speichern fehlgeschlagen.' });
      setLaeuft(false);
    }
  }

  async function kundeErstellen(req: Parameters<typeof api.kundeErstellen>[0]) {
    const neu = await api.kundeErstellen(req);
    setKunden((k) => [...k, neu]);
    setKundeId(String(neu.id));
    setFehler((f) => ({ ...f, kunde: undefined }));
    setKundeModal(false);
    showToast(`Kunde «${neu.name}» wurde erstellt.`);
    return neu;
  }

  return (
    <div className="mx-auto max-w-[720px] px-6 pb-20 pt-7">
      <button
        onClick={() => navigate('/')}
        className="mb-3.5 inline-flex items-center gap-1.5 border-none bg-transparent py-1.5 text-sm font-semibold text-slate-500 hover:text-slate-900 cursor-pointer"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
        Zurück zur Liste
      </button>

      <div className="rounded-2xl border border-slate-200 bg-white p-[30px] shadow-sm">
        <h1 className="mb-1 text-[21px] font-bold tracking-tight">Neuen Auftrag erfassen</h1>
        <p className="mb-6 text-[13.5px] text-slate-500">
          Pflichtfelder sind mit <span className="text-red-600">*</span> markiert.
        </p>

        <div className="mb-[18px]">
          <div className="mb-2 flex items-center justify-between">
            <label className="text-sm font-semibold">
              Kunde <span className="text-red-600">*</span>
            </label>
            <button
              onClick={() => setKundeModal(true)}
              className="inline-flex items-center gap-1.5 border-none bg-transparent p-0 text-[12.5px] font-semibold text-blue-600 cursor-pointer hover:brightness-90"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M12 5v14M5 12h14" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" />
              </svg>
              Neuer Kunde
            </button>
          </div>
          <select
            value={kundeId}
            onChange={(e) => { setKundeId(e.target.value); setFehler((f) => ({ ...f, kunde: undefined })); }}
            className={feldKlasse(!!fehler.kunde)}
          >
            <option value="">Kunde auswählen…</option>
            {kunden.map((k) => (
              <option key={k.id} value={k.id}>{k.name}</option>
            ))}
          </select>
          {fehler.kunde && <div className="mt-1.5 text-[12.5px] text-red-600">{fehler.kunde}</div>}
        </div>

        <div className="mb-[18px]">
          <label className={labelKlasse}>
            Titel <span className="text-red-600">*</span>
          </label>
          <input
            value={titel}
            onChange={(e) => { setTitel(e.target.value); setFehler((f) => ({ ...f, titel: undefined })); }}
            placeholder="z. B. Wasserhahn ersetzen"
            className={feldKlasse(!!fehler.titel)}
          />
          {fehler.titel && <div className="mt-1.5 text-[12.5px] text-red-600">{fehler.titel}</div>}
        </div>

        <div className="mb-[18px]">
          <label className={labelKlasse}>
            Beschreibung <span className="text-red-600">*</span>
          </label>
          <textarea
            value={beschreibung}
            onChange={(e) => { setBeschreibung(e.target.value); setFehler((f) => ({ ...f, beschreibung: undefined })); }}
            rows={4}
            placeholder="Mehrzeilige Beschreibung der auszuführenden Arbeit…"
            className={`${feldKlasse(!!fehler.beschreibung)} resize-y leading-relaxed`}
          />
          {fehler.beschreibung && <div className="mt-1.5 text-[12.5px] text-red-600">{fehler.beschreibung}</div>}
        </div>

        <div className="mb-[26px]">
          <label className={labelKlasse}>
            Gewünschter Termin <span className="font-medium text-slate-400">(optional)</span>
          </label>
          <input
            type="date"
            value={termin}
            onChange={(e) => setTermin(e.target.value)}
            className={`${inputBase} w-[220px] max-w-full border-slate-300`}
          />
        </div>

        <div className="flex gap-3">
          <button onClick={speichern} disabled={laeuft} className={btnPrimary}>Speichern</button>
          <button onClick={() => navigate('/')} className={btnSecondary}>Abbrechen</button>
        </div>
      </div>

      {kundeModal && <KundeModal onClose={() => setKundeModal(false)} onSubmit={kundeErstellen} />}
    </div>
  );
}
