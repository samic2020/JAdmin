package Relatorios.Termino;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class cTermino {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleStringProperty telef;
    private SimpleStringProperty dtinicio;
    private SimpleStringProperty dtfim;

    public cTermino(int id, String rgprp, String rgimv, String contrato, String nome, String telef, String dtinicio, String dtfim) {
        this.id = new SimpleIntegerProperty(id);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.telef = new SimpleStringProperty(telef);
        this.dtinicio = new SimpleStringProperty(dtinicio);
        this.dtfim = new SimpleStringProperty(dtfim);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

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

    public String getTelef() { return telef.get(); }
    public SimpleStringProperty telefProperty() { return telef; }
    public void setTelef(String telef) { this.telef.set(telef); }

    public String getDtinicio() { return dtinicio.get(); }
    public SimpleStringProperty dtinicioProperty() { return dtinicio; }
    public void setDtinicio(String dtinicio) { this.dtinicio.set(dtinicio); }

    public String getDtfim() { return dtfim.get(); }
    public SimpleStringProperty dtfimProperty() { return dtfim; }
    public void setDtfim(String dtfim) { this.dtfim.set(dtfim); }

    @Override
    public String toString() {
        return "cTermino{" +
                "id=" + id +
                ", rgprp=" + rgprp +
                ", rgimv=" + rgimv +
                ", contrato=" + contrato +
                ", nome=" + nome +
                ", telef=" + telef +
                ", DtInicio=" + dtinicio +
                ", DtTermino=" + dtfim +
                '}';
    }
}
