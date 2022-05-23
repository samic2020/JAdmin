package Administrador.Abas.Configuracoes.cnfSistema;

import Administrador.cUnidades;
import Classes.adcUnidades;
import Classes.pUnidades;
import Classes.selectPrinter;
import Funcoes.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class cnfSistema implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    // Parametros do Recibo
    @FXML private TextField logo_width;
    @FXML private TextField logo_Heigth;
    @FXML private ComboBox<String> logo_allign;
    @FXML private CheckBox logo_noprint;
    @FXML private CheckBox razao_noprint;
    @FXML private CheckBox cnpj_noprint;
    @FXML private CheckBox creci_noprint;
    @FXML private CheckBox endereco_noprint;
    @FXML private CheckBox telefone_noprint;
    @FXML private TextField recibo_titulo;
    @FXML private Spinner<Integer> recibo_vias;
    @FXML private CheckBox copa_print;
    @FXML private CheckBox ref_print;
    @FXML private CheckBox qcr_print;
    @FXML private CheckBox md5_print;

    // Configurações do sistema
    @FXML
    private ToggleSwitch tp_impressao;
    @FXML private Label tp_interna;
    @FXML private Label tp_externa;
    @FXML private TextField thermica;
    @FXML private TextField laser;
    @FXML private TextField largura;
    @FXML private TextField altura;
    @FXML private TextField esquerda;
    @FXML private TextField direita;
    @FXML private TextField superior;
    @FXML private TextField inferior;
    @FXML private TextField unidaderede;
    @FXML private TextField bancoDados;
    @FXML private CheckBox PossuiSenha;
    @FXML private TableView<cUnidades> remotas;
    @FXML private TableColumn<cUnidades, Integer> rmt_id;
    @FXML private TableColumn<cUnidades, String> rmt_unidade;
    @FXML private TableColumn<cUnidades, String> rmt_bancodados;
    @FXML private TableColumn<cUnidades, Boolean> rmt_senha;
    @FXML private Button rmt_adc;
    @FXML private Button rmt_del;
    @FXML private TextArea cmd_externo;
    @FXML private Tooltip cmd_externo_tolltip; // Windows: C:\Windows\System32\cmd.exe /c copy "[FILENAME]" \\[IP]\[PRINTER]

    @FXML private CheckBox am_AvisaAniv;
    @FXML private CheckBox am_avTermino;
    @FXML private Spinner<Integer> am_Dias;

    @FXML private CheckBox am_avReaj;
    @FXML private Spinner<Integer> am_ReajDias;

    @FXML private CheckBox am_avSeguro;
    @FXML private Spinner<Integer> am_SeguroDias;
    @FXML private CheckBox reajManAluguel;

    @FXML private Button btThermica;
    @FXML private Button btLaser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initializa_conf();
        Initializa_AvMsg();
        Initializa_RecImpCf();
    }

    private void Initializa_conf() {
        if (VariaveisGlobais.PrinterMode.equalsIgnoreCase("EXTERNA")) {
            tp_interna.setStyle("-fx-text-fill: black;");
            tp_externa.setStyle("-fx-text-fill: red;");
            tp_impressao.setSelected(true);
        } else {
            tp_interna.setStyle("-fx-text-fill: red;");
            tp_externa.setStyle("-fx-text-fill: black;");
            tp_impressao.setSelected(false);
        }
        tp_impressao.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                if (o.getValue().equals(true)) {
                    tp_interna.setStyle("-fx-text-fill: black;");
                    tp_externa.setStyle("-fx-text-fill: red;");
                } else {
                    tp_interna.setStyle("-fx-text-fill: red;");
                    tp_externa.setStyle("-fx-text-fill: black;");
                }
                new Settings().Save("PrinterMode", tp_impressao.isSelected() ? "EXTERNA" : "INTERNA");
                VariaveisGlobais.PrinterMode = tp_impressao.isSelected() ? "EXTERNA" : "INTERNA";
            }
        });

        try {
            unidaderede.setText("" + VariaveisGlobais.unidade);
        } catch (NullPointerException e) {
        }
        unidaderede.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("Unidade", unidaderede.getText().trim());
                VariaveisGlobais.unidade = unidaderede.getText().trim();
            }
        });
        bancoDados.setText(VariaveisGlobais.dbnome);
        bancoDados.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("dbNome", bancoDados.getText().trim());
                VariaveisGlobais.dbnome = bancoDados.getText().trim();
            }
        });
        PossuiSenha.setSelected(VariaveisGlobais.dbsenha);
        PossuiSenha.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                new Settings().Save("dbSenha", PossuiSenha.isSelected() ? "true" : "false");
                VariaveisGlobais.dbsenha = PossuiSenha.isSelected();
            }
        });

        // propriedades da thermica
        // largura, altura, esquerda, direita, superior, inferior
        largura.setText(String.valueOf(VariaveisGlobais.bobinaSize[0]));
        largura.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                String bobina = largura.getText().replace(",", ".") + ", " + altura.getText().replace(",", ".") + ", " +
                        esquerda.getText().replace(",", ".") + ", " + direita.getText().replace(",", ".") + ", " +
                        superior.getText().replace(",", ".") + ", " + inferior.getText().replace(",", ".");
                new Settings().Save("bobinaSize", bobina);
                VariaveisGlobais.bobinaSize[0] = Float.parseFloat(largura.getText().replace(",", "."));
            }
        });
        altura.setText(String.valueOf(VariaveisGlobais.bobinaSize[1]));
        altura.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                String bobina = largura.getText().replace(",", ".") + ", " + altura.getText().replace(",", ".") + ", " +
                        esquerda.getText().replace(",", ".") + ", " + direita.getText().replace(",", ".") + ", " +
                        superior.getText().replace(",", ".") + ", " + inferior.getText().replace(",", ".");
                new Settings().Save("bobinaSize", bobina);
                VariaveisGlobais.bobinaSize[1] = Float.parseFloat(altura.getText().replace(",", "."));
            }
        });
        esquerda.setText(String.valueOf(VariaveisGlobais.bobinaSize[2]));
        esquerda.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                String bobina = largura.getText().replace(",", ".") + ", " + altura.getText().replace(",", ".") + ", " +
                        esquerda.getText().replace(",", ".") + ", " + direita.getText().replace(",", ".") + ", " +
                        superior.getText().replace(",", ".") + ", " + inferior.getText().replace(",", ".");
                new Settings().Save("bobinaSize", bobina);
                VariaveisGlobais.bobinaSize[2] = Float.parseFloat(esquerda.getText().replace(",", "."));
            }
        });
        direita.setText(String.valueOf(VariaveisGlobais.bobinaSize[3]));
        direita.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                String bobina = largura.getText().replace(",", ".") + ", " + altura.getText().replace(",", ".") + ", " +
                        esquerda.getText().replace(",", ".") + ", " + direita.getText().replace(",", ".") + ", " +
                        superior.getText().replace(",", ".") + ", " + inferior.getText().replace(",", ".");
                new Settings().Save("bobinaSize", bobina);
                VariaveisGlobais.bobinaSize[3] = Float.parseFloat(direita.getText().replace(",", "."));
            }
        });
        superior.setText(String.valueOf(VariaveisGlobais.bobinaSize[4]));
        superior.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                String bobina = largura.getText().replace(",", ".") + ", " + altura.getText().replace(",", ".") + ", " +
                        esquerda.getText().replace(",", ".") + ", " + direita.getText().replace(",", ".") + ", " +
                        superior.getText().replace(",", ".") + ", " + inferior.getText().replace(",", ".");
                new Settings().Save("bobinaSize", bobina);
                VariaveisGlobais.bobinaSize[4] = Float.parseFloat(superior.getText().replace(",", "."));
            }
        });
        inferior.setText(String.valueOf(VariaveisGlobais.bobinaSize[5]));
        inferior.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                String bobina = largura.getText().replace(",", ".") + ", " + altura.getText().replace(",", ".") + ", " +
                        esquerda.getText().replace(",", ".") + ", " + direita.getText().replace(",", ".") + ", " +
                        superior.getText().replace(",", ".") + ", " + inferior.getText().replace(",", ".");
                new Settings().Save("bobinaSize", bobina);
                VariaveisGlobais.bobinaSize[5] = Float.parseFloat(inferior.getText().replace(",", "."));
            }
        });

        if (VariaveisGlobais.Thermica == null) thermica.setText("");
        else thermica.setText(VariaveisGlobais.Thermica);
        //thermica.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
        //    if (lostfocus) {
        //        new Settings().Save("Thermica", thermica.getText().trim());
        //        VariaveisGlobais.Thermica = thermica.getText().trim();
        //    }
        //});

        if (VariaveisGlobais.Printer == null) laser.setText("");
        else laser.setText(VariaveisGlobais.Printer);
        //laser.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
        //    if (lostfocus) {
        //        new Settings().Save("Printer", laser.getText().trim());
        //        VariaveisGlobais.Printer = laser.getText().trim();
        //    }
        //});

        btLaser.setOnAction(event -> {
            selectPrinter sprinter = new selectPrinter();
            Optional<String> result = sprinter.selectPrinter();
            result.ifPresent(b -> {
                laser.setText(b);
                new Settings().Save("Printer", laser.getText().trim());
                VariaveisGlobais.Printer = laser.getText().trim();
            });
        });

        btThermica.setOnAction(event -> {
            selectPrinter sprinter = new selectPrinter();
            Optional<String> result = sprinter.selectPrinter();
            result.ifPresent(b -> {
                thermica.setText(b);
                new Settings().Save("Thermica", thermica.getText().trim());
                VariaveisGlobais.Thermica = thermica.getText().trim();
            });
        });

        populateEstacoes();
        rmt_del.disableProperty().bind(remotas.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        rmt_del.setOnAction(event -> {
            for (int i = 1; i <= VariaveisGlobais.unidades.length - 1; i++) {
                new Settings().Remove("remoto" + LerValor.FormatPattern(String.valueOf(i), "#0"));
            }
            VariaveisGlobais.unidades = FuncoesGlobais.ObjectDel(VariaveisGlobais.unidades, remotas.getSelectionModel().getSelectedIndex() + 1);
            populateEstacoes();
            if (remotas.getItems().size() > 0) {
                for (int i = 0; i <= remotas.getItems().size() - 1; i++) {
                    new Settings().Save("remoto" + LerValor.FormatPattern(String.valueOf(i + 1), "#0"),
                            remotas.getItems().get(i).getDns() + "," +
                                    remotas.getItems().get(i).getDatabase() + "," +
                                    (remotas.getItems().get(i).isIsSenha() ? "true" : "false")
                    );
                }
            }
        });

        rmt_adc.setOnAction(event -> {
            adcUnidades dialog = new adcUnidades();
            Optional<pUnidades> result = dialog.adcUnidades();
            result.ifPresent(b -> {
                VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{b.getEstacao(), b.getBasedados(), b.isSenha()});
                new Settings().Save("remoto" + LerValor.FormatPattern(String.valueOf(remotas.getItems().size() + 1), "#0"),
                        b.getEstacao() + "," + b.getBasedados() + "," + (b.isSenha() ? "true" : "false"));
                populateEstacoes();
            });

        });

        cmd_externo_tolltip.setText("\nWindows:\nC:\\Windows\\System32\\cmd.exe /c copy \"[FILENAME]\" \\\\[IP]\\[PRINTER]\n\nUnix:\nlp -d [PRINTER] \"[FILENAME]\"");
        cmd_externo.setText(VariaveisGlobais.Externo);
        cmd_externo.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("Externo", cmd_externo.getText().trim());
                VariaveisGlobais.Externo = cmd_externo.getText().trim();
            }
        });
    }

    private void Initializa_AvMsg() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, 1);
        am_Dias.setValueFactory(valueFactory);
        am_Dias.disableProperty().bind(am_avTermino.selectedProperty().not());

        am_AvisaAniv.setOnAction(event -> {
            VariaveisGlobais.am_aniv = am_AvisaAniv.isSelected();
            new Settings().Save("am_aniv", am_AvisaAniv.isSelected() ? "true" : "false");
        });
        am_AvisaAniv.setSelected(VariaveisGlobais.am_aniv);

        am_avTermino.setOnAction(event -> {
            VariaveisGlobais.am_term = am_avTermino.isSelected();
            new Settings().Save("am_term", am_avTermino.isSelected() ? "true" : "false");
        });
        am_avTermino.setSelected(VariaveisGlobais.am_term);

        am_Dias.setOnInputMethodTextChanged(event -> {
            VariaveisGlobais.am_dias = am_Dias.getValue().toString();
            new Settings().Save("am_dias", am_Dias.getValue().toString());
        });
        am_Dias.getEditor().setText(String.valueOf(VariaveisGlobais.am_dias));

        am_avReaj.setOnAction(event -> {
            VariaveisGlobais.am_reaj = am_avReaj.isSelected();
            new Settings().Save("am_reaj", am_avReaj.isSelected() ? "true" : "false");
        });
        am_avReaj.setSelected(VariaveisGlobais.am_reaj);

        SpinnerValueFactory<Integer> valueFactory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1);
        am_ReajDias.setValueFactory(valueFactory2);
        am_ReajDias.disableProperty().bind(am_avReaj.selectedProperty().not());
        am_ReajDias.setOnInputMethodTextChanged(event -> {
            VariaveisGlobais.am_reajdias = am_ReajDias.getValue().toString();
            new Settings().Save("am_reajdias", am_ReajDias.getValue().toString());
        });
        am_ReajDias.getEditor().setText(String.valueOf(VariaveisGlobais.am_reajdias));

        am_avSeguro.setOnAction(event -> {
            VariaveisGlobais.am_reaj = am_avSeguro.isSelected();
            new Settings().Save("am_reaj", am_avSeguro.isSelected() ? "true" : "false");
        });
        am_avSeguro.setSelected(VariaveisGlobais.am_seg);

        SpinnerValueFactory<Integer> valueFactory3 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 90, 1);
        am_SeguroDias.setValueFactory(valueFactory3);
        am_SeguroDias.disableProperty().bind(am_avSeguro.selectedProperty().not());
        am_SeguroDias.setOnInputMethodTextChanged(event -> {
            VariaveisGlobais.am_reajdias = am_SeguroDias.getValue().toString();
            new Settings().Save("am_segdias", am_SeguroDias.getValue().toString());
        });
        am_SeguroDias.getEditor().setText(String.valueOf(VariaveisGlobais.am_segdias));

        reajManAluguel.setOnAction(event -> {
            VariaveisGlobais.reajManAluguel = reajManAluguel.isSelected();
            new Settings().Save("reajManAluguel", reajManAluguel.isSelected() ? "true" : "false");
        });
        reajManAluguel.setSelected(VariaveisGlobais.reajManAluguel);
    }

    private void populateEstacoes() {
        remotas.getItems().clear();
        List<cUnidades> data = new ArrayList<>();
        if (VariaveisGlobais.unidades.length > 0) {
            for (int i = 1; i <= VariaveisGlobais.unidades.length - 1; i++) {
                int tId = i;
                String tIp = (String) VariaveisGlobais.unidades[i][0];
                String tBd = (String) VariaveisGlobais.unidades[i][1];
                boolean tPw = (boolean) VariaveisGlobais.unidades[i][2];

                data.add(new cUnidades(tId, tIp, tBd, tPw));
            }

            rmt_id.setCellValueFactory(new PropertyValueFactory<cUnidades, Integer>("id"));
            rmt_id.setStyle("-fx-alignment: CENTER;");
            rmt_unidade.setCellValueFactory(new PropertyValueFactory<cUnidades, String>("dns"));
            rmt_unidade.setStyle("-fx-alignment: CENTER;");
            rmt_bancodados.setCellValueFactory(new PropertyValueFactory<cUnidades, String>("database"));
            rmt_senha.setCellValueFactory(new PropertyValueFactory<cUnidades, Boolean>("isSenha"));
            rmt_senha.setStyle("-fx-alignment: CENTER;");
        }
        if (!data.isEmpty()) remotas.setItems(FXCollections.observableArrayList(data));
    }

    private void Initializa_RecImpCf() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
        recibo_vias.setValueFactory(valueFactory);
        recibo_vias.setOnInputMethodTextChanged(event -> {
            VariaveisGlobais.recibo_vias = recibo_vias.getValue();
            new Settings().Save("recibo_vias", recibo_vias.getValue().toString());
        });
        recibo_vias.getEditor().setText(String.valueOf(VariaveisGlobais.recibo_vias));

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Esquerda",
                        "Centro",
                        "Direita"
                );
        logo_allign.getItems().addAll(options);
        logo_allign.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("logo_allign", logo_allign.getSelectionModel().getSelectedItem());
                VariaveisGlobais.logo_allign = logo_allign.getSelectionModel().getSelectedItem();
            }
        });
        logo_allign.getSelectionModel().select(VariaveisGlobais.logo_allign);

        logo_width.setText(String.valueOf(VariaveisGlobais.logo_width));
        logo_width.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("logo_width", logo_width.getText().trim());
                VariaveisGlobais.logo_width = Float.parseFloat(logo_width.getText().replace(",", "."));
            }
        });
        logo_Heigth.setText(String.valueOf(VariaveisGlobais.logo_Heigth));
        logo_Heigth.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("logo_heigth", logo_Heigth.getText().trim());
                VariaveisGlobais.logo_Heigth = Float.parseFloat(logo_Heigth.getText().replace(",", "."));
            }
        });

        logo_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.logo_noprint = logo_noprint.isSelected();
                new Settings().Save("logo_noprint", logo_noprint.isSelected() ? "true" : "false");
            }
        });
        logo_noprint.setSelected(VariaveisGlobais.logo_noprint);

        razao_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.razao_noprint = razao_noprint.isSelected();
                new Settings().Save("razao_noprint", razao_noprint.isSelected() ? "true" : "false");
            }
        });
        razao_noprint.setSelected(VariaveisGlobais.razao_noprint);

        cnpj_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.cnpj_noprint = cnpj_noprint.isSelected();
                new Settings().Save("cnpj_noprint", cnpj_noprint.isSelected() ? "true" : "false");
            }
        });
        cnpj_noprint.setSelected(VariaveisGlobais.cnpj_noprint);

        creci_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.creci_noprint = creci_noprint.isSelected();
                new Settings().Save("creci_noprint", creci_noprint.isSelected() ? "true" : "false");
            }
        });
        creci_noprint.setSelected(VariaveisGlobais.creci_noprint);
        //creci_noprint.selectedProperty().bind(cnpj_noprint.selectedProperty().not());

        endereco_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.endereco_noprint = endereco_noprint.isSelected();
                new Settings().Save("endereco_noprint", endereco_noprint.isSelected() ? "true" : "false");
            }
        });
        endereco_noprint.setSelected(VariaveisGlobais.endereco_noprint);

        telefone_noprint.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.telefone_noprint = telefone_noprint.isSelected();
                new Settings().Save("telefone_noprint", telefone_noprint.isSelected() ? "true" : "false");
            }
        });
        telefone_noprint.setSelected(VariaveisGlobais.telefone_noprint);

        recibo_titulo.setText(VariaveisGlobais.recibo_titulo);
        recibo_titulo.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            if (lostfocus) {
                new Settings().Save("recibo_titulo", recibo_titulo.getText().trim());
                VariaveisGlobais.recibo_titulo = recibo_titulo.getText().trim();
            }
        });

        copa_print.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.copa_print = copa_print.isSelected();
                new Settings().Save("copa_print", copa_print.isSelected() ? "true" : "false");
            }
        });
        copa_print.setSelected(VariaveisGlobais.copa_print);

        ref_print.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.ref_print = ref_print.isSelected();
                new Settings().Save("ref_print", ref_print.isSelected() ? "true" : "false");
            }
        });
        ref_print.setSelected(VariaveisGlobais.ref_print);

        qcr_print.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.qcr_print = qcr_print.isSelected();
                new Settings().Save("qcr_print", qcr_print.isSelected() ? "true" : "false");
            }
        });
        qcr_print.setSelected(VariaveisGlobais.qcr_print);

        md5_print.selectedProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                VariaveisGlobais.md5_print = md5_print.isSelected();
                new Settings().Save("md5_print", md5_print.isSelected() ? "true" : "false");
            }
        });
        md5_print.setSelected(VariaveisGlobais.md5_print);
    }

    private void auditora() {
    }

    private void auditora(String taxa) {
        //System.out.println(taxa + " foi alterada por " + VariaveisGlobais.usuario.toLowerCase().trim());
    }
}
