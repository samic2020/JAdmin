package Administrador.Abas.ContaAdm;

import Administrador.BancoAdm;
import Administrador.cbancos;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContaAdm implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPaneBancoAdm;
    @FXML private ComboBox<cbancos> ba_banco;
    @FXML private TextField ba_agencia;
    @FXML private TextField ba_conta;
    @FXML private TextField ba_digito;
    @FXML private ComboBox<String> ba_tipo;
    @FXML private TextField ba_ted;
    @FXML private TextField ba_doc;
    @FXML private TextField ba_cheque;
    @FXML private TextField ba_transferencia;
    @FXML private Button ba_btlancar;

    @FXML private TableView<BancoAdm> ba_bancos;
    @FXML private TableColumn<BancoAdm, Integer> ba_bancos_id;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_banco;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_agencia;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_conta;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_tipo;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_ted;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_doc;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_cheque;
    @FXML private TableColumn<BancoAdm, String> ba_bancos_transferencia;

    @Override public void initialize(URL location, ResourceBundle resources) {
        Initialize_ba();
    }

    private void Initialize_ba() {
        // Preencher combobox bancos
        {
            ResultSet rs = conn.AbrirTabela("SELECT numero, site, nome, agenciatam, contatam, cedentetam FROM bancos ORDER BY numero;", ResultSet.CONCUR_READ_ONLY);
            try {
                ba_banco.getItems().clear();
                while (rs.next()) {
                    ba_banco.getItems().add(new cbancos(
                            rs.getString("numero"),
                            rs.getString("nome"),
                            rs.getString("site"),
                            rs.getInt("agenciatam"),
                            rs.getInt("contatam"),
                            rs.getInt("cedentetam")
                    ));
                }
            } catch (SQLException e) {
            }
            try {
                DbMain.FecharTabela(rs);
            } catch (Exception e) {
            }

            Callback<ListView<cbancos>, ListCell<cbancos>> cb = new Callback<ListView<cbancos>, ListCell<cbancos>>() {
                @Override
                public ListCell<cbancos> call(ListView<cbancos> l) {
                    return new ListCell<cbancos>() {

                        private final HBox hbx;
                        private final Label codigo;
                        private final Label sep;
                        private final Label nome;

                        {
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            codigo = new Label();
                            codigo.setTextFill(Color.DARKBLUE);
                            codigo.setPrefWidth(30);
                            codigo.setPrefHeight(25);
                            codigo.setAlignment(Pos.CENTER_RIGHT);

                            sep = new Label(" - ");
                            sep.setPrefWidth(20);
                            sep.setPrefHeight(25);
                            sep.setAlignment(Pos.CENTER);

                            nome = new Label();
                            nome.setTextFill(Color.DARKGREEN);
                            nome.setPrefWidth(180);
                            nome.setPrefHeight(25);
                            nome.setAlignment(Pos.CENTER_LEFT);

                            hbx = new HBox(codigo, sep, nome);
                        }

                        @Override
                        protected void updateItem(cbancos item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setGraphic(null);
                            } else {
                                //setText(item.getNumero() + " - "+item.getNome());
                                codigo.setText(item.getNumero());
                                nome.setText(item.getNome());
                                setGraphic(hbx);
                            }
                        }
                    };
                }
            };
            ba_banco.setCellFactory(cb);
        }

        // Preencher tipo
        {
            ba_tipo.getItems().add("Corrente");
            ba_tipo.getItems().add("Poupança");
        }

        MaskFieldUtil.maxField(ba_agencia, 10);
        MaskFieldUtil.numericField(ba_agencia);

        MaskFieldUtil.maxField(ba_conta, 15);
        MaskFieldUtil.numericField(ba_conta);
        MaskFieldUtil.maxField(ba_digito, 1);

        MaskFieldUtil.monetaryField(ba_ted);
        MaskFieldUtil.monetaryField(ba_doc);
        MaskFieldUtil.monetaryField(ba_cheque);
        MaskFieldUtil.monetaryField(ba_transferencia);

        ba_btlancar.setOnAction(event -> {
            String iSql = "INSERT INTO banco_adm(" +
                    "banco, agencia, conta, tipo, ted, doc, cheque, transferencia) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                int nid = 1;
                pstmt.setString(nid++, ba_banco.getSelectionModel().getSelectedItem().getNumero());
                pstmt.setString(nid++, ba_agencia.getText().trim());
                pstmt.setString(nid++, ba_conta.getText().trim() + "-" + ba_digito.getText().trim());
                pstmt.setString(nid++, ba_tipo.getSelectionModel().getSelectedItem());
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(ba_ted.getText())));
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(ba_doc.getText())));
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(ba_cheque.getText())));
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(ba_transferencia.getText())));
                pstmt.executeUpdate();
            } catch (SQLException ex) {
            }

            populateBancoAdm();
            ClearCamposAdm();
        });

        ba_bancos.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String sql = "DELETE FROM banco_adm WHERE id = '%s';";
                    sql = String.format(sql, ba_bancos.getSelectionModel().getSelectedItem().getId());
                    try {
                        conn.ExecutarComando(sql);
                    } catch (Exception e) {
                    }
                    populateBancoAdm();
                    ba_banco.requestFocus();
                }

            }
        });

        // Desabilita Botao se campos vazios
        ba_btlancar.disableProperty().bind(ba_agencia.textProperty().isEqualTo("").or(ba_conta.textProperty().isEqualTo("").or(
                ba_digito.textProperty().isEqualTo("").or(ba_ted.textProperty().isEqualTo("").or(
                        ba_doc.textProperty().isEqualTo("").or(ba_cheque.textProperty().isEqualTo("").or(
                                ba_transferencia.textProperty().isEqualTo(""))))))));
        populateBancoAdm();
        ClearCamposAdm();
    }

    private void ClearCamposAdm() {
        ba_banco.getSelectionModel().select(0);
        ba_agencia.setText("");
        ba_conta.setText("");
        ba_digito.setText("");
        ba_tipo.getSelectionModel().select(0);
        ba_ted.setText("");
        ba_doc.setText("");
        ba_cheque.setText("");
        ba_transferencia.setText("");

        ba_banco.requestFocus();
    }

    private void populateBancoAdm() {
        List<BancoAdm> data = new ArrayList<BancoAdm>();
        ResultSet rs;
        String qSQL = "SELECT id, banco, agencia, conta, tipo, ted, doc, cheque, transferencia FROM banco_adm ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qbanco = null, qagencia = null, qconta = null, qtipo = null;
                BigDecimal qted = null, qdoc = null, qcheque = null, qtransferencia = null;
                int qid = -1;

                try {
                    qid = rs.getInt("id");
                } catch (SQLException e) {
                }
                try {
                    qbanco = rs.getString("banco");
                } catch (SQLException e) {
                }
                try {
                    qagencia = rs.getString("agencia");
                } catch (SQLException e) {
                }
                try {
                    qconta = rs.getString("conta");
                } catch (SQLException e) {
                }
                try {
                    qtipo = rs.getString("tipo");
                } catch (SQLException e) {
                }
                try {
                    qted = rs.getBigDecimal("ted");
                } catch (SQLException e) {
                }
                try {
                    qdoc = rs.getBigDecimal("doc");
                } catch (SQLException e) {
                }
                try {
                    qcheque = rs.getBigDecimal("cheque");
                } catch (SQLException e) {
                }
                try {
                    qtransferencia = rs.getBigDecimal("transferencia");
                } catch (SQLException e) {
                }

                data.add(new BancoAdm(qid, qbanco, qagencia, qconta, qtipo,
                        LerValor.FormatPattern(qted.toPlainString(), "#,##0.00"),
                        LerValor.FormatPattern(qdoc.toPlainString(), "#,##0.00"),
                        LerValor.FormatPattern(qcheque.toPlainString(), "#,##0.00"),
                        LerValor.FormatPattern(qtransferencia.toPlainString(), "#,##0.00"))
                );
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {
        }

        ba_bancos_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        ba_bancos_id.setStyle("-fx-alignment: CENTER;");
        ba_bancos_banco.setCellValueFactory(new PropertyValueFactory<>("banco"));
        ba_bancos_banco.setStyle("-fx-alignment: CENTER-RIGHT;");
        ba_bancos_agencia.setCellValueFactory(new PropertyValueFactory<>("agencia"));
        ba_bancos_agencia.setStyle("-fx-alignment: CENTER-RIGHT;");
        ba_bancos_conta.setCellValueFactory(new PropertyValueFactory<>("conta"));
        ba_bancos_conta.setStyle("-fx-alignment: CENTER-RIGHT;");
        ba_bancos_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        ba_bancos_tipo.setStyle("-fx-alignment: CENTER;");
        ba_bancos_ted.setCellValueFactory(new PropertyValueFactory<>("ted"));
        ba_bancos_ted.setStyle("-fx-alignment: CENTER-RIGHT;");
        ba_bancos_doc.setCellValueFactory(new PropertyValueFactory<>("doc"));
        ba_bancos_doc.setStyle("-fx-alignment: CENTER-RIGHT;");
        ba_bancos_cheque.setCellValueFactory(new PropertyValueFactory<>("cheque"));
        ba_bancos_cheque.setStyle("-fx-alignment: CENTER-RIGHT;");
        ba_bancos_transferencia.setCellValueFactory(new PropertyValueFactory<>("transferencia"));
        ba_bancos_transferencia.setStyle("-fx-alignment: CENTER-RIGHT;");

        if (!data.isEmpty()) ba_bancos.setItems(FXCollections.observableArrayList(data));
    }
}
