/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Locatarios;

import Classes.pimoveisModel;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author supervisor
 */
public class inclusaoLocatario {
    Dialog<pimoveisModel> dialog;
    DbMain conn = VariaveisGlobais.conexao;
    ResultSet rs = null;

    private void PopulateProprietarios(ComboBox<String> il_proprietario) {
        String sqlTxt = "SELECT DISTINCT p.p_rgprp, p.p_nome FROM proprietarios p, imoveis i WHERE p.p_rgprp = CAST(i.i_rgprp AS int) AND Upper(i.i_situacao) = 'VAZIO' ORDER BY p.p_rgprp;";
        rs = conn.AbrirTabela(sqlTxt, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String cbx = rs.getString("p_rgprp") + " - " + rs.getString("p_nome");
                il_proprietario.getItems().addAll(cbx);
            }
        } catch (SQLException e) {e.printStackTrace();}
        try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    public Optional<pimoveisModel> inclusaoLocatario() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        dialog.setTitle("Inclusão de Locatários");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 150, 5, 10));

        final ComboBox<String> prop = new ComboBox<String>();
        prop.setPrefWidth(350); PopulateProprietarios(prop);

        final TableView<pimoveisModel> tbv = new TableView();
        TableColumn trgimv = new TableColumn("RgIMV");
        TableColumn tend = new TableColumn("Endereço");
        tend.setMinWidth(400);
        tbv.getColumns().addAll(trgimv, tend);

        prop.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (!prop.getSelectionModel().getSelectedItem().equalsIgnoreCase("")) {
                    String rgprp = prop.getSelectionModel().getSelectedItem();
                    rgprp = rgprp.substring(0,rgprp.indexOf("-") - 1);

                    List<pimoveisModel> data = new ArrayList<pimoveisModel>();
                    ResultSet imv;
                    //String qSQL = "SELECT i_rgprp, i_rgimv, i_tipo, i_end || ', ' || i_num || ', ' || i_cplto || ' - ' || i_bairro || ' CEP: ' || i_cep AS i_ender, i_situacao, i_tipo, i_fusao FROM imoveis WHERE i_rgprp = '" + rgprp + "' AND UPPER(i_situacao) = 'VAZIO' ORDER BY i_rgimv, i_fusao;";
                    String qSQL = "SELECT i_rgprp, i_rgimv, i_tipo, i_end, i_num, i_cplto, i_bairro, i_cep, i_situacao, i_tipo, i_fusao FROM imoveis WHERE i_rgprp = '" + rgprp + "' AND UPPER(i_situacao) = 'VAZIO' ORDER BY i_rgimv, i_fusao;";
                    try {
                        imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
                        String tender = ""; String tfusao = ""; String tcplto = "";
                        String qrgprp = null,
                                qrgimv = null,
                                qender = null,
                                qnum = null,
                                qcplto = null,
                                qbairro = null,
                                qcep = null,
                                qtipo = null,
                                qfusao = null;
                        while (imv.next()) {
                            try {qrgprp = imv.getString("i_rgprp");} catch (SQLException e) {qrgprp = null;}
                            try {qrgimv = imv.getString("i_rgimv");} catch (SQLException e) {qrgimv = null;}
                            try {qender = imv.getString("i_end");} catch (SQLException e) {qender = null;}
                            try {qnum = imv.getString("i_num");} catch (SQLException e) {qnum = null;}
                            try {qcplto = imv.getString("i_cplto");} catch (SQLException e) {qcplto = null;}
                            try {qbairro = imv.getString("i_bairro");} catch (SQLException e) {qbairro = null;}
                            try {qcep = imv.getString("i_cep");} catch (SQLException e) {qcep = null;}
                            try {qtipo = imv.getString("i_tipo");} catch (SQLException e) {qtipo = null;}
                            try {qfusao = imv.getString("i_fusao");} catch (SQLException e) {qfusao = null;}

                            String ttender = qender + ", " + qnum + " " + qcplto;
                            //if (!tfusao.equalsIgnoreCase(qfusao)) {
                                data.add(new pimoveisModel(qrgprp, qrgimv, qtipo, ttender, null));
                                tfusao = qfusao;
                            //} else {
                            //    tfusao = qfusao;
                            //}
                        }
                        imv.close();
                    } catch (SQLException e) {}

                    trgimv.setCellValueFactory(new PropertyValueFactory<>("rgimv"));
                    tend.setCellValueFactory(new PropertyValueFactory<>("ender"));

                    tbv.setItems(FXCollections.observableArrayList(data));
                    salvarButton.disableProperty().bind(tbv.getSelectionModel().selectedItemProperty().isNull());
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
        hend.add(new Label("Proprietário:"), 0,0);
        hend.add(prop,1,0);
        hend.setHalignment(prop, HPos.LEFT);

        tbv.setPrefWidth(500); tbv.setPrefHeight(200);
        HBox tbvb = new HBox(tbv);
        tbvb.setPrefWidth(500); tbvb.setPrefHeight(200);
        VBox vb = new VBox(hend, grid, tbvb);
        
        dialog.getDialogPane().setContent(vb);

        Platform.runLater(() -> prop.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                pimoveisModel ender = tbv.getSelectionModel().getSelectedItem();
                if (ender != null) {
                    return ender;
                }
            }
            return null;
        });        
        
        Optional<pimoveisModel> result = null;
        try {result = dialog.showAndWait();} catch (Exception e) {}
        return result;
    }    
}
