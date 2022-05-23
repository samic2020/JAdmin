package Movimento.Alteracao;

import Classes.Taxas;
import Classes.paramEvent;
import Funcoes.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableList;

/**
 * Created by supervisor on 06/01/17.
 */
public class TaxasController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String rgprp = null;
    private String rgimv = null;
    private String contrato = null;
    private String refer = null;

    @FXML private AnchorPane anchorPane;
    @FXML private ToggleGroup creddeb;
    @FXML private RadioButton debito;
    @FXML private RadioButton credito;
    @FXML private ComboBox<Taxas> codigo;
    @FXML private TextField descricao;
    @FXML private TextField cotaparc;
    @FXML private CheckBox retencao;
    @FXML private CheckBox extrato;
    @FXML private DatePicker vencimento;
    @FXML private TextField valor;
    @FXML private Button btlancar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if ((String) event.sparam[0] != null) {
                contrato = (String) event.sparam[0];
                rgprp = (String) event.sparam[1];
                rgimv = (String) event.sparam[2];
                refer = (String) event.sparam[3];
            }
        });

        MaskFieldUtil.maxField(descricao, 25);
        MaskFieldUtil.dateRefField(cotaparc);
        MaskFieldUtil.monetaryField(valor);

        btlancar.disableProperty().bind(vencimento.promptTextProperty().isEmpty().or(vencimento.promptTextProperty().lessThan(Dates.DateFormata("dd-MM-yyyy",DbMain.getDateTimeServer()))).and(valor.textProperty().isEmpty().or(valor.textProperty().isEqualToIgnoreCase("0,00"))));
        btlancar.setOnAction(event -> {
            String iSql = "INSERT INTO taxas(" +
                    "            rgprp, rgimv, contrato, precampo, campo, poscampo, cota, " +
                    "            valor, dtvencimento, referencia, retencao, extrato, tipo, " +
                    "            matricula, dtlanc, usr_lanc)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try {
                PreparedStatement pstmt = conn.conn.prepareStatement(iSql);
                int nid = 1;
                pstmt.setInt(nid++, Integer.valueOf(rgprp));
                pstmt.setInt(nid++, Integer.valueOf(rgimv));
                pstmt.setString(nid++, contrato);
                pstmt.setString(nid++, "");
                pstmt.setString(nid++, codigo.getSelectionModel().getSelectedItem().toString());
                pstmt.setString(nid++, descricao.getText().trim());
                pstmt.setString(nid++, cotaparc.getText().trim());
                pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(valor.getText())));
                pstmt.setDate(nid++, Dates.toSqlDate(vencimento));
                pstmt.setString(nid++, refer.trim());
                pstmt.setBoolean(nid++, retencao.isSelected());
                pstmt.setBoolean(nid++, extrato.isSelected());
                pstmt.setString(nid++, "D");
                pstmt.setString(nid++, ""); // Matricula
                pstmt.setDate(nid++, java.sql.Date.valueOf(java.time.LocalDate.now()));
                pstmt.setString(nid++, VariaveisGlobais.usuario);

                pstmt.executeUpdate();
            } catch (SQLException ex) {}

            try {anchorPane.fireEvent(new paramEvent(new String[] {null},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        });

        // Campo Taxas - txtCodigo
        String tSql = "SELECT id, codigo, descricao, predesc, posdesc, retencao, extrato FROM campos ORDER BY codigo;";
        ResultSet rs = conn.AbrirTabela(tSql,ResultSet.CONCUR_READ_ONLY);
        List<Taxas> ttaxas = new ArrayList<>();
        try {
            while (rs.next()) {
                ttaxas.add(
                        new Taxas(
                                rs.getInt("id"),
                                rs.getString("codigo"),
                                rs.getString("descricao"),
                                rs.getBoolean("predesc"),
                                rs.getBoolean("posdesc"),
                                rs.getBoolean("retencao"),
                                rs.getBoolean("extrato")
                        )
                );
            }
        } catch (SQLException e) {}
        try {rs.close();} catch (SQLException e) {}
        ObservableList<Taxas> observableList = observableList(ttaxas);
        codigo.setItems(observableList);

        Callback cb = new Callback<ListView<Taxas>,ListCell<Taxas>>(){
            @Override
            public ListCell<Taxas> call(ListView<Taxas> l){
                return new ListCell<Taxas>(){

                    private final HBox hbx;
                    private final Label codigo;
                    private final Label sep;
                    private final Label nome;

                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        codigo = new Label(); codigo.setTextFill(Color.DARKBLUE);
                        codigo.setPrefWidth(30); codigo.setPrefHeight(25); codigo.setAlignment(Pos.CENTER_RIGHT);

                        sep = new Label(" - ");
                        sep.setPrefWidth(20); sep.setPrefHeight(25); sep.setAlignment(Pos.CENTER);

                        nome = new Label(); nome.setTextFill(Color.DARKGREEN);
                        nome.setPrefWidth(180); nome.setPrefHeight(25); nome.setAlignment(Pos.CENTER_LEFT);

                        hbx = new HBox(codigo, sep, nome);
                    }

                    @Override
                    protected void updateItem(Taxas item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            //setText(item.getNumero() + " - "+item.getNome());
                            codigo.setText(item.getCodigo());
                            nome.setText(item.getDescricao());
                            setGraphic(hbx);
                        }
                    }
                } ;
            }
        };
        codigo.setCellFactory(cb);

        codigo.setOnAction(e -> {
            descricao.setText(null);
            cotaparc.setText(null);
            retencao.setSelected(codigo.getSelectionModel().getSelectedItem().getRetencao());
            extrato.setSelected(codigo.getSelectionModel().getSelectedItem().getExtrato());
            vencimento.setValue(null);
            valor.setText("0,00");
        });

    }
}
