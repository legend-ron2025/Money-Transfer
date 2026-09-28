import Image from 'next/image';
import Link from 'next/link';

/* ── Static data ─────────────────────────────────────────────────────────── */
const NAV_LINKS = [
  { href: '#features',     label: 'Features'     },
  { href: '#how-it-works', label: 'How it works' },
  { href: '#security',     label: 'Security'     },
];

const STATS = [
  { value: '13+', label: 'Banks supported'   },
  { value: '50+', label: 'Auto-categorised'  },
  { value: '100%', label: 'Private & secure' },
  { value: 'Free', label: 'Always free'      },
];

const FEATURES = [
  {
    icon: '🏦',
    title: 'Bank Sync via AA',
    desc: 'Connect your bank accounts through India\'s RBI-regulated Account Aggregator. No passwords, no screen-scraping — just secure, consent-based data.',
    color: 'from-blue-500/10 to-blue-600/5',
  },
  {
    icon: '🤖',
    title: 'Smart Categorisation',
    desc: 'Every transaction is automatically labelled using a 50+ merchant lookup and 20+ keyword rules — Swiggy, Zomato, Amazon and more.',
    color: 'from-purple-500/10 to-purple-600/5',
  },
  {
    icon: '📊',
    title: 'Powerful Analytics',
    desc: 'Visualise spending trends across weeks, months and categories. See exactly where your money goes with beautiful, real-time charts.',
    color: 'from-primary-500/10 to-primary-600/5',
  },
  {
    icon: '💰',
    title: 'Budgets & Goals',
    desc: 'Set monthly budgets per category and track savings goals with real-time progress bars. Get alerted before you overspend.',
    color: 'from-green-500/10 to-green-600/5',
  },
  {
    icon: '📱',
    title: 'SMS Auto-Import',
    desc: 'Bank SMS alerts are parsed instantly to keep transactions up to date — works offline, the moment a payment happens.',
    color: 'from-orange-500/10 to-orange-600/5',
  },
  {
    icon: '🔔',
    title: 'Subscription Detector',
    desc: 'Recurring payments are detected automatically. Never be surprised by a renewal — see every subscription and its annual cost.',
    color: 'from-pink-500/10 to-pink-600/5',
  },
];

const STEPS = [
  {
    n:     '01',
    title: 'Download & Install',
    desc:  'Grab the APK from our download page and install it on any Android 7.0+ device in under a minute.',
    icon:  '📲',
  },
  {
    n:     '02',
    title: 'Connect Your Bank',
    desc:  'Link accounts through India\'s Account Aggregator. Your bank credentials never leave your bank.',
    icon:  '🔗',
  },
  {
    n:     '03',
    title: 'Get Insights',
    desc:  'See your full financial picture instantly — balances, spending, budgets, goals, and smart insights.',
    icon:  '✨',
  },
];

const SECURITY_BADGES = [
  { icon: '🔒', label: 'Biometric Lock'          },
  { icon: '🔐', label: 'End-to-end Encryption'   },
  { icon: '🏛️', label: 'RBI-regulated AA'         },
  { icon: '🛡️', label: 'Play Integrity API'       },
  { icon: '🚫', label: 'No passwords stored'      },
  { icon: '📋', label: 'Revocable consent'        },
];

