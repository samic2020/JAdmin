package Administrador;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 19/01/17.
 */
public class EmailAdm {
    SimpleIntegerProperty id;
    SimpleStringProperty email;
    SimpleStringProperty senha;
    SimpleStringProperty smtp;
    SimpleStringProperty porta;
    SimpleBooleanProperty autentica;
    SimpleBooleanProperty ssl;

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getEmail() { return email.get(); }
    public SimpleStringProperty emailProperty() { return email; }
    public void setEmail(String email) { this.email.set(email); }

    public String getSenha() { return senha.get(); }
    public SimpleStringProperty senhaProperty() { return senha; }
    public void setSenha(String senha) { this.senha.set(senha); }

    public String getSmtp() { return smtp.get(); }
    public SimpleStringProperty smtpProperty() { return smtp; }
    public void setSmtp(String smtp) { this.smtp.set(smtp); }

    public String getPorta() { return porta.get(); }
    public SimpleStringProperty portaProperty() { return porta; }
    public void setPorta(String porta) { this.porta.set(porta); }

    public boolean isAutentica() { return autentica.get(); }
    public SimpleBooleanProperty autenticaProperty() { return autentica; }
    public void setAutentica(boolean autentica) { this.autentica.set(autentica); }

    public boolean isSsl() { return ssl.get(); }
    public SimpleBooleanProperty sslProperty() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl.set(ssl); }

    public EmailAdm(Integer id, String email, String senha, String smtp, String porta, Boolean autentica, Boolean ssl) {
        this.id = new SimpleIntegerProperty(id);
        this.email = new SimpleStringProperty(email);
        this.senha = new SimpleStringProperty(senha);
        this.smtp = new SimpleStringProperty(smtp);
        this.porta = new SimpleStringProperty(porta);
        this.autentica = new SimpleBooleanProperty(autentica);
        this.ssl = new SimpleBooleanProperty(ssl);
    }
}
