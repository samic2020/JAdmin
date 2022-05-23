package Movimento.BaixaManual;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class cBaixaManual {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleStringProperty banco;
    private SimpleStringProperty identificador;
    private SimpleObjectProperty<Date> vencto;
    private SimpleBooleanProperty tag;

    public cBaixaManual(int id, String rgprp, String rgimv, String contrato, String nome, String banco, String identificador, Date vencto, boolean tag) {
        this.id = new SimpleIntegerProperty(id);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.banco = new SimpleStringProperty(banco);
        this.identificador = new SimpleStringProperty(identificador);
        this.vencto = new SimpleObjectProperty<>(vencto);
        this.tag = new SimpleBooleanProperty(tag);
    }

    public cBaixaManual(int id, String rgprp, String rgimv, String contrato, String nome, String banco, String identificador, Date vencto) {
        this.id = new SimpleIntegerProperty(id);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.banco = new SimpleStringProperty(banco);
        this.identificador = new SimpleStringProperty(identificador);
        this.vencto = new SimpleObjectProperty<>(vencto);
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

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getIdentificador() { return identificador.get(); }
    public SimpleStringProperty identificadorProperty() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador.set(identificador); }

    public Date getVencto() { return vencto.get(); }
    public SimpleObjectProperty<Date> venctoProperty() { return vencto; }
    public void setVencto(Date vencto) { this.vencto.set(vencto); }

    public boolean isTag() { return tag.get(); }
    public SimpleBooleanProperty tagProperty() { return tag; }
    public void setTag(boolean tag) { this.tag.set(tag); }

    @Override
    public String toString() {
        return "cBaixaManual{" +
                "id=" + id +
                ", contrato=" + contrato +
                ", nome=" + nome +
                ", identificador=" + identificador +
                ", tag=" + tag +
                '}';
    }
}
