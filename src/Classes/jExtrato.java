package Classes;

import java.math.BigDecimal;

public class jExtrato {
    private String hist_linha;
    private BigDecimal hist_cred;
    private BigDecimal hist_deb;

    public jExtrato() {}

    public jExtrato(String hist_linha, BigDecimal hist_cred, BigDecimal hist_deb) {
        this.hist_linha = hist_linha;
        this.hist_cred = hist_cred;
        this.hist_deb = hist_deb;
    }

    public String getHist_linha() {
        return hist_linha;
    }

    public void setHist_linha(String hist_linha) {
        this.hist_linha = hist_linha;
    }

    public BigDecimal getHist_cred() {
        return hist_cred;
    }

    public void setHist_cred(BigDecimal hist_cred) {
        this.hist_cred = hist_cred;
    }

    public BigDecimal getHist_deb() {
        return hist_deb;
    }

    public void setHist_deb(BigDecimal hist_deb) {
        this.hist_deb = hist_deb;
    }
}
