# Set-Assembler

Aplicação para montar computadores desktop sem erros de compatibilidade: o assistente filtra as peças a cada etapa
(socket, padrão de memória, slots, consumo e fonte), salva os setups do usuário e exporta a Ficha da Máquina em PDF.

| Parte | Tecnologias | Pasta |
|---|---|---|
| Front-end | React 19 + Vite | [`setup-front`](setup-front) |
| Back-end | Java 21, Spring Boot 3.5, Spring Security + JWT, OpenPDF, Lombok, springdoc | [`setup-back`](setup-back) |
| Banco | PostgreSQL 16 no Docker + Flyway | [`setup-back/docker-compose.yml`](setup-back/docker-compose.yml) |

## Como rodar
```bash
cd setup-back
docker compose up -d          # 1. banco (Docker Desktop aberto)
```
2. Abra `setup-back` no IntelliJ e rode o `SetAssemblerApplication` (API em http://localhost:8080).
```bash
cd setup-front
npm install
npm run dev                   # 3. front em http://localhost:5173
```

Admin de desenvolvimento: `admin@setassembler.local` / `admin123`.

## Guias
- [Back-end, banco e IntelliJ](setup-back/README.md)
- [Docker](GUIA-DOCKER.md)
- [Git e GitHub](GUIA-GITHUB.md)
- Documentação da API: http://localhost:8080/swagger-ui.html (com a API rodando)
- Testes da API no Postman: `setup-back/postman/`
