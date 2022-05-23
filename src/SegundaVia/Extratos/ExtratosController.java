package SegundaVia.Extratos;

import Calculos.AvisosMensagens;
import Calculos.Multas;
import Calculos.PegaDivisao;
import Classes.AttachEvent;
import Classes.jExtrato;
import Funcoes.Collections;
import Funcoes.*;
import Gerencia.divSec;
import Movimento.Extrato.ExtratoBloqClass;
import com.sun.prism.impl.Disposer;
import extrato.Extrato;
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
//import sun.plugin.javascript.navig.Anchor;

import java.awt.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class ExtratosController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField extContrato;
    @FXML private TextField extNome;
    @FXML private Spinner<Integer> extAno;
    @FXML private Button btnListar;

    @FXML private TextField extAut;
    @FXML private Button btnImprimir;

    @FXML private TableView<cRectos> extListaRec;
    @FXML private TableColumn<cRectos, Integer> exId;
    @FXML private TableColumn<cRectos, Integer> exAut;
    @FXML private TableColumn<cRectos, Date> exDataHora;
    @FXML private TableColumn<cRectos, BigDecimal> exValor;
    @FXML private TableColumn<cRectos, String> exLogado;
    @FXML private TableColumn<cRectos, String> exLanctos;
    @FXML private TableColumn<Disposer.Record, Boolean> exAcoes;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        extAno.setValueFactory(ano);

        AutocompleteContratoNome();

        btnListar.setOnAction(event -> {
            if (extContrato.getText() == null && extNome.getText() == null) {extContrato.requestFocus(); return;}
            if (!extContrato.getText().trim().equalsIgnoreCase("") && !extNome.getText().trim().equalsIgnoreCase("")) {
                    extListaRec.setItems(null);
                    int irgimv = Integer.valueOf(extContrato.getText().trim());
                    int anoExtrato = Integer.valueOf(extAno.getValue());
                    FillPagtos(irgimv, anoExtrato);
            }
        });

        btnImprimir.setOnAction(event -> {
                    if (extAut.getText() == null) {
                        extAut.requestFocus();
                        return;
                    }
                    if (!extAut.getText().trim().equalsIgnoreCase("")) {

                    }
        });

        Platform.runLater(() -> extContrato.requestFocus());
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

        TextFields.bindAutoCompletion(extContrato, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(extNome, possibleSuggestionsNome);

        extContrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                extContrato.setText(null);
                extNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = extContrato.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, extContrato.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        extNome.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        extNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, extNome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = extContrato.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,extNome.getText())) {
                        extContrato.setText(_possibleSuggestions[pos][0]);
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
        String Sql = "SELECT c.id, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.rgprp = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND operacao = 'DEB' AND documento = 'EXT';";
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

        exId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        exId.setStyle( "-fx-alignment: CENTER;");

        exAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        exAut.setStyle( "-fx-alignment: CENTER;");

        exDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        exDataHora.setCellFactory((AbstractConvertCellFactory<cRectos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        exDataHora.setStyle( "-fx-alignment: CENTER;");

        exValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        exValor.setCellFactory((AbstractConvertCellFactory<cRectos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        exValor.setStyle( "-fx-alignment: CENTER-RIGHT;");


        exLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        exLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        exLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        exLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        exAcoes.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>,
                        ObservableValue<Boolean>>() {

                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Disposer.Record, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });

        //Adding the Button to the cell
        exAcoes.setCellFactory(
                new Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>() {

                    @Override
                    public TableCell<Disposer.Record, Boolean> call(TableColumn<Disposer.Record, Boolean> p) {
                        return new ButtonCell();
                    }

                });


        if (!data.isEmpty()) extListaRec.setItems(FXCollections.observableArrayList(data));

        extListaRec.setOnMouseClicked(event -> {
            cRectos select = extListaRec.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                String lancto = "{{" + extContrato.getText().trim() + "," +
                        select.getAut() + "," +
                        Dates.DateFormata("dd-MM-yyyy", select.getDataHora()) + "," + select.getLogado() + "}}";
                pExtrato(extContrato.getText(), lancto, select.getDataHora(), select.getLogado(), select.getValor(),select.getAut());
            }
        });
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

    private void pExtrato(String rgprp, String lancto, Date dataHora, String logado, BigDecimal vrpago, int aut) {
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

        // Pegar as divisões secundárias
        List<divSec> dSec = new PegaDivisao().PegaDivSecundaria(rgprp);

        // Aqui pega os recibos recebidos e não pagos
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

                // Teste de Gravação de uma variavel modo Read
                Reserva(rs.getInt("id"), rs.getString("reserva"), "movimento");

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
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
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

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

                // CORREÇÃO
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
        psql = String.format(sql,extContrato.getText().trim());
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
        parametros.put("rgprp", extContrato.getText().trim());
        parametros.put("nomeProp", extNome.getText());
        parametros.put("NumeroExtrato", Integer.valueOf(LoadExtratoNumber(extContrato.getText(), aut)));
        parametros.put("Mensagem", new AvisosMensagens().VerificaAniProprietario(extContrato.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        parametros.put("Autenticacao",autenticacao);
        parametros.put("ShowSaldo", true);

        List<extrato.Extrato> listas = new ArrayList<>();
        Extrato bean1 = new Extrato();
        bean1.setnomeProp(extContrato.getText() + " - " + extNome.getText());
        bean1.setextratoNumero(LoadExtratoNumber(extContrato.getText(), aut));
        bean1.setlogoLocation(dadm.get("logo"));
        bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(extContrato.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
        bean1.setautentica(autenticacao);
        //bean1.setbarras(aut.toString());
        int n = 0;
        for (int i = 0; i <= lista.size() - 1; i++) {
            jExtrato item = lista.get(i);

            if (i % 40 == 0 && i > 0) {
                listas.add(bean1);
                bean1 = new Extrato();
                bean1.setnomeProp(extContrato.getText() + " - " + extNome.getText());
                bean1.setextratoNumero(LoadExtratoNumber(extContrato.getText(), aut));
                bean1.setlogoLocation(dadm.get("logo"));
                bean1.setmensagem(new AvisosMensagens().VerificaAniProprietario(extContrato.getText()) ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
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
        new PdfViewer("Extrato do Proprietário", pdfName);
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
                    anchorPane.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){

                @Override
                public void handle(ActionEvent t) {
                    cRectos select = (cRectos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    if (select == null) return;
                    Object[][] rgprp = null;
                    try { rgprp = conn.LerCamposTabela(new String[] {"p_rgprp"},"proprietarios","p_id = ?", new Object[][] {{"int",select.getId()}}); } catch (Exception e) {}
                    String lancto = "{{" + rgprp[0][3].toString() + "," +
                            select.getAut() + "," +
                            Dates.DateFormata("dd-MM-yyyy", select.getDataHora()) + "," + select.getLogado() + "}}";
                    pExtrato(rgprp[0][3].toString(), lancto, select.getDataHora(), select.getLogado(), select.getValor(),select.getAut());
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

    private String[][] ConvertArrayString2ObjectArrays(String value) {
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
            if (vtr.length == 7) {
                retorno = FuncoesGlobais.ArraysAdd(retorno,
                        new String[]{
                                vtr[0].trim().replace("\"", ""),
                                vtr[1].trim().replace("\"", ""),
                                vtr[2].trim().replace("\"", ""),
                                vtr[3].trim().replace("\"", ""),
                                vtr[4].trim().replace("\"", ""),
                                vtr[5].trim().replace("\"", ""),
                                vtr[6].trim().replace("\"", "")
                        });
            } else {
                retorno = FuncoesGlobais.ArraysAdd(retorno,
                        new String[]{
                                vtr[0].trim().replace("\"", ""),
                                vtr[1].trim().replace("\"", ""),
                                vtr[2].trim().replace("\"", ""),
                                vtr[3].trim().replace("\"", ""),
                                vtr[4].trim().replace("\"", ""),
                                vtr[5].trim().replace("\"", "")
                        });
            }
        }
        return retorno;
    }


}
