import { useState } from 'react'
import { entrar, cadastrar, mensagem } from '../api'

export default function Login({ aoEntrar }) {
  const [modo, setModo] = useState('entrar')
  const [f, setF] = useState({ nome: '', email: '', senha: '' })
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const muda = (campo) => (e) => setF({ ...f, [campo]: e.target.value })

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    if (modo === 'cadastrar' && f.senha.length < 6) return setErro('A senha precisa ter pelo menos 6 caracteres.')
    setEnviando(true)
    try {
      const u = modo === 'cadastrar'
        ? await cadastrar({ nome: f.nome.trim(), email: f.email.trim(), senha: f.senha })
        : await entrar(f.email.trim(), f.senha)
      aoEntrar(u)
    } catch (err) {
      setErro(mensagem(err))
      setEnviando(false)
    }
  }

  return (
    <div className="fundo-login">
      <div className="orbita" aria-hidden><span /><span /><span /></div>
      <form key={modo} className="login painel" onSubmit={enviar}>
        <span className="logo grande" aria-hidden>▣</span>
        <h1>Set-Assembler</h1>
        <p className="suave">{modo === 'entrar' ? 'Entre para montar o seu setup.' : 'Crie a sua conta.'}</p>
        {modo === 'cadastrar' && (
          <label>Nome<input required value={f.nome} onChange={muda('nome')} /></label>
        )}
        <label>E-mail<input type="email" required value={f.email} onChange={muda('email')} /></label>
        <label>Senha<input type="password" required value={f.senha} onChange={muda('senha')} /></label>
        {erro && <p className="erro treme">{erro}</p>}
        <button type="submit" disabled={enviando} className={enviando ? 'carregando' : ''}>
          {modo === 'entrar' ? 'Entrar' : 'Cadastrar'}
        </button>
        <p className="suave">
          {modo === 'entrar' ? 'Ainda não tem conta? ' : 'Já tem conta? '}
          <button type="button" className="link" onClick={() => { setErro(''); setModo(modo === 'entrar' ? 'cadastrar' : 'entrar') }}>
            {modo === 'entrar' ? 'Cadastre-se' : 'Entrar'}
          </button>
        </p>
      </form>
    </div>
  )
}
