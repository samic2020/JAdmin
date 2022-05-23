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
public class classDescCod {
    private String codigo;
    private String descricao;

public String getCodigo() {
        return codigo;
    }

    public classDescCod(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
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

    @Override
    public String toString() {
        return descricao;
    }            
}
