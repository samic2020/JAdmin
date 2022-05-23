/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Excluidos;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Relatorios.Saldos.ReportClass;
import Relatorios.Saldos.ReportDivClass;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import pdfViewer.PdfViewer;

/**
 *
 * @author Samic
 */
public class ListaSaldoExcluidos implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;    
    @FXML private Button btnListar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnListar.setOnAction((event) -> {
            List<ReportDivClass> lista = new ArrayList<>();
            for (Object[] value : sdRecibo()) {
                ReportDivClass values = new ReportDivClass(
                        value[0].toString(),
                        value[1].toString(),
                        (int)value[2], 
                        (Date)value[3],
                        (BigDecimal)value[4]
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELATÓRIO DE DIVIDA DOS LOCATÁRIOS");

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosDivLoca", null);
            new PdfViewer("Preview das Dividas de Locatários", pdfName);            
        });
    }

    private Object[][] sdRecibo() {
        Object[][] prop = {};
        int pos = -1; // Inicia ponteiro de pesquisa

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select m.*, l.l_rgprp, l.l_rgimv, l.l_contrato from movimento m, locatarios l where (m.contrato = l.l_contrato) and (not l.exclusao is null) and m.aut_rec = 0 order by 3;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            int rec = 1;
            while (rs.next()) {
                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));

                pos = FuncoesGlobais.FindinObject(prop,0,rs.getString("contrato"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeloca"}, "locatarios", "l_contrato = ?", new Object[][] {{"string", rs.getString("contrato")}});
                    } catch (SQLException e) { e.printStackTrace();}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("contrato"),
                            dadosprop[0][3].toString(),
                            rec,
                            rs.getDate("dtvencimento"),
                            alu,
                            new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][4] = ((BigDecimal)prop[pos][2]).add(alu);
                    prop[pos][2] = rec++;
                }

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where aut_rec = 0 and contrato = ? order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", rs.getString("contrato")}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){}
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,dfrs.getString("contrato"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeloca"}, "locatarios", "l_contrato = ?", new Object[][] {{"string", dfrs.getString("contrato")}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        dfrs.getString("contrato"),
                                        dadosprop[0][3].toString(),
                                        rec,
                                        rs.getDate("dtvencimento"),
                                        dftipo.trim().equalsIgnoreCase("C") ? dfcom : new BigDecimal("0"),
                                        dftipo.trim().equalsIgnoreCase("C") ? new BigDecimal("0") : dfcom
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][4] = ((BigDecimal)prop[pos2][4]).add(dftipo.trim().equalsIgnoreCase("C") ? dfcom : new BigDecimal("0"));
                                prop[pos2][5] = ((BigDecimal)prop[pos2][5]).add(dftipo.trim().equalsIgnoreCase("C") ? new BigDecimal("0") : dfcom);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        prop[pos][3] = ((BigDecimal)prop[pos][3]).add(pirvr);
                    }
                }

                // Seguros - PAREI AQUI 07/01/2021
                {
                    String sgsql = "select * from seguros where aut_rec = 0 order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,sgrs.getString("contrato"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeloca"}, "locatarios", "l_contrato = ?", new Object[][] {{"string", sgrs.getString("contrato")}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        sgrs.getString("contrato"),
                                        dadosprop[0][3].toString(),
                                        rec,
                                        rs.getDate("dtvencimento"),
                                        seg,
                                        new BigDecimal("0")
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][4] = ((BigDecimal)prop[pos2][4]).add(seg);
                                //if (sgrs.getBoolean("retencao")) prop[pos2][5] = ((BigDecimal)prop[pos2][5]).add(dfDB);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

                // Taxas
                {
                    String txsql = "select * from taxas where aut_rec = 0 and contrato = ? order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", rs.getString("l_contrato")}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){}
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,txrs.getString("contrato"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeloca"}, "locatarios", "l_contrato = ?", new Object[][] {{"string", txrs.getString("contrato")}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        txrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        rec,
                                        rs.getDate("dtvencimento"),
                                        (txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")),
                                        (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"))
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][4] = ((BigDecimal)prop[pos2][4]).add(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0"));
                                prop[pos2][5] = ((BigDecimal)prop[pos2][5]).add(txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"));
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    prop[pos][4]= ((BigDecimal)prop[pos][4]).add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return prop;
    }    
}
