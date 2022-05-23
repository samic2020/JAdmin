package Administrador.Abas.ContasEmail;

import Administrador.EditingCellNumber;
import Administrador.EditingCellText;
import Administrador.EditingCellTextEmail;
import Administrador.EmailAdm;
import Funcoes.DbMain;
import Funcoes.EmailFormatValidator;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContasEmail implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPaneContasEmail;
    @FXML private TextField se_email;
    @FXML private TextField se_senha;
    @FXML private TextField se_smtp;
    @FXML private TextField se_porta;
    @FXML private CheckBox se_autentica;
    @FXML private CheckBox se_ssl;
    @FXML private Button se_btok;

    @FXML private TableView<EmailAdm> se_emails;
    @FXML private TableColumn<EmailAdm, Integer> se_emails_id;
    @FXML private TableColumn<EmailAdm, String> se_emails_email;
    @FXML private TableColumn<EmailAdm, String> se_emails_senha;
    @FXML private TableColumn<EmailAdm, String> se_emails_smtp;
    @FXML private TableColumn<EmailAdm, String> se_emails_porta;
    @FXML private TableColumn<EmailAdm, Boolean> se_emails_autentica;
    @FXML private TableColumn<EmailAdm, Boolean> se_emails_ssl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initialize_se();
    }

    private void Initialize_se() {
        se_email.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (lostfocus)
                    if (!new EmailFormatValidator().validate(se_email.getText().trim())) {
                        Alert erro = new Alert(Alert.AlertType.ERROR);
                        erro.setTitle("Erro");
                        erro.setContentText("Email inválido ou mal formatado!");
                        erro.show();
                        se_email.requestFocus();
                    }
            } catch (Exception e) {
            }
        });
        MaskFieldUtil.maxField(se_email, 100);
        MaskFieldUtil.maxField(se_senha, 30);
        MaskFieldUtil.maxField(se_smtp, 100);
        MaskFieldUtil.maxField(se_porta, 5);
        MaskFieldUtil.numericField(se_porta);

        se_btok.setOnAction(event -> {
            String iSql = "INSERT INTO conta_email(" +
                    "email, senha, smtp, porta, autentica, ssl) " +
                    "VALUES (?, ?, ?, ?, ?, ?);";

            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                int nid = 1;
                pstmt.setString(nid++, se_email.getText().trim());
                pstmt.setString(nid++, se_senha.getText().trim());
                pstmt.setString(nid++, se_smtp.getText().trim());
                pstmt.setInt(nid++, Integer.valueOf(se_porta.getText().trim()));
                pstmt.setBoolean(nid++, se_autentica.isSelected());
                pstmt.setBoolean(nid++, se_ssl.isSelected());
                pstmt.executeUpdate();
            } catch (SQLException ex) {
            }

            populateEmail();
            ClearCamposEmail();
        });

        se_emails.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este lançamento?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Sim")) {
                    String sql = "DELETE FROM conta_email WHERE id = '%s';";
                    sql = String.format(sql, se_emails.getSelectionModel().getSelectedItem().getId());
                    try {
                        conn.ExecutarComando(sql);
                    } catch (Exception e) {
                    }
                    populateEmail();
                    se_emails.requestFocus();
                }

            }
        });

        // Desabilita Botao se campos vazios
        se_btok.disableProperty().bind(se_email.textProperty().isEqualTo("").or(se_senha.textProperty().isEqualTo("").or(
                se_smtp.textProperty().isEqualTo("").or(se_porta.textProperty().isEqualTo("")))));
        populateEmail();
        ClearCamposEmail();

    }

    private void ClearCamposEmail() {
        se_email.setText("");
        se_senha.setText("");
        se_smtp.setText("");
        se_porta.setText("");
        se_autentica.setSelected(false);
        se_ssl.setSelected(false);

        se_email.requestFocus();
    }

    private void populateEmail() {
        List<EmailAdm> data = new ArrayList<EmailAdm>();
        ResultSet rs;
        String qSQL = "SELECT id, email, senha, smtp, porta, autentica, ssl FROM conta_email ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qemail = null, qsenha = null, qsmtp = null, qporta = null;
                boolean qautentica = false, qssl = false;
                int qid = -1;

                try { qid = rs.getInt("id"); } catch (SQLException e) { }
                try { qemail = rs.getString("email"); } catch (SQLException e) { }
                try { qsenha = rs.getString("senha"); } catch (SQLException e) { }
                try { qsmtp = rs.getString("smtp"); } catch (SQLException e) { }
                try { qporta = rs.getString("porta"); } catch (SQLException e) { }
                try { qautentica = rs.getBoolean("autentica"); } catch (SQLException e) { }
                try { qssl = rs.getBoolean("ssl"); } catch (SQLException e) { }

                data.add(new EmailAdm(qid, qemail, qsenha, qsmtp, qporta, qautentica, qssl));
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {
        }

        se_emails_id.setCellValueFactory(new PropertyValueFactory<EmailAdm, Integer>("id"));
        se_emails_id.setStyle("-fx-alignment: CENTER;");

        Callback<TableColumn<EmailAdm, String>, TableCell<EmailAdm, String>> cellFactoryEmail = (TableColumn<EmailAdm, String> p) -> new EditingCellTextEmail();
        se_emails_email.setCellValueFactory(new PropertyValueFactory<EmailAdm, String>("email"));
        se_emails_email.setCellFactory(cellFactoryEmail);
        se_emails_email.setStyle("-fx-alignment: CENTER-LEFT;");
        se_emails_email.setEditable(true);
        se_emails_email.setOnEditCommit((TableColumn.CellEditEvent<EmailAdm, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setEmail(t.getNewValue());
                    EmailAdm tbvlinhas = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if (tbvlinhas.getId() > 0) {
                        String uSql = "UPDATE conta_email SET email = '" + tbvlinhas.getEmail() + "' WHERE id = " + tbvlinhas.getId() + ";";
                        conn.ExecutarComando(uSql);
                    }
                }
        );

        Callback<TableColumn<EmailAdm, String>, TableCell<EmailAdm, String>> cellFactorySenha = (TableColumn<EmailAdm, String> p) -> new EditingCellText(30);
        se_emails_senha.setCellValueFactory(new PropertyValueFactory<EmailAdm, String>("senha"));
        se_emails_senha.setCellFactory(cellFactorySenha);
        se_emails_senha.setStyle("-fx-alignment: CENTER-LEFT;");
        se_emails_senha.setEditable(true);
        se_emails_senha.setOnEditCommit((TableColumn.CellEditEvent<EmailAdm, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setSenha(t.getNewValue());
                    EmailAdm tbvlinhas = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if (tbvlinhas.getId() > 0) {
                        String uSql = "UPDATE conta_email SET senha = '" + tbvlinhas.getSenha() + "' WHERE id = " + tbvlinhas.getId() + ";";
                        conn.ExecutarComando(uSql);
                    }
                }
        );

        Callback<TableColumn<EmailAdm, String>, TableCell<EmailAdm, String>> cellFactorySmtp = (TableColumn<EmailAdm, String> p) -> new EditingCellText(100);
        se_emails_smtp.setCellValueFactory(new PropertyValueFactory<EmailAdm, String>("smtp"));
        se_emails_smtp.setCellFactory(cellFactorySmtp);
        se_emails_smtp.setStyle("-fx-alignment: CENTER-LEFT;");
        se_emails_smtp.setEditable(true);
        se_emails_smtp.setOnEditCommit((TableColumn.CellEditEvent<EmailAdm, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setSmtp(t.getNewValue());
                    EmailAdm tbvlinhas = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if (tbvlinhas.getId() > 0) {
                        String uSql = "UPDATE conta_email SET smtp = '" + tbvlinhas.getSmtp() + "' WHERE id = " + tbvlinhas.getId() + ";";
                        conn.ExecutarComando(uSql);
                    }
                }
        );

        Callback<TableColumn<EmailAdm, String>, TableCell<EmailAdm, String>> cellFactoryPorta = (TableColumn<EmailAdm, String> p) -> new EditingCellNumber(5);
        se_emails_porta.setCellValueFactory(new PropertyValueFactory<EmailAdm, String>("porta"));
        se_emails_porta.setCellFactory(cellFactoryPorta);
        se_emails_porta.setStyle("-fx-alignment: CENTER;");
        se_emails_porta.setEditable(true);
        se_emails_porta.setOnEditCommit((TableColumn.CellEditEvent<EmailAdm, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setPorta(t.getNewValue());
                    EmailAdm tbvlinhas = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if (tbvlinhas.getId() > 0) {
                        String uSql = "UPDATE conta_email SET porta = '" + tbvlinhas.getPorta() + "' WHERE id = " + tbvlinhas.getId() + ";";
                        conn.ExecutarComando(uSql);
                    }
                }
        );

        se_emails_autentica.setCellValueFactory(new PropertyValueFactory<EmailAdm, Boolean>("autentica"));
        se_emails_autentica.setCellFactory(CheckBoxTableCell.forTableColumn(se_emails_autentica));
        se_emails_autentica.setStyle("-fx-alignment: CENTER;");

        se_emails_ssl.setCellValueFactory(new PropertyValueFactory<EmailAdm, Boolean>("ssl"));
        se_emails_ssl.setCellFactory(CheckBoxTableCell.forTableColumn(se_emails_ssl));
        se_emails_ssl.setStyle("-fx-alignment: CENTER;");

        if (!data.isEmpty()) se_emails.setItems(FXCollections.observableArrayList(data));
    }
}
