package Editor.activetree;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class adcCondicao {
    Dialog<String> dialog;

    public Optional<String> adcCondicao(String campo) {
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        //dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");

        dialog.setTitle("Condicao");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField qse = new TextField();
        final ComboBox<String> qoperador = new ComboBox<>();
        final TextField qcomparador = new TextField();
        final TextField qverdadeiro = new TextField();
        final TextField qfalso = new TextField();

        qse.setPrefWidth(300); qse.setPrefHeight(25); qse.setText(campo);
        qoperador.setPrefWidth(100); qoperador.setPrefHeight(25);
        qoperador.setItems(new operador().operador());
        qcomparador.setPrefWidth(300); qcomparador.setPrefHeight(25);
        qverdadeiro.setPrefWidth(300); qverdadeiro.setPrefHeight(25);
        qfalso.setPrefWidth(300); qfalso.setPrefHeight(25);

        qfalso.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qoperador.getSelectionModel().getSelectedItem().isEmpty() &&
                        !qcomparador.getText().isEmpty() &&
                        !qverdadeiro.getText().isEmpty() &&
                        !qfalso.getText().isEmpty()) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qoperador.requestFocus();
            }
        });

        grid.add(new Label("Se:"), 0, 0);
        grid.add(qse, 1, 0);
        grid.add(new Label("Operador:"), 0, 1);
        grid.add(qoperador,1,1);
        grid.add(new Label("Comparador:"), 0, 2);
        grid.add(qcomparador,1,2);
        grid.add(new Label("Verdadeiro:"), 0, 3);
        grid.add(qverdadeiro,1,3);
        grid.add(new Label("Falso:"), 0, 4);
        grid.add(qfalso,1,4);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qoperador.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return "Condicao(" + qse.getText().trim() + " " +
                        qoperador.getSelectionModel().getSelectedItem() + " " +
                        qcomparador.getText().trim() + ", " +
                        qverdadeiro.getText() + ", " +
                        qfalso.getText() + ")";
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result;
    }

    public class operador extends ComboBox<String> {
        public ObservableList<String> operador() {
            List<String> list = new ArrayList<>();
            list.add("=="); list.add(">"); list.add(">=");
            list.add("<"); list.add("<="); list.add("!=");
            ObservableList<String> observableList = observableList(list);
            return observableList;
        }
    }
}
