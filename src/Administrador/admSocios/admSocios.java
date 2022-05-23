package Administrador.admSocios;

import Classes.paramEvent;
import Funcoes.DbMain;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class admSocios implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean bInc = false; boolean bAlt = false;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField aId;
    @FXML private TextField aNome;
    @FXML private TextField aBanco;
    @FXML private TextField aAgencia;
    @FXML private TextField aConta;
    @FXML private TextField aPerc;

    @FXML private Button aIncluir;
    @FXML private Button aAlterar;
    @FXML private Button aExcluir;
    @FXML private Button aGravar;
    @FXML private Button aRetornar;

    @FXML private TextField aPercTot;

    @FXML private TableView<admSociosClass> aSocios;
    @FXML private TableColumn<admSociosClass, Integer> atvId;
    @FXML private TableColumn<admSociosClass, String> atvNome;
    @FXML private TableColumn<admSociosClass, Double> atvPerc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Desabilita campos
        DisEn(true);

        // Preenche Grade Sócios
        populateaSocios();
        aSocios.getSelectionModel().selectFirst();

        aIncluir.setOnAction(event -> {
            bInc = true; bAlt = false;
            LimpaTela();
            DisEn(false);

            // Bot�es
            aIncluir.setDisable(true);
            aAlterar.setDisable(true);
            aExcluir.setDisable(true);
            aGravar.setDisable(false);
            aRetornar.setDisable(false);

            aNome.requestFocus();
        });

        aExcluir.setOnAction(event -> {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir esta informa��o?", new ButtonType("Sim"), new ButtonType("N�o"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("N�o")) return;

            admSociosClass selecao = aSocios.getSelectionModel().getSelectedItem();
            String delSQL = "DELETE FROM adm_socios WHERE id = ?";
            if (conn.ExecutarComando(delSQL, new Object[][]{{"int", selecao.getId()}}) > 0) {
                populateaSocios();
                aSocios.getSelectionModel().selectFirst();
            }
            bInc = false; bAlt = false;
        });

        aAlterar.setOnAction(event -> {
            bInc = false; bAlt = true;
            DisEn(false);

            // Bot�es
            aIncluir.setDisable(true);
            aAlterar.setDisable(true);
            aExcluir.setDisable(true);
            aGravar.setDisable(false);
            aRetornar.setDisable(false);

            aNome.requestFocus();
        });

        aGravar.setOnAction(event -> {
            String querySQL = null;
            Object[][] param = null;

            if (bInc) {
                querySQL = "INSERT INTO adm_socios(nome, perc, banco, agencia, conta) " +
                           "VALUES (?, ?, ?, ?, ?);";
                param = new Object[][] {
                        {"string", aNome.getText()},
                        {"double", Double.valueOf(aPerc.getText())},
                        {"string", aBanco.getText()},
                        {"string", aAgencia.getText()},
                        {"string", aConta.getText()}
                };
            } else {
                querySQL = "UPDATE adm_socios SET nome=?, perc=?, banco=?, agencia=?, conta=? " +
                           "WHERE id = ?;";
                param = new Object[][] {
                        {"string", aNome.getText()},
                        {"double", Double.valueOf(aPerc.getText())},
                        {"string", aBanco.getText()},
                        {"string", aAgencia.getText()},
                        {"string", aConta.getText()},
                        {"int", Integer.valueOf(aId.getText())}
                };
            }
            if (conn.ExecutarComando(querySQL, param) > 0) {
                if (bInc) {
                    populateaSocios();
                    aSocios.getSelectionModel().selectLast();
                } else {
                    admSociosClass selecao = aSocios.getSelectionModel().getSelectedItem();
                    populateaSocios();
                    aSocios.getSelectionModel().select(selecao);
                }
            }

            bInc = false; bAlt = false;
            DisEn(true);
        });

        aRetornar.setOnAction(event -> {
            if (bInc) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos!\n\nDeseja dispensar estas informa��es?", new ButtonType("Sim"), new ButtonType("N�o"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("N�o")) {
                    return;
                }

                LerCampos();
                DisEn(true);
                bInc = false; bAlt = false;
            } else {
                if (bAlt) {
                    Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram alterados!\n\nDeseja dispensar estas informa��es?", new ButtonType("Sim"), new ButtonType("N�o"));
                    Optional<ButtonType> result = msg.showAndWait();
                    if (result.get().getText().equals("N�o")) {
                        return;
                    }
                }

                if (!bInc && !bAlt) {
                    // Fecha tela
                    try { anchorPane.fireEvent(new paramEvent(new String[]{"SociosAdm"}, paramEvent.GET_PARAM)); } catch (NullPointerException e) { }
                }
            }
        });

        aPerc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                double perc = Double.valueOf(aPerc.getText().replace(",","."));
                double tperc = Double.valueOf(aPercTot.getText().replace(",","."));
                aGravar.setDisable(perc > tperc);
            }
        });
    }

    private void LerCampos() {
        admSociosClass selecao = aSocios.getSelectionModel().getSelectedItem();

        if (selecao != null) {
            String qid = String.valueOf(selecao.getId());aId.setText(qid);
            String qnome = selecao.getNome();aNome.setText(qnome);
            String qbanco = selecao.getBanco();aBanco.setText(qbanco);
            String qagencia = selecao.getAgencia();aAgencia.setText(qagencia);
            String qconta = selecao.getConta();aConta.setText(qconta);
            String qperc = String.valueOf(selecao.getPerc());aPerc.setText(qperc);

            boolean bLogica =  Double.valueOf(aPercTot.getText().replace(",",".")) <= 0;
            aIncluir.setDisable(false || bLogica);
            aAlterar.setDisable(false);
            aExcluir.setDisable(false);
            aGravar.setDisable(true);
            aRetornar.setDisable(false);
        } else {
            LimpaTela();
            aIncluir.setDisable(false);
            aAlterar.setDisable(true);
            aExcluir.setDisable(true);
            aGravar.setDisable(true);
            aRetornar.setDisable(false);
        }
    }

    private void LimpaTela() {
        aId.setText("");
        aNome.setText("");
        aBanco.setText("");
        aAgencia.setText("");
        aConta.setText("");
        aPerc.setText("");
    }

    private void DisEn(boolean value) {
        aId.setDisable(false);
        aNome.setDisable(value);
        aBanco.setDisable(value);
        aAgencia.setDisable(value);
        aConta.setDisable(value);
        aPerc.setDisable(value);
    }

    private void populateaSocios() {
        aSocios.getItems().clear();

        List<admSociosClass> data = new ArrayList<admSociosClass>();
        ResultSet soc; double tperc = 0;
        String qSQL = "SELECT id, nome, perc, banco, agencia, conta, sant FROM adm_socios ORDER BY id;";
        try {
            soc = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (soc.next()) {
                int qid = -1; String qnome = null; double qperc = 0;
                String qbanco = null, qagencia = null, qconta = null;

                try {qid = soc.getInt("id");} catch (SQLException e) {}
                try {qnome = soc.getString("nome");} catch (SQLException e) {}
                try {qperc = soc.getDouble("perc");} catch (SQLException e) {}
                try {qbanco = soc.getString("banco");} catch (SQLException e) {}
                try {qagencia = soc.getString("agencia");} catch (SQLException e) {}
                try {qconta = soc.getString("conta");} catch (SQLException e) {}

                data.add(new admSociosClass(qid, qnome, qbanco, qagencia, qconta, qperc));
                tperc += qperc;
            }
            soc.close();
        } catch (SQLException e) {}

        atvId.setCellValueFactory(new PropertyValueFactory<>("id"));
        atvNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        atvPerc.setCellValueFactory(new PropertyValueFactory<>("perc"));
        DecimalFormat formato = new DecimalFormat("#.###");
        atvPerc.setCellFactory(tc -> new TableCell<admSociosClass, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(formato.format(price));
                }
            }
        });

        if (data != null) {
            aSocios.setItems(FXCollections.observableArrayList(data));
        } else aSocios.getItems().clear();

        aPercTot.setText(String.format("%.3f", 100 - tperc));

        aSocios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        aSocios.getSelectionModel().setCellSelectionEnabled(false);

        aSocios.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            LerCampos();
        });

        if (tperc <= 0) {
            aIncluir.setDisable(false);
            aAlterar.setDisable(true);
            aExcluir.setDisable(true);
            aGravar.setDisable(true);
            aRetornar.setDisable(false);
        }
    }

}
