/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.MaskFieldUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import masktextfield.MaskTextField;

/**
 *
 * @author supervisor
 */
public class cpoTelefones extends ComboBox<ptelcontatoModel> {
    public ComboBox<ptelcontatoModel> cpoTelefones(ComboBox<ptelcontatoModel> tels) {
        tels.setCellFactory((ListView<ptelcontatoModel> arg0) -> {
            return new ListCell<ptelcontatoModel>() {
                
                private final HBox hbx;
                private final MaskTextField qddd;
                private final TextField qtel;
                private final ComboBox<String> qtipo;
                {
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    qddd = new MaskTextField(); qddd.setMask("NN");
                    qddd.setPrefWidth(36); qddd.setPrefHeight(25);
                    qddd.setPadding(new Insets(0, 2, 0, 0));
                    qtel = new TextField(); //qtel.setMask("*NNNN-NNNN");
                    qtel.setPrefWidth(95); qtel.setPrefHeight(25);
                    qtel.setPadding(new Insets(0, 2, 0, 0));
                    MaskFieldUtil.foneField(qtel);
                    qtipo = new ComboBox<>();
                    qtipo.setPrefWidth(129); qtipo.setPrefHeight(25);
                    qtipo.getItems().addAll("Residencial","Comercial","Recado");
                    hbx = new HBox(qddd,qtel,qtipo);
                }

                @Override
                protected void updateItem(ptelcontatoModel item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        qddd.setText(item.getDdd());
                        qtel.setText(item.getTelf());
                        qtipo.getSelectionModel().select(item.getTipo());
                        setGraphic(hbx);
                    }
                }
            };
        });
        //new AutoCompleteComboBoxListener<>(tels);
        return tels;
    }
}
