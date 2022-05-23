package Classes;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 24/03/17.
 */
public class pUnidades {
    private SimpleStringProperty estacao;
    private SimpleStringProperty basedados;
    private SimpleBooleanProperty senha;

    public pUnidades(String estacao, String basedados, boolean senha) {
        this.estacao = new SimpleStringProperty(estacao);
        this.basedados = new SimpleStringProperty(basedados);
        this.senha = new SimpleBooleanProperty(senha);
    }

    public String getEstacao() { return estacao.get(); }
    public SimpleStringProperty estacaoProperty() { return estacao; }
    public void setEstacao(String estacao) { this.estacao.set(estacao); }

    public String getBasedados() { return basedados.get(); }
    public SimpleStringProperty basedadosProperty() { return basedados; }
    public void setBasedados(String basedados) { this.basedados.set(basedados); }

    public boolean isSenha() { return senha.get(); }
    public SimpleBooleanProperty senhaProperty() { return senha; }
    public void setSenha(boolean senha) { this.senha.set(senha); }
}
