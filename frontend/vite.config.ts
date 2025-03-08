import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    proxy: {
      '/create': 'http://localhost:8080', // Proxy the /create endpoint for shortening URLs
      '^/([a-zA-Z0-9]+)$': {
        target: 'http://localhost:8080', // Proxy dynamic short URL requests (e.g., /Fy4iZV)
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/([a-zA-Z0-9]+)$/, '/$1'), // Forward the short code to the backend
      },
    },
    allowedHosts: ['ururl.xyz', 'localhost', '192.168.50.94'], // Allow your domain and other hosts
  },
});
