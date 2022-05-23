package Locatarios.Fiadores;

import Classes.*;
import Funcoes.*;
import Locatarios.Socios.psociosModel;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import masktextfield.MaskTextField;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;
import samic.serversamic.Consulta;

/**
 * Created by supervisor on 07/03/16.
 */
public class FiadoresController implements Initializable {

    DbMain conn = VariaveisGlobais.conexao;
    String fSql = "SELECT * FROM fiadores ORDER BY f_contrato;";
    ResultSet rs = null;
    String viea = null;
    String f_rgprp = "", f_rgimv = "", f_contrato = "", f_documento = "";
    Integer f_id = -1;

    boolean bInc = false;
    int pID = -1;

    Object[] fields;
    Object[] buttons;

    @FXML private AnchorPane f_anchorPane;
    @FXML private Label f_lblFisJur;
    @FXML private Label f_lblRgInsc;
    @FXML private RadioButton f_fisica;
    @FXML private RadioButton f_juridica;
    @FXML private TextField f_cpf;
    //@FXML private MaskTextField f_cnpj;
    @FXML private TextField f_rginsc;
    @FXML private TabPane f_tabfisjur;
    @FXML private Tab f_tabFisica;
    @FXML private Tab f_tabJuridica;
    @FXML private TextField f_f_nome;
    @FXML private ComboBox<String> f_f_sexo;
    @FXML private DatePicker f_f_dtnasc;
    @FXML private TextField f_f_nacionalidade;
    @FXML private ComboBox<String> f_f_estcivil;

    @FXML private MenuButton f_f_tel_menu;
    @FXML private MenuItem f_f_teladc;
    @FXML private MenuItem f_f_teldel;
    @FXML private ComboBox<ptelcontatoModel> f_f_tel;

    @FXML private TextField f_f_mae;
    @FXML private TextField f_f_pai;
    @FXML private TextField f_f_empresa;
    @FXML private DatePicker f_f_dtadmissao;

    @FXML private TextField f_f_endereco_fiador;
    @FXML private TextField f_f_numero_fiador;
    @FXML private TextField f_f_cplto_fiador;
    @FXML private Button f_f_btFindEndereco_fiador;
    @FXML private TextField f_f_bairro_fiador;
    @FXML private TextField f_f_cidade_fiador;
    @FXML private TextField f_f_estado_fiador;
    @FXML private MaskTextField f_f_cep_fiador;

    @FXML private TextField f_f_endereco;
    @FXML private TextField f_f_numero;
    @FXML private TextField f_f_cplto;
    @FXML private Button f_f_btFindEndereco;
    @FXML private TextField f_f_bairro;
    @FXML private TextField f_f_cidade;
    @FXML private TextField f_f_estado;
    @FXML private MaskTextField f_f_cep;
    @FXML private TextField f_f_cargo;
    @FXML private TextField f_f_salario;
    @FXML private TextField f_f_conjugue;
    @FXML private DatePicker f_f_conjuguedtnasc;
    @FXML private ComboBox<String> f_f_conjuguesexo;
    @FXML private TextField f_f_conjuguerg;
    @FXML private TextField f_f_conjuguecpf;
    @FXML private TextField f_f_conjuguesalario;
    @FXML private TextField f_f_conjugueempresa;

    @FXML private MenuButton f_f_conjuguetelefone_menu;
    @FXML private MenuItem f_f_conjugueteladc;
    @FXML private MenuItem f_f_conjugueteldel;
    @FXML private ComboBox<ptelcontatoModel> f_f_conjuguetelefone;

    @FXML private ComboBox<pemailModel> f_f_email;
    @FXML private MenuButton f_f_email_menu;
    @FXML private MenuItem f_f_emailadc;
    @FXML private MenuItem f_f_emaildel;

    @FXML private TextField f_j_razao;
    @FXML private TextField f_j_fantasia;

    @FXML private TextField f_j_endereco;
    @FXML private Button f_j_btFindEndereco;

    @FXML private TextField f_j_numero;
    @FXML private TextField f_j_cplto;
    @FXML private TextField f_j_bairro;
    @FXML private TextField f_j_cidade;
    @FXML private TextField f_j_estado;
    @FXML private MaskTextField f_j_cep;
    @FXML private DatePicker f_j_dtctrosocial;

    @FXML private MenuButton f_j_tel_menu;
    @FXML private MenuItem f_j_teladc;
    @FXML private MenuItem f_j_teldel;
    @FXML private ComboBox<ptelcontatoModel> f_j_tel;

    @FXML private MenuButton f_j_email_menu;
    @FXML private MenuItem f_j_emailadc;
    @FXML private MenuItem f_j_emaildel;
    @FXML private ComboBox<pemailModel> f_j_email;

    @FXML private TableView<psociosModel> f_j_socios;
    @FXML private TableColumn f_j_socioscpfcnpj;
    @FXML private TableColumn f_j_sociosnome;

    @FXML private TableView<?> f_fiadores;
    @FXML private TableColumn<?, ?> f_fiadorescpfcnpj;
    @FXML private TableColumn<?, ?> f_fiadoresnome;

    @FXML private Button f_btIncluir;
    @FXML private Button f_btAlterar;
    @FXML private Button f_btExcluir;
    @FXML private Button f_btGravar;
    @FXML private Button f_btRetornar;

    @FXML void f_btExcluir_OnAction(ActionEvent event) {
        // para o Final
    }

