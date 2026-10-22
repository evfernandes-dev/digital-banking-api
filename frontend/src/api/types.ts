// Corpo do pedido de criação de conta.
export interface ContaRequest {
  titular: string
}

// Conta devolvida pela API.
export interface ContaResponse {
  id: number
  numero: string
  titular: string
  saldo: number
}

// Resposta da consulta de saldo.
export interface SaldoResponse {
  id: number
  numero: string
  saldo: number
}

// Corpo das operações de depósito e saque.
export interface ValorRequest {
  valor: number
}

// Formato padrão de erro devolvido pela API.
export interface ApiErrorResponse {
  timestamp: string
  status: number
  error: string
  message: string
  details: string[]
}
