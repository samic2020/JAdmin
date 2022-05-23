package Movimento.PassCaixa;

import Classes.paramEvent;
import Funcoes.*;
import PagRec.RecebimentoController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PassCaixaController implements Initializable {
    RecebimentoController controllerRec;
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPanePassCaixa;
    @FXML private Pane tpagtos;
    @FXML private ToggleButton tgbCrDb;
    @FXML private TextArea pcTexto;
    @FXML private ToggleGroup pctgCHDN;
    @FXML private RadioButton pcCheque;
    @FXML private RadioButton pcDinheiro;
    @FXML private TextField pcValorTotal;
    @FXML private ComboBox<cCheques> pcChequesLista;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPanePassCaixa.addEventHandler(paramEvent.GET_PARAM, this::handle);

        FillCheques();
        tgbCrDb.setOnAction((ActionEvent event) -> { CrDb(); });
        Platform.runLater(() -> CrDb());
        pcCheque.setOnAction(event -> TogleCHDN());
        pcDinheiro.setOnAction(event -> TogleCHDN());

        pcChequesLista.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<cCheques>() {
            public void changed(ObservableValue<? extends cCheques> ov, final cCheques oldvalue, final cCheques newvalue) {
                pcValorTotal.setText(new DecimalFormat("#,##0.00").format(pcChequesLista.getSelectionModel().getSelectedItem().getValor()));
                pcValorTotal.requestFocus();
        }});
    }

    private void CrDb() {
        try {tpagtos.getChildren().remove(0);} catch (Exception e) {}
        if (tgbCrDb.isSelected()) {
            tgbCrDb.setText("Débito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#ff5400, #be1d00);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;" +
                            "-fx-font: bold italic 10pt 'Arial'"
            );
            pcCheque.setVisible(true);
            pcChequesLista.setVisible(true);
            pcDinheiro.setVisible(true);
            pcValorTotal.setVisible(true);
            pcTexto.setPrefSize(320,127);
            tpagtos.setVisible(true);
            tpagtos.setStyle("-fx-background-color: red;");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Recebimento.fxml"));
                Pane root = (Pane)loader.load();
                controllerRec = loader.getController();
                tpagtos.getChildren().add(root);
                root.setLayoutX(0); root.setLayoutY(0);
            } catch (Exception e) {e.printStackTrace();}

            tgbCrDb.setText("Crédito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#00ff54, #00be1d);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;"+
                            "-fx-font: bold italic 10pt 'Arial'"
            );
            pcCheque.setVisible(false);
            pcChequesLista.setVisible(false);
            pcDinheiro.setVisible(false);
            pcValorTotal.setVisible(true);
            pcTexto.setPrefSize(320,200);
            tpagtos.setVisible(true);
            tpagtos.setStyle("-fx-background-color:  cornsilk;");
        }

        MaskFieldUtil.monetaryField(pcValorTotal);
        pcValorTotal.requestFocus();
        pcValorTotal.focusedProperty().addListener(this::changed);
    }

    private void FillCheques() {
        pcChequesLista.getItems().clear();

        String selectSQL = "SELECT lancamentos, id, s, aut, datahora, lancamentos[s][1]::varchar(2) tipo, lancamentos[s][5]::varchar(3) banco, lancamentos[s][4]::varchar(5) agencia, lancamentos[s][3]::varchar(10) ncheque, lancamentos[s][6]::varchar(10) dtcheq, lancamentos[s][2]::decimal valor, lancamentos[s][7]::varchar(3) dep FROM (SELECT *, generate_subscripts(lancamentos, 1) AS s FROM caixa) AS foo WHERE lancamentos[s][1] = 'CH' AND (lancamentos[s][7] is null OR lancamentos[s][7] = '') AND Upper(Trim(logado)) = ? ORDER BY 1,7,8,9";
        ResultSet rs = null;
        List<cCheques> data = new ArrayList<>();

        try {
            rs = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", VariaveisGlobais.usuario.toUpperCase()}});
            while (rs.next()) {
                int qid = -1; int qs = -1;
                String qbanco = null, qagencia = null, qncheque = null;
                BigDecimal qvalor = null;
                try {qid = rs.getInt("id");} catch (SQLException sex) {}
                try {qs = rs.getInt("s");} catch (SQLException sex) {}
                try {qbanco = rs.getString("banco");} catch (SQLException sex) {}
                try {qagencia = rs.getString("agencia");} catch (SQLException sex) {}
                try {qncheque = rs.getString("ncheque");} catch (SQLException sex) {}
                try {qvalor = rs.getBigDecimal("valor");} catch (SQLException sex) {}

                data.add(new cCheques(qid, qs, qbanco, qagencia, qncheque, qvalor));
            }
        } catch (Exception ex) {}
        DbMain.FecharTabela(rs);

        pcChequesLista.setItems(FXCollections.observableArrayList(data));
    }

    private void TogleCHDN() {
        if (pcCheque.isSelected()) {
            // Cheque
            pcChequesLista.getSelectionModel().select(0);
            pcChequesLista.setDisable(false);
            pcValorTotal.setText("0,00");
            pcValorTotal.setEditable(false);
            pcValorTotal.setDisable(false);
            pcChequesLista.requestFocus();
        } else {
            // Dinheiro
            pcChequesLista.setDisable(true);
            pcValorTotal.setEditable(true);
            pcValorTotal.setDisable(false);
            pcValorTotal.setText("0,00");
            pcValorTotal.requestFocus();
        }
    }

    private void handle(paramEvent event) {
        if (event.sparam.length == 0) {
            // Cancelar
            pcValorTotal.requestFocus();
        }
        if (event.sparam.length > 0) {
            if (event.sparam[0] != null) {
                BaixaDoSistema(event.sparam);
            }
        } else {
            if (event.sparam[0] == null) {
                pcValorTotal.requestFocus();
            }
        }
    }

    private void BaixaDoSistema(Object[] lanctos) {
        // Gravar Caixa / PASSCAIXA
        BigInteger aut = conn.PegarAutenticacao();
        Collections dadm = VariaveisGlobais.getAdmDados();

    }

    private void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            // gotfocus
            if (tgbCrDb.isSelected()) {
                //
            } else {
                controllerRec.Formas_DisableAll();
            }
        } else {
            // lostfocus
            if (new BigDecimal(LerValor.Number2BigDecimal(pcValorTotal.getText())).doubleValue() > 0) {
                if (tgbCrDb.isSelected()) {
                    //
                } else {
                    controllerRec.Formas_Disable(false);
                    controllerRec.SetValor(new BigDecimal(LerValor.Number2BigDecimal(pcValorTotal.getText())));
                }
                //try {controller.Formas_Disable(false);} catch (Exception e) {}
                //controller.SetValor(new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText())));
            } else {
                pcValorTotal.requestFocus();
            }
        }
    }
}
