package Seguradoras;

import Classes.*;
import Funcoes.*;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import masktextfield.MaskTextField;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableList;

/**
 * Created by supervisor on 02/08/16.
 */
public class SeguradoraController implements Initializable {
    static private String MDIid;
    DbMain conn = VariaveisGlobais.conexao;
    String sSql = "SELECT * FROM seguradoras ORDER BY s_codigo;";
    ResultSet rs = null;
    String viea = null;
    boolean bInc = false;
    int pID = -1;
    String oldTipo = null;
    Object[] fields;
    Object[] buttons;

    @FXML private AnchorPane tela_segdep;
    @FXML private MaskTextField sd_codigo;
    @FXML private TextField sd_nome;
    @FXML private TextField sd_endereco;
    @FXML private Button sd_btn_endereco;
    @FXML private TextField sd_numero;
    @FXML private TextField sd_complemento;
    @FXML private TextField sd_bairro;
    @FXML private TextField sd_cidade;
    @FXML private TextField sd_estado;
    @FXML private MaskTextField sd_cep;
    @FXML private MenuButton sd_btn_telcontato;
    @FXML private MenuItem adc_telcontato;
    @FXML private MenuItem del_telcontato;
    @FXML private TextField sd_cpf_cnpj;
    @FXML private ComboBox<ptelcontatoModel> sd_telcontato;
    @FXML private TextField sd_representante;
    @FXML private DatePicker sd_repdtnasc;
    @FXML private MenuButton sd_btn_banco;
    @FXML private MenuItem adcBancos;
    @FXML private MenuItem delBancos;
    @FXML private ComboBox<pbancosModel> sd_banco;
    @FXML private MenuButton sd_btn_email;
    @FXML private MenuItem adc_btn_email;
    @FXML private MenuItem del_btn_email;
    @FXML private ComboBox<pemailModel> sd_email;
    @FXML private ComboBox<String> blq_diasmeses;
    @FXML private TextField blq_numero;
    @FXML private Button sd_btIncluir;
    @FXML private Button sd_btAlterar;
    @FXML private Button sd_btExcluir;
    @FXML private Button sd_btPrevious;
    @FXML private Button sd_btNext;
    @FXML private Button sd_btGravar;
    @FXML private Button sd_btRetornar;

