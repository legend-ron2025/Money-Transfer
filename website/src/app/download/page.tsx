import type { Metadata } from 'next';
import Image from 'next/image';
import Link from 'next/link';
import { readFileSync } from 'fs';
import path from 'path';

export const metadata: Metadata = {
  title: 'Download MoneyTracker',
  description: 'Download the latest MoneyTracker APK for Android. Free, secure, built for India.',
};

interface ReleaseInfo {
  version:     string;
  apkUrl:      string;
  sha256:      string;
  releaseDate: string;
  fileSize:    string;
  minAndroid:  string;
  changelog:   string[];
}

function getRelease(): ReleaseInfo {
  try {
    const file = readFileSync(path.join(process.cwd(), 'public', 'releases', 'latest.json'), 'utf-8');
    return JSON.parse(file);
  } catch {
    return {
      version: '1.0.0', apkUrl: '/releases/MoneyTracker-1.0.0.apk',
      sha256: 'pending build', releaseDate: '2025-01-15',
      fileSize: '~25 MB', minAndroid: 'Android 7.0 (API 24)',
      changelog: ['Initial production release'],
    };
  }
}

const INSTALL_STEPS = [
  { n: '1', title: 'Enable Unknown Sources', body: 'Go to Settings → Security (or Privacy) → Install Unknown Apps, and enable it for your browser or file manager.' },
  { n: '2', title: 'Download the APK',       body: 'Tap the download button above. The APK file saves to your Downloads folder automatically.' },
  { n: '3', title: 'Install & Open',          body: 'Open the downloaded file from your notifications or file manager. Follow the install prompts, then launch MoneyTracker.' },
];

export default function DownloadPage() {
  const info = getRelease();
  const dateStr = new Date(info.releaseDate).toLocaleDateString('en-IN', {
    year: 'numeric', month: 'long', day: 'numeric',
  });

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      {/* ── Header ──────────────────────────────────────────────────────── */}
      <header className="bg-white/80 dark:bg-gray-950/80 backdrop-blur-xl border-b border-gray-100 dark:border-gray-800 sticky top-0 z-10">
        <div className="container-section h-14 flex items-center gap-4">
          <Link href="/" className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400 hover:text-primary-600 transition-colors">
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Back to Home
          </Link>
          <span className="text-gray-200 dark:text-gray-700">|</span>
          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">Download MoneyTracker</span>
        </div>
      </header>

      <main className="container-section py-16 max-w-3xl mx-auto">

        {/* ── Hero ───────────────────────────────────────────────────────── */}
        <div className="text-center mb-12">
          <div className="flex justify-center mb-6">
            <div className="w-24 h-24 rounded-[28px] overflow-hidden shadow-glow ring-4 ring-white dark:ring-gray-900">
              <Image src="/icon-512.png" alt="MoneyTracker" width={96} height={96} className="w-full h-full object-cover" />
            </div>
          </div>
          <div className="inline-flex items-center gap-2 bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400 px-3 py-1 rounded-full text-xs font-semibold mb-4">
            <span className="w-1.5 h-1.5 rounded-full bg-green-500" />
            Latest Release
          </div>
          <h1 className="text-4xl font-extrabold text-gray-900 dark:text-white mb-3">
            MoneyTracker v{info.version}
          </h1>
          <p className="text-gray-500 dark:text-gray-400">
            Released {dateStr} · {info.fileSize} · Requires {info.minAndroid}
          </p>
        </div>

        {/* ── Download button ─────────────────────────────────────────────── */}
        <div className="text-center mb-14">
          <a href={info.apkUrl} download
            className="btn-primary text-xl px-12 py-4 inline-flex shadow-2xl">
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            Download APK ({info.fileSize})
          </a>
          <p className="mt-4 text-xs text-gray-400">
            By downloading you agree to our{' '}
            <Link href="/terms"   className="underline hover:text-primary-600">Terms</Link> and{' '}
            <Link href="/privacy" className="underline hover:text-primary-600">Privacy Policy</Link>.
          </p>
        </div>

        {/* ── Install steps ───────────────────────────────────────────────── */}
        <div className="card mb-10 divide-y divide-gray-50 dark:divide-gray-800">
          <div className="px-7 py-5">
            <h2 className="font-bold text-lg text-gray-900 dark:text-white">How to Install</h2>
          </div>
          {INSTALL_STEPS.map(s => (
            <div key={s.n} className="px-7 py-5 flex gap-5">
              <div className="shrink-0 w-9 h-9 rounded-xl bg-primary-100 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 font-bold text-base flex items-center justify-center">
                {s.n}
              </div>
              <div>
                <p className="font-semibold text-gray-800 dark:text-gray-200 mb-0.5">{s.title}</p>
                <p className="text-sm text-gray-500 dark:text-gray-400 leading-relaxed">{s.body}</p>
              </div>
            </div>
          ))}
        </div>

        {/* ── Checksum verification ────────────────────────────────────────── */}
        <div className="rounded-2xl bg-amber-50 dark:bg-amber-900/15 border border-amber-200 dark:border-amber-800/40 p-6 mb-10">
          <div className="flex gap-3 mb-3">
            <svg className="w-5 h-5 text-amber-600 dark:text-amber-400 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <p className="font-semibold text-amber-800 dark:text-amber-300 text-sm">Verify Download Integrity</p>
          </div>
          <p className="text-sm text-amber-700 dark:text-amber-400 mb-3">
            Compare the SHA-256 hash below with the downloaded file to confirm it hasn&apos;t been tampered with.
          </p>
          <div className="bg-white dark:bg-gray-900 rounded-xl p-4 font-mono text-xs text-gray-600 dark:text-gray-300 break-all select-all border border-gray-100 dark:border-gray-800">
            SHA-256: {info.sha256}
          </div>
        </div>

        {/* ── Changelog ────────────────────────────────────────────────────── */}
        <div className="card overflow-hidden">
          <div className="px-7 py-5 border-b border-gray-50 dark:border-gray-800 flex items-center justify-between">
            <h2 className="font-bold text-lg text-gray-900 dark:text-white">What&apos;s New in v{info.version}</h2>
            <span className="text-sm text-gray-400">{dateStr}</span>
          </div>
          <ul className="px-7 py-5 space-y-3">
            {info.changelog.map((c, i) => (
              <li key={i} className="flex items-start gap-3 text-sm text-gray-700 dark:text-gray-300">
                <svg className="w-4 h-4 text-green-500 shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                {c}
              </li>
            ))}
          </ul>
        </div>

      </main>
    </div>
  );
}
