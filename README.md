# 💰 MoneyTracker

A production-ready personal finance app for India — Android (Kotlin + Compose), Node.js backend, and a Next.js marketing + web dashboard.

[![Backend CI](https://github.com/legend-ron2025/Money-Transfer/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/legend-ron2025/Money-Transfer/actions/workflows/backend-ci.yml)
[![Android CI](https://github.com/legend-ron2025/Money-Transfer/actions/workflows/android-ci.yml/badge.svg)](https://github.com/legend-ron2025/Money-Transfer/actions/workflows/android-ci.yml)

---

## 🗂️ Project Structure

```
MoneyTracker/
├── android/          # Kotlin + Compose Android app
├── backend/          # Node.js + TypeScript + Express REST API
├── website/          # Next.js 14 marketing site + web dashboard
├── infrastructure/   # Docker Compose (dev + production)
├── start.ps1         # ← One command to run everything locally
├── stop.ps1          # ← One command to stop everything
└── railway.json      # Railway.app deployment config
```

---

## 🚀 Run Locally (One Command)

### Prerequisites

| Tool | Version | Install |
|---|---|---|
| Node.js | 18+ | https://nodejs.org |
| PostgreSQL | 14+ | https://postgresql.org/download/windows |
| Redis 8 | 8.x | Auto-installed via winget (see below) |

**Install Redis 8 for Windows** (one time only):
```powershell
winget install --id taizod1024.redis-windows-fork -e --accept-package-agreements
```

### Start everything

```powershell
# From the project root:
.\start.ps1
```

This single script will:
1. ✅ Check PostgreSQL is running
2. ✅ Start Redis 8.10.1 on port 6380
3. ✅ Install backend deps if needed, run Prisma migrations, seed DB
4. ✅ Start the backend API on **http://localhost:3000**
5. ✅ Start the Next.js website on **http://localhost:3001**
6. ✅ Wait for health checks and print all URLs

### Stop everything

```powershell
.\stop.ps1
```

### URLs when running

| Service | URL |
|---|---|
| 🌐 Website (landing page) | http://localhost:3001 |
| 📱 APK Download page | http://localhost:3001/download |
| 📊 Web Dashboard | http://localhost:3001/dashboard |
| 🚀 Backend API | http://localhost:3000 |
| 🏥 API Health check | http://localhost:3000/health |
| 📋 BullMQ Monitor | http://localhost:3000/admin/queues |

### Manual startup (if start.ps1 doesn't fit your setup)

```powershell
# Terminal 1 — Redis 8
cd "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\taizod1024.redis-windows-fork_...\Redis-8.10.1-Windows-x64-msys2"
.\redis-server.exe --port 6380

# Terminal 2 — Backend
cd backend
npm install
npx prisma migrate dev   # first time only
npm run prisma:seed      # first time only
npm run dev              # starts on http://localhost:3000

# Terminal 3 — Website
cd website
npm install
npm run dev              # starts on http://localhost:3001
```

---

## 🌍 Deploy to Production

### Architecture

```
Vercel (Website)  ──→  Railway (Backend API)  ──→  Railway PostgreSQL
                                               ──→  Railway Redis
                                               ──→  Setu AA (bank sync)
```

### Step 1 — Deploy Backend to Railway

1. Create a free account at [railway.app](https://railway.app)
2. **New Project → Deploy from GitHub repo → select this repo**
3. Railway auto-detects `railway.json` — build + start commands are pre-configured
4. Add a **PostgreSQL** plugin and a **Redis** plugin from the Railway dashboard
5. Set environment variables (Railway auto-injects `DATABASE_URL` and `REDIS_URL`):

```
# Copy from backend/.env.production.example and fill in real values
NODE_ENV=production
JWT_SECRET=<generate: node -e "console.log(require('crypto').randomBytes(48).toString('hex'))">
JWT_REFRESH_SECRET=<generate another one>
CORS_ORIGIN=https://your-site.vercel.app
```

6. Click **Deploy** — Railway runs `npm ci && prisma generate && npm run build` then `prisma migrate deploy && node dist/index.js`
7. Note your backend URL e.g. `https://moneytracker-api.railway.app`

### Step 2 — Deploy Website to Vercel

1. Create a free account at [vercel.com](https://vercel.com)
2. **New Project → Import from GitHub → select this repo**
3. Set **Root Directory** to `website`
4. Set these Environment Variables in Vercel dashboard:

```
NEXT_PUBLIC_API_URL=https://moneytracker-api.railway.app
NEXT_PUBLIC_SITE_URL=https://your-site.vercel.app
NEXT_PUBLIC_APP_NAME=MoneyTracker
NEXT_PUBLIC_APP_VERSION=1.0.0
```

5. Click **Deploy** — Vercel auto-detects Next.js and builds it

6. After deploy, update `CORS_ORIGIN` in Railway to match your Vercel URL.

> **API proxy**: `website/vercel.json` automatically rewrites `/api/backend/*` → your Railway backend, so the website never exposes the backend URL to browsers directly.

### Step 3 — Deploy via Vercel CLI (alternative)

```bash
# Install Vercel CLI
npm install -g vercel

# From the website directory:
cd website
vercel --prod

# Set env vars:
vercel env add NEXT_PUBLIC_API_URL production
# → enter: https://moneytracker-api.railway.app

vercel env add NEXT_PUBLIC_SITE_URL production
# → enter: https://your-site.vercel.app
```

### Step 4 — APK distribution

When you push a git tag like `v1.0.1`:
- GitHub Actions builds the signed release APK
- Uploads it to GitHub Releases
- Updates `website/public/releases/latest.json` with the new URL + SHA256
- Vercel auto-redeploys the website with the new download info

```powershell
git tag v1.0.1
git push origin v1.0.1
```

---

## 📱 Android App

Open `android/` in **Android Studio** (latest stable).

1. Sync Gradle
2. Set the backend URL in `android/app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "API_BASE_URL", '"http://10.0.2.2:3000/api/v1/"')
   // 10.0.2.2 = localhost from Android emulator
   // Replace with your Railway URL for release builds
   ```
3. Run on emulator or device

For a signed release APK, see **Task 17** in the plan or the `.github/workflows/android-release.yml` workflow.

---

## 🗄️ Database Setup (first time)

```powershell
cd backend
npm install

# Create the DB user + database (PostgreSQL must be running)
psql -U postgres -c "CREATE USER moneytracker WITH PASSWORD 'devpassword' CREATEDB;"
psql -U postgres -c "CREATE DATABASE moneytracker OWNER moneytracker;"

# Run migrations
npx prisma migrate dev --name init

# Seed: 13 banks, 262 categories, 30 merchants
npm run prisma:seed
```

---

## 🔑 Environment Variables Reference

### Backend (`backend/.env`)

| Variable | Required | Description |
|---|---|---|
| `DATABASE_URL` | ✅ | PostgreSQL connection string |
| `REDIS_URL` | ✅ | Redis connection string |
| `JWT_SECRET` | ✅ | 48+ char random secret for access tokens |
| `JWT_REFRESH_SECRET` | ✅ | 48+ char random secret for refresh tokens |
| `PORT` | | Server port (default: 3000) |
| `NODE_ENV` | | `development` or `production` |
| `CORS_ORIGIN` | ✅ prod | Comma-separated allowed origins |
| `SETU_AA_CLIENT_ID` | AA only | Setu sandbox/prod client ID |
| `SETU_AA_CLIENT_SECRET` | AA only | Setu client secret |
| `SETU_WEBHOOK_SECRET` | AA only | HMAC secret for webhook verification |
| `AA_FIU_PRIVATE_KEY` | AA only | RSA private key PEM for JWE decryption |
| `FIREBASE_PROJECT_ID` | push notif | Firebase project ID |
| `FIREBASE_PRIVATE_KEY` | push notif | Firebase service account private key |
| `FIREBASE_CLIENT_EMAIL` | push notif | Firebase service account email |

### Website (`website/.env.local`)

| Variable | Required | Description |
|---|---|---|
| `NEXT_PUBLIC_API_URL` | ✅ | Backend URL e.g. `https://api.railway.app` |
| `NEXT_PUBLIC_SITE_URL` | | Website URL e.g. `https://moneytracker.vercel.app` |
| `NEXT_PUBLIC_APP_NAME` | | App display name |
| `NEXT_PUBLIC_APP_VERSION` | | App version string |

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Android | Kotlin + Jetpack Compose, Hilt, Room, Retrofit, WorkManager |
| Backend | Node.js + TypeScript + Express, Prisma ORM, Zod |
| Jobs | BullMQ + Redis 8 |
| Database | PostgreSQL 15+ |
| Website | Next.js 14 + Tailwind CSS + Recharts |
| Hosting | Vercel (website) + Railway (backend + DB + Redis) |
| Bank sync | Setu Account Aggregator (sandbox → production) |
| SMS import | Android READ_SMS + regex parser |
| CI/CD | GitHub Actions |

---

## 📁 Key Files

```
backend/
├── src/index.ts                              # Express app entry point
├── src/integrations/transactionEngine/      # Normalize → dedup → categorise → store
├── src/integrations/aa/                     # Setu AA client + JWE decryptor
├── src/queues/index.ts                      # BullMQ queue definitions
├── src/workers/                             # Background job workers
├── src/sync/routes.ts                       # Delta sync API
├── prisma/schema.prisma                     # Database schema (15 tables)
└── prisma/seed.ts                           # Seed data (banks, categories, merchants)

website/
├── src/app/page.tsx                         # Marketing landing page
├── src/app/download/page.tsx                # APK download page
├── src/app/(dashboard)/dashboard/page.tsx   # Web dashboard with charts
├── src/lib/api.ts                           # Shared API client
└── public/releases/latest.json             # APK version metadata

android/
├── app/src/main/java/com/moneytracker/
│   ├── feature/dashboard/DashboardScreen.kt # Animated balance, charts
│   ├── feature/transactions/                # Filter + search + list
│   ├── core/sms/SmsParser.kt               # Bank SMS regex parser
│   ├── core/sync/SyncWorker.kt             # 15-min background sync
│   └── core/notifications/                 # FCM push handler
└── app/src/main/res/mipmap-*/ic_launcher*   # App icons (all densities)
```

---

## 🛡️ Security

- **No bank credentials stored** — bank sync uses RBI-regulated Account Aggregator (AA)
- **Biometric lock** + 5-minute app-lock timeout
- **FLAG_SECURE** blocks screenshots in release builds
- **EncryptedSharedPreferences** for local token storage
- **TLS 1.3** for all API calls; cleartext blocked via `network_security_config.xml`
- **IDOR protection** — every DB query scoped to authenticated user
- **Audit logs** — all mutations logged to `audit_logs` table
- **Input sanitisation** middleware strips HTML/JS from all request bodies
- **Rate limiting** — 300 req/15min global, 10 req/15min on auth endpoints

---

## 📜 License

MIT — see [LICENSE](LICENSE)

---

## 🔗 Links

- **GitHub**: https://github.com/legend-ron2025/Money-Transfer
- **Download APK**: https://moneytracker.vercel.app/download
- **Web Dashboard**: https://moneytracker.vercel.app/dashboard
- **Privacy Policy**: https://moneytracker.vercel.app/privacy
