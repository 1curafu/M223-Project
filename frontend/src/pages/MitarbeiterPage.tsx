import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, HttpError } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/Toast';
import { btnPrimary, feldKlasse, labelKlasse } from '../ui';
import type { Mitarbeiter, Rolle } from '../types';

const ROLLE_LABEL: Record<Rolle, string> = {
  GESCHAEFTSLEITER: 'Geschäftsleiter',
  BEREICHSLEITER: 'Bereichsleiter',
  MITARBEITER: 'Mitarbeiter',
};

export default function MitarbeiterPage() {
  const navigate = useNavigate();
  const { benutzer } = useAuth();
  const { showToast } = useToast();

  const [liste, setListe] = useState<Mitarbeiter[]>([]);
  const [name, setName] = useState('');
  const [rolle, setRolle] = useState<Rolle>('MITARBEITER');
  const [email, setEmail] = useState('');
  const [passwort, setPasswort] = useState('');
  const [fehler, setFehler] = useState<{ name?: string; email?: string; passwort?: string; allgemein?: string }>({});
  const [laeuft, setLaeuft] = useState(false);

  // Nur Geschaeftsleiter darf diese Seite nutzen.
  useEffect(() => {
    if (benutzer && benutzer.rolle !== 'GESCHAEFTSLEITER') navigate('/', { replace: true });
  }, [benutzer, navigate]);

  function laden() {
    api.mitarbeiter().then(setListe).catch(() => setListe([]));
  }
  useEffect(laden, []);

  async function anlegen() {
    const f: typeof fehler = {};
    if (!name.trim()) f.name = 'Bitte einen Namen eingeben.';
    if (!email.trim()) f.email = 'Bitte eine E-Mail eingeben.';
    if (passwort.length < 8) f.passwort = 'Passwort muss mindestens 8 Zeichen lang sein.';
    if (Object.keys(f).length) { setFehler(f); return; }

    setLaeuft(true);
    try {
      const neu = await api.mitarbeiterErstellen({ name: name.trim(), rolle, email: email.trim(), passwort });
      showToast(`Mitarbeiter «${neu.name}» wurde angelegt.`);
      setName(''); setEmail(''); setPasswort(''); setRolle('MITARBEITER'); setFehler({});
      laden();
    } catch (err) {
      if (err instanceof HttpError && err.status === 400) {
        setFehler({ email: err.body?.meldung ?? 'Eingaben ungültig.' });
      } else {
        setFehler({ allgemein: 'Anlegen fehlgeschlagen.' });
      }
    } finally {
      setLaeuft(false);
    }
  }

  return (
    <div className="mx-auto max-w-[820px] px-6 pb-20 pt-7">
      <button
        onClick={() => navigate('/')}
        className="mb-3.5 inline-flex items-center gap-1.5 border-none bg-transparent py-1.5 text-sm font-semibold text-slate-500 hover:text-slate-900 cursor-pointer"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
        Zurück zur Liste
      </button>

      <h1 className="mb-1 text-[26px] font-bold tracking-tight">Mitarbeiter</h1>
      <p className="mb-6 text-sm text-slate-500">Benutzerkonten verwalten und neue Logins anlegen.</p>

      {/* Anlegen-Formular */}
      <div className="mb-7 rounded-2xl border border-slate-200 bg-white p-[30px] shadow-sm">
        <h2 className="mb-5 text-[17px] font-bold">Neuen Mitarbeiter anlegen</h2>

        <div className="mb-4 grid grid-cols-2 gap-3.5">
          <div>
            <label className={labelKlasse}>Name <span className="text-red-600">*</span></label>
            <input
              value={name}
              onChange={(e) => { setName(e.target.value); setFehler((f) => ({ ...f, name: undefined })); }}
              placeholder="z. B. N. Neumann"
              className={feldKlasse(!!fehler.name)}
            />
            {fehler.name && <div className="mt-1.5 text-[12.5px] text-red-600">{fehler.name}</div>}
          </div>
          <div>
            <label className={labelKlasse}>Rolle <span className="text-red-600">*</span></label>
            <select value={rolle} onChange={(e) => setRolle(e.target.value as Rolle)} className={feldKlasse(false)}>
              <option value="MITARBEITER">Mitarbeiter</option>
              <option value="BEREICHSLEITER">Bereichsleiter</option>
              <option value="GESCHAEFTSLEITER">Geschäftsleiter</option>
            </select>
          </div>
        </div>

        <div className="mb-4 grid grid-cols-2 gap-3.5">
          <div>
            <label className={labelKlasse}>E-Mail <span className="text-red-600">*</span></label>
            <input
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setFehler((f) => ({ ...f, email: undefined })); }}
              placeholder="vorname@glauser.ch"
              className={feldKlasse(!!fehler.email)}
            />
            {fehler.email && <div className="mt-1.5 text-[12.5px] text-red-600">{fehler.email}</div>}
          </div>
          <div>
            <label className={labelKlasse}>Passwort <span className="text-red-600">*</span></label>
            <input
              type="password"
              value={passwort}
              onChange={(e) => { setPasswort(e.target.value); setFehler((f) => ({ ...f, passwort: undefined })); }}
              placeholder="mind. 8 Zeichen"
              className={feldKlasse(!!fehler.passwort)}
            />
            {fehler.passwort && <div className="mt-1.5 text-[12.5px] text-red-600">{fehler.passwort}</div>}
          </div>
        </div>

        {fehler.allgemein && <div className="mb-4 text-[12.5px] text-red-600">{fehler.allgemein}</div>}

        <button onClick={anlegen} disabled={laeuft} className={btnPrimary}>Mitarbeiter anlegen</button>
      </div>

      {/* Bestehende Mitarbeiter */}
      <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
        <table className="w-full border-collapse">
          <thead>
            <tr className="bg-slate-50">
              <th className="px-4 py-3 text-left text-[11.5px] font-semibold uppercase tracking-wider text-slate-500">Name</th>
              <th className="px-4 py-3 text-left text-[11.5px] font-semibold uppercase tracking-wider text-slate-500">Rolle</th>
            </tr>
          </thead>
          <tbody>
            {liste.map((m) => (
              <tr key={m.id} className="border-t border-slate-100">
                <td className="px-4 py-3.5 text-sm font-medium text-slate-900">{m.name}</td>
                <td className="px-4 py-3.5 text-sm text-slate-600">{ROLLE_LABEL[m.rolle]}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
