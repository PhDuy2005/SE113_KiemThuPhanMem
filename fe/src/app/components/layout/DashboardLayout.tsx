import { ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import { TopNav } from './TopNav';

interface DashboardLayoutProps {
  role: 'Customer' | 'Staff' | 'BUSINESS_ADMIN' | 'Technical Admin';
  userName: string;
  userRole: string;
  onLogout: () => void;
  children: ReactNode;
}

export function DashboardLayout({
  role,
  userName,
  userRole,
  onLogout,
  children,
}: DashboardLayoutProps) {
  return (
    <div className="flex h-screen w-full overflow-hidden">
      <Sidebar role={role} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <TopNav userName={userName} userRole={userRole} onLogout={onLogout} />
        <main className="flex-1 overflow-auto bg-background p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
