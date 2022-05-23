package Movimento.Geracao;

import Calculos.AvisosMensagens;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Movimento.tbvGera;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import pdfViewer.PdfViewer;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by supervisor on 17/11/16.
 */
public class GeraMensalController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    Object[][] fimCtros = {};

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<tbvGera> tbvLista;
    @FXML private TableColumn<tbvGera, String> tbvLista_contrato;
    @FXML private TableColumn<tbvGera, String> tbvLista_nome;
    @FXML private TableColumn<tbvGera, String> tbvLista_referencia;
    @FXML private TableColumn<tbvGera, String> tbvLista_vencimento;
    @FXML private RadioButton opc_todos;
    @FXML private ToggleGroup opcoes;
    @FXML private RadioButton opc_periodo;
    @FXML private DatePicker opc_periodo_dtinicial;
    @FXML private DatePicker opc_periodo_dtfinal;
    @FXML private Button btnListar;
    @FXML private Button btnGerar;
    @FXML private Button btnListarBloq;
    @FXML private CheckBox selectAll;
    @FXML private TextField buscar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        opc_periodo_dtinicial.disableProperty().bind(opc_todos.selectedProperty());
        opc_periodo_dtfinal.disableProperty().bind(opc_todos.selectedProperty());
        opc_periodo.setOnAction(event -> {opc_periodo_dtinicial.requestFocus();});
        opc_periodo_dtinicial.setOnKeyReleased(event -> {
            if (opc_periodo_dtinicial.getEditor().getText().length() == 2) opc_periodo_dtinicial.getEditor().appendText("/");
            if (opc_periodo_dtinicial.getEditor().getText().length() == 5) opc_periodo_dtinicial.getEditor().appendText("/");
        });
        opc_periodo_dtfinal.setOnKeyReleased(event -> {
            if (opc_periodo_dtfinal.getEditor().getText().length() == 2) opc_periodo_dtfinal.getEditor().appendText("/");
            if (opc_periodo_dtfinal.getEditor().getText().length() == 5) opc_periodo_dtfinal.getEditor().appendText("/");
        });
        btnListar.setOnAction(event -> {
            if (opc_todos.isSelected()) {
                ListarTodos();
            } else {
                ListarPeriodo(Dates.toSqlDate(opc_periodo_dtinicial), Dates.toSqlDate(opc_periodo_dtfinal));
            }
        });
        selectAll.setOnAction(event -> {
            if (selectAll.isSelected()) {
                tbvLista.getSelectionModel().selectAll();
            } else tbvLista.getSelectionModel().clearSelection();
        });
        btnGerar.setOnAction(event -> {
            if (selectAll.isSelected() && tbvLista.getItems().size() > 0) {
                new GeracaoMovimento(null);
                if (opc_todos.isSelected()) { ListarTodos(); } else { ListarPeriodo(Dates.toSqlDate(opc_periodo_dtinicial), Dates.toSqlDate(opc_periodo_dtfinal)); }
                selectAll.setSelected(false);
            } else {
                Set<tbvGera> selection = new HashSet<tbvGera>(tbvLista.getSelectionModel().getSelectedItems());
                if (selection.size() > 0) {
                    for (tbvGera sel : selection) {
                        new GeracaoMovimento(sel.getContrato());
                    }
                    if (opc_todos.isSelected()) { ListarTodos(); } else { ListarPeriodo(Dates.toSqlDate(opc_periodo_dtinicial), Dates.toSqlDate(opc_periodo_dtfinal)); }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Menssagem");
                    alert.setHeaderText("Seleção Inválida");
                    alert.setContentText("Você deve selecionar ao menos um item na lista!!!");
                    alert.showAndWait();
                }
            }
        });

        btnListarBloq.setOnAction(event -> {
            List<FimCtr> lista = new ArrayList<FimCtr>();
            for (Object[] value : fimCtros) {
                lista.add(new FimCtr((String)value[0], (String)value[1], (Date)value[2], (Date)value[3], (Date)value[4], (BigDecimal)value[5]));
            }

            String pdfName = new PdfViewer().GeraPDFTemp(lista, "ListaBloqueados");
            //new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview do Lista de Bloqueados", pdfName);
        });
    }

    private void ListarTodos() {
        ObservableList<tbvGera> data = FXCollections.observableArrayList();
        ResultSet imv;
        String qSQL = "SELECT c.rgprp, c.rgimv, c.contrato, CASE WHEN l.l_fisjur = TRUE THEN l_f_nome ELSE l_j_razao END AS nome, c.dtvencimento, c.mensal, c.cota, c.referencia FROM carteira c, locatarios l WHERE (exclusao is null) and c.contrato = l.l_contrato;";
        this.fimCtros = new String[][] {};
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qrgprp = null, qrgimv = null, qcontrato = null;
                String qnome = null, qref = null, qvencimento = null;
                String qvalor = null, qcota = null;
                try {qrgprp = imv.getString("rgprp");} catch (SQLException e) {}
                try {qrgimv = imv.getString("rgimv");} catch (SQLException e) {}
                try {qcontrato = imv.getString("contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("nome");} catch (SQLException e) {}
                try {qvencimento = imv.getDate("dtvencimento").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));} catch (SQLException e) {}
                try {qvalor = imv.getString("mensal");} catch (SQLException e) {}
                try {qcota = imv.getString("cota");} catch (SQLException e) {}
                try {qref = imv.getString("referencia");} catch (SQLException e) {}

                Object[] fimCtro = new AvisosMensagens().VerificaFimCtroLocatario(qcontrato);
                if ((boolean)fimCtro[1]) {
                    if (!((String)fimCtro[0]).contains("expirado")) {
                        data.add(new tbvGera(qrgprp, qrgimv, qcontrato, qnome, qvencimento, qvalor, qcota, qref));
                    } else {
                        // Pegar dados na carteira
                        Object[][] dadosfim = conn.LerCamposTabela(new String[] {"dtinicio", "dtfim", "dtaditamento", "mensal"}, "carteira", "contrato = '" + qcontrato + "'");
                        Date qdtinicio = null; Date qdttermino = null; Date qdtaditamento = null; BigDecimal qaluguel = null;
                        if (dadosfim != null) {
                            qdtinicio = (Date)dadosfim[0][3];
                            qdttermino = (Date)dadosfim[1][3];
                            qdtaditamento = (Date)dadosfim[2][3];
                            qaluguel = new BigDecimal((String)dadosfim[3][3]);
                        }
                        this.fimCtros = FuncoesGlobais.ObjectsAdd(this.fimCtros,new Object[] {qcontrato, qnome, qdtinicio, qdttermino, qdtaditamento, qaluguel});
                    }
                } else data.add(new tbvGera(qrgprp, qrgimv, qcontrato, qnome, qvencimento, qvalor, qcota, qref));
            }
            imv.close();
        } catch (SQLException e) {}

        tbvLista_contrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        tbvLista_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tbvLista_referencia.setCellValueFactory(new PropertyValueFactory<>("refer"));
        tbvLista_vencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));

        tbvLista.setItems(FXCollections.observableArrayList(data));
        tbvLista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        FilteredList<tbvGera> filteredData = new FilteredList<tbvGera>(data, e -> true);
        buscar.setOnKeyReleased(e ->{
            buscar.textProperty().addListener((observableValue, oldValue, newValue) ->{
                filteredData.setPredicate((Predicate<? super tbvGera>) user->{
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(user.getContrato().contains(newValue)){
                        return true;
                    }else if(user.getNome().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else if(user.getVencimento().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else return user.getRefer().toLowerCase().contains(lowerCaseFilter);
                });
            });
            SortedList<tbvGera> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tbvLista.comparatorProperty());
            tbvLista.setItems(sortedData);
            tbvLista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        });

        tbvLista.setRowFactory(new Callback<TableView<tbvGera>, TableRow<tbvGera>>() {
            @Override
            public TableRow<tbvGera> call(TableView<tbvGera> tableView2)
            {
                final TableRow<tbvGera> row = new TableRow<>();

                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        final int index = row.getIndex();

                        if (index >= 0 && index < tbvLista.getItems().size())
                        {
                            if(tbvLista.getSelectionModel().isSelected(index))
                                tbvLista.getSelectionModel().clearSelection(index);
                            else
                                tbvLista.getSelectionModel().select(index);

                            event.consume();
                        }
                    }
                });
                return row;
            }
        });

        btnListarBloq.setDisable(fimCtros.length == 0);
    }

    private void ListarPeriodo(Date dtinial, Date dtfinal) {
        ObservableList<tbvGera> data = FXCollections.observableArrayList();
        ResultSet imv;
        String qSQL = "SELECT c.rgprp, c.rgimv, c.contrato, CASE WHEN l.l_fisjur = TRUE THEN l_f_nome ELSE l_j_razao END AS nome, c.dtvencimento, c.mensal, c.cota, c.referencia FROM carteira c, locatarios l WHERE (exclusao is null) and c.contrato = l.l_contrato AND (c.dtvencimento >= '%s' AND c.dtvencimento <= '%s');";
        qSQL = String.format(qSQL,dtinial, dtfinal);
        this.fimCtros = new String[][] {};
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qrgprp = null, qrgimv = null, qcontrato = null;
                String qnome = null, qref = null, qvencimento = null;
                String qvalor = null, qcota = null;
                try {qrgprp = imv.getString("rgprp");} catch (SQLException e) {}
                try {qrgimv = imv.getString("rgimv");} catch (SQLException e) {}
                try {qcontrato = imv.getString("contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("nome");} catch (SQLException e) {}
                try {qvencimento = imv.getDate("dtvencimento").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));} catch (SQLException e) {}
                try {qvalor = imv.getString("mensal");} catch (SQLException e) {}
                try {qcota = imv.getString("cota");} catch (SQLException e) {}
                try {qref = imv.getString("referencia");} catch (SQLException e) {}

                Object[] fimCtro = new AvisosMensagens().VerificaFimCtroLocatario(qcontrato);
                if ((boolean)fimCtro[1]) {
                    if (!((String)fimCtro[0]).contains("expirado")) {
                        data.add(new tbvGera(qrgprp, qrgimv, qcontrato, qnome, qvencimento, qvalor, qcota, qref));
                    } else {
                        Object[][] dadosfim = conn.LerCamposTabela(new String[] {"dtinicio", "dtfim", "dtaditamento", "mensal"}, "carteira", "contrato = '" + qcontrato + "'");
                        Date qdtinicio = null; Date qdttermino = null; Date qdtaditamento = null; BigDecimal qaluguel = null;
                        if (dadosfim != null) {
                            qdtinicio = (Date)dadosfim[0][3];
                            qdttermino = (Date)dadosfim[1][3];
                            qdtaditamento = (Date)dadosfim[2][3];
                            qaluguel = new BigDecimal((String)dadosfim[3][3]);
                        }
                        this.fimCtros = FuncoesGlobais.ObjectsAdd(fimCtros,new Object[] {qcontrato, qnome, qdtinicio, qdttermino, qdtaditamento, qaluguel});
                    }
                } else data.add(new tbvGera(qrgprp, qrgimv, qcontrato, qnome, qvencimento, qvalor, qcota, qref));
            }
            imv.close();
        } catch (SQLException e) {}

        tbvLista_contrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        tbvLista_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tbvLista_referencia.setCellValueFactory(new PropertyValueFactory<>("refer"));
        tbvLista_vencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));

        tbvLista.setItems(FXCollections.observableArrayList(data));
        tbvLista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        FilteredList<tbvGera> filteredData = new FilteredList<tbvGera>(data, e -> true);
        buscar.setOnKeyReleased(e ->{
            buscar.textProperty().addListener((observableValue, oldValue, newValue) ->{
                filteredData.setPredicate((Predicate<? super tbvGera>) user->{
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(user.getContrato().contains(newValue)){
                        return true;
                    }else if(user.getNome().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else if(user.getVencimento().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else return user.getRefer().toLowerCase().contains(lowerCaseFilter);
                });
            });
            SortedList<tbvGera> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tbvLista.comparatorProperty());
            tbvLista.setItems(sortedData);

        });
        btnListarBloq.setDisable(fimCtros.length == 0);
    }
}
