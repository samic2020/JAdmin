package Movimento.Bloqueio;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class BloqueiosController implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private Pane panel;
    @FXML private TextField registro;
    @FXML private Label nome;
    @FXML private TableView<BloqClass> lista;
    @FXML private TableColumn<BloqClass, Integer> id;
    @FXML private TableColumn<BloqClass, Boolean> tag;
    @FXML private TableColumn<BloqClass, String> tipo;
    @FXML private TableColumn<BloqClass, String> nomes;
    @FXML private TableColumn<BloqClass, String> vecto;
    @FXML private TableColumn<BloqClass, String> valor;

    DbMain conn = VariaveisGlobais.conexao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() ->  registro.requestFocus());
        registro.focusedProperty().addListener((observable, oldValue, newValue) -> {if (newValue) lista.setItems(null);});
        registro.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (registro.getText().trim().equalsIgnoreCase("")) {
                    registro.setText(""); registro.requestFocus(); return;
                }

                Boolean isProp = false;
                String sql = "SELECT * FROM proprietarios WHERE (exclusao is null) and p_rgprp = '%s';";
                sql = String.format(sql, registro.getText().trim());
                ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
                try {
                    while (rs.next()) {
                        isProp = true;
                        nome.setText(rs.getString("p_nome"));
                    }
                } catch (SQLException e) {}
                try { DbMain.FecharTabela(rs); } catch (Exception e) {}

                String mSql = "select * from movimento WHERE aut_rec <> 0 AND '%s' = ANY (aut_pag) order by 2,3,4,7,9";
                mSql = String.format(mSql, registro.getText());
                ResultSet mrs = conn.AbrirTabela(mSql, ResultSet.CONCUR_READ_ONLY);
                ObservableList<BloqClass> data = FXCollections.observableArrayList();
                try {
                    while (mrs.next()) {
                        Integer id = null;
                        Boolean tag = false;
                        String tipo = "R";
                        String nomes = null;
                        String vencto = null;
                        String valor = "0,00";

                        try {id = mrs.getInt("id");} catch (SQLException e) {}
                        try {tag = mrs.getString("bloqueio") != null ? true : false;} catch (SQLException e) {}
                        try {nomes = conn.LerCamposTabela(new String[] {"l_f_nome"}, "locatarios", "l_contrato = '" + mrs.getString("contrato") + "'")[0][3].toString();} catch (SQLException e) {}
                        try {vencto = new SimpleDateFormat("dd/MM/yyyy").format(mrs.getDate("dtvencimento"));} catch (SQLException e) {}
                        try {valor = LerValor.BigDecimalToCurrency(mrs.getBigDecimal("mensal"));} catch (SQLException e) {}

                        data.add(new BloqClass(id,tag,tipo,nomes,vencto,valor));
                    }
                } catch (SQLException e) {}
                try {DbMain.FecharTabela(mrs);} catch (Exception e) {}

                // Avisos
                String aSql = "select * from avisos WHERE conta = '1' AND aut_rec <> 0 AND '%s' = ANY (aut_pag) order by 2,3,4,7,9";
                aSql = String.format(aSql, registro.getText());
                ResultSet ars = conn.AbrirTabela(aSql, ResultSet.CONCUR_READ_ONLY);
                try {
                    while (ars.next()) {
                        Integer id = null;
                        Boolean tag = false;
                        String tipo = "A";
                        String nomes = null;
                        String vencto = null;
                        String valor = "0,00";

                        try {id = ars.getInt("id");} catch (SQLException e) {}
                        try {tag = ars.getString("bloqueio") != null ? true : false;} catch (SQLException e) {}
                        try {nomes = ars.getString("texto");} catch (SQLException e) {}
                        try {vencto = ars.getString("tipo");} catch (SQLException e) {}
                        try {valor = LerValor.BigDecimalToCurrency(ars.getBigDecimal("valor"));} catch (SQLException e) {}

                        data.add(new BloqClass(id,tag,tipo,nomes,vencto,valor));
                    }
                } catch (SQLException e) {}
                try {DbMain.FecharTabela(ars);} catch (Exception e) {}

                id.setCellValueFactory(new PropertyValueFactory<>("id"));
                tag.setEditable(true);
                tag.setCellValueFactory(new PropertyValueFactory<BloqClass, Boolean>("tag"));
                final BooleanProperty selected = new SimpleBooleanProperty();
                tag.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(Integer index) {
                        BloqClass tbvlinhas = ((BloqClass) lista.getItems().get(index));
                        String uSql = "UPDATE %s SET bloqueio = %s, dtbloqueio = %s, usr_bloqueio = %s WHERE id = %s;";
                        if (tbvlinhas.isTag()) {
                            uSql = String.format(uSql, tbvlinhas.getTipo().equalsIgnoreCase("R") ? "movimento" : "avisos", "\'" + Dates.DateFormata("MM-yyyy",Dates.DateAdd(Dates.MES,1,DbMain.getDateTimeServer())) + "\'","\'" + Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()) + "\'", "\'" + VariaveisGlobais.usuario + "\'", tbvlinhas.getId());
                        } else {
                            uSql = String.format(uSql, tbvlinhas.getTipo().equalsIgnoreCase("R") ? "movimento" : "avisos", null, null,null , tbvlinhas.getId());
                        }
                        try {conn.ExecutarComando(uSql);} catch (Exception e) {}

                        return lista.getItems().get(index).isCheckedTag();
                    }
                }));

                tag.setCellValueFactory(new PropertyValueFactory<>("tag"));
                tag.setStyle("-fx-alignment: CENTER;");
                tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
                tipo.setStyle("-fx-alignment: CENTER;");
                nomes.setCellValueFactory(new PropertyValueFactory<>("nome"));
                nomes.setStyle("-fx-alignment: CENTER-LEFT;");
                vecto.setCellValueFactory(new PropertyValueFactory<>("vecto"));
                vecto.setStyle("-fx-alignment: CENTER;");
                valor.setCellValueFactory(new PropertyValueFactory<>("valor"));
                valor.setStyle("-fx-alignment: CENTER-RIGHT;");
                lista.setItems(data);
                lista.getSelectionModel().setSelectionMode(null);

                lista.requestFocus();
            }
        });
    }
}
