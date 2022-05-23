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
public class cpoMatriculas extends ComboBox<pmatriculaModel> {
    public ComboBox<pmatriculaModel> cpoMatriculas(ComboBox<pmatriculaModel> mat) {
        mat.setCellFactory(new Callback<ListView<pmatriculaModel>, ListCell<pmatriculaModel>>() {
            @Override
            public ListCell<pmatriculaModel> call(ListView<pmatriculaModel> arg0) {
                return new ListCell<pmatriculaModel>() {

                    private final HBox hbx;
                    private final TextField qid;
                    private final TextField qcod;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        qid = new TextField(); MaskFieldUtil.maxField(qid, 3);
                        qid.setPrefWidth(40);
                        
                        qcod = new TextField(); MaskFieldUtil.maxField(qcod, 10);
                        qcod.setPrefWidth(200);
                        hbx = new HBox(qid, qcod);
                    }

                    @Override
                    protected void updateItem(pmatriculaModel item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            qid.setText(item.getId());
                            qcod.setText(item.getCod());
                            setGraphic(hbx);
                        }
                    }
                };
            }
        });

        return mat;
    }
}
