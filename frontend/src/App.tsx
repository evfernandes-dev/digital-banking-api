import { useCallback, useEffect, useState } from 'react'
import { ApiError } from './api/client'
import { contasApi } from './api/contas'
import type { ContaResponse } from './api/types'
import { ContaDetalhe } from './components/ContaDetalhe'
import { ContaLista } from './components/ContaLista'
import { Mensagem } from './components/Mensagem'
import { NovaContaForm } from './components/NovaContaForm'

// Componente principal da interface.
export function App() {
  // Lista de contas carregadas da API.
  const [contas, setContas] = useState<ContaResponse[]>([])
  // Conta aberta no detalhe.
  const [selecionada, setSelecionada] = useState<ContaResponse | null>(null)
  // Controla a exibição do formulário de nova conta.
  const [criando, setCriando] = useState(false)
  // Indica que a listagem está carregando.
  const [carregando, setCarregando] = useState(true)
  // Mensagem de erro da listagem.
  const [erro, setErro] = useState<string | null>(null)

  // Busca as contas na API.
  const carregar = useCallback(async () => {
    setCarregando(true)
    setErro(null)
    try {
      setContas(await contasApi.listar())
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Não foi possível carregar as contas.')
    } finally {
      setCarregando(false)
    }
  }, [])

  // Carrega a lista ao abrir a tela.
  useEffect(() => {
    void carregar()
  }, [carregar])

  // Adiciona a nova conta e abre o detalhe.
  function aoCriar(conta: ContaResponse) {
    setContas((atual) => [...atual, conta])
    setCriando(false)
    setSelecionada(conta)
  }

  // Atualiza a conta após depósito ou saque.
  function aoAtualizar(conta: ContaResponse) {
    setSelecionada(conta)
    setContas((atual) => atual.map((item) => (item.id === conta.id ? conta : item)))
  }

  return (
    <div className="app">
      {/* Cabeçalho da aplicação. */}
      <header className="topo">
        <h1 className="topo__titulo">Digital Banking</h1>
        <p className="topo__subtitulo">Contas, depósitos e saques</p>
      </header>

      <main className="conteudo">
        {selecionada ? (
          // Mostra o detalhe da conta selecionada.
          <ContaDetalhe
            conta={selecionada}
            onVoltar={() => setSelecionada(null)}
            onAtualizada={aoAtualizar}
          />
        ) : (
          // Mostra a listagem e o formulário de criação.
          <section className="cartao">
            <div className="cartao__cabecalho">
              <h2>Contas cadastradas</h2>
              {!criando && (
                <button type="button" className="btn btn--primario" onClick={() => setCriando(true)}>
                  Nova conta
                </button>
              )}
            </div>

            {/* Formulário de nova conta. */}
            {criando && <NovaContaForm onCriada={aoCriar} onCancelar={() => setCriando(false)} />}
            {erro && <Mensagem tipo="erro" texto={erro} />}

            {carregando ? (
              <p className="vazio">Carregando...</p>
            ) : (
              <ContaLista contas={contas} onSelecionar={(conta) => setSelecionada(conta)} />
            )}
          </section>
        )}
      </main>
    </div>
  )
}
