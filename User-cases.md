Com base no fluxo definido, o sistema é composto por **8 Casos de Uso** principais, que cobrem perfeitamente a modelagem conceitual exigida:

- **Caso de Uso 01 – Montar Setup (Assistente Passo a Passo):** O usuário inicia uma nova configuração e seleciona os componentes em ordem lógica (Placa-Mãe $\rightarrow$ CPU $\rightarrow$ RAM $\rightarrow$ GPU $\rightarrow$ Armazenamento $\rightarrow$ Fonte).
    
- ~~**Caso de Uso 02 – Filtrar Compatibilidade e Energia (`<<include>>` do CDU01):** A cada etapa avançada no CDU01, o sistema executa automaticamente a validação cruzada no banco de dados (igualando _sockets_, padrões de memória e calculando o somatório de Watts para a fonte), ocultando peças incompatíveis.~~
    
- **Caso de Uso 02 – Gerar Relatório Visual do Setup:** Ao finalizar o passo a passo (ou ao abrir um setup salvo), o sistema renderiza uma tela consolidada exibindo o resumo da máquina, as especificações completas de cada componente individual, status de aprovação energética e o valor total do orçamento.
    
- **Caso de Uso 03 – Salvar Setup na Lista:** A partir do relatório final da montagem, o usuário atribui um nome personalizado à máquina (ex: _"PC Gamer Custo-Benefício"_) e persiste aquela combinação de peças no banco de dados.
    
- **Caso de Uso 04 – Consultar Lista de Setups Salvos:** O usuário acessa a tela "Meus Setups" a partir do menu inicial para visualizar todas as configurações que já montou anteriormente, exibindo nome e valor total de cada uma.
    
- **Casos de Uso 05 – Alterar Setup Existente:** Na lista de setups salvos, o usuário seleciona a opção de editar uma máquina. O sistema carrega as peças atuais no assistente passo a passo, permitindo substituir componentes (revalidando a compatibilidade caso troque uma peça chave) e atualizar o registro.
    
- **Caso de Uso 06 – Excluir Setup:** Na lista de setups salvos, o usuário remove permanentemente uma configuração que não deseja mais manter no banco de dados.
    
- **Caso de Uso 07 – Consultar Catálogo de Componentes (Busca Individual):** A partir da tela inicial, o usuário acessa o buscador avulso de peças, podendo filtrar por categoria (Processadores, Placas de Vídeo, etc.) ou pesquisar por nome para abrir uma ficha simples contendo as especificações técnicas e o preço daquele componente isolado.