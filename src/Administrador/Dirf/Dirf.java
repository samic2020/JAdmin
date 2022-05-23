package Administrador.Dirf;

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

public class Dirf implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean inProcess = false;
    Object[][] dirf = {};

    @FXML private AnchorPane anchorPaneDirf;
    @FXML private Spinner<Integer> dirfAno;
    @FXML private ProgressBar dirfBarra;
    @FXML private Label dirfStatus;
    @FXML private CheckTreeView dirfLista;
    @FXML private Button dirfPreview;
    @FXML private CheckBox dirfSelectAll;
    @FXML private TextField dirfBusca;
    @FXML private Label dirfClean;

    @FXML private ToggleGroup proploca;
    @FXML private RadioButton rbtLoca;

    @FXML
    void ClearClick(MouseEvent event) {
        dirfBusca.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, 2050, 2020);
        dirfAno.setValueFactory(ano);
        dirfAno.valueProperty().addListener((observable, oldValue, newValue) -> { ProcessoAno(); });
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
            List<DirfClass> lista = new ArrayList<>();
            for (int i = 0; i < dirfLista.getCheckModel().getCheckedItems().size(); i++) {
                if (!dirfLista.getCheckModel().getCheckedItems().get(i).toString().contains("Prop")) {
                    String rgimv = ((TreeItem)dirfLista.getCheckModel().getCheckedItems().get(i)).getValue().toString().substring(0,6);

                    for (Object[] item : dirf) {
                        Object[] imv = (Object[]) item[3];
                        for (Object subItem : imv) {
                            if(((Object[])((Object[]) subItem)[8])[0].toString().equalsIgnoreCase(rgimv.toString().trim())) {
                                DirfClass values = new DirfClass(
                                        dirfAno.getValue().toString(),
                                        item[0].toString(),
                                        item[1].toString(),
                                        item[2].toString(),
                                        (((Object[])subItem)[7]).toString(),
                                        (((Object[])subItem)[0]).toString(),
                                        (((Object[])subItem)[1].toString() + ", " + ((Object[])subItem)[2].toString() +
                                                " " + ((Object[])subItem)[3].toString()),
                                        (((Object[])subItem)[4]).toString(),
                                        (((Object[])subItem)[5]).toString(),
                                        (((Object[])subItem)[6]).toString(),
                                        (((Object[])((Object[]) subItem)[8])[0]).toString(),
                                        (((Object[])((Object[]) subItem)[8])[2]).toString(),
                                        (((Object[])((Object[]) subItem)[8])[1]).toString(),
                                        Dates.DateFormata("dd-MM-yyyy", (Date)((Object[])((Object[]) subItem)[8])[3])
                                );

                                Object[][] meses = (Object[][])((Object[]) subItem)[9];
                                for (Object[] ames : meses ) {
                                    Integer mes = Integer.valueOf(ames[0].toString()) - 1;
                                    values.setMes(mes,
                                            VariaveisGlobais.contas_ca.get("ALU"),
                                            (BigDecimal) ames[1],
                                            VariaveisGlobais.contas_ca.get("COM"),
                                            (BigDecimal) ames[2],
                                            "IRRF",
                                            (BigDecimal) ames[3],
                                            (BigDecimal) ames[4],
                                            (BigDecimal) ames[5],
                                            VariaveisGlobais.contas_ca.get("MUL"),
                                            (BigDecimal) ames[6],
                                            VariaveisGlobais.contas_ca.get("JUR"),
                                            (BigDecimal) ames[7],
                                            VariaveisGlobais.contas_ca.get("COR"),
                                            (BigDecimal) ames[8],
                                            VariaveisGlobais.contas_ca.get("EXP"),
                                            (BigDecimal) ames[9],
                                            (BigDecimal) ames[10],
                                            (BigDecimal) ames[11],
                                            (BigDecimal) ames[12],
                                            (BigDecimal) ames[13]
                                    );
                                }
                                lista.add(values);
                            }
                        }
                    }
                }
            }
            if (lista.size() >= 0) {
                String pdfName = new PdfViewer().GeraPDFTemp(lista,"Dirf" + (rbtLoca.isSelected() ? "" : "Prop"), null);
                new PdfViewer("Preview da Dirf", pdfName);
            }
        });

        ProcessoAno();
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
                String sqlPart = "FROM movimento m WHERE date_part('year', dtrecebimento) = ?;";
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
                        dirf = FuncoesGlobais.ObjectsAdd(dirf, presult);
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

                        if (obj[3] != null) {
                            for (Object imv : (Object[]) obj[3]) {
                                //Object[] dados_imovel = (Object[]) imv;
                                Object[] dados_locatario = (Object[]) ((Object[]) imv)[8];

                                CheckBoxTreeItem loca = new CheckBoxTreeItem(dados_locatario[0] + " - " + dados_locatario[2]);
                                prop.getChildren().addAll(loca);
                            }
                            if (!prop.getChildren().isEmpty()) rootItem.getChildren().addAll(prop);
                        }
                    }
                    Platform.runLater(() -> dirfLista.setRoot(rootItem));
                } catch (Exception e) {}
                return i;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
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

}
