/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author desenvolvimento-pc
 */
public class BancosErros {
    private SimpleStringProperty contrato;
    private SimpleStringProperty nome;
    private SimpleStringProperty vencimento;
    private SimpleStringProperty codigo;
    private SimpleStringProperty message;

    public BancosErros(String contrato, String nome, String vencimento, String codigo, String message) {
        this.contrato = new SimpleStringProperty(contrato);
        this.nome = new SimpleStringProperty(nome);
        this.vencimento = new SimpleStringProperty(vencimento);
        this.codigo = new SimpleStringProperty(codigo);
        this.message = new SimpleStringProperty(message);
    }

    public SimpleStringProperty getContrato() {
        return contrato;
    }

    public void setContrato(SimpleStringProperty contrato) {
        this.contrato = contrato;
    }

    public SimpleStringProperty getNome() {
        return nome;
    }

    public void setNome(SimpleStringProperty nome) {
        this.nome = nome;
    }

    public SimpleStringProperty getVencimento() {
        return vencimento;
    }

    public void setVencimento(SimpleStringProperty vencimento) {
        this.vencimento = vencimento;
    }
    
    public SimpleStringProperty getCodigo() {
        return codigo;
    }

    public void setCodigo(SimpleStringProperty codigo) {
        this.codigo = codigo;
    }

    public SimpleStringProperty getMessage() {
        return message;
    }

    public void setMessage(SimpleStringProperty message) {
        this.message = message;
    }   
}
