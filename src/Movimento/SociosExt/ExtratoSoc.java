package Movimento.SociosExt;

import Calculos.AutentMult;
import Classes.jExtrato;
import Funcoes.Collections;
import Funcoes.*;
import Movimento.Extrato.ExtratoBloqClass;
import Objetos.JasperViewerFX.JasperViewerFX;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.controlsfx.control.textfield.TextFields;
import pdfViewer.PdfViewer;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class ExtratoSoc implements Initializable {
    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;

    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField eCodigo;
    @FXML private TextField eNome;
    @FXML private AnchorPane eExtratoPdf;
    @FXML private Button ebtnPrint;
    @FXML private Spinner<Integer> socAno;

    @FXML private TableView<cExtSoc> socLista;
    @FXML private TableColumn<cExtSoc, Integer> socRegistro;
    @FXML private TableColumn<cExtSoc, Date> socData;
    @FXML private TableColumn<cExtSoc, Double> socAut;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2021);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        socAno.setValueFactory(ano);
        socAno.valueProperty().addListener((observable, oldValue, newValue) -> {
            FillExtSocFec();
        });

        socLista.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && socLista.getSelectionModel().getSelectedItem() != null) {
                pExtratoSegVia(socLista.getSelectionModel().getSelectedItem().getAut());
            }
        });

        AutocompleteContratoNome();

        ebtnPrint.setOnAction(event -> {
            BigInteger aut = conn.PegarAutenticacao();
            BigDecimal sdSoc = pExtrato(aut);

            // AVISOS
            String avsql = "select id, registro, tipo, texto, valor, dtrecebimento, aut_rec from avisos where registro = '%s' and aut_rec <> 0 and aut_pag is not null and aut_pag[1][2] is null and conta = 3  and (reserva is null or reserva[1][1] = '" + VariaveisGlobais.usuario + "') order by 1;";
            avsql = String.format(avsql, eCodigo.getText());
            ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
            try {
                while (avrs.next()) {
                    Object[][] avAut = new AutentMult().PegaAutentMult_Avi(avrs.getString("registro"),
                            "avisos",
                            avrs.getInt("aut_rec")
                    );

                    avAut = new AutentMult().UpgradeAutent_Avi(
                            avAut, avrs.getString("registro").toString() ,aut.intValue(), new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario
                    );
                    String savAut = new AutentMult().ObjectArrays2String(avAut);
                    //System.out.println(savAut);
                    conn.ExecutarComando("UPDATE avisos SET aut_pag = '" + savAut + "' WHERE id = " + avrs.getInt("id") );
                }
            } catch (Exception e) {e.printStackTrace();}
            try { avrs.close(); } catch (Exception e) {}


            // SALDOPROP
            String sdsql = "select id, registro, valor from adm_socios_saldo where registro = ? and aut_pag is not null AND aut_pag[1][2] is null order by 1;";
            ResultSet sdrs = conn.AbrirTabela(sdsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",eCodigo.getText()}});
            try {
                while (sdrs.next()) {
                    Object[][] sdAut = {{sdrs.getString("registro"),aut.intValue(),new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()),VariaveisGlobais.usuario}};
                    String ssdAut = new AutentMult().ObjectArrays2String(sdAut);
                    //Object[][] param = new Object[][] {{"decimal", controller.getValorPago()}, {"int", sdrs.getInt("id")}};
                    Object[][] param = new Object[][] {{"decimal", new BigDecimal("0")}, {"int", sdrs.getInt("id")}};
                    String saldoSQL = "UPDATE propsaldo SET aut_pag = '%s', valorpago = ? WHERE id = ?";
                    saldoSQL = String.format(saldoSQL,ssdAut);
                    conn.ExecutarComando(saldoSQL, param);
                }
            } catch (Exception e) {e.printStackTrace();}
            try { sdrs.close(); } catch (Exception e) {}

            // Gravação Novo Saldo do Proprietário
            //BigDecimal saldo = controller.GetResta();
            BigDecimal saldo = sdSoc;
            String iSql = "INSERT INTO adm_socios_saldo (registro, valor, aut_pag) VALUES ('%s',%s,'{{%s,null,null,\"\"}}');";
            iSql = String.format(iSql, eCodigo.getText(), saldo, eCodigo.getText());
            /*if (saldo.floatValue() > 0)*/ try {conn.ExecutarComando(iSql);} catch (Exception e) {}

            Platform.runLater(() -> eCodigo.requestFocus());
        });

        //eDtFin.setValue(Dates.toLocalDate(Dates.StringtoDate(Dates.ultDiaMes(DbMain.getDateTimeServer()) + "/" + Dates.DateFormata("MM/yyyy", DbMain.getDateTimeServer()), "dd/MM/yyyy")));
        Platform.runLater(() -> eCodigo.requestFocus());
        Platform.runLater(() -> FillExtSocFec());
    }

    private void FillExtSocFec() {
        List<cExtSoc> data = new ArrayList<cExtSoc>();
        String Sql = "select distinct aut_pag[1][1]::int registro, aut_pag[1][2]::float aut, aut_pag[1][3]::date dtrec from avisos where conta = 3 and not aut_pag[1][1] is null and extract(YEAR FROM aut_pag[1][3]::date) = ? ORDER BY aut_pag[1][1]::int, aut_pag[1][2]::float;";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", socAno.getValue()}});
            int gRegistro = -1; Date gVencto = null; String gAut = null;

            while (rs.next()) {
                try {gRegistro = rs.getInt("registro");} catch (SQLException sqlex) {}
                try {gVencto = rs.getDate("dtrec");} catch (SQLException sqlex) {}
                try {gAut = rs.getString("aut");} catch (SQLException sqlex) {}

                data.add(new cExtSoc(gRegistro, gVencto, gAut));
            }
        } catch (Exception e) { e.printStackTrace();}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        socRegistro.setCellValueFactory(new PropertyValueFactory<>("registro"));

        socData.setCellValueFactory(new PropertyValueFactory<>("data"));
        socData.setCellFactory((AbstractConvertCellFactory<cExtSoc, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        socData.setStyle( "-fx-alignment: CENTER;");

        socAut.setCellValueFactory(new PropertyValueFactory<>("aut"));

        socLista.getItems().clear();
        if (!data.isEmpty()) {
            socLista.setItems(FXCollections.observableArrayList(data));
        }
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

    private void Report(List<jExtrato> lista, BigInteger aut) {
        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = aut.floatValue() != 0 ? dadm.get("marca") +
                FuncoesGlobais.StrZero(String.valueOf(aut),10) +
                Dates.DateFormata("ddMMyyyyHHmm", DbMain.getDateTimeServer())  +
                " " + VariaveisGlobais.usuario : "";

        Map parametros = new HashMap();
        parametros.put("Logo",dadm.get("logo"));
        parametros.put("rgprp", eCodigo.getText());
        parametros.put("nomeProp", eNome.getText());
        parametros.put("NumeroExtrato", 1);
        parametros.put("Mensagem", "Extrato de Sócios");
        parametros.put("Banco","");
        parametros.put("Autenticacao",autenticacao);
        parametros.put("ShowSaldo", true);

        if (aut.intValue() == 0) {
            JasperPrint jasperPrint=null;
            try {
                JRDataSource jrds = new JRBeanCollectionDataSource(lista);
                String reportFileName = System.getProperty("user.dir") + "\\Reports\\jExtrato.jasper";
                JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportFileName);
                jasperPrint = JasperFillManager.fillReport(reporte, parametros, jrds);
            } catch (JRException e) { e.printStackTrace(); }

            String bStyle = "-fx-background-color: cornsilk;";
            bStyle += "-fx-background-radius: 5 5 5 5;";
            bStyle += "-fx-border-color: black;";
            bStyle += "-fx-border-radius: 5 5 5 5;";
            bStyle += "-fx-border-width: 1;";
            JasperViewerFX jrv = new JasperViewerFX((int) eExtratoPdf.getWidth(), (int) eExtratoPdf.getHeight(), bStyle, lista, parametros);
            jrv.viewReport(jasperPrint);
            eExtratoPdf.getChildren().add(jrv);
        } else {
            String pdfName = new PdfViewer().GeraPDFTemp(lista,"jExtrato", parametros);
            new toPrint(pdfName,"LASER","INTERNA");
        }
    }

    private void AutocompleteContratoNome() {
        ResultSet imv = null;
        String qSQL = "SELECT id, nome FROM adm_socios ORDER BY id;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("id");} catch (SQLException e) {}
                try {qnome = imv.getString("nome");} catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(eCodigo, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(eNome, possibleSuggestionsNome);

        eCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                eCodigo.setText(null);
                eNome.setText(null);
                try {eExtratoPdf.getChildren().remove(0);} catch (IndexOutOfBoundsException iex) {}
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = eCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, eCodigo.getText());
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        eNome.setText(_possibleSuggestions[pos][1]);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        eNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions,1, eNome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {pcontrato = eCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions,1,eNome.getText())) {
                        eCodigo.setText(_possibleSuggestions[pos][0]);
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }
                pExtrato(new BigInteger("0"));
            }
        });
    }

    private String LoadExtratoSocioNumber(String rgprp) {
        String retorno = "0000000000";
        String loadSQL = "SELECT Count(registro) AS taut FROM adm_socios_saldo WHERE registro = ?";
        ResultSet loadRS = null;
        try {
            loadRS = conn.AbrirTabela(loadSQL, ResultSet.CONCUR_READ_ONLY,new Object[][] {{"string", rgprp}});
            while (loadRS.next()) {
                retorno += String.valueOf(loadRS.getInt("taut") + 1);
            }
            if (retorno.equalsIgnoreCase("0000000000")) retorno = "0000000001";
        } catch (SQLException SQLex) {}
        try { loadRS.close(); } catch (SQLException SQLex) {}
        return retorno.substring(retorno.length() - 10);
    }

    private BigDecimal pExtrato(BigInteger aut) {
        ObservableList<ExtratoBloqClass> bloqdata = FXCollections.observableArrayList();

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM adm_socios_saldo Where registro = '%s' and aut_pag is not null AND aut_pag[1][2] is null;";
        saSql = String.format(saSql,eCodigo.getText());
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}
        if (ttCR.floatValue() != 0) {
            Extrato = new jExtrato(Descr("<b>Saldo Anterior</b>"), ttCR, null);
            lista.add(Extrato);

            // Pula linha
            Extrato = new jExtrato(null, null, null);
            lista.add(Extrato);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where registro = '%s' and aut_rec <> 0 and aut_pag is not null and aut_pag[1][2] is null and conta = 3 and (reserva is null or reserva[1][1] = '" +
                VariaveisGlobais.usuario + "') order by aut_rec;";
        avsql = String.format(avsql, eCodigo.getText());
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    bloqdata.add(new ExtratoBloqClass(
                            "A",
                            avrs.getString("registro"),
                            avrs.getString("texto"),
                            "-"
                    ));
                    continue;
                }

                Font font = new Font("SansSerif",Font.PLAIN,8);
                Canvas c = new Canvas();
                FontMetrics fm = c.getFontMetrics(font);
                String aLinhas[] = WordWrap.wrap(avrs.getString("texto") + "  " + new SimpleDateFormat("dd/MM/yyyy").format(avrs.getDate("dtrecebimento")) + " - " + avrs.getString("aut_rec"), 230, fm).split("\n");
                for (int k=0;k<aLinhas.length;k++) {
                    BigDecimal lcr = null, ldb = null;
                    lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                    ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;
                    Extrato = new jExtrato(Descr(aLinhas[k]), k == aLinhas.length - 1 ? lcr : null, k == aLinhas.length - 1 ? ldb : null);
                    lista.add(Extrato);
                }

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));

                // Pula Linha
                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Créditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de Déditos</b></font>"), null, ttDB);
        lista.add(Extrato);

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        String tDesc = "";
        if (ttSld.floatValue() > 0) {
            tDesc = "<font color=blue><b>Líquido a Receber</b></font>";
        } else {
            tDesc = "<font color=red><b>Líquido a Receber</b></font>";
        }
        Extrato = new jExtrato(Descr(tDesc), ttSld.floatValue() > 0 ? ttSld : null, ttSld.floatValue() < 0 ? ttSld : null);
        lista.add(Extrato);

        Extrato = new jExtrato(null,  null, null);
        lista.add(Extrato);

        // complementa com linhas em branco para preencher a página
        int npag = lista.size() % 32;
        for (int i=1;i<=(32 - npag);i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }

        Report(lista, aut);

        return ttSld;
    }

    private String Descr(String desc) { return "<html>" + desc + "</html>"; }

    private void pExtratoSegVia(String aut) {
        ObservableList<ExtratoBloqClass> bloqdata = FXCollections.observableArrayList();

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        List<jExtrato> lista = new ArrayList<jExtrato>();
        jExtrato Extrato;

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM adm_socios_saldo Where aut_pag is not null AND aut_pag[1][2] = ?;";
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", aut}});
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}
        if (ttCR.floatValue() != 0) {
            Extrato = new jExtrato(Descr("<b>Saldo Anterior</b>"), ttCR, null);
            lista.add(Extrato);

            // Pula linha
            Extrato = new jExtrato(null, null, null);
            lista.add(Extrato);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and aut_pag is not null and aut_pag[1][2] = ? and conta = 3 order by aut_rec;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", aut}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    bloqdata.add(new ExtratoBloqClass(
                            "A",
                            avrs.getString("registro"),
                            avrs.getString("texto"),
                            "-"
                    ));
                    continue;
                }

                Font font = new Font("SansSerif",Font.PLAIN,8);
                Canvas c = new Canvas();
                FontMetrics fm = c.getFontMetrics(font);
                String aLinhas[] = WordWrap.wrap(avrs.getString("texto") + "  " + new SimpleDateFormat("dd/MM/yyyy").format(avrs.getDate("dtrecebimento")) + " - " + avrs.getString("aut_rec"), 230, fm).split("\n");
                for (int k=0;k<aLinhas.length;k++) {
                    BigDecimal lcr = null, ldb = null;
                    lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                    ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;
                    Extrato = new jExtrato(Descr(aLinhas[k]), k == aLinhas.length - 1 ? lcr : null, k == aLinhas.length - 1 ? ldb : null);
                    lista.add(Extrato);
                }

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));

                // Pula Linha
                Extrato = new jExtrato(null, null, null);
                lista.add(Extrato);
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        Extrato = new jExtrato(null, null, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=blue><b>Total de Créditos</b></font>"), ttCR, null);
        lista.add(Extrato);

        Extrato = new jExtrato(Descr("<font color=red><b>Total de Déditos</b></font>"), null, ttDB);
        lista.add(Extrato);

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        String tDesc = "";
        if (ttSld.floatValue() > 0) {
            tDesc = "<font color=blue><b>Líquido a Receber</b></font>";
        } else {
            tDesc = "<font color=red><b>Líquido a Receber</b></font>";
        }
        Extrato = new jExtrato(Descr(tDesc), ttSld.floatValue() > 0 ? ttSld : null, ttSld.floatValue() < 0 ? ttSld : null);
        lista.add(Extrato);

        Extrato = new jExtrato(null,  null, null);
        lista.add(Extrato);

        // complementa com linhas em branco para preencher a página
        int npag = lista.size() % 32;
        for (int i=1;i<=(32 - npag);i++) {
            Extrato = new jExtrato(null,null,null);
            lista.add(Extrato);
        }

        ReportSoc(lista, new BigInteger(aut));
    }

    private void ReportSoc(List<jExtrato> lista, BigInteger aut) {
        int nSoc = socLista.getSelectionModel().getSelectedItem().getRegistro();
        Collections dadm = VariaveisGlobais.getAdmDados();
        String autenticacao = aut.floatValue() != 0 ? dadm.get("marca") +
                FuncoesGlobais.StrZero(String.valueOf(aut),10) +
                Dates.DateFormata("ddMMyyyyHHmm", DbMain.getDateTimeServer())  +
                " " + VariaveisGlobais.usuario : "";

        Object[][] dadosProp = null;
        try {
            dadosProp = conn.LerCamposTabela(new String[] {"id", "nome"},"adm_socios", "id = ?", new Object[][] {{"int", nSoc}});
        } catch (SQLException e) {}

        String tCodigo = null; String tNome = null;
        if (dadosProp != null) {
            tCodigo = dadosProp[0][3].toString();
            tNome = dadosProp[1][3].toString();
        }

        Map parametros = new HashMap();
        parametros.put("Logo",dadm.get("logo"));
        parametros.put("rgprp", tCodigo);
        parametros.put("nomeProp", tNome);
        parametros.put("NumeroExtrato", 1);
        parametros.put("Mensagem", "Extrato de Sócios");
        parametros.put("Banco","");
        parametros.put("Autenticacao",autenticacao);
        parametros.put("ShowSaldo", true);

        JasperPrint jasperPrint=null;
        try {
            JRDataSource jrds = new JRBeanCollectionDataSource(lista);
            String reportFileName = System.getProperty("user.dir") + "\\Reports\\jExtrato.jasper";
            JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(reportFileName);
            jasperPrint = JasperFillManager.fillReport(reporte, parametros, jrds);
        } catch (JRException e) { e.printStackTrace(); }

        String bStyle = "-fx-background-color: cornsilk;";
        bStyle += "-fx-background-radius: 5 5 5 5;";
        bStyle += "-fx-border-color: black;";
        bStyle += "-fx-border-radius: 5 5 5 5;";
        bStyle += "-fx-border-width: 1;";
        JasperViewerFX jrv = new JasperViewerFX((int) eExtratoPdf.getWidth(), 600, bStyle, lista, parametros);
        jrv.viewReport(jasperPrint);

        Stage primaryStage = new Stage();
        primaryStage.setTitle(".:: 2ª Via Extrato dos Sócios");
        primaryStage.setResizable(false);
        StackPane root = new StackPane();
        root.getChildren().add(jrv);
        primaryStage.setScene(new Scene(root,(int) eExtratoPdf.getWidth(), 600));
        primaryStage.showAndWait();
    }
}
