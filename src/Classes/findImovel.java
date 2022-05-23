/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author supervisor
 */
public class findImovel {
    Dialog<pimoveisModel> dialog;
    DbMain conn = VariaveisGlobais.conexao;

    public Optional<pimoveisModel> findEnderecos() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        dialog.setTitle("Buscador de Imoveis");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 150, 5, 10));

        final TableView<pimoveisModel> tbv = new TableView();
        TableColumn tend = new TableColumn("rgprp");
        tend.setMinWidth(30);
        TableColumn tcom = new TableColumn("rgimv");
        tcom.setMinWidth(30);
        TableColumn tbai = new TableColumn("tipo");
        tbai.setMinWidth(0);
        TableColumn tcid = new TableColumn("ender");
        tcid.setMinWidth(300);
        TableColumn tsit = new TableColumn("situacao");
        tsit.setMinWidth(100);
        tbv.getColumns().addAll(tend, tcom, tbai, tcid, tsit);

        String sql = "SELECT * FROM imoveis ORDER BY i_id;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            List<pimoveisModel> enderecos = new ArrayList<pimoveisModel>();
            while (rs.next()) {
                String xid, xrgprp, xrgimv, xtipo, xend, xsituacao;
                try {xid = rs.getString("i_id");} catch (SQLException e) {xid = null;}
                try {xrgprp = rs.getString("i_rgprp");} catch (SQLException e) {xrgprp = null;}
                try {xrgimv = rs.getString("i_rgimv");} catch (SQLException e) {xrgimv = null;}
                try {xtipo = rs.getString("i_tipo");} catch (SQLException e) {xtipo= null;}
                try {xend = rs.getString("i_end") + ", " + rs.getString("i_num") + " - " + rs.getString("i_cplto") + " / " + rs.getString("i_bairro");} catch (SQLException e) {xend = null;}
                try {xsituacao = rs.getString("i_situacao");} catch (SQLException e) {xsituacao = null;}
                enderecos.add(new pimoveisModel(
                        xid,
                        xrgprp,
                        xrgimv,
                        xtipo,
                        xend,
                        xsituacao
                ));
            }
            rs.close();

            tend.setCellValueFactory(new PropertyValueFactory<>("rgprp"));
            tcom.setCellValueFactory(new PropertyValueFactory<>("rgimv"));
            tbai.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            tcid.setCellValueFactory(new PropertyValueFactory<>("ender"));
            tsit.setCellValueFactory(new PropertyValueFactory<>("situacao"));

            tbv.setItems(FXCollections.observableArrayList(enderecos));
        } catch (SQLException e) {e.printStackTrace();}

        salvarButton.disableProperty().bind(tbv.getSelectionModel().selectedItemProperty().isNull());
/*
        tbv.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                salvarButton.disableProperty().bind(tbv.getSelectionModel().selectedItemProperty().isNull());
            }
        });
*/

        tbv.setPrefWidth(800); tbv.setPrefHeight(400);
        HBox tbvb = new HBox(tbv);
        tbvb.setPrefWidth(800); tbvb.setPrefHeight(400);
        VBox vb = new VBox(tbvb);
        
        dialog.getDialogPane().setContent(vb);

        Platform.runLater(() -> tbvb.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                pimoveisModel imv = tbv.getSelectionModel().getSelectedItem();
                if (imv != null) {
                    return imv;
                }
            }
            return null;
        });        
        
        Optional<pimoveisModel> result = dialog.showAndWait();
        return result;
    }    
    
}