    private int NextField(String field, TextField[] fields) {
        int pos = 0;
        for (pos = 0; pos < fields.length; pos++) {
            if (fields[pos].getId().equalsIgnoreCase(field)) break;
        }
        return pos + 1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tela_segdep.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
            }
        });
        tela_segdep.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });

        Platform.runLater(() -> { verifyBotoes(); });

        sd_btIncluir.setOnAction(event -> {
            this.bInc = true;
            LimpaTela();

            new Controle(this.buttons).BotaoEnabled(new Object[] {sd_btGravar, sd_btRetornar});
            new Controle(this.fields).FieldsEnabled(true);

            sd_codigo.setDisable(true);
            sd_nome.requestFocus();
        });

        sd_btAlterar.setOnAction(event -> {
            this.bInc = false;

            new Controle(this.buttons).BotaoEnabled(new Object[] {sd_btGravar, sd_btRetornar});
            new Controle(this.fields).FieldsEnabled(true);

            sd_codigo.setDisable(true);
            sd_nome.requestFocus();
        });

        sd_btPrevious.setOnAction(event -> {
            try {
                rs.previous();
                if (rs.isBeforeFirst()) {
                    rs.next();
                }
                LerSeg();
            } catch (SQLException e) {}
        });

        sd_btNext.setOnAction(event -> {
            try {
                rs.next();
                if (rs.isAfterLast()) {
                    rs.previous();
                }
                LerSeg();
            } catch (SQLException e) {}
        });

        sd_btGravar.setOnAction(event -> {
            if (this.viea == null) this.viea = "VIEA";
            if (this.viea.contains("I") || this.viea.contains("A")) {
                if (bInc) {
                    int iNewRgPrp = 0;
                    int NewRgPrp = 0;
                    try {
                        NewRgPrp = Integer.parseInt(conn.LerParametros("SEGURADORA"));
                    } catch (SQLException ex) {}

                    iNewRgPrp++;
                    String cPar[] = {"SEGURADORA",String.valueOf(iNewRgPrp),"NUMERICO"};
                    try {
                        conn.GravarParametros(cPar);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    salvar(bInc, iNewRgPrp);
                    sd_codigo.setText(String.valueOf(iNewRgPrp));
                } else {
                    salvar(bInc, this.pID);
                }

                verifyBotoes();
                bInc = false;

                // Atualiza
                try {
                    int pos = rs.getRow();
                    DbMain.FecharTabela(rs);

                    rs = conn.AbrirTabela(sSql, ResultSet.CONCUR_UPDATABLE);
                    rs.absolute(pos);
                    if (DbMain.RecordCount(rs) <= 0) {
                        new Controle(this.buttons).BotaoEnabled(new Object[] {sd_btIncluir, sd_btRetornar});
                    } else {
                        new Controle(this.buttons).BotaoDisabled(new Object[] {sd_btGravar});
                    }
                } catch (SQLException e) {}

                new Controle(this.fields).FieldsEnabled(false);

            }
        });

        sd_btRetornar.setOnAction(event -> {
            if (bInc) {
                Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
                Optional<ButtonType> result = msg.showAndWait();
                if (result.get().getText().equals("Não")) return;
            }

            if (!sd_btGravar.isDisabled()) {
                LerSeg();
                bInc = false;
                new Controle(this.buttons).BotaoDisabled(new Object[]{sd_btGravar});
                new Controle(this.fields).FieldsEnabled(false);
                return;
            }

            ((FXInternalWindow) tela_segdep.getParent().getParent().getParent()).close();
        });

        adcBancos.setOnAction(event -> {
            Classes.adcBancos dialog = new adcBancos();
            Optional<pbancosModel> result = dialog.adcBancos();
            result.ifPresent(b -> {
                ObservableList<pbancosModel> ebancos = sd_banco.getItems();
                ebancos.addAll(b);
                sd_banco.setItems(ebancos);
                try {sd_banco.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        delBancos.setOnAction(event -> {
            if (!sd_banco.getItems().isEmpty()) sd_banco.getItems().removeAll(sd_banco.getSelectionModel().getSelectedItem());
            try {sd_banco.getSelectionModel().select(0);} catch (Exception e) {}
        });

        adc_telcontato.setOnAction(event -> {
            adcTelefones dialog = new adcTelefones();
            Optional<ptelcontatoModel> result = dialog.adcTelefones();
            result.ifPresent(b -> {
                ObservableList<ptelcontatoModel> tels = sd_telcontato.getItems();
                tels.addAll(b);
                sd_telcontato.setItems(tels);
                try {sd_telcontato.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        del_telcontato.setOnAction(event -> {
            if (!sd_telcontato.getItems().isEmpty()) sd_telcontato.getItems().removeAll(sd_telcontato.getSelectionModel().getSelectedItem());
            try {sd_telcontato.getSelectionModel().select(0);} catch (Exception e) {}
        });

        adc_btn_email.setOnAction(event -> {
            adcEmails dialog = new adcEmails();
            Optional<pemailModel> result = dialog.adcEmails(false);
            result.ifPresent(b -> {
                ObservableList<pemailModel> emails = sd_email.getItems();
                emails.addAll(b);
                sd_email.setItems(emails);
                try {sd_email.getSelectionModel().select(0);} catch (Exception e) {}
            });
        });

        del_btn_email.setOnAction(event -> {
            if (!sd_email.getItems().isEmpty()) sd_email.getItems().removeAll(sd_email.getSelectionModel().getSelectedItem());
            try {sd_email.getSelectionModel().select(0);} catch (Exception e) {}
        });

        sd_cpf_cnpj.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
            } else {
                // out focus
                if (sd_cpf_cnpj.getText().trim().length() <= 14) {
                    if (!MaskFieldUtil.isCpf(sd_cpf_cnpj.getText())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CPF inválido");
                        alert.setContentText("Entre com um CPF válido!!!");

                        alert.showAndWait();
                        sd_cpf_cnpj.setText(null); sd_cpf_cnpj.requestFocus();
                    }
                } else {
                    if (!MaskFieldUtil.isCnpj(sd_cpf_cnpj.getText())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CNPJ inválido");
                        alert.setContentText("Entre com um CNPJ válido!!!");

                        alert.showAndWait();
                        sd_cpf_cnpj.setText(null); sd_cpf_cnpj.requestFocus();
                    }
                }
            }
        });

        new cpoTelefones().cpoTelefones(sd_telcontato);
        new cpoEmails().cpoEmails(sd_email);
        new cpoBancos().cpoBancos(sd_banco);

        sd_btn_endereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                sd_endereco.setText(b.getLogradouro());
                sd_bairro.setText(b.getBairro());
                sd_cidade.setText(b.getLocalidade());
                sd_estado.setText(b.getUf());
                sd_cep.setText(b.getCep());
                sd_numero.setText(null); sd_complemento.setText(null);
                sd_numero.requestFocus();
            });
        });

        sd_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if ((sd_endereco.getText() == null || sd_endereco.getText().equalsIgnoreCase("")) && sd_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(sd_cep.getText());

                            sd_endereco.setText(endereco.getLogradouro());
                            sd_bairro.setText(endereco.getBairro());
                            sd_cidade.setText(endereco.getLocalidade());
                            sd_estado.setText(endereco.getUf());

                            sd_numero.setText(null); sd_complemento.setText(null);
                            sd_numero.requestFocus();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        MaskFieldUtil.numericField(blq_numero);
        blq_numero.setText("1");

        List<String> list = new ArrayList<>();
        list.add("Dia(s)"); list.add("Mês(es)");
        ObservableList<String> observableList = observableList(list);
        blq_diasmeses.setItems(observableList);

        LimpaTela();
        OpenSeguradoras();

        this.fields = new Object[] {
                sd_nome, sd_endereco, sd_btn_endereco, sd_numero, sd_complemento, sd_bairro, sd_cidade, sd_estado,
                sd_cep, sd_btn_telcontato, sd_telcontato, sd_cpf_cnpj, sd_representante, sd_repdtnasc, sd_btn_banco,
                sd_banco, sd_btn_email, sd_email, blq_numero, blq_diasmeses
        };
        new Controle(this.fields).Focus();
        new Controle(this.fields).FieldsEnabled(false);

        this.buttons = new Object[] {
                sd_btIncluir, sd_btAlterar, sd_btExcluir, sd_btPrevious, sd_btNext, sd_btGravar, sd_btRetornar
        };

        if (DbMain.RecordCount(rs) <= 0) {
            Platform.runLater(() -> new Controle(this.buttons).BotaoEnabled(new Object[] {sd_btIncluir, sd_btRetornar}));
        } else {
            Platform.runLater(() -> new Controle(this.buttons).BotaoDisabled(new Object[] {sd_btGravar}));
        }

    }

    private void OpenSeguradoras() {
        rs = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        try {
            rs.next();
            LerSeg();
        } catch (SQLException e) {e.printStackTrace();}
    }

    public boolean MoveToProp(String campo, String seek) throws SQLException {
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
        LerSeg();
        return achei;
    }

    private void LimpaTela() {
        this.pID = -1;
        sd_codigo.setText(null);
        MaskFieldUtil.maxField(sd_nome, 60); sd_nome.setText(null);
        MaskFieldUtil.maxField(sd_endereco, 60); sd_endereco.setText(null);
        MaskFieldUtil.maxField(sd_numero, 10); sd_numero.setText(null);
        MaskFieldUtil.maxField(sd_complemento, 15); sd_complemento.setText(null);
        MaskFieldUtil.maxField(sd_bairro, 25); sd_bairro.setText(null);
        MaskFieldUtil.maxField(sd_cidade, 25); sd_cidade.setText(null);
        MaskFieldUtil.maxField(sd_estado, 2); sd_estado.setText(null);
        sd_cep.setText(null);
        sd_telcontato.getItems().clear();
        MaskFieldUtil.maxField(sd_representante, 60); sd_representante.setText(null);
        sd_repdtnasc.setValue(null);
        MaskFieldUtil.cpfCnpjField(sd_cpf_cnpj); sd_cpf_cnpj.setText(null);
        sd_banco.getItems().clear();
        sd_email.getItems().clear();

        blq_numero.setText("1");
        blq_diasmeses.getSelectionModel().select(1);
    }

    private void LerSeg() {
        try {this.pID = rs.getInt("s_id");} catch (SQLException e) {this.pID = -1;}
        try {sd_codigo.setEditable(false);sd_codigo.setText(rs.getString("s_codigo"));} catch (SQLException e) {sd_codigo.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_nome, 60); sd_nome.setText(rs.getString("s_nome"));} catch (SQLException e) {sd_nome.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_endereco, 60); sd_endereco.setText(rs.getString("s_end"));} catch (SQLException e) {sd_endereco.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_numero, 10); sd_numero.setText(rs.getString("s_num"));} catch (SQLException e) {sd_numero.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_complemento, 15); sd_complemento.setText(rs.getString("s_compl"));} catch (SQLException e) {sd_complemento.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_bairro, 25); sd_bairro.setText(rs.getString("s_bairro"));} catch (SQLException e) {sd_bairro.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_cidade, 25); sd_cidade.setText(rs.getString("s_cidade"));} catch (SQLException e) {sd_cidade.setText(null);}
        try {
            MaskFieldUtil.maxField(sd_estado, 2); sd_estado.setText(rs.getString("s_estado"));} catch (SQLException e) {sd_estado.setText(null);}
        try {sd_cep.setText(rs.getString("s_cep"));} catch (SQLException e) {sd_cep.setText(null);}

        // Telefone de contato
        List<ptelcontatoModel> data = null;
        try {data = new setTels(rs.getString("s_tel")).rString();} catch (SQLException e) {}
        if (data != null) sd_telcontato.setItems(observableArrayList(data)); else sd_telcontato.getItems().clear();
        sd_telcontato.setDisable(false);
        try {sd_telcontato.getSelectionModel().select(0);} catch (Exception e) {}

        try {
            MaskFieldUtil.maxField(sd_representante, 60); sd_representante.setText(rs.getString("s_representante"));} catch (SQLException e) {sd_representante.setText(null);}

        try {sd_repdtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("s_repdtnasc")));} catch (Exception e) {sd_repdtnasc.getEditor().clear();}
        try {sd_repdtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("s_repdtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {sd_repdtnasc.setValue(null);}

        try {
            MaskFieldUtil.cpfCnpjField(sd_cpf_cnpj); sd_cpf_cnpj.setText(rs.getString("s_cpfcnpj"));} catch (SQLException e) {sd_cpf_cnpj.setText(null);}

        //bancos
        List<pbancosModel> databancos = null;
        try {databancos = new setBancos(rs.getString("s_bancos")).rString();} catch (SQLException e) {}
        if (databancos != null) sd_banco.setItems(observableArrayList(databancos)); else sd_banco.getItems().clear();
        sd_banco.setDisable(false);
        try {sd_banco.getSelectionModel().select(0);} catch (Exception e) {}

        //email;
        List<pemailModel> dataemail = null;
        try {dataemail = new setEmails(rs.getString("s_email"),false).rString();} catch (SQLException e) {}
        if (dataemail != null) sd_email.setItems(observableArrayList(dataemail)); else sd_email.getItems().clear();
        sd_email.setDisable(false);
        try {sd_email.getSelectionModel().select(0);} catch (Exception e) {}

        try {blq_numero.setText(String.valueOf(rs.getInt("s_numero")));} catch (SQLException e) {}
        try {blq_diasmeses.getSelectionModel().select(rs.getInt("s_diames"));} catch (SQLException e) {blq_diasmeses.getSelectionModel().select(1);}
    }

    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO seguradoras(" +
                    "s_codigo, s_nome, s_end, s_num, s_compl, s_bairro, " +
                    "s_cidade, s_estado, s_cep, s_tel, s_representante, s_repdtnasc, " +
                    "s_cpfcnpj, s_email, s_bancos, s_numero, s_diames) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        } else {
            sql = "UPDATE seguradoras" +
                    " SET s_nome=?, s_end=?, s_num=?, s_compl=?, " +
                    "s_bairro=?, s_cidade=?, s_estado=?, s_cep=?, s_tel=?, s_representante=?, " +
                    "s_repdtnasc=?, s_cpfcnpj=?, s_email=?, s_bancos=?, s_numero=?, s_diames=?" +
                    " WHERE s_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1;
            if (bNew) pstmt.setInt(nid++, Id);
            pstmt.setString(nid++, sd_nome.getText());
            pstmt.setString(nid++, sd_endereco.getText());
            pstmt.setString(nid++, sd_numero.getText());
            pstmt.setString(nid++, sd_complemento.getText());
            pstmt.setString(nid++, sd_bairro.getText());
            pstmt.setString(nid++, sd_cidade.getText());
            pstmt.setString(nid++, sd_estado.getText());
            pstmt.setString(nid++, sd_cep.getText());
            pstmt.setString(nid++, new getTels(sd_telcontato).toString());
            pstmt.setString(nid++, sd_representante.getText());
            pstmt.setDate(nid++, Dates.toSqlDate(sd_repdtnasc));
            pstmt.setString(nid++, sd_cpf_cnpj.getText());
            pstmt.setString(nid++, new getEmails(sd_email,false).toString());
            pstmt.setString(nid++, new getBancos(sd_banco).toString());
            pstmt.setInt(nid++, Integer.valueOf(blq_numero.getText()));
            pstmt.setInt(nid++, blq_diasmeses.getSelectionModel().getSelectedIndex());

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        return retorno;
    }

    private void verifyBotoes() {
        if (this.viea == null) return;
        if (!this.viea.contains("I")) sd_btIncluir.setDisable(true);
        if (!this.viea.contains("E")) sd_btExcluir.setDisable(true);
        if (!this.viea.contains("A")) sd_btGravar.setDisable(true);
    }

    public static String getMDIid() {
        return MDIid;
    }
    public static void setMDIid(String MDIid) {
        SeguradoraController.MDIid = MDIid;
    }
    public void stop() {
        System.out.println("Seguradoras saindo...");
    }

}
