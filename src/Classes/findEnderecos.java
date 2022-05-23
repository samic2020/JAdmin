/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.FuncoesGlobais;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class findEnderecos {
    Dialog<ViaCEPEndereco> dialog;
    
    public Optional<ViaCEPEndereco> findEnderecos() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        dialog.setTitle("Buscador de Endereços");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 150, 5, 10));

        final TextField qend = new TextField();
        qend.setPrefWidth(500);

        List<String> listaCid = new ArrayList<>();
        listaCid.add("Niteroi"); listaCid.add("Sao Goncalo");
        listaCid.add("Itaborai"); listaCid.add("Rio de Janeiro");
        ObservableList<String> observableListCid = observableList(listaCid);
        final ComboBox qcidade = new ComboBox<>(observableListCid);
        qcidade.setEditable(true);
        qcidade.getSelectionModel().select("Sao Goncalo");
        qcidade.setPrefWidth(300);
        
        List<String> list = new ArrayList<>();
        list.add("AC"); list.add("AL"); list.add("AP");
        list.add("AM"); list.add("BA"); list.add("CE");
        list.add("DF"); list.add("ES"); list.add("GO");
        list.add("MA"); list.add("MT"); list.add("MS");
        list.add("MG"); list.add("PA"); list.add("PB");
        list.add("PR"); list.add("PE"); list.add("PI");
        list.add("RJ"); list.add("RN"); list.add("RS");
        list.add("RO"); list.add("RR"); list.add("SC");
        list.add("SP"); list.add("SE"); list.add("TO");
        ObservableList<String> observableList = observableList(list);
        final ComboBox<String> qestado = new ComboBox<>(observableList);
        qestado.getSelectionModel().select("RJ");
        
        final TableView<ViaCEPEndereco> tbv = new TableView();
        TableColumn tend = new TableColumn("Endereço");
        tend.setMinWidth(300);
        TableColumn tcom = new TableColumn("Complemento");
        tcom.setMinWidth(300);
        TableColumn tbai = new TableColumn("Bairro");
        tbai.setMinWidth(300);
        TableColumn tcid = new TableColumn("Cidade");
        tcid.setMinWidth(300);
        TableColumn test = new TableColumn("Estado");
        TableColumn tcep = new TableColumn("Cep");
        tbv.getColumns().addAll(tend, tcom, tbai, tcid, test, tcep);

        qend.setPrefWidth(36); qend.setPrefHeight(25);
        qend.setPadding(new Insets(0, 2, 0, 0));
        qend.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qcidade.requestFocus();
           }
        });
        qcidade.setPrefWidth(200); qcidade.setPrefHeight(25);
        qcidade.setPadding(new Insets(0, 2, 0, 0));
        qcidade.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               qestado.requestFocus();
           }
        });
        qestado.setOnKeyReleased(e -> {
           if (e.getCode() == KeyCode.ENTER) {
               tbv.requestFocus();
           }
        });
        qestado.setPrefWidth(129); qestado.setPrefHeight(25);
        qestado.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (!qend.getText().equalsIgnoreCase("") && !qcidade.getSelectionModel().getSelectedItem().equals("")) {
                    try {
                        ViaCEPClient client = new ViaCEPClient();            
                        List<ViaCEPEndereco> enderecos = client.getEnderecos(FuncoesGlobais.myLetra(qestado.getSelectionModel().getSelectedItem()), FuncoesGlobais.myLetra(qcidade.getSelectionModel().getSelectedItem().toString()), FuncoesGlobais.myLetra(qend.getText()));
                        tend.setCellValueFactory(new PropertyValueFactory<>("logradouro"));
                        tcom.setCellValueFactory(new PropertyValueFactory<>("complemento"));
                        tbai.setCellValueFactory(new PropertyValueFactory<>("bairro"));
                        tcid.setCellValueFactory(new PropertyValueFactory<>("localidade"));
                        test.setCellValueFactory(new PropertyValueFactory<>("uf"));
                        tcep.setCellValueFactory(new PropertyValueFactory<>("cep"));

                        if (enderecos.size() != 0) {
                            tbv.setItems(FXCollections.observableArrayList(enderecos));
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Menssagem");
                            alert.setHeaderText("endereço não encontrado!");
                            alert.setContentText("Entre com um Endereço válido!!!");
                            alert.showAndWait();
                        }

                        salvarButton.disableProperty().bind(tbv.getSelectionModel().selectedItemProperty().isNull());
                    } catch (Exception ex) {ex.printStackTrace();}
                }
            }
        });
        
        GridPane hend = new GridPane();
        hend.setHgap(10);
        hend.setVgap(10);
        hend.setPadding(new Insets(0, 150, 0, 10));
        ColumnConstraints column1 = new ColumnConstraints(68);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(100);
        column2.setHgrow(Priority.ALWAYS);
        hend.getColumnConstraints().addAll(column1, column2);
        hend.add(new Label("Endereço:"), 0,0);
        hend.add(qend,1,0);
        hend.setHalignment(qend, HPos.LEFT);
        
        ColumnConstraints column3 = new ColumnConstraints(68);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(50);
        column4.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column3, column4);
        grid.add(new Label("Cidade:"), 0, 0);
        grid.add(qcidade,1,0);
        grid.setHalignment(qcidade, HPos.LEFT);
        
        ColumnConstraints column5 = new ColumnConstraints(68);
        ColumnConstraints column6 = new ColumnConstraints();
        column6.setPercentWidth(20);
        column6.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(column5, column6);
        grid.add(new Label("Estado:"), 2, 0);
        grid.add(qestado, 3, 0);
        grid.setHalignment(qestado, HPos.LEFT);
        
        tbv.setPrefWidth(800); tbv.setPrefHeight(400);
        HBox tbvb = new HBox(tbv);
        tbvb.setPrefWidth(800); tbvb.setPrefHeight(400);
        VBox vb = new VBox(hend, grid, tbvb);
        
        dialog.getDialogPane().setContent(vb);

        Platform.runLater(() -> qend.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                ViaCEPEndereco ender = tbv.getSelectionModel().getSelectedItem();
                if (ender != null) {
                    return ender;
                }
            }
            return null;
        });        
        
        Optional<ViaCEPEndereco> result = dialog.showAndWait();
        return result;
    }    
    
}
