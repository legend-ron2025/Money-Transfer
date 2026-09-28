import type { Metadata } from 'next';
import Link from 'next/link';

export const metadata: Metadata = {
  title: 'Terms of Service',
  description: 'MoneyTracker terms of service.',
};

const SECTIONS = [
  {
    title: '1. Acceptance of Terms',
    body: 'By downloading, installing, or using MoneyTracker you agree to these Terms of Service. If you do not agree, please uninstall the app and delete your account.',
  },
  {
    title: '2. Description of Service',
    body: 'MoneyTracker is a personal finance management application. It helps you track expenses, manage budgets, and visualise spending patterns. It is not a bank, payment service, investment platform, or financial advisor.',
  },
  {
    title: '3. No Financial Advice',
    body: 'Nothing in MoneyTracker constitutes financial, investment, legal, or tax advice. All insights and analytics are informational only, based on your transaction data. You should consult a qualified professional for financial decisions.',
  },
  {
    title: '4. Account Aggregator Integration',
    body: 'Bank connectivity is provided through RBI-regulated Account Aggregators (currently Setu). Use of this feature is subject to the terms of the relevant AA provider and your participating financial institution. MoneyTracker does not store your bank credentials.',
  },
  {
    title: '5. SMS Permission',
    body: 'If you grant READ_SMS permission, MoneyTracker reads bank transaction SMS alerts to automatically import transactions. You may revoke this permission at any time in Android Settings. No SMS data is shared with third parties.',
  },
  {
    title: '6. User Responsibilities',
    body: 'You are responsible for maintaining the confidentiality of your MoneyTracker account credentials. You must notify us immediately of any unauthorised account access. You agree not to reverse-engineer, decompile, or misuse the application.',
  },
  {
    title: '7. Limitation of Liability',
    body: 'MoneyTracker is provided "as is" without warranty. We are not liable for any financial decisions made based on information displayed in the app, for data inaccuracies, or for losses resulting from unauthorised account access.',
  },
  {
    title: '8. Changes to Terms',
    body: 'We may update these Terms at any time. Continued use of the app after updates constitutes acceptance. We will notify users of significant changes via in-app notification.',
  },
  {
    title: '9. Governing Law',
    body: 'These Terms are governed by the laws of India. Any disputes shall be subject to the exclusive jurisdiction of courts in India.',
  },
  {
    title: '10. Contact',
    body: 'For questions about these Terms, contact us at: legal@moneytracker.app',
  },
];

export default function TermsPage() {
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
          <h1 className="text-4xl font-extrabold text-gray-900 dark:text-white mb-3">Terms of Service</h1>
          <p className="text-gray-500 dark:text-gray-400">Last updated: January 2025</p>
        </div>

        <div className="space-y-6">
          {SECTIONS.map(s => (
            <div key={s.title} className="card p-7">
              <h2 className="text-base font-bold text-gray-900 dark:text-white mb-3">{s.title}</h2>
              <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">{s.body}</p>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
