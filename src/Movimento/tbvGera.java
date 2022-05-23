package Movimento;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 17/11/16.
 */
public class tbvGera {
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleStringProperty vencimento;
    private SimpleStringProperty valor;
    private SimpleStringProperty cota;
    private SimpleStringProperty refer;

    public tbvGera(String rgprp, String rgimv, String contrato, String nome, String vencimento, String valor, String cota, String refer) {
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.vencimento = new SimpleStringProperty(vencimento);
        this.valor = new SimpleStringProperty(valor);
        this.cota = new SimpleStringProperty(cota);
        this.refer = new SimpleStringProperty(refer);
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

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getVencimento() { return vencimento.get(); }
    public SimpleStringProperty vencimentoProperty() { return vencimento; }
    public void setVencimento(String vencimento) { this.vencimento.set(vencimento); }

    public String getValor() { return valor.get(); }
    public SimpleStringProperty valorProperty() { return valor; }
    public void setValor(String valor) { this.valor.set(valor); }

    public String getCota() { return cota.get(); }
    public SimpleStringProperty cotaProperty() { return cota; }
    public void setCota(String cota) { this.cota.set(cota); }

    public String getRefer() { return refer.get(); }
    public SimpleStringProperty referProperty() { return refer; }
    public void setRefer(String refer) { this.refer.set(refer); }

    @Override
    public String toString() {
        return "tbvGera{" +
                "contrato=" + contrato +
                ", nome=" + nome +
                ", vencimento=" + vencimento +
                '}';
    }
}
