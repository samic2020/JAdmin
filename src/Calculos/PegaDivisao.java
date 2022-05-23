package Calculos;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import Gerencia.divItens;
import Gerencia.divProp;
import Gerencia.divSec;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by supervisor on 03/07/17.
 */
public class PegaDivisao {
    DbMain conn = VariaveisGlobais.conexao;

    public List<divSec> PegaDivPrincipal(String registro) {
        List<divSec> data = new ArrayList<divSec>(); boolean temimv = false;
        String qSQL = "select id, rgimv, rgprp_dv, (SELECT p_nome FROM proprietarios WHERE p_rgprp = rgprp_dv LIMIT 1) AS nomebenef, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            String qrgprp_dv = null, qrgimv = null, qender = null;

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");
            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");

            while (imv.next()) {
                temimv = true;
                int qid = -1;
                String qal = null, qsg = null;
                String qiu = null, qir = null;
                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                //try { qid = imv.getInt("id"); } catch (SQLException e) { }
                try { qrgprp_dv = imv.getString("rgprp_dv"); } catch (SQLException e) { }
                try { qrgimv = imv.getString("rgimv"); } catch (SQLException e) { }
                //try { qender = imv.getString("nomebenef"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));
            }
            imv.close();

            // Mostrar dados do Imóvel
            String qtx = "";
            for (Object[] campo : tx) {
                qtx += campo[0] + ":" + (campo[1] != null ? GravarPercent(new BigDecimal("100").subtract((BigDecimal) campo[1])) : "0000000000") + ",";
            }
            if (StringManager.Right(qtx,1).equalsIgnoreCase(",")) qtx = qtx.substring(0,qtx.length() - 1);

            String qal = al != null ? "ALU:" + GravarPercent(new BigDecimal("100").subtract(al)) + "," : "";
            String qqtx = !qtx.equalsIgnoreCase("")                                ? qtx + "," : "";
            String qsg = sg != null ? "SEG:" + GravarPercent(new BigDecimal("100").subtract(sg)) + "," : "";
            String qiu = iu != null ? "IPT:" + GravarPercent(new BigDecimal("100").subtract(iu)) + "," : "";
            String qir = ir != null ? "IRF:" + GravarPercent(new BigDecimal("100").subtract(ir)) + "," : "";

            String qmu = mu != null ? "MUL:" + GravarPercent(new BigDecimal("100").subtract(mu)) + "," : "";
            String qju = ju != null ? "JUR:" + GravarPercent(new BigDecimal("100").subtract(ju)) + "," : "";
            String qco = co != null ? "COR:" + GravarPercent(new BigDecimal("100").subtract(co)) + "," : "";
            String qep = ep != null ? "EXP:" + GravarPercent(new BigDecimal("100").subtract(ep))       : "";

            String qdivisao = qal + qqtx + qsg + qiu + qir + qmu + qju + qco + qep;

            if (StringManager.Right(qdivisao,1).equalsIgnoreCase(",")) qdivisao = qdivisao.substring(0,qdivisao.length() - 1);
            if (temimv) {
                data.add(new divSec("",registro, "", qdivisao));
            }
        } catch (SQLException e) { }
        return data;
    }

    public List<divItens> populateBenefs(String registro, String rgimv) {
        List<divItens> data = new ArrayList<divItens>(); boolean temimv = false;
        String qSQL = "select id, rgimv, rgprp_dv, (SELECT p_nome FROM proprietarios WHERE p_rgprp = rgprp_dv LIMIT 1) AS nomebenef, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp = '%s' AND rgimv = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro, rgimv);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            String qrgprp_dv = null, qrgimv = null, qender = null;

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");

            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");
            while (imv.next()) {
                temimv = true;
                int qid = -1;
                String qal = null, qsg = null;
                String qiu = null, qir = null;

                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                //try { qid = imv.getInt("id"); } catch (SQLException e) { }
                try { qrgprp_dv = imv.getString("rgprp_dv"); } catch (SQLException e) { }
                try { qrgimv = imv.getString("rgimv"); } catch (SQLException e) { }
                //try { qender = imv.getString("nomebenef"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));
            }
            imv.close();

            // Mostrar dados do Imóvel
            if (temimv) data = ShowDataImovels(al,tx,sg,iu,ir, mu, ju, co, ep);
        } catch (SQLException e) { }
        return data;
    }

    private List<divItens> ShowDataImovels(BigDecimal al, Object[][] tx, BigDecimal sg, BigDecimal iu, BigDecimal ir, BigDecimal mu, BigDecimal ju, BigDecimal co, BigDecimal ep) {
        List<divItens> data = new ArrayList<divItens>();

        int id = 0;
        data.add(new divItens(id++,"ALU",VariaveisGlobais.contas_ca.get("ALU"),new BigDecimal("100").subtract(al)));

        // Taxas
        ResultSet crs = conn.AbrirTabela("SELECT codigo, descricao FROM campos ORDER BY codigo::integer;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                String qcod = null, qdescr = null;
                try {qcod = crs.getString("codigo");} catch (SQLException e) {}
                try {qdescr = crs.getString("descricao");} catch (SQLException e) {}

                int pos = -1;
                try {pos = FuncoesGlobais.FindinObject(tx,0,qcod);} catch (Exception e) {}
                if (pos > -1) {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("100").subtract((BigDecimal) tx[pos][1])));
                } else {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("100")));
                }
            }
        } catch (SQLException ex) {}
        try { DbMain.FecharTabela(crs); } catch (Exception e) {}


        data.add(new divItens(id++,"SEG",VariaveisGlobais.contas_ca.get("SEG"), new BigDecimal("100").subtract(sg)));
        data.add(new divItens(id++,"IPT",VariaveisGlobais.contas_ca.get("IPT"),new BigDecimal("100").subtract(iu)));
        data.add(new divItens(id++,"IRF",VariaveisGlobais.contas_ca.get("IRF"),new BigDecimal("100").subtract(ir)));

        data.add(new divItens(id++,"MUL",VariaveisGlobais.contas_ca.get("MUL"),new BigDecimal("100").subtract(mu)));
        data.add(new divItens(id++,"JUR",VariaveisGlobais.contas_ca.get("JUR"),new BigDecimal("100").subtract(ju)));
        data.add(new divItens(id++,"COR",VariaveisGlobais.contas_ca.get("COR"),new BigDecimal("100").subtract(co)));
        data.add(new divItens(id++,"EXP",VariaveisGlobais.contas_ca.get("EXP"),new BigDecimal("100").subtract(ep)));
        return data;
    }

    public List<divItens> FillDataImovel(String registro_dv, String rgimv) {
        List<divItens> dataImovel = null; boolean temimv = false;
        List<divProp> data = new ArrayList<divProp>();
        String qSQL = "select id, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp_dv = '%s' AND rgimv = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro_dv, rgimv);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");

            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");
            while (imv.next()) {
                temimv = true;
                int qid = -1;
                String qrgprp = null, qrgimv = null, qender = null;
                String qal = null, qsg = null;
                String qiu = null, qir = null;

                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                try { qid = imv.getInt("id"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));
            }
            imv.close();

            // Mostrar dados do Imóvel
            if (temimv) dataImovel = ShowDataImovel(al,tx,sg,iu,ir, mu, ju, co, ep);
        } catch (SQLException e) { }

        return dataImovel;
    }

    private List<divItens> ShowDataImovel(BigDecimal al, Object[][] tx, BigDecimal sg, BigDecimal iu, BigDecimal ir, BigDecimal mu, BigDecimal ju, BigDecimal co, BigDecimal ep) {
        List<divItens> data = new ArrayList<divItens>();

        int id = 0;
        data.add(new divItens(id++,"ALU",VariaveisGlobais.contas_ca.get("ALU"),al));

        // Taxas
        ResultSet crs = conn.AbrirTabela("SELECT codigo, descricao FROM campos ORDER BY codigo::integer;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                String qcod = null, qdescr = null;
                try {qcod = crs.getString("codigo");} catch (SQLException e) {}
                try {qdescr = crs.getString("descricao");} catch (SQLException e) {}

                int pos = -1;
                try {pos = FuncoesGlobais.FindinObject(tx,0,qcod);} catch (Exception e) {}
                if (pos > -1) {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),(BigDecimal) tx[pos][1]));
                } else {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("0")));
                }
            }
        } catch (SQLException ex) {}
        try { DbMain.FecharTabela(crs); } catch (Exception e) {}

        data.add(new divItens(id++,"SEG",VariaveisGlobais.contas_ca.get("SEG"), sg));
        data.add(new divItens(id++,"IPT",VariaveisGlobais.contas_ca.get("IPT"),iu));
        data.add(new divItens(id++,"IRF",VariaveisGlobais.contas_ca.get("IRF"),ir));

        data.add(new divItens(id++,"MUL",VariaveisGlobais.contas_ca.get("MUL"),mu));
        data.add(new divItens(id++,"JUR",VariaveisGlobais.contas_ca.get("JUR"),ju));
        data.add(new divItens(id++,"COR",VariaveisGlobais.contas_ca.get("COR"),co));
        data.add(new divItens(id++,"EXP",VariaveisGlobais.contas_ca.get("EXP"),ep));
        return data;
    }

    public List<divSec> PegaDivSecundaria(String registro) {
        List<divSec> data = new ArrayList<divSec>();
        String qSQL = "select id, rgprp, rgimv, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp_dv = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            while (imv.next()) {
                String qrgprp = null, qrgimv = null, qdivisao = null;
                String qal = null, qtx = null, qsg = null, qiu = null, qir = null;
                String qmu = null, qju = null, qco = null, qep = null;
                try { qrgprp = imv.getString("rgprp"); } catch (SQLException e) { }
                try { qrgimv = imv.getString("rgimv"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                try { qtx = imv.getString("tx"); } catch (SQLException e) { }
                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                try { qir = imv.getString("ir"); } catch (SQLException e) { }

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                try { qco = imv.getString("co"); } catch (SQLException e) { }
                try { qep = imv.getString("ep"); } catch (SQLException e) { }

                qdivisao = qal != null ? "ALU:" + qal + "," : "" +
                        qtx != null ?          qtx + "," : "" +
                        qsg != null ? "SEG:" + qsg + "," : "" +
                        qiu != null ? "IPT:" + qiu + "," : "" +
                        qir != null ? "IRF:" + qir + "," : "" +
                        qmu != null ? "MUL:" + qmu + "," : "" +
                        qju != null ? "JUR:" + qju + "," : "" +
                        qco != null ? "COR:" + qco + "," : "" +
                        qep != null ? "EXP:" + qep       : "";
                if (StringManager.Right(qdivisao,1).equalsIgnoreCase(",")) qdivisao = qdivisao.substring(0,qdivisao.length() - 1);
                data.add(new divSec(registro,qrgprp, qrgimv,qdivisao));
            }
            imv.close();
        } catch (SQLException e) { }

        return data;
    }

    public List<divItens> DivSecundaria(String registro) {
        List<divItens> dataImovel = null; boolean temimv = false;
        List<divProp> data = new ArrayList<divProp>();
        String qSQL = "select id, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp_dv = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");

            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");
            while (imv.next()) {
                temimv = true;
                int qid = -1;
                String qrgprp = null, qrgimv = null, qender = null;
                String qal = null, qsg = null;
                String qiu = null, qir = null;

                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                try { qid = imv.getInt("id"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));
            }
            imv.close();

            // Mostrar dados do Imóvel
            if (temimv) dataImovel = ShowDivSecundarial(al,tx,sg,iu,ir, mu, ju, co, ep);
        } catch (SQLException e) { }

        return dataImovel;
    }

    private List<divItens> ShowDivSecundarial(BigDecimal al, Object[][] tx, BigDecimal sg, BigDecimal iu, BigDecimal ir, BigDecimal mu, BigDecimal ju, BigDecimal co, BigDecimal ep) {
        List<divItens> data = new ArrayList<divItens>();

        int id = 0;
        data.add(new divItens(id++,"ALU",VariaveisGlobais.contas_ca.get("ALU"),al));

        // Taxas
        ResultSet crs = conn.AbrirTabela("SELECT codigo, descricao FROM campos ORDER BY codigo::integer;", ResultSet.CONCUR_READ_ONLY);
        try {
            while (crs.next()) {
                String qcod = null, qdescr = null;
                try {qcod = crs.getString("codigo");} catch (SQLException e) {}
                try {qdescr = crs.getString("descricao");} catch (SQLException e) {}

                int pos = -1;
                try {pos = FuncoesGlobais.FindinObject(tx,0,qcod);} catch (Exception e) {}
                if (pos > -1) {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),(BigDecimal) tx[pos][1]));
                } else {
                    data.add(new divItens(id++,qcod,qdescr.trim().toUpperCase(),new BigDecimal("0")));
                }
            }
        } catch (SQLException ex) {}
        try { DbMain.FecharTabela(crs); } catch (Exception e) {}

        data.add(new divItens(id++,"SEG",VariaveisGlobais.contas_ca.get("SEG"),sg));
        data.add(new divItens(id++,"IPT",VariaveisGlobais.contas_ca.get("IPT"),iu));
        data.add(new divItens(id++,"IRF",VariaveisGlobais.contas_ca.get("IRF"),ir));

        data.add(new divItens(id++,"MUL",VariaveisGlobais.contas_ca.get("MUL"),mu));
        data.add(new divItens(id++,"JUR",VariaveisGlobais.contas_ca.get("JUR"),ju));
        data.add(new divItens(id++,"COR",VariaveisGlobais.contas_ca.get("COR"),co));
        data.add(new divItens(id++,"EXP",VariaveisGlobais.contas_ca.get("EXP"),ep));
        return data;
    }

    public BigDecimal LerPercent(String value, boolean temCodigo) {
        if (value == null) return new BigDecimal("0");

        String part1 = "", part2 = "", part3 = "";
        part1 = temCodigo ? value.substring(0,3) : "";
        part2 = temCodigo ? value.substring(4,9) : value.substring(0,5);
        part3 = temCodigo ? value.substring(9,14) : value.substring(5,10);

        return new BigDecimal(part2 + "." + part3);
    }

    public Object[] LerPercent(String value) {
        if (value == null) return null;

        String part1 = "", part2 = "", part3 = "";
        part1 = value.substring(0,3);
        part2 = value.substring(4,9);
        part3 = value.substring(9,14);

        return new Object[] {part1, new BigDecimal(part2 + "." + part3)};
    }

    private String GravarPercent(BigDecimal value) {
        DecimalFormat fmt = new DecimalFormat("00000.00000");
        return fmt.format(value).replace(",","");
    }

    public Object[] ChecaDivisao(String rgprp, String rgimv) {
        String rgprp_principal = null;
        List<divItens> data = new PegaDivisao().populateBenefs(rgprp, rgimv);
        List<divItens> data2 = new PegaDivisao().FillDataImovel(rgprp, rgimv);

        if (data == null && data2 == null) {
            System.out.println("Sem divisão neste imóvel");
            rgprp_principal = rgprp;
        } else {

            if (data != null) {
                System.out.println("Comissões do Proprietário: " + rgprp + " Imóvel: " + rgimv);
                for (divItens dados : data) {
                    System.out.println(dados.getCod() + " " + dados.getDescr() + " " + dados.getPerc());
                }
                System.out.println("=========================================================");
            }

            if (data2 != null) {
                System.out.println("Comissões do Beneficiario: " + rgprp + " Imóvel: " + rgimv);
                for (divItens dados : data2) {
                    System.out.println(dados.getCod() + " " + dados.getDescr() + " " + dados.getPerc());
                }
                System.out.println("=========================================================");
            }
        }

        if (data != null) rgprp_principal = rgprp;
        if (data2 != null) {
            try {
                rgprp_principal = String.valueOf(conn.LerCamposTabela(new String[]{"rgprp"}, "dividir", String.format("rgprp_dv = '%s' AND rgimv = '%s'", rgprp, rgimv))[0][3]);
            } catch (Exception e) {
                rgprp_principal = null;
            }
        }

        System.out.println("Proprietario principal: " + rgprp_principal);

        return new Object[] {rgprp_principal, data, data2};
    }

    public List<divSec> PegaDivisoes(String registro) {
        List<divSec> data = new ArrayList<divSec>(); boolean temimv = false;
        String qSQL = "select id, rgimv, rgprp_dv, (SELECT p_nome FROM proprietarios WHERE p_rgprp = rgprp_dv LIMIT 1) AS nomebenef, al, tx, sg, iu, ir, mu, ju, co, ep from dividir WHERE rgprp = '%s' ORDER BY rgimv::integer, rgprp_dv::integer;";
        qSQL = String.format(qSQL, registro);
        ResultSet imv;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);

            String qrgprp_dv = null, qrgimv = null, qender = null;

            BigDecimal al = new BigDecimal("0"); Object[][] tx = {};
            BigDecimal sg = new BigDecimal("0"), iu = new BigDecimal("0");
            BigDecimal ir = new BigDecimal("0");

            BigDecimal mu = new BigDecimal("0"), ju = new BigDecimal("0");
            BigDecimal co = new BigDecimal("0"), ep = new BigDecimal("0");
            Boolean isFirst = true; String oldImv = null;
            while (imv.next()) {
                if (isFirst) {
                    al = new BigDecimal("0"); tx = new Object[][] {};
                    sg = new BigDecimal("0"); iu = new BigDecimal("0");
                    ir = new BigDecimal("0");

                    mu = new BigDecimal("0"); ju = new BigDecimal("0");
                    co = new BigDecimal("0"); ep = new BigDecimal("0");
                    oldImv = imv.getString("rgimv");
                    isFirst = false;
                }

                if (!imv.getString("rgimv").equalsIgnoreCase(oldImv)) {
                    // Mostrar dados do Imóvel
                    String qtx = "";
                    for (Object[] campo : tx) {
                        qtx += campo[0] + ":" + (campo[1] != null ? GravarPercent(new BigDecimal("100").subtract((BigDecimal) campo[1])) : "0000000000") + ",";
                    }
                    if (StringManager.Right(qtx,1).equalsIgnoreCase(",")) qtx = qtx.substring(0,qtx.length() - 1);

                    String qal = al != null ? "ALU:" + GravarPercent(new BigDecimal("100").subtract(al)) + "," : "";
                    String qqtx = !qtx.equalsIgnoreCase("")                                ? qtx + "," : "";
                    String qsg = sg != null ? "SEG:" + GravarPercent(new BigDecimal("100").subtract(sg)) + "," : "";
                    String qiu = iu != null ? "IPT:" + GravarPercent(new BigDecimal("100").subtract(iu)) + "," : "";
                    String qir = ir != null ? "IRF:" + GravarPercent(new BigDecimal("100").subtract(ir)) + "," : "";

                    String qmu = mu != null ? "MUL:" + GravarPercent(new BigDecimal("100").subtract(mu)) + "," : "";
                    String qju = ju != null ? "JUR:" + GravarPercent(new BigDecimal("100").subtract(ju)) + "," : "";
                    String qco = co != null ? "COR:" + GravarPercent(new BigDecimal("100").subtract(co)) + "," : "";
                    String qep = ep != null ? "EXP:" + GravarPercent(new BigDecimal("100").subtract(ep))       : "";

                    String qdivisao = qal + qqtx + qsg + qiu + qir + qmu + qju + qco + qep;

                    if (StringManager.Right(qdivisao,1).equalsIgnoreCase(",")) qdivisao = qdivisao.substring(0,qdivisao.length() - 1);
                    data.add(new divSec("",registro, oldImv, qdivisao));

                    al = new BigDecimal("0"); tx = new Object[][] {};
                    sg = new BigDecimal("0"); iu = new BigDecimal("0");
                    ir = new BigDecimal("0");

                    mu = new BigDecimal("0"); ju = new BigDecimal("0");
                    co = new BigDecimal("0"); ep = new BigDecimal("0");
                    oldImv = imv.getString("rgimv");
                }
                int qid = -1;
                String qal = null, qsg = null;
                String qiu = null, qir = null;

                String qmu = null, qju = null;
                String qco = null, qep = null;
                String[] qtx = null;

                //try { qid = imv.getInt("id"); } catch (SQLException e) { }
                try { qrgprp_dv = imv.getString("rgprp_dv"); } catch (SQLException e) { }
                try { qrgimv = imv.getString("rgimv"); } catch (SQLException e) { }
                //try { qender = imv.getString("nomebenef"); } catch (SQLException e) { }

                try { qal = imv.getString("al"); } catch (SQLException e) { }
                if (qal != null) al = al.add(LerPercent(qal,false));

                Array btx = null;
                try { btx = imv.getArray("tx"); } catch (SQLException e) {}
                if (btx != null) {
                    qtx = (String[]) btx.getArray();
                }
                if (qtx != null) {
                    for (String atx : qtx) {
                        Object[] ratx = LerPercent(atx);
                        String rcod = (String) ratx[0];
                        BigDecimal rtx = (BigDecimal) ratx[1];
                        int pos = FuncoesGlobais.FindinObject(tx,0,rcod);
                        if (pos == -1) {
                            tx = FuncoesGlobais.ObjectsAdd(tx, new Object[] {rcod, rtx});
                        } else {
                            tx[pos][1] = ((BigDecimal)tx[pos][1]).add(rtx);
                        }
                    }
                }

                try { qsg = imv.getString("sg"); } catch (SQLException e) { }
                if (qsg != null) sg = sg.add(LerPercent(qsg,false));

                try { qiu = imv.getString("iu"); } catch (SQLException e) { }
                if (qiu != null) iu = iu.add(LerPercent(qiu,false));

                try { qir = imv.getString("ir"); } catch (SQLException e) { }
                if (qir != null) ir = ir.add(LerPercent(qir,false));

                try { qmu = imv.getString("mu"); } catch (SQLException e) { }
                if (qmu != null) mu = mu.add(LerPercent(qmu,false));

                try { qju = imv.getString("ju"); } catch (SQLException e) { }
                if (qju != null) ju = ju.add(LerPercent(qju,false));

                try { qco = imv.getString("co"); } catch (SQLException e) { }
                if (qco != null) co = co.add(LerPercent(qco,false));

                try { qep = imv.getString("ep"); } catch (SQLException e) { }
                if (qep != null) ep = ep.add(LerPercent(qep,false));
            }
            imv.close();

            // Mostrar dados do Imóvel
            String qtx = "";
            for (Object[] campo : tx) {
                qtx += campo[0] + ":" + (campo[1] != null ? GravarPercent(new BigDecimal("100").subtract((BigDecimal) campo[1])) : "0000000000") + ",";
            }
            if (StringManager.Right(qtx,1).equalsIgnoreCase(",")) qtx = qtx.substring(0,qtx.length() - 1);

            String qal = al != null ? "ALU:" + GravarPercent(new BigDecimal("100").subtract(al)) + "," : "";
            String qqtx = !qtx.equalsIgnoreCase("")                                ? qtx + "," : "";
            String qsg = sg != null ? "SEG:" + GravarPercent(new BigDecimal("100").subtract(sg)) + "," : "";
            String qiu = iu != null ? "IPT:" + GravarPercent(new BigDecimal("100").subtract(iu)) + "," : "";
            String qir = ir != null ? "IRF:" + GravarPercent(new BigDecimal("100").subtract(ir)) + "," : "";

            String qmu = mu != null ? "MUL:" + GravarPercent(new BigDecimal("100").subtract(mu)) + "," : "";
            String qju = ju != null ? "JUR:" + GravarPercent(new BigDecimal("100").subtract(ju)) + "," : "";
            String qco = co != null ? "COR:" + GravarPercent(new BigDecimal("100").subtract(co)) + "," : "";
            String qep = ep != null ? "EXP:" + GravarPercent(new BigDecimal("100").subtract(ep))       : "";

            String qdivisao = qal + qqtx + qsg + qiu + qir + qmu + qju + qco + qep;

            if (StringManager.Right(qdivisao,1).equalsIgnoreCase(",")) qdivisao = qdivisao.substring(0,qdivisao.length() - 1);
            data.add(new divSec("",registro, oldImv, qdivisao));

        } catch (SQLException e) { }
        return data;
    }

}
