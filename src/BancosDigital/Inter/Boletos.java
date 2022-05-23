/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import Administrador.BancoBoleta;
import Bancos.DadosBanco;
import Bancos.Pagador;
import Bancos.RedeBancaria.Banco;
import Bancos.RedeBancaria.Bradesco;
import Bancos.RedeBancaria.Brasil;
import Bancos.RedeBancaria.Cef;
import Bancos.RedeBancaria.Digital;
import Bancos.RedeBancaria.Itau;
import Bancos.RedeBancaria.Santander;
import BancosDigital.LocalDateTableCell;
import Calculos.AvisosMensagens;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import static Funcoes.FuncoesGlobais.StrZero;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.VariaveisGlobais;
import Movimento.Boletas.BoletasController;
import Movimento.Locatarios;
import Movimento.tbvAltera;
import boleta.Boleta;
import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author Samic
 */
public class Boletos implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    private List<BancosBoleta> bancosBoleta;
    private List<BancosErros> bancosBoletaErros;
    private List<BancosBoleta> BoletasBanco;
    
    private TreeTableView<BoletasController.pagadores> boletas = new TreeTableView<>();
    private final TreeItem<String> root = new TreeItem<>("Bancos");

    private String codErro;
    private String msgErro;

    @FXML private Spinner<String> gMes;
    @FXML private Spinner<Integer> gAno;

    @FXML private RadioButton gEmDia;
    @FXML private ToggleGroup condicao;
    @FXML private RadioButton gAtrasados;
    @FXML private RadioButton gTodos;
    @FXML private RadioButton gPorPeriodo;

    @FXML private DatePicker gVencimento;

    @FXML private DatePicker gInicio;
    @FXML private DatePicker gFinal;

    @FXML private TextField gContrato;

    @FXML private Button gBtnListar;
    @FXML private ProgressBar gpbListar;

    @FXML private Button gGerar;
    @FXML private ProgressBar gGerarBancos;
    @FXML private ProgressBar gGerarBoletas;

    @FXML private TableView<PessoasBoleta> gJaImpressos;
    @FXML private TableColumn<PessoasBoleta, String> gJaImpressos_Contrato;
    @FXML private TableColumn<PessoasBoleta, String> gJaImpressos_Nome;
    @FXML private TableColumn<PessoasBoleta, LocalDate> gJaImpressos_Vencimento;
    @FXML private TableColumn<PessoasBoleta, String> gJaImpressos_NossoNumero;
    @FXML private TableColumn<PessoasBoleta, String> gJaImpressos_TipoEnvio;
    @FXML private TableColumn<PessoasBoleta, String> gJaImpressos_RgPrp;
    @FXML private TableColumn<PessoasBoleta, String> gJaImpressos_RgImv;

    @FXML private TableView<PessoasBoleta> gBoletos;
    @FXML private TableColumn<PessoasBoleta, String> gBoletos_Contrato;
    @FXML private TableColumn<PessoasBoleta, String> gBoletos_Nome;
    @FXML private TableColumn<PessoasBoleta, LocalDate> gBoletos_Vencimento;
    @FXML private TableColumn<PessoasBoleta, LocalDate> gBoletos_Boleta;
    @FXML private TableColumn<PessoasBoleta, String> gBoletos_TipoEnvio;
    @FXML private TableColumn<PessoasBoleta, String> gBoletos_RgPrp;
    @FXML private TableColumn<PessoasBoleta, String> gBoletos_RgImv;
    @FXML private TableColumn<PessoasBoleta, Boolean> gBoletos_Tag;

    @FXML private TableView<BancosErros> gErros;
    @FXML private TableColumn<BancosErros, String> gErros_Contrato;
    @FXML private TableColumn<BancosErros, String> gErros_Nome;
    @FXML private TableColumn<BancosErros, LocalDate> gErros_Vencimento;
    @FXML private TableColumn<BancosErros, String> gErros_Codigo;
    @FXML private TableColumn<BancosErros, String> gErros_Mensagem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Calendar toDay = Calendar.getInstance();

        gVencimento.setValue(LocalDate.of(toDay.get(Calendar.YEAR), toDay.get(Calendar.MONTH) + 1, toDay.get(Calendar.DAY_OF_MONTH)));
        gVencimento.setShowWeekNumbers(false);

        // Converter
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        gVencimento.setConverter(converter);
        gVencimento.setPromptText("dd-MM-yyyy");

        gVencimento.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Desligar
                        if (item.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                        if (item.getMonthValue() < Calendar.getInstance().get(Calendar.MONTH) + 1) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                        if (item.getYear() < Calendar.getInstance().get(Calendar.YEAR)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            };
        });

        ObservableList<String> months = FXCollections.observableArrayList(
                "Janeiro", "Fevereiro", "Março", "Abril",
                "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro");
        SpinnerValueFactory<String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(months);

        // Default value
        valueFactory.setValue(months.get(Calendar.getInstance().get(Calendar.MONTH)));
        gMes.setValueFactory(valueFactory);

        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR) + 10, Calendar.getInstance().get(Calendar.YEAR));
        gAno.setValueFactory(ano);


            //cBtnBaixar.disableProperty().bind(jTipoListagem.rowFactoryProperty().isNull());

            gVencimento.disableProperty().bind(gAtrasados.selectedProperty().not());
            gInicio.disableProperty().bind(gPorPeriodo.selectedProperty().not());
            gFinal.disableProperty().bind(gPorPeriodo.selectedProperty().not());
            //ebtnEnviarTodos.disableProperty().bind(eEnvio.rowFactoryProperty().isNull().or(eAssunto.textProperty().isEmpty()));
            //eEnviarSel.disableProperty().bind(eEnvio.rowFactoryProperty().isNull().or(eAssunto.textProperty().isEmpty()));
            //eEditar.disableProperty().bind(eEnvio.rowFactoryProperty().isNull().or(eAssunto.textProperty().isEmpty()));

            gBtnListar.setOnAction(event -> {
                if (gEmDia.isSelected()) {
                    EmDia();
                } else if (gAtrasados.isSelected()) {
                    Atrasados();
                } else if (gTodos.isSelected()) {
                    Todos();
                } else PorPeriodo(Dates.toDate(gInicio.getValue()), Dates.toDate(gFinal.getValue()));
            });

            gGerar.setOnAction(event -> {
                Geracao();
            });
        
    }

    public void Atrasados() {
        gpbListar.setProgress(0);

        String sContrato = "";
        if (!"".equals(gContrato.getText().trim())) { sContrato = " AND m.contrato = '" + gContrato.getText().trim() + "' "; }
        int iAnoRef = gAno.getValue();
        String Sql = "SELECT m.id, m.rgprp rgprp, m.rgimv rgimv, m.contrato contrato, " +
                "CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END nomerazao, " +
                "m.dtvencimento, l.l_tprecebimento nbanco, l_formaenvio envio, b.numero bcobol, " +
                "b.nome bconome, m.nnumero FROM movimento m INNER JOIN locatarios l ON l.l_contrato = m.contrato AND " +
                "(l.l_tprecebimento != 'REC' AND l.l_tprecebimento <> '') " +
                "INNER JOIN bancos b ON b.numero = l.l_tprecebimento WHERE " +
                "EXISTS(SELECT * FROM bancos_digital bd WHERE l.l_tprecebimento = bd.nbanco LIMIT 1) AND " +
                "m.selected = true AND (m.aut_rec = 0 OR m.aut_rec isnull) AND m.dtvencimento < ? " +
                "AND (EXTRACT(YEAR FROM m.dtvencimento) = ?) " + sContrato +
                "ORDER BY l.l_tprecebimento, CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END, " +
                "m.dtvencimento;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                {"date", Dates.toSqlDate(new Date())},
                {"int", iAnoRef}
        });

        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();
        List<BoletasController.pagadores> Pagadores = new ArrayList<>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();

        int b = 1;

        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        LocalDate tvencto = null;
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);
            String t_banco = "";
            TreeItem<BoletasController.pagadores> root = new TreeItem("Bancos");
            TreeItem<BoletasController.pagadores> Lista = null;
            while (rs.next()) {
                String _banco = rs.getString("bcobol");
                int _id = rs.getInt("id");
                String _contrato = rs.getString("contrato");
                String _nome = rs.getString("nomerazao");
                String _vecto = Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"));

                String msgBloq = null;
                msgBloq = new AvisosMensagens().VerificaBloqueio(_contrato);

                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.toLocalDate(Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MAOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";

                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }

                if (!t_banco.equalsIgnoreCase(_banco)) {
                    if (!t_banco.equalsIgnoreCase("")) {
                        root.getChildren().add(Lista);
                    }
                    t_banco = _banco;
                    Lista = new TreeItem(new BoletasController.pagadores(Integer.valueOf(_banco),"","","",false));
                }

                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false)));
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false), tnnumero));
                }

                oldBco = tbcobol; oldBcoNome = tbcobolnome;

                boolean bBloq =  msgBloq != null ? true : false;
                TreeItem<BoletasController.pagadores> lista_Item = new TreeItem(new BoletasController.pagadores(_id, _contrato, _nome, _vecto, bBloq));
                lista_Item.setExpanded(true);
                Lista.getChildren().add(lista_Item);
                Lista.setExpanded(true);

                int pgs = ((b++ * 100) / rcount) + 1;
                gpbListar.setProgress(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }

            if (!t_banco.equalsIgnoreCase("")) {
                root.getChildren().add(Lista);
            }
            boletas.setRoot(root);
            boletas.setShowRoot(false);
            root.setExpanded(true);
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }

        SetDisplayBoletosPrinted(bancosBoletaPrinted);
        SetDisplayBoletos(bancosBoleta);
        SetDisplayBoletosErros(bancosErros);
    }

    public void Todos() {
        gpbListar.setProgress(0);

        String sContrato = "";
        if (!"".equals(gContrato.getText().trim())) { sContrato = " AND m.contrato = '" + gContrato.getText().trim() + "' "; }
        String Sql = "SELECT m.id, m.rgprp rgprp, m.rgimv rgimv, m.contrato contrato, " +
                "CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END nomerazao, " +
                "m.dtvencimento, l.l_tprecebimento nbanco, l_formaenvio envio, b.numero bcobol, " +
                "b.nome bconome, m.nnumero FROM movimento m INNER JOIN locatarios l ON l.l_contrato = m.contrato AND " +
                "(l.l_tprecebimento != 'REC' AND l.l_tprecebimento <> '') " +
                "INNER JOIN bancos b ON b.numero = l.l_tprecebimento WHERE " +
                "EXISTS(SELECT * FROM bancos_digital bd WHERE l.l_tprecebimento = bd.nbanco LIMIT 1) AND " +
                "m.selected = true AND (m.aut_rec = 0 OR m.aut_rec isnull) " +
                sContrato +
                "ORDER BY l.l_tprecebimento, CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END, " +
                "m.dtvencimento;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);

        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();
        List<BoletasController.pagadores> Pagadores = new ArrayList<>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();

        int b = 1;

        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        LocalDate tvencto = null;
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);

            String t_banco = "";
            TreeItem<BoletasController.pagadores> root = new TreeItem("Bancos");
            TreeItem<BoletasController.pagadores> Lista = null;
            while (rs.next()) {
                String _banco = rs.getString("bcobol");
                int _id = rs.getInt("id");
                String _contrato = rs.getString("contrato");
                String _nome = rs.getString("nomerazao");
                String _vecto = Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"));

                String msgBloq = null;
                msgBloq = new AvisosMensagens().VerificaBloqueio(_contrato);

                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.toLocalDate(Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MAOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";

                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }

                if (!t_banco.equalsIgnoreCase(_banco)) {
                    if (!t_banco.equalsIgnoreCase("")) {
                        root.getChildren().add(Lista);
                    }
                    t_banco = _banco;
                    Lista = new TreeItem(new BoletasController.pagadores(Integer.valueOf(_banco),"","","",false));
                }

                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false)));
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false), tnnumero));
                }

                oldBco = tbcobol; oldBcoNome = tbcobolnome;

                boolean bBloq =  msgBloq != null ? true : false;
                TreeItem<BoletasController.pagadores> lista_Item = new TreeItem(new BoletasController.pagadores(_id, _contrato, _nome, _vecto, bBloq));
                lista_Item.setExpanded(true);
                Lista.getChildren().add(lista_Item);
                Lista.setExpanded(true);

                int pgs = ((b++ * 100) / rcount) + 1;
                gpbListar.setProgress(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }

            if (!t_banco.equalsIgnoreCase("")) {
                root.getChildren().add(Lista);
            }
            boletas.setRoot(root);
            boletas.setShowRoot(false);
            root.setExpanded(true);
        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }

        SetDisplayBoletosPrinted(bancosBoletaPrinted);
        SetDisplayBoletos(bancosBoleta);
        SetDisplayBoletosErros(bancosErros);
    }

    public void PorPeriodo(Date dtInicio, Date dtFinal) {
        gpbListar.setProgress(0);

        String sContrato = "";
        if (!"".equals(gContrato.getText().trim())) { sContrato = " AND m.contrato = '" + gContrato.getText().trim() + "' "; }
        String Sql = "SELECT m.id, m.rgprp rgprp, m.rgimv rgimv, m.contrato contrato, " +
                "CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END nomerazao, " +
                "m.dtvencimento, l.l_tprecebimento nbanco, l_formaenvio envio, b.numero bcobol, " +
                "b.nome bconome, m.nnumero FROM movimento m INNER JOIN locatarios l ON l.l_contrato = m.contrato AND " +
                "(l.l_tprecebimento != 'REC' AND l.l_tprecebimento <> '') " +
                "INNER JOIN bancos b ON b.numero = l.l_tprecebimento WHERE " +
                "EXISTS(SELECT * FROM bancos_digital bd WHERE l.l_tprecebimento = bd.nbanco LIMIT 1) AND " +
                "m.selected = true AND (m.aut_rec = 0 OR m.aut_rec isnull) " +
                sContrato + "AND m.dtvencimento::date between ? AND ? " +
                "ORDER BY l.l_tprecebimento, CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END, " +
                "m.dtvencimento;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                {"date",Dates.toSqlDate(dtInicio)}, {"date", Dates.toSqlDate(dtFinal)}
        });

        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();
        List<BoletasController.pagadores> Pagadores = new ArrayList<>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();

        int b = 1;

        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        LocalDate tvencto = null;
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);

            String t_banco = "";
            TreeItem<BoletasController.pagadores> root = new TreeItem("Bancos");
            TreeItem<BoletasController.pagadores> Lista = null;
            while (rs.next()) {
                String _banco = rs.getString("bcobol");
                int _id = rs.getInt("id");
                String _contrato = rs.getString("contrato");
                String _nome = rs.getString("nomerazao");
                String _vecto = Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"));

                String msgBloq = null;
                msgBloq = new AvisosMensagens().VerificaBloqueio(_contrato);

                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.toLocalDate(Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MAOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";

                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }

                if (!t_banco.equalsIgnoreCase(_banco)) {
                    if (!t_banco.equalsIgnoreCase("")) {
                        root.getChildren().add(Lista);
                    }
                    t_banco = _banco;
                    Lista = new TreeItem(new BoletasController.pagadores(Integer.valueOf(_banco),"","","",false));
                }

                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false)));
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false)));
                }

                oldBco = tbcobol; oldBcoNome = tbcobolnome;

                boolean bBloq =  msgBloq != null ? true : false;
                TreeItem<BoletasController.pagadores> lista_Item = new TreeItem(new BoletasController.pagadores(_id, _contrato, _nome, _vecto, bBloq));
                lista_Item.setExpanded(true);
                Lista.getChildren().add(lista_Item);
                Lista.setExpanded(true);

                int pgs = ((b++ * 100) / rcount) + 1;
                gpbListar.setProgress(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }

            if (!t_banco.equalsIgnoreCase("")) {
                root.getChildren().add(Lista);
            }
            boletas.setRoot(root);
            boletas.setShowRoot(false);
            root.setExpanded(true);

        } catch (SQLException ex) {}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }

        SetDisplayBoletosPrinted(bancosBoletaPrinted);
        SetDisplayBoletos(bancosBoleta);
        SetDisplayBoletosErros(bancosErros);
    }

        private void SetDisplayBoletosErros(List<BancosErros> bancosErros) {
        gErros_Contrato.setCellValueFactory(new PropertyValueFactory("contrato"));
        gErros_Contrato.setStyle("-fx-alignment: CENTER;");
        gErros_Nome.setCellValueFactory(new PropertyValueFactory("nome"));
        gErros_Nome.setStyle("-fx-alignment: LEFT;");
        gErros_Vencimento.setCellValueFactory(new PropertyValueFactory("vencimentoRec"));
        gErros_Vencimento.setCellFactory((BancoInter.AbstractConvertCellFactory<BancosErros, LocalDate>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(value));
        gErros_Vencimento.setStyle("-fx-alignment: CENTER;");
        gErros_Codigo.setCellValueFactory(new PropertyValueFactory("nnumero"));
        gErros_Codigo.setStyle("-fx-alignment: CENTER;");
        gErros_Mensagem.setCellValueFactory(new PropertyValueFactory("tipoEnvio"));
        gErros_Mensagem.setStyle("-fx-alignment: LEFT;");

        gErros.setItems(FXCollections.observableArrayList(bancosErros));
    }

    private void SetDisplayBoletosPrinted(List<BancosBoleta> bancosBoleta) {
        gJaImpressos_Contrato.setCellValueFactory(new PropertyValueFactory("contrato"));
        gJaImpressos_Contrato.setStyle("-fx-alignment: CENTER;");
        gJaImpressos_Nome.setCellValueFactory(new PropertyValueFactory("nome"));
        gJaImpressos_Nome.setStyle("-fx-alignment: LEFT;");
        gJaImpressos_Vencimento.setCellValueFactory(new PropertyValueFactory("vencimentoRec"));
        gJaImpressos_Vencimento.setCellFactory((BancoInter.AbstractConvertCellFactory<PessoasBoleta, LocalDate>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(value));
        gJaImpressos_Vencimento.setStyle("-fx-alignment: CENTER;");
        gJaImpressos_NossoNumero.setCellValueFactory(new PropertyValueFactory("nnumero"));
        gJaImpressos_NossoNumero.setStyle("-fx-alignment: CENTER;");
        gBoletos_TipoEnvio.setCellValueFactory(new PropertyValueFactory("tipoEnvio"));
        gBoletos_TipoEnvio.setStyle("-fx-alignment: CENTER;");
        gBoletos_RgPrp.setCellValueFactory(new PropertyValueFactory("rgprp"));
        gBoletos_RgPrp.setVisible(false);
        gBoletos_RgImv.setCellValueFactory(new PropertyValueFactory("rgimv"));
        gBoletos_RgImv.setVisible(false);

        List<PessoasBoleta> data = new ArrayList<>();
        if (bancosBoleta.size() != 0) {
            for (PessoasBoleta item : bancosBoleta.get(0).getPessoasBoleta()) {
                data.add(new PessoasBoleta(
                                item.getContrato(),
                                item.getNome(),
                                item.getVencimentoRec(),
                                item.getVencimentoBol(),
                                item.getTipoEnvio(),
                                item.getRgprp(),
                                item.getRgimv(),
                                item.getTag(),
                                item.getNnumero()
                        )
                );
            }
        }
        gJaImpressos.setItems(FXCollections.observableArrayList(data));
    }

    private void SetDisplayBoletos(List<BancosBoleta> bancosBoleta) {
        gBoletos_Contrato.setCellValueFactory(new PropertyValueFactory("contrato"));
        gBoletos_Contrato.setStyle("-fx-alignment: CENTER;");
        gBoletos_Nome.setCellValueFactory(new PropertyValueFactory("nome"));
        gBoletos_Nome.setStyle("-fx-alignment: LEFT;");
        gBoletos_Vencimento.setCellValueFactory(new PropertyValueFactory("vencimentoRec"));
        gBoletos_Vencimento.setCellFactory((BancoInter.AbstractConvertCellFactory<PessoasBoleta, LocalDate>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(value));
        gBoletos_Vencimento.setStyle("-fx-alignment: CENTER;");
        gBoletos_Boleta.setCellValueFactory(new PropertyValueFactory<PessoasBoleta, LocalDate>("vencimentoBol"));
        gBoletos_Boleta.setCellFactory(LocalDateTableCell::new);
        gBoletos_Boleta.setStyle("-fx-alignment: CENTER;");
        gBoletos_Boleta.setEditable(true);
        gBoletos_TipoEnvio.setCellValueFactory(new PropertyValueFactory("tipoEnvio"));
        gBoletos_TipoEnvio.setStyle("-fx-alignment: CENTER;");
        gBoletos_RgPrp.setCellValueFactory(new PropertyValueFactory("rgprp"));
        gBoletos_RgPrp.setVisible(false);
        gBoletos_RgImv.setCellValueFactory(new PropertyValueFactory("rgimv"));
        gBoletos_RgImv.setVisible(false);
        gBoletos_Tag.setCellValueFactory(new PropertyValueFactory("tag"));
        gBoletos_Tag.setCellFactory( tc -> new CheckBoxTableCell<>());
        gBoletos_Tag.setStyle("-fx-alignment: CENTER;");
        gBoletos_Tag.setEditable(true);

        List<PessoasBoleta> data = new ArrayList<>();
        if (bancosBoleta.size() != 0) {
            for (PessoasBoleta item : bancosBoleta.get(0).getPessoasBoleta()) {
                data.add(new PessoasBoleta(
                                item.getContrato(),
                                item.getNome(),
                                item.getVencimentoRec(),
                                item.getVencimentoBol(),
                                item.getTipoEnvio(),
                                item.getRgprp(),
                                item.getRgimv(),
                                item.getTag()
                        )
                );
            }
        }

        gBoletos.setItems(FXCollections.observableArrayList(data));
        gBoletos.setOnMouseClicked(event -> {
        });
    }

    public void EmDia() {
        gpbListar.setProgress(0);

        String sContrato = "";
        if (!"".equals(gContrato.getText().trim())) { sContrato = " AND m.contrato = '" + gContrato.getText().trim() + "' "; }
        int iAnoRef = gAno.getValue();
        int iMesRef = Dates.MonthToInteger(gMes.getValue());
        String cMesAnoRef = FuncoesGlobais.StrZero(String.valueOf(iMesRef),2) + "/" +
                FuncoesGlobais.StrZero(String.valueOf(iAnoRef), 4);
        String Sql = "SELECT m.id, m.rgprp rgprp, m.rgimv rgimv, m.contrato contrato, " +
                "CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END nomerazao, " +
                "m.dtvencimento, l.l_tprecebimento nbanco, l_formaenvio envio, b.numero bcobol, " +
                "b.nome bconome, m.nnumero FROM movimento m INNER JOIN locatarios l ON l.l_contrato = m.contrato AND " +
                "(l.l_tprecebimento != 'REC' AND l.l_tprecebimento <> '') " +
                "INNER JOIN bancos b ON b.numero = l.l_tprecebimento WHERE " +
                "EXISTS(SELECT * FROM bancos_digital bd WHERE l.l_tprecebimento = bd.nbanco LIMIT 1) AND " +
                "m.selected = true AND (m.aut_rec = 0 OR m.aut_rec isnull) AND m.referencia = ? " +
                sContrato +
                "ORDER BY l.l_tprecebimento, CASE WHEN l.l_fisjur THEN l.l_f_nome ELSE l.l_j_razao END, " +
                "m.dtvencimento;";
        ResultSet rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", cMesAnoRef}});

        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();
        List<BoletasController.pagadores> Pagadores = new ArrayList<>();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();

        int b = 1;

        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        LocalDate tvencto = null;
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = DbMain.RecordCount(rs);

            String t_banco = "";
            TreeItem<BoletasController.pagadores> root = new TreeItem("Bancos");
            TreeItem<BoletasController.pagadores> Lista = null;
            while (rs.next()) {
                String _banco = rs.getString("bcobol");
                int _id = rs.getInt("id");
                String _contrato = rs.getString("contrato");
                String _nome = rs.getString("nomerazao");
                String _vecto = Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"));

                String msgBloq = null;
                msgBloq = new AvisosMensagens().VerificaBloqueio(_contrato);

                //String _banco = "077";
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.toLocalDate(Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";

                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }

                if (!t_banco.equalsIgnoreCase(_banco)) {
                    if (!t_banco.equalsIgnoreCase("")) {
                        root.getChildren().add(Lista);
                    }
                    t_banco = _banco;
                    Lista = new TreeItem(new BoletasController.pagadores(Integer.valueOf(_banco),"","","",false));
                }

                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false)));
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, null, tenvio, trgprp, trgimv, new Boolean(false)));
                }

                oldBco = tbcobol; oldBcoNome = tbcobolnome;

                boolean bBloq =  msgBloq != null ? true : false;
                TreeItem<BoletasController.pagadores> lista_Item = new TreeItem(new BoletasController.pagadores(_id, _contrato, _nome, _vecto, bBloq));
                lista_Item.setExpanded(true);
                Lista.getChildren().add(lista_Item);
                Lista.setExpanded(true);

                int pgs = ((b++ * 100) / rcount) + 1;
                //jProgress2.setValue(pgs);
                try { Thread.sleep(20); } catch (InterruptedException ex) { }
            }

            if (!t_banco.equalsIgnoreCase("")) {
                root.getChildren().add(Lista);
            }
            boletas.setRoot(root);
            boletas.setShowRoot(false);
            root.setExpanded(true);

            // Ler matriz List
        } catch (SQLException ex) {ex.printStackTrace();}
        DbMain.FecharTabela(rs);

        if (!pessoasBoleta.isEmpty()) {
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }

        SetDisplayBoletosPrinted(bancosBoletaPrinted);
        SetDisplayBoletos(bancosBoleta);
        SetDisplayBoletosErros(bancosErros);
    }

    private void Geracao() {
        // Setar bancos para array de numero de bancos
        int nBancos = TreeTableViewNodeCount(boletas.getRoot());
        DadosBanco[] bancos = new DadosBanco[nBancos];
        for (int i=0;i<nBancos;i++) bancos[i] = new DadosBanco();
        int bco = 0;

        TreeItem<BoletasController.pagadores> root = boletas.getRoot();
        for (TreeItem<BoletasController.pagadores> item : root.getChildren()) {
            if (item.getValue().getBloq()) continue;

            // Dados do Banco
            int banco = item.getValue().getId();

            BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco),3));
            if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return; }

            // Dados Bancários
            bancos[bco].setBanco(banco);
            bancos[bco].setBancoDv(new Banco().CalcDigBancoMod11(StrZero(String.valueOf(banco),3)));
            bancos[bco].setBanco_NNumero(String.valueOf(dadosBanco.getNnumero()));
            bancos[bco].setBanco_Aceite("N");
            bancos[bco].setBanco_Agencia(dadosBanco.getAgencia());
            bancos[bco].setBanco_AgenciaDv(String.valueOf(dadosBanco.getAgenciadv()));
            bancos[bco].setBanco_Carteira(dadosBanco.getCarteira());
            bancos[bco].setBanco_CodBenef(dadosBanco.getCedente());
            bancos[bco].setBanco_CodBenefDv(String.valueOf(dadosBanco.getCedentedv()));
            bancos[bco].setBanco_CodMoeda("9");
            bancos[bco].setBanco_Conta(dadosBanco.getConta());
            bancos[bco].setBanco_ContaDv(String.valueOf(dadosBanco.getContadv()));
            bancos[bco].setBanco_Especie("R$");
            bancos[bco].setBanco_EspecieDoc("REC");
            bancos[bco].setBanco_TamanhoNnumero(17);
            bancos[bco].setBanco_LocalPagamentoLinha1("Este boleto poderá ser pago em qualquer banco.".toUpperCase());
            bancos[bco].setBanco_LocalPagamentoLinha2("Após o vencimento seguir intruções do Beneficiário.".toUpperCase());
            bancos[bco].setBanco_Lote(dadosBanco.getLote());

            // Dados do Beneficiario - Imobiliária
            bancos[bco].setBenef_Codigo("");
            try {bancos[bco].setBenef_Razao((!VariaveisGlobais.pb_razao_noprint) ? conn.LerParametros("da_razao") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Fantasia(conn.LerParametros("da_fanta"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_CNPJ((!VariaveisGlobais.pb_cnpj_noprint) ? conn.LerParametros("da_cnpj") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Inscricao(conn.LerParametros("da_insc"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_InscTipo(conn.LerParametros("da_tipo"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_Endereco((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_ender") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Numero((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_numero") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Complto((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cplto") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Bairro((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_bairro") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Cidade((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cidade") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Estado((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_estado") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Cep((!VariaveisGlobais.pb_endereco_noprint) ? conn.LerParametros("da_cep") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Telefone((!VariaveisGlobais.pb_telefone_noprint) ? conn.LerParametros("da_tel") : "");} catch (SQLException e) {}
            try {bancos[bco].setBenef_Email(conn.LerParametros("da_email"));} catch (SQLException e) {}
            try {bancos[bco].setBenef_HPage(conn.LerParametros("da_hpage"));} catch (SQLException e) {}
            // Não esquecer de dimensionar o logo pb_logo_width/pb_logo_height
            try {bancos[bco].setBenef_Logo(new Image(new File(conn.LerParametros("da_logo")).toURI().toString()));} catch (SQLException e) {}
            // Parametros da QCR

            int pag = 0; int nPag = TreeTableViewNodeCount(item);
            Pagador[] pagador = new Pagador[nPag]; for (int i = 0; i<nPag; i++) pagador[i] = new Pagador();
            for (TreeItem<BoletasController.pagadores> subitem : item.getChildren()) {
                // Dados do Locatario
                Locatarios dadosLoc = new Banco(null,null).LerDadosLocatario(subitem.getValue().getContrato());
                pagador[pag].setBanco(banco);
                pagador[pag].setCodigo(subitem.getValue().getContrato()); // Registro do Proprietário
                pagador[pag].setRazao(dadosLoc.getRazao());
                pagador[pag].setFantasia(dadosLoc.getFantasia());
                pagador[pag].setCNPJ(dadosLoc.getCpfcnpj());
                pagador[pag].setEndereco(dadosLoc.getEndereco());
                pagador[pag].setNumero(dadosLoc.getNumero());
                pagador[pag].setComplto(dadosLoc.getComplemento());
                pagador[pag].setBairro(dadosLoc.getBairro());
                pagador[pag].setCidade(dadosLoc.getCidade());
                pagador[pag].setEstado(dadosLoc.getEstado());
                pagador[pag].setCep(dadosLoc.getCep());
                pagador[pag].setTelefone(dadosLoc.getTelefone());
                pagador[pag].setEmail(dadosLoc.getEmail());
                pagador[pag].setEnvio(dadosLoc.getEnvio());

                pagador[pag].setRc_Codigo(subitem.getValue().getContrato());
                pagador[pag].setRc_NNumero("");
                pagador[pag].setRc_DtDocumento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
                pagador[pag].setRc_NumDocumento(subitem.getValue().getContrato());
                pagador[pag].setRc_DtProcessamento(Dates.DateFormata("dd/MM/yyyy", DbMain.getDateTimeServer()));
                pagador[pag].setRc_Vencimento(subitem.getValue().getVencto());

                pagador[pag].setRc_Dados(new String[][] {}); // {{cod, desc, cp, valor}, ...}
                pagador[pag].setRc_mensagem("");

                pagador[pag].setRc_instrucao01("");
                pagador[pag].setRc_instrucao02("");
                pagador[pag].setRc_instrucao03("");
                pagador[pag].setRc_instrucao04("");
                pagador[pag].setRc_instrucao05("");
                pagador[pag].setRc_instrucao06("");
                pagador[pag].setRc_instrucao07("");
                pagador[pag].setRc_instrucao08("");
                pagador[pag].setRc_instrucao09("");
                pagador[pag].setRc_instrucao10("");

                pagador[pag].setRc_linhaDIgitavel("");
                pagador[pag].setRc_codigoBarras("");

                Banco vbanco = new Banco(subitem.getValue().getContrato(),subitem.getValue().getVencto());
                List<tbvAltera> dados = vbanco.ProcessaCampos();
                pagador[pag].setRc_Valor(dados.get(dados.size() -1).getValor());

                pag++;
            }
            //System.out.println("");

            // Setar pagadores
            bancos[bco].setBenef_pagadores(pagador);

            // Processa dados
            Object vbanco;
            switch (bancos[bco].getBanco()) {
                case 1: // Bb
                    vbanco = new Brasil(bancos[bco]);
                    ((Brasil) vbanco).Processa();
                    break;
                case 341: // Itau
                    vbanco = new Itau(bancos[bco]);
                    ((Itau) vbanco).Processa();
                    break;
                case 33: // Santander
                    vbanco = new Santander(bancos[bco]);
                    ((Santander) vbanco).Processa();
                    break;
                case 77: // Santander
                    vbanco = new Santander(bancos[bco]);
                    ((Santander) vbanco).Processa();
                    break;
                case 247: // Bradesco
                    vbanco = new Bradesco(bancos[bco]);
                    ((Bradesco) vbanco).Processa();
                    break;
                case 104: // Cef
                    vbanco = new Cef(bancos[bco]);
                    ((Cef) vbanco).Processa();
                    break;
                default: // Digital
                    vbanco = new Digital(bancos[bco]);
                    ((Digital) vbanco).Processa();
            }

            bco++;

        }

        // Chama Grade
        for (DadosBanco banco : bancos ) {
/*
            String rgprp = banco. p.getRgprp();
            String rgimv = p.getRgimv();
            String contrato = p.getContrato();
            Date vencto = Dates.toDate(p.getVencimentoRec());
            Date vectoBol = Dates.toDate(p.getVencimentoBol());
*/

            Boleta Bean1 = null;
//            try {
                Bean1 = CreateBoleta(banco);

                if (Bean1 != null) {
                    // Gravar no arquivo Boletas
/*
                    String cSql = "INSERT INTO bloquetos (`rgprp`,`rgimv`,`contrato`," +
                            "`nome`,`vencimento`,`valor`,`nnumero`,`remessa`) " +
                            "VALUES (\"&1.\",\"&2.\",\"&3.\",\"&4.\",\"&5.\",\"&6.\",\"&7.\",\"&8.\")";
                    cSql = FuncoesGlobais.Subst(cSql, new String[] {
                            rgprp,
                            rgimv,
                            contrato,
                            Bean1.getsacDadosNome(),
                            Dates.DateFormata("yyyy-MM-dd", vencto),
                            Bean1.getbolDadosVrdoc(),
                            Bean1.getbolDadosNnumero(),
                            "S"
                    });
                    try {
                        if (conn.ExisteTabelaBloquetos()) conn.ExecutarComando(cSql);
                    } catch (Exception e) {e.printStackTrace();}
*/

                    List<Boleta> lista = new ArrayList<Boleta>();
                    lista.add(Bean1);

                    JRDataSource jrds = new JRBeanCollectionDataSource(lista);
                    try {
                        String fileName = "reports/Boletos.jasper";
                        JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

                        // Create a PDF exporter
                        JRExporter exporter = new JRPdfExporter();

                        if (!new File("reports/BoletasDigital/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/").exists()) return;
                        String pathName = "reports/BoletasDigital/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

/*
                        // Configure the exporter (set output file name and print object)
                        String outFileName = pathName + contrato + "_" + Bean1.getsacDadosNome() + "_" + vencto + "_" + Bean1.getbolDadosNnumero().substring(0,11) + ".pdf";
                        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
*/

                        try {
/*
                            String recUpdSql = "UPDATE RECIBO SET boletapath = '" + outFileName + "' WHERE contrato = '" + contrato + "' AND " +
                                    "dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", vencto) + "';";
                            conn.ExecutarComando(recUpdSql);
*/
                        } catch (Exception err) {err.printStackTrace();}
                            // Export the PDF file
                            exporter.exportReport();
                        } catch (JRException e) {
                            e.printStackTrace();
                            System.exit(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                    }
                } else {
                    //bancosErros.add(new BancosErros(banco.getBenef_Codigo(),banco.getBenef_Razao(), Dates.DateFormata("dd-MM-yyyy", Dates.toDate(Dates.toLocalDate(new Date()))), codErro, msgErro)); //*
                }
//            } catch (SQLException ex) {}
        }
    }

    
    private Boleta CreateBoleta(DadosBanco banco) {
        bancos bcos = new bancos("077");
        classInter pagador = new classInter();

        // Bancos Erros
        List<BancosErros> bancosErros = new ArrayList<BancosErros>();

        List<Boleta> lista = new ArrayList<Boleta>();
        Pagador[] pagadores = banco.getBenef_pagadores();
        Boleta beans = null;
        for (Pagador paga : pagadores) {
            //if (!EstaNaGrade(boleta, paga.getRazao(),true)) continue;

            beans = new Boleta();
            beans.setempNome(banco.getBenef_Razao());
            beans.setempEndL1(!banco.getBenef_Endereco().equalsIgnoreCase("") ? banco.getBenef_Endereco() + ", " + banco.getBenef_Numero() + " " + banco.getBenef_Complto() + " - " + banco.getBenef_Bairro() : "");
            beans.setempEndL2(!banco.getBenef_Endereco().equalsIgnoreCase("") ? banco.getBenef_Cidade() + " - " + banco.getBenef_Estado() + " - CEP " + banco.getBenef_Cep() : "");
            beans.setempEndL3(!banco.getBenef_Telefone().equalsIgnoreCase("") ? "Tel/Fax.: " + banco.getBenef_Telefone() : "");
            beans.setempEndL4(banco.getBenef_HPage() + " / " + banco.getBenef_Email());

            beans.setbolDadosCedente(new Pad(banco.getBenef_Razao(),50).RPad() + (!VariaveisGlobais.pb_cnpj_noprint ? "CNPJ: " + banco.getBenef_CNPJ() : ""));
            pagador.setCnpjCPFBeneficiario(banco.getBenef_CNPJ());

            beans.setlogoBanco(System.getProperty("user.dir") + "/resources/logoBancos/" + StrZero(String.valueOf(banco.getBanco()), 3) + ".jpg");
            beans.setnumeroBanco(StrZero(String.valueOf(banco.getBanco()), 3) + "-" + banco.getBancoDv());

            // Logo da Imobiliaria
            beans.setlogoLocation(banco.getBenef_Logo().impl_getUrl());

            beans.setbcoMsgL01(banco.getBanco_LocalPagamentoLinha1());
            beans.setbcoMsgL02(banco.getBanco_LocalPagamentoLinha2());

            boolean maniv = new AvisosMensagens().VerificaAniLocatario(paga.getRc_NumDocumento());
            beans.setlocaMsgL01(maniv && VariaveisGlobais.am_aniv ? "Este é o mês do seu aniversário. PARABÉNS!" : "");
            pagador.setMensagem_linha4(maniv && VariaveisGlobais.am_aniv ? "Este é o mês do seu aniversário. PARABÉNS!" : "");

            Object[] mreaj = null;
            try {
                mreaj = new AvisosMensagens().VerificaReajuste(paga.getRc_NumDocumento());
            } catch (NullPointerException nex) {}
            beans.setlocaMsgL02((Boolean)mreaj[1] && VariaveisGlobais.am_reaj ? (String)mreaj[0] : "");
            pagador.setMensagem_linha1((Boolean)mreaj[1] && VariaveisGlobais.am_reaj ? (String)mreaj[0] : "");
            //pagador.setMensagem_linha1("Calculos feitos em referencia a data original de Vencimento " + paga.getRc_Vencimento());

            beans.setlocaMsgL03("");
            beans.setlocaMsgL04("");

            // Dados do Boleto
            int linha = 1;
            for (int i = 0; i < paga.getRc_Dados().length - 1; i++) locaDesc(beans, paga.getRc_Dados()[i], linha++);

            BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco.getBanco()),3));
            if (dadosBanco == null) { System.out.println("Banco não cadastrado!"); return null; }

            String vrBoleta = paga.getRc_Dados()[paga.getRc_Dados().length - 1][3];
            if (VariaveisGlobais.bol_txbanc) vrBoleta = LerValor.floatToCurrency(LerValor.StringToFloat(vrBoleta) + (VariaveisGlobais.bol_txbanc ? LerValor.StringToFloat(dadosBanco.getTarifa()) : 0f), 2);
            beans.setbolDadosValor(vrBoleta);

            beans.setbolDadosVencimento(paga.getRc_Vencimento());
            pagador.setDataVencimento(paga.getRc_Vencimento());

            beans.setbolDadosVrdoc(vrBoleta);
            pagador.setValorNominal(vrBoleta.replace(".","").replace(",", "."));
            pagador.setValorAbatimento("0");

            beans.setbolDadosDatadoc(paga.getRc_DtDocumento());

            beans.setbolDadosDtproc(paga.getRc_DtProcessamento());
            pagador.setDataEmissao(paga.getRc_DtProcessamento());

            beans.setbolDadosNumdoc(paga.getRc_NumDocumento());
            pagador.setSeuNumero(paga.getRc_NumDocumento());  //*

            beans.setbolDadosEspecie(banco.getBanco_Especie());
            beans.setbolDadosEspeciedoc(banco.getBanco_EspecieDoc());
            beans.setbolDadosAceite(banco.getBanco_Aceite());
            beans.setbolDadosCarteira(banco.getBanco_Carteira());
            beans.setbolDadosAgcodced(banco.getBanco_Agencia() + "/" + banco.getBanco_CodBenef() + "-" + banco.getBanco_CodBenefDv() );

            // Dados do Pagador
            beans.setsacDadosNome(paga.getRazao());
            pagador.setNome(paga.getRazao());

            beans.setsacDadosEndereco(paga.getEndereco());
            pagador.setEndereco(paga.getEndereco());

            beans.setsacDadosNumero(paga.getNumero());
            pagador.setNumero(paga.getNumero());

            beans.setsacDadosCompl(paga.getComplto());
            pagador.setComplemento(paga.getComplto());

            beans.setsacDadosBairro(paga.getBairro());
            pagador.setBairro(paga.getBairro());

            beans.setsacDadosCidade(paga.getCidade());
            pagador.setCidade(paga.getCidade());

            beans.setsacDadosEstado(paga.getEstado());
            pagador.setUf(paga.getEstado());

            beans.setsacDadosCep(paga.getCep());
            pagador.setCep(paga.getCep());

            String _cpfcnpj = paga.getCNPJ().length() <= 14 ? "CPF: " + paga.getCNPJ() : "CNPJ: " + paga.getCNPJ();
            beans.setsacDadosCpfcnpj(_cpfcnpj);
            pagador.setCnpjCpf(paga.getCNPJ());
            pagador.setTipopessoa(pagador.getCnpjCpf().length() == 11 ? pagador.inter_PAGADOR_TIPOPESSOA_FISICA : pagador.inter_PAGADOR_TIPOPESSOA_JURIDICA);

            beans.setbolDadosMsg01(MsgChanges(VariaveisGlobais.pb_int01, paga));

            beans.setbolDadosMsg02(MsgChanges(VariaveisGlobais.pb_int02, paga));
            pagador.setMensagem_linha2(MsgChanges(VariaveisGlobais.pb_int04, paga));

            beans.setbolDadosMsg03(MsgChanges(VariaveisGlobais.pb_int03, paga));
            pagador.setMensagem_linha3(MsgChanges(VariaveisGlobais.pb_int05, paga));

            beans.setbolDadosMsg04(MsgChanges(VariaveisGlobais.pb_int04, paga));
            pagador.setMensagem_linha4(MsgChanges(VariaveisGlobais.pb_int08, paga));

            beans.setbolDadosMsg05(MsgChanges(VariaveisGlobais.pb_int05, paga));
            pagador.setMensagem_linha5(MsgChanges(VariaveisGlobais.pb_int09, paga));

            beans.setbolDadosMsg06(MsgChanges(VariaveisGlobais.pb_int06, paga));
            beans.setbolDadosMsg07(MsgChanges(VariaveisGlobais.pb_int07, paga));
            beans.setbolDadosMsg08(MsgChanges(VariaveisGlobais.pb_int08, paga));
            beans.setbolDadosMsg09(MsgChanges(VariaveisGlobais.pb_int09, paga));
            beans.setbolDadosMsg10(MsgChanges(VariaveisGlobais.bol_txbanc ? "Incluso Tarifa bancária $F{TXBANCO}" : "", paga));

            pagador.setDesconto1_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
            pagador.setDesconto1_data("");
            pagador.setDesconto1_taxa("0");
            pagador.setDesconto1_valor("0");

            pagador.setDesconto2_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
            pagador.setDesconto2_data("");
            pagador.setDesconto2_taxa("0");
            pagador.setDesconto2_valor("0");

            pagador.setDesconto3_codigoDesconto(pagador.inter_DESCONTO_NAOTEMDESCONTO);
            pagador.setDesconto3_data("");
            pagador.setDesconto3_taxa("0");
            pagador.setDesconto3_valor("0");

            beans.setbolDadosMora("");

            // Multa
            pagador.setMulta_codigoMulta(pagador.inter_MULTA_PERCENTUAL);
            pagador.setMulta_data(Dates.DateFormata("dd/MM/yyyy",
                    Dates.DateAdd(Dates.DIA, 1, Dates.StringtoDate(paga.getRc_Vencimento(),"dd-MM-yyyy"))));

            pagador.setMulta_taxa(String.valueOf(10));
            pagador.setMulta_valor("0");

            // Mora
            pagador.setMora_codigoMora(pagador.inter_MORA_TAXAMENSAL);
            pagador.setMora_data(Dates.DateFormata("dd/MM/yyyy",
                    Dates.DateAdd(Dates.DIA, 1, Dates.StringtoDate(paga.getRc_Vencimento(),"dd-MM-yyyy"))));
            pagador.setMora_taxa(String.valueOf(0.033));
            pagador.setMora_valor("0");

            pagador.setDataLimite(pagador.inter_NUNDIASAGENDA_TRINTA);
            pagador.setNumDiasAgenda(pagador.inter_NUNDIASAGENDA_TRINTA);

                /*
                / Registrar boleta no banco
                */
            String url_ws = "https://apis.bancointer.com.br/openbanking/v1/certificado/boletos";
            String path = bcos.getBanco_CertPath();
            String path_crt = path + bcos.getBanco_CrtFile();
            String path_key = path + bcos.getBanco_KeyFile();

            boolean pegueiBanco = true;
            Inter c  = new Inter();
            Object[] message = null;
            try {
                message = c.insertBoleta(url_ws, path_crt, path_key, pagador.getJSONBoleto());
            } catch (Exception e) { pegueiBanco = false; }

            codErro = c.getCodErro();
            msgErro = c.getMsgErro();

            if (!pegueiBanco) {
                bancosErros.add(new BancosErros(pagador.getSeuNumero(),pagador.getNome(), Dates.DateFormata("dd-MM-yyyy", Dates.toDate(Dates.toLocalDate(Dates.StringtoDate(pagador.getDataVencimento(),"dd-MM-yyyy")))), codErro, msgErro));
            }

            int statusCode = (int)message[0];
            String[] infoMessage = (String[])message[1];
            if ((int)message[0] != 200) {
                System.out.println("Codigo do Erro:" + statusCode + " - " + (infoMessage != null ? infoMessage[0] : ""));
                //return null;
            } else {
                beans.setbolDadosNnumero(infoMessage[0]);
                beans.setcodDadosBarras(infoMessage[1]);
                beans.setcodDadosDigitavel(infoMessage[2]);
            }
        }

/*
        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Boletos");
        // new toPrint(pdfName,"LASER","INTERNA");
        new PdfViewer("Preview do Boleto", pdfName);
*/

        return beans;
    }

        private void locaDesc(Boleta beans, String[] texto, int linha) {
        if (linha < 1 && linha > 14) linha = 1;
        switch (linha) {
            case 1: beans.setlocaDescL01(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL01(texto[2]);beans.setlocaVrL01(texto[3]);break;
            case 2: beans.setlocaDescL02(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL02(texto[2]);beans.setlocaVrL02(texto[3]);break;
            case 3: beans.setlocaDescL03(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL03(texto[2]);beans.setlocaVrL03(texto[3]);break;
            case 4: beans.setlocaDescL04(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL04(texto[2]);beans.setlocaVrL04(texto[3]);break;
            case 5: beans.setlocaDescL05(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL05(texto[2]);beans.setlocaVrL05(texto[3]);break;
            case 6: beans.setlocaDescL06(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL06(texto[2]);beans.setlocaVrL06(texto[3]);break;
            case 7: beans.setlocaDescL07(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL07(texto[2]);beans.setlocaVrL07(texto[3]);break;
            case 8: beans.setlocaDescL08(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL08(texto[2]);beans.setlocaVrL08(texto[3]);break;
            case 9: beans.setlocaDescL09(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL09(texto[2]);beans.setlocaVrL09(texto[3]);break;
            case 10: beans.setlocaDescL10(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL10(texto[2]);beans.setlocaVrL10(texto[3]);break;
            case 11: beans.setlocaDescL11(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL11(texto[2]);beans.setlocaVrL11(texto[3]);break;
            case 12: beans.setlocaDescL12(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL12(texto[2]);beans.setlocaVrL12(texto[3]);break;
            case 13: beans.setlocaDescL13(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL13(texto[2]);beans.setlocaVrL13(texto[3]);break;
            case 14: beans.setlocaDescL14(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL14(texto[2]);beans.setlocaVrL14(texto[3]);break;
            default: beans.setlocaDescL01(texto[1]);if (VariaveisGlobais.pb_copa_print) beans.setlocaCpL01(texto[2]);beans.setlocaVrL01(texto[3]);
        }
    }

    private String MsgChanges(String msg, Pagador banco) {
        String retorno = msg;
        retorno = retorno.replace("$F{VENCIMENTO}", banco.getRc_Vencimento());
        retorno = retorno.replace("$F{CARENCIA}",String.valueOf(VariaveisGlobais.ca_multa));
        retorno = retorno.replace("$F{VENCCARENC}", Dates.DateFormata("dd/MM/yyyy", Dates.sqlDateAdd(Dates.DIA,VariaveisGlobais.ca_multa,Dates.StringtoDate(banco.getRc_Vencimento(),"dd/MM/yyyy"))));
        retorno = retorno.replace("$F{MULTA}", String.valueOf(VariaveisGlobais.mu_res));
        retorno = retorno.replace("$F{ENCARGOS}",String.valueOf(VariaveisGlobais.co_perc));
        retorno = retorno.replace("$F{JUROS}",String.valueOf(VariaveisGlobais.ju_percent));
        retorno = retorno.replace("$F{CORRECAO}",String.valueOf(VariaveisGlobais.co));
        retorno = retorno.replace("$F{VALORBOLETA}",banco.getRc_Valor());

        BancoBoleta dadosBanco = new Banco(null,null).LerBancoBoleta(StrZero(String.valueOf(banco.getBanco()),3));
        if (dadosBanco == null) { System.out.println("Banco não cadastrado!");return retorno; }
        retorno = retorno.replace("$F{TXBANCO}","R$ " + dadosBanco.getTarifa());

        return retorno;
    }

    private int TreeTableViewNodeCount(TreeItem<BoletasController.pagadores> node) {
        int i = 0;
        try {
            for (TreeItem<BoletasController.pagadores> item : node.getChildren()) {
                i += 1;
            }
        } catch (Exception e) {}
        return i;
    }
    
}
