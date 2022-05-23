/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.MaskFieldUtil;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 *
 * @author supervisor
 */
public class cpoImoveis extends ComboBox<pimovelModel> {
    public ComboBox<pimovelModel> cpoImoveis(ComboBox<pimovelModel> tpimovel) {
        tpimovel.setCellFactory(new Callback<ListView<pimovelModel>, ListCell<pimovelModel>>() {
            @Override
            public ListCell<pimovelModel> call(ListView<pimovelModel> arg0) {
                return new ListCell<pimovelModel>() {

                    private final HBox hbx;
                    private final TextField qtpimovel;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        qtpimovel = new TextField(); MaskFieldUtil.maxField(qtpimovel, 60);
                        qtpimovel.setPrefWidth(430);
                        hbx = new HBox(qtpimovel);
                    }

                    @Override
                    protected void updateItem(pimovelModel item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            qtpimovel.setText(item.getTpImovel());
                            setGraphic(hbx);
                        }
                    }
                };
            }
        });

        return tpimovel;
    }
}
