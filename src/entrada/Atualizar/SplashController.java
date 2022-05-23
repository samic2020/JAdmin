package entrada.Atualizar;

import Calculos.AutentMult;
import Calculos.Multas;
import Calculos.PegaDivisao;
import Classes.jExtrato;
import Funcoes.*;
import Gerencia.divSec;
import Movimento.Extrato.ExtratoBloqClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.awt.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = new DbMain(VariaveisGlobais.unidade,"postgres",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);

        VariaveisGlobais.conexao = conn;

        //final Service thread = new Service<Integer>() {
        //    @Override
        //    protected Task<Integer> createTask() {
        //        return new Task<Integer>() {
        //            @Override
        //            protected Integer call() throws InterruptedException {
                        // Apaga arquivo de extratos_movel
                        String dSQL = "DELETE FROM extrato_movel;";
                        conn.ExecutarComando(dSQL);

                        ResultSet imv = null;
                        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios ORDER BY p_rgprp;";
                        try {
                            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
                            int registros = DbMain.RecordCount(imv);
                            int pos = 0;
                            while (imv.next()) {
                                String qcontrato = null, qnome = null;
                                try {qcontrato = imv.getString("p_rgprp");} catch (SQLException e) {}
                                try {qnome = imv.getString("p_nome");} catch (SQLException e) {}

                                Object[] extrato = pExtrato(qcontrato);
                                if (extrato.length > 0) {
                                    float saldo = (float)extrato[0];
                                    List<jExtrato> lista = (List<jExtrato>)extrato[1];
                                    if (saldo > 0) {
                                        for (jExtrato value : lista) {
                                            String iSQL = "INSERT INTO extrato_movel (rgprp, hist_linha, hist_cred, hist_deb) VALUES ('%s','%s','%s','%s');";
                                            iSQL = String.format(iSQL,
                                                    qcontrato,
                                                    value.getHist_linha() != null ? RemoveHtml(value.getHist_linha()) : "",
                                                    value.getHist_cred() != null ? LerValor.BigDecimalToDouble(value.getHist_cred()) : 0,
                                                    value.getHist_deb() != null ? LerValor.BigDecimalToDouble(value.getHist_deb()) : 0
                                            );

                                            conn.ExecutarComando(iSQL);
                                        }
                                    }
                                }
                                //updateProgress(pos++,100);
                                Thread.sleep(10);
                            }
                        } catch (Exception e) {}
                        try { DbMain.FecharTabela(imv); } catch (Exception e) {e.printStackTrace();}

