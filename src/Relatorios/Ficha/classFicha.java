/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Relatorios.Ficha;

/**
 *
 * @author Samic
 */
public class classFicha {
    private String rgprp;
    private String nome_prop;
    private String cpfcnpj_prop;
    private String rgimv;
    private String contrato;
    private String nome_loca;
    private String cpfcnpj_loca;
    private String end_imovel;
    private String tel_prop;
    private String nome_fiador1;
    private String tel_fiador1;
    private String nome_fiador2;
    private String tel_fiador2;
    private String obs_loca;
    private String dtinicio_cart;
    private String dttermino_cart;
    private String dtadito_cart;
    private double comissao;
    private double multa;

    public classFicha(String rgprp, String nome_prop, String cpfcnpj_prop, String rgimv, String contrato, String nome_loca, String cpfcnpj_loca, String end_imovel, String tel_prop, String nome_fiador1, String tel_fiador1, String nome_fiador2, String tel_fiador2, String obs_loca, String dtinicio_cart, String dttermino_cart, String dtadito_cart, double multa, double comissao) {
        this.rgprp = rgprp;
        this.nome_prop = nome_prop;
        this.cpfcnpj_prop = cpfcnpj_prop;
        this.rgimv = rgimv;
        this.contrato = contrato;
        this.nome_loca = nome_loca;
        this.cpfcnpj_loca = cpfcnpj_loca;
        this.end_imovel = end_imovel;
        this.tel_prop = tel_prop;
        this.nome_fiador1 = nome_fiador1;
        this.tel_fiador1 = tel_fiador1;
        this.nome_fiador2 = nome_fiador2;
        this.tel_fiador2 = tel_fiador2;
        this.obs_loca = obs_loca;
        this.dtinicio_cart = dtinicio_cart;
        this.dttermino_cart = dttermino_cart;
        this.dtadito_cart = dtadito_cart;
        this.multa = multa;
        this.comissao = comissao;
    }

    public String getRgprp() {
        return rgprp;
    }

    public void setRgprp(String rgprp) {
        this.rgprp = rgprp;
    }

    public String getNome_prop() {
        return nome_prop;
    }

    public void setNome_prop(String nome_prop) {
        this.nome_prop = nome_prop;
    }

    public String getCpfcnpj_prop() {
        return cpfcnpj_prop;
    }

    public void setCpfcnpj_prop(String cpfcnpj_prop) {
        this.cpfcnpj_prop = cpfcnpj_prop;
    }

    public String getRgimv() {
        return rgimv;
    }

    public void setRgimv(String rgimv) {
        this.rgimv = rgimv;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNome_loca() {
        return nome_loca;
    }

    public void setNome_loca(String nome_loca) {
        this.nome_loca = nome_loca;
    }

    public String getCpfcnpj_loca() {
        return cpfcnpj_loca;
    }

    public void setCpfcnpj_loca(String cpfcnpj_loca) {
        this.cpfcnpj_loca = cpfcnpj_loca;
    }

    public String getEnd_imovel() {
        return end_imovel;
    }

    public void setEnd_imovel(String end_imovel) {
        this.end_imovel = end_imovel;
    }

    public String getTel_prop() {
        return tel_prop;
    }

    public void setTel_prop(String tel_prop) {
        this.tel_prop = tel_prop;
    }

    public String getNome_fiador1() {
        return nome_fiador1;
    }

    public void setNome_fiador1(String nome_fiador1) {
        this.nome_fiador1 = nome_fiador1;
    }

    public String getTel_fiador1() {
        return tel_fiador1;
    }

    public void setTel_fiador1(String tel_fiador1) {
        this.tel_fiador1 = tel_fiador1;
    }

    public String getNome_fiador2() {
        return nome_fiador2;
    }

    public void setNome_fiador2(String nome_fiador2) {
        this.nome_fiador2 = nome_fiador2;
    }

    public String getTel_fiador2() {
        return tel_fiador2;
    }

    public void setTel_fiador2(String tel_fiador2) {
        this.tel_fiador2 = tel_fiador2;
    }

    public String getObs_loca() {
        return obs_loca;
    }

    public void setObs_loca(String obs_loca) {
        this.obs_loca = obs_loca;
    }

    public String getDtinicio_cart() {
        return dtinicio_cart;
    }

    public void setDtinicio_cart(String dtinicio_cart) {
        this.dtinicio_cart = dtinicio_cart;
    }

    public String getDttermino_cart() {
        return dttermino_cart;
    }

    public void setDttermino_cart(String dttermino_cart) {
        this.dttermino_cart = dttermino_cart;
    }

    public String getDtadito_cart() {
        return dtadito_cart;
    }

    public void setDtadito_cart(String dtadito_cart) {
        this.dtadito_cart = dtadito_cart;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }    
    
    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    @Override
    public String toString() {
        return contrato + " - " + nome_loca;
    }        
}
