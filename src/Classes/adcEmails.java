/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import masktextfield.MaskTextField;

import java.util.Optional;

/**
 *
 * @author supervisor
 */
public class adcEmails {
    public Optional<pemailModel> adcEmails(boolean comsenha) {
        // Create the custom dialog.
        Dialog<pemailModel> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        //dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        
        dialog.setTitle("Cadastro de E-Mail");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final MaskTextField qemail = new MaskTextField();
        final TextField qsenha = new TextField();

        qemail.setMask("*!@*!.P!");
        qemail.setPrefWidth(430);
        qemail.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (comsenha) {
                    if (!qemail.getText().isEmpty()) {
                        salvarButton.setDisable(true);
                        qsenha.requestFocus();
                    } else qemail.requestFocus();
                } else {
                    if (!qemail.getText().isEmpty()) {
                        salvarButton.setDisable(false);
                        salvarButton.requestFocus();
                    } else qemail.requestFocus();
                }
            }
        });

        grid.add(new Label("E-Mail:"), 0, 0);
        grid.add(qemail, 1, 0);

        if (comsenha) {
            qsenha.setPrefWidth(430);
            qsenha.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    if (!qsenha.getText().isEmpty()) {
                        salvarButton.setDisable(false);
                        salvarButton.requestFocus();
                    } else qsenha.requestFocus();
                }
            });
            grid.add(new Label("Senha:"), 0, 1);
            grid.add(qsenha, 1, 1);
        }

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qemail.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                if (comsenha) {
                    return new pemailModel(qemail.getText(), qsenha.getText());
                } else {
                    return new pemailModel(qemail.getText());
                }
            }
            return null;
        });

        Optional<pemailModel> result = dialog.showAndWait();
        return result;
    }
}
