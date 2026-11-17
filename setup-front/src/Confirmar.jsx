import { useEffect, useRef, useState } from 'react'

// Pop-up de confirmação no visual do site (substitui o window.confirm do navegador).
// Uso: <Confirmar aberto titulo="..." mensagem="..." aoConfirmar={async () => ...} aoCancelar={() => ...} />
export default function Confirmar({ aberto, titulo, mensagem, rotuloConfirmar = 'Excluir', perigo = true, aoConfirmar, aoCancelar }) {
  const [ocupado, setOcupado] = useState(false)
  const cancelar = useRef(null)

  useEffect(() => {
    if (!aberto) return
    cancelar.current?.focus()
    const tecla = (e) => { if (e.key === 'Escape') aoCancelar() }
    window.addEventListener('keydown', tecla)
    return () => window.removeEventListener('keydown', tecla)
  }, [aberto, aoCancelar])

  if (!aberto) return null

  async function confirmar() {
    setOcupado(true)
    try { await aoConfirmar() } finally { setOcupado(false) }
  }

  return (
    <div className="modal-fundo" onMouseDown={(e) => { if (e.target === e.currentTarget && !ocupado) aoCancelar() }}>
      <div className="modal painel" role="alertdialog" aria-modal="true" aria-labelledby="modal-titulo" aria-describedby="modal-texto">
        <div className={`modal-icone ${perigo ? 'perigo' : ''}`} aria-hidden>{perigo ? '🗑️' : '❔'}</div>
        <h2 id="modal-titulo">{titulo}</h2>
        <p id="modal-texto" className="suave">{mensagem}</p>
        <div className="acoes modal-acoes">
          <button ref={cancelar} className="sec" onClick={aoCancelar} disabled={ocupado}>Cancelar</button>
          <button className={`${perigo ? 'botao-perigo' : ''} ${ocupado ? 'carregando' : ''}`} onClick={confirmar} disabled={ocupado}>
            {rotuloConfirmar}
          </button>
        </div>
      </div>
    </div>
  )
}
