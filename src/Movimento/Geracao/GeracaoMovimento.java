package Movimento.Geracao;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by supervisor on 01/09/16.
 */
public class GeracaoMovimento {
    DbMain conn = VariaveisGlobais.conexao;

    public GeracaoMovimento(String mCarteira) {
        String cSql = null;
        if (mCarteira != null) {
            cSql = "select c.* from carteira c, imoveis i where (c.contrato = '" + mCarteira + "') and c.rgimv = i.i_rgimv and Upper(i.i_situacao) NOT LIKE '%VAZIO%';";
        } else {
            cSql = "select c.* from carteira c, imoveis i where c.rgimv = i.i_rgimv and Upper(i.i_situacao) NOT LIKE '%VAZIO%';";
        }
        ResultSet crs = conn.AbrirTabela(cSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                int crgprp = -1;
                try {crgprp = crs.getInt("rgprp");} catch (SQLException e) {}
                int crgimv = -1;
                try {crgimv = crs.getInt("rgimv");} catch (SQLException e) {}
                String ccontrato = null;
                try {ccontrato = crs.getString("contrato");} catch (SQLException e) {}
                String ccota = null; String ucota = null;
                try {ccota = crs.getString("cota");} catch (SQLException e) {}
                BigDecimal cmensal = null;
                try {cmensal = crs.getBigDecimal("mensal");} catch (SQLException e) {}
                Date cvecto = null; Date uvecto = null;
                try {cvecto = crs.getDate("dtvencimento");} catch (SQLException e) {}
                String cref = null; String uref = null;
                try {cref = crs.getString("referencia");} catch (SQLException e) {}

                ucota = FuncoesGlobais.addCota(ccota,"12");
                uref = FuncoesGlobais.addCota(cref);
                uvecto = Dates.sqlDateAdd(Dates.MES,1,cvecto);

                // atualiza carteira
                try {
                    String tmpSQL = "UPDATE carteira SET cota = '%s', referencia = '%s', dtvencimento = '%s' WHERE contrato = '%s';";
                    tmpSQL = String.format(tmpSQL, ucota, uref, uvecto, ccontrato);
                    if (conn.ExecutarComando(tmpSQL) <= 0) {
                        System.out.println("Não atualizado!!!");
                    }
                } catch (Exception e) {}

                BigDecimal irenda = new BigDecimal("0");
                String pProp = (String)conn.LerCamposTabela(new String[] {"p_fisjur"},"proprietarios","p_rgprp = '" + crgprp + "'")[0][3];
                String pLoca = conn.LerCamposTabela(new String[] {"l_fisjur"},"locatarios","l_contrato = '" + ccontrato + "'")[0][3].toString().toUpperCase().equalsIgnoreCase("F") ? "J" : "F";
                if ( pProp.equalsIgnoreCase("F") && pLoca.equalsIgnoreCase("J")) { // False = Juridica
                    if (VerificaIrrf(Dates.DateFormata("MM/yyyy", cvecto))) {
                        irenda = Irrf(cmensal, Dates.DateFormata("MM/yyyy", cvecto));
                    }
                    System.out.println("Vencimento: " + cvecto + "\nAluguel: " + cmensal + "\nIRRF: " + irenda);
                }

                // insere em movimento
                try {
                    String tmpSQL = "INSERT INTO movimento (rgprp, rgimv, contrato, cota, mensal, dtvencimento, referencia, selected) VALUES (" +
                            "'%s','%s','%s','%s','%s','%s','%s', '%s');";
                    tmpSQL = String.format(tmpSQL, crgprp, crgimv, ccontrato, ccota, cmensal, cvecto, cref, true);
                    if (conn.ExecutarComando(tmpSQL) <= 0) {
                        System.out.println("Não atualizado!!!");
                    }
                } catch (Exception e) {}

                // insere em taxas taxas anteriores
                try {
                    String insertSQL = "INSERT INTO taxas(rgprp, rgimv, contrato, precampo, campo, poscampo, cota, " +
                            "valor, dtvencimento, referencia, retencao, extrato, tipo, matricula, " +
                            "dtretencao, dtlanc, usr_lanc) " +
                            "selected, aut_pag, reserva, ad_pag) " +
                            "(SELECT rgprp, rgimv, contrato, precampo, campo, poscampo, ? cota, " +
                            "valor, ? dtvencimento, ? referencia, retenção, extrato, tipo, matricula, " +
                            "dtretencao, dtlanc, usr_lanc FROM taxas WHERE rgprp = ? and rgimv = ? and contrato = ? anc referencia = ?)";
                    Object[][] param = new Object[][] {
                            {"string", ccota}, {"string", cvecto}, {"string", uref},
                            {"string", crgprp}, {"string", crgimv}, {"string", ccontrato}, {"string",cref}
                    };

                    if (conn.ExecutarComando(insertSQL, param) <= 0) {
                        System.out.println("Não atualizado!!!");
                    }
                } catch (Exception e) {}

                if (FuncoesGlobais.checkCota(ccota)) {
                    // Reajuste
                    // Verifica Indice de Reajuste
                    if (VerificaIRA(Dates.DateFormata("MM/yyyy", uvecto)) && VariaveisGlobais.reajManAluguel) {
                        BigDecimal indReaj = null;
                        indReaj = PegaIRA(Dates.DateFormata("MM/yyyy", uvecto));
                        if (indReaj != null && !VariaveisGlobais.reajManAluguel) {
                            // atualiza aluguel carteira
                            try {
                                String tmpSQL = "UPDATE carteira SET mensal = '%s' WHERE contrato = '%s';";
                                tmpSQL = String.format(tmpSQL, cmensal.multiply(new BigDecimal("1").add(indReaj.divide(new BigDecimal("100")))), ccontrato);
                                if (conn.ExecutarComando(tmpSQL) <= 0) {
                                    System.out.println("Não atualizado!!!");
                                }
                            } catch (Exception e) {}
                        }
                    } else {
                        //TODO - Rotina que lista os contratos que deveriam ter sido reajustados
                        System.out.println("contrato: " + ccontrato + "\n" + "Vencimento: " + cvecto + "/" + uvecto);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { crs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private boolean ReajusteAnualAutomatico(String rCarteira, BigDecimal cVrAluguel) {
        boolean isok = false;

        return isok;
    }

    private boolean VerificaIRA(String mesano) {
        boolean isexist = false;
        String tSql = "SELECT id, mesano, indice FROM ir WHERE mesano = '%s';";
        tSql = String.format(tSql,mesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) {isexist = true;}
        } catch (SQLException e) {} finally {
            try { trs.close();} catch (Exception e) {}
        }
        return isexist;
    }

    private BigDecimal PegaIRA(String mesano) {
        BigDecimal VrIndice = null;
        String tSql = "SELECT id, mesano, indice FROM ir WHERE mesano = '%s';";
        tSql = String.format(tSql,mesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) {VrIndice = trs.getBigDecimal("indice");}
        } catch (SQLException e) {} finally {
            try { trs.close();} catch (Exception e) {}
        }
        return VrIndice;
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


}
