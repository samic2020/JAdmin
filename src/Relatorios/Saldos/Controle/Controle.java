package Relatorios.Saldos.Controle;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Relatorios.Saldos.ReportClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import pdfViewer.PdfViewer;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Controle implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker dpkData;
    @FXML private Button btnPreview;

    DbMain conn = VariaveisGlobais.conexao;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpkData.setValue(Dates.toLocalDate(DbMain.getDateTimeServer()));

        btnPreview.setOnAction(event -> {
            Date iniData = null;
            Date fimData = null;

            if (Dates.iDay(Dates.toDate(dpkData.getValue())) > 1) {
                iniData = Dates.primDataMes(Dates.toDate(dpkData.getValue()));
                fimData = Dates.toDate(dpkData.getValue());
            }

/*
            for (Object[] value : sdAvisoADMContas(0,iniData, fimData)) {
                System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
            }
*/

            List<ReportClass> lista = new ArrayList<>();
            for (Object[] value : sdControle(iniData, fimData)) {
                //System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
                ReportClass values = new ReportClass(
                        value[0].toString(),
                        value[1].toString(),
                        ((BigDecimal)value[2]).subtract((BigDecimal) value[3])
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELATÓRIO DE SALDO DE CONTROLES");
            parametros.put("Periodo", "Per�odo de " + Dates.DateFormata("dd-MM-yyyy", iniData) + " at� " +
                    Dates.DateFormata("dd-MM-yyyy", fimData));

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosRazao", parametros);
            new PdfViewer("Preview do Saldo de Controles", pdfName);
        });
    }

    private Object[][] sdAvisoADMContas(int conta, Date iniData, Date fimData) {
        Object[][] prop = {};

        String qSQL = "select registro, CASE WHEN tipo = 'CRE' THEN SUM(valor) ELSE 0 END Credito, CASE WHEN tipo = 'CRE' THEN 0 ELSE SUM(valor) END Debito from avisos where bloqueio is null and conta = ? and (dtrecebimento BETWEEN ? and ?) group by registro, tipo";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", conta},{"date", Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                int pos = -1;
                pos = FuncoesGlobais.FindinObject(prop,0,rs.getString("registro"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"descricao"}, "adm", "codigo = ?", new Object[][] {{"string", rs.getString("registro")}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("registro"),
                            dadosprop[0][3].toString(),
                            rs.getBigDecimal("credito"),
                            rs.getBigDecimal("debito")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(rs.getBigDecimal("credito"));
                    prop[pos][3] = ((BigDecimal)prop[pos][3]).add(rs.getBigDecimal("debito"));
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return prop;
    }

    private Object[][] sdControle(Date iniData, Date fimData) {
        Object[][] prop = {};
        int pos = -1;

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is null and conta = 4 and (dtrecebimento BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                pos = FuncoesGlobais.FindinObject(prop,0,avrs.getString("registro"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"descricao"}, "adm_contas", "codigo = ?", new Object[][] {{"string", avrs.getString("registro")}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            avrs.getString("registro"),
                            dadosprop[0][3].toString(),
                            avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"),
                            avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                    prop[pos][3] = ((BigDecimal)prop[pos][3]).add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        return prop;
    }

}