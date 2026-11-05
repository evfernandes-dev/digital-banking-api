// Endereço dos endpoints de contas.
const API = '/contas'

// Dados usados na tela.
let contas = []
let contaSelecionada = null

// Elementos da página.
const formConta = document.getElementById('form-conta')
const campoTitular = document.getElementById('titular')
const mensagem = document.getElementById('mensagem')
const aviso = document.getElementById('aviso')
const tabelaContas = document.getElementById('tabela-contas')
const corpoContas = document.getElementById('corpo-contas')
const detalhe = document.getElementById('detalhe')
const detalheTitular = document.getElementById('detalhe-titular')
const detalheNumero = document.getElementById('detalhe-numero')
const detalheSaldo = document.getElementById('detalhe-saldo')
const formDeposito = document.getElementById('form-deposito')
const formSaque = document.getElementById('form-saque')
const valorDeposito = document.getElementById('valor-deposito')
const valorSaque = document.getElementById('valor-saque')
const btnVoltar = document.getElementById('btn-voltar')

// Formata um valor em reais.
function formatarMoeda(valor) {
  return Number(valor).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  })
}

// Mostra uma mensagem de erro ou sucesso.
function mostrarMensagem(texto, tipo) {
  mensagem.textContent = texto
  mensagem.className = 'mensagem mensagem--' + tipo
}

// Esconde a mensagem.
function esconderMensagem() {
  mensagem.className = 'mensagem escondido'
}

// Busca as contas na API.
async function carregarContas() {
  try {
    const resposta = await fetch(API)

    if (!resposta.ok) {
      throw new Error('Não foi possível carregar as contas.')
    }

    contas = await resposta.json()
    mostrarContas()
  } catch (erro) {
    aviso.textContent = erro.message
  }
}

// Mostra as contas na tabela.
function mostrarContas() {
  if (contas.length === 0) {
    aviso.textContent = 'Nenhuma conta cadastrada.'
    aviso.classList.remove('escondido')
    tabelaContas.classList.add('escondido')
    return
  }

  aviso.classList.add('escondido')
  tabelaContas.classList.remove('escondido')
  corpoContas.innerHTML = ''

  for (const conta of contas) {
    const linha = document.createElement('tr')

    const celulaTitular = document.createElement('td')
    celulaTitular.textContent = conta.titular

    const celulaNumero = document.createElement('td')
    celulaNumero.textContent = conta.numero

    const celulaSaldo = document.createElement('td')
    celulaSaldo.textContent = formatarMoeda(conta.saldo)

    const celulaBotao = document.createElement('td')
    const botao = document.createElement('button')
    botao.type = 'button'
    botao.className = 'botao'
    botao.textContent = 'Abrir'
    botao.addEventListener('click', () => abrirConta(conta))
    celulaBotao.appendChild(botao)

    linha.appendChild(celulaTitular)
    linha.appendChild(celulaNumero)
    linha.appendChild(celulaSaldo)
    linha.appendChild(celulaBotao)
    corpoContas.appendChild(linha)
  }
}

// Abre o detalhe de uma conta.
function abrirConta(conta) {
  contaSelecionada = conta
  detalheTitular.textContent = conta.titular
  detalheNumero.textContent = 'Conta ' + conta.numero
  detalheSaldo.textContent = formatarMoeda(conta.saldo)
  detalhe.classList.remove('escondido')
  esconderMensagem()
  detalhe.scrollIntoView({ behavior: 'smooth' })
}

// Atualiza uma conta na lista e no detalhe.
function atualizarConta(contaAtualizada) {
  contas = contas.map((conta) => (conta.id === contaAtualizada.id ? contaAtualizada : conta))
  mostrarContas()
  abrirConta(contaAtualizada)
}

// Cria uma conta.
formConta.addEventListener('submit', async (evento) => {
  evento.preventDefault()
  esconderMensagem()

  try {
    const resposta = await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ titular: campoTitular.value.trim() }),
    })
    const dados = await resposta.json()

    if (!resposta.ok) {
      throw new Error(dados.mensagem || 'Não foi possível criar a conta.')
    }

    contas.push(dados)
    campoTitular.value = ''
    mostrarContas()
    abrirConta(dados)
    mostrarMensagem('Conta criada com sucesso.', 'sucesso')
  } catch (erro) {
    mostrarMensagem(erro.message, 'erro')
  }
})

// Executa um depósito ou saque.
async function operar(caminho, valor, campo) {
  esconderMensagem()

  try {
    const resposta = await fetch(API + '/' + contaSelecionada.id + '/' + caminho, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ valor: Number(valor) }),
    })
    const dados = await resposta.json()

    if (!resposta.ok) {
      throw new Error(dados.mensagem || 'Não foi possível concluir a operação.')
    }

    campo.value = ''
    atualizarConta(dados)
    mostrarMensagem(caminho === 'deposito' ? 'Depósito realizado.' : 'Saque realizado.', 'sucesso')
  } catch (erro) {
    mostrarMensagem(erro.message, 'erro')
  }
}

formDeposito.addEventListener('submit', (evento) => {
  evento.preventDefault()
  operar('deposito', valorDeposito.value, valorDeposito)
})

formSaque.addEventListener('submit', (evento) => {
  evento.preventDefault()
  operar('saque', valorSaque.value, valorSaque)
})

btnVoltar.addEventListener('click', () => {
  detalhe.classList.add('escondido')
  contaSelecionada = null
  esconderMensagem()
})

// Inicia a tela.
carregarContas()
