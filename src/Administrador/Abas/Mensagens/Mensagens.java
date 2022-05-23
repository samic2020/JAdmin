package Administrador.Abas.Mensagens;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Mensagens implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    // Mensagens
    @FXML private TextArea mp_msg;
    @FXML private Button mp_btexcluir;
    @FXML private TextArea ml_msg;
    @FXML private Button ml_btexcluir;

    private String campo = "";
    private String newValue = "";
    private String oldValue = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initializa_ms();
    }

    private void Initializa_ms() {
        try { mp_msg.setText(conn.LerParametros("mp_msg")); } catch (SQLException e) { } catch (NullPointerException e) { }
        try { ml_msg.setText(conn.LerParametros("ml_msg")); } catch (SQLException e) { } catch (NullPointerException e) { }

        mp_msg.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = mp_msg.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"mp_msg", mp_msg.getText().trim(), "texto"});
            } catch (SQLException e) { } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        mp_msg.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "mp_msg";
            this.newValue = newValue;
        }));
        mp_btexcluir.setOnAction(event -> {
            mp_msg.setText("");
            conn.ExecutarComando("DELETE FROM parametros WHERE campo = 'mp_msg';");
        });

        ml_msg.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = ml_msg.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"ml_msg", ml_msg.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        ml_msg.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_fanta";
            this.newValue = newValue;
        }));
        ml_btexcluir.setOnAction(event -> {
            ml_msg.setText("");
            conn.ExecutarComando("DELETE FROM parametros WHERE campo = 'ml_msg';");
        });
    }

    private void auditora() {
    }

    private void auditora(String taxa) {
        //System.out.println(taxa + " foi alterada por " + VariaveisGlobais.usuario.toLowerCase().trim());
    }

}
