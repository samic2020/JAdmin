package Relatorios.Saldos.Depositos;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import Relatorios.Saldos.ReportClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import pdfViewer.PdfViewer;

import javax.swing.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.util.*;

public class Depositos implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker dpkData;
    @FXML private DatePicker dpkData1;
    @FXML private Button btnPreview;

    DbMain conn = VariaveisGlobais.conexao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpkData.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        dpkData1.setValue(Dates.toLocalDate(Dates.ultDataMes(DbMain.getDateTimeServer())));
        dpkData.requestFocus();

        btnPreview.setOnAction(event -> {
            Date iData = Dates.toDate(dpkData.getValue());
            Date fData = Dates.toDate(dpkData1.getValue());

            if (iData.after(fData)) {
                JOptionPane.showMessageDialog(null,"Data inicial maior que data final!");
                dpkData.getEditor().selectAll();
                dpkData.requestFocus();
                return;
            }

            List<ReportClass> lista = new ArrayList<>();
            for (Object[] value : antDeposito(iData, fData)) {
                //System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
                ReportClass values = new ReportClass(
                        value[0].toString(),
                        value[1].toString(),
                        ((BigDecimal)value[2]).subtract((BigDecimal) value[3])
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELAÓ�RIO DE DEP�SITOS");
            parametros.put("Periodo", "Per�odo de " + Dates.DateFormata("dd-MM-yyyy", iData) + " at� " +
                    Dates.DateFormata("dd-MM-yyyy", fData));

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosRazao", parametros);
            new PdfViewer("Preview do Depósitos", pdfName);
        });
    }

    private Object[][] antDeposito(Date iniData, Date fimData) {
        Object[][] dadosImpr = {};

        // CAIXA
        String avsql = "select aut, operacao tipo, documento, valor::numeric(10,2) valor, datahora dtrecebimento from caixa where documento = 'DEP' and (datahora BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date", Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                String docdep = "select datahora, documento, case when contrato = '' then rgprp::character varying(6) else contrato end contrato, lancamentos[s][5] bco, lancamentos[s][4] age, lancamentos[s][3] nch, valor::decimal(19,2), aut, logado from (SELECT *, generate_subscripts(lancamentos, 1) AS s from caixa) as foo where ((documento = 'REC' or documento = 'AVI' or documento = 'PCX') and operacao = 'CRE') and lancamentos[s][7]::integer = ?;";
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        return dadosImpr;
    }
}