/* ── Page component ───────────────────────────────────────────────────────── */
export default function LandingPage() {
  return (
    <div className="min-h-screen bg-white dark:bg-gray-950 overflow-x-hidden">

      {/* ── Navbar ──────────────────────────────────────────────────────── */}
      <header className="sticky top-0 z-50 bg-white/80 dark:bg-gray-950/80 backdrop-blur-xl border-b border-gray-100 dark:border-gray-800/60">
        <nav className="container-section h-16 flex items-center justify-between">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-3 group">
            <div className="w-9 h-9 rounded-xl overflow-hidden ring-2 ring-primary-100 dark:ring-primary-900/40 group-hover:ring-primary-300 transition-all">
              <Image src="/icon-192.png" alt="MoneyTracker" width={36} height={36} className="w-full h-full object-cover" />
            </div>
            <span className="font-bold text-lg text-gray-900 dark:text-white tracking-tight">MoneyTracker</span>
          </Link>

          {/* Desktop links */}
          <div className="hidden md:flex items-center gap-8">
            {NAV_LINKS.map(l => (
              <a key={l.href} href={l.href}
                className="text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors">
                {l.label}
              </a>
            ))}
          </div>

          {/* CTA */}
          <Link href="/download" className="btn-primary py-2.5 px-5 text-sm">
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            Download APK
          </Link>
        </nav>
      </header>

      {/* ── Hero ─────────────────────────────────────────────────────────── */}
      <section className="relative pt-20 pb-28 overflow-hidden" style={{background: 'linear-gradient(180deg, #f0edff 0%, #faf9ff 60%, #ffffff 100%)'}}>
        {/* Decorative blobs */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[900px] h-[500px] bg-primary-400/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute top-40 left-0 w-72 h-72 bg-blue-400/8 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute top-20 right-0 w-72 h-72 bg-purple-400/8 rounded-full blur-3xl pointer-events-none" />

        <div className="container-section relative text-center">
          {/* Badge */}
          <div className="badge mb-8 mx-auto w-fit">
            <span className="pulse-dot" />
            v1.0.0 — Now available for Android
          </div>

          {/* App icon */}
          <div className="flex justify-center mb-8">
            <div className="w-24 h-24 rounded-[28px] overflow-hidden shadow-glow ring-4 ring-white dark:ring-gray-900 animate-float">
              <Image src="/icon-512.png" alt="MoneyTracker App Icon" width={96} height={96} className="w-full h-full object-cover" priority />
            </div>
          </div>

          {/* Headline */}
          <h1 className="text-5xl sm:text-6xl md:text-7xl font-extrabold tracking-tight text-gray-900 dark:text-white leading-[1.05] mb-6">
            Your Personal<br />
            <span className="bg-clip-text text-transparent" style={{backgroundImage: 'linear-gradient(135deg, #6c63ff 0%, #4d42cc 100%)'}}>
              Financial Dashboard
            </span>
          </h1>

          <p className="text-xl text-gray-600 dark:text-gray-300 max-w-2xl mx-auto mb-10 leading-relaxed">
            One app to truly understand your money. Automatic bank sync, smart insights, budgets, goals, and subscription tracking — all built for India.
          </p>

          {/* CTAs */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-14">
            <Link href="/download" className="btn-primary text-base px-8 py-3.5">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              Download for Android — Free
            </Link>
            <a href="#features" className="btn-outline text-base px-8 py-3.5">
              Explore Features
            </a>
          </div>

          {/* Trust row */}
          <div className="flex flex-wrap items-center justify-center gap-x-8 gap-y-3 text-sm text-gray-500 dark:text-gray-400">
            {['RBI Account Aggregator', 'End-to-end Encrypted', 'No credentials stored', 'Android 7.0+'].map(b => (
              <span key={b} className="flex items-center gap-1.5">
                <svg className="w-4 h-4 text-green-500 shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                </svg>
                {b}
              </span>
            ))}
          </div>
        </div>
      </section>

      {/* ── Stats bar ────────────────────────────────────────────────────── */}
      <section className="bg-primary-600 py-10">
        <div className="container-section grid grid-cols-2 md:grid-cols-4 gap-6 text-center text-white">
          {STATS.map(s => (
            <div key={s.label}>
              <p className="text-3xl font-extrabold mb-1">{s.value}</p>
              <p className="text-primary-200 text-sm font-medium">{s.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ── App Preview ──────────────────────────────────────────────────── */}
      <section className="py-24 bg-gray-50 dark:bg-gray-900/50">
        <div className="container-section">
          <div className="text-center mb-12">
            <h2 className="text-3xl sm:text-4xl font-extrabold text-gray-900 dark:text-white">
              See it in action
            </h2>
            <p className="mt-3 text-gray-500 dark:text-gray-400 text-lg">Your complete financial picture, at a glance</p>
          </div>

          {/* Browser-style mockup */}
          <div className="max-w-4xl mx-auto rounded-2xl overflow-hidden shadow-2xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-950">
            {/* Browser chrome */}
            <div className="bg-gray-100 dark:bg-gray-800 px-5 py-3.5 flex items-center gap-3 border-b border-gray-200 dark:border-gray-700">
              <div className="flex gap-2">
                <div className="w-3 h-3 rounded-full bg-red-400" />
                <div className="w-3 h-3 rounded-full bg-yellow-400" />
                <div className="w-3 h-3 rounded-full bg-green-400" />
              </div>
              <div className="flex-1 mx-4">
                <div className="bg-white dark:bg-gray-700 rounded-lg px-4 py-1.5 text-xs text-gray-500 dark:text-gray-400 text-center font-medium">
                  MoneyTracker Dashboard
                </div>
              </div>
            </div>

            {/* Dashboard content */}
            <div className="p-6 md:p-8">
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Left column */}
                <div className="space-y-4">
                  {/* Balance card */}
                  <div className="rounded-2xl p-5 text-white" style={{background: 'linear-gradient(135deg, #6c63ff 0%, #4d42cc 100%)'}}>
                    <p className="text-xs font-medium opacity-75 uppercase tracking-widest mb-2">Total Balance</p>
                    <p className="text-3xl font-extrabold mb-1">₹84,520</p>
                    <p className="text-xs opacity-70 flex items-center gap-1">
                      <span className="text-green-300">↑</span> +₹3,240 this month
                    </p>
                  </div>
                  {/* Income / Expense */}
                  <div className="grid grid-cols-2 gap-3">
                    <div className="rounded-xl bg-green-50 dark:bg-green-900/20 border border-green-100 dark:border-green-900/30 p-4">
                      <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Income</p>
                      <p className="text-lg font-bold text-green-600 dark:text-green-400">₹52,000</p>
                    </div>
                    <div className="rounded-xl bg-red-50 dark:bg-red-900/20 border border-red-100 dark:border-red-900/30 p-4">
                      <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Expenses</p>
                      <p className="text-lg font-bold text-red-500 dark:text-red-400">₹31,240</p>
                    </div>
                  </div>
                  {/* Savings rate */}
                  <div className="rounded-xl bg-primary-50 dark:bg-primary-900/20 border border-primary-100 dark:border-primary-900/30 p-4">
                    <div className="flex justify-between items-center mb-2">
                      <p className="text-xs font-medium text-gray-600 dark:text-gray-400">Savings Rate</p>
                      <p className="text-sm font-bold text-primary-600 dark:text-primary-400">39.9%</p>
                    </div>
                    <div className="h-2 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                      <div className="h-full rounded-full bg-gradient-to-r from-primary-400 to-primary-600" style={{width: '40%'}} />
                    </div>
                  </div>
                </div>

                {/* Right column */}
                <div className="lg:col-span-2 space-y-5">
                  {/* Top categories */}
                  <div>
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">Top Spending Categories</h3>
                    <div className="space-y-3">
                      {[
                        { icon: '🍔', cat: 'Food & Dining',  amt: '₹8,420', pct: 27, color: '#f97316' },
                        { icon: '🛒', cat: 'Shopping',       amt: '₹6,200', pct: 20, color: '#8b5cf6' },
                        { icon: '📱', cat: 'Bills & Utilities',amt: '₹5,400',pct: 17, color: '#ef4444' },
                        { icon: '🚗', cat: 'Transport',      amt: '₹4,200', pct: 13, color: '#3b82f6' },
                      ].map(c => (
                        <div key={c.cat} className="flex items-center gap-3">
                          <span className="text-lg w-7 text-center">{c.icon}</span>
                          <div className="flex-1">
                            <div className="flex justify-between text-sm mb-1">
                              <span className="font-medium text-gray-700 dark:text-gray-300">{c.cat}</span>
                              <span className="text-gray-500 dark:text-gray-400">{c.amt}</span>
                            </div>
                            <div className="h-1.5 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                              <div className="h-full rounded-full transition-all" style={{width: `${c.pct * 3}%`, backgroundColor: c.color}} />
                            </div>
                          </div>
                          <span className="text-xs text-gray-400 w-8 text-right">{c.pct}%</span>
                        </div>
                      ))}
                    </div>
                  </div>
                  {/* Recent transactions */}
                  <div>
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">Recent Transactions</h3>
                    <div className="space-y-2">
                      {[
                        { icon: '🛒', name: 'Grocery',   cat: 'Food',   amt: '-₹850',    color: 'text-red-500' },
                        { icon: '☕', name: 'Starbucks', cat: 'Food',   amt: '-₹280',    color: 'text-red-500' },
                        { icon: '💰', name: 'Salary',    cat: 'Income', amt: '+₹52,000', color: 'text-green-500' },
                        { icon: '📱', name: 'Jio Recharge',cat:'Bills', amt: '-₹299',   color: 'text-red-500' },
                      ].map(t => (
                        <div key={t.name + t.amt} className="flex items-center justify-between py-2 border-b border-gray-50 dark:border-gray-800/60 last:border-0">
                          <div className="flex items-center gap-3">
                            <div className="w-8 h-8 rounded-lg bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-base">{t.icon}</div>
                            <div>
                              <p className="text-sm font-medium text-gray-800 dark:text-gray-200">{t.name}</p>
                              <p className="text-xs text-gray-400">{t.cat}</p>
                            </div>
                          </div>
                          <span className={`text-sm font-bold ${t.color}`}>{t.amt}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── Features ─────────────────────────────────────────────────────── */}
      <section id="features" className="py-24 bg-white dark:bg-gray-950">
        <div className="container-section">
          <div className="text-center mb-16">
            <div className="badge mb-4 mx-auto w-fit">Features</div>
            <h2 className="text-4xl font-extrabold text-gray-900 dark:text-white">Everything you need</h2>
            <p className="mt-4 text-xl text-gray-500 dark:text-gray-400 max-w-xl mx-auto">
              One app. Complete financial clarity for modern India.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {FEATURES.map(f => (
              <div key={f.title}
                className="feature-card card p-7 bg-gradient-to-br border border-gray-100 dark:border-gray-800"
                style={{background: `linear-gradient(135deg, rgba(108,99,255,0.04) 0%, rgba(255,255,255,1) 100%)`}}>
                <div className="w-12 h-12 rounded-2xl bg-primary-50 dark:bg-primary-900/20 flex items-center justify-center text-2xl mb-4">
                  {f.icon}
                </div>
                <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">{f.title}</h3>
                <p className="text-gray-600 dark:text-gray-400 text-sm leading-relaxed">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── How it works ─────────────────────────────────────────────────── */}
      <section id="how-it-works" className="py-24 bg-gray-50 dark:bg-gray-900/40">
        <div className="container-section">
          <div className="text-center mb-16">
            <div className="badge mb-4 mx-auto w-fit">Getting Started</div>
            <h2 className="text-4xl font-extrabold text-gray-900 dark:text-white">Up and running in minutes</h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative">
            {/* Connector line (desktop) */}
            <div className="hidden md:block absolute top-14 left-1/4 right-1/4 h-0.5 bg-gradient-to-r from-primary-200 via-primary-400 to-primary-200 dark:from-primary-900 dark:via-primary-700 dark:to-primary-900" />
            {STEPS.map((s, i) => (
              <div key={s.n} className="text-center relative">
                <div className="relative inline-flex items-center justify-center w-28 h-28 mx-auto mb-6">
                  <div className="absolute inset-0 rounded-full bg-primary-50 dark:bg-primary-900/20" />
                  <div className="relative">
                    <span className="text-4xl">{s.icon}</span>
                    <span className="absolute -top-1 -right-1 w-6 h-6 rounded-full bg-primary-600 text-white text-xs font-bold flex items-center justify-center">
                      {i + 1}
                    </span>
                  </div>
                </div>
                <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">{s.title}</h3>
                <p className="text-gray-500 dark:text-gray-400 text-sm leading-relaxed max-w-xs mx-auto">{s.desc}</p>
              </div>
            ))}
          </div>

          {/* CTA below steps */}
          <div className="text-center mt-14">
            <Link href="/download" className="btn-primary text-base px-8 py-3.5 inline-flex">
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              Download MoneyTracker — It&apos;s Free
            </Link>
          </div>
        </div>
      </section>

      {/* ── Security ─────────────────────────────────────────────────────── */}
      <section id="security" className="py-24 bg-white dark:bg-gray-950">
        <div className="container-section">
          <div className="max-w-3xl mx-auto text-center mb-14">
            <div className="badge mb-4 mx-auto w-fit">Security</div>
            <h2 className="text-4xl font-extrabold text-gray-900 dark:text-white mb-5">Built with trust at the core</h2>
            <p className="text-lg text-gray-600 dark:text-gray-400 leading-relaxed">
              MoneyTracker never stores your bank credentials. All account data flows through India&apos;s RBI-regulated
              Account Aggregator framework — with your explicit, fully revocable consent.
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-4">
            {SECURITY_BADGES.map(b => (
              <div key={b.label} className="card p-4 text-center hover:shadow-card-hover transition-shadow">
                <div className="text-3xl mb-2">{b.icon}</div>
                <p className="text-xs font-semibold text-gray-700 dark:text-gray-300 leading-tight">{b.label}</p>
              </div>
            ))}
          </div>

          {/* AA explanation box */}
          <div className="mt-10 max-w-2xl mx-auto rounded-2xl border border-primary-100 dark:border-primary-900/40 bg-primary-50/50 dark:bg-primary-950/30 p-7">
            <div className="flex gap-4">
              <div className="shrink-0 w-10 h-10 rounded-xl bg-primary-100 dark:bg-primary-900/40 flex items-center justify-center text-xl">🏛️</div>
              <div>
                <h4 className="font-semibold text-gray-900 dark:text-white mb-1">What is Account Aggregator?</h4>
                <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">
                  Account Aggregator (AA) is an RBI-regulated framework that lets you securely share financial data
                  with apps — without ever sharing your net-banking username or password.
                  You stay in full control with granular, time-limited consent.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── CTA Banner ───────────────────────────────────────────────────── */}
      <section className="py-24 relative overflow-hidden" style={{background: 'linear-gradient(135deg, #6c63ff 0%, #4d42cc 100%)'}}>
        <div className="absolute inset-0 opacity-10 pointer-events-none"
          style={{backgroundImage: 'radial-gradient(circle at 20% 50%, rgba(255,255,255,0.4) 0%, transparent 60%), radial-gradient(circle at 80% 20%, rgba(255,255,255,0.3) 0%, transparent 50%)'}} />
        <div className="container-section relative text-center text-white">
          <div className="flex justify-center mb-6">
            <div className="w-16 h-16 rounded-[18px] overflow-hidden shadow-lg ring-4 ring-white/30">
              <Image src="/icon-512.png" alt="MoneyTracker" width={64} height={64} className="w-full h-full object-cover" />
            </div>
          </div>
          <h2 className="text-4xl sm:text-5xl font-extrabold mb-4">Ready to understand your money?</h2>
          <p className="text-white/75 text-xl mb-10 max-w-xl mx-auto">
            Free. No subscription. No ads. Built for India.
          </p>
          <Link href="/download"
            className="inline-flex items-center gap-2.5 bg-white text-primary-700 font-bold text-lg px-10 py-4 rounded-2xl hover:bg-gray-50 transition-colors shadow-2xl">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            Download APK — Free
          </Link>
          <p className="mt-5 text-white/50 text-sm">Android 7.0+ required. ~25 MB download.</p>
        </div>
      </section>

      {/* ── Footer ───────────────────────────────────────────────────────── */}
      <footer className="bg-gray-950 text-gray-400 py-14">
        <div className="container-section">
          <div className="flex flex-col md:flex-row items-center justify-between gap-8 mb-10">
            {/* Brand */}
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl overflow-hidden">
                <Image src="/icon-192.png" alt="MoneyTracker" width={36} height={36} className="w-full h-full object-cover" />
              </div>
              <div>
                <p className="font-bold text-white text-lg leading-none">MoneyTracker</p>
                <p className="text-xs text-gray-500 mt-0.5">Built for India 🇮🇳</p>
              </div>
            </div>
            {/* Links */}
            <div className="flex flex-wrap justify-center gap-6 text-sm">
              {[
                { href: '/download', label: 'Download' },
                { href: '/privacy',  label: 'Privacy Policy' },
                { href: '/terms',    label: 'Terms of Service' },
                { href: 'https://github.com/legend-ron2025/Money-Transfer', label: 'GitHub', target: '_blank' },
              ].map(l => (
                <a key={l.label} href={l.href} target={(l as any).target}
                  rel={(l as any).target === '_blank' ? 'noopener noreferrer' : undefined}
                  className="hover:text-white transition-colors">
                  {l.label}
                </a>
              ))}
            </div>
          </div>
          <div className="border-t border-gray-800 pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-sm text-gray-600">
            <p>© {new Date().getFullYear()} MoneyTracker. All rights reserved.</p>
            <p>Not a SEBI/RBI registered entity. For personal finance tracking only.</p>
          </div>
        </div>
      </footer>

    </div>
  );
}
