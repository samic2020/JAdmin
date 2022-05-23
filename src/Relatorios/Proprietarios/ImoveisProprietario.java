package Relatorios.Proprietarios;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ImoveisProprietario {
    // Proprietarios
    private SimpleIntegerProperty rgprp;
    private SimpleStringProperty nome;
    private SimpleStringProperty tipo;

    private ImoveisImovel[] imovel;

    public ImoveisProprietario(int rgprp, String nome, String tipo) {
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.nome = new SimpleStringProperty(nome);
        this.tipo = new SimpleStringProperty(tipo);
    }

    public ImoveisProprietario(int rgprp, String nome, String tipo, ImoveisImovel[] imovel) {
        this.rgprp = new SimpleIntegerProperty(rgprp);
        this.nome = new SimpleStringProperty(nome);
        this.tipo = new SimpleStringProperty(tipo);
        this.imovel = imovel;
    }

    public int getRgprp() { return rgprp.get(); }
    public SimpleIntegerProperty rgprpProperty() { return rgprp; }
    public void setRgprp(int rgprp) { this.rgprp.set(rgprp); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public ImoveisImovel[] getImovel() { return imovel; }
    public void setImovel(ImoveisImovel[] imovel) { this.imovel = imovel; }
}
