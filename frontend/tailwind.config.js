/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#1e3a8a',
          light: '#2563eb', // lighter shade for hover
          dark: '#1e3a8a',  // deep blue
        },
        secondary: {
          DEFAULT: '#f3f4f6',
          dark: '#e5e7eb',
        },
        accent: {
          DEFAULT: '#f97316',
          hover: '#ea580c',
        }
      },
      fontFamily: {
        sans: ['Inter', '"Noto Sans SC"', '"Source Han Sans CN"', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'card': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        'card-hover': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
      }
    },
  },
  plugins: [],
}
