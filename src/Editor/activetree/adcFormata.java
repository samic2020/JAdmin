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
public class adcFormata {
    Dialog<String> dialog;

    public Optional<String> adcFormata(String campo) {
        dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        //dialogPane.getStylesheets().add(getClass().getResource("/css/background.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/label.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/textfield.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
        dialogPane.getStyleClass().add("background"); dialogPane.getStyleClass().add("label");

        dialog.setTitle("Format");

        // Set the button types.
        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        Node salvarButton = dialog.getDialogPane().lookupButton(salvarButtonType);
        salvarButton.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final ComboBox<String> qtipo = new ComboBox<>();
        final TextField qcampo = new TextField();
        final ComboBox<String> qpattern = new ComboBox<>();

        qtipo.setPrefWidth(60); qtipo.setPrefHeight(25);
        qtipo.setItems(new tipo().tipos());
        qtipo.setOnAction(evt -> {
            qpattern.setValue(null);
            qpattern.getItems().clear();
            int tipo = qtipo.getSelectionModel().getSelectedIndex();
            qpattern.setItems(new pattern().pattern(tipo));
        });

        qcampo.setPrefWidth(100); qcampo.setPrefHeight(25); qcampo.setText(campo);
        qpattern.setPrefWidth(100); qpattern.setPrefHeight(25);

        qpattern.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!qtipo.getSelectionModel().getSelectedItem().isEmpty() && !qpattern.getSelectionModel().getSelectedItem().isEmpty()) {
                    salvarButton.setDisable(false);
                    salvarButton.requestFocus();
                } else qtipo.requestFocus();
            }
        });

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(qtipo, 1, 0);
        grid.add(new Label("Campo:"), 0, 1);
        grid.add(qcampo,1,1);
        grid.add(new Label("Pattern:"), 0, 2);
        grid.add(qpattern,1,2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> qtipo.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                return "Format(" + qcampo.getText().trim() + ", '" +
                        qpattern.getSelectionModel().getSelectedItem().trim() + "', " +
                        qtipo.getSelectionModel().getSelectedIndex() + ")";
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result;
    }

    private class tipo {
        public ObservableList<String> tipos() {
            List<String> list = new ArrayList<>();
            list.add("String"); list.add("Data"); list.add("Currency");
            ObservableList<String> observableList = observableList(list);
            return observableList;
        }
    }

    private class pattern {
        public ObservableList<String> pattern(int tipo) {
            ObservableList<String> retorno;
            if (tipo == 0) {
                retorno = _string();
            } else if (tipo == 1) {
                retorno = _data();
            } else {
                retorno = _currency();
            }
            return retorno;
        }

        private ObservableList<String> _string() {
            List<String> list = new ArrayList<>();
            list.add("###.###.###-##"); list.add("##.###.###/####-##"); list.add("##.###.###-#");
            ObservableList<String> observableList = observableList(list);
            return observableList;
        }

        private ObservableList<String> _data() {
            List<String> list = new ArrayList<>();
            list.add("dd/MM/YYYY"); list.add("dd 'de' MMMM 'de' YYYY"); list.add("hh:mm:ss");
            ObservableList<String> observableList = observableList(list);
            return observableList;
        }

        private ObservableList<String> _currency() {
            List<String> list = new ArrayList<>();
            list.add("Currency"); list.add("#0.00"); list.add("#.##");
            ObservableList<String> observableList = observableList(list);
            return observableList;
        }
    }

}
