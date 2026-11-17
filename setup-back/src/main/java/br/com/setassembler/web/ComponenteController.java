package br.com.setassembler.web;

import br.com.setassembler.dto.ComponenteDTO;
import br.com.setassembler.servico.ComponenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Componentes", description = "Catálogo (consulta livre) e Manter Componentes (somente ADMIN)")
@RestController
@RequestMapping("/api/componentes")
@RequiredArgsConstructor
public class ComponenteController {

    private final ComponenteService service;

    @Operation(summary = "Consultar catálogo — filtra por categoria (placa, cpu, ram, gpu, armazenamento, fonte, gabinete) e/ou busca")
    @GetMapping
    public List<ComponenteDTO> listar(@RequestParam(required = false) String categoria,
                                      @RequestParam(required = false) String busca) {
        return service.listar(categoria, busca);
    }

    @Operation(summary = "Ficha de um componente")
    @GetMapping("/{id}")
    public ComponenteDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Cadastrar componente (ADMIN)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComponenteDTO criar(@Valid @RequestBody ComponenteDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Editar componente (ADMIN)")
    @PutMapping("/{id}")
    public ComponenteDTO atualizar(@PathVariable Long id, @Valid @RequestBody ComponenteDTO dto) {
        return service.atualizar(id, dto);
    }

    @Operation(summary = "Remover componente (ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
