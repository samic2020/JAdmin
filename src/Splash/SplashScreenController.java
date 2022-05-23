package Splash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {
    @FXML private Label progress;
    public static Label label;

    @FXML private ImageView samic_logo;
    @FXML private ImageView program_logo;

    @FXML private ProgressBar progressBar;
    public static ProgressBar statProgressBar;

    @FXML private void handleButtonAction(ActionEvent event) { }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label = progress ;
        statProgressBar = progressBar;

        samic_logo.setImage(new Image(getClass().getResourceAsStream("/Figuras/Imagens/samic.png")));
        program_logo.setImage(new Image(getClass().getResourceAsStream("/Figuras/Imagens/login_administrador.jpg")));
    }

}
