package Calculos;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by supervisor on 21/12/16.
 */
public class Iptu {
    DbMain conn = VariaveisGlobais.conexao;

    public String[] Iptu(String rgimv, String cpoRefer) {
        if (cpoRefer == null) {return null;}

        // IPTU
        ResultSet rs = null;
        String tSql = null;
        tSql = "SELECT id, rgprp, rgimv, matricula, ano, vencimento, faixa1, faixa2, " +
                "       faixa3, faixa4, jan, fev, mar, abr, mai, jun, jul, ago, set, " +
                "       out, nov, dez FROM iptu WHERE rgimv = '%s';";
        tSql = String.format(tSql, rgimv);
        rs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        String rid = null;

        BigDecimal faixa = null;
        String rmes = cpoRefer.substring(0, 2);
        String rano = cpoRefer.substring(3);
        String[] smeses = new String[]{"", "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez"};
        String rrmes = null;
        if (Integer.valueOf(rmes) <= 12) {
            rrmes = smeses[Integer.valueOf(rmes)];
            try {
                while (rs.next()) {
                    try {rid = String.valueOf(rs.getInt("id"));} catch (SQLException e) {}

                    if (rs.getBigDecimal(rrmes) != null && rs.getBigDecimal(rrmes).compareTo(BigDecimal.ZERO) != 0) {
                        faixa = rs.getBigDecimal(rrmes);
                        break;
                    }
                    String rdiavecto = null;
                    try {
                        rdiavecto = rs.getString("vencimento");
                    } catch (SQLException e) {
                    }
                    if (rdiavecto != null) {
                        String faixa1 = rdiavecto + "/" + Dates.DateFormata("MM/yyyy", new java.util.Date());
                        String faixa2 = Dates.DateFormata("dd/MM/yyyy", Dates.DateAdd(Dates.MES, 1, Dates.StringtoDate(faixa1, "dd/MM/yyyy")));
                        String faixa3 = Dates.DateFormata("dd/MM/yyyy", Dates.DateAdd(Dates.MES, 2, Dates.StringtoDate(faixa1, "dd/MM/yyyy")));
                        String faixa4 = Dates.DateFormata("dd/MM/yyyy", Dates.DateAdd(Dates.MES, 3, Dates.StringtoDate(faixa1, "dd/MM/yyyy")));

                        java.util.Date dfaixa1 = Dates.StringtoDate(Dates.StringtoString(faixa1, "dd/MM/yyyy", "yyyy/MM/dd"), "yyyy/MM/dd");
                        java.util.Date dfaixa2 = Dates.StringtoDate(Dates.StringtoString(faixa2, "dd/MM/yyyy", "yyyy/MM/dd"), "yyyy/MM/dd");
                        java.util.Date dfaixa3 = Dates.StringtoDate(Dates.StringtoString(faixa3, "dd/MM/yyyy", "yyyy/MM/dd"), "yyyy/MM/dd");
                        java.util.Date dfaixa4 = Dates.StringtoDate(Dates.StringtoString(faixa4, "dd/MM/yyyy", "yyyy/MM/dd"), "yyyy/MM/dd");

                        java.util.Date hoje = Dates.StringtoDate(Dates.DateFormata("yyyy/MM/dd", new java.util.Date()), "yyyy/MM/dd");

                        if (hoje.compareTo(dfaixa1) >= 0 && hoje.compareTo(dfaixa2) <= 0) {
                            faixa = rs.getBigDecimal("faixa1");
                            break;
                        }
                        if (hoje.compareTo(dfaixa2) >= 0 && hoje.compareTo(dfaixa3) <= 0) {
                            faixa = rs.getBigDecimal("faixa2");
                            break;
                        }
                        if (hoje.compareTo(dfaixa3) >= 0 && hoje.compareTo(dfaixa4) <= 0) {
                            faixa = rs.getBigDecimal("faixa3");
                            break;
                        }
                        if (hoje.compareTo(dfaixa4) >= 0) {
                            faixa = rs.getBigDecimal("faixa4");
                            break;
                        }
                    } else break;
                }
            } catch (SQLException e) {}
        }
        try { rs.close(); } catch (SQLException e) {}

        if (faixa == null) faixa = new BigDecimal(0);
        return new String[] {rid, rrmes, cpoRefer, faixa.toPlainString()};
    }
}
