package Proprietarios;

import Calculos.Multas;
import Classes.*;
import Funcoes.*;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import masktextfield.MaskTextField;

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
import javax.swing.JOptionPane;
import samic.serversamic.Consulta;
import samic.serversamic.SamicServer;
import samic.serversamic.SamicServerImplService;

public class ProprietarioController implements Initializable {
    static private String MDIid;
    DbMain conn = VariaveisGlobais.conexao;
    String pSql = "SELECT * FROM proprietarios WHERE exclusao is null ORDER BY p_rgprp;";
    ResultSet rs = null;
    String viea = null;
    boolean bInc = false;
    int pID = -1;
    String oldTipo = null;
    Object[] fields;
    Object[] buttons;

    @FXML private AnchorPane tlaprop;
    @FXML private TabPane p_tabPane;

    @FXML private MaskTextField p_rgprp;
    @FXML private ChoiceBox<String> p_tipoprop;
    @FXML private TextField p_nome;
    @FXML private TextField p_endereco;
    @FXML private Button p_btn_endereco;
    @FXML private TextField p_numero;
    @FXML private TextField p_complemento;
    @FXML private TextField p_bairro;
    @FXML private TextField p_cidade;
    @FXML private TextField p_estado;
    @FXML private MaskTextField p_cep;
    @FXML private MenuButton p_btn_telcontato;
    @FXML private ComboBox<ptelcontatoModel> p_telcontato;
    @FXML private MenuItem adc_telcontato;
    @FXML private MenuItem del_telcontato;
    @FXML private TextField p_profissao;
    @FXML private TextField p_representante;
    @FXML private DatePicker p_repdtnasc;
    @FXML private TextField p_nacionalidade;
    @FXML private ComboBox<String> p_estcivil;
    @FXML private ComboBox<String> p_sexo;
    @FXML private DatePicker p_dtnasc;
    @FXML private TextField p_insc_rg;
    @FXML private TextField p_cpf_cnpj;
    @FXML private TextField p_conjugue;
    @FXML private DatePicker p_conjdtnasc;
    @FXML private MenuButton p_btn_banco;
    @FXML private ComboBox<pbancosModel> p_banco;
    @FXML private MenuItem adcBancos;
    @FXML private MenuItem delBancos;
    @FXML private MenuButton p_btn_email;
    @FXML private MenuItem adc_btn_email;
    @FXML private MenuItem del_btn_email;
    @FXML private ComboBox<pemailModel> p_email;

    // Mensagens
    @FXML private Tab mensagens;
    @FXML private DatePicker p_msg_dtcadastro;
    @FXML private TextField p_msg_msgboleto;
    @FXML private TextField p_msg_msgrecibo;
    @FXML private TextField p_msg_avisocaixa;

    // correspondencias
    @FXML private Tab correspondencias;
    @FXML private TextField p_cor_endereco;
    @FXML private Button p_cor_btn_endereco;
    @FXML private TextField p_cor_numero;
    @FXML private TextField p_cor_complemento;
    @FXML private TextField p_cor_bairro;
    @FXML private TextField p_cor_cidade;
    @FXML private TextField p_cor_estado;
    @FXML private MaskTextField p_cor_cep;

    // Imoveis
    @FXML private TableView<pimoveisModel> p_imoveis;
    @FXML private TableColumn<pimoveisModel, Boolean> imv_fusao;
    @FXML private TableColumn<pimoveisModel, String> imv_rgprp;
    @FXML private TableColumn<pimoveisModel, String> imv_rgimv;
    @FXML private TableColumn<pimoveisModel, String> imv_tipo;
    @FXML private TableColumn<pimoveisModel, String> imv_endereco;
    @FXML private TableColumn<pimoveisModel, String> imv_situacao;
    @FXML private TableColumn<pimoveisModel, Boolean> imv_div;

    @FXML private Button p_btn_incluir;
    @FXML private Button p_btn_alterar;
    @FXML private Button p_btn_excluir;
    @FXML private Button p_btn_tras;
    @FXML private Button p_btn_frente;
    @FXML private Button p_btn_irpara;
    @FXML private Button p_btn_gravar;
    @FXML private Button p_btn_sair;

    @FXML
    void btIncluir(ActionEvent event) {
        // Tela de Pesquisa
        try {
            ChamaTela("Pesquisa", "/Pesquisa/Pesquisa.fxml", "loca.png");
            //new pesquisaSrv().pesquisaSrv();
        } catch (Exception ex) {}

        this.bInc = true;
        LimpaTela();

        p_msg_dtcadastro.setValue(Dates.toLocalDate(DbMain.getDateTimeServer()));

        new Controle("PROPRIETARIO: NOVO - ", this.buttons).BotaoEnabled(new Object[] {p_btn_gravar, p_btn_sair});
        new Controle("PROPRIETARIO: NOVO - ", this.fields).FieldsEnabled(true);
        
        p_nome.requestFocus();
    }

