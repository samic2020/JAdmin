package BancosDigital.Inter;

import Funcoes.VariaveisGlobais;
import Movimento.Boletas.BoletasController;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javafx.util.Callback;
import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;

public class BancoInter implements Initializable {

    @FXML private AnchorPane anchorPaneBoletos;
    @FXML private AnchorPane anchorPaneEmail;
    @FXML private AnchorPane anchorPaneConsulta;
    @FXML private AnchorPane anchorPaneAvulsas;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChamaBoletos();
        ChamaEmail();
        ChamaConsulta();
        ChamaAvulsas();
    }

    private void ChamaBoletos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BancosDigital/Inter/Boletos.fxml"));
            Pane root = loader.load();
            anchorPaneBoletos.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaEmail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BancosDigital/Inter/Email.fxml"));
            Pane root = loader.load();
            anchorPaneEmail.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaConsulta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BancosDigital/Inter/Consulta.fxml"));
            Pane root = loader.load();
            anchorPaneConsulta.getChildren().add(root);
            root.setLayoutX(0);
            root.setLayoutY(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChamaAvulsas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BancosDigital/Inter/Avulsas.fxml"));
            Pane root = loader.load();
            anchorPaneAvulsas.getChildren().add(root);
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

    private boolean EstaNaGrade(TableView<BoletasController.pagadores> grade, String variavel, boolean All_or_Selected) {
        boolean retorno = false;
        if (All_or_Selected) {
            ObservableList<BoletasController.pagadores> itens = grade.getItems();
            for (BoletasController.pagadores boleto : itens) {
                if (boleto.getNome().equalsIgnoreCase(variavel)) {
                    retorno = true;
                    break;
                }
            }
        } else {
            ObservableList<BoletasController.pagadores> itens = grade.getSelectionModel().getSelectedItems();
            for (BoletasController.pagadores boleto : itens) {
                if (boleto.getNome().equalsIgnoreCase(variavel)) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    public interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
        @Override
        default TableCell<E, T> call(TableColumn<E, T> param) {
            return new TableCell<E, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(convert(item));
                    }
                }
            };
        }

        String convert(T value);
    }
}