    @FXML void f_btGravar(ActionEvent event) {
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            if (bInc) {
                salvar(bInc, -1);
            } else {
                salvar(bInc, this.pID);
                String tpID = String.valueOf(this.pID);
                try {rs.close();} catch (SQLException ex) {}
                OpenFiadores(true);
                try {MoveTo("f_id",tpID);} catch (SQLException ex1) {}
            }

            verifyBotoes();
            bInc = false;

            // Atualiza
            try {
                int pos = rs.getRow();
                DbMain.FecharTabela(rs);

                rs = conn.AbrirTabela(fSql, ResultSet.CONCUR_UPDATABLE);
                rs.absolute(pos);
                if (DbMain.RecordCount(rs) <= 0) {
                    new Controle("FIADOR: " + f_cpf.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {f_btIncluir, f_btRetornar});
                } else {
                    new Controle("FIADOR: " + f_cpf.getText() + " - ", this.buttons).BotaoDisabled(new Object[] {f_btGravar});
                }
            } catch (SQLException e) {}

            new Controle("FIADOR: " + f_cpf.getText() + " - ", this.fields).FieldsEnabled(false);
        }
    }

    @FXML void f_btIncluir_OnAction(ActionEvent event) {
        this.bInc = true;
        LimpaTela();

        new Controle("FIADOR: " + f_cpf.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {f_btGravar, f_btRetornar});
        new Controle("FIADOR: " + f_cpf.getText() + " - ", this.fields).FieldsEnabled(true);

        try {
            ChamaTela("Pesquisa", "/Pesquisa/Pesquisa.fxml", "loca.png");
        } catch (Exception wex) {wex.printStackTrace();}

        f_cpf.requestFocus();
    }

    @FXML void f_btAlterar_OnAction(ActionEvent event) {
        this.bInc = false;

        new Controle("FIADOR: " + f_cpf.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {f_btGravar, f_btRetornar});
        new Controle("FIADOR: " + f_cpf.getText() + " - ", this.fields).FieldsEnabled(true);
        f_cpf.requestFocus();
    }

    @FXML void f_btRetornar_OnAction(ActionEvent event) {
        if (bInc) {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) return;
        }

        if (!f_btGravar.isDisabled()) {
            LerLoca();
            bInc = false;
            new Controle("FIADOR: " + f_cpf.getText() + " - ", this.buttons).BotaoDisabled(new Object[]{f_btGravar});
            new Controle("FIADOR: " + f_cpf.getText() + " - ", this.fields).FieldsEnabled(false);
            return;
        }

        try {f_anchorPane.fireEvent(new paramEvent(new Object[] {"Fiador"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        //((FXInternalWindow)f_anchorPane.getParent().getParent().getParent()).close();
    }

    @FXML void f_f_conjugueteladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = f_f_conjuguetelefone.getItems();
            tels.addAll(b);
            f_f_conjuguetelefone.setItems(tels);
            try {f_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void f_f_conjugueteldef_OnAction(ActionEvent event) {
        if (!f_f_conjuguetelefone.getItems().isEmpty()) f_f_conjuguetelefone.getItems().removeAll(f_f_conjuguetelefone.getSelectionModel().getSelectedItem());
        try {f_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void f_f_emailadc_OnAction(ActionEvent event) {
        adcEmails dialog = new adcEmails();
        Optional<pemailModel> result = dialog.adcEmails(false);
        result.ifPresent(b -> {
            ObservableList<pemailModel> emails = f_f_email.getItems();
            emails.addAll(b);
            f_f_email.setItems(emails);
            try {f_f_email.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void f_f_emaildef_OnAction(ActionEvent event) {
        if (!f_f_email.getItems().isEmpty()) f_f_email.getItems().removeAll(f_f_email.getSelectionModel().getSelectedItem());
        try {f_f_email.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void f_f_teladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = f_f_tel.getItems();
            tels.addAll(b);
            f_f_tel.setItems(tels);
            try {f_f_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void f_f_teldef_OnAction(ActionEvent event) {
        if (!f_f_tel.getItems().isEmpty()) f_f_tel.getItems().removeAll(f_f_tel.getSelectionModel().getSelectedItem());
        try {f_f_tel.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void f_j_emailadc_OnAction(ActionEvent event) {
        adcEmails dialog = new adcEmails();
        Optional<pemailModel> result = dialog.adcEmails(false);
        result.ifPresent(b -> {
            ObservableList<pemailModel> emails = f_j_email.getItems();
            emails.addAll(b);
            f_j_email.setItems(emails);
            try {f_j_email.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void f_j_emaildef_OnAction(ActionEvent event) {
        if (!f_j_email.getItems().isEmpty()) f_j_email.getItems().removeAll(f_j_email.getSelectionModel().getSelectedItem());
        try {f_j_email.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void f_j_teladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = f_j_tel.getItems();
            tels.addAll(b);
            f_j_tel.setItems(tels);
            try {f_j_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void f_j_teldef_OnAction(ActionEvent event) {
        if (!f_j_tel.getItems().isEmpty()) f_j_tel.getItems().removeAll(f_j_tel.getSelectionModel().getSelectedItem());
        try {f_j_tel.getSelectionModel().select(0);} catch (Exception e) {}
    }

    private int NextField(String field, TextField[] fields) {
        int pos = 0;
        for (pos = 0; pos < fields.length; pos++) {
            if (fields[pos].getId().equalsIgnoreCase(field)) break;
        }
        return pos + 1;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        f_anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
            }
        });
        f_anchorPane.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });
        f_anchorPane.addEventHandler(paramEvent.GET_PARAM, event1 -> {
            Object[] param = event1.sparam;
            if (!param[0].equals("Fiador")) {
                f_id = (Integer) param[0];
                f_rgprp = (String) param[1];
                f_rgimv = (String) param[2];
                f_contrato = (String) param[3];
                f_documento = (String) param[4];
            }
        });
        Platform.runLater(() -> { verifyBotoes(); });

        f_cpf.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
                f_cpf.setText(FuncoesGlobais.LimpaCpfCnpj(f_cpf.getText()));
            } else {
                // out focus
                if (f_cpf.getText().trim().length() <= 11) {
                    if (!MaskFieldUtil.isCpf(f_cpf.getText())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CPF inválido");
                        alert.setContentText("Entre com um CPF válido!!!");

                        alert.showAndWait();
                        f_cpf.setText(null); f_cpf.requestFocus();
                        return;
                    } else {
                        f_cpf.setText(FuncoesGlobais.FormatCpfCnpj(f_cpf.getText()));

                        f_fisica.setSelected(true);
                        f_cpf.setVisible(true);
                        f_lblFisJur.setText("CPF:");
                        f_lblRgInsc.setText("RG:");
                        f_tabfisjur.getSelectionModel().select(0);
                        f_rginsc.requestFocus();
                    }
                } else {
                    if (!MaskFieldUtil.isCnpj(f_cpf.getText())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CNPJ inválido");
                        alert.setContentText("Entre com um CNPJ válido!!!");

                        alert.showAndWait();
                        f_cpf.setText(null); f_cpf.requestFocus();
                        return;
                    } else {
                        f_cpf.setText(FuncoesGlobais.FormatCpfCnpj(f_cpf.getText()));

                        f_juridica.setSelected(true);
                        f_cpf.setVisible(true);
                        f_lblFisJur.setText("CNPJ:");
                        f_lblRgInsc.setText("Insc:");
                        f_tabfisjur.getSelectionModel().select(1);
                        f_rginsc.requestFocus();
                    }
                }

                if (bInc) {
                    cFiadores _fiador = null;
                    // Checa se fiador já existe cadastrado em Fiadores
                    _fiador = new Verificacoes().VerificaFiadorEmFiadores(f_cpf.getText());
                    if (_fiador == null) {
                        // Checa se fiador já existe cadastrado em Proprietario
                        _fiador = new Verificacoes().VerificaFiadorEmProprietarios(f_cpf.getText());
                        if (_fiador == null) {
                            // Checa se fiador já existe cadastrado no locatarios
                            _fiador = new Verificacoes().VerificaFiadorEmLocatarios(f_cpf.getText());
                        }
                    }

                    if (_fiador != null) {
                        Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Fiador já cadastrado no sistema!\n\nDeseja aproveitar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
                        Optional<ButtonType> result = msg.showAndWait();
                        if (result.get().getText().equals("Não")) return;
                        LerDadosFiador(_fiador);
                    }
                }
            }
        });

        f_rginsc.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                if (FuncoesGlobais.LimpaCpfCnpj(f_cpf.getText()).trim().length() <= 11) {
                    // CPF
                    f_f_nome.requestFocus();
                } else {
                    // CNPJ
                    f_j_razao.requestFocus();
                }
            }
        });

/*
        f_cpf.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
            } else {
                // out focus
                if (f_cpf.getText() != null) {
                    if (!MaskFieldUtil.isCpf(FuncoesGlobais.LimpaCpfCnpj(f_cpf.getText()))) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CPF inválido");
                        alert.setContentText("Entre com um CPF válido!!!");

                        alert.showAndWait();
                        f_cpf.setText(null);
                        f_cpf.requestFocus();
                        return;
                    }
                    if (bInc) {
                        cFiadores _fiador = null;
                        // Checa se fiador já existe cadastrado em Fiadores
                        _fiador = new Verificacoes().VerificaFiadorEmFiadores(f_cpf.getText());
                        if (_fiador == null) {
                            // Checa se fiador já existe cadastrado em Proprietario
                            _fiador = new Verificacoes().VerificaFiadorEmProprietarios(f_cpf.getText());
                            if (_fiador == null) {
                                // ITODO - Checa se fiador já existe cadastrado no locatarios
                                _fiador = new Verificacoes().VerificaFiadorEmLocatarios(f_cnpj.getText());
                            }
                        }

                        if (_fiador != null) {
                            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Fiador já cadastrado no sistema!\n\nDeseja aproveitar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
                            Optional<ButtonType> result = msg.showAndWait();
                            if (result.get().getText().equals("Não")) return;
                            LerDadosFiador(_fiador);
                        }
                    }
                }
            }
        });
*/

/*
        f_cnpj.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
            } else {
                // out focus
                if (f_cnpj.getText() != null) {
                    if (!MaskFieldUtil.isCnpj(f_cnpj.getText())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CNPJ inválido");
                        alert.setContentText("Entre com um CNPJ válido!!!");

                        alert.showAndWait();
                        f_cnpj.setText(null);
                        f_cnpj.requestFocus();
                        return;
                    }
                    if (bInc) {
                        cFiadores _fiador = null;
                        // Checa dados no webservice samic

                        // Checa se fiador já existe cadastrado em Fiadores
                        _fiador = new Verificacoes().VerificaFiadorEmFiadores(f_cnpj.getText());
                        if (_fiador == null) {
                            // Checa se fiador já existe cadastrado em Proprietario
                            _fiador = new Verificacoes().VerificaFiadorEmProprietarios(f_cnpj.getText());
                            if (_fiador == null) {
                                // ITODO - Checa se fiador já existe cadastrado no locatario
                                _fiador = new Verificacoes().VerificaFiadorEmLocatarios(f_cnpj.getText());
                            }
                        }

                        if (_fiador != null) {
                            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Fiador já cadastrado no sistema!\n\nDeseja aproveitar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
                            Optional<ButtonType> result = msg.showAndWait();
                            if (result.get().getText().equals("Não")) return;
                            LerDadosFiador(_fiador);
                        }
                    }
                }
            }
        });
*/

        new cpoTelefones().cpoTelefones(f_f_tel);
        new cpoTelefones().cpoTelefones(f_f_conjuguetelefone);
        new cpoEmails().cpoEmails(f_f_email);

        new cpoTelefones().cpoTelefones(f_j_tel);
        new cpoEmails().cpoEmails(f_j_email);

        f_f_btFindEndereco_fiador.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                f_f_endereco_fiador.setText(b.getLogradouro());
                f_f_bairro_fiador.setText(b.getBairro());
                f_f_cidade_fiador.setText(b.getLocalidade());
                f_f_estado_fiador.setText(b.getUf());
                f_f_cep_fiador.setText(b.getCep());
                f_f_numero_fiador.setText(null); f_f_cplto_fiador.setText(null);
                f_f_numero_fiador.requestFocus();
            });
        });

        f_f_cep_fiador.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if (f_f_endereco_fiador.getText() == null && f_f_cep_fiador.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(f_f_cep_fiador.getText());

                            f_f_endereco_fiador.setText(endereco.getLogradouro());
                            f_f_bairro_fiador.setText(endereco.getBairro());
                            f_f_cidade_fiador.setText(endereco.getLocalidade());
                            f_f_estado_fiador.setText(endereco.getUf());

                            f_f_numero_fiador.setText(null); f_f_cplto_fiador.setText(null);
                            f_f_numero_fiador.requestFocus();
                        } catch (IOException ex) {}
                    }
                }
            }
        });

        f_f_btFindEndereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                f_f_endereco.setText(b.getLogradouro());
                f_f_bairro.setText(b.getBairro());
                f_f_cidade.setText(b.getLocalidade());
                f_f_estado.setText(b.getUf());
                f_f_cep.setText(b.getCep());
                f_f_numero.setText(null); f_f_cplto.setText(null);
                f_f_numero.requestFocus();
            });
        });

        f_f_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if (f_f_endereco.getText() == null && f_f_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(f_f_cep.getText());

                            f_f_endereco.setText(endereco.getLogradouro());
                            f_f_bairro.setText(endereco.getBairro());
                            f_f_cidade.setText(endereco.getLocalidade());
                            f_f_estado.setText(endereco.getUf());

                            f_f_numero.setText(null); f_f_cplto.setText(null);
                            f_f_numero.requestFocus();
                        } catch (IOException ex) {}
                    }
                }
            }
        });

        f_j_btFindEndereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                f_j_endereco.setText(b.getLogradouro());
                f_j_bairro.setText(b.getBairro());
                f_j_cidade.setText(b.getLocalidade());
                f_j_estado.setText(b.getUf());
                f_j_cep.setText(b.getCep());
                f_j_numero.setText(null); f_f_cplto.setText(null);
                f_j_numero.requestFocus();
            });
        });

        f_j_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if (f_j_endereco.getText() == null  && f_j_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(f_j_cep.getText());

                            f_j_endereco.setText(endereco.getLogradouro());
                            f_j_bairro.setText(endereco.getBairro());
                            f_j_cidade.setText(endereco.getLocalidade());
                            f_j_estado.setText(endereco.getUf());

                            f_j_numero.setText(null); f_j_cplto.setText(null);
                            f_j_numero.requestFocus();
                        } catch (IOException ex) {}
                    }
                }
            }
        });

        f_fisica.setOnAction(event -> {
            f_cpf.setVisible(true);
            //f_cnpj.setVisible(false);
            f_lblFisJur.setText("CPF:");
            f_lblRgInsc.setText("RG:");
            f_tabfisjur.getSelectionModel().select(0);
            f_cpf.requestFocus();
        });
        f_juridica.setOnAction(event -> {
            f_cpf.setVisible(true);
            //f_cnpj.setVisible(false);
            f_lblFisJur.setText("CNPJ:");
            f_lblRgInsc.setText("Insc:");
            f_tabfisjur.getSelectionModel().select(1);

            f_cpf.requestFocus();
            //f_cnpj.requestFocus();
        });

        f_cpf.setVisible(true);
        //f_cnpj.setVisible(false);
        f_lblFisJur.setText("CPF:");
        f_lblRgInsc.setText("RG:");

        f_f_sexo.setItems(new pSexo().Sexo());
        f_f_estcivil.setItems(new pEstCivil().EstCivil());
        f_f_conjuguesexo.setItems(new pSexo().Sexo());

        f_j_socios.setOnMouseReleased((event) -> {
            try {
                ChamaTela("Socios", "/Locatarios/Socios/Socios.fxml", "loca.png");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        LimpaTela();
        OpenFiadores(true);

        this.fields = new Object[] {
                f_fisica, f_juridica, f_cpf, f_rginsc, f_f_nome, f_f_sexo, f_f_dtnasc, f_f_endereco_fiador,
                f_f_btFindEndereco_fiador, f_f_numero_fiador, f_f_cplto_fiador, f_f_bairro_fiador, f_f_cidade_fiador,
                f_f_estado_fiador, f_f_cep_fiador, f_f_nacionalidade, f_f_estcivil, f_f_tel_menu, f_f_tel, f_f_mae,
                f_f_pai, f_f_empresa, f_f_dtadmissao, f_f_endereco, f_f_btFindEndereco, f_f_numero, f_f_cplto,
                f_f_bairro, f_f_cidade, f_f_estado, f_f_cep, f_f_cargo, f_f_salario, f_f_conjugue, f_f_conjuguedtnasc,
                f_f_conjuguesexo, f_f_conjuguetelefone_menu, f_f_conjuguetelefone, f_f_email_menu, f_f_email,
                f_j_razao, f_j_fantasia, f_j_endereco, f_j_btFindEndereco, f_j_numero, f_j_cplto, f_j_bairro,
                f_j_cidade, f_j_estado, f_j_cep, f_j_dtctrosocial, f_j_tel_menu, f_j_tel, f_j_email_menu, f_j_email,
                f_j_socios
        };
        new Controle("FIADOR: " + f_cpf.getText() + " - ", this.fields).Focus();
        new Controle(this.fields).FieldsEnabled(false);

        this.buttons = new Object[] {
                f_btIncluir, f_btAlterar, f_btExcluir, f_btGravar, f_btRetornar
        };

        if (DbMain.RecordCount(rs) <= 0) {
            Platform.runLater(() -> new Controle(this.buttons).BotaoEnabled(new Object[] {f_btIncluir, f_btRetornar}));
        } else {
            Platform.runLater(() -> new Controle(this.buttons).BotaoDisabled(new Object[] {f_btGravar}));
        }
    }

    private void LerSocios(String contrato) {
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

        f_j_socioscpfcnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        f_j_sociosnome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        f_j_socios.setItems(FXCollections.observableArrayList(data));
    }

    private void OpenFiadores(boolean ler) {
        rs = conn.AbrirTabela(fSql, ResultSet.CONCUR_UPDATABLE);
        try {
            rs.next();
            if (ler) LerLoca();
        } catch (SQLException e) {e.printStackTrace();}
        Platform.runLater(() -> {
            if (this.f_id != null && this.f_id != -1) {
                try {MoveTo("f_id", f_id.toString());} catch (SQLException e) {}
            } else LimpaTela();
        });
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
        f_fisica.setSelected(true);
        f_juridica.setSelected(false);

        f_cpf.setText(null);
        //f_cnpj.setText(null);
        MaskFieldUtil.maxField(f_rginsc,20); f_rginsc.setText(null);

        MaskFieldUtil.maxField(f_f_nome,60); f_f_nome.setText(null);
        f_f_sexo.getSelectionModel().select(0);
        f_f_dtnasc.setValue(null);

        MaskFieldUtil.maxField(f_f_endereco_fiador,60); f_f_endereco_fiador.setText(null);
        MaskFieldUtil.maxField(f_f_numero_fiador,10); f_f_numero_fiador.setText(null);
        MaskFieldUtil.maxField(f_f_cplto_fiador,15); f_f_cplto_fiador.setText(null);
        MaskFieldUtil.maxField(f_f_bairro_fiador,25); f_f_bairro_fiador.setText(null);
        MaskFieldUtil.maxField(f_f_cidade_fiador,25); f_f_cidade_fiador.setText(null);
        MaskFieldUtil.maxField(f_f_estado_fiador,2); f_f_estado_fiador.setText(null);

        MaskFieldUtil.maxField(f_f_nacionalidade,25); f_f_nacionalidade.setText(null);
        f_f_estcivil.getSelectionModel().select(0);

        f_f_tel.getItems().clear();

        MaskFieldUtil.maxField(f_f_mae,60);f_f_mae.setText(null);
        MaskFieldUtil.maxField(f_f_pai,60); f_f_pai.setText(null);
        MaskFieldUtil.maxField(f_f_empresa,60); f_f_empresa.setText(null);
        f_f_dtadmissao.setValue(null);
        MaskFieldUtil.maxField(f_f_endereco,60); f_f_endereco.setText(null);
        MaskFieldUtil.maxField(f_f_numero,10); f_f_numero.setText(null);
        MaskFieldUtil.maxField(f_f_cplto,15); f_f_cplto.setText(null);
        MaskFieldUtil.maxField(f_f_bairro,25); f_f_bairro.setText(null);
        MaskFieldUtil.maxField(f_f_cidade,25); f_f_cidade.setText(null);
        MaskFieldUtil.maxField(f_f_estado,2); f_f_estado.setText(null);
        f_f_cep.setText(null);
        MaskFieldUtil.maxField(f_f_cargo,25); f_f_cargo.setText(null);
        MaskFieldUtil.monetaryField(f_f_salario); f_f_salario.setText("0,00");
        MaskFieldUtil.maxField(f_f_conjugue,60); f_f_conjugue.setText(null);
        f_f_conjuguedtnasc.setValue(null);
        f_f_conjuguesexo.getSelectionModel().select(0);
        MaskFieldUtil.maxField(f_f_conjuguerg,20); f_f_conjuguerg.setText(null);
        MaskFieldUtil.cpfCnpjField(f_f_conjuguecpf); f_f_conjuguecpf.setText(null);
        MaskFieldUtil.monetaryField(f_f_conjuguesalario); f_f_conjuguesalario.setText("0,00");
        MaskFieldUtil.maxField(f_f_conjugueempresa,60); f_f_conjugueempresa.setText(null);

        f_f_conjuguetelefone.getItems().clear();

        f_f_email.getItems().clear();

        MaskFieldUtil.maxField(f_j_razao,60); f_j_razao.setText(null);
        MaskFieldUtil.maxField(f_j_fantasia,60); f_j_fantasia.setText(null);

        MaskFieldUtil.maxField(f_j_endereco,60); f_j_endereco.setText(null);
        MaskFieldUtil.maxField(f_j_numero,10); f_j_numero.setText(null);
        MaskFieldUtil.maxField(f_j_cplto,15); f_j_cplto.setText(null);
        MaskFieldUtil.maxField(f_j_bairro,25); f_j_bairro.setText(null);
        MaskFieldUtil.maxField(f_j_cidade,25); f_j_cidade.setText(null);
        MaskFieldUtil.maxField(f_j_estado,2); f_j_estado.setText(null);
        f_j_cep.setText(null);
        f_j_dtctrosocial.setValue(null);

        f_j_tel.getItems().clear();

        f_j_email.getItems().clear();
    }

    private void LerLoca() {
        try {this.pID = rs.getInt("f_id");} catch (SQLException e) {this.pID = -1;}

        try {if (rs.getBoolean("f_fisjur")) f_fisica.setSelected(true); else f_juridica.setSelected(true);} catch (SQLException e) {}
        if (f_fisica.isSelected()) {
            f_tabfisjur.getSelectionModel().select(0);
        } else {
            f_tabfisjur.getSelectionModel().select(1);
        }

        if (f_fisica.isSelected()) {
            try { f_cpf.setText(rs.getString("f_cpfcnpj")); } catch (SQLException e) {f_cpf.setText(null);}
        } else {
            //try { f_cnpj.setText(rs.getString("f_cpfcnpj"));} catch (SQLException e) {f_cnpj.setText(null);}
            try { f_cpf.setText(rs.getString("f_cpfcnpj"));} catch (SQLException e) {f_cpf.setText(null);}
        }

        try {f_rginsc.setText(rs.getString("f_rginsc"));} catch (SQLException e) {f_rginsc.setText(null);}

        try {f_f_nome.setText(rs.getString("f_f_nome"));} catch (SQLException e) {f_f_nome.setText(null);}
        try {f_f_sexo.getSelectionModel().select(rs.getString("f_f_sexo"));} catch (SQLException e) {f_f_sexo.getSelectionModel().select(0);}

        try {f_f_dtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("f_f_dtnasc")));} catch (Exception e) {f_f_dtnasc.getEditor().clear();}
        try {f_f_dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("f_f_dtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {f_f_dtnasc.setValue(null);}

        try {f_f_endereco_fiador.setText(rs.getString("f_f_endereco_fiador"));} catch (SQLException e) {f_f_endereco_fiador.setText(null);}
        try {f_f_numero_fiador.setText(rs.getString("f_f_numero_fiador"));} catch (SQLException e) {f_f_numero_fiador.setText(null);}
        try {f_f_cplto_fiador.setText(rs.getString("f_f_cplto_fiador"));} catch (SQLException e) {f_f_cplto_fiador.setText(null);}
        try {f_f_bairro_fiador.setText(rs.getString("f_f_bairro_fiador"));} catch (SQLException e) {f_f_bairro_fiador.setText(null);}
        try {f_f_cidade_fiador.setText(rs.getString("f_f_cidade_fiador"));} catch (SQLException e) {f_f_cidade_fiador.setText(null);}
        try {f_f_estado_fiador.setText(rs.getString("f_f_estado_fiador"));} catch (SQLException e) {f_f_estado_fiador.setText(null);}
        try {f_f_cep_fiador.setText(rs.getString("f_f_cep_fiador"));} catch (SQLException e) {f_f_cep_fiador.setText(null);}

        try {f_f_nacionalidade.setText(rs.getString("f_f_nacionalidade"));} catch (SQLException e) {f_f_nacionalidade.setText(null);}
        try {f_f_estcivil.getSelectionModel().select(rs.getString("f_f_estcivil"));} catch (SQLException e) {f_f_estcivil.getSelectionModel().select(0);}

        List<ptelcontatoModel> dataf_f_tel = null;
        try {dataf_f_tel = new setTels(rs.getString("f_f_tel")).rString();} catch (SQLException e) {}
        if (dataf_f_tel != null) f_f_tel.setItems(observableArrayList(dataf_f_tel)); else f_f_tel.getItems().clear();
        f_f_tel.setDisable(false);
        try {f_f_tel.getSelectionModel().select(0);} catch (Exception e) {}

        try {f_f_mae.setText(rs.getString("f_f_mae"));} catch (SQLException e) {f_f_mae.setText(null);}
        try {f_f_pai.setText(rs.getString("f_f_pai"));} catch (SQLException e) {f_f_pai.setText(null);}
        try {f_f_empresa.setText(rs.getString("f_f_empresa"));} catch (SQLException e) {f_f_empresa.setText(null);}

        try {f_f_dtadmissao.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("f_f_dtadmissao")));} catch (Exception e) {f_f_dtadmissao.getEditor().clear();}
        try {f_f_dtadmissao.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("f_f_dtadmissao").toString(), "yyyy-MM-dd")));} catch (Exception e) {f_f_dtadmissao.setValue(null);}

        try {f_f_endereco.setText(rs.getString("f_f_endereco"));} catch (SQLException e) {f_f_endereco.setText(null);}
        try {f_f_numero.setText(rs.getString("f_f_numero"));} catch (SQLException e) {f_f_numero.setText(null);}
        try {f_f_cplto.setText(rs.getString("f_f_cplto"));} catch (SQLException e) {f_f_cplto.setText(null);}
        try {f_f_bairro.setText(rs.getString("f_f_bairro"));} catch (SQLException e) {f_f_bairro.setText(null);}
        try {f_f_cidade.setText(rs.getString("f_f_cidade"));} catch (SQLException e) {f_f_cidade.setText(null);}
        try {f_f_estado.setText(rs.getString("f_f_estado"));} catch (SQLException e) {f_f_estado.setText(null);}
        try {f_f_cep_fiador.setText(rs.getString("f_f_cep"));} catch (SQLException e) {f_f_cep.setText(null);}
        try {f_f_cargo.setText(rs.getString("f_f_cargo"));} catch (SQLException e) {f_f_cargo.setText(null);}
        try {f_f_salario.setText(rs.getString("f_f_salario").toString());} catch (SQLException e) {f_f_salario.setText("0,00");}
        try {f_f_conjugue.setText(rs.getString("f_f_conjugue"));} catch (SQLException e) {f_f_conjugue.setText(null);}

        try {f_f_conjuguedtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("f_f_conjuguedtnasc")));} catch (Exception e) {f_f_conjuguedtnasc.getEditor().clear();}
        try {f_f_conjuguedtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("f_f_conjuguedtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {f_f_conjuguedtnasc.setValue(null);}

        try {f_f_conjuguesexo.getSelectionModel().select(rs.getString("f_f_conjuguesexo"));} catch (SQLException e) {f_f_conjuguesexo.getSelectionModel().select(0);}
        try {f_f_conjuguerg.setText(rs.getString("f_f_conjuguerg"));} catch (SQLException e) {f_f_conjuguerg.setText(null);}
        try {f_f_conjuguecpf.setText(rs.getString("f_f_conjuguecpf"));} catch (SQLException e) {f_f_conjuguecpf.setText(null);}
        try {f_f_conjuguesalario.setText(rs.getString("f_f_conjuguesalario").toString());} catch (SQLException e) {f_f_conjuguesalario.setText("0,00");}
        try {f_f_conjugueempresa.setText(rs.getString("f_f_conjugueempresa"));} catch (SQLException e) {f_f_conjugueempresa.setText(null);}

        List<ptelcontatoModel> dataf_f_conjuguetelefone = null;
        try {dataf_f_conjuguetelefone = new setTels(rs.getString("f_f_conjuguetelefone")).rString();} catch (SQLException e) {}
        if (dataf_f_conjuguetelefone != null) f_f_conjuguetelefone.setItems(observableArrayList(dataf_f_conjuguetelefone)); else f_f_conjuguetelefone.getItems().clear();
        f_f_conjuguetelefone.setDisable(false);
        try {f_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}

        List<pemailModel> dataf_f_email = null;
        try {dataf_f_email = new setEmails(rs.getString("f_f_email"),false).rString();} catch (SQLException e) {}
        if (dataf_f_email != null) f_f_email.setItems(observableArrayList(dataf_f_email)); else f_f_email.getItems().clear();
        f_f_email.setDisable(false);
        try {f_f_email.getSelectionModel().select(0);} catch (Exception e) {}

        try {f_j_razao.setText(rs.getString("f_j_razao"));} catch (SQLException e) {f_j_razao.setText(null);}
        try {f_j_fantasia.setText(rs.getString("f_j_fantasia"));} catch (SQLException e) {f_j_fantasia.setText(null);}

        try {f_j_endereco.setText(rs.getString("f_j_endereco"));} catch (SQLException e) {f_j_endereco.setText(null);}
        try {f_j_numero.setText(rs.getString("f_j_numero"));} catch (SQLException e) {f_j_numero.setText(null);}
        try {f_j_cplto.setText(rs.getString("f_j_cplto"));} catch (SQLException e) {f_j_cplto.setText(null);}
        try {f_j_bairro.setText(rs.getString("f_j_bairro"));} catch (SQLException e) {f_j_bairro.setText(null);}
        try {f_j_cidade.setText(rs.getString("f_j_cidade"));} catch (SQLException e) {f_j_cidade.setText(null);}
        try {f_j_estado.setText(rs.getString("f_j_estado"));} catch (SQLException e) {f_j_estado.setText(null);}
        try {f_j_cep.setText(rs.getString("f_j_cep"));} catch (SQLException e) {f_j_cep.setText(null);}

        try {f_j_dtctrosocial.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("f_j_dtctrosocial")));} catch (Exception e) {f_j_dtctrosocial.getEditor().clear();}
        try {f_j_dtctrosocial.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("f_j_dtctrosocial").toString(), "yyyy-MM-dd")));} catch (Exception e) {f_j_dtctrosocial.setValue(null);}

        List<ptelcontatoModel> dataf_j_tel = null;
        try {dataf_j_tel = new setTels(rs.getString("f_j_tel")).rString();} catch (SQLException e) {}
        if (dataf_j_tel != null) f_j_tel.setItems(observableArrayList(dataf_j_tel)); else f_j_tel.getItems().clear();
        f_j_tel.setDisable(false);
        try {f_j_tel.getSelectionModel().select(0);} catch (Exception e) {}

        List<pemailModel> dataf_j_email = null;
        try {dataf_j_email = new setEmails(rs.getString("f_j_email"),false).rString();} catch (SQLException e) {}
        if (dataf_j_email != null) f_j_email.setItems(observableArrayList(dataf_j_email)); else f_j_email.getItems().clear();
        f_j_email.setDisable(false);
        try {f_j_email.getSelectionModel().select(0);} catch (Exception e) {}

        LerSocios(f_contrato.trim());
    }

    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO fiadores(f_rgprp, f_rgimv, f_contrato, f_fisjur, f_cpfcnpj, " +
                    "            f_rginsc, f_f_nome, f_f_sexo, f_f_dtnasc, " +
                    "            f_f_endereco_fiador, f_f_numero_fiador, f_f_cplto_fiador, f_f_bairro_fiador, f_f_cidade_fiador, " +
                    "            f_f_estado_fiador, f_f_cep_fiador, f_f_nacionalidade, " +
                    "            f_f_estcivil, f_f_tel, f_f_mae, f_f_pai, f_f_empresa, f_f_dtadmissao, " +
                    "            f_f_endereco, f_f_numero, f_f_cplto, f_f_bairro, f_f_cidade, " +
                    "            f_f_estado, f_f_cep, f_f_cargo, f_f_salario, f_f_conjugue, f_f_conjuguedtnasc, " +
                    "            f_f_conjuguesexo, f_f_conjuguerg, f_f_conjuguecpf, f_f_conjuguesalario, " +
                    "            f_f_conjugueempresa, f_f_conjuguetelefone, f_f_email, f_j_razao, " +
                    "            f_j_fantasia, f_j_endereco, f_j_numero, f_j_cplto, f_j_bairro, " +
                    "            f_j_cidade, f_j_estado, f_j_cep, f_j_dtctrosocial, f_j_tel, f_j_email)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?);";
        } else {
            sql = "UPDATE fiadores" +
                    "   SET f_rgprp=?, f_rgimv=?, f_contrato=?, f_fisjur=?, " +
                    "       f_cpfcnpj=?, f_rginsc=?, f_f_nome=?, f_f_sexo=?, f_f_dtnasc=?, " +
                    "       f_f_endereco_fiador=?, f_f_numero_fiador=?, f_f_cplto_fiador=?, " +
                    "       f_f_bairro_fiador=?, f_f_cidade_fiador=?, f_f_estado_fiador=?, f_f_cep_fiador=?, " +
                    "       f_f_nacionalidade=?, f_f_estcivil=?, f_f_tel=?, f_f_mae=?, f_f_pai=?, " +
                    "       f_f_empresa=?, f_f_dtadmissao=?, f_f_endereco=?, f_f_numero=?, " +
                    "       f_f_cplto=?, f_f_bairro=?, f_f_cidade=?, f_f_estado=?, f_f_cep=?, " +
                    "       f_f_cargo=?, f_f_salario=?, f_f_conjugue=?, f_f_conjuguedtnasc=?, " +
                    "       f_f_conjuguesexo=?, f_f_conjuguerg=?, f_f_conjuguecpf=?, f_f_conjuguesalario=?, " +
                    "       f_f_conjugueempresa=?, f_f_conjuguetelefone=?, f_f_email=?, f_j_razao=?, " +
                    "       f_j_fantasia=?, f_j_endereco=?, f_j_numero=?, f_j_cplto=?, f_j_bairro=?, " +
                    "       f_j_cidade=?, f_j_estado=?, f_j_cep=?, f_j_dtctrosocial=?, f_j_tel=?, " +
                    "       f_j_email=?" +
                    " WHERE f_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1;
            pstmt.setString(nid++, f_rgprp);
            pstmt.setString(nid++, f_rgimv);
            pstmt.setString(nid++, f_contrato);
            pstmt.setBoolean(nid++, f_fisica.isSelected() ? true : false);
            //pstmt.setString(nid++, f_fisica.isSelected() ? f_cpf.getText() : f_cnpj.getText());
            pstmt.setString(nid++, f_fisica.isSelected() ? f_cpf.getText() : f_cpf.getText());
            pstmt.setString(nid++, f_rginsc.getText());
            pstmt.setString(nid++, f_f_nome.getText());
            pstmt.setString(nid++, f_f_sexo.getSelectionModel().getSelectedItem().toString());
            pstmt.setDate(nid++, Dates.toSqlDate(f_f_dtnasc));

            pstmt.setString(nid++, f_f_endereco_fiador.getText());
            pstmt.setString(nid++, f_f_numero_fiador.getText());
            pstmt.setString(nid++, f_f_cplto_fiador.getText());
            pstmt.setString(nid++, f_f_bairro_fiador.getText());
            pstmt.setString(nid++, f_f_cidade_fiador.getText());
            pstmt.setString(nid++, f_f_estado_fiador.getText());
            pstmt.setString(nid++, f_f_cep_fiador.getText());

            pstmt.setString(nid++, f_f_nacionalidade.getText());
            pstmt.setString(nid++, f_f_estcivil.getSelectionModel().getSelectedItem().toString());
            pstmt.setString(nid++, new getTels(f_f_tel).toString());
            pstmt.setString(nid++, f_f_mae.getText());
            pstmt.setString(nid++, f_f_pai.getText());
            pstmt.setString(nid++, f_f_empresa.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(f_f_dtadmissao));
            pstmt.setString(nid++, f_f_endereco.getText());
            pstmt.setString(nid++, f_f_numero.getText());
            pstmt.setString(nid++, f_f_cplto.getText());
            pstmt.setString(nid++, f_f_bairro.getText());
            pstmt.setString(nid++, f_f_cidade.getText());
            pstmt.setString(nid++, f_f_estado.getText());
            pstmt.setString(nid++, f_f_cep.getText());
            pstmt.setString(nid++, f_f_cargo.getText());
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(f_f_salario.getText())));
            pstmt.setString(nid++, f_f_conjugue.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(f_f_conjuguedtnasc));
            pstmt.setString(nid++, f_f_conjuguesexo.getSelectionModel().getSelectedItem().toString());

            pstmt.setString(nid++, f_f_conjuguerg.getText());
            pstmt.setString(nid++, f_f_conjuguecpf.getText());
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(f_f_conjuguesalario.getText())));
            pstmt.setString(nid++, f_f_conjugueempresa.getText());
            pstmt.setString(nid++, new getTels(f_f_conjuguetelefone).toString());
            pstmt.setString(nid++, new getEmails(f_f_email,false).toString());
            pstmt.setString(nid++, f_j_razao.getText());
            pstmt.setString(nid++, f_j_fantasia.getText());
            pstmt.setString(nid++, f_j_endereco.getText());
            pstmt.setString(nid++, f_j_numero.getText());
            pstmt.setString(nid++, f_j_cplto.getText());
            pstmt.setString(nid++, f_j_bairro.getText());
            pstmt.setString(nid++, f_j_cidade.getText());
            pstmt.setString(nid++, f_j_estado.getText());
            pstmt.setString(nid++, f_j_cep.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(f_j_dtctrosocial));
            pstmt.setString(nid++, new getTels(f_j_tel).toString());
            pstmt.setString(nid++, new getEmails(f_j_email,false).toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        return retorno;
    }

    private void verifyBotoes() {
        if (this.viea == null) return;

        if (!this.viea.contains("I")) f_btIncluir.setDisable(true);
        if (!this.viea.contains("E")) f_btExcluir.setDisable(true);
        if (!this.viea.contains("A")) f_btGravar.setDisable(true);
    }

    private void ChamaTela(String nome, String url, String icone) throws IOException, Exception {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource(url)); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(false);


        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.fireEvent(new vieaEvent("IEA", vieaEvent.GET_VIEA));

        psociosModel psocios = f_j_socios.getSelectionModel().getSelectedItem();
        if (psocios != null) {
            root.fireEvent(new paramEvent(new Object[]{psocios.getId(), psocios.getRgprp(), psocios.getRgimv(), psocios.getContrato(), psocios.getCnpj()}, paramEvent.GET_PARAM));
        } else {
            root.fireEvent(new paramEvent(new Object[]{-1, f_rgprp, f_rgimv, f_contrato, null}, paramEvent.GET_PARAM));
        }

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            Consulta consulta = (Consulta)event.sparam[0];
            try {internalFrame.close();} catch (NullPointerException e) {}
            if (consulta != null) {
                f_f_nome.setText(consulta.getNomerazao());
                f_f_nome.setDisable(true);
                f_cpf.setText(consulta.getCpfcnpj());
                f_cpf.setDisable(true);
                f_f_endereco.setText(consulta.getEndereco());
                f_f_numero.setText(consulta.getNumero());
                f_f_cplto.setText(consulta.getComplemento());
                f_f_bairro.setText(consulta.getBairro());
                f_f_cidade.setText(consulta.getCidade());
                f_f_estado.setText(consulta.getEstado());
                f_f_cep.setText(consulta.getCep());
                f_rginsc.setText(consulta.getRginsc());

                // Telefones
                List<ptelcontatoModel> data = new setTels(consulta.getTelefones()).rString();
                f_f_tel.getItems().clear();
                if (data != null) f_f_tel.setItems(observableArrayList(data));
                else f_f_tel.getItems().clear();
                f_f_tel.setDisable(false);
                try {
                    f_f_tel.getSelectionModel().select(0);
                    f_f_tel.getEditor().setText(data.get(0).toString());
                } catch (Exception e) {
                }

                // Emails
                List<pemailModel> dataemail = null;
                String emailp = consulta.getEmails();
                if (emailp != null) {
                    dataemail = new setEmails(emailp, true).rString();
                }
                f_f_email.getItems().clear();
                if (dataemail != null) f_f_email.setItems(observableArrayList(dataemail));
                else f_f_email.getItems().clear();
                f_f_email.setDisable(false);
                try { f_f_email.getSelectionModel().select(0); } catch (Exception e) { }
            }
        });
    }

