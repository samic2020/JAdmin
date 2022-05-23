/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Locatarios;

import Classes.*;
import Funcoes.*;
import Locatarios.Fiadores.pfiadoresModel;
import Locatarios.Socios.psociosModel;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * FXML Controller class
 *
 * @author supervisor
 */
public class LocatarioController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    String lSql = "SELECT * FROM locatarios WHERE exclusao is null ORDER BY l_contrato;"
            ;
    ResultSet rs = null;
    String viea = null;

    boolean bInc = false;
    int pID = -1;

    Object[] fields;
    Object[] buttons;

    @FXML private AnchorPane l_anchorPane;
    @FXML private TextField l_rgprp;
    @FXML private TextField l_rgimv;
    @FXML private TextField l_tpimovel;
    @FXML private TextField l_contrato;
    @FXML private RadioButton l_fisica;
    @FXML private RadioButton l_juridica;
    @FXML private TextField l_cpfcnpj;
    @FXML private TextField l_rginsc;
    @FXML private TabPane l_tabfisjur;
    @FXML private Tab l_tabFisica;
    @FXML private Tab l_tabJuridica;
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
    @FXML private TextField l_f_numero;
    @FXML private TextField l_f_cplto;
    @FXML private Button l_f_btFindEndereco;
    @FXML private TextField l_f_bairro;
    @FXML private TextField l_f_cidade;
    @FXML private TextField l_f_estado;
    @FXML private MaskTextField l_f_cep;
    @FXML private TextField l_f_cargo;
    @FXML private TextField l_f_salario;
    @FXML private TextField l_f_conjugue;
    @FXML private DatePicker l_f_conjuguedtnasc;
    @FXML private ComboBox<String> l_f_conjuguesexo;
    @FXML private TextField l_f_conjuguerg;
    @FXML private TextField l_f_conjuguecpf;
    @FXML private TextField l_f_conjuguesalario;
    @FXML private TextField l_f_conjugueempresa;

    @FXML private MenuButton l_f_conjuguetelefone_menu;
    @FXML private MenuItem l_f_conjugueteladc;
    @FXML private MenuItem l_f_conjugueteldel;
    @FXML private ComboBox<ptelcontatoModel> l_f_conjuguetelefone;

    @FXML private ComboBox<pemailModel> l_f_email;
    @FXML private MenuButton l_f_email_menu;
    @FXML private MenuItem l_f_emailadc;
    @FXML private MenuItem l_f_emaildel;

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
    @FXML private TableColumn l_j_socioscpfcnpj;
    @FXML private TableColumn l_j_sociosnome;

    @FXML private TableView<pfiadoresModel> l_fiadores;
    @FXML private TableColumn l_fiadorescpfcnpj;
    @FXML private TableColumn l_fiadoresnome;

    @FXML private TextArea l_historico;

    @FXML private TextField l_avisos;
    @FXML private TextField l_msg;
    @FXML private ComboBox<String> l_tprecebimento;
    @FXML private ComboBox<String> l_formaenvio;

    @FXML private Button l_btIncluir;
    @FXML private Button l_btAlterar;
    @FXML private Button l_btExcluir;
    @FXML private Button l_btCarteira;
    @FXML private Button l_btIrPara;
    @FXML private Button l_btPrevious;
    @FXML private Button l_btNext;
    @FXML private Button l_btPagamentos;
    @FXML private Button l_btBoletas;
    @FXML private Button l_btGravar;
    @FXML private Button l_btRetornar;

    // Seguradora/Deposito
    @FXML private ToggleGroup depseg;
    @FXML private RadioButton deposito;
    @FXML private TextField vrdeposito;

    @FXML private RadioButton seguradora;
    @FXML private ComboBox<String> nmseguradora;
    @FXML private TextField nroapolice;
    @FXML private DatePicker dtapolice;

    // Tab Acordos de Pagamento
    @FXML private TextField acp_nvectos;
    @FXML private ComboBox<String> acp_vectos;
    @FXML private DatePicker acp_dtacordo;
    @FXML private Spinner acp_nvectosatrz;
    @FXML private CheckBox acp_ultvectos;

    // Locatários Adicionais
    @FXML private TableView<LocAdicionais> l_adicionais;
    @FXML private TableColumn<LocAdicionais, Integer> l_adcid;
    @FXML private TableColumn<LocAdicionais, String> l_adccpfcnpj;
    @FXML private TableColumn<LocAdicionais, String> l_adcnome;

    @FXML void l_btCarteira_OnAction(ActionEvent event) {
        try {ChamaTela("Carteira","/Locatarios/Carteira/Carteira.fxml", "loca.png");} catch (Exception e) {}
    }

    @FXML void l_btExcluir_OnAction(ActionEvent event) {
        // Movimentos - Aluguel
        Object[][] movto = null;
        try {
            movto = conn.LerCamposTabela(new String[]{"dtvencimento"}, "movimento", "contrato = '" + l_contrato.getText() + "' AND dtrecebimento is null AND aut_rec is null");
        } catch (Exception e) {}
        if (movto != null) {
            // Mensagem avisando que não esta vazio
            Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) recibo(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg1.showAndWait();
            if (result.get().getText().equals("Não")) {
                return;
            }
        }

        // Taxas
        Object[][] taxas = null;
        try {
            taxas = conn.LerCamposTabela(new String[] {"dtvencimento"},"taxas", "contrato = '" + l_contrato.getText() + "' AND dtrecebimento is null AND aut_rec is null");
        } catch (Exception e){}
        if (taxas != null) {
            Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) taxa(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg1.showAndWait();
            if (result.get().getText().equals("Não")) {
                return;
            }
        }

        // Seguros
        Object[][] seguros = null;
        try {
            seguros = conn.LerCamposTabela(new String[] {"dtvencimento"},"seguros", "contrato = '" + l_contrato.getText() + "' AND dtrecebimento is null AND aut_rec is null");
        } catch (Exception e) {}
        if (seguros != null) {
            Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) seguro(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg1.showAndWait();
            if (result.get().getText().equals("Não")) {
                return;
            }
        }

        // DescDif
        Object[][] descdif = null;
        try {
            descdif = conn.LerCamposTabela(new String[] {"dtrecebimento"},"descdif", "contrato = '" + l_contrato.getText() + "' AND dtrecebimento is null AND aut_rec is null");
        } catch (Exception e) {}
        if (descdif != null) {
            Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe(m) desconto(s) e/ou diferença(s) não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg1.showAndWait();
            if (result.get().getText().equals("Não")) {
                return;
            }
        }

        // Avisos
        String sumSQL = "SELECT CASE WHEN tipo = 'CRE' THEN sum(valor) ELSE 0 END AS tcred, CASE WHEN tipo = 'DEB' THEN sum(valor) ELSE 0 END AS tdeb FROM avisos WHERE registro = '" + l_contrato.getText() + "' AND conta = '2' GROUP BY tipo;";
        ResultSet sumRs = conn.AbrirTabela(sumSQL, ResultSet.CONCUR_READ_ONLY);
        BigDecimal tcred = new BigDecimal("0");
        BigDecimal tdeb = new BigDecimal("0");
        try {
            while (sumRs.next()) {
                tcred = tcred.add(sumRs.getBigDecimal("tcred"));
                tdeb = tdeb.add(sumRs.getBigDecimal("tdeb"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        try { DbMain.FecharTabela(sumRs); } catch (Exception e) {}
        if (tcred.subtract(tdeb).compareTo(BigDecimal.ZERO) != 0) {
            // Existe saldo
            Alert msg1 = new Alert(Alert.AlertType.INFORMATION,"Existe saldo de aviso não quitados para este imóvel.\n\nDeseja continuar?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg1.showAndWait();
            if (result.get().getText().equals("Não")) {
                return;
            }
        }
        
        Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este locatario?", new ButtonType("Sim"), new ButtonType("Não"));
        Optional<ButtonType> result = msg.showAndWait();
        if (result.get().getText().equals("Sim")) {
            String sql = "UPDATE locatarios SET exclusao = '%s' WHERE l_contrato = '%s';";
            sql = String.format(sql, Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()), l_contrato.getText());
            try { conn.ExecutarComando(sql); } catch (Exception e) { }
        }
    }

    @FXML void l_btGravar(ActionEvent event) {
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            if (bInc) {
                int NewRgPrp = 0;
                try {
                    NewRgPrp = Integer.parseInt(conn.LerParametros("CONTRATO"));
                } catch (SQLException ex) {}

                String cPar[] = {"CONTRATO",String.valueOf(NewRgPrp + 1),"NUMERICO"};
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

                rs = conn.AbrirTabela(lSql, ResultSet.CONCUR_UPDATABLE);
                rs.absolute(pos);
                if (DbMain.RecordCount(rs) <= 0) {
                    new Controle(this.buttons).BotaoEnabled(new Object[] {l_btIncluir, l_btRetornar});
                } else {
                    new Controle(this.buttons).BotaoDisabled(new Object[] {l_btGravar});
                }
            } catch (SQLException e) {}

            new Controle(this.fields).FieldsEnabled(false);
        }
    }

    @FXML void l_btIncluir_OnAction(ActionEvent event) {
        inclusaoLocatario dialog = new inclusaoLocatario();
        Optional<pimoveisModel> result = dialog.inclusaoLocatario();
        result.ifPresent(b -> {
            this.bInc = true;
            LimpaTela();

            new Controle(this.buttons).BotaoEnabled(new Object[] {l_btGravar, l_btRetornar});
            new Controle(this.fields).FieldsEnabled(true);

            l_contrato.setText(null); l_contrato.setDisable(true);
            l_rgprp.setText(b.getRgprp());
            l_rgimv.setText(b.getRgimv());
            l_tpimovel.setText(b.getTipo());
            l_fisica.requestFocus();
        });
    }

    @FXML void l_btAlterar_OnAction(ActionEvent event) {
        this.bInc = false;

        new Controle(this.buttons).BotaoEnabled(new Object[] {l_btGravar, l_btRetornar});
        new Controle(this.fields).FieldsEnabled(true);

        l_fisica.requestFocus();
    }

    @FXML void l_btIrPara_OnAction(ActionEvent event) {
        l_contrato.setEditable(true);
        l_contrato.requestFocus();
    }

    @FXML void l_btPagamentos_OnAction(ActionEvent event) {
        try {
            ChamaTela("Recebimentos", "/Locatarios/Pagamentos/PagtosLocatario.fxml", "loca.png");
        } catch (Exception e) {}
    }

    @FXML void l_btPrevious_OnAction(ActionEvent event) {
        try {
            rs.previous();
            if (rs.isBeforeFirst()) {
                rs.next();
            }
            LerLoca();
        } catch (SQLException e) {}
    }

    @FXML void l_btNext_OnAction(ActionEvent event) {
        try {
            rs.next();
            if (rs.isAfterLast()) {
                rs.previous();
            }
            LerLoca();
        } catch (SQLException e) {}
    }

    @FXML void l_btRetornar_OnAction(ActionEvent event) {
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

        try {l_anchorPane.fireEvent(new paramEvent(new String[] {"Locatario"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
    }

    @FXML void l_f_conjugueteladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = l_f_conjuguetelefone.getItems();
            tels.addAll(b);
            l_f_conjuguetelefone.setItems(tels);
            try {l_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void l_f_conjugueteldel_OnAction(ActionEvent event) {
        if (!l_f_conjuguetelefone.getItems().isEmpty()) l_f_conjuguetelefone.getItems().removeAll(l_f_conjuguetelefone.getSelectionModel().getSelectedItem());
        try {l_f_conjuguetelefone.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void l_f_emailadc_OnAction(ActionEvent event) {
        adcEmails dialog = new adcEmails();
        Optional<pemailModel> result = dialog.adcEmails(false);
        result.ifPresent(b -> {
            ObservableList<pemailModel> emails = l_f_email.getItems();
            emails.addAll(b);
            l_f_email.setItems(emails);
            try {l_f_email.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void l_f_emaildel_OnAction(ActionEvent event) {
        if (!l_f_email.getItems().isEmpty()) l_f_email.getItems().removeAll(l_f_email.getSelectionModel().getSelectedItem());
        try {l_f_email.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void l_f_teladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = l_f_tel.getItems();
            tels.addAll(b);
            l_f_tel.setItems(tels);
            try {l_f_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void l_f_teldel_OnAction(ActionEvent event) {
        if (!l_f_tel.getItems().isEmpty()) l_f_tel.getItems().removeAll(l_f_tel.getSelectionModel().getSelectedItem());
        try {l_f_tel.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void l_j_emailadc_OnAction(ActionEvent event) {
        adcEmails dialog = new adcEmails();
        Optional<pemailModel> result = dialog.adcEmails(false);
        result.ifPresent(b -> {
            ObservableList<pemailModel> emails = l_j_email.getItems();
            emails.addAll(b);
            l_j_email.setItems(emails);
            try {l_j_email.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void l_j_emaildel_OnAction(ActionEvent event) {
        if (!l_j_email.getItems().isEmpty()) l_j_email.getItems().removeAll(l_j_email.getSelectionModel().getSelectedItem());
        try {l_j_email.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void l_j_teladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = l_j_tel.getItems();
            tels.addAll(b);
            l_j_tel.setItems(tels);
            try {l_j_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void l_j_teldel_OnAction(ActionEvent event) {
        if (!l_j_tel.getItems().isEmpty()) l_j_tel.getItems().removeAll(l_j_tel.getSelectionModel().getSelectedItem());
        try {l_j_tel.getSelectionModel().select(0);} catch (Exception e) {}
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
        l_anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
            }
        });
        l_anchorPane.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });

        Platform.runLater(() -> { verifyBotoes(); });

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

        l_tprecebimento.setItems(new pTipoRecebimento().TipoRecebimento());
        l_formaenvio.setItems(new pFormaEnvio().FormaEnvio());
        l_formaenvio.disableProperty().bind(l_tprecebimento.getSelectionModel().selectedItemProperty().isEqualTo("Recibo"));

        l_fiadores.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                try {ChamaTela("Fiadores", "/Locatarios/Fiadores/Fiadores.fxml", "loca.png");} catch (Exception ex) {}
            }
        });

        l_j_socios.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                try {ChamaTela("Sócios", "/Locatarios/Socios/Socios.fxml", "loca.png");} catch (Exception ex) {}
            }
        });

        // Cadastros serão feito pelo preContrato
        l_btIncluir.setVisible(false);

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
                seguradora, nmseguradora, nroapolice, dtapolice, l_fiadores, l_historico, l_avisos, l_msg,
                l_tprecebimento, l_formaenvio, acp_vectos, acp_dtacordo, acp_nvectosatrz, acp_ultvectos
        };
        new Controle("LOCATARIO: " + l_contrato.getText() + " - ", this.fields).Focus();
        new Controle(this.fields).FieldsEnabled(false);

        this.buttons = new Object[] {
                l_btIncluir, l_btAlterar, l_btExcluir, l_btCarteira, l_btIrPara, l_btPrevious, l_btNext,
                l_btPagamentos, l_btGravar, l_btRetornar
        };

        if (DbMain.RecordCount(rs) <= 0) {
            new Controle(this.buttons).BotaoEnabled(new Object[] {l_btIncluir, l_btRetornar});
        } else {
            new Controle(this.buttons).BotaoDisabled(new Object[] {l_btGravar});
        }

        l_adicionais.setOnMousePressed(event -> {
            if (!bInc) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try { ChamaTela("adcLocatarios", "/Locatarios/Adicionais/AdcLocatarios.fxml", "loca.png"); } catch (Exception ex) { }
                }
            }
        });

    }

    private void OpenLocatarios(boolean ler) {
        rs = conn.AbrirTabela(lSql, ResultSet.CONCUR_READ_ONLY);
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

        l_historico.setText(null);

        MaskFieldUtil.maxField(l_avisos,60); l_avisos.setText(null);
        MaskFieldUtil.maxField(l_msg,60); l_msg.setText(null);
        l_tprecebimento.getSelectionModel().select(0);
        l_formaenvio.getSelectionModel().select(0);

        deposito.setSelected(false);
        seguradora.setSelected(false);
        vrdeposito.setText("0,00");
        nmseguradora.getSelectionModel().select(-1);
        nroapolice.setText(null);
        dtapolice.getEditor().setText(null);

        // Aba acordos
        acp_nvectos.setText("");
        acp_vectos.getItems().clear();
        acp_dtacordo.setValue(null);
        acp_nvectosatrz.getEditor().setText("0");
        acp_ultvectos.setSelected(false);
    }

    private void LerLoca() {
        try {this.pID = rs.getInt("l_id");} catch (SQLException e) {this.pID = -1;}
        try {l_rgprp.setEditable(false);l_rgprp.setText(rs.getString("l_rgprp"));} catch (SQLException e) {l_rgprp.setText(null);}
        try {l_rgimv.setEditable(false);l_rgimv.setText(rs.getString("l_rgimv"));} catch (SQLException e) {l_rgimv.setText(null);}
        try {l_tpimovel.setEditable(false);l_tpimovel.setText(rs.getString("l_tipoimovel"));} catch (SQLException e) {l_tpimovel.setText(null);}
        try {l_contrato.setEditable(false);l_contrato.setText(rs.getString("l_contrato"));} catch (SQLException e) {l_contrato.setText(null);}

        try {l_cpfcnpj.setText(rs.getString("l_cpfcnpj"));} catch (SQLException e) {l_cpfcnpj.setText(null);}

        if (FuncoesGlobais.LimpaCpfCnpj(l_cpfcnpj.getText()).length() == 11) l_fisica.setSelected(true); else l_juridica.setSelected(true);
        if (l_fisica.isSelected()) {
            l_tabfisjur.getSelectionModel().select(0);
        } else {
            l_tabfisjur.getSelectionModel().select(1);
        }

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

        try {l_historico.setText(rs.getString("l_historico"));} catch (SQLException e) {l_historico.setText(null);}

        try {l_avisos.setText(rs.getString("l_avisos"));} catch (SQLException e) {l_avisos.setText(null);}
        try {l_msg.setText(rs.getString("l_msg"));} catch (SQLException e) {l_msg.setText(null);}

        try {
            String tprec = null;
            tprec = rs.getString("l_tprecebimento");
            if (tprec != null) {
                l_tprecebimento.getSelectionModel().select(tprec.equalsIgnoreCase("REC") ? "Recibo" : tprec);
            } else l_tprecebimento.getSelectionModel().select("Recibo");
        } catch (SQLException e) {
            //e.printStackTrace();
            l_tprecebimento.getSelectionModel().select(0);
        }

        try {
            String fenvio = null;
            fenvio = rs.getString("l_formaenvio");
            if (fenvio != null) {l_formaenvio.getSelectionModel().select(Integer.valueOf(fenvio));}else{l_formaenvio.getSelectionModel().select(0);}
        } catch (SQLException e) {
            l_formaenvio.getSelectionModel().select(0);
        }

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

        LerFiadores(l_contrato.getText());
        LerSocios(l_contrato.getText());
        try {LerAdicionais(this.pID);} catch (NullPointerException e) {}

        // Ler dados de Acordos de Pagamento
        Object[] nvectos = FiltraVencimentos(l_contrato.getText());
        nvectos = FuncoesGlobais.ObjectsOrdenaData(nvectos);
        acp_nvectos.setText(String.valueOf(nvectos.length));
        acp_vectos.getItems().clear();
        if (nvectos.length > 0) {
            for (Object d : nvectos) {
                String dt = Dates.DateFormata("dd/MM/yyyy",(Date)d);
                acp_vectos.getItems().add(dt);
            }
            acp_vectos.getSelectionModel().select(0);
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, nvectos.length, nvectos.length);
            acp_nvectosatrz.setValueFactory(valueFactory);
            int tvectatz = -1; try { tvectatz = rs.getInt("l_nrecatz"); } catch (SQLException e) {}
            if (tvectatz < 0) tvectatz = nvectos.length;
            acp_nvectosatrz.getEditor().setText(String.valueOf(tvectatz));

            Date tdtacordo = null;
            try {tdtacordo = rs.getDate("l_dtacordo");} catch (SQLException e) {}
            try {if (tdtacordo != null) acp_dtacordo.setValue(Dates.toLocalDate(tdtacordo));} catch (Exception e) {}

            try { acp_ultvectos.setSelected(rs.getBoolean("l_ultrecto")); } catch (Exception e) {acp_ultvectos.setSelected(false);}
        }
    }

    private void LerSocios(String contrato) {
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
            sql = "INSERT INTO locatarios(l_rgprp, l_rgimv, l_contrato, l_tipoimovel, l_fisjur, l_cpfcnpj, " +
                    "            l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade, " +
                    "            l_f_estcivil, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao, " +
                    "            l_f_endereco, l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade, " +
                    "            l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue, l_f_conjuguedtnasc, " +
                    "            l_f_conjuguesexo, l_f_conjuguerg, l_f_conjuguecpf, l_f_conjuguesalario, " +
                    "            l_f_conjugueempresa, l_f_conjuguetelefone, l_f_email, l_j_razao, " +
                    "            l_j_fantasia, l_j_endereco, l_j_numero, l_j_cplto, l_j_bairro, " +
                    "            l_j_cidade, l_j_estado, l_j_cep, l_j_dtctrosocial, l_j_tel, l_j_email, " +
                    "            l_historico, l_avisos, l_msg, l_tprecebimento, " +
                    "            l_formaenvio, l_dtacordo, l_ultrecto, l_nrecatz)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?);";
        } else {
            sql = "UPDATE locatarios" +
                    "   SET l_id=?, l_rgprp=?, l_rgimv=?, l_contrato=?, l_tipoimovel=?, l_fisjur=?, " +
                    "       l_cpfcnpj=?, l_rginsc=?, l_f_nome=?, l_f_sexo=?, l_f_dtnasc=?, " +
                    "       l_f_nacionalidade=?, l_f_estcivil=?, l_f_tel=?, l_f_mae=?, l_f_pai=?, " +
                    "       l_f_empresa=?, l_f_dtadmissao=?, l_f_endereco=?, l_f_numero=?, " +
                    "       l_f_cplto=?, l_f_bairro=?, l_f_cidade=?, l_f_estado=?, l_f_cep=?, " +
                    "       l_f_cargo=?, l_f_salario=?, l_f_conjugue=?, l_f_conjuguedtnasc=?, " +
                    "       l_f_conjuguesexo=?, l_f_conjuguerg=?, l_f_conjuguecpf=?, l_f_conjuguesalario=?, " +
                    "       l_f_conjugueempresa=?, l_f_conjuguetelefone=?, l_f_email=?, l_j_razao=?, " +
                    "       l_j_fantasia=?, l_j_endereco=?, l_j_numero=?, l_j_cplto=?, l_j_bairro=?, " +
                    "       l_j_cidade=?, l_j_estado=?, l_j_cep=?, l_j_dtctrosocial=?, l_j_tel=?, " +
                    "       l_j_email=?, l_historico=?, " +
                    "       l_avisos=?, l_msg=?, l_tprecebimento=?, l_formaenvio=?, l_dtacordo=?, l_ultrecto=?," +
                    " l_nrecatz=? WHERE l_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1;
            if (!bNew) pstmt.setInt(nid++, Id);
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
            pstmt.setString(nid++, l_historico.getText());
            pstmt.setString(nid++, l_avisos.getText());

            pstmt.setString(nid++, l_msg.getText());
            pstmt.setString(nid++, l_tprecebimento.getSelectionModel().getSelectedItem().toString().substring(0,3).toUpperCase());
            pstmt.setString(nid++, String.valueOf(l_formaenvio.getSelectionModel().getSelectedIndex()));
            pstmt.setDate(nid++, Dates.toSqlDate(acp_dtacordo));
            pstmt.setBoolean(nid++, acp_ultvectos.isSelected() ? true : false);
            
            int natz = 0; try { natz = (int)acp_nvectosatrz.getValue(); } catch (Exception e) {}
            pstmt.setInt(nid++, natz);

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        return retorno;
    }

    private void verifyBotoes() {
        if (this.viea == null) return;

        //if (!this.viea.contains("V")) l_btCarteira.setDisable(true);
        if (!this.viea.contains("V")) l_btPagamentos.setDisable(false);
        if (!this.viea.contains("I")) l_btIncluir.setDisable(true);
        if (!this.viea.contains("E")) l_btExcluir.setDisable(true);
        if (!this.viea.contains("A")) l_btGravar.setDisable(true);
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
        internalFrame.setClosable(nome.toUpperCase().trim().equals("CARTEIRA"));

        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.fireEvent(new vieaEvent("VIEA", vieaEvent.GET_VIEA));

        if (nome.toUpperCase().trim().equals("FIADORES")) {
            pfiadoresModel pfiadores = l_fiadores.getSelectionModel().getSelectedItem();
            if (pfiadores != null) {
                root.fireEvent(new paramEvent(new Object[]{pfiadores.getId(), pfiadores.getRgprp(), pfiadores.getRgimv(), pfiadores.getContrato(), pfiadores.getCnpj()}, paramEvent.GET_PARAM));
            } else {
                root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
            }
        } else if (nome.toUpperCase().trim().equals("SÓCIOS")) {
            psociosModel psocios = l_j_socios.getSelectionModel().getSelectedItem();
            if (psocios != null) {
                root.fireEvent(new paramEvent(new Object[]{psocios.getId(), psocios.getRgprp(), psocios.getRgimv(), psocios.getContrato(), psocios.getCnpj()}, paramEvent.GET_PARAM));
            } else {
                root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
            }
        } else if (nome.toUpperCase().trim().equals("RECEBIMENTOS")) {
            internalFrame.setClosable(true);
            root.fireEvent(new paramEvent(new Object[]{l_contrato.getText(), l_rgimv.getText(), l_fisica.isSelected() ? l_f_nome.getText() : l_j_razao.getText()}, paramEvent.GET_PARAM));
        } else if (nome.toUpperCase().trim().equals("ADCLOCATARIOS")) {
            int pID = -1;
            if (l_adicionais.getSelectionModel().getSelectedItem() != null) pID = l_adicionais.getSelectionModel().getSelectedItem().getId();
            root.fireEvent(new paramEvent(new Object[]{pID, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), this.pID}, paramEvent.GET_PARAM));
        } else {
            root.fireEvent(new paramEvent(new Object[]{-1, l_rgprp.getText(), l_rgimv.getText(), l_contrato.getText(), null}, paramEvent.GET_PARAM));
        }

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            try {internalFrame.close();} catch (NullPointerException e) {}
            LerFiadores(l_contrato.getText());
        });
    }

    private Object[] FiltraVencimentos(String contrato) {
        Object[] venctos = {};

        String sql = "SELECT dtvencimento FROM movimento WHERE dtrecebimento Is Null AND contrato = '%s' ORDER BY dtvencimento;";
        sql = String.format(sql, contrato);
        ResultSet resultSet = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                Date vcto = null;
                try { vcto = resultSet.getDate("dtvencimento"); } catch (SQLException e) {}
                if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
            }
        } catch (Exception e) {}
        try {
            DbMain.FecharTabela(resultSet);} catch (Exception e) {}

/*
        sql = "SELECT dtvencimento FROM taxas WHERE dtrecebimento Is Null AND contrato = '%s';";
        sql = String.format(sql, contrato);
        resultSet = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                Date vcto = null;
                try { vcto = resultSet.getDate("dtvencimento"); } catch (SQLException e) {}
                if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
            }
        } catch (Exception e) {}
        try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}

        sql = "SELECT dtvencimento FROM seguros WHERE dtrecebimento Is Null AND contrato = '%s';";
        sql = String.format(sql, contrato);
        resultSet = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                Date vcto = null;
                try { vcto = resultSet.getDate("dtvencimento"); } catch (SQLException e) {}
                if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
            }
        } catch (Exception e) {}
        try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}
*/

        return FuncoesGlobais.ObjectsRemoveDup(venctos);
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
