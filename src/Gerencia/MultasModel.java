package Gerencia;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 26/01/17.
 */
public class MultasModel {
    SimpleIntegerProperty id;
    SimpleStringProperty multas_tipo;
    SimpleStringProperty pa_mu;
    SimpleStringProperty pa_ju;
    SimpleStringProperty pa_co;
    SimpleStringProperty pa_ep;
    SimpleStringProperty mu_res;
    SimpleStringProperty mu_com;
    SimpleStringProperty co_tipo;
    SimpleStringProperty co_perc;
    SimpleBooleanProperty co_limite;
    SimpleStringProperty co_dias;
    SimpleStringProperty ju_tipo;
    SimpleStringProperty ju_percent;
    SimpleStringProperty ca_multa;
    SimpleStringProperty ca_juros;
    SimpleStringProperty ca_correcao;
    SimpleStringProperty co;
    SimpleStringProperty ep_percent;
    SimpleStringProperty ep_vrlor;
    SimpleBooleanProperty bol_txbanc;

    public MultasModel(Integer id, String multas_tipo, String pa_mu, String pa_ju, String pa_co, String pa_ep,
                       String mu_res, String mu_com, String co_tipo, String co_perc, Boolean co_limite, String co_dias,
                       String ju_tipo, String ju_percent, String ca_multa, String ca_juros, String ca_correcao,
                       String co, String ep_percent, String ep_vrlor, boolean bol_txbanc) {
        this.id = new SimpleIntegerProperty(id);
        this.multas_tipo = new SimpleStringProperty(multas_tipo);
        this.pa_mu = new SimpleStringProperty(pa_mu);
        this.pa_ju = new SimpleStringProperty(pa_ju);
        this.pa_co = new SimpleStringProperty(pa_co);
        this.pa_ep = new SimpleStringProperty(pa_ep);
        this.mu_res = new SimpleStringProperty(mu_res);
        this.mu_com = new SimpleStringProperty(mu_com);
        this.co_tipo = new SimpleStringProperty(co_tipo);
        this.co_perc = new SimpleStringProperty(co_perc);
        this.co_limite = new SimpleBooleanProperty(co_limite);
        this.co_dias = new SimpleStringProperty(co_dias);
        this.ju_tipo = new SimpleStringProperty(ju_tipo);
        this.ju_percent = new SimpleStringProperty(ju_percent);
        this.ca_multa = new SimpleStringProperty(ca_multa);
        this.ca_juros = new SimpleStringProperty(ca_juros);
        this.ca_correcao = new SimpleStringProperty(ca_correcao);
        this.co = new SimpleStringProperty(co);
        this.ep_percent = new SimpleStringProperty(ep_percent);
        this.ep_vrlor = new SimpleStringProperty(ep_vrlor);
        this.bol_txbanc = new SimpleBooleanProperty(bol_txbanc);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getMultas_tipo() {
        return multas_tipo.get();
    }

    public SimpleStringProperty multas_tipoProperty() {
        return multas_tipo;
    }

    public void setMultas_tipo(String multas_tipo) {
        this.multas_tipo.set(multas_tipo);
    }

    public String getPa_mu() {
        return pa_mu.get();
    }

    public SimpleStringProperty pa_muProperty() {
        return pa_mu;
    }

    public void setPa_mu(String pa_mu) {
        this.pa_mu.set(pa_mu);
    }

    public String getPa_ju() {
        return pa_ju.get();
    }

    public SimpleStringProperty pa_juProperty() {
        return pa_ju;
    }

    public void setPa_ju(String pa_ju) {
        this.pa_ju.set(pa_ju);
    }

    public String getPa_co() {
        return pa_co.get();
    }

    public SimpleStringProperty pa_coProperty() {
        return pa_co;
    }

    public void setPa_co(String pa_co) {
        this.pa_co.set(pa_co);
    }

    public String getPa_ep() {
        return pa_ep.get();
    }

    public SimpleStringProperty pa_epProperty() {
        return pa_ep;
    }

    public void setPa_ep(String pa_ep) {
        this.pa_ep.set(pa_ep);
    }

    public String getMu_res() {
        return mu_res.get();
    }

    public SimpleStringProperty mu_resProperty() {
        return mu_res;
    }

    public void setMu_res(String mu_res) {
        this.mu_res.set(mu_res);
    }

    public String getMu_com() {
        return mu_com.get();
    }

    public SimpleStringProperty mu_comProperty() {
        return mu_com;
    }

    public void setMu_com(String mu_com) {
        this.mu_com.set(mu_com);
    }

    public String getCo_tipo() {
        return co_tipo.get();
    }

    public SimpleStringProperty co_tipoProperty() {
        return co_tipo;
    }

    public void setCo_tipo(String co_tipo) {
        this.co_tipo.set(co_tipo);
    }

    public String getCo_perc() {
        return co_perc.get();
    }

    public SimpleStringProperty co_percProperty() {
        return co_perc;
    }

    public void setCo_perc(String co_perc) {
        this.co_perc.set(co_perc);
    }

    public boolean getCo_limite() {
        return co_limite.get();
    }

    public SimpleBooleanProperty co_limiteProperty() {
        return co_limite;
    }

    public void setCo_limite(boolean co_limite) {
        this.co_limite.set(co_limite);
    }

    public String getCo_dias() {
        return co_dias.get();
    }

    public SimpleStringProperty co_diasProperty() {
        return co_dias;
    }

    public void setCo_dias(String co_dias) {
        this.co_dias.set(co_dias);
    }

    public String getJu_tipo() {
        return ju_tipo.get();
    }

    public SimpleStringProperty ju_tipoProperty() {
        return ju_tipo;
    }

    public void setJu_tipo(String ju_tipo) {
        this.ju_tipo.set(ju_tipo);
    }

    public String getJu_percent() {
        return ju_percent.get();
    }

    public SimpleStringProperty ju_percentProperty() {
        return ju_percent;
    }

    public void setJu_percent(String ju_percent) {
        this.ju_percent.set(ju_percent);
    }

    public String getCa_multa() {
        return ca_multa.get();
    }

    public SimpleStringProperty ca_multaProperty() {
        return ca_multa;
    }

    public void setCa_multa(String ca_multa) {
        this.ca_multa.set(ca_multa);
    }

    public String getCa_juros() {
        return ca_juros.get();
    }

    public SimpleStringProperty ca_jurosProperty() {
        return ca_juros;
    }

    public void setCa_juros(String ca_juros) {
        this.ca_juros.set(ca_juros);
    }

    public String getCa_correcao() {
        return ca_correcao.get();
    }

    public SimpleStringProperty ca_correcaoProperty() {
        return ca_correcao;
    }

    public void setCa_correcao(String ca_correcao) {
        this.ca_correcao.set(ca_correcao);
    }

    public String getCo() {
        return co.get();
    }

    public SimpleStringProperty coProperty() {
        return co;
    }

    public void setCo(String co) {
        this.co.set(co);
    }

    public String getEp_percent() {
        return ep_percent.get();
    }

    public SimpleStringProperty ep_percentProperty() {
        return ep_percent;
    }

    public void setEp_percent(String ep_percent) {
        this.ep_percent.set(ep_percent);
    }

    public String getEp_vrlor() {
        return ep_vrlor.get();
    }

    public SimpleStringProperty ep_vrlorProperty() {
        return ep_vrlor;
    }

    public void setEp_vrlor(String ep_vrlor) {
        this.ep_vrlor.set(ep_vrlor);
    }

    public boolean isBol_txbanc() { return bol_txbanc.get(); }

    public SimpleBooleanProperty bol_txbancProperty() { return bol_txbanc; }

    public void setBol_txbanc(boolean bol_txbanc) { this.bol_txbanc.set(bol_txbanc); }
}
