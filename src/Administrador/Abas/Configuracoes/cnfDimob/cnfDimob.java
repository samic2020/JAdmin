package Administrador.Abas.Configuracoes.cnfDimob;

import Funcoes.DbMain;
import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class cnfDimob implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private CheckBox dip_tx;
    @FXML private CheckBox dip_mu;
    @FXML private CheckBox dip_ju;
    @FXML private CheckBox dip_co;
    @FXML private CheckBox dip_ep;
    @FXML private CheckBox dia_mu;
    @FXML private CheckBox dia_ju;
    @FXML private CheckBox dia_co;
    @FXML private CheckBox dia_ep;
    @FXML private CheckBox dia_tx;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Init_Dimob();
    }

    private void Init_Dimob() {
        dip_tx.setSelected(Boolean.valueOf(VariaveisGlobais.dip_tx));
        dip_tx.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dip_tx = dip_tx.isSelected();
                new Settings().Save("dip_tx", dip_tx.isSelected() ? "true" : "false");
            }
        });

        dip_mu.setSelected(Boolean.valueOf(VariaveisGlobais.dip_mu));
        dip_mu.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dip_mu = dip_mu.isSelected();
                new Settings().Save("dip_mu", dip_mu.isSelected() ? "true" : "false");
            }
        });

        dip_co.setSelected(Boolean.valueOf(VariaveisGlobais.dip_co));
        dip_co.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dip_co = dip_co.isSelected();
                new Settings().Save("dip_co", dip_co.isSelected() ? "true" : "false");
            }
        });

        dip_ju.setSelected(Boolean.valueOf(VariaveisGlobais.dip_ju));
        dip_ju.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dip_ju = dip_ju.isSelected();
                new Settings().Save("dip_ju", dip_ju.isSelected() ? "true" : "false");
            }
        });

        dip_ep.setSelected(Boolean.valueOf(VariaveisGlobais.dip_ep));
        dip_ep.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dip_ep = dip_ep.isSelected();
                new Settings().Save("dip_ep", dip_ep.isSelected() ? "true" : "false");
            }
        });

        dia_tx.setSelected(Boolean.valueOf(VariaveisGlobais.dia_tx));
        dia_tx.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dia_tx = dia_tx.isSelected();
                new Settings().Save("dia_tx", dia_tx.isSelected() ? "true" : "false");
            }
        });

        dia_mu.setSelected(Boolean.valueOf(VariaveisGlobais.dia_mu));
        dia_mu.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dia_mu = dia_mu.isSelected();
                new Settings().Save("dia_mu", dia_mu.isSelected() ? "true" : "false");
            }
        });

        dia_ju.setSelected(Boolean.valueOf(VariaveisGlobais.dia_ju));
        dia_ju.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dia_ju = dia_ju.isSelected();
                new Settings().Save("dia_ju", dia_ju.isSelected() ? "true" : "false");
            }
        });

        dia_co.setSelected(Boolean.valueOf(VariaveisGlobais.dia_co));
        dia_co.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dia_co = dia_co.isSelected();
                new Settings().Save("dia_co", dia_co.isSelected() ? "true" : "false");
            }
        });

        dia_ep.setSelected(Boolean.valueOf(VariaveisGlobais.dia_ep));
        dia_ep.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.dia_ep = dia_ep.isSelected();
                new Settings().Save("dia_ep", dia_ep.isSelected() ? "true" : "false");
            }
        });

    }
}
