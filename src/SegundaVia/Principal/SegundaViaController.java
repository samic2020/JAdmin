package SegundaVia.Principal;

import Calculos.AvisosMensagens;
import Calculos.Multas;
import Calculos.PegaDivisao;
import Classes.AttachEvent;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Classes.jExtrato;
import Funcoes.Collections;
import Funcoes.*;
import Gerencia.divSec;
import Locatarios.Pagamentos.cPagtos;
import Movimento.Avisos.TableRetencao;
import Movimento.Depositos.cDeposito;
import Movimento.Extrato.ExtratoBloqClass;
import Movimento.FecCaixa.cBanco;
import Movimento.FecCaixa.cCaixa;
import SegundaVia.Avisos.cAvisos;
import SegundaVia.Depositos.cDepositos;
import SegundaVia.Despesas.cDespesas;
import SegundaVia.Extratos.cRectos;
import com.sun.prism.impl.Disposer.Record;
import extrato.Extrato;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import pdfViewer.PdfViewer;

import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class SegundaViaController implements Initializable {
    private DbMain conn = VariaveisGlobais.conexao;
    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;
    private String nomeprop;
    private String[][] doctos = {};

    @FXML private AnchorPane anchorPane;
    @FXML private AnchorPane anchorPaneRecibos;
    @FXML private AnchorPane anchorPaneExtratos;
    @FXML private AnchorPane anchorPaneAdiantamentos;
    @FXML private AnchorPane anchorPaneAvisos;
    @FXML private AnchorPane anchorPaneRetencoes;
    @FXML private AnchorPane anchorPaneDespesas;
    @FXML private AnchorPane anchorPaneDepositos;
    @FXML private AnchorPane anchorPaneCaixas;

    @FXML private TableView<cAnexos> anxListaRec;
    @FXML private TableColumn<cAnexos, Integer> lrId;
    @FXML private TableColumn<cAnexos, String> lrDoc;
    @FXML private TableColumn<cAnexos, Integer> lrAut;
    @FXML private TableColumn<cAnexos, Date> lrVencto;
    @FXML private TableColumn<cAnexos, Date> lrRecto;
    @FXML private TableColumn<cAnexos, BigDecimal> lrValor;
    @FXML private TableColumn<cAnexos, Date> lrDataHora;
    @FXML private TableColumn<cAnexos, String> lrLogado;
    @FXML private TableColumn<cAnexos, String> lrLanctos;
    @FXML private TableColumn<Record, Boolean> lrAcoes;

    @FXML private Button btnSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InitializeGridAnexos();

        ChamaRecibos();
        ChamaExtratos();
        ChamaAdiantamentos();
        ChamaAvisos();
        ChamaRetencao();
        ChamaDespesas();
        ChamaDepositos();
        ChamaCaixas();

        btnSend.setOnAction(event -> {
            // Processar todos os anexos para gerar os arquivos temporários de pdf
            doctos = new String[][]{};
            for(cAnexos titem :  anxListaRec.getItems()) {
                String doc = null;
                switch (titem.getDoc().toString().toLowerCase()) {
                    case "rec":
                        doc = ReciboPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"REC", doc});
                        break;
                    case "ext":
                        doc = ExtratoPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"EXT", doc});
                        break;
                    case "avi":
                        doc = AvisoPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"AVI", doc});
                        break;
                    case "ret":
                        doc = RetencaoPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"RET", doc});
                        break;
                    case "dps":
                        doc = DespesaPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"DSP", doc});
                        break;
                    case "dep":
                        doc = DepositoPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"DEP", doc});
                        break;
                    case "cxa":
                        doc = CaixaPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"CXA", doc});
                        break;
                    case "adi":
                        doc = AdiantamentoPDF(titem);
                        if (doc != null) doctos = FuncoesGlobais.ArraysAdd(doctos, new String[] {"ADI", doc});
                        break;
                    default:
                        // Não identificado
                        break;
                }
            }

            try { ChamaTela("EMail", "/Email/JEmail.fxml", "loca.png"); } catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    private String ReciboPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        // Pega dados do Movimento
        Object[][] dMovi = null;
        try {
            dMovi = conn.LerCamposTabela(new String[] {"rgprp", "rgimv", "contrato"}, "movimento", "aut_rec = ?", new Object[][] {{"int", rAut}});
            this.rgprp = dMovi[0][3].toString();
            this.rgimv = dMovi[1][3].toString();
            this.contrato = dMovi[2][3].toString();
        } catch (Exception e) {
            this.rgprp = null;
            this.rgimv = null;
            this.contrato = null;
            this.nomeloca = null;
        }

        if (this.contrato == null) return retorno;
        try {
            this.nomeloca = conn.LerCamposTabela(new String[]{"CASE WHEN l_fisjur = 'F' THEN l_f_nome ELSE l_j_razao END AS nomeloca"}, "locatarios", "l_contrato = ?", new Object[][]{{"string", this.contrato}})[0][3].toString();
        } catch (SQLException e) {}

        Object[][] DadosImovel = null;
        try {
            DadosImovel = conn.LerCamposTabela(new String[] {
                    "i_end || ', ' || i_num || ' ' || i_cplto AS i_ender",
                    "i_bairro",
                    "i_cidade",
                    "i_estado",
                    "i_cep"
            }, "imoveis","i_rgimv = '" + rgimv + "'");
        } catch (Exception e) {}
        String qiend = "", qibai = "", qicid = "", qiest = "", qicep = "";
        if (DadosImovel != null) {
            qiend = DadosImovel[0][3].toString();
            qibai = DadosImovel[1][3].toString();
            qicid = DadosImovel[2][3].toString();
            qiest = DadosImovel[3][3].toString();
            qicep = DadosImovel[4][3].toString();
        }

        gRecibo recibo = new gRecibo();
        recibo.GeraReciboSegundaVia(contrato, Dates.DateFormata("dd/MM/yyyy", select.getVencto()));

        Collections dadm = VariaveisGlobais.getAdmDados();
        String[][] Lancamentos = ConvertArrayString2ObjectArrays_REC(select.getLanctos().toString());

        DadosLocatario dadosLocatario = new DadosLocatario(contrato, nomeloca, qiend, "", "", qibai, qicid, qiest, qicep);
        retorno = new Impressao(new BigInteger(String.valueOf(select.getAut())), Lancamentos, select.getDataHora(), select.getLogado()).ImprimeReciboPDF(dadm, null, dadosLocatario, recibo, true, false);

        return retorno;
    }

    private String ExtratoPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        // Pega dados do Movimento
        Object[][] dMovi = null;
        try {
            dMovi = conn.LerCamposTabela(new String[] {"rgprp", "rgimv"}, "caixa", "aut = ?", new Object[][] {{"int", rAut}});
            this.rgprp = dMovi[0][3].toString();
            this.rgimv = dMovi[1][3].toString();
        } catch (Exception e) {
            this.rgprp = null;
            this.rgimv = null;
            this.contrato = null;
            this.nomeprop = null;
        }

        this.contrato = this.rgprp;
        if (this.contrato == null) return retorno;

        Object[][] aProps = null;
        try {
            aProps = conn.LerCamposTabela(new String[]{"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][]{{"int", Integer.valueOf(this.contrato)}});
        } catch (SQLException e) {}
        if (aProps != null) this.nomeprop = aProps[0][3].toString();

        String lancto = "{{" + this.contrato.trim() + "," +
                select.getAut() + "," +
                Dates.DateFormata("dd-MM-yyyy", select.getDataHora()) + "," + select.getLogado() + "}}";
        retorno = pExtrato(this.contrato, lancto, select.getDataHora(), select.getLogado(), select.getValor(),select.getAut());

        return retorno;
    }

    private String AvisoPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        Object[][] dadosAviso = null;
        try { dadosAviso = conn.LerCamposTabela(new String[] {"documento", "operacao", "aut", "datahora", "valor", "logado", "lancamentos"}, "caixa", "aut = " + rAut); } catch (Exception e) {}
        if (dadosAviso == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aten��o");
            alert.setHeaderText("Autentica��o");
            alert.setContentText("N�o existe este autentica��o!!!");
            alert.showAndWait();
            return null;
        }
        if (!dadosAviso[0][3].toString().equalsIgnoreCase("AVI")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aten��o");
            alert.setHeaderText("Autentica��o");
            alert.setContentText("Esta autentica��o n�o � um aviso!!!");
            alert.showAndWait();
            return null;
        }

        String[][] lancto = ConvertArrayString2ObjectArrays(dadosAviso[6][3].toString());
        BigInteger Aut = new BigInteger(String.valueOf(rAut));
        Collections dadm = VariaveisGlobais.getAdmDados();

        Object[][] textoAdm = null;
        try { textoAdm = conn.LerCamposTabela(new String[] {"texto", "conta", "registro"},"avisos", "aut_rec = " + rAut); } catch (Exception e) {}
        String Texto = null;
        if (textoAdm != null) Texto = textoAdm[0][3].toString();

        String tpAviso = null; String codigo = null; String nome = null;
        if (textoAdm[1][3].toString().equalsIgnoreCase("0")) {
            codigo = textoAdm[2][3].toString();
            try {nome = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
            tpAviso = "ADM";
        } else if (textoAdm[1][3].toString().equalsIgnoreCase("1")) {
            codigo = textoAdm[2][3].toString();
            try {nome = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
            tpAviso = "PROPRIET�RIOS";
        } else if (textoAdm[1][3].toString().equalsIgnoreCase("2")) {
            codigo = textoAdm[2][3].toString();
            try {nome = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome"}, "locatarios", "l_contrato = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
            tpAviso = "LOCAT�RIOS";
        } else if (textoAdm[1][3].toString().equalsIgnoreCase("3")) {
            codigo = textoAdm[2][3].toString();
            try {nome = conn.LerCamposTabela(new String[] {"s_nome"}, "socios", "s_id = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
            tpAviso = "S�CIOS";
        } else {
            codigo = textoAdm[2][3].toString();
            try {nome = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = '" + codigo + "'")[0][3].toString();} catch (Exception e) {}
            tpAviso = "CONTAS";
        }

        Object[] dados = {dadosAviso[1][3].toString().equalsIgnoreCase("DEB") ? "D�BITO" : "CR�DITO", tpAviso, codigo, nome, new BigDecimal(LerValor.Number2BigDecimal(dadosAviso[4][3].toString().replace("R$ ",""))), Texto};
        retorno = new Impressao(Aut, lancto, Dates.String2Date(dadosAviso[3][3].toString()), dadosAviso[5][3].toString()).ImprimeAvisoPDF(dadm, dados, true, false);

        return retorno;
    }

    private String AdiantamentoPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        // Pega dados do Movimento
        Object[][] dMovi = null;
        try {
            dMovi = conn.LerCamposTabela(new String[] {"rgprp", "rgimv"}, "caixa", "aut = ?", new Object[][] {{"int", rAut}});
            this.rgprp = dMovi[0][3].toString();
            this.rgimv = dMovi[1][3].toString();
        } catch (Exception e) {
            this.rgprp = null;
            this.rgimv = null;
            this.contrato = null;
            this.nomeprop = null;
        }

        this.contrato = this.rgprp;
        if (this.contrato == null) return retorno;

        String lancto = "{{" + this.contrato + "," +
                select.getAut() + "," +
                Dates.DateFormata("dd-MM-yyyy", select.getDataHora()) + "," + select.getLogado() + "}}";
        retorno = pExtrato(this.contrato, lancto, select.getDataHora(), select.getLogado(), select.getValor(),select.getAut());

        return retorno;
    }

    private String RetencaoPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
        BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
        Collections dadm = VariaveisGlobais.getAdmDados();

        BigDecimal tRet = new BigDecimal(0);
        List<TableRetencao> lista = new ArrayList<TableRetencao>();
        String retSQL = "SELECT t.*, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campoDesc FROM taxas t WHERE aut_ret = " + select.getAut();
        ResultSet retRS = conn.AbrirTabela(retSQL, ResultSet.CONCUR_READ_ONLY);
        try {
            while (retRS.next()) {
                lista.add(
                        new TableRetencao(
                                retRS.getInt("id"),
                                "T",
                                retRS.getString("rgimv"),
                                "",
                                retRS.getString("campoDesc"),
                                retRS.getBigDecimal("valor"),
                                Dates.DateFormata("dd-MM-yyyy", retRS.getDate("dtretencao")),
                                Dates.DateFormata("dd-MM-yyyy", retRS.getDate("dtvencimento")),
                                true
                        )
                );
                tRet = tRet.add(retRS.getBigDecimal("valor"));
            }
        } catch (SQLException rex) {}
        try { DbMain.FecharTabela(retRS); } catch (Exception e) {}

        retorno = new Impressao(Aut, lancto, select.getDataHora(), select.getLogado()).ImprimeRetencaoPDF(dadm, lista, tRet, true);

        return retorno;
    }

    private String DespesaPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
        BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
        Collections dadm = VariaveisGlobais.getAdmDados();

        // Pega dados do Movimento
        Object[][] dMovi = null;
        try {
            dMovi = conn.LerCamposTabela(new String[] {"contrato"}, "caixa", "aut = ?", new Object[][] {{"int", rAut}});
            this.contrato = dMovi[0][3].toString();
        } catch (Exception e) {
            this.rgprp = null;
            this.rgimv = null;
            this.contrato = null;
            this.nomeprop = null;
        }

        if (this.contrato == null) return retorno;

        String codigo = null; String nome = null;
        codigo = this.contrato;

        // Pega Nome Despesa
        Object[][] aDespNome = null;
        try {
            aDespNome = conn.LerCamposTabela(new String[] {"descricao"},"despesasgrupo","id = ?", new Object[][] {{"int", Integer.valueOf(codigo)}});
        } catch (Exception e) {}
        if (aDespNome != null) nome = aDespNome[0][3].toString();

        Object[][] textoAdm = null;
        try { textoAdm = conn.LerCamposTabela(new String[] {"texto"},"despesas", "aut = " + select.getAut()); } catch (Exception e) {}
        String Texto = null;
        if (textoAdm != null) Texto = textoAdm[0][3].toString();

        Object[] dados = {null, null, codigo, nome, select.getValor(), Texto};
        retorno = new Impressao(Aut, lancto, select.getDataHora(), select.getLogado()).ImprimeDespesaPDF(dadm, dados, true, false);

        return retorno;
    }

    private String DepositoPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
        BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
        Collections dadm = VariaveisGlobais.getAdmDados();

        // Pega dados Lancamentos da autenticacao do cheque
        List<cDeposito> dados = new ArrayList<>();
        ResultSet cxrs = null;
        String selectSQL = "SELECT lancamentos, id, s, aut, datahora, lancamentos[s][1]::varchar(2) tipo, " +
                           "lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, " +
                           "lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, " +
                           "lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM " +
                           "(SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE " +
                           "(lancamentos[s][1] = 'CH') AND (not lancamentos[s][7] is null AND " +
                           "lancamentos[s][7] = '" + Aut.toString() + "' AND documento != 'DEP') ORDER BY 1,7,8,9";
        BigDecimal tch = new BigDecimal("0");
        try {
            cxrs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
            while (cxrs.next()) {
                int tid = 0; int ts = 0; Date tdatahora = null; Date tdtcheq = null;
                String tbanco = null; String tagencia = null; String tncheque = null;
                BigDecimal tvalor = null;
                try {tid = cxrs.getInt("id"); } catch (SQLException nex) {}
                try {ts = cxrs.getInt("s"); } catch (SQLException nex) {}
                try {tdatahora = cxrs.getDate("datahora"); } catch (SQLException nex) {}
                try {tdtcheq = cxrs.getDate("dtcheq"); } catch (SQLException nex) {}
                try {tbanco = cxrs.getString("banco"); } catch (SQLException nex) {}
                try {tagencia = cxrs.getString("agencia"); } catch (SQLException nex) {}
                try {tncheque = cxrs.getString("ncheque"); } catch (SQLException nex) {}
                try {tvalor = cxrs.getBigDecimal("valor"); } catch (SQLException nex) {}
                dados.add(
                        new cDeposito(
                                tid,
                                ts,
                                tdatahora,
                                tdtcheq,
                                tbanco,
                                tagencia,
                                tncheque,
                                tvalor,
                                true
                        )
                );
                tch = tch.add(cxrs.getBigDecimal("valor"));
            }
        } catch (Exception sex) {}
        try {DbMain.FecharTabela(cxrs);} catch (Exception ex) {}

        String depBanco = "";
        String depAgencia = "";
        String depConta = "";
        BigDecimal vrCheque = new BigDecimal("0");
        BigDecimal vrDinheiro = new BigDecimal("0");

        for (String[] o : lancto) {
            if (o[0].equalsIgnoreCase("DN")) {
                vrDinheiro = vrDinheiro.add(new BigDecimal(o[5]));
            } else if (o[0].equalsIgnoreCase("CH")) {
                depBanco = o[4];
                depAgencia = o[3];
                depConta = o[2];
                vrCheque = vrCheque.add(new BigDecimal(o[1]));
            }
        }

        retorno = new Impressao(Aut, new String[][] {
                {
                        "DP",
                        depBanco,
                        depAgencia,
                        depConta,
                        Dates.DateFormata("dd-MM-yyyy HH:mm:ss", select.getDataHora()),
                        LerValor.Number2BigDecimal(select.getValor().toPlainString())
                }},select.getDataHora(),select.getLogado()).ImprimeDepositoPDF(dadm, dados, vrCheque.add(vrDinheiro), vrDinheiro, true, false);

        return retorno;
    }

    private String CaixaPDF(cAnexos item) {
        String retorno = null;

        cAnexos select = item;
        int rAut = select.getAut();

        Date tcxData = select.getDataHora();
        String tcxUsuario = select.getLogado();
        String tcxLanctos = select.getLanctos();
        String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
        BigDecimal tcxDn = new BigDecimal(lancto[0][5]);
        BigDecimal tcxCh = new BigDecimal(lancto[1][5]);

        List<cCaixa> Lista = new ArrayList<>();
        String selectSQL = "SELECT rgprp, rgimv, contrato, lancamentos, id, s, aut, datahora, documento, operacao, lancamentos[s][1]::varchar(2) tipo, valor::decimal total, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE Upper(Trim(logado)) = ? AND (to_char(datahora,'DD-MM-YYYY') = ? OR datahora Is Null) AND fechado = TRUE ORDER BY to_char(datahora,'YYYY-MM-DD'), documento, operacao, aut, s, lancamentos[s][1]::varchar(2);";
        ResultSet fcRs = null;
        try {
            int oldaut = -1;
            List<cBanco> dataBanco = new ArrayList<>();
            fcRs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY,new Object[][] {
                    {"string", tcxUsuario.trim().toUpperCase()},
                    {"string", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(tcxData))}
            });
            String doc = null; int aut = -1; Date hora = null; BigDecimal total = null;
            String registro = null, situacao = null, operacao = null, tipo = null;

            while (fcRs.next()) {
                if (fcRs.getString("tipo").trim().toUpperCase().equalsIgnoreCase("CH")) {
                    if (oldaut != fcRs.getInt("aut")) {
                        if (oldaut == -1) {
                            try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                            try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                            try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                            try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                            try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                            try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                            try {total = fcRs.getBigDecimal("total"); } catch (SQLException e) {}
                            try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}

                            dataBanco = new ArrayList<>();
                            String banco = null, agencia = null, ncheque = null; Date datapre =  null; BigDecimal valor = null;
                            try {banco = fcRs.getString("banco");} catch (SQLException e) {}
                            try {agencia = fcRs.getString("agencia");} catch (SQLException e) {}
                            try {ncheque = fcRs.getString("ncheque");} catch (SQLException e) {}
                            try {datapre = Dates.StringtoDate(fcRs.getString("dtcheq"),"dd/MM/yyyy");} catch (SQLException e) {}
                            try {valor = fcRs.getBigDecimal("valor");} catch (SQLException e) {}
                            dataBanco.add(new cBanco(banco, agencia, ncheque, datapre, valor));

                            oldaut = fcRs.getInt("aut");
                        } else {
                            try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                            try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                            try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                            try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                            try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                            try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                            try {total = fcRs.getBigDecimal("total"); } catch (SQLException e) {}
                            try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}

                            String banco = null, agencia = null, ncheque = null; Date datapre =  null; BigDecimal valor = null;
                            try {banco = fcRs.getString("banco");} catch (SQLException e) {}
                            try {agencia = fcRs.getString("agencia");} catch (SQLException e) {}
                            try {ncheque = fcRs.getString("ncheque");} catch (SQLException e) {}
                            try {datapre = Dates.StringtoDate(fcRs.getString("dtcheq"),"dd/MM/yyyy");} catch (SQLException e) {}
                            try {valor = fcRs.getBigDecimal("valor");} catch (SQLException e) {}
                            dataBanco.add(new cBanco(banco, agencia, ncheque, datapre, valor));
                        }
                    } else {
                        String banco = null, agencia = null, ncheque = null; Date datapre =  null; BigDecimal valor = null;
                        try {banco = fcRs.getString("banco");} catch (SQLException e) {}
                        try {agencia = fcRs.getString("agencia");} catch (SQLException e) {}
                        try {ncheque = fcRs.getString("ncheque");} catch (SQLException e) {}
                        try {datapre = Dates.StringtoDate(fcRs.getString("dtcheq"),"dd/MM/yyyy");} catch (SQLException e) {}
                        try {valor = fcRs.getBigDecimal("valor");} catch (SQLException e) {}
                        dataBanco.add(new cBanco(banco, agencia, ncheque, datapre, valor));
                    }
                } else {
                    if (oldaut == -1) {
                        try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                        try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                        try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                        try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                        try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                        try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                        try {total = fcRs.getBigDecimal("valor"); } catch (SQLException e) {} //total
                        try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}
                        dataBanco = new ArrayList<>();

                        if (tipo.equalsIgnoreCase("CH")) {
                            total = TotalCheques(dataBanco);
                        }
                        Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, total, tipo, dataBanco));
                    } else {
                        if (tipo.equalsIgnoreCase("CH")) {
                            total = TotalCheques(dataBanco);
                        }
                        Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, TotalCheques(dataBanco), tipo, dataBanco));

                        try {doc = fcRs.getString("documento"); } catch (SQLException e) {}
                        try {aut = fcRs.getInt("aut"); } catch (SQLException e) {}
                        try {hora = fcRs.getTimestamp("datahora"); } catch (SQLException e) {}
                        try {registro = fcRs.getString("contrato"); } catch (SQLException e) {}
                        try {situacao = fcRs.getString("situacao"); } catch (SQLException e) {}
                        try {operacao = fcRs.getString("operacao"); } catch (SQLException e) {}
                        try {total = fcRs.getBigDecimal("valor"); } catch (SQLException e) {} // total
                        try {tipo = fcRs.getString("tipo"); } catch (SQLException e) {}
                        dataBanco = new ArrayList<>();

                        if (tipo.equalsIgnoreCase("CH")) {
                            total = TotalCheques(dataBanco);
                        }
                        Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, total, tipo, dataBanco));
                        oldaut = -1;
                    }
                }
            }
            if (oldaut != -1) {
                if (tipo.equalsIgnoreCase("CH")) {
                    total = TotalCheques(dataBanco);
                }
                Lista.add(new cCaixa(doc,aut,hora, registro, situacao, operacao, total, tipo, dataBanco));
                oldaut = -1;
            }
        } catch (SQLException sex) {sex.printStackTrace();}
        DbMain.FecharTabela(fcRs);
        System.out.println(Lista);

        Collections dadm = VariaveisGlobais.getAdmDados();
        retorno = new Impressao(new BigInteger("0"), new String[][] {}, select.getDataHora(), tcxUsuario).ImprimeCaixaPDF(dadm, new BigDecimal[] {tcxDn, tcxCh}, Lista, true, false);

        return retorno;
    }

    private BigDecimal TotalCheques(List<cBanco> dataBanco) {
        if (dataBanco.size() == 0) return new BigDecimal("0");

        BigDecimal tTotal = new BigDecimal("0");
        for (cBanco o : dataBanco) {
            tTotal = tTotal.add(o.getValor());
        }
        return tTotal;
    }

    private void ChamaCaixas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Caixas/Caixas.fxml"));
            Pane root = loader.load();
            anchorPaneCaixas.getChildren().add(root);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cDepositos cell = (cDepositos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "CXA";
                int tAut = cell.getAut();
                Date tVencto = cell.getDataHora();
                Date tRecto = cell.getDataHora();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Caixa j� incluso!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });

            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void ChamaRecibos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Recibos/Recibos.fxml"));
            Pane root = loader.load();
            anchorPaneRecibos.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cPagtos cell = (cPagtos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "REC";
                int tAut = cell.getAut();
                Date tVencto = cell.getVencto();
                Date tRecto = cell.getRecto();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Recibo j� incluso!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitializeGridAnexos() {
        lrId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        lrId.setStyle( "-fx-alignment: CENTER;");

        lrDoc.setCellValueFactory(new PropertyValueFactory<>("Doc"));
        lrDoc.setStyle( "-fx-alignment: CENTER;");

        lrAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        lrAut.setStyle( "-fx-alignment: CENTER;");

        lrVencto.setCellValueFactory(new PropertyValueFactory<>("Vencto"));
        lrVencto.setCellFactory((AbstractConvertCellFactory<cAnexos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        lrVencto.setStyle( "-fx-alignment: CENTER;");

        lrRecto.setCellValueFactory(new PropertyValueFactory<>("Recto"));
        lrRecto.setCellFactory((AbstractConvertCellFactory<cAnexos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        lrRecto.setStyle( "-fx-alignment: CENTER;");

        lrValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        lrValor.setCellFactory((AbstractConvertCellFactory<cAnexos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        lrValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        lrDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        lrDataHora.setCellFactory((AbstractConvertCellFactory<cAnexos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        lrDataHora.setStyle( "-fx-alignment: CENTER;");

        lrLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        lrLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        lrLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        lrLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        lrAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));

        //Adding the Button to the cell
        lrAcoes.setCellFactory(p -> new ButtonCell());
    }

    private void ChamaExtratos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Extratos/Extratos.fxml"));
            Pane root = loader.load();
            anchorPaneExtratos.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cRectos cell = (cRectos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "EXT";
                int tAut = cell.getAut();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();

                cAnexos newItem = new cAnexos(tId, tDoc, tAut, null, null, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Extrato j� incluso!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void ChamaAdiantamentos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Adiantamentos/Adiantamentos.fxml"));
            Pane root = loader.load();
            anchorPaneAdiantamentos.getChildren().add(root);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cRectos cell = (cRectos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "ADI";
                int tAut = cell.getAut();
                Date tVencto = cell.getDataHora();
                Date tRecto = cell.getDataHora();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Adiantamento j� incluso!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });

            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void ChamaAvisos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Avisos/Avisos.fxml"));
            Pane root = loader.load();
            anchorPaneAvisos.getChildren().add(root);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cAvisos cell = (cAvisos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "AVI";
                int tAut = cell.getAut();
                Date tVencto = cell.getDataHora();
                Date tRecto = cell.getDataHora();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Aviso j� incluso!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });

            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void ChamaRetencao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Retencao/Retencao.fxml"));
            Pane root = loader.load();
            anchorPaneRetencoes.getChildren().add(root);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cAvisos cell = (cAvisos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "RET";
                int tAut = cell.getAut();
                Date tVencto = cell.getDataHora();
                Date tRecto = cell.getDataHora();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Reten��o j� inclusa!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });

            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaDespesas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Despesas/Despesas.fxml"));
            Pane root = loader.load();
            anchorPaneDespesas.getChildren().add(root);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cDespesas cell = (cDespesas)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "DPS";
                int tAut = cell.getAut();
                Date tVencto = cell.getDataHora();
                Date tRecto = cell.getDataHora();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Despesa j� inclusa!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });

            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void ChamaDepositos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SegundaVia/Depositos/Depositos.fxml"));
            Pane root = loader.load();
            anchorPaneDepositos.getChildren().add(root);

            root.addEventHandler(AttachEvent.GET_ATTACH, event -> {
                cDepositos cell = (cDepositos)event.sparam[0];

                // Adiciona
                int tId = cell.getId();
                String tDoc = "DEP";
                int tAut = cell.getAut();
                Date tVencto = cell.getDataHora();
                Date tRecto = cell.getDataHora();
                BigDecimal tValor = cell.getValor();
                Date tDataHora = cell.getDataHora();
                String tLogado = cell.getLogado();
                String tLanctos = cell.getLanctos();
                cAnexos newItem = new cAnexos(tId, tDoc, tAut, tVencto, tRecto, tValor, tDataHora, tLogado, tLanctos);

                // Checa se j� foi lan�ado
                for(cAnexos titem :  anxListaRec.getItems()) {
                    if (titem.getId() == newItem.getId()) {
                        new Alert(INFORMATION, "Dep�sito j� incluso!").showAndWait();
                        return;
                    }
                }

                anxListaRec.getItems().add(newItem);
                anxListaRec.refresh();
            });

            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private String[][] ConvertArrayString2ObjectArrays_REC(String value) {
        String[][] retorno = {};

        // Fase 1 - Remo��o dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ArraysAdd(retorno,
                    new String[]{
                            vtr[0].trim().replace("\"",""),
                            vtr[4].trim().replace("\"",""),
                            vtr[3].trim().replace("\"",""),
                            vtr[2].trim().replace("\"",""),
                            vtr[5].trim().replace("\"",""),
                            vtr[1].trim().replace("\"","")
                    });
        }
        return retorno;
    }

    private void ChamaTela(String nome, String url, String icone) {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource(url)); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);

        internalFrame.pack();
        internalFrame.setVisible(true);


        // Envio dos anexos
        root.fireEvent(new AttachEvent(new Object[]{this.doctos}, AttachEvent.GET_ATTACH));

    }

    // Classe que cria o bot�o
    private class ButtonCell extends TableCell<Record, Boolean> {
        final Button cellButton = new Button("Del");

        ButtonCell(){
            //Action when the button is pressed
            cellButton.setOnAction(t -> {
                // get Selected Item
                cAnexos select = (cAnexos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                // Delete Item
                anxListaRec.getItems().remove(select);
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty) setGraphic(cellButton);
        }
    }

    public interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
        @Override
        default TableCell<E, T> call(TableColumn<E, T> param) {
            return new TableCell<E, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(convert(item));
                    }
                }
            };
        }

        String convert(T value);
    }

    private String pExtrato(String rgprp, String lancto, Date dataHora, String logado, BigDecimal vrpago, int aut) {
        ObservableList<ExtratoBloqClass> bloqdata = FXCollections.observableArrayList();

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");
        BigDecimal tVRP = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        // Saldo Anterior
        String saSql = "SELECT registro, valor, valorpago, aut_pag FROM propsaldo Where aut_pag = '%s';";
        saSql = String.format(saSql,lancto);
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
                tVRP = tVRP.add(sars.getBigDecimal("valorpago"));
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}
        if (ttCR.floatValue() != 0) {
            Extrato = new jExtrato(Descr("<b>Saldo Anterior</b>"), ttCR, null);
            lista.add(Extrato);

            // Pula linha
            Extrato = new jExtrato(null, null, null);
            lista.add(Extrato);
        }

        // Pegar as percentagens do principal
        List<divSec> dPrin = new PegaDivisao().PegaDivisoes(rgprp);

        // Pegar as divis�es secund�rias
        List<divSec> dSec = new PegaDivisao().PegaDivSecundaria(rgprp);

        // Aqui pega os recibos recebidos e n�o pagos
        String sql = "";
        sql = "select * from movimento where aut_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                Object[][] endImovel = null;
                try {
                    endImovel = conn.LerCamposTabela(new String[] {"i_end", "i_num", "i_cplto"},"imoveis", "i_rgimv = '" + rs.getString("rgimv") + "'");
                } catch (Exception e) {}
                String linha = "<b>" + rs.getString("rgimv") + "</b> - " + endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim();
                if (!bloq) {
                    Extrato = new jExtrato(Descr(linha), null, null);
                    lista.add(Extrato);
                }

                Object[][] nomeLoca = null;
                try {
                    nomeLoca = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"},"locatarios", "l_contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {}
                if (!bloq) {
                    Extrato = new jExtrato(Descr((String) nomeLoca[0][3]), null, null);
                    lista.add(Extrato);

                    Extrato = new jExtrato(Descr("[" + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtrecebimento")) + "] - " + rs.getString("aut_rec")), null, null);
                    lista.add(Extrato);
                }

                if (bloq) {
                    bloqdata.add(new ExtratoBloqClass(
                            "R",
                            rs.getString("rgimv"),
                            endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim(),
                            new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")))
                    );
                    continue;
                }

                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                int dpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                if (dpos > -1) {
                    String[] divisao = dPrin.get(dpos).getDivisao().split(",");
                    int apos = FuncoesGlobais.IndexOf(divisao,"ALU");
                    if (apos > -1) {
                        palu = new PegaDivisao().LerPercent(divisao[apos],true);
                    }
                } else {
                    dpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                    if (dpos > -1) {
                        String[] divisao = dSec.get(dpos).getDivisao().split(",");
                        int apos = FuncoesGlobais.IndexOf(divisao, "ALU");
                        if (apos > -1) {
                            palu = new PegaDivisao().LerPercent(divisao[apos], true);
                        } else {
                            palu = new BigDecimal("100");
                        }
                    } else {
                        palu = new BigDecimal("100");
                    }
                }
                alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("ALU") + "  " + rs.getBigDecimal("mensal") + "  " + palu),alu, null );
                lista.add(Extrato);
                ttCR = ttCR.add(alu);

                // Teste de Grava��o de uma variavel modo Read
                Reserva(rs.getInt("id"), rs.getString("reserva"), "movimento");

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferen�a
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    dfsql = "select * from descdif where aut_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferen�a de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            Extrato = new jExtrato(Descr(dftipostr + VariaveisGlobais.contas_ca.get("ALU") + " " + dfrs.getString("descricao")),dftipo.trim().equalsIgnoreCase("C") ? dfCR : null, dftipo.trim().equalsIgnoreCase("D") ? dfDB : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comiss�o
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COM") + "  " + rs.getBigDecimal("cm")),null, com);
                lista.add(Extrato);
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    int irpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (irpos > -1) {
                        String[] divisao = dPrin.get(irpos).getDivisao().split(",");
                        int airpos = FuncoesGlobais.IndexOf(divisao,"IRF");
                        if (airpos > -1) {
                            pir = new PegaDivisao().LerPercent(divisao[airpos],true);
                        }
                    } else {
                        irpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (irpos > -1) {
                            String[] divisao = dSec.get(irpos).getDivisao().split(",");
                            int airpos = FuncoesGlobais.IndexOf(divisao, "IRF");
                            if (airpos > -1) {
                                pir = new PegaDivisao().LerPercent(divisao[airpos], true);
                            }
                        }
                    }
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IRF")), null, pirvr);
                        lista.add(Extrato);
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    sgsql = "select * from seguros where aut_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            int dsgpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (dsgpos > -1) {
                                String[] divisao = dPrin.get(dsgpos).getDivisao().split(",");
                                int asgpos = FuncoesGlobais.IndexOf(divisao,"SEG");
                                if (asgpos > -1) {
                                    psg = new PegaDivisao().LerPercent(divisao[asgpos],true);
                                }
                            } else {
                                dsgpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (dsgpos > -1) {
                                    String[] divisao = dSec.get(dsgpos).getDivisao().split(",");
                                    int asgpos = FuncoesGlobais.IndexOf(divisao, "SEG");
                                    if (asgpos > -1) {
                                        psg = new PegaDivisao().LerPercent(divisao[asgpos], true);
                                    } else {
                                        psg = new BigDecimal("100");
                                    }
                                } else {
                                    psg = new BigDecimal("100");
                                }
                            }

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("SEG") + "  " + sgrs.getString("cota")),seg,sgrs.getBoolean("retencao") ? seg : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            int ippos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (ippos > -1) {
                                String[] divisao = dPrin.get(ippos).getDivisao().split(",");
                                int aippos = FuncoesGlobais.IndexOf(divisao,"IRF");
                                if (aippos > -1) {
                                    pir = new PegaDivisao().LerPercent(divisao[aippos],true);
                                }
                            } else {
                                ippos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (ippos > -1) {
                                    String[] divisao = dSec.get(ippos).getDivisao().split(",");
                                    int aippos = FuncoesGlobais.IndexOf(divisao, "IPT");
                                    if (aippos > -1) {
                                        pip = new PegaDivisao().LerPercent(divisao[aippos], true);
                                    }
                                }
                            }
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar reten��o
                            if (pipvr.doubleValue() != 0) {
                                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IPT")), null, pipvr);
                                lista.add(Extrato);
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "";
                    txsql = "select * from taxas where aut_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            int dtxpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (dtxpos > -1) {
                                String[] divisao = dPrin.get(dtxpos).getDivisao().split(",");
                                int atxpos = FuncoesGlobais.IndexOf(divisao,txrs.getString("campo"));
                                if (atxpos > -1) {
                                    ptx = new PegaDivisao().LerPercent(divisao[atxpos],true);
                                }
                            } else {
                                dtxpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (dtxpos > -1) {
                                    String[] divisao = dSec.get(dtxpos).getDivisao().split(",");
                                    int atxpos = FuncoesGlobais.IndexOf(divisao, txrs.getString("campo"));
                                    if (atxpos > -1) {
                                        ptx = new PegaDivisao().LerPercent(divisao[atxpos], true);
                                    } else {
                                        ptx = new BigDecimal("100");
                                    }
                                } else {
                                    ptx = new BigDecimal("100");
                                }
                            }

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            String txdecr = conn.LerCamposTabela(new String[] {"descricao"}, "campos","codigo = '" + txrs.getString("campo") + "'")[0][3].toString();
                            Extrato = new jExtrato(Descr(txdecr + "  " + txrs.getString("cota")),(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : null), (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : null));
                            lista.add(Extrato);
                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    int mupos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (mupos > -1) {
                        String[] divisao = dPrin.get(mupos).getDivisao().split(",");
                        int amupos = FuncoesGlobais.IndexOf(divisao,"MUL");
                        if (amupos > -1) {
                            pmu = new PegaDivisao().LerPercent(divisao[amupos],true);
                        }
                    } else {
                        mupos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (mupos > -1) {
                            String[] divisao = dSec.get(mupos).getDivisao().split(",");
                            int amupos = FuncoesGlobais.IndexOf(divisao, "MUL");
                            if (amupos > -1) {
                                pmu = new PegaDivisao().LerPercent(divisao[amupos], true);
                            } else {
                                pmu = new BigDecimal("100");
                            }
                        } else {
                            pmu = new BigDecimal("100");
                        }
                    }
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("MUL")), pmuvr, null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    int jupos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (jupos > -1) {
                        String[] divisao = dPrin.get(jupos).getDivisao().split(",");
                        int ajupos = FuncoesGlobais.IndexOf(divisao,"JUR");
                        if (ajupos > -1) {
                            pju = new PegaDivisao().LerPercent(divisao[ajupos],true);
                        }
                    } else {
                        jupos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (jupos > -1) {
                            String[] divisao = dSec.get(jupos).getDivisao().split(",");
                            int ajupos = FuncoesGlobais.IndexOf(divisao, "JUR");
                            if (ajupos > -1) {
                                pju = new PegaDivisao().LerPercent(divisao[ajupos], true);
                            } else {
                                pju = new BigDecimal("100");
                            }
                        } else {
                            pju = new BigDecimal("100");
                        }
                    }
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("JUR")), pjuvr, null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                {
                    int copos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (copos > -1) {
                        String[] divisao = dPrin.get(copos).getDivisao().split(",");
                        int acopos = FuncoesGlobais.IndexOf(divisao,"COR");
                        if (acopos > -1) {
                            pco = new PegaDivisao().LerPercent(divisao[acopos],true);
                        }
                    } else {
                        copos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (copos > -1) {
                            String[] divisao = dSec.get(copos).getDivisao().split(",");
                            int acopos = FuncoesGlobais.IndexOf(divisao, "COR");
                            if (acopos > -1) {
                                pco = new PegaDivisao().LerPercent(divisao[acopos], true);
                            } else {
                                pco = new BigDecimal("100");
                            }
                        } else {
                            pco = new BigDecimal("100");
                        }
                    }
                    try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                    try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                    try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                    if (pcovr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COR")), pcovr,null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pcovr);
                    }
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                {
                    int eppos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (eppos > -1) {
                        String[] divisao = dPrin.get(eppos).getDivisao().split(",");
                        int aeppos = FuncoesGlobais.IndexOf(divisao,"EXP");
                        if (aeppos > -1) {
                            pep = new PegaDivisao().LerPercent(divisao[aeppos],true);
                        }
                    } else {
                        eppos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (eppos > -1) {
                            String[] divisao = dSec.get(eppos).getDivisao().split(",");
                            int aeppos = FuncoesGlobais.IndexOf(divisao, "EXP");
                            if (aeppos > -1) {
                                pep = new PegaDivisao().LerPercent(divisao[aeppos], true);
                            } else {
                                pep = new BigDecimal("100");
                            }
                        } else {
                            pep = new BigDecimal("100");
                        }
                    }
                    try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                    try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                    try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                    if (pepvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("EXP")), pepvr,null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pepvr);
                    }
                }

                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (SQLException ex) {}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_pag::varchar like '%" + lancto + "%' order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    bloqdata.add(new ExtratoBloqClass(
                            "A",
                            avrs.getString("registro"),
                            avrs.getString("texto"),
                            "-"
                    ));
                    continue;
                }

                Font font = new Font("SansSerif",Font.PLAIN,8);
                Canvas c = new Canvas();
                FontMetrics fm = c.getFontMetrics(font);
                String aLinhas[] = WordWrap.wrap(avrs.getString("texto") + "  " + new SimpleDateFormat("dd/MM/yyyy").format(avrs.getDate("dtrecebimento")) + " - " + avrs.getString("aut_rec"), 230, fm).split("\n");
                for (int k=0;k<aLinhas.length;k++) {
                    BigDecimal lcr = null, ldb = null;
                    lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                    ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;
                    Extrato = new jExtrato(Descr(aLinhas[k]), k == aLinhas.length - 1 ? lcr : null, k == aLinhas.length - 1 ? ldb : null);
                    lista.add(Extrato);
                }

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));

                // Pula Linha
                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Cr�ditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de D�ditos</b></font>"), null, ttDB);
        lista.add(Extrato);

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        String tDesc = "";
        if (ttSld.floatValue() > 0) {
            tDesc = "<font color=blue><b>L�quido a Receber</b></font>";
        } else {
            tDesc = "<font color=red><b>L�quido a Receber</b></font>";
        }
        Extrato = new jExtrato(Descr(tDesc), ttSld.floatValue() > 0 ? ttSld : null, ttSld.floatValue() < 0 ? ttSld : null);
        lista.add(Extrato);

        Extrato = new jExtrato(null,  null, null);
        lista.add(Extrato);

        // Dados Banc�rios para Dep�sito
        sql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        sql = String.format(sql,rgprp);
        rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        String nomeProp = null; String banco = null;
        try {
            rs.next();
            nomeProp = rs.getString("p_nome");
            banco = rs.getString("p_bancos");
        } catch (SQLException ex) {}
        try {rs.close();} catch (Exception ex) {}

        String[] bancos = null; String[] bancoPrin = null;
        if (banco != null) {
            bancos = banco.split(";");
        }
        if (bancos != null) {
            bancoPrin = bancos[0].split(",");
        }

        if (bancoPrin != null) {
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Banco: " + bancoPrin[0] + " - " + conn.LerCamposTabela(new String[] {"nome"},"bancos","numero = '" + bancoPrin[0] + "'")[0][3] + "</b></font>"),  null, null); } catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Ag�ncia: " + bancoPrin[1] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>C/C: " + bancoPrin[2] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Favorecido: " + (bancoPrin[3].equalsIgnoreCase("null") ? "O Pr�prio" : bancoPrin[3])  + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
        }

        // complementa com linhas em branco para preencher a p�gina
        int npag = lista.size() % 32;
        for (int i=1;i<=(32 - npag);i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }

        String psql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        psql = String.format(sql,this.contrato.trim());
        ResultSet prs = conn.AbrirTabela(psql,ResultSet.CONCUR_READ_ONLY);
        String pnomeProp = null; String pbanco = null;
        try {
            prs.next();
            pnomeProp = rs.getString("p_nome");
            pbanco = rs.getString("p_bancos");
        } catch (SQLException ex) {}
        try {prs.close();} catch (Exception ex) {}

        String[] pbancos = null; String[] pbancoPrin = null;
        if (pbanco != null) {
            pbancos = pbanco.split(";");
        }
        if (pbancos != null) {
            pbancoPrin = pbancos[0].split(",");
        }

        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = dadm.get("marca") +
                FuncoesGlobais.StrZero(String.valueOf(aut),10) +
                Dates.DateFormata("ddMMyyyyHHmm", dataHora) +
                new DecimalFormat("#,##0.00").format(vrpago).replace(",","").replace(".","") +
                " " + logado;
        Map parametros = new HashMap();
        parametros.put("Logo",dadm.get("logo"));
        parametros.put("rgprp", this.contrato.trim());
        parametros.put("nomeProp",this.nomeprop);
        parametros.put("NumeroExtrato", Integer.valueOf(LoadExtratoNumber(this.contrato.trim(), aut)));
        parametros.put("Mensagem", new AvisosMensagens().VerificaAniProprietario(this.contrato.trim()) ? "Este � o m�s do seu anivers�rio. PARAB�NS!" : "");
        parametros.put("Autenticacao",autenticacao);
        parametros.put("ShowSaldo", true);

        List<extrato.Extrato> listas = new ArrayList<>();
        extrato.Extrato bean1 = new Extrato();
        bean1.setnomeProp(this.contrato.trim() + " - " + this.nomeprop);
        bean1.setextratoNumero(LoadExtratoNumber(this.contrato.trim(), aut));
        bean1.setlogoLocation(dadm.get("logo"));
        bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(this.contrato.trim()) ? "Este � o m�s do seu anivers�rio. PARAB�NS!" : "");
        bean1.setautentica(autenticacao);
        //bean1.setbarras(aut.toString());
        int n = 0;
        for (int i = 0; i <= lista.size() - 1; i++) {
            jExtrato item = lista.get(i);

            if (i % 40 == 0 && i > 0) {
                listas.add(bean1);
                bean1 = new Extrato();
                bean1.setnomeProp(this.contrato.trim() + " - " + this.nomeprop);
                bean1.setextratoNumero(LoadExtratoNumber(this.contrato.trim(), aut));
                bean1.setlogoLocation(dadm.get("logo"));
                bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(this.contrato.trim()) ? "Este � o m�s do seu anivers�rio. PARAB�NS!" : "");
                bean1.setautentica(autenticacao);
                //bean1.setbarras(aut.toString());
                n = 0;
            }
            String cHist = null;
            try { cHist = item.getHist_linha(); } catch (Exception ex) {}
            if (cHist == null) cHist = "";
            bean1.sethist_linhan(n + 1, cHist);
            String cCred = ""; String cDeb = "";
            try { cCred = new DecimalFormat("#,##0.00").format(item.getHist_cred()); } catch (Exception ex) {}
            try { cDeb = new DecimalFormat("#,##0.00").format(item.getHist_deb()); } catch (Exception ex) {}
            bean1.sethist_credn(n + 1, cCred);
            bean1.sethist_debn(n + 1, cDeb);
            n++;
        }
        if (n < 40) listas.add(bean1);

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"jExtrato", parametros);
        // new toPrint(pdfName,"LASER","INTERNA");
        //new PdfViewer("Extrato do Propriet�rio", pdfName);
        return pdfName;
    }

    private String Descr(String desc) { return "<html>" + desc + "</html>"; }
    private void Reserva(int id, String fieldReserva, String local) {}
    private String LoadExtratoNumber(String rgprp, float naut) {
        String retorno = "0000000000";
        String loadSQL = "SELECT Count(registro) AS taut FROM propsaldo WHERE registro = ? AND aut_pag is not null AND aut_pag[1][2]::float <= ?;";
        ResultSet loadRS = null;
        try {
            loadRS = conn.AbrirTabela(loadSQL, ResultSet.CONCUR_READ_ONLY,new Object[][] {{"string", rgprp}, {"float", naut}});
            while (loadRS.next()) {
                retorno += String.valueOf(loadRS.getInt("taut") + 1);
            }
            if (retorno.equalsIgnoreCase("0000000000")) retorno = "0000000001";
        } catch (SQLException SQLex) {}
        try { loadRS.close(); } catch (SQLException SQLex) {}
        return retorno.substring(retorno.length() - 10);
    }

    private String[][] ConvertArrayString2ObjectArrays(String value) {
        String[][] retorno = {};

        // Fase 1 - Remo��o dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ArraysAdd(retorno,
                    new String[]{
                            vtr[0].trim().replace("\"",""),
                            vtr[4].trim().replace("\"",""),
                            vtr[3].trim().replace("\"",""),
                            vtr[2].trim().replace("\"",""),
                            vtr[5].trim().replace("\"",""),
                            vtr[1].trim().replace("\"","")
                    });
        }
        return retorno;
    }

}
