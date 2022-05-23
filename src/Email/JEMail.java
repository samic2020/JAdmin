package Email;

import Classes.AttachEvent;
import Classes.paramEvent;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class JEMail implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    String[] email_anexos = {};
    @FXML private AnchorPane anchorPane;
    @FXML private Pane PanelBg;
    @FXML private ComboBox<String> email_De;
    @FXML private TextField email_Assunto;
    @FXML private TextArea email_Mensagem;
    @FXML private ListView<String> email_Anexos;
    @FXML private Button btnAnexos;
    @FXML private Button btnEnviar;
    @FXML private TextField email_Para;
    @FXML private Button btnBuscar;
    private Object[] param = {};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Captura os Anexos enviado pela 2a Via
        anchorPane.addEventHandler(AttachEvent.GET_ATTACH, event -> {
            this.param = event.sparam;
            if (this.param.length > 0) {
                String[][] a = (String[][])param[0];
                for (String[] o : a) {
                    email_Anexos.getItems().add(o[1]);
                }
            }
        });

        Destinatarios();

        email_Para.setText("");
        btnBuscar.setDisable(true);
        email_Assunto.setText("");
        email_Mensagem.setText("");
        btnAnexos.setText("[00] Anexo(s)");
        btnEnviar.setDisable(true);

        btnBuscar.disableProperty().bind(email_Para.textProperty().isEmpty());
        btnEnviar.disableProperty().bind(email_Para.textProperty().isEmpty());

        email_Anexos.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                email_Anexos.getSelectionModel().getSelectedItems().remove(email_Anexos.getSelectionModel().getSelectedIndex());
                btnAnexos.setText("[" + email_Anexos.getItems().size() + "] Anexo(s)");
            }
        });

        btnAnexos.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.rte"),
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                    new FileChooser.ExtensionFilter("Pdf Files", "*.pdf"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            List<File> selectedFile = fileChooser.showOpenMultipleDialog(new Stage());
            if (selectedFile != null) {
                btnAnexos.setText("[" + selectedFile.size() + "] Anexo(s)");

                for (int i = 0; i < selectedFile.size(); i++ ) {
                    email_Anexos.getItems().add(selectedFile.get(i).getAbsolutePath());
                }
            }
        });

        btnEnviar.setOnAction(event -> {
            for (int i = 0; i < email_Anexos.getItems().size(); i++ ) {
                email_anexos = FuncoesGlobais.ArrayAdd(email_anexos, System.getProperty("user.dir") + "/" + email_Anexos.getItems().get(i).toString());
            }

            try {
                SendEmail email = new SendEmail();
                boolean bEniviado = email.sendMsg(
                        email_Para.getText().trim(),
                        email_Assunto.getText().trim(),
                        email_Mensagem.getText(),
                        email_anexos
                );
                
                // Colocar mensagem de envio
                System.out.println(bEniviado);
                if (bEniviado) {
                    new Alert(INFORMATION, "Email enviado com sucesso!").show();
                    try {anchorPane.fireEvent(new paramEvent(new String[] {"Locatario"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
                } else {
                    new Alert(INFORMATION, "Erro ao enviar o Email.\nConfira as informações.").show();
                }
            } catch (Exception e) {e.printStackTrace();}
        });
    }

    private void Destinatarios() {
        ResultSet rs;
        String qSQL = "SELECT id, email, senha, smtp, porta, autentica, ssl FROM conta_email ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qemail = null;
                try {qemail = rs.getString("email");} catch (SQLException e) {}
                email_De.getItems().add(qemail);
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}
    }

    public String[] Destinatarios(int n) {
        ResultSet rs; String[] retorno = {};
        String qSQL = "SELECT id, email, senha, smtp, porta, autentica, ssl FROM conta_email ORDER BY id LIMIT " + n + ";";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                try {retorno = FuncoesGlobais.ArrayAdd(retorno, rs.getString("email"));} catch (SQLException e) {}
                try {retorno = FuncoesGlobais.ArrayAdd(retorno, rs.getString("senha"));} catch (SQLException e) {}
                try {retorno = FuncoesGlobais.ArrayAdd(retorno, rs.getString("smtp"));} catch (SQLException e) {}
                try {retorno = FuncoesGlobais.ArrayAdd(retorno, rs.getString("porta"));} catch (SQLException e) {}
                try {retorno = FuncoesGlobais.ArrayAdd(retorno, rs.getBoolean("autentica") ? "true" : "false");} catch (SQLException e) {}
                try {retorno = FuncoesGlobais.ArrayAdd(retorno, rs.getBoolean("ssl") ? "true" : "false");} catch (SQLException e) {}
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}
        return retorno;
    }
}
