/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Excluidos;

import Calculos.AvisosMensagens;
import Calculos.Processa;
import Classes.DadosLocador;
import Classes.DadosLocatario;
import Classes.gRecibo;
import Classes.paramEvent;
import Funcoes.Collections;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.Impressao;
import Funcoes.LerValor;
import Funcoes.VariaveisGlobais;
import Movimento.tbvAltera;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author Samic
 */
public class SaldoExcluido implements Initializable{
    DbMain conn = VariaveisGlobais.conexao;

    private String[] _possibleSuggestionsContrato = {};
    private String[] _possibleSuggestionsNome = {};
    private String[][] _possibleSuggestions = {};
    private Set<String> possibleSuggestionsContrato;
    private Set<String> possibleSuggestionsNome;
    private boolean isSearchContrato = true;
    private boolean isSearchNome = true;
    boolean isFillVecto;
    public int linha = 0;
    private String rgprp = null;
    private DadosLocatario dadosLocatario;
    private DadosLocador dadosLocador;
    private BigInteger Autenticacao;
    private int nvenctosatz = 0;
    private boolean ultimovctoatz = false;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField contrato;
    @FXML private TextField nome;
    @FXML private TextField rgimv;
    @FXML private TextField nomeprop;
    @FXML private TextField enderimv;
    @FXML private TextField bairroimv;
    @FXML private TextField cidadeimv;
    @FXML private TextField estadoimv;
    @FXML private TextField cepimv;
    @FXML private TableView<tbvAltera> tableView;
    @FXML private TableColumn<tbvAltera, Boolean> tableColumn_Tag;
    @FXML private TableColumn<tbvAltera, String> tableColumn_Descr;
    @FXML private TableColumn<tbvAltera, String> tableColumn_Cota;
    @FXML private TableColumn<tbvAltera, String> tableColumn_Valor;
    @FXML private Spinner<Integer> nrvenctos;
    @FXML private ComboBox<String> venctos;
    @FXML private Button btDemons;
    @FXML private TextField valorvenctos;
    @FXML private TextField valortotal;
    @FXML private ProgressBar progressBar;
    @FXML private TextField muvr;
    @FXML private TextField covr;
    @FXML private TextField juvr;
    @FXML private TextField tevr;
    @FXML private Label mu;
    @FXML private Label co;
    @FXML private Label ju;
    @FXML private Label te;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.addEventHandler(paramEvent.GET_PARAM, event -> {
            if (event.sparam.length == 0) {
                // Cancelar Recebimento
                Platform.runLater(() -> contrato.requestFocus());
            }
            Platform.runLater(() -> contrato.requestFocus());
        });

        // Botão de Impressão do demonstrativo de recibo
        btDemons.setOnAction(event -> {
            gRecibo recibo = new gRecibo();
            recibo.GeraRecibo(contrato.getText(), venctos.getSelectionModel().getSelectedItem().trim());

            Collections dadm = VariaveisGlobais.getAdmDados();
            new Impressao(new BigInteger("0"), null).ImprimeReciboPDF(dadm, null, dadosLocatario, recibo, false);
        });

        AutocompleteContratoNome();

