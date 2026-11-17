package br.com.setassembler.servico;

import br.com.setassembler.dominio.componente.*;
import br.com.setassembler.dto.ComponenteDTO;
import br.com.setassembler.web.ApiException;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converte entre as subclasses de Componente (banco) e o ComponenteDTO (JSON do front). */
@Component
public class ComponenteMapper {

    public ComponenteDTO paraDto(Componente componente) {
        Componente c = Hibernate.unproxy(componente, Componente.class);
        ComponenteDTO.ComponenteDTOBuilder b = ComponenteDTO.builder()
                .id(c.getId())
                .categoria(c.getCategoria().id())
                .marca(c.getMarca())
                .nome(c.getModelo())
                .info(c.getDescricao())
                .preco(c.getPreco());

        switch (c) {
            case PlacaMae p -> b.socket(p.getSocket()).memoria(p.getTipoMemoria()).formato(p.getFormato())
                    .slotsRam(p.getSlotsRam()).ramMax(p.getRamMaxMhz()).m2(p.getSlotsM2());
            case Processador p -> b.socket(p.getSocket()).tdp(p.getTdpW());
            case MemoriaRam r -> b.tipo(r.getTipo()).freq(r.getFrequenciaMhz()).tdp(r.getTdpW());
            case PlacaDeVideo g -> b.tdp(g.getTdpW()).comp(g.getComprimentoMm());
            case Armazenamento a -> b.tipo(a.getTipo()).tdp(a.getTdpW());
            case Fonte f -> b.watts(f.getPotenciaW());
            case Gabinete g -> b.formatos(g.listaFormatos()).maxGpu(g.getMaxGpuMm());
            default -> throw new IllegalStateException("Categoria desconhecida: " + c.getClass());
        }
        return b.build();
    }

    /** Cria a subclasse certa de acordo com a categoria do DTO. */
    public Componente novo(ComponenteDTO dto) {
        Componente c = switch (Categoria.deId(dto.categoria())) {
            case PLACA -> new PlacaMae();
            case CPU -> new Processador();
            case RAM -> new MemoriaRam();
            case GPU -> new PlacaDeVideo();
            case ARMAZENAMENTO -> new Armazenamento();
            case FONTE -> new Fonte();
            case GABINETE -> new Gabinete();
        };
        aplicar(dto, c);
        return c;
    }

    /** Copia os dados do DTO para a entidade, exigindo as especificações de cada categoria. */
    public void aplicar(ComponenteDTO dto, Componente c) {
        if (!c.getCategoria().id().equalsIgnoreCase(dto.categoria())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Não é possível mudar a categoria de um componente.");
        }
        c.setMarca(dto.marca().trim());
        c.setModelo(dto.nome().trim());
        c.setDescricao(dto.info().trim());
        c.setPreco(dto.preco());

        switch (c) {
            case PlacaMae p -> {
                p.setSocket(texto(dto.socket(), "Socket"));
                p.setTipoMemoria(opcao(dto.memoria(), "Tipo de memória", List.of("DDR4", "DDR5")));
                p.setFormato(opcao(dto.formato(), "Formato", List.of("mATX", "ATX", "ITX")));
                p.setSlotsRam(num(dto.slotsRam(), "Slots de RAM"));
                p.setRamMaxMhz(num(dto.ramMax(), "Frequência máx. de RAM"));
                p.setSlotsM2(num(dto.m2(), "Slots M.2"));
            }
            case Processador p -> {
                p.setSocket(texto(dto.socket(), "Socket"));
                p.setTdpW(num(dto.tdp(), "Consumo"));
            }
            case MemoriaRam r -> {
                r.setTipo(opcao(dto.tipo(), "Tipo", List.of("DDR4", "DDR5")));
                r.setFrequenciaMhz(num(dto.freq(), "Frequência"));
                r.setTdpW(num(dto.tdp(), "Consumo"));
            }
            case PlacaDeVideo g -> {
                g.setTdpW(num(dto.tdp(), "Consumo"));
                g.setComprimentoMm(num(dto.comp(), "Comprimento"));
            }
            case Armazenamento a -> {
                a.setTipo(opcao(dto.tipo(), "Tipo", List.of("M.2", "SATA")));
                a.setTdpW(num(dto.tdp(), "Consumo"));
            }
            case Fonte f -> f.setPotenciaW(num(dto.watts(), "Potência"));
            case Gabinete g -> {
                if (dto.formatos() == null || dto.formatos().isEmpty()) {
                    throw obrigatorio("Formatos de placa aceitos");
                }
                g.setFormatos(String.join(",", dto.formatos().stream().map(String::trim).toList()));
                g.setMaxGpuMm(num(dto.maxGpu(), "Placa de vídeo até (mm)"));
            }
            default -> throw new IllegalStateException("Categoria desconhecida: " + c.getClass());
        }
    }

    private static String texto(String v, String campo) {
        if (v == null || v.isBlank()) throw obrigatorio(campo);
        return v.trim();
    }

    private static String opcao(String v, String campo, List<String> validas) {
        String t = texto(v, campo);
        return validas.stream().filter(o -> o.equalsIgnoreCase(t)).findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                        campo + " deve ser um destes: " + String.join(", ", validas) + "."));
    }

    private static Integer num(Integer v, String campo) {
        if (v == null) throw obrigatorio(campo);
        return v;
    }

    private static ApiException obrigatorio(String campo) {
        return new ApiException(HttpStatus.BAD_REQUEST, "Informe: " + campo + ".");
    }
}
