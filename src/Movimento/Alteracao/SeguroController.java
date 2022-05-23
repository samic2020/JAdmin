package Movimento.Alteracao;

import Classes.paramEvent;
import Funcoes.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static Funcoes.FuncoesGlobais.StrZero;

/**
 * Created by supervisor on 11/01/17.
 */
public class SeguroController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String rgprp = null;
    private String rgimv = null;
    private String contrato = null;
    private String refer = null;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField cotaparc;
    @FXML private CheckBox extrato;
    @FXML private TextField valor;
    @FXML private Button btlancar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if ((String) event.sparam[0] != null) {
                contrato = (String) event.sparam[0];
                rgprp = (String) event.sparam[1];
                rgimv = (String) event.sparam[2];
                refer = (String) event.sparam[3];
            }
        });

        MaskFieldUtil.dateRefField(cotaparc);
        MaskFieldUtil.monetaryField(valor);

        btlancar.disableProperty().bind(valor.textProperty().isEmpty().or(valor.textProperty().isEqualToIgnoreCase("0,00")));
        btlancar.setOnAction(event -> {
            int rmes = 0, rano = 0;
            rmes = Integer.parseInt(refer.substring(0,2).toString());
            rano = Integer.parseInt(refer.substring(3,7).toString());
            String tcota = cotaparc.getText().trim();
            int mes = 0; int ano = 0;
            boolean isCota = false;
            if (tcota.trim().length() < 5) {
                new Alert(Alert.AlertType.ERROR,"Você de colocar Cota (MM/AAAA) ou parcela (NN/TT)!").showAndWait();
                cotaparc.requestFocus();
                return;
            } else if (tcota.trim().length() == 5) {
                // Parcelas
                mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                ano = Integer.parseInt(tcota.substring(3,5).toString());
                if (mes == 0 || ano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                    cotaparc.requestFocus();
                    return;
                } else if (mes > ano) {
                    new Alert(Alert.AlertType.ERROR,"Parcela (NN/TT)!\n\nAonde NN > 0, NN <= TT e TT > 0").showAndWait();
                    cotaparc.requestFocus();
                    return;
                }
                isCota = false;
            } else {
                // Cotas
                mes = Integer.parseInt(tcota.subSequence(0,2).toString());
                ano = Integer.parseInt(tcota.substring(3,7).toString());
                if (mes == 0 || ano == 0) {
                    new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    cotaparc.requestFocus();
                    return;
                }
                if (mes < 1 || mes > 12) {
                    new Alert(Alert.AlertType.ERROR,"Cota (MM/AAAA)!\n\nAonde MM > 0, MM = {01,02,...,12} e AAAA > 0").showAndWait();
                    cotaparc.requestFocus();
                    return;
                }
                isCota = true;
            }

            String iSql = "INSERT INTO seguros(" +
                    "            rgprp, rgimv, contrato, cota, valor, dtvencimento, referencia, " +
                    "            extrato, apolice, dtlanc, usr_lanc)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                int nid = 1;
                pstmt.setInt(nid++, Integer.valueOf(rgprp));
                pstmt.setInt(nid++, Integer.valueOf(rgimv));
                pstmt.setString(nid++, contrato);

                String tmpCotaParc = ""; String tmpRef = "";
                if (isCota) {
                    if (mes > 12) { mes = 1; ano += 1; }
                    if (rmes > 12) { rmes = 1; rano += 1; }
                    tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),4);
                    tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                } else {
                    if (rmes > 12) { rmes = 1; rano += 1; }
                    tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                    tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),2);
                }
                pstmt.setString(nid++, tmpCotaParc);

                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(valor.getText())));
                pstmt.setDate(nid++, Dates.toSqlDate(DbMain.getDateTimeServer()));
                pstmt.setString(nid++, tmpRef);
                pstmt.setBoolean(nid++, extrato.isSelected());
                pstmt.setString(nid++, rgprp+rgimv+contrato);

                pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                pstmt.setString(nid++, VariaveisGlobais.usuario);

                pstmt.executeUpdate();
            } catch (SQLException ex) {}

            try {anchorPane.fireEvent(new paramEvent(new String[] {null},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        });

    }
}
