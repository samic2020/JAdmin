/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 *
 * @author supervisor
 */
public class adcImoveis {
    public Optional<pimovelModel> adcImoveis() {
        // Create the custom dialog.
        Dialog<pimovelModel> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/fundo.css").toExternalForm());
        //dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");
        
        dialog.setTitle("Cadastro Tipo de Imóveis");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField qtpimv = new TextField();

        qtpimv.setPrefWidth(430);
        qtpimv.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qtpimv.getText().isEmpty()) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qtpimv.requestFocus();
            }
        });
        
        grid.add(new Label("Tipo Imv:"), 0, 0);
        grid.add(qtpimv, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qtpimv.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return new pimovelModel(qtpimv.getText());
            }
            return null;
        });

        Optional<pimovelModel> result = dialog.showAndWait();
        return result;
    }
}
