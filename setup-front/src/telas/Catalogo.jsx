import { useEffect, useState } from 'react'
import { CATEGORIAS, especificacoes, moeda } from '../dados'
import { listarComponentes, mensagem } from '../api'
import { Carregando } from '../animacoes'

export default function Catalogo() {
  const [todos, setTodos] = useState(null)
  const [erro, setErro] = useState('')
  const [cat, setCat] = useState('todas')
  const [busca, setBusca] = useState('')
  const [item, setItem] = useState(null)

  useEffect(() => {
    listarComponentes().then(setTodos).catch((e) => setErro(mensagem(e)))
  }, [])

  if (item) {
    return (
      <article className="entra">
        <button className="sec" onClick={() => setItem(null)}>← Voltar ao catálogo</button>
        <h1 style={{ marginTop: 16 }}>{item.nome}</h1>
        <p className="suave">{item.marca} · {CATEGORIAS.find((c) => c.id === item.categoria)?.nome}</p>
        <table className="linhas-animadas">
          <tbody>{especificacoes(item).map(([r, v], i) => <tr key={r} style={{ '--i': i }}><td>{r}</td><td>{v}</td></tr>)}</tbody>
        </table>
        <p>Preço: <strong className="destaque">{moeda(item.preco)}</strong></p>
      </article>
    )
  }

  const termo = busca.trim().toLowerCase()
  const lista = (todos || []).filter((i) => (cat === 'todas' || i.categoria === cat) && `${i.marca} ${i.nome} ${i.info}`.toLowerCase().includes(termo))

  return (
    <section>
      <h1>Explorar Catálogo</h1>
      <p className="suave">Filtre por categoria ou pesquise pelo nome.</p>
      <label>Buscar peça<input value={busca} onChange={(e) => setBusca(e.target.value)} placeholder="Ex.: Ryzen, DDR5, 750 W" /></label>
      <div className="chips">
        {[{ id: 'todas', nome: 'Todas' }, ...CATEGORIAS].map((c) => (
          <button key={c.id} className={`chip ${cat === c.id ? 'ativo' : 'sec'}`} onClick={() => setCat(c.id)}>{c.nome}</button>
        ))}
      </div>
      {erro && <p className="erro">{erro}</p>}
      {!todos && !erro && <Carregando />}
      {todos && lista.length === 0 && <p className="suave">Nenhuma peça encontrada.</p>}
      {/* key muda com o filtro para os cartões entrarem de novo, em cascata */}
      <div className="grade" key={cat + termo}>
        {lista.map((i, n) => (
          <button key={i.id} className="cartao" style={{ '--i': n }} onClick={() => setItem(i)}>
            <span className="tag">{CATEGORIAS.find((c) => c.id === i.categoria)?.nome}</span>
            <strong>{i.nome}</strong>
            <span className="suave">{i.marca} · {i.info}</span>
            <span>{moeda(i.preco)}</span>
          </button>
        ))}
      </div>
    </section>
  )
}
