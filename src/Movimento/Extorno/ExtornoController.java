package Movimento.Extorno;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

public class ExtornoController  implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private TextField txbAutent;
    @FXML private Button btnExtornar;
    @FXML private Label lblMsg;

    DbMain conn = VariaveisGlobais.conexao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> txbAutent.requestFocus());
        btnExtornar.setOnAction(event -> {
            String cSql = "SELECT * FROM caixa WHERE logado = '%s' AND to_char(datahora, 'YYYY-MM-DD') = '%s' AND aut = %s;";
            cSql = String.format(cSql,VariaveisGlobais.usuario, new SimpleDateFormat("yyyy-MM-dd").format(DbMain.getDateTimeServer()), txbAutent.getText());
            ResultSet rs = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
            boolean existAut = false;
            Integer IDcaixa = -1;
            String Document = null;
            String Valor = "";
            String[][] autpag = null;

            try {
                while (rs.next()) {
                    existAut = true;
                    IDcaixa = rs.getInt("id");
                    Document = rs.getString("Documento");
                    Valor = rs.getString("valor");
                    autpag = (String[][])rs.getArray("lancamentos").getArray();
                }
            } catch (Exception e) {}
            try { DbMain.FecharTabela(rs); } catch (Exception e) {}

            String meioPag = ""; String sMsg = "";
            if (existAut) {
                for (String[] saut : autpag) {
                    meioPag += saut[0] + " " + saut[1] + "\n";
                }
                sMsg = "Você deseja extornar a autenticação " + txbAutent.getText() + "\n" +
                        "no valor de " + Valor + "\n\n" +
                        meioPag;
            }

            boolean extornado = false;
            if (existAut) {
                if (Document.length() == 4) {
                    MsgBox("Autenticação já extornada!", Color.DARKBLUE, 2);
                    txbAutent.requestFocus();
                    return;
                }

                Alert quest = new Alert(Alert.AlertType.CONFIRMATION, sMsg, new ButtonType("Sim"), new ButtonType("Não"));
                quest.setTitle("Pergunta");
                Optional<ButtonType> result = quest.showAndWait();
                if (result.get().getText().equals("Não")) return;

                if (Document.equalsIgnoreCase("REC")) {
                    extornado = ExtornaREC(txbAutent.getText());
                } else if (Document.equalsIgnoreCase("AVI")) {
                    extornado = ExtornaAVI(txbAutent.getText());
                } else if (Document.equalsIgnoreCase("PAV")) {
                    extornado = ExtornaPAVI(txbAutent.getText());
                } else if (Document.equalsIgnoreCase("EXT")) {
                    extornado = ExtornaEXT(txbAutent.getText());
                } else if (Document.equalsIgnoreCase("ADI")) {
                    extornado = ExtornaADI(txbAutent.getText());
                }

                if (extornado) {
                    MsgBox("Extornando CAIXA.", Color.DARKBLUE, 1);

                    String uSql = "UPDATE caixa SET documento = '%s' WHERE id = %s;";
                    uSql = String.format(uSql, Document + "X", IDcaixa);
                    if (conn.ExecutarComando(uSql) > 0) {
                        MsgBox("Extornado com sucesso.", Color.DARKBLUE, 0);
                    } else {
                        MsgBox("Falha ao extronar...", Color.DARKRED, 0);
                    }
                }
            } else {
                MsgBox("Autenticação Inexistente!", Color.DARKRED, 2);
                txbAutent.requestFocus();
            }
        });
    }

    private boolean ExtornaREC(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Recibo.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM movimento WHERE aut_rec = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                // Checa se foi emitido Nota Fiscal para este recibo
                if (rs.getInt("notafiscal") != 0 ) {
                    if (JOptionPane.showConfirmDialog(null, "Foi gerada Nota Fiscal para este recibo!\n\nJá cancelou?") == 1) return false; ;
                }
                String uSql = "UPDATE movimento SET aut_rec = null, dtrecebimento = null, usr_rec = null, notafiscal = 0 WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        ExtornoRECDescDif(aut);
        ExtornoRECTaxas(aut);
        ExtornoRECSeguros(aut);
        return retorno;
    }

    private boolean ExtornoRECSeguros(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Recibo Seguros.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM seguros WHERE aut_rec = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String uSql = "UPDATE seguros SET aut_rec = null, dtrecebimento = null, usr_rec = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private boolean ExtornoRECTaxas(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Recibo Taxas.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM taxas WHERE aut_rec = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String uSql = "UPDATE taxas SET aut_rec = null, dtrecebimento = null, usr_rec = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private boolean ExtornoRECDescDif(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Recibo DescDif.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM descdif WHERE aut_rec = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String uSql = "UPDATE descdif SET aut_rec = null, dtrecebimento = null, usr_rec = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private boolean ExtornaAVI(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Avisos.",Color.DARKBLUE,1);
        String eSql = "DELETE FROM avisos WHERE aut_rec = '%s';";
        eSql = String.format(eSql, aut);
        retorno = (conn.ExecutarComando(eSql) > 0);
        return retorno;
    }

    private boolean ExtornaPAVI(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Pré Avisos.",Color.DARKBLUE,1);
        String eSql = "UPDATE avisos SET dtrecebimento = null, usr_rec = null, aut_pag = null, aut_rec = aut_rec * -1  WHERE aut_rec = '%s';";
        eSql = String.format(eSql, aut);
        retorno = (conn.ExecutarComando(eSql) > 0);
        return retorno;
    }

    private boolean ExtornaEXT(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Extrato.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(aut_pag, 1) AS s FROM movimento) AS foo WHERE aut_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        String rgprp = null;
        try {
            while (rs.next()) {
                try { rgprp = rs.getString("rgprp"); } catch (SQLException SQLEx) {}
                Array autpag = rs.getArray("aut_pag");
                String[][] tmpArray = (String[][])autpag.getArray();
                int autpos = rs.getInt("s");

                String uSql = "UPDATE movimento SET aut_pag[%s][2] = null, aut_pag[%s][3] = null, aut_pag[%s][4] = '' WHERE id = %s;";
                uSql = String.format(uSql, autpos, autpos, autpos, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        ExtornaEXTTaxas(aut);
        ExtornaEXTDescdif(aut);
        ExtornaEXTSLD(aut, rgprp);
        ExtornaEXTAVI(aut);
        ExtornaEXTSeguros(aut);
        return retorno;
    }

    private boolean ExtornaADI(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Adiantamento.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(ad_pag, 1) AS s FROM movimento) AS foo WHERE ad_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        String rgprp = null;
        try {
            while (rs.next()) {
                String uSql = "UPDATE movimento SET ad_pag = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        ExtornaADITaxas(aut);
        ExtornaADIAvi(aut);
        ExtornaADIDescdif(aut);
        ExtornaADISeguros(aut);
        return retorno;
    }

    private  boolean ExtornaEXTSeguros(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Extrato Seguros.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(aut_pag, 1) AS s FROM seguros) AS foo WHERE aut_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Array autpag = rs.getArray("aut_pag");
                String[][] tmpArray = (String[][])autpag.getArray();
                int autpos = rs.getInt("s");

                String uSql = "UPDATE seguros SET aut_pag[%s][2] = null, aut_pag[%s][3] = null, aut_pag[%s][4] = '' WHERE id = %s;";
                uSql = String.format(uSql, autpos, autpos, autpos, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private  boolean ExtornaADISeguros(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Adiantamento Seguros.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(ad_pag, 1) AS s FROM seguros) AS foo WHERE ad_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String uSql = "UPDATE seguros SET ad_pag = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private  boolean ExtornaEXTDescdif(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Extrato DescDif.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(aut_pag, 1) AS s FROM descdif) AS foo WHERE aut_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Array autpag = rs.getArray("aut_pag");
                String[][] tmpArray = (String[][])autpag.getArray();
                int autpos = rs.getInt("s");

                String uSql = "UPDATE descdif SET aut_pag[%s][2] = null, aut_pag[%s][3] = null, aut_pag[%s][4] = '' WHERE id = %s;";
                uSql = String.format(uSql, autpos, autpos, autpos, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private  boolean ExtornaADIDescdif(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Adiantamento DescDif.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(ad_pag, 1) AS s FROM descdif) AS foo WHERE ad_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String uSql = "UPDATE descdif SET ad_pag = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private  boolean ExtornaEXTTaxas(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Extrato Taxas.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(aut_pag, 1) AS s FROM taxas) AS foo WHERE aut_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Array autpag = rs.getArray("aut_pag");
                String[][] tmpArray = (String[][])autpag.getArray();
                int autpos = rs.getInt("s");

                String uSql = "UPDATE taxas SET aut_pag[%s][2] = null, aut_pag[%s][3] = null, aut_pag[%s][4] = '' WHERE id = %s;";
                uSql = String.format(uSql, autpos, autpos, autpos, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private  boolean ExtornaADITaxas(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Adiantamento Taxas.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(ad_pag, 1) AS s FROM taxas) AS foo WHERE ad_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String uSql = "UPDATE taxas SET ad_pag = null WHERE id = %s;";
                uSql = String.format(uSql, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private boolean ExtornaADIAvi(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Avisos do Adiantamento.",Color.DARKBLUE,0);

        String uSql = "UPDATE avisos SET dtrecebimento = null, aut_rec = null, usr_rec = null WHERE aut_rec = %s;";
        uSql = String.format(uSql, aut);
        retorno = (conn.ExecutarComando(uSql) > 0);

        return retorno;
    }

    private boolean ExtornaEXTAVI(String aut) {
        boolean retorno = false;
        MsgBox("Extornando Avisos do Extrato.",Color.DARKBLUE,0);

        String eSql = "SELECT * FROM (SELECT *, generate_subscripts(aut_pag, 1) AS s FROM avisos) AS foo WHERE aut_pag[s][2] = '%s' ORDER BY 1;";
        eSql = String.format(eSql, aut);

        ResultSet rs = conn.AbrirTabela(eSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Array autpag = rs.getArray("aut_pag");
                String[][] tmpArray = (String[][])autpag.getArray();
                int autpos = rs.getInt("s");

                String uSql = "UPDATE avisos SET aut_pag[%s][2] = null, aut_pag[%s][3] = null, aut_pag[%s][4] = '' WHERE id = %s;";
                uSql = String.format(uSql, autpos, autpos, autpos, rs.getInt("id"));
                retorno = (conn.ExecutarComando(uSql) > 0);
            }
        } catch (Exception e) {}
        return retorno;
    }

    private boolean ExtornaEXTSLD(String aut, String rgprp) {
        boolean retorno = false;
        MsgBox("Extornando Saldo Anterior.",Color.DARKBLUE,0);

        String deleteSQL = "delete from propsaldo where registro = ? and aut_pag is not null AND aut_pag[1][2] is null";
        if (conn.ExecutarComando(deleteSQL, new Object[][]{{"string", rgprp}}) > 0) {
            deleteSQL = "update propsaldo SET aut_pag = '{{%s,NULL,NULL,\"\"}}', valorpago = 0 WHERE registro = ? and aut_pag is not null AND aut_pag[1][2] = ?";
            deleteSQL = String.format(deleteSQL, rgprp);
            retorno = conn.ExecutarComando(deleteSQL, new Object[][] {{"string", rgprp}, {"string", aut}}) > 0;
        }

        return retorno;
    }

    private void MsgBox(String msg, Color cor, int tempo) {
        tempo = tempo * 1000;

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(tempo),
                ae -> lblMsg.setText("")));

        lblMsg.setStyle("-fx-effect:  innershadow( three-pass-box , rgba(0,0,0,0.7) , 5, 0.0 , 3, 3 );" +
                "-fx-background-color:  white;" +
                "-fx-background-radius: 5 5 5 5;" +
                "-fx-text-fill: '" + cor.toString() + "';");
        lblMsg.setText(msg);
        if (tempo > 0) timeline.play();
    }
}
