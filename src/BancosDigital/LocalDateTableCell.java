package BancosDigital;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateTableCell<T> extends TableCell<T, LocalDate> {
    private final DateTimeFormatter parser = DateTimeFormatter.ofPattern("d/M/yyyy");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DatePicker datePicker;

    public LocalDateTableCell(TableColumn<T, LocalDate> column) {
        this.datePicker = new DatePicker();
        this.datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                String rv = null;
                if (object != null) {
                    rv = formatter.format(object);
                }
                return rv;
            }

            @Override
            public LocalDate fromString(String string) {
                LocalDate rv = null;
                if (!Optional.ofNullable(string).orElse("").isEmpty()) {
                    rv = LocalDate.parse(string, parser);
                }
                return rv;
            }
        });

        this.datePicker.getEditor().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    final TableView<T> tableView = getTableView();
                    tableView.getSelectionModel().select(getTableRow().getIndex());
                    tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
                } else {
                    try {
                        LocalDate data = LocalDate.parse(datePicker.getEditor().textProperty().get(), parser);
                        commitEdit(data);
                    } catch (Exception e) {
                        // If error in parsing I set the previous value
                        cancelEdit();
                        if (datePicker.getValue() != null) datePicker.getEditor().setText(formatter.format(datePicker.getValue()));
                    }
                }
            }
        });

        this.datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditing()) {
                commitEdit(newValue);
            }
        });

        editableProperty().bind(column.editableProperty());
        contentDisplayProperty().bind(Bindings.when(editableProperty()).then(ContentDisplay.GRAPHIC_ONLY).otherwise(ContentDisplay.TEXT_ONLY));
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            // Datepicker can handle null values
            this.datePicker.setValue(item);
            setGraphic(this.datePicker);

            if (item == null) {
                setText(null);
            } else {
                setText(formatter.format(item));
            }
        }
    }
}