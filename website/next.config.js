/** @type {import('next').NextConfig} */

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000';

const nextConfig = {
  // ── Images ──────────────────────────────────────────────────────────────
  images: {
    unoptimized: true,            // works for Vercel + self-hosted icons
    remotePatterns: [
      { protocol: 'https', hostname: 'i.postimg.cc' },
      { protocol: 'https', hostname: '**.github.io' },
      { protocol: 'https', hostname: 'avatars.githubusercontent.com' },
    ],
  },

  // ── API proxy rewrites ───────────────────────────────────────────────────
  // On Vercel: NEXT_PUBLIC_API_URL points to Railway/Render backend
  // Locally:   falls back to http://localhost:3000
  async rewrites() {
    return [
      {
        source:      '/api/backend/:path*',
        destination: `${API_URL}/api/v1/:path*`,
      },
    ];
  },

  // ── Security headers ─────────────────────────────────────────────────────
  async headers() {
    return [
      {
        source: '/(.*)',
        headers: [
          { key: 'X-Content-Type-Options', value: 'nosniff' },
          { key: 'X-Frame-Options',        value: 'DENY'    },
          { key: 'Referrer-Policy',        value: 'strict-origin-when-cross-origin' },
        ],
      },
      {
        // Serve APK with correct MIME type
        source: '/releases/(.*)\\.apk',
        headers: [
          { key: 'Content-Type',        value: 'application/vnd.android.package-archive' },
          { key: 'Content-Disposition', value: 'attachment' },
          { key: 'Cache-Control',       value: 'public, max-age=31536000, immutable' },
        ],
      },
    ];
  },

  // ── Environment variable exposure ────────────────────────────────────────
  env: {
    NEXT_PUBLIC_API_URL:      process.env.NEXT_PUBLIC_API_URL      || 'http://localhost:3000',
    NEXT_PUBLIC_SITE_URL:     process.env.NEXT_PUBLIC_SITE_URL     || 'http://localhost:3001',
    NEXT_PUBLIC_APP_NAME:     process.env.NEXT_PUBLIC_APP_NAME     || 'MoneyTracker',
    NEXT_PUBLIC_APP_VERSION:  process.env.NEXT_PUBLIC_APP_VERSION  || '1.0.0',
  },
};

module.exports = nextConfig;
