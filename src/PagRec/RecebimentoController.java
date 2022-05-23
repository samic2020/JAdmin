package PagRec;

import Classes.paramEvent;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.MaskFieldUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import static Funcoes.LerValor.BigDecimalToCurrency;

public class RecebimentoController implements Initializable {
    private BigDecimal valor;
    private BigDecimal lancado;
    private String[][] cheques = {};

    @FXML private AnchorPane anchorPane;
    @FXML private Button btDN;
    @FXML private Button btCH;
    @FXML private Button btBC;
    @FXML private Label lblTrocoResta;
    @FXML private TextField numero;
    @FXML private TextField banco;
    @FXML private TextField agencia;
    @FXML private TextField nrcheque;
    @FXML private DatePicker predata;
    @FXML private TextField vrch;
    @FXML private TextField vrdn;
    @FXML private Button btLancar;
    @FXML private Button btCancelar;
    @FXML private TextField vrresta;
    @FXML private TextField vrtroco;

    int tipoRecebimento = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btDN.setOnKeyReleased(event -> { if (event.getCode() == KeyCode.DOWN) btCH.requestFocus(); });
        btCH.setOnKeyReleased(event -> { if (event.getCode() == KeyCode.UP) btDN.requestFocus(); else if (event.getCode() == KeyCode.DOWN) btBC.requestFocus(); });
        btBC.setOnKeyReleased(event -> { if (event.getCode() == KeyCode.UP) btCH.requestFocus(); });

        btDN.setOnAction(event -> {Dinheiro();});
        btCH.setOnAction(event -> {Cheque();});
        btBC.setOnAction(event -> {Banco();});

        btCancelar.setOnAction(event -> {
            if (tipoRecebimento == 0) {
                anchorPane.getParent().fireEvent(new paramEvent(new String[] {"RECIBO"}, paramEvent.GET_PARAM));
            } else if (tipoRecebimento != 0) {
                Formas_Disable(false);
            }
        });

        btLancar.setOnAction(event -> {
            boolean fimtransacao = false;
            String valorTextField = !vrdn.getText().equalsIgnoreCase("0,00") ? vrdn.getText() : vrch.getText();
            if (valorTextField.trim().equalsIgnoreCase("0,00")) {
                if (tipoRecebimento == 1 || tipoRecebimento == 3) {
                    vrdn.selectAll();
                    vrdn.requestFocus();
                } else {
                    vrch.selectAll();
                    vrch.requestFocus();
                }
                return;
            }
            lancado = lancado.add(new BigDecimal(LerValor.Number2BigDecimal(valorTextField)));

            if (valor.subtract(lancado).compareTo(BigDecimal.ZERO) == -1) {
                // Dar troco
                BigDecimal vrdado = new BigDecimal(LerValor.Number2BigDecimal(valorTextField)).subtract(lancado.subtract(valor));

                if (tipoRecebimento > 1) {
                    // Cheque e Banco os Valores não podem dar troco
                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setHeaderText("Atenção!");
                    alerta.setContentText("Não existe TROCO para " + (tipoRecebimento == 2 ? "Cheque" : "Banco") + "!");
                    alerta.showAndWait();
                    lancado = lancado.subtract(new BigDecimal(LerValor.Number2BigDecimal(valorTextField)));

                    if (tipoRecebimento == 2) Cheque();
                    if (tipoRecebimento == 3) Banco();
                    return;
                }

                try {
                    this.cheques = FuncoesGlobais.ArraysAdd(this.cheques, new String[]{numero.getText(), banco.getText(), agencia.getText(), nrcheque.getText(), predata.getValue().toString(), vrdado.toPlainString()});
                } catch (NullPointerException e) {
                    this.cheques = FuncoesGlobais.ArraysAdd(this.cheques, new String[]{numero.getText(), banco.getText(), agencia.getText(), nrcheque.getText(), "", vrdado.toPlainString()});
                }
                fimtransacao = true;
            } else if (valor.subtract(lancado).compareTo(BigDecimal.ZERO) == 0) {
                // Valor exato
                try {
                    this.cheques = FuncoesGlobais.ArraysAdd(this.cheques, new String[]{numero.getText(), banco.getText(), agencia.getText(), nrcheque.getText(), predata.getValue().toString(), valorTextField});
                } catch (NullPointerException e) {
                    this.cheques = FuncoesGlobais.ArraysAdd(this.cheques, new String[]{numero.getText(), banco.getText(), agencia.getText(), nrcheque.getText(), "", valorTextField});
                }
                fimtransacao = true;
            } else if (valor.subtract(lancado).compareTo(BigDecimal.ZERO) == 1) {
                // Ainda Falta
                try {
                    this.cheques = FuncoesGlobais.ArraysAdd(this.cheques, new String[]{numero.getText(), banco.getText(), agencia.getText(), nrcheque.getText(), predata.getValue().toString(), valorTextField});
                } catch (NullPointerException e) {
                    this.cheques = FuncoesGlobais.ArraysAdd(this.cheques, new String[]{numero.getText(), banco.getText(), agencia.getText(), nrcheque.getText(), "", valorTextField});
                }
                fimtransacao = false;
            }
            vrresta.setText(valor.subtract(lancado).compareTo(BigDecimal.ZERO) > 0 ? BigDecimalToCurrency(valor.subtract(lancado)) : "0,00");
            vrtroco.setText(lancado.subtract(valor).compareTo(BigDecimal.ZERO) > 0 ? BigDecimalToCurrency(lancado.subtract(valor)) : "0,00");

            if (tipoRecebimento == 1 && !fimtransacao) {
                // Dinheiro
                Dinheiro();
            } else if (tipoRecebimento == 2 && !fimtransacao) {
                // Cheque
                Cheque();
            } else if (tipoRecebimento == 3 && !fimtransacao) {
                // Banco
                Banco();
            } else if (fimtransacao) {
                anchorPane.getParent().fireEvent(new paramEvent(new Object[] {"RECEBER",this.cheques}, paramEvent.GET_PARAM));
            }
        });

