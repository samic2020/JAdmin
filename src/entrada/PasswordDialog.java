package entrada;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PasswordDialog extends Dialog<Boolean> {
    DbMain conn = VariaveisGlobais.conexao;

    private PasswordField passwordField;

    public PasswordDialog() {
        setTitle("Senha");
        setHeaderText("Por favor entre com sua Senha.");

        ButtonType passwordButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(passwordButtonType, ButtonType.CANCEL);

        passwordField = new PasswordField();
        passwordField.setPromptText("Senha:");

        HBox hBox = new HBox();
        hBox.getChildren().add(passwordField);
        hBox.setPadding(new Insets(20));

        HBox.setHgrow(passwordField, Priority.ALWAYS);

        getDialogPane().setContent(hBox);

        Platform.runLater(() -> passwordField.requestFocus());

        setResultConverter(dialogButton -> {
            if (dialogButton == passwordButtonType) {
                Object[][] senha = null;
                try {
                    senha = conn.LerCamposTabela(new String[] {"user_passwd"},"usuarios", "user_login = '" + VariaveisGlobais.usuario + "' AND user_passwd = '" + passwordField.getText() + "'");
                } catch (Exception e) {}

                return senha != null;
            }
            return null;
        });
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }
}
