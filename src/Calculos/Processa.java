package Calculos;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by supervisor on 06/02/17.
 */
public class Processa {
    DbMain conn = VariaveisGlobais.conexao;
    private String rgprp = null, rgimv = null, contrato = null, tpimovel = null;
    private Date vencimento = null, dtBaseCalc = null;
    private BigDecimal aluguel = new BigDecimal(0);
    private BigDecimal mu = new BigDecimal(0);
    private BigDecimal ju = new BigDecimal(0);
    private BigDecimal co = new BigDecimal(0);
    private BigDecimal ep = new BigDecimal(0);
    private BigDecimal ir = new BigDecimal(0);
    private BigDecimal descontos = new BigDecimal(0);
    private BigDecimal diferenca = new BigDecimal(0);
    private BigDecimal irenda = new BigDecimal(0);
    private BigDecimal iptu = new BigDecimal(0);
    private BigDecimal txcredito = new BigDecimal(0);
    private BigDecimal txdebito = new BigDecimal(0);
    private BigDecimal seguro = new BigDecimal(0);

    public boolean isIsmu() { return ismu; }
    public boolean isIsju() { return isju; }
    public boolean isIsco() { return isco; }
    public boolean isIsep() { return isep; }

    private boolean ismu = false;
    private boolean isju = false;
    private boolean isco = false;
    private boolean isep = false;

    public Processa(String rgprp, String rgimv, String contrato, Date vencimento, Date dtBaseCalc) {
        this.rgprp = rgprp;
        this.rgimv = rgimv;
        this.contrato = contrato;
        this.vencimento = vencimento;
        this.dtBaseCalc = dtBaseCalc;

        try {
            this.tpimovel = conn.LerCamposTabela(new String[] {"i_tipo"},"imoveis","i_rgimv = '" + rgimv + "'")[0][3].toString().toUpperCase();
        } catch (SQLException e) {
            System.out.println(rgimv);
            System.out.println(this.tpimovel);
            this.tpimovel = "RESIDENCIAL";
        }
        new Multas(this.rgprp, this.rgimv);

        // Aluguel
        String sql = "SELECT mensal, mu, lmu, ju, lju, co, lco, ep, lep, ir, referencia FROM movimento WHERE contrato = ? AND dtvencimento = ? and selected = true;";
        String referencia = null;
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {
                {"string", this.contrato},
                {"date", Dates.toSqlDate(this.vencimento)}
        });
        try {
            while (rs.next()) {
                try {this.aluguel = rs.getBigDecimal("mensal");} catch (NullPointerException e) {this.aluguel = new BigDecimal(0);}
                if (this.aluguel == null) this.aluguel = new BigDecimal(0);

                try {this.mu = rs.getBigDecimal("mu");} catch (NullPointerException e) {this.mu = new BigDecimal(0);}
                if (this.mu == null) this.mu = new BigDecimal(0);
                try {this.ismu = rs.getBoolean("lmu");} catch (Exception e) {this.ismu = false;}

                try {this.ju = rs.getBigDecimal("ju");} catch (NullPointerException e) {this.ju = new BigDecimal(0);}
                if (this.ju == null) this.ju = new BigDecimal(0);
                try {this.isju = rs.getBoolean("lju");} catch (Exception e) {this.isju = false;}

                try {this.co = rs.getBigDecimal("co");} catch (NullPointerException e) {this.co = new BigDecimal(0);}
                if (this.co == null) this.co = new BigDecimal(0);
                try {this.isco = rs.getBoolean("lco");} catch (Exception e) {this.isco = false;}

                try {this.ep = rs.getBigDecimal("ep");} catch (NullPointerException e) {this.ep = new BigDecimal(0);}
                if (this.ep == null) this.ep = new BigDecimal(0);
                try {this.isep = rs.getBoolean("lep");} catch (Exception e) {this.isep = false;}

                try {this.ir = rs.getBigDecimal("ir");} catch (NullPointerException e) {this.ir = new BigDecimal(0);}
                if (this.ir == null) this.ir = new BigDecimal(0);

                referencia = rs.getString("referencia");
            }
        } catch (SQLException e) {}
        try {
            DbMain.FecharTabela(rs);} catch (Exception e) {}
        //System.out.println("Aluguel " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.aluguel));

        if (referencia == null)  referencia = PegaReferencia(vencimento);

