// Ordem das etapas: cada uma é filtrada pelas escolhas anteriores.
export const ETAPAS = [
  { id: 'placa', nome: 'Placa-mãe' },
  { id: 'cpu', nome: 'Processador' },
  { id: 'ram', nome: 'Memória RAM' },
  { id: 'gpu', nome: 'Placa de vídeo' },
  { id: 'armazenamento', nome: 'Armazenamento' },
  { id: 'fonte', nome: 'Fonte' },
]

// DADOS DE EXEMPLO (preços e medidas aproximados). Depois virão do banco, pela API do Spring Boot.
export const CATALOGO = {
  placa: [
    { id: 'p1', marca: 'ASUS', ramMax: 6400, nome: 'Placa-mãe B650M', info: 'AM5 · DDR5 · mATX · 2 M.2', socket: 'AM5', memoria: 'DDR5', formato: 'mATX', slotsRam: 4, m2: 2, preco: 899 },
    { id: 'p2', marca: 'Gigabyte', ramMax: 3600, nome: 'Placa-mãe B550M', info: 'AM4 · DDR4 · mATX · 1 M.2', socket: 'AM4', memoria: 'DDR4', formato: 'mATX', slotsRam: 4, m2: 1, preco: 629 },
    { id: 'p3', marca: 'MSI', ramMax: 3200, nome: 'Placa-mãe B760M', info: 'LGA1700 · DDR4 · mATX · 2 M.2', socket: 'LGA1700', memoria: 'DDR4', formato: 'mATX', slotsRam: 4, m2: 2, preco: 739 },
    { id: 'p4', marca: 'ASUS', ramMax: 7200, nome: 'Placa-mãe Z790', info: 'LGA1700 · DDR5 · ATX · 3 M.2', socket: 'LGA1700', memoria: 'DDR5', formato: 'ATX', slotsRam: 4, m2: 3, preco: 1649 },
  ],
  cpu: [
    { id: 'c1', marca: 'AMD', nome: 'Ryzen 5 7600', info: 'AM5 · 65 W', socket: 'AM5', tdp: 65, preco: 1299 },
    { id: 'c2', marca: 'AMD', nome: 'Ryzen 5 5600', info: 'AM4 · 65 W', socket: 'AM4', tdp: 65, preco: 699 },
    { id: 'c3', marca: 'Intel', nome: 'Core i5-12400F', info: 'LGA1700 · 65 W', socket: 'LGA1700', tdp: 65, preco: 749 },
    { id: 'c4', marca: 'Intel', nome: 'Core i5-14600K', info: 'LGA1700 · 125 W', socket: 'LGA1700', tdp: 125, preco: 1799 },
  ],
  gpu: [
    { id: 'g1', marca: 'NVIDIA', nome: 'GeForce RTX 4060', info: '8 GB · 115 W · 240 mm', tdp: 115, comp: 240, preco: 1999 },
    { id: 'g2', marca: 'AMD', nome: 'Radeon RX 7600', info: '8 GB · 165 W · 240 mm', tdp: 165, comp: 240, preco: 1599 },
    { id: 'g3', marca: 'NVIDIA', nome: 'GeForce RTX 4070 SUPER', info: '12 GB · 220 W · 300 mm', tdp: 220, comp: 300, preco: 4299 },
  ],
  ram: [
    { id: 'r1', marca: 'Kingston', freq: 3200, tdp: 3, nome: '8 GB DDR4 3200 MHz', info: 'DDR4 · preço por pente', tipo: 'DDR4', preco: 125 },
    { id: 'r2', marca: 'Corsair', freq: 5200, tdp: 3, nome: '8 GB DDR5 5200 MHz', info: 'DDR5 · preço por pente', tipo: 'DDR5', preco: 175 },
    { id: 'r3', marca: 'Corsair', freq: 6000, tdp: 3, nome: '16 GB DDR5 6000 MHz', info: 'DDR5 · preço por pente', tipo: 'DDR5', preco: 340 },
  ],
  armazenamento: [
    { id: 'a1', marca: 'WD', tdp: 8, nome: 'SSD NVMe 1 TB', info: 'M.2', tipo: 'M.2', preco: 449 },
    { id: 'a2', marca: 'Kingston', tdp: 4, nome: 'SSD SATA 480 GB', info: 'SATA', tipo: 'SATA', preco: 229 },
    { id: 'a3', marca: 'Seagate', tdp: 8, nome: 'HD 1 TB', info: 'SATA · 7200 rpm', tipo: 'SATA', preco: 289 },
  ],
  fonte: [
    { id: 'f1', marca: 'Corsair', nome: 'Fonte 550 W 80 Plus Bronze', info: '550 W', watts: 550, preco: 299 },
    { id: 'f2', marca: 'Corsair', nome: 'Fonte 650 W 80 Plus Bronze', info: '650 W', watts: 650, preco: 379 },
    { id: 'f3', marca: 'Corsair', nome: 'Fonte 750 W 80 Plus Gold', info: '750 W', watts: 750, preco: 549 },
    { id: 'f4', marca: 'Corsair', nome: 'Fonte 850 W 80 Plus Gold', info: '850 W', watts: 850, preco: 689 },
  ],
  gabinete: [
    { id: 'z1', marca: 'Cooler Master', nome: 'Gabinete Compacto mATX', info: 'mATX · placa de vídeo até 260 mm', formatos: ['mATX'], maxGpu: 260, preco: 249 },
    { id: 'z2', marca: 'NZXT', nome: 'Gabinete Mid Tower', info: 'mATX/ATX · placa de vídeo até 330 mm', formatos: ['mATX', 'ATX'], maxGpu: 330, preco: 399 },
    { id: 'z3', marca: 'Corsair', nome: 'Gabinete Full Tower', info: 'mATX/ATX · placa de vídeo até 400 mm', formatos: ['mATX', 'ATX'], maxGpu: 400, preco: 749 },
  ],
}

