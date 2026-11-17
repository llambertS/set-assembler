import { useCallback, useEffect, useState } from 'react'
import { CATEGORIAS, CAMPOS, moeda } from '../dados'
import { catalogo, salvarComponente, excluirComponente, mensagem } from '../api'
import { Aviso, Carregando } from '../animacoes'
import Confirmar from '../Confirmar'

const BASE = [['marca', 'Marca', 'text'], ['nome', 'Modelo', 'text'], ['info', 'Descrição', 'text'], ['preco', 'Preço (R$)', 'number']]

export default function ManterComponentes() {
  const [dados, setDados] = useState(null)
  const [cat, setCat] = useState(CATEGORIAS[0].id)
  const [form, setForm] = useState(null) // { item, v }
  const [erro, setErro] = useState('')
  const [msg, setMsg] = useState('')
  const [salvando, setSalvando] = useState(false)
  const [removendo, setRemovendo] = useState(null)
  const limparMsg = useCallback(() => setMsg(''), [])
  const fecharPopup = useCallback(() => setRemovendo(null), [])

  const recarregar = () => catalogo().then(setDados).catch((e) => setErro(mensagem(e)))
  useEffect(() => { recarregar() }, [])

  const campos = [...BASE, ...CAMPOS[cat]]
  const lista = dados?.[cat] || []

  function abrir(item) {
    const v = {}
    campos.forEach(([k]) => {
      const x = item?.[k]
      v[k] = x === undefined || x === null ? '' : Array.isArray(x) ? x.join(', ') : String(x)
    })
    setForm({ item, v }); setErro(''); setMsg('')
  }

  // Valida os dados da peça e cadastra (novo) ou atualiza (edição) pela API.
  async function salvar(e) {
    e.preventDefault()
    const novo = { categoria: cat, id: form.item?.id }
    for (const [k, rotulo, tipo] of campos) {
      const bruto = form.v[k].trim()
      if (!bruto) return setErro(`Informe: ${rotulo}.`)
      if (tipo === 'number') {
        const n = Number(bruto.replace(',', '.'))
        if (Number.isNaN(n) || n < 0 || (k === 'preco' && n === 0)) return setErro(`Valor inválido em: ${rotulo}.`)
        novo[k] = n
      } else {
        novo[k] = k === 'formatos' ? bruto.split(',').map((x) => x.trim()).filter(Boolean) : bruto
      }
    }
    setSalvando(true)
    try {
      await salvarComponente(novo)
      await recarregar()
      setForm(null)
      setMsg(form.item ? 'Componente atualizado com sucesso!' : 'Componente cadastrado com sucesso!')
    } catch (err) {
      setErro(mensagem(err))
    } finally {
      setSalvando(false)
    }
  }

  // Chamado pelo botão "Remover" do pop-up de confirmação.
  async function remover() {
    try {
      await excluirComponente(removendo.id)
      await recarregar()
      setMsg('Componente removido!')
    } catch (err) {
      setMsg(''); setErro(mensagem(err))
    } finally {
      setRemovendo(null)
    }
  }

  if (form) {
    return (
      <form className="painel entra" onSubmit={salvar} style={{ maxWidth: 520 }}>
        <h1>{form.item ? 'Editar' : 'Novo'} componente</h1>
        <p className="suave">{CATEGORIAS.find((c) => c.id === cat).nome}</p>
        {campos.map(([k, rotulo, tipo, opcoes]) => (
          <label key={k}>{rotulo}
            {tipo === 'select' ? (
              <select value={form.v[k]} onChange={(e) => setForm({ ...form, v: { ...form.v, [k]: e.target.value } })}>
                <option value="">Selecione</option>
                {opcoes.map((o) => <option key={o}>{o}</option>)}
              </select>
            ) : (
              <input type={tipo === 'number' ? 'number' : 'text'} step="any" value={form.v[k]}
                onChange={(e) => setForm({ ...form, v: { ...form.v, [k]: e.target.value } })} />
            )}
          </label>
        ))}
        {erro && <p className="erro treme">{erro}</p>}
        <div className="acoes">
          <button type="submit" disabled={salvando} className={salvando ? 'carregando' : ''}>Salvar</button>
          <button type="button" className="sec" onClick={() => setForm(null)}>Cancelar</button>
        </div>
      </form>
    )
  }

  return (
    <section>
      <h1>Manter Componentes</h1>
      <p className="suave">Cadastre, edite ou remova peças do catálogo.</p>
      <Aviso texto={msg} aoSumir={limparMsg} />
      {erro && <p className="erro">{erro}</p>}
      <div className="chips">
        {CATEGORIAS.map((c) => (
          <button key={c.id} className={`chip ${cat === c.id ? 'ativo' : 'sec'}`} onClick={() => { setCat(c.id); setMsg(''); setErro('') }}>{c.nome}</button>
        ))}
      </div>
      <div className="acoes"><button onClick={() => abrir(null)}>+ Novo componente</button></div>
      {!dados ? <Carregando cartoes={3} /> : lista.length === 0 ? <p className="suave">Nenhuma peça nesta categoria.</p> : (
        <div className="scroll">
          <table className="linhas-animadas" key={cat}>
            <thead><tr><th>Modelo</th><th>Marca</th><th>Preço</th><th></th></tr></thead>
            <tbody>
              {lista.map((i, n) => (
                <tr key={i.id} style={{ '--i': n }}>
                  <td>{i.nome}</td><td>{i.marca}</td><td>{moeda(i.preco)}</td>
                  <td>
                    <button className="link" onClick={() => abrir(i)}>Editar</button>
                    <button className="link perigo" onClick={() => { setErro(''); setRemovendo(i) }}>Remover</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <Confirmar
        aberto={!!removendo}
        titulo="Remover componente?"
        mensagem={removendo && <><strong>“{removendo.nome}”</strong> sairá do catálogo.</>}
        rotuloConfirmar="Remover"
        aoConfirmar={remover}
        aoCancelar={fecharPopup}
      />
    </section>
  )
}
