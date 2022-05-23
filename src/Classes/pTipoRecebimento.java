/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableList;

/**
 *
 * @author supervisor
 */
public class pTipoRecebimento {
    DbMain conn = VariaveisGlobais.conexao;
    ResultSet rs = null;

    public ObservableList<String> TipoRecebimento() {
        List<String> list = new ArrayList<>();
        list.add("Recibo");

        rs = conn.AbrirTabela("SELECT numero, nome FROM bancos ORDER BY numero;", ResultSet.CONCUR_READ_ONLY);
        try {
            while  (rs.next()) {
                list.add(rs.getString("numero"));
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);

        ObservableList<String> observableList = observableList(list);
        return observableList;
    }
}
