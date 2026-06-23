import { BrowserRouter, Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './auth/AuthContext';
import { ToastProvider } from './components/Toast';
import TopBar from './components/TopBar';
import LoginPage from './pages/LoginPage';
import ListPage from './pages/ListPage';
import NewOrderPage from './pages/NewOrderPage';
import DetailPage from './pages/DetailPage';
import PrintPage from './pages/PrintPage';
import MitarbeiterPage from './pages/MitarbeiterPage';

/** Layout fuer eingeloggte Bereiche: schuetzt vor Zugriff ohne Login. */
function GeschuetztesLayout() {
  const { istEingeloggt } = useAuth();
  if (!istEingeloggt) return <Navigate to="/login" replace />;
  return (
    <>
      <TopBar />
      <Outlet />
    </>
  );
}

function LoginRoute() {
  const { istEingeloggt } = useAuth();
  if (istEingeloggt) return <Navigate to="/" replace />;
  return <LoginPage />;
}

export default function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginRoute />} />
            <Route element={<GeschuetztesLayout />}>
              <Route path="/" element={<ListPage />} />
              <Route path="/auftraege/neu" element={<NewOrderPage />} />
              <Route path="/auftraege/:id" element={<DetailPage />} />
              <Route path="/auftraege/:id/druck" element={<PrintPage />} />
              <Route path="/mitarbeiter" element={<MitarbeiterPage />} />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </ToastProvider>
    </AuthProvider>
  );
}
