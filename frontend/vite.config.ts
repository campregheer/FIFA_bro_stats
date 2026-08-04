import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/players': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/teams': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/matches': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/statistics': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/dashboard': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
