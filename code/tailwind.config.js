/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,css,vue}",
    "./templates/**/*.{html,twig}",
  ],
  theme: {
    container: {
      center: true,
      padding: '2rem',
    },
    extend: {
      fontFamily: {
        'sans': ['Inter', 'ui-sans-serif', 'system-ui'],
        'serif': ['Noto Serif', 'ui-serif', 'Georgia'],
        'mono': ['ui-monospace', 'SFMono-Regular'],
      }
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}