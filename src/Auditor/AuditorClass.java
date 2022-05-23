/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auditor;

/**
 *
 * @author Samic
 */
public class AuditorClass {
    private String usuario;
    private String datahora;
    private String velho;
    private String novo;
    private String maquina;

    public AuditorClass(String usuario, String datahora, String velho, String novo, String maquina) {
        this.usuario = usuario;
        this.datahora = datahora;
        this.velho = velho;
        this.novo = novo;
        this.maquina = maquina;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }

    public String getVelho() {
        return velho;
    }

    public void setVelho(String velho) {
        this.velho = velho;
    }

    public String getNovo() {
        return novo;
    }

    public void setNovo(String novo) {
        this.novo = novo;
    }

    public String getMaquina() {
        return maquina;
    }

    public void setMaquina(String maquina) {
        this.maquina = maquina;
    }

    @Override
    public String toString() {
        return "AuditorClass{" + "usuario=" + usuario + ", datahora=" + datahora + ", velho=" + velho + ", novo=" + novo + ", maquina=" + maquina + '}';
    }        
}