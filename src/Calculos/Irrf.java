package Calculos;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by supervisor on 21/12/16.
 */
public class Irrf {
    DbMain conn = VariaveisGlobais.conexao;

    public BigDecimal Irrf(String rgprp, String contrato, String cpoRefer, BigDecimal cpoMensalValor, BigDecimal difal, BigDecimal desal) {
        BigDecimal irenda = new BigDecimal("0");
        try {
            if (conn.LerCamposTabela(new String[]{"p_fisjur"}, "proprietarios", "p_rgprp = '" + rgprp + "'")[0][3].equals("F") &&
                    conn.LerCamposTabela(new String[]{"l_fisjur"}, "locatarios", "l_contrato = '" + contrato + "'")[0][3].equals("f")) {
                if (VerificaIrrf(cpoRefer)) {
                    irenda = Irrf(cpoMensalValor.add(difal).subtract(desal), cpoRefer);
                }
            }
        } catch (SQLException e) {}

        return irenda;
    }

    private BigDecimal Irrf(BigDecimal aluguel, String tmesano) {
        BigDecimal ir = new BigDecimal("0");
        String tSql = "SELECT * FROM irrf WHERE mesano = '%s';";
        tSql = String.format(tSql, tmesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) {
                if (aluguel.compareTo(trs.getBigDecimal("faixa1")) == -1 || aluguel.compareTo(trs.getBigDecimal("faixa1")) == 0) {
                    // Isento
                    ir = new BigDecimal("0");
                } else if (aluguel.compareTo(trs.getBigDecimal("faixa1")) == 1 && (aluguel.compareTo(trs.getBigDecimal("faixa2")) == -1 || aluguel.compareTo(trs.getBigDecimal("faixa2")) == 0)) {
                    ir = aluguel.multiply(trs.getBigDecimal("aliquota2").divide(new BigDecimal("100"))).subtract(trs.getBigDecimal("deducao2"));
                } else if (aluguel.compareTo(trs.getBigDecimal("faixa2")) == 1 && (aluguel.compareTo(trs.getBigDecimal("faixa3")) == -1 || aluguel.compareTo(trs.getBigDecimal("faixa3")) == 0)) {
                    ir = aluguel.multiply(trs.getBigDecimal("aliquota3").divide(new BigDecimal("100"))).subtract(trs.getBigDecimal("deducao3"));
                } else if (aluguel.compareTo(trs.getBigDecimal("faixa3")) == 1 && (aluguel.compareTo(trs.getBigDecimal("faixa4")) == -1 || aluguel.compareTo(trs.getBigDecimal("faixa4")) == 0)) {
                    ir = aluguel.multiply(trs.getBigDecimal("aliquota4").divide(new BigDecimal("100"))).subtract(trs.getBigDecimal("deducao4"));
                } else if (aluguel.compareTo(trs.getBigDecimal("faixa4")) == 1 && (aluguel.compareTo(trs.getBigDecimal("faixa5")) == -1 || aluguel.compareTo(trs.getBigDecimal("faixa5")) == 0)) {
                    ir = aluguel.multiply(trs.getBigDecimal("aliquota5").divide(new BigDecimal("100"))).subtract(trs.getBigDecimal("deducao5"));
                } else if (aluguel.compareTo(trs.getBigDecimal("faixa5")) == 1 ) {
                    ir = aluguel.multiply(trs.getBigDecimal("aliquota5").divide(new BigDecimal("100"))).subtract(trs.getBigDecimal("deducao5"));
                }
            }
        } catch (SQLException e) {}
        finally { try {trs.close();} catch (SQLException e) {} }
        return ir.setScale(2, RoundingMode.UP);
    }

    private boolean VerificaIrrf(String tmesano) {
        boolean iscad = false;
        String tSql = "SELECT * FROM irrf WHERE mesano = '%s';";
        tSql = String.format(tSql, tmesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) { iscad = true; }
        } catch (SQLException e) {}
        finally { try {trs.close();} catch (SQLException e) {} }
        return iscad;
    }
}
