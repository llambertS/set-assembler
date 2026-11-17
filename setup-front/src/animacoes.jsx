import { useEffect, useRef, useState } from 'react'
import { moeda } from './dados'

const semMovimento = () => window.matchMedia?.('(prefers-reduced-motion: reduce)').matches

// Número que "rola" até o valor novo (usado no total do setup).
export function useContador(alvo, duracao = 550) {
  const [valor, setValor] = useState(alvo)
  const atual = useRef(alvo)
  useEffect(() => {
    const d = semMovimento() ? 0 : duracao
    const inicio = atual.current
    const t0 = performance.now()
    let id
    const passo = (t) => {
      const p = d ? Math.min(1, (t - t0) / d) : 1
      const v = inicio + (alvo - inicio) * (1 - (1 - p) ** 3) // ease-out
      atual.current = v
      setValor(v)
      if (p < 1) id = requestAnimationFrame(passo)
    }
    id = requestAnimationFrame(passo)
    return () => cancelAnimationFrame(id)
  }, [alvo, duracao])
  return valor
}

export function Moeda({ valor }) {
  return <span className="numero">{moeda(useContador(valor))}</span>
}

// Placeholders "brilhando" enquanto os dados chegam da API.
export function Carregando({ cartoes = 6 }) {
  return (
    <div className="grade" aria-busy="true" aria-label="Carregando">
      {Array.from({ length: cartoes }, (_, i) => (
        <div key={i} className="cartao esqueleto" style={{ '--i': i }}>
          <span /><span /><span />
        </div>
      ))}
    </div>
  )
}

// Mensagem que entra deslizando e some sozinha.
export function Aviso({ texto, tipo = '', aoSumir, tempo = 4000 }) {
  useEffect(() => {
    if (!texto || !aoSumir) return
    const t = setTimeout(aoSumir, tempo)
    return () => clearTimeout(t)
  }, [texto, aoSumir, tempo])
  if (!texto) return null
  return <p key={texto} className={`aviso entra ${tipo}`} role="status">{texto}</p>
}
