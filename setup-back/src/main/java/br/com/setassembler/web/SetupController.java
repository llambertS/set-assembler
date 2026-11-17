package br.com.setassembler.web;

import br.com.setassembler.dominio.Usuario;
import br.com.setassembler.dto.SetupDTOs.SetupRequest;
import br.com.setassembler.dto.SetupDTOs.SetupResponse;
import br.com.setassembler.servico.RelatorioPdfService;
import br.com.setassembler.servico.SetupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.List;

@Tag(name = "Setups", description = "Montar, salvar, consultar, alterar, excluir e exportar setups do usuário logado")
@RestController
@RequestMapping("/api/setups")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService service;
    private final RelatorioPdfService pdf;

    @Operation(summary = "Consultar lista de setups salvos")
    @GetMapping
    public List<SetupResponse> listar(@AuthenticationPrincipal Usuario u) {
        return service.listar(u);
    }

    @Operation(summary = "Relatório (dados) de um setup")
    @GetMapping("/{id}")
    public SetupResponse buscar(@AuthenticationPrincipal Usuario u, @PathVariable Long id) {
        return service.buscar(u, id);
    }

    @Operation(summary = "Salvar setup (valida compatibilidade e energia)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SetupResponse criar(@AuthenticationPrincipal Usuario u, @Valid @RequestBody SetupRequest req) {
        return service.criar(u, req);
    }

    @Operation(summary = "Alterar setup")
    @PutMapping("/{id}")
    public SetupResponse atualizar(@AuthenticationPrincipal Usuario u, @PathVariable Long id,
                                   @Valid @RequestBody SetupRequest req) {
        return service.atualizar(u, id, req);
    }

    @Operation(summary = "Excluir setup")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal Usuario u, @PathVariable Long id) {
        service.excluir(u, id);
    }

    @Operation(summary = "Exportar relatório em PDF")
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportarPdf(@AuthenticationPrincipal Usuario u, @PathVariable Long id) {
        record Arquivo(String nome, byte[] bytes) {
        }
        Arquivo a = service.comSetup(u, id, s -> new Arquivo(s.getNome(), pdf.gerar(s)));
        String nomeArquivo = "setup-" + slug(a.nome()) + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nomeArquivo).build().toString())
                .body(a.bytes());
    }

    private static String slug(String s) {
        String semAcento = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String r = semAcento.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return r.isEmpty() ? "sem-nome" : r;
    }
}
