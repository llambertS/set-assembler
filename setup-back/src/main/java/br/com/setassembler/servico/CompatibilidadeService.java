package br.com.setassembler.servico;

import br.com.setassembler.dominio.SetupItem;
import br.com.setassembler.dominio.componente.*;
import br.com.setassembler.web.ApiException;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras de compatibilidade física e elétrica (as mesmas que o front usa para filtrar as listas).
 * O front esconde as peças incompatíveis; aqui o servidor confere de novo antes de gravar,
 * porque qualquer um pode chamar a API direto (Postman, por exemplo).
 */
@Service
public class CompatibilidadeService {

    /** Consumo fixo de placa-mãe, ventoinhas, etc. */
    public static final int CONSUMO_BASE_W = 100;
    /** Margem de segurança da fonte (30%). */
    public static final double MARGEM_FONTE = 1.3;

    /** Resultado da análise de um conjunto de peças. */
    public record Analise(BigDecimal precoTotal, int consumoW, int recomendadaW, int fonteW) {
        public boolean energiaAprovada() {
            return fonteW >= recomendadaW;
        }
    }

    public Analise validar(List<SetupItem> itens) {
        List<String> problemas = new ArrayList<>();

        PlacaMae placa = unico(itens, PlacaMae.class, "placa-mãe", problemas);
        Processador cpu = unico(itens, Processador.class, "processador", problemas);
        PlacaDeVideo gpu = unico(itens, PlacaDeVideo.class, "placa de vídeo", problemas);
        Fonte fonte = unico(itens, Fonte.class, "fonte", problemas);
        SetupItem itemRam = itemUnico(itens, MemoriaRam.class, "memória RAM", problemas);
        Gabinete gabinete = opcional(itens, Gabinete.class, problemas);
        List<Armazenamento> arms = todos(itens, Armazenamento.class);
        if (arms.isEmpty()) problemas.add("Escolha pelo menos um armazenamento.");

        for (SetupItem i : itens) {
            if (i.getQuantidade() > 1 && !(real(i) instanceof MemoriaRam) && !(real(i) instanceof Armazenamento)) {
                problemas.add("Só é possível repetir memória RAM e armazenamento.");
            }
        }

        if (placa != null) {
            if (cpu != null && !placa.getSocket().equalsIgnoreCase(cpu.getSocket())) {
                problemas.add("Processador " + cpu.getModelo() + " (" + cpu.getSocket()
                        + ") não encaixa no socket " + placa.getSocket() + " da placa-mãe.");
            }
            if (itemRam != null) {
                MemoriaRam ram = (MemoriaRam) real(itemRam);
                if (!placa.getTipoMemoria().equalsIgnoreCase(ram.getTipo())) {
                    problemas.add("A placa-mãe usa " + placa.getTipoMemoria() + ", mas a memória é " + ram.getTipo() + ".");
                }
                if (ram.getFrequenciaMhz() > placa.getRamMaxMhz()) {
                    problemas.add("A memória de " + ram.getFrequenciaMhz() + " MHz passa do máximo da placa ("
                            + placa.getRamMaxMhz() + " MHz).");
                }
                if (itemRam.getQuantidade() > placa.getSlotsRam()) {
                    problemas.add("A placa-mãe tem só " + placa.getSlotsRam() + " slots de RAM.");
                }
            }
            long m2 = itens.stream().filter(i -> real(i) instanceof Armazenamento a && a.isM2())
                    .mapToLong(SetupItem::getQuantidade).sum();
            if (m2 > placa.getSlotsM2()) {
                problemas.add("A placa-mãe tem só " + placa.getSlotsM2() + " slot(s) M.2.");
            }
            if (gabinete != null && !gabinete.listaFormatos().contains(placa.getFormato())) {
                problemas.add("O gabinete não aceita placas " + placa.getFormato() + ".");
            }
        }
        if (gabinete != null && gpu != null && gpu.getComprimentoMm() > gabinete.getMaxGpuMm()) {
            problemas.add("A placa de vídeo (" + gpu.getComprimentoMm() + " mm) não cabe no gabinete (até "
                    + gabinete.getMaxGpuMm() + " mm).");
        }

        int consumo = consumo(itens);
        int recomendada = recomendada(consumo);
        int fonteW = fonte == null ? 0 : fonte.getPotenciaW();
        if (fonte != null && fonteW < recomendada) {
            problemas.add("Fonte de " + fonteW + " W é insuficiente: o mínimo recomendado é " + recomendada + " W.");
        }

        if (!problemas.isEmpty()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "O setup tem peças incompatíveis.",
                    problemas.stream().distinct().toList());
        }
        BigDecimal total = itens.stream().map(SetupItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Analise(total, consumo, recomendada, fonteW);
    }

    /** CPU + GPU + RAM (por pente) + armazenamentos + 100 W de base. */
    public int consumo(List<SetupItem> itens) {
        return CONSUMO_BASE_W + itens.stream().mapToInt(i -> real(i).consumoW() * i.getQuantidade()).sum();
    }

    /** Consumo + 30%, arredondado para cima em passos de 50 W. */
    public int recomendada(int consumo) {
        return (int) (Math.ceil(consumo * MARGEM_FONTE / 50.0) * 50);
    }

    // ---- auxiliares ----

    private static Componente real(SetupItem i) {
        return Hibernate.unproxy(i.getComponente(), Componente.class);
    }

    private static <T extends Componente> List<T> todos(List<SetupItem> itens, Class<T> tipo) {
        return itens.stream().map(CompatibilidadeService::real).filter(tipo::isInstance).map(tipo::cast).toList();
    }

    private static <T extends Componente> SetupItem itemUnico(List<SetupItem> itens, Class<T> tipo, String nome,
                                                              List<String> problemas) {
        List<SetupItem> achados = itens.stream().filter(i -> tipo.isInstance(real(i))).toList();
        if (achados.size() != 1) {
            problemas.add(achados.isEmpty() ? "Falta escolher: " + nome + "." : "Escolha só um modelo de " + nome + ".");
            return null;
        }
        return achados.get(0);
    }

    private static <T extends Componente> T unico(List<SetupItem> itens, Class<T> tipo, String nome,
                                                  List<String> problemas) {
        SetupItem i = itemUnico(itens, tipo, nome, problemas);
        return i == null ? null : tipo.cast(real(i));
    }

    private static <T extends Componente> T opcional(List<SetupItem> itens, Class<T> tipo, List<String> problemas) {
        List<T> achados = todos(itens, tipo);
        if (achados.size() > 1) problemas.add("Escolha só um gabinete.");
        return achados.isEmpty() ? null : achados.get(0);
    }
}
