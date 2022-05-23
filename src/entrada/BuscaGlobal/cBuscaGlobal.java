package entrada.BuscaGlobal;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class cBuscaGlobal {
    private SimpleIntegerProperty id;
    private SimpleStringProperty rgprp;
    private SimpleStringProperty rgimv;
    private SimpleStringProperty contrato;
    private SimpleStringProperty cpfcnpj;
    private SimpleStringProperty nome;
    private SimpleStringProperty fantasia;
    private SimpleBooleanProperty ativo;

    public cBuscaGlobal(int id, String rgprp, String rgimv, String contrato, String cpfcnpj, String nome, String fantasia, boolean ativo) {
        this.id = new SimpleIntegerProperty(id);
        this.rgprp = new SimpleStringProperty(rgprp);
        this.rgimv = new SimpleStringProperty(rgimv);
        this.contrato = new SimpleStringProperty(contrato);
        this.cpfcnpj = new SimpleStringProperty(cpfcnpj);
        this.nome = new SimpleStringProperty(nome);
        this.fantasia = new SimpleStringProperty(fantasia);
        this.ativo = new SimpleBooleanProperty(ativo);
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

    public String getCpfcnpj() { return cpfcnpj.get(); }
    public SimpleStringProperty cpfcnpjProperty() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj.set(cpfcnpj); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getFantasia() { return fantasia.get(); }
    public SimpleStringProperty fantasiaProperty() { return fantasia; }
    public void setFantasia(String fantasia) { this.fantasia.set(fantasia); }

    public boolean isAtivo() { return ativo.get(); }
    public SimpleBooleanProperty ativoProperty() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo.set(ativo); }

    @Override
    public String toString() {
        return "cBuscaGlobal{" +
                "id=" + id +
                ", rgprp=" + rgprp +
                ", rgimv=" + rgimv +
                ", contrato=" + contrato +
                ", cpfcnpj=" + cpfcnpj +
                ", nome=" + nome +
                ", fantasia=" + fantasia +
                '}';
    }
}
