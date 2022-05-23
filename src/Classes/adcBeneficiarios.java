/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.MaskFieldUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import masktextfield.MaskTextField;

import javax.rad.genui.container.UIFrame;
import java.util.Optional;

/**
 *
 * @author supervisor
 */
public class adcBeneficiarios extends UIFrame {
    Dialog<pBeneficiarios> dialog;
    
    public Optional<pBeneficiarios> adcBeneficiarios() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        
        dialog.setTitle("Inclusão de Beneficiários");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final MaskTextField qcpf = new MaskTextField();
        final MaskTextField qnome = new MaskTextField();
        final DatePicker qnasc = new DatePicker();
        final ComboBox<String> qbanco = new ComboBox<>();
        final MaskTextField qagencia = new MaskTextField();
        final MaskTextField qoperacao = new MaskTextField();
        final MaskTextField qconta = new MaskTextField();

        qcpf.setMask("N!");
        qcpf.setPrefWidth(60); qcpf.setPrefHeight(25);
        qcpf.setPadding(new Insets(0, 2, 0, 0));
        qcpf.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qnome.requestFocus();
           }
        });
        qcpf.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                String tcpf = qcpf.getText();
                if (tcpf != null) {
                    tcpf = tcpf.replace(".", "");
                    tcpf = tcpf.replace("-", "");
                }
                qcpf.setText(tcpf);
            } else {
                // out focus
                try {
                    String tcpf = "";
                    tcpf = qcpf.getText().substring(0, 3) + "." + qcpf.getText().substring(3, 6) + "." + qcpf.getText().substring(6, 9) + "-" + qcpf.getText().substring(9, 11);
                    qcpf.setText(tcpf);
                } catch (Exception e) {}
                if (!MaskFieldUtil.isCpf(qcpf.getText())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Menssagem");
                    alert.setHeaderText("CPF inválido");
                    alert.setContentText("Entre com um CPF válido!!!");

                    alert.showAndWait();
                    qcpf.setText(null); qcpf.requestFocus();
                }
            }
        });

        qnome.setMask("*!");
        qnome.setPrefWidth(200); qnome.setPrefHeight(25);
        qnome.setPadding(new Insets(0, 2, 0, 0));
        qnome.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qnasc.requestFocus();
           }
        });

        qnasc.setPrefWidth(200); qnasc.setPrefHeight(25);
        qnasc.setPadding(new Insets(0, 2, 0, 0));
        qnasc.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qbanco.requestFocus();
           }
        });

        qbanco.setPrefWidth(70); qbanco.setPrefHeight(25);
        qbanco.getItems().addAll("001","033","104","237","341");
        qbanco.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                qagencia.requestFocus();
            }
        });

        qagencia.setMask("N!");
        qagencia.setPrefWidth(30); qagencia.setPrefHeight(25);
        qagencia.setPadding(new Insets(0, 2, 0, 0));
        qagencia.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                qoperacao.requestFocus();
            }
        });

        qoperacao.setMask("N!");
        qoperacao.setPrefWidth(30); qoperacao.setPrefHeight(25);
        qoperacao.setPadding(new Insets(0, 2, 0, 0));
        qoperacao.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                qconta.requestFocus();
            }
        });

        qconta.setMask("N!-A!");
        qconta.setPrefWidth(80); qconta.setPrefHeight(25);
        qconta.setPadding(new Insets(0, 2, 0, 0));
        qconta.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qcpf.getText().isEmpty() && !qnome.getText().isEmpty() &&
                        !qnasc.getEditor().getText().isEmpty() &&
                        !qbanco.getSelectionModel().isEmpty() &&
                        !qagencia.getText().isEmpty() &&
                        !qconta.getText().isEmpty()) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qcpf.requestFocus();
            }
        });

        grid.add(new Label("Cpf:"), 0, 0);
        grid.add(qcpf, 1, 0);
        grid.add(new Label("Nome:"), 0, 1);
        grid.add(qnome,1,1);
        grid.add(new Label("Dt.Nasc:"), 0, 2);
        grid.add(qnasc,1,2);
        grid.add(new Label("Banco:"), 0, 3);
        grid.add(qbanco, 1, 3);
        grid.add(new Label("Agencia:"), 0, 4);
        grid.add(qagencia, 1, 4);
        grid.add(new Label("Operação:"), 0, 5);
        grid.add(qoperacao, 1, 5);
        grid.add(new Label("Conta:"), 0, 6);
        grid.add(qconta, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qcpf.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return new pBeneficiarios(
                        qcpf.getText(),
                        qnome.getText(),
                        qnasc,
                        qbanco.getSelectionModel().getSelectedItem(),
                        qagencia.getText(),
                        qoperacao.getText(),
                        qconta.getText()
                );
            }
            return null;
        });        
        
        Optional<pBeneficiarios> result = null;
        try {result = dialog.showAndWait();} catch (NullPointerException e) {}
        return result;
    }

}
