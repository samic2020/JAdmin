package Administrador.Abas.Configuracoes;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.net.URL;
import java.util.ResourceBundle;

public class Configuracoes implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPaneConfig;
    @FXML private AnchorPane anchorPanecnfRecibos;
    @FXML private AnchorPane anchorPanecnfBoletos;
    @FXML private AnchorPane anchorPanecnfSistema;
    @FXML private AnchorPane anchorPanecnfDimob;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChamaRecibos();
        ChamaBoletos();
        ChamaSistemas();
        ChamaDimob();
    }

    private void ChamaRecibos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Configuracoes/cnfRecibo/cnfRecibo.fxml"));
            Pane root = loader.load();
            anchorPanecnfRecibos.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaBoletos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Configuracoes/cnfBoletos/cnfBoletos.fxml"));
            Pane root = loader.load();
            anchorPanecnfBoletos.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaSistemas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Configuracoes/cnfSistema/cnfSistema.fxml"));
            Pane root = loader.load();
            anchorPanecnfSistema.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaDimob() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Configuracoes/cnfDimob/cnfDimob.fxml"));
            Pane root = loader.load();
            anchorPanecnfDimob.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaTela(String nome, String url, String icone) throws Exception {
        AnchorPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);


        internalFrame.setBackground(new UIColor(221, 221, 221));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);
    }


}
