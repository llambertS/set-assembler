# Guia: subir o Set-Assembler para o GitHub

Vamos criar **um repositório só** com tudo dentro:

```
SetAssembler/                ← raiz do repositório
├── .gitignore               ← o que NÃO deve subir (node_modules, target...)
├── README.md
├── GUIA-DOCKER.md / GUIA-GITHUB.md
├── *.asta, *.md             ← diagramas e documentação
├── setup-back/              ← API Spring Boot + docker-compose
└── setup-front/             ← React
```

O que **não** sobe (por causa do `.gitignore`):
- `node_modules/` e `target/`: dependências e arquivos compilados, que são pesados e recriados com `npm install` e o Maven.
- `.idea/`: configuração pessoal do IntelliJ.
- `*.zip`: o `Set-Assembler1.zip` é uma cópia antiga.

O Git já está instalado e configurado com o seu nome (`git config user.name`).

---

## Passo 1. Criar o repositório vazio no GitHub
1. Entre em **github.com** e faça login (ou crie uma conta).
2. Clique no **+** (canto superior direito) → **New repository**.
3. Preencha:
   - **Repository name:** `set-assembler`
   - **Description:** `Montador de setups de PC com verificação de compatibilidade (React + Spring Boot + PostgreSQL)`
   - **Public** ou **Private** (privado se não quiser que outras turmas vejam; dá para mudar depois).
   - **NÃO** marque *Add a README*, *.gitignore* nem *license*. O repositório precisa nascer **vazio**,
     senão o primeiro envio dá conflito.
4. Clique em **Create repository** e copie a URL que aparece, algo como
   `https://github.com/SEU-USUARIO/set-assembler.git`.

## Passo 2. Transformar a pasta em repositório Git
Abra o terminal **na pasta `SetAssembler`** (a que contém `setup-back` e `setup-front`):

```bash
cd "C:\Users\danie\OneDrive\Desktop\FERRAMENTAS FACULDADE\SetAssembler"
```
```bash
git init -b main
```
Isso cria a pasta oculta `.git`, que é onde o histórico fica guardado. A branch principal se chama `main`.

## Passo 3. Conferir o que vai subir
```bash
git add .
```
```bash
git status
```
Confira na lista:
- ✅ Devem aparecer `setup-back/src/...`, `setup-front/src/...`, `pom.xml`, `package.json`, `docker-compose.yml` e os `.asta`.
- ❌ **Não** podem aparecer `node_modules`, `target` nem `.idea`. Se aparecerem, o `.gitignore` não está na raiz:
  rode `git rm -r --cached node_modules` (ou a pasta que apareceu) e confira de novo.

## Passo 4. Primeiro commit
```bash
git commit -m "Primeira versão: front React, API Spring Boot, PostgreSQL no Docker"
```
Um *commit* é uma "foto" do projeto naquele momento, com uma mensagem dizendo o que mudou.

## Passo 5. Ligar ao GitHub e enviar
Cole a URL copiada no Passo 1:
```bash
git remote add origin https://github.com/SEU-USUARIO/set-assembler.git
```
```bash
git push -u origin main
```
Na primeira vez abre uma janela do **Git Credential Manager** pedindo login no GitHub.
Clique em **Sign in with your browser**, autorize, e pronto: ele lembra o login nas próximas vezes.
Atualize a página do repositório no GitHub e o projeto vai estar lá.

---

## Passo 6. Colocar os colegas no projeto
1. No repositório: **Settings → Collaborators → Add people** → digite o usuário do GitHub de cada um.
2. Cada colega aceita o convite (chega por e-mail) e baixa o projeto:
   ```bash
   git clone https://github.com/SEU-USUARIO/set-assembler.git
   ```
3. Para rodar na máquina do colega:
   ```bash
   cd set-assembler/setup-back
   docker compose up -d
   ```
   Depois: abrir `setup-back` no IntelliJ e rodar a API; em outro terminal, `cd setup-front`, `npm install` e `npm run dev`.

---

## Dia a dia: enviar alterações
Depois de mudar alguma coisa:
```bash
git status
```
```bash
git add .
```
```bash
git commit -m "Adiciona etapa de gabinete no assistente"
```
```bash
git push
```

Antes de começar a trabalhar, **sempre** baixe o que os colegas enviaram:
```bash
git pull
```

### Trabalhando em grupo sem pisar no código dos outros (recomendado)
Cada tarefa ganha uma *branch* (uma linha de trabalho separada):
```bash
git switch -c feature/tela-catalogo     # cria a branch e entra nela
# ... edita, testa ...
git add .
git commit -m "Melhora filtros do catálogo"
git push -u origin feature/tela-catalogo
```
No GitHub aparece o botão **Compare & pull request**. Abra o *pull request*, um colega revisa e clica em **Merge**.
Depois, volte para a `main` e atualize:
```bash
git switch main
```
```bash
git pull
```

---

## Cuidados
- **Senhas:** o `application.yml` só tem valores de **desenvolvimento** (`admin123`, a chave JWT de exemplo e a senha
  `setassembler` do banco). Para um trabalho de faculdade isso é aceitável. Nunca coloque no repositório senhas
  reais, por exemplo a do seu e-mail ou de um banco em produção.
- **Conflito no `git pull`:** o Git marca o trecho com `<<<<<<<` e `>>>>>>>`. Escolha qual versão fica, apague as
  marcações, depois rode `git add .`, `git commit` e `git push`.
- **IntelliJ e VS Code** também fazem tudo isso com botões (menu **Git** no IntelliJ e o ícone de *Source Control* no VS Code).
  Os comandos acima ajudam a entender o que esses botões fazem.
