package Administrador.Mensalao;

import Administrador.DadosProp;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.CheckTreeView;
import pdfViewer.PdfViewer;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Mensalao implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean inProcess = false;
    Object[][] dirf = {};

    @FXML private AnchorPane anchorPaneMensalao;
    @FXML private Spinner<Integer> dirfAno;
    @FXML private ProgressBar dirfBarra;
    @FXML private Button dirfPreview;
    @FXML private Label dirfStatus;
    @FXML private CheckTreeView<?> dirfLista;
    @FXML private CheckBox dirfSelectAll;
    @FXML private TextField dirfBusca;
    @FXML private Label dirfClean;
    @FXML private ComboBox<String> dirfTabela;
    @FXML private ComboBox<String> dirfMes;
    @FXML void ClearClick(MouseEvent event) { dirfBusca.setText(""); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, 2050, 2020);
        dirfAno.setValueFactory(ano);
        dirfAno.valueProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {PopulateMeses();});});
        dirfLista.setShowRoot(false);

        dirfSelectAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dirfLista.getCheckModel().checkAll();
            } else {
                dirfLista.getCheckModel().clearChecks();
            }
        });

        dirfBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            TreeItem item = seekItem(dirfLista.getRoot(), dirfBusca.getText());
            if (item != null) {
                dirfLista.getSelectionModel().select(item);
            }
        });

        dirfPreview.setOnAction(event -> {
            List<MensalaoClass> lista = new ArrayList<>();
            for (int i = 0; i < dirfLista.getCheckModel().getCheckedItems().size(); i++) {
                if (!dirfLista.getCheckModel().getCheckedItems().get(i).toString().contains("Proprietarios")) {
                    String rgprp = ((TreeItem)dirfLista.getCheckModel().getCheckedItems().get(i)).getValue().toString().substring(6,11).trim();
                    for (Object[] presult : dirf) {
                        if (!presult[0].toString().equalsIgnoreCase(rgprp)) continue;

                        // Verificar na tabela selecionada
                        BigDecimal pfAL = new BigDecimal("0");
                        BigDecimal pfIR = new BigDecimal("0");
                        BigDecimal pjAL = new BigDecimal("0");
                        BigDecimal pjIR = new BigDecimal("0");
                        Object[] imv = (Object[]) presult[3];
                        for (Object o : (Object[]) imv) {
                            String doc = (((Object[]) ((Object[]) o)[8])[1].toString()).replace(".", "").replace("-", "").replace("/", "");
                            Object[] meses = (Object[]) ((Object[]) o)[9];
                            for (Object[] m : (Object[][]) meses) {
                                if (m[0].toString().equalsIgnoreCase(ConvMes(dirfMes.getSelectionModel().getSelectedItem().toString()))) {
                                    if (doc.length() <= 11) {
                                        // Fisica
                                        pfAL = pfAL.add((BigDecimal) m[1]).add((BigDecimal) m[6]).add((BigDecimal) m[7])
                                                .add((BigDecimal) m[8]).add((BigDecimal) m[5]).subtract((BigDecimal) m[4]);
                                        pfIR = pfIR.add((BigDecimal) m[3]);
                                    } else {
                                        // Juridica
                                        pjAL = pjAL.add((BigDecimal) m[1]).add((BigDecimal) m[6]).add((BigDecimal) m[7])
                                                .add((BigDecimal) m[8]).add((BigDecimal) m[5]).subtract((BigDecimal) m[4]);
                                        pjIR = pjIR.add((BigDecimal) m[3]);
                                    }
                                }
                            }
                        }

                        // Calculos
                        BigDecimal rendLiquido = pfAL.add(pjAL); // new BigDecimal("14002"); // Para testes
                        BigDecimal irrfApurado = pfIR.add(pjIR);

                        BigDecimal[] pf_irf = TabelaIR(dirfTabela.getSelectionModel().getSelectedItem().toString(), rendLiquido);
                        BigDecimal impostoCalc = rendLiquido.multiply(pf_irf[0].divide(new BigDecimal("100"))).subtract(pf_irf[1]);
                        BigDecimal recFacult = impostoCalc.subtract(irrfApurado);
                        //if (recFacult.longValue() > 10) {
                            lista.add(
                                    new MensalaoClass(
                                            rgprp,
                                            presult[1].toString(),
                                            presult[2].toString(),
                                            Dates.StringtoDate("31/12/" + dirfAno.getValue(), "dd/MM/yyyy"),
                                            "0246",
                                            Dates.ultDataMes(new Date()),
                                            pfAL,
                                            pjAL,
                                            rendLiquido,
                                            irrfApurado,
                                            recFacult
                                    )
                            );
                        //}
                    }
                }
            }

            if (lista.size() > 0) {
                String pdfName = new PdfViewer().GeraPDFTemp(lista,"DARF", null);
                new PdfViewer("Preview do Darf", pdfName);
            }

        });

        {
            // Populate Combobox IR
            dirfTabela.getItems().clear();
            String sql = "SELECT DISTINCT mesano FROM irrf ORDER BY mesano;";
            ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
            try {
                while (rs.next()) {
                    dirfTabela.getItems().add(rs.getString("mesano"));
                }
            } catch (SQLException e) {}
            DbMain.FecharTabela(rs);
            dirfTabela.getSelectionModel().select(dirfTabela.getItems().size() - 1);
            dirfTabela.valueProperty().addListener((observable, oldValue, newValue) -> { ProcessoAno(); });
        }

        Platform.runLater(() -> {PopulateMeses();});
        //ProcessoAno();
    }

    private void PopulateMeses() {
        // Populate meses de movimento do ano ????
        String[] ames = new String[] {"Janeiro","Fevereiro","Março","Abril","Maio","Junho",
                "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        dirfMes.getItems().clear();
        String sql = "SELECT DISTINCT date_part('month',m.dtrecebimento) mes FROM movimento m INNER JOIN proprietarios p ON p.p_rgprp = m.rgprp::int AND p.p_fisjur = 'J' WHERE date_part('year', dtrecebimento) = ?;";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", dirfAno.getValue()}});
        try {
            while (rs.next()) {
                dirfMes.getItems().add(ames[rs.getInt("mes") - 1]);
            }
        } catch (SQLException e) {}
        DbMain.FecharTabela(rs);
        dirfMes.getSelectionModel().select(dirfMes.getItems().size() - 1);
        dirfMes.valueProperty().addListener((observable, oldValue, newValue) -> {});
        Platform.runLater(() -> ProcessoAno());
    }

    private TreeItem seekItem(TreeItem lista, String value) {
        if (lista != null && lista.getValue().toString().contains(value))
            return  lista;

        for (Object child : lista.getChildren()){
            if (((TreeItem) child).getValue().toString().contains(value)) return (TreeItem) child;

            for (Object o : ((TreeItem) child).getChildren()) {
                if (((TreeItem) o).getValue().toString().contains(value)) return (TreeItem) o;
            }
        }
        return null;
    }

    private void ProcessoAno() {
        dirfPreview.setDisable(true);

        Task<Integer> task = new Task<Integer>() {
            int i = 1;

            @Override
            protected Integer call() throws InterruptedException {
                String sqlPart = "FROM movimento m INNER JOIN proprietarios p ON p.p_rgprp = m.rgprp::int AND p.p_fisjur = 'J' WHERE date_part('year', dtrecebimento) = ?;";
                String sql = "SELECT DISTINCT m.rgprp ";
                ResultSet rs = conn.AbrirTabela(sql + sqlPart ,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", dirfAno.getValue()}});
                int nTotReg = DbMain.RecordCount(rs);
                dirf = new Object[][] {};
                try {
                    while (rs.next()) {
                        Thread.sleep(30);
                        updateProgress(i, nTotReg);
                        updateMessage("(" + i++ + "/" + nTotReg + ")");

                        Object[] presult = new DadosProp().DadosProp(rs.getString("rgprp"), dirfAno.getValue().toString());

                        // Verificar na tabela selecionada
                        BigDecimal pfAL = new BigDecimal("0");
                        BigDecimal pfIR = new BigDecimal("0");
                        BigDecimal pjAL = new BigDecimal("0");
                        BigDecimal pjIR = new BigDecimal("0");
                        Object[] imv = (Object[]) presult[3];
                        for (Object o : (Object[]) imv) {
                            String doc = (((Object[])((Object[]) o)[8])[1].toString()).replace(".","").replace("-", "").replace("/","");
                            Object[] meses = (Object[]) ((Object[]) o)[9];
                            for (Object[] m : (Object[][]) meses) {
                                if (m[0].toString().equalsIgnoreCase(ConvMes(dirfMes.getSelectionModel().getSelectedItem().toString()))) {
                                    if (doc.length() <= 11) {
                                        // Fisica
                                        pfAL = pfAL.add((BigDecimal) m[1]).add((BigDecimal) m[6]).add((BigDecimal) m[7])
                                                .add((BigDecimal) m[8]).add((BigDecimal) m[5]).subtract((BigDecimal) m[4]);
                                        pfIR = pfIR.add((BigDecimal) m[3]);
                                    } else {
                                        // Juridica
                                        pjAL = pjAL.add((BigDecimal) m[1]).add((BigDecimal) m[6]).add((BigDecimal) m[7])
                                                .add((BigDecimal) m[8]).add((BigDecimal) m[5]).subtract((BigDecimal) m[4]);
                                        pjIR = pjIR.add((BigDecimal) m[3]);
                                    }
                                }
                            }
                        }
                        BigDecimal[] pf_irf = TabelaIR(dirfTabela.getSelectionModel().getSelectedItem().toString(), pfAL);
                        BigDecimal[] pj_irf = TabelaIR(dirfTabela.getSelectionModel().getSelectedItem().toString(), pjAL);
                        if ( pf_irf[0].longValue() > 0 || pj_irf[0].longValue() > 0) {
                            dirf = FuncoesGlobais.ObjectsAdd(dirf, presult);
                        }
                    }
                } catch (SQLException e) {}

                return i;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                ListaArvore();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelado!");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Falhou!");
            }
        };
        dirfBarra.progressProperty().bind(task.progressProperty());
        dirfStatus.textProperty().bind(task.messageProperty());
        dirfSelectAll.disableProperty().bind(task.runningProperty());
        dirfAno.setDisable(true);


        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void ListaArvore() {
        Task<Integer> task2 = new Task<Integer>() {
            int i = 1;
            @Override
            protected Integer call() {
                try {
                    i = 1; int nTotReg = dirf.length; int j = 1;
                    CheckBoxTreeItem prop = null;
                    CheckBoxTreeItem rootItem = new CheckBoxTreeItem("Proprietarios");
                    for (Object[] obj : dirf) {
                        Thread.sleep(30);
                        updateProgress(i, nTotReg);
                        updateMessage("(" + i++ + "/" + nTotReg + ")");

                        prop = new CheckBoxTreeItem("Prop: " + obj[0] + " - " + obj[1]);
                        rootItem.getChildren().addAll(prop);
                    }
                    Platform.runLater(() -> dirfLista.setRoot(rootItem));
                } catch (Exception e) {}
                return i;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage(dirf.length > 0 ? "Pronto!" : "Sem processos...");

                dirfAno.setDisable(false);

                Platform.runLater(() -> {
                    dirfLista.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
                        @Override
                        public void onChanged(Change c) {
                            dirfPreview.setDisable(c.getList().size() == 0);
                        }
                    });
                });
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelado!");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Falhou!");
            }
        };

        dirfBarra.progressProperty().bind(task2.progressProperty());
        dirfStatus.textProperty().bind(task2.messageProperty());
        dirfSelectAll.disableProperty().bind(task2.runningProperty());

        Thread t = new Thread(task2);
        t.setDaemon(true);
        t.start();
    }
    
    private BigDecimal[] TabelaIR(String mesano, BigDecimal valor) {
        BigDecimal ir = new BigDecimal("0");
        BigDecimal dir = new BigDecimal("0");

        String tSql = "SELECT * FROM irrf WHERE mesano = '%s';";
        tSql = String.format(tSql, mesano);
        ResultSet trs = conn.AbrirTabela(tSql, ResultSet.CONCUR_READ_ONLY);
        try {
            while (trs.next()) {
                if (valor.compareTo(trs.getBigDecimal("faixa1")) == -1 || valor.compareTo(trs.getBigDecimal("faixa1")) == 0) {
                    // Isento
                    ir = new BigDecimal("0");
                    dir = new BigDecimal("0");
                } else if (valor.compareTo(trs.getBigDecimal("faixa1")) == 1 && (valor.compareTo(trs.getBigDecimal("faixa2")) == -1 || valor.compareTo(trs.getBigDecimal("faixa2")) == 0)) {
                    ir = trs.getBigDecimal("aliquota2");
                    dir = trs.getBigDecimal("deducao2");
                } else if (valor.compareTo(trs.getBigDecimal("faixa2")) == 1 && (valor.compareTo(trs.getBigDecimal("faixa3")) == -1 || valor.compareTo(trs.getBigDecimal("faixa3")) == 0)) {
                    ir = trs.getBigDecimal("aliquota3");
                    dir = trs.getBigDecimal("deducao3");
                } else if (valor.compareTo(trs.getBigDecimal("faixa3")) == 1 && (valor.compareTo(trs.getBigDecimal("faixa4")) == -1 || valor.compareTo(trs.getBigDecimal("faixa4")) == 0)) {
                    ir = trs.getBigDecimal("aliquota4");
                    dir = trs.getBigDecimal("deducao4");
                } else if (valor.compareTo(trs.getBigDecimal("faixa4")) == 1 && (valor.compareTo(trs.getBigDecimal("faixa5")) == -1 || valor.compareTo(trs.getBigDecimal("faixa5")) == 0)) {
                    ir = trs.getBigDecimal("aliquota5");
                    dir = trs.getBigDecimal("deducao5");
                } else if (valor.compareTo(trs.getBigDecimal("faixa5")) == 1 ) {
                    ir = trs.getBigDecimal("aliquota5");
                    dir = trs.getBigDecimal("deducao5");
                }
            }
        } catch (SQLException e) {}
        finally { try {trs.close();} catch (SQLException e) {} }

        return new BigDecimal[] {ir, dir};
    }

    private String ConvMes(String mes) {
        String nmes = "00";
        switch (mes) {
            case "Janeiro":
                nmes = "01";
                break;
            case "Fevereiro":
                nmes = "02";
                break;
            case "Março":
                nmes = "03";
                break;
            case "Abril":
                nmes = "04";
                break;
            case "Maio":
                nmes = "05";
                break;
            case "Junho":
                nmes = "06";
                break;
            case "Julho":
                nmes = "07";
                break;
            case "Agosto":
                nmes = "08";
                break;
            case "Setembro":
                nmes = "09";
                break;
            case "Outubro":
                nmes = "10";
                break;
            case "Novembro":
                nmes = "11";
                break;
            case "Dezembro":
                nmes = "12";
                break;
        }
        return nmes;
    }
}
