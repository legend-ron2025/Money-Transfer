import type { Metadata } from 'next';
import Link from 'next/link';

export const metadata: Metadata = {
  title: 'Privacy Policy',
  description: 'MoneyTracker privacy policy — how we collect, use, and protect your data.',
};

const SECTIONS = [
  {
    title: 'Data We Collect',
    body: [
      'MoneyTracker collects only the financial data you explicitly authorise via India\'s Account Aggregator framework.',
      'We collect your email address and name during account registration.',
      'Transaction data, account balances, and bank metadata are fetched via AA consent — never by storing your bank credentials.',
      'When you grant SMS permission, we read bank transaction SMS alerts to import transactions locally on your device.',
    ],
  },
  {
    title: 'How We Use Your Data',
    body: [
      'Your financial data is used solely to provide spending analytics, budgets, and financial insights within the app.',
      'We do not sell, share, rent, or monetise your personal financial data to any third party.',
      'Analytics computations are performed on our secure backend servers and results are cached temporarily.',
      'SMS data is processed locally on your device and sent to our server only over HTTPS with your credentials.',
    ],
  },
  {
    title: 'Data Security',
    body: [
      'All data is transmitted over TLS 1.3 encrypted connections.',
      'Tokens and sensitive credentials are stored in Android\'s EncryptedSharedPreferences backed by AndroidKeyStore.',
      'Database fields for sensitive data use AES-256 encryption at rest.',
      'Automatic backups are disabled; the local database is excluded from Android backup.',
      'Screenshots are blocked in release builds (FLAG_SECURE).',
    ],
  },
  {
    title: 'Account Aggregator & Consent',
    body: [
      'Bank account linking uses the RBI-regulated Account Aggregator (AA) framework through Setu.',
      'You provide explicit, time-limited, purpose-specific consent before any bank data is accessed.',
      'You can revoke consent at any time from the Privacy Center inside the app, or directly through your AA provider.',
      'Revocation immediately stops further data access. Historical data can be deleted by requesting account deletion.',
    ],
  },
  {
    title: 'SMS Permission',
    body: [
      'The READ_SMS permission is used exclusively to parse bank transaction alert messages.',
      'No SMS content is stored beyond what is necessary to create a transaction record.',
      'We never read personal or non-banking SMS messages.',
      'You may deny or revoke this permission at any time in Android Settings. The app continues to work without it.',
    ],
  },
  {
    title: 'Data Retention & Deletion',
    body: [
      'Your data is retained as long as your account is active.',
      'You can request complete data deletion from Settings → Privacy → Delete Account.',
      'Upon deletion, all your data is permanently removed from our servers within 30 days.',
      'You may export all your data as CSV before deleting your account.',
    ],
  },
  {
    title: 'Contact',
    body: [
      'For privacy questions, data requests, or to report a concern, email: privacy@moneytracker.app',
      'We aim to respond to all privacy enquiries within 5 business days.',
    ],
  },
];

export default function PrivacyPage() {
  return (
    <div className="min-h-screen bg-white dark:bg-gray-950">
      <header className="bg-white/80 dark:bg-gray-950/80 backdrop-blur-xl border-b border-gray-100 dark:border-gray-800 sticky top-0 z-10">
        <div className="container-section h-14 flex items-center gap-4">
          <Link href="/" className="flex items-center gap-2 text-sm text-gray-500 hover:text-primary-600 transition-colors">
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Home
          </Link>
        </div>
      </header>

      <main className="container-section py-16 max-w-3xl mx-auto">
        <div className="mb-10">
          <div className="badge mb-4">Legal</div>
          <h1 className="text-4xl font-extrabold text-gray-900 dark:text-white mb-3">Privacy Policy</h1>
          <p className="text-gray-500 dark:text-gray-400">Last updated: January 2025</p>
        </div>

        <p className="text-gray-600 dark:text-gray-400 leading-relaxed mb-10">
          MoneyTracker is built with privacy as a core principle. We are a personal finance management tool —
          not a financial advisor, and not an advertising platform. This policy explains what data we collect,
          why, and how we protect it.
        </p>

        <div className="space-y-8">
          {SECTIONS.map(s => (
            <div key={s.title} className="card p-7">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-4">{s.title}</h2>
              <ul className="space-y-2.5">
                {s.body.map((line, i) => (
                  <li key={i} className="flex gap-3 text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                    <svg className="w-4 h-4 text-primary-500 shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    {line}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
