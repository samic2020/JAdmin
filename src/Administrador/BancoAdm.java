package Administrador;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 19/01/17.
 */
public class BancoAdm {
    SimpleIntegerProperty id;
    SimpleStringProperty banco;
    SimpleStringProperty agencia;
    SimpleStringProperty conta;
    SimpleStringProperty tipo;
    SimpleStringProperty ted;
    SimpleStringProperty doc;
    SimpleStringProperty cheque;
    SimpleStringProperty transferencia;

    public BancoAdm(Integer id, String banco, String agencia, String conta, String tipo, String ted, String doc, String cheque, String transferencia) {
        this.id = new SimpleIntegerProperty(id);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.conta = new SimpleStringProperty(conta);
        this.tipo = new SimpleStringProperty(tipo);
        this.ted = new SimpleStringProperty(ted);
        this.doc = new SimpleStringProperty(doc);
        this.cheque = new SimpleStringProperty(cheque);
        this.transferencia = new SimpleStringProperty(transferencia);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public String getAgencia() { return agencia.get(); }
    public SimpleStringProperty agenciaProperty() { return agencia; }
    public void setAgencia(String agencia) { this.agencia.set(agencia); }

    public String getConta() { return conta.get(); }
    public SimpleStringProperty contaProperty() { return conta; }
    public void setConta(String conta) { this.conta.set(conta); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getTed() { return ted.get(); }
    public SimpleStringProperty tedProperty() { return ted; }
    public void setTed(String ted) { this.ted.set(ted); }

    public String getDoc() { return doc.get(); }
    public SimpleStringProperty docProperty() { return doc; }
    public void setDoc(String doc) { this.doc.set(doc); }

    public String getCheque() { return cheque.get(); }
    public SimpleStringProperty chequeProperty() { return cheque; }
    public void setCheque(String cheque) { this.cheque.set(cheque); }

    public String getTransferencia() { return transferencia.get(); }
    public SimpleStringProperty transferenciaProperty() { return transferencia; }
    public void setTransferencia(String transferencia) { this.transferencia.set(transferencia); }
}
