package Relatorios.Saldos.Proprietarios;

import Calculos.Multas;
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

public class Proprietarios implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker dpkData;
    @FXML private Button btnPreview;

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
            for (Object[] value : sdExtrato(iniData, fimData)) {
                //System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
                ReportClass values = new ReportClass(
                        value[0].toString(),
                        value[1].toString(),
                        ((BigDecimal)value[2]).subtract((BigDecimal) value[3])
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELATÓRIO DE SALDO DE PROPRIETÁRIOS");
            parametros.put("Periodo", "Período de " + Dates.DateFormata("dd-MM-yyyy", iniData) + " até " +
                    Dates.DateFormata("dd-MM-yyyy", fimData));

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosRazao", parametros);
            new PdfViewer("Preview do Saldo de Proprietários", pdfName);
        });
    }

    private Object[][] sdExtrato(Date iniData, Date fimData) {
        Object[][] prop = {};
        int pos = -1; // Inicia ponteiro de pesquisa

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM propsaldo Where aut_pag is not null AND not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?);";
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date", Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (sars.next()) {
                pos = FuncoesGlobais.FindinObject(prop,0,sars.getString("registro"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(sars.getString("registro"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            sars.getString("registro"),
                            dadosprop[0][3].toString(),
                            sars.getBigDecimal("valor"),
                            new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(sars.getBigDecimal("valor"));
                }
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select * from movimento where aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }

                pos = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgprp"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(rs.getString("rgprp"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("rgprp"),
                            dadosprop[0][3].toString(),
                            alu,
                            new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(alu);
                }

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? AND ?) order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,dfrs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(dfrs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        dfrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        dftipo.trim().equalsIgnoreCase("C") ? dfcom : new BigDecimal("0"),
                                        dftipo.trim().equalsIgnoreCase("C") ? new BigDecimal("0") : dfcom
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(dftipo.trim().equalsIgnoreCase("C") ? dfcom : new BigDecimal("0"));
                                prop[pos2][3] = ((BigDecimal)prop[pos2][2]).add(dftipo.trim().equalsIgnoreCase("C") ? new BigDecimal("0") : dfcom);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                prop[pos][3] = ((BigDecimal)prop[pos][3]).add(com);

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
                    String sgsql = "select * from seguros where aut_rec <> 0 and not aut_pag is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            //ttCR = ttCR.add(seg);
                            //if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,sgrs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(sgrs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        sgrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        seg,
                                        new BigDecimal("0")
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(seg);
                                if (sgrs.getBoolean("retencao")) prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(dfDB);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,txrs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(txrs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        txrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        (txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")),
                                        (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"))
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0"));
                                prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"));
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        prop[pos][2] = ((BigDecimal)prop[pos][2]).add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        prop[pos][2] = ((BigDecimal)prop[pos][2]).add(pjuvr);
                    }
                }

                // CORREÇÃO
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    prop[pos][2]= ((BigDecimal)prop[pos][2]).add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not aut_pag is not null and aut_pag[1][2] is null and conta = 1 and (aut_pag[1][3]::date BETWEEN ? and ?) order by 1;";
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

                int pos2 = FuncoesGlobais.FindinObject(prop,0,avrs.getString("rgprp"));
                if (pos2 == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(avrs.getString("registro"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            avrs.getString("registro"),
                            dadosprop[0][3].toString(),
                            avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"),
                            avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                    prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        return prop;
    }

}
