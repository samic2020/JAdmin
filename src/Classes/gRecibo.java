package Classes;

import Calculos.Processa;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import Movimento.tbvAltera;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by supervisor on 18/05/17.
 */
public class gRecibo {
    DbMain conn = VariaveisGlobais.conexao;
    private int linha;
    public String rgprp;
    public String rgimv;
    public String contrato;
    public String vencto;
    public iRecibo.CorpoRecibo[] corpoRecibos = {};


    public iRecibo.CorpoRecibo[] GeraRecibo(String contrato, String vcto) {
        // Pegar o mes de referencia do Vencimento no descdif
        String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));

        linha = 0;
        List<tbvAltera> data = new ArrayList<tbvAltera>();

        rgprp = null;
        this.contrato = contrato;
        this.vencto = vcto;

        // Movimento
        String sql = "SELECT * FROM movimento WHERE contrato = '%s' AND dtvencimento = '%s';";
        //sql = String.format(sql, contrato.trim(), descdif_refer.trim());
        sql = String.format(sql, contrato.trim(), Dates.StringtoString(vcto,"dd/MM/yyyy", "yyyy/MM/dd"));

        ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        BigDecimal vrAluguel = new BigDecimal(0);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "C";
                String qdes = "ALUGUEL", qcota = "99/99";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "mensal";

                try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                try {rgimv = resultSet.getString("rgimv");} catch (SQLException e) {}

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                //try {qdes = resultSet.getString("descricao");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));
                try {vrAluguel = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Desconto Diferença
        BigDecimal desal = new BigDecimal("0");
        BigDecimal difal = new BigDecimal("0");
        sql = "SELECT * FROM descdif WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "";
                String qdes = "IPTU #" + ++linha, qcota = "99/99";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "valor";

                if (rgprp == null) {
                    try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                }

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    try {qdes = "Dif.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                } else {
                    // Desconto
                    try {qdes = "Desc.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                }
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("valor");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));

                if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    difal = difal.add(resultSet.getBigDecimal("valor"));
                } else {
                    // Desconto
                    desal = desal.add(resultSet.getBigDecimal("valor"));
                }

            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // TODO - IRRF Implementar
        //BigDecimal irenda = new Calculos.Irrf().Irrf(rgprp,contrato,descdif_refer.trim(),vrAluguel,difal,desal);
        //if (irenda.compareTo(BigDecimal.ZERO) == 1) {
        //    corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("IRRF",descdif_refer.trim(), irenda));
        //}

        // Taxas
        sql = "SELECT * FROM taxas WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "";
                String qdes = "" , qpos = "", qcota = "";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "valor";

                // Ler o nome da taxa
                String sWhere = null;
                try {sWhere = "codigo = '" + resultSet.getString("campo") + "'";} catch (SQLException e) {}

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                try {qdes = (qtipo.equalsIgnoreCase("D") ? "Desc." : "Dif." ) + (String)conn.LerCamposTabela(new String[] {"descricao"},"campos", sWhere)[0][3];} catch (SQLException e) {}
                try {qpos = resultSet.getString("poscampo");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("valor");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Seguros
        sql = "SELECT * FROM seguros WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "C";
                String qdes = "SEGURO", qcota = "99/99";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "valor";

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("valor");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Todo - IPTU implementar
/*
        String[] ciptu = new Calculos.Iptu().Iptu(rgimv,descdif_refer.trim());
        int iptuId = 0;
        String iptuMes = null;
        String iptuRef = null;
        BigDecimal vrIptu = BigDecimal.ZERO;
        if (ciptu != null) {
            iptuId = Integer.valueOf(ciptu[0] == null ? "0" : ciptu[0]);
            iptuMes = ciptu[1];
            iptuRef = ciptu[2];
            vrIptu = new BigDecimal(ciptu[3]);
        }
        if (vrIptu.compareTo(BigDecimal.ZERO) == 1) {
            corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("IPTU",iptuRef, vrIptu));
            //data.add(new tbvAltera("iptu",iptuMes, iptuId, true, "C", "IPTU", iptuRef, vrIptu));
        }
*/

        try {
            Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vcto, "dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()), "dd-MM-yyyy"));
            BigDecimal tmulta = calc.Multa(); if ( tmulta.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Multa","", tmulta));
            BigDecimal tcorr = calc.Correcao(); if ( tcorr.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Correção","", tcorr));
            BigDecimal tjur = calc.Juros(); if ( tjur.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Juros","", tjur));
            BigDecimal texp = calc.Expediente(); if ( texp.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Expediente","", texp));
            corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Total Recibo","", calc.TotalRecibo()));
        } catch (Exception e) {}

        return corpoRecibos;
    }

    public iRecibo.CorpoRecibo[] GeraReciboSegundaVia(String contrato, String vcto) {
        // Pegar o mes de referencia do Vencimento no descdif
        String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1, Dates.StringtoDate(vcto,"dd/MM/yyyy")));

        linha = 0;
        List<tbvAltera> data = new ArrayList<tbvAltera>();

        rgprp = null;
        this.contrato = contrato;
        this.vencto = vcto;

        // Movimento
        String sql = "SELECT * FROM movimento WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        BigDecimal vrAluguel = new BigDecimal(0);

        // MU, JU, CO, EP, IR
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "C";
                String qdes = "ALUGUEL", qcota = "99/99";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "mensal";

                try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                try {rgimv = resultSet.getString("rgimv");} catch (SQLException e) {}

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                //try {qdes = resultSet.getString("descricao");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));
                try {vrAluguel = resultSet.getBigDecimal("mensal");} catch (SQLException e) {}
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Desconto Diferença
        BigDecimal desal = new BigDecimal("0");
        BigDecimal difal = new BigDecimal("0");
        sql = "SELECT * FROM descdif WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "";
                String qdes = "IPTU #" + ++linha, qcota = "99/99";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "valor";

                if (rgprp == null) {
                    try {rgprp = resultSet.getString("rgprp");} catch (SQLException e) {}
                }

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    try {qdes = "Dif.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                } else {
                    // Desconto
                    try {qdes = "Desc.Aluguel " + resultSet.getString("descricao");} catch (SQLException e) {}
                }
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("valor");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));

                if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                    // Diferença
                    difal = difal.add(resultSet.getBigDecimal("valor"));
                } else {
                    // Desconto
                    desal = desal.add(resultSet.getBigDecimal("valor"));
                }

            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // IRRF
        BigDecimal irenda = new Calculos.Irrf().Irrf(rgprp,contrato,descdif_refer.trim(),vrAluguel,difal,desal);
        if (irenda.compareTo(BigDecimal.ZERO) == 1) {
            corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("IRRF",descdif_refer.trim(), irenda));
        }

        // Taxas
        sql = "SELECT * FROM taxas WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "";
                String qdes = "" , qpos = "", qcota = "";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "valor";

                // Ler o nome da taxa
                String sWhere = null;
                try {sWhere = "codigo = '" + resultSet.getString("campo") + "'";} catch (SQLException e) {}

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qtipo = resultSet.getString("tipo");} catch (SQLException e) {}
                try {qdes = (qtipo.equalsIgnoreCase("D") ? "Desc." : "Dif." ) + (String)conn.LerCamposTabela(new String[] {"descricao"},"campos", sWhere)[0][3];} catch (SQLException e) {}
                try {qpos = resultSet.getString("poscampo");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("valor");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        // Seguros
        sql = "SELECT * FROM seguros WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql, contrato.trim(), descdif_refer.trim());

        resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                // MontaGrade
                Boolean qtag = true; int qid = 0; String qtipo = "C";
                String qdes = "SEGURO", qcota = "99/99";
                BigDecimal qvalor = new BigDecimal(0); String qvariavel = "valor";

                try {qid = resultSet.getInt("id");} catch (SQLException e) {}
                try {qtag = resultSet.getBoolean("selected");} catch (SQLException e) {}
                try {qcota = resultSet.getString("cota");} catch (SQLException e) {}
                try {qvalor = resultSet.getBigDecimal("valor");} catch (SQLException e) {}

                corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo(qdes,qcota, qvalor));
            }
        } catch (Exception e) {e.printStackTrace();}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        String[] ciptu = new Calculos.Iptu().Iptu(rgimv,descdif_refer.trim());
        int iptuId = 0;
        String iptuMes = null;
        String iptuRef = null;
        BigDecimal vrIptu = BigDecimal.ZERO;
        if (ciptu != null) {
            iptuId = Integer.valueOf(ciptu[0] == null ? "0" : ciptu[0]);
            iptuMes = ciptu[1];
            iptuRef = ciptu[2];
            vrIptu = new BigDecimal(ciptu[3]);
        }
        if (vrIptu.compareTo(BigDecimal.ZERO) == 1) {
            corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("IPTU",iptuRef, vrIptu));
            //data.add(new tbvAltera("iptu",iptuMes, iptuId, true, "C", "IPTU", iptuRef, vrIptu));
        }

        try {
            Processa calc = new Processa(rgprp, rgimv, contrato, Dates.StringtoDate(vcto, "dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()), "dd-MM-yyyy"));
            BigDecimal tmulta = calc.Multa(new BigDecimal("0")); if ( tmulta.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Multa","", tmulta));
            BigDecimal tcorr = calc.Correcao(new BigDecimal("0")); if ( tcorr.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Correção","", tcorr));
            BigDecimal tjur = calc.Juros(new BigDecimal("0")); if ( tjur.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Juros","", tjur));
            BigDecimal texp = calc.Expediente(new BigDecimal("0")); if ( texp.compareTo(BigDecimal.ZERO) == 1) corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Expediente","", texp));
            corpoRecibos = CorpoReciboAdd(corpoRecibos, new iRecibo.CorpoRecibo("Total Recibo","", calc.TotalRecibo()));
        } catch (Exception e) {}

        return corpoRecibos;
    }

    private static iRecibo.CorpoRecibo[] CorpoReciboAdd(iRecibo.CorpoRecibo[] mArray, iRecibo.CorpoRecibo value) {
        iRecibo.CorpoRecibo[] temp = new iRecibo.CorpoRecibo[mArray.length+1];
        System.arraycopy(mArray,0,temp,0,mArray.length);
        temp[mArray.length] = value;
        return temp;
    }
}
