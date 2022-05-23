/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Samic
 */
public class classBaixa {
    private String tipo;
    private String contrato;
    private String nome;
    private String emissao;
    private String vencimento;
    private String cpfcnpj;
    private String nnumero;
    private BigDecimal multa;
    private BigDecimal juros;
    private BigDecimal valor;
    private boolean tag;
    private BigInteger aut;
    
    public classBaixa(String tipo, String nome, String emissao, String vencimento, 
                      String cpfcnpj, String nnumero, BigDecimal multa, 
                      BigDecimal juros, BigDecimal valor, boolean tag) {
        this.tipo = tipo;
        this.nome = nome;
        this.emissao = emissao;
        this.vencimento = vencimento;
        this.cpfcnpj = cpfcnpj;
        this.nnumero = nnumero;
        this.multa = multa;
        this.juros = juros;
        this.valor = valor;
        this.tag = tag;
    }

    public classBaixa(String tipo, String contrato, String nome, String emissao, String vencimento, 
                      String cpfcnpj, String nnumero, BigDecimal valor, BigInteger aut) {
        this.tipo = tipo;
        this.contrato = contrato;
        this.nome = nome;
        this.emissao = emissao;
        this.vencimento = vencimento;
        this.cpfcnpj = cpfcnpj;
        this.nnumero = nnumero;
        this.valor = valor;
        this.aut = aut;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmissao() {
        return emissao;
    }

    public void setEmissao(String emissao) {
        this.emissao = emissao;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public String getNnumero() {
        return nnumero;
    }

    public void setNnumero(String nnumero) {
        this.nnumero = nnumero;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean getTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public BigInteger getAut() {
        return aut;
    }

    public void setAut(BigInteger aut) {
        this.aut = aut;
    }
    
    @Override
    public String toString() {
        return "classBaixa{" + "tipo=" + tipo + ", nome=" + nome + ", emissao=" + emissao + ", vencimento=" + vencimento + ", cpfcnpj=" + cpfcnpj + ", nnumero=" + nnumero + ", valor=" + valor + '}';
    }
        
}
