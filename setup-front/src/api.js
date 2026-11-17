import { CATEGORIAS } from './dados'

// Comunicação com a API do Spring Boot (setup-back).
// Em desenvolvimento o Vite repassa /api para http://localhost:8080 (veja vite.config.js).
const BASE = import.meta.env.VITE_API_URL || '/api'

const ler = (k, v) => { try { return JSON.parse(localStorage.getItem(k)) ?? v } catch { return v } }

// Sessão = { token, nome, email, papel }. Só o token JWT fica no navegador, nunca a senha.
export const sessao = () => ler('sessao', null)
export const sair = () => localStorage.removeItem('sessao')
export const ehAdmin = (u) => u?.papel === 'ADMIN'

export class ErroApi extends Error {
  constructor(status, mensagem, detalhes = []) {
    super(mensagem)
    this.status = status
    this.detalhes = detalhes
  }
}

async function req(caminho, { method = 'GET', body, arquivo = false } = {}) {
  const headers = {}
  const token = sessao()?.token
  if (token) headers.Authorization = `Bearer ${token}`
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  let r
  try {
    r = await fetch(BASE + caminho, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) })
  } catch {
    throw new ErroApi(0, 'Não foi possível falar com o servidor. A API (porta 8080) está rodando?')
  }

  if (r.status === 401 && token) {
    sair()
    window.dispatchEvent(new Event('sessao-expirada'))
  }
  if (!r.ok) {
    const e = await r.json().catch(() => ({}))
    throw new ErroApi(r.status, e.erro || `Erro ${r.status}`, e.detalhes || [])
  }
  if (arquivo) return r
  return r.status === 204 ? null : r.json()
}

// ---------- Autenticação ----------
function guardar(t) {
  localStorage.setItem('sessao', JSON.stringify(t))
  return t
}
export const entrar = async (email, senha) => guardar(await req('/auth/login', { method: 'POST', body: { email, senha } }))
export const cadastrar = async (f) => guardar(await req('/auth/cadastro', { method: 'POST', body: f }))

// ---------- Componentes ----------
export const listarComponentes = (categoria, busca) => {
  const q = new URLSearchParams()
  if (categoria && categoria !== 'todas') q.set('categoria', categoria)
  if (busca) q.set('busca', busca)
  return req(`/componentes${q.toString() ? `?${q}` : ''}`)
}

// Agrupa por categoria no formato { placa: [...], cpu: [...] } usado pelo assistente.
export async function catalogo() {
  const lista = await listarComponentes()
  const c = Object.fromEntries(CATEGORIAS.map((x) => [x.id, []]))
  lista.forEach((i) => c[i.categoria]?.push(i))
  return c
}

export const salvarComponente = (item) =>
  item.id ? req(`/componentes/${item.id}`, { method: 'PUT', body: item }) : req('/componentes', { method: 'POST', body: item })
export const excluirComponente = (id) => req(`/componentes/${id}`, { method: 'DELETE' })

// ---------- Setups ----------
// Converte a resposta da API no formato que as telas (Relatorio, MontarSetup) já usavam.
function paraTela(s) {
  const escolhas = {}
  for (const it of s.itens) {
    const c = it.componente
    if (c.categoria === 'armazenamento') {
      for (let n = 0; n < it.quantidade; n++) (escolhas.armazenamento ||= []).push(c)
    } else if (c.categoria === 'ram') escolhas.ram = { ...c, pentes: it.quantidade }
    else escolhas[c.categoria] = c
  }
  return {
    id: s.id,
    nome: s.nome,
    data: new Date(s.atualizadoEm || s.criadoEm).toLocaleDateString('pt-BR'),
    itens: s.itens.map((it) => ({
      etapa: it.etapa, marca: it.componente.marca, nome: it.componente.nome,
      desc: it.componente.info, preco: it.componente.preco, qtd: it.quantidade,
    })),
    total: s.precoTotal, consumo: s.consumoW, recomendada: s.recomendadaW, fonteW: s.fonteW,
    escolhas,
  }
}

export const meusSetups = async () => (await req('/setups')).map(paraTela)

export async function salvarSetup({ id, nome, escolhas }) {
  const { placa, cpu, ram, gpu, fonte, gabinete, armazenamento = [] } = escolhas
  const itens = [
    { componenteId: placa.id, quantidade: 1 },
    { componenteId: cpu.id, quantidade: 1 },
    { componenteId: ram.id, quantidade: ram.pentes },
    { componenteId: gpu.id, quantidade: 1 },
    ...armazenamento.map((a) => ({ componenteId: a.id, quantidade: 1 })),
    { componenteId: fonte.id, quantidade: 1 },
    ...(gabinete ? [{ componenteId: gabinete.id, quantidade: 1 }] : []),
  ]
  const body = { nome, itens }
  return paraTela(await (id ? req(`/setups/${id}`, { method: 'PUT', body }) : req('/setups', { method: 'POST', body })))
}

export const excluirSetup = (id) => req(`/setups/${id}`, { method: 'DELETE' })

// Baixa a Ficha da Máquina em PDF gerada pelo servidor (OpenPDF).
export async function baixarPdf(setup) {
  const r = await req(`/setups/${setup.id}/pdf`, { arquivo: true })
  const url = URL.createObjectURL(await r.blob())
  const a = document.createElement('a')
  a.href = url
  a.download = `setup-${setup.id}.pdf`
  const nome = /filename="?([^";]+)"?/i.exec(r.headers.get('Content-Disposition') || '')
  if (nome) a.download = decodeURIComponent(nome[1])
  document.body.appendChild(a)
  a.click()
  a.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}

// Texto amigável para mostrar na tela (inclui a lista de problemas de compatibilidade, se houver).
export const mensagem = (e) => (e?.detalhes?.length ? `${e.message} ${e.detalhes.join(' ')}` : e?.message || 'Erro inesperado.')
