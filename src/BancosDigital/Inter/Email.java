/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;

/**
 *
 * @author Samic
 */
public class Email {
 @FXML
    private AnchorPane anchorPaneEmail;

    @FXML
    private Button ebtnListar;

    @FXML
    private CheckBox eSys;

    @FXML
    private Button ebtnEnviarTodos;

    @FXML
    private Button eEnviarSel;

    @FXML
    private Button eEditar;

    @FXML
    private ProgressBar epgListar;

    @FXML
    private TextField eAssunto;

    @FXML
    private HTMLEditor eBody;

    @FXML
    private TableView<?> eEnvio;

    @FXML
    private TableColumn<?, ?> eEnvio_Contrato;

    @FXML
    private TableColumn<?, ?> eEnvio_Nome;

    @FXML
    private TableColumn<?, ?> eEnvio_Vencimento;

    @FXML
    private TableColumn<?, ?> eEnvio_Boleta;

    @FXML
    private TableColumn<?, ?> eEnvio_NossoNumero;

    @FXML
    private TableColumn<?, ?> eEnvio_FileName;
    
}
