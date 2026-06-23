import { useState } from 'react';
import Modal from './Modal';
import { btnPrimary, btnSecondary, feldKlasse, inputBase, labelKlasse } from '../ui';
import type { Kunde, KundeRequest } from '../types';

interface Props {
  onClose: () => void;
  onSubmit: (req: KundeRequest) => Promise<Kunde>;
}

export default function KundeModal({ onClose, onSubmit }: Props) {
  const [name, setName] = useState('');
  const [adresse, setAdresse] = useState('');
  const [telefon, setTelefon] = useState('');
  const [email, setEmail] = useState('');
  const [fehler, setFehler] = useState('');
  const [laeuft, setLaeuft] = useState(false);

  async function erstellen() {
    if (!name.trim()) {
      setFehler('Bitte einen Namen eingeben.');
      return;
    }
    setLaeuft(true);
    try {
      await onSubmit({
        name: name.trim(),
        adresse: adresse.trim() || undefined,
        telefon: telefon.trim() || undefined,
        email: email.trim() || undefined,
      });
    } catch {
      setFehler('Kunde konnte nicht erstellt werden.');
      setLaeuft(false);
    }
  }

  return (
    <Modal breite="max-w-[460px]" onClose={onClose}>
      <h2 className="mb-1 text-lg font-bold">Neuen Kunden erstellen</h2>
      <p className="mb-6 text-sm text-slate-500">
        Der Kunde wird gespeichert und direkt im Auftrag ausgewählt.
      </p>

      <div className="mb-4">
        <label className={labelKlasse}>
          Name / Firma <span className="text-red-600">*</span>
        </label>
        <input
          value={name}
          onChange={(e) => { setName(e.target.value); setFehler(''); }}
          placeholder="z. B. M. Müller oder Hauser GmbH"
          className={feldKlasse(!!fehler && !name.trim())}
        />
      </div>

      <div className="mb-4">
        <label className={labelKlasse}>Adresse</label>
        <input
          value={adresse}
          onChange={(e) => setAdresse(e.target.value)}
          placeholder="Strasse Nr., PLZ Ort"
          className={`${inputBase} border-slate-300`}
        />
      </div>

      <div className="mb-6 grid grid-cols-2 gap-3.5">
        <div>
          <label className={labelKlasse}>Telefon</label>
          <input
            value={telefon}
            onChange={(e) => setTelefon(e.target.value)}
            placeholder="052 000 00 00"
            className={`${inputBase} border-slate-300`}
          />
        </div>
        <div>
          <label className={labelKlasse}>E-Mail</label>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="kunde@example.ch"
            className={`${inputBase} border-slate-300`}
          />
        </div>
      </div>

      {fehler && <div className="-mt-3 mb-4 text-[12.5px] text-red-600">{fehler}</div>}

      <div className="flex justify-end gap-2.5">
        <button onClick={onClose} className={btnSecondary}>Abbrechen</button>
        <button onClick={erstellen} disabled={laeuft} className={btnPrimary}>Kunde erstellen</button>
      </div>
    </Modal>
  );
}
