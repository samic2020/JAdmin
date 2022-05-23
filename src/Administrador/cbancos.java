package Administrador;

/**
 * Created by supervisor on 19/01/17.
 */
public class cbancos {
    String numero;
    String nome;
    String site;
    int agenciatam;
    int contatam;
    int cedentetam;

    public cbancos(String numero, String nome) {
        this.numero = numero;
        this.nome = nome;
    }

    public cbancos(String numero, String nome, int agenciatam, int contatam, int cedentetam) {
        this.numero = numero;
        this.nome = nome;
        this.agenciatam = agenciatam;
        this.contatam = contatam;
        this.cedentetam = cedentetam;
    }

    public cbancos(String numero, String nome, String site) {
        this.numero = numero;
        this.nome = nome;
        this.site = site;
    }

    public cbancos(String numero, String nome, String site, int agenciatam, int contatam, int cedentetam) {
        this.numero = numero;
        this.nome = nome;
        this.site = site;
        this.agenciatam = agenciatam;
        this.contatam = contatam;
        this.cedentetam = cedentetam;
    }

    public int getAgenciatam() { return agenciatam; }
    public void setAgenciatam(int agenciatam) { this.agenciatam = agenciatam; }

    public int getContatam() { return contatam; }
    public void setContatam(int contatam) { this.contatam = contatam; }

    public int getCedentetam() { return cedentetam; }
    public void setCedentetam(int cedentetam) { this.cedentetam = cedentetam; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    @Override
    public String toString() {
        return numero + " - " + nome;
    }
}