//                        return null;
  //                  }
    //            };
      //      }
        //};

        //thread.start();
    }

    private void AjustaExtratoMovel() {
        // Apaga arquivo de extratos_movel
        String dSQL = "DELETE FROM extrato_movel;";
        conn.ExecutarComando(dSQL);

        ResultSet imv = null;
        String qSQL = "SELECT p_rgprp, p_nome FROM proprietarios ORDER BY p_rgprp;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            int registros = DbMain.RecordCount(imv);
            int pos = 0;
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("p_rgprp");} catch (SQLException e) {}
                try {qnome = imv.getString("p_nome");} catch (SQLException e) {}

                Object[] extrato = pExtrato(qcontrato);
                if (extrato.length > 0) {
                    float saldo = (float)extrato[0];
                    List<jExtrato> lista = (List<jExtrato>)extrato[1];
                    if (saldo > 0) {
                        for (jExtrato value : lista) {
                            String iSQL = "INSERT INTO extrato_movel (rgprp, hist_linha, hist_cred, hist_deb) VALUES ('%s','%s','%s','%s');";
                            iSQL = String.format(iSQL,
                                        qcontrato,
                                        value.getHist_linha(),
                                        value.getHist_cred(),
                                        value.getHist_deb()
                                    );
                            conn.ExecutarComando(iSQL);
                        }
                    }
                }

            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

    }

    private Object[] pExtrato(String eCodigo) {
        ObservableList<ExtratoBloqClass> bloqdata = FXCollections.observableArrayList();

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM propsaldo Where registro = '%s' and aut_pag is not null AND aut_pag[1][2] is null;";
        saSql = String.format(saSql,eCodigo);
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}
        if (ttCR.floatValue() != 0) {
            Extrato = new jExtrato(Descr("<b>Saldo Anterior</b>"), ttCR, null);
            lista.add(Extrato);

            // Pula linha
            Extrato = new jExtrato(null, null, null);
            lista.add(Extrato);
        }

        // Pegar as percentagens do principal
        List<divSec> dPrin = new PegaDivisao().PegaDivisoes(eCodigo);

        // Pegar as divisões secundárias
        List<divSec> dSec = new PegaDivisao().PegaDivSecundaria(eCodigo);
        String sDivWhere = "";
        for (divSec campo : dSec) {
            sDivWhere += "select * from (select *, generate_subscripts(aut_pag,1) as pos, generate_subscripts(reserva,1) as rpos from movimento where rgprp = '" + campo.getRgprp() +
                    "' and rgimv = '" + campo.getRgimv() + "' and aut_rec <> 0) as foo where aut_pag[pos][1] = '" + eCodigo +
                    "' and aut_pag[pos][2] is null and (reserva is null or reserva[rpos][1] = '" + VariaveisGlobais.usuario + "') union all ";
        }
        if (sDivWhere.length() > 0) sDivWhere = sDivWhere.substring(0, sDivWhere.length() - 10);

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        if (sDivWhere.length() == 0) {
            sql = "select * from movimento where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
            sql = String.format(sql,eCodigo, eCodigo);
        } else {
            sql = "select *, 0 as pos, 0 as rpos from movimento where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
            sql = String.format(sql,eCodigo, eCodigo, sDivWhere);
        }

        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                Object[][] endImovel = null;
                try {
                    endImovel = conn.LerCamposTabela(new String[] {"i_end", "i_num", "i_cplto"},"imoveis", "i_rgimv = '" + rs.getString("rgimv") + "'");
                } catch (Exception e) {}
                String linha = "<b>" + rs.getString("rgimv") + "</b> - " + endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim();
                if (!bloq) {
                    Extrato = new jExtrato(Descr(linha), null, null);
                    lista.add(Extrato);
                }

                Object[][] nomeLoca = null;
                try {
                    nomeLoca = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"},"locatarios", "l_contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {}
                if (!bloq) {
                    Extrato = new jExtrato(Descr((String) nomeLoca[0][3]), null, null);
                    lista.add(Extrato);

                    Extrato = new jExtrato(Descr("[" + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtrecebimento")) + "] - " + rs.getString("aut_rec")), null, null);
                    lista.add(Extrato);
                }

                if (bloq) {
                    bloqdata.add(new ExtratoBloqClass(
                            "R",
                            rs.getString("rgimv"),
                            endImovel[0][3].toString().trim() + ", " + endImovel[1][3].toString().trim() + " " + endImovel[2][3].toString().trim(),
                            new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("dtvencimento")))
                    );
                    continue;
                }

                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                int dpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                if (dpos > -1) {
                    String[] divisao = dPrin.get(dpos).getDivisao().split(",");
                    int apos = FuncoesGlobais.IndexOf(divisao,"ALU");
                    if (apos > -1) {
                        palu = new PegaDivisao().LerPercent(divisao[apos],true);
                    }
                } else {
                    dpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                    if (dpos > -1) {
                        String[] divisao = dSec.get(dpos).getDivisao().split(",");
                        int apos = FuncoesGlobais.IndexOf(divisao, "ALU");
                        if (apos > -1) {
                            palu = new PegaDivisao().LerPercent(divisao[apos], true);
                        }
                    }
                }
                alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("ALU") + "  " + rs.getBigDecimal("mensal") + "  " + palu),alu, null );
                lista.add(Extrato);
                ttCR = ttCR.add(alu);

                // Teste de Gravação de uma variavel modo Read
                Reserva(rs.getInt("id"), rs.getString("reserva"), "movimento");

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    if (sDivWhere.length() == 0) {
                        dfsql = "select * from descdif where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        dfsql = String.format(dfsql,eCodigo, eCodigo);
                    } else {
                        dfsql = "select *, 0 as pos, 0 as rpos from descdif where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        dfsql = String.format(dfsql,eCodigo, eCodigo, sDivWhere.replace("movimento","descdif"));
                    }
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY);
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

                            Extrato = new jExtrato(Descr(dftipostr + VariaveisGlobais.contas_ca.get("ALU") + " " + dfrs.getString("descricao")),dftipo.trim().equalsIgnoreCase("C") ? dfCR : null, dftipo.trim().equalsIgnoreCase("D") ? dfDB : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COM") + "  " + rs.getBigDecimal("cm")),null, com);
                lista.add(Extrato);
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    int irpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (irpos > -1) {
                        String[] divisao = dPrin.get(irpos).getDivisao().split(",");
                        int airpos = FuncoesGlobais.IndexOf(divisao,"IRF");
                        if (airpos > -1) {
                            pir = new PegaDivisao().LerPercent(divisao[airpos],true);
                        }
                    } else {
                        irpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (irpos > -1) {
                            String[] divisao = dSec.get(irpos).getDivisao().split(",");
                            int airpos = FuncoesGlobais.IndexOf(divisao, "IRF");
                            if (airpos > -1) {
                                pir = new PegaDivisao().LerPercent(divisao[airpos], true);
                            }
                        }
                    }
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IRF")), null, pirvr);
                        lista.add(Extrato);
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    if (sDivWhere.length() == 0) {
                        sgsql = "select * from seguros where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        sgsql = String.format(sgsql,eCodigo, eCodigo);
                    } else {
                        sgsql = "select *, 0 as pos, 0 as rpos from seguros where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        sgsql = String.format(sgsql,eCodigo, eCodigo, sDivWhere.replace("movimento","seguros"));
                    }
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            int dsgpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (dsgpos > -1) {
                                String[] divisao = dPrin.get(dsgpos).getDivisao().split(",");
                                int asgpos = FuncoesGlobais.IndexOf(divisao,"SEG");
                                if (asgpos > -1) {
                                    psg = new PegaDivisao().LerPercent(divisao[asgpos],true);
                                }
                            } else {
                                dsgpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (dsgpos > -1) {
                                    String[] divisao = dSec.get(dsgpos).getDivisao().split(",");
                                    int asgpos = FuncoesGlobais.IndexOf(divisao, "SEG");
                                    if (asgpos > -1) {
                                        psg = new PegaDivisao().LerPercent(divisao[asgpos], true);
                                    }
                                }
                            }

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("SEG") + "  " + sgrs.getString("cota")),seg,sgrs.getBoolean("retencao") ? seg : null);
                            lista.add(Extrato);
                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            int ippos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (ippos > -1) {
                                String[] divisao = dPrin.get(ippos).getDivisao().split(",");
                                int aippos = FuncoesGlobais.IndexOf(divisao,"IRF");
                                if (aippos > -1) {
                                    pir = new PegaDivisao().LerPercent(divisao[aippos],true);
                                }
                            } else {
                                ippos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (ippos > -1) {
                                    String[] divisao = dSec.get(ippos).getDivisao().split(",");
                                    int aippos = FuncoesGlobais.IndexOf(divisao, "IPT");
                                    if (aippos > -1) {
                                        pip = new PegaDivisao().LerPercent(divisao[aippos], true);
                                    }
                                }
                            }
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IPT")), null, pipvr);
                                lista.add(Extrato);
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "";
                    if (sDivWhere.length() == 0) {
                        txsql = "select * from taxas where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 2,3,4,7,9;";
                        txsql = String.format(txsql,eCodigo, eCodigo);
                    } else {
                        txsql = "select *, 0 as pos, 0 as rpos from taxas where rgprp = '%s' and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') union all %s order by 2,3,4,7,9";
                        txsql = String.format(txsql,eCodigo, eCodigo, sDivWhere.replace("movimento","taxas"));
                    }
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            int dtxpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (dtxpos > -1) {
                                String[] divisao = dPrin.get(dtxpos).getDivisao().split(",");
                                int atxpos = FuncoesGlobais.IndexOf(divisao,txrs.getString("campo"));
                                if (atxpos > -1) {
                                    ptx = new PegaDivisao().LerPercent(divisao[atxpos],true);
                                }
                            } else {
                                dtxpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (dtxpos > -1) {
                                    String[] divisao = dSec.get(dtxpos).getDivisao().split(",");
                                    int atxpos = FuncoesGlobais.IndexOf(divisao, txrs.getString("campo"));
                                    if (atxpos > -1) {
                                        ptx = new PegaDivisao().LerPercent(divisao[atxpos], true);
                                    }
                                }
                            }

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            String txdecr = conn.LerCamposTabela(new String[] {"descricao"}, "campos","codigo = '" + txrs.getString("campo") + "'")[0][3].toString();
                            Extrato = new jExtrato(Descr(txdecr + "  " + txrs.getString("cota")),(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : null), (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : null));
                            lista.add(Extrato);
                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    int mupos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (mupos > -1) {
                        String[] divisao = dPrin.get(mupos).getDivisao().split(",");
                        int amupos = FuncoesGlobais.IndexOf(divisao,"MUL");
                        if (amupos > -1) {
                            pmu = new PegaDivisao().LerPercent(divisao[amupos],true);
                        }
                    } else {
                        mupos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (mupos > -1) {
                            String[] divisao = dSec.get(mupos).getDivisao().split(",");
                            int amupos = FuncoesGlobais.IndexOf(divisao, "MUL");
                            if (amupos > -1) {
                                pmu = new PegaDivisao().LerPercent(divisao[amupos], true);
                            }
                        }
                    }
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("MUL")), pmuvr, null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    int jupos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (jupos > -1) {
                        String[] divisao = dPrin.get(jupos).getDivisao().split(",");
                        int ajupos = FuncoesGlobais.IndexOf(divisao,"JUR");
                        if (ajupos > -1) {
                            pju = new PegaDivisao().LerPercent(divisao[ajupos],true);
                        }
                    } else {
                        jupos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (jupos > -1) {
                            String[] divisao = dSec.get(jupos).getDivisao().split(",");
                            int ajupos = FuncoesGlobais.IndexOf(divisao, "JUR");
                            if (ajupos > -1) {
                                pju = new PegaDivisao().LerPercent(divisao[ajupos], true);
                            }
                        }
                    }
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("JUR")), pjuvr, null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pjuvr);
                    }
                }

                // CORREÇÃO
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                {
                    int copos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (copos > -1) {
                        String[] divisao = dPrin.get(copos).getDivisao().split(",");
                        int acopos = FuncoesGlobais.IndexOf(divisao,"COR");
                        if (acopos > -1) {
                            pco = new PegaDivisao().LerPercent(divisao[acopos],true);
                        }
                    } else {
                        copos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (copos > -1) {
                            String[] divisao = dSec.get(copos).getDivisao().split(",");
                            int acopos = FuncoesGlobais.IndexOf(divisao, "COR");
                            if (acopos > -1) {
                                pco = new PegaDivisao().LerPercent(divisao[acopos], true);
                            }
                        }
                    }
                    try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                    try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                    try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                    if (pcovr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("COR")), pcovr,null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pcovr);
                    }
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                {
                    int eppos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (eppos > -1) {
                        String[] divisao = dPrin.get(eppos).getDivisao().split(",");
                        int aeppos = FuncoesGlobais.IndexOf(divisao,"EXP");
                        if (aeppos > -1) {
                            pep = new PegaDivisao().LerPercent(divisao[aeppos],true);
                        }
                    } else {
                        eppos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (eppos > -1) {
                            String[] divisao = dSec.get(eppos).getDivisao().split(",");
                            int aeppos = FuncoesGlobais.IndexOf(divisao, "EXP");
                            if (aeppos > -1) {
                                pep = new PegaDivisao().LerPercent(divisao[aeppos], true);
                            }
                        }
                    }
                    try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                    try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                    try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                    if (pepvr.doubleValue() != 0) {
                        Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("EXP")), pepvr,null);
                        lista.add(Extrato);
                        ttCR = ttCR.add(pepvr);
                    }
                }

                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (SQLException ex) {}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where registro = '%s' and aut_rec <> 0 and aut_pag is not null and aut_pag[1][2] is null and conta = 1 and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 1;";
        avsql = String.format(avsql, eCodigo);
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    bloqdata.add(new ExtratoBloqClass(
                            "A",
                            avrs.getString("registro"),
                            avrs.getString("texto"),
                            "-"
                    ));
                    continue;
                }

                Font font = new Font("SansSerif",Font.PLAIN,8);
                Canvas c = new Canvas();
                FontMetrics fm = c.getFontMetrics(font);
                String aLinhas[] = WordWrap.wrap(avrs.getString("texto") + "  " + new SimpleDateFormat("dd/MM/yyyy").format(avrs.getDate("dtrecebimento")) + " - " + avrs.getString("aut_rec"), 230, fm).split("\n");
                for (int k=0;k<aLinhas.length;k++) {
                    BigDecimal lcr = null, ldb = null;
                    lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                    ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;
                    Extrato = new jExtrato(Descr(aLinhas[k]), k == aLinhas.length - 1 ? lcr : null, k == aLinhas.length - 1 ? ldb : null);
                    lista.add(Extrato);
                }

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));

                // Pula Linha
                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Créditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de Déditos</b></font>"), null, ttDB);
        lista.add(Extrato);

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        String tDesc = "";
        if (ttSld.floatValue() > 0) {
            tDesc = "<font color=blue><b>Líquido a Receber</b></font>";
        } else {
            tDesc = "<font color=red><b>Líquido a Receber</b></font>";
        }
        Extrato = new jExtrato(Descr(tDesc), ttSld.floatValue() > 0 ? ttSld : null, ttSld.floatValue() < 0 ? ttSld : null);
        lista.add(Extrato);

        Extrato = new jExtrato(null,  null, null);
        lista.add(Extrato);

        // Dados Bancários para Depósito
        sql = "SELECT p_nome, p_bancos FROM proprietarios WHERE p_rgprp = '%s';";
        sql = String.format(sql,eCodigo);
        rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        String nomeProp = null; String banco = null;
        try {
            rs.next();
            nomeProp = rs.getString("p_nome");
            banco = rs.getString("p_bancos");
        } catch (SQLException ex) {}
        try {rs.close();} catch (Exception ex) {}

        String[] bancos = null; String[] bancoPrin = null;
        if (banco != null) {
            bancos = banco.split(";");
        }
        if (bancos != null) {
            bancoPrin = bancos[0].split(",");
        }

        if (bancoPrin != null) {
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Banco: " + bancoPrin[0] + " - " + conn.LerCamposTabela(new String[] {"nome"},"bancos","numero = '" + bancoPrin[0] + "'")[0][3] + "</b></font>"),  null, null); } catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Agência: " + bancoPrin[1] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>C/C: " + bancoPrin[2] + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
            try { Extrato = new jExtrato(Descr("<font color=blue><b>Favorecido: " + (bancoPrin[3].equalsIgnoreCase("null") ? "O Próprio" : bancoPrin[3])  + "</b></font>"),  null, null);} catch (Exception e) {}
            lista.add(Extrato);
        }

        return new Object[] {ttSld.floatValue(), lista};
    }

    private String Descr(String desc) { return "<html>" + desc + "</html>"; }

    private void Reserva(int id, String fieldReserva, String local) {
        String sReserva = "";
        if (fieldReserva == null) {
            sReserva = new AutentMult().ReservaMontaAutInicial();
        } else {
            Object[][] oReserva = new AutentMult().ReservaConvertArrayString2ObjectArrays(fieldReserva);
            if (oReserva == null) {
                sReserva = new AutentMult().ReservaMontaAutInicial();
            } else {
                int rpos = FuncoesGlobais.FindinObject(oReserva,0,VariaveisGlobais.usuario);
                if (rpos < 0) {
                    oReserva = FuncoesGlobais.ObjectsAdd(oReserva, new Object[] {VariaveisGlobais.usuario, new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer())});
                }
                sReserva = new AutentMult().ObjectArrays2String(oReserva);
            }
        }
        String uSql = "";
        if (local.equalsIgnoreCase("movimento")) {
            uSql = "UPDATE %s SET reserva = '%s' WHERE id = %s";
            uSql = String.format(uSql,local, sReserva,id);
        }

        conn.ExecutarComando(uSql);
    }

    private String RemoveHtml(String value) {
        String retorno = value;
        retorno = retorno.replace("<html>","");
        retorno = retorno.replace("</html>","");
        retorno = retorno.replace("<b>","");
        retorno = retorno.replace("</b>","");
        retorno = retorno.replace("<i>","");
        retorno = retorno.replace("</i>","");
        retorno = retorno.replace("<font color=blue>","");
        retorno = retorno.replace("<font color=green>","");
        retorno = retorno.replace("<font color=red>","");
        retorno = retorno.replace("<font color=yellow>","");
        retorno = retorno.replace("</font>","");
        return retorno;
    }
}
