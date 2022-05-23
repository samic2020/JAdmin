package Bancos;

public class Pagador extends PagadorRecibo {
    private int Banco;
    private String Codigo;
    private String Razao;
    private String Fantasia;
    private String CNPJ;
    private String Endereco;
    private String Numero;
    private String Complto;
    private String Bairro;
    private String Cidade;
    private String Estado;
    private String Cep;
    private String Telefone;
    private String Email;
    private String envio;

    public int getBanco() { return Banco; }

    public void setBanco(int banco) { Banco = banco; }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getRazao() {
        return Razao;
    }

    public void setRazao(String razao) {
        Razao = razao;
    }

    public String getFantasia() {
        return Fantasia;
    }

    public void setFantasia(String fantasia) {
        Fantasia = fantasia;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public String getEndereco() {
        return Endereco;
    }

    public void setEndereco(String endereco) {
        Endereco = endereco;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getComplto() {
        return Complto;
    }

    public void setComplto(String complto) {
        Complto = complto;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getCep() {
        return Cep;
    }

    public void setCep(String cep) {
        Cep = cep;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) { Telefone = telefone; }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setEnvio(String envio) { this.envio = envio; }

    public String getEnvio() { return envio; }
}
