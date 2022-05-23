package Classes;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Date;

public class cFiadores {
    private SimpleBooleanProperty fisjur;
    private SimpleStringProperty cpfcnpj;
    private SimpleStringProperty rginsc;
    private SimpleStringProperty nome;
    private SimpleStringProperty fantasia;
    private SimpleStringProperty sexo;
    private SimpleObjectProperty<Date> dtnasc;
    private SimpleObjectProperty<Date> dtctro;
    private SimpleStringProperty endereco;
    private SimpleStringProperty numero;
    private SimpleStringProperty cplto;
    private SimpleStringProperty bairro;
    private SimpleStringProperty cidade;
    private SimpleStringProperty estado;
    private SimpleStringProperty cep;
    private SimpleStringProperty nacinalidade;
    private SimpleStringProperty ecivil;
    private SimpleStringProperty mae;
    private SimpleStringProperty pai;

    public cFiadores(boolean fisjur, String cpfcnpj, String rginsc, String nome, String fantasia, String sexo, Date dtnasc, Date dtctro, String endereco, String numero, String cplto, String bairro, String cidade, String estado, String cep, String nacinalidade, String ecivil, String mae, String pai) {
        this.fisjur = new SimpleBooleanProperty(fisjur);
        this.cpfcnpj = new SimpleStringProperty(cpfcnpj);
        this.rginsc = new SimpleStringProperty(rginsc);
        this.nome = new SimpleStringProperty(nome);
        this.fantasia = new SimpleStringProperty(fantasia);
        this.sexo = new SimpleStringProperty(sexo);
        this.dtnasc = new SimpleObjectProperty<>(dtnasc);
        this.dtctro = new SimpleObjectProperty<>(dtctro);
        this.endereco = new SimpleStringProperty(endereco);
        this.numero = new SimpleStringProperty(numero);
        this.cplto = new SimpleStringProperty(cplto);
        this.bairro = new SimpleStringProperty(bairro);
        this.cidade = new SimpleStringProperty(cidade);
        this.estado = new SimpleStringProperty(estado);
        this.cep = new SimpleStringProperty(cep);
        this.nacinalidade = new SimpleStringProperty(nacinalidade);
        this.ecivil = new SimpleStringProperty(ecivil);
        this.mae = new SimpleStringProperty(mae);
        this.pai = new SimpleStringProperty(pai);
    }

    public boolean getFisjur() { return fisjur.get(); }
    public SimpleBooleanProperty fisjurProperty() { return fisjur; }
    public void setFisjur(boolean fisjur) { this.fisjur.set(fisjur); }

    public String getCpfcnpj() { return cpfcnpj.get(); }
    public SimpleStringProperty cpfcnpjProperty() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj.set(cpfcnpj); }

    public String getRginsc() { return rginsc.get(); }
    public SimpleStringProperty rginscProperty() { return rginsc; }
    public void setRginsc(String rginsc) { this.rginsc.set(rginsc); }

    public String getNome() { return nome.get(); }
    public SimpleStringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getFantasia() { return fantasia.get(); }
    public SimpleStringProperty fantasiaProperty() { return fantasia; }
    public void setFantasia(String fantasia) { this.fantasia.set(fantasia); }

    public String getSexo() { return sexo.get(); }
    public SimpleStringProperty sexoProperty() { return sexo; }
    public void setSexo(String sexo) { this.sexo.set(sexo); }

    public Date getDtnasc() { return dtnasc.get(); }
    public SimpleObjectProperty<Date> dtnascProperty() { return dtnasc; }
    public void setDtnasc(Date dtnasc) { this.dtnasc.set(dtnasc); }

    public Date getDtctro() { return dtctro.get(); }
    public SimpleObjectProperty<Date> dtctroProperty() { return dtctro; }
    public void setDtctro(Date dtctro) { this.dtctro.set(dtctro); }

    public String getEndereco() { return endereco.get(); }
    public SimpleStringProperty enderecoProperty() { return endereco; }
    public void setEndereco(String endereco) { this.endereco.set(endereco); }

    public String getNumero() { return numero.get(); }
    public SimpleStringProperty numeroProperty() { return numero; }
    public void setNumero(String numero) { this.numero.set(numero); }

    public String getCplto() { return cplto.get(); }
    public SimpleStringProperty cpltoProperty() { return cplto; }
    public void setCplto(String cplto) { this.cplto.set(cplto); }

    public String getBairro() { return bairro.get(); }
    public SimpleStringProperty bairroProperty() { return bairro; }
    public void setBairro(String bairro) { this.bairro.set(bairro); }

    public String getCidade() { return cidade.get(); }
    public SimpleStringProperty cidadeProperty() { return cidade; }
    public void setCidade(String cidade) { this.cidade.set(cidade); }

    public String getEstado() { return estado.get(); }
    public SimpleStringProperty estadoProperty() { return estado; }
    public void setEstado(String estado) { this.estado.set(estado); }

    public String getCep() { return cep.get(); }
    public SimpleStringProperty cepProperty() { return cep; }
    public void setCep(String cep) { this.cep.set(cep); }

    public String getNacinalidade() { return nacinalidade.get(); }
    public SimpleStringProperty nacinalidadeProperty() { return nacinalidade; }
    public void setNacinalidade(String nacinalidade) { this.nacinalidade.set(nacinalidade); }

    public String getEcivil() { return ecivil.get(); }
    public SimpleStringProperty ecivilProperty() { return ecivil; }
    public void setEcivil(String ecivil) { this.ecivil.set(ecivil); }

    public String getMae() { return mae.get(); }
    public SimpleStringProperty maeProperty() { return mae; }
    public void setMae(String mae) { this.mae.set(mae); }

    public String getPai() { return pai.get(); }
    public SimpleStringProperty paiProperty() { return pai; }
    public void setPai(String pai) { this.pai.set(pai); }
}
