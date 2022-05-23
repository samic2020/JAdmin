package Administrador.Dimob;

import Administrador.DadosProp;
import Funcoes.*;
import Locatarios.LocatarioController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.rad.genui.UIColor;
import javax.rad.genui.UIImage;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

import static Funcoes.LerValor.FloatToString;

public class Dimob implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean inProcess = false;
    Object[][] dimob = {};

    @FXML private AnchorPane anchorPaneDimob;
    @FXML private Spinner<Integer> dmbAno;
    @FXML private ProgressBar dmbProgress;
    @FXML private Label dmbFase;
    @FXML private TextField dmbFile;
    @FXML private Button dmbBtnFile;
    @FXML private Button dmbBtnGerar;
    @FXML private TextField bu_linha;
    @FXML private Button btnBusca;
    @FXML private Label bu_tipo;
    @FXML private TextField bu_result;
    @FXML private Button btnFicha;
    @FXML private Label dmbStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, 2050, 2020);
            dmbAno.setValueFactory(ano);

            dmbFile.setText("");
            dmbFase.setText("FASE (00/00):");

            dmbBtnFile.setOnAction(event -> {
                FileChooser fd = new FileChooser();
                fd.setTitle("Arquivos de exportacao Dimob");
                fd.setInitialDirectory(new File(VariaveisGlobais.appPath));
                fd.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));

                Node e = (Node) event.getSource();
                Window ps = e.getScene().getWindow();

                File file = fd.showSaveDialog(ps);
                if (file != null) {
                    dmbFile.setText(file.toString());
                }
            });
        });

        btnBusca.setOnAction(event -> {
            if (Integer.valueOf(bu_linha.getText()) <= 0) return;
            try (
                BufferedReader reader = new BufferedReader(new FileReader(new File(dmbFile.getText())))) {
                String line; int curLine = 1;
                while ((line = reader.readLine()) != null) {
                    if (curLine == Integer.valueOf(bu_linha.getText())) {
                        if (line.length() > 170) {
                            String searchContrato = line.substring(174, 179);
                            bu_result.setText(searchContrato);
                        } else bu_result.setText("");
                        break;
                    }
                    curLine++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnFicha.setOnAction(event -> {
            if (bu_result.getText().isEmpty()) return;
            try { ChamaTela("Locatarios", "/Locatarios/Locatario.fxml", "prop.png", null, false, false); } catch (Exception e) {}
        });

        dmbBtnGerar.setOnAction(event -> {
            dmbFase.setText("FASE (01/02):");
            Task<Integer> task = new Task<Integer>() {
                int i = 1;

                @Override
                protected Integer call() throws InterruptedException {
                    String sqlPart = "FROM movimento m WHERE date_part('year', dtrecebimento) = ?;";
                    String sql = "SELECT DISTINCT m.rgprp ";
                    ResultSet rs = conn.AbrirTabela(sql + sqlPart ,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", dmbAno.getValue()}});
                    int nTotReg = DbMain.RecordCount(rs);
                    try {
                        while (rs.next()) {
                            Thread.sleep(30);
                            updateProgress(i, nTotReg);
                            updateMessage("(" + i++ + "/" + nTotReg + ")");

                            Object[] presult = new DadosProp().DadosProp(rs.getString("rgprp"), dmbAno.getValue().toString());
                            dimob = FuncoesGlobais.ObjectsAdd(dimob, presult);
                        }
                    } catch (SQLException e) {}

                    return i;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    updateMessage("Fase (01/02) Completo!");
                    Fase02();
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
            dmbProgress.progressProperty().bind(task.progressProperty());
            dmbStatus.textProperty().bind(task.messageProperty());
            dmbBtnGerar.disableProperty().bind(task.runningProperty());

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });
    }

    private void Fase02() {
        dmbFase.setText("FASE (02/02):");
        Task<Integer> task2 = new Task<Integer>() {
            int i = 0;
            @Override
            protected Integer call() {
                try {
                    // -x-x-x-x-x-x-x-
                    StreamFile file = new StreamFile(new String[] {dmbFile.getText()});
                    if (file.Open()) {
                        // Registro tipo Header
                        file.Print(
                                "DIMOB" +
                                        FuncoesGlobais.Space(369) +
                                        "\r\n"
                        );

                        // Reristro tipo 01
                        Collections dadm = VariaveisGlobais.getAdmDados();
                        file.Print(
                                "R01" +
                                        VariaveisGlobais.da_cnpj.replace(".", "").replace("-", "").replace("/", "") +
                                        FuncaoN(dmbAno.getValue().toString(),4) +
                                        "0" +
                                        FuncoesGlobais.Space(10) +
                                        "0" +
                                        "00000000" +
                                        "00" +
                                        FuncaoX(dadm.get("empresa").toUpperCase(), 60) +
                                        (VariaveisGlobais.da_respcpf == null ? "00000000000" : ("" + VariaveisGlobais.da_respcpf).replace(".", "").replace("-", "").replace("/", "")) +
                                        FuncaoX((dadm.get("endereco") + ", " + dadm.get("numero") + " " + dadm.get("complemento")).toUpperCase(), 120) +
                                        FuncaoX(dadm.get("estado").toUpperCase(), 2) +
                                        FuncaoN(VariaveisGlobais.da_codmun, 4) +
                                        FuncoesGlobais.Space(20) +
                                        FuncoesGlobais.Space(10) +
                                        "\r\n"
                        );

                        i = 0; int nTotReg = dimob.length; int j = 1;
                        for (Object[] obj : dimob) {
                            Thread.sleep(30);
                            updateProgress(i, nTotReg);
                            updateMessage("(" + i++ + "/" + nTotReg + ")");

                            for (Object imv : (Object[]) obj[3]) {
                                Object[] dados_imovel = (Object[]) imv;
                                Object[] dados_locatario = (Object[]) ((Object[]) imv)[8];
                                Object[][] meses = (Object[][]) ((Object[]) imv)[9];
                                String cMeses = "";
                                BigDecimal _AL = new BigDecimal("0"); BigDecimal _CM = new BigDecimal("0");
                                BigDecimal _MU = new BigDecimal("0"); BigDecimal _JU = new BigDecimal("0");
                                BigDecimal _CO = new BigDecimal("0"); BigDecimal _EP = new BigDecimal("0");
                                BigDecimal _TX = new BigDecimal("0"); BigDecimal _IR = new BigDecimal("0");
                                BigDecimal _DC = new BigDecimal("0"); BigDecimal _DF = new BigDecimal("0");
                                for (int m=0; m<meses.length;m++) {
                                    _AL = (BigDecimal) meses[m][1];
                                    _DC = (BigDecimal) meses[m][4];
                                    _DF = (BigDecimal) meses[m][5];

                                    _CM = (BigDecimal) meses[m][2];
                                    _IR = (BigDecimal) meses[m][3];

                                    _MU = (BigDecimal) meses[m][6];
                                    _JU = (BigDecimal) meses[m][7];
                                    _CO = (BigDecimal) meses[m][8];
                                    _EP = (BigDecimal) meses[m][9];

                                    _TX = (BigDecimal) meses[m][10];

                                    if (VariaveisGlobais.dip_al) {
                                        BigDecimal _RS = new BigDecimal("0");
                                        BigDecimal _RSC = new BigDecimal("0");
                                        BigDecimal _RSI = new BigDecimal("0");
                                        _RS = _AL.add(VariaveisGlobais.dip_mu ? _MU : new BigDecimal("0")).
                                                add(VariaveisGlobais.dip_ju ? _JU : new BigDecimal("0")).
                                                add(VariaveisGlobais.dip_co ? _CO : new BigDecimal("0")).
                                                add(VariaveisGlobais.dip_ep ? _EP : new BigDecimal("0")).
                                                add(VariaveisGlobais.dip_tx ? _TX : new BigDecimal("0")).
                                                add(_DF).subtract(_DC);
                                        _RSC = _CM.add(VariaveisGlobais.dia_mu ? _MU : new BigDecimal("0")).
                                                add(VariaveisGlobais.dia_ju ? _JU : new BigDecimal("0")).
                                                add(VariaveisGlobais.dia_co ? _CO : new BigDecimal("0")).
                                                add(VariaveisGlobais.dia_ep ? _EP : new BigDecimal("0")).
                                                add(VariaveisGlobais.dia_tx ? _TX : new BigDecimal("0"));
                                        _RSI = _IR;

                                        cMeses += FuncaoR(_RS) + FuncaoR(_RSC) + FuncaoR(_RSI);
                                    }
                                }

                                file.Print(
                            "R02" +
                                    // Dados de cabeçário
                                    VariaveisGlobais.da_cnpj.replace(".", "").replace("-", "").replace("/", "") +
                                    FuncaoN(dmbAno.getValue().toString(),4) +
                                    FuncaoN(String.valueOf(j++).trim().replace(".0", "").replace(".00", ""),5) +

                                    FuncaoX(obj[2].toString().replace(".", "").replace("-", "").replace("/", ""),14) +
                                    FuncaoX(obj[1].toString().toUpperCase(), 60) +

                                    FuncaoX(dados_locatario[1].toString().replace(".", "").replace("-", "").replace("/", ""),14) +

                                    FuncaoX(dados_locatario[2].toString().toUpperCase(), 60) +
                                    FuncaoX(dados_locatario[0].toString(), 6) +
                                    Dates.StringtoString(Dates.DateFormata("dd/MM/yyyy", (Date) dados_locatario[3]), "dd/MM/yyyy", "ddMMyyyy") +
                                    cMeses +

                                    // Dados do imóvel
                                    FuncaoX(dados_imovel[7].toString().toUpperCase(), 1) +
                                    FuncaoX((dados_imovel[1].toString() + "," + dados_imovel[2].toString() + " " + dados_imovel[3].toString()).toUpperCase(), 60) +
                                    FuncaoN(dados_imovel[4].toString().replace("-", ""), 8) +
                                    FuncaoN(dados_imovel[5].toString(), 4) +
                                    FuncoesGlobais.Space(20) +
                                    FuncaoX(dados_imovel[6].toString().toUpperCase(), 2) +
                                    FuncoesGlobais.Space(10) +
                                    "\r\n"
                                );
                            }
                        }
                    }

                    // Registro tipo T9
                    file.Print(
                            "T9" +
                                    FuncoesGlobais.Space(100) +
                                    "\r\n"
                    );

                    file.Close();
                } catch (Exception e) {e.printStackTrace();}
                return i;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Fase (02/02) Completo!");
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

        dmbProgress.progressProperty().bind(task2.progressProperty());
        dmbStatus.textProperty().bind(task2.messageProperty());
        dmbBtnGerar.disableProperty().bind(task2.runningProperty());

        Thread t = new Thread(task2);
        t.setDaemon(true);
        t.start();
    }

    private void ChamaTela(String nome, String url, String icone, String viea, boolean resize, boolean close) throws IOException, Exception {
        AnchorPane root = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            loader = new FXMLLoader(getClass().getResource(url));
            try { root = (AnchorPane) loader.load(); } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
        UICustomComponent wrappedRoot = new UICustomComponent(root);

        UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
        internalFrame.setLayout(new UIBorderLayout());
        internalFrame.setModal(false);
        internalFrame.setResizable(resize);
        internalFrame.setMaximizable(false);
        internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
        internalFrame.setTitle(nome.replace("_", ""));
        internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true); //close);
        internalFrame.setBackground(new UIColor(221,221, 221));

        internalFrame.pack();
        internalFrame.setVisible(true);

        LocatarioController controller = null;
        controller = loader.getController();
        controller.MoveTo("l_contrato",bu_result.getText());
    }

    private String TrocaNome(String value) {
        for (int contas = 0; contas <= VariaveisGlobais.contas_ca.size() - 1; contas++) {
            value = value.replace("[" + VariaveisGlobais.contas_ca.key(contas) + "]", VariaveisGlobais.contas_ca.get(VariaveisGlobais.contas_ca.key(contas)));
        }
        return value;
    }

    private String FuncaoN(String Value, int tam) {
        if (Value == null) return FuncoesGlobais.Repete("0", tam);
        return FuncoesGlobais.StrZero(Value, tam);
    }

    private String FuncaoX(String Value, int tam) {
        if (Value == null) return FuncoesGlobais.Space(tam);
        return FuncoesGlobais.deAccent(new Pad(Value, tam).RPad());
    }

    private String FuncaoR(BigDecimal Value) {
        String zeros = FuncoesGlobais.Repete("0", 12);
        String numero = FloatToString(Value.floatValue()).trim();
        int virgula = numero.indexOf(",");
        String parte1 = numero.substring(0,virgula);
        String parte2 = numero.substring(virgula + 1);
        if (parte2.length() == 1) parte2 += "0"; if(parte2.length() == 0) parte2 += "00";
        String rvalor1 = zeros + parte1;
        String rvalor2 =  parte2 + "00";

        //String rvalor = StringManager.Right(rvalor1, 12) + StringManager.Right(rvalor2, 2);

        String rvalor = rvalor1.substring(rvalor1.length() - 12,rvalor1.length()) + rvalor2.substring(0, 2);
        return rvalor;
    }

    // -x-x-x-x-x-x-
/*
    public Object[] DimobProp(String rgprp) {
        // Pega dados do Proprietário
        Object[][] dadosProp = null;
        try {
            dadosProp = conn.LerCamposTabela(new String[] {"p_rgprp", "p_nome", "p_cpfcnpj"},"proprietarios", "p_rgprp = '" + rgprp + "'");
        } catch (Exception e) {}
        Object[] dProp = new Object[] {
                dadosProp[0][3].toString(),  // 00 - rgprp
                dadosProp[1][3].toString(),  // 01 - nome
                dadosProp[2][3].toString(),  // 02 - cpfcnpj
                null                         // 03 - {imoveis]
        };

        Object[][] meses = new Object[][] {
                //MES  AL                       CM                       IR                       DC                       DF                       MU                       JU                       CO                       EP                       TX                       O1                       O2                       O3
                {"01", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"02", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"03", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"04", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"05", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"06", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"07", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"08", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"09", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"10", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"11", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                {"12", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")}
        };

        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // Pegar as percentagens do principal
        List<divSec> dPrin = new PegaDivisao().PegaDivisoes(rgprp);

        // Pegar as divisões secundárias
        List<divSec> dSec = new PegaDivisao().PegaDivSecundaria(rgprp);
        String sDivWhere = "";
        for (divSec campo : dSec) {
            sDivWhere += "select * from (select *, generate_subscripts(aut_pag,1) as pos, generate_subscripts(reserva,1) as rpos from movimento where rgprp = '" + campo.getRgprp() +
                    "' and rgimv = '" + campo.getRgimv() + "' and aut_rec <> 0) as foo where date_part('year', dtrecebimento) = " + dmbAno.getValue() + " AND aut_pag[pos][1] = '" + campo.getRgprp() +
                    "' and aut_pag[pos][2] is null union all ";
        }
        if (sDivWhere.length() > 0) sDivWhere = sDivWhere.substring(0, sDivWhere.length() - 10);

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        if (sDivWhere.length() == 0) {
            sql = "select * from movimento where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and date_part('year', dtrecebimento) = " + dmbAno.getValue() + " order by 2,3,4,7,9;";
            sql = String.format(sql, rgprp);
        } else {
            sql = "select *, 0 as pos, 0 as rpos from movimento where rgprp = '%s' and aut_rec <> 0 and date_part('year', dtrecebimento) = " + dmbAno.getValue() + " union all %s order by 2,3,4,7,9";
            sql = String.format(sql, rgprp, sDivWhere);
        }

        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                Object[][] endImovel = null;
                String rgimv = null;
                try {
                    endImovel = conn.LerCamposTabela(new String[] {"i_end", "i_num", "i_cplto", "i_cep", "i_cdmun", "i_estado", "i_ur"},"imoveis", "i_rgimv = '" + rs.getString("rgimv") + "'");
                    rgimv = rs.getString("rgimv");
                } catch (Exception e) {}

                Object[][] nomeLoca = null;
                try {
                    nomeLoca = conn.LerCamposTabela(new String[] {"CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nomeLoca", "l_cpfcnpj"},"locatarios", "l_contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {}

                Object[][] cartLoca = null;
                try {
                    cartLoca = conn.LerCamposTabela(new String[] {"dtinicio", "dtaditamento"}, "carteira", "contrato = '" + rs.getString("contrato") + "'");
                } catch (Exception e) {}

                //rs.getDate("dtvencimento"),
                String trecebimento = rs.getString("dtrecebimento");

                int pos = -1;
                if (dProp[3] == null) {
                    meses = new Object[][] {
                            //MES  AL                       CM                       IR                       DC                       DF                       MU                       JU                       CO                       EP                       TX                       O1                       O2                       O3
                            {"01", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"02", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"03", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"04", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"05", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"06", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"07", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"08", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"09", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"10", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"11", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                            {"12", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")}
                    };
                    Object[] dImovel = new Object[] {
                            rgimv,                             // 00 - rgimv
                            endImovel[0][3].toString(),        // 01 - endereço
                            endImovel[1][3].toString(),        // 02 - numero
                            endImovel[2][3].toString(),        // 03 - complemento
                            endImovel[3][3].toString(),        // 04 - cep
                            endImovel[4][3].toString(),        // 05 - cod municipio
                            endImovel[5][3].toString(),        // 06 - estado
                            endImovel[6][3].toString().trim().equals("") ? "U" : endImovel[6][3].toString().toUpperCase().substring(0,1),        // 07 - urbano/rural
                            new Object[] {                     // 08
                                    rs.getString("contrato"),                                                           // 08-00 contrato
                                    nomeLoca[1][3].toString(),                                                                     // 08-01 cpfcnpj
                                    nomeLoca[0][3].toString(),                                                                     // 08-02 nomerazao
                                    cartLoca[1][2].toString().equals("0") ? cartLoca[0][3] : cartLoca[1][3],                       // 08-03 dtinicio
                            },
                            meses                              // 09 - meses
                    };
                    dProp[3] = new Object[] {dImovel};
                    pos = 0;
                } else {
                    int ipos = -1;
                    try { ipos = FuncoesGlobais.SeekNObjects((Object[]) dProp[3], 0, rgimv); } catch (Exception e) { ipos = -1;}
                    if (ipos < 0) {
                        // Não achei
                        meses = new Object[][] {
                                //MES  AL                       CM                       IR                       DC                       DF                       MU                       JU                       CO                       EP                       TX                       O1                       O2                       O3
                                {"01", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"02", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"03", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"04", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"05", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"06", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"07", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"08", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"09", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"10", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"11", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")},
                                {"12", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")}
                        };
                        Object[] dImovel = new Object[] {
                                rgimv,                             // 00 - rgimv
                                endImovel[0][3].toString(),        // 01 - endereço
                                endImovel[1][3].toString(),        // 02 - numero
                                endImovel[2][3].toString(),        // 03 - complemento
                                endImovel[3][3].toString(),        // 04 - cep
                                endImovel[4][3].toString(),        // 05 - cod municipio
                                endImovel[5][3].toString(),        // 06 - estado
                                endImovel[6][3].toString().trim().equals("") ? "U" : endImovel[6][3].toString().toUpperCase().substring(0,1),        // 07 - urbano/rural
                                new Object[] {                     // 08
                                        rs.getString("contrato"),                                                           // 08-00 contrato
                                        nomeLoca[1][3].toString(),                                                                     // 08-01 cpfcnpj
                                        nomeLoca[0][3].toString(),                                                                     // 08-02 nomerazao
                                        cartLoca[1][2].toString().equals("0") ? cartLoca[0][3] : cartLoca[1][3],                       // 08-03 dtinicio
                                },
                                meses                              // 09 - meses
                        };
                        dProp[3] = FuncoesGlobais.ObjectsAdd((Object[]) dProp[3], dImovel);
                        pos = ((Object[])dProp[3]).length - 1;
                    } else {
*/
/*
                        Object[] imv = (Object[]) dProp[3];
                        imv[pos] = dImovel;
                        dProp[3] = imv;
*//*

                        pos = ipos;
                    }
                }
                Object[] _imoveis = (Object[]) dProp[3];
                Object[] _imovel = (Object[]) _imoveis[pos];
                Object[][] _meses = (Object[][]) _imovel[9];

                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                int dpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                if (dpos > -1) {
                    String[] divisao = dPrin.get(dpos).getDivisao().split(",");
                    int apos = FuncoesGlobais.IndexOf(divisao,"ALU");
                    if (apos > -1) {
                        palu = new PegaDivisao().LerPercent(divisao[apos],true);
                    }
                } else {
                    dpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                    if (dpos > -1) {
                        String[] divisao = dSec.get(dpos).getDivisao().split(",");
                        int apos = FuncoesGlobais.IndexOf(divisao, "ALU");
                        if (apos > -1) {
                            palu = new PegaDivisao().LerPercent(divisao[apos], true);
                        } else {
                            palu = new BigDecimal("100");
                        }
                    } else {
                        palu = new BigDecimal("100");
                    }
                }
                alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                int mpos = FuncoesGlobais.FindinObjects(meses, 0, trecebimento.trim().substring(5, 7));
                if (mpos > -1) {
                    meses[mpos][1] = ((BigDecimal)meses[mpos][1]).add(rs.getBigDecimal("mensal"));
                }

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    if (sDivWhere.length() == 0) {
                        dfsql = "select * from descdif where rgprp = '%s' and aut_rec <> 0 and ad_pag is null and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null AND date_part('year', dtrecebimento) = " + dmbAno.getValue() + " order by 2,3,4,7,9;";
                        dfsql = String.format(dfsql, rgprp, rgprp);
                    } else {
                        dfsql = "select *, 0 as pos, 0 as rpos from descdif where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null AND date_part('year', dtrecebimento) = " + dmbAno.getValue() + " union all %s order by 2,3,4,7,9";
                        dfsql = String.format(dfsql, rgprp, rgprp, sDivWhere.replace("movimento","descdif"));
                    }
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "DiferenÃ§a de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            if (mpos > -1) {
                                meses[mpos][4] = dfCR;
                                meses[mpos][5] = dfDB;
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                ttDB = ttDB.add(com);
                if (mpos > -1) {
                    meses[mpos][2] = ((BigDecimal) meses[mpos][2]).add(com);
                }

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    int irpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (irpos > -1) {
                        String[] divisao = dPrin.get(irpos).getDivisao().split(",");
                        int airpos = FuncoesGlobais.IndexOf(divisao,"IRF");
                        if (airpos > -1) {
                            pir = new PegaDivisao().LerPercent(divisao[airpos],true);
                        }
                    } else {
                        irpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (irpos > -1) {
                            String[] divisao = dSec.get(irpos).getDivisao().split(",");
                            int airpos = FuncoesGlobais.IndexOf(divisao, "IRF");
                            if (airpos > -1) {
                                pir = new PegaDivisao().LerPercent(divisao[airpos], true);
                            }
                        }
                    }
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }

                    if (mpos > -1) {
                        meses[mpos][3] = ((BigDecimal) meses[mpos][3]).add(pirvr);
                    }

                }

                // Seguros
                {
                    String sgsql = "";
                    if (sDivWhere.length() == 0) {
                        sgsql = "select * from seguros where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null AND date_part('year', dtrecebimento) = " + dmbAno.getValue() + " order by 2,3,4,7,9;";
                        sgsql = String.format(sgsql, rgprp, rgprp);
                    } else {
                        sgsql = "select *, 0 as pos, 0 as rpos from seguros where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null AND date_part('year', dtrecebimento) = " + dmbAno.getValue() + " union all %s order by 2,3,4,7,9";
                        sgsql = String.format(sgsql, rgprp, rgprp, sDivWhere.replace("movimento","seguros"));
                    }
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            int dsgpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (dsgpos > -1) {
                                String[] divisao = dPrin.get(dsgpos).getDivisao().split(",");
                                int asgpos = FuncoesGlobais.IndexOf(divisao,"SEG");
                                if (asgpos > -1) {
                                    psg = new PegaDivisao().LerPercent(divisao[asgpos],true);
                                }
                            } else {
                                dsgpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (dsgpos > -1) {
                                    String[] divisao = dSec.get(dsgpos).getDivisao().split(",");
                                    int asgpos = FuncoesGlobais.IndexOf(divisao, "SEG");
                                    if (asgpos > -1) {
                                        psg = new PegaDivisao().LerPercent(divisao[asgpos], true);
                                    } else {
                                        psg = new BigDecimal("100");
                                    }
                                } else {
                                    psg = new BigDecimal("100");
                                }
                            }

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                            if (mpos > -1) {
                                meses[mpos][10] = ((BigDecimal) meses[mpos][10]).add(seg);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

*/
/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            int ippos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (ippos > -1) {
                                String[] divisao = dPrin.get(ippos).getDivisao().split(",");
                                int aippos = FuncoesGlobais.IndexOf(divisao,"IRF");
                                if (aippos > -1) {
                                    pir = new PegaDivisao().LerPercent(divisao[aippos],true);
                                }
                            } else {
                                ippos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (ippos > -1) {
                                    String[] divisao = dSec.get(ippos).getDivisao().split(",");
                                    int aippos = FuncoesGlobais.IndexOf(divisao, "IPT");
                                    if (aippos > -1) {
                                        pip = new PegaDivisao().LerPercent(divisao[aippos], true);
                                    }
                                }
                            }
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                Extrato = new jExtrato(Descr(VariaveisGlobais.contas_ca.get("IPT")), null, pipvr);
                                lista.add(Extrato);
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*//*


                // Taxas
                {
                    String txsql = "";
                    if (sDivWhere.length() == 0) {
                        txsql = "select * from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null AND date_part('year', dtrecebimento) = " + dmbAno.getValue() + " order by 2,3,4,7,9;";
                        txsql = String.format(txsql, rgprp, rgprp);
                    } else {
                        txsql = "select *, 0 as pos, 0 as rpos from taxas where rgprp = '%s' and ad_pag is null and aut_rec <> 0 and (aut_pag is null or aut_pag[1][1] = '%s') and aut_pag[1][2] is null AND date_part('year', dtrecebimento) = " + dmbAno.getValue() + " union all %s order by 2,3,4,7,9";
                        txsql = String.format(txsql, rgprp, rgprp, sDivWhere.replace("movimento","taxas"));
                    }
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            int dtxpos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                            if (dtxpos > -1) {
                                String[] divisao = dPrin.get(dtxpos).getDivisao().split(",");
                                int atxpos = FuncoesGlobais.IndexOf(divisao,txrs.getString("campo"));
                                if (atxpos > -1) {
                                    ptx = new PegaDivisao().LerPercent(divisao[atxpos],true);
                                }
                            } else {
                                dtxpos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                                if (dtxpos > -1) {
                                    String[] divisao = dSec.get(dtxpos).getDivisao().split(",");
                                    int atxpos = FuncoesGlobais.IndexOf(divisao, txrs.getString("campo"));
                                    if (atxpos > -1) {
                                        ptx = new PegaDivisao().LerPercent(divisao[atxpos], true);
                                    } else {
                                        ptx = new BigDecimal("100");
                                    }
                                } else {
                                    ptx = new BigDecimal("100");
                                }
                            }

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            String txdecr = conn.LerCamposTabela(new String[] {"descricao"}, "campos","codigo = '" + txrs.getString("campo") + "'")[0][3].toString();
                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                            if (mpos > -1) {
                                meses[mpos][10] = ((BigDecimal) meses[mpos][10]).add(txcom);
                            }
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    int mupos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (mupos > -1) {
                        String[] divisao = dPrin.get(mupos).getDivisao().split(",");
                        int amupos = FuncoesGlobais.IndexOf(divisao,"MUL");
                        if (amupos > -1) {
                            pmu = new PegaDivisao().LerPercent(divisao[amupos],true);
                        }
                    } else {
                        mupos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (mupos > -1) {
                            String[] divisao = dSec.get(mupos).getDivisao().split(",");
                            int amupos = FuncoesGlobais.IndexOf(divisao, "MUL");
                            if (amupos > -1) {
                                pmu = new PegaDivisao().LerPercent(divisao[amupos], true);
                            } else {
                                pmu = new BigDecimal("100");
                            }
                        } else {
                            pmu = new BigDecimal("100");
                        }
                    }
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pmuvr);
                        if (mpos > -1) {
                            meses[mpos][6] = ((BigDecimal) meses[mpos][6]).add(pmuvr);
                        }
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    int jupos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (jupos > -1) {
                        String[] divisao = dPrin.get(jupos).getDivisao().split(",");
                        int ajupos = FuncoesGlobais.IndexOf(divisao,"JUR");
                        if (ajupos > -1) {
                            pju = new PegaDivisao().LerPercent(divisao[ajupos],true);
                        }
                    } else {
                        jupos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (jupos > -1) {
                            String[] divisao = dSec.get(jupos).getDivisao().split(",");
                            int ajupos = FuncoesGlobais.IndexOf(divisao, "JUR");
                            if (ajupos > -1) {
                                pju = new PegaDivisao().LerPercent(divisao[ajupos], true);
                            } else {
                                pju = new BigDecimal("100");
                            }
                        } else {
                            pju = new BigDecimal("100");
                        }
                    }
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pjuvr);
                        if (mpos > -1) {
                            meses[mpos][7] = ((BigDecimal) meses[mpos][7]).add(pjuvr);
                        }
                    }
                }

                // CORREÇÃO
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                {
                    int copos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (copos > -1) {
                        String[] divisao = dPrin.get(copos).getDivisao().split(",");
                        int acopos = FuncoesGlobais.IndexOf(divisao,"COR");
                        if (acopos > -1) {
                            pco = new PegaDivisao().LerPercent(divisao[acopos],true);
                        }
                    } else {
                        copos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (copos > -1) {
                            String[] divisao = dSec.get(copos).getDivisao().split(",");
                            int acopos = FuncoesGlobais.IndexOf(divisao, "COR");
                            if (acopos > -1) {
                                pco = new PegaDivisao().LerPercent(divisao[acopos], true);
                            } else {
                                pco = new BigDecimal("100");
                            }
                        } else {
                            pco = new BigDecimal("100");
                        }
                    }
                    try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                    try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                    try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                    if (pcovr.doubleValue() != 0) {
                        ttCR = ttCR.add(pcovr);
                        if (mpos > -1) {
                            meses[mpos][8] = ((BigDecimal) meses[mpos][8]).add(pcovr);
                        }
                    }
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                {
                    int eppos = FuncoesGlobais.FindinList(dPrin, rs.getString("rgimv"));
                    if (eppos > -1) {
                        String[] divisao = dPrin.get(eppos).getDivisao().split(",");
                        int aeppos = FuncoesGlobais.IndexOf(divisao,"EXP");
                        if (aeppos > -1) {
                            pep = new PegaDivisao().LerPercent(divisao[aeppos],true);
                        }
                    } else {
                        eppos = FuncoesGlobais.FindinList(dSec, rs.getString("rgimv"));
                        if (eppos > -1) {
                            String[] divisao = dSec.get(eppos).getDivisao().split(",");
                            int aeppos = FuncoesGlobais.IndexOf(divisao, "EXP");
                            if (aeppos > -1) {
                                pep = new PegaDivisao().LerPercent(divisao[aeppos], true);
                            } else {
                                pep = new BigDecimal("100");
                            }
                        } else {
                            pep = new BigDecimal("100");
                        }
                    }
                    try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                    try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                    try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                    if (pepvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pepvr);
                        if (mpos > -1) {
                            meses[mpos][9] = ((BigDecimal) meses[mpos][9]).add(pepvr);
                        }
                    }
                }
                _imovel[9] = _meses;
                _imoveis[pos] = _imovel;
                dProp[3] = _imoveis;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return dProp;
    }
*/
}
