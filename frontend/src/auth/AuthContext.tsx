import { createContext, useContext, useState, type ReactNode } from 'react';
import { api, clearToken, getToken, setToken } from '../api/client';
import type { LoginResponse, Rolle } from '../types';

interface Benutzer {
  mitarbeiterId: number;
  name: string;
  email: string;
  rolle: Rolle;
}

interface AuthContextValue {
  benutzer: Benutzer | null;
  istEingeloggt: boolean;
  login: (email: string, passwort: string) => Promise<void>;
  logout: () => void;
}

const BENUTZER_KEY = 'sc_benutzer';

function geladenerBenutzer(): Benutzer | null {
  if (!getToken()) return null;
  const roh = localStorage.getItem(BENUTZER_KEY);
  return roh ? (JSON.parse(roh) as Benutzer) : null;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [benutzer, setBenutzer] = useState<Benutzer | null>(geladenerBenutzer);

  async function login(email: string, passwort: string) {
    const res: LoginResponse = await api.login(email, passwort);
    setToken(res.token);
    const b: Benutzer = {
      mitarbeiterId: res.mitarbeiterId,
      name: res.name,
      email: res.email,
      rolle: res.rolle,
    };
    localStorage.setItem(BENUTZER_KEY, JSON.stringify(b));
    setBenutzer(b);
  }

  function logout() {
    clearToken();
    localStorage.removeItem(BENUTZER_KEY);
    setBenutzer(null);
  }

  return (
    <AuthContext.Provider value={{ benutzer, istEingeloggt: !!benutzer, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth muss innerhalb von AuthProvider verwendet werden');
  return ctx;
}
