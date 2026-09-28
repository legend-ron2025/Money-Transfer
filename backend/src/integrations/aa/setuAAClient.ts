/**
 * Setu AA (Account Aggregator) sandbox client.
 * Register at: https://aa-sandbox.setu.co
 * Docs:        https://docs.setu.co/data/account-aggregator
 */

const BASE_URL    = process.env.SETU_AA_BASE_URL    ?? 'https://aa-sandbox.setu.co';
const CLIENT_ID   = process.env.SETU_AA_CLIENT_ID   ?? '';
const CLIENT_SECRET = process.env.SETU_AA_CLIENT_SECRET ?? '';

let cachedToken: { token: string; expiresAt: number } | null = null;

// ── Auth token ────────────────────────────────────────────────────────────────
async function getToken(): Promise<string> {
  if (cachedToken && Date.now() < cachedToken.expiresAt - 60_000) {
    return cachedToken.token;
  }

  const resp = await fetch(`${BASE_URL}/auth/token`, {
    method:  'POST',
    headers: { 'Content-Type': 'application/json' },
    body:    JSON.stringify({ clientId: CLIENT_ID, secret: CLIENT_SECRET }),
  });

  if (!resp.ok) throw new Error(`Setu auth failed: ${resp.status}`);
  const json = await resp.json() as { accessToken: string; expiresIn: number };

  cachedToken = {
    token:     json.accessToken,
    expiresAt: Date.now() + (json.expiresIn ?? 3600) * 1000,
  };
  return cachedToken.token;
}

async function post(path: string, body: unknown) {
  const token = await getToken();
  const resp  = await fetch(`${BASE_URL}${path}`, {
    method:  'POST',
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    body:    JSON.stringify(body),
  });
  if (!resp.ok) {
    const err = await resp.text();
    throw new Error(`Setu POST ${path} failed ${resp.status}: ${err}`);
  }
  return resp.json();
}

async function get(path: string) {
  const token = await getToken();
  const resp  = await fetch(`${BASE_URL}${path}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!resp.ok) throw new Error(`Setu GET ${path} failed ${resp.status}`);
  return resp.json();
}

// ── Consent ───────────────────────────────────────────────────────────────────
export interface CreateConsentOptions {
  userId:      string;
  redirectUrl: string;
  purpose?:    string;
  dateRange?:  { from: string; to: string };
}

export async function createConsent(opts: CreateConsentOptions) {
  const now        = new Date();
  const oneYear    = new Date(now.getTime() + 365 * 24 * 60 * 60 * 1000);
  const dataFrom   = opts.dateRange?.from ?? new Date(now.getTime() - 180 * 24 * 60 * 60 * 1000).toISOString();
  const dataTo     = opts.dateRange?.to   ?? now.toISOString();

  return post('/consents', {
    redirectUrl:  opts.redirectUrl,
    purpose:      { text: opts.purpose ?? 'Personal Finance Management' },
    consentTypes: ['TRANSACTIONS', 'BALANCE', 'PROFILE'],
    fiTypes:      ['DEPOSIT', 'CREDIT_CARD', 'RECURRING_DEPOSIT'],
    dataRange:    { from: dataFrom, to: dataTo },
    consentExpiry: oneYear.toISOString(),
  });
}

export async function getConsentStatus(consentHandle: string) {
  return get(`/consents/${consentHandle}`);
}

export async function revokeConsent(consentHandle: string) {
  return post(`/consents/${consentHandle}/revoke`, {});
}

// ── Data sessions ─────────────────────────────────────────────────────────────
export async function createDataSession(consentId: string, dateRange: { from: string; to: string }) {
  return post('/data/sessions', {
    consentId,
    dataRange: dateRange,
  });
}

export async function fetchFIData(sessionId: string) {
  return get(`/data/sessions/${sessionId}`);
}
