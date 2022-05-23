/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class cpoBancos extends ComboBox<pbancosModel> {
    DbMain conn = VariaveisGlobais.conexao;
    ResultSet rs = null;
    
    public ComboBox<pbancosModel> cpoBancos(ComboBox<pbancosModel> bancos) {
        bancos.setCellFactory((ListView<pbancosModel> arg0) -> {
            return new ListCell<pbancosModel>() {
                
                private final HBox hbx;
                private final ComboBox<String> qbanco;
                private final TextField qagencia;
                private final TextField qconta;
                private final TextField qfavorecido;
                
                {
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    List<String> list = new ArrayList<>();
                    rs = conn.AbrirTabela("SELECT numero, nome FROM bancos ORDER BY numero;", ResultSet.CONCUR_READ_ONLY);
                    try {
                        while  (rs.next()) {
                            list.add(rs.getString("numero"));
                        }
                    } catch (SQLException e) {}
                    DbMain.FecharTabela(rs);

                    ObservableList<String> observableList = observableList(list);
                    qbanco = new ComboBox<>(observableList);
                    qbanco.setPrefWidth(90); qbanco.setPrefHeight(25);

                    qagencia = new TextField(); 
                    qagencia.setPrefWidth(100); qagencia.setPrefHeight(25);
                    qagencia.setPadding(new Insets(0, 2, 0, 0));
                    qconta = new TextField();
                    qconta.setPrefWidth(100); qconta.setPrefHeight(25);
                    qconta.setPadding(new Insets(0, 2, 0, 0));

                    qfavorecido = new TextField();
                    qfavorecido.setPrefWidth(450); qfavorecido.setPrefHeight(25);
                    qfavorecido.setPadding(new Insets(0, 2, 0, 0));
                    hbx = new HBox(qbanco,qagencia,qconta,qfavorecido);
                }

                @Override
                protected void updateItem(pbancosModel item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        qbanco.getSelectionModel().select(item.getBanco());
                        qagencia.setText(item.getAgencia());
                        qconta.setText(item.getConta());
                        qfavorecido.setText(item.getFavorecido() != null && !item.getFavorecido().equalsIgnoreCase("null") ? item.getFavorecido() : "");
                        setGraphic(hbx);
                    }
                }
            };
        });
        //new AutoCompleteComboBoxListener<>(tels);
        return bancos;
    }
}
