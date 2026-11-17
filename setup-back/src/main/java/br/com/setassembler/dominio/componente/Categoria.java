package br.com.setassembler.dominio.componente;

import java.util.Arrays;

/** Categorias de peça. O "id" é o mesmo usado pelo front-end e pela coluna componente.categoria. */
public enum Categoria {
    PLACA("placa", "Placa-mãe"),
    CPU("cpu", "Processador"),
    RAM("ram", "Memória RAM"),
    GPU("gpu", "Placa de vídeo"),
    ARMAZENAMENTO("armazenamento", "Armazenamento"),
    FONTE("fonte", "Fonte"),
    GABINETE("gabinete", "Gabinete");

    private final String id;
    private final String rotulo;

    Categoria(String id, String rotulo) {
        this.id = id;
        this.rotulo = rotulo;
    }

    public String id() { return id; }
    public String rotulo() { return rotulo; }

    public static Categoria deId(String id) {
        return Arrays.stream(values()).filter(c -> c.id.equalsIgnoreCase(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida: " + id));
    }
}
