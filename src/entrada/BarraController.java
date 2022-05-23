package entrada;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.Internet;
import Funcoes.VariaveisGlobais;
import entrada.BuscaGlobal.BuscaGlobal;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javax.rad.genui.UIColor;
import javax.rad.genui.UIImage;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class BarraController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    private BuscaGlobal controller = null;
    private boolean ativaBusca = false;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField usuario;
    @FXML private TextField horalogado;
    @FXML private TextField osnome;
    @FXML private TextField local;
    @FXML private TextField thermica;
    @FXML private TextField inklaser;
    @FXML private Label relogio;
    @FXML private Label calendario;
    @FXML private TextField buscar;
    @FXML private Circle ledInternet;
    @FXML private Label lblInfo;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> jRelogio()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Checar Internet
        Timeline internetTimeLine = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> InternetColor()));
        internetTimeLine.setCycleCount(Animation.INDEFINITE);
        internetTimeLine.play();

        // Transmissão Automatica ao Banco
        if (VariaveisGlobais.timTimer > 0) {
            Timeline timeUpgrade = new Timeline(new KeyFrame(
                    Duration.millis(VariaveisGlobais.timTimer * 1000),
                    ae -> AtualizaTransAuto()));
            timeUpgrade.setCycleCount(Animation.INDEFINITE);
            timeUpgrade.play();
        }

        try {
            if (VariaveisGlobais.cargo.equalsIgnoreCase("GER")) {
                lblInfo.setText("Dados:");
                horalogado.setText("");
            } else {
                lblInfo.setText("Logado em:");
                horalogado.setText(DbMain.getDateTimeServer().toString());
            }
        } catch (Exception e) {}

        //osnome.setText(System.getProperty("os.name") + " - " + System.getProperty("os.version"));
        // Pegar Visitas pendentes
        VisitasPend();

        thermica.setStyle("-fx-text-fill: green;");
        thermica.setText(VariaveisGlobais.Thermica);
        thermica.setOnMouseReleased(event -> {
            VariaveisGlobais.statPrinterThermica = !VariaveisGlobais.statPrinterThermica;
            if (VariaveisGlobais.statPrinterThermica) {
                thermica.setStyle("-fx-text-fill: green;");
            } else {
                thermica.setStyle("-fx-text-fill: red;");
            }
        });

        inklaser.setStyle("-fx-text-fill: green;");
        inklaser.setText(VariaveisGlobais.Printer);
        inklaser.setOnMouseReleased(event -> {
            VariaveisGlobais.statPrinterInkLaser = !VariaveisGlobais.statPrinterInkLaser;
            if (VariaveisGlobais.statPrinterInkLaser) {
                inklaser.setStyle("-fx-text-fill: green;");
            } else {
                inklaser.setStyle("-fx-text-fill: red;");
            }
        });

        osnome.setOnMouseClicked(event -> {
            if (!osnome.getText().equalsIgnoreCase("0")) {
                ChamaTela("Pendências Imóveis", "/Imoveis/Pendencias/Pendencia.fxml", null);
            }
        });

        buscar.setOnKeyReleased(event -> {
            if (!ativaBusca) {
                AnchorPane root = null;
                FXMLLoader loader = new FXMLLoader();
                try {
                    loader = new FXMLLoader(getClass().getResource("/entrada/BuscaGlobal/BuscaGlobal.fxml"));
                    try { root = (AnchorPane) loader.load(); } catch (Exception e) {e.printStackTrace();}
                } catch (Exception e) {e.printStackTrace();}
                UICustomComponent wrappedRoot = new UICustomComponent(root);

                UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
                internalFrame.setLayout(new UIBorderLayout());
                internalFrame.setModal(false);
                internalFrame.setResizable(false);
                internalFrame.setMaximizable(false);
                internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
                internalFrame.setTitle("Busca Globalizada");
                internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
                internalFrame.setClosable(true);

                internalFrame.setBackground(new UIColor(221,221, 221));

                internalFrame.pack();
                internalFrame.setVisible(true);

                controller = loader.getController();

                ativaBusca = true;
            }
            controller.setBusca(buscar.getText());
            //ativaBusca = controller.getAtiva();
        });
    }

    private void InternetColor() {
        VariaveisGlobais.bInternet = Internet.isInternetAvailable();
        try {
            Color corInternet = Color.RED;
            if (VariaveisGlobais.bInternet) {
                corInternet = Color.GREEN;
            }
            ledInternet.setFill(corInternet);
            ledInternet.setStroke(corInternet);
        } catch (Exception e) {}
    }

    private void jRelogio() {
        Date dhData = new Date();
        try { dhData = DbMain.getDateTimeServer(); } catch (Exception e) {}

        relogio.setText(Dates.DateFormata("HH:mm", dhData));
        calendario.setText(Dates.DateFormata("dd-MM-yyyy", dhData));

        usuario.setText(VariaveisGlobais.usuario);
        if (VariaveisGlobais.local) {
            usuario.setStyle("-fx-text-fill: black;");
        } else {
            usuario.setStyle("-fx-text-fill: red;");
        }
        local.setText(VariaveisGlobais.unidade);
        
        try {
            if (VariaveisGlobais.cargo.equalsIgnoreCase("GER")) {
                lblInfo.setText("Dados:");
                horalogado.setText(DadosSistema());
            } else {
                lblInfo.setText("Logado em:");
                horalogado.setText(DbMain.getDateTimeServer().toString());
            }
        } catch (Exception e) {}
                
    }

    private String DadosSistema() {
        String retorno = "";
        String selectSQL = "SELECT COUNT(*) total FROM proprietarios WHERE exclusao is null;";
        int tprop = 0;
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                tprop = rs.getInt("total");
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}
        
        selectSQL = "SELECT COUNT(*) total FROM locatarios WHERE exclusao is null;";
        int tloc = 0;
        try {
            rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                tloc = rs.getInt("total");
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        selectSQL = "SELECT COUNT(*) total FROM imoveis WHERE exclusao is null;";
        int timo = 0;
        try {
            rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                timo = rs.getInt("total");
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}
        
        retorno = "P: " + tprop + " - I: " + timo + " - L: " + tloc + " - V: " + (timo - tloc);
        return retorno;
    }
    
    private void AtualizaTransAuto() {
        // TODO - Rotinas de transmissão automática dos bancos
    }

    public void VisitasPend() {
        String Sql = "SELECT COUNT(*) AS total FROM visitas WHERE not v_dthrsaida is null and v_dthrdevolucao is null;";
        ResultSet vst = null; int total = 0;
        try {
            vst = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
            while (vst.next()) {
                total = vst.getInt("total");
            }
        } catch (NullPointerException e) {} catch (SQLException sex) {}
        try { DbMain.FecharTabela(vst); } catch (Exception e) {}
        osnome.setText(String.valueOf(total));
    }

    private void ChamaTela(String nome, String url, String icone) {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource(url)); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(false);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);

        internalFrame.setBackground(new UIColor(221,221, 221));

        internalFrame.pack();
        internalFrame.setVisible(true);
    }
}
