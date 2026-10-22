import type { ApiErrorResponse } from './types'

// URL base da API, ajustável por variável de ambiente.
const BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/+$/, '')

// Mensagens padrão por status HTTP.
const STATUS_MESSAGES: Record<number, string> = {
  400: 'Requisição inválida. Verifique os dados informados.',
  404: 'Recurso não encontrado.',
  405: 'Método não permitido para este recurso.',
  409: 'Operação não permitida para o estado atual da conta.',
  500: 'Erro interno no servidor.',
}

// Erro customizado com status e detalhes da API.
export class ApiError extends Error {
  readonly status: number
  readonly details: string[]

  // Monta o erro com status e lista de detalhes.
  constructor(status: number, message: string, details: string[] = []) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.details = details
  }
}

// Opções aceitas pelo cliente HTTP.
interface RequestOptions {
  method?: 'GET' | 'POST'
  body?: unknown
}

// Executa uma requisição e trata a resposta.
export async function apiFetch<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body } = options

  // Cabeçalhos padrão da requisição.
  const headers: Record<string, string> = { Accept: 'application/json' }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  // Envia a requisição para a API.
  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  // Lê o corpo como texto para aceitar respostas vazias.
  const text = await response.text()
  let payload: unknown
  if (text.length > 0) {
    try {
      payload = JSON.parse(text)
    } catch {
      payload = undefined
    }
  }

  // Converte respostas de erro em ApiError.
  if (!response.ok) {
    const apiError = payload as Partial<ApiErrorResponse> | undefined
    const message =
      apiError && typeof apiError.message === 'string' && apiError.message.length > 0
        ? apiError.message
        : STATUS_MESSAGES[response.status] ?? `Erro inesperado (HTTP ${response.status}).`
    const details = apiError && Array.isArray(apiError.details) ? apiError.details : []
    throw new ApiError(response.status, message, details)
  }

  // Devolve o corpo já tipado.
  return payload as T
}
