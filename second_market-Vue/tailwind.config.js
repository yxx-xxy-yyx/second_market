/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        primary: 'rgb(54 179 194 / <alpha-value>)',
        primaryDark: 'rgb(38 143 156 / <alpha-value>)',
        primaryLight: 'rgb(98 198 210 / <alpha-value>)',
        ivory: 'rgb(250 249 246 / <alpha-value>)'
      },
      boxShadow: {
        'cyan-soft': '0 1px 12px rgba(54, 179, 194, 0.14)'
      },
      borderRadius: {
        xl: '12px',
        '2xl': '16px',
        '3xl': '24px'
      }
    },
  },
  plugins: [],
}

