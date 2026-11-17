# Guia do Docker no Set-Assembler

## 1. O que o Docker faz neste trabalho

O sistema tem três partes:

```
 Navegador ──► React (Vite, porta 5173) ──► API Spring Boot (porta 8080) ──► PostgreSQL 16 (porta 5433)
                setup-front                    setup-back                      DOCKER
```

O Docker cuida da terceira parte: **o banco de dados PostgreSQL 16 roda dentro de um container**.
Um container funciona como um "mini computador" isolado, que já vem com o PostgreSQL instalado e configurado.

### Por que usar o Docker em vez de instalar o PostgreSQL?

| Funcionalidade | O que significa na prática |
|---|---|
| **Ambiente igual para todo o grupo** | Todos usam o mesmo PostgreSQL 16, com o mesmo banco, usuário e senha. Acaba o "no meu PC funciona". |
| **Sem instalação** | Não precisa instalar o PostgreSQL no Windows. O Docker baixa a imagem `postgres:16-alpine` sozinho. |
| **Configuração como código** | Tudo está escrito em `setup-back/docker-compose.yml`, que vai junto para o GitHub. Quem clonar o projeto sobe o banco com um comando. |
| **Isolamento** | O banco do projeto não se mistura com outros bancos do PC. Por isso ele usa a porta **5433**: a 5432 já é do PostgreSQL 18 instalado no Windows. |
| **Dados persistentes** | Os dados ficam num *volume* (`setassembler-dados`). Desligar o PC ou parar o container não apaga nada. |
| **Recomeçar do zero em segundos** | `docker compose down -v` apaga o banco. Na próxima vez que a API subir, o Flyway recria as tabelas e o catálogo. Ótimo para testes e para a apresentação. |
| **Healthcheck** | O Docker confere a cada 5 segundos se o banco está pronto para conexões (`pg_isready`) e mostra isso como `healthy`. |

### Quem faz o quê
- **Docker**: liga o servidor PostgreSQL, com um banco vazio chamado `setassembler`.
- **Flyway** (dentro da API): ao iniciar, cria as tabelas (`V1__criar_tabelas.sql`) e insere o catálogo (`V2__catalogo_inicial.sql`).
- **Spring Boot / JPA**: lê e grava os dados (usuários, componentes, setups).
- **pgAdmin, DBeaver ou IntelliJ**: servem para *ver* o banco.

---

## 2. Instalação (uma vez por computador)

1. Baixe o **Docker Desktop** em docker.com → *Download for Windows*.
2. Instale aceitando as opções padrão. Se ele pedir o **WSL 2**, aceite: o Docker no Windows roda em cima dele.
3. Reinicie o PC, se o instalador pedir.
4. Abra o Docker Desktop e espere aparecer **"Engine running"** no canto inferior esquerdo.
5. Teste no terminal:
   ```bash
   docker --version
   docker compose version
   ```

> Na primeira vez que abrir, o Docker pode pedir login. **Não é obrigatório**: dá para clicar em *Skip* e continuar.

---

## 3. O arquivo `docker-compose.yml`, explicado

```yaml
services:
  postgres:                                # nome do serviço
    image: postgres:16-alpine              # PostgreSQL 16, versão "alpine" (leve, cerca de 100 MB)
    container_name: setassembler-db        # nome que aparece no Docker Desktop
    restart: unless-stopped                # liga sozinho junto com o Docker, a não ser que você pare
    environment:                           # configuração que o PostgreSQL lê ao criar o banco
      POSTGRES_DB: setassembler            #   nome do banco
      POSTGRES_USER: setassembler          #   usuário
      POSTGRES_PASSWORD: setassembler      #   senha (só para desenvolvimento)
      TZ: America/Sao_Paulo                #   fuso horário
    ports:
      - "5433:5432"                        # porta do SEU PC : porta DENTRO do container
    volumes:
      - setassembler-dados:/var/lib/postgresql/data   # onde os dados ficam guardados
    healthcheck:                           # como o Docker sabe se o banco está pronto
      test: ["CMD-SHELL", "pg_isready -U setassembler -d setassembler"]
      interval: 5s

volumes:
  setassembler-dados:                      # volume com nome: sobrevive a "docker compose down"
```

