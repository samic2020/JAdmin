package Administrador;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 24/03/17.
 */
public class cUnidades {
    private SimpleIntegerProperty id;
    private SimpleStringProperty dns;
    private SimpleStringProperty database;
    private SimpleBooleanProperty isSenha;

    public cUnidades(int id, String dns, String database, boolean isSenha) {
        this.id = new SimpleIntegerProperty(id);
        this.dns = new SimpleStringProperty(dns);
        this.database = new SimpleStringProperty(database);
        this.isSenha = new SimpleBooleanProperty(isSenha);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getDns() { return dns.get(); }
    public SimpleStringProperty dnsProperty() { return dns; }
    public void setDns(String dns) { this.dns.set(dns); }

    public String getDatabase() { return database.get(); }
    public SimpleStringProperty databaseProperty() { return database; }
    public void setDatabase(String database) { this.database.set(database); }

    public boolean isIsSenha() { return isSenha.get(); }
    public SimpleBooleanProperty isSenhaProperty() { return isSenha; }
    public void setIsSenha(boolean isSenha) { this.isSenha.set(isSenha); }
}
