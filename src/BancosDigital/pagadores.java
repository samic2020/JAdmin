package BancosDigital;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class pagadores {
    private SimpleIntegerProperty id;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleStringProperty vencto;
    private SimpleBooleanProperty bloq;

    public pagadores(int id, String contrato, String nome, String vencto, boolean bloq) {
        this.id = new SimpleIntegerProperty(id);
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.vencto = new SimpleStringProperty(vencto);
        this.bloq = new SimpleBooleanProperty(bloq);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getVencto() { return vencto.get(); }
    public SimpleStringProperty venctoProperty() { return vencto; }
    public void setVencto(String vencto) { this.vencto.set(vencto); }

    public boolean getBloq() { return bloq.get(); }
    public SimpleBooleanProperty bloqProperty() { return bloq; }
    public void setBloq(boolean bloq) { this.bloq.set(bloq); }
}