/*
    private void ChamaTela2(String nome, String url, String icone) throws IOException, Exception {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource(url));
            MDIWindow mDIWindow = new MDIWindow(nome, new ImageView(icone != "" ? "/Figuras/" + icone : null), nome, root,true);

            MDICanvas canvas = ((MDICanvas) f_anchorPane.getParent().getParent().getParent());
            canvas.addMDIWindow(mDIWindow);

            root.fireEvent(new vieaEvent("IEA", vieaEvent.GET_VIEA));

            psociosModel psocios = f_j_socios.getSelectionModel().getSelectedItem();
            if (psocios != null) {
                root.fireEvent(new paramEvent(new Object[]{psocios.getId(), psocios.getRgprp(), psocios.getRgimv(), psocios.getContrato(), psocios.getCnpj()}, paramEvent.GET_PARAM));
            } else {
                root.fireEvent(new paramEvent(new Object[]{-1, f_rgprp, f_rgimv, f_contrato, null}, paramEvent.GET_PARAM));
            }
            mDIWindow.setCenter(root);
        } catch (Exception e) {e.printStackTrace();}
    }
*/
    private void LerDadosFiador(cFiadores dados) {
        if (dados.getFisjur()) f_fisica.setSelected(true); else f_juridica.setSelected(true);
        if (dados.getFisjur()) {
            f_tabfisjur.getSelectionModel().select(0);
            if (dados.getCpfcnpj() != null)f_cpf.setText(dados.getCpfcnpj());
            if (dados.getRginsc() != null)f_rginsc.setText(dados.getRginsc());
            if (dados.getNome() != null)f_f_nome.setText(dados.getNome());
            if (dados.getSexo() != null)f_f_sexo.getSelectionModel().select(dados.getSexo());
            if (dados.getDtnasc() != null)f_f_dtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", dados.getDtnasc()));
            if (dados.getDtnasc() != null)f_f_dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(dados.getDtnasc().toString(), "yyyy-MM-dd")));
            if (dados.getEndereco() != null)f_f_endereco_fiador.setText(dados.getEndereco());
            if (dados.getNumero() != null)f_f_numero_fiador.setText(dados.getNumero());
            if (dados.getCplto() != null)f_f_cplto_fiador.setText(dados.getCplto());
            if (dados.getBairro() != null)f_f_bairro_fiador.setText(dados.getBairro());
            if (dados.getCidade() != null)f_f_cidade_fiador.setText(dados.getCidade());
            if (dados.getEstado() != null)f_f_estado_fiador.setText(dados.getEstado());
            if (dados.getCep() != null)f_f_cep_fiador.setText(dados.getCep());
            if (dados.getNacinalidade() != null)f_f_nacionalidade.setText(dados.getNacinalidade());
            if (dados.getEcivil() != null)f_f_estcivil.getSelectionModel().select(dados.getEcivil());
            if (dados.getMae() != null)f_f_mae.setText(dados.getMae());
            if (dados.getPai() != null)f_f_pai.setText(dados.getPai());
        } else {
            f_tabfisjur.getSelectionModel().select(1);
            if (dados.getNome() != null)f_j_razao.setText(dados.getNome());
            if (dados.getFantasia() != null)f_j_fantasia.setText(dados.getFantasia());
            if (dados.getEndereco() != null)f_j_endereco.setText(dados.getEndereco());
            if (dados.getNumero() != null)f_j_numero.setText(dados.getNumero());
            if (dados.getCplto() != null)f_j_cplto.setText(dados.getCplto());
            if (dados.getBairro() != null)f_j_bairro.setText(dados.getBairro());
            if (dados.getCidade() != null)f_j_cidade.setText(dados.getCidade());
            if (dados.getEstado() != null)f_j_estado.setText(dados.getEstado());
            if (dados.getCep() != null) f_j_cep.setText(dados.getCep());
            if (dados.getDtctro() != null) f_j_dtctrosocial.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", dados.getDtctro()));
            if (dados.getDtctro() != null)f_j_dtctrosocial.setValue(Dates.toLocalDate(Dates.StringtoDate(dados.getDtctro().toString(), "yyyy-MM-dd")));
        }
    }
}
