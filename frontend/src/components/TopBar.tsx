import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

function initialen(name: string): string {
  return name
    .split(/[\s.]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((t) => t[0]?.toUpperCase() ?? '')
    .join('');
}

export default function TopBar() {
  const { benutzer, logout } = useAuth();
  const navigate = useNavigate();

  function abmelden() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <div className="no-print sticky top-0 z-30 flex h-[58px] items-center gap-3 border-b border-slate-200 bg-white px-6">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" className="text-blue-600">
        <path d="M12 2.5S5 9.6 5 14.4a7 7 0 0 0 14 0C19 9.6 12 2.5 12 2.5Z" fill="currentColor" />
        <path d="M9.4 13.6a2.6 2.6 0 0 0 2.6 2.6" stroke="#fff" strokeWidth="1.4" strokeLinecap="round" fill="none" />
      </svg>
      <button
        onClick={() => navigate('/')}
        className="border-none bg-transparent p-0 text-[15px] font-bold tracking-tight cursor-pointer"
      >
        Glauser Illnau AG
      </button>
      <div className="h-5 w-px bg-slate-200" />
      <div className="text-sm font-medium text-slate-500">Service-Cockpit</div>
      {benutzer?.rolle === 'GESCHAEFTSLEITER' && (
        <button
          onClick={() => navigate('/mitarbeiter')}
          className="border-none bg-transparent px-1 py-1.5 text-sm font-semibold text-slate-500 hover:text-slate-900 cursor-pointer"
        >
          Mitarbeiter
        </button>
      )}
      <div className="ml-auto flex items-center gap-3.5 text-sm text-slate-500">
        <span>{benutzer?.name}</span>
        <div className="flex h-[30px] w-[30px] items-center justify-center rounded-full bg-slate-200 text-xs font-semibold text-slate-600">
          {benutzer ? initialen(benutzer.name) : ''}
        </div>
        <button
          onClick={abmelden}
          className="inline-flex items-center gap-1.5 border-none bg-transparent px-0.5 py-1.5 text-sm font-semibold text-slate-500 hover:text-slate-900 cursor-pointer"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <path
              d="M15 17l5-5-5-5M20 12H9M9 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h3"
              stroke="currentColor"
              strokeWidth="1.7"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
          Abmelden
        </button>
      </div>
    </div>
  );
}
