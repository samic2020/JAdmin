package Movimento.Extrato;

import javafx.beans.property.SimpleStringProperty;

public class ExtratoBloqClass {
    private SimpleStringProperty tipo;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty nome;
    private SimpleStringProperty vecto;

    public ExtratoBloqClass(String tipo, String rgimv, String nome, String vecto) {
        this.tipo = new SimpleStringProperty(tipo);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.nome = new SimpleStringProperty(nome);
        this.vecto = new SimpleStringProperty(vecto);
    }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getRgimv() { return rgimv.get(); }
    public SimpleStringProperty rgimvProperty() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv.set(rgimv); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getVecto() { return vecto.get(); }
    public SimpleStringProperty vectoProperty() { return vecto; }
    public void setVecto(String vecto) { this.vecto.set(vecto); }

    @Override
    public String toString() {
        return "ExtratoBloqClass{" +
                "tipo=" + tipo +
                ", rgimv=" + rgimv +
                ", nome=" + nome +
                ", vecto=" + vecto +
                '}';
    }
}
