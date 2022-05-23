package entrada.Acesso;

import javafx.beans.property.SimpleStringProperty;

public class cUser {
    private SimpleStringProperty cod;
    private SimpleStringProperty nome;
    private SimpleStringProperty cargo;
    private SimpleStringProperty protocolo;

    public cUser(String cod, String nome, String cargo, String protocolo) {
        this.cod = new SimpleStringProperty(cod);
        this.nome = new SimpleStringProperty(nome);
        this.cargo = new SimpleStringProperty(cargo);
        this.protocolo = new SimpleStringProperty(protocolo);
    }

    public String getCod() { return cod.get(); }
    public SimpleStringProperty codProperty() { return cod; }
    public void setCod(String cod) { this.cod.set(cod); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getCargo() { return cargo.get(); }
    public SimpleStringProperty cargoProperty() { return cargo; }
    public void setCargo(String cargo) { this.cargo.set(cargo); }

    public String getProtocolo() { return protocolo.get(); }
    public SimpleStringProperty protocoloProperty() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo.set(protocolo); }

    @Override
    public String toString() {
        return cod.get() + " - " + nome.get();
    }
}
