package Usuarios;

import Classes.*;
import Funcoes.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import masktextfield.MaskTextField;
import org.controlsfx.control.table.TableFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;


public class UsuarioController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    ResultSet rs = null;
    String viea = null;
    boolean bInc = false;
    boolean bAlt = false;
    Object[] fields;

    @FXML private AnchorPane usuariosAnchorPane;
    
    @FXML private ImageView foto;
    @FXML private Button scanner;
    @FXML private Button arquivo;
    @FXML private TextField nome;
    @FXML private TextField cpf;
    @FXML private TextField ender;
    @FXML private TextField numero;
    @FXML private TextField cplto;
    @FXML private TextField bairro;
    @FXML private TextField cidade;
    @FXML private TextField estado;
    @FXML private MaskTextField cep;
    @FXML private MenuButton tel_opc;
    @FXML private ComboBox<ptelcontatoModel> tel;
    @FXML private DatePicker dtnasc;
    @FXML private MenuButton email_opc;
    @FXML private ComboBox<pemailModel> email;
    @FXML private TextField login;
    @FXML private TextField password;
    @FXML private CheckBox resenha;
    
    @FXML private TableView<pusuariosModel> listaUser;
    @FXML private TableColumn<pusuariosModel,Integer> listaUser_id;
    @FXML private TableColumn<pusuariosModel,String> listaUser_nome;
    @FXML private Button listaUser_btIncluir;
    @FXML private Button listaUser_btAlterar;
    @FXML private Button listaUser_btExcluir;

    @FXML private Button btGravar;
    @FXML private Button btRetornar;

    @FXML void btIncluir_OnAction(ActionEvent event) {
        bInc = true;
        LimpaTela();
        
        listaUser_btIncluir.setDisable(true);
        listaUser_btExcluir.setDisable(true);
        scanner.setDisable(true); arquivo.setDisable(true);
        btGravar.setDisable(false); btRetornar.setDisable(false);

        listaUser.setDisable(true);
        
        new Controle(this.fields).FieldsEnabled(true);
        nome.requestFocus();
    }

    @FXML void btAlterar_OnAction(ActionEvent event) {
        if (!listaUser.getSelectionModel().isSelected(listaUser.getSelectionModel().getSelectedIndex())) return;
        bInc = false;

        listaUser_btIncluir.setDisable(true);
        listaUser_btExcluir.setDisable(true);
        scanner.setDisable(true); arquivo.setDisable(true);
        btGravar.setDisable(false); btRetornar.setDisable(false);

        listaUser.setDisable(true);
        
        new Controle(this.fields).FieldsEnabled(true);
        nome.requestFocus();
    }

    @FXML void btExcluir_OnAction(ActionEvent event) {
        pusuariosModel pusuario = listaUser.getSelectionModel().getSelectedItem();
        if (pusuario != null) {
            try {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmação!!!");
                alert.setHeaderText("Deseja excluir este usuário?");
                alert.setContentText(pusuario.getNome());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    conn.ExecutarComando("DELETE FROM usuarios WHERE user_id = " + pusuario.getId() + ";");
                    listaUser.getItems().removeAll(pusuario);
                }
            } catch (Exception e) {}
        }        
    }

    @FXML void btGravar_OnAction(ActionEvent event) {
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            pusuariosModel pusuario = listaUser.getSelectionModel().getSelectedItem();
            if (pusuario == null) {
                salvarFuncionario(bInc, -1);
            } else {
                salvarFuncionario(bInc, pusuario.getId());
            }
            listaUser_btIncluir.setDisable(false);
            listaUser_btExcluir.setDisable(false);
            scanner.setDisable(false); arquivo.setDisable(false);
            btGravar.setDisable(false); btRetornar.setDisable(false);
            verifyBotoes();
            if (bInc) populateUsuarios();
            bInc = false; bAlt = false;
            new Controle(this.fields).FieldsEnabled(false);
            listaUser.setDisable(false);
        }
    }

    @FXML void btRetornar_OnAction(ActionEvent event) {
        if (bInc || bAlt) {
            Alert msg = new Alert(AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) return; else {
                bInc = false;
                bAlt = false;
                new Controle(this.fields).FieldsEnabled(false);
                listaUser.setDisable(false);
                //return;
            }
        }
        try {usuariosAnchorPane.fireEvent(new paramEvent(new String[] {"Usuario"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
    }

    @FXML void listaUsers_newKeyRelease(KeyEvent event) {
        SelecionaUsuario();
    }

    @FXML void listaUsers_newButtonRelease() {
        SelecionaUsuario();
    }

    @FXML void email_adc(ActionEvent event) {
        adcEmails dialog = new adcEmails();
        Optional<pemailModel> result = dialog.adcEmails(false);
        result.ifPresent(b -> {
            ObservableList<pemailModel> emails = email.getItems();
            emails.addAll(b);
            email.setItems(emails);
            try {email.getSelectionModel().select(0);} catch (Exception e) {}
        });
    }

    @FXML void email_del(ActionEvent event) {
        if (!email.getItems().isEmpty()) email.getItems().removeAll(email.getSelectionModel().getSelectedItem());
        try {email.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void tel_adc(ActionEvent event) {
        adcTelefones dialog = new adcTelefones();
        Optional<ptelcontatoModel> result = dialog.adcTelefones();
        result.ifPresent(b -> {
            ObservableList<ptelcontatoModel> tels = tel.getItems();
            tels.addAll(b);
            tel.setItems(tels);
            try {tel.getSelectionModel().select(0);} catch (Exception e) {}
            //System.out.println("DDD=" + b.getDdd() + ", Telefone=" + b.getTelf() + ", Tipo=" + b.getTipo());
        });

    }

    @FXML void tel_del(ActionEvent event) {
        if (!tel.getItems().isEmpty()) tel.getItems().removeAll(tel.getSelectionModel().getSelectedItem());
        try {tel.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML void ender_OnMouseClicked() {

    }

    @FXML void cep_OnAction(ActionEvent event) {

    }

     @FXML void arquivo_OnAction(ActionEvent event) {
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            pusuariosModel pusuario = listaUser.getSelectionModel().getSelectedItem();
            if (pusuario != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Selecione o arquivo de foto");
                fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    String imgPath = selectedFile.getAbsolutePath();
                    File file = new File(imgPath);
                    Image pfoto = new Image(file.toURI().toString());
                    foto.setImage(pfoto);

                    salvarFuncionarioFotoCorpo(imgPath, pusuario.getId());
                }
            }
        }
    }

    @FXML void rotinas_OnDragDetect(MouseEvent event) {
//        // drag was detected, start drag-and-drop gesture
//        urotinasModel selected = (urotinasModel)rotinas.getSelectionModel().getSelectedItem();
//        if (selected != null) {
//            Dragboard db = rotinas.startDragAndDrop(TransferMode.ANY);
//            ClipboardContent content = new ClipboardContent();
//            content.putString(selected.toString());
//            db.setContent(content);
//            event.consume();
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fields = new Object[] {foto, nome, cpf, ender, numero, cplto, bairro, cidade, estado, cep, tel_opc,
                tel, dtnasc, email_opc, email, login, password, resenha
        };

        usuariosAnchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || new InputVerify().InputVerify(event)) {
                // auditor
                // usuario, tela, data, hora, campo, valor velho, valor novo
                bAlt = true;
            }
        });
        usuariosAnchorPane.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });

        Platform.runLater(() -> { verifyBotoes(); });

        // Setar Campos
        MaskFieldUtil.maxField(cpf,15);
        MaskFieldUtil.cpfCnpjField(cpf);
        cpf.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (cpf.getText() == null) return;
            String tcpf = cpf.getText().replace(".", "");
            tcpf = tcpf.replace("-", "");
            tcpf = tcpf.replace("/", "");

            if (newValue) {
                //gotfocus
                cpf.setText(cpf.getText().replace(".", ""));
                cpf.setText(cpf.getText().replace("-", ""));
                cpf.setText(cpf.getText().replace("/", ""));
            } else {
                if (tcpf.length() < 11 || tcpf.length() > 11) {
                    Alert alerta = new Alert(AlertType.INFORMATION);
                    alerta.setTitle("Atenção");
                    alerta.setContentText("Não é um cpf válido!");
                    alerta.showAndWait();
                    cpf.requestFocus();
                    return;
                }
                if (tcpf == "00000000000") return;
                if (!MaskFieldUtil.isCpf(tcpf)) {
                    Alert alerta = new Alert(AlertType.INFORMATION);
                    alerta.setTitle("Atenção");
                    alerta.setContentText("Não é um cpf válido!");
                    alerta.showAndWait();
                    cpf.requestFocus();
                    return;
                }
            }
        });

        new cpoTelefones().cpoTelefones(tel);
        new cpoEmails().cpoEmails(email);

        //rotinas_icone.setCellValueFactory(cellData -> cellData.getValue().getIcon());

        populateUsuarios();
        populateRotinas();
        
        LimpaTela();

        new Controle(this.fields).Focus();
        new Controle(this.fields).FieldsEnabled(false);
    }

    private void SelecionaUsuario() {
        pusuariosModel pusuario = listaUser.getSelectionModel().getSelectedItem();
        if (pusuario != null) {
            lerUsuarios(pusuario.getId());
        }
    }
    
    private void populateUsuarios() {
        List<pusuariosModel> data = new ArrayList<pusuariosModel>();
        ResultSet user;
        String qSQL = "SELECT user_id, user_nome FROM usuarios ORDER BY Upper(user_nome);";
        try {
            user = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (user.next()) {
                int qid = -1; String qnome = null;
                try {qid = user.getInt("user_id");} catch (SQLException e) {}
                try {qnome = user.getString("user_nome");} catch (SQLException e) {}
                data.add(new pusuariosModel(qid, qnome));
            }
            DbMain.FecharTabela(user);
        } catch (SQLException e) {}
        
        listaUser_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        listaUser_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        listaUser.setItems(FXCollections.observableArrayList(data));

        TableFilter<pusuariosModel> tableFilter = new TableFilter<pusuariosModel>(listaUser);
    }

    private void populateRotinas() {
        List<urotinasModel> data = new ArrayList<urotinasModel>();
        ResultSet rot;
        String qSQL = "SELECT * FROM rotinas ORDER BY rot_id;";
        try {
            rot = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rot.next()) {
                int qid = -1; String qdesc = null; String qlook = null; 
                String qoptions = null; String qicon = null;
                try {qid = rot.getInt("rot_id");} catch (SQLException e) {}
                try {qdesc = rot.getString("rot_desc");} catch (SQLException e) {}
                try {qlook = rot.getString("rot_look");} catch (SQLException e) {}
                try {qicon = rot.getString("rot_icon");} catch (SQLException e) {}
                try {qoptions = rot.getString("rot_options");} catch (SQLException e) {}
                data.add(new urotinasModel(qid, qdesc, qicon, qlook, qoptions));
            }
            DbMain.FecharTabela(rot);
        } catch (SQLException e) {}
        
    }

    private void LimpaTela() {
        foto.setImage(null);
        MaskFieldUtil.maxField(nome, 60); nome.setText(null);
        cpf.setText(null);
        MaskFieldUtil.maxField(ender, 60); ender.setText(null);
        MaskFieldUtil.maxField(numero, 10); numero.setText(null);
        MaskFieldUtil.maxField(cplto, 25); cplto.setText(null);
        MaskFieldUtil.maxField(bairro, 30); bairro.setText(null);
        MaskFieldUtil.maxField(cidade, 30); cidade.setText(null);
        MaskFieldUtil.maxField(estado, 2); estado.setText(null);
        cep.setText(null); cep.setMask("NNNNN-NNN");
        tel.getItems().clear();
        dtnasc.getEditor().clear();
        email.getItems().clear();
        MaskFieldUtil.maxField(login, 15); login.setText(null);
        MaskFieldUtil.maxField(password, 15); password.setText(null);
        resenha.setSelected(false);        
    }
    
    private void lerUsuarios(int uid) {
        vieaToArray oviea = new vieaToArray(this.viea);
        String[] viea1 = oviea.getViea();
        
        String qSql = "SELECT user_id, user_foto, user_nome, user_cpf, user_enderereco, user_numero, " +
                      "user_cplto, user_bairro, user_cidade, user_estado, user_cep, " +
                      "user_tel, user_dtnasc, user_email, user_login, user_passwd, user_repwd" +
                      " FROM usuarios WHERE user_id = " + uid + ";";
        rs = conn.AbrirTabela(qSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                try {foto.setImage(capturaImagemRosto(uid));} catch (Exception e) {foto.setImage(null);}
                try {nome.setText(rs.getString("user_nome"));} catch (SQLException e) {nome.setText(null);}
                try {cpf.setText(rs.getString("user_cpf"));} catch (SQLException e) {cpf.setText(null);}
                try {ender.setText(rs.getString("user_enderereco"));} catch (SQLException e) {ender.setText(null);}
                try {numero.setText(rs.getString("user_numero"));} catch (SQLException e) {numero.setText(null);}
                try {cplto.setText(rs.getString("user_cplto"));} catch (SQLException e) {cplto.setText(null);}
                try {bairro.setText(rs.getString("user_bairro"));} catch (SQLException e) {bairro.setText(null);}
                try {cidade.setText(rs.getString("user_cidade"));} catch (SQLException e) {cidade.setText(null);}
                try {estado.setText(rs.getString("user_estado"));} catch (SQLException e) {estado.setText(null);}
                try {cep.setText(rs.getString("user_cep"));} catch (SQLException e) {cep.setText(null); cep.setMask("NNNNN-NNN");}

                // Telefone de contato
                List<ptelcontatoModel> data = null;
                try {data = new setTels(rs.getString("user_tel")).rString();} catch (SQLException e) {}
                if (!data.isEmpty()) tel.setItems(observableArrayList(data)); else tel.getItems().clear();
                tel.setDisable(false);
                try {tel.getSelectionModel().select(0);} catch (Exception e) {}
                
                try {dtnasc.getEditor().setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("user_dtnasc")));} catch (Exception e) {dtnasc.getEditor().clear();}
                try {dtnasc.setValue(Dates.toLocalDate(Dates.StringtoDate(rs.getDate("user_dtnasc").toString(), "yyyy-MM-dd")));} catch (Exception e) {dtnasc.setValue(null);}
                
                //email;
                List<pemailModel> dataemail = null;
                try {dataemail = new setEmails(rs.getString("user_email"),false).rString();} catch (SQLException e) {}
                if (!dataemail.isEmpty()) email.setItems(observableArrayList(dataemail)); else email.getItems().clear();
                email.setDisable(false);
                try {email.getSelectionModel().select(0);} catch (Exception e) {}
                
                try {login.setText(rs.getString("user_login"));} catch (SQLException e) {login.setText(null);}
                try {password.setText(rs.getString("user_passwd"));} catch (SQLException e) {password.setText(null);}
                try {resenha.setSelected(rs.getBoolean("user_repwd"));} catch (SQLException e) {resenha.setSelected(false);}     
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {}
    }
    
    private void verifyBotoes() {
        //System.out.println(this.viea);
        if (this.viea == null) return;
        if (!this.viea.contains("I")) listaUser_btIncluir.setDisable(true);
        if (!this.viea.contains("E")) listaUser_btExcluir.setDisable(true);
        if (!this.viea.contains("A")) btGravar.setDisable(true);        
    }

    public boolean salvarFuncionario(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO usuarios (user_nome, user_cpf, user_enderereco, " +
                  "user_numero, user_cplto, user_bairro, user_cidade, user_estado, " +
                  "user_cep, user_tel, user_dtnasc, user_email, user_login, " +
                  "user_passwd, user_repwd) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        } else {
            sql = "UPDATE usuarios SET user_nome=?, user_cpf=?, user_enderereco=?, " +
                  "user_numero=?, user_cplto=?, user_bairro=?, user_cidade=?, user_estado=?, " +
                  "user_cep=?, user_tel=?, user_dtnasc=?, user_email=?, user_login=?, " +
                  "user_passwd=?, user_repwd=? WHERE user_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            pstmt.setString(1, nome.getText());
            pstmt.setString(2, cpf.getText());
            pstmt.setString(3, ender.getText());
            pstmt.setString(4, numero.getText());
            pstmt.setString(5, cplto.getText());
            pstmt.setString(6, bairro.getText());
            pstmt.setString(7, cidade.getText());
            pstmt.setString(8, estado.getText());
            pstmt.setString(9, cep.getText());
            pstmt.setString(10, new getTels(tel).toString());
            pstmt.setDate(11, Dates.toSqlDate(dtnasc));
            pstmt.setString(12, new getEmails(email,false).toString());
            pstmt.setString(13, login.getText());
            pstmt.setString(14, password.getText());
            pstmt.setInt(15, resenha.isSelected() ? 1 : 0);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {retorno = false;}
        
        return retorno;
    }
    
    public boolean salvarFuncionarioFotoCorpo(String imagem, int Id) {  
        String insertRow = "UPDATE usuarios SET user_foto=? WHERE user_id = "  
                + Id + ";";  
        try {  
            PreparedStatement pstmt = conn.conn.prepareStatement(insertRow);  
            File imagemFile = new File(imagem);  
            byte[] imagemArray = new byte[(int) imagemFile.length()];  
            DataInputStream imagemStream = new DataInputStream(new FileInputStream(imagemFile));  
            imagemStream.readFully(imagemArray);  
            imagemStream.close();  
            pstmt.setBytes(1, imagemArray);  
            pstmt.executeUpdate();  
        } catch (SQLException | IOException e) {}  
        return true;  
    }  

    public Image capturaImagemRosto(int Id) {  
        byte[] imageByte = null;  
        Image image = null;  
        String sql = "SELECT user_foto FROM usuarios WHERE user_id = " + Id;  
        try {  
            Statement stm = conn.conn.createStatement();  
            ResultSet rset = stm.executeQuery(sql);  

            rset.next();
            imageByte = rset.getBytes("user_foto");
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageByte));
            Image icon = SwingFXUtils.toFXImage(bi, null);
            return icon;
        } catch (SQLException | IOException e) {}
        return image;  
    }      
}
