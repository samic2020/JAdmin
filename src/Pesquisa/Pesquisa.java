package Pesquisa;

import Classes.paramEvent;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.input.KeyCode;
import javax.xml.ws.WebServiceException;
import samic.serversamic.Cnpj;
import samic.serversamic.Consulta;
import samic.serversamic.Cpf;
import samic.serversamic.SamicServer;
import samic.serversamic.SamicServerImplService;

public class Pesquisa implements Initializable {
    private Consulta _consulta;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField pCpfCnpj;
    @FXML private Button btnPesquisar;
    @FXML private TextArea pObservacoes;
    @FXML private Button btnAceitar;
    @FXML private Button btnCancelar;
    @FXML private Label btnClear;

    public Consulta get_consulta() { return _consulta; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pCpfCnpj.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (pCpfCnpj.getText().length() == 11) btnPesquisar.requestFocus();
            }
        });
        
        // Campo só aceita numeros de [0-9]
        pCpfCnpj.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    pCpfCnpj.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        // Limpa campos para nova pesquisa
        btnClear.setOnMouseClicked(event -> {
            pCpfCnpj.setText("");
            pObservacoes.setText("");
            //btnAceitar.setDisable(true);
        });

        btnPesquisar.disableProperty().bind(pCpfCnpj.textProperty().length().greaterThan(11).and(pCpfCnpj.textProperty().length().lessThan(14)));

        //btnAceitar.setDisable(true);

        btnPesquisar.setOnAction(event -> {
            if (pCpfCnpj.getText().length() < 11) {
                    JOptionPane.showMessageDialog(null, "Cpf/Cnpj inválido!");
                    pCpfCnpj.requestFocus();
                    return;                
            }

            // Pesquisar primeiro no webserver samic
            
            SamicServer ss = null;
            Consulta retorno = null;

            try {
                ss = new SamicServerImplService().getSamicServerImplPort();
                retorno = ss.buscaCpfCnpj(VariaveisGlobais.cliente,VariaveisGlobais.estacao, pCpfCnpj.getText());
            } catch (WebServiceException ex) {
                pObservacoes.setText("Servidor SAMIC desligado!\n\nContate o administrador do sistema.");
                _consulta = retorno;

                btnAceitar.setDisable(true);
                btnCancelar.requestFocus();    
                return;
            }
            
            if (retorno.getId() == -1) {
                if (pCpfCnpj.getText().length() == 11) {
                    // Pesquisa CPF
                    Cpf cpfRF = ss.buscaCpfReceita(VariaveisGlobais.cliente,VariaveisGlobais.estacao, pCpfCnpj.getText());
                    if (cpfRF.getCpf() == null) {
                        btnAceitar.setDisable(true);
                        pObservacoes.setText("CPF não Encontrado ou Inválido!\n\nServidor samic - NÃO\nReceita Federal - NÃO");
                    } else {
                        retorno.setCpfcnpj(cpfRF.getCpf());
                        retorno.setNomerazao(cpfRF.getNomeDaPf());

                        //btnAceitar.setDisable(cpfRF.getSituacaoCadastral().equalsIgnoreCase("REGULAR") ? false : true);
                        pObservacoes.setText("Situação Cadastral na RFB - " + cpfRF.getSituacaoCadastral());
                    }
                } else {
                    // Pesquisa CNPJ
                    Cnpj cnpjRF = ss.buscaCnpjReceita(VariaveisGlobais.cliente, VariaveisGlobais.estacao, pCpfCnpj.getText());
                    if (cnpjRF.getCnpj() == null) {
                        btnAceitar.setDisable(true);
                        pObservacoes.setText("CNPJ não Encontrado ou Inválido!\n\nServidor samic - NÃO\nReceita Federal - NÃO");
                    } else {
                        retorno.setCpfcnpj(cnpjRF.getCnpj());
                        retorno.setNomerazao(cnpjRF.getNome());

                        //btnAceitar.setDisable(cnpjRF.getSituacao().equalsIgnoreCase("ATIVA") ? false : true);
                        pObservacoes.setText("Situação Cadastral na RFB - " + cnpjRF.getSituacao());
                    }
                }
            } else {
                if (retorno.isPositivo()) {
                    btnAceitar.setDisable(true);
                    pObservacoes.setText(retorno.getObservacoes());
                } else {
                    btnAceitar.setDisable(false);
                    pObservacoes.setText("Encontrado no webservice samic!");
                }
            }
            _consulta = retorno;
            
            btnAceitar.requestFocus();
        });


        btnCancelar.setOnAction(event -> {
            _consulta = null;
            try {anchorPane.fireEvent(new paramEvent(new Object[] {_consulta},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        });

        btnAceitar.setOnAction(event -> {
            try {
                anchorPane.fireEvent(new paramEvent(new Object[] {_consulta},paramEvent.GET_PARAM));                
            } catch (NullPointerException e) {}
        });

        Platform.runLater(() -> {
            btnAceitar.setDisable(true);
            btnCancelar.setDisable(false);
            pCpfCnpj.requestFocus();
                                });
    }

    private void close(){
         Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }
}
