package Movimento.Alteracao;

import Classes.paramEvent;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static Funcoes.FuncoesGlobais.StrZero;

/**
 * Created by supervisor on 06/01/17.
 */
public class DescDifController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String rgprp = null;
    private String rgimv = null;
    private String contrato = null;
    private String refer = null;

    @FXML private AnchorPane anchorPane;
    @FXML private RadioButton desconto;
    @FXML private ToggleGroup descdif;
    @FXML private RadioButton diferenca;
    @FXML private TextField descricao;
    @FXML private TextField cotaparc;
    @FXML private TextField valor;
    @FXML private Button btlancar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if ((String)event.sparam[0] != null) {
                contrato = (String) event.sparam[0];
                rgprp = (String) event.sparam[1];
                rgimv = (String) event.sparam[2];
                refer = (String) event.sparam[3];
            }
        });

        MaskFieldUtil.maxField(descricao,25);
        MaskFieldUtil.dateRefField(cotaparc);
        MaskFieldUtil.monetaryField(valor);

        btlancar.setOnAction(event -> {
            int rmes = 0, rano = 0;
            rmes = Integer.parseInt(refer.substring(0,2).toString());
            rano = Integer.parseInt(refer.substring(3,7).toString());

            int mes = 0; int ano = 0;
            String tcota = cotaparc.getText();
            boolean isCota = false;

            if (tcota.trim().length() == 5) {
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

            String sql = "INSERT INTO descdif(tipo, rgprp, rgimv, contrato, descricao, cota, referencia, valor, dtlanc, usr_lanc) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(sql);
                int nid = 1;
                pstmt.setString(nid++, desconto.isSelected() ? "D" : "C");
                pstmt.setString(nid++, String.valueOf(rgprp));
                pstmt.setString(nid++, String.valueOf(rgimv));
                pstmt.setString(nid++, contrato);
                pstmt.setString(nid++, descricao.getText());

                String tmpCotaParc = ""; String tmpRef = "";
                if (isCota) {
                    if (mes > 12) { mes = 1; ano += 1; }
                    if (rmes > 12) { rmes = 1; rano += 1; }
                    tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),4);
                    tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                } else {
                    if (rmes > 12) { rmes = 1; rano += 1; }
                    tmpCotaParc = StrZero(String.valueOf(mes++),2) + "/" + StrZero(String.valueOf(ano),2);
                    tmpRef = StrZero(String.valueOf(rmes++),2) + "/" + StrZero(String.valueOf(rano),4);
                }
                pstmt.setString(nid++, tcota.trim().length() <= 3 ? "" : tmpCotaParc);

                pstmt.setString(nid++, tmpRef);
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Number2BigDecimal(valor.getText())));
                pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                pstmt.setString(nid++, VariaveisGlobais.usuario);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Dispara FireEvent para atualizar
            try {anchorPane.fireEvent(new paramEvent(new String[] {null},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        });
    }
}
