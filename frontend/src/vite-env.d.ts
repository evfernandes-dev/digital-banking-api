/// <reference types="vite/client" />

// Variáveis de ambiente expostas pelo Vite.
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string
  readonly VITE_API_TARGET?: string
}

// Tipagem de import.meta.env.
interface ImportMeta {
  readonly env: ImportMetaEnv
}
