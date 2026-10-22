import { useState, type FormEvent } from 'react'
import { contasApi } from '../api/contas'
import { ApiError } from '../api/client'
import type { ContaResponse } from '../api/types'
import { formatarMoeda } from '../lib/formatters'
import { Mensagem } from './Mensagem'

// Tipos de operação disponíveis na conta.
type TipoOperacao = 'deposito' | 'saque'

// Propriedades recebidas do componente pai.
interface ContaDetalheProps {
  conta: ContaResponse
  onVoltar: () => void
  onAtualizada: (conta: ContaResponse) => void
}

// Tela com o detalhe da conta e as operações.
export function ContaDetalhe({ conta, onVoltar, onAtualizada }: ContaDetalheProps) {
  // Operação aberta no momento.
  const [operacao, setOperacao] = useState<TipoOperacao | null>(null)
  // Valor digitado pelo usuário.
  const [valor, setValor] = useState('')
  // Indica envio em andamento.
  const [enviando, setEnviando] = useState(false)
  // Mensagem de sucesso.
  const [mensagem, setMensagem] = useState<string | null>(null)
  // Mensagem de erro.
  const [erro, setErro] = useState<string | null>(null)

  // Abre o formulário da operação escolhida.
  function abrirOperacao(tipo: TipoOperacao) {
    setOperacao(tipo)
    setValor('')
    setMensagem(null)
    setErro(null)
  }

  // Valida o valor e envia a operação.
  async function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (!operacao) {
      return
    }

    setErro(null)
    setMensagem(null)

    // Converte o texto digitado em número.
    const numero = Number(valor)
    if (!Number.isFinite(numero) || numero <= 0) {
      setErro('Informe um valor maior que zero.')
      return
    }

    setEnviando(true)
    try {
      // Chama depósito ou saque conforme a operação.
      const atualizada =
        operacao === 'deposito'
          ? await contasApi.depositar(conta.id, numero)
          : await contasApi.sacar(conta.id, numero)

      // Atualiza a tela com o novo saldo.
      onAtualizada(atualizada)
      setMensagem(operacao === 'deposito' ? 'Depósito realizado com sucesso.' : 'Saque realizado com sucesso.')
      setOperacao(null)
      setValor('')
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Não foi possível concluir a operação.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section className="cartao">
      {/* Volta para a listagem. */}
      <button type="button" className="btn btn--texto" onClick={onVoltar}>
        ← Voltar
      </button>

      {/* Dados do titular e da conta. */}
      <h2 className="conta-titular">{conta.titular}</h2>
      <p className="conta-numero">Conta {conta.numero}</p>

      {/* Saldo disponível. */}
      <div className="saldo">
        <span className="saldo__rotulo">Saldo disponível</span>
        <span className="saldo__valor">{formatarMoeda(conta.saldo)}</span>
      </div>

      {/* Mensagens de retorno. */}
      {mensagem && <Mensagem tipo="sucesso" texto={mensagem} />}
      {erro && <Mensagem tipo="erro" texto={erro} />}

      {operacao === null ? (
        // Botões para escolher a operação.
        <div className="acoes">
          <button type="button" className="btn btn--primario" onClick={() => abrirOperacao('deposito')}>
            Depositar
          </button>
          <button type="button" className="btn btn--secundario" onClick={() => abrirOperacao('saque')}>
            Sacar
          </button>
        </div>
      ) : (
        // Formulário de depósito ou saque.
        <form className="formulario" onSubmit={enviar}>
          <label className="campo">
            <span>Valor do {operacao === 'deposito' ? 'depósito' : 'saque'}</span>
            <input
              className="input"
              type="number"
              min="0.01"
              step="0.01"
              value={valor}
              onChange={(evento) => setValor(evento.target.value)}
              placeholder="0,00"
              autoFocus
              required
            />
          </label>
          <div className="acoes">
            <button type="submit" className="btn btn--primario" disabled={enviando}>
              {enviando ? 'Enviando...' : 'Confirmar'}
            </button>
            <button
              type="button"
              className="btn btn--secundario"
              onClick={() => setOperacao(null)}
              disabled={enviando}
            >
              Cancelar
            </button>
          </div>
        </form>
      )}
    </section>
  )
}
