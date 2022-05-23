package Movimento.Adiantamento;

import Calculos.AvisosMensagens;
import Calculos.Processa;
import Classes.jExtrato;
import Classes.paramEvent;
import Funcoes.Collections;
import Funcoes.*;
import PagRec.PagamentosController;
import extrato.Extrato;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;
import pdfViewer.PdfViewer;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class AdiantaController implements Initializable {
    private DbMain conn = VariaveisGlobais.conexao;
    private boolean bAbateAvDeb = false;

    private String[] _possibleSuggestionsPrRegistro = {};
    private String[] _possibleSuggestionsPrNome = {};
    private String[][] _possibleSuggestionsPr = {};
    private Set<String> possibleSuggestionsPrRegistro;
    private Set<String> possibleSuggestionsPrNome;
    private boolean isSearchPrRegistro = true;
    private boolean isSearchPrNome = true;

    private BigInteger Autenticacao;

    private PagamentosController controllerPag;

    @FXML private AnchorPane anchorPane;

    @FXML private TextField adCodigo;
    @FXML private TextField adNome;

    @FXML private TableView<classAdImoveis> adImoveis;
    @FXML private TableColumn<classAdImoveis, Boolean> adImoveisTag;
    @FXML private TableColumn<classAdImoveis, String> adImoveisRgimv;
    @FXML private TableColumn<classAdImoveis, String> adImoveisContrato;
    @FXML private TableColumn<classAdImoveis, String> adImoveisEndereco;
    @FXML private TableColumn<classAdImoveis, Date> adImoveisVencimento;
    @FXML private TableColumn<classAdImoveis, BigDecimal> adImoveisValor;

    @FXML private TableView<classAdRecibo> adRecibo;
    @FXML private TableColumn<classAdRecibo, Integer> adReciboId;
    @FXML private TableColumn<classAdRecibo, String> adReciboTabela;
    @FXML private TableColumn<classAdRecibo, Boolean> adReciboTag;
    @FXML private TableColumn<classAdRecibo, String> adReciboDescricao;
    @FXML private TableColumn<classAdRecibo, String> adReciboCotaParcela;
    @FXML private TableColumn<classAdRecibo, BigDecimal> adReciboValor;
    @FXML private TextField adReciboComissao;
    @FXML private TextField adReciboLiquido;

    @FXML private TextField adValor;
    @FXML private CheckBox adEnviarEmail;
    @FXML private Pane adTpagtos;

    @FXML private TableView<classDebLista> adDebLista;
    @FXML private TableColumn<classDebLista, Integer> adDebListaId;
    @FXML private TableColumn<classDebLista, String> adDebListaDesc;
    @FXML private TableColumn<classDebLista, BigDecimal> adDebListaValor;

    @FXML private TextArea adDebMsg;
    @FXML private TextField adDebValor;
    @FXML private TextField adDebValorTotal;
    @FXML private Button btnLancDeb;

    @FXML private Button btnImprimir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam.length == 0) {
                // Cancelar Recebimento
                Platform.runLater(() -> adCodigo.requestFocus());
            }
            if (event.sparam.length > 0) {
                if (event.sparam[0] != null) {
                    // Imprimir Recebimento
                    String[][] Lancamentos = (String[][]) event.sparam[1];

                    Collections dadm = VariaveisGlobais.getAdmDados();

                    BigInteger aut = conn.PegarAutenticacao();
                    this.Autenticacao = aut;

                    Distribuicao(adCodigo.getText(), "0", "", DbMain.GeraLancamentosArray(Lancamentos), LerValor.Number2BigDecimal(adValor.getText()));

                    // Impressao
                    pExtrato(aut);

                    adCodigo.requestFocus();
                } else {
                    Platform.runLater(() -> adCodigo.requestFocus());
                }
            } else {
                Platform.runLater(() -> adCodigo.requestFocus());
            }
        });

        adDebMsg.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                adDebMsg.appendText("\n");
            }
        });

        btnLancDeb.setOnAction(event -> {
            if (adCodigo.getText().equalsIgnoreCase("")) {
                Alert msg = new Alert(Alert.AlertType.INFORMATION);
                msg.setContentText("Um proprietário dese ser escolhido.");
                msg.showAndWait();
                adCodigo.requestFocus();
                return;
            }
            if (adDebMsg.getText().trim().equalsIgnoreCase("")) {
                Alert msg = new Alert(Alert.AlertType.INFORMATION);
                msg.setContentText("Você deve inserir uma descriminação.");
                msg.showAndWait();
                adDebMsg.requestFocus();
                return;
            }
            if (LerValor.StringToFloat(adDebValor.getText()) <= 0 ) {
                Alert msg = new Alert(Alert.AlertType.INFORMATION);
                msg.setContentText("Você deve inserir um valor.");
                msg.showAndWait();
                adDebValor.requestFocus();
                return;
            }

            String insertSQL = "INSERT INTO avisos (conta, registro, tipo, texto, valor) VALUES (6, ?, 'DEB', ?, ?);";
            conn.ExecutarComando(insertSQL, new Object[][] {{"string", adCodigo.getText()}, {"string", adDebMsg.getText().trim()}, {"decimal", new BigDecimal(LerValor.Number2BigDecimal(adDebValor.getText()))}});
            FillAvAdianta();
            adDebMsg.setText(""); adDebValor.setText("0,00");
        });

        btnImprimir.setOnAction(event -> {
            BigDecimal ttCR = new BigDecimal("0");
            BigDecimal ttDB = new BigDecimal("0");

            List<jExtrato> lista = new ArrayList<jExtrato>();
            for (classAdImoveis imv : adImoveis.getItems()) {
                if (imv.getTag()) {
                    BigDecimal cm = RetornaComissaoImoveis(adCodigo.getText(), imv.getRgimv(), imv.getContrato(), Dates.DateFormata("dd-MM-yyyy", imv.getVencimento()));
                    Object[][] mRef = null;
                    try {
                        mRef = conn.LerCamposTabela(new String[] {"referencia"}, "movimento", "contrato = ? AND dtvencimento = ? AND selected = true",
                                new Object[][] {
                                        {"string", imv.getContrato()},
                                        {"date", Dates.toSqlDate(imv.getVencimento())}
                                });
                    } catch (SQLException esx) { System.out.println(esx); }
                    if (mRef != null) {
                        Object[] pRef = pExtratoPreview(imv.getContrato(), mRef[0][3].toString(), new Object[] {lista, ttCR, ttDB});
                        BigDecimal tCR = (BigDecimal) pRef[1];
                        BigDecimal tDB = (BigDecimal) pRef[2];
                        ttCR = tCR;
                        ttDB = tDB;
                    }
                }
            }
            pExtratoPreviewRodape(new Object[] {lista, ttCR, ttDB});
        });

        avAdiantaEnable(false);
        MaskFieldUtil.monetaryField(adDebValor);

        Deb();
        AutocompletePr();
        Platform.runLater(() -> adCodigo.requestFocus());
    }

    private void avAdiantaEnable(boolean enable) {
        adDebLista.setDisable(!enable);
        adDebMsg.setDisable(!enable);
        adDebValor.setDisable(!enable);
        adDebValorTotal.setDisable(!enable);
        btnLancDeb.setDisable(!enable);
    }

    private void pExtrato(BigInteger aut) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        String movimentoSQL = "select rgprp, rgimv, contrato, cota, mensal, dtvencimento, cm, referencia, ad_pag[1][3] as dtrecebimento, ad_pag[1][2] as aut_rec from movimento where ad_pag[1][2] = '" + aut + "'";
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Object[][] endImovel = null;
                try {
                    endImovel = conn.LerCamposTabela(new String[]{"i_end", "i_num", "i_cplto"}, "imoveis", "i_rgimv = '" + rs.getString("rgimv") + "'");
                } catch (Exception e) {
                }
                String linha = "<b>" + rs.getString("rgimv") + "</b> - " + endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim();
                Extrato = new jExtrato(Descr(linha), null, null);
                lista.add(Extrato);

                Object[][] nomeLoca = null;
                try {
                    nomeLoca = conn.LerCamposTabela(new String[]{"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"}, "locatarios", "l_contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {
                }
                Extrato = new jExtrato(Descr((String) nomeLoca[0][3]), null, null);
                lista.add(Extrato);

                Extrato = new jExtrato(Descr("[" + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")) + " - " + rs.getString("dtrecebimento") + "] - " + rs.getString("aut_rec")), null, null);
                lista.add(Extrato);

                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try {
                    com = rs.getBigDecimal("cm");
                } catch (Exception ex) {
                    com = new BigDecimal("0");
                }
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("ALU") + "  " + rs.getBigDecimal("mensal") + "  " + palu), alu, null);
                lista.add(Extrato);
                ttCR = ttCR.add(alu);

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    dfsql = "select * from descdif where ad_pag[1][2] = '" + aut + "'";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
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
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COM") + "  " + rs.getBigDecimal("cm")),null, com);
                lista.add(Extrato);
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IRF")), null, pirvr);
                        lista.add(Extrato);
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    sgsql = "select * from seguros where ad_pag[1][2] = '" + aut + "'";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor"); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("SEG") + "  " + sgrs.getString("cota")),seg,sgrs.getBoolean("retencao") ? seg : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
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
                    txsql = "select * from taxas where ad_pag[1][2] = '" + aut + "'";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            String txdecr = conn.LerCamposTabela(new String[] {"descricao"}, "campos","codigo = '" + txrs.getString("campo") + "'")[0][3].toString();
                            Extrato = new jExtrato(Descr(txdecr + "  " + txrs.getString("cota")),(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : null), (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : null));
                            lista.add(Extrato);
                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }
                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (SQLException sqlex) {
            System.out.println(sqlex);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where registro = '%s' and aut_rec <> 0 and conta = 6 order by 1;";
        avsql = String.format(avsql, adCodigo.getText());
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (avrs.next()) {
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

/*
        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);
*/

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Créditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de Déditos</b></font>"), null, ttDB);
        lista.add(Extrato);

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        String tDesc = "";
        if (ttSld.floatValue() > 0) {
            tDesc = "<font color=blue><b>Líquido a Receber</b></font>";
        } else {
            tDesc = "<font color=red><b>Líquido a Receber</b></font>";
        }
        Extrato = new jExtrato(Descr(tDesc), ttSld.floatValue() > 0 ? ttSld : null, ttSld.floatValue() < 0 ? ttSld : null);
        lista.add(Extrato);

        Extrato = new jExtrato(null,  null, null);
        lista.add(Extrato);

        // Dados Bancários para Depósito
        String sql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        sql = String.format(sql,adCodigo.getText());
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
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Agência: " + bancoPrin[1] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>C/C: " + bancoPrin[2] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Favorecido: " + (bancoPrin[3].equalsIgnoreCase("null") ? "O Próprio" : bancoPrin[3])  + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
        }

        // complementa com linhas em branco para preencher a página
        int npag = lista.size() % 32;
        for (int i=1;i<=32-npag;i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }

        //Report(lista, aut);
        PrintExtrato(lista,aut);
    }

    //private String Descr(String desc) { return "<html>" + desc + "</html>"; }
    private String Descr(String desc) { return desc; }

    private void Report(List<jExtrato> lista, BigInteger aut) {
        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = aut.toString();
        if (aut.doubleValue() >= 0) {
            autenticacao = aut.floatValue() != 0 ? dadm.get("marca") +
                    FuncoesGlobais.StrZero(String.valueOf(aut), 10) +
                    Dates.DateFormata("ddMMyyyyHHmm", DbMain.getDateTimeServer()) +
                    new DecimalFormat("#,##0.00").format(controllerPag.getValorPago()).replace(",", "").replace(".", "") +
                    " " + VariaveisGlobais.usuario : "";
        }
        Map parametros = new HashMap();
        parametros.put("Logo", dadm.get("logo"));
        parametros.put("rgprp", adCodigo.getText());
        parametros.put("nomeProp", adNome.getText());
        parametros.put("NumeroExtrato",Integer.valueOf(LoadExtratoNumber(adCodigo.getText())));
        parametros.put("Mensagem", new AvisosMensagens().VerificaAniProprietario(adCodigo.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        parametros.put("Autenticacao",aut.doubleValue() == 0 ? "" : autenticacao);
        parametros.put("ShowSaldo", true);

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"jExtratoMeiaPagina", parametros);
            //new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Adiantamento ao Proprietário", pdfName);
    }

    private String LoadExtratoNumber(String rgprp) {
        String retorno = "0000000000";
        String loadSQL = "SELECT Count(registro) AS taut FROM propsaldo WHERE registro = ?";
        ResultSet loadRS = null;
        try {
            loadRS = conn.AbrirTabela(loadSQL, ResultSet.CONCUR_READ_ONLY,new Object[][] {{"string", rgprp}});
            while (loadRS.next()) {
                retorno += String.valueOf(loadRS.getInt("taut") + 1);
            }
            if (retorno.equalsIgnoreCase("0000000000")) retorno = "0000000001";
        } catch (SQLException SQLex) {}
        try { loadRS.close(); } catch (SQLException SQLex) {}
        return retorno.substring(retorno.length() - 10);
    }

    private void Distribuicao(String rgprp, String rgimv, String contrato, String lancamentos, String valor) {
        BigInteger aut = this.Autenticacao;
        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                    "contrato, valor, lancamentos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            String lanctos = lancamentos;
            String tlanctos = lanctos.replace("{{","").replace("}}","").replace("\"","");
            String[] alanctos = tlanctos.split(",");
            String alc1 = ""; try { alc1 = alanctos[0]; } catch (Exception e) {}
            String alc2 = ""; try { alc2 = alanctos[1]; } catch (Exception e) {}
            String alc3 = ""; try { alc3 = alanctos[2]; } catch (Exception e) {}
            String alc4 = ""; try { alc4 = alanctos[3]; } catch (Exception e) {}
            String alc5 = ""; try { alc5 = alanctos[4]; } catch (Exception e) {}
            String alc6 = ""; try { alc6 = alanctos[5]; } catch (Exception e) {}

            BigDecimal vrRecibo = new BigDecimal(valor);
            if (bAbateAvDeb) {
                vrRecibo = vrRecibo.subtract(new BigDecimal(LerValor.Number2BigDecimal(adDebValorTotal.getText())));
            }
            if (conn.ExecutarComando(caixaSQL, new Object[][] {
                    {"bigint", aut},
                    {"date", Dates.toSqlDate(DbMain.getDateTimeServer())},
                    {"string", VariaveisGlobais.usuario},
                    {"string", "DEB"},
                    {"string", "ADI"},
                    {"int", Integer.valueOf(rgprp)},
                    {"int",Integer.valueOf(rgimv)},
                    {"string", contrato},
                    {"decimal", vrRecibo},
                    {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{alc1, alc2, alc3, alc4,alc5,alc6}})}
            }) > 0) {
                for (classAdImoveis imv : adImoveis.getItems()) {
                    if (imv.getTag()) {
                        BigDecimal cm = RetornaComissaoImoveis(adCodigo.getText(), imv.getRgimv(), imv.getContrato(), Dates.DateFormata("dd-MM-yyyy", imv.getVencimento()));
                        String updateSQL = "UPDATE movimento SET ad_pag = ?, cm = ? WHERE rgprp = ? AND rgimv = ? AND contrato = ? AND dtvencimento = ? AND selected = true;";
                        conn.ExecutarComando(updateSQL, new Object[][] {
                                {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{adCodigo.getText(), aut, Dates.DateFormata("dd-MM-yyyy", DbMain.getDateTimeServer()), VariaveisGlobais.usuario}})},
                                {"decimal", cm},
                                {"string", adCodigo.getText()},
                                {"string", imv.getRgimv()},
                                {"string", imv.getContrato()},
                                {"date", Dates.toSqlDate(imv.getVencimento())},
                        });

                        Object[][] mRef = null;
                        try {
                            mRef = conn.LerCamposTabela(new String[] {"referencia"}, "movimento", "contrato = ? AND dtvencimento = ? AND selected = true",
                                        new Object[][] {
                                                {"string", imv.getContrato()},
                                                {"date", Dates.toSqlDate(imv.getVencimento())}
                            });
                         } catch (SQLException esx) { System.out.println(esx); }
                        if (mRef != null) {
                            updateSQL = "UPDATE descdif SET ad_pag = ? WHERE contrato = ? AND referencia = ? AND selected = true;";
                            conn.ExecutarComando(updateSQL, new Object[][] {
                                    {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{adCodigo.getText(), aut, Dates.DateFormata("dd-MM-yyyy", DbMain.getDateTimeServer()), VariaveisGlobais.usuario}})},
                                    {"string", imv.getContrato()},
                                    {"string", mRef[0][3]}
                            });

                            updateSQL = "UPDATE taxas SET ad_pag = ? WHERE contrato = ? AND referencia = ? AND selected = true;";
                            conn.ExecutarComando(updateSQL, new Object[][] {
                                    {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{adCodigo.getText(), aut, Dates.DateFormata("dd-MM-yyyy", DbMain.getDateTimeServer()), VariaveisGlobais.usuario}})},
                                    {"string", imv.getContrato()},
                                    {"string", mRef[0][3]}
                            });

                            if (bAbateAvDeb) {
                                for (classDebLista itens : adDebLista.getItems()) {
                                    updateSQL = "UPDATE avisos SET dtrecebimento = CURRENT_DATE, aut_rec = ?, usr_rec = ? WHERE id = ?";
                                    conn.ExecutarComando(updateSQL, new Object[][]{
                                            {"bigint",aut},
                                            {"string", VariaveisGlobais.usuario.toLowerCase()},
                                            {"int", itens.getId()}
                                    });
                                }
                                bAbateAvDeb = false;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LimpaTela() {
        adImoveis.getItems().clear();
        adValor.setText("0,00");
        adRecibo.getItems().clear();
        adReciboComissao.setText("0,00");
        adReciboLiquido.setText("0,00");
        adDebMsg.setText("");
        adDebValor.setText("0,00");
        adDebValorTotal.setText("0,00");
        avAdiantaEnable(false);
        adDebLista.getItems().clear();
    }

    private void AutocompletePr() {
        _possibleSuggestionsPrRegistro = new String[]{};
        _possibleSuggestionsPrNome = new String[]{};
        _possibleSuggestionsPr = new String[][]{};
        possibleSuggestionsPrRegistro = new HashSet<String>();
        possibleSuggestionsPrNome = new HashSet<String>();
        isSearchPrRegistro = true;
        isSearchPrNome = true;

        try {
            TextFields.bindAutoCompletion(adCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(adNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Proprietarios
        qSQL = "SELECT p_rgprp AS l_contrato, p_nome AS l_nome FROM proprietarios ORDER BY p_rgprp;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsPrRegistro = FuncoesGlobais.ArrayAdd(_possibleSuggestionsPrRegistro, qcontrato);
                possibleSuggestionsPrRegistro = new HashSet<>(Arrays.asList(_possibleSuggestionsPrRegistro));

                _possibleSuggestionsPrNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsPrNome, qnome);
                possibleSuggestionsPrNome = new HashSet<>(Arrays.asList(_possibleSuggestionsPrNome));

                _possibleSuggestionsPr = FuncoesGlobais.ArraysAdd(_possibleSuggestionsPr, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(adCodigo, possibleSuggestionsPrRegistro);
        TextFields.bindAutoCompletion(adNome, possibleSuggestionsPrNome);

        adCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                adCodigo.setText(null);
                adNome.setText(null);

                controllerPag.Formas_DisableAll();
                LimpaTela();
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = adCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsPr, 0, adCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchPrNome = false;
                        adNome.setText(_possibleSuggestionsPr[pos][1]);
                        isSearchPrNome = true;
                    }
                } else {
                    isSearchPrRegistro = false;
                    isSearchPrNome = true;
                }
            }
        });

        adNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsPr,1, adNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = adCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchPrNome && pcontrato != null) {
                    isSearchPrRegistro = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsPr,1,adNome.getText())) {
                        adCodigo.setText(_possibleSuggestionsPr[pos][0]);
                    }
                    isSearchPrRegistro = true;

                    // Preenche tabela com imóveis
                    ListaImoveis();
                    avAdiantaEnable(true);
                    FillAvAdianta();

                    // Se tiver email cadastrado
                    Object[][] email = null;
                    try {
                        email = conn.LerCamposTabela(
                                new String[] {"p_email"},
                                "proprietarios",
                                "p_rgprp::varchar = ?",
                                new Object[][] {{"string", adCodigo.getText()}}
                        );
                    } catch (Exception ex) {}
                    adEnviarEmail.setSelected(false);
                    if (email != null) {
                        if (!email[0][3].toString().trim().equalsIgnoreCase("")) {
                            adEnviarEmail.setStyle("-fx-text-fill: darkgreen;");
                        } else {
                            adEnviarEmail.setStyle("-fx-text-fill: black;");
                        }
                    } else {
                        adEnviarEmail.setStyle("-fx-text-fill: black;");
                    }
                } else {
                    isSearchPrRegistro = true;
                    isSearchPrNome = false;
                }
            }
        });
    }

    private void FillAvAdianta() {
        ObservableList<classDebLista> items = FXCollections.observableArrayList();
        String selectSQL = "SELECT id, texto, valor FROM avisos WHERE conta = 6 AND registro = ? AND dtrecebimento Is Null;";
        ResultSet rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", adCodigo.getText()}});
        try {
            int qid = 0; String qtexto = null; BigDecimal qvalor = new BigDecimal("0");
            while (rs.next()) {
                try { qid = rs.getInt("id"); } catch (SQLException exsql) {}
                try { qtexto = rs.getString("texto"); } catch (SQLException exsql) {}
                try { qvalor = rs.getBigDecimal("valor"); } catch (SQLException exsql) {}

                items.add(new classDebLista(qid, qtexto, qvalor));
            }
        } catch (SQLException sqlex) {}
        try { DbMain.FecharTabela(rs); } catch (Exception ex) {}

        adDebListaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        adDebListaId.setStyle( "-fx-alignment: CENTER; -fx-text-fill: darkred");

        adDebListaDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        adDebListaDesc.setStyle( "-fx-alignment: LEFT; -fx-text-fill: darkred");

        adDebListaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        adDebListaValor.setCellFactory((AbstractConvertCellFactory<classDebLista, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        adDebListaValor.setStyle( "-fx-alignment: CENTER-RIGHT; -fx-text-fill: darkred");

        adDebLista.setOnKeyPressed(event -> {
            if (adDebLista.getSelectionModel().isEmpty()) return;

            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este aviso?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Sim")) {
                String deleteSQL = "DELETE FROM avisos WHERE id = ? AND aut_rec IS NULL;";
                conn.ExecutarComando(deleteSQL, new Object[][]{{"int", adDebLista.getSelectionModel().getSelectedItem().getId()}});
                FillAvAdianta();
            }
        });
        if (!items.isEmpty()) adDebLista.setItems(items);

        adDebValorTotal.setText(LerValor.BigDecimalToCurrency(TotalizaAvAdianta()));
    }

    private BigDecimal TotalizaAvAdianta() {
        BigDecimal total = new BigDecimal("0");
        for (classDebLista item : adDebLista.getItems()) {
            total = total.add(item.getValor());
        }
        return total;
    }

    private void Deb() {
        try {adTpagtos.getChildren().remove(0);} catch (Exception e) {}
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Pagamentos.fxml"));
            Pane root = (Pane)loader.load();
            controllerPag = loader.getController();
            adTpagtos.getChildren().add(root);
            root.setLayoutX(0); root.setLayoutY(0);
        } catch (Exception e) {e.printStackTrace();}

        MaskFieldUtil.monetaryField(adValor);
        adValor.requestFocus();
        adValor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // gotfocus
                controllerPag.Formas_DisableAll();
            } else {
                adValor.selectAll();
                adValor.requestFocus();
                controllerPag.Formas_Disable(false);
            }
        });
        adValor.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB && LerValor.StringToFloat(adValor.getText()) >= 0) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja abater os avisos?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    bAbateAvDeb = true;
                    BigDecimal vrDebito = new BigDecimal(LerValor.Number2BigDecimal(adValor.getText())).subtract(TotalizaAvAdianta());
                    if (vrDebito.floatValue() < 0) {
                        Alert msg2 = new Alert(Alert.AlertType.INFORMATION);
                        msg2.setContentText("Valor do débito não pode ser negativo!");
                        msg2.showAndWait();
                        adValor.requestFocus();
                    } else {
                        controllerPag.Formas_Disable(false);
                        controllerPag.SetValor(vrDebito);
                    }
                    adTpagtos.requestFocus();
                } else {
                    bAbateAvDeb = false;
                    controllerPag.Formas_Disable(false);
                    controllerPag.SetValor(new BigDecimal(LerValor.Number2BigDecimal(adValor.getText())));
                }
            }
        });
    }

    private void ListaImoveis() {
        ObservableList<classAdImoveis> items = FXCollections.observableArrayList();
        String selectSQL = "SELECT i.i_rgimv as rgimv, l.l_contrato as contrato, " +
                "i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro as endereco FROM " +
                "imoveis i, locatarios l " +
                "INNER JOIN proprietarios p " +
                "ON p.p_rgprp::varchar = l.l_rgprp WHERE i.i_rgimv = l.l_rgimv AND " +
                "Upper(Trim(i.i_situacao)) = 'OCUPADO' AND " +
                "p.p_adianta = true AND i.i_rgprp = ?;";
        ResultSet rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", adCodigo.getText()}});
        try {
            String qrgimv = null; String qcontrato = null; String qendereco = null;
            while (rs.next()) {
                try { qrgimv = rs.getString("rgimv"); } catch (SQLException exsql) {}
                try { qcontrato = rs.getString("contrato"); } catch (SQLException exsql) {}
                try { qendereco = rs.getString("endereco"); } catch (SQLException exsql) {}

                String movimentoSQL = "SELECT dtvencimento FROM movimento WHERE aut_rec isNull AND ad_pag isNull AND rgimv = ? ORDER BY dtvencimento;";
                ResultSet moviRs = conn.AbrirTabela(movimentoSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", rs.getString("rgimv")}});
                try {
                    Date qvencimento = null;
                    while (moviRs.next()) {
                        try { qvencimento = moviRs.getDate("dtvencimento"); } catch (SQLException mvex) {}

                        BigDecimal qvrRecibo = UpdateCalculosImoveis(
                                adCodigo.getText(),
                                qrgimv,
                                qcontrato,
                                Dates.DateFormata("dd-MM-yyyy", qvencimento)
                        );
                        items.add(new classAdImoveis(false, qrgimv, qcontrato, qendereco, qvencimento, qvrRecibo));
                    }
                } catch (SQLException moviex) {}
                try { DbMain.FecharTabela(moviRs); } catch (Exception exsql) {}
            }
        } catch (SQLException sqlex) {}
        try { DbMain.FecharTabela(rs); } catch (Exception ex) {}

        Platform.runLater(() -> {
            adImoveisTag.setCellValueFactory(new PropertyValueFactory<classAdImoveis, Boolean>("tag"));
            final BooleanProperty selected = new SimpleBooleanProperty();
            adImoveisTag.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(Integer index) {
                    classAdImoveis tbvlinhas = ((classAdImoveis) adImoveis.getItems().get(index));
                    if (tbvlinhas.getValor().doubleValue() <= 0) {
                        tbvlinhas.setTag(false);
                    }
                    Platform.runLater(() -> TotalizaAdianta());
                    return tbvlinhas.isCheckedTag();
                }
            }));
        });

        adImoveisRgimv.setCellValueFactory(new PropertyValueFactory<>("rgimv"));
        adImoveisRgimv.setStyle( "-fx-alignment: CENTER; -fx-text-fill: darkred");

        adImoveisContrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        adImoveisContrato.setStyle( "-fx-alignment: CENTER; -fx-text-fill: darkred");

        adImoveisEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        adImoveisEndereco.setStyle( "-fx-alignment: CENTER-LEFT; -fx-text-fill: darkred");

        adImoveisVencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));
        adImoveisVencimento.setCellFactory((AbstractConvertCellFactory<classAdImoveis, Date>) value -> Dates.DateFormata("dd-MM-yyyy", value));
        adImoveisVencimento.setStyle( "-fx-alignment: CENTER; -fx-text-fill: darkred");

        adImoveisValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        adImoveisValor.setCellFactory((AbstractConvertCellFactory<classAdImoveis, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        adImoveisValor.setStyle( "-fx-alignment: CENTER-RIGHT; -fx-text-fill: darkred");

        if (!items.isEmpty()) adImoveis.setItems(items);
        adImoveis.setOnMouseClicked(event -> {
            //if (adImoveis.getSelectionModel().isSelected(adImoveis.getSelectionModel().getSelectedIndex())) return;

            Task task = ProcessaCampos();
            new Thread(task).start();
        });
    }

    private void TotalizaAdianta() {
        BigDecimal ta = new BigDecimal("0");
        for (classAdImoveis imv : adImoveis.getItems()) {
            if (imv.getTag()) ta = ta.add(imv.getValor());
        }
        adValor.setText(new DecimalFormat("#,##0.00").format(ta));
        adValor.setEditable(false);
        adValor.setDisable(!(ta.doubleValue() > 0));
        if (!adValor.isDisabled()) adValor.requestFocus();
    }

    private interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
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

    public Task ProcessaCampos() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(() -> {
                    adRecibo.getItems().clear();
                });

                // Pegar o mes de referencia do Vencimento no descdif
                String vcto = Dates.DateFormata("dd/MM/yyyy",adImoveis.getSelectionModel().getSelectedItem().getVencimento());
                String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));

                int linha = 0;
                List<classAdRecibo> data = new ArrayList<classAdRecibo>();

                String rgprp = null;

                // Movimento
                String sql = "SELECT * FROM movimento WHERE contrato = ? AND referencia = ?;";
                ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                        {"string", adImoveis.getSelectionModel().getSelectedItem().getContrato().trim()},
                        {"string", descdif_refer.trim()}
                });
                int recordCount = DbMain.RecordCount(resultSet);

                BigDecimal vrAluguel = new BigDecimal(0);
                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true; int qid = 0; String qtipo = "C";
                        String qdes = "ALUGUEL", qcota = "99/99";
                        String qvalor = "0,00"; String qvariavel = "mensal";

                        try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        //try {qdes = resultSet.getString("descricao");} catch (SQLException e) {}
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("mensal").toString(),"#,##0.00");} catch (SQLException e) {}

                        data.add(new classAdRecibo(qid,"movimento", qtag, qdes, qcota, new BigDecimal(LerValor.Number2BigDecimal(qvalor))));

                        try {vrAluguel = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}
                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // Desconto Diferença
                BigDecimal desal = new BigDecimal("0");
                BigDecimal difal = new BigDecimal("0");
                sql = "SELECT * FROM descdif WHERE contrato = ? AND referencia = ?;";
                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                        {"string", adImoveis.getSelectionModel().getSelectedItem().getContrato().trim()},
                        {"string", descdif_refer.trim()}
                });
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true; int qid = 0; String qtipo = "";
                        String qdes = "IPTU #" + ++linha, qcota = "99/99";
                        String qvalor = "0,00"; String qvariavel = "valor";

                        if (rgprp == null) {
                            try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                        }

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                        if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                            // Diferença
                            try {qdes = "Dif.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                        } else {
                            // Desconto
                            try {qdes = "Desc.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                        }
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}

                        data.add(new classAdRecibo(qid,"descdif", qtag, qdes, qcota, new BigDecimal(LerValor.Number2BigDecimal(qvalor))));

                        if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                            // Diferença
                            difal = difal.add(resultSet.getBigDecimal("valor"));
                        } else {
                            // Desconto
                            desal = desal.add(resultSet.getBigDecimal("valor"));
                        }

                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // IRRF
                BigDecimal irenda = new BigDecimal("0");
                try {
                    irenda = new Calculos.Irrf().Irrf(
                            rgprp,
                            adImoveis.getSelectionModel().getSelectedItem().getContrato().trim(),
                            descdif_refer.trim(),
                            vrAluguel,
                            difal,
                            desal
                    );
                    if (irenda.compareTo(BigDecimal.ZERO) == 1) {
                        data.add(new classAdRecibo(0,"irrf", false, "IRRF", "", irenda));
                        //data.add(new classAdRecibo("irrf", "", 0, true, "D", "IRRF", descdif_refer.trim(), LerValor.FormatPattern(irenda.toPlainString(), "#,##0.00"), false));
                    }
                } catch (Exception e) {}

                // Taxas
                sql = "SELECT * FROM taxas WHERE contrato = ? AND referencia = ?;";
                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                        {"string", adImoveis.getSelectionModel().getSelectedItem().getContrato().trim()},
                        {"string", descdif_refer.trim()}
                });
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true, qret = false; int qid = 0; String qtipo = "";
                        String qdes = "" , qpos = "", qcota = "";
                        String qvalor = "0,00"; String qvariavel = "valor";

                        // Ler o nome da taxa
                        String sWhere = null;
                        try {sWhere = "codigo = '" + resultSet.getString("campo") + "'";} catch (SQLException e) {}

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                        try {qdes = (qtipo.equalsIgnoreCase("D") ? "Desc." : "Dif." ) + (String)conn.LerCamposTabela(new String[] {"descricao"},"campos", sWhere)[0][3];} catch (SQLException e) {}
                        try {qpos = resultSet.getString("poscampo");} catch (SQLException e) {}
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}
                        try {qret = resultSet.getBoolean("retencao");} catch (SQLException e) {}

                        data.add(new classAdRecibo(qid,"taxas", qtag, qdes + " " + qpos, qcota, new BigDecimal(qvalor)));
                        //'data.add(new classAdRecibo("taxas", qvariavel, qid, qtag, qtipo, qdes + " " + qpos, qcota, qvalor, qret));
                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // Seguros
                sql = "SELECT * FROM seguros WHERE contrato = ? AND referencia = ?;";
                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                        {"string", adImoveis.getSelectionModel().getSelectedItem().getContrato().trim()},
                        {"string", descdif_refer.trim()}
                });
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true; int qid = 0; String qtipo = "C";
                        String qdes = "SEGURO", qcota = "99/99";
                        String qvalor = "0,00"; String qvariavel = "valor";

                        try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                        try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                        try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                        try {qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(),"#,##0.00");} catch (SQLException e) {}

                        data.add(new classAdRecibo(qid,"seguros", qtag, qdes, qcota, new BigDecimal(qvalor)));
                        //'data.add(new classAdRecibo("seguros", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));
                    }
                } catch (Exception e) {e.printStackTrace();}
                try { DbMain.FecharTabela(resultSet);} catch (Exception e) {}


                String[] ciptu = new Calculos.Iptu().Iptu(
                        adImoveis.getSelectionModel().getSelectedItem().getRgimv().trim(),
                        descdif_refer.trim()
                );
                int iptuId = 0;
                String iptuMes = null;
                String iptuRef = null;
                String vrIptu = null;
                if (ciptu != null) {
                    if (ciptu[0] != null) {
                        iptuId = Integer.valueOf(ciptu[0]);
                        iptuMes = ciptu[1];
                        iptuRef = ciptu[2];
                        vrIptu = LerValor.FormatPattern(ciptu[3], "#,##0.00");
                    }
                }
                if (vrIptu != null && !vrIptu.trim().equalsIgnoreCase("0,00")) {
                    data.add(new classAdRecibo(iptuId,"iptu", false, "IPTU", iptuRef, new BigDecimal(vrIptu)));
                    //'data.add(new classAdRecibo("iptu",iptuMes, iptuId, true, "C", "IPTU", iptuRef, vrIptu, false));
                }

                Platform.runLater(() -> {
                    adReciboTag.setCellValueFactory(new PropertyValueFactory<classAdRecibo, Boolean>("tag"));
                    adReciboTag.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                        @Override
                        public ObservableValue<Boolean> call(Integer index) {
                            classAdRecibo tbvlinhas = ((classAdRecibo) adRecibo.getItems().get(index));
                            String uSql = "UPDATE " + tbvlinhas.getTabela() + " SET selected = " + adRecibo.getItems().get(index).isCheckedTag().get() + " WHERE id = " + tbvlinhas.getId() + ";";
                            try {conn.ExecutarComando(uSql);} catch (Exception e) {}

                            BigDecimal vrRecibo = UpdateCalculos(
                                    adCodigo.getText(),
                                    adImoveis.getItems().get(adImoveis.getSelectionModel().getFocusedIndex()).getRgimv(),
                                    adImoveis.getItems().get(adImoveis.getSelectionModel().getFocusedIndex()).getContrato(),
                                    Dates.DateFormata("dd-MM-yyyy",adImoveis.getItems().get(adImoveis.getSelectionModel().getFocusedIndex()).getVencimento())
                            );

                            Platform.runLater(() -> TotalizaAdianta());
                            //if (vrRecibo.doubleValue() <= 0) adImoveis.getItems().get(adImoveis.getSelectionModel().getSelectedIndex()).setTag(false);
                            return adRecibo.getItems().get(index).isCheckedTag();
                        }
                    }));

                    adReciboDescricao.setCellValueFactory(new PropertyValueFactory<classAdRecibo, String>("descricao"));

                    adReciboCotaParcela.setCellValueFactory(new PropertyValueFactory<classAdRecibo, String>("cotaparc"));
                    adReciboCotaParcela.setStyle( "-fx-alignment: CENTER;");

                    adReciboValor.setCellValueFactory(new PropertyValueFactory<classAdRecibo, BigDecimal>("valor"));
                    adReciboValor.setCellFactory((AbstractConvertCellFactory<classAdRecibo, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
                    adReciboValor.setStyle( "-fx-alignment: CENTER-RIGHT; -fx-text-fill: darkred");

                    if (!data.isEmpty()) adRecibo.setItems(FXCollections.observableArrayList(data));
                });
                return true;
            }
        };
    }

    public BigDecimal[] ProcessarCampos(String rgprp, String rgimv, String contrato, String vctos) {
        Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vctos, "MM-dd-yyyy"), Dates.StringtoDate(Dates.DatetoString(DbMain.getDateTimeServer()), "dd-MM-yyyy"));

        return new BigDecimal[]{calc.getAluguel(), calc.getDiferenca(), calc.getDescontos(), calc.getIrenda(), calc.getIptu(), calc.getSeguro(), calc.Multa(), calc.Juros(), calc.Correcao(), calc.Expediente()};
    }

    private BigDecimal UpdateCalculos(String rgprp, String rgimv, String contrato, String vcto) {
        Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vcto,"dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(DbMain.getDateTimeServer()),"dd-MM-yyyy"));

        // Calcula comissao
        BigDecimal baseComissao = calc.getAluguel().subtract(calc.getDescontos()).add(calc.getDiferenca());
        BigDecimal Comissao = new BigDecimal(baseComissao.doubleValue() * (VariaveisGlobais.co / 100));
        BigDecimal vrTotal = calc.TotalRecibo(true).subtract(Comissao);

        Platform.runLater(() -> adImoveis.getItems().get(adImoveis.getSelectionModel().getSelectedIndex()).setValor(vrTotal));

        adReciboComissao.setText(new DecimalFormat("#,##0.00").format(Comissao));
        adReciboLiquido.setText(new DecimalFormat("#,##0.00").format(vrTotal));
        return vrTotal;
    }

    private BigDecimal UpdateCalculosImoveis(String rgprp, String rgimv, String contrato, String vcto) {
            Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vcto,"dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(DbMain.getDateTimeServer()),"dd-MM-yyyy"));
            BigDecimal baseComissao = calc.getAluguel().subtract(calc.getDescontos()).add(calc.getDiferenca());
            BigDecimal Comissao = new BigDecimal(baseComissao.doubleValue() * (VariaveisGlobais.co / 100));
            return calc.TotalRecibo(true).subtract(Comissao);
    }

    private BigDecimal RetornaComissaoImoveis(String rgprp, String rgimv, String contrato, String vcto) {
            Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vcto,"dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(DbMain.getDateTimeServer()),"dd-MM-yyyy"));
            BigDecimal baseComissao = calc.getAluguel().subtract(calc.getDescontos()).add(calc.getDiferenca());
            BigDecimal Comissao = new BigDecimal(baseComissao.doubleValue() * (VariaveisGlobais.co / 100));
            return Comissao;
    }

    private Object[] pExtratoPreview(String contrato, String referencia, Object[] returns) {
        BigDecimal ttCR = (BigDecimal) returns[1];
        BigDecimal ttDB = (BigDecimal) returns[2];

        List<jExtrato> lista = (List) returns[0];
        jExtrato Extrato;

        String movimentoSQL = "select rgprp, rgimv, contrato, cota, mensal, dtvencimento, cm, referencia from movimento where ad_pag is Null AND contrato = ? AND referencia = ?";
        ResultSet rs = conn.AbrirTabela(movimentoSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}, {"string", referencia}});
        try {
            while (rs.next()) {
                Object[][] endImovel = null;
                try {
                    endImovel = conn.LerCamposTabela(new String[]{"i_end", "i_num", "i_cplto"}, "imoveis", "i_rgimv = '" + rs.getString("rgimv") + "'");
                } catch (Exception e) {
                }
                String linha = "<b>" + rs.getString("rgimv") + "</b> - " + endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim();
                Extrato = new jExtrato(Descr(linha), null, null);
                lista.add(Extrato);

                Object[][] nomeLoca = null;
                try {
                    nomeLoca = conn.LerCamposTabela(new String[]{"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"}, "locatarios", "l_contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {
                }
                Extrato = new jExtrato(Descr((String) nomeLoca[0][3]), null, null);
                lista.add(Extrato);

                Extrato = new jExtrato(Descr("[" + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")) + "]"), null, null);
                lista.add(Extrato);

                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try {
                    com = rs.getBigDecimal("cm");
                } catch (Exception ex) {
                    com = new BigDecimal("0");
                }
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("ALU") + "  " + rs.getBigDecimal("mensal") + "  " + palu), alu, null);
                lista.add(Extrato);
                ttCR = ttCR.add(alu);

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    dfsql = "select * from descdif where ad_pag is Null AND contrato = ? and referencia = ?";
                    ResultSet dfrs = conn.AbrirTabela(dfsql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}, {"string", referencia}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try {
                                dfcom = dfrs.getBigDecimal("valor");
                            } catch (Exception ex) {
                                com = new BigDecimal("0");
                            }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            Extrato = new jExtrato(Descr(dftipostr + VariaveisGlobais.contas_ca.get("ALU") + " " + dfrs.getString("descricao")), dftipo.trim().equalsIgnoreCase("C") ? dfCR : null, dftipo.trim().equalsIgnoreCase("D") ? dfDB : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {
                        DbMain.FecharTabela(dfrs);
                    } catch (Exception ex) {
                    }
                }

                // Comissão
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COM") + "  " + rs.getBigDecimal("cm")), null, com);
                lista.add(Extrato);
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try {
                        pirvr = rs.getBigDecimal("ir");
                    } catch (Exception ex) {
                        pirvr = new BigDecimal("0");
                    }

                    if (pirvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IRF")), null, pirvr);
                        lista.add(Extrato);
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    sgsql = "select * from seguros where ad_pag is Null AND contrato = ? and referencia = ?";
                    ResultSet sgrs = conn.AbrirTabela(sgsql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}, {"string", referencia}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            BigDecimal seg = new BigDecimal("0");
                            try {
                                seg = sgrs.getBigDecimal("valor");
                            } catch (Exception ex) {
                                seg = new BigDecimal("0");
                            }

                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("SEG") + "  " + sgrs.getString("cota")), seg, sgrs.getBoolean("retencao") ? seg : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {
                        DbMain.FecharTabela(sgrs);
                    } catch (Exception ex) {
                    }
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
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
                    txsql = "select * from taxas where ad_pag is Null AND contrato = ? and referencia = ?";
                    ResultSet txrs = conn.AbrirTabela(txsql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}, {"string", referencia}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            BigDecimal txcom = new BigDecimal("0");
                            try {
                                txcom = txrs.getBigDecimal("valor");
                            } catch (Exception ex) {
                                com = new BigDecimal("0");
                            }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            String txdecr = conn.LerCamposTabela(new String[]{"descricao"}, "campos", "codigo = '" + txrs.getString("campo") + "'")[0][3].toString();
                            Extrato = new jExtrato(Descr(txdecr + "  " + txrs.getString("cota")), (txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : null), (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : null));
                            lista.add(Extrato);
                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) { System.out.println(e); }
                    try { DbMain.FecharTabela(txrs); } catch (Exception ex) { }
                }

                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (SQLException sqlex) { System.out.println(sqlex); }

        return new Object[] {lista, ttCR, ttDB};
    }

    private void pExtratoPreviewRodape(Object[] returns) {
        BigDecimal ttCR = (BigDecimal) returns[1];
        BigDecimal ttDB = (BigDecimal) returns[2];

        List<jExtrato> lista = (List) returns[0];
        jExtrato Extrato;

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where registro = '%s' and (aut_rec = 0 or aut_rec Is Null) and conta = 6 order by 1;";
        avsql = String.format(avsql, adCodigo.getText());
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (avrs.next()) {
                Font font = new Font("SansSerif",Font.PLAIN,8);
                Canvas c = new Canvas();
                FontMetrics fm = c.getFontMetrics(font);
                String aLinhas[] = WordWrap.wrap(avrs.getString("texto"), 230, fm).split("\n");
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

/*
        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);
*/

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Créditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de Déditos</b></font>"), null, ttDB);
        lista.add(Extrato);

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        String tDesc = "";
        if (ttSld.floatValue() > 0) {
            tDesc = "<font color=blue><b>Líquido a Receber</b></font>";
        } else {
            tDesc = "<font color=red><b>Líquido a Receber</b></font>";
        }
        Extrato = new jExtrato(Descr(tDesc), ttSld.floatValue() > 0 ? ttSld : null, ttSld.floatValue() < 0 ? ttSld : null);
        lista.add(Extrato);

        Extrato = new jExtrato(null,  null, null);
        lista.add(Extrato);

        // Dados Bancários para Depósito
        String sql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        sql = String.format(sql,adCodigo.getText());
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
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
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Agência: " + bancoPrin[1] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>C/C: " + bancoPrin[2] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Favorecido: " + (bancoPrin[3].equalsIgnoreCase("null") ? "O Próprio" : bancoPrin[3])  + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
        }

/*
        // complementa com linhas em branco para preencher a página
        int npag = lista.size() % 32;
        for (int i=1;i<=32-npag;i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }
*/

        //Report(lista, new BigInteger("0"));
        PrintExtrato(lista, new BigInteger("0"));
    }

    private void PrintExtrato(List<jExtrato> lista, BigInteger aut) {
        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = aut.toString();
        if (aut.doubleValue() >= 0) {
            autenticacao = aut.floatValue() != 0 ? dadm.get("marca") +
                    FuncoesGlobais.StrZero(String.valueOf(aut), 10) +
                    Dates.DateFormata("ddMMyyyyHHmm", DbMain.getDateTimeServer()) +
                    new DecimalFormat("#,##0.00").format(controllerPag.getValorPago()).replace(",", "").replace(".", "") +
                    " " + VariaveisGlobais.usuario : "";
        }

        List<Extrato> listas = new ArrayList<>();
        Extrato bean1 = new Extrato();
        bean1.setnomeProp(adCodigo.getText() + " - " + adNome.getText());
        bean1.setextratoNumero(LoadExtratoNumber(adCodigo.getText()));
        bean1.setlogoLocation(dadm.get("logo"));
        bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(adCodigo.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        bean1.setautentica(autenticacao);
        //bean1.setbarras(aut.toString());
        int n = 0;
        for (int i = 0; i <= lista.size() - 1; i++) {
            jExtrato item = lista.get(i);

            if (i % 40 == 0 && i > 0) {
                listas.add(bean1);
                bean1 = new Extrato();
                bean1.setnomeProp(adCodigo.getText() + " - " + adNome.getText());
                bean1.setextratoNumero(LoadExtratoNumber(adCodigo.getText()));
                bean1.setlogoLocation(dadm.get("logo"));
                bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(adCodigo.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
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

        Report2(listas, aut);
    }

    private void Report2(List<Extrato> lista, BigInteger aut) {
        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = aut.toString();
        if (aut.doubleValue() >= 0) {
            autenticacao = aut.floatValue() != 0 ? dadm.get("marca") +
                    FuncoesGlobais.StrZero(String.valueOf(aut), 10) +
                    Dates.DateFormata("ddMMyyyyHHmm", DbMain.getDateTimeServer()) +
                    new DecimalFormat("#,##0.00").format(controllerPag.getValorPago()).replace(",", "").replace(".", "") +
                    " " + VariaveisGlobais.usuario : "";
        }
        Map parametros = new HashMap();
        parametros.put("Logo", dadm.get("logo"));
        parametros.put("rgprp", adCodigo.getText());
        parametros.put("nomeProp", adNome.getText());
        parametros.put("NumeroExtrato",Integer.valueOf(LoadExtratoNumber(adCodigo.getText())));
        parametros.put("Mensagem", new AvisosMensagens().VerificaAniProprietario(adCodigo.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        parametros.put("Autenticacao",aut.doubleValue() == 0 ? "" : autenticacao);
        parametros.put("ShowSaldo", true);

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Extrato_artvida", parametros);
        //new toPrint(pdfName,"LASER","INTERNA");
        new PdfViewer("Adiantamento ao Proprietário", pdfName);
    }
}
