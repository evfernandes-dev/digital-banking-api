import { apiFetch } from './client'
import type { ContaRequest, ContaResponse, SaldoResponse, ValorRequest } from './types'

// Funções que chamam os endpoints de contas.
export const contasApi = {
  // Cria uma conta com o titular informado.
  criar: (titular: string) =>
    apiFetch<ContaResponse>('/contas', { method: 'POST', body: { titular } satisfies ContaRequest }),

  // Lista todas as contas.
  listar: () => apiFetch<ContaResponse[]>('/contas'),

  // Busca uma conta pelo id.
  buscar: (id: number) => apiFetch<ContaResponse>(`/contas/${id}`),

  // Consulta o saldo de uma conta.
  saldo: (id: number) => apiFetch<SaldoResponse>(`/contas/${id}/saldo`),

  // Deposita um valor na conta.
  depositar: (id: number, valor: number) =>
    apiFetch<ContaResponse>(`/contas/${id}/deposito`, {
      method: 'POST',
      body: { valor } satisfies ValorRequest,
    }),

  // Saca um valor da conta.
  sacar: (id: number, valor: number) =>
    apiFetch<ContaResponse>(`/contas/${id}/saque`, {
      method: 'POST',
      body: { valor } satisfies ValorRequest,
    }),
}
