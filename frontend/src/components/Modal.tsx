import type { ReactNode } from 'react';

interface Props {
  /** Tailwind-Breitenklasse, z. B. "max-w-[440px]". */
  breite?: string;
  onClose: () => void;
  children: ReactNode;
}

/** Abgedunkeltes Overlay mit zentrierter Karte. Klick auf den Hintergrund schliesst. */
export default function Modal({ breite = 'max-w-[440px]', onClose, children }: Props) {
  return (
    <div
      className="no-print animate-sc-overlay fixed inset-0 z-50 flex items-center justify-center bg-slate-900/45 p-6"
      onClick={onClose}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className={`animate-sc-in w-full ${breite} rounded-2xl bg-white p-7 shadow-2xl`}
      >
        {children}
      </div>
    </div>
  );
}
