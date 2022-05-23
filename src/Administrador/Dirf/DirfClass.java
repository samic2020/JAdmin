package Administrador.Dirf;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class DirfClass {
    private SimpleStringProperty ano;
    
    private SimpleStringProperty p_registro;
    private SimpleStringProperty p_nome;
    private SimpleStringProperty p_cpfcnpj;
    private SimpleStringProperty p_urbrural;
    
    private SimpleStringProperty i_registro;
    private SimpleStringProperty i_endereco;
    private SimpleStringProperty i_cep;
    private SimpleStringProperty i_codmun;
    private SimpleStringProperty i_estado;
    
    private SimpleStringProperty l_registro;
    private SimpleStringProperty l_nome;
    private SimpleStringProperty l_cpfcnpj;
    private SimpleStringProperty l_contrato;
    
    private SimpleStringProperty m_al_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_al_vr[] = new SimpleObjectProperty[12];
    private SimpleStringProperty m_cm_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_cm_vr[] = new SimpleObjectProperty[12];
    private SimpleStringProperty m_ir_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_ir_vr[] = new SimpleObjectProperty[12];
    private SimpleObjectProperty<BigDecimal> m_dc[] = new SimpleObjectProperty[12];
    private SimpleObjectProperty<BigDecimal> m_df[] = new SimpleObjectProperty[12];
    private SimpleStringProperty m_mu_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_mu_vr[] = new SimpleObjectProperty[12];
    private SimpleStringProperty m_ju_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_ju_vr[] = new SimpleObjectProperty[12];
    private SimpleStringProperty m_co_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_co_vr[] = new SimpleObjectProperty[12];
    private SimpleStringProperty m_ep_nm[] = new SimpleStringProperty[12];
    private SimpleObjectProperty<BigDecimal> m_ep_vr[] = new SimpleObjectProperty[12];
    private SimpleObjectProperty<BigDecimal> m_tx[] = new SimpleObjectProperty[12];
    private SimpleObjectProperty<BigDecimal> m_01[] = new SimpleObjectProperty[12];
    private SimpleObjectProperty<BigDecimal> m_02[] = new SimpleObjectProperty[12];
    private SimpleObjectProperty<BigDecimal> m_03[] = new SimpleObjectProperty[12];

    private String jan_al_nm = null;
    private BigDecimal jan_al_vr = null;
    private String jan_cm_nm = null;
    private BigDecimal jan_cm_vr = null;
    private String jan_ir_nm = null;
    private BigDecimal jan_ir_vr = null;
    private BigDecimal jan_m_dc = null;
    private BigDecimal jan_m_df = null;
    private String jan_mu_nm = null;
    private BigDecimal jan_mu_vr = null;
    private String jan_ju_nm = null;
    private BigDecimal jan_ju_vr = null;
    private String jan_co_nm = null;
    private BigDecimal jan_co_vr = null;
    private String jan_ep_nm = null;
    private BigDecimal jan_ep_vr = null;
    private BigDecimal jan_m_tx = null;
    private BigDecimal jan_m_01 = null;
    private BigDecimal jan_m_02 = null;
    private BigDecimal jan_m_03 = null;

    private String fev_al_nm = null;
    private BigDecimal fev_al_vr = null;
    private String fev_cm_nm = null;
    private BigDecimal fev_cm_vr = null;
    private String fev_ir_nm = null;
    private BigDecimal fev_ir_vr = null;
    private BigDecimal fev_m_dc = null;
    private BigDecimal fev_m_df = null;
    private String fev_mu_nm = null;
    private BigDecimal fev_mu_vr = null;
    private String fev_ju_nm = null;
    private BigDecimal fev_ju_vr = null;
    private String fev_co_nm = null;
    private BigDecimal fev_co_vr = null;
    private String fev_ep_nm = null;
    private BigDecimal fev_ep_vr = null;
    private BigDecimal fev_m_tx = null;
    private BigDecimal fev_m_01 = null;
    private BigDecimal fev_m_02 = null;
    private BigDecimal fev_m_03 = null;

    private String mar_al_nm = null;
    private BigDecimal mar_al_vr = null;
    private String mar_cm_nm = null;
    private BigDecimal mar_cm_vr = null;
    private String mar_ir_nm = null;
    private BigDecimal mar_ir_vr = null;
    private BigDecimal mar_m_dc = null;
    private BigDecimal mar_m_df = null;
    private String mar_mu_nm = null;
    private BigDecimal mar_mu_vr = null;
    private String mar_ju_nm = null;
    private BigDecimal mar_ju_vr = null;
    private String mar_co_nm = null;
    private BigDecimal mar_co_vr = null;
    private String mar_ep_nm = null;
    private BigDecimal mar_ep_vr = null;
    private BigDecimal mar_m_tx = null;
    private BigDecimal mar_m_01 = null;
    private BigDecimal mar_m_02 = null;
    private BigDecimal mar_m_03 = null;

    private String abr_al_nm = null;
    private BigDecimal abr_al_vr = null;
    private String abr_cm_nm = null;
    private BigDecimal abr_cm_vr = null;
    private String abr_ir_nm = null;
    private BigDecimal abr_ir_vr = null;
    private BigDecimal abr_m_dc = null;
    private BigDecimal abr_m_df = null;
    private String abr_mu_nm = null;
    private BigDecimal abr_mu_vr = null;
    private String abr_ju_nm = null;
    private BigDecimal abr_ju_vr = null;
    private String abr_co_nm = null;
    private BigDecimal abr_co_vr = null;
    private String abr_ep_nm = null;
    private BigDecimal abr_ep_vr = null;
    private BigDecimal abr_m_tx = null;
    private BigDecimal abr_m_01 = null;
    private BigDecimal abr_m_02 = null;
    private BigDecimal abr_m_03 = null;

    private String mai_al_nm = null;
    private BigDecimal mai_al_vr = null;
    private String mai_cm_nm = null;
    private BigDecimal mai_cm_vr = null;
    private String mai_ir_nm = null;
    private BigDecimal mai_ir_vr = null;
    private BigDecimal mai_m_dc = null;
    private BigDecimal mai_m_df = null;
    private String mai_mu_nm = null;
    private BigDecimal mai_mu_vr = null;
    private String mai_ju_nm = null;
    private BigDecimal mai_ju_vr = null;
    private String mai_co_nm = null;
    private BigDecimal mai_co_vr = null;
    private String mai_ep_nm = null;
    private BigDecimal mai_ep_vr = null;
    private BigDecimal mai_m_tx = null;
    private BigDecimal mai_m_01 = null;
    private BigDecimal mai_m_02 = null;
    private BigDecimal mai_m_03 = null;

    private String jun_al_nm = null;
    private BigDecimal jun_al_vr = null;
    private String jun_cm_nm = null;
    private BigDecimal jun_cm_vr = null;
    private String jun_ir_nm = null;
    private BigDecimal jun_ir_vr = null;
    private BigDecimal jun_m_dc = null;
    private BigDecimal jun_m_df = null;
    private String jun_mu_nm = null;
    private BigDecimal jun_mu_vr = null;
    private String jun_ju_nm = null;
    private BigDecimal jun_ju_vr = null;
    private String jun_co_nm = null;
    private BigDecimal jun_co_vr = null;
    private String jun_ep_nm = null;
    private BigDecimal jun_ep_vr = null;
    private BigDecimal jun_m_tx = null;
    private BigDecimal jun_m_01 = null;
    private BigDecimal jun_m_02 = null;
    private BigDecimal jun_m_03 = null;

    private String jul_al_nm = null;
    private BigDecimal jul_al_vr = null;
    private String jul_cm_nm = null;
    private BigDecimal jul_cm_vr = null;
    private String jul_ir_nm = null;
    private BigDecimal jul_ir_vr = null;
    private BigDecimal jul_m_dc = null;
    private BigDecimal jul_m_df = null;
    private String jul_mu_nm = null;
    private BigDecimal jul_mu_vr = null;
    private String jul_ju_nm = null;
    private BigDecimal jul_ju_vr = null;
    private String jul_co_nm = null;
    private BigDecimal jul_co_vr = null;
    private String jul_ep_nm = null;
    private BigDecimal jul_ep_vr = null;
    private BigDecimal jul_m_tx = null;
    private BigDecimal jul_m_01 = null;
    private BigDecimal jul_m_02 = null;
    private BigDecimal jul_m_03 = null;

    private String ago_al_nm = null;
    private BigDecimal ago_al_vr = null;
    private String ago_cm_nm = null;
    private BigDecimal ago_cm_vr = null;
    private String ago_ir_nm = null;
    private BigDecimal ago_ir_vr = null;
    private BigDecimal ago_m_dc = null;
    private BigDecimal ago_m_df = null;
    private String ago_mu_nm = null;
    private BigDecimal ago_mu_vr = null;
    private String ago_ju_nm = null;
    private BigDecimal ago_ju_vr = null;
    private String ago_co_nm = null;
    private BigDecimal ago_co_vr = null;
    private String ago_ep_nm = null;
    private BigDecimal ago_ep_vr = null;
    private BigDecimal ago_m_tx = null;
    private BigDecimal ago_m_01 = null;
    private BigDecimal ago_m_02 = null;
    private BigDecimal ago_m_03 = null;

    private String set_al_nm = null;
    private BigDecimal set_al_vr = null;
    private String set_cm_nm = null;
    private BigDecimal set_cm_vr = null;
    private String set_ir_nm = null;
    private BigDecimal set_ir_vr = null;
    private BigDecimal set_m_dc = null;
    private BigDecimal set_m_df = null;
    private String set_mu_nm = null;
    private BigDecimal set_mu_vr = null;
    private String set_ju_nm = null;
    private BigDecimal set_ju_vr = null;
    private String set_co_nm = null;
    private BigDecimal set_co_vr = null;
    private String set_ep_nm = null;
    private BigDecimal set_ep_vr = null;
    private BigDecimal set_m_tx = null;
    private BigDecimal set_m_01 = null;
    private BigDecimal set_m_02 = null;
    private BigDecimal set_m_03 = null;

    private String out_al_nm = null;
    private BigDecimal out_al_vr = null;
    private String out_cm_nm = null;
    private BigDecimal out_cm_vr = null;
    private String out_ir_nm = null;
    private BigDecimal out_ir_vr = null;
    private BigDecimal out_m_dc = null;
    private BigDecimal out_m_df = null;
    private String out_mu_nm = null;
    private BigDecimal out_mu_vr = null;
    private String out_ju_nm = null;
    private BigDecimal out_ju_vr = null;
    private String out_co_nm = null;
    private BigDecimal out_co_vr = null;
    private String out_ep_nm = null;
    private BigDecimal out_ep_vr = null;
    private BigDecimal out_m_tx = null;
    private BigDecimal out_m_01 = null;
    private BigDecimal out_m_02 = null;
    private BigDecimal out_m_03 = null;

    private String nov_al_nm = null;
    private BigDecimal nov_al_vr = null;
    private String nov_cm_nm = null;
    private BigDecimal nov_cm_vr = null;
    private String nov_ir_nm = null;
    private BigDecimal nov_ir_vr = null;
    private BigDecimal nov_m_dc = null;
    private BigDecimal nov_m_df = null;
    private String nov_mu_nm = null;
    private BigDecimal nov_mu_vr = null;
    private String nov_ju_nm = null;
    private BigDecimal nov_ju_vr = null;
    private String nov_co_nm = null;
    private BigDecimal nov_co_vr = null;
    private String nov_ep_nm = null;
    private BigDecimal nov_ep_vr = null;
    private BigDecimal nov_m_tx = null;
    private BigDecimal nov_m_01 = null;
    private BigDecimal nov_m_02 = null;
    private BigDecimal nov_m_03 = null;

    private String dez_al_nm = null;
    private BigDecimal dez_al_vr = null;
    private String dez_cm_nm = null;
    private BigDecimal dez_cm_vr = null;
    private String dez_ir_nm = null;
    private BigDecimal dez_ir_vr = null;
    private BigDecimal dez_m_dc = null;
    private BigDecimal dez_m_df = null;
    private String dez_mu_nm = null;
    private BigDecimal dez_mu_vr = null;
    private String dez_ju_nm = null;
    private BigDecimal dez_ju_vr = null;
    private String dez_co_nm = null;
    private BigDecimal dez_co_vr = null;
    private String dez_ep_nm = null;
    private BigDecimal dez_ep_vr = null;
    private BigDecimal dez_m_tx = null;
    private BigDecimal dez_m_01 = null;
    private BigDecimal dez_m_02 = null;
    private BigDecimal dez_m_03 = null;

    public DirfClass(String ano, String p_registro, String p_nome, String p_cpfcnpj, String p_urbrural,
                     String i_registro, String i_endereco, String i_cep, String i_codmun, String i_estado,
                     String l_registro, String l_nome, String l_cpfcnpj, String l_contrato) {
        this.ano = new SimpleStringProperty(ano);
        this.p_registro = new SimpleStringProperty(p_registro);
        this.p_nome = new SimpleStringProperty(p_nome);
        this.p_cpfcnpj = new SimpleStringProperty(p_cpfcnpj);
        this.p_urbrural = new SimpleStringProperty(p_urbrural);
        this.i_registro = new SimpleStringProperty(i_registro);
        this.i_endereco = new SimpleStringProperty(i_endereco);
        this.i_cep = new SimpleStringProperty(i_cep);
        this.i_codmun = new SimpleStringProperty(i_codmun);
        this.i_estado = new SimpleStringProperty(i_estado);
        this.l_registro = new SimpleStringProperty(l_registro);
        this.l_nome = new SimpleStringProperty(l_nome);
        this.l_cpfcnpj = new SimpleStringProperty(l_cpfcnpj);
        this.l_contrato = new SimpleStringProperty(l_contrato);

        for (int i = 0; i <= 11; i++) {
            setMes(i,null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null);
        }
    }

    public String getAno() { return ano.get(); }
    public SimpleStringProperty anoProperty() { return ano; }
    public void setAno(String ano) { this.ano.set(ano); }

    public String getP_registro() { return p_registro.get(); }
    public SimpleStringProperty p_registroProperty() { return p_registro; }
    public void setP_registro(String p_registro) { this.p_registro.set(p_registro); }

    public String getP_nome() { return p_nome.get(); }
    public SimpleStringProperty p_nomeProperty() { return p_nome; }
    public void setP_nome(String p_nome) { this.p_nome.set(p_nome); }

    public String getP_cpfcnpj() { return p_cpfcnpj.get(); }
    public SimpleStringProperty p_cpfcnpjProperty() { return p_cpfcnpj; }
    public void setP_cpfcnpj(String p_cpfcnpj) { this.p_cpfcnpj.set(p_cpfcnpj); }

    public String getP_urbrural() { return p_urbrural.get(); }
    public SimpleStringProperty p_urbruralProperty() { return p_urbrural; }
    public void setP_urbrural(String p_urbrural) { this.p_urbrural.set(p_urbrural); }

    public String getI_registro() { return i_registro.get(); }
    public SimpleStringProperty i_registroProperty() { return i_registro; }
    public void setI_registro(String i_registro) { this.i_registro.set(i_registro); }

    public String getI_endereco() { return i_endereco.get(); }
    public SimpleStringProperty i_enderecoProperty() { return i_endereco; }
    public void setI_endereco(String i_endereco) { this.i_endereco.set(i_endereco); }

    public String getI_cep() { return i_cep.get(); }
    public SimpleStringProperty i_cepProperty() { return i_cep; }
    public void setI_cep(String i_cep) { this.i_cep.set(i_cep); }

    public String getI_codmun() { return i_codmun.get(); }
    public SimpleStringProperty i_codmunProperty() { return i_codmun; }
    public void setI_codmun(String i_codmun) { this.i_codmun.set(i_codmun); }

    public String getI_estado() { return i_estado.get(); }
    public SimpleStringProperty i_estadoProperty() { return i_estado; }
    public void setI_estado(String i_estado) { this.i_estado.set(i_estado); }

    public String getL_registro() { return l_registro.get(); }
    public SimpleStringProperty l_registroProperty() { return l_registro; }
    public void setL_registro(String l_registro) { this.l_registro.set(l_registro); }

    public String getL_nome() { return l_nome.get(); }
    public SimpleStringProperty l_nomeProperty() { return l_nome; }
    public void setL_nome(String l_nome) { this.l_nome.set(l_nome); }

    public String getL_cpfcnpj() { return l_cpfcnpj.get(); }
    public SimpleStringProperty l_cpfcnpjProperty() { return l_cpfcnpj; }
    public void setL_cpfcnpj(String l_cpfcnpj) { this.l_cpfcnpj.set(l_cpfcnpj); }

    public String getL_contrato() { return l_contrato.get(); }
    public SimpleStringProperty l_contratoProperty() { return l_contrato; }
    public void setL_contrato(String l_contrato) { this.l_contrato.set(l_contrato); }

    public void setMes(int mes, String al_nm, BigDecimal al_vr,
                         String cm_nm, BigDecimal cm_vr,
                         String ir_nm, BigDecimal ir_vr,
                         BigDecimal dc, BigDecimal df,
                         String mu_nm, BigDecimal mu_vr,
                         String ju_nm, BigDecimal ju_vr,
                         String co_nm, BigDecimal co_vr,
                         String ep_nm, BigDecimal ep_vr,
                         BigDecimal tx, BigDecimal _01,
                         BigDecimal _02, BigDecimal _03) {
        m_al_nm[mes] = new SimpleStringProperty(al_nm);
        m_al_vr[mes] = new SimpleObjectProperty<>(al_vr);

        m_cm_nm[mes] = new SimpleStringProperty(cm_nm);
        m_cm_vr[mes] = new SimpleObjectProperty(cm_vr);

        m_ir_nm[mes] = new SimpleStringProperty(ir_nm);
        m_ir_vr[mes] = new SimpleObjectProperty(ir_vr);

        m_dc[mes] = new SimpleObjectProperty(dc);
        m_df[mes] = new SimpleObjectProperty(df);

        m_mu_nm[mes] = new SimpleStringProperty(mu_nm);
        m_mu_vr[mes] = new SimpleObjectProperty(mu_vr);

        m_ju_nm[mes] = new SimpleStringProperty(ju_nm);
        m_ju_vr[mes] = new SimpleObjectProperty(ju_vr);

        m_co_nm[mes] = new SimpleStringProperty(co_nm);
        m_co_vr[mes] = new SimpleObjectProperty(co_vr);

        m_ep_nm[mes] = new SimpleStringProperty(ep_nm);
        m_ep_vr[mes] = new SimpleObjectProperty(ep_vr);

        m_tx[mes] = new SimpleObjectProperty(tx);
        m_01[mes] = new SimpleObjectProperty(_01);
        m_02[mes] = new SimpleObjectProperty(_02);
        m_03[mes] = new SimpleObjectProperty(_03);

        if (mes == 0) {
            jan_al_nm = al_nm;
            jan_al_vr = al_vr;
            jan_cm_nm = cm_nm;
            jan_cm_vr = cm_vr;
            jan_ir_nm = ir_nm;
            jan_ir_vr = ir_vr;
            jan_m_dc = dc;
            jan_m_df = df;
            jan_mu_nm = mu_nm;
            jan_mu_vr = mu_vr;
            jan_ju_nm = ju_nm;
            jan_ju_vr = ju_vr;
            jan_co_nm = co_nm;
            jan_co_vr = co_vr;
            jan_ep_nm = ep_nm;
            jan_ep_vr = ep_vr;
            jan_m_tx = tx;
            jan_m_01 = _01;
            jan_m_02 = _02;
            jan_m_03 = _03;
        }
        if (mes == 1) {
            fev_al_nm = al_nm;
            fev_al_vr = al_vr;
            fev_cm_nm = cm_nm;
            fev_cm_vr = cm_vr;
            fev_ir_nm = ir_nm;
            fev_ir_vr = ir_vr;
            fev_m_dc = dc;
            fev_m_df = df;
            fev_mu_nm = mu_nm;
            fev_mu_vr = mu_vr;
            fev_ju_nm = ju_nm;
            fev_ju_vr = ju_vr;
            fev_co_nm = co_nm;
            fev_co_vr = co_vr;
            fev_ep_nm = ep_nm;
            fev_ep_vr = ep_vr;
            fev_m_tx = tx;
            fev_m_01 = _01;
            fev_m_02 = _02;
            fev_m_03 = _03;
        }
        if (mes == 2) {
            mar_al_nm = al_nm;
            mar_al_vr = al_vr;
            mar_cm_nm = cm_nm;
            mar_cm_vr = cm_vr;
            mar_ir_nm = ir_nm;
            mar_ir_vr = ir_vr;
            mar_m_dc = dc;
            mar_m_df = df;
            mar_mu_nm = mu_nm;
            mar_mu_vr = mu_vr;
            mar_ju_nm = ju_nm;
            mar_ju_vr = ju_vr;
            mar_co_nm = co_nm;
            mar_co_vr = co_vr;
            mar_ep_nm = ep_nm;
            mar_ep_vr = ep_vr;
            mar_m_tx = tx;
            mar_m_01 = _01;
            mar_m_02 = _02;
            mar_m_03 = _03;
        }
        if (mes == 3) {
            abr_al_nm = al_nm;
            abr_al_vr = al_vr;
            abr_cm_nm = cm_nm;
            abr_cm_vr = cm_vr;
            abr_ir_nm = ir_nm;
            abr_ir_vr = ir_vr;
            abr_m_dc = dc;
            abr_m_df = df;
            abr_mu_nm = mu_nm;
            abr_mu_vr = mu_vr;
            abr_ju_nm = ju_nm;
            abr_ju_vr = ju_vr;
            abr_co_nm = co_nm;
            abr_co_vr = co_vr;
            abr_ep_nm = ep_nm;
            abr_ep_vr = ep_vr;
            abr_m_tx = tx;
            abr_m_01 = _01;
            abr_m_02 = _02;
            abr_m_03 = _03;
        }
        if (mes == 4) {
            mai_al_nm = al_nm;
            mai_al_vr = al_vr;
            mai_cm_nm = cm_nm;
            mai_cm_vr = cm_vr;
            mai_ir_nm = ir_nm;
            mai_ir_vr = ir_vr;
            mai_m_dc = dc;
            mai_m_df = df;
            mai_mu_nm = mu_nm;
            mai_mu_vr = mu_vr;
            mai_ju_nm = ju_nm;
            mai_ju_vr = ju_vr;
            mai_co_nm = co_nm;
            mai_co_vr = co_vr;
            mai_ep_nm = ep_nm;
            mai_ep_vr = ep_vr;
            mai_m_tx = tx;
            mai_m_01 = _01;
            mai_m_02 = _02;
            mai_m_03 = _03;
        }
        if (mes == 5) {
            jun_al_nm = al_nm;
            jun_al_vr = al_vr;
            jun_cm_nm = cm_nm;
            jun_cm_vr = cm_vr;
            jun_ir_nm = ir_nm;
            jun_ir_vr = ir_vr;
            jun_m_dc = dc;
            jun_m_df = df;
            jun_mu_nm = mu_nm;
            jun_mu_vr = mu_vr;
            jun_ju_nm = ju_nm;
            jun_ju_vr = ju_vr;
            jun_co_nm = co_nm;
            jun_co_vr = co_vr;
            jun_ep_nm = ep_nm;
            jun_ep_vr = ep_vr;
            jun_m_tx = tx;
            jun_m_01 = _01;
            jun_m_02 = _02;
            jun_m_03 = _03;
        }
        if (mes == 6) {
            jul_al_nm = al_nm;
            jul_al_vr = al_vr;
            jul_cm_nm = cm_nm;
            jul_cm_vr = cm_vr;
            jul_ir_nm = ir_nm;
            jul_ir_vr = ir_vr;
            jul_m_dc = dc;
            jul_m_df = df;
            jul_mu_nm = mu_nm;
            jul_mu_vr = mu_vr;
            jul_ju_nm = ju_nm;
            jul_ju_vr = ju_vr;
            jul_co_nm = co_nm;
            jul_co_vr = co_vr;
            jul_ep_nm = ep_nm;
            jul_ep_vr = ep_vr;
            jul_m_tx = tx;
            jul_m_01 = _01;
            jul_m_02 = _02;
            jul_m_03 = _03;
        }
        if (mes == 7) {
            ago_al_nm = al_nm;
            ago_al_vr = al_vr;
            ago_cm_nm = cm_nm;
            ago_cm_vr = cm_vr;
            ago_ir_nm = ir_nm;
            ago_ir_vr = ir_vr;
            ago_m_dc = dc;
            ago_m_df = df;
            ago_mu_nm = mu_nm;
            ago_mu_vr = mu_vr;
            ago_ju_nm = ju_nm;
            ago_ju_vr = ju_vr;
            ago_co_nm = co_nm;
            ago_co_vr = co_vr;
            ago_ep_nm = ep_nm;
            ago_ep_vr = ep_vr;
            ago_m_tx = tx;
            ago_m_01 = _01;
            ago_m_02 = _02;
            ago_m_03 = _03;
        }
        if (mes == 8) {
            set_al_nm = al_nm;
            set_al_vr = al_vr;
            set_cm_nm = cm_nm;
            set_cm_vr = cm_vr;
            set_ir_nm = ir_nm;
            set_ir_vr = ir_vr;
            set_m_dc = dc;
            set_m_df = df;
            set_mu_nm = mu_nm;
            set_mu_vr = mu_vr;
            set_ju_nm = ju_nm;
            set_ju_vr = ju_vr;
            set_co_nm = co_nm;
            set_co_vr = co_vr;
            set_ep_nm = ep_nm;
            set_ep_vr = ep_vr;
            set_m_tx = tx;
            set_m_01 = _01;
            set_m_02 = _02;
            set_m_03 = _03;
        }
        if (mes == 9) {
            out_al_nm = al_nm;
            out_al_vr = al_vr;
            out_cm_nm = cm_nm;
            out_cm_vr = cm_vr;
            out_ir_nm = ir_nm;
            out_ir_vr = ir_vr;
            out_m_dc = dc;
            out_m_df = df;
            out_mu_nm = mu_nm;
            out_mu_vr = mu_vr;
            out_ju_nm = ju_nm;
            out_ju_vr = ju_vr;
            out_co_nm = co_nm;
            out_co_vr = co_vr;
            out_ep_nm = ep_nm;
            out_ep_vr = ep_vr;
            out_m_tx = tx;
            out_m_01 = _01;
            out_m_02 = _02;
            out_m_03 = _03;
        }
        if (mes == 10) {
            nov_al_nm = al_nm;
            nov_al_vr = al_vr;
            nov_cm_nm = cm_nm;
            nov_cm_vr = cm_vr;
            nov_ir_nm = ir_nm;
            nov_ir_vr = ir_vr;
            nov_m_dc = dc;
            nov_m_df = df;
            nov_mu_nm = mu_nm;
            nov_mu_vr = mu_vr;
            nov_ju_nm = ju_nm;
            nov_ju_vr = ju_vr;
            nov_co_nm = co_nm;
            nov_co_vr = co_vr;
            nov_ep_nm = ep_nm;
            nov_ep_vr = ep_vr;
            nov_m_tx = tx;
            nov_m_01 = _01;
            nov_m_02 = _02;
            nov_m_03 = _03;
        }
        if (mes == 11) {
            dez_al_nm = al_nm;
            dez_al_vr = al_vr;
            dez_cm_nm = cm_nm;
            dez_cm_vr = cm_vr;
            dez_ir_nm = ir_nm;
            dez_ir_vr = ir_vr;
            dez_m_dc = dc;
            dez_m_df = df;
            dez_mu_nm = mu_nm;
            dez_mu_vr = mu_vr;
            dez_ju_nm = ju_nm;
            dez_ju_vr = ju_vr;
            dez_co_nm = co_nm;
            dez_co_vr = co_vr;
            dez_ep_nm = ep_nm;
            dez_ep_vr = ep_vr;
            dez_m_tx = tx;
            dez_m_01 = _01;
            dez_m_02 = _02;
            dez_m_03 = _03;
        }
    }

    public SimpleStringProperty[] getM_al_nm() { return m_al_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_al_vr() { return m_al_vr; }
    public SimpleStringProperty[] getM_cm_nm() { return m_cm_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_cm_vr() { return m_cm_vr; }
    public SimpleStringProperty[] getM_ir_nm() { return m_ir_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_ir_vr() { return m_ir_vr; }
    public SimpleObjectProperty<BigDecimal>[] getM_dc() { return m_dc; }
    public SimpleObjectProperty<BigDecimal>[] getM_df() { return m_df; }
    public SimpleStringProperty[] getM_mu_nm() { return m_mu_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_mu_vr() { return m_mu_vr; }
    public SimpleStringProperty[] getM_ju_nm() { return m_ju_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_ju_vr() { return m_ju_vr; }
    public SimpleStringProperty[] getM_co_nm() { return m_co_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_co_vr() { return m_co_vr; }
    public SimpleStringProperty[] getM_ep_nm() { return m_ep_nm; }
    public SimpleObjectProperty<BigDecimal>[] getM_ep_vr() { return m_ep_vr; }
    public SimpleObjectProperty<BigDecimal>[] getM_tx() { return m_tx; }
    public SimpleObjectProperty<BigDecimal>[] getM_01() { return m_01; }
    public SimpleObjectProperty<BigDecimal>[] getM_02() { return m_02; }
    public SimpleObjectProperty<BigDecimal>[] getM_03() { return m_03; }

    public String getJan_al_nm() { return jan_al_nm; }
    public BigDecimal getJan_al_vr() { return jan_al_vr; }
    public String getJan_cm_nm() { return jan_cm_nm; }
    public BigDecimal getJan_cm_vr() { return jan_cm_vr; }
    public String getJan_ir_nm() { return jan_ir_nm; }
    public BigDecimal getJan_ir_vr() { return jan_ir_vr; }
    public BigDecimal getJan_m_dc() { return jan_m_dc; }
    public BigDecimal getJan_m_df() { return jan_m_df; }
    public String getJan_mu_nm() { return jan_mu_nm; }
    public BigDecimal getJan_mu_vr() { return jan_mu_vr; }
    public String getJan_ju_nm() { return jan_ju_nm; }
    public BigDecimal getJan_ju_vr() { return jan_ju_vr; }
    public String getJan_co_nm() { return jan_co_nm; }
    public BigDecimal getJan_co_vr() { return jan_co_vr; }
    public String getJan_ep_nm() { return jan_ep_nm; }
    public BigDecimal getJan_ep_vr() { return jan_ep_vr; }
    public BigDecimal getJan_m_tx() { return jan_m_tx; }
    public BigDecimal getJan_m_01() { return jan_m_01; }
    public BigDecimal getJan_m_02() { return jan_m_02; }
    public BigDecimal getJan_m_03() { return jan_m_03; }

    public String getFev_al_nm() { return fev_al_nm; }
    public BigDecimal getFev_al_vr() { return fev_al_vr; }
    public String getFev_cm_nm() { return fev_cm_nm; }
    public BigDecimal getFev_cm_vr() { return fev_cm_vr; }
    public String getFev_ir_nm() { return fev_ir_nm; }
    public BigDecimal getFev_ir_vr() { return fev_ir_vr; }
    public BigDecimal getFev_m_dc() { return fev_m_dc; }
    public BigDecimal getFev_m_df() { return fev_m_df; }
    public String getFev_mu_nm() { return fev_mu_nm; }
    public BigDecimal getFev_mu_vr() { return fev_mu_vr; }
    public String getFev_ju_nm() { return fev_ju_nm; }
    public BigDecimal getFev_ju_vr() { return fev_ju_vr; }
    public String getFev_co_nm() { return fev_co_nm; }
    public BigDecimal getFev_co_vr() { return fev_co_vr; }
    public String getFev_ep_nm() { return fev_ep_nm; }
    public BigDecimal getFev_ep_vr() { return fev_ep_vr; }
    public BigDecimal getFev_m_tx() { return fev_m_tx; }
    public BigDecimal getFev_m_01() { return fev_m_01; }
    public BigDecimal getFev_m_02() { return fev_m_02; }
    public BigDecimal getFev_m_03() { return fev_m_03; }
    
    public String getMar_al_nm() { return mar_al_nm; }
    public BigDecimal getMar_al_vr() { return mar_al_vr; }
    public String getMar_cm_nm() { return mar_cm_nm; }
    public BigDecimal getMar_cm_vr() { return mar_cm_vr; }
    public String getMar_ir_nm() { return mar_ir_nm; }
    public BigDecimal getMar_ir_vr() { return mar_ir_vr; }
    public BigDecimal getMar_m_dc() { return mar_m_dc; }
    public BigDecimal getMar_m_df() { return mar_m_df; }
    public String getMar_mu_nm() { return mar_mu_nm; }
    public BigDecimal getMar_mu_vr() { return mar_mu_vr; }
    public String getMar_ju_nm() { return mar_ju_nm; }
    public BigDecimal getMar_ju_vr() { return mar_ju_vr; }
    public String getMar_co_nm() { return mar_co_nm; }
    public BigDecimal getMar_co_vr() { return mar_co_vr; }
    public String getMar_ep_nm() { return mar_ep_nm; }
    public BigDecimal getMar_ep_vr() { return mar_ep_vr; }
    public BigDecimal getMar_m_tx() { return mar_m_tx; }
    public BigDecimal getMar_m_01() { return mar_m_01; }
    public BigDecimal getMar_m_02() { return mar_m_02; }
    public BigDecimal getMar_m_03() { return mar_m_03; }
    
    public String getAbr_al_nm() { return abr_al_nm; }
    public BigDecimal getAbr_al_vr() { return abr_al_vr; }
    public String getAbr_cm_nm() { return abr_cm_nm; }
    public BigDecimal getAbr_cm_vr() { return abr_cm_vr; }
    public String getAbr_ir_nm() { return abr_ir_nm; }
    public BigDecimal getAbr_ir_vr() { return abr_ir_vr; }
    public BigDecimal getAbr_m_dc() { return abr_m_dc; }
    public BigDecimal getAbr_m_df() { return abr_m_df; }
    public String getAbr_mu_nm() { return abr_mu_nm; }
    public BigDecimal getAbr_mu_vr() { return abr_mu_vr; }
    public String getAbr_ju_nm() { return abr_ju_nm; }
    public BigDecimal getAbr_ju_vr() { return abr_ju_vr; }
    public String getAbr_co_nm() { return abr_co_nm; }
    public BigDecimal getAbr_co_vr() { return abr_co_vr; }
    public String getAbr_ep_nm() { return abr_ep_nm; }
    public BigDecimal getAbr_ep_vr() { return abr_ep_vr; }
    public BigDecimal getAbr_m_tx() { return abr_m_tx; }
    public BigDecimal getAbr_m_01() { return abr_m_01; }
    public BigDecimal getAbr_m_02() { return abr_m_02; }
    public BigDecimal getAbr_m_03() { return abr_m_03; }
    
    public String getMai_al_nm() { return mai_al_nm; }
    public BigDecimal getMai_al_vr() { return mai_al_vr; }
    public String getMai_cm_nm() { return mai_cm_nm; }
    public BigDecimal getMai_cm_vr() { return mai_cm_vr; }
    public String getMai_ir_nm() { return mai_ir_nm; }
    public BigDecimal getMai_ir_vr() { return mai_ir_vr; }
    public BigDecimal getMai_m_dc() { return mai_m_dc; }
    public BigDecimal getMai_m_df() { return mai_m_df; }
    public String getMai_mu_nm() { return mai_mu_nm; }
    public BigDecimal getMai_mu_vr() { return mai_mu_vr; }
    public String getMai_ju_nm() { return mai_ju_nm; }
    public BigDecimal getMai_ju_vr() { return mai_ju_vr; }
    public String getMai_co_nm() { return mai_co_nm; }
    public BigDecimal getMai_co_vr() { return mai_co_vr; }
    public String getMai_ep_nm() { return mai_ep_nm; }
    public BigDecimal getMai_ep_vr() { return mai_ep_vr; }
    public BigDecimal getMai_m_tx() { return mai_m_tx; }
    public BigDecimal getMai_m_01() { return mai_m_01; }
    public BigDecimal getMai_m_02() { return mai_m_02; }
    public BigDecimal getMai_m_03() { return mai_m_03; }
    
    public String getJun_al_nm() { return jun_al_nm; }
    public BigDecimal getJun_al_vr() { return jun_al_vr; }
    public String getJun_cm_nm() { return jun_cm_nm; }
    public BigDecimal getJun_cm_vr() { return jun_cm_vr; }
    public String getJun_ir_nm() { return jun_ir_nm; }
    public BigDecimal getJun_ir_vr() { return jun_ir_vr; }
    public BigDecimal getJun_m_dc() { return jun_m_dc; }
    public BigDecimal getJun_m_df() { return jun_m_df; }
    public String getJun_mu_nm() { return jun_mu_nm; }
    public BigDecimal getJun_mu_vr() { return jun_mu_vr; }
    public String getJun_ju_nm() { return jun_ju_nm; }
    public BigDecimal getJun_ju_vr() { return jun_ju_vr; }
    public String getJun_co_nm() { return jun_co_nm; }
    public BigDecimal getJun_co_vr() { return jun_co_vr; }
    public String getJun_ep_nm() { return jun_ep_nm; }
    public BigDecimal getJun_ep_vr() { return jun_ep_vr; }
    public BigDecimal getJun_m_tx() { return jun_m_tx; }
    public BigDecimal getJun_m_01() { return jun_m_01; }
    public BigDecimal getJun_m_02() { return jun_m_02; }
    public BigDecimal getJun_m_03() { return jun_m_03; }
    
    public String getJul_al_nm() { return jul_al_nm; }
    public BigDecimal getJul_al_vr() { return jul_al_vr; }
    public String getJul_cm_nm() { return jul_cm_nm; }
    public BigDecimal getJul_cm_vr() { return jul_cm_vr; }
    public String getJul_ir_nm() { return jul_ir_nm; }
    public BigDecimal getJul_ir_vr() { return jul_ir_vr; }
    public BigDecimal getJul_m_dc() { return jul_m_dc; }
    public BigDecimal getJul_m_df() { return jul_m_df; }
    public String getJul_mu_nm() { return jul_mu_nm; }
    public BigDecimal getJul_mu_vr() { return jul_mu_vr; }
    public String getJul_ju_nm() { return jul_ju_nm; }
    public BigDecimal getJul_ju_vr() { return jul_ju_vr; }
    public String getJul_co_nm() { return jul_co_nm; }
    public BigDecimal getJul_co_vr() { return jul_co_vr; }
    public String getJul_ep_nm() { return jul_ep_nm; }
    public BigDecimal getJul_ep_vr() { return jul_ep_vr; }
    public BigDecimal getJul_m_tx() { return jul_m_tx; }
    public BigDecimal getJul_m_01() { return jul_m_01; }
    public BigDecimal getJul_m_02() { return jul_m_02; }
    public BigDecimal getJul_m_03() { return jul_m_03; }
    
    public String getAgo_al_nm() { return ago_al_nm; }
    public BigDecimal getAgo_al_vr() { return ago_al_vr; }
    public String getAgo_cm_nm() { return ago_cm_nm; }
    public BigDecimal getAgo_cm_vr() { return ago_cm_vr; }
    public String getAgo_ir_nm() { return ago_ir_nm; }
    public BigDecimal getAgo_ir_vr() { return ago_ir_vr; }
    public BigDecimal getAgo_m_dc() { return ago_m_dc; }
    public BigDecimal getAgo_m_df() { return ago_m_df; }
    public String getAgo_mu_nm() { return ago_mu_nm; }
    public BigDecimal getAgo_mu_vr() { return ago_mu_vr; }
    public String getAgo_ju_nm() { return ago_ju_nm; }
    public BigDecimal getAgo_ju_vr() { return ago_ju_vr; }
    public String getAgo_co_nm() { return ago_co_nm; }
    public BigDecimal getAgo_co_vr() { return ago_co_vr; }
    public String getAgo_ep_nm() { return ago_ep_nm; }
    public BigDecimal getAgo_ep_vr() { return ago_ep_vr; }
    public BigDecimal getAgo_m_tx() { return ago_m_tx; }
    public BigDecimal getAgo_m_01() { return ago_m_01; }
    public BigDecimal getAgo_m_02() { return ago_m_02; }
    public BigDecimal getAgo_m_03() { return ago_m_03; }
    
    public String getSet_al_nm() { return set_al_nm; }
    public BigDecimal getSet_al_vr() { return set_al_vr; }
    public String getSet_cm_nm() { return set_cm_nm; }
    public BigDecimal getSet_cm_vr() { return set_cm_vr; }
    public String getSet_ir_nm() { return set_ir_nm; }
    public BigDecimal getSet_ir_vr() { return set_ir_vr; }
    public BigDecimal getSet_m_dc() { return set_m_dc; }
    public BigDecimal getSet_m_df() { return set_m_df; }
    public String getSet_mu_nm() { return set_mu_nm; }
    public BigDecimal getSet_mu_vr() { return set_mu_vr; }
    public String getSet_ju_nm() { return set_ju_nm; }
    public BigDecimal getSet_ju_vr() { return set_ju_vr; }
    public String getSet_co_nm() { return set_co_nm; }
    public BigDecimal getSet_co_vr() { return set_co_vr; }
    public String getSet_ep_nm() { return set_ep_nm; }
    public BigDecimal getSet_ep_vr() { return set_ep_vr; }
    public BigDecimal getSet_m_tx() { return set_m_tx; }
    public BigDecimal getSet_m_01() { return set_m_01; }
    public BigDecimal getSet_m_02() { return set_m_02; }
    public BigDecimal getSet_m_03() { return set_m_03; }
    
    public String getOut_al_nm() { return out_al_nm; }
    public BigDecimal getOut_al_vr() { return out_al_vr; }
    public String getOut_cm_nm() { return out_cm_nm; }
    public BigDecimal getOut_cm_vr() { return out_cm_vr; }
    public String getOut_ir_nm() { return out_ir_nm; }
    public BigDecimal getOut_ir_vr() { return out_ir_vr; }
    public BigDecimal getOut_m_dc() { return out_m_dc; }
    public BigDecimal getOut_m_df() { return out_m_df; }
    public String getOut_mu_nm() { return out_mu_nm; }
    public BigDecimal getOut_mu_vr() { return out_mu_vr; }
    public String getOut_ju_nm() { return out_ju_nm; }
    public BigDecimal getOut_ju_vr() { return out_ju_vr; }
    public String getOut_co_nm() { return out_co_nm; }
    public BigDecimal getOut_co_vr() { return out_co_vr; }
    public String getOut_ep_nm() { return out_ep_nm; }
    public BigDecimal getOut_ep_vr() { return out_ep_vr; }
    public BigDecimal getOut_m_tx() { return out_m_tx; }
    public BigDecimal getOut_m_01() { return out_m_01; }
    public BigDecimal getOut_m_02() { return out_m_02; }
    public BigDecimal getOut_m_03() { return out_m_03; }
    
    public String getNov_al_nm() { return nov_al_nm; }
    public BigDecimal getNov_al_vr() { return nov_al_vr; }
    public String getNov_cm_nm() { return nov_cm_nm; }
    public BigDecimal getNov_cm_vr() { return nov_cm_vr; }
    public String getNov_ir_nm() { return nov_ir_nm; }
    public BigDecimal getNov_ir_vr() { return nov_ir_vr; }
    public BigDecimal getNov_m_dc() { return nov_m_dc; }
    public BigDecimal getNov_m_df() { return nov_m_df; }
    public String getNov_mu_nm() { return nov_mu_nm; }
    public BigDecimal getNov_mu_vr() { return nov_mu_vr; }
    public String getNov_ju_nm() { return nov_ju_nm; }
    public BigDecimal getNov_ju_vr() { return nov_ju_vr; }
    public String getNov_co_nm() { return nov_co_nm; }
    public BigDecimal getNov_co_vr() { return nov_co_vr; }
    public String getNov_ep_nm() { return nov_ep_nm; }
    public BigDecimal getNov_ep_vr() { return nov_ep_vr; }
    public BigDecimal getNov_m_tx() { return nov_m_tx; }
    public BigDecimal getNov_m_01() { return nov_m_01; }
    public BigDecimal getNov_m_02() { return nov_m_02; }
    public BigDecimal getNov_m_03() { return nov_m_03; }
    
    public String getDez_al_nm() { return dez_al_nm; }
    public BigDecimal getDez_al_vr() { return dez_al_vr; }
    public String getDez_cm_nm() { return dez_cm_nm; }
    public BigDecimal getDez_cm_vr() { return dez_cm_vr; }
    public String getDez_ir_nm() { return dez_ir_nm; }
    public BigDecimal getDez_ir_vr() { return dez_ir_vr; }
    public BigDecimal getDez_m_dc() { return dez_m_dc; }
    public BigDecimal getDez_m_df() { return dez_m_df; }
    public String getDez_mu_nm() { return dez_mu_nm; }
    public BigDecimal getDez_mu_vr() { return dez_mu_vr; }
    public String getDez_ju_nm() { return dez_ju_nm; }
    public BigDecimal getDez_ju_vr() { return dez_ju_vr; }
    public String getDez_co_nm() { return dez_co_nm; }
    public BigDecimal getDez_co_vr() { return dez_co_vr; }
    public String getDez_ep_nm() { return dez_ep_nm; }
    public BigDecimal getDez_ep_vr() { return dez_ep_vr; }
    public BigDecimal getDez_m_tx() { return dez_m_tx; }
    public BigDecimal getDez_m_01() { return dez_m_01; }
    public BigDecimal getDez_m_02() { return dez_m_02; }
    public BigDecimal getDez_m_03() { return dez_m_03; }
}
