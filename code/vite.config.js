import vue from '@vitejs/plugin-vue';
import ViteRestart from 'vite-plugin-restart';

// https://vitejs.dev/config/
export default ({command}) => ({
  base: command === 'dev' ? '' : '/dist/',
  build: {
    manifest: true,
    outDir: './web/dist/',
    rollupOptions: {
      input: {
        app: './src/app.js',
      }
    },
  },
  server: {
    host: '0.0.0.0',
    port: 3000,
    fs: {
      strict: false
    },
    origin: 'http://localhost:3000',
    strictPort: true,
  },
  plugins: [
    ViteRestart({
      reload: [
        './templates/**/*',
        './src/js/vue-components/*',
      ],
    }),
    vue(),
  ],
});