export const moeda = (v) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

export const TIPOS_ARM = [
  { id: 'M.2', nome: 'SSD NVMe (M.2)' },
  { id: 'SATA', nome: 'SATA (SSD ou HD)' },
]

// Consumo estimado e potência recomendada da fonte (consumo + 30% de folga, em passos de 50 W).
export const consumo = (e) =>
  (e.cpu?.tdp || 0) + (e.gpu?.tdp || 0) + (e.ram ? e.ram.tdp * e.ram.pentes : 0) +
  (e.armazenamento || []).reduce((s, a) => s + a.tdp, 0) + 100 // 100 W para placa-mãe, ventoinhas etc.
export const recomendada = (e) => Math.ceil((consumo(e) * 1.3) / 50) * 50

// "Oculta componentes incompatíveis": devolve só o que combina com o que já foi escolhido.
export function opcoes(etapa, e, tipo, cat = CATALOGO) {
  const lista = cat[etapa] || []
  switch (etapa) {
    case 'cpu': return lista.filter((c) => c.socket === e.placa.socket)
    case 'ram': return lista.filter((r) => r.tipo === e.placa.memoria && r.freq <= e.placa.ramMax)
    case 'armazenamento': return lista.filter((a) => a.tipo === tipo)
    case 'gabinete': return lista.filter((g) => g.formatos.includes(e.placa.formato) && g.maxGpu >= e.gpu.comp)
    case 'fonte': return lista.filter((f) => f.watts >= recomendada(e))
    default: return lista
  }
}

