package Administrador.Abas.DadosAdm;

import Administrador.cRegTrib;
import Classes.*;
import Funcoes.DbMain;
import Funcoes.MaskFieldUtil;
import Funcoes.VariaveisGlobais;
import com.github.gilbertotorrezan.viacep.server.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javafx.collections.FXCollections.observableArrayList;

public class DadosAdm implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private TextField da_razao;
    @FXML private TextField da_fanta;
    @FXML private TextField da_cnpj;
    @FXML private TextField da_creci;
    @FXML private ComboBox<String> da_tipo;
    @FXML private TextField da_insc;
    @FXML private TextField da_ender;
    @FXML private TextField da_numero;
    @FXML private TextField da_cplto;
    @FXML private TextField da_bairro;
    @FXML private TextField da_cidade;
    @FXML private TextField da_estado;
    @FXML private TextField da_cep;
    @FXML private ComboBox<ptelcontatoModel> da_tel;
    @FXML private TextField da_email;
    @FXML private TextField da_hpage;
    @FXML private TextField da_marca;
    @FXML private ImageView da_logo;
    @FXML private Button plus;
    @FXML private Button minus;
    @FXML private TextField da_codmun;
    @FXML private TextField da_responsavel;
    @FXML private TextField da_respcpf;

    @FXML private ComboBox<cRegTrib> nfRegTrib;
    @FXML private RadioButton nfSimplesSim;
    @FXML private ToggleGroup nfSimples;
    @FXML private RadioButton nfSimplesNao;
    @FXML private TextField nfAtividade;
    @FXML private TextField nfCNAE;

    private String campo = "";
    private String newValue = "";
    private String oldValue = "";

    private final String[] _estados = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initialize_da();
        Set_da();
    }

    private void Initialize_da() {
        MaskFieldUtil.maxField(da_razao, 100);
        MaskFieldUtil.maxField(da_fanta, 100);
        MaskFieldUtil.cnpjField(da_cnpj);
        MaskFieldUtil.maxField(da_creci, 10);
        MaskFieldUtil.maxField(da_insc, 20);
        da_insc.disableProperty().bind(da_tipo.getSelectionModel().selectedIndexProperty().isEqualTo(0));
        ObservableList<String> _tipos = da_tipo.getItems();
        _tipos.add("Isenta");
        _tipos.add("Municipal");
        _tipos.add("Estadual");
        da_tipo.setItems(_tipos);
        da_tipo.getSelectionModel().select(0);

        MaskFieldUtil.maxField(da_ender, 100);
        MaskFieldUtil.maxField(da_numero, 10);
        MaskFieldUtil.maxField(da_cplto, 25);
        MaskFieldUtil.maxField(da_bairro, 40);
        MaskFieldUtil.maxField(da_cidade, 40);
        MaskFieldUtil.maxField(da_estado, 2);
        TextFields.bindAutoCompletion(da_estado, _estados);
        MaskFieldUtil.maxField(da_cep, 9);
        da_cep.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    // on focus
                    //p_rgprp.setEditable(true);
                } else {
                    // out focus
                    if ((da_ender.getText() == null || da_ender.getText().equalsIgnoreCase("")) && da_cep.getText().length() == 9) {
                        try {
                            ViaCEPClient client = new ViaCEPClient();
                            ViaCEPEndereco endereco = client.getEndereco(da_cep.getText());

                            da_ender.setText(endereco.getLogradouro());
                            da_bairro.setText(endereco.getBairro());
                            da_cidade.setText(endereco.getLocalidade());
                            da_estado.setText(endereco.getUf());

                            da_numero.setText(null);
                            da_cplto.setText(null);
                            da_numero.requestFocus();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        // da_tel
        new telCombo().telCombo(da_tel);
        plus.setOnAction(event -> {
            adcTelefones dialog = new adcTelefones();
            Optional<ptelcontatoModel> result = dialog.adcTelefones();
            result.ifPresent(b -> {
                ObservableList<ptelcontatoModel> tel = da_tel.getItems();
                tel.addAll(b);
                da_tel.setItems(tel);
                try {
                    da_tel.getSelectionModel().select(0);
                } catch (Exception e) {
                }
                da_tel.requestFocus();
            });
        });
        minus.setOnAction(event -> {
            if (!da_tel.getItems().isEmpty()) da_tel.getItems().removeAll(da_tel.getSelectionModel().getSelectedItem());
            try {
                da_tel.getSelectionModel().select(0);
            } catch (Exception e) {
            }
            da_tel.requestFocus();
        });

        MaskFieldUtil.maxField(da_email, 100);
        MaskFieldUtil.maxField(da_hpage, 100);
        MaskFieldUtil.maxField(da_marca, 10);

        da_logo.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Arquivos de Logotipos");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Logotipos no formato jpg", "*.jpg"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Logotipos no formato gif", "*.gip"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Logotipos no format bmp", "*.bmp"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                String iname = selectedFile.getName();
                String ipath = selectedFile.getAbsolutePath().replace(iname, "");

                System.out.println(ipath);
                System.out.println(iname);
                System.out.println(System.getProperty("user.dir"));
                Path copy_from_1 = Paths.get(ipath, iname);

                Path copy_to_1 = Paths.get(System.getProperty("user.dir") + "/resources/", copy_from_1
                        .getFileName().toString());
                try {
                    Files.copy(copy_from_1, copy_to_1, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
                } catch (IOException e) {
                    System.err.println(e);
                }
                da_logo.setImage(new Image(new File(System.getProperty("user.dir") + "/resources/" + iname).toURI().toString()));

                // Salvar no PARAMETROS
                try {
                    conn.GravarParametros(new String[]{"da_logo", System.getProperty("user.dir") + "/resources/" + iname, "texto"});
                } catch (SQLException e) {
                } finally {
                    auditora(" novo: " + System.getProperty("user.dir") + "/resources/" + iname);
                }
            }
        });

        MaskFieldUtil.maxField(da_responsavel, 100);
        MaskFieldUtil.cpfCnpjField(da_respcpf);

        // Ler Dados
        {
            try { da_razao.setText(conn.LerParametros("da_razao")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_fanta.setText(conn.LerParametros("da_fanta")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_cnpj.setText(conn.LerParametros("da_cnpj")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_creci.setText(conn.LerParametros("da_creci")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_tipo.getSelectionModel().select(conn.LerParametros("da_tipo")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_insc.setText(conn.LerParametros("da_insc")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_ender.setText(conn.LerParametros("da_ender")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_numero.setText(conn.LerParametros("da_numero")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_cplto.setText(conn.LerParametros("da_cplto")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_bairro.setText(conn.LerParametros("da_bairro")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_cidade.setText(conn.LerParametros("da_cidade")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_codmun.setText(conn.LerParametros("da_codmun")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_estado.setText(conn.LerParametros("da_estado")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_cep.setText(conn.LerParametros("da_cep")); } catch (SQLException e) { } catch (NullPointerException e) { }

            // Telefone de contato da empresa
            List<ptelcontatoModel> data = null;
            try { data = new setTels(conn.LerParametros("da_tel")).rString(); } catch (SQLException e) { }
            if (data != null) da_tel.setItems(observableArrayList(data)); else da_tel.getItems().clear();
            da_tel.setDisable(false);
            try { da_tel.getSelectionModel().select(0); } catch (Exception e) { }
            try { da_email.setText(conn.LerParametros("da_email")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_hpage.setText(conn.LerParametros("da_hpage")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_marca.setText(conn.LerParametros("da_marca")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_logo.setImage(new Image(new File(conn.LerParametros("da_logo")).toURI().toString())); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_responsavel.setText(conn.LerParametros("da_responsavel")); } catch (SQLException e) { } catch (NullPointerException e) { }
            try { da_respcpf.setText(conn.LerParametros("da_respcpf")); } catch (SQLException e) { } catch (NullPointerException e) { }
        }

        // Nota Fiscal
        {
            List<cRegTrib> tabela = new ArrayList<cRegTrib>();
            tabela.add(new cRegTrib(1,"Microempresa Municipal", false, false));
            tabela.add(new cRegTrib(2,"Estimativa", false, false));
            tabela.add(new cRegTrib(3,"Sociedade de profissionais", true,false));
            tabela.add(new cRegTrib(4,"Cooperativa", false,false));
            tabela.add(new cRegTrib(5,"MEI", false,true));
            tabela.add(new cRegTrib(6,"ME EPP", true,false));
            tabela.add(new cRegTrib(7,"Tributação por faturamento (variável)", false,false));
            tabela.add(new cRegTrib(8,"Fixo", true,false));
            tabela.add(new cRegTrib(9,"Isenção", false,false));
            tabela.add(new cRegTrib(10,"Imune", false,false));
            tabela.add(new cRegTrib(10,"Exibilidade sespenda p/ação jud.", false,false));
            tabela.add(new cRegTrib(11,"Exibilidade sespenda p/proc. adm.", false,false));
            tabela.add(new cRegTrib(12,"Não Tributável", false,false));
            nfRegTrib.getItems().setAll(observableArrayList(tabela));

            nfRegTrib.valueProperty().addListener((observable, oldValue1, newValue1) -> {
                nfSimplesSim.setDisable(!nfRegTrib.getSelectionModel().getSelectedItem().isSimples());
                nfSimplesNao.setDisable(!nfRegTrib.getSelectionModel().getSelectedItem().isSimples());
                nfSimplesSim.setSelected(nfRegTrib.getSelectionModel().getSelectedItem().isPadrao());
                nfSimplesNao.setSelected(!nfRegTrib.getSelectionModel().getSelectedItem().isPadrao());
            });

            // Leitura
            {
                try { nfRegTrib.getSelectionModel().select(Integer.valueOf(conn.LerParametros("nf_regtrib"))); } catch (SQLException e) { nfRegTrib.getSelectionModel().select(0); } catch (NullPointerException e) { nfRegTrib.getSelectionModel().select(0); }
                try {
                    boolean bSIMNAO = Boolean.valueOf(conn.LerParametros("nf_simple"));
                    if (bSIMNAO) nfSimplesSim.setSelected(true); else nfSimplesNao.setSelected(true);
                } catch (SQLException | NullPointerException e) { }
            }

            try {
                nfAtividade.setText(conn.LerParametros("nf_atividade"));
            } catch (SQLException | NullPointerException e) {}

            try {
                nfCNAE.setText(conn.LerParametros("nf_cnae"));
            } catch (SQLException | NullPointerException e) {}
        }
    }

    private void Set_da() {
        da_razao.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_razao.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_razao", da_razao.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_razao.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_razao";
            this.newValue = newValue;
        }));

        da_fanta.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_fanta.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_fanta", da_fanta.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_fanta.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_fanta";
            this.newValue = newValue;
        }));

        da_cnpj.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_cnpj.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_cnpj", da_cnpj.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_cnpj.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_cnpj";
            this.newValue = newValue;
        }));

        da_creci.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_creci.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_creci", da_creci.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_creci.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_creci";
            this.newValue = newValue;
        }));

        da_tipo.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_tipo.getSelectionModel().getSelectedItem();
                if (lostfocus)
                    conn.GravarParametros(new String[]{"da_tipo", da_tipo.getSelectionModel().getSelectedItem(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_tipo.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_tipo";
            this.newValue = newValue;
        }));

        da_insc.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_insc.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_insc", da_insc.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_insc.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_insc";
            this.newValue = newValue;
        }));

        da_ender.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_ender.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_ender", da_ender.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_ender.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_ender";
            this.newValue = newValue;
        }));

        da_numero.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_numero.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_numero", da_numero.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_numero.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_numero";
            this.newValue = newValue;
        }));

        da_cplto.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_cplto.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_cplto", da_cplto.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_cplto.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_cplto";
            this.newValue = newValue;
        }));

        da_bairro.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_bairro.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_bairro", da_bairro.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_bairro.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_bairro";
            this.newValue = newValue;
        }));

        da_cidade.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_cidade.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_cidade", da_cidade.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_cidade.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_cidade";
            this.newValue = newValue;
        }));

        da_codmun.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_cidade.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_codmun", da_codmun.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_codmun.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_codmun";
            this.newValue = newValue;
        }));

        da_estado.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_estado.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_estado", da_estado.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_estado.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_estado";
            this.newValue = newValue;
        }));

        da_cep.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_cep.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_cep", da_cep.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_cep.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_cep";
            this.newValue = newValue;
        }));

        da_tel.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = new getTels(da_tel).toString();
                if (lostfocus) conn.GravarParametros(new String[]{"da_tel", new getTels(da_tel).toString(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_tel.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_tel";
            this.newValue = newValue;
        }));

        da_email.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_email.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_email", da_email.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_email.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_email";
            this.newValue = newValue;
        }));

        da_hpage.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_hpage.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_hpage", da_hpage.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_hpage.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_hpage";
            this.newValue = newValue;
        }));

        da_marca.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_marca.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_marca", da_marca.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_marca.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_marca";
            this.newValue = newValue;
        }));

        da_responsavel.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_responsavel.getText();
                if (lostfocus)
                    conn.GravarParametros(new String[]{"da_responsavel", da_responsavel.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_responsavel.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_responsavel";
            this.newValue = newValue;
        }));

        da_respcpf.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = da_respcpf.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"da_respcpf", da_respcpf.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        da_respcpf.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "da_respcpf";
            this.newValue = newValue;
        }));

        //da_logo

        // NotaFiscal
        nfRegTrib.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = String.valueOf(nfRegTrib.getSelectionModel().getSelectedIndex());
                if (lostfocus) conn.GravarParametros(new String[] {"nf_regtrib",String.valueOf(nfRegTrib.getSelectionModel().getSelectedIndex()), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        nfRegTrib.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "nfRegTrib";
            this.newValue = newValue;
        }));

        nfSimplesSim.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = nfSimplesSim.isSelected() ? "1" : "0";
                if (lostfocus) conn.GravarParametros(new String[]{"nf_simples", nfSimplesSim.isSelected() ? "1" : "0", "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        nfSimplesSim.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "nfSimples";
            this.newValue = newValue;
        }));

        nfSimplesNao.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = nfSimplesNao.isSelected() ? "1" : "0";
                if (lostfocus) conn.GravarParametros(new String[]{"nf_simples", nfSimplesNao.isSelected() ? "1" : "0", "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        nfSimplesNao.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "nfSimples";
            this.newValue = newValue;
        }));

        nfAtividade.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = nfAtividade.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"nf_atividade", nfAtividade.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        nfAtividade.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "nfAtividade";
            this.newValue = newValue;
        }));

        nfCNAE.focusedProperty().addListener((arg0, lostfocus, gotfocus) -> {
            try {
                if (gotfocus) oldValue = nfCNAE.getText();
                if (lostfocus) conn.GravarParametros(new String[]{"nf_cnae", nfCNAE.getText().trim(), "texto"});
            } catch (SQLException e) {
            } finally {
                if (lostfocus) auditora(campo + " velho: " + oldValue + " novo: " + newValue);
            }
        });
        nfCNAE.textProperty().addListener(((observable, oldValue, newValue) -> {
            campo = "nfCNAE";
            this.newValue = newValue;
        }));
    }

    private void auditora() {
    }

    private void auditora(String taxa) {
        //System.out.println(taxa + " foi alterada por " + VariaveisGlobais.usuario.toLowerCase().trim());
    }
}
