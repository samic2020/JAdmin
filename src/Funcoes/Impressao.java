package Funcoes;

import Calculos.AvisosMensagens;
import Classes.DadosLocador;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Movimento.Avisos.TableRetencao;
import Movimento.Depositos.cDeposito;
import Movimento.FecCaixa.cBanco;
import Movimento.FecCaixa.cCaixa;
import Relatorios.Proprietarios.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import pdfViewer.PdfViewer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by supervisor on 03/05/17.
 */
public class Impressao {
    private float Autenticacao;
    private String logado;
    private String[][] Valores;
    private Date dataImpressao = null;

    DbMain conn = VariaveisGlobais.conexao;

    public Impressao(BigInteger nAut, String[][] Lancamentos) { this.Autenticacao = nAut.intValue(); this.Valores = Lancamentos; }
    public Impressao(BigInteger nAut, String[][] Lancamentos, Date DataImpressao, String logado) { this.Autenticacao = nAut.intValue(); this.Valores = Lancamentos; this.dataImpressao = DataImpressao; this.logado = logado; }
    public Impressao(String logado) { this.logado = logado; }

    public String ImprimeReciboPDF(Collections adm, DadosLocador prop, DadosLocatario loca, gRecibo corpo, boolean preview, boolean... bAttach) {
        System.out.println("ImprimeReciboPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        com.itextpdf.text.pdf.BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font =  new com.itextpdf.text.Font(bf, 9, com.itextpdf.text.Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(this.Autenticacao == 0 ? "D E M O N S T R A T I V O" : "R E C I B O", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        if (this.dataImpressao == null) {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
            table.completeRow();
            pdf.doc_add(table);
        } else {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
            table.completeRow();
            pdf.doc_add(table);
        }

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Dados do locatario
        columnWidths = new float[] {35, 65 };
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        font = new Font(bf, 8, Font.NORMAL);
        cell1 = new PdfPCell(new Phrase("Locatário: " + corpo.contrato,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(loca.getNomelocatario()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);

        font = new Font(bf, 9, Font.NORMAL);
        cell1 = new PdfPCell(new Phrase("Imóvel: " + corpo.rgimv,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("Vencimento: " + corpo.vencto,font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print(StringManager.ConvStr(loca.getEndimovel()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr( loca.getBaiimovel() + " - " + loca.getCidimovel()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(loca.getEstimovel() + " - Cep: " + loca.getCepimovel()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(VariaveisGlobais.copa_print ? "C/P" : "REF",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        // Dados do Recibo
        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        for (int i = 0; i <= corpo.corpoRecibos.length - 2; i++) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(corpo.corpoRecibos[i].getDescr(),font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase(VariaveisGlobais.copa_print ? corpo.corpoRecibos[i].getCopa() : "",font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);
            cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(corpo.corpoRecibos[i].getVlr()), font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);
        }
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(corpo.corpoRecibos[corpo.corpoRecibos.length - 1].getVlr()), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        // Mensagem de Aviso de Não Validade
        columnWidths = new float[]{100};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 8, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase(this.Autenticacao != 0 ? "" : "Este documento não vale como recibo.", font));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        if (prop != null) {
            // Dados do Locador
            columnWidths = new float[]{35, 65};
            table = new PdfPTable(columnWidths);
            table.setHeaderRows(0);
            table.setWidthPercentage(100);
            font = new Font(bf, 8, Font.NORMAL);
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase("Propriet(s): " + prop.getRgprp(), font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.TOP);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(prop.getNomeprop()), font));
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setBorder(Rectangle.TOP);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);

            // Divisão
/*
            String dvSql = "SELECT id, rgprp, rgimv, cpf, perc, taxa, rgprp_dv FROM divisao WHERE rgprp = '%s' AND rgimv = '%s';";
            dvSql = String.format(dvSql, corpo.rgprp, corpo.rgimv);
            ResultSet dv = conn.AbrirTabela(dvSql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (dv.next()) {
                    Object[][] nmProDiv = {};
                    try {nmProDiv = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = '" + dv.getString("rgprp_dv") + "'");} catch (SQLException ex) {}
                    try {
                        font.setColor(BaseColor.BLACK);
                        cell1 = new PdfPCell(new Phrase("Propriet(s): " + dv.getString("rgprp_dv"),font));
                        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell1.setBorder(Rectangle.NO_BORDER);
                        cell1.setBackgroundColor(BaseColor.WHITE);
                        table.addCell(cell1);
                        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr((String)nmProDiv[0][3]),font));
                        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell2.setBorder(Rectangle.NO_BORDER);
                        cell2.setBackgroundColor(BaseColor.WHITE);
                        table.addCell(cell2);
                    } catch (Exception e) {}
                }
            } catch (Exception e) {}
            try {dv.close();} catch (Exception e) {}
*/

        }
        table.completeRow();
        pdf.doc_add(table);

        font = new Font(bf, 8, Font.NORMAL);
        // Mensagem do Recibo
        String msgem = "";
        try {
            try { msgem = (String) conn.LerCamposTabela(new String[] {"l_msg"}, "locatarios", "l_contrato = '" + loca.getContrato() + "'")[0][3]; } catch (SQLException ex) {msgem = "";}
            if (!msgem.trim().equalsIgnoreCase("")) {
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("__________ MENSAGEM __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print(msgem, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
            }
        } catch (Exception e) {}

        boolean maniv = new AvisosMensagens().VerificaAniLocatario(corpo.contrato);
        if (maniv && VariaveisGlobais.am_aniv) {
           p = pdf.print("Este é o mês do seu aniversário. PARABÉNS!", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
           pdf.doc_add(p);
        }


/*
        Object[] mreaj = new AvisosMensagens().VerificaReajuste(corpo.contrato);
        if ((boolean)mreaj[1] && VariaveisGlobais.am_reaj) {
            p = pdf.print((String)mreaj[0], pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
        }
*/

        if (this.Autenticacao > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            if (this.Valores != null) {
                for (int i = 0; i < this.Valores.length; i++) {
                    String bLinha = "";
                    if (!"".equals(this.Valores[i][1].trim())) {
                        bLinha = "BCO:" + new Pad(this.Valores[i][1], 3).RPad() + " AG:" + new Pad(this.Valores[i][2], 4).RPad() + " CH:" + new Pad(this.Valores[i][3], 8).RPad() + " DT: " + new Pad(this.Valores[i][4], 10).CPad() + " VR:" + new Pad(this.Valores[i][5], 10).LPad();
                    } else {
                        bLinha = (this.Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : this.Valores[i][0].trim().toUpperCase()) + ":" + new Pad(this.Valores[i][5], 10).LPad();
                    }

                    p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            p = pdf.print("Este recibo não quita qualquer débito anterior.", pdf.HELVETICA, 6, pdf.BOLDITALIC, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            if (this.dataImpressao == null) {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", DbMain.getDateTimeServer()) +
                                new DecimalFormat("#,##0.00").format(corpo.corpoRecibos[corpo.corpoRecibos.length - 1].getVlr()) + " " + VariaveisGlobais.usuario,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            } else {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", this.dataImpressao) +
                                new DecimalFormat("#,##0.00").format(corpo.corpoRecibos[corpo.corpoRecibos.length - 1].getVlr()).replace(",","").replace(".","") +
                                " " + this.logado,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            }
            pdf.doc_add(p);

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();

        // Impressão
        if (!preview) {
            if (bAttach != null ) {
                if (bAttach.length > 0) if (!bAttach[0]) new toPrint(pdf.getPathName() + docName, "THERMICA", VariaveisGlobais.PrinterMode);
            }
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Recibo", pdf.getPathName() + docName);
            }
        }

        String rPathName = pdf.getPathName();
        
        pdf.setPathName("");
        pdf.setDocName("");

        return rPathName + docName;
    }

    public String ImprimeAvisoPDF(Collections adm, Object[] dados, boolean preview, boolean ... bAttach) {
        String retorno = null;
        System.out.println("ImprimeAvisoPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("A V I S O " + dados[0].toString().toUpperCase().trim() + " " +
                dados[1].toString().toUpperCase().trim(), pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Dados de identificação
        String tDados2 = ""; try {tDados2 = dados[2].toString();} catch (Exception e) {}
        String tDados3 = ""; try {tDados3 = dados[3].toString();} catch (Exception e) {}
        p = pdf.print("Código: " + tDados2 + " Nome: " + tDados3, pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = null;
        if (this.dataImpressao == null) {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario, font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
        } else {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + this.logado, font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
        }

        if (this.dataImpressao == null) {
            PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
        } else {
            PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
        }
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Mensagem de Aviso de Não Validade
        columnWidths = new float[] {100};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 8, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase(dados[5].toString().trim(),font));
        cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        table.completeRow();
        pdf.doc_add(table);

        if (this.Autenticacao > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            if (this.Valores != null) {
                for (int i = 0; i < this.Valores.length; i++) {
                    String bLinha = "";
                    if (!"".equals(this.Valores[i][1].trim())) {
                        bLinha = "BCO:" + new Pad(this.Valores[i][1], 3).RPad() + " AG:" + new Pad(this.Valores[i][2], 4).RPad() + " CH:" + new Pad(this.Valores[i][3], 8).RPad() + " DT: " + new Pad(this.Valores[i][4], 10).CPad() + " VR:" + new Pad(this.Valores[i][5], 10).LPad();
                    } else {
                        bLinha = (this.Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : this.Valores[i][0].trim().toUpperCase()) + ":" + new Pad(this.Valores[i][5], 10).LPad();
                    }

                    p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            if (this.dataImpressao == null) {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", DbMain.getDateTimeServer()) +
                                new DecimalFormat("#,##0.00").format(dados[4]).replace(",","").replace(".","")
                                + " " + VariaveisGlobais.usuario,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            } else {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", this.dataImpressao) +
                                new DecimalFormat("#,##0.00").format(dados[4]).replace(",","").replace(".","")
                                + " " + this.logado,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            }
            pdf.doc_add(p);

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();

        // Impressão
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA",VariaveisGlobais.PrinterMode);
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Recibo do Aviso", pdf.getPathName() + docName);
            }
        }

        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        retorno = rPathName + docName;
        return retorno;
    }

    public String ImprimeRetencaoPDF(Collections adm, List<TableRetencao> dados, BigDecimal valor, boolean preview, boolean ... bAttach) {
        String retorno = null;
        System.out.println("ImprimeAvisoPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {65, 35 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);


        columnWidths = new float[] {100};
        PdfPTable tabletitulo = new PdfPTable(columnWidths);
        tabletitulo.setHeaderRows(0);
        tabletitulo.setWidthPercentage(100);
        font = new Font(bf, 9, Font.BOLD + Font.ITALIC);
        font.setColor(BaseColor.BLACK);
        PdfPCell cellt = new PdfPCell(new Phrase("A V I S O - DÉBITO RETENÇÃO",font));
        cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellt.setBorder(Rectangle.NO_BORDER);
        cellt.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabletitulo.addCell(cellt);
        tabletitulo.completeRow();
        pdf.doc_add(tabletitulo);

/*
        p = pdf.print("A V I S O - DÉBITO RETENÇÃO", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
*/
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {47, 73 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = null;
        if (this.dataImpressao == null) {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario, font));
        } else {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + this.logado, font));
        }
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        if (this.dataImpressao == null) {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
        } else {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
        }
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
/*
        p.add(new Chunk(l));
        pdf.doc_add(p);
*/

        // Cabeçario do recibo
        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {35, 35, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("RGIMV/RECBTO",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        // Dados do Recibo
        columnWidths = new float[] {35, 35, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        for( final TableRetencao os : dados) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(os.getTaxa(),font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase(os.getRgimv() + " - " + os.getDtrecebto(),font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);
            cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(os.getValor()), font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);
        }
        table.completeRow();
        pdf.doc_add(table);

        font = new Font(bf, 9, Font.NORMAL);

        if (this.Autenticacao > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            if (this.Valores != null) {
                for (int i = 0; i < this.Valores.length; i++) {
                    String bLinha = "";
                    if (!"".equals(this.Valores[i][1].trim())) {
                        bLinha = "BCO:" + new Pad(this.Valores[i][1], 3).RPad() + " AG:" + new Pad(this.Valores[i][2], 4).RPad() + " CH:" + new Pad(this.Valores[i][3], 8).RPad() + " DT: " + new Pad(this.Valores[i][4], 10).CPad() + " VR:" + new Pad(this.Valores[i][5], 10).LPad();
                    } else {
                        bLinha = (this.Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : this.Valores[i][0].trim().toUpperCase()) + ":" + new Pad(this.Valores[i][5], 10).LPad();
                    }

                    p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            if (this.dataImpressao == null) {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", DbMain.getDateTimeServer()) +
                                new DecimalFormat("#,##0.00").format(valor) + " " + VariaveisGlobais.usuario,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            } else {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", this.dataImpressao) +
                                new DecimalFormat("#,##0.00").format(valor).replace(",","").replace(".","") + " " + this.logado,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            }
            pdf.doc_add(p);

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA", VariaveisGlobais.PrinterMode);
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Recibo de Retenção", pdf.getPathName() + docName);
            }
        }

        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        retorno = rPathName + docName;
        return retorno;
    }

    public void ImprimeAntecipacaoPDF(Collections adm, List<TableRetencao> dados, BigDecimal valor, boolean preview) {
        System.out.println("ImprimeAntecipacaoPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {65, 35 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);


        columnWidths = new float[] {100};
        PdfPTable tabletitulo = new PdfPTable(columnWidths);
        tabletitulo.setHeaderRows(0);
        tabletitulo.setWidthPercentage(100);
        font = new Font(bf, 9, Font.BOLD + Font.ITALIC);
        font.setColor(BaseColor.BLACK);
        PdfPCell cellt = new PdfPCell(new Phrase("A V I S O - DÉBITO ANTECIPAÇÃO",font));
        cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellt.setBorder(Rectangle.NO_BORDER);
        cellt.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabletitulo.addCell(cellt);
        tabletitulo.completeRow();
        pdf.doc_add(tabletitulo);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {47, 73 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
/*
        p.add(new Chunk(l));
        pdf.doc_add(p);
*/

        // Cabeçario do recibo
        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {35, 35, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("RGIMV/RECBTO",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        // Dados do Recibo
        columnWidths = new float[] {35, 35, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        for( final TableRetencao os : dados) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(os.getTaxa(),font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase(os.getRgimv() + " - " + os.getDtrecebto(),font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);
            cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(os.getValor()), font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);
        }
        table.completeRow();
        pdf.doc_add(table);

        font = new Font(bf, 9, Font.NORMAL);

        if (this.Autenticacao > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            if (this.Valores != null) {
                for (int i = 0; i < this.Valores.length; i++) {
                    String bLinha = "";
                    if (!"".equals(this.Valores[i][1].trim())) {
                        bLinha = "BCO:" + new Pad(this.Valores[i][1], 3).RPad() + " AG:" + new Pad(this.Valores[i][2], 4).RPad() + " CH:" + new Pad(this.Valores[i][3], 8).RPad() + " DT: " + new Pad(this.Valores[i][4], 10).CPad() + " VR:" + new Pad(this.Valores[i][5], 10).LPad();
                    } else {
                        bLinha = (this.Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : this.Valores[i][0].trim().toUpperCase()) + ":" + new Pad(this.Valores[i][5], 10).LPad();
                    }

                    p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao), 7) +
                            Dates.DateFormata("ddMMyyyyHHmmss", DbMain.getDateTimeServer()) +
                            new DecimalFormat("#,##0.00").format(valor) + " " + VariaveisGlobais.usuario,
                    pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA", VariaveisGlobais.PrinterMode);
        } else {
            new PdfViewer("Preview do Recibo de Antecipação", pdf.getPathName() + docName);
        }
        pdf.setPathName("");
        pdf.setDocName("");
    }

    public String ImprimeDespesaPDF(Collections adm, Object[] dados, boolean preview, boolean ... bAttach) {
        String retorno = null;
        System.out.println("ImprimeDespesaPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("D E S P E S A  -  D É B I T O", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Dados de identificação
        p = pdf.print("ID: " + dados[2].toString() + " Grupo: " + dados[3].toString(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = null;
        if (this.dataImpressao == null) {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario, font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
        } else {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + this.logado, font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
        }

        if (this.dataImpressao == null) {
            PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
        } else {
            PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
        }
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Mensagem de Aviso de Não Validade
        columnWidths = new float[] {100};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 8, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase(dados[5].toString().trim(),font));
        cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        table.completeRow();
        pdf.doc_add(table);

        if (this.Autenticacao > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            if (this.Valores != null) {
                for (int i = 0; i < this.Valores.length; i++) {
                    String bLinha = "";
                    if (!"".equals(this.Valores[i][1].trim())) {
                        bLinha = "BCO:" + new Pad(this.Valores[i][1], 3).RPad() + " AG:" + new Pad(this.Valores[i][2], 4).RPad() + " CH:" + new Pad(this.Valores[i][3], 8).RPad() + " DT: " + new Pad(this.Valores[i][4], 10).CPad() + " VR:" + new Pad(this.Valores[i][5], 10).LPad();
                    } else {
                        bLinha = (this.Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : this.Valores[i][0].trim().toUpperCase()) + ":" + new Pad(this.Valores[i][5], 10).LPad();
                    }

                    p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            if (this.dataImpressao == null) {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", DbMain.getDateTimeServer()) +
                                new DecimalFormat("#,##0.00").format(dados[4]).replace(",","").replace(".","")
                                + " " + VariaveisGlobais.usuario,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            } else {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", this.dataImpressao) +
                                new DecimalFormat("#,##0.00").format(dados[4]).replace(",","").replace(".","")
                                + " " + this.logado,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            }
            pdf.doc_add(p);

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();

        // Impressão
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA",VariaveisGlobais.PrinterMode);
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Recibo de Despesa", pdf.getPathName() + docName);
            }
        }
        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        retorno = rPathName + docName;

        return retorno;
    }

    public String ImprimeDepositoPDF(Collections adm, List<cDeposito> dados, BigDecimal valor, BigDecimal dnvalor, boolean preview, boolean ... bAttach) {
        String retorno = null;
        System.out.println("ImprimeDepositoPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {65, 35 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);


        columnWidths = new float[] {100};
        PdfPTable tabletitulo = new PdfPTable(columnWidths);
        tabletitulo.setHeaderRows(0);
        tabletitulo.setWidthPercentage(100);
        font = new Font(bf, 9, Font.BOLD + Font.ITALIC);
        font.setColor(BaseColor.BLACK);
        PdfPCell cellt = new PdfPCell(new Phrase("D E P O S I T O S - DÉBITO",font));
        cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellt.setBorder(Rectangle.NO_BORDER);
        cellt.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabletitulo.addCell(cellt);
        tabletitulo.completeRow();
        pdf.doc_add(tabletitulo);

        //p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        //pdf.doc_add(p);

        columnWidths = new float[] {47, 73 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = null;
        if (this.dataImpressao == null) {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario, font));
        } else {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + this.logado, font));
        }
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        if (this.dataImpressao == null) {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
        } else {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
        }
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);

        // Cabeçario
        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {10, 15, 30, 25, 20};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("BCO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("AGCIA",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("CHEQUE", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Phrase("PRÉ", font));
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Phrase("VALOR", font));
        cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell5.setBorder(Rectangle.NO_BORDER);
        cell5.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell5);
        table.completeRow();
        pdf.doc_add(table);

        // Dados do Recibo
        columnWidths = new float[] {10, 15, 30, 25, 20};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        BigDecimal tcomum = new BigDecimal("0"); int ntcomum = 0;
        BigDecimal tpre = new BigDecimal("0"); int ntpre = 0;
        for( final cDeposito os : dados) {
            if (os.getDtpre() == null) {
                tcomum = tcomum.add(os.getValor());
                ntcomum += 1;
            } else {
                tpre = tpre.add(os.getValor());
                ntpre += 1;
            }
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(os.getBanco(),font));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase(os.getAgencia(),font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);

            cell3 = new PdfPCell(new Phrase(os.getNumero(),font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);

            cell4 = new PdfPCell(new Phrase(Dates.DateFormata("dd-MM-yyyy",os.getDtpre()),font));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell4);

            cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(os.getValor()), font));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell5);
        }
        table.completeRow();
        pdf.doc_add(table);

        // totalizadores
        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {70,30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Cheques Comum (" + FuncoesGlobais.StrZero(String.valueOf(ntcomum),3) + ")",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(LerValor.BigDecimalToCurrency(tcomum),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("Cheques Pré (" + FuncoesGlobais.StrZero(String.valueOf(ntpre),3) + ")",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(LerValor.BigDecimalToCurrency(tpre),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("Dinheiro",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(LerValor.BigDecimalToCurrency(dnvalor),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("=============",font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("Total a Depositar",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(LerValor.BigDecimalToCurrency(valor),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);
        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new Font(bf, 9, Font.NORMAL);
        if (this.Autenticacao > 0) {
            p = pdf.print("__________ BANCO A DEPOSITAR  __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            if (this.Valores != null) {
                for (int i = 0; i < this.Valores.length; i++) {
                    String bLinha = "";
                    if (!"".equals(this.Valores[i][1].trim())) {
                        bLinha = "BCO:" + new Pad(this.Valores[i][1], 3).RPad() + " AG:" + new Pad(this.Valores[i][2], 4).RPad() + " CT:" + new Pad(this.Valores[i][3], 8).RPad() + " DT: " + new Pad(this.Valores[i][4], 10).CPad() + " VR:" + new Pad(this.Valores[i][5], 10).LPad();
                    } else {
                        bLinha = (this.Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : this.Valores[i][0].trim().toUpperCase()) + ":" + new Pad(this.Valores[i][5], 10).LPad();
                    }

                    p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            if (this.dataImpressao == null) {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", DbMain.getDateTimeServer()) +
                                new DecimalFormat("#,##0.00").format(valor).replace(",","").replace(".","") + " " + VariaveisGlobais.usuario,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            } else {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", this.dataImpressao) +
                                new DecimalFormat("#,##0.00").format(valor).replace(",","").replace(".","") + " " + this.logado,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            }
            pdf.doc_add(p);

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA", VariaveisGlobais.PrinterMode);
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Depositos", pdf.getPathName() + docName);
            }
        }
        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        retorno = rPathName + docName;

        return retorno;
    }

    public String ImprimeCaixaPDF(Collections adm, BigDecimal[] apucx, List<cCaixa> dados, boolean preview, boolean ... bAttach) {
        String retorno = null;
        System.out.println("ImprimeCaixaPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {65, 35 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);


        columnWidths = new float[] {100};
        PdfPTable tabletitulo = new PdfPTable(columnWidths);
        tabletitulo.setHeaderRows(0);
        tabletitulo.setWidthPercentage(100);
        font = new Font(bf, 9, Font.BOLD + Font.ITALIC);
        font.setColor(BaseColor.BLACK);
        PdfPCell cellt = new PdfPCell(new Phrase("F E C H A M E N T O  D E  C A I X A",font));
        cellt.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellt.setBorder(Rectangle.NO_BORDER);
        cellt.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabletitulo.addCell(cellt);
        tabletitulo.completeRow();
        pdf.doc_add(tabletitulo);

        //p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        //pdf.doc_add(p);

        columnWidths = new float[] {47, 73 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell0 = null;PdfPCell cell1 = null;
        if (this.dataImpressao == null) {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario, font));
        } else {
            cell1 = new PdfPCell(new Phrase("CAIXA: " + this.logado, font));
        }
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        if (this.dataImpressao == null) {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
        } else {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
        }
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);

        // Dados do Recibo
        columnWidths = new float[] {20, 15, 15, 20, 20, 10};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        BigDecimal tcrdn = new BigDecimal("0"); BigDecimal tdbdn = new BigDecimal("0");
        BigDecimal tcrch = new BigDecimal("0"); BigDecimal tdbch = new BigDecimal("0");
        BigDecimal tcrbc = new BigDecimal("0"); BigDecimal tdbbc = new BigDecimal("0");
        BigDecimal tcrbo = new BigDecimal("0"); BigDecimal tdbbo = new BigDecimal("0");
        BigDecimal tcrct = new BigDecimal("0"); BigDecimal tdbct = new BigDecimal("0");

        PdfPCell cell3 = null;PdfPCell cell4 = null; PdfPCell cell5 = null; PdfPCell cell6 = null;
        String oldDoc = ""; String oldTipo = ""; Object[][] Totais = {};
        for( final cCaixa os : dados) {
            // Para encapsular
            // Contabilização dos Totais
            if (os.getTipo().equalsIgnoreCase("DN")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrdn = tcrdn.add(os.getValor());
                } else {
                    tdbdn = tdbdn.add(os.getValor());
                }
            } else if (os.getTipo().equalsIgnoreCase("CH")) {
                for (cBanco obc : os.getDatabanco()) {
                    if (os.getOperacao().equalsIgnoreCase("CRE")) {
                        tcrch = tcrch.add(obc.getValor());
                    } else {
                        tdbch = tdbch.add(obc.getValor());
                    }
                }
            } else if (os.getTipo().equalsIgnoreCase("BC")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrbc = tcrbc.add(os.getValor());
                } else {
                    tdbbc = tdbbc.add(os.getValor());
                }
            } else if (os.getTipo().equalsIgnoreCase("BO")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrbo = tcrbo.add(os.getValor());
                } else {
                    tdbbo = tdbbo.add(os.getValor());
                }
            } else if (os.getTipo().equalsIgnoreCase("CT")) {
                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    tcrct = tcrct.add(os.getValor());
                } else {
                    tdbct = tdbct.add(os.getValor());
                }
            }

            if (oldDoc != os.getDoc()) {
                int pos = FuncoesGlobais.FindinObject(Totais,0,os.getDoc());
                if (pos < 0) {
                    Totais = FuncoesGlobais.ObjectsAdd(Totais, new Object[]{os.getDoc(), new Object[]{new Object[]{"CRE", new Object[]{tcrdn, tcrch, tcrbc, tcrbo, tcrct}}}, new Object[]{new Object[]{"DEB", new Object[]{tdbdn, tdbch, tdbbc, tdbbo, tdbct}}}});
                } else {
                    // CRE
                    Object[] tTCred = (Object[]) Totais[pos][1];
                    Object[] tmpTtCrDb = (Object[]) tTCred[0];
                    Object[] tmpCRE = (Object[])tmpTtCrDb[1];

                    tmpCRE[0] = ((BigDecimal)tmpCRE[0]).add(tcrdn);
                    tmpCRE[1] = ((BigDecimal)tmpCRE[1]).add(tcrch);
                    tmpCRE[2] = ((BigDecimal)tmpCRE[2]).add(tcrbc);
                    tmpCRE[3] = ((BigDecimal)tmpCRE[3]).add(tcrbo);
                    tmpCRE[4] = ((BigDecimal)tmpCRE[4]).add(tcrct);
                    // DEB
                    Object[] tTDeb = (Object[]) Totais[pos][2];
                    Object[] tmpTtDeb = (Object[]) tTDeb[0];
                    Object[] tmpDeb = (Object[])tmpTtDeb[1];
                    tmpDeb[0] = ((BigDecimal)tmpDeb[0]).add(tdbdn);
                    tmpDeb[1] = ((BigDecimal)tmpDeb[1]).add(tdbch);
                    tmpDeb[2] = ((BigDecimal)tmpDeb[2]).add(tdbbc);
                    tmpDeb[3] = ((BigDecimal)tmpDeb[3]).add(tdbbo);
                    tmpDeb[4] = ((BigDecimal)tmpDeb[4]).add(tdbct);
                }
            }

            tcrdn = new BigDecimal("0"); tdbdn = new BigDecimal("0");
            tcrch = new BigDecimal("0"); tdbch = new BigDecimal("0");
            tcrbc = new BigDecimal("0"); tdbbc = new BigDecimal("0");
            tcrbo = new BigDecimal("0"); tdbbo = new BigDecimal("0");
            tcrct = new BigDecimal("0"); tdbct = new BigDecimal("0");

            if (!oldDoc.equalsIgnoreCase(os.getDoc()) || !oldTipo.equalsIgnoreCase(os.getOperacao())) {
                font.setColor(BaseColor.WHITE);
                cell0 = new PdfPCell(new Phrase(VariaveisGlobais.contas_ca.get(os.getDoc()), font));
                cell0.setColspan(2);
                cell0.setBorder(Rectangle.NO_BORDER);
                cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell0);
                oldDoc = os.getDoc();

                if (os.getOperacao().equalsIgnoreCase("CRE")) {
                    font.setColor(BaseColor.WHITE);
                    cell1 = new PdfPCell(new Phrase("CRÉDITOS", font));
                    cell1.setColspan(4);
                    cell1.setBorder(Rectangle.NO_BORDER);
                    cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(cell1);
                } else {
                    font.setColor(BaseColor.WHITE);
                    cell1 = new PdfPCell(new Phrase("DÉBITOS", font));
                    cell1.setColspan(4);
                    cell1.setBorder(Rectangle.NO_BORDER);
                    cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(cell1);
                }
                oldTipo = os.getOperacao();
            }

            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(String.valueOf(os.getAut()),font));
            cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);

            cell2 = new PdfPCell(new Phrase(Dates.DateFormata("HH:mm",os.getHora()),font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);

            cell3 = new PdfPCell(new Phrase(os.getRegistro(),font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);

            cell4 = new PdfPCell(new Phrase(os.getDoc().endsWith("X") ? "EXTORNO" : "",font));
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell4);

            cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(os.getValor()), font));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell5);

            cell6 = new PdfPCell(new Phrase(os.getTipo(), font));
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            cell6.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell6);

            for (cBanco ob : os.getDatabanco()) {
                font.setColor(BaseColor.BLACK);
                cell1 = new PdfPCell(new Phrase("",font));
                cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);

                cell2 = new PdfPCell(new Phrase(ob.getBanco(),font));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell2);

                cell3 = new PdfPCell(new Phrase(ob.getAgencia(),font));
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell3.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell3);

                cell4 = new PdfPCell(new Phrase(ob.getNcheque(),font));
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell4.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell4);

                cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(ob.getValor()), font));
                cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell5.setBorder(Rectangle.NO_BORDER);
                cell5.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell5);

                cell6 = new PdfPCell(new Phrase("", font));
                cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell6.setBorder(Rectangle.NO_BORDER);
                cell6.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell6);
            }
        }
        table.completeRow();
        pdf.doc_add(table);

        if (oldDoc != "") {
            int pos = FuncoesGlobais.FindinObject(Totais,0,oldDoc);
            if (pos < 0) {
                Totais = FuncoesGlobais.ObjectsAdd(Totais, new Object[]{oldDoc, new Object[]{new Object[]{"CRE", new Object[]{tcrdn, tcrch, tcrbc, tcrbo, tcrct}}}, new Object[]{new Object[]{"DEB", new Object[]{tdbdn, tdbch, tdbbc, tdbbo, tdbct}}}});
            } else {
                // CRE
                Object[] tTCred = (Object[]) Totais[pos][1];
                Object[] tmpTtCrDb = (Object[]) tTCred[0];
                Object[] tmpCRE = (Object[])tmpTtCrDb[1];

                tmpCRE[0] = ((BigDecimal)tmpCRE[0]).add(tcrdn);
                tmpCRE[1] = ((BigDecimal)tmpCRE[1]).add(tcrch);
                tmpCRE[2] = ((BigDecimal)tmpCRE[2]).add(tcrbc);
                tmpCRE[3] = ((BigDecimal)tmpCRE[3]).add(tcrbo);
                tmpCRE[4] = ((BigDecimal)tmpCRE[4]).add(tcrct);
                // DEB
                Object[] tTDeb = (Object[]) Totais[pos][2];
                Object[] tmpTtDeb = (Object[]) tTDeb[0];
                Object[] tmpDeb = (Object[])tmpTtDeb[1];
                tmpDeb[0] = ((BigDecimal)tmpDeb[0]).add(tdbdn);
                tmpDeb[1] = ((BigDecimal)tmpDeb[1]).add(tdbch);
                tmpDeb[2] = ((BigDecimal)tmpDeb[2]).add(tdbbc);
                tmpDeb[3] = ((BigDecimal)tmpDeb[3]).add(tdbbo);
                tmpDeb[4] = ((BigDecimal)tmpDeb[4]).add(tdbct);
            }

            tcrdn = new BigDecimal("0"); tdbdn = new BigDecimal("0");
            tcrch = new BigDecimal("0"); tdbch = new BigDecimal("0");
            tcrbc = new BigDecimal("0"); tdbbc = new BigDecimal("0");
            tcrbo = new BigDecimal("0"); tdbbo = new BigDecimal("0");
            tcrct = new BigDecimal("0"); tdbct = new BigDecimal("0");
        }

        // totalizadores
        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("=== T O T A L I Z A D O R E S ===", pdf.HELVETICA, 7, pdf.BOLD, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {15,17,17,17,17,17};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);

        cell1 = new PdfPCell(new Phrase("DSC",font));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setBorder(Rectangle.BOX);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase("DN",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.BOX);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell3 = new PdfPCell(new Phrase("CH",font));
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setBorder(Rectangle.BOX);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        cell4 = new PdfPCell(new Phrase("BC",font));
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setBorder(Rectangle.BOX);
        cell4.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell4);

        cell5 = new PdfPCell(new Phrase("BO",font));
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setBorder(Rectangle.BOX);
        cell5.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell5);

        cell6 = new PdfPCell(new Phrase("CT",font));
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell6.setBorder(Rectangle.BOX);
        cell6.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell6);

        BigDecimal tdin = new BigDecimal("0"); BigDecimal tche = new BigDecimal("0");
        BigDecimal tban = new BigDecimal("0"); BigDecimal tbol = new BigDecimal("0");
        BigDecimal tcar = new BigDecimal("0");

        BigDecimal tEtd = new BigDecimal("0"); BigDecimal tEta = new BigDecimal("0");
        for (Object[] tt : Totais) {
            Object[] tTCred = (Object[]) tt[1];
            Object[] tmpTtCrDb = (Object[]) tTCred[0];
            Object[] tmpCRE = (Object[])tmpTtCrDb[1];
            // DEB
            Object[] tTDeb = (Object[]) tt[2];
            Object[] tmpTtDeb = (Object[]) tTDeb[0];
            Object[] tmpDeb = (Object[])tmpTtDeb[1];

            tdin = tdin.add(((BigDecimal)tmpCRE[0]).subtract((BigDecimal)tmpDeb[0]));
            tche = tche.add(((BigDecimal)tmpCRE[1]).subtract((BigDecimal)tmpDeb[1]));
            tban = tban.add(((BigDecimal)tmpCRE[2]).subtract((BigDecimal)tmpDeb[2]));
            tbol = tbol.add(((BigDecimal)tmpCRE[3]).subtract((BigDecimal)tmpDeb[3]));
            tcar = tcar.add(((BigDecimal)tmpCRE[4]).subtract((BigDecimal)tmpDeb[4]));

            tEtd = tEtd.add((BigDecimal)tmpCRE[0]);
            tEtd = tEtd.add((BigDecimal)tmpCRE[1]);

            tEta = tEta.add((BigDecimal)tmpDeb[0]);
            tEta = tEta.add((BigDecimal)tmpDeb[1]);

            if (!(((BigDecimal)tmpCRE[0]).floatValue() == 0 &&
                ((BigDecimal)tmpCRE[1]).floatValue() == 0 &&
                ((BigDecimal)tmpCRE[2]).floatValue() == 0 &&
                ((BigDecimal)tmpCRE[3]).floatValue() == 0 &&
                ((BigDecimal)tmpCRE[4]).floatValue() == 0) ) {
                cell1 = new PdfPCell(new Phrase(tt[0].toString() + "+", font));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);

                cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpCRE[0]), font));
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell2);

                cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpCRE[1]), font));
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell3.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell3);

                cell4 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpCRE[2]), font));
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell4.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell4);

                cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpCRE[3]), font));
                cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell5.setBorder(Rectangle.NO_BORDER);
                cell5.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell5);

                cell6 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpCRE[4]), font));
                cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell6.setBorder(Rectangle.NO_BORDER);
                cell6.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell6);
            }

            // DEBITOS
            if (!(((BigDecimal)tmpDeb[0]).floatValue() == 0 &&
               ((BigDecimal)tmpDeb[1]).floatValue() == 0 &&
               ((BigDecimal)tmpDeb[2]).floatValue() == 0 &&
               ((BigDecimal)tmpDeb[3]).floatValue() == 0 &&
               ((BigDecimal)tmpDeb[4]).floatValue() == 0) ) {
                cell1 = new PdfPCell(new Phrase(tt[0].toString() + "-", font));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setBorder(Rectangle.NO_BORDER);
                cell1.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell1);

                cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpDeb[0]), font));
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell2);

                cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpDeb[1]), font));
                cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell3.setBorder(Rectangle.NO_BORDER);
                cell3.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell3);

                cell4 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpDeb[2]), font));
                cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell4.setBorder(Rectangle.NO_BORDER);
                cell4.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell4);

                cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpDeb[3]), font));
                cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell5.setBorder(Rectangle.NO_BORDER);
                cell5.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell5);

                cell6 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format((BigDecimal) tmpDeb[4]), font));
                cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell6.setBorder(Rectangle.NO_BORDER);
                cell6.setBackgroundColor(BaseColor.WHITE);
                table.addCell(cell6);
            }
        }

        // Apuração de Caixa
        cell1 = new PdfPCell(new Phrase("ACX-", font));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(apucx[0]), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(apucx[1]), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        cell4 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(0), font));
        cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell4);

        cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(0), font));
        cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell5.setBorder(Rectangle.NO_BORDER);
        cell5.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell5);

        cell6 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(0), font));
        cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell6.setBorder(Rectangle.NO_BORDER);
        cell6.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell6);

        table.completeRow();
        pdf.doc_add(table);

        tdin = tdin.subtract(apucx[0]);
        tche = tche.subtract(apucx[1]);

        tEta = tEta.add(apucx[0]);
        tEta = tEta.add(apucx[1]);

        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("=== R E S U L T A D O   F I N A L ===", pdf.HELVETICA, 7, pdf.BOLD, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {70,30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);

        cell1 = new PdfPCell(new Phrase("TOTAL EM DINHEIRO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tdin), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("TOTAL EM CHEQUE",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tche), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        table.completeRow();
        pdf.doc_add(table);

        l = new LineSeparator();
        l.setPercentage(100f);
        p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {70,30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);

        cell1 = new PdfPCell(new Phrase("TOTAL EM BANCO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tban), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("TOTAL EM BOLETA",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tbol), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("TOTAL EM CARTÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tcar), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        font = new Font(bf, 7, Font.NORMAL);
        columnWidths = new float[] {70,30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);

        cell1 = new PdfPCell(new Phrase("TOTAL DE ENTRADAS",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tEtd), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Phrase("TOTAL DE SAIDAS",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tEta), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        cell2 = new PdfPCell(new Phrase("----------------", font));
        cell2.setColspan(2);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        String fecCaixa = "";
        if (tEtd.subtract(tEta).floatValue() == 0) {
            fecCaixa = "( C A I X A  O K )";
        } else if (tEtd.subtract(tEta).floatValue() > 0) {
            fecCaixa = "( F A L T A  D E  C A I X A )";
        } else {
            fecCaixa = "( S O B R A  D E  C A I X A )";
        }
        font = new Font(bf, 7, Font.BOLD);
        cell1 = new PdfPCell(new Phrase("SALDO " + fecCaixa,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);

        font = new Font(bf, 7, Font.NORMAL);
        cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(tEtd.subtract(tEta)), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        table.completeRow();
        pdf.doc_add(table);

        font = new Font(bf, 9, Font.NORMAL);
        if (this.Autenticacao > 0) {
            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

/*
            // Imprimir Autenticação
            if (this.dataImpressao == null) {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", new Date()) +
                                new DecimalFormat("#,##0.00").format(valor).replace(",","").replace(".","") + " " + VariaveisGlobais.usuario,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            } else {
                p = pdf.print(gVar.get("marca").trim() + FuncoesGlobais.StrZero(String.valueOf((int) this.Autenticacao), 7) +
                                Dates.DateFormata("ddMMyyyyHHmmss", this.dataImpressao) +
                                new DecimalFormat("#,##0.00").format(valor).replace(",","").replace(".","") + " " + this.logado,
                        pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            }
            pdf.doc_add(p);
*/

            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)this.Autenticacao),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA", VariaveisGlobais.PrinterMode);
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Fechamento de Caixa", pdf.getPathName() + docName);
            }
        }
        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        retorno = rPathName + docName;

        return retorno;
    }

    public void ImprimeSaidaPDF(Collections adm, Object[] dados, boolean preview) {
        System.out.println("ImprimeSaidaPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Font font = new Font(bf, 9, Font.NORMAL);

        pdf.open();

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30 };
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("S A I D A  D E  C H A V E", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Dados de identificação
        p = pdf.print("Código: " + dados[0].toString() + " End.: " + dados[1].toString(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = null;
        cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario, font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Mensagem de Aviso de Não Validade
        columnWidths = new float[] {100};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 8, Font.NORMAL);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase(dados[2].toString().trim(),font));
        cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        table.completeRow();
        pdf.doc_add(table);

        if (this.Autenticacao > 0) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);
        }

        p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("__________________________________________________", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Assinatura", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) {
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        pdf.close();

        // Impressão
        if (!preview) {
            new toPrint(pdf.getPathName() + docName, "THERMICA",VariaveisGlobais.PrinterMode);
        } else {
            new PdfViewer("Preview do Recibo do Aviso", pdf.getPathName() + docName);
        }
        pdf.setPathName("");
        pdf.setDocName("");
    }

    public String ImprimeRazaoPDF(Collections adm,Object[][] dadosImpr , boolean preview, boolean... bAttach) {
        System.out.println("ImprimeRazaoPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        com.itextpdf.text.pdf.BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font =  new com.itextpdf.text.Font(bf, 9, com.itextpdf.text.Font.NORMAL);

        pdf.open(595f,842f,  20f, 20f, 10f, 10f);

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30};
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("R A Z A O", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = new PdfPCell(new Phrase("LOGADO: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        if (this.dataImpressao == null) {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
            table.completeRow();
            pdf.doc_add(table);
        } else {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
            table.completeRow();
            pdf.doc_add(table);
        }

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {50, 10, 10, 10, 10, 10};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase("CRÉDITO",font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("DÉBITO", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Phrase("SALDO", font));
        cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Phrase("ANTERIOR", font));
        cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell5.setBorder(Rectangle.NO_BORDER);
        cell5.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell5);

        PdfPCell cell6 = new PdfPCell(new Phrase("ATUAL", font));
        cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell6.setBorder(Rectangle.NO_BORDER);
        cell6.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell6);

        table.completeRow();
        pdf.doc_add(table);

        // Dados do Razao
        columnWidths = new float[] {50, 10, 10, 10, 10, 10};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        for (Object[] dados : dadosImpr) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(dados[0].toString(), font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);

            String celula2 = "";
            if (dados[1].getClass().getSimpleName().equalsIgnoreCase("String")) {
                celula2 = dados[1].toString();
            } else {
                celula2 = new DecimalFormat("#,##0.00").format(dados[1]);
            }
            cell2 = new PdfPCell(new Phrase(celula2, font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);

            String celula3 = "";
            if (dados[2].getClass().getSimpleName().equalsIgnoreCase("String")) {
                celula3 = dados[2].toString();
            } else {
                celula3 = new DecimalFormat("#,##0.00").format(dados[2]);
            }
            cell3 = new PdfPCell(new Phrase(celula3, font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);

            String celula4 = "";
            if (dados[3].getClass().getSimpleName().equalsIgnoreCase("String")) {
                celula4 = dados[3].toString();
            } else {
                celula4 = new DecimalFormat("#,##0.00").format(dados[3]);
            }
            cell4 = new PdfPCell(new Phrase(celula4, font));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell4);

            String celula5 = "";
            if (dados[4].getClass().getSimpleName().equalsIgnoreCase("String")) {
                celula5 = dados[4].toString();
            } else {
                celula5 = new DecimalFormat("#,##0.00").format(dados[4]);
            }
            cell5 = new PdfPCell(new Phrase(celula5, font));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell5);

            String celula6 = "";
            if (dados[5].getClass().getSimpleName().equalsIgnoreCase("String")) {
                celula6 = dados[5].toString();
            } else {
                celula6 = new DecimalFormat("#,##0.00").format(dados[5]);
            }
            cell6 = new PdfPCell(new Phrase(celula6, font));
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            cell6.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell6);
        }

        table.completeRow();
        pdf.doc_add(table);

/*
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(corpo.corpoRecibos[corpo.corpoRecibos.length - 1].getVlr()), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);
*/

        pdf.close();

        // Impressão
        if (!preview) {
            if (bAttach != null ) {
                if (bAttach.length > 0) if (!bAttach[0]) new toPrint(pdf.getPathName() + docName, "LASER", VariaveisGlobais.PrinterMode);
            }
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Razao", pdf.getPathName() + docName);
            }
        }

        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        return rPathName + docName;
    }

    public String ImprimeSaldosPDF(Collections adm,Object[][] dadosImpr , boolean preview, boolean... bAttach) {
        System.out.println("ImprimeRazaoPDF");

        float[] columnWidths = {};
        Collections gVar = adm;
        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        com.itextpdf.text.pdf.BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font =  new com.itextpdf.text.Font(bf, 9, com.itextpdf.text.Font.NORMAL);

        pdf.open(595f,842f,  20f, 20f, 10f, 10f);

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(gVar.get("logo").trim());
                if (VariaveisGlobais.logo_allign.equalsIgnoreCase("ESQUERDA")) {
                    img.setAlignment(Element.ALIGN_LEFT);
                } else if (VariaveisGlobais.logo_allign.equalsIgnoreCase("CENTRO")) {
                    img.setAlignment(Element.ALIGN_CENTER);
                } else {
                    img.setAlignment(Element.ALIGN_RIGHT);
                }
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30};
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = gVar.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("C O N T R O L E", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new Font(bf, 9, Font.NORMAL);
        font.setColor(BaseColor.BLACK);

        PdfPCell cell1 = new PdfPCell(new Phrase("LOGADO: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = null;
        if (this.dataImpressao == null) {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", DbMain.getDateTimeServer()), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
            table.completeRow();
            pdf.doc_add(table);
        } else {
            cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", this.dataImpressao), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell2);
            table.completeRow();
            pdf.doc_add(table);
        }

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {50, 10, 10, 10, 10, 10};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);

        cell2 = new PdfPCell(new Phrase("CRÉDITO",font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("DÉBITO", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Phrase("SALDO", font));
        cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell4.setBorder(Rectangle.NO_BORDER);
        cell4.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Phrase("ANTERIOR", font));
        cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell5.setBorder(Rectangle.NO_BORDER);
        cell5.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell5);

        PdfPCell cell6 = new PdfPCell(new Phrase("ATUAL", font));
        cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell6.setBorder(Rectangle.NO_BORDER);
        cell6.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell6);

        table.completeRow();
        pdf.doc_add(table);

        // Dados do Razao
        columnWidths = new float[] {50, 10, 10, 10, 10, 10};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        for (Object[] dados : dadosImpr) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase(dados[0].toString(), font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);

            cell2 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(dados[1]), font));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);

            cell3 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(dados[2]), font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);

            cell4 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(dados[3]), font));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell4);

            cell5 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(dados[4]), font));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell5);

            cell6 = new PdfPCell(new Phrase(new DecimalFormat("#,##0.00").format(dados[5]), font));
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setBorder(Rectangle.NO_BORDER);
            cell6.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell6);

        }

        table.completeRow();
        pdf.doc_add(table);

        pdf.close();

        // Impressão
        if (!preview) {
            if (bAttach != null ) {
                if (bAttach.length > 0) if (!bAttach[0]) new toPrint(pdf.getPathName() + docName, "LASER", VariaveisGlobais.PrinterMode);
            }
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Saldos", pdf.getPathName() + docName);
            }
        }

        String rPathName = pdf.getPathName();

        pdf.setPathName("");
        pdf.setDocName("");

        return rPathName + docName;
    }

    public String ImprimePropImvPDF(ImoveisProprietario prop, boolean preview, boolean... bAttach) {
        System.out.println("ImprimePropImvPDF");

        Collections dadm = VariaveisGlobais.getAdmDados();
        float[] columnWidths = {};

        jPDF pdf = new jPDF();
        String sFileName = new tempFile("pdf").getsPathNameExt();
        pdf.setPathName(new tempFile().getTempPath());
        String docID = new tempFile().getTempFileName(sFileName);
        String docName = docID;
        pdf.setDocName(docName);

        // - Começo
        com.itextpdf.text.pdf.BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font =  new com.itextpdf.text.Font(bf, 9, com.itextpdf.text.Font.NORMAL);

        pdf.open(595f,842f,  20f, 20f, 10f, 10f);

        if (!VariaveisGlobais.logo_noprint) {
            // Logo
            Image img;
            try {
                img = Image.getInstance(dadm.get("logo").trim());
                img.setAlignment(Element.ALIGN_LEFT);
                img.scaleAbsolute(180, 60);
                pdf.doc_add(img);
            } catch (Exception e) { e.printStackTrace(); }
        }

        Paragraph p;

        if (!VariaveisGlobais.razao_noprint) {
            p = pdf.print(dadm.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        // CNPJ e CRECI
        columnWidths = new float[] {70, 30};
        PdfPTable tablecnpjcreci = new PdfPTable(columnWidths);
        tablecnpjcreci.setHeaderRows(0);
        tablecnpjcreci.setWidthPercentage(100);
        font.setColor(BaseColor.BLACK);
        PdfPCell cella = new PdfPCell(new Phrase(!VariaveisGlobais.cnpj_noprint ? "CNPJ: " + VariaveisGlobais.da_cnpj : "",font));
        cella.setHorizontalAlignment(Element.ALIGN_LEFT);
        cella.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cella);
        PdfPCell cellb = new PdfPCell(new Phrase(!VariaveisGlobais.creci_noprint ? "CRECI: " + VariaveisGlobais.da_creci : "",font));
        cellb.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellb.setBorder(Rectangle.NO_BORDER);
        tablecnpjcreci.addCell(cellb);
        tablecnpjcreci.completeRow();
        pdf.doc_add(tablecnpjcreci);

        if (!VariaveisGlobais.endereco_noprint) {
            p = pdf.print(dadm.get("endereco") + ", " + dadm.get("numero") + " " + dadm.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print(dadm.get("bairro") + " - " + dadm.get("cidade") + " - " + dadm.get("estado") + " - " + dadm.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }

        if (!VariaveisGlobais.telefone_noprint) {
            String[] tel = dadm.get("telefone").split(";");
            String ttel = "";
            if (tel.length > 0) {
                ttel = tel[0].split(",")[0];
            }
            p = pdf.print("Tel/Fax:" + ttel, pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Proprietário e seus imóveis", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);

        p.clear();
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("Proprietário: " + prop.getRgprp() + " - " + prop.getNome() + " / Tipo: " + prop.getTipo(), pdf.HELVETICA, 9, pdf.BOLD, pdf.LEFT, pdf.RED);
        pdf.doc_add(p);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Imoveis
        for (ImoveisImovel imovel : prop.getImovel()) {
            p = pdf.print("Imovel: " + imovel.getRgimv() + " - End.: " + imovel.getEnder(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print("Bairro: " + imovel.getBairro() + " Cidade: " + imovel.getCidade() + " Estado: " + imovel.getEstado() + " Cep: " + imovel.getCep(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
            p = pdf.print("Situação: " + imovel.getSituacao(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            if (imovel.getBaixa() != null) {
                ImoveisBaixa baixa = imovel.getBaixa();

                p = pdf.print("Data Baixa: " + Dates.DateFormata("dd-MM-yyyy",baixa.getDtbaixa()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("Motivo: " + baixa.getBxmotivo(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);

                p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
            }

            if (imovel.getVisitas().length != 0) {
                p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("VISITAS AO IMÓVEL", pdf.HELVETICA, 9, pdf.BOLD, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                for (ImoveisVisitas visita : imovel.getVisitas()) {
                    p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                    pdf.doc_add(p);
                    p = pdf.print("Nome: " + visita.getNome() + " Documento: " + visita.getDocumento(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                    pdf.doc_add(p);
                    p = pdf.print("Data da Visita: " + Dates.DateFormata("dd-MM-yyyy",visita.getData()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                    pdf.doc_add(p);
                    p = pdf.print("Histórico: " + visita.getHistorico(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            if (imovel.getCarteira() != null) {
                p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                ImoveisCarteira carteira = imovel.getCarteira();
                p = pdf.print("Contrato: " + carteira.getContrato() + " Dt.Inicio: " + Dates.DateFormata("dd-MM-yyyy",carteira.getDtinicio()) +
                        " Dt.Termino: " + Dates.DateFormata("dd-MM-yyyy",carteira.getDtermino()) + " Dt.Adito: " + Dates.DateFormata("dd-MM-yyyy",carteira.getDtadito()) +
                        " Reajuste: " + carteira.getReajuste(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
            }

            if (imovel.getMovimentos().length != 0) {
                p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("MOVIMENTOS", pdf.HELVETICA, 9, pdf.BOLD, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                for (ImoveisMovimento mov : imovel.getMovimentos()) {
                    p = pdf.print("Mês: " + mov.getMes() + " Ano: " + mov.getAno() + " Aluguel: " + mov.getAluguel(), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                    pdf.doc_add(p);
                }
            }

            p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            p.clear();
            LineSeparator l2 = new LineSeparator();
            l2.setAlignment(0);
            l2.setLineColor(pdf.BLUE);
            l2.setPercentage(80f);
            p.add(new Chunk(l2));
            pdf.doc_add(p);
        }
        // - Fim

        pdf.close();

        // Impressão
        if (!preview) {
            if (bAttach != null ) {
                if (bAttach.length > 0) if (!bAttach[0]) new toPrint(pdf.getPathName() + docName, "LASER", VariaveisGlobais.PrinterMode);
            }
        } else {
            if (bAttach.length == 0) {
                new PdfViewer("Preview do Proprietario e seus imoveis", pdf.getPathName() + docName);
            }
        }

        String rPathName = pdf.getPathName();
        pdf.setPathName("");
        pdf.setDocName("");
        return rPathName + docName;
    }
}
