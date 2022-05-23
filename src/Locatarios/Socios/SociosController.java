/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Locatarios.Socios;

import Classes.*;
import Funcoes.*;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;
import samic.serversamic.Consulta;

/*import jfxtras.labs.scene.control.window.Window;*/

public class SociosController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    String sSql = "SELECT * FROM socios ORDER BY s_contrato;";
    ResultSet rs = null;
    String viea = null;
    String s_rgprp = "", s_rgimv = "", s_contrato = "", s_documento = "";
    Integer s_id = -1;
    Object[] fields;
    Object[] buttons;

    boolean bInc = false;
    int pID = -1;

    @FXML private AnchorPane AnchorPane;

    @FXML private TextField s_nome;
    @FXML private ComboBox<String> s_sexo;
    @FXML private DatePicker s_dtnasc;
    @FXML private TextField s_cpf;
    @FXML private TextField s_rginsc;

    @FXML private TextField s_endereco;
    @FXML private Button s_btFindEndereco;
    @FXML private TextField s_numero;
    @FXML private TextField s_cplto;
    @FXML private TextField s_bairro;
    @FXML private TextField s_cidade;
    @FXML private TextField s_estado;
    @FXML private MaskTextField s_cep;
    @FXML private TextField s_nacionalidade;
    @FXML private ComboBox<String> s_estcivil;
    @FXML private ComboBox<ptelcontatoModel> s_tel;
    @FXML private MenuButton telef_menu;
    @FXML private MenuItem s_teladc;
    @FXML private MenuItem s_teldel;

    @FXML private TextField s_mae;
    @FXML private TextField s_pai;
    @FXML private TextField s_renda;
    @FXML private TextField s_cargo;

    @FXML private Button s_btincluir;
    @FXML private Button s_btalterar;
    @FXML private Button s_btgravar;
    @FXML private Button s_btretornar;

    @FXML void s_teladc_OnAction(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = s_tel.getItems();
            tels.addAll(b);
            s_tel.setItems(tels);
            try {s_tel.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void s_teldel_OnAction(ActionEvent event) {
        if (!s_tel.getItems().isEmpty()) s_tel.getItems().removeAll(s_tel.getSelectionModel().getSelectedItem());
        try {s_tel.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void s_btgravar_OnAction(ActionEvent event) {
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            if (bInc) {
                salvar(bInc, -1);
            } else {
                salvar(bInc, this.pID);
                String tpID = String.valueOf(this.pID);
                try {rs.close();} catch (SQLException ex) {}
                OpenSocios(true);
                try {MoveTo("s_id",tpID);} catch (SQLException ex1) {}
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
                    new Controle(this.buttons).BotaoEnabled(new Object[] {s_btincluir, s_btretornar});
                } else {
                    new Controle(this.buttons).BotaoDisabled(new Object[] {s_btgravar});
                }
            } catch (SQLException e) {}

            new Controle(this.fields).FieldsEnabled(false);
        }
    }

    @FXML void s_btincluir_OnAction(ActionEvent event) {
        this.bInc = true;
        LimpaTela();

        new Controle(this.buttons).BotaoEnabled(new Object[] {s_btgravar, s_btretornar});
        new Controle(this.fields).FieldsEnabled(true);

        try {
            ChamaTela("Pesquisa", "/Pesquisa/Pesquisa.fxml", "loca.png");
        } catch (Exception wex) {wex.printStackTrace();}

        s_nome.requestFocus();
    }

    @FXML void s_btalterar_OnAction(ActionEvent event) {
        this.bInc = false;

        new Controle(this.buttons).BotaoEnabled(new Object[] {s_btgravar, s_btretornar});
        new Controle(this.fields).FieldsEnabled(true);

        s_nome.requestFocus();
    }

    @FXML void s_btretornar_OnAction(ActionEvent event) {
        if (bInc) {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) return;
        }

        if (!s_btgravar.isDisabled()) {
            LerSocios();
            bInc = false;
            new Controle(this.buttons).BotaoDisabled(new Object[]{s_btgravar});
            new Controle(this.fields).FieldsEnabled(false);
            return;
        }

        try {AnchorPane.fireEvent(new paramEvent(new Object[] {"Socio"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        //((FXInternalWindow) AnchorPane.getParent().getParent().getParent()).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
            }
        });

        AnchorPane.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });

        AnchorPane.addEventHandler(paramEvent.GET_PARAM, event1 -> {
            Object[] param = event1.sparam;
            s_id = (Integer)param[0];
            s_rgprp = (String)param[1];
            s_rgimv = (String)param[2];
            s_contrato = (String)param[3];
            s_documento = (String)param[4];
        });

        Platform.runLater(() -> { verifyBotoes(); });

        s_cpf.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
            } else {
                // out focus
                if (s_cpf.getText() != null) {
                    if (!MaskFieldUtil.isCpf(s_cpf.getText())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Menssagem");
                        alert.setHeaderText("CPF inválido");
                        alert.setContentText("Entre com um CPF válido!!!");

                        alert.showAndWait();
                        s_cpf.setText(null);
                        s_cpf.requestFocus();
                    }
                }
            }
        });

        s_btFindEndereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                s_endereco.setText(b.getLogradouro());
                s_bairro.setText(b.getBairro());
                s_cidade.setText(b.getLocalidade());
                s_estado.setText(b.getUf());
                s_cep.setText(b.getCep());
                s_numero.setText(null); s_cplto.setText(null);
                s_numero.requestFocus();
            });
        });

        s_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                } else {
                    // out focus
                    if (s_endereco.getText() == null && s_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(s_cep.getText());

                            s_endereco.setText(endereco.getLogradouro());
                            s_bairro.setText(endereco.getBairro());
                            s_cidade.setText(endereco.getLocalidade());
                            s_estado.setText(endereco.getUf());

                            s_numero.setText(null); s_cplto.setText(null);
                            s_numero.requestFocus();
                        } catch (IOException ex) {}
                    }
                }
            }
        });

        s_sexo.setItems(new pSexo().Sexo());
        s_estcivil.setItems(new pEstCivil().EstCivil());
        new cpoTelefones().cpoTelefones(s_tel);

        LimpaTela();
        if (s_id != null) OpenSocios(true);

        this.fields = new Object[] {s_nome, s_sexo, s_dtnasc,
                s_rginsc, s_endereco, s_btFindEndereco,
                s_numero, s_cplto, s_bairro, s_cidade, s_estado, s_cep, s_nacionalidade, s_estcivil,
                telef_menu, s_tel, s_mae, s_pai, s_renda, s_cargo
        };
        new Controle("SOCIOS: " + s_contrato + " - ", this.fields).Focus();
        new Controle(this.fields).FieldsEnabled(false);

        this.buttons = new Object[] {
                s_btincluir, s_btalterar, s_btgravar, s_btretornar
        };

        if (DbMain.RecordCount(rs) <= 0) {
            Platform.runLater(() -> new Controle(this.buttons).BotaoEnabled(new Object[] {s_btincluir, s_btretornar}));
        } else {
            Platform.runLater(() -> new Controle(this.buttons).BotaoDisabled(new Object[] {s_btgravar}));
        }
    }

    private void OpenSocios(boolean ler) {
        rs = conn.AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        try {
            rs.next();
            if (ler) LerSocios();
        } catch (SQLException e) {e.printStackTrace();}
        Platform.runLater(() -> {if (this.s_id != -1) {try {MoveTo("s_id", s_id.toString());} catch (SQLException e) {}}});
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
        LerSocios();
        return achei;
    }

    private void LimpaTela() {
        MaskFieldUtil.maxField(s_nome,60); s_nome.setText(null);
        s_sexo.getSelectionModel().select(0);
        s_dtnasc.setValue(null);

        s_cpf.setText(null);
        MaskFieldUtil.maxField(s_rginsc,20); s_rginsc.setText(null);

        MaskFieldUtil.maxField(s_endereco,60); s_endereco.setText(null);
        MaskFieldUtil.maxField(s_numero,10); s_numero.setText(null);
        MaskFieldUtil.maxField(s_cplto,15); s_cplto.setText(null);
        MaskFieldUtil.maxField(s_bairro,25); s_bairro.setText(null);
        MaskFieldUtil.maxField(s_cidade,25); s_cidade.setText(null);
        MaskFieldUtil.maxField(s_estado,2); s_estado.setText(null);
        s_cep.setText(null);

        MaskFieldUtil.maxField(s_nacionalidade,25); s_nacionalidade.setText(null);
        s_estcivil.getSelectionModel().select(0);
        s_tel.getItems().clear();

        MaskFieldUtil.maxField(s_mae,60);s_mae.setText(null);
        MaskFieldUtil.maxField(s_pai,60); s_pai.setText(null);
        MaskFieldUtil.maxField(s_cargo,25); s_cargo.setText(null);
        MaskFieldUtil.monetaryField(s_renda); s_renda.setText("0,00");
    }

    private void LerSocios() {
        try {this.pID = rs.getInt("f_id");} catch (SQLException e) {this.pID = -1;}

        try {s_nome.setText(rs.getString("s_nome"));} catch (SQLException e) {s_nome.setText(null);}
        try {s_sexo.getSelectionModel().select(rs.getString("s_sexo"));} catch (SQLException e) {s_sexo.getSelectionModel().select(0);}
        try {s_dtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("s_dtnasc")));} catch (Exception e) {s_dtnasc.getEditor().clear();}
        try {s_dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("s_dtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {s_dtnasc.setValue(null);}

        try {s_cpf.setText(rs.getString("s_cpfcnpj")); } catch (SQLException e) {s_cpf.setText(null);}
        try {s_rginsc.setText(rs.getString("s_rginsc"));} catch (SQLException e) {s_rginsc.setText(null);}

        try {s_endereco.setText(rs.getString("s_endereco"));} catch (SQLException e) {s_endereco.setText(null);}
        try {s_numero.setText(rs.getString("s_numero"));} catch (SQLException e) {s_numero.setText(null);}
        try {s_cplto.setText(rs.getString("s_cplto"));} catch (SQLException e) {s_cplto.setText(null);}
        try {s_bairro.setText(rs.getString("s_bairro"));} catch (SQLException e) {s_bairro.setText(null);}
        try {s_cidade.setText(rs.getString("s_cidade"));} catch (SQLException e) {s_cidade.setText(null);}
        try {s_estado.setText(rs.getString("s_estado"));} catch (SQLException e) {s_estado.setText(null);}
        try {s_cep.setText(rs.getString("s_cep"));} catch (SQLException e) {s_cep.setText(null);}

        try {s_nacionalidade.setText(rs.getString("s_nacionalidade"));} catch (SQLException e) {s_nacionalidade.setText(null);}
        try {s_estcivil.getSelectionModel().select(rs.getString("s_estcivil"));} catch (SQLException e) {s_estcivil.getSelectionModel().select(0);}

        List<ptelcontatoModel> datas_tel = null;
        try {datas_tel = new setTels(rs.getString("s_tel")).rString();} catch (SQLException e) {}
        if (datas_tel != null) s_tel.setItems(observableArrayList(datas_tel)); else s_tel.getItems().clear();
        s_tel.setDisable(false);
        try {s_tel.getSelectionModel().select(0);} catch (Exception e) {}

        try {s_mae.setText(rs.getString("s_mae"));} catch (SQLException e) {s_mae.setText(null);}
        try {s_pai.setText(rs.getString("s_pai"));} catch (SQLException e) {s_pai.setText(null);}

        try {s_cargo.setText(rs.getString("s_cargo"));} catch (SQLException e) {s_cargo.setText(null);}
        try {s_renda.setText(rs.getString("s_renda").toString());} catch (SQLException e) {s_renda.setText("0,00");}
    }

    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO socios(\n" +
                    "            s_rgprp, s_rgimv, s_contrato, s_nome, s_sexo, s_dtnasc, " +
                    "            s_cpfcnpj, s_rginsc, s_endereco, s_numero, s_cplto, s_bairro, " +
                    "            s_cidade, s_estado, s_cep, s_nacionalidade, s_estcivil, s_tel, " +
                    "            s_mae, s_pai, s_cargo, s_renda)" +
                    "    VALUES (?, ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?, ?, ?, ?, " +
                    "            ?, ?, ?);";
        } else {
            sql = "UPDATE socios\n" +
                    "   s_rgprp=?, s_rgimv=?, s_contrato=?, s_nome=?, s_sexo=?, " +
                    "       s_dtnasc=?, s_cpfcnpj=?, s_rginsc=?, s_endereco=?, s_numero=?, " +
                    "       s_cplto=?, s_bairro=?, s_cidade=?, s_estado=?, s_cep=?, s_nacionalidade=?, " +
                    "       s_estcivil=?, s_tel=?, s_mae=?, s_pai=?, s_cargo=?, s_renda=?" +
                    " WHERE s_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1;
            pstmt.setString(nid++, s_rgprp);
            pstmt.setString(nid++, s_rgimv);
            pstmt.setString(nid++, s_contrato);

            pstmt.setString(nid++, s_nome.getText());
            pstmt.setString(nid++, s_sexo.getSelectionModel().getSelectedItem().toString());
            pstmt.setDate(nid++, Dates.toSqlDate(s_dtnasc));

            pstmt.setString(nid++, s_cpf.getText());
            pstmt.setString(nid++, s_rginsc.getText());

            pstmt.setString(nid++, s_endereco.getText());
            pstmt.setString(nid++, s_numero.getText());
            pstmt.setString(nid++, s_cplto.getText());
            pstmt.setString(nid++, s_bairro.getText());
            pstmt.setString(nid++, s_cidade.getText());
            pstmt.setString(nid++, s_estado.getText());
            pstmt.setString(nid++, s_cep.getText());

            pstmt.setString(nid++, s_nacionalidade.getText());
            pstmt.setString(nid++, s_estcivil.getSelectionModel().getSelectedItem().toString());
            pstmt.setString(nid++, new getTels(s_tel).toString());

            pstmt.setString(nid++, s_mae.getText());
            pstmt.setString(nid++, s_pai.getText());
            pstmt.setString(nid++, s_cargo.getText());
            pstmt.setBigDecimal(nid++, new BigDecimal(LerValor.Decimal2String(s_renda.getText())));

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        return retorno;
    }

    private void verifyBotoes() {
        if (this.viea == null) return;

        if (!this.viea.contains("I")) s_btincluir.setDisable(true);
        if (!this.viea.contains("A")) s_btgravar.setDisable(true);
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
                s_nome.setText(consulta.getNomerazao());
                s_nome.setDisable(true);
                s_cpf.setText(consulta.getCpfcnpj());
                s_cpf.setDisable(true);
                s_endereco.setText(consulta.getEndereco());
                s_numero.setText(consulta.getNumero());
                s_cplto.setText(consulta.getComplemento());
                s_bairro.setText(consulta.getBairro());
                s_cidade.setText(consulta.getCidade());
                s_estado.setText(consulta.getEstado());
                s_cep.setText(consulta.getCep());
                s_rginsc.setText(consulta.getRginsc());

                // Telefones
                List<ptelcontatoModel> data = new setTels(consulta.getTelefones()).rString();
                s_tel.getItems().clear();
                if (data != null) s_tel.setItems(observableArrayList(data));
                else s_tel.getItems().clear();
                s_tel.setDisable(false);
                try {
                    s_tel.getSelectionModel().select(0);
                    s_tel.getEditor().setText(data.get(0).toString());
                } catch (Exception e) {
                }

                // Emails
                List<pemailModel> dataemail = null;
                String emailp = consulta.getEmails();
                if (emailp != null) {
                    dataemail = new setEmails(emailp, true).rString();
                }
/*
                s_email.getItems().clear();
                if (dataemail != null) s_email.setItems(observableArrayList(dataemail));
                else s_email.getItems().clear();
                s_email.setDisable(false);
                try { s_email.getSelectionModel().select(0); } catch (Exception e) { }
*/
            }
        });
    }
}
