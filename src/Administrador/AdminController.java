package Administrador;

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

/**
 * Created by supervisor on 12/01/17.
 */
public class AdminController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    // DadodAdm
    @FXML private AnchorPane anchorPaneDadosAdm;
    // Contas Adm
    @FXML private AnchorPane anchorPaneContasAdm;
    // Contas Boletas
    @FXML private AnchorPane anchorPaneContasBoletas;
    // Bancos Adm
    @FXML private AnchorPane anchorPaneBancoAdm;
    // Contas Email
    @FXML private AnchorPane anchorPaneContasEmail;
    // Mensagens
    @FXML private AnchorPane anchorPaneMsg;
    // Mensagens
    @FXML private AnchorPane anchorPaneConfig;
    // MU/JU/CO/EP
    @FXML private AnchorPane anchorPaneMJCE;
    // Whatsapp
    @FXML private AnchorPane anchorPaneWhatsapp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChamaDadosAdm();
        ChamaContasAdm();
        ChamaContasBancos();
        ChamaBancosAdm();
        ChamaContasEmail();
        ChamaMsg();
        ChamaConfig();
        ChamaMJCE();
        ChamaWhatsapp();
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

    private void ChamaDadosAdm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/DadosAdm/DadosAdm.fxml"));
            Pane root = loader.load();
            anchorPaneDadosAdm.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaContasAdm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/ContasAdmin/ContasAdmin.fxml"));
            Pane root = loader.load();
            anchorPaneContasAdm.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaContasBancos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/ContasBoleta/ContasBoleta.fxml"));
            Pane root = loader.load();
            anchorPaneContasBoletas.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaBancosAdm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/ContaAdm/ContaAdm.fxml"));
            Pane root = loader.load();
            anchorPaneBancoAdm.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaContasEmail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/ContasEmail/ContasEmail.fxml"));
            Pane root = loader.load();
            anchorPaneContasEmail.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaMsg() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Mensagens/Mensagens.fxml"));
            Pane root = loader.load();
            anchorPaneMsg.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaConfig() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Configuracoes/Configuracoes.fxml"));
            Pane root = loader.load();
            anchorPaneConfig.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaMJCE() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/MuJuCoEp/MuJuCoEp.fxml"));
            Pane root = loader.load();
            anchorPaneMJCE.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaWhatsapp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Administrador/Abas/Whatsapp/Whatsapp.fxml"));
            Pane root = loader.load();
            anchorPaneWhatsapp.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
