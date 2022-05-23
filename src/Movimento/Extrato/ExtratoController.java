package Movimento.Extrato;

import Calculos.AutentMult;
import Calculos.AvisosMensagens;
import Calculos.Multas;
import Calculos.PegaDivisao;
import Classes.jExtrato;
import Classes.paramEvent;
import Funcoes.Collections;
import Funcoes.*;
import Gerencia.divSec;
import Movimento.Extrato.Depositos.Depositos;
import Movimento.Extrato.Depositos.cDepositos;
import Objetos.JasperViewerFX.JasperViewerFX;
import PagRec.PagamentosController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
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

public class ExtratoController implements Initializable {
    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    PagamentosController controller;
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<ExtratoBloqClass> eBloqueados;
    @FXML private TableColumn<ExtratoBloqClass, String> eBloqTipo;
    @FXML private TableColumn<ExtratoBloqClass, String> eBloqRgImv;
    @FXML private TableColumn<ExtratoBloqClass, String> eBloqNome;
    @FXML private TableColumn<ExtratoBloqClass, String> eBloqVencto;

    @FXML private Pane ePagtos;
    @FXML private TextField eCodigo;
    @FXML private TextField eNome;
    @FXML private AnchorPane eExtratoPdf;

    @FXML private ListView<ExtPagAnt> extPagAnt;
    @FXML private Button btnDepositos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDepositos.setOnAction(event -> {
            eCodigo.setDisable(true);
            eNome.setDisable(true);

            Depositos dialog = new Depositos();
            Optional<cDepositos> result = dialog.Depositos();
            result.ifPresent(b -> {
                eCodigo.setText(b.getRgprp());
                eNome.setText(b.getNome());

                pExtrato(new BigInteger("0"));
                PagAnteriores(b.getRgprp());
            });

            eCodigo.setDisable(false);
            eNome.setDisable(false);
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Pagamentos.fxml"));
            Pane root = (Pane)loader.load();
            controller = (PagamentosController) loader.getController();
            ePagtos.getChildren().add(root);
            root.setLayoutX(0); root.setLayoutY(0);
        } catch (Exception e) {e.printStackTrace();}

        AutocompleteContratoNome();

        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam.length == 0) {
                // Cancelar Recebimento
                Platform.runLater(() -> eCodigo.requestFocus());
            }
            if (event.sparam.length > 0) {
                if (event.sparam[0] != null) {
                    // Imprimir Recebimento
                    String[][] Lancamentos = (String[][]) event.sparam[1];
                    BigInteger aut = conn.PegarAutenticacao();

                    pExtrato(aut);
                    Baixa(aut, Lancamentos);
                    eCodigo.requestFocus();
                } else {
                    Platform.runLater(() -> eCodigo.requestFocus());
                }
            } else {
                Platform.runLater(() -> eCodigo.requestFocus());
            }
        });

        Platform.runLater(() -> eCodigo.requestFocus());
    }

    private void PagAnteriores(String rgprp) {
        ObservableList<ExtPagAnt> items = FXCollections.observableArrayList();
        String sdaSQL = "select aut, datahora, valor, logado from caixa where rgprp::varchar = ? AND operacao = 'DEB' AND documento = 'EXT' order by aut;";
        ResultSet pgaRS = conn.AbrirTabela(sdaSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", eCodigo.getText()}});
        try {
            float qaut = 0; String qvalor = null;
            Date qdata = null; String qlogado = null;
            while (pgaRS.next()) {
                try { qdata = pgaRS.getDate("datahora"); } catch (SQLException dex) {}
                try { qvalor = pgaRS.getString("valor"); } catch (SQLException bex) {}
                try { qaut = pgaRS.getFloat("aut"); } catch (SQLException fex) {}
                try { qlogado = pgaRS.getString("logado"); } catch (SQLException sex) {}
                items.add(new ExtPagAnt(qdata, qvalor, qaut, qlogado));
            }
        } catch (SQLException e) {}
        try {DbMain.FecharTabela(pgaRS);} catch (Exception e) {}
        extPagAnt.setItems(items);
    }

    private void Report(List<jExtrato> lista, BigInteger aut) {
        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = aut.floatValue() != 0 ? dadm.get("marca") +
                FuncoesGlobais.StrZero(String.valueOf(aut),10) +
                Dates.DateFormata("ddMMyyyyHHmm", DbMain.getDateTimeServer()) +
                new DecimalFormat("#,##0.00").format(controller.getValorPago()).replace(",","").replace(".","") +
                " " + VariaveisGlobais.usuario : "";
        Map parametros = new HashMap();
        parametros.put("Logo", dadm.get("logo"));
        parametros.put("rgprp", eCodigo.getText());
        parametros.put("nomeProp", eNome.getText());
        parametros.put("NumeroExtrato",Integer.valueOf(LoadExtratoNumber(eCodigo.getText())));
        parametros.put("Mensagem", new AvisosMensagens().VerificaAniProprietario(eCodigo.getText()) ? "Este Ã© o mÃªs do seu aniversÃ¡rio. PARABÃ‰NS!" : "");
        parametros.put("Autenticacao",autenticacao);
        parametros.put("ShowSaldo", true);

        if (aut.intValue() == 0) {
            JasperPrint jasperPrint = null;
            try {
                JRDataSource jrds = new JRBeanCollectionDataSource(lista);

                String sql = "SELECT p_nome, p_bancos FROM proprietarios WHERE (exclusao is null) and p_rgprp = '%s';";
                sql = String.format(sql, eCodigo.getText());
                ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                String nomeProp = null;
                String banco = null;
                try {
                    rs.next();
                    nomeProp = rs.getString("p_nome");
                    banco = rs.getString("p_bancos");
                } catch (SQLException ex) {
                }
                try {
                    rs.close();
                } catch (Exception ex) {
                }

                String[] bancos = null;
                String[] bancoPrin = null;
                if (banco != null) {
                    bancos = banco.split(";");
                }
                if (bancos != null) {
                    bancoPrin = bancos[0].split(",");
                }

                String reportFileName = System.getProperty("user.dir") + "\\Reports\\jExtrato.jasper";
                JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportFileName);
                jasperPrint = JasperFillManager.fillReport(reporte, parametros, jrds);
            } catch (JRException e) {
                e.printStackTrace();
            }

            String bStyle = "-fx-background-color: cornsilk;";
            bStyle += "-fx-background-radius: 5 5 5 5;";
            bStyle += "-fx-border-color: black;";
            bStyle += "-fx-border-radius: 5 5 5 5;";
            bStyle += "-fx-border-width: 1;";
            JasperViewerFX jrv = new JasperViewerFX((int) eExtratoPdf.getWidth(), (int) eExtratoPdf.getHeight(), bStyle, lista, parametros);
            jrv.viewReport(jasperPrint);
            eExtratoPdf.getChildren().add(jrv);
        } else {
            String pdfName = new PdfViewer().GeraPDFTemp(lista,"jExtratoMeiaPagina", parametros);
            new toPrint(pdfName,"LASER","INTERNA");
            // new PdfViewer("Extrato do ProprietÃ¡rio", pdfName);
        }
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

    private void AutocompleteContratoNome() {
        ResultSet imv = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios WHERE exclusao is null ORDER BY p_rgprp;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("p_rgprp");} catch (SQLException e) {}
                try {qnome = imv.getString("p_nome");} catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(eCodigo, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(eNome, possibleSuggestionsNome);

        eCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                eCodigo.setText(null);
                eNome.setText(null);

                // Botoes

                controller.Formas_DisableAll();
                try {eExtratoPdf.getChildren().remove(0);} catch (IndexOutOfBoundsException iex) {}
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = eCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, eCodigo.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        Platform.runLater(() -> eNome.setText(_possibleSuggestions[pos][1]));
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        eNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, eNome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = eCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,eNome.getText())) {
                        eCodigo.setText(_possibleSuggestions[pos][0]);
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }
                pExtrato(new BigInteger("0"));
                PagAnteriores(eCodigo.getText());
            }
        });
    }

    private void Bloqueados(ObservableList<ExtratoBloqClass> data) {
        eBloqTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        eBloqTipo.setStyle("-fx-alignment: CENTER;");
        eBloqRgImv.setCellValueFactory(new PropertyValueFactory<>("rgimv"));
        eBloqRgImv.setStyle("-fx-alignment: CENTER;");
        eBloqNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        eBloqNome.setStyle("-fx-alignment: CENTER-LEFT;");
        eBloqVencto.setCellValueFactory(new PropertyValueFactory<>("vecto"));
        eBloqVencto.setStyle("-fx-alignment: CENTER;");
        eBloqueados.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        eBloqueados.setItems(data);
    }

    private void pExtrato(BigInteger aut) {
        ObservableList<ExtratoBloqClass> bloqdata = FXCollections.observableArrayList();

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM propsaldo Where registro = '%s' and aut_pag is not null AND aut_pag[1][2] is null;";
        saSql = String.format(saSql,eCodigo.getText());
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
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
        List<divSec> dPrin = new PegaDivisao().PegaDivisoes(eCodigo.getText());

        // Pegar as divisÃµes secundÃ¡rias
        List<divSec> dSec = new PegaDivisao().PegaDivSecundaria(eCodigo.getText());
        String sDivWhere = "";
        for (divSec campo : dSec) {
            sDivWhere += "select * from (select *, generate_subscripts(aut_pag,1) as pos, generate_subscripts(reserva,1) as rpos from movimento where rgprp = '" + campo.getRgprp() +
                    "' and rgimv = '" + campo.getRgimv() + "' and aut_rec <> 0) as foo where aut_pag[pos][1] = '" + campo.getRgprp() +
                    "' and aut_pag[pos][2] is null and (reserva is null or reserva[rpos][1] = '" + VariaveisGlobais.usuario + "') union all ";
/*
            sDivWhere += "select * from (select *, generate_subscripts(aut_pag,1) as pos, generate_subscripts(reserva,1) as rpos from movimento where rgprp = '" + campo.getRgprp() +
                    "' and rgimv = '" + campo.getRgimv() + "' and aut_rec <> 0) as foo where aut_pag[pos][1] = '" + eCodigo.getText() +
                    "' and aut_pag[pos][2] is null and (reserva is null or reserva[rpos][1] = '" + VariaveisGlobais.usuario + "') union all ";
*/
        }
        if (sDivWhere.length() > 0) sDivWhere = sDivWhere.substring(0, sDivWhere.length() - 10);

        // Aqui pega os recibos recebidos e nÃ£o pagos
        String sql = "";
        if (sDivWhere.length() == 0) {
            sql = "select * from movimento where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
            sql = String.format(sql,eCodigo.getText(), eCodigo.getText());
        } else {
            sql = "select *, 0 as pos, 0 as rpos from movimento where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
            sql = String.format(sql,eCodigo.getText(), eCodigo.getText(), sDivWhere);
        }

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

                // Teste de GravaÃ§Ã£o de uma variavel modo Read
                Reserva(rs.getInt("id"), rs.getString("reserva"), "movimento");

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/DiferenÃ§a
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    if (sDivWhere.length() == 0) {
                        dfsql = "select * from descdif where rgprp = '%s' and aut_rec <> 0 and ad_pag is null and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        dfsql = String.format(dfsql,eCodigo.getText(), eCodigo.getText());
                    } else {
                        dfsql = "select *, 0 as pos, 0 as rpos from descdif where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        dfsql = String.format(dfsql,eCodigo.getText(), eCodigo.getText(), sDivWhere.replace("movimento","descdif"));
                    }
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "DiferenÃ§a de ";

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

                // Comissão
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
                    if (sDivWhere.length() == 0) {
                        sgsql = "select * from seguros where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        sgsql = String.format(sgsql,eCodigo.getText(), eCodigo.getText());
                    } else {
                        sgsql = "select *, 0 as pos, 0 as rpos from seguros where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        sgsql = String.format(sgsql,eCodigo.getText(), eCodigo.getText(), sDivWhere.replace("movimento","seguros"));
                    }
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

                            // Lembrar retenÃ§Ã£o
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
                    if (sDivWhere.length() == 0) {
                        txsql = "select * from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        txsql = String.format(txsql,eCodigo.getText(), eCodigo.getText());
                    } else {
                        txsql = "select *, 0 as pos, 0 as rpos from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        txsql = String.format(txsql,eCodigo.getText(), eCodigo.getText(), sDivWhere.replace("movimento","taxas"));
                    }
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

                // CORREÃ‡ÃƒO
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
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where registro = '%s' and aut_rec <> 0 and aut_pag is not null and aut_pag[1][2] is null and conta = 1 and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 1;";
        avsql = String.format(avsql, eCodigo.getText());
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

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Créditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de Débitos</b></font>"), null, ttDB);
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

        // Dados BancÃ¡rios para DepÃ³sito
        sql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        sql = String.format(sql,eCodigo.getText());
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
            try { Extrato = new jExtrato(Descr("<font color=blue><b>AgÃªncia: " + bancoPrin[1] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>C/C: " + bancoPrin[2] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Favorecido: " + (bancoPrin[3].equalsIgnoreCase("null") ? "O PrÃ³prio" : bancoPrin[3])  + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
        }

        // complementa com linhas em branco para preencher a pÃ¡gina
        int npag = lista.size() % 32;
        for (int i=1;i<=(32 - npag);i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }

        if (aut.floatValue() == 0) {
            if (ttSld.floatValue() > 0) {
                controller.Formas_Disable(false);
                controller.SetValor(ttSld);
            } else {
                controller.Formas_Disable(true);
                controller.SetValor(ttSld);
            }
        }

        // Mostra Bloqueados
        Bloqueados(bloqdata);

        Report(lista, aut);
    }

    private String Descr(String desc) { return "<html>" + desc + "</html>"; }

    private void Baixa(BigInteger aut, String[][] lancamentos) {
        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, contrato," +
                    "valor, lancamentos) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";
            String lanctos = DbMain.GeraLancamentosArray(lancamentos);
            caixaSQL = String.format(caixaSQL,
                    aut,
                    new java.util.Date(),
                    VariaveisGlobais.usuario,
                    "DEB",
                    "EXT",
                    eCodigo.getText(), "0","",
                    LerValor.BigDecimalToCurrency(controller.getValorPago()),
                    lanctos
            );
            conn.ExecutarComando(caixaSQL);
        } catch (Exception e) { e.printStackTrace(); }

        // Pegar as percentagens do principal
        List<divSec> dPrin = new PegaDivisao().PegaDivisoes(eCodigo.getText());

        // Pegar as divisÃµes secundÃ¡rias
        List<divSec> dSec = new PegaDivisao().PegaDivSecundaria(eCodigo.getText());
        String sDivWhere = "";
        for (divSec campo : dSec) {
            sDivWhere += "select * from (select *, generate_subscripts(aut_pag,1) as pos, generate_subscripts(reserva,1) as rpos from movimento where rgprp = '" + campo.getRgprp() +
                    "' and rgimv = '" + campo.getRgimv() + "' and aut_rec <> 0) as foo where aut_pag[pos][1] = '" + eCodigo.getText() +
                    "' and aut_pag[pos][2] is null and (reserva is null or reserva[rpos][1] = '" + VariaveisGlobais.usuario + "') union all ";
        }
        if (sDivWhere.length() > 0) sDivWhere = sDivWhere.substring(0, sDivWhere.length() - 10);

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        if (sDivWhere.length() == 0) {
            sql = "select * from movimento where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
            sql = String.format(sql,eCodigo.getText(), eCodigo.getText());
        } else {
            sql = "select *, 0 as pos, 0 as rpos from movimento where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
            sql = String.format(sql,eCodigo.getText(), eCodigo.getText(), sDivWhere);
        }

        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                // Movimento
                Object[][] mAut = new AutentMult().PegaAutentMult_Mov(rs.getString("rgprp"),
                        rs.getString("rgimv"),
                        "movimento",
                        rs.getString("dtvencimento"),
                        rs.getString("referencia")
                );

                // Gravação Movimento
                mAut = new AutentMult().UpgradeAutent(mAut, rs.getString("rgprp").toString() ,aut.intValue(),new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario );
                String sAut = new AutentMult().ObjectArrays2String(mAut);
                //System.out.println(sAut);
                conn.ExecutarComando("UPDATE movimento SET aut_pag = '" + sAut + "' WHERE id = " + rs.getInt("id") );

                // Desconto/Diferença
                {
                    String dfsql = "";
                    if (sDivWhere.length() == 0) {
                        dfsql = "select * from descdif where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        dfsql = String.format(dfsql,eCodigo.getText(), eCodigo.getText());
                    } else {
                        dfsql = "select *, 0 as pos, 0 as rpos from descdif where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        dfsql = String.format(dfsql,eCodigo.getText(), eCodigo.getText(), sDivWhere.replace("movimento","descdif"));
                    }
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            Object[][] dfAut = new AutentMult().PegaAutentMult_Taxas(rs.getString("rgprp"),
                                    rs.getString("rgimv"),
                                    "descdif",
                                    rs.getString("referencia")
                            );

                            dfAut = new AutentMult().UpgradeAutent(dfAut, rs.getString("rgprp").toString() ,aut.intValue(),new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario );
                            String sdfAut = new AutentMult().ObjectArrays2String(dfAut);
                            //System.out.println(sdfAut);
                            conn.ExecutarComando("UPDATE descdif SET aut_pag = '" + sdfAut + "' WHERE id = " + dfrs.getInt("id") );
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Seguros
                {
                    String sgsql = "";
                    if (sDivWhere.length() == 0) {
                        sgsql = "select * from seguros where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        sgsql = String.format(sgsql,eCodigo.getText(), eCodigo.getText());
                    } else {
                        sgsql = "select *, 0 as pos, 0 as rpos from seguros where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        sgsql = String.format(sgsql,eCodigo.getText(), eCodigo.getText(), sDivWhere.replace("movimento","seguros"));
                    }
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }
                            Object[][] sgAut = new AutentMult().PegaAutentMult_Taxas(rs.getString("rgprp"),
                                    rs.getString("rgimv"),
                                    "seguros",
                                    rs.getString("referencia")
                            );

                            sgAut = new AutentMult().UpgradeAutent(sgAut, rs.getString("rgprp").toString() ,aut.intValue(),new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario );
                            String ssgAut = new AutentMult().ObjectArrays2String(sgAut);
                            //System.out.println(ssgAut);
                            conn.ExecutarComando("UPDATE seguros SET aut_pag = '" + ssgAut + "' WHERE id = " + sgrs.getInt("id") );
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

                // Taxas
                {
                    String txsql = "";
                    if (sDivWhere.length() == 0) {
                        txsql = "select * from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        txsql = String.format(txsql,eCodigo.getText(), eCodigo.getText());
                    } else {
                        txsql = "select *, 0 as pos, 0 as rpos from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        txsql = String.format(txsql,eCodigo.getText(), eCodigo.getText(), sDivWhere.replace("movimento","taxas"));
                    }
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }
                            Object[][] txAut = new AutentMult().PegaAutentMult_Taxas(rs.getString("rgprp"),
                                    rs.getString("rgimv"),
                                    "taxas",
                                    rs.getString("referencia")
                            );

                            txAut = new AutentMult().UpgradeAutent(txAut, rs.getString("rgprp").toString() ,aut.intValue(),new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario );
                            String stxAut = new AutentMult().ObjectArrays2String(txAut);
                            //System.out.println(stxAut);
                            conn.ExecutarComando("UPDATE taxas SET aut_pag = '" + stxAut + "' WHERE id = " + txrs.getInt("id") );
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }
            }
        } catch (SQLException ex) {}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select id, registro, tipo, texto, valor, dtrecebimento, aut_rec from avisos where registro = '%s' and aut_rec <> 0 and aut_pag is not null and aut_pag[1][2] is null and conta = 1  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 1;";
        avsql = String.format(avsql, eCodigo.getText());
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (avrs.next()) {
                Object[][] avAut = new AutentMult().PegaAutentMult_Avi(avrs.getString("registro"),
                        "avisos",
                        avrs.getInt("aut_rec")
                );

                avAut = new AutentMult().UpgradeAutent_Avi(
                        avAut, avrs.getString("registro").toString() ,aut.intValue(), new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario
                );
                String savAut = new AutentMult().ObjectArrays2String(avAut);
                //System.out.println(savAut);
                conn.ExecutarComando("UPDATE avisos SET aut_pag = '" + savAut + "' WHERE id = " + avrs.getInt("id") );
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}


        // SALDOPROP
        String sdsql = "select id, registro, valor from propsaldo where registro = ? and aut_pag is not null AND aut_pag[1][2] is null order by 1;";
        ResultSet sdrs = conn.AbrirTabela(sdsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",eCodigo.getText()}});
        try {
            while (sdrs.next()) {
                Object[][] sdAut = {{sdrs.getString("registro"),aut.intValue(),new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario}};
                String ssdAut = new AutentMult().ObjectArrays2String(sdAut);
                //conn.ExecutarComando("UPDATE propsaldo SET aut_pag = '" + ssdAut + "' WHERE id = " + sdrs.getInt("id") );
                Object[][] param = new Object[][] {{"decimal", controller.getValorPago()}, {"int", sdrs.getInt("id")}};
                String saldoSQL = "UPDATE propsaldo SET aut_pag = '%s', valorpago = ? WHERE id = ?";
                saldoSQL = String.format(saldoSQL,ssdAut);
                conn.ExecutarComando(saldoSQL, param);
            }
        } catch (Exception e) {e.printStackTrace();}
        try { sdrs.close(); } catch (Exception e) {}

        // Gravação Novo Saldo do Proprietário
        BigDecimal saldo = controller.GetResta();
        String iSql = "INSERT INTO propsaldo (registro, valor, aut_pag) VALUES ('%s',%s,'{{%s,null,null,\"\"}}');";
        iSql = String.format(iSql, eCodigo.getText(), saldo, eCodigo.getText());
        /*if (saldo.floatValue() > 0)*/ try {conn.ExecutarComando(iSql);} catch (Exception e) {}
    }

    private void Reserva(int id, String fieldReserva, String local) {
        String sReserva = "";
        if (fieldReserva == null) {
            sReserva = new AutentMult().ReservaMontaAutInicial();
        } else {
            Object[][] oReserva = new AutentMult().ReservaConvertArrayString2ObjectArrays(fieldReserva);
            if (oReserva == null) {
                sReserva = new AutentMult().ReservaMontaAutInicial();
            } else {
                int rpos = FuncoesGlobais.FindinObject(oReserva,0,VariaveisGlobais.usuario);
                if (rpos < 0) {
                    oReserva = FuncoesGlobais.ObjectsAdd(oReserva, new Object[] {VariaveisGlobais.usuario, new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer())});
                }
                sReserva = new AutentMult().ObjectArrays2String(oReserva);
            }
        }
        String uSql = "";
        if (local.equalsIgnoreCase("movimento")) {
            uSql = "UPDATE %s SET reserva = '%s' WHERE id = %s";
            uSql = String.format(uSql,local, sReserva,id);
        }

        conn.ExecutarComando(uSql);
    }
}
