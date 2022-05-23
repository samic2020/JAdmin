/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class adcBancos {
    DbMain conn = VariaveisGlobais.conexao;
    ResultSet rs = null;
    Dialog<pbancosModel> dialog;
    
    public Optional<pbancosModel> adcBancos() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        //dialogPane.getStylesheets().add(getClass().getResource("/css/combobox.css").toExternalForm());
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

        List<String> list = new ArrayList<>();
        rs = conn.AbrirTabela("SELECT numero, nome FROM bancos ORDER BY numero;", ResultSet.CONCUR_READ_ONLY);
        try {
            while  (rs.next()) {
                list.add(rs.getString("numero"));
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);
        
        ObservableList<String> observableList = observableList(list);
        final ComboBox<String> qbancos = new ComboBox<>(observableList);
        //qbancos.getSelectionModel().select(""); Banco padrão da adm
        
        final TextField qagencia = new TextField();
        final TextField qconta = new TextField();
        final TextField qfavorecido = new TextField();
        
        qbancos.setPrefWidth(86); qbancos.setPrefHeight(25);
        qbancos.setPadding(new Insets(0, 2, 0, 0));
        qbancos.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qagencia.requestFocus();
           }
        });

        qagencia.setPrefWidth(95); qagencia.setPrefHeight(25);
        qagencia.setPadding(new Insets(0, 2, 0, 0));
        qagencia.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qconta.requestFocus();
           }
        });
        
        qconta.setPrefWidth(129); qconta.setPrefHeight(25);
        qconta.setPadding(new Insets(0, 2, 0, 0));
        qconta.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qfavorecido.requestFocus();
           }
        });

        qfavorecido.setPrefWidth(229); qfavorecido.setPrefHeight(25);
        qfavorecido.setPadding(new Insets(0, 2, 0, 0));
        qfavorecido.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qagencia.getText().equalsIgnoreCase("") && !qconta.getText().equalsIgnoreCase("")) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qbancos.requestFocus();
            }
        });
        
        grid.add(new Label("Banco:"), 0, 0);
        grid.add(qbancos, 1, 0);
        grid.add(new Label("Agência:"), 0, 1);
        grid.add(qagencia,1,1);
        grid.add(new Label("Conta:"), 0, 2);
        grid.add(qconta, 1, 2);
        grid.add(new Label("Favorecido:"), 0, 3);
        grid.add(qfavorecido, 1, 3);
        
        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qbancos.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return new pbancosModel(qbancos.getSelectionModel().getSelectedItem().toString(), qagencia.getText(), qconta.getText(), qfavorecido.getText());
            }
            return null;
        });        
        
        Optional<pbancosModel> result = dialog.showAndWait();
        return result;
    }    
}
