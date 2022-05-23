package SegundaVia.Despesas;

import Classes.AttachEvent;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Funcoes.Collections;
import Funcoes.*;
import Locatarios.Pagamentos.cPagtos;
import SegundaVia.Avisos.AvisosController;
import SegundaVia.Depositos.DepositosController;
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
import org.controlsfx.control.textfield.TextFields;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DespesasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField despContrato;
    @FXML private TextField despNome;
    @FXML private Spinner<Integer> despMes;
    @FXML private Spinner<Integer> despAno;
    @FXML private Button btnListar;
    @FXML private TextField despAut;
    @FXML private Button btnImprimir;
    @FXML private TableView<cDespesas> despLista;
    @FXML private TableColumn<cDespesas, Integer> despListaId;
    @FXML private TableColumn<cDespesas, Integer> despListaAut;
    @FXML private TableColumn<cDespesas, Date> despListaDataHora;
    @FXML private TableColumn<cDespesas, BigDecimal> despListaValor;
    @FXML private TableColumn<cDespesas, String> despListaLogado;
    @FXML private TableColumn<cDespesas, String> despListaLancto;
    @FXML private TableColumn<Disposer.Record, Boolean> despAcoes;

    private String[] _possibleSuggestionsLcContrato = {};
    private String[] _possibleSuggestionsLcNome = {};
    private String[][] _possibleSuggestionsLc = {};
    private Set<String> possibleSuggestionsLcContrato;
    private Set<String> possibleSuggestionsLcNome;
    private boolean isSearchLcContrato = true;
    private boolean isSearchLcNome = true;

    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2018);
        despAno.setValueFactory(ano);
        SpinnerValueFactory mes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        despMes.setValueFactory(mes);
        Platform.runLater(() -> {
            mes.setValue(Dates.iMonth(DbMain.getDateTimeServer()));
            ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        });
        AutocompleteDesp();

        btnListar.setOnAction(event -> {
            FillDespesas(despContrato.getText(), despMes.getValue(), despAno.getValue());
        });

        btnImprimir.setOnAction(event -> {
            if (Float.valueOf(despAut.getText()) <= 0) return;
            Object[][] dadosDesp = null;
            try { dadosDesp = conn.LerCamposTabela(new String[] {"documento", "operacao", "aut", "datahora", "valor", "logado", "lancamentos"}, "caixa", "aut = " + despAut.getText()); } catch (Exception e) {}
            if (dadosDesp == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Autenticação");
                alert.setContentText("Não existe este autenticação!!!");
                alert.showAndWait();
                return;
            }
            if (!dadosDesp[0][3].toString().equalsIgnoreCase("DPS")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Atenção");
                alert.setHeaderText("Autenticação");
                alert.setContentText("Esta autenticação não é uma Despesa!!!");
                alert.showAndWait();
                return;
            }

            String[][] lancto = ConvertArrayString2ObjectArrays(dadosDesp[6][3].toString());
            BigInteger Aut = new BigInteger(despAut.getText());
            Collections dadm = VariaveisGlobais.getAdmDados();

            String codigo = null; String nome = null;
            Object[][] despDados = null;
            try {
                despDados = conn.LerCamposTabela(new String[] {"idgrupo", "descricao"},"despesas", "aut = ?",
                        new Object[][] {{"bigint", Aut}});
            } catch (SQLException sex) {}
            if (despDados != null) {
                codigo = String.valueOf((int)despDados[0][3]);
                nome = (String)despDados[1][3];
            }

            Object[][] textoAdm = null;
            try { textoAdm = conn.LerCamposTabela(new String[] {"texto"},"despesas", "aut = " + despAut.getText()); } catch (Exception e) {}
            String Texto = null;
            if (textoAdm != null) Texto = textoAdm[0][3].toString();

            Object[] dados = {null, null, codigo, nome, new BigDecimal(LerValor.Number2BigDecimal(dadosDesp[4][3].toString().replace("R$ ",""))), Texto};
            new Impressao(Aut, lancto, Dates.String2Date(dadosDesp[3][3].toString()), dadosDesp[5][3].toString()).ImprimeDespesaPDF(dadm, dados, true);
        });
    }

    private void AutocompleteDesp() {
        _possibleSuggestionsLcContrato = new String[]{};
        _possibleSuggestionsLcNome = new String[]{};
        _possibleSuggestionsLc = new String[][]{};
        possibleSuggestionsLcContrato = new HashSet<String>();
        possibleSuggestionsLcNome = new HashSet<String>();
        isSearchLcContrato = true;
        isSearchLcNome = true;

        try {
            TextFields.bindAutoCompletion(despContrato, new HashSet<String>());
            TextFields.bindAutoCompletion(despNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Grupo de Despesas
        qSQL = "SELECT id, descricao FROM despesasgrupo ORDER BY Upper(Trim(descricao));";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("id");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsLcContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcContrato, qcontrato);
                possibleSuggestionsLcContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsLcContrato));

                _possibleSuggestionsLcNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcNome, qnome);
                possibleSuggestionsLcNome = new HashSet<>(Arrays.asList(_possibleSuggestionsLcNome));

                _possibleSuggestionsLc = FuncoesGlobais.ArraysAdd(_possibleSuggestionsLc, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(despContrato, possibleSuggestionsLcContrato);
        TextFields.bindAutoCompletion(despNome, possibleSuggestionsLcNome);

        despContrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                despContrato.setText(null);
                despNome.setText(null);
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = despContrato.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc, 0, despContrato.getText());
                    if (pos > -1 && isSearchLcContrato) {
                        isSearchLcNome = false;
                        despNome.setText(_possibleSuggestionsLc[pos][1]);
                        isSearchLcNome = true;
                    }
                } else {
                    isSearchLcContrato = false;
                    isSearchLcNome = true;
                }
            }
        });

        despNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc,1, despNome.getText());
                String pcontrato = null;
                try {pcontrato = despContrato.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchLcNome && pcontrato == null) {
                    isSearchLcContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsLc,1,despNome.getText())) {
                        despContrato.setText(_possibleSuggestionsLc[pos][0]);
                    }
                    isSearchLcContrato = true;
                } else {
                    isSearchLcContrato = true;
                    isSearchLcNome = false;
                }
            }
        });
    }

    private void FillDespesas(String documento, int mesDespesa, int anoDespesa) {
        List<cDespesas> data = new ArrayList<cDespesas>();
        String Sql = "SELECT c.id, c.aut, c.datahora, c.valor, c.logado, c.lancamentos FROM caixa c WHERE c.contrato = ? AND EXTRACT(MONTH FROM c.datahora) = ? AND EXTRACT(YEAR FROM c.datahora) = ? AND documento = 'DPS';";

        Object[][] param = new Object[][] {
                {"string", documento},
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

                data.add(new cDespesas(gId, gAut, gDataHora, gValor, gLogado, gLanctos));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        despListaId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        despListaId.setStyle( "-fx-alignment: CENTER;");

        despListaAut.setCellValueFactory(new PropertyValueFactory<>("Aut"));
        despListaAut.setStyle( "-fx-alignment: CENTER;");

        despListaDataHora.setCellValueFactory(new PropertyValueFactory<>("DataHora"));
        despListaDataHora.setCellFactory((AvisosController.AbstractConvertCellFactory<cDespesas, Date>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(Dates.toLocalDate(value)));
        despListaDataHora.setStyle( "-fx-alignment: CENTER;");

        despListaValor.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        despListaValor.setCellFactory((AvisosController.AbstractConvertCellFactory<cDespesas, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        despListaValor.setStyle( "-fx-alignment: CENTER-RIGHT;");

        despListaLogado.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        despListaLogado.setStyle( "-fx-alignment: CENTER-LEFT;");

        despListaLancto.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        despListaLancto.setStyle( "-fx-alignment: CENTER-LEFT;");

        despAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
        despAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) despLista.setItems(FXCollections.observableArrayList(data));

        despLista.setOnMouseClicked(event -> {
            cDespesas select = despLista.getSelectionModel().getSelectedItem();
            if (select == null) return;
            if (event.getClickCount() == 2) {
                String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
                Collections dadm = VariaveisGlobais.getAdmDados();

                String codigo = null; String nome = null;
                codigo = despContrato.getText();
                nome = despNome.getText();

                Object[][] textoAdm = null;
                try { textoAdm = conn.LerCamposTabela(new String[] {"texto"},"despesas", "aut = " + select.getAut()); } catch (Exception e) {}
                String Texto = null;
                if (textoAdm != null) Texto = textoAdm[0][3].toString();

                Object[] dados = {null, null, codigo, nome, select.getValor(), Texto};
                new Impressao(Aut, lancto, select.getDataHora(), select.getLogado()).ImprimeDespesaPDF(dadm, dados, true);
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

    // Classe que cria o botão
    private class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final Button cellButton = new Button("P");
        final Button cellAnexar = new Button("A");

        ButtonCell() {
            cellAnexar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cDespesas select = (cDespesas) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    anchorPane.fireEvent(new AttachEvent(new Object[]{select}, AttachEvent.GET_ATTACH));
                }
            });

            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    cDespesas select = (cDespesas) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                    if (select == null) return;

                    String[][] lancto = ConvertArrayString2ObjectArrays(select.getLanctos());
                    BigInteger Aut = new BigInteger(String.valueOf(select.getAut()));
                    Collections dadm = VariaveisGlobais.getAdmDados();

                    String codigo = null; String nome = null;
                    codigo = despContrato.getText();
                    nome = despNome.getText();

                    Object[][] textoAdm = null;
                    try { textoAdm = conn.LerCamposTabela(new String[] {"texto"},"despesas", "aut = " + select.getAut()); } catch (Exception e) {}
                    String Texto = null;
                    if (textoAdm != null) Texto = textoAdm[0][3].toString();

                    Object[] dados = {null, null, codigo, nome, select.getValor(), Texto};
                    new Impressao(Aut, lancto, select.getDataHora(), select.getLogado()).ImprimeDespesaPDF(dadm, dados, true);
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                HBox pane = new HBox(cellButton, cellAnexar);
                setGraphic(pane);
            }
        }
    }
}
