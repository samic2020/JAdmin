package entrada;

import Classes.paramEvent;
import Funcoes.CriptografiaUtil;
import Funcoes.DbMain;
import Funcoes.SettingPwd;
import Funcoes.VariaveisGlobais;
import java.awt.AWTException;
import java.awt.Robot;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static java.awt.image.ImageObserver.WIDTH;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.ESCAPE;

/**
 * Created by supervisor on 04/11/16.
 */
public class LoginController implements Initializable {
    @FXML AnchorPane anchorPane;
    @FXML ImageView imgLogo;
    @FXML private ComboBox<String> lUnidade;
    @FXML private TextField lUsuario;
    @FXML private PasswordField lSenha;
    @FXML private Label lbMsg;

    private DbMain conn = null;
    private FadeTransition trans;

    private void Initialize() {
        new SettingPwd();
        String crySenha  = System.getProperty("Key", "7kf51b");
        if (crySenha != "7kf51b") {
            VariaveisGlobais.KeyPwd = CriptografiaUtil.decrypt(crySenha, CriptografiaUtil.ALGORITMO_AES, CriptografiaUtil.ALGORITMO_AES);
        } else VariaveisGlobais.KeyPwd = crySenha;
        System.out.println("pwd = " + VariaveisGlobais.KeyPwd);

        lUnidade.setDisable(false);
        lUsuario.setDisable(true);
        lSenha.setDisable(true);

        Platform.runLater(() -> {
            trans = new FadeTransition(Duration.seconds(2), anchorPane);
            trans.setFromValue(1.0);
            trans.setToValue(.20);
            trans.setCycleCount(FadeTransition.INDEFINITE);
            trans.setAutoReverse(true);
            trans.play();


            for (Object[] unidade : VariaveisGlobais.unidades) {
                String unit = unidade[0].toString();
                lUnidade.getItems().addAll(unit);
            }

            lUnidade.setOnKeyReleased(event -> {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    Stage stage = (Stage) lUnidade.getScene().getWindow();
                    stage.close();
                }

                if (event.getCode().equals(KeyCode.ENTER)) {
                    anchorPane.setOpacity(100);
                    trans.stop();
                    int _pos = lUnidade.getSelectionModel().getSelectedIndex();
                    boolean bPode = false;
                    if (_pos > -1) {
                        if (!VariaveisGlobais.unidades[_pos][0].toString().trim().equals("")) VariaveisGlobais.unidade = VariaveisGlobais.unidades[_pos][0].toString();
                        if (!VariaveisGlobais.unidades[_pos][1].toString().trim().equals("")) VariaveisGlobais.dbnome = VariaveisGlobais.unidades[_pos][1].toString();
                        if (!VariaveisGlobais.unidades[_pos][2].toString().trim().equals("")) VariaveisGlobais.dbsenha = (Boolean)VariaveisGlobais.unidades[_pos][2];

                        System.out.println(VariaveisGlobais.unidade + ", " + VariaveisGlobais.dbnome + ", " + VariaveisGlobais.dbsenha);
                        try { conn.FecharConexao(); conn = null;} catch (Exception e) {}
                        conn = new DbMain(VariaveisGlobais.unidade,"postgres",VariaveisGlobais.KeyPwd,VariaveisGlobais.dbnome);

                        VariaveisGlobais.conexao = conn;
                        try {
                            if (conn.conn == null) {
                                lbMsg.setText("Unidade fora do Ar!!!");
                                lbMsg.setTextFill(Color.RED);

                                lUsuario.setDisable(true);
                                lSenha.setDisable(true);

                                bPode = false;
                                lUnidade.requestFocus();
                            } else {
                                lUsuario.setDisable(false);
                                try {lSenha.setDisable(true);} catch (Exception e) {}
                                lbMsg.setText(null);
                                bPode = true;
                            }
                        } catch (Exception e) {}
                    } else {
                        lUsuario.setDisable(true);
                        lSenha.setDisable(true);
                        bPode = false;
                        lUnidade.requestFocus();
                    }
                    if (bPode) lUsuario.requestFocus();
                }
            });

            lUnidade.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (newPropertyValue) {
                        // on focus
                        try {
                            lUsuario.setText(null);
                            lUsuario.setDisable(true);
                            lSenha.setText(null);
                            lbMsg.setText(null);
                            trans.play();
                        } catch (NullPointerException e) {}
                    }
                }
            });

            lUsuario.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == ESCAPE) {
                    lUsuario.setText(null); lUsuario.setDisable(true);
                    try {lSenha.setText(null); lSenha.setDisable(true);} catch (RuntimeException e) {}
                    lUnidade.requestFocus();
                }
                if (t.getCode() == ENTER && lUsuario.getText() != "") {
                    try {lSenha.setDisable(false);} catch (RuntimeException e) {}
                    lSenha.requestFocus();
                }
            });
            lUsuario.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (newPropertyValue) {
                        // on focus
                        try {
                            lUsuario.setText(null);
                            lUsuario.setDisable(false);
                            lSenha.setText("");
                            lSenha.setDisable(true);
                            lbMsg.setText(null);
                        } catch (NullPointerException e) {} catch (RuntimeException e) {}
                    }
                }
            });

            lSenha.disableProperty().bind(lUsuario.textProperty().isEmpty());
            lSenha.setOnKeyReleased((KeyEvent event) -> {
                if (event.getCode() == ESCAPE) {
                    lSenha.setText(null);
                    lUsuario.setText(null);
                    lUsuario.requestFocus();
                    return;
                }

                if (event.getCode() == ENTER && lSenha.getText() != "") {
                    String mnome = "", mcpf = "", uid = "", mcargo = "USU", mprotocolo = null;
                    ResultSet rs = conn.AbrirTabela("SELECT user_id, user_login, user_passwd, user_nome, user_cpf, user_cargo, user_protocolo FROM usuarios WHERE user_login = '" + lUsuario.getText().trim() + "' AND user_passwd = '" + lSenha.getText().trim() + "';", WIDTH);
                    try {
                        if (rs.next()) {
                            lbMsg.setText(null);
                            uid = rs.getString("user_id");
                            mnome = rs.getString("user_nome");
                            mcpf = rs.getString("user_cpf");
                            mcargo = rs.getString("user_cargo");
                            mprotocolo = rs.getString("user_protocolo");
                        } else {
                            lbMsg.setText("Usuário ou Senha não conferem!!!");
                            lbMsg.setTextFill(Color.RED);
                            try {
                                lUsuario.setText(null);
                                lSenha.setText(null);
                            } catch (RuntimeException e) {}
                            lUsuario.requestFocus();
                            return;
                        }
                    } catch (SQLException ex) {}

                    VariaveisGlobais.user_id = uid;
                    VariaveisGlobais.usuario = lUsuario.getText().toLowerCase().trim();
                    VariaveisGlobais.senha = lSenha.getText().toLowerCase().trim();
                    VariaveisGlobais.cargo = mcargo;
                    VariaveisGlobais.protocolo = mprotocolo;

                    DbMain.FecharTabela(rs);

                    System.out.println("logado...");

                    // Verifica se existe a variavel caixa_aut no postgresql
                    // Pegar a ultima autenticação no caixa
                    int maxaut = -1;
                    rs = conn.AbrirTabela(VariaveisGlobais.pegarMaxAutenticacao,ResultSet.CONCUR_READ_ONLY);
                    try {
                        if (rs.next()) {
                            maxaut = rs.getInt("autenticacao");
                        }
                    } catch (SQLException e) {}
                    DbMain.FecharTabela(rs);
                    if (maxaut != -1) {
                        String atualiza = String.format(VariaveisGlobais.criaAutenticacao,++maxaut);
                        try { conn.ExecutarComando(atualiza); } catch (Exception e) {}
                    }

                    // Avisa tela anterior que deve fechar esta tela
                    //anchorPane.getParent().getParent().getParent().setVisible(false);
                    anchorPane.fireEvent(new paramEvent(new String[] {"Login"},paramEvent.GET_PARAM));

/*
                    // Ler parametros Iniciais - 06/02/2017
                    new Calculos_mujucoep();
                    new Calculos.Config().Config_ADM();
                    new Calculos.Config().Config_AC();
                    new Calculos.Config().Config_CA();
                    new Calculos.Config().Config_BA();
                    new Calculos.Config().Config_BB();
                    new Calculos.Config().Config_Email();
                    new Calculos.Config().Config_MsgProp();
*/

                }
            });

            lUnidade.setFocusTraversable(true);
            lUnidade.getSelectionModel().select(0);
            lUnidade.requestFocus();

            try {
                Robot robot = new Robot();
                robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
                robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);

                robot.keyPress(java.awt.event.KeyEvent.VK_W);
                robot.keyRelease(java.awt.event.KeyEvent.VK_W);
                robot.keyPress(java.awt.event.KeyEvent.VK_E);
                robot.keyRelease(java.awt.event.KeyEvent.VK_E);
                robot.keyPress(java.awt.event.KeyEvent.VK_L);
                robot.keyRelease(java.awt.event.KeyEvent.VK_L);
                robot.keyPress(java.awt.event.KeyEvent.VK_L);
                robot.keyRelease(java.awt.event.KeyEvent.VK_L);
                robot.keyPress(java.awt.event.KeyEvent.VK_S);
                robot.keyRelease(java.awt.event.KeyEvent.VK_S);
                robot.keyPress(java.awt.event.KeyEvent.VK_P);
                robot.keyRelease(java.awt.event.KeyEvent.VK_P);
                robot.keyPress(java.awt.event.KeyEvent.VK_I);
                robot.keyRelease(java.awt.event.KeyEvent.VK_I);
                robot.keyPress(java.awt.event.KeyEvent.VK_N);
                robot.keyRelease(java.awt.event.KeyEvent.VK_N);
                robot.keyPress(java.awt.event.KeyEvent.VK_T);
                robot.keyRelease(java.awt.event.KeyEvent.VK_T);
                robot.keyPress(java.awt.event.KeyEvent.VK_O);
                robot.keyRelease(java.awt.event.KeyEvent.VK_O);

                robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
                robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);

                robot.keyPress(java.awt.event.KeyEvent.VK_3);
                robot.keyRelease(java.awt.event.KeyEvent.VK_3);
                robot.keyPress(java.awt.event.KeyEvent.VK_F);
                robot.keyRelease(java.awt.event.KeyEvent.VK_F);
                robot.keyPress(java.awt.event.KeyEvent.VK_G);
                robot.keyRelease(java.awt.event.KeyEvent.VK_G);
                robot.keyPress(java.awt.event.KeyEvent.VK_6);
                robot.keyRelease(java.awt.event.KeyEvent.VK_6);
                robot.keyPress(java.awt.event.KeyEvent.VK_4);
                robot.keyRelease(java.awt.event.KeyEvent.VK_4);
                robot.keyPress(java.awt.event.KeyEvent.VK_1);
                robot.keyRelease(java.awt.event.KeyEvent.VK_1);

                robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
                robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);

            } catch (AWTException e) {}

        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { Initialize(); }

}
