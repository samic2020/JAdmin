package SegundaVia.Retencao;

import Classes.AttachEvent;
import Funcoes.*;
import Movimento.Avisos.TableRetencao;
import SegundaVia.Avisos.cAvisos;
import com.sun.prism.impl.Disposer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
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

public class RetencaoController implements Initializable {
    private DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPaneRetencao;

    @FXML private Spinner<Integer> retMes;
    @FXML private Spinner<Integer> retAno;
    @FXML private Button btnListar;

    @FXML private TextField retAutent;
    @FXML private Button btnImprimir;

    @FXML private TableView<cAvisos> ListaRet;
    @FXML private TableColumn<cAvisos, Integer> retId;
    @FXML private TableColumn<cAvisos, String> retOper;
    @FXML private TableColumn<cAvisos, String> retAut;
    @FXML private TableColumn<cAvisos, Date> retDataHora;
    @FXML private TableColumn<cAvisos, BigDecimal> retValor;
    @FXML private TableColumn<cAvisos, String> retLogado;
    @FXML private TableColumn<cAvisos, String> retLanctos;
    @FXML private TableColumn<Disposer.Record, Boolean> retAcoes;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        retAno.setValueFactory(ano);

        SpinnerValueFactory mes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        retMes.setValueFactory(mes);

        Platform.runLater(() -> {
            ano.setValue(Dates.iMonth(DbMain.getDateTimeServer()));
            ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        });

        btnListar.setOnAction(event -> {
            FillRetencao(retAno.getValue(), retMes.getValue());
        });

        btnImprimir.setOnAction(event -> {
            if (Integer.valueOf(retAutent.getText()) <= 0) return;
            Object[][] dadosAvisoRet = null;
            try { dadosAvisoRet = conn.LerCamposTabela(new String[] {"documento", "operacao", "aut", "datahora", "valor", "logado", "lancamentos"}, "caixa", "aut = " + retAutent.getText()); } catch (Exception e) {}
            if (dadosAvisoRet == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Autenticação");
                alert.setContentText("Não existe este autenticação!!!");
                alert.showAndWait();
                return;
            }
            if (!dadosAvisoRet[0][3].toString().equalsIgnoreCase("AVI")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Autenticação");
                alert.setContentText("Esta autenticação não é uma lista de Retenção!!!");
                alert.showAndWait();
                return;
            }

            String[][] lancto = ConvertArrayString2ObjectArrays(dadosAvisoRet[6][3].toString());
            BigInteger Aut = new BigInteger(retAutent.getText());
            Collections dadm = VariaveisGlobais.getAdmDados();

            BigDecimal tRet = new BigDecimal(0);
            List<TableRetencao> lista = new ArrayList<TableRetencao>();
            String retSQL = "SELECT t.*, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campoDesc FROM taxas t WHERE aut_ret = " + retAutent.getText();
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

            new Impressao(Aut, lancto, Dates.String2Date(dadosAvisoRet[3][3].toString()), dadosAvisoRet[5][3].toString()).ImprimeRetencaoPDF(dadm, lista, tRet, true);
        });
    }

    private void FillRetencao(int anoAviso, int mesAviso) {
        List<cAvisos> data = new ArrayList<cAvisos>();
        String Sql = "SELECT c.id, c.operacao, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.contrato = 'RET' AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'AVI';";

        Object[][] param = new Object[][] {
                {"int", mesAviso},
                {"int", anoAviso}
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

                data.add(new cAvisos(gId, gOper, gAut, gDataHora, gValor, gLogado, gLanctos));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        retId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        retId.setStyle( "-fx-alignment: CENTER;");

        retOper.setCellValueFactory(new PropertyValueFactory<>("Operacao"));
        retOper.setStyle( "-fx-alignment: CENTER;");

        retAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        retAut.setStyle( "-fx-alignment: CENTER;");

        retDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        retDataHora.setCellFactory((AbstractConvertCellFactory<cAvisos, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        retDataHora.setStyle( "-fx-alignment: CENTER;");

        retValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        retValor.setCellFactory((AbstractConvertCellFactory<cAvisos, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        retValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        retLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        retLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        retLanctos.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        retLanctos.setStyle( "-fx-alignment: CENTER-LEFT;");

        retAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        retAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) ListaRet.setItems(FXCollections.observableArrayList(data));
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

    // Classe que cria o botão
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("P");
        final Button cellAnexar = new Button("A");

        ButtonCell(){
            cellAnexar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cAvisos select = (cAvisos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    anchorPaneRetencao.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    cAvisos select = (cAvisos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

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

                    new Impressao(Aut, lancto, select.getDataHora(), select.getLogado()).ImprimeRetencaoPDF(dadm, lista, tRet, true);
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
