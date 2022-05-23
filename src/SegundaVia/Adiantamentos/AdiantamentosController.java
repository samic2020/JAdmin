package SegundaVia.Adiantamentos;

import Calculos.AvisosMensagens;
import Calculos.Multas;
import Classes.AttachEvent;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Classes.jExtrato;
import Funcoes.Collections;
import Funcoes.*;
import Locatarios.Pagamentos.cPagtos;
import Movimento.Extrato.ExtratoBloqClass;
import SegundaVia.Extratos.ExtratosController;
import SegundaVia.Extratos.cRectos;
import SegundaVia.Recibos.RecibosController;
import com.sun.prism.impl.Disposer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class AdiantamentosController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    @FXML private AnchorPane anchorPaneAdiantamentos;
    @FXML private TextField adtContrato;
    @FXML private TextField adtNome;
    @FXML private Spinner<Integer> adtAno;
    @FXML private Button btnListar;
    @FXML private TextField adtAutent;
    @FXML private Button btnImprimir;

    @FXML private TableView<cRectos> adtListaRec;
    @FXML private TableColumn<cRectos, Integer> adtId;
    @FXML private TableColumn<cRectos, Integer> adtAut;
    @FXML private TableColumn<cRectos, Date> adtDataHora;
    @FXML private TableColumn<cRectos, BigDecimal> adtValor;
    @FXML private TableColumn<cRectos, String> adtLogado;
    @FXML private TableColumn<cRectos, String> adtLanctos;
    @FXML private TableColumn<Disposer.Record, Boolean> adtAcoes;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        adtAno.setValueFactory(ano);

        AutocompleteContratoNome();

        btnListar.setOnAction(event -> {
            if (adtContrato.getText() == null && adtNome.getText() == null) {adtContrato.requestFocus(); return;}
            if (!adtContrato.getText().trim().equalsIgnoreCase("") && !adtNome.getText().trim().equalsIgnoreCase("")) {
                adtListaRec.setItems(null);
                int irgimv = Integer.valueOf(adtContrato.getText().trim());
                int anoExtrato = Integer.valueOf(adtAno.getValue());
                FillPagtos(irgimv, anoExtrato);
            }
        });

        btnImprimir.setOnAction(event -> {
            if (adtAutent.getText() == null) {
                adtAutent.requestFocus();
                return;
            }
            if (!adtAutent.getText().trim().equalsIgnoreCase("")) {

            }
        });

        Platform.runLater(() -> adtContrato.requestFocus());
    }

    private void AutocompleteContratoNome() {
        ResultSet imv = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios ORDER BY p_rgprp;";
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

        TextFields.bindAutoCompletion(adtContrato, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(adtNome, possibleSuggestionsNome);

        adtContrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                adtContrato.setText(null);
                adtNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = adtContrato.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, adtContrato.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        adtNome.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        adtNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, adtNome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = adtContrato.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,adtNome.getText())) {
                        adtContrato.setText(_possibleSuggestions[pos][0]);
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }
            }
        });
    }

    private void FillPagtos(int rgprp, int anoExtrato) {
        List<cRectos> data = new ArrayList<cRectos>();
        String Sql = "SELECT c.id, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.rgprp = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND operacao = 'DEB' AND documento = 'ADI';";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", rgprp}, {"int", anoExtrato}});
            int gId = -1; int gAut = -1;
            BigDecimal gValor = new BigDecimal("0");
            Date gDataHora = null;
            String gLogado = null; String gLanctos = null;

            while (rs.next()) {
                try {gId = rs.getInt("id");} catch (SQLException sqlex) {}
                try {gAut = rs.getInt("aut");} catch (SQLException sqlex) {}
                try {gDataHora = Dates.String2Date(rs.getString("datahora"));} catch (SQLException sqlex) {}
                try {gValor = new BigDecimal(LerValor.Number2BigDecimal(rs.getString("valor").replace("R$ ","")));} catch (SQLException sqlex) {}
                try {gLogado = rs.getString("logado");} catch (SQLException sqlex) {}
                try {gLanctos = rs.getString("lancamentos");} catch (SQLException sqlex) {}

                data.add(new cRectos(gId, gAut, gDataHora, gValor, gLogado, gLanctos));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        adtId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        adtId.setStyle( "-fx-alignment: CENTER;");

        adtAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        adtAut.setStyle( "-fx-alignment: CENTER;");

        adtDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        adtDataHora.setCellFactory((ExtratosController.AbstractConvertCellFactory<cRectos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        adtDataHora.setStyle( "-fx-alignment: CENTER;");

        adtValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        adtValor.setCellFactory((ExtratosController.AbstractConvertCellFactory<cRectos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        adtValor.setStyle( "-fx-alignment: CENTER-RIGHT;");


        adtLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        adtLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        adtLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        adtLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        adtAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        adtAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) adtListaRec.setItems(FXCollections.observableArrayList(data));

/*
        adtListaRec.setOnMouseClicked(event -> {
            cRectos select = adtListaRec.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                String lancto = "{{" + adtContrato.getText().trim() + "," +
                        select.getAut() + "," +
                        Dates.DateFormata("dd-MM-yyyy", select.getDataHora()) + "," + select.getLogado() + "}}";
                pExtrato(adtContrato.getText(), lancto, select.getDataHora(), select.getLogado(), select.getValor(),select.getAut());
            }
        });
*/
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

    private String[][] ConvertArrayString2ObjectArrays_REC(String value) {
        String[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
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

    private String[][] ConvertArrayString2ObjectArrays_EXT(String value) {
        String[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
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
                            vtr[1].trim().replace("\"",""),
                            vtr[2].trim().replace("\"",""),
                            vtr[3].trim().replace("\"","")
                    });
        }
        return retorno;
    }

    private void pExtrato(String rgprp, String lancto, Date dataHora, String logado, BigDecimal vrpago, int aut) {
        ObservableList<ExtratoBloqClass> bloqdata = FXCollections.observableArrayList();

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");
        BigDecimal tVRP = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select * from movimento where ad_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Object[][] endImovel = null;
                try {
                    endImovel = conn.LerCamposTabela(new String[] {"i_end", "i_num", "i_cplto"},"imoveis", "i_rgimv = '" + rs.getString("rgimv") + "'");
                } catch (Exception e) {}
                String linha = "<b>" + rs.getString("rgimv") + "</b> - " + endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim();
                Extrato = new jExtrato(Descr(linha), null, null);
                lista.add(Extrato);

                Object[][] nomeLoca = null;
                try {
                    nomeLoca = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"},"locatarios", "l_contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {}
                Extrato = new jExtrato(Descr((String) nomeLoca[0][3]), null, null);
                lista.add(Extrato);

                Extrato = new jExtrato(Descr("[" + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")) + "]"), null, null);
                lista.add(Extrato);

                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try { com = rs.getBigDecimal("cm"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("ALU") + "  " + rs.getBigDecimal("mensal") + "  " + palu),alu, null );
                lista.add(Extrato);
                ttCR = ttCR.add(alu);

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    dfsql = "select * from descdif where ad_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
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
                    try { pirvr = rs.getBigDecimal("ir"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }
                    if (pirvr != null) {
                        if (pirvr.doubleValue() != 0) {
                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IRF")), null, pirvr);
                            lista.add(Extrato);
                            ttDB = ttDB.add(pirvr);
                        }
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    sgsql = "select * from seguros where ad_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
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
                    txsql = "select * from taxas where ad_pag::varchar like '%" + lancto + "%' order by 2,3,4,7,9;";
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
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

/*
                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    if (pmuvr != null) {
                        if (pmuvr.doubleValue() != 0) {
                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("MUL")), pmuvr, null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(pmuvr);
                        }
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr != null) {
                        if (pjuvr.doubleValue() != 0) {
                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("JUR")), pjuvr, null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(pjuvr);
                        }
                    }
                }

                // CORREÇÃO
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                {
                    try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                    if (pcovr != null) {
                        if (pcovr.doubleValue() != 0) {
                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COR")), pcovr, null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(pcovr);
                        }
                    }
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                {
                    try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                    if (pepvr != null) {
                        if (pepvr.doubleValue() != 0) {
                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("EXP")), pepvr, null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(pepvr);
                        }
                    }
                }
*/

                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (SQLException ex) {}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String[][] aAut = ConvertArrayString2ObjectArrays_EXT(lancto);
        BigInteger biAut = new BigInteger(aAut[0][1]);
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec = ? and conta = 6 order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"bigint", biAut}});
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

        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);

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
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Agência: " + bancoPrin[1] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>C/C: " + bancoPrin[2] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Favorecido: " + (bancoPrin[3].equalsIgnoreCase("null") ? "O Próprio" : bancoPrin[3])  + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
        }

        // complementa com linhas em branco para preencher a página
        int npag = lista.size() % 32;
        for (int i=1;i<=(32 - npag);i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }

        String psql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        psql = String.format(sql,adtContrato.getText().trim());
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

        List<extrato.Extrato> listas = new ArrayList<>();
        extrato.Extrato bean1 = new extrato.Extrato();
        bean1.setnomeProp(adtContrato.getText() + " - " + adtNome.getText());
        bean1.setextratoNumero(LoadExtratoNumber(adtContrato.getText(), aut));
        bean1.setlogoLocation(dadm.get("logo"));
        bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(adtContrato.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        bean1.setautentica(autenticacao);
        //bean1.setbarras(aut.toString());
        int n = 0;
        for (int i = 0; i <= lista.size() - 1; i++) {
            jExtrato item = lista.get(i);

            if (i % 40 == 0 && i > 0) {
                listas.add(bean1);
                bean1 = new extrato.Extrato();
                bean1.setnomeProp(adtContrato.getText() + " - " + adtNome.getText());
                bean1.setextratoNumero(LoadExtratoNumber(adtContrato.getText(), aut));
                bean1.setlogoLocation(dadm.get("logo"));
                bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(adtContrato.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
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

        Map parametros = new HashMap();
        parametros.put("Logo",dadm.get("logo"));
        parametros.put("rgprp", adtContrato.getText().trim());
        parametros.put("nomeProp", adtNome.getText());
        parametros.put("NumeroExtrato", Integer.valueOf(LoadExtratoNumber(adtContrato.getText(), aut)));
        parametros.put("Mensagem", new AvisosMensagens().VerificaAniProprietario(adtContrato.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        parametros.put("Autenticacao",autenticacao);
        parametros.put("ShowSaldo", true);

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"jExtrato", parametros);
        // new toPrint(pdfName,"LASER","INTERNA");
        new PdfViewer("Adiantamento ao Proprietário", pdfName);
    }

    private String Descr(String desc) { return desc; }

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

    // Classe que cria o botão
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("P");
        final Button cellAnexar = new Button("A");

        ButtonCell(){
            cellAnexar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cRectos select = (cRectos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    anchorPaneAdiantamentos.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){

                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    cRectos select = (cRectos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                    if (select == null) return;

                    String lancto = "{{" + adtContrato.getText().trim() + "," +
                            select.getAut() + "," +
                            Dates.DateFormata("dd-MM-yyyy", select.getDataHora()) + "," + select.getLogado() + "}}";
                    pExtrato(adtContrato.getText(), lancto, select.getDataHora(), select.getLogado(), select.getValor(),select.getAut());
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                HBox pane = new HBox(cellButton, cellAnexar);
                setGraphic(pane);
            }
        }
    }
}
