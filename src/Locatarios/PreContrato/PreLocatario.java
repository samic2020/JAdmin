package Locatarios.PreContrato;


import Classes.*;
import Editor.activetree.CustomJEditor;
import Editor.activetree.ProcessaTexto;
import Funcoes.*;
import Locatarios.Fiadores.pfiadoresModel;
import Locatarios.LocAdicionais;
import Locatarios.Socios.psociosModel;
import Locatarios.inclusaoLocatario;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import masktextfield.MaskTextField;

import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;
import samic.serversamic.Consulta;
import samic.serversamic.SamicServer;
import samic.serversamic.SamicServerImplService;

/**
 * Created by supervisor on 02/08/16.
 */
public class PreLocatario extends Component implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    String plSql = "SELECT * FROM prelocatarios ORDER BY l_contrato;";
    ResultSet rs = null;
    String viea = null;

    boolean bInc = false;
    int pID = -1;

    Object[] fields;
    Object[] buttons;

    JTextArea text = new JTextArea();

    @FXML private AnchorPane l_anchorPane;
    @FXML private TextField l_rgprp;
    @FXML private TextField l_rgimv;
    @FXML private TextField l_tpimovel;
    @FXML private TextField l_contrato;
    @FXML private RadioButton l_fisica;
    @FXML private ToggleGroup l_gfisjur;
    @FXML private RadioButton l_juridica;
    @FXML private MaskTextField l_cpfcnpj;
    @FXML private TextField l_rginsc;
    @FXML private TabPane l_tabfisjur;
    @FXML private Tab l_tabFisica;
    @FXML private TextField l_f_nome;
    @FXML private ComboBox<String> l_f_sexo;
    @FXML private DatePicker l_f_dtnasc;
    @FXML private TextField l_f_nacionalidade;
    @FXML private ComboBox<String> l_f_estcivil;
    @FXML private MenuButton l_f_tel_menu;
    @FXML private MenuItem l_f_teladc;
    @FXML private MenuItem l_f_teldel;
    @FXML private ComboBox<ptelcontatoModel> l_f_tel;
    @FXML private TextField l_f_mae;
    @FXML private TextField l_f_pai;
    @FXML private TextField l_f_empresa;
    @FXML private DatePicker l_f_dtadmissao;
    @FXML private TextField l_f_endereco;
    @FXML private Button l_f_btFindEndereco;
    @FXML private TextField l_f_numero;
    @FXML private TextField l_f_cplto;
    @FXML private TextField l_f_bairro;
    @FXML private TextField l_f_cidade;
    @FXML private TextField l_f_estado;
    @FXML private MaskTextField l_f_cep;
    @FXML private TextField l_f_cargo;
    @FXML private TextField l_f_salario;
    @FXML private TextField l_f_conjugue;
    @FXML private DatePicker l_f_conjuguedtnasc;
    @FXML private ComboBox<String> l_f_conjuguesexo;
    @FXML private TextField l_f_conjuguecpf;
    @FXML private TextField l_f_conjuguerg;
    @FXML private TextField l_f_conjuguesalario;
    @FXML private TextField l_f_conjugueempresa;
    @FXML private MenuButton l_f_conjuguetelefone_menu;
    @FXML private MenuItem l_f_conjugueteladc;
    @FXML private MenuItem l_f_conjugueteldel;
    @FXML private ComboBox<ptelcontatoModel> l_f_conjuguetelefone;
    @FXML private  MenuButton l_f_email_menu;
    @FXML private MenuItem l_f_emailadc;
    @FXML private MenuItem l_f_emaildel;
    @FXML private ComboBox<pemailModel> l_f_email;
    @FXML private Tab l_tabJuridica;
    @FXML private TextField l_j_razao;
    @FXML private TextField l_j_fantasia;
    @FXML private TextField l_j_endereco;
    @FXML private Button l_j_btFindEndereco;
    @FXML private TextField l_j_numero;
    @FXML private TextField l_j_cplto;
    @FXML private TextField l_j_bairro;
    @FXML private TextField l_j_cidade;
    @FXML private TextField l_j_estado;
    @FXML private MaskTextField l_j_cep;
    @FXML private DatePicker l_j_dtctrosocial;
    @FXML private MenuButton l_j_tel_menu;
    @FXML private MenuItem l_j_teladc;
    @FXML private MenuItem l_j_teldel;
    @FXML private ComboBox<ptelcontatoModel> l_j_tel;
    @FXML private MenuButton l_j_email_menu;
    @FXML private MenuItem l_j_emailadc;
    @FXML private MenuItem l_j_emaildel;
    @FXML private ComboBox<pemailModel> l_j_email;
    @FXML private TableView<psociosModel> l_j_socios;
    @FXML private TableColumn<psociosModel, String> l_j_socioscpfcnpj;
    @FXML private TableColumn<psociosModel, String> l_j_sociosnome;
    @FXML private TableView<pfiadoresModel> l_fiadores;
    @FXML private TableColumn<pfiadoresModel, String> l_fiadorescpfcnpj;
    @FXML private TableColumn<pfiadoresModel, String> l_fiadoresnome;

    // Seguradora/Deposito
    @FXML private ToggleGroup depseg;
    @FXML private RadioButton deposito;
    @FXML private TextField vrdeposito;

    @FXML private RadioButton seguradora;
    @FXML private ComboBox<String> nmseguradora;
    @FXML private TextField nroapolice;
    @FXML private DatePicker dtapolice;

    // Carteira
    @FXML private DatePicker ct_dtinicio;
    @FXML private DatePicker ct_dtfim;
    @FXML private TextField ct_aluguel;

    // Botoes
    @FXML private Button l_btIncluir;
    @FXML private Button l_btAlterar;
    @FXML private Button l_btExcluir;
    @FXML private Button l_btPrevious;
    @FXML private Button l_btNext;
    @FXML private Button l_btGravar;
    @FXML private Button l_btRetornar;
    @FXML private Button l_btPrint;
    @FXML private Button l_btConcretizar;

    // Arquivo de contrato
    @FXML private TextField nm_contrato;
    @FXML private Button btFindCtro;

    // Locatários Adicionais
    @FXML private TableView<LocAdicionais> l_adicionais;
    @FXML private TableColumn<LocAdicionais, Integer> l_adcid;
    @FXML private TableColumn<LocAdicionais, String> l_adccpfcnpj;
    @FXML private TableColumn<LocAdicionais, String> l_adcnome;

    private int NextField(String field, TextField[] fields) {
        int pos = 0;
        for (pos = 0; pos < fields.length; pos++) {
            if (fields[pos].getId().equalsIgnoreCase(field)) break;
        }
        return pos + 1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        l_anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
            }
        });
        l_anchorPane.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });

        l_anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            try {LerFiadores(l_contrato.getText());} catch (NullPointerException e) {}
            try {LerAdicionais(this.pID);} catch (NullPointerException e) {}
            try {LerSocios(l_contrato.getText());} catch (NullPointerException e) {}
        });

        //Platform.runLater(() -> { 
        verifyBotoes(); //});

        l_contrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                Task task = new Task() {
                    @Override
                    protected Integer call() throws Exception {
                        l_contrato.setEditable(false);
                        l_contrato.setCursor(Cursor.WAIT); //Change cursor to wait style
                        MoveTo("l_contrato", l_contrato.getText().trim());
                        l_contrato.setCursor(Cursor.DEFAULT); //Change cursor to default style
                        return 0;
                    }
                };
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });

        l_cpfcnpj.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
                l_cpfcnpj.setText(FuncoesGlobais.LimpaCpfCnpj(l_cpfcnpj.getText()));
            } else {
                // out focus
                l_cpfcnpj.setText(FuncoesGlobais.FormatCpfCnpj(l_cpfcnpj.getText()));
                if (FuncoesGlobais.LimpaCpfCnpj(l_cpfcnpj.getText()).length() == 11) {
                    l_tabfisjur.getSelectionModel().select(0);
                } else {
                    l_tabfisjur.getSelectionModel().select(1);
                }
            }
        });

        new cpoTelefones().cpoTelefones(l_f_tel);
        new cpoTelefones().cpoTelefones(l_f_conjuguetelefone);
        new cpoEmails().cpoEmails(l_f_email);

        new cpoTelefones().cpoTelefones(l_j_tel);
        new cpoEmails().cpoEmails(l_j_email);

        l_f_btFindEndereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                l_f_endereco.setText(b.getLogradouro());
                l_f_bairro.setText(b.getBairro());
                l_f_cidade.setText(b.getLocalidade());
                l_f_estado.setText(b.getUf());
                l_f_cep.setText(b.getCep());
                l_f_numero.setText(null); l_f_cplto.setText(null);
                l_f_numero.requestFocus();
            });
        });

        l_f_cep.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
            } else {
                // out focus
                if (l_f_endereco.getText() == null && l_f_cep.getText().length() == 9) {
                    try {
                        ViaCEPClient client = new ViaCEPClient();
                        ViaCEPEndereco endereco = client.getEndereco(l_f_cep.getText());

                        l_f_endereco.setText(endereco.getLogradouro());
                        l_f_bairro.setText(endereco.getBairro());
                        l_f_cidade.setText(endereco.getLocalidade());
                        l_f_estado.setText(endereco.getUf());

                        l_f_numero.setText(null); l_f_cplto.setText(null);
                        l_f_numero.requestFocus();
                    } catch (IOException ex) {}
                }
            }
        });

        l_j_btFindEndereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                l_j_endereco.setText(b.getLogradouro());
                l_j_bairro.setText(b.getBairro());
                l_j_cidade.setText(b.getLocalidade());
                l_j_estado.setText(b.getUf());
                l_j_cep.setText(b.getCep());
                l_j_numero.setText(null); l_f_cplto.setText(null);
                l_j_numero.requestFocus();
            });
        });

        l_j_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if (l_j_endereco.getText() == null  && l_j_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(l_j_cep.getText());

                            l_j_endereco.setText(endereco.getLogradouro());
                            l_j_bairro.setText(endereco.getBairro());
                            l_j_cidade.setText(endereco.getLocalidade());
                            l_j_estado.setText(endereco.getUf());

                            l_j_numero.setText(null); l_j_cplto.setText(null);
                            l_j_numero.requestFocus();
                        } catch (IOException ex) {}
                    }
                }
            }
        });

        l_fisica.setOnAction(event -> {
            l_tabfisjur.getSelectionModel().select(0);
        });
        l_juridica.setOnAction(event -> {
            l_tabfisjur.getSelectionModel().select(1);
        });

        l_f_sexo.setItems(new pSexo().Sexo());
        l_f_estcivil.setItems(new pEstCivil().EstCivil());
        l_f_conjuguesexo.setItems(new pSexo().Sexo());

        l_fiadores.setOnMousePressed(event -> {
            if (!bInc) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try { ChamaTela("Fiadores", "/Locatarios/Fiadores/Fiadores.fxml", "loca.png"); } catch (Exception ex) { }
                }
            }
        });

        l_adicionais.setOnMousePressed(event -> {
            if (!bInc) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try { ChamaTela("adcLocatarios", "/Locatarios/Adicionais/AdcLocatarios.fxml", "loca.png"); } catch (Exception ex) { }
                }
            }
        });

        l_j_socios.setOnMousePressed(event -> {
            if (!bInc) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try { ChamaTela("Socios", "/Locatarios/Socios/Socios.fxml", "loca.png"); } catch (Exception ex) { }
                }
            }
        });

        l_btGravar.setOnAction(event -> {
            if (this.viea == null) this.viea = "VIEA";
            if (this.viea.contains("I") || this.viea.contains("A")) {
                if (bInc) {
                    int NewRgPrp = 0;
                    try {
                        NewRgPrp = Integer.parseInt(conn.LerParametros("PRECONTRATO"));
                    } catch (SQLException ex) {}

                    String cPar[] = {"PRECONTRATO",String.valueOf(NewRgPrp + 1),"NUMERICO"};
                    try {
                        conn.GravarParametros(cPar);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    salvar(bInc, NewRgPrp);
                    l_contrato.setText(String.valueOf(NewRgPrp));
                } else {
                    salvar(bInc, this.pID);
                    String tpID = String.valueOf(this.pID);
                    try {rs.close();} catch (SQLException ex) {}
                    OpenLocatarios(true);
                    try {MoveTo("l_id",tpID);} catch (SQLException ex1) {}
                }

                verifyBotoes();
                bInc = false;

                // Atualiza
                try {
                    int pos = rs.getRow();
                    DbMain.FecharTabela(rs);

                    rs = conn.AbrirTabela(plSql, ResultSet.CONCUR_UPDATABLE);
                    rs.absolute(pos);
                    if (DbMain.RecordCount(rs) <= 0) {
                        new Controle(this.buttons).BotaoEnabled(new Object[] {l_btIncluir, l_btRetornar});
                    } else {
                        new Controle(this.buttons).BotaoDisabled(new Object[] {l_btGravar});
                    }
                } catch (SQLException e) {}

                new Controle(this.fields).FieldsEnabled(false);
            }
        });

        l_btIncluir.setOnAction(event -> {
            l_cpfcnpj.setText("");
            inclusaoLocatario dialog = new inclusaoLocatario();
            Optional<pimoveisModel> result = null;
            try {
                result = dialog.inclusaoLocatario();
            } catch (Exception e) {
                LerLoca();
                bInc = false;
                new Controle(this.buttons).BotaoDisabled(new Object[]{l_btGravar});
                new Controle(this.fields).FieldsEnabled(false);
                return;
            }
            if (!result.isPresent()) {
                LerLoca();
                bInc = false;
                new Controle(this.buttons).BotaoDisabled(new Object[]{l_btGravar});
                new Controle(this.fields).FieldsEnabled(false);
                return;
            }

            result.ifPresent(b -> {
                // Tela de Pesquisa
                try {
                    ChamaTela("Pesquisa", "/Pesquisa/Pesquisa.fxml", "loca.png");
                } catch (Exception ex) {}

                if (l_cpfcnpj.getText() == null) l_cpfcnpj.setText("");
                if (l_cpfcnpj.getText().trim().equalsIgnoreCase("")) {
                    this.bInc = true;
                    LimpaTela();

                    new Controle(this.buttons).BotaoEnabled(new Object[]{l_btGravar, l_btRetornar});
                    new Controle(this.fields).FieldsEnabled(true);

                    l_contrato.setText(null);
                    l_contrato.setDisable(true);
                    l_rgprp.setText(b.getRgprp());
                    l_rgimv.setText(b.getRgimv());
                    l_tpimovel.setText(b.getTipo());
                    l_fisica.requestFocus();
                } else {
                    LerLoca();
                    bInc = false;
                    new Controle(this.buttons).BotaoDisabled(new Object[]{l_btGravar});
                    new Controle(this.fields).FieldsEnabled(false);
                    return;
                }
            });
        });

        l_btPrevious.setOnAction(event -> {
            try {
                rs.previous();
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                LerLoca();
            } catch (SQLException e) {}
        });

        l_btNext.setOnAction(event -> {
            try {
                rs.next();
                if (rs.isAfterLast()) {
                    rs.previous();
                }
                LerLoca();
            } catch (SQLException e) {}
        });

        l_btRetornar.setOnAction(event -> {
            if (bInc) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Não")) return;
            }
            if (!l_btGravar.isDisabled()) {
                LerLoca();
                bInc = false;
                new Controle(this.buttons).BotaoDisabled(new Object[]{l_btGravar});
                new Controle(this.fields).FieldsEnabled(false);
                return;
            }
            ((FXInternalWindow) l_anchorPane.getParent().getParent().getParent()).close();
        });

        l_btAlterar.setOnAction(event -> {
            this.bInc = false;

            new Controle(this.buttons).BotaoEnabled(new Object[] {l_btGravar, l_btRetornar});
            new Controle(this.fields).FieldsEnabled(true);

            l_fisica.requestFocus();
        });

        l_f_conjugueteladc.setOnAction(event -> {
            adcTelefones dialog = new adcTelefones();
            Optional<ptelcontatoModel> result = dialog.adcTelefones();
            result.ifPresent(b -> {
                ObservableList<ptelcontatoModel> tels = l_f_conjuguetelefone.getItems();
                tels.addAll(b);
                l_f_conjuguetelefone.setItems(tels);
                try {l_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        l_f_conjugueteldel.setOnAction(event -> {
            if (!l_f_conjuguetelefone.getItems().isEmpty()) l_f_conjuguetelefone.getItems().removeAll(l_f_conjuguetelefone.getSelectionModel().getSelectedItem());
            try {l_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
        });

        l_f_emailadc.setOnAction(event -> {
            adcEmails dialog = new adcEmails();
            Optional<pemailModel> result = dialog.adcEmails(false);
            result.ifPresent(b -> {
                ObservableList<pemailModel> emails = l_f_email.getItems();
                emails.addAll(b);
                l_f_email.setItems(emails);
                try {l_f_email.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        l_f_emaildel.setOnAction(event -> {
            if (!l_f_email.getItems().isEmpty()) l_f_email.getItems().removeAll(l_f_email.getSelectionModel().getSelectedItem());
            try {l_f_email.getSelectionModel().select(0);} catch (Exception e) {}
        });

        l_f_teladc.setOnAction(event -> {
            adcTelefones dialog = new adcTelefones();
            Optional<ptelcontatoModel> result = dialog.adcTelefones();
            result.ifPresent(b -> {
                ObservableList<ptelcontatoModel> tels = l_f_tel.getItems();
                tels.addAll(b);
                l_f_tel.setItems(tels);
                try {l_f_tel.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        l_f_teldel.setOnAction(event -> {
            if (!l_f_tel.getItems().isEmpty()) l_f_tel.getItems().removeAll(l_f_tel.getSelectionModel().getSelectedItem());
            try {l_f_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });

        l_j_emailadc.setOnAction(event -> {
            adcEmails dialog = new adcEmails();
            Optional<pemailModel> result = dialog.adcEmails(false);
            result.ifPresent(b -> {
                ObservableList<pemailModel> emails = l_j_email.getItems();
                emails.addAll(b);
                l_j_email.setItems(emails);
                try {l_j_email.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        l_j_emaildel.setOnAction(event -> {
            if (!l_j_email.getItems().isEmpty()) l_j_email.getItems().removeAll(l_j_email.getSelectionModel().getSelectedItem());
            try {l_j_email.getSelectionModel().select(0);} catch (Exception e) {}
        });

        l_j_teladc.setOnAction(event -> {
            adcTelefones dialog = new adcTelefones();
            Optional<ptelcontatoModel> result = dialog.adcTelefones();
            result.ifPresent(b -> {
                ObservableList<ptelcontatoModel> tels = l_j_tel.getItems();
                tels.addAll(b);
                l_j_tel.setItems(tels);
                try {l_j_tel.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        l_j_teldel.setOnAction(event -> {
            if (!l_j_tel.getItems().isEmpty()) l_j_tel.getItems().removeAll(l_j_tel.getSelectionModel().getSelectedItem());
            try {l_j_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });

        btFindCtro.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Arquivos de Impressão de contratos");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Arquivo de Texto FXRent", "*.rtf"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                nm_contrato.setText(selectedFile.getAbsolutePath());
            }
        });

        l_btPrint.setOnAction(event -> {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String FileName = nm_contrato.getText().trim();
                            if (!new File(FileName).exists()) {
                                return;
                            }

                            String textoContrato = readFile(new File(FileName));

                            // Chama o Processamento
                            ProcessaTexto processaTexto = new ProcessaTexto(l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), textoContrato);
                            InputStream targetStream = processaTexto.getL_targetStream();

                            CustomJEditor editor2 = new CustomJEditor();
                            editor2.set_isPreContrato(true);
                            editor2.postInit();
                            editor2.openDocument(targetStream);
                            editor2.setToTop(true);
                            editor2.setVisible(true);
                        }
                    });
        });

        l_btConcretizar.setOnAction(event -> {
            if (ct_dtinicio.getValue() == null || ct_dtfim.getValue() == null || LerValor.StringToFloat(ct_aluguel.getText()) == 0) {
                new Alert(Alert.AlertType.ERROR, "Aba Locação não completada!").showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Atenção!");
            alert.setHeaderText("Deseja concretizar este pré-lançamento?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                int NewRgPrp = 0;
                try {
                    NewRgPrp = Integer.parseInt(conn.LerParametros("CONTRATO"));
                } catch (SQLException ex) {
                }

                String cPar[] = {"CONTRATO", String.valueOf(NewRgPrp + 1), "NUMERICO"};
                try {
                    conn.GravarParametros(cPar);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Imovel setado para OCUPADO
                String iSql = "UPDATE imoveis SET i_situacao = 'Ocupado' WHERE i_rgimv = '%s'";
                iSql = String.format(iSql, l_rgimv.getText());
                try {conn.ExecutarComando(iSql);} catch (Exception e) {e.printStackTrace();}

                String cSql = "INSERT INTO carteira(" +
                        "rgprp, rgimv, contrato, dtinicio, dtfim, cota, " +
                        "mensal, dtvencimento, referencia) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '01/12', '%s', '%s', '%s');";
                cSql = String.format(cSql,
                            l_rgprp.getText(),
                            l_rgimv.getText(),
                            NewRgPrp,
                            Dates.StringtoString(ct_dtinicio.getEditor().getText(),"dd-MM-yyyy","yyyy-MM-dd"),
                            Dates.StringtoString(ct_dtfim.getEditor().getText(),"dd-MM-yyyy","yyyy-MM-dd"),
                            ct_aluguel.getText().replace(".","").replace(",","."),
                            Dates.DateFormata("yyyy-MM-dd", Dates.DateAdd(Dates.MES,1, Dates.StringtoDate(ct_dtinicio.getEditor().getText(),"dd-MM-yyyy"))),
                            Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES,1, Dates.StringtoDate(ct_dtinicio.getEditor().getText(),"dd-MM-yyyy")))
                       );
                try {conn.ExecutarComando(cSql);} catch (Exception e) {e.printStackTrace();}

                String lSQL = "INSERT INTO locatarios (l_rgprp, l_rgimv, l_contrato, l_tipoimovel, l_fisjur, l_cpfcnpj," +
                        "                   l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade," +
                        "                   l_f_estcivil, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao," +
                        "                   l_f_endereco, l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade," +
                        "                   l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue, l_f_conjuguedtnasc," +
                        "                   l_f_conjuguesexo, l_f_conjuguerg, l_f_conjuguecpf, l_f_conjuguesalario," +
                        "                   l_f_conjugueempresa, l_f_conjuguetelefone, l_f_email, l_j_razao," +
                        "                   l_j_fantasia, l_j_endereco, l_j_numero, l_j_cplto, l_j_bairro," +
                        "                   l_j_cidade, l_j_estado, l_j_cep, l_j_dtctrosocial, l_j_tel, l_j_email," +
                        "                   l_historico, l_avisos, l_msg, l_tprecebimento, l_formaenvio," +
                        "                   l_depseg, l_vrdeposito, l_cdseguradora, l_nrapolice, l_dtapolice) " +
                        "                   SELECT l_rgprp, l_rgimv, '%s' AS l_contrato, l_tipoimovel, l_fisjur, l_cpfcnpj," +
                        "                   l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade," +
                        "                   l_f_estcivil, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao," +
                        "                   l_f_endereco, l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade," +
                        "                   l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue, l_f_conjuguedtnasc," +
                        "                   l_f_conjuguesexo, l_f_conjuguerg, l_f_conjuguecpf, l_f_conjuguesalario," +
                        "                   l_f_conjugueempresa, l_f_conjuguetelefone, l_f_email, l_j_razao," +
                        "                   l_j_fantasia, l_j_endereco, l_j_numero, l_j_cplto, l_j_bairro," +
                        "                   l_j_cidade, l_j_estado, l_j_cep, l_j_dtctrosocial, l_j_tel, l_j_email," +
                        "                   l_historico, l_avisos, l_msg, l_tprecebimento, l_formaenvio," +
                        "                   l_depseg, l_vrdeposito, l_cdseguradora, l_nrapolice, l_dtapolice" +
                        "                   FROM prelocatarios WHERE l_contrato = '%s';";
                lSQL = String.format(lSQL, NewRgPrp, l_contrato.getText().trim());
                try {conn.ExecutarComando(lSQL);} catch (Exception e) {e.printStackTrace();}

                String fSql = "UPDATE fiadores SET f_contrato = '%s' WHERE f_contrato = '%s';";
                fSql = String.format(fSql, NewRgPrp, l_contrato.getText().trim());
                try {conn.ExecutarComando(fSql);} catch (Exception e) {e.printStackTrace();}

                String sSql = "UPDATE socios SET s_contrato = '%s' WHERE s_contrato = '%s';";
                fSql = String.format(sSql, NewRgPrp, l_contrato.getText().trim());
                try {conn.ExecutarComando(sSql);} catch (Exception e) {e.printStackTrace();}

                Object[][] dadosLoca = null;
                try {
                    dadosLoca = conn.LerCamposTabela(new String[] {"l_id"},"locatarios", "l_contrato = ?", new Object[][] {{"string",NewRgPrp}});
                } catch (Exception e) {}
                if (dadosLoca != null) {
                    String aSql = "UPDATE adclocatarios set l_contrato = '%s', l_idloca = '%s' WHERE l_idloca = '%s';";
                    aSql = String.format(aSql, NewRgPrp, dadosLoca[0][3], this.pID);
                    try { conn.ExecutarComando(aSql); } catch (Exception e) { e.printStackTrace(); }
                }
                dadosLoca = null;

                String eSql = "DELETE FROM prelocatarios WHERE l_contrato = '%s';";
                eSql = String.format(eSql, l_contrato.getText().trim());
                try {conn.ExecutarComando(eSql);} catch (Exception e) {e.printStackTrace();}

                LimpaTela();

                try {rs.close();} catch (SQLException ex) {}
                OpenLocatarios(true);

                LerLoca();

                if (DbMain.RecordCount(rs) <= 0) {
                    new Controle(this.buttons).BotaoEnabled(new Object[] {l_btIncluir, l_btRetornar});
                } else {
                    new Controle(this.buttons).BotaoDisabled(new Object[] {l_btGravar});
                }
            }
        });

        l_btExcluir.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Atenção!");
            alert.setHeaderText("Deseja EXCLUIR este pré-lançamento?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                String fSql = "DELETE FROM fiadores WHERE f_contrato = '%s';";
                fSql = String.format(fSql, l_contrato.getText().trim());
                try {conn.ExecutarComando(fSql);} catch (Exception e) {e.printStackTrace();}

                String sSql = "DELETE FROM socios WHERE s_contrato = '%s';";
                fSql = String.format(sSql, l_contrato.getText().trim());
                try {conn.ExecutarComando(sSql);} catch (Exception e) {e.printStackTrace();}

                String eSql = "DELETE FROM prelocatarios WHERE l_contrato = '%s';";
                eSql = String.format(eSql, l_contrato.getText().trim());
                try {conn.ExecutarComando(eSql);} catch (Exception e) {e.printStackTrace();}
            }
        });

        MaskFieldUtil.monetaryField(vrdeposito);
        MaskFieldUtil.monetaryField(ct_aluguel);

        vrdeposito.disableProperty().bind(deposito.selectedProperty().not());
        nmseguradora.disableProperty().bind(seguradora.selectedProperty().not());
        nroapolice.disableProperty().bind(seguradora.selectedProperty().not());
        dtapolice.disableProperty().bind(seguradora.selectedProperty().not());
        l_btPrint.disableProperty().bind(nm_contrato.textProperty().isEmpty());

        PopulateSeguradoras(nmseguradora);

        LimpaTela();
        OpenLocatarios(true);

        this.fields = new Object[] {
                l_fisica, l_juridica, l_cpfcnpj, l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade, l_f_estcivil,
                l_f_tel_menu, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao, l_f_endereco, l_f_btFindEndereco,
                l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade, l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue,
                l_f_conjuguedtnasc, l_f_conjuguesexo, l_f_conjuguecpf, l_f_conjuguerg, l_f_conjuguesalario, l_f_conjugueempresa,
                l_f_conjuguetelefone_menu, l_f_conjuguetelefone, l_f_email_menu, l_f_email, l_j_razao, l_j_fantasia,
                l_j_endereco, l_j_btFindEndereco, l_j_numero, l_j_cplto, l_j_bairro, l_j_cidade, l_j_estado, l_j_cep,
                l_j_dtctrosocial, l_j_tel_menu, l_j_tel, l_j_email_menu, l_j_email, l_j_socios, deposito, vrdeposito,
                seguradora, nmseguradora, nroapolice, dtapolice, l_fiadores, ct_dtinicio, ct_dtfim, ct_aluguel,
                nm_contrato, btFindCtro
        };
        new Controle("PRELOCATARIO: " + l_contrato.getText() + " - ", this.fields).Focus();
        new Controle(this.fields).FieldsEnabled(false);

        this.buttons = new Object[] {
                l_btIncluir, l_btAlterar, l_btExcluir, l_btPrevious, l_btNext,
                l_btGravar, l_btRetornar, l_btPrint, l_btConcretizar
        };

        if (DbMain.RecordCount(rs) <= 0) {
            new Controle(this.buttons).BotaoEnabled(new Object[] {l_btIncluir, l_btRetornar});

            Object[][] props = null;
            try { props = conn.LerCamposTabela(new String[] {"rgprp"}, "proprietarios", ""); } catch (Exception e) {}
            l_btIncluir.setDisable(props != null);
        } else {
            new Controle(this.buttons).BotaoDisabled(new Object[] {l_btGravar});
        }                
    }

    private void save(String file, String texto) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(file));
            fos.write(texto.getBytes());
        } catch (IOException fnfe) {
            fnfe.printStackTrace();
        } finally {
            if (fos != null) { try {fos.close();} catch (IOException ioex) {}}
        }
    }

    private void print() {
        MessageFormat header = createFormat("headerField");
        MessageFormat footer = createFormat("footerField");
        boolean interactive = true;
        boolean background = true;

        PrintingTask task = new PrintingTask(header, footer, interactive);
        if (background) {
            task.execute();
        } else {
            task.run();
        }
    }

    private class PrintingTask extends SwingWorker<Object, Object> {
        private final MessageFormat headerFormat;
        private final MessageFormat footerFormat;
        private final boolean interactive;
        private volatile boolean complete = false;
        private volatile String message;

        public PrintingTask(MessageFormat header, MessageFormat footer,
                            boolean interactive) {
            this.headerFormat = header;
            this.footerFormat = footer;
            this.interactive = interactive;
        }

        @Override
        protected Object doInBackground() {
            try {
                complete = text.print(headerFormat, footerFormat,
                        true, null, null, interactive);
                message = "Printing " + (complete ? "complete" : "canceled");
            } catch (PrinterException ex) {
                message = "Sorry, a printer error occurred";
            } catch (SecurityException ex) {
                message =
                        "Sorry, cannot access the printer due to security reasons";
            }
            return null;
        }

        @Override
        protected void done() {
            message(!complete, message);
        }
    }

    private MessageFormat createFormat(String source) {
        String text = source;
        if (text != null && text.length() > 0) {
            try {
                return new MessageFormat(text);
            } catch (IllegalArgumentException e) {
                error("Desculpe, Este formato é inválido.");
            }
        }
        return null;
    }

    private void message(boolean error, String msg) {
        int type = (error ? JOptionPane.ERROR_MESSAGE :
                JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this, msg, "Printing", type);
    }

    private void error(String msg) {
        message(true, msg);
    }

    private String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }

    private void PopulateSeguradoras(ComboBox<String> seguradora) {
        String sqlTxt = "SELECT DISTINCT s_codigo, s_nome FROM seguradoras ORDER BY s_nome;";
        rs = conn.AbrirTabela(sqlTxt, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String cbx = rs.getString("s_nome");
                seguradora.getItems().addAll(cbx);
            }
        } catch (SQLException e) {e.printStackTrace();}
        try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    private void OpenLocatarios(boolean ler) {
        rs = conn.AbrirTabela(plSql, ResultSet.CONCUR_UPDATABLE);
        try {
            rs.next();
            if (ler) LerLoca();
        } catch (SQLException e) {e.printStackTrace();}
    }

    public boolean MoveTo(String campo, String seek) throws SQLException {
        boolean achei = false;
        int nrow = rs.getRow();
        try {rs.beforeFirst();} catch (SQLException e) {}
        while (rs.next()) {
            if (rs.getInt(campo) == Integer.parseInt(seek)) {
                achei = true;
                break;
            }
        }
        if (!achei) {rs.first(); rs.absolute(nrow);}
        LerLoca();
        return achei;
    }

    private void LimpaTela() {
        l_rgprp.setText(null);
        l_rgimv.setText(null);
        l_tpimovel.setText(null);
        l_contrato.setText(null);

        l_fisica.setSelected(true);
        l_juridica.setSelected(false);

        l_cpfcnpj.setText(null);
        MaskFieldUtil.maxField(l_rginsc,20); l_rginsc.setText(null);

        MaskFieldUtil.maxField(l_f_nome,60); l_f_nome.setText(null);
        l_f_sexo.getSelectionModel().select(0);
        l_f_dtnasc.setValue(null);
        MaskFieldUtil.maxField(l_f_nacionalidade,25); l_f_nacionalidade.setText(null);
        l_f_estcivil.getSelectionModel().select(0);

        l_f_tel.getItems().clear();

        MaskFieldUtil.maxField(l_f_mae,60);l_f_mae.setText(null);
        MaskFieldUtil.maxField(l_f_pai,60); l_f_pai.setText(null);
        MaskFieldUtil.maxField(l_f_empresa,60); l_f_empresa.setText(null);
        l_f_dtadmissao.setValue(null);
        MaskFieldUtil.maxField(l_f_endereco,60); l_f_endereco.setText(null);
        MaskFieldUtil.maxField(l_f_numero,10); l_f_numero.setText(null);
        MaskFieldUtil.maxField(l_f_cplto,15); l_f_cplto.setText(null);
        MaskFieldUtil.maxField(l_f_bairro,25); l_f_bairro.setText(null);
        MaskFieldUtil.maxField(l_f_cidade,25); l_f_cidade.setText(null);
        MaskFieldUtil.maxField(l_f_estado,2); l_f_estado.setText(null);
        l_f_cep.setText(null);
        MaskFieldUtil.maxField(l_f_cargo,25); l_f_cargo.setText(null);
        MaskFieldUtil.monetaryField(l_f_salario); l_f_salario.setText("0,00");
        MaskFieldUtil.maxField(l_f_conjugue,60); l_f_conjugue.setText(null);
        l_f_conjuguedtnasc.setValue(null);
        l_f_conjuguesexo.getSelectionModel().select(0);
        MaskFieldUtil.maxField(l_f_conjuguerg,20); l_f_conjuguerg.setText(null);
        MaskFieldUtil.cpfCnpjField(l_f_conjuguecpf); l_f_conjuguecpf.setText(null);
        MaskFieldUtil.monetaryField(l_f_conjuguesalario); l_f_conjuguesalario.setText("0,00");
        MaskFieldUtil.maxField(l_f_conjugueempresa,60); l_f_conjugueempresa.setText(null);

        l_f_conjuguetelefone.getItems().clear();

        l_f_email.getItems().clear();

        MaskFieldUtil.maxField(l_j_razao,60); l_j_razao.setText(null);
        MaskFieldUtil.maxField(l_j_fantasia,60); l_j_fantasia.setText(null);

        MaskFieldUtil.maxField(l_j_endereco,60); l_j_endereco.setText(null);
        MaskFieldUtil.maxField(l_j_numero,10); l_j_numero.setText(null);
        MaskFieldUtil.maxField(l_j_cplto,15); l_j_cplto.setText(null);
        MaskFieldUtil.maxField(l_j_bairro,25); l_j_bairro.setText(null);
        MaskFieldUtil.maxField(l_j_cidade,25); l_j_cidade.setText(null);
        MaskFieldUtil.maxField(l_j_estado,2); l_j_estado.setText(null);
        l_j_cep.setText(null);
        l_j_dtctrosocial.setValue(null);

        l_j_tel.getItems().clear();

        l_j_email.getItems().clear();

        deposito.setSelected(false);
        seguradora.setSelected(false);
        vrdeposito.setText("0,00");
        nmseguradora.getSelectionModel().select(-1);
        nroapolice.setText(null);
        dtapolice.getEditor().setText(null);
        nm_contrato.setText(null);

        ct_dtinicio.getEditor().setText(null);
        ct_dtfim.getEditor().setText(null);
        ct_aluguel.setText("0,00");
    }

    private void LerLoca() {
        try {this.pID = rs.getInt("l_id");} catch (SQLException e) {this.pID = -1;}
        try {l_rgprp.setEditable(false);l_rgprp.setText(rs.getString("l_rgprp"));} catch (SQLException e) {l_rgprp.setText(null);}
        try {l_rgimv.setEditable(false);l_rgimv.setText(rs.getString("l_rgimv"));} catch (SQLException e) {l_rgimv.setText(null);}
        try {l_tpimovel.setEditable(false);l_tpimovel.setText(rs.getString("l_tipoimovel"));} catch (SQLException e) {l_tpimovel.setText(null);}
        try {l_contrato.setEditable(false);l_contrato.setText(rs.getString("l_contrato"));} catch (SQLException e) {l_contrato.setText(null);}

        try {if (rs.getBoolean("l_fisjur")) l_fisica.setSelected(true); else l_juridica.setSelected(true);} catch (SQLException e) {}
        if (l_fisica.isSelected()) {
            l_tabfisjur.getSelectionModel().select(0);
        } else {
            l_tabfisjur.getSelectionModel().select(1);
        }

        try {l_cpfcnpj.setText(rs.getString("l_cpfcnpj"));} catch (SQLException e) {l_cpfcnpj.setText(null);}
        try {l_rginsc.setText(rs.getString("l_rginsc"));} catch (SQLException e) {l_rginsc.setText(null);}

        try {l_f_nome.setText(rs.getString("l_f_nome"));} catch (SQLException e) {l_f_nome.setText(null);}
        try {l_f_sexo.getSelectionModel().select(rs.getString("l_f_sexo"));} catch (SQLException e) {l_f_sexo.getSelectionModel().select(0);}

        try {l_f_dtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("l_f_dtnasc")));} catch (Exception e) {l_f_dtnasc.getEditor().clear();}
        try {l_f_dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("l_f_dtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {l_f_dtnasc.setValue(null);}

        try {l_f_nacionalidade.setText(rs.getString("l_f_nacionalidade"));} catch (SQLException e) {l_f_nacionalidade.setText(null);}
        try {l_f_estcivil.getSelectionModel().select(rs.getString("l_f_estcivil"));} catch (SQLException e) {l_f_estcivil.getSelectionModel().select(0);}

        List<ptelcontatoModel> datal_f_tel = null;
        try {datal_f_tel = new setTels(rs.getString("l_f_tel")).rString();} catch (SQLException e) {}
        if (datal_f_tel != null) l_f_tel.setItems(observableArrayList(datal_f_tel)); else l_f_tel.getItems().clear();
        l_f_tel.setDisable(false);
        try {l_f_tel.getSelectionModel().select(0);} catch (Exception e) {}

        try {l_f_mae.setText(rs.getString("l_f_mae"));} catch (SQLException e) {l_f_mae.setText(null);}
        try {l_f_pai.setText(rs.getString("l_f_pai"));} catch (SQLException e) {l_f_pai.setText(null);}
        try {l_f_empresa.setText(rs.getString("l_f_empresa"));} catch (SQLException e) {l_f_empresa.setText(null);}

        try {l_f_dtadmissao.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("l_f_dtadmissao")));} catch (Exception e) {l_f_dtadmissao.getEditor().clear();}
        try {l_f_dtadmissao.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("l_f_dtadmissao").toString(), "yyyy-MM-dd")));} catch (Exception e) {l_f_dtadmissao.setValue(null);}

        try {l_f_endereco.setText(rs.getString("l_f_endereco"));} catch (SQLException e) {l_f_endereco.setText(null);}
        try {l_f_numero.setText(rs.getString("l_f_numero"));} catch (SQLException e) {l_f_numero.setText(null);}
        try {l_f_cplto.setText(rs.getString("l_f_cplto"));} catch (SQLException e) {l_f_cplto.setText(null);}
        try {l_f_bairro.setText(rs.getString("l_f_bairro"));} catch (SQLException e) {l_f_bairro.setText(null);}
        try {l_f_cidade.setText(rs.getString("l_f_cidade"));} catch (SQLException e) {l_f_cidade.setText(null);}
        try {l_f_estado.setText(rs.getString("l_f_estado"));} catch (SQLException e) {l_f_estado.setText(null);}
        try {l_f_cep.setText(rs.getString("l_f_cep"));} catch (SQLException e) {l_f_cep.setText(null);}
        try {l_f_cargo.setText(rs.getString("l_f_cargo"));} catch (SQLException e) {l_f_cargo.setText(null);}
        try {l_f_salario.setText(rs.getString("l_f_salario").toString());} catch (SQLException e) {l_f_salario.setText("0,00");}
        try {l_f_conjugue.setText(rs.getString("l_f_conjugue"));} catch (SQLException e) {l_f_conjugue.setText(null);}

        try {l_f_conjuguedtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("l_f_conjuguedtnasc")));} catch (Exception e) {l_f_conjuguedtnasc.getEditor().clear();}
        try {l_f_conjuguedtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("l_f_conjuguedtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {l_f_conjuguedtnasc.setValue(null);}

        try {l_f_conjuguesexo.getSelectionModel().select(rs.getString("l_f_conjuguesexo"));} catch (SQLException e) {l_f_conjuguesexo.getSelectionModel().select(0);}
        try {l_f_conjuguerg.setText(rs.getString("l_f_conjuguerg"));} catch (SQLException e) {l_f_conjuguerg.setText(null);}
        try {l_f_conjuguecpf.setText(rs.getString("l_f_conjuguecpf"));} catch (SQLException e) {l_f_conjuguecpf.setText(null);}
        try {l_f_conjuguesalario.setText(rs.getString("l_f_conjuguesalario").toString());} catch (SQLException e) {l_f_conjuguesalario.setText("0,00");}
        try {l_f_conjugueempresa.setText(rs.getString("l_f_conjugueempresa"));} catch (SQLException e) {l_f_conjugueempresa.setText(null);}

        try {
            List<ptelcontatoModel> datal_f_conjuguetelefone = null;
            try {datal_f_conjuguetelefone = new setTels(rs.getString("l_f_conjuguetelefone")).rString();} catch (SQLException e) {}
            if (datal_f_conjuguetelefone != null) l_f_conjuguetelefone.setItems(observableArrayList(datal_f_conjuguetelefone));
            else l_f_conjuguetelefone.getItems().clear();
            l_f_conjuguetelefone.setDisable(false);
            try {l_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
        } catch (Exception e) {}

        try {
            List<pemailModel> datal_f_email = null;
            try {datal_f_email = new setEmails(rs.getString("l_f_email"), false).rString();} catch (SQLException e) {}
            if (datal_f_email != null) l_f_email.setItems(observableArrayList(datal_f_email));
            else l_f_email.getItems().clear();
            l_f_email.setDisable(false);
            try {l_f_email.getSelectionModel().select(0);} catch (Exception e) {}
        } catch (Exception e) {}

        try {l_j_razao.setText(rs.getString("l_j_razao"));} catch (SQLException e) {l_j_razao.setText(null);}
        try {l_j_fantasia.setText(rs.getString("l_j_fantasia"));} catch (SQLException e) {l_j_fantasia.setText(null);}

        try {l_j_endereco.setText(rs.getString("l_j_endereco"));} catch (SQLException e) {l_j_endereco.setText(null);}
        try {l_j_numero.setText(rs.getString("l_j_numero"));} catch (SQLException e) {l_j_numero.setText(null);}
        try {l_j_cplto.setText(rs.getString("l_j_cplto"));} catch (SQLException e) {l_j_cplto.setText(null);}
        try {l_j_bairro.setText(rs.getString("l_j_bairro"));} catch (SQLException e) {l_j_bairro.setText(null);}
        try {l_j_cidade.setText(rs.getString("l_j_cidade"));} catch (SQLException e) {l_j_cidade.setText(null);}
        try {l_j_estado.setText(rs.getString("l_j_estado"));} catch (SQLException e) {l_j_estado.setText(null);}
        try {l_j_cep.setText(rs.getString("l_j_cep"));} catch (SQLException e) {l_j_cep.setText(null);}

        try {l_j_dtctrosocial.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("l_j_dtctrosocial")));} catch (Exception e) {l_j_dtctrosocial.getEditor().clear();}
        try {l_j_dtctrosocial.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("l_j_dtctrosocial").toString(), "yyyy-MM-dd")));} catch (Exception e) {l_j_dtctrosocial.setValue(null);}

        List<ptelcontatoModel> datal_j_tel = null;
        try {datal_j_tel = new setTels(rs.getString("l_j_tel")).rString();} catch (SQLException e) {}
        if (datal_j_tel != null) l_j_tel.setItems(observableArrayList(datal_j_tel)); else l_j_tel.getItems().clear();
        l_j_tel.setDisable(false);
        try {l_j_tel.getSelectionModel().select(0);} catch (Exception e) {}

        try {
            List<pemailModel> datal_j_email = null;
            try {datal_j_email = new setEmails(rs.getString("l_j_email"),false).rString();} catch (SQLException e) {}
            if (datal_j_email != null) l_j_email.setItems(observableArrayList(datal_j_email));
            else l_j_email.getItems().clear();
            l_j_email.setDisable(false);
            try {l_j_email.getSelectionModel().select(0);} catch (Exception e) {}
        } catch (Exception ex) {}

        try {
            int tdepseg = 0;
            tdepseg = rs.getInt("l_depseg");
            if (tdepseg == 1) {
                deposito.setSelected(true);
            } else if (tdepseg == 2) {
                seguradora.setSelected(true);
            }
        } catch (SQLException e) {}
        try {vrdeposito.setText(LerValor.StringValue2Currency(rs.getBigDecimal("l_vrdeposito").toString().replace(".",",")));} catch (SQLException e) {}
        try {nmseguradora.getSelectionModel().select(rs.getInt("l_cdseguradora"));} catch (SQLException e) {}
        try {nroapolice.setText(rs.getString("l_nrapolice"));} catch (SQLException e) {}

        Date tdtapolice = null;
        try {tdtapolice = rs.getDate("l_dtapolice");} catch (SQLException e) {}
        try {if (tdtapolice != null) dtapolice.setValue(Dates.toLocalDate(tdtapolice));} catch (Exception e) {}

        try {nm_contrato.setText(rs.getString("l_nmcontrato"));} catch (SQLException e) {}

        Date tdtinicio = null;
        try {tdtinicio = rs.getDate("l_dtinicioctro");} catch (SQLException e) {}
        try {if (tdtinicio != null) ct_dtinicio.setValue(Dates.toLocalDate(tdtinicio));} catch (Exception e) {}

        Date tdttermino = null;
        try {tdttermino = rs.getDate("l_dtfimctro");} catch (SQLException e) {}
        try {if (tdttermino != null) ct_dtfim.setValue(Dates.toLocalDate(tdttermino));} catch (Exception e) {}

        try {ct_aluguel.setText(LerValor.StringValue2Currency(rs.getBigDecimal("l_vrmensal").toString().replace(".",",")));} catch (SQLException e) {}

        try { LerFiadores(l_contrato.getText()); } catch (NullPointerException e) {}
        try {LerAdicionais(this.pID);} catch (NullPointerException e) {}
        try {
            LerSocios(l_contrato.getText());
        } catch (NullPointerException e) {}
    }

    private void LerSocios(String contrato) {
        l_j_socios.getItems().clear();
        if (contrato == null) return;
        List<psociosModel> data = new ArrayList<psociosModel>();
        ResultSet imv;
        String qSQL = "SELECT s_id, s_rgprp, s_rgimv, s_contrato, s_cpfcnpj, s_nome FROM socios WHERE s_contrato = '%s' ORDER BY s_rgimv;";
        qSQL = String.format(qSQL,contrato);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                Integer qid = -1;
                String qrgprp = null, qrgimv = null, qcontrato = null, qcnpj = null, qnome = null;
                try {qid = imv.getInt("s_id");} catch (SQLException e) {}
                try {qrgprp = imv.getString("s_rgprp");} catch (SQLException e) {}
                try {qrgimv = imv.getString("s_rgimv");} catch (SQLException e) {}
                try {qcontrato = imv.getString("s_contrato");} catch (SQLException e) {}
                try {qcnpj = imv.getString("s_cpfcnpj");} catch (SQLException e) {}
                try {qnome = imv.getString("s_nome");} catch (SQLException e) {}
                data.add(new psociosModel(qid, qrgprp, qrgimv, qcontrato, qcnpj, qnome));
            }
            imv.close();
        } catch (SQLException e) {}

        l_j_socioscpfcnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        l_j_sociosnome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        l_j_socios.setItems(FXCollections.observableArrayList(data));

    }

    private void LerFiadores(String contrato) {
        l_fiadores.getItems().clear();
        if (contrato == null) return;
        List<pfiadoresModel> data = new ArrayList<pfiadoresModel>();
        ResultSet imv;
        String qSQL = "SELECT f_id, f_rgprp, f_rgimv, f_contrato, f_cpfcnpj, CASE WHEN f_fisjur = TRUE THEN f_f_nome ELSE f_j_razao END AS f_nome FROM fiadores WHERE f_contrato = '%s' ORDER BY f_rgimv;";
        qSQL = String.format(qSQL,contrato);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                Integer qid = -1;
                String qrgprp = null, qrgimv = null, qcontrato = null, qcnpj = null, qnome = null;
                try {qid = imv.getInt("f_id");} catch (SQLException e) {}
                try {qrgprp = imv.getString("f_rgprp");} catch (SQLException e) {}
                try {qrgimv = imv.getString("f_rgimv");} catch (SQLException e) {}
                try {qcontrato = imv.getString("f_contrato");} catch (SQLException e) {}
                try {qcnpj = imv.getString("f_cpfcnpj");} catch (SQLException e) {}
                try {qnome = imv.getString("f_nome");} catch (SQLException e) {}
                data.add(new pfiadoresModel(qid, qrgprp, qrgimv, qcontrato, qcnpj, qnome));
            }
            imv.close();
        } catch (SQLException e) {}

        l_fiadorescpfcnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        l_fiadoresnome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        l_fiadores.setItems(FXCollections.observableArrayList(data));

    }

    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO prelocatarios(l_rgprp, l_rgimv, l_contrato, l_tipoimovel, l_fisjur, l_cpfcnpj, " +
                    "            l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade, " +
                    "            l_f_estcivil, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao, " +
                    "            l_f_endereco, l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade, " +
                    "            l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue, l_f_conjuguedtnasc, " +
                    "            l_f_conjuguesexo, l_f_conjuguerg, l_f_conjuguecpf, l_f_conjuguesalario, " +
                    "            l_f_conjugueempresa, l_f_conjuguetelefone, l_f_email, l_j_razao, " +
                    "            l_j_fantasia, l_j_endereco, l_j_numero, l_j_cplto, l_j_bairro, " +
                    "            l_j_cidade, l_j_estado, l_j_cep, l_j_dtctrosocial, l_j_tel, l_j_email, " +
                    "            l_depseg, l_vrdeposito, l_cdseguradora, l_nrapolice, l_dtapolice, " +
                    "            l_nmcontrato, l_dtinicioctro, l_dtfimctro, l_vrmensal)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?);";
        } else {
            sql = "UPDATE prelocatarios" +
                    "   SET l_rgprp=?, l_rgimv=?, l_contrato=?, l_tipoimovel=?, l_fisjur=?, " +
                    "       l_cpfcnpj=?, l_rginsc=?, l_f_nome=?, l_f_sexo=?, l_f_dtnasc=?, " +
                    "       l_f_nacionalidade=?, l_f_estcivil=?, l_f_tel=?, l_f_mae=?, l_f_pai=?, " +
                    "       l_f_empresa=?, l_f_dtadmissao=?, l_f_endereco=?, l_f_numero=?, " +
                    "       l_f_cplto=?, l_f_bairro=?, l_f_cidade=?, l_f_estado=?, l_f_cep=?, " +
                    "       l_f_cargo=?, l_f_salario=?, l_f_conjugue=?, l_f_conjuguedtnasc=?, " +
                    "       l_f_conjuguesexo=?, l_f_conjuguerg=?, l_f_conjuguecpf=?, l_f_conjuguesalario=?, " +
                    "       l_f_conjugueempresa=?, l_f_conjuguetelefone=?, l_f_email=?, l_j_razao=?, " +
                    "       l_j_fantasia=?, l_j_endereco=?, l_j_numero=?, l_j_cplto=?, l_j_bairro=?, " +
                    "       l_j_cidade=?, l_j_estado=?, l_j_cep=?, l_j_dtctrosocial=?, l_j_tel=?, " +
                    "       l_j_email=?,  l_depseg=?, l_vrdeposito=?, l_cdseguradora=?, l_nrapolice=?, " +
                    "       l_dtapolice=?, l_nmcontrato=?, l_dtinicioctro=?, l_dtfimctro=?, l_vrmensal=? " +
                    "       WHERE l_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1;
            //if (!bNew) pstmt.setInt(nid++, Id);
            pstmt.setString(nid++, l_rgprp.getText());
            pstmt.setString(nid++, l_rgimv.getText());
            pstmt.setString(nid++, (bNew ? String.valueOf(Id) : l_contrato.getText()));
            pstmt.setString(nid++, l_tpimovel.getText());
            pstmt.setBoolean(nid++, l_fisica.isSelected() ? true : false);
            pstmt.setString(nid++, l_cpfcnpj.getText());
            pstmt.setString(nid++, l_rginsc.getText());
            pstmt.setString(nid++, l_f_nome.getText());
            pstmt.setString(nid++, l_f_sexo.getSelectionModel().getSelectedItem().toString());
            pstmt.setDate(nid++, Dates.toSqlDate(l_f_dtnasc));
            pstmt.setString(nid++, l_f_nacionalidade.getText());
            pstmt.setString(nid++, l_f_estcivil.getSelectionModel().getSelectedItem().toString());
            pstmt.setString(nid++, new getTels(l_f_tel).toString());
            pstmt.setString(nid++, l_f_mae.getText());
            pstmt.setString(nid++, l_f_pai.getText());
            pstmt.setString(nid++, l_f_empresa.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(l_f_dtadmissao));
            pstmt.setString(nid++, l_f_endereco.getText());
            pstmt.setString(nid++, l_f_numero.getText());
            pstmt.setString(nid++, l_f_cplto.getText());

            pstmt.setString(nid++, l_f_bairro.getText());
            pstmt.setString(nid++, l_f_cidade.getText());
            pstmt.setString(nid++, l_f_estado.getText());
            pstmt.setString(nid++, l_f_cep.getText());
            pstmt.setString(nid++, l_f_cargo.getText());
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(l_f_salario.getText())));
            pstmt.setString(nid++, l_f_conjugue.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(l_f_conjuguedtnasc));
            pstmt.setString(nid++, l_f_conjuguesexo.getSelectionModel().getSelectedItem().toString());

            pstmt.setString(nid++, l_f_conjuguerg.getText());
            pstmt.setString(nid++, l_f_conjuguecpf.getText());
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(l_f_conjuguesalario.getText())));
            pstmt.setString(nid++, l_f_conjugueempresa.getText());
            pstmt.setString(nid++, new getTels(l_f_conjuguetelefone).toString());
            pstmt.setString(nid++, new getEmails(l_f_email,false).toString());
            pstmt.setString(nid++, l_j_razao.getText());
            pstmt.setString(nid++, l_j_fantasia.getText());
            pstmt.setString(nid++, l_j_endereco.getText());
            pstmt.setString(nid++, l_j_numero.getText());
            pstmt.setString(nid++, l_j_cplto.getText());
            pstmt.setString(nid++, l_j_bairro.getText());
            pstmt.setString(nid++, l_j_cidade.getText());
            pstmt.setString(nid++, l_j_estado.getText());
            pstmt.setString(nid++, l_j_cep.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(l_j_dtctrosocial));
            pstmt.setString(nid++, new getTels(l_j_tel).toString());
            pstmt.setString(nid++, new getEmails(l_j_email,false).toString());

            int tdepseg = 0;
            if (deposito.isSelected()) {
                tdepseg = 1;
            } else if (seguradora.isSelected()) {
                tdepseg = 2;
            } else tdepseg = 0;
            pstmt.setInt(nid++, tdepseg);
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(vrdeposito.getText())));
            pstmt.setInt(nid++, nmseguradora.getSelectionModel().getSelectedIndex());

            String tnrapolice = null;
            try {tnrapolice = nroapolice.getText().trim();} catch (NullPointerException e) {tnrapolice = "";}
            pstmt.setString(nid++, tnrapolice);
            
            pstmt.setDate(nid++, Dates.toSqlDate(dtapolice));
            pstmt.setString(nid++, bNew ? String.valueOf(Id) : l_contrato.getText().trim());
            pstmt.setDate(nid++, Dates.toSqlDate(ct_dtinicio));
            pstmt.setDate(nid++, Dates.toSqlDate(ct_dtfim));
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(ct_aluguel.getText())));

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        SamicServer ss = new SamicServerImplService().getSamicServerImplPort();
        Consulta dados = new Consulta();
        {
            dados.setCliente(VariaveisGlobais.cliente);
            dados.setEstacao(VariaveisGlobais.estacao);
            dados.setTipo("L");
            dados.setCpfcnpj(l_cpfcnpj.getText());
            dados.setDatacadastro(Dates.DateFormata("yyyy/MM/dd", DbMain.getDateTimeServer()));
            dados.setNomerazao(l_fisica.isSelected() ? l_f_nome.getText() : l_j_razao.getText());
            dados.setRginsc(l_rginsc.getText());
            dados.setEndereco(l_fisica.isSelected() ? l_f_endereco.getText() : l_j_endereco.getText());
            dados.setNumero(l_fisica.isSelected() ? l_f_numero.getText() : l_j_endereco.getText());
            dados.setComplemento(l_fisica.isSelected() ? l_f_cplto.getText() : l_j_cplto.getText());
            dados.setBairro(l_fisica.isSelected() ? l_f_bairro.getText() : l_j_bairro.getText());
            dados.setCidade(l_fisica.isSelected() ? l_f_cidade.getText() : l_j_cidade.getText());
            dados.setEstado(l_fisica.isSelected() ? l_f_estado.getText() : l_j_estado.getText());
            dados.setCep(l_fisica.isSelected() ? l_f_cep.getText() : l_j_cep.getText());
            dados.setTelefones(new getTels(l_fisica.isSelected() ? l_f_tel : l_j_tel).toString());
            dados.setEmails(new getEmails(l_fisica.isSelected() ? l_f_email : l_j_email,false).toString());

            dados.setPositivo(false);
            dados.setObservacoes("");
        }
        boolean iOk = ss.inclusao(dados, (bNew ? "I" : "A"));

        bNew = false;

        return retorno;
    }

    private void verifyBotoes() {
        if (this.viea == null) return;

        if (!this.viea.contains("I")) l_btIncluir.setDisable(true);
        if (!this.viea.contains("E")) l_btExcluir.setDisable(true);
        if (!this.viea.contains("A")) l_btGravar.setDisable(true);
    }

