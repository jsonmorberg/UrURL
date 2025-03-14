import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      '/create': 'http://localhost:8080',
      '^/([a-zA-Z0-9]+)$': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/([a-zA-Z0-9]+)$/, '/$1'),
      },
    },
    allowedHosts: ['ururl.xyz', 'localhost', '192.168.50.94'],
  },
});

