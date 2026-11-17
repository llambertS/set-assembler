
# Set-Assembler: back-end (setup-back)

API REST em **Spring Boot 3.5.0 / JDK 21 / Maven**, com **PostgreSQL 16 no Docker**, migrações com **Flyway**,
login com **Spring Security + JWT**, relatório em **OpenPDF**, **Lombok** e documentação **springdoc (Swagger)**.

```
SetAssembler/
├── setup-front/   ← React (Vite): telas
└── setup-back/    ← esta pasta: API + banco
    ├── docker-compose.yml               PostgreSQL 16 (porta 5433)
    ├── pom.xml                          dependências Maven
    ├── postman/                         coleção pronta para o Postman
    └── src/main/
        ├── java/br/com/setassembler/
        │   ├── config/        Segurança, CORS, Swagger, criação do admin
        │   ├── seguranca/     JwtService + FiltroJwt
        │   ├── dominio/       Entidades do diagrama de classes
        │   │   └── componente/  Componente + PlacaMae, Processador, MemoriaRam,
        │   │                    PlacaDeVideo, Armazenamento, Fonte, Gabinete
        │   ├── repositorio/   Spring Data JPA
        │   ├── dto/           JSON de entrada/saída
        │   ├── servico/       Regras: compatibilidade, setups, PDF, login
        │   └── web/           Controllers REST + tratamento de erros
        └── resources/
            ├── application.yml
            └── db/migration/  V1__criar_tabelas.sql, V2__catalogo_inicial.sql
```

---

## Passo a passo: Docker + PostgreSQL + IntelliJ

### 0. Instale (uma vez só)
| Ferramenta | Onde |
|---|---|
| Docker Desktop | docker.com (no Windows ele pede o WSL 2; aceite) |
| IntelliJ IDEA | jetbrains.com (Community serve; a Ultimate tem a aba *Database*) |
| JDK 21 | pelo próprio IntelliJ (passo 3) |
| Node.js LTS | nodejs.org (para o front) |
| DBeaver e Postman | opcionais, para ver o banco e testar a API |

> O Maven já vem dentro do IntelliJ, então não precisa instalar à parte.

### 1. Suba o banco no Docker
1. Abra o **Docker Desktop** e espere aparecer *Engine running*.
2. No terminal, dentro de `setup-back`:
   ```bash
   docker compose up -d
   ```
3. Confira se ele está `healthy`:
   ```bash
   docker compose ps
   ```

O `docker-compose.yml` cria:
- container `setassembler-db` com `postgres:16-alpine`
- banco `setassembler`, usuário `setassembler`, senha `setassembler`
- porta **5433** do Windows → 5432 do container. **Por que 5433?** Neste PC a 5432 já está ocupada por um
  PostgreSQL instalado no Windows. Se os dois usassem a mesma porta, a API conectaria no banco errado.
- volume `setassembler-dados`: os dados continuam lá mesmo depois de `docker compose down`.

> Dá para fazer isso **pelo IntelliJ**: abra o `docker-compose.yml` e clique no ▶ duplo ao lado de `services:`.
> O container aparece na janela **Services** (Alt+8), onde você vê logs, para e reinicia.

### 2. Abra o projeto no IntelliJ
1. **File → Open…** → selecione a pasta **`setup-back`** (a que tem o `pom.xml`) → *Trust Project*.
2. O IntelliJ reconhece o Maven e baixa as dependências (barra de progresso no rodapé). Se não começar sozinho,
   abra a aba **Maven** (lateral direita) e clique em 🔄 *Reload All Maven Projects*.

### 3. Configure o JDK 21
1. **File → Project Structure → Project**.
2. Em **SDK** → *Add SDK → Download JDK…* → Version **21**, Vendor **Eclipse Temurin** → Download.
3. **Language level**: 21.

> Neste PC só tem o JDK 25 instalado. A API até roda com ele (testei), mas o projeto é Java 21: use o 21 para
> ficar igual à apresentação e aos colegas.

### 4. Lombok
O plugin do Lombok já vem no IntelliJ. Se aparecer o aviso *"Lombok requires enabled annotation processing"*,
clique em **Enable**. Ou faça manualmente: *Settings → Build, Execution, Deployment → Compiler → Annotation Processors →
Enable annotation processing*.

### 5. Conecte o IntelliJ ao banco (para ver as tabelas)
**IntelliJ Ultimate**: aba **Database** (lateral direita) → **+ → Data Source → PostgreSQL**
| Campo | Valor |
|---|---|
| Host | `localhost` |
| Port | `5433` |
| Database | `setassembler` |
| User / Password | `setassembler` / `setassembler` |

Clique em *Download missing driver files* → **Test Connection** → OK.

**IntelliJ Community ou DBeaver**: *Nova conexão → PostgreSQL* com os mesmos dados da tabela acima.

> As tabelas só aparecem **depois** que a API rodar pela primeira vez (passo 6): quem cria as tabelas é o Flyway.

