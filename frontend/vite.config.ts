import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// O Front-End fala com o Back-End pelo proxy de desenvolvimento do Vite
// (prefixo /api), evitando configurar CORS no Back-End.
// O alvo do proxy pode ser ajustado pela variavel VITE_API_TARGET (padrao: 8081).
export default defineConfig(({ mode }) => {
  // Carrega as variáveis de ambiente do projeto.
  const env = loadEnv(mode, '.', '')
  // Endereço do Back-End usado pelo proxy.
  const target = env.VITE_API_TARGET || 'http://localhost:8081'

  return {
    // Ativa o plugin do React.
    plugins: [react()],
    // Configuração do servidor de desenvolvimento.
    server: {
      port: 5173,
      // Encaminha /api para o Back-End.
      proxy: {
        '/api': {
          target,
          changeOrigin: true,
          // Remove o prefixo /api antes de enviar.
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
  }
})
