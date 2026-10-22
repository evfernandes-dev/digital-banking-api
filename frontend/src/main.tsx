import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { App } from './App'
import './index.css'

// Ponto de entrada do React.
const container = document.getElementById('root')

// Garante que o elemento raiz existe.
if (!container) {
  throw new Error('Elemento #root não encontrado no index.html.')
}

// Renderiza o componente App dentro do StrictMode.
createRoot(container).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
