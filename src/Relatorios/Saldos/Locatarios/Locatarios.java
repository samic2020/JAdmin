package Relatorios.Saldos.Locatarios;

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

public class Locatarios  implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker dpkData;
    @FXML private Button btnPreview;

    DbMain conn = VariaveisGlobais.conexao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPreview.setOnAction(event -> {
            Date iniData = null;
            Date fimData = null;

            if (Dates.iDay(Dates.toDate(dpkData.getValue())) > 1) {
                iniData = Dates.primDataMes(Dates.toDate(dpkData.getValue()));
                fimData = Dates.toDate(dpkData.getValue());
            }

            List<ReportClass> lista = new ArrayList<>();
            for (Object[] value : sdLocatarios(iniData, fimData)) {
                //System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
                ReportClass values = new ReportClass(
                        value[0].toString(),
                        value[1].toString(),
                        ((BigDecimal)value[2]).subtract((BigDecimal) value[3])
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELAÓ�RIO DE SALDO DE LOCAT�RIOS");
            parametros.put("Periodo", "Per�odo de " + Dates.DateFormata("dd-MM-yyyy", iniData) + " at� " +
                    Dates.DateFormata("dd-MM-yyyy", fimData));

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosRazao", parametros);
            new PdfViewer("Preview do Saldo de Locatários", pdfName);
        });
    }

    private Object[][] sdLocatarios(Date iniData, Date fimData) {
        Object[][] prop = {};
        int pos = -1; // Inicia ponteiro de pesquisa

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is not null and conta = 2 and (dtrecebimento BETWEEN ? and ?) order by 1;";
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

                pos = FuncoesGlobais.FindinObject(prop,0,avrs.getString("rgprp"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(avrs.getString("rgprp"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            avrs.getString("rgprp"),
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
