package br.com.setassembler.servico;

import br.com.setassembler.dominio.Setup;
import br.com.setassembler.dominio.SetupItem;
import br.com.setassembler.dominio.Usuario;
import br.com.setassembler.dominio.componente.Componente;
import br.com.setassembler.dominio.componente.Fonte;
import br.com.setassembler.dto.SetupDTOs.ItemRequest;
import br.com.setassembler.dto.SetupDTOs.ItemResponse;
import br.com.setassembler.dto.SetupDTOs.SetupRequest;
import br.com.setassembler.dto.SetupDTOs.SetupResponse;
import br.com.setassembler.repositorio.ComponenteRepository;
import br.com.setassembler.repositorio.SetupRepository;
import br.com.setassembler.web.ApiException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Casos de uso 03 (Salvar), 04 (Consultar lista), 05 (Alterar) e 06 (Excluir) setup. */
@Service
@RequiredArgsConstructor
public class SetupService {

    private final SetupRepository setups;
    private final ComponenteRepository componentes;
    private final CompatibilidadeService compatibilidade;
    private final ComponenteMapper mapper;

    @Transactional(readOnly = true)
    public List<SetupResponse> listar(Usuario usuario) {
        return setups.findByUsuarioIdOrderByAtualizadoEmDesc(usuario.getId()).stream().map(this::paraResposta).toList();
    }

    @Transactional(readOnly = true)
    public SetupResponse buscar(Usuario usuario, Long id) {
        return paraResposta(carregar(usuario, id));
    }

    @Transactional
    public SetupResponse criar(Usuario usuario, SetupRequest req) {
        Setup s = new Setup();
        s.setUsuario(usuario);
        preencher(s, req);
        return paraResposta(setups.save(s));
    }

    @Transactional
    public SetupResponse atualizar(Usuario usuario, Long id, SetupRequest req) {
        Setup s = carregar(usuario, id);
        preencher(s, req);
        return paraResposta(setups.saveAndFlush(s));
    }

    @Transactional
    public void excluir(Usuario usuario, Long id) {
        setups.delete(carregar(usuario, id));
    }

    /** Usado pelo relatório em PDF (precisa da entidade completa). */
    @Transactional(readOnly = true)
    public <T> T comSetup(Usuario usuario, Long id, Function<Setup, T> acao) {
        return acao.apply(carregar(usuario, id));
    }

    // ---- auxiliares ----

    private void preencher(Setup s, SetupRequest req) {
        List<Long> ids = req.itens().stream().map(ItemRequest::componenteId).distinct().toList();
        Map<Long, Componente> encontrados = componentes.findAllById(ids).stream()
                .collect(Collectors.toMap(Componente::getId, Function.identity()));
        List<SetupItem> itens = req.itens().stream().map(i -> {
            Componente c = encontrados.get(i.componenteId());
            if (c == null) throw ApiException.naoEncontrado("Componente " + i.componenteId());
            return new SetupItem(c, i.qtd());
        }).toList();

        CompatibilidadeService.Analise a = compatibilidade.validar(itens);
        s.setNome(req.nome().trim());
        s.setPrecoTotal(a.precoTotal());
        s.setConsumoW(a.consumoW());
        s.setRecomendadaW(a.recomendadaW());
        s.substituirItens(itens);
    }

    private Setup carregar(Usuario usuario, Long id) {
        return setups.findByIdAndUsuarioId(id, usuario.getId()).orElseThrow(() -> ApiException.naoEncontrado("Setup"));
    }

    /** Itens ordenados na mesma ordem do assistente (placa, cpu, ram, gpu, armazenamento, fonte, gabinete). */
    public static List<SetupItem> itensOrdenados(Setup s) {
        return s.getItens().stream()
                .sorted(Comparator.comparing((SetupItem i) -> i.getComponente().getCategoria().ordinal())
                        .thenComparing(SetupItem::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public static int potenciaFonte(Setup s) {
        return s.getItens().stream()
                .map(i -> Hibernate.unproxy(i.getComponente(), Componente.class))
                .filter(c -> c instanceof Fonte)
                .mapToInt(c -> ((Fonte) c).getPotenciaW())
                .findFirst().orElse(0);
    }

    private SetupResponse paraResposta(Setup s) {
        List<ItemResponse> itens = itensOrdenados(s).stream()
                .map(i -> new ItemResponse(i.getComponente().getCategoria().rotulo(), i.getQuantidade(), i.subtotal(),
                        mapper.paraDto(i.getComponente())))
                .toList();
        int fonteW = potenciaFonte(s);
        return new SetupResponse(s.getId(), s.getNome(), s.getPrecoTotal(), s.getConsumoW(), s.getRecomendadaW(),
                fonteW, fonteW >= s.getRecomendadaW(), s.getCriadoEm(), s.getAtualizadoEm(), itens);
    }
}