**Sobre `"5433:5432"`:** dentro do container o PostgreSQL usa a porta padrão 5432. No seu PC ela aparece como 5433.
É por isso que a API (`application.yml`) e o pgAdmin conectam em `localhost:5433`.

---

## 4. Uso no dia a dia

Todos os comandos rodam **dentro da pasta `setup-back`** (onde está o `docker-compose.yml`).

### Começar a trabalhar
1. Abra o **Docker Desktop**.
2. Suba o banco:
   ```bash
   docker compose up -d
   ```
   O `-d` (*detached*) roda em segundo plano e libera o terminal.
3. Confira se ele está pronto (coluna STATUS mostra `healthy`):
   ```bash
   docker compose ps
   ```
4. Rode a API no IntelliJ (▶ no `SetAssemblerApplication`) e depois o front (`npm run dev`).

> Como o container usa `restart: unless-stopped`, depois da primeira vez ele liga sozinho sempre que o
> Docker Desktop abrir. Mesmo assim, rodar `docker compose up -d` de novo não faz mal.

### Parar
```bash
docker compose stop        # pausa o container (mais rápido para voltar)
docker compose down        # remove o container, mas MANTÉM os dados (o volume continua)
```

### Ver o que está acontecendo
```bash
docker compose logs -f postgres   # logs do banco ao vivo (Ctrl+C para sair)
docker stats setassembler-db      # uso de CPU e memória
```

### Entrar no banco pelo terminal (sem pgAdmin)
```bash
docker exec -it setassembler-db psql -U setassembler -d setassembler
```
Dentro do psql: `\dt` lista as tabelas, `\d componente` mostra as colunas e `\q` sai.

### Recomeçar o banco do zero
```bash
docker compose down -v     # -v apaga o volume, ou seja, TODOS os dados
docker compose up -d
```
Depois, rode a API de novo: o Flyway recria as tabelas, o catálogo e o administrador.

### Fazer backup e restaurar
```bash
# Backup: gera um arquivo .sql com tudo
docker exec setassembler-db pg_dump -U setassembler setassembler > backup.sql

# Restaurar (com o banco vazio, logo depois de um "down -v" e "up -d")
docker exec -i setassembler-db psql -U setassembler -d setassembler < backup.sql
```

---

## 5. Pelo Docker Desktop (sem terminal)
- **Containers**: o `setassembler-db` aparece com bolinha verde quando está ligado. Os botões ▶, ■ e 🗑 ligam, param e removem.
- Clique no container para ver os **Logs**. A aba **Exec** abre um terminal dentro dele.
- **Volumes**: mostra o `setup-back_setassembler-dados` e o espaço que ele ocupa.
- **Images**: mostra o `postgres:16-alpine` baixado.

## 6. Pelo IntelliJ
- Abra o `docker-compose.yml` e clique no ▶▶ ao lado de `services:` para subir o banco.
- A janela **Services** (Alt+8) mostra o container, os logs e os botões de parar e reiniciar.

---

## 7. Problemas comuns

| Sintoma | Causa | Solução |
|---|---|---|
| `error during connect` / `cannot find the file specified` | Docker Desktop fechado | Abra o Docker Desktop e espere "Engine running" |
| `port is already allocated` (5433) | Outro programa usa a 5433 | Troque para `"5434:5432"` no compose e no `application.yml` |
| API: `Connection refused` | Container parado | `docker compose up -d` |
| API: `password authentication failed` | Conectou na 5432 (PostgreSQL do Windows) | Use a porta **5433** |
| `unhealthy` | Banco não iniciou | `docker compose logs postgres` para ver o motivo |
| Quero os dados de exemplo de volta | O banco foi alterado | `docker compose down -v`, `up -d` e rode a API |

---

## 8. Resumo para colar no trabalho

> O projeto usa **Docker** para executar o **PostgreSQL 16** em um container isolado, definido no arquivo
> `docker-compose.yml`, versionado junto com o código. Assim, todos os integrantes trabalham com o mesmo ambiente
> de banco de dados, sem precisar instalar o PostgreSQL. Os dados ficam num volume persistente, e o container
> tem verificação de saúde (*healthcheck*). A estrutura do banco e o catálogo inicial são criados
> automaticamente pelo **Flyway** quando a API Spring Boot inicia, e o ambiente pode ser recriado do zero
> com dois comandos.
