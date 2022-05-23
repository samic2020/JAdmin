package Relatorios;

import Calculos.Multas;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GetFields {
    DbMain conn = VariaveisGlobais.conexao;

    public GetFields() {}

   public GetFieldsClass GetFields(String eCodigo, String rgimv, String contrato, Date ini, Date fim) {
        // Inicia as Variaveis
       BigDecimal tAluguel = new BigDecimal("0");
       Date tDtVencto = null;
       BigDecimal tComissao = new BigDecimal("0");
       BigDecimal tDc = new BigDecimal("0");
       BigDecimal tDf = new BigDecimal("0");
       BigDecimal tCom = new BigDecimal("0");
       BigDecimal tIr = new BigDecimal("0");
       BigDecimal tIp = new BigDecimal("0");
       BigDecimal tSg = new BigDecimal("0");
       BigDecimal tTx = new BigDecimal("0");
       BigDecimal tMu = new BigDecimal("0");
       BigDecimal tJu = new BigDecimal("0");
       BigDecimal tCo = new BigDecimal("0");
       BigDecimal tEp = new BigDecimal("0");
       BigInteger tAut = new BigInteger("0");

        // Aqui pega os dados iniciais (rgprp, rgimv, contrato, nome)
       Object[][] nomeLoca = null;
       try {
           nomeLoca = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"},"locatarios", "l_contrato = '" + contrato + "'");
       } catch (Exception e) {}

       //Extrato = new jExtrato(Descr((String) nomeLoca[0][3]), null, null);

        // Aqui pega os recibos recebidos e não pagos
        String sql = "select * from movimento where rgprp = ? and rgimv = ? and aut_rec <> 0 and (dtrecebimento between ? and ?) order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][]{
                {"string", eCodigo},
                {"string", rgimv},
                {"date", Dates.toSqlDate(ini)},
                {"date", Dates.toSqlDate(fim)}
        });
        try {
            while (rs.next()) {
                tDtVencto = rs.getDate("dtvencimento");
                tAut = new BigInteger(rs.getString("aut_rec"));

                BigDecimal alu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                com = rs.getBigDecimal("cm");

                tAluguel = rs.getBigDecimal("mensal");
                tDtVencto = rs.getDate("dtvencimento");
                //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("ALU") + "  " + rs.getBigDecimal("mensal") + "  " + palu),alu, null );

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));
                tComissao = new BigDecimal(String.valueOf(VariaveisGlobais.co / 100));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where rgprp = ? and rgimv = ? and aut_rec <> 0 and (dtrecebimento between ? and ?) order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][]{
                            {"string", eCodigo},
                            {"string", rgimv},
                            {"date", Dates.toSqlDate(ini)},
                            {"date", Dates.toSqlDate(fim)}
                    });
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            dfcom = dfrs.getBigDecimal("valor");
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                tDc = dfcom;
                            } else {
                                tDf = dfcom;
                            }
                            //Extrato = new jExtrato(Descr(dftipostr + VariaveisGlobais.contas_ca.get("ALU") + " " + dfrs.getString("descricao")),dftipo.trim().equalsIgnoreCase("C") ? dfCR : null, dftipo.trim().equalsIgnoreCase("D") ? dfDB : null);
                        }
                    } catch (SQLException e) {}
                    try {
                        DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                tCom = rs.getBigDecimal("cm");
                //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COM") + "  " + rs.getBigDecimal("cm")),null, com);

                // IR
                BigDecimal pirvr = new BigDecimal("0");
                {
                    pirvr = rs.getBigDecimal("ir");

                    if (pirvr.doubleValue() != 0) {
                        tIr = pirvr;
                        //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IRF")), null, pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where rgprp = ? and rgimv = ? and aut_rec <> 0 and (dtrecebimento between ? and ?) order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][]{
                            {"string", eCodigo},
                            {"string", rgimv},
                            {"date", Dates.toSqlDate(ini)},
                            {"date", Dates.toSqlDate(fim)}
                    });
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal seg = new BigDecimal("0");
                            seg = sgrs.getBigDecimal("valor");

                            tSg = seg;
                            //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("SEG") + "  " + sgrs.getString("cota")),seg,sgrs.getBoolean("retencao") ? seg : null);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                        retorno.setip(rs.getBigDecimal("ip"));
                            pipvr = rs.getBigDecimal("ip");
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                    txsql = String.format(txsql,eCodigo, eCodigo);
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY);
                    BigDecimal ttxcr = new BigDecimal("0");
                    BigDecimal ttxdb = new BigDecimal("0");
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal txcom = new BigDecimal("0");
                            txcom = txrs.getBigDecimal("valor");
                            String txtipo = txrs.getString("tipo").trim();

                            if (txtipo.toUpperCase() == "C") {
                                ttxcr = ttxcr.add(txcom);
                            } else {
                                ttxdb = ttxdb.add(txcom);
                            }
                            //Extrato = new jExtrato(Descr(txdecr + "  " + txrs.getString("cota")),(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : null), (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : null));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                    tTx = ttxcr.subtract(ttxdb);
                }

                // MULTA
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    if (pmuvr.doubleValue() != 0) {
                        tMu = pmuvr;
                        //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("MUL")), pmuvr, null);
                    }
                }

                // JUROS
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    if (pjuvr.doubleValue() != 0) {
                        tJu = pjuvr;
                        //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("JUR")), pjuvr, null);
                    }
                }

                // CORREÇÃO
                BigDecimal pcovr = new BigDecimal("0");
                {
                    try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                    if (pcovr.doubleValue() != 0) {
                        tCo = pcovr;
                        //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COR")), pcovr,null);
                    }
                }

                // EXPEDIENTE
                BigDecimal pepvr = new BigDecimal("0");
                {
                    try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                    if (pepvr.doubleValue() != 0) {
                        tEp = pepvr;
                        //Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("EXP")), pepvr,null);
                    }
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return new GetFieldsClass(eCodigo, rgimv, contrato, nomeLoca[0][3].toString(), tDtVencto, tAluguel, tDc, tDf, tComissao, tCom, tIr, tIp, tEp, tMu, tJu, tCo, tSg, tTx, tAut);
   }
}
