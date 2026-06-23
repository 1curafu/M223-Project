import { useState } from 'react';
import Modal from './Modal';
import { dateInputToIso } from '../theme';
import { btnPrimary, btnSecondary, feldKlasse, labelKlasse } from '../ui';
import type { DisponierenRequest, Mitarbeiter } from '../types';

interface Props {
  mitarbeiter: Mitarbeiter[];
  onClose: () => void;
  onSubmit: (req: DisponierenRequest) => Promise<void>;
}

export default function DisponierenModal({ mitarbeiter, onClose, onSubmit }: Props) {
  const [mitarbeiterId, setMitarbeiterId] = useState('');
  const [termin, setTermin] = useState('');
  const [fehler, setFehler] = useState('');
  const [laeuft, setLaeuft] = useState(false);

  async function zuweisen() {
    if (!mitarbeiterId || !termin) {
      setFehler('Bitte Mitarbeiter und Termin angeben.');
      return;
    }
    setLaeuft(true);
    try {
      await onSubmit({ mitarbeiterId: Number(mitarbeiterId), terminAm: dateInputToIso(termin) });
    } catch {
      setFehler('Disponieren fehlgeschlagen.');
      setLaeuft(false);
    }
  }

  return (
    <Modal onClose={onClose}>
      <h2 className="mb-1 text-lg font-bold">Auftrag disponieren</h2>
      <p className="mb-6 text-sm text-slate-500">Mitarbeiter zuweisen und Termin festlegen.</p>

      <div className="mb-4">
        <label className={labelKlasse}>
          Mitarbeiter <span className="text-red-600">*</span>
        </label>
        <select
          value={mitarbeiterId}
          onChange={(e) => { setMitarbeiterId(e.target.value); setFehler(''); }}
          className={feldKlasse(!!fehler && !mitarbeiterId)}
        >
          <option value="">Mitarbeiter auswählen…</option>
          {mitarbeiter.map((m) => (
            <option key={m.id} value={m.id}>{m.name}</option>
          ))}
        </select>
      </div>

      <div className="mb-6">
        <label className={labelKlasse}>
          Termin <span className="text-red-600">*</span>
        </label>
        <input
          type="date"
          value={termin}
          onChange={(e) => { setTermin(e.target.value); setFehler(''); }}
          className={feldKlasse(!!fehler && !termin)}
        />
      </div>

      {fehler && <div className="-mt-3 mb-4 text-[12.5px] text-red-600">{fehler}</div>}

      <div className="flex justify-end gap-2.5">
        <button onClick={onClose} className={btnSecondary}>Abbrechen</button>
        <button onClick={zuweisen} disabled={laeuft} className={btnPrimary}>Zuweisen</button>
      </div>
    </Modal>
  );
}
