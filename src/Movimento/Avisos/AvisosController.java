package Movimento.Avisos;

import Classes.paramEvent;
import Funcoes.Collections;
import Funcoes.*;
import Movimento.PreAviso.TablePreAviso;
import PagRec.PagamentosController;
import PagRec.RecebimentoController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.control.table.TableFilter;
import org.controlsfx.control.textfield.TextFields;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pdfViewer.PdfViewer;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AvisosController implements Initializable {
    RecebimentoController controllerRec;
    PagamentosController controllerPag;

    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsAvCodigo = {};
    private String[] _possibleSuggestionsAvNome = {};
    private String[][] _possibleSuggestionsAv = {};
    private Set<String> possibleSuggestionsAvCodigo;
    private Set<String> possibleSuggestionsAvNome;
    private boolean isSearchAvCodigo = true;
    private boolean isSearchAvNome = true;

    private String[] _possibleSuggestionsPrRegistro = {};
    private String[] _possibleSuggestionsPrNome = {};
    private String[][] _possibleSuggestionsPr = {};
    private Set<String> possibleSuggestionsPrRegistro;
    private Set<String> possibleSuggestionsPrNome;
    private boolean isSearchPrRegistro = true;
    private boolean isSearchPrNome = true;

    private String[] _possibleSuggestionsLcContrato = {};
    private String[] _possibleSuggestionsLcNome = {};
    private String[][] _possibleSuggestionsLc = {};
    private Set<String> possibleSuggestionsLcContrato;
    private Set<String> possibleSuggestionsLcNome;
    private boolean isSearchLcContrato = true;
    private boolean isSearchLcNome = true;

    private String[] _possibleSuggestionsScCodigo = {};
    private String[] _possibleSuggestionsScNome = {};
    private String[][] _possibleSuggestionsSc = {};
    private Set<String> possibleSuggestionsScCodigo;
    private Set<String> possibleSuggestionsScNome;
    private boolean isSearchScCodigo = true;
    private boolean isSearchScNome = true;

    private String[] _possibleSuggestionsCtCodigo = {};
    private String[] _possibleSuggestionsCtNome = {};
    private String[][] _possibleSuggestionsCt = {};
    private Set<String> possibleSuggestionsCtCodigo;
    private Set<String> possibleSuggestionsCtNome;
    private boolean isSearchCtCodigo = true;
    private boolean isSearchCtNome = true;

    public static DataFormat dataFormat = new DataFormat("mydata");

    @FXML private AnchorPane anchorPane;
    @FXML private TabPane abas;

    @FXML private Tab av_Avisos;
    @FXML private TabPane tbpAvisos;

    @FXML private Tab avAdm;
    @FXML private TextField txbAvCodigo;
    @FXML private TextField txbAvNome;

    @FXML private Tab AvPrp;
    @FXML private TextField txbPrCodigo;
    @FXML private TextField txbPrNome;

    @FXML private Tab AvLoc;
    @FXML private TextField txbLcCodigo;
    @FXML private TextField txbLcNome;

    @FXML private Tab AvSc;
    @FXML private TextField txbScCodigo;
    @FXML private TextField txbScNome;

    @FXML private Tab AvCta;
    @FXML private TextField txbCtCodigo;
    @FXML private TextField txbCtNome;

    @FXML private TextArea txaTexto;

    @FXML private Tab av_Retencao;
    @FXML private TableView<TableRetencao> tbvRetencoes;
    @FXML private TableColumn<TableRetencao, Integer> rt_id;
    @FXML private TableColumn<TableRetencao, String> rt_tipo;
    @FXML private TableColumn<TableRetencao, String> rt_rgimv;
    @FXML private TableColumn<TableRetencao, String> rt_end;
    @FXML private TableColumn<TableRetencao, String> rt_taxa;
    @FXML private TableColumn<TableRetencao, Object> rt_valor;
    @FXML private TableColumn<TableRetencao, String> rt_recto;
    @FXML private TableColumn<TableRetencao, String> rt_vecto;
    @FXML private TableColumn<TableRetencao, Boolean> rt_select;
    @FXML private TextField txbFiltroRet;
    @FXML private Button btnTotalRet;
    @FXML private CheckBox chbSelTudoRet;
    @FXML private Button btnListarRet;

    @FXML private Tab av_PreAvisos;
    @FXML private TableView<TablePreAviso> tbvPreAvisos;
    @FXML private TableColumn<TablePreAviso, Integer> tblLan_id;
    @FXML private TableColumn<TablePreAviso, String> tblLan_conta;
    @FXML private TableColumn<TablePreAviso, String> tblLan_codigo;
    @FXML private TableColumn<TablePreAviso, String> tblLan_tipo;
    @FXML private TableColumn<TablePreAviso, String> tblLan_texto;
    @FXML private TableColumn<TablePreAviso, BigDecimal> tblLan_valor;
    @FXML private TableColumn<TablePreAviso, String> tblLan_usuario;
    @FXML private TableColumn<TablePreAviso, String> tblLan_dtlanc;
    @FXML private TableColumn<TablePreAviso, String> tblLan_obs;

    @FXML private TextArea txaDescPAv;
    @FXML private Button btnImprimir;

    @FXML private Tab av_Antecipa;
    @FXML private TableView<TableRetencao> tbvAntecip;
    @FXML private TableColumn<TableRetencao, Integer> at_id;
    @FXML private TableColumn<TableRetencao, String> at_tipo;
    @FXML private TableColumn<TableRetencao, String> at_rgimv;
    @FXML private TableColumn<TableRetencao, String> at_end;
    @FXML private TableColumn<TableRetencao, String> at_taxa;
    @FXML private TableColumn<TableRetencao, Object> at_valor;
    @FXML private TableColumn<TableRetencao, String> at_recto;
    @FXML private TableColumn<TableRetencao, String> at_vecto;
    @FXML private TableColumn<TableRetencao, Boolean> at_select;
    @FXML private TextField txbFiltrarAntecip;
    @FXML private Button btnTotalizarAntecip;
    @FXML private CheckBox chbSelTodosAntecip;
    @FXML private Button btnListarAntecip;

    // Painel de Recebimentos
    @FXML private Pane tpagtos;
    @FXML private ToggleButton tgbCrDb;
    @FXML private TextField txbValor;
    @FXML private CheckBox chbVias;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam.length == 0) {
                // Cancelar Recebimento
                txbValor.requestFocus();
            }
            if (event.sparam.length > 0) {
                if (event.sparam[0] != null) {
                    // Imprimir Recebimento
                    if (event.sparam.length == 0) return;

                    String[][] Lancamentos = null;
                    try {Lancamentos = (String[][]) event.sparam[1];} catch (Exception e) { return; }

                    BigInteger aut = conn.PegarAutenticacao();
                    Collections dadm = VariaveisGlobais.getAdmDados();

                    if (av_Avisos.isSelected()) {
                        String tipoAviso = null, codigo = null, nome = null;
                        if (avAdm.isSelected()) {
                            tipoAviso = "ADM";
                            codigo = txbAvCodigo.getText();
                            nome = txbAvNome.getText();
                        } else if (AvPrp.isSelected()) {
                            tipoAviso = "PROPRIETARIO";
                            codigo = txbPrCodigo.getText();
                            nome = txbPrNome.getText();
                        } else if (AvLoc.isSelected()) {
                            tipoAviso = "LOCATARIO";
                            codigo = txbLcCodigo.getText();
                            nome = txbLcNome.getText();
                        } else if (AvSc.isSelected()) {
                            tipoAviso = "SOCIO";
                            codigo = txbScCodigo.getText();
                            nome = txbScNome.getText();
                        } else if (AvCta.isSelected()) {
                            tipoAviso = "CONTAS";
                            codigo = txbCtCodigo.getText();
                            nome = txbCtNome.getText();
                        }
                        Object[] dados = {tgbCrDb.isSelected() ? "DÉBITO" : "CRÉDITO", tipoAviso, codigo, nome, new BigDecimal(txbValor.getText().replace(".", "").replace(",", ".")), txaTexto.getText()};

                        new Impressao(aut, Lancamentos).ImprimeAvisoPDF(dadm, dados, false);

                        if (avAdm.isSelected()) {
                            GravaAviso(0, "0", "0", txbAvCodigo.getText(), DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                            txbAvCodigo.requestFocus();
                        } else if (AvPrp.isSelected()) {
                            GravaAviso(1, txbPrCodigo.getText(), "0", "", DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                            txbPrCodigo.requestFocus();
                        } else if (AvLoc.isSelected()) {
                            GravaAviso(2, "0", "0", txbLcCodigo.getText(), DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                            txbLcCodigo.requestFocus();
                        } else if (AvSc.isSelected()) {
                            GravaAviso(3, txbScCodigo.getText(), "0", "", DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                            txbScCodigo.requestFocus();
                        } else if (AvCta.isSelected()) {
                            GravaAviso(4, "0", "0", txbCtCodigo.getText(), DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                            txbCtCodigo.requestFocus();
                        } else {
                            // Erro
                        }
                    } else if (av_Retencao.isSelected()) {
                        BigDecimal tRet = new BigDecimal(0);
                        List<TableRetencao> lista = new ArrayList<TableRetencao>();
                        for( final TableRetencao os : tbvRetencoes.getItems()) {
                            if (os.tagProperty().get()) {
                                lista.add(new TableRetencao(os.getId(), "T", os.getRgimv(), os.getEnder(), os.getTaxa(), os.getValor(), os.getDtrecebto(), os.getDtvencto(), os.isTag()));
                                tRet = tRet.add(os.getValor());
                            }
                        }
                        new Impressao(aut, Lancamentos).ImprimeRetencaoPDF(dadm, lista, tRet, true);

                        final Set<TableRetencao> del = new HashSet<>();
                        for( final TableRetencao os : tbvRetencoes.getItems()) if( os.tagProperty().get()) del.add(os);

                        // Grava��o
                        GravaRetencao(lista,DbMain.GeraLancamentosArray(Lancamentos),tRet.toPlainString());

                        // Remove os selecionados
                        tbvRetencoes.getItems().removeAll( del );
                    } else if (av_PreAvisos.isSelected()) {
                        int idConta = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getId();
                        String tipoAviso = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getConta();
                        String codigo = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getCodigo();
                        String texto = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getTexto();
                        String nome = "";
                        String tpDBCR = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getTipo() == "CRE" ? "CREDITO" : "DEBITO";
                        switch (tipoAviso) {
                            case "ADM":
                                Object[][] nmCodigo = null;
                                try {
                                    nmCodigo = conn.LerCamposTabela(new String[]{"descricao"}, "adm", "codigo = '" + codigo + "'");
                                } catch (Exception e) {}
                                if (nmCodigo != null) nome = (String) nmCodigo[0][3]; else {
                                    try {
                                        nmCodigo = conn.LerCamposTabela(new String[]{"descricao"}, "adm_contas", "codigo = '" + codigo + "' AND tipo = 'ADM' AND deletada = false");
                                    } catch (Exception e) {}
                                    if (nmCodigo != null) nome = (String) nmCodigo[0][3];
                                }
                                break;
                            case "PROPRIETARIO":
                                Object[][] nmProp = null;
                                try {
                                    nmProp = conn.LerCamposTabela(new String[]{"p_nome"}, "proprietarios", "p_rgprp = '" + codigo + "'");
                                } catch (Exception e) {}
                                if (nmProp != null) nome = (String) nmProp[0][3];
                                break;
                            case "LOCATARIO":
                                Object[][] nmLoca = null;
                                try {
                                    nmLoca = conn.LerCamposTabela(new String[]{"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca"}, "locatarios", "l_contrato = '" + codigo + "'");
                                } catch (Exception e) {}
                                if (nmLoca != null) nome = (String) nmLoca[0][3];
                                break;
                            case "SOCIO":
                                Object[][] nmSoc = null;
                                try {
                                    nmSoc = conn.LerCamposTabela(new String[]{"s_nome"}, "socios", "s_id = '" + codigo + "'");
                                } catch (Exception e) {}
                                if (nmSoc != null) nome = (String) nmSoc[0][3];
                                break;
                            case "CONTAS":
                                Object[][] nmContas = null;
                                try {
                                    nmContas = conn.LerCamposTabela(new String[]{"descricao"}, "adm_contas", "codigo = '" + codigo + "' AND tipo = 'CONTAS' AND deletada = false");
                                } catch (Exception e) {}
                                if (nmContas != null) nome = (String) nmContas[0][3];
                                break;
                        }
                        Object[] dados = {tpDBCR, tipoAviso, codigo, nome, new BigDecimal(txbValor.getText().replace(".", "").replace(",", ".")), texto};

                        new Impressao(aut, Lancamentos).ImprimeAvisoPDF(dadm, dados, false);

                        switch (tipoAviso) {
                            case "ADM":
                                GravaAvisoPre(idConta,0, "0", "0", codigo, DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                                break;
                            case "PROPRIETARIOS":
                                GravaAvisoPre(idConta, 1, codigo, "0", "", DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                                break;
                            case "LOCATARIOS":
                                GravaAvisoPre(idConta, 2, "0", "0", codigo, DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                                break;
                            case "SOCIOS":
                                GravaAvisoPre(idConta, 3, codigo, "0", "", DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                                break;
                            case "CONTAS":
                                GravaAvisoPre(idConta, 4, "0", "0", codigo, DbMain.GeraLancamentosArray(Lancamentos), txbValor.getText());
                                break;
                        }
                    } else {
                        // Antecipa��o
                        BigDecimal tRet = new BigDecimal(0);
                        List<TableRetencao> lista = new ArrayList<TableRetencao>();
                        for( final TableRetencao os : tbvAntecip.getItems()) {
                            if (os.tagProperty().get()) {
                                lista.add(new TableRetencao(os.getId(), "T", os.getRgimv(), os.getEnder(), os.getTaxa(), os.getValor(), os.getDtrecebto(), os.getDtvencto(), os.isTag()));
                                tRet = tRet.add(os.getValor());
                            }
                        }
                        new Impressao(aut, Lancamentos).ImprimeAntecipacaoPDF(dadm, lista, tRet, true);

                        final Set<TableRetencao> del = new HashSet<>();
                        for( final TableRetencao os : tbvAntecip.getItems()) if( os.tagProperty().get()) del.add(os);

                        // Grava��o
                        GravaAntecip(lista,DbMain.GeraLancamentosArray(Lancamentos),tRet.toPlainString());

                        // Remove os selecionados
                        tbvAntecip.getItems().removeAll( del );
                    }
                }
            } else {
                txbValor.requestFocus();
            }

            // Limpa Formul�rio
            try {tpagtos.getChildren().remove(0);} catch (Exception e) {}
            txbValor.setText("0,00");
            txaTexto.setText(null);

            CrDb();

            // Foca no Objeto C�digo
            if (av_Avisos.isSelected()) {
                if (avAdm.isSelected()) {
                    txbAvCodigo.requestFocus();
                } else if (AvPrp.isSelected()) {
                    txbPrCodigo.requestFocus();
                } else if (AvLoc.isSelected()) {
                    txbLcCodigo.requestFocus();
                } else if (AvSc.isSelected()) {
                    txbScCodigo.requestFocus();
                } else if (AvCta.isSelected()) {
                    txbCtCodigo.requestFocus();
                } else {
                    // Erro
                }
            } else if (av_Retencao.isSelected()) {

            } else if (av_PreAvisos.isSelected()) {

            } else {
                // av_Antecipa
            }
        });

        tgbCrDb.setOnAction((ActionEvent event) -> {
            CrDb();
        });

        AutocompletePr();
        AutocompleteLc();
        AutocompleteAdm();
        AutocompleteSocios();
        AutocompleteContas();

        abas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == av_Avisos) {
            } else if (newValue == av_Retencao) {
                abaRetencao();
            } else if (newValue == av_PreAvisos) {
                abaPreAviso();
            } else if (newValue == av_Antecipa){
                abaAntecipa();
            }
        });

        btnTotalRet.setOnAction(event -> {
            BigDecimal tRet = new BigDecimal(0);
            final Set<TableRetencao> del = new HashSet<>();
            for( final TableRetencao os : tbvRetencoes.getItems()) {
                if( os.tagProperty().get()) {
                    del.add( os );
                    tRet = tRet.add(os.getValor());
                }
            }

            txbValor.setText(tRet.toPlainString());
            tgbCrDb.setSelected(true);
            CrDb();
            tgbCrDb.setDisable(true);

            // Remove os selecionados
            //tbvRetencoes.getItems().removeAll( del );
        });

        chbSelTudoRet.setOnAction(event -> {
            if (chbSelTudoRet.isSelected()) {
                for( final TableRetencao os : tbvRetencoes.getItems()) os.setTag(true);
            } else {
                for( final TableRetencao os : tbvRetencoes.getItems()) os.setTag(false);
            }
        });

        btnListarRet.setOnAction(event -> {
            List<TableRetencao> lista = new ArrayList<TableRetencao>();
            for( final TableRetencao os : tbvRetencoes.getItems()) {
                if (os.tagProperty().get()) {
                    lista.add(new TableRetencao(os.getId(), "T", os.getRgimv(), os.getEnder(), os.getTaxa(), os.getValor(), os.getDtrecebto(), os.getDtvencto(), os.isTag()));
                }
            }
            String pdfName = new PdfViewer().GeraPDFTemp(lista,"ListaRetencao");
            //new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview do Lista de Reten��es", pdfName);
        });

        // -x-x-x-x-x-x-x-x-x-x-x-
        btnTotalizarAntecip.setOnAction(event -> {
            BigDecimal tRet = new BigDecimal(0);
            final Set<TableRetencao> del = new HashSet<>();
            for( final TableRetencao os : tbvAntecip.getItems()) {
                if( os.tagProperty().get()) {
                    del.add( os );
                    tRet = tRet.add(os.getValor());
                }
            }

            txbValor.setText(tRet.toPlainString());
            tgbCrDb.setSelected(true);
            CrDb();
            tgbCrDb.setDisable(true);

            // Remove os selecionados
            //tbvAntecip.getItems().removeAll( del );
        });

        chbSelTodosAntecip.setOnAction(event -> {
            if (chbSelTodosAntecip.isSelected()) {
                for( final TableRetencao os : tbvAntecip.getItems()) os.setTag(true);
            } else {
                for( final TableRetencao os : tbvAntecip.getItems()) os.setTag(false);
            }
        });

        btnListarAntecip.setOnAction(event -> {
            List<TableRetencao> lista = new ArrayList<TableRetencao>();
            for( final TableRetencao os : tbvAntecip.getItems()) {
                if (os.tagProperty().get()) {
                    lista.add(new TableRetencao(os.getId(), "T", os.getRgimv(), os.getEnder(), os.getTaxa(), os.getValor(), os.getDtrecebto(), os.getDtvencto(), os.isTag()));
                }
            }
            String pdfName = new PdfViewer().GeraPDFTemp(lista, "ListaRetencao");
            //new toPrint(pdfName,"LASER","INTERNA");
            new PdfViewer("Preview do Lista de Antecipa��es", pdfName);
        });

        CrDb();
    }

    private void CrDb() {
        try {tpagtos.getChildren().remove(0);} catch (Exception e) {}
        if (tgbCrDb.isSelected()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Pagamentos.fxml"));
                Pane root = (Pane)loader.load();
                controllerPag = loader.getController();
                tpagtos.getChildren().add(root);
                root.setLayoutX(0); root.setLayoutY(0);
            } catch (Exception e) {e.printStackTrace();}

            tgbCrDb.setText("D�bito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#ff5400, #be1d00);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;" +
                            "-fx-font: bold italic 10pt 'Arial'"
            );
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Recebimento.fxml"));
                Pane root = (Pane)loader.load();
                controllerRec = loader.getController();
                tpagtos.getChildren().add(root);
                root.setLayoutX(0); root.setLayoutY(0);
            } catch (Exception e) {e.printStackTrace();}

            tgbCrDb.setText("Cr�dito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#00ff54, #00be1d);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;"+
                            "-fx-font: bold italic 10pt 'Arial'"
            );
        }

        MaskFieldUtil.monetaryField(txbValor);
        txbValor.requestFocus();
        txbValor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // gotfocus
                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // lostfocus
                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_Disable(false);
                    controllerPag.SetValor(new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText())));
                } else {
                    controllerRec.Formas_Disable(false);
                    controllerRec.SetValor(new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText())));
                }
                //try {controller.Formas_Disable(false);} catch (Exception e) {}
                //controller.SetValor(new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText())));
            }
        });
    }

    private void GravaAviso(int conta, String rgprp, String rgimv, String contrato, String lancamentos, String valor) {
        BigInteger aut = conn.PegarAutenticacao();
        //System.out.println(aut);

        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                    "contrato, valor, lancamentos) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";
            String lanctos = lancamentos;
            caixaSQL = String.format(caixaSQL,
                    aut,
                    DbMain.getDateTimeServer(),
                    VariaveisGlobais.usuario,
                    tgbCrDb.isSelected() ? "DEB" : "CRE",
                    "AVI",
                    rgprp, rgimv, contrato,
                    valor, lanctos
            );
            if (conn.ExecutarComando(caixaSQL) > 0) {
                System.out.println("incluido.");
                String registro = !rgprp.equalsIgnoreCase("0") ? rgprp : (!rgimv.equalsIgnoreCase("0") ? rgimv : contrato);
                caixaSQL = "INSERT INTO avisos (conta, registro, tipo, texto, valor, dtrecebimento, aut_rec, usr_rec, aut_pag) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement stm = conn.conn.prepareStatement(caixaSQL);
                stm.setInt(1, conta);
                stm.setString(2, registro);
                stm.setString(3, tgbCrDb.isSelected() ? "DEB" : "CRE");
                stm.setString(4, txaTexto.getText());
                stm.setBigDecimal(5, new BigDecimal(LerValor.Number2BigDecimal(valor)));
                stm.setDate(6, new java.sql.Date(DbMain.getDateTimeServer().getTime()));
                stm.setInt(7, aut.intValue());
                stm.setString(8, VariaveisGlobais.usuario);
                stm.setArray(9, conn.conn.createArrayOf("text" +
                        "", new Object[][] {{registro, null, null, ""}}));
                stm.executeUpdate();
                stm.close();
            }
        } catch (Exception e) {e.printStackTrace();}

    }

    private void GravaAvisoPre(int nId, int conta, String rgprp, String rgimv, String contrato, String lancamentos, String valor) {
        BigInteger aut = conn.PegarAutenticacao();

        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                    "contrato, valor, lancamentos) VALUES (?,?,?,?,?,?,?,?,?,?);";
            String lanctos = lancamentos;
            String tlanctos = lanctos.replace("{{","").replace("}}","").replace("\"","");
            String[] alanctos = tlanctos.split(",");
            String alc1 = ""; try { alc1 = alanctos[0]; } catch (Exception e) {}
            String alc2 = ""; try { alc2 = alanctos[1]; } catch (Exception e) {}
            String alc3 = ""; try { alc3 = alanctos[2]; } catch (Exception e) {}
            String alc4 = ""; try { alc4 = alanctos[3]; } catch (Exception e) {}
            String alc5 = ""; try { alc5 = alanctos[4]; } catch (Exception e) {}
            String alc6 = ""; try { alc6 = alanctos[5]; } catch (Exception e) {}

            Object[][] param = new Object[][] {
                    {"bigint", aut},
                    {"date", new java.sql.Date(DbMain.getDateTimeServer().getTime())},
                    {"string", VariaveisGlobais.usuario},
                    {"string", tgbCrDb.isSelected() ? "DEB" : "CRE"},
                    {"string", "PAV"},
                    {"int", Integer.valueOf(rgprp)},
                    {"int", Integer.valueOf(rgimv)},
                    {"string", contrato},
                    {"decimal", new BigDecimal(LerValor.Number2BigDecimal(valor))},
                    {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{alc1, alc2, alc3, alc4,alc5,alc6}})}
            };

            if (conn.ExecutarComando(caixaSQL, param) > 0) {
                System.out.println("incluido.");
                String registro = !rgprp.equalsIgnoreCase("0") ? rgprp : (!rgimv.equalsIgnoreCase("0") ? rgimv : contrato);
                caixaSQL = "UPDATE avisos SET valor = ?, dtrecebimento = ?, aut_rec = ?, usr_rec = ?, aut_pag = ? WHERE id = ?;";
                param = new Object[][] {
                        {"decimal", new BigDecimal(LerValor.Number2BigDecimal(valor))},
                        {"date", new java.sql.Date(DbMain.getDateTimeServer().getTime())},
                        {"bigint", aut},
                        {"string", VariaveisGlobais.usuario},
                        {"array", conn.conn.createArrayOf("text" + "", new Object[][] {{registro, null, null, ""}})},
                        {"int",nId}
                };

                conn.ExecutarComando(caixaSQL, param);
                tbvAntecip.getItems().removeAll(tbvAntecip.getSelectionModel().getSelectedItems());
            }
        } catch (Exception e) {e.printStackTrace();}

    }

    private void GravaRetencao(List<TableRetencao> dados, String lancamentos, String valor) {
        BigInteger aut = conn.PegarAutenticacao();
        //System.out.println(aut);

        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, " +
                    "contrato, valor, lancamentos) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s');";
            String lanctos = lancamentos;
            caixaSQL = String.format(caixaSQL,
                    aut,
                    DbMain.getDateTimeServer(),
                    VariaveisGlobais.usuario,
                    tgbCrDb.isSelected() ? "DEB" : "CRE",
                    "AVI", "RET",
                    valor, lanctos
            );
            if (conn.ExecutarComando(caixaSQL) > 0) {
                System.out.println("incluido.");
                // Grava em taxas/seguro -> data/usuario/aut
                for (TableRetencao o : dados) {
                    String uTaxasSeguros = "UPDATE %s SET dtretencao = '%s', aut_ret = '%s', usr_ret = '%s' WHERE id = '%s';";
                    if (o.isTag()) {
                        if (o.getTipo().equalsIgnoreCase("T")) {
                            uTaxasSeguros = String.format(uTaxasSeguros,
                                    "taxas",
                                    DbMain.getDateTimeServer(),
                                    aut,
                                    VariaveisGlobais.usuario,
                                    o.getId()
                            );
                        } else {
                            uTaxasSeguros = String.format(uTaxasSeguros,
                                    "seguros",
                                    DbMain.getDateTimeServer(),
                                    aut,
                                    VariaveisGlobais.usuario,
                                    o.getId()
                            );
                        }
                        conn.ExecutarComando(uTaxasSeguros);
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}

    }

    private void GravaAntecip(List<TableRetencao> dados, String lancamentos, String valor) {
        BigInteger aut = conn.PegarAutenticacao();

        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, " +
                    "contrato, valor, lancamentos) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s');";
            String lanctos = lancamentos;
            caixaSQL = String.format(caixaSQL,
                    aut,
                    DbMain.getDateTimeServer(),
                    VariaveisGlobais.usuario,
                    tgbCrDb.isSelected() ? "DEB" : "CRE",
                    "AVI", "ANT",
                    valor, lanctos
            );
            if (conn.ExecutarComando(caixaSQL) > 0) {
                System.out.println("incluido.");
                // Grava em taxas/seguro -> data/usuario/aut
                for (TableRetencao o : dados) {
                    String uTaxasSeguros = "UPDATE %s SET dtretencao = '%s', aut_ret = '%s', usr_ret = '%s' WHERE id = '%s';";
                    if (o.isTag()) {
                        if (o.getTipo().equalsIgnoreCase("T")) {
                            uTaxasSeguros = String.format(uTaxasSeguros,
                                    "taxas",
                                    DbMain.getDateTimeServer(),
                                    aut,
                                    VariaveisGlobais.usuario,
                                    o.getId()
                            );
                        } else {
                            uTaxasSeguros = String.format(uTaxasSeguros,
                                    "seguros",
                                    DbMain.getDateTimeServer(),
                                    aut,
                                    VariaveisGlobais.usuario,
                                    o.getId()
                            );
                        }
                        conn.ExecutarComando(uTaxasSeguros);
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    /*
    CREATE TABLE public.avisos (
    id integer NOT NULL DEFAULT nextval('avisos_id_seq'::regclass),
    conta integer,
    registro character varying(6),
    tipo character varying(3),
    texto text,
    dtpagamento date,
    aut_pag bigint,
    usr_pag character varying(20),
    bloqueio character varying(7),
    dtbloqueio date,
    usr_bloqueio character varying(20),
    dtrecebimento date,
    aut_rec bigint,
    usr_rec character varying(20)
)
*/

    private void abaRetencao() {
        List<TableRetencao> data = new ArrayList<TableRetencao>();
        ResultSet imv;
        String qSQL = "SELECT t.id, t.rgprp, t.rgimv, t.contrato, (SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro " +
                      "FROM imoveis i WHERE (exclusao is null) and i.i_rgimv = t.rgimv) AS ender, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campo, " +
                      "t.cota, t.valor, t.dtvencimento, t.referencia, t.dtrecebimento FROM taxas t WHERE t.tipo = 'D' AND t.retencao = True AND " +
                      "t.dtrecebimento is not null AND aut_ret is null;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                retClass registro = new retClass();
                try {registro.setId(imv.getInt("id"));} catch (SQLException e) {}
                try {registro.setRgimv(imv.getString("rgimv"));} catch (SQLException e) {}
                try {registro.setEnd(imv.getString("ender"));} catch (SQLException e) {}
                try {registro.setTaxa(imv.getString("campo"));} catch (SQLException e) {}
                try {registro.setValor(imv.getBigDecimal("valor"));} catch (SQLException e) {}
                try {registro.setRecto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtrecebimento")));} catch (SQLException e) {}
                try {registro.setVencto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtvencimento")));} catch (SQLException e) {}
                registro.setTag(false);

                data.add(new TableRetencao(registro.getId(), "T", registro.getRgimv(), registro.getEnd(), registro.getTaxa(), registro.getValor(), registro.getRecto(), registro.getVencto(), registro.isTag()));
            }
            imv.close();
        } catch (SQLException e) {}

        // Seguros
        qSQL = "SELECT s.id, s.rgprp, s.rgimv, s.contrato, s.cota, s.valor, s.dtvencimento, s.referencia, \n" +
                "(SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro FROM imoveis i WHERE (exclusao is null) and i.i_rgimv = s.rgimv) AS ender," +
                "       s.extrato, s.apolice, s.dtseguro, s.aut_seg, s.usr_seg, s.dtrecebimento, \n" +
                "       s.aut_rec, s.usr_rec, s.banco, s.nnumero, s.bloqueio, s.dtbloqueio, s.usr_bloqueio, \n" +
                "       s.dtlanc, s.usr_lanc, s.selected, s.aut_pag, s.retencao, '2018-01-01' dtrecebimento, s.reserva, 'SEGURO' campo\n" +
                "  FROM seguros s WHERE s.retencao = true and s.dtrecebimento is not null and aut_ret is null";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                retClass registro = new retClass();
                try {registro.setId(imv.getInt("id"));} catch (SQLException e) {}
                try {registro.setRgimv(imv.getString("rgimv"));} catch (SQLException e) {}
                try {registro.setEnd(imv.getString("ender"));} catch (SQLException e) {}
                try {registro.setTaxa(imv.getString("campo"));} catch (SQLException e) {}
                try {registro.setValor(imv.getBigDecimal("valor"));} catch (SQLException e) {}
                try {registro.setRecto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtrecebimento")));} catch (SQLException e) {}
                try {registro.setVencto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtvencimento")));} catch (SQLException e) {}
                registro.setTag(false);

                data.add(new TableRetencao(registro.getId(), "S", registro.getRgimv(), registro.getEnd(), registro.getTaxa(), registro.getValor(), registro.getRecto(), registro.getVencto(), registro.isTag()));
            }
            imv.close();
        } catch (SQLException e) {}

        rt_id.setCellValueFactory(new PropertyValueFactory("id"));
        rt_tipo.setCellValueFactory(new PropertyValueFactory("tipo"));
        rt_rgimv.setCellValueFactory(new PropertyValueFactory("rgimv"));
        rt_end.setCellValueFactory(new PropertyValueFactory("ender"));
        rt_taxa.setCellValueFactory(new PropertyValueFactory("taxa"));
        rt_valor.setCellValueFactory(new PropertyValueFactory("valor"));
        rt_recto.setCellValueFactory(new PropertyValueFactory("dtrecebto"));
        rt_vecto.setCellValueFactory(new PropertyValueFactory("dtvencto"));
        rt_select.setCellValueFactory(new PropertyValueFactory("tag"));
        rt_select.setCellFactory( tc -> new CheckBoxTableCell<>());

        tbvRetencoes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvRetencoes.setItems(FXCollections.observableArrayList(data));
        tbvRetencoes.setEditable(true);

        //TableFilter<pimoveisModel> tableFilter = new TableFilter<pimoveisModel>(p_imoveis);

        setRowFactory();
        setRowSelection();
    }

    public void setRowFactory() {
        tbvRetencoes.setRowFactory(p -> {
            final TableRow<TableRetencao> row = new TableRow<TableRetencao>();

            row.setOnDragEntered(t -> setSelection(row));

            row.setOnDragDetected(t -> {
                Dragboard db = row.getTableView().startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.put(dataFormat, "XData");
                db.setContent(content);
                setSelection(row);
                t.consume();
            });
            return row;
        });
    }

    public void setRowSelection() {
        tbvRetencoes.getSelectionModel().clearSelection();
        tbvRetencoes.getSelectionModel().setCellSelectionEnabled(false);
    }

    private void setSelection(IndexedCell cell) {
        if (cell.isSelected()) {
            System.out.println("False");
            tbvRetencoes.getSelectionModel().clearSelection(cell.getIndex());
        } else {
            System.out.println("true");
            tbvRetencoes.getSelectionModel().select(cell.getIndex());
        }
    }

    private void abaAntecipa() {
        List<TableRetencao> data = new ArrayList<TableRetencao>();
        ResultSet imv;
        String qSQL = "SELECT t.id, t.rgprp, t.rgimv, t.contrato, (SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro " +
                "FROM imoveis i WHERE (exclusao is null) and i.i_rgimv = t.rgimv) AS ender, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campo, " +
                "t.cota, t.valor, t.dtvencimento, t.referencia, t.dtrecebimento FROM taxas t WHERE t.tipo = 'C' AND t.retencao = True AND " +
                "t.dtrecebimento is null and aut_ret is null;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                retClass registro = new retClass();
                try {registro.setId(imv.getInt("id"));} catch (SQLException e) {}
                try {registro.setRgimv(imv.getString("rgimv"));} catch (SQLException e) {}
                try {registro.setEnd(imv.getString("ender"));} catch (SQLException e) {}
                try {registro.setTaxa(imv.getString("campo"));} catch (SQLException e) {}
                try {registro.setValor(imv.getBigDecimal("valor"));} catch (SQLException e) {}
                try {registro.setRecto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtrecebimento")));} catch (SQLException e) {}
                try {registro.setVencto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtvencimento")));} catch (SQLException e) {}
                registro.setTag(false);

                data.add(new TableRetencao(registro.getId(), "T", registro.getRgimv(), registro.getEnd(), registro.getTaxa(), registro.getValor(), registro.getRecto(), registro.getVencto(), registro.isTag()));
            }
            imv.close();
        } catch (SQLException e) {}

        // Seguros
        qSQL = "SELECT s.id, s.rgprp, s.rgimv, s.contrato, s.cota, s.valor, s.dtvencimento, s.referencia, \n" +
                "(SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro FROM imoveis i WHERE i.i_rgimv = s.rgimv) AS ender," +
                "       s.extrato, s.apolice, s.dtseguro, s.aut_seg, s.usr_seg, s.dtrecebimento, \n" +
                "       s.aut_rec, s.usr_rec, s.banco, s.nnumero, s.bloqueio, s.dtbloqueio, s.usr_bloqueio, \n" +
                "       s.dtlanc, s.usr_lanc, s.selected, s.aut_pag, s.retencao, '2018-01-01' dtrecebimento, s.reserva, 'SEGURO' campo\n" +
                "  FROM seguros s WHERE s.retencao = true and s.dtrecebimento is null and aut_ret is null;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                retClass registro = new retClass();
                try {registro.setId(imv.getInt("id"));} catch (SQLException e) {}
                try {registro.setRgimv(imv.getString("rgimv"));} catch (SQLException e) {}
                try {registro.setEnd(imv.getString("ender"));} catch (SQLException e) {}
                try {registro.setTaxa(imv.getString("campo"));} catch (SQLException e) {}
                try {registro.setValor(imv.getBigDecimal("valor"));} catch (SQLException e) {}
                try {registro.setRecto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtrecebimento")));} catch (SQLException e) {}
                try {registro.setVencto(Dates.DateFormata("dd/MM/yyyy", imv.getDate("dtvencimento")));} catch (SQLException e) {}
                registro.setTag(false);

                data.add(new TableRetencao(registro.getId(), "S", registro.getRgimv(), registro.getEnd(), registro.getTaxa(), registro.getValor(), registro.getRecto(), registro.getVencto(), registro.isTag()));
            }
            imv.close();
        } catch (SQLException e) {}

        at_id.setCellValueFactory(new PropertyValueFactory("id"));
        at_tipo.setCellValueFactory(new PropertyValueFactory("tipo"));
        at_rgimv.setCellValueFactory(new PropertyValueFactory("rgimv"));
        at_end.setCellValueFactory(new PropertyValueFactory("ender"));
        at_taxa.setCellValueFactory(new PropertyValueFactory("taxa"));

        at_valor.setCellValueFactory(new PropertyValueFactory("valor"));
        at_recto.setCellValueFactory(new PropertyValueFactory("dtrecebto"));
        at_vecto.setCellValueFactory(new PropertyValueFactory("dtvencto"));
        at_select.setCellValueFactory(new PropertyValueFactory("tag"));
        at_select.setCellFactory( tc -> new CheckBoxTableCell<>());

        tbvAntecip.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvAntecip.setItems(FXCollections.observableArrayList(data));
        tbvAntecip.setEditable(true);

        TableFilter<TableRetencao> tableFilter = new TableFilter<TableRetencao>(tbvAntecip);

        tbvAntecip.setOnMousePressed(event -> {
            if (tbvAntecip.getSelectionModel().isEmpty()) return;
            if (event.isSecondaryButtonDown()) {
                BigDecimal soma = new BigDecimal("0");
                for (TableRetencao item : tbvAntecip.getSelectionModel().getSelectedItems()) {
                    soma = soma.add(item.getValor());
                }
                String htmlStr = "<body style=\"background-color:cornsilk; border-style: none;\"> <b><font color=\"red\">" + soma.toPlainString() + "</font></b></body>\n";
                tbvAntecip.setTooltip(createToolTip(htmlStr));
            }
        });

        setRowFactory();
        setRowSelection();
    }

    private Tooltip createToolTip(String htmlStr) {
        Tooltip thisToolTip = new Tooltip();

//        String htmlStr = "<body style=\"background-color:cornsilk; "
//                + "border-style: none;\"> <u><b><font color=\"red\">Click Mouse's right button to see options</font></b></u><br><br>(3) Subha Jawahar of Chennai<br> now @ Chennai<br>Female <-> Married <-> Alive<br>Period : 1800 to 2099<br>D/o Dr. Subbiah [2] - <br> <b>Spouse :</b> Jawahar Rajamanickam [7] <br><br><b>Children :</b><br><br>Rudhra Jawahar [9]<br>Mithran Jawahar [10]<br><br></body>\n";
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.loadContent(htmlStr);

        thisToolTip.setStyle("\n"
                + "    -fx-border-color: black;\n"
                + "    -fx-border-width: 1px;\n"
                + "    -fx-font: normal bold 12pt \"Times New Roman\" ;\n"
                + "    -fx-background-color: cornsilk;\n"
                + "    -fx-text-fill: black;\n"
                + "    -fx-background-radius: 4;\n"
                + "    -fx-border-radius: 4;\n"
                + "    -fx-opacity: 1.0;");

        thisToolTip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        thisToolTip.setGraphic(browser);
        thisToolTip.setAutoHide(false);
        thisToolTip.setMaxWidth(100);
        thisToolTip.setMaxHeight(60);
        thisToolTip.setGraphicTextGap(0.0);

        return thisToolTip;
    }

    private void AutocompleteLc() {
        _possibleSuggestionsLcContrato = new String[]{};
        _possibleSuggestionsLcNome = new String[]{};
        _possibleSuggestionsLc = new String[][]{};
        possibleSuggestionsLcContrato = new HashSet<String>();
        possibleSuggestionsLcNome = new HashSet<String>();
        isSearchLcContrato = true;
        isSearchLcNome = true;

        try {
            TextFields.bindAutoCompletion(txbLcCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbLcNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Locatarios
        qSQL = "SELECT l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome FROM locatarios WHERE exclusao is null ORDER BY l_contrato;";

        // Proprietarios
        //qSQL = "SELECT p_rgprp AS l_contrato, p_nome AS l_nome FROM proprietarios ORDER BY p_rgprp;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsLcContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcContrato, qcontrato);
                possibleSuggestionsLcContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsLcContrato));

                _possibleSuggestionsLcNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcNome, qnome);
                possibleSuggestionsLcNome = new HashSet<>(Arrays.asList(_possibleSuggestionsLcNome));

                _possibleSuggestionsLc = FuncoesGlobais.ArraysAdd(_possibleSuggestionsLc, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbLcCodigo, possibleSuggestionsLcContrato);
        TextFields.bindAutoCompletion(txbLcNome, possibleSuggestionsLcNome);

        txbLcCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbLcCodigo.setText(null);
                txbLcNome.setText(null);

                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbLcCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc, 0, txbLcCodigo.getText());
                    if (pos > -1 && isSearchLcContrato) {
                        isSearchLcNome = false;
                        txbLcNome.setText(_possibleSuggestionsLc[pos][1]);
                        isSearchLcNome = true;
                    }
                } else {
                    isSearchLcContrato = false;
                    isSearchLcNome = true;
                }
            }
        });

        txbLcNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc,1, txbLcNome.getText());
                String pcontrato = null;
                try {pcontrato = txbLcCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchLcNome && pcontrato == null) {
                    isSearchLcContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsLc,1,txbLcNome.getText())) {
                        txbLcCodigo.setText(_possibleSuggestionsLc[pos][0]);
                    } else {
                        WindowEnderecos(txbLcNome.getText());
                    }
                    isSearchLcContrato = true;
                } else {
                    isSearchLcContrato = true;
                    isSearchLcNome = false;
                }
            }
        });
    }

    private void WindowEnderecos(String snome) {
        try {
            AnchorPane root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/Movimento/Alteracao/Enderecos.fxml"));
            } catch (Exception e) {e.printStackTrace();}
            UICustomComponent wrappedRoot = new UICustomComponent(root);

            UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
            internalFrame.setLayout(new UIBorderLayout());
            internalFrame.setModal(true);
            internalFrame.setResizable(false);
            internalFrame.setMaximizable(false);
            internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
            internalFrame.setTitle("Endere�os");
            //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
            internalFrame.setClosable(true);


            internalFrame.setBackground(new UIColor(103,165, 162));
            //internalFrame.setBackground(new UIColor(51,81, 135));

            internalFrame.pack();
            internalFrame.setVisible(true);

            root.fireEvent(new paramEvent(new Object[] {anchorPane, txbLcNome.getText()}, paramEvent.GET_PARAM));
        } catch (Exception e) {e.printStackTrace();}
    }

    private void AutocompletePr() {
        _possibleSuggestionsPrRegistro = new String[]{};
        _possibleSuggestionsPrNome = new String[]{};
        _possibleSuggestionsPr = new String[][]{};
        possibleSuggestionsPrRegistro = new HashSet<String>();
        possibleSuggestionsPrNome = new HashSet<String>();
        isSearchPrRegistro = true;
        isSearchPrNome = true;

        try {
            TextFields.bindAutoCompletion(txbPrCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbPrNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Proprietarios
        qSQL = "SELECT p_rgprp AS l_contrato, p_nome AS l_nome FROM proprietarios WHERE exclusao is null ORDER BY p_rgprp;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("l_contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                _possibleSuggestionsPrRegistro = FuncoesGlobais.ArrayAdd(_possibleSuggestionsPrRegistro, qcontrato);
                possibleSuggestionsPrRegistro = new HashSet<>(Arrays.asList(_possibleSuggestionsPrRegistro));

                _possibleSuggestionsPrNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsPrNome, qnome);
                possibleSuggestionsPrNome = new HashSet<>(Arrays.asList(_possibleSuggestionsPrNome));

                _possibleSuggestionsPr = FuncoesGlobais.ArraysAdd(_possibleSuggestionsPr, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbPrCodigo, possibleSuggestionsPrRegistro);
        TextFields.bindAutoCompletion(txbPrNome, possibleSuggestionsPrNome);

        txbPrCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbPrCodigo.setText(null);
                txbPrNome.setText(null);

                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbPrCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsPr, 0, txbPrCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchPrNome = false;
                        txbPrNome.setText(_possibleSuggestionsPr[pos][1]);
                        isSearchPrNome = true;
                    }
                } else {
                    isSearchPrRegistro = false;
                    isSearchPrNome = true;
                }
            }
        });

        txbPrNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsPr,1, txbPrNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbPrCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchPrNome && pcontrato != null) {
                    isSearchPrRegistro = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsPr,1,txbPrNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsPr[pos][0]);
                    }
                    isSearchPrRegistro = true;
                } else {
                    isSearchPrRegistro = true;
                    isSearchPrNome = false;
                }
            }
        });
    }

    private void AutocompleteAdm() {
        _possibleSuggestionsAvCodigo = new String[]{};
        _possibleSuggestionsAvNome = new String[]{};
        _possibleSuggestionsAv = new String[][]{};
        possibleSuggestionsAvCodigo = new HashSet<String>();
        possibleSuggestionsAvNome = new HashSet<String>();
        isSearchAvCodigo = true;
        isSearchAvNome = true;

        try {
            TextFields.bindAutoCompletion(txbAvCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbAvNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Contas ADM Fixas
        qSQL = "SELECT id, codigo, descricao FROM adm ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("codigo");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsAvCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvCodigo, qcontrato);
                possibleSuggestionsAvCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsAvCodigo));

                _possibleSuggestionsAvNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvNome, qnome);
                possibleSuggestionsAvNome = new HashSet<>(Arrays.asList(_possibleSuggestionsAvNome));

                _possibleSuggestionsAv = FuncoesGlobais.ArraysAdd(_possibleSuggestionsAv, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        // Contas ADM Variaveis
        qSQL = "SELECT id, codigo, descricao FROM adm_contas WHERE tipo = 'ADM' AND deletada = false ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("codigo");} catch (SQLException e) {}
                try {qnome = imv.getString("descricao");} catch (SQLException e) {}
                _possibleSuggestionsAvCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvCodigo, qcontrato);
                possibleSuggestionsAvCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsAvCodigo));

                _possibleSuggestionsAvNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsAvNome, qnome);
                possibleSuggestionsAvNome = new HashSet<>(Arrays.asList(_possibleSuggestionsAvNome));

                _possibleSuggestionsAv = FuncoesGlobais.ArraysAdd(_possibleSuggestionsAv, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbAvCodigo, possibleSuggestionsAvCodigo);
        TextFields.bindAutoCompletion(txbAvNome, possibleSuggestionsAvNome);

        txbAvCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbAvCodigo.setText(null);
                txbAvNome.setText(null);

                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbAvCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsAv, 0, txbAvCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchAvNome = false;
                        txbAvNome.setText(_possibleSuggestionsAv[pos][1]);
                        isSearchAvNome = true;
                    }
                } else {
                    isSearchAvCodigo = false;
                    isSearchAvNome = true;
                }
            }
        });

        txbAvNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsAv,1, txbAvNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbAvCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchAvNome && pcontrato != null) {
                    isSearchAvCodigo = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsAv,1,txbAvNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsAv[pos][0]);
                    }
                    isSearchAvCodigo = true;
                } else {
                    isSearchAvCodigo = true;
                    isSearchAvNome = false;
                }
            }
        });
    }

    private void AutocompleteSocios() {
        _possibleSuggestionsScCodigo = new String[]{};
        _possibleSuggestionsScNome = new String[]{};
        _possibleSuggestionsSc = new String[][]{};
        possibleSuggestionsScCodigo = new HashSet<String>();
        possibleSuggestionsScNome = new HashSet<String>();
        isSearchScCodigo = true;
        isSearchScNome = true;

        try {
            TextFields.bindAutoCompletion(txbAvCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbAvNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Socios_Adm
        qSQL = "SELECT id, nome FROM adm_socios ORDER BY id;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("id");} catch (SQLException e) {}
                try {qnome = imv.getString("nome");} catch (SQLException e) {}
                _possibleSuggestionsScCodigo = FuncoesGlobais.ArrayAdd(_possibleSuggestionsScCodigo, qcontrato);
                possibleSuggestionsScCodigo = new HashSet<>(Arrays.asList(_possibleSuggestionsScCodigo));

                _possibleSuggestionsScNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsScNome, qnome);
                possibleSuggestionsScNome = new HashSet<>(Arrays.asList(_possibleSuggestionsScNome));

                _possibleSuggestionsSc = FuncoesGlobais.ArraysAdd(_possibleSuggestionsSc, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(txbScCodigo, possibleSuggestionsScCodigo);
        TextFields.bindAutoCompletion(txbScNome, possibleSuggestionsScNome);

        txbScCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbScCodigo.setText(null);
                txbScNome.setText(null);

                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbScCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsSc, 0, txbScCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchScNome = false;
                        txbScNome.setText(_possibleSuggestionsSc[pos][1]);
                        isSearchScNome = true;
                    }
                } else {
                    isSearchScCodigo = false;
                    isSearchScNome = true;
                }
            }
        });

        txbScNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsSc,1, txbScNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbScCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchScNome && pcontrato != null) {
                    isSearchScCodigo = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsSc,1,txbScNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsSc[pos][0]);
                    }
                    isSearchScCodigo = true;
                } else {
                    isSearchScCodigo = true;
                    isSearchScNome = false;
                }
            }
        });
    }

    private void AutocompleteContas() {
        _possibleSuggestionsCtCodigo = new String[]{};
        _possibleSuggestionsCtNome = new String[]{};
        _possibleSuggestionsCt = new String[][]{};
        possibleSuggestionsCtCodigo = new HashSet<String>();
        possibleSuggestionsCtNome = new HashSet<String>();
        isSearchCtCodigo = true;
        isSearchCtNome = true;

        try {
            TextFields.bindAutoCompletion(txbAvCodigo, new HashSet<String>());
            TextFields.bindAutoCompletion(txbAvNome, new HashSet<String>());
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

        TextFields.bindAutoCompletion(txbCtCodigo, possibleSuggestionsCtCodigo);
        TextFields.bindAutoCompletion(txbCtNome, possibleSuggestionsCtNome);

        txbCtCodigo.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                txbCtCodigo.setText(null);
                txbCtNome.setText(null);

                if (tgbCrDb.isSelected()) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = txbCtCodigo.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsCt, 0, txbCtCodigo.getText());
                    if (pos > -1 && isSearchPrRegistro) {
                        isSearchCtNome = false;
                        txbCtNome.setText(_possibleSuggestionsCt[pos][1]);
                        isSearchCtNome = true;
                    }
                } else {
                    isSearchCtCodigo = false;
                    isSearchCtNome = true;
                }
            }
        });

        txbCtNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsCt,1, txbCtNome.getText());} catch (NullPointerException e){}
                String pcontrato = null;
                try {pcontrato = txbCtCodigo.getText();} catch (NullPointerException e) {}
                if (pos > -1 && isSearchCtNome && pcontrato != null) {
                    isSearchCtCodigo = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsCt,1,txbCtNome.getText())) {
                        txbPrCodigo.setText(_possibleSuggestionsCt[pos][0]);
                    }
                    isSearchCtCodigo = true;
                } else {
                    isSearchCtCodigo = true;
                    isSearchCtNome = false;
                }
            }
        });
    }

    private void abaPreAviso() {
        List<TablePreAviso> data = new ArrayList<TablePreAviso>();
        ResultSet vrs;
        String Sql = "SELECT id, conta, registro, tipo, texto, valor, pre_usr, pre_dtlanc, pre_obs FROM avisos WHERE dtrecebimento is null and not pre_dtlanc is null;";

        try {
            vrs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY);
            while (vrs.next()) {
                TablePreAviso registro = new TablePreAviso();
                try {registro.setId(vrs.getInt("id"));} catch (SQLException e) {}
                try {registro.setConta(tipoConta(vrs.getString("conta")));} catch (SQLException e) {}
                try {registro.setCodigo(vrs.getString("registro"));} catch (SQLException e) {}
                try {registro.setTipo(vrs.getString("tipo"));} catch (SQLException e) {}
                try {registro.setTexto(getPreview(vrs.getString("texto")));} catch (SQLException e) {}
                try {registro.setValor(vrs.getBigDecimal("valor"));} catch (SQLException e) {}
                try {registro.setUsuario(vrs.getString("pre_usr"));} catch (SQLException e) {}
                try {registro.setDatalanc(vrs.getDate("pre_dtlanc"));} catch (SQLException e) {}
                try {registro.setObs(vrs.getString("pre_obs"));} catch (SQLException e) {}

                data.add(new TablePreAviso(registro.getId(), registro.getConta(), registro.getCodigo(), registro.getTipo(), registro.getTexto(), registro.getValor(), registro.getUsuario(), registro.getDatalanc(), registro.getObs()));
            }
            vrs.close();
        } catch (SQLException e) {}

        tblLan_id.setCellValueFactory(new PropertyValueFactory("id"));
        tblLan_conta.setCellValueFactory(new PropertyValueFactory("conta"));
        tblLan_codigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        tblLan_tipo.setCellValueFactory(new PropertyValueFactory("tipo"));
        tblLan_texto.setCellValueFactory(new PropertyValueFactory("texto"));
        /*tblLan_texto.setCellFactory(new Callback<TableColumn<TablePreAviso, String>, TableCell<TablePreAviso, String>>() {

            @Override
            public TableCell<TablePreAviso, String> call(TableColumn<TablePreAviso, String> param) {
                return new HTMLCell();
            }
        });*/

        tblLan_valor.setCellValueFactory(new PropertyValueFactory("valor"));
        tblLan_usuario.setCellValueFactory(new PropertyValueFactory("usuario"));
        tblLan_dtlanc.setCellValueFactory(new PropertyValueFactory("datalanc"));
        tblLan_obs.setCellValueFactory(new PropertyValueFactory("obs"));

        tbvPreAvisos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvPreAvisos.setItems(FXCollections.observableArrayList(data));
        tbvPreAvisos.setEditable(true);

        tbvPreAvisos.setOnMouseClicked(event -> {
            ShowTextoPreAviso();
        });
        tbvPreAvisos.setOnKeyReleased(event -> {
            ShowTextoPreAviso();
        });
    }

    private void ShowTextoPreAviso() {
        String cTexto = ""; Boolean cCRDB = false; BigDecimal cValor = new BigDecimal("0");
        if (!tbvPreAvisos.getSelectionModel().getSelectedItems().isEmpty()) {
            cTexto = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getObs();
            cCRDB = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getTipo() == "CRE" ? false : true;
            cValor = tbvPreAvisos.getSelectionModel().getSelectedItems().get(0).getValor();
        }

        if (cValor.equals(BigDecimal.ZERO)) return;

        txaDescPAv.setText(cTexto);
        CrDbPre(cCRDB);
        txbValor.setText(LerValor.BigDecimalToCurrency(cValor));
    }

    private String tipoConta(String conta) {
        String retorno = "";
        switch (conta) {
            case "0": retorno = "ADM"; break;
            case "1": retorno = "PROPRIET�RIOS"; break;
            case "2": retorno = "LOCAT�RIOS"; break;
            case "3": retorno = "S�CIOS"; break;
            case "4": retorno = "CONTAS"; break;
        }
        return retorno;
    }

    private String getPreview(String _html) {
        String preview = null;
        if (_html != null) {
            Document doc = Jsoup.parse(_html);
            preview = doc.body().text();
        }
        return preview;
    }

    private void CrDbPre(boolean bCRDB) {
        try {tpagtos.getChildren().remove(0);} catch (Exception e) {}
        if (bCRDB) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Pagamentos.fxml"));
                Pane root = (Pane)loader.load();
                controllerPag = loader.getController();
                tpagtos.getChildren().add(root);
                root.setLayoutX(0); root.setLayoutY(0);
            } catch (Exception e) {e.printStackTrace();}

            tgbCrDb.setText("D�bito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#ff5400, #be1d00);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;" +
                            "-fx-font: bold italic 10pt 'Arial'"
            );
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PagRec/Recebimento.fxml"));
                Pane root = (Pane)loader.load();
                controllerRec = loader.getController();
                tpagtos.getChildren().add(root);
                root.setLayoutX(0); root.setLayoutY(0);
            } catch (Exception e) {e.printStackTrace();}

            tgbCrDb.setText("Cr�dito");
            tgbCrDb.setStyle(
                    "-fx-background-color: linear-gradient(#00ff54, #00be1d);" +
                            "-fx-background-radius: 30;" +
                            "-fx-background-insets: 0;" +
                            "-fx-text-fill: white;"+
                            "-fx-font: bold italic 10pt 'Arial'"
            );
        }

        MaskFieldUtil.monetaryField(txbValor);
        txbValor.requestFocus();
        txbValor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // gotfocus
                if (bCRDB) {
                    controllerPag.Formas_DisableAll();
                } else {
                    controllerRec.Formas_DisableAll();
                }
            } else {
                // lostfocus
                if (bCRDB) {
                    controllerPag.Formas_Disable(false);
                    controllerPag.SetValor(new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText())));
                } else {
                    controllerRec.Formas_Disable(false);
                    controllerRec.SetValor(new BigDecimal(LerValor.Number2BigDecimal(txbValor.getText())));
                }
            }
        });
    }

}