        // Descontos, Diferenças
        sql = "SELECT tipo, valor FROM descdif WHERE contrato = '%s' AND referencia = '%s' and selected = true;";
        sql = String.format(sql,this.contrato, referencia);
        rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                BigDecimal valor = new BigDecimal(0);
                try {valor = rs.getBigDecimal("valor");} catch (SQLException e) {}
                if (rs.getString("tipo").equalsIgnoreCase("D")) this.descontos = this.descontos.add(valor); else this.diferenca = this.diferenca.add(valor);
            }
        } catch (SQLException e) {}
        try {
            DbMain.FecharTabela(rs);} catch (Exception e) {}
        //System.out.println("Descontos " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.descontos));
        //System.out.println("Diferenças " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.diferenca));

        // IRRF
        try {
            if (this.ir.compareTo(BigDecimal.ZERO) == 0) {
                this.irenda = new Calculos.Irrf().Irrf(this.rgprp, this.contrato, referencia, this.aluguel, this.diferenca, this.descontos).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else this.irenda = this.ir;
            //System.out.println("IRRF " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.irenda));
        } catch (Exception e) {this.irenda = this.ir;}

        // IPTU
        String[] ciptu = new Calculos.Iptu().Iptu(this.rgimv,referencia);
        try { if (ciptu != null) this.iptu = new BigDecimal(ciptu[3]); } catch (Exception e) {}
        //System.out.println("IPTU " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.iptu));

        // Taxas
        sql = "SELECT tipo, valor FROM taxas WHERE contrato = '%s' AND referencia = '%s' and selected = true;";
        sql = String.format(sql,this.contrato, referencia);
        rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                BigDecimal valor = new BigDecimal(0);
                try {valor = rs.getBigDecimal("valor");} catch (SQLException e) {}
                if (rs.getString("tipo").equalsIgnoreCase("D")) this.txdebito = this.txdebito.add(valor); else this.txcredito = this.txcredito.add(valor);
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(rs);} catch (Exception e) {}
        //System.out.println("txCredito " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.txcredito));
        //System.out.println("txDébito " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.txdebito));

        // Seguro
        sql = "SELECT valor FROM seguros WHERE contrato = '%s' AND referencia = '%s' and selected = true;";
        sql = String.format(sql,this.contrato, referencia);
        rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                BigDecimal valor = new BigDecimal(0);
                try {valor = rs.getBigDecimal("valor");} catch (SQLException e) {}
                this.seguro = this.seguro.add(valor);
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(rs);} catch (Exception e) {}
        //System.out.println("Seguro " + new DecimalFormat("'R$ ' #,###,##0.00").format(this.seguro));

    }

    public BigDecimal Multa() {
        if (this.mu.compareTo(BigDecimal.ZERO) != 0 && this.ismu) return this.mu;
        if (this.mu.compareTo(BigDecimal.ZERO) == 0 && this.ismu) return new BigDecimal(0);

        if (this.aluguel.compareTo(BigDecimal.ZERO) == 0) return new BigDecimal(0);

        Date newvencimento = Dates.DateAdd(Dates.DIA, VariaveisGlobais.ca_multa, this.vencimento);

        LocalDate vecto  = Dates.FimDeSemana(newvencimento);
        LocalDate dtbase = Dates.FimDeSemana(this.dtBaseCalc);

        float dias = dias = Dates.DtDiff("d", vecto.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dtbase.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if (dias <= 0) return new BigDecimal("0");

        newvencimento = Dates.DateAdd(Dates.DIA, 0, this.vencimento);
        vecto  = Dates.FimDeSemana(newvencimento);
        dias = Dates.DtDiff("d", vecto.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dtbase.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        float totMulta = (int)(dias / 30) + ((dias % 30) > 0 ? 1 : 0);
        float multa_imovel = (this.tpimovel.equalsIgnoreCase("RESIDENCIAL") ? (float) VariaveisGlobais.mu_res : (float) VariaveisGlobais.mu_com) / 100;

        BigDecimal multa_total = new BigDecimal(0);
        if (VariaveisGlobais.mu_al) {
            multa_total = multa_total.add(this.aluguel);
            multa_total = multa_total.add(this.diferenca);
            multa_total = multa_total.subtract(this.descontos);
        }
        if (VariaveisGlobais.mu_tx) {
            multa_total = multa_total.add(this.txcredito);
            multa_total = multa_total.subtract(this.txdebito);
        }

        if (VariaveisGlobais.mu_te) multa_total = multa_total.add(Expediente());
        multa_total = multa_total.add(this.seguro);
        return multa_total.multiply(new BigDecimal(String.valueOf(multa_imovel))).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal Juros() {
        if (this.ju.compareTo(BigDecimal.ZERO) != 0) return this.ju;
        if (this.ju.compareTo(BigDecimal.ZERO) == 0 && this.isju) return new BigDecimal(0);

        Date newvencimento = Dates.DateAdd(Dates.DIA, VariaveisGlobais.ca_multa, this.vencimento);

        LocalDate vecto  = Dates.FimDeSemana(newvencimento);
        LocalDate dtbase = Dates.FimDeSemana(this.dtBaseCalc);

        float dias = Dates.DtDiff("d", vecto.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dtbase.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if (dias <= 0) return new BigDecimal("0");

        newvencimento = Dates.DateAdd(Dates.DIA, 0, this.vencimento);
        vecto  = Dates.FimDeSemana(newvencimento);
        dias = Dates.DtDiff("d", vecto.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dtbase.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        float tot_juros = 0;
        if (VariaveisGlobais.ju_tipo == null) VariaveisGlobais.ju_tipo = "SIMPLES";

        if (VariaveisGlobais.ju_tipo.equalsIgnoreCase("SIMPLES")) {
            tot_juros = 1;
        } else {
            if (dias >= VariaveisGlobais.ca_juros) {
                tot_juros = (int)(dias / 30) + ((dias % 30) > 0 ? 1 : 0);
            } else tot_juros = 0;
        }
        BigDecimal juros_total = new BigDecimal(0);
        if (VariaveisGlobais.ju_al) juros_total = juros_total.add(this.aluguel.add(this.diferenca).subtract(this.descontos));
        if (VariaveisGlobais.ju_tx) juros_total = juros_total.add(this.txcredito).subtract(this.txdebito);
        if (VariaveisGlobais.ju_ep) juros_total = juros_total.add(Expediente());
        if (VariaveisGlobais.ju_sg) juros_total = juros_total.add(this.seguro);
        if (VariaveisGlobais.ju_mu) juros_total = juros_total.add(Multa());
        if (VariaveisGlobais.ju_co) juros_total = juros_total.add(Correcao());

        BigDecimal retorno = juros_total.multiply(new BigDecimal(String.valueOf((VariaveisGlobais.ju_percent / 100) * tot_juros))).setScale(2, BigDecimal.ROUND_HALF_UP);
        return retorno.compareTo(BigDecimal.ZERO) == -1 ? retorno.multiply(new BigDecimal(-1)) : retorno;
    }

    public BigDecimal Correcao() {
        if (this.co.compareTo(BigDecimal.ZERO) != 0) return this.co.setScale(2, BigDecimal.ROUND_HALF_UP);
        if (this.co.compareTo(BigDecimal.ZERO) == 0 && this.isco) return new BigDecimal(0);

        Date newvencimento = Dates.DateAdd(Dates.DIA, VariaveisGlobais.ca_multa, this.vencimento);

        LocalDate vecto  = Dates.FimDeSemana(newvencimento);
        LocalDate dtbase = Dates.FimDeSemana(this.dtBaseCalc);

        float dias = Dates.DtDiff("d", vecto.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dtbase.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if (dias <= 0) return new BigDecimal("0");

        newvencimento = Dates.DateAdd(Dates.DIA, 0, this.vencimento);
        vecto  = Dates.FimDeSemana(newvencimento);
        dias = Dates.DtDiff("d", vecto.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dtbase.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        BigDecimal cor_total = new BigDecimal(0);
        if (VariaveisGlobais.co_al) cor_total = cor_total.add(this.aluguel.add(this.diferenca).subtract(this.descontos));
        if (VariaveisGlobais.co_tx) cor_total = cor_total.add(this.txcredito).subtract(this.txdebito);
        if (VariaveisGlobais.co_ep) cor_total = cor_total.add(Expediente());
        if (VariaveisGlobais.co_sg) cor_total = cor_total.add(this.seguro);
        if (VariaveisGlobais.co_mu) cor_total = cor_total.add(Multa());
        if (VariaveisGlobais.co_ju) cor_total = cor_total.add(Juros());

        if (VariaveisGlobais.co_dia > 0) if (dias >= VariaveisGlobais.co_dia)  dias = Float.parseFloat(String.valueOf(VariaveisGlobais.co_dia));
        BigDecimal cor_final = new BigDecimal(0);
        if (VariaveisGlobais.co_tipo == null) VariaveisGlobais.co_tipo = "SIMPLES";
        if (VariaveisGlobais.co_tipo.equalsIgnoreCase("SIMPLES"))  {
            BigDecimal retorno = cor_total.multiply(new BigDecimal(String.valueOf((VariaveisGlobais.co_perc / 100) * dias)));
            return retorno.compareTo(BigDecimal.ZERO) == -1 ? retorno.multiply(new BigDecimal(-1)) : retorno;
        } else {
            cor_final = cor_total;
            for (int i=1; i<=dias; i++) cor_final = cor_final.multiply(new BigDecimal(String.valueOf(VariaveisGlobais.co_perc / 100)));
        }

        BigDecimal retorno = cor_final.subtract(cor_total);
        if (retorno.compareTo(BigDecimal.ZERO) == -1) retorno = retorno.multiply(new BigDecimal(-1));
        return retorno.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal Expediente() {
        if (this.ep.compareTo(BigDecimal.ZERO) != 0) return this.ep;
        if (this.ep.compareTo(BigDecimal.ZERO) == 0 && this.isep) return new BigDecimal(0);

        if (this.aluguel.compareTo(BigDecimal.ZERO) == 0) return new BigDecimal(0);

        BigDecimal ep_total = new BigDecimal(0);
        if (VariaveisGlobais.ep_al) ep_total = ep_total.add(this.aluguel);
        if (VariaveisGlobais.ep_tx) ep_total = ep_total.add(this.txcredito).subtract(this.txdebito);
        if (VariaveisGlobais.ep_sg) ep_total = ep_total.add(this.seguro);
        if (!VariaveisGlobais.ep_bl) ep_total = ep_total.add(this.diferenca).subtract(this.descontos);

        BigDecimal ep_valor = new BigDecimal(0);
        ep_valor = ep_total.multiply(new BigDecimal(String.valueOf(VariaveisGlobais.ep_percent / 100)));
        if (ep_valor.compareTo(new BigDecimal(String.valueOf(VariaveisGlobais.ep_vrlor))) == -1 ||
                ep_valor.compareTo(new BigDecimal(String.valueOf(VariaveisGlobais.ep_vrlor))) == 0)
            ep_valor = new BigDecimal(String.valueOf(VariaveisGlobais.ep_vrlor));

        return ep_valor.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    public BigDecimal Multa(BigDecimal multa) {
        if (this.mu.compareTo(BigDecimal.ZERO) != 0) return this.mu;
        this.mu = multa;
        return this.mu;
    }

    public BigDecimal Juros(BigDecimal juros) {
        if (this.ju.compareTo(BigDecimal.ZERO) != 0) return this.ju;
        this.ju = juros;
        return this.ju;
    }

    public BigDecimal Correcao(BigDecimal correcao) {
        if (this.co.compareTo(BigDecimal.ZERO) != 0) return this.co.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.co = correcao;
        return this.co;
    }

    public BigDecimal Expediente(BigDecimal expediente) {
        if (this.ep.compareTo(BigDecimal.ZERO) != 0) return this.ep;
        this.ep = expediente;
        return this.ep;
    }

    public BigDecimal TotalRecibo() {
        BigDecimal trec = new BigDecimal(0);
        trec = trec.add(this.aluguel);
        trec = trec.subtract(this.descontos);
        trec = trec.add(this.diferenca);
        trec = trec.subtract(this.irenda);
        trec = trec.add(this.iptu);
        trec = trec.add(this.txcredito);
        trec = trec.subtract(this.txdebito);
        trec = trec.add(this.seguro);
        trec = trec.compareTo(BigDecimal.ZERO) == -1 ? trec.multiply(new BigDecimal(-1)) : trec;
        trec = trec.add(Expediente());
        trec = trec.add(Multa());
        trec = trec.add(Juros());
        trec = trec.add(Correcao());
        return trec.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal TotalRecibo(boolean semMuJuCoEp) {
        BigDecimal trec = new BigDecimal(0);
        trec = trec.add(this.aluguel);
        trec = trec.subtract(this.descontos);
        trec = trec.add(this.diferenca);
        trec = trec.add(this.irenda);
        trec = trec.add(this.iptu);
        trec = trec.add(this.txcredito);
        trec = trec.subtract(this.txdebito);
        trec = trec.add(this.seguro);
        trec = trec.compareTo(BigDecimal.ZERO) == -1 ? trec.multiply(new BigDecimal(-1)) : trec;
        if (!semMuJuCoEp) {
            trec = trec.add(Expediente());
            trec = trec.add(Multa());
            trec = trec.add(Juros());
            trec = trec.add(Correcao());
        }
        return trec.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String PegaReferencia(Date vencimento) {
        return Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,-1,vencimento));
    }

    public BigDecimal getAluguel() {
        return this.aluguel;
    }

    public BigDecimal getDescontos() {
        return this.descontos;
    }

    public BigDecimal getDiferenca() {
        return this.diferenca;
    }

    public BigDecimal getIrenda() {
        return this.irenda;
    }

    public BigDecimal getIptu() {
        return this.iptu;
    }

    public BigDecimal getSeguro() {
        return this.seguro;
    }

    public BigDecimal getTxcredito() {
        return this.txcredito;
    }

    public BigDecimal getTxdebito() {
        return this.txdebito;
    }
}
