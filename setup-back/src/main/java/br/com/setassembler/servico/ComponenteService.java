package br.com.setassembler.servico;

import br.com.setassembler.dominio.componente.Categoria;
import br.com.setassembler.dominio.componente.Componente;
import br.com.setassembler.dto.ComponenteDTO;
import br.com.setassembler.repositorio.ComponenteRepository;
import br.com.setassembler.repositorio.SetupRepository;
import br.com.setassembler.web.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Caso de uso 07 (Consultar Catálogo) e "Manter Componentes" (admin). */
@Service
@RequiredArgsConstructor
public class ComponenteService {

    private final ComponenteRepository componentes;
    private final SetupRepository setups;
    private final ComponenteMapper mapper;

    @Transactional(readOnly = true)
    public List<ComponenteDTO> listar(String categoria, String busca) {
        Categoria cat = categoria == null || categoria.isBlank() || categoria.equals("todas")
                ? null : Categoria.deId(categoria);
        return componentes.buscar(busca == null ? "" : busca.trim()).stream()
                .filter(c -> cat == null || c.getCategoria() == cat)
                .map(mapper::paraDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComponenteDTO buscar(Long id) {
        return mapper.paraDto(carregar(id));
    }

    @Transactional
    public ComponenteDTO criar(ComponenteDTO dto) {
        return mapper.paraDto(componentes.save(mapper.novo(dto)));
    }

    @Transactional
    public ComponenteDTO atualizar(Long id, ComponenteDTO dto) {
        Componente c = carregar(id);
        mapper.aplicar(dto, c);
        return mapper.paraDto(c);
    }

    @Transactional
    public void excluir(Long id) {
        Componente c = carregar(id);
        if (setups.existsByItensComponenteId(id)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Este componente faz parte de setups salvos e não pode ser removido.");
        }
        componentes.delete(c);
    }

    private Componente carregar(Long id) {
        return componentes.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Componente"));
    }
}
