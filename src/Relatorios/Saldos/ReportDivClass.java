/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Relatorios.Saldos;

import java.math.BigDecimal;
import java.util.Date;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Samic
 */
public class ReportDivClass {
    private String registro;
    private String descricao;
    private int recibos;
    private Date data;
    private BigDecimal saldo;

    public ReportDivClass(String registro, String descricao, int recibos, Date data, BigDecimal saldo) {
        this.registro = registro;
        this.descricao = descricao;
        this.recibos = recibos;
        this.data = data;
        this.saldo = saldo;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getRecibos() {
        return recibos;
    }

    public void setRecibos(int recibos) {
        this.recibos = recibos;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "ReportDivClass{" + "registro=" + registro + ", descricao=" + descricao + ", recibos=" + recibos + ", data=" + data + ", saldo=" + saldo + '}';
    }    
}