        vrdn.textProperty().addListener((obs, oldText, newText) -> {MostraValores(new BigDecimal(LerValor.Number2BigDecimal(newText)));});
        vrch.textProperty().addListener((obs, oldText, newText) -> {MostraValores(new BigDecimal(LerValor.Number2BigDecimal(newText)));});

        MaskFieldUtil.numericField(numero);
        MaskFieldUtil.maxField(numero, 15);
        numero.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (numero.getText().trim().equalsIgnoreCase("")) {numero.selectAll(); numero.requestFocus();}
            }
        });

        MaskFieldUtil.numericField(banco);
        MaskFieldUtil.maxField(banco, 3);
        banco.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (banco.getText().trim().equalsIgnoreCase("")) {banco.selectAll(); banco.requestFocus();}
            }
        });

        MaskFieldUtil.numericField(agencia);
        MaskFieldUtil.maxField(agencia, 5);
        agencia.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (agencia.getText().trim().equalsIgnoreCase("")) {agencia.selectAll(); agencia.requestFocus();}
            }
        });

        MaskFieldUtil.numericField(nrcheque);
        MaskFieldUtil.maxField(nrcheque, 10);
        nrcheque.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (nrcheque.getText().trim().equalsIgnoreCase("")) {nrcheque.selectAll(); nrcheque.requestFocus();}
            }
        });

        MaskFieldUtil.monetaryField(vrch);
        MaskFieldUtil.monetaryField(vrdn);
    }

    private void MostraValores(BigDecimal value) {
        vrresta.setText(valor.subtract(lancado.add(value)).compareTo(BigDecimal.ZERO) > 0 ? BigDecimalToCurrency(valor.subtract(lancado.add(value))) : "0,00");
        vrtroco.setText(value.subtract(valor.subtract(lancado)).compareTo(BigDecimal.ZERO) > 0 ? BigDecimalToCurrency(value.subtract(valor.subtract(lancado))) : "0,00");
    }

    public void CloseWindow() {
/*
        Window window = anchorPane.getScene().getWindow();
        if (window instanceof Stage){
            ((Stage) window).close();
        }
*/
    }

    public void Formas_Disable(boolean disable) {
        tipoRecebimento = 0;

        btDN.setDisable(disable);
        btCH.setDisable(disable);
        btBC.setDisable(disable);

        if (!disable) {
            lblTrocoResta.setDisable(true);
            numero.setDisable(true);
            numero.setText("");
            banco.setDisable(true);
            banco.setText("");
            agencia.setDisable(true);
            agencia.setText("");
            nrcheque.setDisable(true);
            nrcheque.setText("");
            predata.setDisable(true);
            predata.setValue(null);
            vrch.setDisable(true);
            vrch.setText("0,00");
            vrdn.setDisable(true);
            vrdn.setText("0,00");

            //vrresta.setText("0,00");
            vrresta.setDisable(true);
            //vrtroco.setText("0,00");
            vrtroco.setDisable(true);

            try {btLancar.setDisable(true);} catch (NullPointerException e) {}
            try {btCancelar.setDisable(false);} catch (NullPointerException e) {}

            Platform.runLater(() -> btDN.requestFocus());
        } else {
            try {btLancar.setDisable(false);} catch (NullPointerException e) {}
            try {btCancelar.setDisable(true);} catch (NullPointerException e) {}
        }
    }

    public void Formas_DisableAll() {
        tipoRecebimento = 0;

        btDN.setDisable(true);
        btCH.setDisable(true);
        btBC.setDisable(true);

        lblTrocoResta.setDisable(true);
        numero.setDisable(true);
        numero.setText("");
        banco.setDisable(true);
        banco.setText("");
        agencia.setDisable(true);
        agencia.setText("");
        nrcheque.setDisable(true);
        nrcheque.setText("");
        predata.setDisable(true);
        predata.setValue(null);
        vrch.setDisable(true);
        vrch.setText("0,00");
        vrdn.setDisable(true);
        vrdn.setText("0,00");

        vrresta.setText("0,00");
        vrresta.setDisable(true);
        vrtroco.setText("0,00");
        vrtroco.setDisable(true);

        try {btLancar.setDisable(true);} catch (NullPointerException e) {}
        try {btCancelar.setDisable(true);} catch (NullPointerException e) {}
    }

    private void Dinheiro() {
        tipoRecebimento = 1;

        btDN.setDisable(false);
        btCH.setDisable(true);
        btBC.setDisable(true);

        lblTrocoResta.setDisable(false);
        numero.setDisable(true);
        numero.setText("");
        banco.setDisable(true);
        banco.setText("");
        agencia.setDisable(true);
        agencia.setText("");
        nrcheque.setDisable(true);
        nrcheque.setText("");
        predata.setDisable(true);
        predata.setValue(null);
        vrch.setDisable(true);
        vrch.setText("0,00");
        vrdn.setDisable(false);
        vrdn.setText("0,00");

        vrresta.setDisable(false);
        vrtroco.setDisable(false);

        try {btLancar.setDisable(false);} catch (NullPointerException e) {}
        try {btCancelar.setDisable(false);} catch (NullPointerException e) {}

        Platform.runLater(() ->  vrdn.selectAll());
        vrdn.requestFocus();
    }

    private void Cheque() {
        tipoRecebimento = 2;

        btDN.setDisable(true);
        btCH.setDisable(false);
        btBC.setDisable(true);

        lblTrocoResta.setDisable(true);
        numero.setDisable(true);
        numero.setText("");
        banco.setDisable(false);
        banco.setText("");
        agencia.setDisable(false);
        agencia.setText("");
        nrcheque.setDisable(false);
        nrcheque.setText("");
        predata.setDisable(false);
        predata.setValue(null);
        vrch.setDisable(false);
        vrch.setText("0,00");
        vrdn.setDisable(true);
        vrdn.setText("0,00");

        vrresta.setDisable(true);
        vrtroco.setDisable(true);

        try {btLancar.setDisable(false);} catch (NullPointerException e) {}
        try {btCancelar.setDisable(false);} catch (NullPointerException e) {}

        Platform.runLater(() ->  banco.selectAll());
        banco.requestFocus();
    }

    private void Banco() {
        tipoRecebimento = 3;

        btDN.setDisable(true);
        btCH.setDisable(true);
        btBC.setDisable(false);

        lblTrocoResta.setDisable(true);
        numero.setDisable(false);
        numero.setText("");
        banco.setDisable(true);
        banco.setText("");
        agencia.setDisable(true);
        agencia.setText("");
        nrcheque.setDisable(true);
        nrcheque.setText("");
        predata.setDisable(true);
        predata.setValue(null);
        vrch.setDisable(true);
        vrch.setText("0,00");
        vrdn.setDisable(false);
        vrdn.setText("0,00");

        vrresta.setDisable(true);
        vrtroco.setDisable(true);

        try {btLancar.setDisable(false);} catch (NullPointerException e) {}
        try {btCancelar.setDisable(false);} catch (NullPointerException e) {}

        Platform.runLater(() ->  numero.selectAll());
        numero.requestFocus();
    }

    public void SetValor(BigDecimal valor) {
        this.valor = valor;
        vrresta.setText(BigDecimalToCurrency(valor));
        this.lancado = new BigDecimal(0);
        this.cheques = new String[][] {};
    }
}
