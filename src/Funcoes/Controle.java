package Funcoes;

import javafx.scene.control.*;
import masktextfield.MaskTextField;

import javax.swing.*;

public class Controle {
    DbMain conn = VariaveisGlobais.conexao;
    private String Chamada = "";
    private Object[] objects;
    private final  String GOTFOCUS = "rgba(255, 255, 255, 0.3),\n" +
            "                         linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.8) 50%),\n" +
            "                         rgb(218, 226, 224);\n" +
            "                         -fx-text-fill: darkblue;";
    private final  String LOSTFOCUS = "-fx-background-color:\n" +
            "                         rgba(235, 235, 235, 0.5),\n" +
            "                         rgba(0, 0, 0, 0.4),\n" +
            "                         rgb(255, 255, 255);";

    private String ValueOld = null;
    private String ValueNew = null;
    
    public Controle(Object[] objects) { this.objects = objects; }

    public Controle(String chamada, Object[] objects) { 
        this.objects = objects;         
        this.Chamada = chamada;
    }
    
    public void BotaoEnableDisable(Object[][] botao) {
        if (botao.length <= 0) return;
        for (Object jb : this.objects) for (Object[] bt : botao) if (((Button)jb).getId().equalsIgnoreCase(((Button)bt[0]).getId())) try {((Button)jb).setDisable((Boolean)bt[1]);} catch (Exception e) {}
    }

    public void BotaoEnabled(Object[] botao) {
        // Disable all buttons
        for (Object jb : this.objects) try{((Button)jb).setDisable(true);} catch (Exception e) {}
        if (botao.length <= 0) return;
        // Enable same buttons
        for (Object jb : this.objects) {
            for (Object bt : botao) {
                if (((Button)jb).getId().equalsIgnoreCase(((Button)bt).getId())) try {((Button)jb).setDisable(false);} catch (Exception e) {}
            }
        }
    }

    public void BotaoDisabled(Object[] botao) {
        // Disable all buttons
        for (Object jb : this.objects) try {((Button)jb).setDisable(false);} catch (Exception e) {}
        if (botao.length <= 0) return;
        // Enable same buttons
        for (Object jb : this.objects) {
            for (Object bt : botao) {
                if (((Button)jb).getId().equalsIgnoreCase(((Button)bt).getId())) try {((Button)jb).setDisable(true);} catch (Exception e) {}
            }
        }
    }

    public void FieldsEnabled(boolean value) {
        for (Object jb : this.objects) {
            //System.out.println(jb.getClass().getSimpleName());
            switch (jb.getClass().getSimpleName()) {
                case "TextField":
                    ((TextField)jb).setEditable(value);
                    break;
                case "Label":
                    ((Label)jb).setDisable(!value);
                    break;
                case "ComboBox":
                    //((ComboBox)jb).setEditable(value);
                    //((ComboBox)jb).getSelectionModel().select(((ComboBox)jb).getSelectionModel().getSelectedIndex());
                    //((ComboBox)jb).setDisable(!value);
                    break;
                case "Spinner":
                    ((Spinner)jb).setEditable(value);
                    break;
                case "CheckBox":
                    ((CheckBox)jb).setDisable(!value);
                    break;
                case "JTextArea":
                    ((JTextArea)jb).setEditable(value);
                    break;
                case "MaskTextField":
                    ((MaskTextField)jb).setEditable(value);
                    break;
                case "TreeView":
                    ((TreeView)jb).setEditable(value);
                    break;
                case "TableView":
                    ((TableView)jb).setEditable(value);
                    break;
                case "DatePicker":
                    ((DatePicker)jb).setEditable(value);
                    break;
                case "MenuButton":
                    for ( int i = 0; i <= ((MenuButton)jb).getItems().size() - 1; i++) {
                        ((MenuButton)jb).getItems().get(i).setDisable(!value);
                    }
                    break;
                case "Button":
                    ((Button)jb).setDisable(!value);
                    break;
            }
        }
    }

