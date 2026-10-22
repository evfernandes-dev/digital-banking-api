// Propriedades da mensagem.
interface MensagemProps {
  tipo: 'erro' | 'sucesso'
  texto: string
}

// Exibe uma mensagem de erro ou sucesso.
export function Mensagem({ tipo, texto }: MensagemProps) {
  return (
    <p className={`mensagem mensagem--${tipo}`} role="status">
      {texto}
    </p>
  )
}
