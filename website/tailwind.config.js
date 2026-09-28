/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,ts,jsx,tsx,mdx}'],
  // Use 'media' so dark mode works automatically from OS preference, no JS needed
  darkMode: 'media',
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#f0edff',
          100: '#e0dcff',
          200: '#c7bdff',
          300: '#a392ff',
          400: '#856dff',
          500: '#6c63ff',
          600: '#5a50e6',
          700: '#4d42cc',
          800: '#4138a6',
          900: '#383185',
          950: '#231f6b',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      borderRadius: {
        xl:  '0.75rem',
        '2xl': '1rem',
        '3xl': '1.5rem',
        '4xl': '2rem',
      },
      boxShadow: {
        'glow':    '0 0 40px -8px rgba(108, 99, 255, 0.35)',
        'glow-sm': '0 0 20px -4px rgba(108, 99, 255, 0.25)',
        'card':    '0 1px 3px 0 rgb(0 0 0 / 0.07), 0 1px 2px -1px rgb(0 0 0 / 0.07)',
        'card-hover': '0 12px 40px -8px rgb(0 0 0 / 0.12)',
      },
      animation: {
        'fade-up':    'fadeUp 0.6s ease-out both',
        'fade-in':    'fadeIn 0.5s ease-out both',
        'float':      'float 3s ease-in-out infinite',
        'pulse-slow': 'pulse 3s ease-in-out infinite',
      },
      keyframes: {
        fadeUp:  { '0%': { opacity: '0', transform: 'translateY(24px)' }, '100%': { opacity: '1', transform: 'translateY(0)' } },
        fadeIn:  { '0%': { opacity: '0' }, '100%': { opacity: '1' } },
        float:   { '0%, 100%': { transform: 'translateY(0)' }, '50%': { transform: 'translateY(-8px)' } },
      },
      backgroundImage: {
        'hero-gradient':    'linear-gradient(180deg, #f0edff 0%, #ffffff 100%)',
        'primary-gradient': 'linear-gradient(135deg, #6c63ff 0%, #4d42cc 100%)',
        'card-gradient':    'linear-gradient(135deg, rgba(108,99,255,0.08) 0%, rgba(77,66,204,0.04) 100%)',
      },
    },
  },
  plugins: [],
};
