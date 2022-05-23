package Movimento.Despesas;

import Classes.paramEvent;
import Funcoes.*;
import PagRec.PagamentosController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class DespesasController implements Initializable {
    PagamentosController controllerPag;

    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private Pane tpagtos;

    @FXML private TableView<grpDespesas> grpDespesas;
    @FXML private TableColumn<grpDespesas, Integer> grpDespesasId;
    @FXML private TableColumn<grpDespesas, String> grpDespesasDescr;

    @FXML private TextField grpDespesasFiltro;
    @FXML private Button btnAdc;

    @FXML private TextArea despTexto;
    @FXML private TextField despValor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam.length == 0) {
                // Cancelar Recebimento
                Platform.runLater(() -> grpDespesasFiltro.requestFocus());
            }
            if (event.sparam.length > 0) {
                if (event.sparam[0] != null) {
                    String[][] Lancamentos = (String[][]) event.sparam[1];
                    Collections dadm = VariaveisGlobais.getAdmDados();
                    BigInteger aut = conn.PegarAutenticacao();
                    try {
                        String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                                "contrato, valor, lancamentos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                        String lanctos = DbMain.GeraLancamentosArray(Lancamentos);
                        String tlanctos = lanctos.replace("{{","").replace("}}","").replace("\"","");
                        String[] alanctos = tlanctos.split(",");
                        String alc1 = ""; try { alc1 = alanctos[0]; } catch (Exception e) {}
                        String alc2 = ""; try { alc2 = alanctos[1]; } catch (Exception e) {}
                        String alc3 = ""; try { alc3 = alanctos[2]; } catch (Exception e) {}
                        String alc4 = ""; try { alc4 = alanctos[3]; } catch (Exception e) {}
                        String alc5 = ""; try { alc5 = alanctos[4]; } catch (Exception e) {}
                        String alc6 = ""; try { alc6 = alanctos[5]; } catch (Exception e) {}

                        BigDecimal vrRecibo = new BigDecimal(LerValor.Decimal2String(despValor.getText()));
                        if (conn.ExecutarComando(caixaSQL, new Object[][] {
                                {"bigint", aut},
                                {"date", Dates.toSqlDate(DbMain.getDateTimeServer())},
                                {"string", VariaveisGlobais.usuario},
                                {"string", "DEB"},
                                {"string", "DPS"},
                                {"int", 0},
                                {"int",0},
                                {"string", String.valueOf(grpDespesas.getSelectionModel().getSelectedItem().getId())},
                                {"decimal", vrRecibo},
                                {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{alc1, alc2, alc3, alc4,alc5,alc6}})}
                        }) > 0) {
                            String insertSQL = "INSERT INTO despesas (idgrupo, descricao, texto, valor, dtpagto, logado, aut) VALUES (?, ?, ?, ?, ?, ?, ?);";
                            Object[][] param = new Object[][] {
                                    {"int", grpDespesas.getSelectionModel().getSelectedItem().getId()},
                                    {"string", grpDespesas.getSelectionModel().getSelectedItem().getDescricao()},
                                    {"string", despTexto.getText().trim()},
                                    {"decimal", vrRecibo},
                                    {"date", Dates.toSqlDate(DbMain.getDateTimeServer())},
                                    {"string", VariaveisGlobais.usuario},
                                    {"bigint", aut}
                            };
                            conn.ExecutarComando(insertSQL, param);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Object[] dados = {null, null, grpDespesas.getSelectionModel().getSelectedItem().getId(), grpDespesas.getSelectionModel().getSelectedItem().getDescricao(), new BigDecimal(LerValor.Decimal2String(despValor.getText())), despTexto.getText()};
                    new Impressao(aut, Lancamentos).ImprimeDespesaPDF(dadm, dados, false);

                    grpDespesas.getSelectionModel().clearSelection();
                    despTexto.setText(""); despTexto.setDisable(true);
                    despValor.setText("0,00"); despValor.setDisable(true);
                    controllerPag.Formas_DisableAll();
                    grpDespesasFiltro.setText("");
                    grpDespesasFiltro.requestFocus();
                }
            }
        });

        FillGrupos();
        LoadPagtos();

        grpDespesasFiltro.setOnKeyPressed(event -> {
            if (grpDespesas.getItems().size() < 1) {
                btnAdc.setVisible(true);
            } else {
                btnAdc.setVisible(false);
            }
        });

        btnAdc.setOnAction(event -> {
            String insertSQL = "INSERT INTO despesasgrupo (descricao) VALUES (?)";
            conn.ExecutarComando(insertSQL, new Object[][] {{"string", grpDespesasFiltro.getText().trim()}});
            FillGrupos();
            btnAdc.setDisable(true);
        });
    }

    private void FillGrupos() {
        ObservableList<grpDespesas> items = FXCollections.observableArrayList();
        String selectSQL = "SELECT id, descricao FROM despesasgrupo ORDER BY Upper(descricao);";
        ResultSet rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY);
        try {
            int qid = 0; String qdescr = null;
            while (rs.next()) {
                try { qid = rs.getInt("id"); } catch (SQLException exsql) {}
                try { qdescr = rs.getString("descricao"); } catch (SQLException exsql) {}

                items.add(new grpDespesas(qid, qdescr));
            }
        } catch (SQLException sqlex) {}
        try { DbMain.FecharTabela(rs); } catch (Exception ex) {}

        grpDespesasId.setCellValueFactory(new PropertyValueFactory<>("id"));
        grpDespesasId.setStyle( "-fx-alignment: CENTER; -fx-text-fill: darkred");

        grpDespesasDescr.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        grpDespesasDescr.setStyle( "-fx-alignment: LEFT; -fx-text-fill: darkred");

        grpDespesas.setOnKeyPressed(event -> {
            if (grpDespesas.getSelectionModel().isEmpty()) return;

            if (event.getCode() == KeyCode.DELETE) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este grupo?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String deleteSQL = "DELETE FROM despesasgrupo WHERE id = ?;";
                    conn.ExecutarComando(deleteSQL, new Object[][]{{"int", grpDespesas.getSelectionModel().getSelectedItem().getId()}});
                    FillGrupos();
                }
            }
        });
        grpDespesas.setOnMouseClicked(event -> {
            if (grpDespesas.getSelectionModel().getSelectedItem() != null) {
                despTexto.setDisable(false);
                despValor.setDisable(false);
                despTexto.requestFocus();
            } else {
                despTexto.setDisable(true);
                despValor.setDisable(true);
            }
        });

        if (!items.isEmpty()) grpDespesas.setItems(items);


        FilteredList<grpDespesas> filteredData = new FilteredList<grpDespesas>(items, e -> true);
        grpDespesasFiltro.setOnKeyReleased(e ->{
            grpDespesasFiltro.textProperty().addListener((observableValue, oldValue, newValue) ->{
                filteredData.setPredicate((Predicate<? super grpDespesas>) user->{
                    boolean returned = false;
                    if(newValue == null || newValue.isEmpty()) {
                        returned = true;
                    } else {
                        String lowerCaseFilter = FuncoesGlobais.deAccent(newValue.toLowerCase());
                        if (String.valueOf(user.getId()).contains(newValue)) {
                            returned = true;
                        } else if (FuncoesGlobais.deAccent(user.getDescricao().toLowerCase()).contains(lowerCaseFilter)) {
                            returned = true;
                        }
                    }
                    return returned;
                });
            });
            SortedList<grpDespesas> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(grpDespesas.comparatorProperty());
            grpDespesas.setItems(sortedData);
            grpDespesas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        });
    }

    private void LoadPagtos() {
        try {tpagtos.getChildren().remove(0);} catch (Exception e) {}
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Pagamentos.fxml"));
            Pane root = (Pane)loader.load();
            controllerPag = loader.getController();
            tpagtos.getChildren().add(root);
            root.setLayoutX(0); root.setLayoutY(0);
        } catch (Exception e) {e.printStackTrace();}

        MaskFieldUtil.monetaryField(despValor);
        despValor.requestFocus();
        despValor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // gotfocus
                controllerPag.Formas_DisableAll();
            } else {
                // lostfocus
                    controllerPag.Formas_Disable(false);
                    controllerPag.SetValor(new BigDecimal(LerValor.Number2BigDecimal(despValor.getText())));
            }
        });
    }

}
