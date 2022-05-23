package SegundaVia.Caixas;

import Classes.AttachEvent;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Funcoes.*;
import Locatarios.Pagamentos.cPagtos;
import Movimento.FecCaixa.cBanco;
import Movimento.FecCaixa.cCaixa;
import SegundaVia.Adiantamentos.AdiantamentosController;
import SegundaVia.Avisos.AvisosController;
import SegundaVia.Depositos.DepositosController;
import SegundaVia.Depositos.cDepositos;
import com.sun.prism.impl.Disposer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CaixasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private Spinner<Integer> cxaAno;
    @FXML private Button btnListar;
    @FXML private TextField cxaAut;
    @FXML private Button btnImprimir;
    @FXML private Spinner<Integer> cxaMes;
    @FXML private TableView<cDepositos> cxaLista;
    @FXML private TableColumn<cDepositos, Integer> cxaListaId;
    @FXML private TableColumn<cDepositos, Integer> cxaListaAut;
    @FXML private TableColumn<cDepositos, Date> cxaListaDataHora;
    @FXML private TableColumn<cDepositos, BigDecimal> cxaListaValor;
    @FXML private TableColumn<cDepositos, String> cxaListaLogado;
    @FXML private TableColumn<cDepositos, String> cxaListaLancto;
    @FXML private TableColumn<Disposer.Record, Boolean> cxaAcoes;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        cxaAno.setValueFactory(ano);
        SpinnerValueFactory mes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        cxaMes.setValueFactory(mes);
        Platform.runLater(() -> {
            mes.setValue(Dates.iMonth(DbMain.getDateTimeServer()));
            ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        });
        btnListar.setOnAction(event -> {
            FillCaixas(cxaMes.getValue(), cxaAno.getValue());
        });
    }

    private void FillCaixas(int mesDespesa, int anoDespesa) {
        List<cDepositos> data = new ArrayList<cDepositos>();
        String Sql = "SELECT c.id, c.aut, c.lancamentos[1][7]::timestamp without time zone AS datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE EXTRACT(MONTH FROM c.lancamentos[1][7]::date) = ? AND EXTRACT(YEAR FROM c.lancamentos[1][7]::date) = ? AND documento = 'CXA';";

        Object[][] param = new Object[][] {
                {"int", mesDespesa},
                {"int", anoDespesa}
        };
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, param);
            int gId = -1; int gAut = -1;
            BigDecimal gValor = new BigDecimal("0");
            Date gDataHora = null;
            String gLogado = null; String gLanctos = null; String gOper = null;

            while (rs.next()) {
                try {gId = rs.getInt("id");} catch (SQLException sqlex) {}
                try {gOper = rs.getString("operacao");} catch (SQLException sqlex) {}
                try {gAut = rs.getInt("aut");} catch (SQLException sqlex) {}
                try {gDataHora = Dates.String2Date(rs.getString("datahora"));} catch (SQLException sqlex) {}
                try {gValor = new BigDecimal(LerValor.Number2BigDecimal(rs.getString("valor").replace("R$ ","")));} catch (SQLException sqlex) {}
                try {gLogado = rs.getString("logado");} catch (SQLException sqlex) {}
                try {gLanctos = rs.getString("lancamentos");} catch (SQLException sqlex) {}

                data.add(new cDepositos(gId, gAut, gDataHora, gValor, gLogado, gLanctos));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {e.printStackTrace();}

        cxaListaId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        cxaListaId.setStyle( "-fx-alignment: CENTER;");

        cxaListaAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        cxaListaAut.setStyle( "-fx-alignment: CENTER;");

        cxaListaDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        cxaListaDataHora.setCellFactory((DepositosController.AbstractConvertCellFactory<cDepositos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        cxaListaDataHora.setStyle( "-fx-alignment: CENTER;");

        cxaListaValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        cxaListaValor.setCellFactory((DepositosController.AbstractConvertCellFactory<cDepositos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        cxaListaValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        cxaListaLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        cxaListaLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        cxaListaLancto.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        cxaListaLancto.setStyle( "-fx-alignment: CENTER-LEFT;");

        cxaAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        cxaAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) cxaLista.setItems(FXCollections.observableArrayList(data));

        cxaLista.setOnMouseClicked(event -> {
            cDepositos select = cxaLista.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                Date tcxData = select.getDataHora();
                String tcxUsuario = select.getLogado();
                String tcxLanctos = select.getLanctos();
                String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                BigDecimal tcxDn = new BigDecimal(lancto[0][1]);
                BigDecimal tcxCh = new BigDecimal(lancto[1][1]);

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
                new Impressao(new BigInteger("0"), new String[][] {}, select.getDataHora(), tcxUsuario).ImprimeCaixaPDF(dadm, new BigDecimal[] {tcxDn, tcxCh}, Lista, true);
            }
        });
    }

    private BigDecimal TotalCheques(List<cBanco> dataBanco) {
        if (dataBanco.size() == 0) return new BigDecimal("0");

        BigDecimal tTotal = new BigDecimal("0");
        for (cBanco o : dataBanco) {
            tTotal = tTotal.add(o.getValor());
        }
        return tTotal;
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

    // Classe que cria o botão
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("P");
        final Button cellAnexar = new Button("A");

        ButtonCell(){
            cellAnexar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cDepositos select = (cDepositos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    anchorPane.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){

                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    cDepositos select = (cDepositos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                    if (select == null) return;

                    Date tcxData = select.getDataHora();
                    String tcxUsuario = select.getLogado();
                    String tcxLanctos = select.getLanctos();
                    String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                    BigDecimal tcxDn = new BigDecimal(lancto[0][1]);
                    BigDecimal tcxCh = new BigDecimal(lancto[1][1]);

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
                    new Impressao(new BigInteger("0"), new String[][] {}, select.getDataHora(), tcxUsuario).ImprimeCaixaPDF(dadm, new BigDecimal[] {tcxDn, tcxCh}, Lista, true);
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
}
