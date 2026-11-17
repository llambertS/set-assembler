import { ehAdmin } from '../api'

const OPCOES = [
  ['montar', '🛠️', 'Criar Novo Setup', 'Assistente passo a passo, com a compatibilidade verificada a cada etapa.'],
  ['setups', '💾', 'Meus Setups', 'Abra o relatório, exporte em PDF, altere ou exclua as máquinas salvas.'],
  ['catalogo', '🔎', 'Explorar Catálogo', 'Consulte as especificações e o preço de cada peça.'],
  ['componentes', '🗂️', 'Manter Componentes', 'Cadastre, edite ou remova as peças do catálogo.', true],
]

export default function Home({ usuario, ir }) {
  const opcoes = OPCOES.filter((o) => !o[4] || ehAdmin(usuario))
  return (
    <section>
      <h1>Olá, <span className="destaque">{usuario.nome.split(' ')[0]}</span></h1>
      <p className="suave">O que você quer fazer?</p>
      <div className="grade">
        {opcoes.map(([id, icone, titulo, texto], i) => (
          <button key={id} className="cartao cartao-home" style={{ '--i': i }} onClick={() => ir(id)}>
            <span className="icone" aria-hidden>{icone}</span>
            <strong>{titulo}</strong>
            <span>{texto}</span>
          </button>
        ))}
      </div>
    </section>
  )
}
