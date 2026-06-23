import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { HttpError } from '../api/client';
import { feldKlasse } from '../ui';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [passwort, setPasswort] = useState('');
  const [fehler, setFehler] = useState('');
  const [laeuft, setLaeuft] = useState(false);

  async function anmelden(e: FormEvent) {
    e.preventDefault();
    if (!email.trim() || !passwort.trim()) {
      setFehler('Bitte E-Mail und Passwort eingeben.');
      return;
    }
    setLaeuft(true);
    setFehler('');
    try {
      await login(email.trim(), passwort);
      navigate('/', { replace: true });
    } catch (err) {
      if (err instanceof HttpError && err.status === 401) {
        setFehler('E-Mail oder Passwort ist falsch.');
      } else {
        setFehler('Anmeldung fehlgeschlagen. Läuft das Backend?');
      }
    } finally {
      setLaeuft(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[radial-gradient(120%_100%_at_50%_0,#eff6ff_0,#f1f5f9_55%)] p-6">
      <div className="w-full max-w-[400px]">
        <div className="mb-7 flex flex-col items-center">
          <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl border border-slate-200 bg-white shadow-md">
            <svg width="30" height="30" viewBox="0 0 24 24" fill="none" className="text-blue-600">
              <path d="M12 2.5S5 9.6 5 14.4a7 7 0 0 0 14 0C19 9.6 12 2.5 12 2.5Z" fill="currentColor" />
            </svg>
          </div>
          <div className="text-lg font-bold tracking-tight">Glauser Illnau AG</div>
          <div className="mt-1 text-sm text-slate-500">Service-Cockpit · Anmeldung</div>
        </div>

        <form onSubmit={anmelden} className="rounded-2xl border border-slate-200 bg-white p-7 shadow-lg">
          <div className="mb-4">
            <label className="mb-2 block text-sm font-semibold">E-Mail</label>
            <input
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setFehler(''); }}
              placeholder="z. B. gl@glauser.ch"
              className={feldKlasse(false)}
            />
          </div>
          <div className="mb-5">
            <label className="mb-2 block text-sm font-semibold">Passwort</label>
            <input
              type="password"
              value={passwort}
              onChange={(e) => { setPasswort(e.target.value); setFehler(''); }}
              placeholder="••••••••"
              className={feldKlasse(false)}
            />
          </div>
          {fehler && <div className="-mt-2.5 mb-4 text-[12.5px] text-red-600">{fehler}</div>}
          <button
            type="submit"
            disabled={laeuft}
            className="w-full rounded-lg bg-blue-600 py-3 text-sm font-semibold text-white hover:brightness-95 disabled:opacity-70 cursor-pointer"
          >
            {laeuft ? 'Anmelden…' : 'Anmelden'}
          </button>
          <div className="mt-3.5 text-center text-xs text-slate-400">
            Test-Login: gl@glauser.ch / bl@glauser.ch / ma@glauser.ch · Passwort test1234
          </div>
        </form>
      </div>
    </div>
  );
}
