package Administrador.Abas.Configuracoes.cnfBoletos;

import Funcoes.DbMain;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class cnfBoletos implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    // Configurações Impressão Boleta
    @FXML
    private TextField pb_logo_width;
    @FXML private TextField pb_logo_Heigth;
    @FXML private CheckBox pb_logo_noprint;
    @FXML private CheckBox pb_razao_noprint;
    @FXML private CheckBox pb_cnpj_noprint;
    @FXML private CheckBox pb_creci_noprint;
    @FXML private CheckBox pb_endereco_noprint;
    @FXML private CheckBox pb_telefone_noprint;
    @FXML private CheckBox pb_copa_print;
    @FXML private CheckBox pb_qcr_print;

    // Instruções do boleto
    @FXML private TextField pb_int01;
    @FXML private TextField pb_int02;
    @FXML private TextField pb_int03;
    @FXML private TextField pb_int04;
    @FXML private TextField pb_int05;
    @FXML private TextField pb_int06;
    @FXML private TextField pb_int07;
    @FXML private TextField pb_int08;
    @FXML private TextField pb_int09;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initializa_pb();
    }

    private void Initializa_pb() {
        pb_logo_width.setText(String.valueOf(VariaveisGlobais.pb_logo_width));
        pb_logo_width.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_logo_width", pb_logo_width.getText().trim());
                VariaveisGlobais.pb_logo_width = Float.parseFloat(pb_logo_width.getText().replace(",", "."));
            }
        });
        pb_logo_Heigth.setText(String.valueOf(VariaveisGlobais.pb_logo_Heigth));
        pb_logo_Heigth.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_logo_heigth", pb_logo_Heigth.getText().trim());
                VariaveisGlobais.pb_logo_Heigth = Float.parseFloat(pb_logo_Heigth.getText().replace(",", "."));
            }
        });

        pb_logo_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_logo_noprint = pb_logo_noprint.isSelected();
                new Settings().Save("pb_logo_noprint", pb_logo_noprint.isSelected() ? "true" : "false");
            }
        });
        pb_logo_noprint.setSelected(VariaveisGlobais.pb_logo_noprint);

        pb_razao_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_razao_noprint = pb_razao_noprint.isSelected();
                new Settings().Save("pb_razao_noprint", pb_razao_noprint.isSelected() ? "true" : "false");
            }
        });
        pb_razao_noprint.setSelected(VariaveisGlobais.pb_razao_noprint);

        pb_cnpj_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_cnpj_noprint = pb_cnpj_noprint.isSelected();
                new Settings().Save("pb_cnpj_noprint", pb_cnpj_noprint.isSelected() ? "true" : "false");
            }
        });
        pb_cnpj_noprint.setSelected(VariaveisGlobais.pb_cnpj_noprint);

        pb_creci_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_creci_noprint = pb_creci_noprint.isSelected();
                new Settings().Save("pb_creci_noprint", pb_creci_noprint.isSelected() ? "true" : "false");
            }
        });
        pb_creci_noprint.setSelected(VariaveisGlobais.pb_creci_noprint);

        pb_endereco_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_endereco_noprint = pb_endereco_noprint.isSelected();
                new Settings().Save("pb_endereco_noprint", pb_endereco_noprint.isSelected() ? "true" : "false");
            }
        });
        pb_endereco_noprint.setSelected(VariaveisGlobais.pb_endereco_noprint);

        pb_telefone_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_telefone_noprint = pb_telefone_noprint.isSelected();
                new Settings().Save("pb_telefone_noprint", pb_telefone_noprint.isSelected() ? "true" : "false");
            }
        });
        pb_telefone_noprint.setSelected(VariaveisGlobais.pb_telefone_noprint);

        pb_copa_print.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_copa_print = pb_copa_print.isSelected();
                new Settings().Save("pb_copa_print", pb_copa_print.isSelected() ? "true" : "false");
            }
        });
        pb_copa_print.setSelected(VariaveisGlobais.pb_copa_print);

        pb_qcr_print.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.pb_qcr_print = pb_qcr_print.isSelected();
                new Settings().Save("pb_qcr_print", pb_qcr_print.isSelected() ? "true" : "false");
            }
        });
        pb_qcr_print.setSelected(VariaveisGlobais.pb_qcr_print);

        // Instruções do Boleto
        pb_int01.setText(VariaveisGlobais.pb_int01);
        pb_int01.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst01", pb_int01.getText().trim());
                VariaveisGlobais.pb_int01 = pb_int01.getText().trim();
            }
        });

        pb_int02.setText(VariaveisGlobais.pb_int02);
        pb_int02.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst02", pb_int02.getText().trim());
                VariaveisGlobais.pb_int02 = pb_int02.getText().trim();
            }
        });

        pb_int03.setText(VariaveisGlobais.pb_int03);
        pb_int03.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst03", pb_int03.getText().trim());
                VariaveisGlobais.pb_int03 = pb_int03.getText().trim();
            }
        });

        pb_int04.setText(VariaveisGlobais.pb_int04);
        pb_int04.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst04", pb_int04.getText().trim());
                VariaveisGlobais.pb_int04 = pb_int04.getText().trim();
            }
        });

        pb_int05.setText(VariaveisGlobais.pb_int05);
        pb_int05.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst05", pb_int05.getText().trim());
                VariaveisGlobais.pb_int05 = pb_int05.getText().trim();
            }
        });

        pb_int06.setText(VariaveisGlobais.pb_int06);
        pb_int06.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst06", pb_int06.getText().trim());
                VariaveisGlobais.pb_int06 = pb_int06.getText().trim();
            }
        });

        pb_int07.setText(VariaveisGlobais.pb_int07);
        pb_int07.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst07", pb_int07.getText().trim());
                VariaveisGlobais.pb_int07 = pb_int07.getText().trim();
            }
        });

        pb_int08.setText(VariaveisGlobais.pb_int08);
        pb_int08.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst08", pb_int08.getText().trim());
                VariaveisGlobais.pb_int08 = pb_int08.getText().trim();
            }
        });

        pb_int09.setText(VariaveisGlobais.pb_int09);
        pb_int09.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("pb_inst09", pb_int09.getText().trim());
                VariaveisGlobais.pb_int09 = pb_int09.getText().trim();
            }
        });

    }
}
