package Administrador;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 18/01/17.
 */
public class BancoBoleta {
    SimpleIntegerProperty id;
    SimpleStringProperty banco;
    SimpleStringProperty agencia;
    SimpleIntegerProperty agenciadv;
    SimpleStringProperty conta;
    SimpleIntegerProperty contadv;
    SimpleStringProperty cedente;
    SimpleIntegerProperty cedentedv;
    SimpleStringProperty carteira;
    SimpleStringProperty tarifa;
    SimpleDoubleProperty nnumero;
    SimpleDoubleProperty nnumerotam;
    SimpleIntegerProperty lote;
    SimpleStringProperty crtfile;
    SimpleStringProperty keyfile;
    SimpleStringProperty path;

    public BancoBoleta(
            Integer id,
            String banco,
            String agencia,
            int agenciadv,
            String conta,
            int contadv,
            String cedente,
            int cedentedv,
            String carteira,
            String tarifa,
            double nnumero,
            double nnumerotam,
            int lote,
            String crtfile,
            String keyfile,
            String path
    ) {
        this.id = new SimpleIntegerProperty(id);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.agenciadv = new SimpleIntegerProperty(agenciadv);
        this.conta = new SimpleStringProperty(conta);
        this.contadv = new SimpleIntegerProperty(contadv);
        this.cedente = new SimpleStringProperty(cedente);
        this.cedentedv = new SimpleIntegerProperty(cedentedv);
        this.carteira = new SimpleStringProperty(carteira);
        this.tarifa = new SimpleStringProperty(tarifa);
        this.nnumero = new SimpleDoubleProperty(nnumero);
        this.nnumerotam = new SimpleDoubleProperty(nnumerotam);
        this.lote = new SimpleIntegerProperty(lote);
        this.crtfile = new SimpleStringProperty(crtfile);
        this.keyfile = new SimpleStringProperty(keyfile);
        this.path = new SimpleStringProperty(path);
    }

    public BancoBoleta(
            Integer id,
            String banco,
            String agencia,
            int agenciadv,
            String conta,
            int contadv,
            String cedente,
            int cedentedv,
            String carteira,
            String tarifa,
            double nnumero,
            double nnumerotam,
            int lote
    ) {
        this.id = new SimpleIntegerProperty(id);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.agenciadv = new SimpleIntegerProperty(agenciadv);
        this.conta = new SimpleStringProperty(conta);
        this.contadv = new SimpleIntegerProperty(contadv);
        this.cedente = new SimpleStringProperty(cedente);
        this.cedentedv = new SimpleIntegerProperty(cedentedv);
        this.carteira = new SimpleStringProperty(carteira);
        this.tarifa = new SimpleStringProperty(tarifa);
        this.nnumero = new SimpleDoubleProperty(nnumero);
        this.nnumerotam = new SimpleDoubleProperty(nnumerotam);
        this.lote = new SimpleIntegerProperty(lote);
    }

    public BancoBoleta(
            Integer id,
            String banco,
            String agencia,
            int agenciadv,
            String conta,
            String cedente,
            String carteira,
            String crtfile,
            String keyfile,
            String path
    ) {
        this.id = new SimpleIntegerProperty(id);
        this.banco = new SimpleStringProperty(banco);
        this.agencia = new SimpleStringProperty(agencia);
        this.agenciadv = new SimpleIntegerProperty(agenciadv);
        this.conta = new SimpleStringProperty(conta);
        this.cedente = new SimpleStringProperty(cedente);
        this.carteira = new SimpleStringProperty(carteira);
        this.crtfile = new SimpleStringProperty(crtfile);
        this.keyfile = new SimpleStringProperty(keyfile);
        this.path = new SimpleStringProperty(path);
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

    public String getCedente() { return cedente.get(); }
    public SimpleStringProperty cedenteProperty() { return cedente; }
    public void setCedente(String cedente) { this.cedente.set(cedente); }

    public String getCarteira() { return carteira.get(); }
    public SimpleStringProperty carteiraProperty() { return carteira; }
    public void setCarteira(String carteira) { this.carteira.set(carteira); }

    public String getTarifa() { return tarifa.get(); }
    public SimpleStringProperty tarifaProperty() { return tarifa; }
    public void setTarifa(String tarifa) { this.tarifa.set(tarifa); }

    public double getNnumero() { return nnumero.get(); }
    public SimpleDoubleProperty nnumeroProperty() { return nnumero; }
    public void setNnumero(double nnumero) { this.nnumero.set(nnumero); }

    public int getAgenciadv() { return agenciadv.get(); }
    public SimpleIntegerProperty agenciadvProperty() { return agenciadv; }

    public void setAgenciadv(int agenciadv) { this.agenciadv.set(agenciadv); }
    public int getContadv() { return contadv.get(); }

    public SimpleIntegerProperty contadvProperty() { return contadv; }
    public void setContadv(int contadv) { this.contadv.set(contadv); }

    public int getCedentedv() { return cedentedv.get(); }
    public SimpleIntegerProperty cedentedvProperty() { return cedentedv; }

    public void setCedentedv(int cedentedv) { this.cedentedv.set(cedentedv); }
    public double getNnumerotam() { return nnumerotam.get(); }

    public SimpleDoubleProperty nnumerotamProperty() { return nnumerotam; }
    public void setNnumerotam(double nnumerotam) { this.nnumerotam.set(nnumerotam); }

    public int getLote() { return lote.get(); }
    public SimpleIntegerProperty loteProperty() { return lote; }
    public void setLote(int lote) { this.lote.set(lote); }

    public String getCrtfile() {return crtfile.get();}
    public SimpleStringProperty crtfileProperty() {return crtfile;}
    public void setCrtfile(String crtfile) {this.crtfile.set(crtfile);}

    public String getKeyfile() {return keyfile.get();}
    public SimpleStringProperty keyfileProperty() {return keyfile;}
    public void setKeyfile(String keyfile) {this.keyfile.set(keyfile);}

    public String getPath() {return path.get();}
    public SimpleStringProperty pathProperty() {return path;}
    public void setPath(String path) {this.path.set(path);}
}
