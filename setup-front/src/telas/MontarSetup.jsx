import { useEffect, useState } from 'react'
import { ETAPAS, TIPOS_ARM, opcoes, requisito, consumo, recomendada, revalidar, moeda } from '../dados'
import { salvarSetup, catalogo, mensagem } from '../api'
import { Carregando, Moeda } from '../animacoes'
import Relatorio from './Relatorio'

const IDX_ARM = ETAPAS.findIndex((e) => e.id === 'armazenamento')

export default function MontarSetup({ inicial, aoConcluir }) {
  const [cat, setCat] = useState(null)
  const [erroCat, setErroCat] = useState('')
  const [escolhas, setEscolhas] = useState(inicial?.escolhas || {})
  const [armOk, setArmOk] = useState(!!inicial)
  const [maisArm, setMaisArm] = useState(false)
  const [tipoArm, setTipoArm] = useState(null)
  const [ramPendente, setRamPendente] = useState(null)
  const [revisando, setRevisando] = useState(!!inicial)
  const [nome, setNome] = useState(inicial?.nome || '')
  const [salvando, setSalvando] = useState(false)
  const [erroSalvar, setErroSalvar] = useState('')

  // Catálogo vem do banco (PostgreSQL) pela API.
  useEffect(() => { catalogo().then(setCat).catch((e) => setErroCat(mensagem(e))) }, [])

  const pronto = (id) => (id === 'armazenamento' ? armOk : !!escolhas[id])
  const indice = ETAPAS.findIndex((e) => !pronto(e.id))
  const completo = indice === -1
  const atual = completo ? null : ETAPAS[indice]
  const arms = escolhas.armazenamento || []
  const usadosM2 = arms.filter((a) => a.tipo === 'M.2').length
  const feitas = ETAPAS.filter((e) => pronto(e.id) && escolhas[e.id]).length

  const total = Object.entries(escolhas).reduce((s, [k, v]) => (
    s + (k === 'armazenamento' ? v.reduce((a, x) => a + x.preco, 0) : k === 'ram' ? v.preco * v.pentes : v.preco)
  ), 0)

  const rotulo = (id) => {
    const v = escolhas[id]
    if (id === 'ram') return `${v.pentes}x ${v.nome}`
    if (id === 'armazenamento') return v.map((a) => a.nome).join(', ')
    return v.nome
  }

  // Aplica a escolha e revalida as demais: o que deixou de ser compatível sai do setup.
  function aplicar(novo) {
    const c = revalidar(novo)
    setEscolhas(c)
    setArmOk((a) => a && !!c.armazenamento)
  }

  function trocar(i) {
    const resto = { ...escolhas }
    delete resto[ETAPAS[i].id]
    setEscolhas(resto)
    if (i === IDX_ARM) setArmOk(false)
    setMaisArm(false); setTipoArm(null); setRamPendente(null); setRevisando(true)
  }

  function escolher(item) {
    if (atual.id === 'ram') return setRamPendente(item)
    if (atual.id === 'armazenamento') {
      aplicar({ ...escolhas, armazenamento: [...arms, item] }); setTipoArm(null); setMaisArm(false); return
    }
    aplicar({ ...escolhas, [atual.id]: item })
  }

  // Formato usado pela Ficha da Máquina (prévia antes de salvar).
  function montar() {
    const { placa, cpu, ram, gpu, fonte } = escolhas
    const um = (etapa, c, qtd = 1) => ({ etapa, marca: c.marca, nome: c.nome, desc: c.info, preco: c.preco, qtd })
    return {
      id: inicial?.id, nome: nome.trim() || 'Setup sem nome',
      data: new Date().toLocaleDateString('pt-BR'),
      itens: [um('Placa-mãe', placa), um('Processador', cpu), um('Memória RAM', ram, ram.pentes), um('Placa de vídeo', gpu),
        ...arms.map((a) => um('Armazenamento', a)), um('Fonte', fonte)],
      total, consumo: consumo(escolhas), recomendada: recomendada(escolhas), fonteW: fonte.watts, escolhas,
    }
  }

  async function salvar() {
    setSalvando(true); setErroSalvar('')
    try {
      await salvarSetup({ id: inicial?.id, nome: nome.trim(), escolhas })
      aoConcluir()
    } catch (e) {
      setErroSalvar(mensagem(e))
      setSalvando(false)
    }
  }

  if (erroCat) return <p className="erro">{erroCat}</p>
  if (!cat) return <Carregando />

  if (completo && !revisando) {
    return (
      <Relatorio setup={montar()} aoFechar={() => setRevisando(true)} rotuloVoltar="Alterar peças">
        <label>Nome do setup<input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Ex.: PC Gamer Custo-Benefício" /></label>
        {erroSalvar && <p className="erro treme">{erroSalvar}</p>}
        <div className="acoes">
          <button disabled={!nome.trim() || salvando} className={salvando ? 'carregando' : ''} onClick={salvar}>
            {inicial ? 'Salvar alterações' : 'Salvar setup'}
          </button>
        </div>
      </Relatorio>
    )
  }

  const noArm = atual?.id === 'armazenamento'
  const pedirTipo = noArm && (arms.length === 0 || maisArm) && !tipoArm
  const perguntaMais = noArm && arms.length > 0 && !maisArm
  const lista = !atual || ramPendente || pedirTipo || perguntaMais ? [] : opcoes(atual.id, escolhas, tipoArm, cat)
  const tiposLivres = noArm ? TIPOS_ARM.filter((t) => t.id !== 'M.2' || usadosM2 < escolhas.placa.m2) : []
  // Muda sempre que o conteúdo da direita muda, para a etapa nova entrar animada.
  const chaveEtapa = [atual?.id, !!ramPendente, pedirTipo, perguntaMais, tipoArm, completo].join('-')

  return (
    <div className="montar">
      <aside className="painel resumo">
        <h2>{inicial ? 'Alterando setup' : 'Seu setup'}</h2>
        <div className="progresso" role="progressbar" aria-valuemin={0} aria-valuemax={ETAPAS.length} aria-valuenow={feitas}>
          <span style={{ width: `${(feitas / ETAPAS.length) * 100}%` }} />
        </div>
        <ol>
          {ETAPAS.map((e, i) => {
            const ok = pronto(e.id) && escolhas[e.id]
            return (
              <li key={e.id} className={`${i === indice ? 'atual' : ''} ${ok ? 'feito' : ''}`}>
                <span className="etapa"><span className="bolinha">{ok ? '✓' : i + 1}</span>{e.nome}</span>
                {ok ? (
                  <>
                    <strong key={rotulo(e.id)} className="surge">{rotulo(e.id)}</strong>
                    <span><button className="link" onClick={() => trocar(i)}>Trocar</button></span>
                  </>
                ) : <span className="suave">—</span>}
              </li>
            )
          })}
        </ol>
        <div className="total"><span>Total</span><strong><Moeda valor={total} /></strong></div>
        {escolhas.cpu && <small>Consumo estimado: {consumo(escolhas)} W</small>}
      </aside>

      <section key={chaveEtapa} className="entra">
        {completo ? (
          <>
            <h1>Setup completo 🎉</h1>
            <p className="suave">Use “Trocar” ao lado para substituir uma peça (as demais são revalidadas) ou abra a ficha.</p>
            <div className="acoes"><button className="pulsa" onClick={() => setRevisando(false)}>Ver Ficha da Máquina</button></div>
          </>
        ) : ramPendente ? (
          <>
            <h1>Quantos pentes?</h1>
            <p className="suave">{ramPendente.nome}. A placa aceita até {escolhas.placa.slotsRam}.</p>
            <div className="grade">
              {Array.from({ length: escolhas.placa.slotsRam }, (_, n) => n + 1).map((n) => (
                <button key={n} className="cartao" style={{ '--i': n }} onClick={() => { aplicar({ ...escolhas, ram: { ...ramPendente, pentes: n } }); setRamPendente(null) }}>
                  <strong>{n} {n === 1 ? 'pente' : 'pentes'}</strong>
                  <span>{moeda(ramPendente.preco * n)}</span>
                </button>
              ))}
            </div>
            <button className="sec" onClick={() => setRamPendente(null)}>Voltar</button>
          </>
        ) : perguntaMais ? (
          <>
            <h1>Armazenamento</h1>
            <p className="suave">{arms.length} unidade(s) escolhida(s). Quer adicionar mais?</p>
            <div className="acoes">
              <button className="sec" onClick={() => setMaisArm(true)}>Adicionar mais armazenamento</button>
              <button onClick={() => setArmOk(true)}>Continuar</button>
            </div>
          </>
        ) : (
          <>
            <p className="passo">Etapa {indice + 1} de {ETAPAS.length}</p>
            <h1>{pedirTipo ? 'Tipo de armazenamento' : `Escolha: ${atual.nome}`}</h1>
            <p className="suave">{requisito(atual.id, escolhas)}</p>
            {pedirTipo && (
              <div className="grade">
                {tiposLivres.map((t, n) => (
                  <button key={t.id} className="cartao" style={{ '--i': n }} onClick={() => setTipoArm(t.id)}><strong>{t.nome}</strong></button>
                ))}
                {tiposLivres.length === 0 && <p className="erro">Os slots M.2 acabaram. Escolha SATA.</p>}
              </div>
            )}
            {!pedirTipo && lista.length === 0 && <p className="erro">Nenhuma opção compatível. Troque uma etapa anterior.</p>}
            <div className="grade">
              {lista.map((item, n) => (
                <button key={item.id} className="cartao" style={{ '--i': n }} onClick={() => escolher(item)}>
                  <strong>{item.nome}</strong>
                  <span className="suave">{item.marca} · {item.info}</span>
                  <span>{moeda(item.preco)}</span>
                </button>
              ))}
            </div>
          </>
        )}
      </section>
    </div>
  )
}
