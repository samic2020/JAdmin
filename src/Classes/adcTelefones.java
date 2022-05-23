/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.MaskFieldUtil;
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
public class adcTelefones {
    Dialog<ptelcontatoModel> dialog;
    
    public Optional<ptelcontatoModel> adcTelefones() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        //dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        
        dialog.setTitle("Cadastro de Telefones");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final MaskTextField qddd = new MaskTextField();
        final TextField qtel = new TextField(); //new MaskTextField();
        MaskFieldUtil.foneField(qtel);
        final ComboBox<String> qtipo = new ComboBox<>();

        qddd.setMask("NN");
        qddd.setPrefWidth(36); qddd.setPrefHeight(25);
        qddd.setPadding(new Insets(0, 2, 0, 0));
        qddd.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qtel.requestFocus();
           }
        });
        //qtel.setMask("*NNNN-NNNN");
        qtel.setPrefWidth(95); qtel.setPrefHeight(25);
        qtel.setPadding(new Insets(0, 2, 0, 0));
        qtel.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qtipo.requestFocus();
           }
        });
        
        //MaskFieldUtil.foneField(qtel);
        qtipo.setPrefWidth(129); qtipo.setPrefHeight(25);
        qtipo.getItems().addAll("Residencial","Comercial","Recado");
        qtipo.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qddd.getText().isEmpty() && !qtel.getText().isEmpty() && !qtipo.getSelectionModel().isEmpty()) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qddd.requestFocus();
            }
        });
        
        grid.add(new Label("DDD:"), 0, 0);
        grid.add(qddd, 1, 0);
        grid.add(new Label("Telefone:"), 0, 1);
        grid.add(qtel,1,1);
        grid.add(new Label("Tipo:"), 0, 2);
        grid.add(qtipo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qddd.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return new ptelcontatoModel(qddd.getText(), qtel.getText(), qtipo.getValue());
            }
            return null;
        });        
        
        Optional<ptelcontatoModel> result = dialog.showAndWait();
        return result;
    }    
}
