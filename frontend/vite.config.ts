import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/create': 'http://localhost:8080', // Proxy requests to the backend
    },
  },
});
