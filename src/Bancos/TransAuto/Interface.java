package Bancos.TransAuto;

import Funcoes.Settings;
import Funcoes.VariaveisGlobais;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Interface implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField remPath;
    @FXML private Button remButton;
    @FXML private TextField tranPath;
    @FXML private Button tranButton;
    @FXML private TextField retPath;
    @FXML private Button retButton;
    @FXML private TextField recPath;
    @FXML private Button recButton;
    @FXML private Slider timTempo;
    @FXML private Label timLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timTempo.valueProperty().addListener((observable, oldValue, newValue) -> {
            new Settings().Save("timTempo", String.valueOf(timTempo.getValue()));
            VariaveisGlobais.timTimer = ((int) timTempo.getValue());
        });
        timLabel.textProperty().bind(Bindings.format("%3.0f", timTempo.valueProperty()));

        remButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            String rPath = remPath.getText().trim();
            if (rPath.isEmpty()) rPath = System.getProperty("user.dir");
            directoryChooser.setInitialDirectory(new File(rPath));
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                remPath.setText(selectedDirectory.getAbsolutePath());
                new Settings().Save("remPath", remPath.getText().trim());
                VariaveisGlobais.remPath = remPath.getText();
            }
        });

        tranButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            String rPath = tranPath.getText().trim();
            if (rPath.isEmpty()) rPath = System.getProperty("user.dir");
            directoryChooser.setInitialDirectory(new File(rPath));
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                tranPath.setText(selectedDirectory.getAbsolutePath());
                new Settings().Save("tranPath", tranPath.getText().trim());
                VariaveisGlobais.traPath = tranPath.getText();
            }
        });

        retButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            String rPath = retPath.getText().trim();
            if (rPath.isEmpty()) rPath = System.getProperty("user.dir");
            directoryChooser.setInitialDirectory(new File(rPath));
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                retPath.setText(selectedDirectory.getAbsolutePath());
                new Settings().Save("retPath", retPath.getText().trim());
                VariaveisGlobais.retPath = retPath.getText();
            }
        });

        recButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            String rPath = recPath.getText().trim();
            if (rPath.isEmpty()) rPath = System.getProperty("user.dir");
            directoryChooser.setInitialDirectory(new File(rPath));
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                recPath.setText(selectedDirectory.getAbsolutePath());
                new Settings().Save("recPath", recPath.getText().trim());
                VariaveisGlobais.recPath = recPath.getText();
            }
        });

        LerParamTransAuto();
    }

    private void LerParamTransAuto() {
        remPath.setText(VariaveisGlobais.remPath);
        tranPath.setText(VariaveisGlobais.traPath);
        recPath.setText(VariaveisGlobais.recPath);
        retPath.setText(VariaveisGlobais.retPath);
        timTempo.setValue(VariaveisGlobais.timTimer);
    }
}