    @FXML
    void btAlterar(ActionEvent event) {
        this.bInc = false;
        p_msg_dtcadastro.setValue(Dates.toLocalDate(DbMain.getDateTimeServer()));

        new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {p_btn_gravar, p_btn_sair});
        new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(true);
        p_nome.requestFocus();
    }

    @FXML
    void btExcluir(ActionEvent event) {
        // Checar se proprietário tem saldo
        Object[][] value = sdExtrato(p_rgprp.getText());        
        if (((BigDecimal)value[0][2]).subtract((BigDecimal) value[0][3]).floatValue() != 0) {
            JOptionPane.showMessageDialog(null, "Proprietário com saldo!");
            return;
        }
        
        Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este proprietário?", new ButtonType("Sim"), new ButtonType("Não"));
        Optional<ButtonType> result = msg.showAndWait();
        if (result.get().getText().equals("Sim")) {
            conn.Auditor("PROPRIETARIO:EXCLUSAO",p_rgprp.getText() + " NomeRazao: " + p_nome.getText());
            
            String sql = "UPDATE proprietarios SET exclusao = '%s' WHERE p_rgprp = '%s';";
            sql = String.format(sql, Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()), p_rgprp.getText());
            try { conn.ExecutarComando(sql); } catch (Exception e) { }
            
            sql = "UPDATE imoveis SET exclusao = '%s' WHERE i_rgprp = '%s';";
            sql = String.format(sql, Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()), p_rgprp.getText());
            try { conn.ExecutarComando(sql); } catch (Exception e) { }

            sql = "UPDATE locatarios SET exclusao = '%s' WHERE l_rgprp = '%s';";
            sql = String.format(sql, Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()), p_rgprp.getText());
            try { conn.ExecutarComando(sql); } catch (Exception e) { }               
        }        
    }

    @FXML
    void btTras(ActionEvent event) {
        try {
            rs.previous(); 
            if (rs.isBeforeFirst()) {
                rs.next();
            }
            LerProp();
        } catch (SQLException e) {}
    }

    @FXML
    void btFrente(ActionEvent event) {
        try {
            rs.next(); 
            if (rs.isAfterLast()) {
                rs.previous();
            }
            LerProp();
        } catch (SQLException e) {}
    }

    @FXML
    void btIrPara(ActionEvent event) {
        p_rgprp.setEditable(true);
        p_rgprp.requestFocus();
    }

    @FXML
    void btGravar(ActionEvent event) {
        Object[] camposobrig = new Object[] {p_endereco, p_bairro, p_cidade, p_estado, p_cep, p_estcivil, p_sexo, p_email};
        if (!new Controle("PROPRIETARIO: NOVO - ", camposobrig).ChecaVazio()) {
            return;
        }

        String wRgimg = "";
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            if (bInc) {
                int iNewRgPrp = 0;
                int NewRgPrp = 0;
                try {
                    NewRgPrp = Integer.parseInt(conn.LerParametros("PROP"));
                } catch (SQLException ex) {}
                int PropInc = 0;
                try {
                    PropInc = Integer.parseInt(conn.LerParametros("PROPINC"));
                } catch (SQLException ex) {}
                iNewRgPrp = NewRgPrp + PropInc;
                wRgimg = String.valueOf(iNewRgPrp);

                String cPar[] = {"PROP",String.valueOf(iNewRgPrp),"NUMERICO"};
                try {
                    conn.GravarParametros(cPar);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
                salvar(bInc, iNewRgPrp);
                p_rgprp.setText(String.valueOf(iNewRgPrp));
            } else salvar(bInc, this.pID);

            // Atualiza
            try {
                int pos = rs.getRow();
                DbMain.FecharTabela(rs);

                rs = conn.AbrirTabela(pSql, ResultSet.CONCUR_UPDATABLE);
                rs.absolute(pos);
                if (DbMain.RecordCount(rs) <= 0) {
                    new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {p_btn_incluir, p_btn_sair});
                } else {
                    new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoDisabled(new Object[] {p_btn_gravar});
                }
            } catch (SQLException e) {}

            verifyBotoes();
            bInc = false;

            new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(false);
            p_nome.setDisable(false);
            p_cpf_cnpj.setDisable(false);
        }
        populateImoveis(p_rgprp.getText());
    }

    @FXML
    void btSair(ActionEvent event) {
        if (bInc) {
            Alert msg = new Alert(AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) return;
        }

        if (!p_btn_gravar.isDisabled()) {
            LerProp();
            bInc = false;
            new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoDisabled(new Object[]{p_btn_gravar});
            new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(false);
            
            if (Integer.valueOf(p_rgprp.getText()) == 0) {
                new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {p_btn_incluir, p_btn_sair});
                new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(false);
            }                
            
            p_nome.setDisable(false);
            p_cpf_cnpj.setDisable(false);
            return;
        }

        try {tlaprop.fireEvent(new paramEvent(new String[] {"Proprietario"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
    }

    @FXML void adcBancos_OnAction(ActionEvent event) {
        Classes.adcBancos dialog = new adcBancos();
        Optional<pbancosModel> result = dialog.adcBancos();
        result.ifPresent(b -> {
            ObservableList<pbancosModel> ebancos = p_banco.getItems();
            ebancos.addAll(b);
            p_banco.setItems(ebancos);
            try {p_banco.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void delBancos_OnAction(ActionEvent event) {
        if (!p_banco.getItems().isEmpty()) p_banco.getItems().removeAll(p_banco.getSelectionModel().getSelectedItem());
        try {p_banco.getSelectionModel().select(0);} catch (Exception e) {}
    }
    
    @FXML void adc_telcontato_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = p_telcontato.getItems();
            tels.addAll(b);
            p_telcontato.setItems(tels);
            try {p_telcontato.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void del_telcontato_OnAction(ActionEvent event) {
        if (!p_telcontato.getItems().isEmpty()) p_telcontato.getItems().removeAll(p_telcontato.getSelectionModel().getSelectedItem());
        try {p_telcontato.getSelectionModel().select(0);} catch (Exception e) {}
    }    
    
    @FXML void adc_btn_email_OnAction(ActionEvent event) {
        adcEmails dialog = new adcEmails();
        Optional<pemailModel> result = dialog.adcEmails(false);
        result.ifPresent(b -> {
            ObservableList<pemailModel> emails = p_email.getItems();

            emails.addAll(new pemailModel(b.getEmail())); //, b.getSenha()));
            p_email.setItems(emails);
            try {p_email.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void del_btn_email_onAction(ActionEvent event) {
        if (!p_email.getItems().isEmpty()) p_email.getItems().removeAll(p_email.getSelectionModel().getSelectedItem());
        try {p_email.getSelectionModel().select(0);} catch (Exception e) {}
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
        p_tabPane.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        tlaprop.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
            }
        });
        tlaprop.addEventHandler(vieaEvent.GET_VIEA, event -> { this.viea = event.sviea; });
        tlaprop.addEventHandler(paramEvent.GET_PARAM, event -> {
            populateImoveis(p_rgprp.getText());
        });
        
        // Tipo de propriet�rio
        p_tipoprop.getItems().addAll("ÚNICO","DIVIDIDO","ESPÓLIO");

        p_rgprp.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus

                    //Task task = new Task() {
                    //            @Override
                    //            protected Integer call() throws Exception {
                    //                p_rgprp.setEditable(false);
                    //                p_rgprp.setCursor(Cursor.WAIT); //Change cursor to wait style
                    //                MoveToProp("p_rgprp", p_rgprp.getText().trim());
                    //                p_rgprp.setCursor(Cursor.DEFAULT); //Change cursor to default style
                    //                return 0;
                    //            }
                    //};
                    //Thread th = new Thread(task);
                    //th.setDaemon(true);
                    //th.start();

                    Platform.runLater(() -> {
                        if (p_rgprp.getText() != null) {
                            p_rgprp.setEditable(false);
                            p_rgprp.setCursor(Cursor.WAIT); //Change cursor to wait style
                            MoveToProp("p_rgprp", p_rgprp.getText().trim());
                            p_rgprp.setCursor(Cursor.DEFAULT); //Change cursor to default style
                        }
                    });

                    }
            }
        });
        
        p_cpf_cnpj.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                p_cpf_cnpj.setText(FuncoesGlobais.LimpaCpfCnpj(p_cpf_cnpj.getText()));
            } else {
                // out focus
                p_cpf_cnpj.setText(FuncoesGlobais.FormatCpfCnpj(p_cpf_cnpj.getText()));
            }
        });

        new cpoTelefones().cpoTelefones(p_telcontato);
        new cpoEmails().cpoEmails(p_email);
        new cpoBancos().cpoBancos(p_banco);
        
        p_btn_endereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                p_endereco.setText(b.getLogradouro());
                p_bairro.setText(b.getBairro());
                p_cidade.setText(b.getLocalidade());
                p_estado.setText(b.getUf());
                p_cep.setText(b.getCep());
                p_numero.setText(null); p_complemento.setText(null);
                p_numero.requestFocus();
            });
        });
        
        p_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if ((p_endereco.getText() == null || p_endereco.getText().equalsIgnoreCase("")) && p_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(p_cep.getText());
                            if (endereco != null) {
                                p_endereco.setText(endereco.getLogradouro());
                                p_bairro.setText(endereco.getBairro());
                                p_cidade.setText(endereco.getLocalidade());
                                p_estado.setText(endereco.getUf());

                                p_numero.setText(null);
                                p_complemento.setText(null);
                                p_numero.requestFocus();
                            } else {
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle("Menssagem");
                                alert.setHeaderText("Cep não encontrado!");
                                alert.setContentText("Entre com um Cep válido!!!");
                                alert.showAndWait();
                                p_cep.requestFocus();
                            }
                        } catch (IOException ex) { }
                    }
                }
            }
        });
        
        p_cor_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                } else {
                    // out focus
                    if ((p_cor_endereco.getText() == null || p_cor_endereco.getText().equalsIgnoreCase("")) && p_cor_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(p_cor_cep.getText());
                            if (endereco != null) {
                                p_cor_endereco.setText(endereco.getLogradouro());
                                p_cor_bairro.setText(endereco.getBairro());
                                p_cor_cidade.setText(endereco.getLocalidade());
                                p_cor_estado.setText(endereco.getUf());

                                p_cor_numero.setText(null);
                                p_cor_complemento.setText(null);
                                p_cor_numero.requestFocus();
                            } else {
                                Alert alert = new Alert(AlertType.WARNING);
                                alert.setTitle("Menssagem");
                                alert.setHeaderText("Cep náo encontrado!");
                                alert.setContentText("Entre com um Cep válido!!!");
                                alert.showAndWait();
                                p_cor_cep.requestFocus();
                            }
                        } catch (IOException ex) { }
                    }
                }
            }
        });
        
        p_cor_btn_endereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                p_cor_endereco.setText(b.getLogradouro());
                p_cor_bairro.setText(b.getBairro());
                p_cor_cidade.setText(b.getLocalidade());
                p_cor_estado.setText(b.getUf());
                p_cor_cep.setText(b.getCep());
                p_cor_numero.setText(null); p_cor_complemento.setText(null);
                p_cor_numero.requestFocus();
            });
        });

        p_imoveis.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        p_imoveis.getSelectionModel().setCellSelectionEnabled(false);
        p_imoveis.setOnMousePressed(event -> {
            TableView.TableViewSelectionModel<pimoveisModel> select = p_imoveis.getSelectionModel();
            if (!select.getSelectedItems().isEmpty() && select.getSelectedItems().size() > 1) {
                String tfusao = "";
                try {tfusao = select.getSelectedItems().get(0).getFusao();} catch (Exception e) {}
                String tend = select.getSelectedItems().get(0).getEnder();
                int pos = tend.indexOf(",");
                pos = tend.indexOf(",", pos + 1);
                tend = tend.substring(0, pos);
                boolean isDifer = false;
                List<Integer> posDifer = new ArrayList<Integer>();
                int size = select.getSelectedItems().size();
                for (int i = 0; i < size; i++) {
                    try {
                        String gend = select.getSelectedItems().get(i).getEnder();
                        int rpos = gend.indexOf(",");
                        rpos = gend.indexOf(",", rpos + 1);
                        gend = gend.substring(0, rpos);

                        if (!tend.equalsIgnoreCase(gend)) {
                            isDifer = true;
                            posDifer.add(i);
                        }
                    } catch (NullPointerException e) {}
                }
                if (isDifer) {
                    for (int i : posDifer) {
                        //System.out.println("(" + i + ") - " + select.getSelectedItems().get(i).getEnder());
                        select.clearSelection(i);
                    }
                }
            }

            if (event.isSecondaryButtonDown() && event.getClickCount() == 1) {
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem removeMenuItem = new MenuItem("Remove");
                //removeMenuItem.setOnAction(event1 -> p_imoveis.getItems().remove(row.getItem()));
                contextMenu.getItems().add(removeMenuItem);
                p_imoveis.contextMenuProperty().bind(
                        Bindings.when(p_imoveis.getSelectionModel().getSelectedItem().fusaoProperty().isNotEmpty())
                                .then((ContextMenu)null)
                                .otherwise(contextMenu)
                );
            }

            // Mostra tela do imovel
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                try {
                    ChamaTela("Imóveis", "/Imoveis/Imovel.fxml", "loca.png");
                } catch (Exception ex) {}
            }
        });

        p_sexo.setItems(new pSexo().Sexo());
        p_estcivil.setItems(new pEstCivil().EstCivil());

        LimpaTela();
        OpenProprietarios();

        this.fields = new Object[] {p_tipoprop, p_nome, p_endereco, p_btn_endereco, p_numero, p_complemento,
                p_bairro, p_cidade, p_estado,p_cep, p_btn_telcontato, p_telcontato, p_profissao, p_nacionalidade, p_estcivil, p_sexo,
                p_dtnasc, p_insc_rg, p_cpf_cnpj, p_representante, p_repdtnasc, p_conjugue, p_conjdtnasc, p_btn_banco, p_banco,
                p_btn_email, p_email, p_msg_dtcadastro, p_msg_msgboleto, p_msg_msgrecibo, p_msg_avisocaixa, p_cor_endereco,
                p_cor_btn_endereco, p_cor_numero, p_cor_complemento, p_cor_bairro, p_cor_cidade, p_cor_estado, p_cor_cep,
                p_imoveis
        };
        new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).Focus();
        new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(false);

        this.buttons = new Object[] {p_btn_incluir, p_btn_alterar, p_btn_excluir, p_btn_tras, p_btn_frente,
                p_btn_irpara, p_btn_gravar, p_btn_sair
        };

        //Platform.runLater(() -> 
        verifyBotoes(); //);
        if (DbMain.RecordCount(rs) <= 0) {
            new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {p_btn_incluir, p_btn_sair});
        } else {
            new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoDisabled(new Object[] {p_btn_gravar});
        }
        boolean temInternet = VariaveisGlobais.bInternet;
        Platform.runLater(() -> p_btn_incluir.setDisable(!temInternet) );
    }
    
    private void OpenProprietarios() {
        rs = conn.AbrirTabela(this.pSql, ResultSet.CONCUR_UPDATABLE);
        try {
            rs.next();
            LerProp();
        } catch (SQLException e) {        }
    }
    
    public boolean MoveToProp(String campo, String seek) {
        boolean achei = false;
        try {
            int nrow = rs.getRow();
            try {
                rs.beforeFirst();
            } catch (SQLException e) {
            }
            while (rs.next()) {
                if (rs.getInt(campo) == Integer.parseInt(seek)) {
                    achei = true;
                    break;
                }
            }
            if (!achei) {
                rs.first();
                rs.absolute(nrow);
            }
        } catch (Exception es) {}
        LerProp();
        return achei;
    }
    
    private void LimpaTela() {
        this.pID = -1;
        p_rgprp.setText(null);
        p_tipoprop.getSelectionModel().select(0);
        MaskFieldUtil.maxField(p_nome, 60); p_nome.setText(null);
        MaskFieldUtil.maxField(p_endereco, 60); p_endereco.setText(null);
        MaskFieldUtil.maxField(p_numero, 10); p_numero.setText(null);
        MaskFieldUtil.maxField(p_complemento, 15); p_complemento.setText(null);
        MaskFieldUtil.maxField(p_bairro, 25); p_bairro.setText(null);
        MaskFieldUtil.maxField(p_cidade, 25); p_cidade.setText(null);
        MaskFieldUtil.maxField(p_estado, 2); p_estado.setText(null);
        p_cep.setText(null);
        p_telcontato.getItems().clear();
        MaskFieldUtil.maxField(p_profissao, 30); p_profissao.setText(null);
        MaskFieldUtil.maxField(p_representante, 60); p_representante.setText(null);
        p_repdtnasc.setValue(null);
        MaskFieldUtil.maxField(p_nacionalidade, 30); p_nacionalidade.setText(null);
        p_estcivil.getSelectionModel().select(-1);
        p_sexo.getSelectionModel().select(-1);
        p_dtnasc.setValue(null);
        MaskFieldUtil.maxField(p_insc_rg,30); p_insc_rg.setText(null);
        MaskFieldUtil.cpfCnpjField(p_cpf_cnpj); p_cpf_cnpj.setText(null);
        MaskFieldUtil.maxField(p_conjugue,60); p_conjugue.setText(null);
        p_conjdtnasc.setValue(null);
        p_banco.getItems().clear();
        p_email.getItems().clear();      
        
        // Aba Menssagens
        p_msg_dtcadastro.setValue(null);
        MaskFieldUtil.maxField(p_msg_msgboleto,100); p_msg_msgboleto.setText(null);
        MaskFieldUtil.maxField(p_msg_msgrecibo, 100); p_msg_msgrecibo.setText(null);
        MaskFieldUtil.maxField(p_msg_avisocaixa, 100); p_msg_avisocaixa.setText(null);
        
        // Aba Correspond�ncias
        MaskFieldUtil.maxField(p_cor_endereco, 60); p_cor_endereco.setText(null);
        MaskFieldUtil.maxField(p_cor_numero, 10); p_cor_numero.setText(null);
        MaskFieldUtil.maxField(p_cor_complemento, 15); p_cor_complemento.setText(null);
        MaskFieldUtil.maxField(p_cor_bairro, 25); p_cor_bairro.setText(null);
        MaskFieldUtil.maxField(p_cor_cidade, 25); p_cor_cidade.setText(null);
        MaskFieldUtil.maxField(p_cor_estado, 2); p_cor_estado.setText(null);
        p_cor_cep.setText(null);

        p_imoveis.getItems().clear();
    }
    
    private void LerProp() {
        try {this.pID = rs.getInt("p_id");} catch (SQLException e) {this.pID = -1;}
        try {p_rgprp.setEditable(false);p_rgprp.setText(rs.getString("p_rgprp"));} catch (SQLException e) {p_rgprp.setText(null);}
        try {p_tipoprop.getSelectionModel().select(rs.getString("tipoprop")); oldTipo = p_tipoprop.getSelectionModel().getSelectedItem();} catch (SQLException e) {p_tipoprop.getSelectionModel().select(0);}
        try { MaskFieldUtil.maxField(p_nome, 60); MaskFieldUtil.ignoreKeys(p_nome); p_nome.setText(rs.getString("p_nome"));} catch (SQLException e) {p_nome.setText(null);}
        try { MaskFieldUtil.maxField(p_endereco, 60); p_endereco.setText(rs.getString("p_end"));} catch (SQLException e) {p_endereco.setText(null);}
        try { MaskFieldUtil.maxField(p_numero, 10); p_numero.setText(rs.getString("p_num"));} catch (SQLException e) {p_numero.setText(null);}
        try { MaskFieldUtil.maxField(p_complemento, 15); p_complemento.setText(rs.getString("p_compl"));} catch (SQLException e) {p_complemento.setText(null);}
        try { MaskFieldUtil.maxField(p_bairro, 25); p_bairro.setText(rs.getString("p_bairro"));} catch (SQLException e) {p_bairro.setText(null);}
        try { MaskFieldUtil.maxField(p_cidade, 25); p_cidade.setText(rs.getString("p_cidade"));} catch (SQLException e) {p_cidade.setText(null);}
        try { MaskFieldUtil.maxField(p_estado, 2); p_estado.setText(rs.getString("p_estado"));} catch (SQLException e) {p_estado.setText(null);}
        try {p_cep.setText(rs.getString("p_cep"));} catch (SQLException e) {p_cep.setText(null);}

        // Telefone de contato
        List<ptelcontatoModel> data = null;
        try {data = new setTels(rs.getString("p_tel")).rString();} catch (SQLException e) {}
        p_telcontato.getItems().clear();
        if (data != null) p_telcontato.setItems(observableArrayList(data)); else p_telcontato.getItems().clear();
        p_telcontato.setDisable(false);
        try {p_telcontato.getSelectionModel().select(0); p_telcontato.getEditor().setText(data.get(0).toString());} catch (Exception e) {}
        
        try { MaskFieldUtil.maxField(p_profissao,30); p_profissao.setText(rs.getString("p_profissao"));} catch (SQLException e) {p_profissao.setText(null);}
        try { MaskFieldUtil.maxField(p_representante, 60); p_representante.setText(rs.getString("p_representante"));} catch (SQLException e) {p_representante.setText(null);}

        try {p_repdtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("p_repdtnasc")));} catch (Exception e) {p_repdtnasc.getEditor().clear();}
        try {p_repdtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("p_repdtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {p_repdtnasc.setValue(null);}

        try {p_nacionalidade.setText(rs.getString("p_nacionalidade"));} catch (SQLException e) {p_nacionalidade.setText(null);}
        try {p_estcivil.getSelectionModel().select(rs.getString("p_estcivil"));} catch (SQLException e) {p_estcivil.getSelectionModel().select(0);}
        try {p_sexo.getSelectionModel().select(rs.getString("p_sexo"));} catch (SQLException e) {p_sexo.getSelectionModel().select(0);}

        try {p_dtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("p_dtnasc")));} catch (Exception e) {p_dtnasc.getEditor().clear();}
        try {p_dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("p_dtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {p_dtnasc.setValue(null);}

        try {p_insc_rg.setText(rs.getString("p_rginsc"));} catch (SQLException e) {p_insc_rg.setText(null);}
        try { MaskFieldUtil.cpfCnpjField(p_cpf_cnpj); p_cpf_cnpj.setText(rs.getString("p_cpfcnpj"));} catch (SQLException e) {p_cpf_cnpj.setText(null);}
        
        try { MaskFieldUtil.maxField(p_conjugue,60); p_conjugue.setText(rs.getString("p_conjugue"));} catch (SQLException e) {p_conjugue.setText(null);}
        try {p_conjdtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("p_conjdtnasc")));} catch (Exception e) {p_conjdtnasc.getEditor().clear();}
        try {p_conjdtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("p_conjdtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {p_conjdtnasc.setValue(null);}
        
        //bancos
        List<pbancosModel> databancos = null;
        try {databancos = new setBancos(rs.getString("p_bancos")).rString();} catch (SQLException e) {}
        if (databancos != null) p_banco.setItems(observableArrayList(databancos)); else p_banco.getItems().clear();
        p_banco.setDisable(false);
        try {p_banco.getSelectionModel().select(0);} catch (Exception e) {}
        
        //email;
        List<pemailModel> dataemail = null;
        String emailp = null;
        try { emailp = rs.getString("p_email"); } catch (SQLException e) {}
        if (emailp != null) {dataemail = new setEmails(emailp, true).rString();}
        p_email.getItems().clear();
        if (dataemail != null) p_email.setItems(observableArrayList(dataemail)); else p_email.getItems().clear();
        p_email.setDisable(false);
        try {p_email.getSelectionModel().select(0);} catch (Exception e) {}
        
        // Aba mensagens
        try {p_msg_dtcadastro.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("p_dtcadastro")));} catch (Exception e) {p_msg_dtcadastro.getEditor().clear();}
        try {p_msg_dtcadastro.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("p_dtcadastro").toString(), "yyyy-MM-dd")));} catch (Exception e) {p_msg_dtcadastro.setValue(null);}

        try { MaskFieldUtil.maxField(p_msg_msgboleto,30); p_msg_msgboleto.setText(rs.getString("p_msgboleto"));} catch (SQLException e) {p_msg_msgboleto.setText(null);}
        try { MaskFieldUtil.maxField(p_msg_msgrecibo,30); p_msg_msgrecibo.setText(rs.getString("p_msgrecibo"));} catch (SQLException e) {p_msg_msgrecibo.setText(null);}
        try { MaskFieldUtil.maxField(p_msg_avisocaixa,30); p_msg_avisocaixa.setText(rs.getString("p_msgcaixa"));} catch (SQLException e) {p_msg_avisocaixa.setText(null);}
        
        // Aba Correspond�ncia
        try { MaskFieldUtil.maxField(p_cor_endereco, 60); p_cor_endereco.setText(rs.getString("p_cor_end"));} catch (SQLException e) {p_cor_endereco.setText(null);}
        try { MaskFieldUtil.maxField(p_cor_numero, 10); p_cor_numero.setText(rs.getString("p_cor_num"));} catch (SQLException e) {p_cor_numero.setText(null);}
        try { MaskFieldUtil.maxField(p_cor_complemento, 15); p_cor_complemento.setText(rs.getString("p_cor_compl"));} catch (SQLException e) {p_cor_complemento.setText(null);}
        try { MaskFieldUtil.maxField(p_cor_bairro, 25); p_cor_bairro.setText(rs.getString("p_cor_bairro"));} catch (SQLException e) {p_cor_bairro.setText(null);}
        try { MaskFieldUtil.maxField(p_cor_cidade, 25); p_cor_cidade.setText(rs.getString("p_cor_cidade"));} catch (SQLException e) {p_cor_cidade.setText(null);}
        try { MaskFieldUtil.maxField(p_cor_estado, 2); p_cor_estado.setText(rs.getString("p_cor_estado"));} catch (SQLException e) {p_cor_estado.setText(null);}
        try {p_cor_cep.setText(rs.getString("p_cor_cep"));} catch (SQLException e) {p_cor_cep.setText(null);}

        populateImoveis(p_rgprp.getText());
    }

    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO proprietarios(" +
                    "p_rgprp, p_nome, p_fisjur, p_end, p_num, p_compl, p_bairro, " +
                    "p_cidade, p_estado, p_cep, p_tel, p_representante, p_repdtnasc, " +
                    "p_cpfcnpj, p_email, p_profissao, p_nacionalidade, p_estcivil, " +
                    "p_sexo, p_dtnasc, p_rginsc, p_conjugue, p_conjdtnasc, p_bancos, " +
                    "p_dtcadastro, p_msgboleto, p_msgrecibo, p_msgcaixa, p_cor_end, " +
                    "p_cor_num, p_cor_compl, p_cor_bairro, p_cor_cidade, p_cor_estado, " +
                    "p_cor_cep, tipoprop) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?);";
        } else {
            sql = "UPDATE proprietarios" +
                    " SET p_nome=?, p_fisjur=?, p_end=?, p_num=?, p_compl=?, " +
                    "p_bairro=?, p_cidade=?, p_estado=?, p_cep=?, p_tel=?, p_representante=?, " +
                    "p_repdtnasc=?, p_cpfcnpj=?, p_email=?, p_profissao=?, p_nacionalidade=?, " +
                    "p_estcivil=?, p_sexo=?, p_dtnasc=?, p_rginsc=?, p_conjugue=?, " +
                    "p_conjdtnasc=?, p_bancos=?, p_dtcadastro=?, p_msgboleto=?, p_msgrecibo=?, " +
                    "p_msgcaixa=?, p_cor_end=?, p_cor_num=?, p_cor_compl=?, p_cor_bairro=?, " +
                    "p_cor_cidade=?, p_cor_estado=?, p_cor_cep=?, tipoprop=?" +
                    " WHERE p_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1; 
            if (bNew) pstmt.setInt(nid++, Id);
            pstmt.setString(nid++, p_nome.getText());
            pstmt.setString(nid++, p_cpf_cnpj.getText().trim().length() <= 14 ? "F" : "J");
            pstmt.setString(nid++, p_endereco.getText());
            pstmt.setString(nid++, p_numero.getText());
            pstmt.setString(nid++, p_complemento.getText());
            pstmt.setString(nid++, p_bairro.getText());
            pstmt.setString(nid++, p_cidade.getText());
            pstmt.setString(nid++, p_estado.getText());
            pstmt.setString(nid++, p_cep.getText());
            pstmt.setString(nid++, new getTels(p_telcontato).toString());
            pstmt.setString(nid++, p_representante.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(p_repdtnasc));
            pstmt.setString(nid++, p_cpf_cnpj.getText());
            pstmt.setString(nid++, new getEmails(p_email,false).toString());
            pstmt.setString(nid++, p_profissao.getText());
            pstmt.setString(nid++, p_nacionalidade.getText());
            pstmt.setString(nid++, p_estcivil.getSelectionModel().getSelectedItem().toString());
            pstmt.setString(nid++, p_sexo.getSelectionModel().getSelectedItem().toString());
            pstmt.setDate(nid++, Dates.toSqlDate(p_dtnasc));
            pstmt.setString(nid++, p_insc_rg.getText());
            pstmt.setString(nid++, p_conjugue.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(p_conjdtnasc));
            pstmt.setString(nid++, new getBancos(p_banco).toString());

            // Aba mensagens
            pstmt.setDate(nid++, Dates.toSqlDate(p_msg_dtcadastro));
            pstmt.setString(nid++, p_msg_msgboleto.getText());
            pstmt.setString(nid++, p_msg_msgrecibo.getText());
            pstmt.setString(nid++, p_msg_avisocaixa.getText());
            
            // Aba Correspond�ncia
            pstmt.setString(nid++, p_cor_endereco.getText());
            pstmt.setString(nid++, p_cor_numero.getText());
            pstmt.setString(nid++, p_cor_complemento.getText());
            pstmt.setString(nid++, p_cor_bairro.getText());
            pstmt.setString(nid++, p_cor_cidade.getText());
            pstmt.setString(nid++, p_cor_estado.getText());
            pstmt.setString(nid++, p_cor_cep.getText());

            // tipoprop
            pstmt.setString(nid, p_tipoprop.getSelectionModel().getSelectedItem());

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        SamicServer ss = new SamicServerImplService().getSamicServerImplPort();
        Consulta dados = new Consulta();
        {
            dados.setCliente(VariaveisGlobais.cliente);
            dados.setEstacao(VariaveisGlobais.estacao);
            dados.setTipo("P");
            dados.setCpfcnpj(p_cpf_cnpj.getText());
            dados.setDatacadastro(p_msg_dtcadastro.getValue().toString());
            dados.setNomerazao(p_nome.getText());
            dados.setRginsc(p_insc_rg.getText());
            dados.setEndereco(p_endereco.getText());
            dados.setNumero(p_numero.getText());
            dados.setComplemento(p_complemento.getText());
            dados.setBairro(p_bairro.getText());
            dados.setCidade(p_cidade.getText());
            dados.setEstado(p_estado.getText());
            dados.setCep(p_cep.getText());
            dados.setTelefones(new getTels(p_telcontato).toString());
            dados.setEmails(new getEmails(p_email,false).toString());

            dados.setPositivo(false);
            dados.setObservacoes("");
        }
        boolean iOk = ss.inclusao(dados, (bNew ? "I" : "A"));

        return retorno;
    }
    
    private void verifyBotoes() {
        if (this.viea == null) return;
        if (this.viea.contains("I")) p_btn_incluir.setDisable(false); else p_btn_incluir.setDisable(true);
        if (this.viea.contains("E")) p_btn_excluir.setDisable(false); else p_btn_excluir.setDisable(true);
        if (this.viea.contains("A")) p_btn_alterar.setDisable(false); else p_btn_alterar.setDisable(true);
    }
    
    private void populateImoveis(String rgprp) {
        p_imoveis.getItems().clear();

        List<pimoveisModel> data = new ArrayList<pimoveisModel>();
        ResultSet imv;
        String qSQL = "SELECT i_rgprp, i_rgimv, i_tipo, coalesce(i_end,'') || ', ' || coalesce(i_num,'') || ', ' || coalesce(i_cplto,'') || ' - ' || coalesce(i_bairro,'') || ' - CEP: ' || coalesce(i_cep,'') AS i_ender, i_situacao, i_fusao FROM imoveis WHERE i_rgprp = '" + rgprp + "' ORDER BY i_rgimv;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qrgprp = null, qrgimv = null, qtipo = null, qender = null, qsitua = null;
                boolean qdiv = false; String qfusao = null; String qid = null;
                try {qrgprp = imv.getString("i_rgprp");} catch (SQLException e) {}
                try {qrgimv = imv.getString("i_rgimv");} catch (SQLException e) {}
                try {qtipo = imv.getString("i_tipo");} catch (SQLException e) {}
                try {qender = imv.getString("i_ender");} catch (SQLException e) {}
                try {qsitua = imv.getString("i_situacao");} catch (SQLException e) {}

                // Avalia conforme o tipoprop (NORMAL, ESPOLIO, DIVIDIDO)
                if (p_tipoprop.getSelectionModel().getSelectedItem().equalsIgnoreCase("�NICO")) {
                    // NORMAL - Avisar que o imovel n�o divide com ningu�m
                    qdiv = false;
                } else if (p_tipoprop.getSelectionModel().getSelectedItem().equalsIgnoreCase("DIVIDIDO")) {
                    // DIVIDIDO - AVisar que o im�vel � dividido com outro propriet�rio
                    qdiv = true; // checar no arquivo divis�o
                } else if (p_tipoprop.getSelectionModel().getSelectedItem().equalsIgnoreCase("ESP�LIO")) {
                    // ESP�LIO - Todos os im�veis tem de estar dividido.
                    qdiv = true; // checar no arquivo divis�o
                }
                try {qfusao = imv.getString("i_fusao");} catch (SQLException e) {}
                try {qid = imv.getString("i_id");} catch (SQLException e) {}

                data.add(new pimoveisModel(qid, qrgprp, qrgimv, qtipo, qender, qsitua, qdiv, qfusao));
            }
            imv.close();
        } catch (SQLException e) {}
        
        imv_rgprp.setCellValueFactory(new PropertyValueFactory<>("rgprp"));
        imv_rgimv.setCellValueFactory(new PropertyValueFactory<>("rgimv"));
        imv_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        imv_endereco.setCellValueFactory(new PropertyValueFactory<>("ender"));
        imv_situacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        imv_div.setCellValueFactory(new PropertyValueFactory<>("div"));
        imv_fusao.setCellValueFactory(new PropertyValueFactory<>("fusao"));

        if (data != null) {
            p_imoveis.setItems(FXCollections.observableArrayList(data));
        } else p_imoveis.getItems().clear();

        p_imoveis.setRowFactory(
                new Callback<TableView<pimoveisModel>, TableRow<pimoveisModel>>() {
                    @Override
                    public TableRow<pimoveisModel> call(TableView<pimoveisModel> tableView) {
                        final TableRow<pimoveisModel> row = new TableRow<>();
                        final ContextMenu rowMenu = new ContextMenu();
                        MenuItem editItem = new MenuItem("Junta");
                        editItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                String updateSQL = "UPDATE imoveis SET i_fusao = '+' WHERE i_rgimv = '" + p_imoveis.getSelectionModel().getSelectedItems().get(0).getRgimv()+ "' OR i_rgimv = '" +
                                        p_imoveis.getSelectionModel().getSelectedItems().get(1).getRgimv() + "'";
                                conn.ExecutarComando(updateSQL);
                                //fusao = "+"
                            }
                        });

                        MenuItem removeItem = new MenuItem("Separa");
                        removeItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                String updateSQL = "UPDATE imoveis SET i_fusao = '' WHERE i_rgimv = '" + p_imoveis.getSelectionModel().getSelectedItems().get(0).getRgimv()+ "' OR i_rgimv = '" +
                                        p_imoveis.getSelectionModel().getSelectedItems().get(1).getRgimv() + "'";
                                conn.ExecutarComando(updateSQL);
                            }
                        });
                        rowMenu.getItems().addAll(editItem, removeItem);

                        // only display context menu for non-empty rows:
                        row.contextMenuProperty().bind(
                                Bindings.when(row.emptyProperty())
                                        .then((ContextMenu) null)
                                        .otherwise(rowMenu));
                        return row;
                    }
                });
    }

    public static String getMDIid() {
        return MDIid;
    }

    public static void setMDIid(String MDIid) {
        ProprietarioController.MDIid = MDIid;
    }

    public void stop() {
        System.out.println("Proprietarios saindo...");
    }
    
    private void ChamaTela(String nome, String url, String icone) throws IOException, Exception {
        AnchorPane root = null;
        try { root = FXMLLoader.load(getClass().getResource(url)); } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setIconifiable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(false);

        //internalFrame.setBackground(new UIColor(221,221, 221));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.fireEvent(new vieaEvent("IEA", vieaEvent.GET_VIEA));

        pimoveisModel pimoveis = p_imoveis.getSelectionModel().getSelectedItem();
        if (pimoveis != null) {
            root.fireEvent(new paramEvent(new String[] {pimoveis.getRgprp(), pimoveis.getRgimv(), p_tipoprop.getSelectionModel().getSelectedItem().toUpperCase().substring(0,1)}, paramEvent.GET_PARAM));
        } else {
            root.fireEvent(new paramEvent(new String[] {p_rgprp.getText(), null, p_tipoprop.getSelectionModel().getSelectedItem().toUpperCase().substring(0,1)}, paramEvent.GET_PARAM));
        }

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
/*
            if (event.sparam[0] == null) {
                internalFrame.close();
            }
*/
            if (event.sparam[0] != null) if (event.sparam[0].toString().equalsIgnoreCase("imovel")) try {internalFrame.close();} catch (NullPointerException e) {}

            Consulta consulta = (Consulta)event.sparam[0];
            populateImoveis(p_rgprp.getText());
            if (consulta != null) {
                try {p_nome.setText(consulta.getNomerazao()); p_nome.setDisable(true);} catch (Exception e) {}
                try {p_dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(consulta.getDatanasc(),"dd/MM/yyyy")));} catch (Exception e) {}
                try {p_cpf_cnpj.setText(consulta.getCpfcnpj()); p_cpf_cnpj.setDisable(true);} catch (Exception e) {}
                try {p_endereco.setText(consulta.getEndereco());} catch (Exception e) {}
                try {p_numero.setText(consulta.getNumero());} catch (Exception e) {}
                try {p_complemento.setText(consulta.getComplemento());} catch (Exception e) {}
                try {p_bairro.setText(consulta.getBairro());} catch (Exception e) {}
                try {p_cidade.setText(consulta.getCidade());} catch (Exception e) {}
                try {p_estado.setText(consulta.getEstado());} catch (Exception e) {}
                try {p_cep.setText(consulta.getCep());} catch (Exception e) {}
                try {p_insc_rg.setText(consulta.getRginsc());} catch (Exception e) {}

                // Telefones
                List<ptelcontatoModel> data = null;
                try { data = new setTels(consulta.getTelefones()).rString(); } catch (ArrayIndexOutOfBoundsException aex) { data = null; }

                p_telcontato.getItems().clear();
                if (data != null) p_telcontato.setItems(observableArrayList(data)); else p_telcontato.getItems().clear();
                p_telcontato.setDisable(false);
                try {p_telcontato.getSelectionModel().select(0); p_telcontato.getEditor().setText(data.get(0).toString());} catch (Exception e) {}

                // Emails
                List<pemailModel> dataemail = null;
                String emailp = consulta.getEmails();
                if (emailp != null) {dataemail = new setEmails(emailp, true).rString();}
                p_email.getItems().clear();
                if (dataemail != null) p_email.setItems(observableArrayList(dataemail)); else p_email.getItems().clear();
                p_email.setDisable(false);
                try {p_email.getSelectionModel().select(0);} catch (Exception e) {}

                // Fecha Pesquisa
                internalFrame.close();
            } else {
                LerProp();
                bInc = false;
                new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoDisabled(new Object[]{p_btn_gravar});
                new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(false);
                if (p_rgprp.getText() != null) { 
                    if (Integer.valueOf(p_rgprp.getText()) == 0) {
                        new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {p_btn_incluir, p_btn_sair});
                        new Controle("PROPRIETARIO: " + p_rgprp.getText() + " - ", this.fields).FieldsEnabled(false);
                    }                
                }
                p_nome.setDisable(false);
                p_cpf_cnpj.setDisable(false);

                // Fecha Pesquisa
                internalFrame.close();

                return;
            }
        });
    }
    
    private Object[][] sdExtrato(String rgprp) {
        Object[][] prop = {};
        int pos = -1; // Inicia ponteiro de pesquisa

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM propsaldo Where registro = ? and aut_pag is not null AND not aut_pag[1][2] is null;";
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", rgprp}});
        try {
            while (sars.next()) {
                pos = FuncoesGlobais.FindinObject(prop,0,sars.getString("registro"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(sars.getString("registro"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            sars.getString("registro"),
                            dadosprop[0][3].toString(),
                            sars.getBigDecimal("valor"),
                            new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(sars.getBigDecimal("valor"));
                }
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select * from movimento where rgprp = ? and aut_rec <> 0 and not aut_pag[1][2] is null order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",rgprp}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }

                pos = FuncoesGlobais.FindinObject(prop,0,rs.getString("rgprp"));
                if (pos == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(rs.getString("rgprp"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            rs.getString("rgprp"),
                            dadosprop[0][3].toString(),
                            alu,
                            new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(alu);
                }

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where rgprp = ? and aut_rec <> 0 and not aut_pag[1][2] is null order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",rgprp}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,dfrs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(dfrs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        dfrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        dftipo.trim().equalsIgnoreCase("C") ? dfcom : new BigDecimal("0"),
                                        dftipo.trim().equalsIgnoreCase("C") ? new BigDecimal("0") : dfcom
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(dftipo.trim().equalsIgnoreCase("C") ? dfcom : new BigDecimal("0"));
                                prop[pos2][3] = ((BigDecimal)prop[pos2][2]).add(dftipo.trim().equalsIgnoreCase("C") ? new BigDecimal("0") : dfcom);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                prop[pos][3] = ((BigDecimal)prop[pos][3]).add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        prop[pos][3] = ((BigDecimal)prop[pos][3]).add(pirvr);
                    }
                }

                // Seguros - PAREI AQUI 07/01/2021
                {
                    String sgsql = "select * from seguros where rgprp = ? and aut_rec <> 0 and not aut_pag is null order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",rgprp}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            //ttCR = ttCR.add(seg);
                            //if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,sgrs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(sgrs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        sgrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        seg,
                                        new BigDecimal("0")
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(seg);
                                if (sgrs.getBoolean("retencao")) prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(dfDB);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where rgprp = ? and aut_rec <> 0 and not aut_pag[1][2] is null order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",rgprp}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            int pos2 = FuncoesGlobais.FindinObject(prop,0,txrs.getString("rgprp"));
                            if (pos2 == -1) {
                                Object[][] dadosprop = null;
                                try {
                                    dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(txrs.getString("rgprp"))}});
                                } catch (SQLException e) {}
                                prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                                        txrs.getString("rgprp"),
                                        dadosprop[0][3].toString(),
                                        (txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")),
                                        (txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"))
                                });
                                pos = prop.length - 1;
                            } else {
                                prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0"));
                                prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0"));
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        prop[pos][2] = ((BigDecimal)prop[pos][2]).add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        prop[pos][2] = ((BigDecimal)prop[pos][2]).add(pjuvr);
                    }
                }

                // CORREÇÃO
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    prop[pos][2] = ((BigDecimal)prop[pos][2]).add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    prop[pos][2]= ((BigDecimal)prop[pos][2]).add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where rgprp = ? and aut_rec <> 0 and not aut_pag is not null and aut_pag[1][2] is null and conta = 1 order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string",rgprp}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                int pos2 = FuncoesGlobais.FindinObject(prop,0,avrs.getString("rgprp"));
                if (pos2 == -1) {
                    Object[][] dadosprop = null;
                    try {
                        dadosprop = conn.LerCamposTabela(new String[] {"p_nome"}, "proprietarios", "p_rgprp = ?", new Object[][] {{"int", Integer.valueOf(avrs.getString("registro"))}});
                    } catch (SQLException e) {}
                    prop = FuncoesGlobais.ObjectsAdd(prop, new Object[] {
                            avrs.getString("registro"),
                            dadosprop[0][3].toString(),
                            avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"),
                            avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0")
                    });
                    pos = prop.length - 1;
                } else {
                    prop[pos2][2] = ((BigDecimal)prop[pos2][2]).add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                    prop[pos2][3] = ((BigDecimal)prop[pos2][3]).add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        return prop;
    }
    
}