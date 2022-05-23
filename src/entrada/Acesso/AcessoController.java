package entrada.Acesso;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AcessoController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPane;
    @FXML private VBox vboxArea;
    @FXML private ComboBox<cUser> acsUser;
    @FXML private TextField acsCargo;
    @FXML private CheckBox acsInc;
    @FXML private CheckBox acsExc;
    @FXML private CheckBox acsAlt;
    @FXML private Button btnMostrar;
    @FXML private Button btnGravar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnMostrar.setDisable(false);
        btnGravar.setDisable(true);

        acsInc.setDisable(true);
        acsAlt.setDisable(true);
        acsExc.setDisable(true);

        populateUsers();

        btnMostrar.setOnAction(event -> {
            btnMostrar.setDisable(true);
            btnGravar.setDisable(false);

            acsUser.setDisable(true);
            MonatMenuPrincipal(acsUser.getSelectionModel().selectedItemProperty().getValue().getProtocolo());
        });

        btnGravar.setOnAction(event -> {
            btnMostrar.setDisable(false);
            btnGravar.setDisable(true);
            acsUser.setDisable(false);

            String tProt = "";
            for (Object jb : vboxArea.getChildren()) {
                switch (jb.getClass().getSimpleName()) {
                    case "Label":
                        System.out.println(((Label)jb).getText());
                        break;
                    case "CheckBox":
                        if (((CheckBox)jb).isSelected()) {
                            if (((CheckBox)jb).isSelected()) {
                                String tViea = ((CheckBox)jb).getId();
                                String gViea = "";
                                if (tViea != "") {
                                    gViea = ":V" + tViea;
                                }
                                tProt += ((CheckBox)jb).getUserData() + gViea + ";";
                            }
                            //System.out.println(((CheckBox)jb).getUserData());
                        }
                        break;
                }
            }
            vboxArea.getChildren().clear();
            String updateSql = "UPDATE usuarios SET user_protocolo = ? WHERE user_id = ?;";
            conn.ExecutarComando(updateSql, new Object[][] {{"string", tProt},{"int", Integer.parseInt(acsUser.getSelectionModel().getSelectedItem().getCod())}});
            //System.out.println(tProt);
        });
    }

    private void populateUsers() {
        acsUser.getItems().clear();

        List<cUser> data = new ArrayList<cUser>();
        ResultSet imv;
        String qSQL = "SELECT user_id, user_login, user_cargo, user_protocolo FROM usuarios;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qid = null, qnome = null, qcargo = null, qprot = null;
                try {qid = imv.getString("user_id");} catch (SQLException e) {}
                try {qnome = imv.getString("user_login");} catch (SQLException e) {}
                try {qcargo = imv.getString("user_cargo");} catch (SQLException e) {}
                try {qprot = imv.getString("user_protocolo");} catch (SQLException e) {}

                data.add(new cUser(qid, qnome, qcargo, qprot));
            }
            imv.close();
        } catch (SQLException e) {}

        if (data != null) {
            acsUser.setItems(FXCollections.observableArrayList(data));
        } else acsUser.getItems().clear();
        acsUser.setOnAction(event -> acsCargo.setText(acsUser.getSelectionModel().getSelectedItem().getCargo()));
    }

    private void MonatMenuPrincipal(String protocolo) {
        ResultSet mnu = null;
        String mnuSQL = "SELECT rot_id, rot_desc, rot_call, rot_shortcut, rot_icon, rot_look, rot_options, resize, close, menu, submenu FROM rotinas ORDER BY menu, submenu;";
        mnu = conn.AbrirTabela(mnuSQL, ResultSet.CONCUR_READ_ONLY);

        String rot_desc = "", rot_call = "", rot_shortcut = "", rot_icon = "", rot_options = "";
        int rot_look = 0;
        boolean resize = false, close = false;
        int imenu = -1, isubmenu = -1;
        try {
            while (mnu.next()) {
                rot_desc = TrocaNome(mnu.getString("rot_desc"));
                rot_call = mnu.getString("rot_call");
                rot_shortcut = mnu.getString("rot_shortcut");
                rot_icon = mnu.getString("rot_icon");
                rot_look = mnu.getInt("rot_look");
                rot_options = mnu.getString("rot_options");
                resize = mnu.getBoolean("resize");
                close = mnu.getBoolean("close");

                imenu = mnu.getInt("menu");
                isubmenu = mnu.getInt("submenu");

                Object[] ttmenu = new Object[]{rot_desc, rot_call, rot_shortcut, rot_icon, rot_look, rot_options, resize, close};
                Object[] sProtocolo = SeekInProtocol(protocolo, imenu, isubmenu);
                if (isubmenu == 0) {
                    CheckBox menu = new CheckBox(ttmenu[0].toString());
                    //TextField field1 = new TextField();
                    //menu.setLabelFor(field1);
                    menu.setMnemonicParsing(true);
                    menu.setPrefSize(300, 25);
                    menu.setPadding(new Insets(5, 5, 5, 5));
                    menu.setStyle("-fx-text-fill: blue; -fx-font-family: 'Arial Black'; -fx-font-size: 12; -fx-stroke-width: 5;-fx-stroke: green;");
                    menu.setUserData(String.valueOf(imenu) + "," + String.valueOf(isubmenu));
                    menu.setId("");
                    menu.setSelected((Boolean) sProtocolo[0]);
                    vboxArea.getChildren().addAll(menu);
                    //System.out.println(String.valueOf(imenu) + "," + String.valueOf(isubmenu) + ";");
                } else {
                    CheckBox submenu = new CheckBox(ttmenu[0].toString());
                    //submenu.setText(ttmenu[0].toString());
                    submenu.setPrefSize(290, 25);
                    submenu.setPadding(new Insets(5, 15, 5, 10));
                    submenu.setSelected((Boolean) sProtocolo[0]);
                    submenu.setStyle(String.valueOf(imenu) + "," + String.valueOf(isubmenu));
                    submenu.setId(sProtocolo[1].toString());
                    String cssColor = "darkgreen";
                    if (ttmenu[5] != null) {
                        if (((String) sProtocolo[1]).length() > 0 || ttmenu[5].toString().contains("V") || ttmenu[5].toString().contains("I") || ttmenu[5].toString().contains("E") || ttmenu[5].toString().contains("A")) {
                            cssColor = "red";

                            acsInc.setDisable(false);
                            acsAlt.setDisable(false);
                            acsExc.setDisable(false);

                            submenu.focusedProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue) {
                                    // onfocus
                                    acsInc.setDisable(false);
                                    acsAlt.setDisable(false);
                                    acsExc.setDisable(false);

                                    acsInc.setSelected(sProtocolo[1].toString().contains("I"));
                                    acsExc.setSelected(sProtocolo[1].toString().contains("E"));
                                    acsAlt.setSelected(sProtocolo[1].toString().contains("A"));
                                } else {
                                    // outfocus
                                }
                            });
                            submenu.setOnAction(event -> {
                                acsInc.setOnAction(event1 -> {
                                    if (acsInc.isSelected()) {
                                        sProtocolo[1] = sProtocolo[1].toString() + "I";
                                    } else {
                                        sProtocolo[1] = sProtocolo[1].toString().replace("I","");
                                    }
                                    submenu.setId(sProtocolo[1].toString());
                                });
                                acsExc.setOnAction(event1 -> {
                                    if (acsExc.isSelected()) {
                                        sProtocolo[1] = sProtocolo[1].toString() + "E";
                                    } else {
                                        sProtocolo[1] = sProtocolo[1].toString().replace("E","");
                                    }
                                    submenu.setId(sProtocolo[1].toString());
                                });
                                acsAlt.setOnAction(event1 -> {
                                    if (acsAlt.isSelected()) {
                                        sProtocolo[1] = sProtocolo[1].toString() + "A";
                                    } else {
                                        sProtocolo[1] = sProtocolo[1].toString().replace("A","");
                                    }
                                    submenu.setId(sProtocolo[1].toString());
                                });
                            });
                        } else {
                            acsInc.setSelected(false);
                            acsExc.setSelected(false);
                            acsAlt.setSelected(false);
                            acsInc.setDisable(true);
                            acsAlt.setDisable(true);
                            acsExc.setDisable(true);
                            submenu.setId("");
                        }
                    } else {
                        acsInc.setSelected(false);
                        acsExc.setSelected(false);
                        acsAlt.setSelected(false);
                        acsInc.setDisable(true);
                        acsAlt.setDisable(true);
                        acsExc.setDisable(true);
                        submenu.setId("");
                    }

                    submenu.setStyle("-fx-font-weight: bold; -fx-text-fill: " + cssColor + ";");
                    submenu.setUserData(String.valueOf(imenu) + "," + String.valueOf(isubmenu));
                    vboxArea.getChildren().addAll(submenu);
                    //System.out.println(String.valueOf(imenu) + "," + String.valueOf(isubmenu) + ";");
                }
            }
        } catch (SQLException e) { }
        DbMain.FecharTabela(mnu);
    }

    private String TrocaNome(String value) {
        for (int contas = 0; contas <= VariaveisGlobais.contas_ca.size() - 1; contas++) {
            value = value.replace("[" + VariaveisGlobais.contas_ca.key(contas) + "]", VariaveisGlobais.contas_ca.get(VariaveisGlobais.contas_ca.key(contas)));
        }
        return value;
    }

    private Object[] SeekInProtocol(String protocolo, int menu, int submenu) {
        boolean retorno = false; String viea = "";
        if (protocolo == null) {
            retorno = true;
            viea = "";
        } else {
            String tmpSeek = menu + "," + submenu;
            String[] aproto = protocolo.split(";");
            if (aproto.length > 0) {
                int npos = FuncoesGlobais.FindLike(aproto, tmpSeek);
                if (npos > -1) {
                    retorno = true;
                    int spos = aproto[npos].indexOf(":");
                    if (spos > -1) {
                        viea = aproto[npos].substring(spos + 1);
                    } else viea = "";
                }
            } else {
                retorno = true;
                viea = "";
            }
        }
        return new Object[] {retorno,viea};
    }

}