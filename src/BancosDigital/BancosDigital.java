package BancosDigital;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BancosDigital implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    @FXML private AnchorPane anchorPaneDigital;
    @FXML private ComboBox<String> bancos;
    @FXML private AnchorPane digitalBancos;

    private int width = 810;
    private int height = 610;
    
    private int dwidth  = 800;
    private int dheight = 650;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPaneDigital.setPrefSize(width, height);
        anchorPaneDigital.setMinSize(width, height);
        anchorPaneDigital.setMaxSize(width, height);
        anchorPaneDigital.resize(width, height);
        
        digitalBancos.setPrefSize(dwidth, dheight);
        digitalBancos.setMinSize(dwidth, dheight);
        digitalBancos.setMaxSize(dwidth, dheight);
        digitalBancos.resize(dwidth, dheight);
        
        FillBancos();
    }

    private void FillBancos() {
        String sSql = "SELECT b.numero codigo, b.nome FROM bancos b WHERE EXISTS(SELECT bd.nbanco FROM bancos_digital bd WHERE b.numero = bd.nbanco LIMIT 1);";

        bancos.getItems().removeAll();
        ResultSet rs = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (rs.next()) {
                bancos.getItems().add(rs.getString("codigo") + " - " + rs.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(rs);

        bancos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("Inter")) {
                ChamaBancoDigital("/BancosDigital/Inter/BancoInter.fxml");
            }
        });

        bancos.getSelectionModel().select(0);
    }

    private void ChamaBancoDigital(String chamada) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(chamada));
            Pane root = loader.load();
            digitalBancos.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(-10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
