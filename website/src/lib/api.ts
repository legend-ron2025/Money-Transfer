/**
 * Shared API client for the MoneyTracker website.
 *
 * Locally:   calls http://localhost:3000/api/v1/...
 * On Vercel: calls the backend via the /api/backend/ rewrite proxy,
 *            which Next.js rewrites to NEXT_PUBLIC_API_URL/api/v1/...
 *
 * The /api/backend/ prefix works in both environments because:
 *   - locally  → next.config.js rewrites it → http://localhost:3000/api/v1/...
 *   - vercel   → vercel.json  rewrites it   → https://your-backend.railway.app/api/v1/...
 */

const BASE =
  typeof window !== 'undefined'
    ? '/api/backend'                                 // client-side: use rewrite proxy
    : `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:3000'}/api/v1`; // server-side: direct

// ── Low-level fetch wrapper ──────────────────────────────────────────────────
async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const url = `${BASE}${path}`;
  const res = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    // Don't cache API responses
    next: { revalidate: 0 },
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(err.message ?? `API error ${res.status}`);
  }
  return res.json() as Promise<T>;
}

// ── Authenticated fetch ──────────────────────────────────────────────────────
export function apiAuth<T>(path: string, token: string, options: RequestInit = {}): Promise<T> {
  return apiFetch<T>(path, {
    ...options,
    headers: { Authorization: `Bearer ${token}`, ...(options.headers ?? {}) },
  });
}

// ── Auth ─────────────────────────────────────────────────────────────────────
export interface LoginResponse {
  data: {
    user: { id: string; email: string; name: string | null };
    accessToken: string;
    refreshToken: string;
  };
}

export const auth = {
  login:    (email: string, password: string) =>
    apiFetch<LoginResponse>('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),

  register: (name: string, email: string, password: string) =>
    apiFetch<LoginResponse>('/auth/register', { method: 'POST', body: JSON.stringify({ name, email, password }) }),
};

// ── Analytics ────────────────────────────────────────────────────────────────
export const analytics = {
  overview:       (token: string, period = 'month') =>
    apiAuth<any>(`/analytics/overview?period=${period}`, token),

  spending:       (token: string, period = 'month') =>
    apiAuth<any>(`/analytics/spending?period=${period}`, token),

  insights:       (token: string) =>
    apiAuth<any>('/analytics/insights', token),

  subscriptions:  (token: string) =>
    apiAuth<any>('/analytics/subscriptions', token),

  trends:         (token: string, months = 6) =>
    apiAuth<any>(`/analytics/trends?months=${months}`, token),
};

// ── Transactions ─────────────────────────────────────────────────────────────
export const transactions = {
  list: (token: string, params = '') =>
    apiAuth<any>(`/transactions${params ? '?' + params : ''}`, token),
};

// ── Accounts ─────────────────────────────────────────────────────────────────
export const accounts = {
  list: (token: string) =>
    apiAuth<any>('/accounts', token),
};

// ── Budgets / Goals ──────────────────────────────────────────────────────────
export const budgets = {
  list: (token: string) =>
    apiAuth<any>('/budgets', token),
};

export const goals = {
  list: (token: string) =>
    apiAuth<any>('/goals', token),
};

// ── Health check ─────────────────────────────────────────────────────────────
export const health = {
  check: () =>
    fetch(
      typeof window !== 'undefined'
        ? '/api/backend/health'.replace('/api/v1', '')  // strip extra segment
        : `${process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:3000'}/health`,
    ).then(r => r.json()),
};

export default apiFetch;
