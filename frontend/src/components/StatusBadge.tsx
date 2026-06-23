import { STATUS_BADGE_CLASS, STATUS_LABEL } from '../theme';
import type { Status } from '../types';

interface Props {
  status: Status;
  /** Kompakte Variante ohne Punkt (z. B. fuer die Druckansicht). */
  schlicht?: boolean;
}

export default function StatusBadge({ status, schlicht }: Props) {
  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full border font-semibold ${
        schlicht ? 'px-2.5 py-0.5 text-[11px]' : 'px-3 py-1 text-xs'
      } ${STATUS_BADGE_CLASS[status]}`}
    >
      {!schlicht && <span className="h-1.5 w-1.5 rounded-full bg-current" />}
      {STATUS_LABEL[status]}
    </span>
  );
}