        Platform.runLater(() -> {
            mu.setText(VariaveisGlobais.contas_ca.get("MUL"));
            ju.setText(VariaveisGlobais.contas_ca.get("JUR"));
            co.setText(VariaveisGlobais.contas_ca.get("COR"));
            te.setText(VariaveisGlobais.contas_ca.get("EXP"));

            contrato.requestFocus();
        });
        
    }
    
    private void AutocompleteContratoNome() {
        ResultSet imv = null;
        String qSQL = "SELECT l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS l_nome FROM locatarios WHERE not exclusao is null ORDER BY l_contrato;";
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {
                    qcontrato = imv.getString("l_contrato");
                } catch (SQLException e) {}
                try {
                    qnome = imv.getString("l_nome");
                } catch (SQLException e) {}
                _possibleSuggestionsContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsContrato, qcontrato);
                possibleSuggestionsContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsContrato));

                _possibleSuggestionsNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsNome, qnome);
                possibleSuggestionsNome = new HashSet<>(Arrays.asList(_possibleSuggestionsNome));

                _possibleSuggestions = FuncoesGlobais.ArraysAdd(_possibleSuggestions, new String[]{qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try {DbMain.FecharTabela(imv);} catch (Exception e) {}

        TextFields.bindAutoCompletion(contrato, possibleSuggestionsContrato);
        TextFields.bindAutoCompletion(nome, possibleSuggestionsNome);

        contrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                contrato.setText(null);
                nome.setText(null);

                // Limpa dados do Imovel
                rgimv.setText(null);
                nomeprop.setText(null);
                enderimv.setText(null);
                bairroimv.setText(null);
                cidadeimv.setText(null);
                estadoimv.setText(null);
                cepimv.setText(null);

                // Botoes
                tableView.setDisable(false);
                tableView.setItems(FXCollections.observableArrayList());

                nrvenctos.getEditor().setText(null);
                venctos.setItems(FXCollections.observableArrayList());
                btDemons.setDisable(true);
                valorvenctos.setText("0,00");
                valortotal.setText("0,00");

                muvr.setText("0,00");
                covr.setText("0,00");
                juvr.setText("0,00");
                tevr.setText("0,00");
            } else {
                // out focus
                String pcontrato = null;
                try {
                    pcontrato = contrato.getText();
                } catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = -1;
                    try { pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 0, contrato.getText());} catch (Exception e) {}
                    if (pos > -1 && isSearchContrato) {
                        isSearchNome = false;
                        final String tName = _possibleSuggestions[pos][1];
                        Platform.runLater(() -> nome.setText(tName));
                        nome.setText(tName);
                        isSearchNome = true;
                    }
                } else {
                    isSearchContrato = false;
                    isSearchNome = true;
                }
            }
        });

        nome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
            } else {
                // out focus
                int pos = -1;
                try {pos = FuncoesGlobais.FindinArrays(_possibleSuggestions, 1, nome.getText());} catch (Exception e) {}
                String pcontrato = null;
                try {
                    pcontrato = contrato.getText();
                } catch (NullPointerException e) {}
                if (pos > -1 && isSearchNome && pcontrato == null) {
                    isSearchContrato = false;
                    if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestions, 1, nome.getText())) {
                        contrato.setText(_possibleSuggestions[pos][0]);
                    } else {
                        WindowEnderecos(nome.getText());
                    }
                    isSearchContrato = true;
                } else {
                    isSearchContrato = true;
                    isSearchNome = false;
                }

                /*
                String msgBloq = null;
                msgBloq = new AvisosMensagens().VerificaBloqueio(contrato.getText());
                if (msgBloq != null) {
                    new Alert(INFORMATION, msgBloq).showAndWait();
                    contrato.requestFocus();
                    return;
                }
                */

                DadosImovel(true);
                Object[] fimCtro = new AvisosMensagens().VerificaFimCtroLocatario(contrato.getText());
                if ((boolean)fimCtro[1] && VariaveisGlobais.am_term) {
                    new Alert(INFORMATION, (String)fimCtro[0]).show();
                }
                if (new AvisosMensagens().VerificaAniLocatario(contrato.getText()) && VariaveisGlobais.am_aniv) {
                    new Alert(INFORMATION, "Locatário faz aniversário neste mês!").show();
                }
            }
        });
    }

    private void WindowEnderecos(String snome) {
        AnchorPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Movimento/Alteracao/Enderecos.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(true);
        internalFrame.setResizable(false);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(snome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);


        internalFrame.setBackground(new UIColor(103, 165, 162));

        internalFrame.pack();
        internalFrame.setVisible(true);
    }

    private void DadosImovel(boolean isOk) {
        String sql = "SELECT p.p_rgprp, p.p_nome, i.i_rgimv, i.i_end || ', ' || i.i_num || ' ' || i.i_cplto AS i_ender, i.i_bairro, i.i_cidade, i.i_estado, i.i_cep, l.l_nrecatz, l.l_ultrecto FROM locatarios l, imoveis i, proprietarios p WHERE (not i.exclusao is null) and l.l_contrato = '%s' and l.l_rgimv = i.i_rgimv and l.l_rgprp = CAST(p.p_rgprp AS text)";
        sql = String.format(sql, contrato.getText());
        ResultSet imv = null;
        try {
            imv = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qpnome = null, qirgprp = null, qirgimv = null, qiend = null, qibai = null, qicid = null;
                String qiest = null, qicep = null;
                try { qpnome = imv.getString("p_nome"); } catch (SQLException e) { }
                try { qirgimv = imv.getString("i_rgimv"); } catch (SQLException e) { }
                try { qirgprp = imv.getString("p_rgprp"); } catch (SQLException e) { }
                try { qiend = imv.getString("i_ender"); } catch (SQLException e) { }
                try { qibai = imv.getString("i_bairro"); } catch (SQLException e) { }
                try { qicid = imv.getString("i_cidade"); } catch (SQLException e) { }
                try { qiest = imv.getString("i_estado"); } catch (SQLException e) { }
                try { qicep = imv.getString("i_cep"); } catch (SQLException e) { }

                try { this.nvenctosatz = imv.getInt("l_nrecatz"); } catch (SQLException e) { this.nvenctosatz = 0; }
                try { this.ultimovctoatz = imv.getBoolean("l_ultrecto"); } catch (SQLException e) { this.ultimovctoatz = false; }

                rgimv.setText(qirgimv);
                nomeprop.setText(qpnome);
                enderimv.setText(qiend);
                bairroimv.setText(qibai);
                cidadeimv.setText(qicid);
                estadoimv.setText(qiest);
                cepimv.setText(qicep);

                dadosLocador = new DadosLocador(qirgprp, qpnome, 0);
                dadosLocatario = new DadosLocatario(contrato.getText(), nome.getText(), qiend, "", "", qibai, qicid, qiest, qicep);
            }
        } catch (SQLException e) { }
        try { DbMain.FecharTabela(imv); } catch (Exception e) { }

        if (isOk) {
            nrvenctos.setDisable(false);
            venctos.setDisable(false);
            valorvenctos.setDisable(false);

            cepimv.requestFocus();
        }

        isFillVecto = true;
        Object[] vctos = FiltraVencimentos(contrato.getText());
        vctos = FuncoesGlobais.ObjectsOrdenaData(vctos);

        if (vctos.length > 0) {
            // Value factory.
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, vctos.length, vctos.length);
            nrvenctos.setValueFactory(valueFactory);
            nrvenctos.getEditor().setText(String.valueOf(vctos.length));
            venctos.getItems().clear();

            for (Object d : vctos) {
                String dt = Dates.DateFormata("dd/MM/yyyy", (Date) d);
                venctos.getItems().add(dt);
            }
            venctos.getSelectionModel().select(0);
        } else {
            new Alert(INFORMATION,"Contrato sem Vencimentos").show();
            Platform.runLater(() -> contrato.requestFocus());
            return;
        }

        isFillVecto = false;

        venctos.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isFillVecto) {
                if (venctos.getItems().size() > 0) {
                    Task task = ProcessaCampos();
                    progressBar.setVisible(true);
                    progressBar.progressProperty().bind(task.progressProperty());
                    new Thread(task).start();
                }
            }
        });

        Task task = ProcessaCampos();
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();

        nrvenctos.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                isFillVecto = true;
                Object[] vctos = FiltraVencimentos(contrato.getText(), newValue);
                vctos = FuncoesGlobais.ObjectsOrdenaData(vctos);

                venctos.getItems().clear();
                if (vctos.length > 0) {
                    for (Object d : vctos) {
                        String dt = Dates.DateFormata("dd/MM/yyyy", (Date) d);
                        venctos.getItems().add(dt);
                    }
                    venctos.getSelectionModel().select(0);
                }
                isFillVecto = false;

                BigDecimal trecs = TotalRecibos(vctos);
            }
        });

        BigDecimal trecs = TotalRecibos(vctos);

    }

    private Object[] FiltraVencimentos(String contrato) {
        int posVecto = 1;
        Object[] venctos = {};

        String sql = "SELECT dtvencimento FROM movimento WHERE dtrecebimento Is Null AND contrato = '%s' ORDER BY dtvencimento;";
        sql = String.format(sql, contrato);
        ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        // Setar recebimentos em caso de acordo
        int nvectos = DbMain.RecordCount(resultSet);
        boolean bvenctos[] =  new boolean[nvectos];
        if (this.nvenctosatz > 0) {
            for (int i = 0; i < nvectos; i++) {
                if (posVecto <= this.nvenctosatz) {
                    bvenctos[i] = true;
                }
                posVecto++;
            }
            if (this.ultimovctoatz) bvenctos[bvenctos.length - 1] = true;
        } else {
            for (int i = 0; i < nvectos; i++) {
                bvenctos[i] = true;
            }
        }

        posVecto = 0;
        try {
            while (resultSet.next()) {
                if (!bvenctos[posVecto++]) continue;

                Date vcto = null;
                try {
                    vcto = resultSet.getDate("dtvencimento");
                } catch (SQLException e) { }
                if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
            }
        } catch (Exception e) { }
        try { DbMain.FecharTabela(resultSet); } catch (Exception e) { }

        if (venctos.length == 0) {
            sql = "SELECT dtvencimento FROM taxas WHERE dtrecebimento Is Null AND contrato = '%s';";
            sql = String.format(sql, contrato);
            resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (resultSet.next()) {
                    Date vcto = null;
                    try {
                        vcto = resultSet.getDate("dtvencimento");
                    } catch (SQLException e) {
                    }
                    if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
                }
            } catch (Exception e) {}
            try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}
        }

        if (venctos.length == 0) {
            sql = "SELECT dtvencimento FROM seguros WHERE dtrecebimento Is Null AND contrato = '%s';";
            sql = String.format(sql, contrato);
            resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (resultSet.next()) {
                    Date vcto = null;
                    try {
                        vcto = resultSet.getDate("dtvencimento");
                    } catch (SQLException e) {}
                    if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
                }
            } catch (Exception e) {}
            try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}
        }

        return FuncoesGlobais.ObjectsRemoveDup(venctos);
    }

    private Object[] FiltraVencimentos(String contrato, int limite) {
        Object[] venctos = {};

        String sql = "SELECT dtvencimento FROM movimento WHERE dtrecebimento Is Null AND contrato = '%s' ORDER BY dtvencimento LIMIT %s;";
        sql = String.format(sql, contrato, limite);
        ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (resultSet.next()) {
                Date vcto = null;
                try {
                    vcto = resultSet.getDate("dtvencimento");
                } catch (SQLException e) {}
                if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
            }
        } catch (Exception e) { }
        try { DbMain.FecharTabela(resultSet); } catch (Exception e) { }

        if (venctos.length == 0) {
            sql = "SELECT dtvencimento FROM taxas WHERE dtrecebimento Is Null AND contrato = '%s';";
            sql = String.format(sql, contrato);
            resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (resultSet.next()) {
                    Date vcto = null;
                    try {
                        vcto = resultSet.getDate("dtvencimento");
                    } catch (SQLException e) {}
                    if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
                }
            } catch (Exception e) {}
            try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}
        }

        if (venctos.length == 0) {
            sql = "SELECT dtvencimento FROM seguros WHERE dtrecebimento Is Null AND contrato = '%s';";
            sql = String.format(sql, contrato);
            resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (resultSet.next()) {
                    Date vcto = null;
                    try {
                        vcto = resultSet.getDate("dtvencimento");
                    } catch (SQLException e) {}
                    if (vcto != null) venctos = FuncoesGlobais.ObjectsAdd(venctos, vcto);
                }
            } catch (Exception e) {}
            try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}
        }

        return FuncoesGlobais.ObjectsRemoveDup(venctos);
    }
    
    public Task ProcessaCampos() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(() -> {
                    contrato.setDisable(true);
                    nome.setDisable(true);

                    tableView.getItems().clear();
                });

                // Pegar o mes de referencia do Vencimento no descdif
                String vcto = venctos.getSelectionModel().getSelectedItem();
                String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES, venctos.getItems().size() <= 1 ? 0 :-1, Dates.StringtoDate(vcto, "dd/MM/yyyy")));

                linha = 0;
                List<tbvAltera> data = new ArrayList<tbvAltera>();

                rgprp = null;

                // Movimento
                String sql = "SELECT * FROM movimento WHERE contrato = '%s' AND dtvencimento = '%s' and selected = true and (aut_rec = 0 or aut_rec is null);";
                sql = String.format(sql, contrato.getText().trim(), Dates.StringtoString(vcto, "dd/MM/yyyy", "yyyy/MM/dd"));

                ResultSet resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                int recordCount = DbMain.RecordCount(resultSet);

                BigDecimal vrAluguel = new BigDecimal(0);
                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true;
                        int qid = 0;
                        String qtipo = "C";
                        String qdes = "ALUGUEL", qcota = "99/99";
                        String qvalor = "0,00";
                        String qvariavel = "mensal";

                        try {
                            rgprp = resultSet.getString("rgprp");
                        } catch (SQLException e) {}

                        try {
                            qid = resultSet.getInt("id");
                        } catch (SQLException e) {}

                        try {
                            qtag = resultSet.getBoolean("selected");
                        } catch (SQLException e) {}
                        //try {qdes = resultSet.getString("descricao");} catch (SQLException e) {}
                        try {
                            qcota = resultSet.getString("cota");
                        } catch (SQLException e) {}

                        try {
                            qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("mensal").toString(), "#,##0.00");
                        } catch (SQLException e) {}

                        data.add(new tbvAltera("movimento", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));

                        try {
                            vrAluguel = resultSet.getBigDecimal("mensal");
                        } catch (SQLException e) {}
                    }
                } catch (Exception e) {e.printStackTrace();}
                try {DbMain.FecharTabela(resultSet);} catch (Exception e) {}

                // Desconto Diferença
                BigDecimal desal = new BigDecimal("0");
                BigDecimal difal = new BigDecimal("0");
                sql = "SELECT * FROM descdif WHERE contrato = '%s' AND referencia = '%s' and selected = true and (aut_rec = 0 or aut_rec is null);";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true;
                        int qid = 0;
                        String qtipo = "";
                        String qdes = "IPTU #" + ++linha, qcota = "99/99";
                        String qvalor = "0,00";
                        String qvariavel = "valor";

                        if (rgprp == null) {
                            try {
                                rgprp = resultSet.getString("rgprp");
                            } catch (SQLException e) {}
                        }

                        try {
                            qid = resultSet.getInt("id");
                        } catch (SQLException e) {}
                        try {
                            qtag = resultSet.getBoolean("selected");
                        } catch (SQLException e) {}
                        try {
                            qtipo = resultSet.getString("tipo");
                        } catch (SQLException e) {}
                        if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                            // Diferença
                            try {
                                qdes = "Dif.Aluguel " + resultSet.getString("descricao");
                            } catch (SQLException e) {}
                        } else {
                            // Desconto
                            try {
                                qdes = "Desc.Aluguel " + resultSet.getString("descricao");
                            } catch (SQLException e) {}
                        }
                        try {
                            qcota = resultSet.getString("cota");
                        } catch (SQLException e) {}
                        try {
                            qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(), "#,##0.00");
                        } catch (SQLException e) {}

                        data.add(new tbvAltera("descdif", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));

                        if (resultSet.getString("tipo").equalsIgnoreCase("C")) {
                            // Diferença
                            difal = difal.add(resultSet.getBigDecimal("valor"));
                        } else {
                            // Desconto
                            desal = desal.add(resultSet.getBigDecimal("valor"));
                        }

                    }
                } catch (Exception e) {e.printStackTrace();}
                try {DbMain.FecharTabela(resultSet);} catch (Exception e) { }

                if (rgprp == null) {
                    contrato.setDisable(false);
                    contrato.requestFocus();
                    return null;
                }
                
                // IRRF
                try {
                    BigDecimal irenda = new Calculos.Irrf().Irrf(rgprp, contrato.getText(), descdif_refer.trim(), vrAluguel, difal, desal);
                    if (irenda.compareTo(BigDecimal.ZERO) == 1) {
                        data.add(new tbvAltera("irrf", "", 0, true, "D", VariaveisGlobais.contas_ca.get("IRF"), descdif_refer.trim(), LerValor.FormatPattern(irenda.toPlainString(), "#,##0.00"), false));
                    }
                } catch (Exception ex) {}

                // Taxas
                sql = "SELECT * FROM taxas WHERE contrato = '%s' AND referencia = '%s' and selected = true and (aut_rec = 0 or aut_rec is null);";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true, qret = false;
                        int qid = 0;
                        String qtipo = "";
                        String qdes = "", qpos = "", qcota = "";
                        String qvalor = "0,00";
                        String qvariavel = "valor";

                        // Ler o nome da taxa
                        String sWhere = null;
                        try { sWhere = "codigo = '" + resultSet.getString("campo") + "'"; } catch (SQLException e) { }

                        try { qid = resultSet.getInt("id"); } catch (SQLException e) { }
                        try { qtag = resultSet.getBoolean("selected"); } catch (SQLException e) { }
                        try { qtipo = resultSet.getString("tipo"); } catch (SQLException e) { }
                        try { qdes = (qtipo.equalsIgnoreCase("D") ? "Desc." : "Dif.") + (String) conn.LerCamposTabela(new String[]{"descricao"}, "campos", sWhere)[0][3]; } catch (SQLException e) { }
                        try { qpos = resultSet.getString("poscampo"); } catch (SQLException e) { }
                        try { qcota = resultSet.getString("cota"); } catch (SQLException e) { }
                        try { qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(), "#,##0.00"); } catch (SQLException e) { }
                        try { qret = resultSet.getBoolean("retencao"); } catch (SQLException e) { }

                        data.add(new tbvAltera("taxas", qvariavel, qid, qtag, qtipo, qdes + " " + qpos, qcota, qvalor, qret));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    DbMain.FecharTabela(resultSet);
                } catch (Exception e) {
                }

                // Seguros
                sql = "SELECT * FROM seguros WHERE contrato = '%s' AND referencia = '%s' and selected = true and aut_rec = 0;" +
                        ";";
                sql = String.format(sql, contrato.getText().trim(), descdif_refer.trim());

                resultSet = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
                recordCount = DbMain.RecordCount(resultSet);

                try {
                    while (resultSet.next()) {
                        Thread.sleep(30);
                        updateProgress(resultSet.getRow(), recordCount);

                        // MontaGrade
                        Boolean qtag = true;
                        int qid = 0;
                        String qtipo = "C";
                        String qdes = "SEGURO", qcota = "99/99";
                        String qvalor = "0,00";
                        String qvariavel = "valor";

                        try { qid = resultSet.getInt("id"); } catch (SQLException e) { }
                        try { qtag = resultSet.getBoolean("selected"); } catch (SQLException e) { }
                        try { qcota = resultSet.getString("cota"); } catch (SQLException e) { }
                        try { qvalor = LerValor.FormatPattern(resultSet.getBigDecimal("valor").toString(), "#,##0.00"); } catch (SQLException e) { }

                        data.add(new tbvAltera("seguros", qvariavel, qid, qtag, qtipo, qdes, qcota, qvalor, false));
                    }
                } catch (Exception e) { e.printStackTrace(); }
                try { DbMain.FecharTabela(resultSet); } catch (Exception e) { }

                // TODO IPTU implementar