/*
    private void ChamaTela2(String nome, String url, String icone) throws IOException, Exception {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource(url));
            MDIWindow mDIWindow = new MDIWindow(nome, new ImageView(icone != "" ? "/Figuras/" + icone : null), nome, root,true);

            MDICanvas canvas = ((MDICanvas) l_anchorPane.getParent().getParent().getParent());
            canvas.addMDIWindow(mDIWindow);

            root.fireEvent(new vieaEvent("IEA", vieaEvent.GET_VIEA));

            if (nome.toUpperCase().trim().equals("FIADORES")) {
                pfiadoresModel pfiadores = l_fiadores.getSelectionModel().getSelectedItem();
                if (pfiadores != null) {
                    root.fireEvent(new paramEvent(new Object[]{pfiadores.getId(), pfiadores.getRgprp(), pfiadores.getRgimv(), pfiadores.getContrato(), pfiadores.getCnpj()}, paramEvent.GET_PARAM));
                } else {
                    root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
                }
            } else {
                psociosModel psocios = l_j_socios.getSelectionModel().getSelectedItem();
                if (psocios != null) {
                    root.fireEvent(new paramEvent(new Object[]{psocios.getId(), psocios.getRgprp(), psocios.getRgimv(), psocios.getContrato(), psocios.getCnpj()}, paramEvent.GET_PARAM));
                } else {
                    root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
                }
            }
            mDIWindow.setCenter(root);
        } catch (Exception e) {e.printStackTrace();}
    }
*/

    private void ChamaTela(String nome, String url, String icone) throws IOException, Exception {
        AnchorPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(url));
        } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);


        //internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.fireEvent(new vieaEvent("VIEA", vieaEvent.GET_VIEA));

        if (nome.toUpperCase().trim().equals("FIADORES")) {
            pfiadoresModel pfiadores = l_fiadores.getSelectionModel().getSelectedItem();
            if (!bInc) {
                if (pfiadores != null) {
                    root.fireEvent(new paramEvent(new Object[]{pfiadores.getId(), pfiadores.getRgprp(), pfiadores.getRgimv(), pfiadores.getContrato(), pfiadores.getCnpj()}, paramEvent.GET_PARAM));
                } else {
                    root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
                }
            }
        } else if (nome.toUpperCase().trim().equals("SOCIOS")) {
            if (!bInc) {
                psociosModel psocios = l_j_socios.getSelectionModel().getSelectedItem();
                if (psocios != null) {
                    root.fireEvent(new paramEvent(new Object[]{psocios.getId(), psocios.getRgprp(), psocios.getRgimv(), psocios.getContrato(), psocios.getCnpj()}, paramEvent.GET_PARAM));
                } else {
                    root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
                }
            }
        } else if (nome.toUpperCase().trim().equals("ADCLOCATARIOS")) {
            if(!bInc) {
                int pID = -1;
                if (l_adicionais.getSelectionModel().getSelectedItem() != null) pID = l_adicionais.getSelectionModel().getSelectedItem().getId();
                if (pID == -1) {
                    root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), this.pID}, paramEvent.GET_PARAM));
                } else {
                    root.fireEvent(new paramEvent(new Object[]{pID, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), this.pID}, paramEvent.GET_PARAM));
                }
            }
        }

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam[0].toString().equalsIgnoreCase("fiador")) {
                try {internalFrame.close();} catch (NullPointerException e) {}
                LerFiadores(l_contrato.getText());
                return;
            }
            if (event.sparam[0].toString().equalsIgnoreCase("socio")) {
                try {internalFrame.close();} catch (NullPointerException e) {}
                LerSocios(l_contrato.getText());
                return;
            }
            if (event.sparam[0].toString().equalsIgnoreCase("adclocatarios")) {
                try {internalFrame.close();} catch (NullPointerException e) {}
                LerAdicionais(this.pID);
                return;
            }

            Consulta consulta = (Consulta)event.sparam[0];
            try {internalFrame.close();} catch (NullPointerException e) {}
            if (consulta != null) {
                if (FuncoesGlobais.LimpaCpfCnpj(consulta.getCpfcnpj()).length() == 11) {
                    // Física
                    l_f_nome.setText(consulta.getNomerazao());
                    l_f_nome.setDisable(true);
                    l_cpfcnpj.setText(consulta.getCpfcnpj());
                    l_cpfcnpj.setDisable(true);
                    l_f_endereco.setText(consulta.getEndereco());
                    l_f_numero.setText(consulta.getNumero());
                    l_f_cplto.setText(consulta.getComplemento());
                    l_f_bairro.setText(consulta.getBairro());
                    l_f_cidade.setText(consulta.getCidade());
                    l_f_estado.setText(consulta.getEstado());
                    l_f_cep.setText(consulta.getCep());
                    l_rginsc.setText(consulta.getRginsc());

                    // Telefones
                    List<ptelcontatoModel> data = new setTels(consulta.getTelefones()).rString();
                    l_f_tel.getItems().clear();
                    if (data != null) l_f_tel.setItems(observableArrayList(data));
                    else l_f_tel.getItems().clear();
                    l_f_tel.setDisable(false);
                    try {
                        l_f_tel.getSelectionModel().select(0);
                        l_f_tel.getEditor().setText(data.get(0).toString());
                    } catch (Exception e) {
                    }

                    // Emails
                    List<pemailModel> dataemail = null;
                    String emailp = consulta.getEmails();
                    if (emailp != null) {
                        dataemail = new setEmails(emailp, true).rString();
                    }
                    l_f_email.getItems().clear();
                    if (dataemail != null) l_f_email.setItems(observableArrayList(dataemail));
                    else l_f_email.getItems().clear();
                    l_f_email.setDisable(false);
                    try {
                        l_f_email.getSelectionModel().select(0);
                    } catch (Exception e) {
                    }
                }
            } else {
                // Jurídica

            }

            try {LerFiadores(l_contrato.getText());} catch (NullPointerException e) {}
            try {LerAdicionais(this.pID);} catch (NullPointerException e) {}
            try {LerSocios(l_contrato.getText());} catch (NullPointerException e) {}
        });
    }

    private void LerAdicionais(int id) {
        l_adicionais.getItems().clear();
        if (id == -1) return;
        List<LocAdicionais> data = new ArrayList<LocAdicionais>();
        ResultSet imv;
        String qSQL = "SELECT l_id, l_cpfcnpj, l_f_nome AS l_nome FROM adclocatarios WHERE l_idloca = %s ORDER BY l_id;";
        qSQL = String.format(qSQL,id);
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                Integer qid = -1;
                String qcnpj = null, qnome = null;
                try {qid = imv.getInt("l_id");} catch (SQLException e) {}
                try {qcnpj = imv.getString("l_cpfcnpj");} catch (SQLException e) {}
                try {qnome = imv.getString("l_nome");} catch (SQLException e) {}
                data.add(new LocAdicionais(qid, qcnpj, qnome));
            }
            imv.close();
        } catch (SQLException e) {}

        l_adccpfcnpj.setCellValueFactory(new PropertyValueFactory<>("cpfcnpj"));
        l_adcnome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        l_adicionais.setItems(FXCollections.observableArrayList(data));

    }
}