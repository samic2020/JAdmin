package Relatorios.Saldos.Adiantamentos;

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

public class Adiantamentos implements Initializable {
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
            for (Object[] value : sdAdiantamentos(iniData, fimData)) {
                //System.out.println(value[0] + ", " + value[1] + ", " + value[2] + ", " + value[3]);
                ReportClass values = new ReportClass(
                        value[0].toString(),
                        value[1].toString(),
                        ((BigDecimal)value[2]).subtract((BigDecimal) value[3])
                );
                lista.add(values);
            }

            Map parametros = new HashMap();
            parametros.put("NomeRelatorio", "RELATÓRIO DE SALDO DE ADIANTAMENTOS");
            parametros.put("Periodo", "Per�odo de " + Dates.DateFormata("dd-MM-yyyy", iniData) + " at� " +
                    Dates.DateFormata("dd-MM-yyyy", fimData));

            String pdfName = new PdfViewer().GeraPDFTemp(lista,"SaldosRazao", parametros);
            new PdfViewer("Preview do Saldo de Adiantamentos", pdfName);
        });
    }

    private Object[][] sdAdiantamentos(Date iniData, Date fimData) {
        Object[][] prop = {};
        int pos = -1; // Inicia ponteiro de pesquisa

        String movimentoSQL = "select rgprp, rgimv, contrato, cota, mensal, dtvencimento, cm, referencia, ad_pag[1][3] as dtrecebimento, ad_pag[1][2] as aut_rec from movimento where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date", Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try {
                    com = rs.getBigDecimal("cm");
                } catch (Exception ex) {
                    com = new BigDecimal("0");
                }

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

                // Desconto/Diferen�a
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    dfsql = "select * from descdif where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferen�a de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(rs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        rs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"),
                                        dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0")
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                                prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comiss�o
                prop[pos][3] = ((BigDecimal)prop[pos][3]).add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        prop[pos][3] = ((BigDecimal)prop[pos][3]).add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    sgsql = "select * from seguros where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor"); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(rs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        rs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        seg,
                                        new BigDecimal("0")
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(seg);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar reten��o
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "";
                    txsql = "select * from taxas where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(rs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        rs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        (txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")),
                                        (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"))
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                                prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }
            }
        } catch (SQLException sqlex) {
            System.out.println(sqlex);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and conta = 6 and (dtrecebimento BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                int pos2 = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgprp"));
                if (pos2 == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(rs.getString("rgprp"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("rgprp"),
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

    private BigDecimal[] diaAdiantamentos(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        String movimentoSQL = "select rgprp, rgimv, contrato, cota, mensal, dtvencimento, cm, referencia, ad_pag[1][3] as dtrecebimento, ad_pag[1][2] as aut_rec from movimento where not ad_pag is null and (ad_pag[1][3]::date = ?)";
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try {
                    com = rs.getBigDecimal("cm");
                } catch (Exception ex) {
                    com = new BigDecimal("0");
                }
                ttCR = ttCR.add(alu);

                // Desconto/Diferen�a
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where not ad_pag is null and ad_pag[1][3] = ?";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferen�a de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comiss�o
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where not ad_pag is null and ad_pag[1][3]::date = ?";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor"); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar reten��o
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where not ad_pag is null and ad_pag[1][3]::date = ?";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }
            }
        } catch (SQLException sqlex) {
            System.out.println(sqlex);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and conta = 6 and dtrecebimento = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }
}
