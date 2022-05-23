package Relatorios.Saldos.Retencao;

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

public class Retencao implements Initializable {
    @FXML
    private AnchorPane anchorPane;
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

            List<ReportClass> lista = new ArrayList<>();
            for (Object[] value : sdRetencao(iniData, fimData)) {
                //System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
                ReportClass values = new ReportClass(
                        value[0].toString(),
                        value[1].toString(),
                        ((BigDecimal)value[2]).subtract((BigDecimal) value[3])
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELATÓRIO DE SALDO DE RETEN��O");
            parametros.put("Periodo", "Per�odo de " + Dates.DateFormata("dd-MM-yyyy", iniData) + " at� " +
                    Dates.DateFormata("dd-MM-yyyy", fimData));

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosRazao", parametros);
            new PdfViewer("Preview do Saldo de Retenção", pdfName);
        });
    }

    private Object[][] sdRetencao(Date iniData, Date fimData) {
        Object[][] prop = {};
        int pos = -1;

        // Taxas
        String qSQL = "SELECT t.id, t.rgprp, t.rgimv, t.contrato, (SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro " +
                "FROM imoveis i WHERE i.i_rgimv = t.rgimv) AS ender, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campo, " +
                "t.cota, t.valor, t.dtvencimento, t.referencia, t.dtrecebimento FROM taxas t WHERE t.tipo = 'D' AND t.retencao = True AND " +
                "(t.dtrecebimento BETWEEN ? AND ?) AND not aut_ret is null;";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                pos = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgimv"));
                if (pos == -1) {
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("rgimv"),
                            rs.getString("ender"),
                            new BigDecimal("0"),
                            rs.getBigDecimal("valor")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][3] = ((BigDecimal)prop[pos][3]).add(rs.getBigDecimal("valor"));
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // Seguros
        qSQL = "SELECT s.id, s.rgprp, s.rgimv, s.contrato, s.cota, s.valor, s.dtvencimento, s.referencia, \n" +
                "(SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro FROM imoveis i WHERE i.i_rgimv = s.rgimv) AS ender," +
                "       s.extrato, s.apolice, s.dtseguro, s.aut_seg, s.usr_seg, s.dtrecebimento, \n" +
                "       s.aut_rec, s.usr_rec, s.banco, s.nnumero, s.bloqueio, s.dtbloqueio, s.usr_bloqueio, \n" +
                "       s.dtlanc, s.usr_lanc, s.selected, s.aut_pag, s.retencao, s.dtrecebimento, s.reserva, 'SEGURO' campo\n" +
                "  FROM seguros s WHERE s.retencao = true and (s.dtrecebimento BETWEEN ? and ?) and not aut_ret is null";
        rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                pos = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgimv"));
                if (pos == -1) {
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("rgimv"),
                            rs.getString("ender"),
                            new BigDecimal("0"),
                            rs.getBigDecimal("valor")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][3] = ((BigDecimal)prop[pos][3]).add(rs.getBigDecimal("valor"));
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return prop;
    }
}
