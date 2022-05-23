package Imoveis;

import Classes.*;
import Funcoes.*;
import Imoveis.Fotos.fotosImovel;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.util.Callback;
import masktextfield.MaskTextField;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
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
import javax.swing.JOptionPane;

public class ImovelController implements Initializable {
    int iID = -1;
    String i_rgprp = null;
    
    DbMain conn = VariaveisGlobais.conexao;
    String iSql = "SELECT * FROM imoveis WHERE exclusao is null ORDER BY i_rgprp, i_rgimv;";
    ResultSet rs = null;
    String viea = null;
    Object[] param = null;
    boolean bInc = false;
    Object[] fields;
    Object[] buttons;

    @FXML private AnchorPane anchorImovel;
    @FXML private Pane i_pane;

    @FXML private TextField i_rgimv;
    @FXML private ComboBox<String> i_tipo;
    @FXML private ComboBox<String> i_situacao;
    @FXML private TextField i_endereco;
    @FXML private Button i_btEndereco;
    @FXML private TextField i_num;
    @FXML private TextField i_cplto;
    @FXML private TextField i_bairro;
    @FXML private TextField i_cidade;
    @FXML private TextField i_cdmun;
    @FXML private TextField i_estado;
    @FXML private ComboBox<String> i_ur;
    @FXML private MaskTextField i_cep;
    @FXML private MenuButton i_mat_menu;
    @FXML private ComboBox<pmatriculaModel> i_matriculas;
    
    @FXML private MenuItem btMatExcluir;
    @FXML void btMatExcluir_OnAction(ActionEvent event) {
        if (!i_matriculas.getItems().isEmpty()) i_matriculas.getItems().removeAll(i_matriculas.getSelectionModel().getSelectedItem());
        try {i_matriculas.getSelectionModel().select(0);} catch (Exception e) {}
    }    
    
    @FXML private CheckBox i_retertaxas;
    @FXML private CheckBox i_Dimob;
    @FXML private TextArea i_descricao;
    @FXML private TextField i_msg;
    @FXML private TextField i_obs;
    @FXML private CheckBox i_anunciar;

    @FXML private ComboBox<pimovelModel> i_tipoimv;
    @FXML private TableView<panuncioModel> i_anuncios;
    @FXML private TableColumn<panuncioModel, String> i_campo;
    @FXML private TableColumn<panuncioModel, String> i_conteudo;

    @FXML private MenuButton i_tpimovel;
    @FXML private MenuItem i_tpimovel_adc;
    @FXML void i_tpimovel_adc_OnAction(ActionEvent event) {
        adcImoveis dialog = new adcImoveis();
        Optional<pimovelModel> result = dialog.adcImoveis();
        result.ifPresent(b -> {
            ObservableList<pimovelModel> tpimovel = i_tipoimv.getItems();
            tpimovel.addAll(b);
            i_tipoimv.setItems(tpimovel);
            try {i_tipoimv.getSelectionModel().select(0);} catch (Exception e) {}
        });

    }

    @FXML private MenuItem i_tpimovel_del;
    @FXML void i_tpimovel_del_OnAction(ActionEvent event) {
        if (!i_tipoimv.getItems().isEmpty()) i_tipoimv.getItems().removeAll(i_tipoimv.getSelectionModel().getSelectedItem());
        try {i_tipoimv.getSelectionModel().select(0);} catch (Exception e) {}
    }

    @FXML private Button i_btInc;
    @FXML private Button i_btAlt;
    @FXML private Button i_btExcluir;
    @FXML private Button i_btBaixar;
    @FXML private Button i_btFotos;
    @FXML private Button i_btGravar;
    @FXML private Button i_btRetornar;

    @FXML private TextField i_reservado;
    @FXML private TextField i_dtreserva;
    @FXML private TextField i_reservtels;
    @FXML private TextField i_dtfinal;

    @FXML private Sphere i_dividido;
    @FXML private Label i_ldividido;

