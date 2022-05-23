package Classes;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import masktextfield.MaskTextField;

/**
 * Created by supervisor on 18/01/17.
 */
public class telCombo extends ComboBox<ptelcontatoModel> {
    public ComboBox<ptelcontatoModel> telCombo(ComboBox<ptelcontatoModel> tels) {
        tels.setCellFactory((ListView<ptelcontatoModel> arg0) -> {
            return new ListCell<ptelcontatoModel>() {

                private final HBox hbx;
                private final MaskTextField qddd;
                private final MaskTextField qtel;
                private final ComboBox<String> qtipo;
                //private final Button plus;
                //private final Button minus;

                {
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    qddd = new MaskTextField(); qddd.setMask("NN");
                    qddd.setPrefWidth(20); qddd.setPrefHeight(25);
                    qddd.setPadding(new Insets(0, 2, 0, 0));
                    qtel = new MaskTextField(); qtel.setMask("*NNNN-NNNN");
                    qtel.setPrefWidth(80); qtel.setPrefHeight(25);
                    qtel.setPadding(new Insets(0, 2, 0, 0));
                    qtipo = new ComboBox<>();
                    qtipo.setPrefWidth(120); qtipo.setPrefHeight(25);
                    qtipo.getItems().addAll("Residencial","Comercial","Celular","Recado");

                    //plus = new Button("+");
                    //minus = new Button("-");

                    hbx = new HBox(qddd,qtel,qtipo); //,plus,minus);
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
        return tels;
    }
}