/*
                String[] ciptu = new Calculos.Iptu().Iptu(rgimv.getText(), descdif_refer.trim());
                int iptuId = 0;
                String iptuMes = null;
                String iptuRef = null;
                String vrIptu = null;
                if (ciptu != null) {
                    if (ciptu[0] != null) {
                        iptuId = Integer.valueOf(ciptu[0]);
                        iptuMes = ciptu[1];
                        iptuRef = ciptu[2];
                        vrIptu = LerValor.FormatPattern(ciptu[3], "#,##0.00");
                    }
                }
                if (vrIptu != null && !vrIptu.trim().equalsIgnoreCase("0,00"))
                    data.add(new tbvAltera("iptu", iptuMes, iptuId, true, "C", "IPTU", iptuRef, vrIptu, false));
*/

                Platform.runLater(() -> {
                    tableColumn_Tag.setCellValueFactory(new PropertyValueFactory<tbvAltera, Boolean>("tag"));
                    tableColumn_Descr.setCellValueFactory(new PropertyValueFactory<tbvAltera, String>("desc"));

                    tableColumn_Cota.setCellValueFactory(new PropertyValueFactory<tbvAltera, String>("cota"));
                    tableColumn_Cota.setStyle("-fx-alignment: CENTER;");

                    tableColumn_Valor.setCellValueFactory(new PropertyValueFactory<tbvAltera, String>("valor"));
                    tableColumn_Valor.setEditable(true);
                    tableColumn_Valor.setStyle("-fx-alignment: CENTER-RIGHT;");

                    if (!data.isEmpty()) tableView.setItems(FXCollections.observableArrayList(data));
                    if (venctos.getItems().size() > 0 && !vcto.isEmpty()) UpdateCalculos(vcto);

                    progressBar.setVisible(false);
                    contrato.setDisable(false);
                    nome.setDisable(false);
                    progressBar.progressProperty().unbind();
                    //progressBar.setProgress(0);
                });
                return true;
            }
        };
    }

    private void UpdateCalculos(String vcto) {
        try {
            Platform.runLater(() -> {
                Processa calc = new Processa(rgprp, rgimv.getText(), contrato.getText(), Dates.StringtoDate(vcto, "dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()), "dd-MM-yyyy"));
                valorvenctos.setText(new DecimalFormat("#,##0.00").format(calc.TotalRecibo()));
                muvr.setText(new DecimalFormat("#,##0.00").format(calc.Multa()));
                covr.setText(new DecimalFormat("#,##0.00").format(calc.Correcao()));
                juvr.setText(new DecimalFormat("#,##0.00").format(calc.Juros()));
                tevr.setText(new DecimalFormat("#,##0.00").format(calc.Expediente()));
            });
        } catch (Exception e) {
        }
    }

    private BigDecimal TotalRecibos(Object[] vctos) {
        try {
            this.rgprp = (String) conn.LerCamposTabela(new String[]{"l_rgprp"}, "locatarios", "l_contrato = '" + contrato.getText() + "'")[0][3];
        } catch (Exception e) {
        }

        BigDecimal trecibos = new BigDecimal(0);
        try {
            for (Object vcto : vctos) {
                Processa calc = new Processa(this.rgprp, rgimv.getText(), contrato.getText(), Dates.StringtoDate(vcto.toString(), "yyyy-MM-dd"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()), "dd-MM-yyyy"));
                trecibos = trecibos.add(calc.TotalRecibo());
            }
            valortotal.setText(new DecimalFormat("#,##0.00").format(trecibos));
        } catch (Exception e) {
        }
        return trecibos;
    }

    public BigDecimal[] ProcessarCampos(String vctos) {
        try {
            this.rgprp = (String) conn.LerCamposTabela(new String[]{"l_rgprp"}, "locatarios", "l_contrato = '" + contrato.getText() + "'")[0][3];
        } catch (Exception e) {
        }
        Processa calc = new Processa(this.rgprp, rgimv.getText(), contrato.getText(), Dates.StringtoDate(vctos, "dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()), "dd-MM-yyyy"));

        return new BigDecimal[]{calc.getAluguel(), calc.getDiferenca(), calc.getDescontos(), calc.getIrenda(), calc.getIptu(), calc.getSeguro(), calc.Multa(), calc.Juros(), calc.Correcao(), calc.Expediente()};
    }    
}
