import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Sempre na 5173. Se ela estiver ocupada (outro "npm run dev" aberto), o Vite avisa em vez de
    // trocar sozinho para 5174/5175, o que fazia a API recusar o login (erro 403 de CORS).
    port: 5173,
    strictPort: true,
    // Chamadas para /api vão para o Spring Boot (setup-back).
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        // Para a API, a chamada vem do próprio proxy (mesma origem), então o CORS não entra em jogo,
        // seja qual for a porta ou o endereço usado para abrir o site.
        configure: (proxy) => proxy.on('proxyReq', (req) => req.removeHeader('origin')),
      },
    },
  },
})
