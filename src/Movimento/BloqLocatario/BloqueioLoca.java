package Movimento.BloqLocatario;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class BloqueioLoca {
    SimpleIntegerProperty Id;
    SimpleStringProperty Contrato;
    SimpleStringProperty Nome;
    SimpleObjectProperty<Date> Data;

    public BloqueioLoca(int id, String contrato, String nome, Date data) {
        Id = new SimpleIntegerProperty(id);
        Contrato = new SimpleStringProperty(contrato);
        Nome = new SimpleStringProperty(nome);
        Data = new SimpleObjectProperty<>(data);
    }

    public int getId() { return Id.get(); }
    public SimpleIntegerProperty idProperty() { return Id; }
    public void setId(int id) { this.Id.set(id); }

    public String getContrato() { return Contrato.get(); }
    public SimpleStringProperty contratoProperty() { return Contrato; }
    public void setContrato(String contrato) { this.Contrato.set(contrato); }

    public String getNome() { return Nome.get(); }
    public SimpleStringProperty nomeProperty() { return Nome; }
    public void setNome(String nome) { this.Nome.set(nome); }

    public Date getData() { return Data.get(); }
    public SimpleObjectProperty<Date> dataProperty() { return Data; }
    public void setData(Date data) { this.Data.set(data); }
}
