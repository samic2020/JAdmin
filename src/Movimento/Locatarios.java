package Movimento;

import javafx.beans.property.SimpleStringProperty;

public class Locatarios {
    private SimpleStringProperty contrato;
    private SimpleStringProperty razao;
    private SimpleStringProperty fantasia;
    private SimpleStringProperty cpfcnpj;
    private SimpleStringProperty endereco;
    private SimpleStringProperty numero;
    private SimpleStringProperty complemento;
    private SimpleStringProperty bairro;
    private SimpleStringProperty cidade;
    private SimpleStringProperty estado;
    private SimpleStringProperty cep;
    private SimpleStringProperty telefone;
    private SimpleStringProperty email;
    private SimpleStringProperty envio;

    public Locatarios(String contrato, String razao, String fantasia, String cpfcnpj, String endereco, String numero, String complemento, String bairro, String cidade, String estado, String cep, String telefone, String email, String envio) {
        this.contrato = new SimpleStringProperty(contrato);
        this.razao = new SimpleStringProperty(razao);
        this.fantasia = new SimpleStringProperty(fantasia);
        this.cpfcnpj = new SimpleStringProperty(cpfcnpj);
        this.endereco = new SimpleStringProperty(endereco);
        this.numero = new SimpleStringProperty(numero);
        this.complemento = new SimpleStringProperty(complemento);
        this.bairro = new SimpleStringProperty(bairro);
        this.cidade = new SimpleStringProperty(cidade);
        this.estado = new SimpleStringProperty(estado);
        this.cep = new SimpleStringProperty(cep);
        this.telefone = new SimpleStringProperty(telefone);
        this.email = new SimpleStringProperty(email);
        this.envio = new SimpleStringProperty(envio);
    }

    public String getContrato() { return contrato.get(); }
    public SimpleStringProperty contratoProperty() { return contrato; }
    public void setContrato(String contrato) { this.contrato.set(contrato); }

    public String getRazao() { return razao.get(); }
    public SimpleStringProperty razaoProperty() { return razao; }
    public void setRazao(String razao) { this.razao.set(razao); }

    public String getFantasia() { return fantasia.get(); }
    public SimpleStringProperty fantasiaProperty() { return fantasia; }
    public void setFantasia(String fantasia) { this.fantasia.set(fantasia); }

    public String getCpfcnpj() { return cpfcnpj.get(); }
    public SimpleStringProperty cpfcnpjProperty() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj.set(cpfcnpj); }

    public String getEndereco() { return endereco.get(); }
    public SimpleStringProperty enderecoProperty() { return endereco; }
    public void setEndereco(String endereco) { this.endereco.set(endereco); }

    public String getNumero() { return numero.get(); }
    public SimpleStringProperty numeroProperty() { return numero; }
    public void setNumero(String numero) { this.numero.set(numero); }

    public String getComplemento() { return complemento.get(); }
    public SimpleStringProperty complementoProperty() { return complemento; }
    public void setComplemento(String complemento) { this.complemento.set(complemento); }

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

    public String getTelefone() {
        String retorno = "";
        String[] tels = null;
        try { tels = telefone.get().split(";");} catch (Exception e) {}
        if (tels != null || tels.length != 0 ) {
            if (!tels[0].toString().equalsIgnoreCase("")) retorno = "(" + tels[0].substring(0,3) + ") " + tels[0].substring(3,13);
        }
        return retorno;
    }
    public SimpleStringProperty telefoneProperty() { return telefone; }
    public void setTelefone(String telefone) { this.telefone.set(telefone); }

    public String getEmail() {
        String retorno = "";
        String[] emails = null;
        try { emails = email.get().split(";");} catch (Exception e) {}
        if (emails != null) retorno = emails[0];
        return retorno;
    }
    public SimpleStringProperty emailProperty() { return email; }
    public void setEmail(String email) { this.email.set(email); }

    public String getEnvio() { return envio.get(); }
    public SimpleStringProperty envioProperty() { return envio; }
    public void setEnvio(String envio) { this.envio.set(envio); }
}
