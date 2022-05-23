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
public class cpoEmails extends ComboBox<pemailModel> {
    public ComboBox<pemailModel> cpoEmails(ComboBox<pemailModel> email) {
        email.setCellFactory(new Callback<ListView<pemailModel>, ListCell<pemailModel>>() {
            @Override
            public ListCell<pemailModel> call(ListView<pemailModel> arg0) {
                return new ListCell<pemailModel>() {

                    private final HBox hbx;
                    private final TextField qemail;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        qemail = new TextField(); MaskFieldUtil.maxField(qemail, 60);
                        qemail.setPrefWidth(430);
                        hbx = new HBox(qemail);
                    }

                    @Override
                    protected void updateItem(pemailModel item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            qemail.setText(item.getEmail());
                            setGraphic(hbx);
                        }
                    }
                };
            }
        });

        return email;
    }
}