### 6. Rode a API
1. Abra `src/main/java/br/com/setassembler/SetAssemblerApplication.java` → clique no ▶ verde ao lado da classe.
2. No console, procure:
   ```
   Migrating schema "public" to version "1 - criar tabelas"
   Migrating schema "public" to version "2 - catalogo inicial"
   Started SetAssemblerApplication in ... seconds
   Administrador criado: admin@setassembler.local
   ```
3. Abra **http://localhost:8080/swagger-ui.html**: todos os endpoints, testáveis pelo navegador
   (botão **Authorize** → cole o token do login).

O que aconteceu na primeira execução:
- **Flyway** rodou `V1` (tabelas `usuario`, `componente`, `setup`, `setup_item`) e `V2` (31 peças do catálogo),
  e registrou tudo na tabela `flyway_schema_history`.
- **Hibernate** (`ddl-auto: validate`) conferiu se as entidades Java batem com as tabelas. Se algo não bater,
  a API nem sobe, e assim você descobre o erro na hora.
- **AdminInicial** criou o administrador (a senha vai com hash BCrypt, por isso não fica no SQL).

### 7. Rode o front
```bash
cd ../setup-front
npm install
npm run dev
```
Abra http://localhost:5173. O Vite repassa tudo o que começa com `/api` para `localhost:8080`
(`vite.config.js`), por isso o front não precisa saber o endereço da API.

### 8. Teste com o Postman
*Import* → `postman/SetAssembler.postman_collection.json`. Rode **Login (admin)** ou **Cadastro** primeiro:
o token fica salvo na variável `{{token}}` e vale para as outras requisições. Para o PDF, use *Send and Download*.

---

## Contas
| Papel | E-mail | Senha |
|---|---|---|
| Admin (Manter Componentes) | `admin@setassembler.local` | `admin123` |
| Usuário | crie pela tela de cadastro | |

> Troque a senha do admin e o `JWT_SECRET` por variáveis de ambiente antes de publicar
> (*Run → Edit Configurations → Environment variables*: `ADMIN_SENHA=...;JWT_SECRET=...`).

## Endpoints
| Método | Rota | Quem | Caso de uso |
|---|---|---|---|
| POST | `/api/auth/cadastro` | livre | Realizar cadastro |
| POST | `/api/auth/login` | livre | Efetuar login |
| GET | `/api/componentes?categoria=&busca=` | livre | Consultar catálogo / montar setup |
| GET | `/api/componentes/{id}` | livre | Ficha da peça |
| POST/PUT/DELETE | `/api/componentes[/{id}]` | ADMIN | Manter Componentes |
| GET | `/api/setups` | logado | Consultar lista |
| GET | `/api/setups/{id}` | dono | Gerar relatório |
| POST | `/api/setups` | logado | Salvar setup (valida compatibilidade) |
| PUT | `/api/setups/{id}` | dono | Alterar setup |
| DELETE | `/api/setups/{id}` | dono | Excluir setup |
| GET | `/api/setups/{id}/pdf` | dono | Exportar setup (PDF) |

O servidor **revalida** a compatibilidade ao salvar, mesmo que o front já filtre as listas
(veja `CompatibilidadeService`). Ele confere socket CPU × placa, DDR4/DDR5, frequência máxima, nº de pentes,
slots M.2, gabinete × formato/comprimento da GPU e fonte ≥ (consumo × 1,3, arredondado para cima em 50 W).
Se algo falhar, a resposta é **422** com a lista de problemas.

## Mudou o banco? Crie uma migração nova
Nunca edite `V1`/`V2` depois que já rodaram: o Flyway compara o checksum e a API não sobe.
Crie `src/main/resources/db/migration/V3__descricao.sql`, por exemplo:
```sql
ALTER TABLE componente ADD COLUMN url_imagem VARCHAR(255);
```
Depois adicione o campo na entidade Java correspondente e reinicie a API.

## Comandos úteis do Docker
```bash
docker compose ps                         # status
docker compose logs -f postgres           # logs do banco
docker exec -it setassembler-db psql -U setassembler -d setassembler   # SQL direto
docker compose down                       # para (mantém os dados)
docker compose down -v                    # para e APAGA os dados (Flyway recria tudo no próximo start)
```

## Problemas comuns
| Sintoma | Causa / solução |
|---|---|
| `Connection refused` na porta 5433 | Docker Desktop fechado ou container parado → `docker compose up -d` |
| `password authentication failed` | A API está apontando para a 5432 (Postgres do Windows). Confira a URL no `application.yml` |
| `Validate failed: Migrations have failed validation` | Uma migração antiga foi editada. Desfaça a edição ou, em dev, `docker compose down -v` |
| `Schema-validation: missing column` | A entidade tem um campo que não existe na tabela → crie uma migração `V3__...` |
| Erros de getter/setter "não encontrado" | Annotation processing desligado (passo 4) |
| `Port 8080 was already in use` | Já tem outra API rodando: pare-a no IntelliJ (■) |
| Front mostra "Não foi possível falar com o servidor" | API não está rodando na 8080 |