    @FXML void i_btInc_OnAction(ActionEvent event) {
        this.bInc = true;
        LimpaTela();

        i_btBaixar.disableProperty().unbind();
        new Controle("PROPRIETARIO: NOVO - ", this.buttons).BotaoEnabled(new Object[] {i_btGravar, i_btRetornar});
        new Controle("PROPRIETARIO: NOVO - ", this.fields).FieldsEnabled(true);

        i_situacao.getEditor().setText("Vazio");
        //i_anuncios.setDisable(true);
        i_rgimv.setDisable(true);
        i_tipo.requestFocus();
    }

    @FXML void i_btAlt_OnAction(ActionEvent event) {
        this.bInc = false;

        i_btBaixar.disableProperty().unbind();
        new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {i_btGravar, i_btRetornar});
        new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.fields).FieldsEnabled(true);

        i_situacao.getEditor().setText("Vazio");
        //i_anuncios.setDisable(true);
        i_rgimv.setDisable(true);
        i_tipo.requestFocus();
    }

    @FXML void i_btExcluir_OnAction(ActionEvent event) {
        Object[][] avazio = null; String svazio = "VAZIO";
        try {avazio = conn.LerCamposTabela(new String[] {"i_situacao"}, "imoveis", "i_rgimv = ?", new String[][] {{"string",i_rgimv.getText()}}); } catch (Exception e) {}
        if (avazio != null) {
            svazio = avazio[0][3].toString().toUpperCase();
        }
        
        if (!svazio.equalsIgnoreCase("VAZIO")) {
            JOptionPane.showMessageDialog(null, "Imovel tem de estar vazio!");
            return;
        }
        
        Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Deseja excluir este locatario?", new ButtonType("Sim"), new ButtonType("Não"));
        Optional<ButtonType> result = msg.showAndWait();
        if (result.get().getText().equals("Sim")) {
            String sql = "UPDATE imoveis SET exclusao = '%s' WHERE i_rgimv = '%s';";
            sql = String.format(sql, Dates.DateFormata("yyyy-MM-dd", DbMain.getDateTimeServer()), i_rgimv.getText());
            try { conn.ExecutarComando(sql); } catch (Exception e) { }
        }        
    }

    @FXML void i_btBaixar_OnAction(ActionEvent event) {
        try {
            ChamaTela("Baixa", "/Imoveis/Baixa/BaixaImovel.fxml", "loca.png");
            LerImovel();
            verifyBotoes();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @FXML void i_btFotos_OnAction(ActionEvent event) {
        try {
            fotosImovel fim = new fotosImovel();
            fim.setTitulo(i_endereco.getText().trim() + ", " + i_num.getText().trim() + " - " + i_cplto.getText().trim());
            fim.setRgimv(i_rgimv.getText().trim());
            fim.start(new Stage());
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @FXML void i_btGravar_OnAction(ActionEvent event) {
        if (this.viea == null) this.viea = "VIEA";
        if (this.viea.contains("I") || this.viea.contains("A")) {
            if (bInc) {
                int varProp =  Integer.valueOf(this.param[0].toString());
                int varImv = 0;
                try {
                    varImv = Integer.parseInt(conn.LerParametros(String.format("PROP%s",this.param[0])));
                } catch (Exception ex) {varImv = 0;}
                varImv = varImv + 1;

                if (salvar(bInc, varProp + varImv)) {
                    String cPar[] = {"PROP" + this.param[0],String.valueOf(varImv),"NUMERICO"};
                    try {conn.GravarParametros(cPar);} catch (SQLException ex) {}
                }
                i_rgimv.setText(String.valueOf(Integer.valueOf((String)this.param[0]) + varImv));
            } else {
                salvar(bInc, this.iID);
            }

            // Atualiza
            try {
                int pos = rs.getRow();
                DbMain.FecharTabela(rs);

                rs = conn.AbrirTabela(iSql, ResultSet.CONCUR_UPDATABLE);
                rs.absolute(pos);
                if (DbMain.RecordCount(rs) <= 0) {
                    new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {i_btInc, i_btRetornar});
                } else {
                    new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.buttons).BotaoDisabled(new Object[] {i_btGravar});
                }
            } catch (SQLException e) {}

            new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.fields).FieldsEnabled(false);
            i_btBaixar.disableProperty().bind(i_situacao.valueProperty().isNotEqualTo("Ocupado"));

            //i_anuncios.setDisable(false);

            //LerImovel();
            Platform.runLater(() -> { verifyBotoes(); });
            bInc = false;
        }
    }

    @FXML void i_btRetornar_OnAction(ActionEvent event) {
        if (bInc) {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "Dados foram incluidos ou alterados!\n\nDeseja dispensar estas informações?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) return;
        }

        if (!i_btGravar.isDisabled()) {
            bInc = false;
            LerImovel();
            new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.buttons).BotaoDisabled(new Object[]{i_btGravar});
            new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.fields).FieldsEnabled(false);

            //i_anuncios.setDisable(false);

            i_btBaixar.disableProperty().bind(i_situacao.valueProperty().isNotEqualTo("Ocupado"));
            return;
        }
        try {anchorImovel.fireEvent(new paramEvent(new String[] {"Imovel"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorImovel.addEventHandler(vieaEvent.GET_VIEA, event -> {
            this.viea = event.sviea;
        });

        anchorImovel.addEventHandler(paramEvent.GET_PARAM, event -> {
            this.param = event.sparam;
        });
        
        Platform.runLater(() -> { verifyBotoes(); });

        Platform.runLater(() -> {
            if (this.param != null) {
                if (!this.param[0].equals("Imovel")) {
                    i_anuncios.setDisable(this.param[0] == null);
                    // Verifica se é dividido | Verde = Sim ; Cinza = Não
                    isDividido((String) this.param[1]);
                }
            }
        });


        i_btEndereco.setOnAction((event) -> {
            findEnderecos dialog = new findEnderecos();
            Optional<ViaCEPEndereco> result = dialog.findEnderecos();
            result.ifPresent(b -> {
                i_endereco.setText(b.getLogradouro());
                i_bairro.setText(b.getBairro());
                i_cidade.setText(b.getLocalidade());
                i_estado.setText(b.getUf());
                i_cep.setText(b.getCep());

                Platform.runLater(() -> CodMunicipioDimob(b.getLocalidade(), i_cdmun));
                //i_cdmun.setText(b.getIbge());
                i_num.setText(null); i_cplto.setText(null);
                i_num.requestFocus();
            });
        });

        i_cep.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                //p_rgprp.setEditable(true);
            } else {
                // out focus
                if ((i_endereco.getText() == null || i_endereco.getText().equalsIgnoreCase("")) && i_cep.getText().length() == 9) {
                    try {
                        ViaCEPClient client = new ViaCEPClient();
                        ViaCEPEndereco endereco = client.getEndereco(i_cep.getText());
                        
                        i_endereco.setText(endereco.getLogradouro());
                        i_bairro.setText(endereco.getBairro());
                        i_cidade.setText(endereco.getLocalidade());
                        i_estado.setText(endereco.getUf());

                        Platform.runLater(() -> CodMunicipioDimob(endereco.getLocalidade(), i_cdmun));
                        //i_cdmun.setText(endereco.getIbge());
                        i_num.setText(null); i_cplto.setText(null);
                        i_num.requestFocus();
                    } catch (IOException ex) {}
                }
            }
        });        
        
        new cpoMatriculas().cpoMatriculas(i_matriculas);
        i_tipo.setItems(new pTipo().Tipo());
        i_situacao.setItems(new pSituacao().Situacao());
        i_ur.setItems(new pUrbRur().UrbRur());
        
        LimpaTela();
        OpenImoveis();
        FillTipoImv(i_tipoimv);

        // Anuncios
        {
            class EditingCellConteudo extends TableCell<panuncioModel, String> {

                private TextField textField;

                public EditingCellConteudo() {
                }

                @Override
                public void startEdit() {
                    if (!isEmpty()) {
                        super.startEdit();
                        createTextField();
                        setText(null);
                        setGraphic(textField);
                        textField.requestFocus();
                        textField.selectAll();
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText((String) getItem());
                    setGraphic(null);
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (isEditing()) {
                            if (textField != null) {
                                textField.setText(getString());
                            }
                            setText(null);
                            setGraphic(textField);
                        } else {
                            setText(getString());
                            setGraphic(null);
                        }
                    }
                }

                private void createTextField() {

                    textField = new TextField(getString());
                    textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

                    textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                        if(!newValue) {
                            //System.out.println( "Focus lost, current value: " + textField.getText());
                            commitEdit();
                        }
                    });

                    textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                        if( e.getCode() == KeyCode.ESCAPE) {
                            cancelEdit();
                        }
                    });

                }

                private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                }

                private boolean commitEdit() {
                    if (!isEditing()) return true;
                    super.commitEdit(textField.getText());
                    return true;
                }
            }

            Callback<TableColumn<panuncioModel, String>, TableCell<panuncioModel, String>> cellFactory = (TableColumn<panuncioModel, String> p) -> new EditingCellConteudo();
            i_conteudo.setCellFactory(cellFactory);
            i_conteudo.setOnEditCommit((TableColumn.CellEditEvent<panuncioModel, String> t) -> {
                    String xcampo = ((panuncioModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).getCampo();
                    String sqlUpdate = "UPDATE anunciosimovel SET conteudo = '%s' WHERE rgimv = '%s' AND campo = '%s';";
                    sqlUpdate = String.format(sqlUpdate, t.getNewValue().toString(), i_rgimv.getText(), xcampo);
                    conn.ExecutarComando(sqlUpdate);
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setCampo(t.getNewValue());
                }
            );
        }

        i_btBaixar.disableProperty().bind(i_situacao.valueProperty().isNotEqualTo("Ocupado"));

        this.fields = new Object[] {i_tipo, i_situacao, i_endereco, i_num, i_cplto, i_bairro, i_cidade, i_cdmun, i_estado,
                i_ur, i_cep, i_mat_menu, i_matriculas, i_retertaxas, i_Dimob, i_msg, i_obs, i_descricao,
                i_anunciar, i_tpimovel, i_tipoimv, i_anuncios
        };
        new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.fields).Focus();
        new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.fields).FieldsEnabled(false);

        // i_reservado, i_dtreserva, i_reservtels, i_dtfinal

        this.buttons = new Object[] {i_btInc, i_btAlt, i_btExcluir, i_btBaixar, i_btFotos, i_btGravar, i_btRetornar};
        if (DbMain.RecordCount(rs) <= 0) {
            Platform.runLater(() ->  new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.buttons).BotaoEnabled(new Object[] {i_btInc, i_btRetornar}) );
        } else {
            Platform.runLater(() -> new Controle("IMOVEL: " + i_rgimv.getText() + " - ", this.buttons).BotaoDisabled(new Object[] {i_btGravar}) );
        }

    }

    private void isDividido(String rgimv) {
        boolean edividido = false;
        if (rgimv != null) {
            try {
                edividido = conn.LerCamposTabela(new String[]{"rgimv"}, "dividir", String.format("rgimv = '%s'", rgimv)).length > 0;
            } catch (Exception e) { }
        }
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(edividido ? Color.LIGHTGREEN : Color.WHITE);
        material.setSpecularColor(Color.BLACK);
        i_dividido.setMaterial(material);
        i_ldividido.setText(edividido ? "Imóvel dividido" : "Imóvel não dividido");
    }

    private void verifyBotoes() {
        if (this.viea == null) return;
        if (!this.viea.contains("I")) {i_btInc.setDisable(true); i_btBaixar.setDisable(true); i_btFotos.setDisable(true);}
        if (!this.viea.contains("A")) {i_btAlt.setDisable(true); i_btBaixar.setDisable(true); i_btFotos.setDisable(true);}
        if (!this.viea.contains("E")) i_btExcluir.setDisable(true);
        if (!this.viea.contains("A")) {i_btGravar.setDisable(true); i_btBaixar.setDisable(true); i_btFotos.setDisable(true);}
    }
    
    private void CodMunicipioDimob(String cidade, TextField campo) {
        Object[][] ibge = null;
        try { ibge = conn.LerCamposTabela(new String[] {"cdserpro"}, "ibge", "Upper(municipio) = ?",new Object[][] {{"string", FuncoesGlobais.myLetra(cidade).toUpperCase()}}); } catch (SQLException e) {}
        if (ibge != null) {
            campo.setText(ibge[0][3].toString());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Menssagem");
            alert.setHeaderText("Município");
            alert.setContentText("Município não consta na lista!!!");
            alert.showAndWait();
        }

/*
            WebView view = new WebView();
            view.setVisible(false);
            final WebEngine eng = view.getEngine();
            eng.load("http://www.fazenda.mg.gov.br/governo/assuntos_municipais/codigomunicipio/codmunicoutest_rj.htm");
            eng.getLoadWorker().stateProperty().addListener(
                    new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                            if (newState == Worker.State.SUCCEEDED) {
                                Document dc = eng.getDocument();
                                String html = (String) eng.executeScript("document.documentElement.outerHTML");
                                String busca = "<td class=\"xl29\">" + FuncoesGlobais.myLetra(cidade).toUpperCase() + "</td><td class=\"xl30\">";
                                String codigo = html.substring(html.indexOf(busca));
                                int pos = codigo.indexOf("<td class=\"xl30\"><div style=\"text-align: center; \">") + "<td class=\"xl30\"><div style=\"text-align: center; \">".length();
                                String scodigo = codigo.substring(pos);
                                int pos2 = scodigo.indexOf("<td class=\"xl30\"><div style=\"text-align: center; \">") + "<td class=\"xl30\"><div style=\"text-align: center; \">".length();
                                scodigo = scodigo.substring(pos2, pos2 + 4);
                                campo.setText(scodigo);
                            }
                        }
                    }
            );
*/
    }
    
    private void LimpaTela() {
        i_rgprp = null;
        i_rgimv.setText(null);
        MaskFieldUtil.maxField(i_endereco, 60); i_endereco.setText(null);
        MaskFieldUtil.maxField(i_num, 10); i_num.setText(null);
        MaskFieldUtil.maxField(i_cplto, 15); i_cplto.setText(null);
        MaskFieldUtil.maxField(i_bairro, 25); i_bairro.setText(null);
        MaskFieldUtil.maxField(i_cidade, 25); i_cidade.setText(null);
        i_cdmun.setText(null);
        MaskFieldUtil.maxField(i_estado, 2); i_estado.setText(null);
        i_cep.setText(null);
        i_matriculas.getItems().clear();
        i_retertaxas.setSelected(false);
        i_descricao.setText(null);
        MaskFieldUtil.maxField(i_msg, 100); i_msg.setText(null);
        MaskFieldUtil.maxField(i_obs, 100); i_obs.setText(null);
        i_anunciar.setSelected(false);
        i_tipoimv.getSelectionModel().select(0);
        i_anuncios.getItems().clear();

        i_reservado.setText("");
        i_dtreserva.setText("");
        i_reservtels.setText("");
        i_dtfinal.setText("");
    }
    
    private void LerImovel() {
        try {this.iID = rs.getInt("i_id");} catch (SQLException e) {this.iID = -1;}
        try {i_rgprp = rs.getString("i_rgprp");} catch (SQLException e) {this.i_rgprp = null;}
        try {i_rgimv.setText(rs.getString("i_rgimv"));} catch (SQLException e) {i_rgimv.setText(null);}
        try {i_tipo.getSelectionModel().select(rs.getString("i_tipo"));} catch (SQLException e) {i_tipo.getSelectionModel().select(0);}
        try {i_situacao.getSelectionModel().select(rs.getString("i_situacao"));} catch (SQLException e) {i_situacao.getSelectionModel().select(0);}
        try {i_endereco.setText(rs.getString("i_end"));} catch (SQLException e) {i_endereco.setText(null);}
        try {i_num.setText(rs.getString("i_num"));} catch (SQLException e) {i_num.setText(null);}
        try {i_cplto.setText(rs.getString("i_cplto"));} catch (SQLException e) {i_cplto.setText(null);}
        try {i_bairro.setText(rs.getString("i_bairro"));} catch (SQLException e) {i_bairro.setText(null);}
        try {i_cidade.setText(rs.getString("i_cidade"));} catch (SQLException e) {i_cidade.setText(null);}
        try {i_cdmun.setText(rs.getString("i_cdmun"));} catch (SQLException e) {i_cdmun.setText(null);}
        try {i_estado.setText(rs.getString("i_estado"));} catch (SQLException e) {i_estado.setText(null);}
        try {i_ur.getSelectionModel().select(rs.getString("i_ur"));} catch (SQLException e) {i_ur.getSelectionModel().select(0);}
        try {i_cep.setText(rs.getString("i_cep"));} catch (SQLException e) {i_cep.setText(null);}

        List<pmatriculaModel> datamat = null;
        try {datamat = new setMatriculas(rs.getString("i_matriculas")).rString();} catch (SQLException e) {}
        if (datamat != null) i_matriculas.setItems(observableArrayList(datamat)); else i_matriculas.getItems().clear();
        i_matriculas.setDisable(false);
        try {i_matriculas.getSelectionModel().select(0);} catch (Exception e) {}

        try {i_retertaxas.setSelected(rs.getBoolean("i_rtaxas"));} catch (SQLException e) {i_retertaxas.setSelected(false);}
        try {i_descricao.setText(rs.getString("i_desc"));} catch (SQLException e) {i_descricao.setText(null);}

        String t_tipoimv = null;
        try {t_tipoimv = rs.getString("i_tipoimv").toLowerCase();} catch (SQLException e) {}
        i_tipoimv.setDisable(false);
        try {
            i_tipoimv.getEditor().setText(t_tipoimv);
            i_tipoimv.getSelectionModel().select(new pimovelModel(t_tipoimv));
        } catch (Exception e) {i_tipoimv.getSelectionModel().select(0);}

        try {i_msg.setText(rs.getString("i_msg"));} catch (SQLException e) {i_msg.setText(null);}
        try {i_obs.setText(rs.getString("i_obs"));} catch (SQLException e) {i_obs.setText(null);}
        try {i_anunciar.setSelected(rs.getBoolean("i_anunciar"));} catch (SQLException e) {i_anunciar.setSelected(false);}

        try {i_reservado.setText(rs.getString("i_reservado"));} catch (SQLException e) {i_reservado.setText("");}
        try {i_dtreserva.setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("i_dtreserva")));} catch (SQLException e) {i_dtreserva.setText("");}
        try {i_reservtels.setText(rs.getString("i_reservtels"));} catch (SQLException e) {i_reservtels.setText("");}
        try {i_dtfinal.setText(Dates.DateFormata("dd/MM/yyyy", rs.getDate("i_dtfimreserv")));} catch (SQLException e) {i_dtfinal.setText("");}

        populateAnuncios(i_rgimv.getText());
    }
    
    public boolean salvar(boolean bNew, int Id) {
        String sql = ""; boolean retorno = true;
        if (bNew) {
            sql = "INSERT INTO imoveis(i_rgprp, i_rgimv, i_tipo, i_situacao, " +
                  "i_end, i_num, i_cplto, i_bairro, i_cidade, i_cdmun, i_estado, " +
                  "i_ur, i_cep, i_matriculas, i_rtaxas, i_tipoimv, i_desc, i_msg, i_obs, i_anunciar)" +
                  " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        } else {
            sql = "UPDATE imoveis" +
                    " SET i_tipo=?, i_situacao=?, i_end=?, " +
                    "i_num=?, i_cplto=?, i_bairro=?, i_cidade=?, i_cdmun=?, i_estado=?, " +
                    "i_ur=?, i_cep=?, i_matriculas=?, i_rtaxas=?, i_tipoimv=?, i_desc=?, i_msg=?, " +
                    "i_obs=?, i_anunciar=?" +
                    " WHERE i_id = " + Id + ";";
        }
        try {
            PreparedStatement pstmt = conn.conn.prepareStatement(sql);
            int nid = 1; 
            if (bNew) {
                pstmt.setString(nid++, (String)param[0]);
                pstmt.setString(nid++, String.valueOf(Id));
            }
            pstmt.setString(nid++, i_tipo.getSelectionModel().getSelectedItem());
            pstmt.setString(nid++, bNew ? "Vazio" : i_situacao.getSelectionModel().getSelectedItem());
            pstmt.setString(nid++, i_endereco.getText());
            pstmt.setString(nid++, i_num.getText());
            pstmt.setString(nid++, i_cplto.getText());
            pstmt.setString(nid++, i_bairro.getText());
            pstmt.setString(nid++, i_cidade.getText());
            pstmt.setString(nid++, i_cdmun.getText());
            pstmt.setString(nid++, i_estado.getText());
            pstmt.setString(nid++, i_ur.getSelectionModel().getSelectedItem());
            pstmt.setString(nid++, i_cep.getText());
            
            // matriculas
            pstmt.setString(nid++, new getMatriculas(i_matriculas).toString());
            
            pstmt.setBoolean(nid++, i_retertaxas.isSelected());
            pstmt.setString(nid++, i_tipoimv.getSelectionModel().getSelectedItem().toString());
            pstmt.setString(nid++, i_descricao.getText());
            pstmt.setString(nid++, i_msg.getText());
            pstmt.setString(nid++, i_obs.getText());

            boolean tanuncio = false;
            try { tanuncio = i_descricao.getText().trim().length() > 0;} catch (Exception e) {tanuncio = false;}
            pstmt.setBoolean(nid, tanuncio);

            pstmt.executeUpdate();
        } catch (SQLException e) {e.printStackTrace(); retorno = false;}

        boolean tanuncio = false;
        try { tanuncio = i_descricao.getText().trim().length() > 0;} catch (Exception e) {tanuncio = false;}
        if (!tanuncio) conn.ExecutarComando(String.format("DELETE FROM anunciosimovel WHERE rgimv = '%s';",i_rgimv.getText()));
        return retorno;
    }
    
    private void OpenImoveis() {
        rs = conn.AbrirTabela(this.iSql, ResultSet.CONCUR_UPDATABLE);
        Platform.runLater(() -> { if (this.param != null) if (this.param[1] != null) try { MoveTo("i_rgimv", (String)param[1]); } catch (SQLException e) {} });
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
        LerImovel();
        return achei;
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
        internalFrame.setClosable(false);


        internalFrame.setBackground(new UIColor(103,165, 162));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);

        root.fireEvent(new paramEvent(new String[] {i_rgimv.getText()}, paramEvent.GET_PARAM));

        root.addEventHandler(paramEvent.GET_PARAM, event -> {
            try {internalFrame.close();} catch (NullPointerException e) {}
        });
    }

    private void FillTipoImv(ComboBox<pimovelModel> combo) {
        try {
            ResultSet trs = conn.AbrirTabela("SELECT tipo FROM tipoimovel ORDER BY UPPER(tipo);", ResultSet.CONCUR_READ_ONLY);
            while (trs.next()) {
                combo.getItems().addAll(new pimovelModel(trs.getString("tipo")));
            }
            combo.setFocusTraversable(true);
            trs.close();
        } catch (Exception e) {}
    }

    private void populateAnuncios(String rgimv) {
        // Limpa
        i_anuncios.getItems().clear();

        List<panuncioModel> data = new ArrayList<panuncioModel>();
        ResultSet imv;
        String qSQL = "SELECT campo, conteudo FROM anunciosimovel WHERE rgimv = '" + rgimv + "' ORDER BY rgimv;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            boolean isVazio = true;
            while (imv.next()) {
                isVazio = false;
                String qcampo = null, qconteudo = null;
                try {qcampo = imv.getString("campo");} catch (SQLException e) {}
                try {qconteudo = imv.getString("conteudo");} catch (SQLException e) {}
                data.add(new panuncioModel(qcampo, qconteudo));
            }
            imv.close();

            if (isVazio && !bInc) {
                String[] campos = {}; String[] tcampos = {};
                boolean tanuncio = false;
                try { tanuncio = i_descricao.getText().trim().length() > 0;} catch (Exception e) {tanuncio = false;}
                if (tanuncio) {
                    campos = FuncoesGlobais.ArrayAdd(campos,"Aluguel");
                    tcampos = i_descricao.getText().trim().split(",");
                    for (String campo : tcampos) campos = FuncoesGlobais.ArrayAdd(campos, campo.trim());
                    for (String campo : campos) {
                        data.add(new panuncioModel(campo, ""));
                        conn.ExecutarComando(String.format("INSERT INTO anunciosimovel(rgimv, campo) " +
                                "VALUES ('%s', '%s');", rgimv, campo));
                    }
                }
            }
        } catch (SQLException e) {e.printStackTrace();}

        i_campo.setCellValueFactory(new PropertyValueFactory<>("campo"));
        i_conteudo.setCellValueFactory(new PropertyValueFactory<>("descr"));

        if (!data.isEmpty()) i_anuncios.setItems(FXCollections.observableArrayList(data));
    }
}