    public void Focus() {
        for (final Object jb : this.objects) {
            if (jb instanceof TextField) {
                ((TextField)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        // gotfocus
                        ValueOld = ((TextField)jb).getText();
                        if (((TextField)jb).isEditable()) {
                            ((TextField)jb).setStyle(GOTFOCUS);
                            if (((TextField)jb).getText() != null) ((TextField)jb).setText(((TextField)jb).getText().toUpperCase());
                        }
                    } else {
                        // lostfocus
                        if (((TextField)jb).isEditable()) {
                            ((TextField) jb).setStyle(LOSTFOCUS);
                            if (((TextField)jb).getText() != null) {                                
                                if (!ValueOld.trim().equalsIgnoreCase(((TextField)jb).getText().trim())) conn.Auditor(this.Chamada + ValueOld, ((TextField)jb).getText());
                                ((TextField)jb).setText(StringManager.ConvStr(((TextField)jb).getText()));
                            }
                        }
                    }

                });
            } else if (jb instanceof ComboBox) {
                ((ComboBox)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        // gotfocus
                        ValueOld = ((ComboBox)jb).getEditor().getText();
                        if (((ComboBox)jb).isEditable()) {
                            ((ComboBox)jb).getEditor().setStyle(GOTFOCUS);
                        }
                    } else {
                        //lostfocus
                        if (!ValueOld.trim().equalsIgnoreCase(((ComboBox)jb).getEditor().getText().trim())) conn.Auditor(this.Chamada + ValueOld, ((ComboBox)jb).getEditor().getText());
                        if (((ComboBox)jb).isEditable()) {
                            ((ComboBox)jb).getEditor().setStyle(LOSTFOCUS);
                        }
                    }
                });
            } else if (jb instanceof Spinner) {
                if (((Spinner)jb).isEditable()) {
                    ((Spinner)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            //gotfocus
                            ValueOld = ((Spinner)jb).getEditor().getText();
                            ((Spinner)jb).getEditor().setStyle(GOTFOCUS);
                        } else {
                            //lostfocus
                            if (!ValueOld.trim().equalsIgnoreCase(((Spinner)jb).getEditor().getText().trim())) conn.Auditor(this.Chamada + ValueOld, ((Spinner)jb).getEditor().getText());
                            ((Spinner)jb).getEditor().setStyle(LOSTFOCUS);
                        }
                    });
                }
            } else if (jb instanceof CheckBox) {
                if (!((CheckBox)jb).isDisable()) {
                    ((CheckBox)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            //gotfocus
                            ValueOld = ((CheckBox)jb).isPressed() ? "TRUE" : "FALSE";
                            ((CheckBox)jb).setStyle(GOTFOCUS);
                        } else {
                            //lostfocus
                            if (!ValueOld.trim().equalsIgnoreCase(((CheckBox)jb).isPressed() ? "TRUE" : "FALSE")) conn.Auditor(this.Chamada + ValueOld, ((CheckBox)jb).isPressed() ? "TRUE" : "FALSE");
                            ((CheckBox)jb).setStyle(LOSTFOCUS);
                        }
                    });
                }
            } else if (jb instanceof TextArea) {
                if (((TextArea)jb).isEditable()) {
                    ((TextArea)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            ValueOld = ((TextArea)jb).getText();
                            //gotfocus
                            ((TextArea)jb).setStyle(GOTFOCUS);
                        } else {
                            //lostfocus
                            if (!ValueOld.trim().equalsIgnoreCase(((TextArea)jb).getText().trim())) conn.Auditor(this.Chamada + ValueOld, ((TextArea)jb).getText());
                            ((TextArea)jb).setStyle(LOSTFOCUS);
                        }
                    });
                }
            } else if (jb instanceof MaskTextField) {
                if (((MaskTextField)jb).isEditable()) {
                    ((MaskTextField)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            ValueOld = ((MaskTextField)jb).getText();
                            //gotfocus
                            ((MaskTextField)jb).setStyle(GOTFOCUS);
                        } else {
                            //lostfocus
                            if (!ValueOld.trim().equalsIgnoreCase(((MaskTextField)jb).getText().trim())) conn.Auditor(this.Chamada + ValueOld, ((MaskTextField)jb).getText());
                            ((MaskTextField)jb).setStyle(LOSTFOCUS);
                        }
                    });
                }
            } else if (jb instanceof DatePicker) {
                if (((DatePicker)jb).isEditable()) {
                    ((DatePicker)jb).focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            //gotfocus
                            ValueOld = ((DatePicker)jb).getValue().toString();
                            ((DatePicker)jb).getEditor().setStyle(GOTFOCUS);
                        } else {
                            //lostfocus
                            if (!ValueOld.trim().equalsIgnoreCase(((DatePicker)jb).getValue().toString().trim())) conn.Auditor(this.Chamada + ValueOld, ((DatePicker)jb).getValue().toString());
                            ((DatePicker)jb).getEditor().setStyle(LOSTFOCUS);
                        }
                    });
                }
            }

        }
    }

    public boolean ChecaVazio() {
        for (Object item : this.objects ) {
            if (item instanceof TextField) {
                if (((TextField)item).getText().isEmpty()) {
                    Alert msg = new Alert(Alert.AlertType.WARNING, "Campo obrigatório!");
                    msg.showAndWait();
                    ((TextField) item).requestFocus();
                    return false;
                }
            } else if(item instanceof ComboBox) {
                if (((ComboBox)item).getSelectionModel().isEmpty()) {
                    Alert msg = new Alert(Alert.AlertType.WARNING, "Campo obrigatório!");
                    msg.showAndWait();
                    ((ComboBox) item).requestFocus();
                    return false;
                }
            } else if (item instanceof  DatePicker) {
                if (((DatePicker)item).getEditor().getText().isEmpty()) {
                    Alert msg = new Alert(Alert.AlertType.WARNING, "Campo obrigatório!");
                    msg.showAndWait();
                    ((DatePicker) item).requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

}
