# 💰 MoneyTracker

A production-ready personal finance app for India — Android (Kotlin + Compose), Node.js backend, and a Next.js marketing + web-dashboard site.

---

## Architecture

```
Android App (Kotlin + Compose)
        ↓  HTTPS/REST
    API Gateway (Express)
        ↓
  ┌─────┴──────────────┐
  Auth  Transactions  Analytics  Sync  Consents
        ↓
  Transaction Intelligence Engine
  (normalize → deduplicate → categorise → detect)
        ↓
  PostgreSQL + Redis + BullMQ Workers
        ↓
  Account Aggregator (Setu sandbox)
  Bank SMS parsing
```

---

## Quick Start (Local)

### 1. Start services

```bash
docker-compose -f infrastructure/docker-compose.dev.yml up -d
```

### 2. Backend

```bash
cd backend
cp .env.example .env    # fill in values
npm install
npx prisma migrate dev --name init
npm run prisma:seed
npm run dev             # http://localhost:3000
```

### 3. Website

```bash
cd website
npm install
npm run dev             # http://localhost:3001
```

### 4. Android

Open `android/` in Android Studio. Sync Gradle. Run on emulator or device.

Set `BuildConfig.API_BASE_URL` to `http://10.0.2.2:3000/api/v1/` for the emulator.

---

## Deployment

| Component | Platform | Trigger |
|-----------|----------|---------|
| Backend   | Railway  | Push to `main` (backend path) |
| Website   | Vercel   | Push to `main` (website path) |
| Android APK | GitHub Releases | Push tag `v*.*.*` |

### GitHub Secrets Required

| Secret | Description |
|--------|-------------|
| `RAILWAY_TOKEN` | Railway deploy token |
| `PROD_DATABASE_URL` | Production PostgreSQL URL |
| `PROD_API_URL` | Deployed backend URL |
| `VERCEL_TOKEN` | Vercel deploy token |
| `VERCEL_ORG_ID` | Vercel org ID |
| `VERCEL_PROJECT_ID` | Vercel project ID |
| `KEYSTORE_BASE64` | Release keystore encoded as base64 |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias in keystore |
| `KEY_PASSWORD` | Key password |

### Generate release keystore

```bash
keytool -genkey -v \
  -keystore moneytracker-release.keystore \
  -alias moneytracker \
  -keyalg RSA -keysize 2048 -validity 10000

# Encode for GitHub secret
base64 -i moneytracker-release.keystore | pbcopy
```

---

## Features

- **Bank sync** via India's Account Aggregator (Setu sandbox)
- **SMS auto-import** — reads bank transaction alerts via READ_SMS
- **Transaction Intelligence Engine** — 50+ merchant lookup, 20+ keyword rules, deduplication, transfer detection, recurring detection
- **Budgets & Goals** with real-time progress
- **Subscription detector** — automatic recurring payment detection
- **Smart insights** — spending changes, upcoming payments, budget alerts
- **BullMQ background jobs** — analytics cache, AA data fetch, FCM push
- **Offline-first** with 15-minute periodic WorkManager sync
- **Security** — biometric lock, 5-min app-lock timeout, FLAG_SECURE, backup exclusions, cleartext blocked, IDOR guard, audit logs, input sanitisation

---

## Project Structure

```
moneytracker/
├── android/          # Kotlin + Compose Android app
├── backend/          # Node.js + TypeScript + Express API
│   └── src/
│       ├── auth/     integrations/transactionEngine/
│       ├── analytics/integrations/aa/
│       ├── sync/     queues/   workers/
│       └── ...
├── website/          # Next.js 14 marketing + web dashboard (Vercel)
├── infrastructure/   # Docker Compose (dev + production)
└── .github/workflows/
    ├── backend-ci.yml      backend-deploy.yml
    ├── android-ci.yml      android-release.yml
    └── website-deploy.yml
```

---

## Account Aggregator Setup (Setu Sandbox)

1. Register at [aa-sandbox.setu.co](https://aa-sandbox.setu.co)
2. Add your `SETU_AA_CLIENT_ID` and `SETU_AA_CLIENT_SECRET` to `.env`
3. Set your webhook URL: `https://your-api.railway.app/api/v1/consents/webhook`
4. Generate an RSA key pair for JWE decryption:

```bash
openssl genrsa -out fiu-private.pem 2048
openssl rsa -in fiu-private.pem -pubout -out fiu-public.pem
# Add fiu-private.pem content to AA_FIU_PRIVATE_KEY in .env
```

---

## License

MIT
