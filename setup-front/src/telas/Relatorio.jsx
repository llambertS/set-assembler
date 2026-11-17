import { useState } from 'react'
import { moeda } from '../dados'
import { baixarPdf, mensagem } from '../api'
import { Moeda } from '../animacoes'

// "Ficha da Máquina": resumo do setup, especificações de cada peça, status de energia e orçamento.
export default function Relatorio({ setup, aoFechar, rotuloVoltar = 'Voltar', children }) {
  const aprovada = setup.fonteW >= (setup.recomendada || 0)
  const [baixando, setBaixando] = useState(false)
  const [erro, setErro] = useState('')
  const uso = setup.recomendada ? Math.min(100, Math.round((setup.consumo / setup.fonteW) * 100)) : 0

  async function exportar() {
    setBaixando(true); setErro('')
    try { await baixarPdf(setup) } catch (e) { setErro(mensagem(e)) } finally { setBaixando(false) }
  }

  return (
    <article className="entra">
      <div className="acoes nao-imprimir">
        <button className="sec" onClick={aoFechar}>← {rotuloVoltar}</button>
        {/* Setup salvo (tem id): PDF gerado no servidor com OpenPDF. Ainda não salvo: impressão do navegador. */}
        {setup.id
          ? <button className={baixando ? 'carregando' : ''} disabled={baixando} onClick={exportar}>Exportar PDF</button>
          : <button className="sec" onClick={() => window.print()}>Imprimir</button>}
      </div>
      {erro && <p className="erro">{erro}</p>}
      <h1>Ficha da Máquina</h1>
      <p className="suave">{setup.nome} · {setup.data}</p>
      <div className="scroll">
        <table className="linhas-animadas">
          <thead><tr><th>Componente</th><th>Marca</th><th>Modelo</th><th>Especificações</th><th>Qtd</th><th>Preço</th></tr></thead>
          <tbody>
            {setup.itens.map((i, n) => (
              <tr key={n} style={{ '--i': n }}>
                <td>{i.etapa}</td><td>{i.marca || '-'}</td><td>{i.nome}</td><td>{i.desc ?? i.info}</td>
                <td>{i.qtd || 1}</td><td>{moeda(i.preco * (i.qtd || 1))}</td>
              </tr>
            ))}
          </tbody>
          <tfoot><tr><td colSpan="5">Orçamento total</td><td className="destaque"><Moeda valor={setup.total} /></td></tr></tfoot>
        </table>
      </div>
      {setup.recomendada && (
        <div className={`aviso ${aprovada ? '' : 'alerta'}`}>
          <p style={{ margin: 0 }}>
            Consumo estimado: {setup.consumo} W · Fonte: {setup.fonteW} W (mínimo recomendado: {setup.recomendada} W).{' '}
            <strong>{aprovada ? 'Aprovado na análise de energia.' : 'Fonte insuficiente.'}</strong>
          </p>
          <div className="energia" title={`${uso}% da fonte em uso`}><span style={{ '--uso': `${uso}%` }} /></div>
        </div>
      )}
      <div className="nao-imprimir">{children}</div>
    </article>
  )
}
