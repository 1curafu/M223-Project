import { useState } from 'react';
import Modal from './Modal';
import { btnPrimary, btnSecondary, feldKlasse, labelKlasse } from '../ui';
import type { RapportRequest } from '../types';

interface Props {
  onClose: () => void;
  onSubmit: (req: RapportRequest) => Promise<void>;
}

export default function RapportModal({ onClose, onSubmit }: Props) {
  const [arbeitszeit, setArbeitszeit] = useState('');
  const [material, setMaterial] = useState('');
  const [bemerkung, setBemerkung] = useState('');
  const [fehler, setFehler] = useState('');
  const [laeuft, setLaeuft] = useState(false);

  async function speichern() {
    const zeit = arbeitszeit.trim().replace(',', '.').replace(/\s*h$/i, '');
    const zahl = Number(zeit);
    if (!arbeitszeit.trim() || !material.trim()) {
      setFehler('Bitte Arbeitszeit und Material angeben.');
      return;
    }
    if (Number.isNaN(zahl) || zahl < 0) {
      setFehler('Arbeitszeit muss eine Zahl ≥ 0 sein (z. B. 2.5).');
      return;
    }
    setLaeuft(true);
    try {
      await onSubmit({ arbeitszeit: zahl, material: material.trim(), bemerkung: bemerkung.trim() });
    } catch {
      setFehler('Speichern fehlgeschlagen.');
      setLaeuft(false);
    }
  }

  return (
    <Modal breite="max-w-[480px]" onClose={onClose}>
      <h2 className="mb-1 text-lg font-bold">Auftrag rapportieren</h2>
      <p className="mb-6 text-sm text-slate-500">
        Arbeitszeit und Material erfassen. Der Auftrag wird als ausgeführt markiert.
      </p>

      <div className="mb-4">
        <label className={labelKlasse}>
          Arbeitszeit (Stunden) <span className="text-red-600">*</span>
        </label>
        <input
          value={arbeitszeit}
          onChange={(e) => { setArbeitszeit(e.target.value); setFehler(''); }}
          placeholder="z. B. 2.5"
          className={feldKlasse(!!fehler && !arbeitszeit.trim())}
        />
      </div>

      <div className="mb-4">
        <label className={labelKlasse}>
          Material <span className="text-red-600">*</span>
        </label>
        <input
          value={material}
          onChange={(e) => { setMaterial(e.target.value); setFehler(''); }}
          placeholder="z. B. 1× Mischbatterie, Dichtungen"
          className={feldKlasse(!!fehler && !material.trim())}
        />
      </div>

      <div className="mb-6">
        <label className={labelKlasse}>
          Bemerkung <span className="font-medium text-slate-400">(optional)</span>
        </label>
        <textarea
          value={bemerkung}
          onChange={(e) => setBemerkung(e.target.value)}
          rows={3}
          placeholder="Besonderheiten, Empfehlungen…"
          className={`${feldKlasse(false)} resize-y leading-relaxed`}
        />
      </div>

      {fehler && <div className="-mt-3 mb-4 text-[12.5px] text-red-600">{fehler}</div>}

      <div className="flex justify-end gap-2.5">
        <button onClick={onClose} className={btnSecondary}>Abbrechen</button>
        <button onClick={speichern} disabled={laeuft} className={btnPrimary}>Als ausgeführt speichern</button>
      </div>
    </Modal>
  );
}
