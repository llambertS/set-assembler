import { useCallback, useEffect, useState } from 'react'
import { meusSetups, excluirSetup, baixarPdf, mensagem } from '../api'
import { moeda } from '../dados'
import { Aviso, Carregando } from '../animacoes'
import Relatorio from './Relatorio'
import Confirmar from '../Confirmar'

export default function SetupsCriados({ aviso, aoMontar, aoEditar }) {
  const [lista, setLista] = useState(null)
  const [aberto, setAberto] = useState(null)
  const [msg, setMsg] = useState(aviso || '')
  const [erro, setErro] = useState('')
  const [saindo, setSaindo] = useState(null) // id do cartão em animação de saída
  const [confirmando, setConfirmando] = useState(null) // setup aguardando confirmação de exclusão
  const limparMsg = useCallback(() => setMsg(''), [])
  const fecharPopup = useCallback(() => setConfirmando(null), [])

  useEffect(() => { meusSetups().then(setLista).catch((e) => setErro(mensagem(e))) }, [])

  if (aberto) return <Relatorio setup={aberto} aoFechar={() => setAberto(null)} />

  // Chamado pelo botão "Excluir" do pop-up.
  async function excluir() {
    const s = confirmando
    setErro('')
    try {
      await excluirSetup(s.id)
      setConfirmando(null)
      setSaindo(s.id)
      setTimeout(() => { setLista((l) => l.filter((x) => x.id !== s.id)); setSaindo(null) }, 320)
      setMsg('Setup excluído!')
    } catch (e) {
      setConfirmando(null)
      setErro(mensagem(e))
    }
  }

  async function pdf(s) {
    setErro('')
    try { await baixarPdf(s) } catch (e) { setErro(mensagem(e)) }
  }

  return (
    <section>
      <h1>Meus Setups</h1>
      <Aviso texto={msg} aoSumir={limparMsg} />
      {erro && <p className="erro">{erro}</p>}
      {!lista && !erro && <Carregando cartoes={3} />}
      {lista && lista.length === 0 && (
        <div className="vazio entra">
          <p className="suave">Você ainda não salvou nenhum setup.</p>
          <button onClick={aoMontar}>Criar Novo Setup</button>
        </div>
      )}
      {lista && lista.length > 0 && (
        <div className="grade">
          {lista.map((s, n) => (
            <div key={s.id} className={`cartao ${saindo === s.id ? 'sai' : ''}`} style={{ '--i': n }}>
              <strong>{s.nome}</strong>
              <span className="suave">{s.itens.length} peças · {s.data}</span>
              <span className="preco">{moeda(s.total)}</span>
              <span>
                <button className="link" onClick={() => setAberto(s)}>Relatório</button>
                <button className="link" onClick={() => pdf(s)}>PDF</button>
                <button className="link" onClick={() => aoEditar(s)}>Alterar</button>
                <button className="link perigo" onClick={() => setConfirmando(s)}>Excluir</button>
              </span>
            </div>
          ))}
        </div>
      )}
      <Confirmar
        aberto={!!confirmando}
        titulo="Excluir setup?"
        mensagem={confirmando && <>O setup <strong>“{confirmando.nome}”</strong> será removido permanentemente. Essa ação não pode ser desfeita.</>}
        aoConfirmar={excluir}
        aoCancelar={fecharPopup}
      />
    </section>
  )
}
