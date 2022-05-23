/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Auditor;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Samic
 */
public class Auditor implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    
    @FXML private AnchorPane anchorPane;
    @FXML private ComboBox<String> audUser;
    @FXML private DatePicker audDe;
    @FXML private DatePicker audAte;
    @FXML private Button btnListar;
    @FXML private TableView<AuditorClass> audLista;
    @FXML private TableColumn<AuditorClass, String> audListaUser;
    @FXML private TableColumn<AuditorClass, String> audListaDatahora;
    @FXML private TableColumn<AuditorClass, String> audListaVelho;
    @FXML private TableColumn<AuditorClass, String> audListaNovo;
    @FXML private TableColumn<AuditorClass, String> audListaMaquina;    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        audDe.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        audAte.setValue(Dates.toLocalDate(Dates.ultDataMes(DbMain.getDateTimeServer())));
        
        FillUsers();
        
        btnListar.setOnAction((event) -> {
            SetDisplayAuditor();
        });
    }
    
    private void SetDisplayAuditor() {
        audListaUser.setCellValueFactory(new PropertyValueFactory("usuario"));
        audListaUser.setStyle("-fx-alignment: CENTER;");
        audListaDatahora.setCellValueFactory(new PropertyValueFactory("datahora"));
        audListaDatahora.setStyle("-fx-alignment: CENTER;");
        audListaVelho.setCellValueFactory(new PropertyValueFactory("velho"));
        audListaVelho.setStyle("-fx-alignment: LEFT;");
        audListaNovo.setCellValueFactory(new PropertyValueFactory("novo"));
        audListaNovo.setStyle("-fx-alignment: LEFT;");
        audListaMaquina.setCellValueFactory(new PropertyValueFactory("maquina"));
        audListaMaquina.setStyle("-fx-alignment: CENTER;");

        List<AuditorClass> Lista = new ArrayList<AuditorClass>();
        String selectSQL = "SELECT usuario, datahora, velho, novo, maquina FROM auditor WHERE " + 
                (audUser.getSelectionModel().getSelectedIndex() == 0 ? "" : "usuario = '" + audUser.getSelectionModel().getSelectedItem().toString() + "' AND ") + 
                "(datahora between '" + Dates.DateFormata("yyyy-MM-dd", Dates.toDate(audDe.getValue())) + "' and '" + Dates.DateFormata("yyyy-MM-dd", Dates.toDate(audAte.getValue())) + "');";
        ResultSet rs = this.conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY);

        try {
            while (rs.next()) {
                Lista.add(new AuditorClass(
                        rs.getString("usuario"), 
                        rs.getString("datahora"),
                        rs.getString("velho"), 
                        rs.getString("novo"), 
                        rs.getString("maquina"))
                );
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        DbMain.FecharTabela(rs);        
        
        List<AuditorClass> data = new ArrayList<>();
        if (Lista.size() != 0) {
            for (AuditorClass item : Lista) {
                data.add(new AuditorClass(
                                item.getUsuario(),
                                item.getDatahora(),
                                item.getVelho(),
                                item.getNovo(),
                                item.getMaquina()
                        )
                );
            }
        }
        audLista.setItems(FXCollections.observableArrayList(data));
    }
    
    private void FillUsers() {
        String sSql = "SELECT DISTINCT usuario FROM auditor ORDER BY usuario;";

        audUser.getItems().removeAll();
        ResultSet rs = this.conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);

        try {
            audUser.getItems().add("Todos");
            while (rs.next()) {
                audUser.getItems().add(rs.getString("usuario"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DbMain.FecharTabela(rs);
        audUser.getSelectionModel().select(0);
    }
    
}
