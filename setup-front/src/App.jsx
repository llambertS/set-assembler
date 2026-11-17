import { useEffect, useState } from 'react'
import { sessao, sair, ehAdmin } from './api'
import Login from './telas/Login'
import Home from './telas/Home'
import MontarSetup from './telas/MontarSetup'
import SetupsCriados from './telas/SetupsCriados'
import Catalogo from './telas/Catalogo'
import ManterComponentes from './telas/ManterComponentes'

export default function App() {
  const [usuario, setUsuario] = useState(sessao)
  const [tela, setTela] = useState('home')
  const [editando, setEditando] = useState(null)
  const [aviso, setAviso] = useState('')

  // Token expirado ou inválido: a API responde 401 e o api.js avisa aqui.
  useEffect(() => {
    const expirou = () => setUsuario(null)
    window.addEventListener('sessao-expirada', expirou)
    return () => window.removeEventListener('sessao-expirada', expirou)
  }, [])

  if (!usuario) return <Login aoEntrar={(u) => { setUsuario(u); setTela('home') }} />

  const admin = ehAdmin(usuario)
  const ir = (t) => { setEditando(null); setAviso(''); setTela(t) }
  const logout = () => { sair(); setUsuario(null); ir('home') }
  const menu = [
    ['montar', 'Criar Novo Setup'],
    ['setups', 'Meus Setups'],
    ['catalogo', 'Explorar Catálogo'],
    ...(admin ? [['componentes', 'Manter Componentes']] : []),
  ]

  return (
    <>
      <header className="topo nao-imprimir">
        <div className="topo-dentro">
          <button className="link marca" onClick={() => ir('home')}><span className="logo" aria-hidden>▣</span> Set-Assembler</button>
          <nav>
            {menu.map(([id, nome]) => (
              <button key={id} className={`link aba ${tela === id ? 'ativa' : ''}`} onClick={() => ir(id)}>{nome}</button>
            ))}
          </nav>
          <span className="suave">{usuario.nome}{admin && <span className="tag">admin</span>}</span>
          <button className="link" onClick={logout}>Sair</button>
        </div>
      </header>
      {/* A key faz a tela inteira "entrar" com animação a cada troca */}
      <main key={tela + (editando?.id || '')} className="tela">
        {tela === 'home' && <Home usuario={usuario} ir={ir} />}
        {tela === 'montar' && (
          <MontarSetup key={editando?.id || 'novo'} inicial={editando}
            aoConcluir={() => { setEditando(null); setAviso('Setup salvo com sucesso!'); setTela('setups') }} />
        )}
        {tela === 'setups' && (
          <SetupsCriados aviso={aviso} aoMontar={() => ir('montar')}
            aoEditar={(s) => { setEditando(s); setTela('montar') }} />
        )}
        {tela === 'catalogo' && <Catalogo />}
        {tela === 'componentes' && admin && <ManterComponentes />}
      </main>
    </>
  )
}
