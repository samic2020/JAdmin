package Relatorios;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class GetFieldsClass {
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nomerazao;
    private SimpleObjectProperty<Date> vencimento;
    private SimpleObjectProperty<BigDecimal> aluguel;
    private SimpleObjectProperty<BigDecimal> desconto;
    private SimpleObjectProperty<BigDecimal> diferenca;
    private SimpleObjectProperty<BigDecimal> percomissao;
    private SimpleObjectProperty<BigDecimal> comissão;
    private SimpleObjectProperty<BigDecimal> ir;
    private SimpleObjectProperty<BigDecimal> ip;
    private SimpleObjectProperty<BigDecimal> ep;
    private SimpleObjectProperty<BigDecimal> mu;
    private SimpleObjectProperty<BigDecimal> ju;
    private SimpleObjectProperty<BigDecimal> co;
    private SimpleObjectProperty<BigDecimal> sg;
    private SimpleObjectProperty<BigDecimal> tx;
    private SimpleObjectProperty<BigInteger> aut;

    public GetFieldsClass(String rgprp, String rgimv, String contrato, String nomerazao,Date vencimento,
                          BigDecimal aluguel, BigDecimal desconto, BigDecimal diferenca, BigDecimal percomissao,
                          BigDecimal comissão, BigDecimal ir, BigDecimal ip, BigDecimal ep, BigDecimal mu,
                          BigDecimal ju, BigDecimal co, BigDecimal sg, BigDecimal tx, BigInteger aut) {
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.nomerazao = new SimpleStringProperty(nomerazao);
        this.vencimento = new SimpleObjectProperty<>(vencimento);
        this.aluguel = new SimpleObjectProperty<>(aluguel);
        this.desconto = new SimpleObjectProperty<>(desconto);
        this.diferenca = new SimpleObjectProperty<>(diferenca);
        this.percomissao = new SimpleObjectProperty<>(percomissao);
        this.comissão = new SimpleObjectProperty<>(comissão);
        this.ir = new SimpleObjectProperty<>(ir);
        this.ip = new SimpleObjectProperty<>(ip);
        this.ep = new SimpleObjectProperty<>(ep);
        this.mu = new SimpleObjectProperty<>(mu);
        this.ju = new SimpleObjectProperty<>(ju);
        this.co = new SimpleObjectProperty<>(co);
        this.sg = new SimpleObjectProperty<>(sg);
        this.tx = new SimpleObjectProperty<>(tx);
        this.aut = new SimpleObjectProperty<>(aut);
    }

    public String getRgprp() { return rgprp.get(); }
    public SimpleStringProperty rgprpProperty() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp.set(rgprp); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getNomerazao() { return nomerazao.get(); }
    public SimpleStringProperty nomerazaoProperty() { return nomerazao; }
    public void setNomerazao(String nomerazao) { this.nomerazao.set(nomerazao); }

    public Date getVencimento() { return vencimento.get(); }
    public SimpleObjectProperty<Date> vencimentoProperty() { return vencimento; }
    public void setVencimento(Date vencimento) { this.vencimento.set(vencimento); }

    public BigDecimal getAluguel() { return aluguel.get(); }
    public SimpleObjectProperty<BigDecimal> aluguelProperty() { return aluguel; }
    public void setAluguel(BigDecimal aluguel) { this.aluguel.set(aluguel); }

    public BigDecimal getDesconto() { return desconto.get(); }
    public SimpleObjectProperty<BigDecimal> descontoProperty() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto.set(desconto); }

    public BigDecimal getDiferenca() { return diferenca.get(); }
    public SimpleObjectProperty<BigDecimal> diferencaProperty() { return diferenca; }
    public void setDiferenca(BigDecimal diferenca) { this.diferenca.set(diferenca); }

    public BigDecimal getPercomissao() { return percomissao.get(); }
    public SimpleObjectProperty<BigDecimal> percomissaoProperty() { return percomissao; }
    public void setPercomissao(BigDecimal percomissao) { this.percomissao.set(percomissao); }

    public BigDecimal getComissão() { return comissão.get(); }
    public SimpleObjectProperty<BigDecimal> comissãoProperty() { return comissão; }
    public void setComissão(BigDecimal comissão) { this.comissão.set(comissão); }

    public BigDecimal getIr() { return ir.get(); }
    public SimpleObjectProperty<BigDecimal> irProperty() { return ir; }
    public void setIr(BigDecimal ir) { this.ir.set(ir); }

    public BigDecimal getIp() { return ip.get(); }
    public SimpleObjectProperty<BigDecimal> ipProperty() { return ip; }
    public void setIp(BigDecimal ip) { this.ip.set(ip); }

    public BigDecimal getEp() { return ep.get(); }
    public SimpleObjectProperty<BigDecimal> epProperty() { return ep; }
    public void setEp(BigDecimal ep) { this.ep.set(ep); }

    public BigDecimal getMu() { return mu.get(); }
    public SimpleObjectProperty<BigDecimal> muProperty() { return mu; }
    public void setMu(BigDecimal mu) { this.mu.set(mu); }

    public BigDecimal getJu() { return ju.get(); }
    public SimpleObjectProperty<BigDecimal> juProperty() { return ju; }
    public void setJu(BigDecimal ju) { this.ju.set(ju); }

    public BigDecimal getCo() { return co.get(); }
    public SimpleObjectProperty<BigDecimal> coProperty() { return co; }
    public void setCo(BigDecimal co) { this.co.set(co); }

    public BigDecimal getSg() { return sg.get(); }
    public SimpleObjectProperty<BigDecimal> sgProperty() { return sg; }
    public void setSg(BigDecimal sg) { this.sg.set(sg); }

    public BigDecimal getTx() { return tx.get(); }
    public SimpleObjectProperty<BigDecimal> txProperty() { return tx; }
    public void setTx(BigDecimal tx) { this.tx.set(tx); }

    public BigInteger getAut() { return aut.get(); }
    public SimpleObjectProperty<BigInteger> autProperty() { return aut; }
    public void setAut(BigInteger aut) { this.aut.set(aut); }

    @Override
    public String toString() {
        return "GetFieldsClass{" +
                "rgprp=" + rgprp +
                ", rgimv=" + rgimv +
                ", contrato=" + contrato +
                ", nomerazao=" + nomerazao +
                ", vencimento=" + vencimento +
                ", aluguel=" + aluguel +
                ", desconto=" + desconto +
                ", diferenca=" + diferenca +
                ", percomissao=" + percomissao +
                ", comissão=" + comissão +
                ", ir=" + ir +
                ", ip=" + ip +
                ", ep=" + ep +
                ", mu=" + mu +
                ", ju=" + ju +
                ", co=" + co +
                ", sg=" + sg +
                ", tx=" + tx +
                '}';
    }
}
