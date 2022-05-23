/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Imoveis.Baixa;

import Classes.paramEvent;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author supervisor
 */
public class BaixaImovelController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    Object[] param = null;
    String rgimv = null;
    String contrato = null;

    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker motivo_dtBaixa;
    @FXML private ComboBox<String> motivo_mtBaixa;
    @FXML private Button motivo_btBaixar;
    @FXML private Button motivo_btRetornar;
    @FXML private ToggleGroup classifica;
    @FXML private RadioButton classifica_bom;
    @FXML private RadioButton classifica_medio;
    @FXML private RadioButton classifica_ruim;
    @FXML private TextArea classifica_obs;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            this.param = event.sparam;

            if (this.param != null) {
                this.rgimv = (String)this.param[0];

                Object[][] aContrato = null;
                try {
                    aContrato = conn.LerCamposTabela(new String[] {"l_contrato"},"locatarios", "l_rgimv = '" + this.rgimv + "' ORDER BY l_id DESC LIMIT 1");
                } catch (Exception e) {}

                if (aContrato != null) {
                    this.contrato = (String)aContrato[0][3];
                }
            }
        });

        motivo_btRetornar.setOnAction(event -> {
            ((FXInternalWindow) anchorPane.getParent().getParent().getParent()).close();
        });

        motivo_btBaixar.setOnAction(event -> {
            // Verifica se existe algum saldo em aberto para o locatário.

            // Movimentos - Aluguel
            Object[][] movto = null;
            try {
                movto = conn.LerCamposTabela(new String[]{"dtvencimento"}, "movimento", "contrato = '" + this.contrato + "' AND dtrecebimento is null AND aut_rec is null");
            } catch (Exception e) {}
            if (movto != null) {
                // Mensagem avisando que não esta vazio
                Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) recibo(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg1.showAndWait();
                if (result.get().getText().equals("Não")) {
                    return;
                }
            }

            // Taxas
            Object[][] taxas = null;
            try {
                taxas = conn.LerCamposTabela(new String[] {"dtvencimento"},"taxas", "contrato = '" + this.contrato + "' AND dtrecebimento is null AND aut_rec is null");
            } catch (Exception e){}
            if (taxas != null) {
                Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) taxa(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg1.showAndWait();
                if (result.get().getText().equals("Não")) {
                    return;
                }
            }

            // Seguros
            Object[][] seguros = null;
            try {
                seguros = conn.LerCamposTabela(new String[] {"dtvencimento"},"seguros", "contrato = '" + this.contrato + "' AND dtrecebimento is null AND aut_rec is null");
            } catch (Exception e) {}
            if (seguros != null) {
                Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) seguro(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg1.showAndWait();
                if (result.get().getText().equals("Não")) {
                    return;
                }
            }

            // DescDif
            Object[][] descdif = null;
            try {
                descdif = conn.LerCamposTabela(new String[] {"dtrecebimento"},"descdif", "contrato = '" + this.contrato + "' AND dtrecebimento is null AND aut_rec is null");
            } catch (Exception e) {}
            if (descdif != null) {
                Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) desconto(s) e/ou diferença(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg1.showAndWait();
                if (result.get().getText().equals("Não")) {
                    return;
                }
            }

            // Avisos
            String sumSQL = "SELECT CASE WHEN tipo = 'CRE' THEN sum(valor) ELSE 0 END AS tcred, CASE WHEN tipo = 'DEB' THEN sum(valor) ELSE 0 END AS tdeb FROM avisos WHERE registro = '" + contrato + "' AND conta = '2' GROUP BY tipo;";
            ResultSet sumRs = conn.AbrirTabela(sumSQL, ResultSet.CONCUR_READ_ONLY);
            BigDecimal tcred = new BigDecimal("0");
            BigDecimal tdeb = new BigDecimal("0");
            try {
                while (sumRs.next()) {
                    tcred = tcred.add(sumRs.getBigDecimal("tcred"));
                    tdeb = tdeb.add(sumRs.getBigDecimal("tdeb"));
                }
            } catch (Exception e) { e.printStackTrace(); }
            try { DbMain.FecharTabela(sumRs); } catch (Exception e) {}
            if (tcred.subtract(tdeb).compareTo(BigDecimal.ZERO) != 0) {
                // Existe saldo
                Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe saldo de aviso não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg1.showAndWait();
                if (result.get().getText().equals("Não")) {
                    return;
                }
            }

            String updateSQL = "UPDATE imoveis SET i_situacao = 'Vazio' WHERE i_rgimv = '" + this.rgimv + "';";
            conn.ExecutarComando(updateSQL);

            String alterSQL = "UPDATE locatarios SET l_dtbaixa = ?, l_baixamotivo = ?, l_baixaclass = ?, l_baixaobs = ? WHERE l_contrato = ?";
            conn.ExecutarComando(alterSQL,new Object[][] {
                    {"date", Dates.toSqlDate(motivo_dtBaixa)},
                    {"string",motivo_mtBaixa.getSelectionModel().getSelectedItem()},
                    {"string", classifica_bom.isSelected() ? "Bom" : classifica_medio.isSelected() ? "Médio" : "Ruim"},
                    {"string", classifica_obs.getText()},
                    {"string", this.contrato}
            });

            ((FXInternalWindow) anchorPane.getParent().getParent().getParent()).close();


        });

        motivo_mtBaixa.getItems().clear();
        motivo_mtBaixa.getItems().addAll(
                "Fim de Contrato",
                "Saiu do Imóvel",
                "Abandono",
                "Despejo",
                "Ação Cuncluida");
    }
}