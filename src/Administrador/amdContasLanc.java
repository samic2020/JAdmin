package Administrador;

import Funcoes.DbMain;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class amdContasLanc implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField codigo;
    @FXML private TextField descricao;
    @FXML private Button btnLancar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.maxField(codigo, 3);
        MaskFieldUtil.maxField(descricao, 60);

        codigo.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                // LostFocus
                Object[][] _codigo = null;
                try {
                    _codigo = conn.LerCamposTabela(new String[] {"Upper(codigo) AS codigo"},"adm_contas","Upper(codigo) Like '%" + codigo.getText().trim().toUpperCase() + "%'");
                } catch (SQLException e) { }
                if (_codigo != null) {
                    Alert alt = new Alert(Alert.AlertType.INFORMATION);
                    alt.setTitle("Atenção"); alt.setHeaderText("Codigo já existe!");
                    alt.showAndWait();
                    codigo.requestFocus();
                }
            }
        });

        btnLancar.setOnAction(event -> {
            Object[][] _codigo = null;
            try {
                _codigo = conn.LerCamposTabela(new String[] {"Upper(codigo) AS codigo"},"adm_contas","Upper(codigo) Like '%" + codigo.getText().trim().toUpperCase() + "%'");
            } catch (SQLException e) { }
            if (_codigo != null) {
                Alert alt = new Alert(Alert.AlertType.INFORMATION);
                alt.setTitle("Atenção"); alt.setHeaderText("Codigo já existe!");
                alt.showAndWait();
                codigo.requestFocus();
                return;
            }

            String sql = "INSERT INTO adm_contas (codigo, descricao) VALUES ('%s','%s');";
            sql = String.format(sql, codigo.getText().trim().toUpperCase(), descricao.getText().trim().toUpperCase());
            if (conn.ExecutarComando(sql) <= 0) {
                Alert alt = new Alert(Alert.AlertType.INFORMATION);
                alt.setTitle("Atenção"); alt.setHeaderText("Não Consegui gravar!\nTente Novamente...");
                alt.showAndWait();
                codigo.requestFocus();
                return;
            }
            ((FXInternalWindow) anchorPane.getParent().getParent().getParent()).close();
        });

        Platform.runLater(() -> codigo.requestFocus());
    }
}
