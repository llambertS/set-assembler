### **1. Visão Geral do Sistema** 

#### 1. Visão Geral e Fluxo do Sistema

O **Set-Assembler** é uma aplicação interativa voltada para auxiliar usuários na montagem de computadores desktop, eliminando erros de incompatibilidade física e elétrica entre componentes. O sistema conta com uma base de dados previamente populada com peças reais e suas especificações técnicas (sockets, tipo de memória, frequências e consumo energético em Watts/TDP).

**Fluxo de Navegação e Experiência do Usuário (Interfaces):**

- **Tela Inicial (Dashboard):** Apresenta três caminhos principais de navegação:
    
    1. _Criar Novo Setup_ (inicia o assistente passo a passo).
        
    2. _Meus Setups_ (acessa a lista de configurações já salvas).
        
    3. _Explorar Catálogo_ (busca avulsa de peças para consulta de especificações).
        
- **Fluxo de Montagem Guiada (Passo a Passo):** Ao iniciar um novo setup, o usuário segue uma trilha sequencial lógica:
    
    1. **Placa-Mãe (Mobo):** Ponto de partida que define as regras base da máquina (Socket e geração de RAM).
        
    2. **Processador (CPU):** O sistema já exibe a lista filtrada apenas com processadores compatíveis com o socket da placa-mãe escolhida.
        
    3. **Memória RAM:** Lista filtrada pelo padrão (ex: DDR4/DDR5) e frequência suportada pela placa-mãe.
        
    4. **Placa de Vídeo (GPU) e Armazenamento:** Seleção livre via barramento PCIe/SATA/NVMe, somando a demanda energética ao conjunto.
        
    5. **Fonte de Alimentação (PSU):** O sistema soma o consumo total (TDP) das peças anteriores, aplica uma margem de segurança e filtra/recomenda apenas fontes capazes de segurar a configuração com estabilidade.
        
- **Tela de Resumo e Relatório Final:** Ao concluir a seleção, o sistema exibe uma tela de fechamento ("Ficha da Máquina"), apresentando o _setup_ completo, o detalhamento técnico de cada peça escolhida, o consumo estimado em Watts e o orçamento total (soma dos preços).
    
- **Gestão de Setups Salvos:** O usuário pode salvar quantos _setups_ quiser. Na tela "Meus Setups", ele visualiza sua lista e pode abrir o relatório de uma máquina pronta, carregar um setup de volta para o assistente para trocar peças (Alterar) ou removê-lo da lista (Deletar).
    

#### 2. Identificação dos Atores

- **Usuário:** Único ator interagente do sistema, responsável por simular montagens, gerenciar sua lista de _setups_ e consultar o catálogo de peças. _(Nota: O catálogo de hardware já estará persistido no banco de dados como pré-condição do sistema)._
    

