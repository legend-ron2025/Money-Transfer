/** @type {import('next').NextConfig} */
const nextConfig = {
  // No static export — we need server-side auth
  images: { unoptimized: true },
  async rewrites() {
    const api = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000';
    return [
      { source: '/api/backend/:path*', destination: `${api}/api/v1/:path*` },
    ];
  },
};

module.exports = nextConfig;
