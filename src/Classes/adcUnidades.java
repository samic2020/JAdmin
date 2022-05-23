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

import java.util.Optional;

/**
 *
 * @author supervisor
 */
public class adcUnidades {
    Dialog<pUnidades> dialog;
    
    public Optional<pUnidades> adcUnidades() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        //dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        
        dialog.setTitle("Cadastro de Unidades Remotas");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField qIpDns = new TextField();
        final TextField qBaseDados = new TextField();
        final CheckBox qSenha = new CheckBox();

        qIpDns.setPrefWidth(36); qIpDns.setPrefHeight(25);
        qIpDns.setPadding(new Insets(0, 2, 0, 0));
        qIpDns.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qBaseDados.requestFocus();
           }
        });

        qBaseDados.setPrefWidth(95); qBaseDados.setPrefHeight(25);
        qBaseDados.setPadding(new Insets(0, 2, 0, 0));
        qBaseDados.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qSenha.requestFocus();
           }
        });

        qSenha.setPrefWidth(129); qSenha.setPrefHeight(25);
        qSenha.setText("Senha");
        qSenha.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qIpDns.getText().isEmpty() && !qBaseDados.getText().isEmpty() && !qSenha.getText().isEmpty()) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qIpDns.requestFocus();
            }
        });
        
        grid.add(new Label("Ip/Dns:"), 0, 0);
        grid.add(qIpDns, 1, 0);
        grid.add(new Label("Base de Dados:"), 0, 1);
        grid.add(qBaseDados,1,1);
        grid.add(new Label("Senha:"), 0, 2);
        grid.add(qSenha, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qIpDns.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return new pUnidades(qIpDns.getText(), qBaseDados.getText(), qSenha.isSelected());
            }
            return null;
        });        
        
        Optional<pUnidades> result = dialog.showAndWait();
        return result;
    }    
}
