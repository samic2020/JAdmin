package Gerencia.Controle;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;
import pdfViewer.PdfViewer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class ContasControle implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsCtCodigo = {};
    private String[] _possibleSuggestionsCtNome = {};
    private String[][] _possibleSuggestionsCt = {};
    private Set<String> possibleSuggestionsCtCodigo;
    private Set<String> possibleSuggestionsCtNome;

    private String codConta = null;

    @FXML private AnchorPane anchorPane;
    @FXML private Button btnExclusao;
    @FXML private TextField txtContas;
    @FXML private Button btnListar;

    @FXML private TableView<ctaControle> tbvLanc;
    @FXML private TableColumn<ctaControle, BigInteger> l_id;
    @FXML private TableColumn<ctaControle, String> l_registro;
    @FXML private TableColumn<ctaControle, String> l_descreg;
    @FXML private TableColumn<ctaControle, String> l_data;
    @FXML private TableColumn<ctaControle, String> l_desc;
    @FXML private TableColumn<ctaControle, BigDecimal> l_cr;
    @FXML private TableColumn<ctaControle, BigDecimal> l_db;
    @FXML private TableColumn<ctaControle, BigInteger> l_aut;

    @FXML private TextField txtLocalizar;
    @FXML private ImageView btnClear;
    @FXML private Label lbl_TCr;
    @FXML private Label lbl_TDb;
    @FXML private Label lbl_TSaldo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutocompleteContas();
        btnClear.setOnMouseReleased(event -> {txtLocalizar.clear(); txtLocalizar.requestFocus();});
        btnExclusao.setDisable(true);
        btnExclusao.setOnAction(event -> {
            Alert msg = new Alert(CONFIRMATION, "Deseja excluir esta conta?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) {
                return;
            }

            Alert msg2 = new Alert(CONFIRMATION, "Você já imprimiu o relatório desta conta?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result2 = msg.showAndWait();
            if (result2.get().getText().equals("Não")) {
                return;
            }

            String dSql = "UPDATE adm_contas SET deletada = true WHERE codigo = ?;";
            try {
                if (conn.ExecutarComando(dSql, new Object[][] {{"string", codConta}}) >= 0) {
                    new Alert(INFORMATION, "Conta excluida!");
                }
            } catch (Exception e) {}
            AutocompleteContas();
            Platform.runLater(() -> txtContas.requestFocus());
        });

        btnListar.setOnAction(event -> {
            List<ctaControle> lista = new ArrayList<ctaControle>();
            for( final ctaControle os : tbvLanc.getItems()) {
                lista.add(new ctaControle(os.getId(), os.getRegistro(), os.getDescreg(),os.getData(), os.getDescr(), os.getCr(), os.getDb(), os.getAut()));
            }
            String pdfName = new PdfViewer().GeraPDFTemp(lista, "ListaContas");
            //new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview do Lista de Contas", pdfName);
        });

        Platform.runLater(() -> txtContas.requestFocus());
    }

    private void AutocompleteContas() {
        _possibleSuggestionsCtCodigo = new String[]{};
        _possibleSuggestionsCtNome = new String[]{};
        _possibleSuggestionsCt = new String[][]{};
        possibleSuggestionsCtCodigo = new HashSet<String>();
        possibleSuggestionsCtNome = new HashSet<String>();

        try {
            TextFields.bindAutoCompletion(txtContas, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Socios_Adm
        qSQL = "SELECT id, codigo, descricao FROM adm_contas WHERE tipo = 'CONTAS' AND deletada = false ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("codigo");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsCtCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsCtCodigo, qcontrato);
                possibleSuggestionsCtCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsCtCodigo));

                _possibleSuggestionsCtNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsCtNome, qnome);
                possibleSuggestionsCtNome = new HashSet<>(Arrays.asList(_possibleSuggestionsCtNome));

                _possibleSuggestionsCt = FuncoesGlobais.ArraysAdd(_possibleSuggestionsCt, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txtContas, possibleSuggestionsCtNome);

        txtContas.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txtContas.setText("");
                tbvLanc.setItems(FXCollections.observableArrayList());
                txtLocalizar.setText("");
                lbl_TCr.setText("0,00");
                lbl_TDb.setText("0,00");
                lbl_TSaldo.setText("0,00");
                lbl_TSaldo.setTextFill(Color.DARKGREEN);
                btnExclusao.setDisable(true);
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsCt,1, txtContas.getText());} catch (NullPointerException e){}
                if (pos > -1) {
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsCt,1,txtContas.getText())) {
                        codConta = _possibleSuggestionsCt[pos][0];
                    }
                } else {
                    codConta = null;
                }

                if (codConta != null) {
                    MostraConta(codConta);
                } else {
                    if (CriaConta()) {
                        txtLocalizar.requestFocus();
                    } else txtLocalizar.requestFocus();
                }
            }
        });
    }

    private void MostraConta(String cdConta) {
        //List<ctaControle> data = new ArrayList<ctaControle>();
        ObservableList<ctaControle> data = FXCollections.observableArrayList();

        ResultSet vrs;
        String Sql = "SELECT a.id, a.registro, c.descricao, a.dtrecebimento, a.texto, CAST(CASE WHEN a.tipo = 'CRE' THEN a.valor ELSE 0 END AS Decimal(10,2)) AS credito, CAST(CASE WHEN a.tipo = 'DEB' THEN a.valor ELSE 0 END AS Decimal(10,2)) AS debito, a.aut_rec FROM avisos a INNER JOIN adm_contas c ON c.codigo = a.registro WHERE conta = 4 and registro = ? and not dtrecebimento is null;";

        BigDecimal tCr = new BigDecimal("0");
        BigDecimal tDb = new BigDecimal("0");
        BigDecimal tSl = new BigDecimal("0");

        try {
            vrs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", cdConta}});
            while (vrs.next()) {
                ctaControle registro = new ctaControle();
                try {registro.setId(vrs.getInt("id"));} catch (SQLException e) {}
                try {registro.setRegistro(vrs.getString("registro"));} catch (SQLException e) {}
                try {registro.setDescreg(vrs.getString("descricao"));} catch (SQLException e) {}
                try {registro.setData(Dates.DateFormata("dd/MM/yyyy", vrs.getDate("dtrecebimento")));} catch (SQLException e) {}
                try {registro.setDescr(vrs.getString("texto"));} catch (SQLException e) {}
                try {registro.setCr(vrs.getBigDecimal("credito"));} catch (SQLException e) {}
                try {registro.setDb(vrs.getBigDecimal("debito"));} catch (SQLException e) {}
                try {registro.setAut((new BigInteger(vrs.getString("aut_rec"))));} catch (SQLException e) {}

                data.add(new ctaControle(registro.getId(), registro.getRegistro(), registro.getDescreg(),registro.getData(), registro.getDescr(), registro.getCr(), registro.getDb(), registro.getAut()));
                tCr = tCr.add(registro.getCr());
                tDb = tDb.add(registro.getDb());
                tSl = tSl.add(registro.getCr().subtract(registro.getDb()));
            }
            vrs.close();
        } catch (SQLException e) {}

        l_id.setCellValueFactory(new PropertyValueFactory("id"));
        l_registro.setCellValueFactory(new PropertyValueFactory("registro"));
        l_descreg.setCellValueFactory(new PropertyValueFactory("descreg"));
        l_data.setCellValueFactory(new PropertyValueFactory("data"));
        l_data.setStyle( "-fx-alignment: CENTER; -fx-text-fill: darkblue");

        l_desc.setCellValueFactory(new PropertyValueFactory("descr"));

        l_cr.setCellValueFactory(new PropertyValueFactory("cr"));
        l_cr.setCellFactory((AbstractConvertCellFactory<ctaControle, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        l_cr.setStyle( "-fx-alignment: CENTER-RIGHT; -fx-text-fill: darkblue");

        l_db.setCellValueFactory(new PropertyValueFactory("db"));
        l_db.setCellFactory((AbstractConvertCellFactory<ctaControle, BigDecimal>) value -> new DecimalFormat("#,##0.00").format(value));
        l_db.setStyle( "-fx-alignment: CENTER-RIGHT; -fx-text-fill: darkred");

        l_aut.setCellValueFactory(new PropertyValueFactory("aut"));
        l_aut.setStyle( "-fx-alignment: CENTER; -fx-text-fill: black");

        tbvLanc.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tbvLanc.setItems(FXCollections.observableArrayList(data));
        tbvLanc.setEditable(false);

        lbl_TCr.setText(new DecimalFormat("#,##0.00").format(tCr));
        lbl_TDb.setText(new DecimalFormat("#,##0.00").format(tDb));
        lbl_TSaldo.setText(new DecimalFormat("#,##0.00").format(tSl));
        lbl_TSaldo.setTextFill(tSl.compareTo(BigDecimal.ZERO) <= 0 ? Color.DARKRED : Color.DARKGREEN );

        btnExclusao.disableProperty().bindBidirectional(new SimpleBooleanProperty(tSl.compareTo(BigDecimal.ZERO) > 0));

        FilteredList<ctaControle> filteredData = new FilteredList<ctaControle>(data, e -> true);
        txtLocalizar.setOnKeyReleased(e ->{
            txtLocalizar.textProperty().addListener((observableValue, oldValue, newValue) ->{
                filteredData.setPredicate((Predicate<? super ctaControle>) user->{
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(user.getData().contains(newValue)){
                        return true;
                    }else return user.getDescr().toLowerCase().contains(lowerCaseFilter);
                });
            });
            SortedList<ctaControle> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tbvLanc.comparatorProperty());
            tbvLanc.setItems(sortedData);
        });

        txtLocalizar.requestFocus();
    }

    private boolean CriaConta() {
        Alert msg = new Alert(CONFIRMATION, "Conta não existe!\nDeseja criar esta conta?", new ButtonType("Sim"), new ButtonType("Não"));
        Optional<ButtonType> result = msg.showAndWait();
        if (result.get().getText().equals("Não")) {
            return false;
        }

        String mContas = null; String mContasText = "A"; int mContasNumber = 0;
        try {
            mContas = conn.LerParametros("CONTAS");
        } catch (SQLException ex) {}
        if (mContas != null) {
            mContasText = mContas.substring(0,1).toUpperCase();
            mContasNumber = Integer.parseInt(mContas.substring(1)) + 1;
        }
        String mSql = "INSERT INTO adm_contas (codigo, descricao, tipo) VALUES (?, ?, 'CONTAS');";
        if (conn.ExecutarComando(mSql, new Object[][] {
                    {"string", mContasText + FuncoesGlobais.StrZero(String.valueOf(mContasNumber),2)},
                {"string", txtContas.getText()}
                }
        ) > 0) {
            try {
                conn.GravarParametros(new String[]{"CONTAS",mContasText + FuncoesGlobais.StrZero(String.valueOf(mContasNumber),2),"TEXTO"});
            } catch (SQLException e) {}

            AutocompleteContas();
        }

        return true;
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
}
