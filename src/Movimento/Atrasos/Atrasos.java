package Movimento.Atrasos;

import Funcoes.*;
import com.sun.prism.impl.Disposer.Record;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Atrasos implements Initializable {
    private DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;

    @FXML private TableView<cAtrasos> atrasados;
    @FXML private TableColumn<cAtrasos, Integer> atzId;
    @FXML private TableColumn<cAtrasos, String> atzTipo;
    @FXML private TableColumn<cAtrasos, String> atzContrato;
    @FXML private TableColumn<cAtrasos, String> atzNome;
    @FXML private TableColumn<cAtrasos, Date> atzVencimento;
    @FXML private TableColumn<cAtrasos, String> atzTelefones;
    @FXML private TableColumn<cAtrasos, String> atzEMails;
    @FXML private TableColumn<Record, Boolean> atzAcoes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FillVectos();

    }

    private void FillVectos() {
        List<cAtrasos> data = new ArrayList<cAtrasos>();
        String Sql = "select CASE WHEN l.l_fisjur THEN 'Física' ELSE 'Jurídica' END tipo," +
                "m.contrato,CASE WHEN l.l_fisjur THEN l.l_f_nome else l.l_j_razao END nome," +
                "CASE WHEN l.l_fisjur THEN l.l_f_tel else l.l_j_tel END tel," +
                "CASE WHEN l.l_fisjur THEN l.l_f_email else l.l_j_email END email," +
                "m.dtvencimento,m.referencia FROM movimento m " +
                "INNER JOIN locatarios l ON l.l_contrato = m.contrato WHERE (exclusao is null) and " +
                "m.dtvencimento < CURRENT_DATE ORDER BY m.contrato, m.dtvencimento;";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
            int gId = -1; String gTipo = null, gContrato = null, gNome = null, gTelefones = null, gEmails = null;
            Date gVencimento = null;

            while (rs.next()) {
                try {gId = rs.getInt("id");} catch (SQLException sqlex) {}
                try {gTipo = rs.getString("tipo");} catch (SQLException sqlex) {}
                try {gContrato = rs.getString("contrato");} catch (SQLException sqlex) {}
                try {gNome = rs.getString("nome");} catch (SQLException sqlex) {}
                try {gVencimento = rs.getDate("dtvencimento");} catch (SQLException sqlex) {}
                try {gTelefones = rs.getString("tel");} catch (SQLException sqlex) {}
                try {gEmails = rs.getString("email");} catch (SQLException sqlex) {}

                data.add(new cAtrasos(gId, gTipo, gContrato, gNome, gVencimento, gTelefones, gEmails));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        atzId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        atzId.setStyle( "-fx-alignment: CENTER;");

        atzTipo.setCellValueFactory(new PropertyValueFactory<>("Tipo"));
        atzTipo.setStyle( "-fx-alignment: CENTER;");

        atzContrato.setCellValueFactory(new PropertyValueFactory<>("Contrato"));
        atzContrato.setStyle("-fx-alignment: CENTER;");

        atzNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        atzNome.setStyle("-fx-alignment: LEFT;");

        atzVencimento.setCellValueFactory(new PropertyValueFactory<>("Vencimento"));
        atzVencimento.setCellFactory((AbstractConvertCellFactory<cAtrasos, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        atzVencimento.setStyle( "-fx-alignment: CENTER;");

        atzTelefones.setCellValueFactory(new PropertyValueFactory<>("Logado"));
        atzTelefones.setStyle( "-fx-alignment: CENTER-LEFT;");

        atzEMails.setCellValueFactory(new PropertyValueFactory<>("Lanctos"));
        atzEMails.setStyle( "-fx-alignment: CENTER-LEFT;");

        atzAcoes.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));

        //Adding the Button to the cell
        atzAcoes.setCellFactory(p -> new ButtonCell());

        if (!data.isEmpty()) atrasados.setItems(FXCollections.observableArrayList(data));
    }

    public interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
        @Override
        default TableCell<E, T> call(TableColumn<E, T> param) {
            return new TableCell<E, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(convert(item));
                    }
                }
            };
        }

        String convert(T value);
    }

    // Classe que cria o botão
    private class ButtonCell extends TableCell<Record, Boolean> {
        final Button cellEmail = new Button("@");
        final Button cellZap = new Button("Z");
        final Button cellTel = new Button("T");

        ButtonCell(){
            atrasados.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (newPropertyValue) {
                        cellEmail.setDisable(false);
                        cellZap.setDisable(false);
                        cellTel.setDisable(false);
                    } else {
                        cellEmail.setDisable(true);
                        cellZap.setDisable(true);
                        cellTel.setDisable(true);
                    }
                }
            });

            cellEmail.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cAtrasos select = (cAtrasos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                }
            });

            cellZap.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cAtrasos select = (cAtrasos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                }
            });

            cellTel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // get Selected Item
                    cAtrasos select = (cAtrasos) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                }
            });

        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                HBox pane = new HBox(cellEmail, cellZap, cellTel);
                setGraphic(pane);
            }
        }
    }
}


    //select
    //	CASE WHEN l.l_fisjur THEN 'Física' ELSE 'Jurídica' END tipo,
    //	m.contrato,
    //	CASE WHEN l.l_fisjur THEN l.l_f_nome else l.l_j_razao END nome,
    //	CASE WHEN l.l_fisjur THEN l.l_f_tel else l.l_j_tel END tel,
    //	CASE WHEN l.l_fisjur THEN l.l_f_email else l.l_j_email END email,
    //	m.dtvencimento,
    //	m.referencia
    //FROM
    //	movimento m
    //	INNER JOIN locatarios l ON l.l_contrato = m.contrato
    //WHERE
    //	m.dtvencimento < CURRENT_DATE
    //ORDER BY
    //	m.contrato,
    //	m.dtvencimento;
