import type { Metadata, Viewport } from 'next';
import './globals.css';

export const metadata: Metadata = {
  metadataBase: new URL(process.env.NEXT_PUBLIC_SITE_URL ?? 'https://moneytracker.app'),
  title: {
    default:  'MoneyTracker — Personal Finance Dashboard for India',
    template: '%s | MoneyTracker',
  },
  description:
    'Track expenses, manage budgets, and achieve your financial goals with MoneyTracker. Automatic bank sync via India\'s Account Aggregator ecosystem.',
  keywords: [
    'money tracker', 'expense tracker', 'budget app', 'personal finance',
    'India', 'UPI', 'account aggregator', 'bank sync', 'spending analytics',
  ],
  authors: [{ name: 'MoneyTracker' }],
  openGraph: {
    type:        'website',
    siteName:    'MoneyTracker',
    title:       'MoneyTracker — Personal Finance Dashboard for India',
    description: 'Track expenses, manage budgets, and achieve financial goals.',
    images:      [{ url: '/og-image.png', width: 1200, height: 630, alt: 'MoneyTracker' }],
  },
  twitter: {
    card:  'summary_large_image',
    title: 'MoneyTracker — Personal Finance Dashboard for India',
    images: ['/og-image.png'],
  },
  manifest:  '/manifest.json',
  icons: {
    icon:  [
      { url: '/favicon-16.png', sizes: '16x16', type: 'image/png' },
      { url: '/favicon-32.png', sizes: '32x32', type: 'image/png' },
    ],
    apple: '/apple-touch-icon.png',
  },
};

export const viewport: Viewport = {
  width:        'device-width',
  initialScale: 1,
  themeColor: [
    { media: '(prefers-color-scheme: light)', color: '#6c63ff' },
    { media: '(prefers-color-scheme: dark)',  color: '#231f6b' },
  ],
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
