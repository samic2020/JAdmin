package Locatarios.Adicionais;

import Classes.*;
import Funcoes.*;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import masktextfield.MaskTextField;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;
import samic.serversamic.Consulta;

public class AdcLocatarios implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean bInc = false;
    int pID = -1;
    int pIdLoca = -1;

    Object[] fields;
    Object[] buttons;

    private String l_rgprp;
    private String l_rgimv;
    private String l_tpimovel;
    private String l_contrato;

    @FXML private AnchorPane l_anchorPane;
    @FXML private RadioButton l_fisica;
    @FXML private ToggleGroup l_gfisjur;
    @FXML private RadioButton l_juridica;
    @FXML private MaskTextField l_cpfcnpj;
    @FXML private TextField l_rginsc;
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
    @FXML private MenuButton l_f_email_menu;
    @FXML private MenuItem l_f_emailadc;
    @FXML private MenuItem l_f_emaildel;
    @FXML private ComboBox<pemailModel> l_f_email;
    @FXML private Button l_btIncluir;
    @FXML private Button l_btExcluir;
    @FXML private Button l_btGravar;
    @FXML private Button l_btRetornar;
    @FXML private Button l_btAlterar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        l_anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            try {
                this.pID = (int)event.sparam[0];
                this.l_rgprp = (String) event.sparam[1];
                this.l_rgimv = (String) event.sparam[2];
                this.l_contrato = (String) event.sparam[3];
                this.pIdLoca = (int) event.sparam[4];
            } catch (NullPointerException nex) {
                this.l_rgprp = null;
                this.l_rgimv = null;
                this.l_contrato = null;
                this.pIdLoca = -1;
            }
        });

        // Monstrar
        this.buttons = new Object[] {
                l_btIncluir, l_btAlterar, l_btExcluir,
                l_btGravar, l_btRetornar
        };

        this.fields = new Object[] {
                l_fisica, l_juridica, l_cpfcnpj, l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade, l_f_estcivil,
                l_f_tel_menu, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao, l_f_endereco, l_f_btFindEndereco,
                l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade, l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue,
                l_f_conjuguedtnasc, l_f_conjuguesexo, l_f_conjuguecpf, l_f_conjuguerg, l_f_conjuguesalario, l_f_conjugueempresa,
                l_f_conjuguetelefone_menu, l_f_conjuguetelefone, l_f_email_menu, l_f_email
        };

        Platform.runLater(() -> {
            this.bInc = false;
            if (this.pID == -1) {
                new Controle(this.buttons).BotaoEnabled(new Object[]{l_btIncluir, l_btRetornar});
                new Controle(this.fields).FieldsEnabled(false);
                l_btIncluir.requestFocus();
            } else {
                LerLoca(this.pID);
                new Controle(this.buttons).BotaoEnabled(new Object[]{l_btIncluir, l_btAlterar, l_btExcluir, l_btRetornar});
                new Controle(this.fields).FieldsEnabled(false);
                l_btAlterar.requestFocus();
            }
        });

        new cpoTelefones().cpoTelefones(l_f_tel);
        new cpoTelefones().cpoTelefones(l_f_conjuguetelefone);
        new cpoEmails().cpoEmails(l_f_email);

        l_f_sexo.setItems(new pSexo().Sexo());
        l_f_estcivil.setItems(new pEstCivil().EstCivil());
        l_f_conjuguesexo.setItems(new pSexo().Sexo());

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

        l_btIncluir.setOnAction(event -> {
            if (l_contrato == null) return;
            if (!l_contrato.equalsIgnoreCase("")) {
                this.bInc = true;
                LimpaTela();

                new Controle(this.buttons).BotaoEnabled(new Object[]{l_btGravar, l_btRetornar});
                new Controle(this.fields).FieldsEnabled(true);

                try {
                    ChamaTela("Pesquisa", "/Pesquisa/Pesquisa.fxml", "loca.png");
                } catch (Exception wex) {wex.printStackTrace();}
            } else {
                return;
            }
        });

        l_btGravar.setOnAction(event -> {
            salvar(bInc, this.pID);
            String tpID = String.valueOf(this.pID);
            bInc = false;

            try {
                if (l_contrato.length() <= 0) {
                    new Controle(this.buttons).BotaoEnabled(new Object[] {l_btIncluir, l_btRetornar});
                } else {
                    new Controle(this.buttons).BotaoDisabled(new Object[] {l_btGravar});
                }
            } catch (Exception e) {}

            new Controle(this.fields).FieldsEnabled(false);
        });

        l_btRetornar.setOnAction(event -> {
            if (bInc) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("N�o"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("N�o")) return;
            }
            if (!l_btGravar.isDisabled()) {
                LerLoca(this.pID);
                bInc = false;
                new Controle(this.buttons).BotaoDisabled(new Object[]{l_btGravar});
                new Controle(this.fields).FieldsEnabled(false);
                return;
            }
            try {l_anchorPane.fireEvent(new paramEvent(new Object[] {"AdcLocatarios"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
            //((FXInternalWindow) l_anchorPane.getParent().getParent().getParent()).close();
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

        l_btRetornar.setOnAction(event -> {
            if (bInc) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informa��es?", new ButtonType("Sim"), new ButtonType("N�o"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("N�o")) return;
            }
            if (!l_btGravar.isDisabled()) {
                LerLoca(this.pID);
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

    }

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


        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.fireEvent(new vieaEvent("VIEA", vieaEvent.GET_VIEA));
        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            Consulta consulta = (Consulta)event.sparam[0];
            try {internalFrame.close();} catch (NullPointerException e) {}
            if (consulta != null) {
                l_f_nome.setText(consulta.getNomerazao());
                l_f_nome.setDisable(false);
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
                } catch (Exception e) { }
            } else {
                l_f_nome.setText("");
                l_f_nome.setDisable(false);
                l_cpfcnpj.setText("");
                l_cpfcnpj.setDisable(false);
                l_f_endereco.setText("");
                l_f_numero.setText("");
                l_f_cplto.setText("");
                l_f_bairro.setText("");
                l_f_cidade.setText("");
                l_f_estado.setText("");
                l_f_cep.setText("");
                l_rginsc.setText("");
                l_cpfcnpj.requestFocus();
            }
        });
    }

    private void LimpaTela() {
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
    }

    private void LerLoca(int pID) {
        ResultSet rs = conn.AbrirTabela("SELECT * from adclocatarios WHERE l_id = ?;",ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int",pID}} );
        if (rs == null) return;

        try {rs.next();} catch (SQLException ex) {}

        try {this.pID = rs.getInt("l_id");} catch (SQLException e) {this.pID = -1;}
        try {l_rgprp = rs.getString("l_rgprp");} catch (SQLException e) {l_rgprp = null;}
        try {l_rgimv = rs.getString("l_rgimv");} catch (SQLException e) {l_rgimv = null;}
        try {l_tpimovel = rs.getString("l_tipoimovel");} catch (SQLException e) {l_tpimovel = null;}
        try {l_contrato = rs.getString("l_contrato");} catch (SQLException e) {l_contrato = null;}

        try {if (rs.getBoolean("l_fisjur")) l_fisica.setSelected(true); else l_juridica.setSelected(true);} catch (SQLException e) {}

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
    }

    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        Object[][] param = null;
        if (bNew) {
            param = new Object[][] {
                    {"int", Integer.valueOf(this.l_rgprp)},
                    {"int", Integer.valueOf(this.l_rgimv)},
                    {"string", this.l_contrato},
                    {"string", l_cpfcnpj.getText()},
                    {"string", l_rginsc.getText()},
                    {"string", l_f_nome.getText()},
                    {"string", l_f_sexo.getSelectionModel().getSelectedItem()},
                    {"date", Dates.toSqlDate(l_f_dtnasc)},
                    {"string", l_f_nacionalidade.getText()},
                    {"string", l_f_estcivil.getSelectionModel().getSelectedItem()},
                    {"string", new getTels(l_f_tel).toString()},
                    {"string", l_f_mae.getText()},
                    {"string", l_f_pai.getText()},
                    {"string", l_f_empresa.getText()},
                    {"date", Dates.toSqlDate(l_f_dtadmissao)},
                    {"string", l_f_endereco.getText()},
                    {"string", l_f_numero.getText()},
                    {"string", l_f_cplto.getText()},
                    {"string", l_f_bairro.getText()},
                    {"string", l_f_cidade.getText()},
                    {"string", l_f_estado.getText()},
                    {"string", l_f_cep.getText()},
                    {"string", l_f_cargo.getText()},
                    {"decimal", new BigDecimal(LerValor.Decimal2String(l_f_salario.getText()))},
                    {"string", l_f_conjugue.getText()},
                    {"date", Dates.toSqlDate(l_f_conjuguedtnasc)},
                    {"string", l_f_conjuguesexo.getSelectionModel().getSelectedItem()},
                    {"string", l_f_conjuguerg.getText()},
                    {"string", l_f_conjuguecpf.getText()},
                    {"decimal", new BigDecimal(LerValor.Decimal2String(l_f_conjuguesalario.getText()))},
                    {"string", l_f_conjugueempresa.getText()},
                    {"string", new getTels(l_f_conjuguetelefone).toString()},
                    {"string", new getEmails(l_f_email, false).toString()},
                    {"int", this.pIdLoca}
            };
            sql = "INSERT INTO adclocatarios(l_rgprp, l_rgimv, l_contrato, l_cpfcnpj, " +
                    "            l_rginsc, l_f_nome, l_f_sexo, l_f_dtnasc, l_f_nacionalidade, " +
                    "            l_f_estcivil, l_f_tel, l_f_mae, l_f_pai, l_f_empresa, l_f_dtadmissao, " +
                    "            l_f_endereco, l_f_numero, l_f_cplto, l_f_bairro, l_f_cidade, " +
                    "            l_f_estado, l_f_cep, l_f_cargo, l_f_salario, l_f_conjugue, l_f_conjuguedtnasc, " +
                    "            l_f_conjuguesexo, l_f_conjuguerg, l_f_conjuguecpf, l_f_conjuguesalario, " +
                    "            l_f_conjugueempresa, l_f_conjuguetelefone, l_f_email, l_idloca)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?);";

            this.pID = Id;
        } else {
            sql = "UPDATE adclocatarios" +
                    "   SET l_rgprp=?, l_rgimv=?, l_contrato=?, " +
                    "       l_cpfcnpj=?, l_rginsc=?, l_f_nome=?, l_f_sexo=?, l_f_dtnasc=?, " +
                    "       l_f_nacionalidade=?, l_f_estcivil=?, l_f_tel=?, l_f_mae=?, l_f_pai=?, " +
                    "       l_f_empresa=?, l_f_dtadmissao=?, l_f_endereco=?, l_f_numero=?, " +
                    "       l_f_cplto=?, l_f_bairro=?, l_f_cidade=?, l_f_estado=?, l_f_cep=?, " +
                    "       l_f_cargo=?, l_f_salario=?, l_f_conjugue=?, l_f_conjuguedtnasc=?, " +
                    "       l_f_conjuguesexo=?, l_f_conjuguerg=?, l_f_conjuguecpf=?, l_f_conjuguesalario=?, " +
                    "       l_f_conjugueempresa=?, l_f_conjuguetelefone=?, l_f_email=? " +
                    "       WHERE l_id = ?;";

            param = new Object[][] {
                    {"int", Integer.valueOf(this.l_rgprp)},
                    {"int", Integer.valueOf(this.l_rgimv)},
                    {"string", this.l_contrato},
                    {"string", l_cpfcnpj.getText()},
                    {"string", l_rginsc.getText()},
                    {"string", l_f_nome.getText()},
                    {"string", l_f_sexo.getSelectionModel().getSelectedItem()},
                    {"date", Dates.toSqlDate(l_f_dtnasc)},
                    {"string", l_f_nacionalidade.getText()},
                    {"string", l_f_estcivil.getSelectionModel().getSelectedItem()},
                    {"string", new getTels(l_f_tel).toString()},
                    {"string", l_f_mae.getText()},
                    {"string", l_f_pai.getText()},
                    {"string", l_f_empresa.getText()},
                    {"date", Dates.toSqlDate(l_f_dtadmissao)},
                    {"string", l_f_endereco.getText()},
                    {"string", l_f_numero.getText()},
                    {"string", l_f_cplto.getText()},
                    {"string", l_f_bairro.getText()},
                    {"string", l_f_cidade.getText()},
                    {"string", l_f_estado.getText()},
                    {"string", l_f_cep.getText()},
                    {"string", l_f_cargo.getText()},
                    {"decimal", new BigDecimal(LerValor.Decimal2String(l_f_salario.getText()).replace("R$ ",""))},
                    {"string", l_f_conjugue.getText()},
                    {"date", Dates.toSqlDate(l_f_conjuguedtnasc)},
                    {"string", l_f_conjuguesexo.getSelectionModel().getSelectedItem()},
                    {"string", l_f_conjuguerg.getText()},
                    {"string", l_f_conjuguecpf.getText()},
                    {"decimal", new BigDecimal(LerValor.Decimal2String(l_f_conjuguesalario.getText()).replace("R$ ",""))},
                    {"string", l_f_conjugueempresa.getText()},
                    {"string", new getTels(l_f_conjuguetelefone).toString()},
                    {"string", new getEmails(l_f_email, false).toString()},
                    {"int", Id}
            };
        }
        conn.ExecutarComando(sql, param);
        bNew = false;
        return retorno;
    }
}
