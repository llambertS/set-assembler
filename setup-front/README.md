# Set-Assembler: front-end (setup-front)

React 19 + Vite. As telas falam com a API Spring Boot da pasta `../setup-back` por `src/api.js`.

```bash
npm install
npm run dev      # http://localhost:5173  (a API precisa estar rodando na 8080)
```

- `src/api.js`: todas as chamadas HTTP (login JWT, catálogo, setups, download do PDF).
- `src/dados.js`: regras de filtragem do assistente (as mesmas que o servidor revalida).
- `src/animacoes.jsx`: contador animado, esqueletos de carregamento e avisos.
- `vite.config.js`: proxy de `/api` para `http://localhost:8080`.

O guia completo (Docker, IntelliJ, banco) está em `../setup-back/README.md`.
