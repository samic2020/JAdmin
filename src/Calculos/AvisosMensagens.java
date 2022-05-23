package Calculos;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AvisosMensagens {
    DbMain conn = VariaveisGlobais.conexao;

    public Object[] VerificaFimCtroLocatario(String contrato) {
        Object[] retorno = new Object[] {null,false};
        String sql = "SELECT dtfim, dtaditamento FROM carteira WHERE contrato = '%s';";
        sql = String.format(sql, contrato);
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        long dtDiff = 0;
        try {
            while (ars.next()) {
                if (ars.getDate("dtfim") != null && ars.getDate("dtaditamento") == null) {
                    dtDiff = Dates.DtDiff(
                            Dates.DIA,

                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    DbMain.getDateTimeServer()
                            ),
                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    Dates.DateAdd(
                                            Dates.DIA,
                                            Integer.valueOf(VariaveisGlobais.am_dias) * - 1,
                                            new Date(ars.getDate("dtfim").getTime())
                                    )
                            )
                    );
                } else if (ars.getDate("dtfim") != null && ars.getDate("dtaditamento") != null) {
                    dtDiff = Dates.DtDiff(
                            Dates.DIA,

                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    DbMain.getDateTimeServer()
                            ),
                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    Dates.DateAdd(
                                            Dates.DIA,
                                            Integer.valueOf(VariaveisGlobais.am_dias) * - 1,
                                            new Date(ars.getDate("dtaditamento").getTime())
                                    )
                            )
                    );
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(ars);

        if (dtDiff > 0 && dtDiff <= Integer.valueOf(VariaveisGlobais.am_dias)) {
            retorno = new Object[] {"Este contrato termina em " + FuncoesGlobais.StrZero(String.valueOf(dtDiff),2)+ " dia(s)!",true};
        } else if (dtDiff < 0) {
            retorno = new Object[] {"Contrato expirado em " + FuncoesGlobais.StrZero(String.valueOf((dtDiff * -1) - Integer.valueOf(VariaveisGlobais.am_dias)) ,3) + " dia(s)!",true};
        } else {
            retorno = new Object[] {null,false};
        }
        return retorno;
    }

    public boolean VerificaAniLocatario(String contrato) {
        boolean retorno = false;

        String sql = "SELECT CASE WHEN l_fisjur THEN l_f_dtnasc ELSE l_j_dtctrosocial END AS dtAniv FROM locatarios WHERE l_contrato = '%s';";
        sql = String.format(sql, contrato);
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (ars.next()) {
                if (Dates.DateFormata("MM",Dates.convertFromSQLDateToJAVADate(ars.getDate("dtaniv"))) == Dates.DateFormata("MM", DbMain.getDateTimeServer())) {
                    retorno = true;
                }
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(ars);

        return retorno;
    }

    public boolean VerificaAniProprietario(String rgprp) {
        boolean retorno = false;

        String sql = "SELECT p_fisjur AS dtAniv FROM proprietarios WHERE p_rgprp = '%s' AND tipoprop <> 'ESPÓLIO';";
        sql = String.format(sql, rgprp);
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (ars.next()) {
                if (Dates.DateFormata("MM",Dates.convertFromSQLDateToJAVADate(ars.getDate("dtaniv"))) == Dates.DateFormata("MM", DbMain.getDateTimeServer())) {
                    retorno = true;
                }
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(ars);

        return retorno;
    }

    public Object[] VerificaFimdoSeguro(String contrato) {
        Object[] retorno = new Object[] {null,false};
        String sql = "SELECT l_dtapolice FROM locatarios WHERE l_contrato = '%s';";
        sql = String.format(sql, contrato);
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        long dtDiff = 0;
        try {
            while (ars.next()) {
                if (ars.getDate("l_dtapolice") != null) {
                    dtDiff = Dates.DtDiff(
                            Dates.DIA,

                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    DbMain.getDateTimeServer()
                            ),
                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    Dates.DateAdd(
                                            Dates.DIA,
                                            Integer.valueOf(VariaveisGlobais.am_segdias) * - 1,
                                            new Date(ars.getDate("l_dtapolice").getTime())
                                    )
                            )
                    );
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        DbMain.FecharTabela(ars);

        if (dtDiff > 0 && dtDiff <= Integer.valueOf(VariaveisGlobais.am_segdias)) {
            retorno = new Object[] {"Este Seguro termina em " + FuncoesGlobais.StrZero(String.valueOf(dtDiff),2)+ " dia(s)!",true};
        } else if (dtDiff < 0) {
            retorno = new Object[] {"Contrato com Seguro expirado em " + FuncoesGlobais.StrZero(String.valueOf((dtDiff * -1) - Integer.valueOf(VariaveisGlobais.am_dias)) ,3) + " dia(s)!",true};
        } else {
            retorno = new Object[] {null,false};
        }
        return retorno;
    }

    public Object[] VerificaReajuste(String contrato) {
        Object[] retorno = new Object[] {null,false};
        String sql = "SELECT dtinicio, dtfim, dtaditamento FROM carteira WHERE contrato = '%s';";
        sql = String.format(sql, contrato);
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        long dtDiff = 0;
        try {
            while (ars.next()) {
                if (ars.getDate("dtfim") != null) {
                    dtDiff = Dates.DtDiff(
                            Dates.MES,

                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    DbMain.getDateTimeServer()
                            ),
                            Dates.DateFormata(
                                    "yyyy-MM-dd",
                                    Dates.DateAdd(
                                            Dates.MES,
                                            Integer.valueOf(VariaveisGlobais.am_dias) * - 1,
                                            new Date(ars.getDate("dtaditamento").getTime())
                                    )
                            )
                    );
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        DbMain.FecharTabela(ars);

        if (dtDiff > 0 && dtDiff <= Integer.valueOf(VariaveisGlobais.am_dias)) {
            retorno = new Object[] {"Este contrato sera reajustado em " + FuncoesGlobais.StrZero(String.valueOf(dtDiff),2)+ " mês(es)!",true};
        } else if (dtDiff < 0) {
            retorno = new Object[] {"Contrato com reajuste expirado em " + FuncoesGlobais.StrZero(String.valueOf((dtDiff * -1) - Integer.valueOf(VariaveisGlobais.am_dias)) ,3) + " mês(es)!",true};
        } else {
            retorno = new Object[] {null,false};
        }
        return retorno;
    }

    public String VerificaBloqueio(String contrato) {
        String retorno = null;
        String selectSQL = "Select * FROM locabloq WHERE contrato = ?;";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY,new Object[][]{{"string", contrato}});
            while (rs.next()) {
                retorno = rs.getString("historico");
            }
        } catch (SQLException e) {retorno = null;}
        return retorno;
    }
}

/*
 private String ChecaTermino(String contrato) {
        String msg = "";
        String[][] campos = null;
        try {
            campos = conn.LerCamposTabela(new String[] {"dtinicio","dttermino","dtadito","dtseguro",
                    FuncoesGlobais.Subst("((Month(StrDate(dttermino)) = &1. AND Year(StrDate(dttermino)) = &2.)) AS pinta",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))})}, "CARTEIRA",
                    FuncoesGlobais.Subst("contrato = '" + contrato + "' AND ((Month(StrDate(dtinicio)) >= &1. AND " +
                    "Year(StrDate(dttermino)) >= &2.) OR (Month(StrDate(dttermino)) >= &1. AND Year(StrDate(dttermino)) = &2.))",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))}));
        } catch (Exception e) {}
        if (campos != null) {
            String tmesanor = campos[3][3]; if (tmesanor == null) tmesanor = "";
            String tdtini = campos[0][3];
            Date _inic = Dates.StringtoDate(tdtini,"dd/MM/yyyy");
            Date _comp = new Date();
            if (_inic.getYear() != _comp.getYear()) {
                if (tmesanor.isEmpty()) {
                    String meses = null;
                    try {meses = conn.LerParametros("REAJNUM");} catch (Exception e) {meses = "1";}
                    if (_inic.getMonth() - 1 >= _comp.getMonth() - 1) {
                        if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) <= Integer.valueOf(meses)) {
                            if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) > 0) {
                                msg = "Faltam " + ((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) + " Mes(es) para o reajuste!!!";
                            } else msg = "";
                        }
                    } else if (_inic.getMonth() == _comp.getMonth()) {
                        msg = "Este é o mês do reajuste!!!";
                        if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                    }
                } else {
                    boolean reaj = Integer.valueOf(tmesanor.substring(3,7)) >= Dates.iYear(new Date());
                    if (!reaj) {
                        if (_inic.getMonth() - 1 > _comp.getMonth() - 1) {
                            String meses = null;
                            try {meses = conn.LerParametros("REAJNUM");} catch (Exception e) {meses = "1";}
                            msg = "Faltam " + meses + " Mes(es) para o reajuste!!!";
                        } else if (_inic.getMonth() == _comp.getMonth()) {
                            msg = "Este é o mês do reajuste!!!";
                            if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                        }
                    }
                }
            }

        }
        return msg;
    }
 */