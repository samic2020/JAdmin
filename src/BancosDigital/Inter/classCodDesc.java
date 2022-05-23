/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

/**
 *
 * @author Samic
 */
public class classCodDesc {
    private String codigo;
    private String descricao;
    private String cpfcnpj;
    private String endereco;
    private String numero;
    private String complto;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String email;

    public classCodDesc(String codigo, String descricao, String cpfcnpj, String endereco, String numero, String complto, String bairro, String cidade, String estado, String cep, String email) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.cpfcnpj = cpfcnpj;
        this.endereco = endereco;
        this.numero = numero;
        this.complto = complto;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.email = email;
    }
        
    public classCodDesc(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplto() {
        return complto;
    }

    public void setComplto(String complto) {
        this.complto = complto;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return codigo;
    }        
}
