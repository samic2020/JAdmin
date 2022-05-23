package Administrador;

import Funcoes.MaskFieldUtil;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by supervisor on 01/12/16.
 */
public class EditingCellNumber extends TableCell<EmailAdm, String> {

    private TextField textField;
    private int maxlen;

    public EditingCellNumber(int maxlen) { this.maxlen = maxlen; }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.requestFocus();
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {

        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        MaskFieldUtil.maxField(textField, this.maxlen);
        MaskFieldUtil.numericField(textField);

        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if(newValue == false) {
                //System.out.println( "Focus lost, current value: " + textField.getText());
                commitEdit();
            }
        });

        textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if( e.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }

    private boolean commitEdit() {
        if (!isEditing()) return true;
        super.commitEdit(textField.getText());
        return true;
    }
}
