import { describe, expect, it } from 'vitest'
import { formatarMoeda } from './formatters'

// Testes da formatação de moeda.
describe('formatarMoeda', () => {
  // Deve incluir o símbolo do real.
  it('formata um valor com o símbolo do real', () => {
    expect(formatarMoeda(10)).toContain('R$')
  })

  // Deve usar vírgula como separador decimal.
  it('usa vírgula como separador decimal', () => {
    expect(formatarMoeda(1234.5)).toContain('1.234,50')
  })

  // Deve mostrar duas casas decimais no zero.
  it('formata o zero com duas casas decimais', () => {
    expect(formatarMoeda(0)).toContain('0,00')
  })
})
