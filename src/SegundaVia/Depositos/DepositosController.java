package SegundaVia.Depositos;

import Classes.AttachEvent;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Funcoes.*;
import Locatarios.Pagamentos.cPagtos;
import Movimento.Depositos.cDeposito;
import SegundaVia.Adiantamentos.AdiantamentosController;
import SegundaVia.Recibos.RecibosController;
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

public class DepositosController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;

    @FXML private Spinner<Integer> depMes;
    @FXML private Spinner<Integer> depAno;
    @FXML private Button btnListar;

    @FXML private TextField depAut;
    @FXML private Button btnImprimir;

    @FXML private TableView<cDepositos> depLista;
    @FXML private TableColumn<cDepositos, Integer> depListaId;
    @FXML private TableColumn<cDepositos, Integer> depListaAut;
    @FXML private TableColumn<cDepositos, Date> depListaDataHora;
    @FXML private TableColumn<cDepositos, BigDecimal> depListaValor;
    @FXML private TableColumn<cDepositos, String> depListaLogado;
    @FXML private TableColumn<cDepositos, String> depListaLancto;
    @FXML private TableColumn<Disposer.Record, Boolean> depAcoes;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        depAno.setValueFactory(ano);
        SpinnerValueFactory mes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        depMes.setValueFactory(mes);
        Platform.runLater(() -> {
            mes.setValue(Dates.iMonth(DbMain.getDateTimeServer()));
            ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        });
        btnListar.setOnAction(event -> {
            FillDepositos(depMes.getValue(), depAno.getValue());
        });

    }

    private void FillDepositos(int mesDespesa, int anoDespesa) {
        List<cDepositos> data = new ArrayList<cDepositos>();
        String Sql = "SELECT c.id, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'DEP';";

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
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        depListaId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        depListaId.setStyle( "-fx-alignment: CENTER;");

        depListaAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        depListaAut.setStyle( "-fx-alignment: CENTER;");

        depListaDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        depListaDataHora.setCellFactory((AbstractConvertCellFactory<cDepositos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        depListaDataHora.setStyle( "-fx-alignment: CENTER;");

        depListaValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        depListaValor.setCellFactory((AbstractConvertCellFactory<cDepositos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        depListaValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        depListaLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        depListaLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        depListaLancto.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        depListaLancto.setStyle( "-fx-alignment: CENTER-LEFT;");

        depAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        depAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) depLista.setItems(FXCollections.observableArrayList(data));

        depLista.setOnMouseClicked(event -> {
            cDepositos select = depLista.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
                Collections dadm = VariaveisGlobais.getAdmDados();

                // Pega dados Lancamentos da autenticacao do cheque
                List<cDeposito> dados = new ArrayList<>();
                ResultSet cxrs = null;
                String selectSQL = "SELECT lancamentos, id, s, aut, datahora, lancamentos[s][1]::varchar(2) tipo, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE (lancamentos[s][1] = 'CH') AND (not lancamentos[s][7] is null AND lancamentos[s][7] = '" + Aut.toString() + "' AND documento != 'DEP') ORDER BY 1,7,8,9";
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
                        vrDinheiro = vrDinheiro.add(new BigDecimal(o[1]));
                    } else if (o[0].equalsIgnoreCase("CH")) {
                        depBanco = o[4];
                        depAgencia = o[3];
                        depConta = o[2];
                        vrCheque = vrCheque.add(new BigDecimal(o[1]));
                    }
                }

                new Impressao(Aut, new String[][] {
                        {
                                "DP",
                                depBanco,
                                depAgencia,
                                depConta,
                                Dates.DateFormata("dd-MM-yyyy HH:mm:ss", select.getDataHora()),
                                LerValor.Number2BigDecimal(select.getValor().toPlainString())
                        }},select.getDataHora(),select.getLogado()).ImprimeDepositoPDF(dadm, dados, vrCheque.add(vrDinheiro), vrDinheiro, true);
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

                    String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                    BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
                    Collections dadm = VariaveisGlobais.getAdmDados();

                    // Pega dados Lancamentos da autenticacao do cheque
                    List<cDeposito> dados = new ArrayList<>();
                    ResultSet cxrs = null;
                    String selectSQL = "SELECT lancamentos, id, s, aut, datahora, lancamentos[s][1]::varchar(2) tipo, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE (lancamentos[s][1] = 'CH') AND (not lancamentos[s][7] is null AND lancamentos[s][7] = '" + Aut.toString() + "' AND documento != 'DEP') ORDER BY 1,7,8,9";
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
                            vrDinheiro = vrDinheiro.add(new BigDecimal(o[1]));
                        } else if (o[0].equalsIgnoreCase("CH")) {
                            depBanco = o[4];
                            depAgencia = o[3];
                            depConta = o[2];
                            vrCheque = vrCheque.add(new BigDecimal(o[1]));
                        }
                    }

                    new Impressao(Aut, new String[][] {
                            {
                                    "DP",
                                    depBanco,
                                    depAgencia,
                                    depConta,
                                    Dates.DateFormata("dd-MM-yyyy HH:mm:ss", select.getDataHora()),
                                    LerValor.Number2BigDecimal(select.getValor().toPlainString())
                            }},select.getDataHora(),select.getLogado()).ImprimeDepositoPDF(dadm, dados, vrCheque.add(vrDinheiro), vrDinheiro, true);
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
