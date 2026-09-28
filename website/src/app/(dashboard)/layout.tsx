'use client';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

const NAV = [
  { href: '/dashboard',              icon: '🏠', label: 'Dashboard'    },
  { href: '/dashboard/transactions', icon: '📋', label: 'Transactions' },
  { href: '/dashboard/analytics',    icon: '📊', label: 'Analytics'    },
  { href: '/dashboard/budgets',      icon: '💰', label: 'Budgets'      },
  { href: '/dashboard/goals',        icon: '🎯', label: 'Goals'        },
  { href: '/dashboard/settings',     icon: '⚙️', label: 'Settings'     },
];

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  const isActive = (href: string) =>
    href === '/dashboard' ? pathname === href : (pathname ?? '').startsWith(href);

  const pageTitle = NAV.find(n => isActive(n.href))?.label ?? 'Dashboard';

  return (
    <div className="min-h-screen flex bg-gray-50 dark:bg-gray-950">

      {/* ── Sidebar ─────────────────────────────────────────────────────── */}
      <aside className="hidden md:flex w-60 flex-col bg-white dark:bg-gray-900 border-r border-gray-100 dark:border-gray-800 fixed inset-y-0 left-0 z-20">
        {/* Brand */}
        <Link href="/" className="h-16 flex items-center gap-3 px-5 border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
          <div className="w-8 h-8 rounded-xl overflow-hidden ring-2 ring-primary-100 dark:ring-primary-900/40">
            <Image src="/icon-192.png" alt="MoneyTracker" width={32} height={32} className="w-full h-full object-cover" />
          </div>
          <span className="font-bold text-gray-900 dark:text-white">MoneyTracker</span>
        </Link>

        {/* Nav */}
        <nav className="flex-1 py-4 px-2 space-y-0.5 overflow-y-auto">
          {NAV.map(item => (
            <Link key={item.href} href={item.href}
              className={`flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium transition-all ${
                isActive(item.href)
                  ? 'bg-primary-50 dark:bg-primary-900/20 text-primary-700 dark:text-primary-400'
                  : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800/60 hover:text-gray-900 dark:hover:text-white'
              }`}>
              <span className="text-base">{item.icon}</span>
              {item.label}
            </Link>
          ))}
        </nav>

        {/* Bottom link */}
        <div className="p-4 border-t border-gray-100 dark:border-gray-800">
          <Link href="/" className="flex items-center gap-2 text-xs text-gray-400 hover:text-primary-600 transition-colors px-2">
            <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Back to website
          </Link>
        </div>
      </aside>

      {/* ── Main area ───────────────────────────────────────────────────── */}
      <div className="flex-1 flex flex-col min-w-0 md:ml-60">
        {/* Topbar */}
        <header className="h-16 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between px-6 sticky top-0 z-10">
          <h1 className="font-semibold text-gray-900 dark:text-white">{pageTitle}</h1>
          <div className="flex items-center gap-3">
            <span className="inline-flex items-center gap-1.5 bg-amber-50 dark:bg-amber-900/20 text-amber-700 dark:text-amber-400 text-xs font-medium px-2.5 py-1 rounded-full border border-amber-200 dark:border-amber-800/40">
              <span className="w-1.5 h-1.5 rounded-full bg-amber-500" />
              Web Beta
            </span>
            <div className="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-600 dark:text-primary-400 font-bold text-sm">
              A
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 p-6 overflow-auto">
          {children}
        </main>
      </div>
    </div>
  );
}