export function requisito(etapa, e) {
  switch (etapa) {
    case 'placa': return 'Comece pela base do setup: as próximas etapas serão filtradas por ela.'
    case 'cpu': return `Compatíveis com o socket ${e.placa.socket}.`
    case 'ram': return `Memória ${e.placa.memoria} até ${e.placa.ramMax} MHz. A placa aceita até ${e.placa.slotsRam} pentes.`
    case 'gpu': return 'Todas usam o slot PCIe x16 da placa-mãe.'
    case 'armazenamento': return `A placa tem ${e.placa.m2} slot(s) M.2 e portas SATA.`
    case 'fonte': return `Consumo estimado de ${consumo(e)} W. Só aparecem fontes a partir de ${recomendada(e)} W (com margem de segurança).`
    case 'gabinete': return `Formato ${e.placa.formato} e placa de vídeo de ${e.gpu.comp} mm.`
    default: return ''
  }
}

export const CATEGORIAS = [
  { id: 'placa', nome: 'Placas-mãe' },
  { id: 'cpu', nome: 'Processadores' },
  { id: 'ram', nome: 'Memórias RAM' },
  { id: 'gpu', nome: 'Placas de vídeo' },
  { id: 'armazenamento', nome: 'Armazenamento' },
  { id: 'fonte', nome: 'Fontes' },
  { id: 'gabinete', nome: 'Gabinetes' },
]

const ROTULOS = [
  ['socket', 'Socket'], ['memoria', 'Memória'], ['tipo', 'Tipo'], ['freq', 'Frequência (MHz)'],
  ['ramMax', 'Frequência máx. de RAM (MHz)'], ['slotsRam', 'Slots de RAM'], ['formato', 'Formato'],
  ['m2', 'Slots M.2'], ['tdp', 'Consumo (W)'], ['comp', 'Comprimento (mm)'], ['watts', 'Potência (W)'],
  ['maxGpu', 'Placa de vídeo até (mm)'],
]
export const especificacoes = (item) =>
  ROTULOS.filter(([k]) => item[k] !== undefined).map(([k, r]) => [r, String(item[k])])
    .concat(item.formatos ? [['Formatos de placa', item.formatos.join(', ')]] : [])

// Revalida o setup depois de uma troca: mantém o que ainda é compatível e remove o resto.
export function revalidar(e) {
  const r = {}
  for (const { id } of ETAPAS) {
    const v = e[id]
    if (!v) continue
    if (id === 'cpu' && v.socket !== r.placa?.socket) continue
    if (id === 'ram' && !(r.placa && v.tipo === r.placa.memoria && v.freq <= r.placa.ramMax && v.pentes <= r.placa.slotsRam)) continue
    if (id === 'armazenamento') {
      let m2 = 0
      const ok = v.filter((a) => a.tipo !== 'M.2' || (r.placa && ++m2 <= r.placa.m2))
      if (ok.length) r.armazenamento = ok
      continue
    }
    if (id === 'fonte' && v.watts < recomendada(r)) continue
    r[id] = v
  }
  return r
}

// Campos de cada categoria no cadastro de componentes: [chave, rótulo, tipo, opções].
const DDR = ['DDR4', 'DDR5']
export const CAMPOS = {
  placa: [['socket', 'Socket', 'text'], ['memoria', 'Tipo de memória', 'select', DDR], ['formato', 'Formato', 'select', ['mATX', 'ATX', 'ITX']],
    ['slotsRam', 'Slots de RAM', 'number'], ['ramMax', 'Frequência máx. de RAM (MHz)', 'number'], ['m2', 'Slots M.2', 'number']],
  cpu: [['socket', 'Socket', 'text'], ['tdp', 'Consumo (W)', 'number']],
  ram: [['tipo', 'Tipo', 'select', DDR], ['freq', 'Frequência (MHz)', 'number'], ['tdp', 'Consumo por pente (W)', 'number']],
  gpu: [['tdp', 'Consumo (W)', 'number'], ['comp', 'Comprimento (mm)', 'number']],
  armazenamento: [['tipo', 'Tipo', 'select', ['M.2', 'SATA']], ['tdp', 'Consumo (W)', 'number']],
  fonte: [['watts', 'Potência (W)', 'number']],
  gabinete: [['formatos', 'Formatos de placa aceitos (separe por vírgula)', 'text'], ['maxGpu', 'Placa de vídeo até (mm)', 'number']],
}