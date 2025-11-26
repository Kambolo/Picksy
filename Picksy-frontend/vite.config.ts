import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: '0.0.0.0', // 👈 To pozwala na dostęp z zewnątrz
    port: 5173, // Twój port
  },
  plugins: [react()],
})
