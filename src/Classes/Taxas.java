package Classes;

/**
 * Created by supervisor on 15/06/16.
 */
public class Taxas {
    int id;
    String codigo;
    String descricao;
    Boolean predesc;
    Boolean posdesc;
    Boolean retencao;
    Boolean extrato;

    @Override
    public String toString() {
        return this.codigo;
    }

    public Taxas(String codigo) {
        this.codigo = codigo;
    }

    public Taxas(int id, String codigo, String descricao, Boolean predesc, Boolean posdesc, Boolean retencao, Boolean extrato) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.predesc = predesc;
        this.posdesc = posdesc;
        this.retencao = retencao;
        this.extrato = extrato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Boolean getPredesc() {
        return predesc;
    }

    public void setPredesc(Boolean predesc) {
        this.predesc = predesc;
    }

    public Boolean getPosdesc() {
        return posdesc;
    }

    public void setPosdesc(Boolean posdesc) {
        this.posdesc = posdesc;
    }

    public Boolean getRetencao() {
        return retencao;
    }

    public void setRetencao(Boolean retencao) {
        this.retencao = retencao;
    }

    public Boolean getExtrato() {
        return extrato;
    }

    public void setExtrato(Boolean extrato) {
        this.extrato = extrato;
    }

}
