package br.com.setassembler.servico;

import br.com.setassembler.dominio.Setup;
import br.com.setassembler.dominio.SetupItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Caso de uso "Gerar Relatório" + "Exportar Setup": a Ficha da Máquina em PDF (OpenPDF). */
@Service
public class RelatorioPdfService {

    private static final Color LARANJA = new Color(0xF0, 0xA0, 0x4B);
    private static final Color ESCURO = new Color(0x17, 0x1F, 0x28);
    private static final Color CINZA = new Color(0x93, 0xA1, 0xB0);
    private static final Color VERDE = new Color(0x2E, 0x8B, 0x57);
    private static final Color VERMELHO = new Color(0xC0, 0x39, 0x2B);
    private static final NumberFormat REAIS = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerar(Setup setup) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(doc, saida);
        doc.addTitle("Ficha da Máquina - " + setup.getNome());
        doc.addCreator("Set-Assembler");
        doc.open();

        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, ESCURO);
        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11, CINZA);
        Font cabecalho = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10, ESCURO);
        Font negrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, ESCURO);

        Paragraph marca = new Paragraph("SET-ASSEMBLER", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, LARANJA));
        doc.add(marca);
        doc.add(new Paragraph("Ficha da Máquina", titulo));
        LocalDateTime quando = setup.getAtualizadoEm() != null ? setup.getAtualizadoEm() : LocalDateTime.now();
        doc.add(new Paragraph(setup.getNome() + "  ·  " + setup.getUsuario().getNome() + "  ·  " + DATA.format(quando),
                subtitulo));
        doc.add(Chunk.NEWLINE);

        PdfPTable tabela = new PdfPTable(new float[]{2.2f, 1.6f, 3.2f, 3.8f, 0.8f, 1.6f});
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);
        for (String h : new String[]{"Componente", "Marca", "Modelo", "Especificações", "Qtd", "Preço"}) {
            PdfPCell c = new PdfPCell(new Phrase(h, cabecalho));
            c.setBackgroundColor(ESCURO);
            c.setPadding(7);
            c.setBorderColor(ESCURO);
            tabela.addCell(c);
        }
        boolean zebra = false;
        for (SetupItem i : SetupService.itensOrdenados(setup)) {
            Color fundo = zebra ? new Color(0xF4, 0xF6, 0xF8) : Color.WHITE;
            celula(tabela, i.getComponente().getCategoria().rotulo(), normal, fundo, Element.ALIGN_LEFT);
            celula(tabela, i.getComponente().getMarca(), normal, fundo, Element.ALIGN_LEFT);
            celula(tabela, i.getComponente().getModelo(), normal, fundo, Element.ALIGN_LEFT);
            celula(tabela, i.getComponente().getDescricao(), normal, fundo, Element.ALIGN_LEFT);
            celula(tabela, String.valueOf(i.getQuantidade()), normal, fundo, Element.ALIGN_CENTER);
            celula(tabela, reais(i.subtotal()), normal, fundo, Element.ALIGN_RIGHT);
            zebra = !zebra;
        }
        PdfPCell rotuloTotal = new PdfPCell(new Phrase("Orçamento total", negrito));
        rotuloTotal.setColspan(5);
        rotuloTotal.setPadding(8);
        rotuloTotal.setBorderColor(CINZA);
        tabela.addCell(rotuloTotal);
        PdfPCell valorTotal = new PdfPCell(new Phrase(reais(setup.getPrecoTotal()), negrito));
        valorTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valorTotal.setPadding(8);
        valorTotal.setBorderColor(CINZA);
        valorTotal.setBackgroundColor(new Color(0xFD, 0xF1, 0xE4));
        tabela.addCell(valorTotal);
        doc.add(tabela);

        int fonteW = SetupService.potenciaFonte(setup);
        boolean ok = fonteW >= setup.getRecomendadaW();
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph(String.format("Consumo estimado: %d W  ·  Fonte: %d W  ·  Mínimo recomendado: %d W",
                setup.getConsumoW(), fonteW, setup.getRecomendadaW()), normal));
        doc.add(new Paragraph(ok ? "Aprovado na análise de energia." : "Fonte insuficiente.",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, ok ? VERDE : VERMELHO)));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Preços aproximados. Consumo calculado como CPU + GPU + RAM + armazenamento + 100 W "
                + "de base, com 30% de margem de segurança para a fonte.", subtitulo));

        doc.close();
        return saida.toByteArray();
    }

    private static void celula(PdfPTable t, String texto, Font f, Color fundo, int alinhamento) {
        PdfPCell c = new PdfPCell(new Phrase(texto == null ? "-" : texto, f));
        c.setBackgroundColor(fundo);
        c.setPadding(6);
        c.setBorderColor(new Color(0xDD, 0xE2, 0xE8));
        c.setHorizontalAlignment(alinhamento);
        t.addCell(c);
    }

    private static String reais(BigDecimal v) {
        return REAIS.format(v);
    }
}
