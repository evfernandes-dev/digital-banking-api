import { useState, type FormEvent } from 'react'
import { contasApi } from '../api/contas'
import { ApiError } from '../api/client'
import type { ContaResponse } from '../api/types'
import { Mensagem } from './Mensagem'

// Propriedades do formulário de nova conta.
interface NovaContaFormProps {
  onCriada: (conta: ContaResponse) => void
  onCancelar: () => void
}

// Formulário para criar uma conta.
export function NovaContaForm({ onCriada, onCancelar }: NovaContaFormProps) {
  // Nome digitado pelo usuário.
  const [titular, setTitular] = useState('')
  // Indica envio em andamento.
  const [enviando, setEnviando] = useState(false)
  // Mensagem de erro.
  const [erro, setErro] = useState<string | null>(null)

  // Envia o nome do titular para a API.
  async function enviar(evento: FormEvent) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    try {
      const conta = await contasApi.criar(titular.trim())
      setTitular('')
      onCriada(conta)
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Não foi possível criar a conta.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form className="formulario" onSubmit={enviar}>
      {/* Erro de criação, se houver. */}
      {erro && <Mensagem tipo="erro" texto={erro} />}

      {/* Campo do nome do titular. */}
      <label className="campo">
        <span>Nome do titular</span>
        <input
          className="input"
          value={titular}
          onChange={(evento) => setTitular(evento.target.value)}
          placeholder="Ex.: Ana Silva"
          maxLength={120}
          required
        />
      </label>

      {/* Ações do formulário. */}
      <div className="acoes">
        <button type="submit" className="btn btn--primario" disabled={enviando}>
          {enviando ? 'Criando...' : 'Criar conta'}
        </button>
        <button type="button" className="btn btn--secundario" onClick={onCancelar} disabled={enviando}>
          Cancelar
        </button>
      </div>
    </form>
  )
}
