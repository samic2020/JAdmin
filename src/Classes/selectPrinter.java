package Classes;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.print.PrintService;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class selectPrinter {
    Dialog dialog;

    public Optional<String> selectPrinter() {
        dialog = new Dialog();

        dialog.setTitle("Selecione uma impressora instalada no sistema");

        ButtonType salvarButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(2);
        grid.setPadding(new Insets(5, 150, 5, 10));

        final ListView listView = new ListView();
        PrintService[] pservices = PrinterJob.lookupPrintServices();

        List<String> data = new ArrayList<>();

        if (pservices.length > 0) {
            for (PrintService ps : pservices) {
                data.add(ps.getName());
            }
        }
        listView.setItems(FXCollections.observableArrayList(data));
        salvarButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());

        listView.setPrefWidth(800); listView.setPrefHeight(400);
        HBox tbvb = new HBox(listView);
        tbvb.setPrefWidth(800); tbvb.setPrefHeight(400);
        VBox vb = new VBox(tbvb);

        dialog.getDialogPane().setContent(vb);

        Platform.runLater(() -> listView.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                String sprint = listView.getSelectionModel().getSelectedItem().toString();
                if (sprint != null) {
                    return sprint;
                }
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result;
    }
}
