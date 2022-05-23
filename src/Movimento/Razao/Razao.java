package Movimento.Razao;

import Calculos.Multas;
import Funcoes.*;
import Funcoes.Collections;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Razao implements Initializable {
    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker dpkData;
    @FXML private CheckBox rzFecha;

    @FXML private Spinner<Integer> razAno;
    @FXML private TableView<cRazao> razLista;
    @FXML private TableColumn<cRazao, Date> razLista_data;

    @FXML private Button btnPreview;

    DbMain conn = VariaveisGlobais.conexao;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory ano = new SpinnerValueFactory.IntegerSpinnerValueFactory(2018, 2050, 2021);
        ano.setValue(Dates.iYear(DbMain.getDateTimeServer()));
        razAno.setValueFactory(ano);
        razAno.valueProperty().addListener((observable, oldValue, newValue) -> {
            FillRazaoFechadosNoMes();
        });

        dpkData.setValue(Dates.toLocalDate(DbMain.getDateTimeServer()));

        razLista.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && razLista.getSelectionModel().getSelectedItem() != null) {
                ListaRazao(razLista.getSelectionModel().getSelectedItem().getData());
            }
        });

        btnPreview.setOnAction(event -> {
            Date iniData = null;
            Date fimData = null;
            Date atuData = Dates.toDate(dpkData.getValue());

            if (Dates.iDay(Dates.toDate(dpkData.getValue())) > 1) {
                iniData = Dates.primDataMes(Dates.toDate(dpkData.getValue()));
                fimData = Dates.DateAdd(Dates.DIA, -1, Dates.toDate(dpkData.getValue()));
            }

            // Variavel de Impress�o
            Object[][] dadosImpr = {};

            // Extratos
            BigDecimal[] antEXT = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antEXT = antExtrato(iniData, fimData);
            }

            BigDecimal[] diaEXT = diaExtrato(atuData);
            BigDecimal sdaEXT = diaEXT[2].add(antEXT[2]);
            //System.out.println("PROPRIETARIOS.......... CR: " + diaEXT[0].doubleValue() + " DB: " + diaEXT[1].doubleValue() + " SD: " + diaEXT[2].doubleValue() + " SD.ANT: " + antEXT[2].doubleValue() + " SD.ATU: " + sdaEXT.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"PROPRIETARIOS", diaEXT[0].doubleValue(), diaEXT[1].doubleValue(), diaEXT[2].doubleValue(), antEXT[2].doubleValue(), sdaEXT.doubleValue()});
            // - FIM Extratos

            // Adiantamentos
            BigDecimal[] antADI = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antADI = antAdiantamentos(iniData, fimData);
            }

            BigDecimal[] diaADI = diaAdiantamentos(atuData);
            BigDecimal sdaADI = diaADI[2].add(antADI[2]);

            //System.out.println("ADIANTAMENTOS.......... CR: " + diaADI[0].doubleValue() + " DB: " + diaADI[1].doubleValue() + " SD: " + diaADI[2].doubleValue() + " SD.ANT: " + antADI[2].doubleValue() + " SD.ATU: " + sdaADI.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"ADIANTAMENTOS", diaADI[0].doubleValue(), diaADI[1].doubleValue(), diaADI[2].doubleValue(), antADI[2].doubleValue(), sdaADI.doubleValue()});
            // - FIM Adiantamentos

            // Locatarios
            BigDecimal[] antLOC = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antLOC = antLocatario(iniData, fimData);
            }

            BigDecimal[] diaLOC = diaLocatario(atuData);
            BigDecimal sdaLOC = diaLOC[2].add(antLOC[2]);
            //System.out.println("LDCATARIOS............. CR: " + diaLOC[0].doubleValue() + " DB: " + diaLOC[1].doubleValue() + " SD: " + diaLOC[2].doubleValue() + " SD.ANT: " + antLOC[2].doubleValue() + " SD.ATU: " + sdaLOC.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"LOCATARIOS", diaLOC[0].doubleValue(), diaLOC[1].doubleValue(), diaLOC[2].doubleValue(), antLOC[2].doubleValue(), sdaLOC.doubleValue()});
            // - FIM Locatarios

            // Bloqueados
            BigDecimal[] antBLO = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antBLO = antBloqueados(iniData, fimData);
            }

            BigDecimal[] diaBLO = diaBloqueados(atuData);
            BigDecimal sdaBLO = diaBLO[2].add(antBLO[2]);
            //System.out.println("BLOQUEADOS............. CR: " + diaBLO[0].doubleValue() + " DB: " + diaBLO[1].doubleValue() + " SD: " + diaBLO[2].doubleValue() + " SD.ANT: " + antBLO[2].doubleValue() + " SD.ATU: " + sdaBLO.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"BLOQUEADOS", diaBLO[0].doubleValue(), diaBLO[1].doubleValue(), diaBLO[2].doubleValue(), antBLO[2].doubleValue(), sdaBLO.doubleValue()});
            // - FIM Bloqueados

            // Controle
            BigDecimal[] antCTR = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antCTR = antControle(iniData, fimData);
            }

            BigDecimal[] diaCTR = diaControle(atuData);
            BigDecimal sdaCTR = diaCTR[2].add(antCTR[2]);
            //System.out.println("CONTROLE............... CR: " + diaCTR[0].doubleValue() + " DB: " + diaCTR[1].doubleValue() + " SD: " + diaCTR[2].doubleValue() + " SD.ANT: " + antCTR[2].doubleValue() + " SD.ATU: " + sdaCTR.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CONTROLE", diaCTR[0].doubleValue(), diaCTR[1].doubleValue(), diaCTR[2].doubleValue(), antCTR[2].doubleValue(), sdaCTR.doubleValue()});
            // - FIM Controle

            // Retencao
            BigDecimal antRT = new BigDecimal("0");
            if (iniData != null && fimData != null) {
                antRT = antRetencao(iniData, fimData);
            }
            BigDecimal diaRT = diaRetencao(atuData);
            BigDecimal sdaRT = diaRT.add(antRT);
            //System.out.println("RT..................... CR: " + diaRT.doubleValue() + " DB: 0.00 SD: " + diaRT.doubleValue() + " SD.ANT: " + antRT.doubleValue() + " SD.ATU: " + sdaRT.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"RETENÇÃO", diaRT.doubleValue(), 0d, diaRT.doubleValue(), antRT.doubleValue(), sdaRT.doubleValue()});
            // - FIM Retencao

            // Totaliza PRO+ADI+LOCA+BLOQ+CTRL+RT
            BigDecimal col1 = diaEXT[0].add(diaADI[0]).add(diaLOC[0]).add(diaBLO[0]).add(diaCTR[0]).add(diaRT);
            BigDecimal col2 = diaEXT[1].add(diaADI[1]).add(diaLOC[1]).add(diaBLO[1]).add(diaCTR[1]);
            BigDecimal col3 = diaEXT[2].add(diaADI[2]).add(diaLOC[2]).add(diaBLO[2]).add(diaCTR[2]).add(diaRT);
            BigDecimal col4 = antEXT[2].add(antADI[2]).add(antLOC[2]).add(antBLO[2]).add(antCTR[2]).add(antRT);
            BigDecimal col5 = sdaEXT.add(sdaADI).add(sdaLOC).add(sdaBLO).add(sdaCTR).add(sdaRT);
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"", "=========", "=========", "=========", "=========", "========="});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"TOTAL", col1.doubleValue(), col2.doubleValue(), col3.doubleValue(), col4.doubleValue(), col5.doubleValue()});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
            //

            // Socios
            BigDecimal[] antSOC = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antSOC = antSocios(iniData, fimData);
            }

            BigDecimal[] diaSOC = diaSocios(atuData);
            BigDecimal sdaSOC = diaSOC[2].add(antSOC[2]);
            //System.out.println("SOCIOS................. CR: " + diaSOC[0].doubleValue() + " DB: " + diaSOC[1].doubleValue() + " SD: " + diaSOC[2].doubleValue() + " SD.ANT: " + antSOC[2].doubleValue() + " SD.ATU: " + sdaSOC.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"SOCIOS", diaSOC[0].doubleValue(), diaSOC[1].doubleValue(), diaSOC[2].doubleValue(), antSOC[2].doubleValue(), sdaSOC.doubleValue()});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
            // - FIM Socios

            // Deposito
            BigDecimal[] antDEP = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antDEP = antDeposito(iniData, fimData);
            }

            BigDecimal[] diaDEP = diaDeposito(atuData);
            BigDecimal sdaDEP = diaDEP[2].add(antDEP[2]);
            //System.out.println("DEPOSITO............... CR: " + diaDEP[0].doubleValue() + " DB: " + diaDEP[1].doubleValue() + " SD: " + diaDEP[2].doubleValue() + " SD.ANT: " + antDEP[2].doubleValue() + " SD.ATU: " + sdaDEP.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"DEPOSITO", diaDEP[0].doubleValue(), diaDEP[1].doubleValue(), diaDEP[2].doubleValue(), antDEP[2].doubleValue(), sdaDEP.doubleValue()});
            // - FIM Deposito

            // Despesas
            BigDecimal antDS = new BigDecimal("0");
            if (iniData != null && fimData != null) {
                antDS = antDespesas(iniData, fimData, null);
            }
            BigDecimal diaDS = diaDespesas(atuData, null);
            BigDecimal sddDS = new BigDecimal("0").subtract(diaDS);
            BigDecimal sdaDS = new BigDecimal("0").subtract(antDS);
            BigDecimal sdfDS = sddDS.add(sdaDS);
            //System.out.println("DESPESAS............... CR: " + diaDS.doubleValue() + " DB: 0.00 SD: " + diaDS.doubleValue() + " SD.ANT: " + antDS.doubleValue() + " SD.ATU: " + sdaDS.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"DESPESAS", 0d, diaDS.doubleValue(), sddDS.doubleValue(), sdaDS.doubleValue(), sdfDS.doubleValue()});
            // - FIM Despesas

            // Caixa
            BigDecimal[] antCXA = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antCXA = antCaixa(iniData, fimData);
            }

            BigDecimal[] diaCXA = diaCaixa(atuData);
            BigDecimal sdaCXA = diaCXA[2].add(antCXA[2]);
            //System.out.println("CAIXA.................. CR: " + diaCXA[0].doubleValue() + " DB: " + diaCXA[1].doubleValue() + " SD: " + diaCXA[2].doubleValue() + " SD.ANT: " + antCXA[2].doubleValue() + " SD.ATU: " + sdaCXA.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CAIXA", diaCXA[0].doubleValue(), diaCXA[1].doubleValue(), diaCXA[2].doubleValue(), antCXA[2].doubleValue(), sdaCXA.doubleValue()});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
            // - FIM Caixa

            // Adm
            BigDecimal[] antADM = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            if (iniData != null && fimData != null) {
                antADM = antAdmin(iniData, fimData, null);
            }

            BigDecimal[] diaADM = diaAdmin(atuData, null);
            BigDecimal sdaCM = diaADM[0].add(antADM[0]);
            BigDecimal sdaMU = diaADM[1].add(antADM[1]);
            BigDecimal sdaJU = diaADM[2].add(antADM[2]);
            BigDecimal sdaCO = diaADM[3].add(antADM[3]);
            BigDecimal sdaEP = diaADM[4].add(antADM[4]);
            BigDecimal sdaSG = diaADM[5].add(antADM[5]);
            //System.out.println("CM..................... CR: " + diaADM[0].doubleValue() + " DB: 0.00 SD: " + diaADM[0].doubleValue() + " SD.ANT: " + antADM[0].doubleValue() + " SD.ATU: " + sdaCM.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CM", diaADM[0].doubleValue(), 0d, diaADM[0].doubleValue(), antADM[0].doubleValue(), sdaCM.doubleValue()});

            //System.out.println("MU..................... CR: " + diaADM[1].doubleValue() + " DB: 0.00 SD: " + diaADM[1].doubleValue() + " SD.ANT: " + antADM[1].doubleValue() + " SD.ATU: " + sdaMU.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"MU", diaADM[1].doubleValue(), 0d, diaADM[1].doubleValue(), antADM[1].doubleValue(), sdaMU.doubleValue()});

            //System.out.println("JU..................... CR: " + diaADM[2].doubleValue() + " DB: 0.00 SD: " + diaADM[2].doubleValue() + " SD.ANT: " + antADM[2].doubleValue() + " SD.ATU: " + sdaJU.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"JU", diaADM[2].doubleValue(), 0d, diaADM[2].doubleValue(), antADM[2].doubleValue(), sdaJU.doubleValue()});

            //System.out.println("CO..................... CR: " + diaADM[3].doubleValue() + " DB: 0.00 SD: " + diaADM[3].doubleValue() + " SD.ANT: " + antADM[3].doubleValue() + " SD.ATU: " + sdaCO.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CO", diaADM[3].doubleValue(), 0d, diaADM[3].doubleValue(), antADM[3].doubleValue(), sdaCO.doubleValue()});

            //System.out.println("EP..................... CR: " + diaADM[4].doubleValue() + " DB: 0.00 SD: " + diaADM[4].doubleValue() + " SD.ANT: " + antADM[4].doubleValue() + " SD.ATU: " + sdaEP.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"EP", diaADM[4].doubleValue(), 0d, diaADM[4].doubleValue(), antADM[4].doubleValue(), sdaEP.doubleValue()});

            //System.out.println("SG..................... CR: " + diaADM[5].doubleValue() + " DB: 0.00 SD: " + diaADM[5].doubleValue() + " SD.ANT: " + antADM[5].doubleValue() + " SD.ATU: " + sdaSG.doubleValue());
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"SG", diaADM[5].doubleValue(), 0d, diaADM[5].doubleValue(), antADM[5].doubleValue(), sdaSG.doubleValue()});
            // - FIM Adm

            // Contas ADM
            AvisoADMContas[] antATC = null;
            if (iniData != null && fimData != null) {
                antATC = antAvisoADMContas(0, iniData, fimData, null);
            }
            AvisoADMContas[] diaATC = diaAvisoADMContas(0, atuData, null);
            double tdcr = 0;double tddb = 0;double tdsd = 0;double tacr = 0;
            double tadb = 0;double tasd = 0;double tstu = 0;
            if (antATC != null) {
                if (antATC.length > 0) {
                    for (AvisoADMContas ACT : antATC) {
                        int pos = ClassOf(diaATC, ACT.getRegistro());
                        double dcr = 0;
                        double ddb = 0;
                        double dsd = 0;
                        double acr = 0;
                        double adb = 0;
                        double asd = 0;
                        double stu = 0;
                        if (pos != -1) {
                            dcr = diaATC[pos].getCredito().doubleValue();
                            ddb = diaATC[pos].getDebito().doubleValue();
                            dsd = dcr - ddb;
                        }
                        acr = ACT.getCredito().doubleValue();
                        adb = ACT.getDebito().doubleValue();
                        asd = acr - adb;

                        stu = dsd + asd;

                        String nmCTA = null;
                        try {
                            nmCTA = conn.LerCamposTabela(new String[]{"descricao"}, "adm", "codigo = ?", new Object[][]{{"string", ACT.getRegistro()}})[0][3].toString();
                        } catch (Exception e) {
                        }
                        //System.out.println(nmCTA + ".................... CR: " + dcr + " DB: " + ddb + " SD: " + dsd + " SD.ANT: " + asd + " SD.ATU: " + stu);
                        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[]{nmCTA, dcr, ddb, dsd, asd, stu});
                        tdcr += dcr;
                        tddb += ddb;
                        tdsd += dsd;
                        tasd += asd;
                        tstu += stu;
                    }
                }
            } else {
                for (AvisoADMContas ACT : diaATC) {
                    double dcr = 0;double ddb = 0;double dsd = 0;double acr = 0;
                    double adb = 0;double asd = 0;double stu = 0;

                    dcr = ACT.getCredito().doubleValue();
                    ddb = ACT.getDebito().doubleValue();
                    dsd = dcr - ddb;

                    stu = dsd + asd;

                    String nmCTA = null;
                    try { nmCTA = conn.LerCamposTabela(new String[]{"descricao"}, "adm", "codigo = ?", new Object[][]{{"string", ACT.getRegistro()}})[0][3].toString(); } catch (Exception e) { }
                    //System.out.println(nmCTA + ".................... CR: " + dcr + " DB: " + ddb + " SD: " + dsd + " SD.ANT: " + asd + " SD.ATU: " + stu);
                    dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {nmCTA, dcr, ddb, dsd, asd, stu});
                    tdcr += dcr; tddb += ddb; tdsd += dsd; tasd += asd; tstu += stu;
                }
            }
            // - FIM Contas Adm

            BigDecimal coll1 = diaADM[0].add(diaADM[1]).add(diaADM[2]).add(diaADM[3]).add(diaADM[4]).add(diaADM[5]).add(BigDecimal.valueOf(tdcr));
            BigDecimal coll2 = new BigDecimal(tddb);
            BigDecimal coll3 = diaADM[0].add(diaADM[1]).add(diaADM[2]).add(diaADM[3]).add(diaADM[4]).add(diaADM[5]).add(BigDecimal.valueOf(tdsd));
            BigDecimal coll4 = antADM[0].add(antADM[1]).add(antADM[2]).add(antADM[3]).add(antADM[4]).add(antADM[5]).add(BigDecimal.valueOf(tasd));
            BigDecimal coll5 = sdaCM.add(sdaMU).add(sdaJU).add(sdaCO).add(sdaEP).add(sdaSG).add(BigDecimal.valueOf(tstu));

            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"", "=========", "=========", "=========", "=========", "========="});
            dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"TOTAL", coll1.doubleValue(), coll2.doubleValue(), coll3.doubleValue(), coll4.doubleValue(), coll5.doubleValue()});

            {
                // Divisao entre S�cios
                BigDecimal divSocios = coll5.subtract(sdfDS.multiply(new BigDecimal("-1")));

                if (divSocios.doubleValue() != 0) {
                    //dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[]{" ", " ", " ", " ", " ", " "});
                    //dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[]{" ", " ", " ", " ", " ", " "});
                    //dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[]{"DIVIDIR", " ", " ", " ", " ", divSocios.doubleValue()});

                    // Pegar o dia inicial do fechamento
                    String fDia = RetDia();
                    //

                    Fecha_antDespesas(atuData, iniData, fimData);
                    Fecha_diaDespesas(atuData);

                    Fecha_antAdmin(atuData, iniData, fimData);
                    Fecha_diaAdmin(atuData);

                    Fecha_antAvisoADMContas(0,atuData, iniData, fimData);
                    Fecha_diaAvisoADMContas(0, atuData);

                    String socSQL = "SELECT id, perc FROM adm_socios ORDER BY id;";
                    ResultSet socRs = conn.AbrirTabela(socSQL,ResultSet.CONCUR_READ_ONLY);
                    try {
                        while (socRs.next()) {
                            if (divSocios.doubleValue() > 0) {
                                GravaAviSocio(socRs.getString("id"), "CRE", "Lucro do dia [" + fDia + " ate " + Dates.DateFormata("dd/MM/yyyy", Dates.toDate(dpkData.getValue())) + "] - ", divSocios.multiply(socRs.getBigDecimal("perc").divide(new BigDecimal("100"))));
                            } else if (divSocios.doubleValue() < 0) {
                                GravaAviSocio(socRs.getString("id"), "DEB", "Prejuizo do dia [" + fDia + " ate " + Dates.DateFormata("dd/MM/yyyy", Dates.toDate(dpkData.getValue())) + "] - ", divSocios.multiply(new BigDecimal("-1")).multiply(socRs.getBigDecimal("perc").divide(new BigDecimal("100"))));
                            }
                        }
                    } catch (SQLException e) {}
                }
            }

            Collections dadm = VariaveisGlobais.getAdmDados();
            new Impressao(VariaveisGlobais.usuario).ImprimeRazaoPDF(dadm, dadosImpr, true);
        });
    }

    private String RetDia() {
        Object[] aDia = {};
        String fDia = "01";
        String sql = "select razao from avisos WHERE not razao is null and extract(MONTH FROM razao) = ? and extract(YEAR FROM razao) = ? UNION SELECT razao FROM despesas where not razao is null and extract(MONTH FROM razao) = ? and extract(YEAR FROM razao) = ? UNION SELECT razao FROM movimento where not razao is null and extract(MONTH FROM razao) = ? and extract(YEAR FROM razao) = ?;";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {
                {"int", dpkData.getValue().getMonthValue()},
                {"int", dpkData.getValue().getYear()},
                {"int", dpkData.getValue().getMonthValue()},
                {"int", dpkData.getValue().getYear()},
                {"int", dpkData.getValue().getMonthValue()},
                {"int", dpkData.getValue().getYear()}
        });
        try{
            while (rs.next()) {
                aDia = FuncoesGlobais.ObjectsAdd(aDia, Dates.DateFormata("dd", Dates.DateAdd(Dates.DIA, 1, rs.getDate("razao"))));
            }
        } catch (SQLException e) { aDia = new String[] {}; }
        if (aDia.length == 0) {
            fDia = "01";
        } else {
            aDia = FuncoesGlobais.OrdenaMatriz(aDia);
            fDia = (String)aDia[aDia.length - 1];
        }
        return fDia;
    }

    private void FillRazaoFechadosNoMes() {
        List<cRazao> data = new ArrayList<cRazao>();
        String Sql = "select razao from avisos WHERE not razao is null and extract(YEAR FROM razao) = ? UNION SELECT razao FROM despesas where not razao is null and extract(YEAR FROM razao) = ? UNION SELECT razao FROM movimento where not razao is null and extract(YEAR FROM razao) = ?;";
        ResultSet rs = null;
        try {
            rs = conn.AbrirTabela(Sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", razAno.getValue()}, {"int", razAno.getValue()}, {"int", razAno.getValue()}});
            Date gVencto = null;

            while (rs.next()) {
                try {gVencto = rs.getDate("razao");} catch (SQLException sqlex) {}

                data.add(new cRazao(gVencto));
            }
        } catch (Exception e) {}
        try { DbMain.FecharTabela(rs); } catch (Exception e) {}

        razLista_data.setCellValueFactory(new PropertyValueFactory<>("data"));
        razLista_data.setCellFactory((AbstractConvertCellFactory<cRazao, Date>) value -> DateTimeFormatter.ofPattern("dd/MM/yyyy").format(Dates.toLocalDate(value)));
        razLista_data.setStyle( "-fx-alignment: CENTER;");

        razLista.getItems().clear();
        if (!data.isEmpty()) {
            data.sort(new Comparator<cRazao>() {
                @Override
                public int compare(cRazao o1, cRazao o2) {
                    return o1.getData().compareTo(o2.getData());
                }
            });
            razLista.setItems(FXCollections.observableArrayList(data));
        }
/*
        razLista.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ListaRazao(newSelection.getData());
            }
        });
*/
    }

    private interface AbstractConvertCellFactory<E, T> extends Callback<TableColumn<E, T>, TableCell<E, T>> {
        @Override
        default TableCell<E, T> call(TableColumn<E, T> param) {
            return new TableCell<E, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(convert(item));
                    }
                }
            };
        }

        String convert(T value);
    }

    private void GravaAviSocio(String registro, String tipo, String txaTexto, BigDecimal valor) {
        BigInteger aut = conn.PegarAutenticacao();
        String caixaSQL = "INSERT INTO avisos (conta, registro, tipo, texto, valor, dtrecebimento, aut_rec, usr_rec, aut_pag) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement stm = conn.conn.prepareStatement(caixaSQL);
            stm.setInt(1, 3);
            stm.setString(2, registro);
            stm.setString(3, tipo);
            stm.setString(4, txaTexto);
            stm.setBigDecimal(5, valor);
            stm.setDate(6, new java.sql.Date(DbMain.getDateTimeServer().getTime()));
            stm.setInt(7, aut.intValue());
            stm.setString(8, VariaveisGlobais.usuario);
            stm.setArray(9, conn.conn.createArrayOf("text" +
                    "", new Object[][]{{registro, null, null, ""}}));
            stm.executeUpdate();
            stm.close();
        } catch (SQLException e) {}
    }

    private BigDecimal[] antExtrato(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM propsaldo Where aut_pag is not null AND not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?);";
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select * from movimento where aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                ttCR = ttCR.add(alu);

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? AND ?) order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {}
                    try {
                        DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where aut_rec <> 0 and not aut_pag is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    ttCR = ttCR.add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    ttCR = ttCR.add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not aut_pag is not null and aut_pag[1][2] is null and conta = 1 and (aut_pag[1][3]::date BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaExtrato(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // Saldo Anterior
        String saSql = "SELECT registro, valor, aut_pag FROM propsaldo Where aut_pag is not null AND not aut_pag[1][2] is null and aut_pag[1][3]::date = ?;";
        ResultSet sars = conn.AbrirTabela(saSql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (sars.next()) {
                ttCR = ttCR.add(sars.getBigDecimal("valor"));
            }
        } catch (Exception e) {}
        try { sars.close(); } catch (Exception e) {}

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select * from movimento where aut_rec <> 0 and not aut_pag[1][2] is null and aut_pag[1][3]::date = ? order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                ttCR = ttCR.add(alu);

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where aut_rec <> 0 and not aut_pag[1][2] is null and aut_pag[1][3]::date = ? order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {}
                    try {
                        DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where aut_rec <> 0 and not aut_pag is null and aut_pag[1][3]::date = ? order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where aut_rec <> 0 and not aut_pag[1][2] is null and aut_pag[1][3]::date = ? order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    ttCR = ttCR.add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    ttCR = ttCR.add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not aut_pag is not null and aut_pag[1][2] is null and conta = 1 and aut_pag[1][3]::date = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] antLocatario(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is not null and conta = 2 and (dtrecebimento BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaLocatario(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is not null and conta = 2 and dtrecebimento = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] antSocios(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is not null and conta = 3 and (dtrecebimento BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaSocios(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is not null and conta = 3 and dtrecebimento = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] antControle(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is null and conta = 4 and (dtrecebimento BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaControle(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and not dtrecebimento is null and conta = 4 and dtrecebimento = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] antAdmin(Date iniData, Date fimData, Date razao) {
        BigDecimal ttCR_MU = new BigDecimal("0");
        BigDecimal ttCR_JU = new BigDecimal("0");
        BigDecimal ttCR_CO = new BigDecimal("0");
        BigDecimal ttCR_EP = new BigDecimal("0");
        BigDecimal ttCR_CM = new BigDecimal("0");
        BigDecimal ttCR_SG = new BigDecimal("0");

        // Aqui pega os recibos recebidos e n�o pagos
        String sql = ""; String sRazao = "";
        if (razao == null) {
            sRazao = "(razao is null) and ";
        } else {
            sRazao = "razao = '" + Dates.DateFormata("yyyy-MM-dd", razao) + "' and ";
        }
        sql = "select * from movimento where " + sRazao + "aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Comissão
                ttCR_CM = ttCR_CM.add(com);

                // Seguros
                {
                    String sgsql = "select * from seguros where aut_rec <> 0 and not aut_pag is null and (aut_pag[1][3]::date BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR_SG = ttCR_SG.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR_MU = ttCR_MU.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR_JU = ttCR_JU.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    ttCR_JU = ttCR_CO.add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    ttCR_EP = ttCR_EP.add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return new BigDecimal[] {ttCR_CM, ttCR_MU, ttCR_JU, ttCR_CO, ttCR_EP, ttCR_SG};
    }

    private void Fecha_antAdmin(Date atuDate, Date iniData, Date fimData) {
        // Aqui pega os recibos recebidos e n�o pagos
        String sql = "";
        sql = "UPDATE movimento SET razao = ? where (bloqueio is null or bloqueio = '') and (razao is null) and aut_rec <> 0 and not aut_pag[1][2] is null and (aut_pag[1][3]::date BETWEEN ? and ?);";
        conn.ExecutarComando(sql,new Object[][] {{"date",Dates.toSqlDate(atuDate)}, {"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
    }

    private BigDecimal[] diaAdmin(Date atuData, Date razao) {
        BigDecimal ttCR_MU = new BigDecimal("0");
        BigDecimal ttCR_JU = new BigDecimal("0");
        BigDecimal ttCR_CO = new BigDecimal("0");
        BigDecimal ttCR_EP = new BigDecimal("0");
        BigDecimal ttCR_CM = new BigDecimal("0");
        BigDecimal ttCR_SG = new BigDecimal("0");

        // Aqui pega os recibos recebidos e não pagos
        String sql = ""; String sRazao = "";
        if (razao == null) {
            sRazao = "(razao is null) and ";
        } else {
            sRazao = "razao = '" + Dates.DateFormata("yyyy-MM-dd", razao) + "' and ";
        }

        sql = "select * from movimento where " + sRazao + "aut_rec <> 0 and not aut_pag[1][2] is null and aut_pag[1][3]::date = ? order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Comiss�o
                ttCR_CM = ttCR_CM.add(com);

                // Seguros
                {
                    String sgsql = "select * from seguros where aut_rec <> 0 and not aut_pag is null and aut_pag[1][3]::date = ? order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR_SG = ttCR_SG.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR_MU = ttCR_MU.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR_JU = ttCR_JU.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    ttCR_CO = ttCR_CO.add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    ttCR_EP = ttCR_EP.add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return new BigDecimal[] {ttCR_CM, ttCR_MU, ttCR_JU, ttCR_CO, ttCR_EP, ttCR_SG};
    }

    private void Fecha_diaAdmin(Date atuData) {
        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "UPDATE movimento SET razao = ? where (bloqueio is null or bloqueio = '') and (razao is null) and aut_rec <> 0 and not aut_pag[1][2] is null and aut_pag[1][3]::date = ?;";
        conn.ExecutarComando(sql,new Object[][] {{"date", Dates.toSqlDate(atuData)}, {"date",Dates.toSqlDate(atuData)}});
    }

    private BigDecimal antRetencao(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");

        // Taxas
        String qSQL = "SELECT t.id, t.rgprp, t.rgimv, t.contrato, (SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro " +
                "FROM imoveis i WHERE i.i_rgimv = t.rgimv) AS ender, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campo, " +
                "t.cota, t.valor, t.dtvencimento, t.referencia, t.dtrecebimento FROM taxas t WHERE t.tipo = 'D' AND t.retencao = True AND " +
                "(t.dtrecebimento BETWEEN ? AND ?) AND not aut_ret is null;";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                ttCR = ttCR.add(rs.getBigDecimal("valor"));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // Seguros
        qSQL = "SELECT s.id, s.rgprp, s.rgimv, s.contrato, s.cota, s.valor, s.dtvencimento, s.referencia, \n" +
                "(SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro FROM imoveis i WHERE i.i_rgimv = s.rgimv) AS ender," +
                "       s.extrato, s.apolice, s.dtseguro, s.aut_seg, s.usr_seg, s.dtrecebimento, \n" +
                "       s.aut_rec, s.usr_rec, s.banco, s.nnumero, s.bloqueio, s.dtbloqueio, s.usr_bloqueio, \n" +
                "       s.dtlanc, s.usr_lanc, s.selected, s.aut_pag, s.retencao, s.dtrecebimento, s.reserva, 'SEGURO' campo\n" +
                "  FROM seguros s WHERE s.retencao = true and (s.dtrecebimento BETWEEN ? and ?) and not aut_ret is null";
        rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                ttCR = ttCR.add(rs.getBigDecimal("valor"));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return ttCR;
    }

    private BigDecimal diaRetencao(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");

        // Taxas
        String qSQL = "SELECT t.id, t.rgprp, t.rgimv, t.contrato, (SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro " +
                "FROM imoveis i WHERE i.i_rgimv = t.rgimv) AS ender, (SELECT c.descricao FROM campos c WHERE c.codigo = t.campo) AS campo, " +
                "t.cota, t.valor, t.dtvencimento, t.referencia, t.dtrecebimento FROM taxas t WHERE t.tipo = 'D' AND t.retencao = True AND " +
                "t.dtrecebimento = ? AND not aut_ret is null;";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                ttCR = ttCR.add(rs.getBigDecimal("valor"));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // Seguros
        qSQL = "SELECT s.id, s.rgprp, s.rgimv, s.contrato, s.cota, s.valor, s.dtvencimento, s.referencia, \n" +
                "(SELECT i.i_end || ', ' || i.i_num || ' ' || i.i_cplto || ' - ' || i.i_bairro FROM imoveis i WHERE i.i_rgimv = s.rgimv) AS ender," +
                "       s.extrato, s.apolice, s.dtseguro, s.aut_seg, s.usr_seg, s.dtrecebimento, \n" +
                "       s.aut_rec, s.usr_rec, s.banco, s.nnumero, s.bloqueio, s.dtbloqueio, s.usr_bloqueio, \n" +
                "       s.dtlanc, s.usr_lanc, s.selected, s.aut_pag, s.retencao, s.dtrecebimento, s.reserva, 'SEGURO' campo\n" +
                "  FROM seguros s WHERE s.retencao = true and s.dtrecebimento = ? and not aut_ret is null";
        rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                ttCR = ttCR.add(rs.getBigDecimal("valor"));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return ttCR;
    }

    private BigDecimal antDespesas(Date iniData, Date fimData, Date razao) {
        BigDecimal ttDB = new BigDecimal("0");

        String sRazao = "";
        if (razao == null) {
            sRazao = "(razao is null) and ";
        } else {
            sRazao = "razao = '" + Dates.DateFormata("yyyy-MM-dd", razao) + "' and ";
        }
        String qSQL = "SELECT valor FROM despesas WHERE " + sRazao + "not aut is null and (dtpagto BETWEEN ? and ?)";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                ttDB = ttDB.add(rs.getBigDecimal("valor"));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return ttDB;
    }

    private void Fecha_antDespesas(Date atuDate,Date iniData, Date fimData) {
        String qSQL = "UPDATE despesas SET razao = ? WHERE not aut is null and (dtpagto BETWEEN ? and ?) and razao is null";
        conn.ExecutarComando(qSQL,new Object[][] {{"date", Dates.toSqlDate(atuDate)}, {"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
    }

    private BigDecimal diaDespesas(Date atuData, Date razao) {
        BigDecimal ttDB = new BigDecimal("0");

        String sRazao = "";
        if (razao == null) {
            sRazao = "(razao is null) and ";
        } else {
            sRazao = "razao = '" + Dates.DateFormata("yyyy-MM-dd", razao) + "' and ";
        }
        String qSQL = "SELECT valor FROM despesas WHERE " + sRazao + "not aut is null and dtpagto = ?";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                ttDB = ttDB.add(rs.getBigDecimal("valor"));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return ttDB;
    }

    private void Fecha_diaDespesas(Date atuData) {
        String qSQL = "UPDATE despesas SET razao = ? WHERE not aut is null and dtpagto = ? and razao is null";
        conn.ExecutarComando(qSQL,new Object[][] {{"date", Dates.toSqlDate(atuData)}, {"date",Dates.toSqlDate(atuData)}});
    }

    private BigDecimal[] antAdiantamentos(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        String movimentoSQL = "select rgprp, rgimv, contrato, cota, mensal, dtvencimento, cm, referencia, ad_pag[1][3] as dtrecebimento, ad_pag[1][2] as aut_rec from movimento where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try {
                    com = rs.getBigDecimal("cm");
                } catch (Exception ex) {
                    com = new BigDecimal("0");
                }
                ttCR = ttCR.add(alu);

                // Desconto/Diferen�a
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "";
                    dfsql = "select * from descdif where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferen�a de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comiss�o
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "";
                    sgsql = "select * from seguros where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor"); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar reten��o
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "";
                    txsql = "select * from taxas where not ad_pag is null and (ad_pag[1][3]::date BETWEEN ? and ?)";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }
            }
        } catch (SQLException sqlex) {
            System.out.println(sqlex);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and conta = 6 and (dtrecebimento BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaAdiantamentos(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        String movimentoSQL = "select rgprp, rgimv, contrato, cota, mensal, dtvencimento, cm, referencia, ad_pag[1][3] as dtrecebimento, ad_pag[1][2] as aut_rec from movimento where not ad_pag is null and (ad_pag[1][3]::date = ?)";
        ResultSet rs = conn.AbrirTabela(movimentoSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                BigDecimal alu = new BigDecimal("0");
                BigDecimal palu = new BigDecimal("0");
                BigDecimal com = new BigDecimal("0");
                alu = rs.getBigDecimal("mensal");
                try {
                    com = rs.getBigDecimal("cm");
                } catch (Exception ex) {
                    com = new BigDecimal("0");
                }
                ttCR = ttCR.add(alu);

                // Desconto/Diferen�a
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where not ad_pag is null and ad_pag[1][3] = ?";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferen�a de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comiss�o
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where not ad_pag is null and ad_pag[1][3]::date = ?";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("0");
                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor"); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip"); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar reten��o
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where not ad_pag is null and ad_pag[1][3]::date = ?";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("0");
                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor"); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {
                        System.out.println(e);
                    }
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }
            }
        } catch (SQLException sqlex) {
            System.out.println(sqlex);
        }

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and conta = 6 and dtrecebimento = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] antBloqueados(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // Aqui pega os recibos recebidos e não pagos
        String sql = "";
        sql = "select * from movimento where aut_rec <> 0 and (dtbloqueio BETWEEN ? and ?) order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (!bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                ttCR = ttCR.add(alu);

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where aut_rec <> 0 and (dtbloqueio BETWEEN ? AND ?) order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {}
                    try {
                        DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comissão
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where aut_rec <> 0 and (dtbloqueio BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where aut_rec <> 0 and (dtbloqueio BETWEEN ? and ?) order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    ttCR = ttCR.add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    ttCR = ttCR.add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and conta = 1 and (dtbloqueio BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (!bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaBloqueados(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // Aqui pega os recibos recebidos e n�o pagos
        String sql = "";
        sql = "select * from movimento where aut_rec <> 0 and dtbloqueio = ? order by 2,3,4,7,9;";
        ResultSet rs = conn.AbrirTabela(sql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                boolean bloq = (rs.getString("bloqueio") != null);

                if (!bloq) {
                    continue;
                }

                BigDecimal palu = new BigDecimal("100");
                BigDecimal alu = rs.getBigDecimal("mensal").multiply(palu.divide(new BigDecimal("100")));
                BigDecimal com = new BigDecimal("0");
                try { com = rs.getBigDecimal("cm").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                ttCR = ttCR.add(alu);

                // Parametros de multas
                new Multas(rs.getString("rgprp"), rs.getString("rgimv"));

                // Desconto/Diferença
                BigDecimal dfCR = new BigDecimal("0");
                BigDecimal dfDB = new BigDecimal("0");
                {
                    String dfsql = "select * from descdif where aut_rec <> 0 and dtbloqueio = ? order by 2,3,4,7,9;";
                    ResultSet dfrs = conn.AbrirTabela(dfsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (dfrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(dfrs.getString("referencia"))) {
                                continue;
                            }
                            String dftipo = dfrs.getString("tipo");
                            String dftipostr = dftipo.trim().equalsIgnoreCase("D") ? "Desconto de " : "Diferença de ";

                            BigDecimal dfcom = new BigDecimal("0");
                            try { dfcom = dfrs.getBigDecimal("valor").multiply(palu.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            if (dftipo.trim().equalsIgnoreCase("C")) {
                                dfCR = dfCR.add(dfcom);
                            } else {
                                dfDB = dfDB.add(dfcom);
                            }

                            ttCR = ttCR.add(dftipo.trim().equalsIgnoreCase("C") ? dfCR : new BigDecimal("0"));
                            ttDB = ttDB.add(dftipo.trim().equalsIgnoreCase("D") ? dfDB : new BigDecimal("0"));
                        }
                    } catch (SQLException e) {}
                    try {
                        DbMain.FecharTabela(dfrs);} catch (Exception ex) {}
                }

                // Comiss�o
                ttDB = ttDB.add(com);

                // IR
                BigDecimal pir = new BigDecimal("0");
                BigDecimal pirvr = new BigDecimal("0");
                {
                    try { pirvr = rs.getBigDecimal("ir").multiply(pir.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                    if (pirvr.doubleValue() != 0) {
                        ttDB = ttDB.add(pirvr);
                    }
                }

                // Seguros
                {
                    String sgsql = "select * from seguros where aut_rec <> 0 and dtbloqueio = ? order by 2,3,4,7,9;";
                    ResultSet sgrs = conn.AbrirTabela(sgsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (sgrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(sgrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal psg = new BigDecimal("100");

                            BigDecimal seg = new BigDecimal("0");
                            try { seg = sgrs.getBigDecimal("valor").multiply(psg.divide(new BigDecimal("100"))); } catch (Exception ex ){ seg = new BigDecimal("0"); }

                            ttCR = ttCR.add(seg);
                            if (sgrs.getBoolean("retencao")) ttDB = ttDB.add(seg);
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(sgrs);} catch (Exception ex) {}
                }

/*
                        // Iptu
                        BigDecimal pip = new BigDecimal("0");
                        BigDecimal pipvr = new BigDecimal("0");
                        {
                            try { pipvr = rs.getBigDecimal("ip").multiply(pip.divide(new BigDecimal("100"))); } catch (Exception ex ){ pirvr = new BigDecimal("0"); }

                            // Lembrar retenção
                            if (pipvr.doubleValue() != 0) {
                                ttDB = ttDB.adc(pipvr);
                                if (sgrs.getBoolean("retencao")) ttCR = ttCR.Add(pipvr);
                            }
                        }
*/

                // Taxas
                {
                    String txsql = "select * from taxas where aut_rec <> 0 and dtbloqueio = ? order by 2,3,4,7,9;";
                    ResultSet txrs = conn.AbrirTabela(txsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
                    try {
                        while (txrs.next()) {
                            if (!rs.getString("referencia").equalsIgnoreCase(txrs.getString("referencia"))) {
                                continue;
                            }

                            BigDecimal ptx = new BigDecimal("100");

                            BigDecimal txcom = new BigDecimal("0");
                            try { txcom = txrs.getBigDecimal("valor").multiply(ptx.divide(new BigDecimal("100"))); } catch (Exception ex ){ com = new BigDecimal("0"); }
                            String txtipo = txrs.getString("tipo").trim();
                            boolean txret = txrs.getBoolean("retencao");

                            ttCR = ttCR.add((txtipo.equalsIgnoreCase("C") ? txcom : txret ? txcom : new BigDecimal("0")));
                            ttDB = ttDB.add((txtipo.equalsIgnoreCase("D") ? txcom : txret ? txcom : new BigDecimal("0")));
                        }
                    } catch (SQLException e) {}
                    try {DbMain.FecharTabela(txrs);} catch (Exception ex) {}
                }

                // MULTA
                BigDecimal pmu = new BigDecimal("0");
                BigDecimal pmuvr = new BigDecimal("0");
                {
                    pmu = new BigDecimal("100");
                    try { pmuvr = rs.getBigDecimal("mu"); } catch (Exception ex ){ pmuvr = new BigDecimal("0"); }
                    try { pmuvr = pmuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_mu)).divide(new BigDecimal("100")))); } catch (Exception e) {pmuvr = new BigDecimal("0");}
                    try { pmuvr = pmuvr.multiply(pmu.divide(new BigDecimal("100"))); } catch (Exception e) {pmuvr = new BigDecimal("0");}

                    if (pmuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pmuvr);
                    }
                }

                // JUROS
                BigDecimal pju = new BigDecimal("0");
                BigDecimal pjuvr = new BigDecimal("0");
                {
                    pju = new BigDecimal("100");
                    try { pjuvr = rs.getBigDecimal("ju"); } catch (Exception ex ){ pjuvr = new BigDecimal("0"); }
                    try { pjuvr = pjuvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ju)).divide(new BigDecimal("100")))); } catch (Exception e) {pjuvr = new BigDecimal("0");}
                    try { pjuvr = pjuvr.multiply(pju.divide(new BigDecimal("100"))); } catch (Exception e) {pjuvr = new BigDecimal("0");}

                    if (pjuvr.doubleValue() != 0) {
                        ttCR = ttCR.add(pjuvr);
                    }
                }

                // CORRE��O
                BigDecimal pco = new BigDecimal("0");
                BigDecimal pcovr = new BigDecimal("0");
                pco = new BigDecimal("100");
                try { pcovr = rs.getBigDecimal("co"); } catch (Exception ex ){ pcovr = new BigDecimal("0"); }
                try { pcovr = pcovr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_co)).divide(new BigDecimal("100")))); } catch (Exception e) {pcovr = new BigDecimal("0");}
                try { pcovr = pcovr.multiply(pco.divide(new BigDecimal("100"))); } catch (Exception e) {pcovr = new BigDecimal("0");}

                if (pcovr.doubleValue() != 0) {
                    ttCR = ttCR.add(pcovr);
                }

                // EXPEDIENTE
                BigDecimal pep = new BigDecimal("0");
                BigDecimal pepvr = new BigDecimal("0");
                pep = new BigDecimal("100");
                try { pepvr = rs.getBigDecimal("ep"); } catch (Exception ex ){ pepvr = new BigDecimal("0"); }
                try { pepvr = pepvr.multiply(new BigDecimal("1").subtract(new BigDecimal(String.valueOf(VariaveisGlobais.pa_ep)).divide(new BigDecimal("100")))); } catch (Exception e) {pepvr = new BigDecimal("0");}
                try { pepvr = pepvr.multiply(pep.divide(new BigDecimal("100"))); } catch (Exception e) {pepvr = new BigDecimal("0");}

                if (pepvr.doubleValue() != 0) {
                    ttCR = ttCR.add(pepvr);
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        // AVISOS
        String avsql = "select registro, tipo, texto, valor, dtrecebimento, aut_rec, bloqueio from avisos where aut_rec <> 0 and conta = 1 and dtbloqueio = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                boolean bloq = (avrs.getString("bloqueio") != null);

                if (!bloq) {
                    continue;
                }

                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private AvisoADMContas[] ClassAdd(AvisoADMContas[] mArray, AvisoADMContas value) {
        AvisoADMContas[] temp = new AvisoADMContas[mArray.length + 1];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        temp[mArray.length] = value;
        return temp;
    }

    private int ClassOf(AvisoADMContas[] value, String search) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= value.length - 1; i++) {
            if (value[i].getRegistro().equalsIgnoreCase(search)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    private AvisoADMContas[] antAvisoADMContas(int conta, Date iniData, Date fimData, Date razao) {
        AvisoADMContas contas[] = {};

        String sRazao = "";
        if (razao == null) {
            sRazao = "(razao is null) and ";
        } else {
            sRazao = "razao = '" + Dates.DateFormata("yyyy-MM-dd", razao) + "' and ";
        }

        String qSQL = "select registro, CASE WHEN tipo = 'CRE' THEN SUM(valor) ELSE 0 END Credito, CASE WHEN tipo = 'CRE' THEN 0 ELSE SUM(valor) END Debito from avisos where " + sRazao + "bloqueio is null and conta = ? and dtrecebimento between ? and ? group by registro, tipo";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", conta},{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (rs.next()) {
                int pos = -1;
                pos = ClassOf(contas,rs.getString("registro"));
                if (pos == -1) {
                    contas = ClassAdd(contas,new AvisoADMContas(rs.getString("registro"), rs.getBigDecimal("credito"), rs.getBigDecimal("debito")));
                } else {
                    contas[pos].setCredito(contas[pos].getCredito().add(rs.getBigDecimal("credito")));
                    contas[pos].setDebito(contas[pos].getDebito().add(rs.getBigDecimal("debito")));
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return contas;
    }

    private void Fecha_antAvisoADMContas(int conta, Date atuDate, Date iniData, Date fimData) {
        String qSQL = "UPDATE avisos SET razao = ? where (razao is null) and bloqueio is null and conta = ? and dtrecebimento between ? and ?;";
        conn.ExecutarComando(qSQL, new Object[][] {{"date", Dates.toSqlDate(atuDate)}, {"int", conta},{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
    }

    private AvisoADMContas[] diaAvisoADMContas(int conta, Date atuData, Date razao) {
        AvisoADMContas contas[] = {};

        String sRazao = "";
        if (razao == null) {
            sRazao = "(razao is null) and ";
        } else {
            sRazao = "razao = '" + Dates.DateFormata("yyyy-MM-dd", razao) + "' and ";
        }
        String qSQL = "select registro, CASE WHEN tipo = 'CRE' THEN SUM(valor) ELSE 0 END Credito, CASE WHEN tipo = 'CRE' THEN 0 ELSE SUM(valor) END Debito from avisos where " + sRazao + "bloqueio is null and conta = ? and dtrecebimento = ? group by registro, tipo";
        ResultSet rs = conn.AbrirTabela(qSQL,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"int", conta},{"date",Dates.toSqlDate(atuData)}});
        try {
            while (rs.next()) {
                int pos = -1;
                pos = ClassOf(contas,rs.getString("registro"));
                if (pos == -1) {
                    contas = ClassAdd(contas,new AvisoADMContas(rs.getString("registro"), rs.getBigDecimal("credito"), rs.getBigDecimal("debito")));
                } else {
                    contas[pos].setCredito(contas[pos].getCredito().add(rs.getBigDecimal("credito")));
                    contas[pos].setDebito(contas[pos].getDebito().add(rs.getBigDecimal("debito")));
                }
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        try { rs.close(); } catch (Exception e) {}

        return contas;
    }

    private void Fecha_diaAvisoADMContas(int conta, Date atuData) {
        String qSQL = "UPDATE avisos SET razao = ? where (razao is null) and bloqueio is null and conta = ? and dtrecebimento = ?;";
        conn.ExecutarComando(qSQL, new Object[][] {{"date", Dates.toSqlDate(atuData)}, {"int", conta},{"date",Dates.toSqlDate(atuData)}});
    }

    private BigDecimal[] antDeposito(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // CAIXA
        String avsql = "select operacao tipo, valor::numeric(10,2) valor, datahora dtrecebimento from caixa where documento = 'DEP' and (datahora BETWEEN ? and ?) order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaDeposito(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // CAIXA
        String avsql = "select operacao tipo, valor::numeric(10,2) valor, datahora dtrecebimento from caixa where documento = 'DEP' and datahora = ? order by 1;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                BigDecimal lcr = null, ldb = null;
                lcr = avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : null;
                ldb = avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : null;

                ttCR = ttCR.add(avrs.getString("tipo").equalsIgnoreCase("CRE") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
                ttDB = ttDB.add(avrs.getString("tipo").equalsIgnoreCase("DEB") ? avrs.getBigDecimal("valor") : new BigDecimal("0"));
            }
        } catch (Exception e) {e.printStackTrace();}
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] diaCaixa(Date atuData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // CAIXA
        String avsql = "select id, aut, datahora dtrecebimento, logado, operacao, documento, rgprp, rgimv, contrato, " +
                       "valor::numeric(10,2) valor, lancamentos, lancamentos[s][1]::character varying(2) tipo, " +
                       "lancamentos[s][2]::numeric(19,2) valoroper, fechado from " +
                       "(SELECT *, generate_subscripts(lancamentos, 1) AS s from caixa) as foo where datahora::date = ? " +
                       "and aut > 0 and fechado = true order by upper(logado), datahora;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(atuData)}});
        try {
            while (avrs.next()) {
                BigDecimal valorDN = new BigDecimal("0");
                BigDecimal valorCH = new BigDecimal("0");
                BigDecimal valorBC = new BigDecimal("0");
                BigDecimal valorCT = new BigDecimal("0");
                if (avrs.getString("operacao").equalsIgnoreCase("CRE")) {
                    if (avrs.getString("tipo").equalsIgnoreCase("DN")) {
                         valorDN = avrs.getBigDecimal("valoroper").min(avrs.getBigDecimal("valor"));
                    } else if (avrs.getString("tipo").equalsIgnoreCase("CH")) {
                        valorCH = avrs.getBigDecimal("valoroper");
                    } else if (avrs.getString("tipo").equalsIgnoreCase("BC")) {
                        valorBC = avrs.getBigDecimal("valoroper");
                    } else valorCT = avrs.getBigDecimal("valoroper");
                    ttCR = ttCR.add(valorDN.add(valorCH));
                } else {
                    if (avrs.getString("tipo").equalsIgnoreCase("DN")) {
                        valorDN = avrs.getBigDecimal("valoroper").min(avrs.getBigDecimal("valor"));
                    } else if (avrs.getString("tipo").equalsIgnoreCase("CH")) {
                        valorCH = avrs.getBigDecimal("valoroper");
                    } else if (avrs.getString("tipo").equalsIgnoreCase("BC")) {
                        valorBC = avrs.getBigDecimal("valoroper");
                    } else valorCT = avrs.getBigDecimal("valoroper");
                    ttDB = ttDB.add(valorDN.add(valorCH));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private BigDecimal[] antCaixa(Date iniData, Date fimData) {
        BigDecimal ttCR = new BigDecimal("0");
        BigDecimal ttDB = new BigDecimal("0");

        // CAIXA
        String avsql = "select id, aut, datahora dtrecebimento, logado, operacao, documento, rgprp, rgimv, contrato, valor::numeric(10,2) valor, lancamentos, lancamentos[s][1]::character varying(2) tipo, lancamentos[s][2]::numeric(19,2) valoroper, fechado from (SELECT *, generate_subscripts(lancamentos, 1) AS s from caixa) as foo where (datahora::date BETWEEN ? and ?) and aut > 0 and fechado = true order by upper(logado), datahora;";
        ResultSet avrs = conn.AbrirTabela(avsql,ResultSet.CONCUR_READ_ONLY, new Object[][] {{"date",Dates.toSqlDate(iniData)}, {"date", Dates.toSqlDate(fimData)}});
        try {
            while (avrs.next()) {
                BigDecimal valorDN = new BigDecimal("0");
                BigDecimal valorCH = new BigDecimal("0");
                BigDecimal valorBC = new BigDecimal("0");
                BigDecimal valorCT = new BigDecimal("0");
                if (avrs.getString("operacao").equalsIgnoreCase("CRE")) {
                    if (avrs.getString("tipo").equalsIgnoreCase("DN")) {
                        valorDN = avrs.getBigDecimal("valoroper").min(avrs.getBigDecimal("valor"));
                    } else if (avrs.getString("tipo").equalsIgnoreCase("CH")) {
                        valorCH = avrs.getBigDecimal("valoroper");
                    } else if (avrs.getString("tipo").equalsIgnoreCase("BC")) {
                        valorBC = avrs.getBigDecimal("valoroper");
                    } else valorCT = avrs.getBigDecimal("valoroper");
                    ttCR = ttCR.add(valorDN.add(valorCH));
                } else {
                    if (avrs.getString("tipo").equalsIgnoreCase("DN")) {
                        valorDN = avrs.getBigDecimal("valoroper").min(avrs.getBigDecimal("valor"));
                    } else if (avrs.getString("tipo").equalsIgnoreCase("CH")) {
                        valorCH = avrs.getBigDecimal("valoroper");
                    } else if (avrs.getString("tipo").equalsIgnoreCase("BC")) {
                        valorBC = avrs.getBigDecimal("valoroper");
                    } else valorCT = avrs.getBigDecimal("valoroper");
                    ttDB = ttDB.add(valorDN.add(valorCH));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        try { avrs.close(); } catch (Exception e) {}

        BigDecimal ttSld = new BigDecimal("0");
        ttSld = ttCR.subtract(ttDB);

        return new BigDecimal[] {ttCR, ttDB, ttSld};
    }

    private void ListaRazao(Date value) {
        Date iniData = Dates.primDataMes(value);
        Date fimData = Dates.DateAdd(Dates.DIA, -1, value);
        Date atuData = value;

        // Variavel de Impress�o
        Object[][] dadosImpr = {};

        // Extratos
        BigDecimal[] antEXT = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antEXT = antExtrato(iniData, fimData);
        }

        BigDecimal[] diaEXT = diaExtrato(atuData);
        BigDecimal sdaEXT = diaEXT[2].add(antEXT[2]);
        //System.out.println("PROPRIETARIOS.......... CR: " + diaEXT[0].doubleValue() + " DB: " + diaEXT[1].doubleValue() + " SD: " + diaEXT[2].doubleValue() + " SD.ANT: " + antEXT[2].doubleValue() + " SD.ATU: " + sdaEXT.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"PROPRIETARIOS", diaEXT[0].doubleValue(), diaEXT[1].doubleValue(), diaEXT[2].doubleValue(), antEXT[2].doubleValue(), sdaEXT.doubleValue()});
        // - FIM Extratos

        // Adiantamentos
        BigDecimal[] antADI = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antADI = antAdiantamentos(iniData, fimData);
        }

        BigDecimal[] diaADI = diaAdiantamentos(atuData);
        BigDecimal sdaADI = diaADI[2].add(antADI[2]);

        //System.out.println("ADIANTAMENTOS.......... CR: " + diaADI[0].doubleValue() + " DB: " + diaADI[1].doubleValue() + " SD: " + diaADI[2].doubleValue() + " SD.ANT: " + antADI[2].doubleValue() + " SD.ATU: " + sdaADI.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"ADIANTAMENTOS", diaADI[0].doubleValue(), diaADI[1].doubleValue(), diaADI[2].doubleValue(), antADI[2].doubleValue(), sdaADI.doubleValue()});
        // - FIM Adiantamentos

        // Locatarios
        BigDecimal[] antLOC = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antLOC = antLocatario(iniData, fimData);
        }

        BigDecimal[] diaLOC = diaLocatario(atuData);
        BigDecimal sdaLOC = diaLOC[2].add(antLOC[2]);
        //System.out.println("LDCATARIOS............. CR: " + diaLOC[0].doubleValue() + " DB: " + diaLOC[1].doubleValue() + " SD: " + diaLOC[2].doubleValue() + " SD.ANT: " + antLOC[2].doubleValue() + " SD.ATU: " + sdaLOC.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"LOCATARIOS", diaLOC[0].doubleValue(), diaLOC[1].doubleValue(), diaLOC[2].doubleValue(), antLOC[2].doubleValue(), sdaLOC.doubleValue()});
        // - FIM Locatarios

        // Bloqueados
        BigDecimal[] antBLO = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antBLO = antBloqueados(iniData, fimData);
        }

        BigDecimal[] diaBLO = diaBloqueados(atuData);
        BigDecimal sdaBLO = diaBLO[2].add(antBLO[2]);
        //System.out.println("BLOQUEADOS............. CR: " + diaBLO[0].doubleValue() + " DB: " + diaBLO[1].doubleValue() + " SD: " + diaBLO[2].doubleValue() + " SD.ANT: " + antBLO[2].doubleValue() + " SD.ATU: " + sdaBLO.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"BLOQUEADOS", diaBLO[0].doubleValue(), diaBLO[1].doubleValue(), diaBLO[2].doubleValue(), antBLO[2].doubleValue(), sdaBLO.doubleValue()});
        // - FIM Bloqueados

        // Controle
        BigDecimal[] antCTR = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antCTR = antControle(iniData, fimData);
        }

        BigDecimal[] diaCTR = diaControle(atuData);
        BigDecimal sdaCTR = diaCTR[2].add(antCTR[2]);
        //System.out.println("CONTROLE............... CR: " + diaCTR[0].doubleValue() + " DB: " + diaCTR[1].doubleValue() + " SD: " + diaCTR[2].doubleValue() + " SD.ANT: " + antCTR[2].doubleValue() + " SD.ATU: " + sdaCTR.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CONTROLE", diaCTR[0].doubleValue(), diaCTR[1].doubleValue(), diaCTR[2].doubleValue(), antCTR[2].doubleValue(), sdaCTR.doubleValue()});
        // - FIM Controle

        // Retencao
        BigDecimal antRT = new BigDecimal("0");
        if (iniData != null && fimData != null) {
            antRT = antRetencao(iniData, fimData);
        }
        BigDecimal diaRT = diaRetencao(atuData);
        BigDecimal sdaRT = diaRT.add(antRT);
        //System.out.println("RT..................... CR: " + diaRT.doubleValue() + " DB: 0.00 SD: " + diaRT.doubleValue() + " SD.ANT: " + antRT.doubleValue() + " SD.ATU: " + sdaRT.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"RETEN��O", diaRT.doubleValue(), 0d, diaRT.doubleValue(), antRT.doubleValue(), sdaRT.doubleValue()});
        // - FIM Retencao

        // Totaliza PRO+ADI+LOCA+BLOQ+CTRL+RT
        BigDecimal col1 = diaEXT[0].add(diaADI[0]).add(diaLOC[0]).add(diaBLO[0]).add(diaCTR[0]).add(diaRT);
        BigDecimal col2 = diaEXT[1].add(diaADI[1]).add(diaLOC[1]).add(diaBLO[1]).add(diaCTR[1]);
        BigDecimal col3 = diaEXT[2].add(diaADI[2]).add(diaLOC[2]).add(diaBLO[2]).add(diaCTR[2]).add(diaRT);
        BigDecimal col4 = antEXT[2].add(antADI[2]).add(antLOC[2]).add(antBLO[2]).add(antCTR[2]).add(antRT);
        BigDecimal col5 = sdaEXT.add(sdaADI).add(sdaLOC).add(sdaBLO).add(sdaCTR).add(sdaRT);
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"", "=========", "=========", "=========", "=========", "========="});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"TOTAL", col1.doubleValue(), col2.doubleValue(), col3.doubleValue(), col4.doubleValue(), col5.doubleValue()});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
        //

        // Socios
        BigDecimal[] antSOC = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antSOC = antSocios(iniData, fimData);
        }

        BigDecimal[] diaSOC = diaSocios(atuData);
        BigDecimal sdaSOC = diaSOC[2].add(antSOC[2]);
        //System.out.println("SOCIOS................. CR: " + diaSOC[0].doubleValue() + " DB: " + diaSOC[1].doubleValue() + " SD: " + diaSOC[2].doubleValue() + " SD.ANT: " + antSOC[2].doubleValue() + " SD.ATU: " + sdaSOC.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"SOCIOS", diaSOC[0].doubleValue(), diaSOC[1].doubleValue(), diaSOC[2].doubleValue(), antSOC[2].doubleValue(), sdaSOC.doubleValue()});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
        // - FIM Socios

        // Deposito
        BigDecimal[] antDEP = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antDEP = antDeposito(iniData, fimData);
        }

        BigDecimal[] diaDEP = diaDeposito(atuData);
        BigDecimal sdaDEP = diaDEP[2].add(antDEP[2]);
        //System.out.println("DEPOSITO............... CR: " + diaDEP[0].doubleValue() + " DB: " + diaDEP[1].doubleValue() + " SD: " + diaDEP[2].doubleValue() + " SD.ANT: " + antDEP[2].doubleValue() + " SD.ATU: " + sdaDEP.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"DEPOSITO", diaDEP[0].doubleValue(), diaDEP[1].doubleValue(), diaDEP[2].doubleValue(), antDEP[2].doubleValue(), sdaDEP.doubleValue()});
        // - FIM Deposito

        // Despesas
        BigDecimal antDS = new BigDecimal("0");
        if (iniData != null && fimData != null) {
            antDS = antDespesas(iniData, fimData, value);
        }
        BigDecimal diaDS = diaDespesas(atuData, value);
        BigDecimal sddDS = new BigDecimal("0").subtract(diaDS);
        BigDecimal sdaDS = new BigDecimal("0").subtract(antDS);
        BigDecimal sdfDS = sddDS.add(sdaDS);
        //System.out.println("DESPESAS............... CR: " + diaDS.doubleValue() + " DB: 0.00 SD: " + diaDS.doubleValue() + " SD.ANT: " + antDS.doubleValue() + " SD.ATU: " + sdaDS.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"DESPESAS", 0d, diaDS.doubleValue(), sddDS.doubleValue(), sdaDS.doubleValue(), sdfDS.doubleValue()});
        // - FIM Despesas

        // Caixa
        BigDecimal[] antCXA = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antCXA = antCaixa(iniData, fimData);
        }

        BigDecimal[] diaCXA = diaCaixa(atuData);
        BigDecimal sdaCXA = diaCXA[2].add(antCXA[2]);
        //System.out.println("CAIXA.................. CR: " + diaCXA[0].doubleValue() + " DB: " + diaCXA[1].doubleValue() + " SD: " + diaCXA[2].doubleValue() + " SD.ANT: " + antCXA[2].doubleValue() + " SD.ATU: " + sdaCXA.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CAIXA", diaCXA[0].doubleValue(), diaCXA[1].doubleValue(), diaCXA[2].doubleValue(), antCXA[2].doubleValue(), sdaCXA.doubleValue()});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {" ", " ", " ", " ", " ", " "});
        // - FIM Caixa

        // Adm
        BigDecimal[] antADM = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
        if (iniData != null && fimData != null) {
            antADM = antAdmin(iniData, fimData, value);
        }

        BigDecimal[] diaADM = diaAdmin(atuData, value);
        BigDecimal sdaCM = diaADM[0].add(antADM[0]);
        BigDecimal sdaMU = diaADM[1].add(antADM[1]);
        BigDecimal sdaJU = diaADM[2].add(antADM[2]);
        BigDecimal sdaCO = diaADM[3].add(antADM[3]);
        BigDecimal sdaEP = diaADM[4].add(antADM[4]);
        BigDecimal sdaSG = diaADM[5].add(antADM[5]);
        //System.out.println("CM..................... CR: " + diaADM[0].doubleValue() + " DB: 0.00 SD: " + diaADM[0].doubleValue() + " SD.ANT: " + antADM[0].doubleValue() + " SD.ATU: " + sdaCM.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CM", diaADM[0].doubleValue(), 0d, diaADM[0].doubleValue(), antADM[0].doubleValue(), sdaCM.doubleValue()});

        //System.out.println("MU..................... CR: " + diaADM[1].doubleValue() + " DB: 0.00 SD: " + diaADM[1].doubleValue() + " SD.ANT: " + antADM[1].doubleValue() + " SD.ATU: " + sdaMU.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"MU", diaADM[1].doubleValue(), 0d, diaADM[1].doubleValue(), antADM[1].doubleValue(), sdaMU.doubleValue()});

        //System.out.println("JU..................... CR: " + diaADM[2].doubleValue() + " DB: 0.00 SD: " + diaADM[2].doubleValue() + " SD.ANT: " + antADM[2].doubleValue() + " SD.ATU: " + sdaJU.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"JU", diaADM[2].doubleValue(), 0d, diaADM[2].doubleValue(), antADM[2].doubleValue(), sdaJU.doubleValue()});

        //System.out.println("CO..................... CR: " + diaADM[3].doubleValue() + " DB: 0.00 SD: " + diaADM[3].doubleValue() + " SD.ANT: " + antADM[3].doubleValue() + " SD.ATU: " + sdaCO.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"CO", diaADM[3].doubleValue(), 0d, diaADM[3].doubleValue(), antADM[3].doubleValue(), sdaCO.doubleValue()});

        //System.out.println("EP..................... CR: " + diaADM[4].doubleValue() + " DB: 0.00 SD: " + diaADM[4].doubleValue() + " SD.ANT: " + antADM[4].doubleValue() + " SD.ATU: " + sdaEP.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"EP", diaADM[4].doubleValue(), 0d, diaADM[4].doubleValue(), antADM[4].doubleValue(), sdaEP.doubleValue()});

        //System.out.println("SG..................... CR: " + diaADM[5].doubleValue() + " DB: 0.00 SD: " + diaADM[5].doubleValue() + " SD.ANT: " + antADM[5].doubleValue() + " SD.ATU: " + sdaSG.doubleValue());
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"SG", diaADM[5].doubleValue(), 0d, diaADM[5].doubleValue(), antADM[5].doubleValue(), sdaSG.doubleValue()});
        // - FIM Adm

        // Contas ADM
        AvisoADMContas[] antATC = null;
        if (iniData != null && fimData != null) {
            antATC = antAvisoADMContas(0, iniData, fimData, value);
        }
        AvisoADMContas[] diaATC = diaAvisoADMContas(0, atuData, value);
        double tdcr = 0;double tddb = 0;double tdsd = 0;double tacr = 0;
        double tadb = 0;double tasd = 0;double tstu = 0;
        if (antATC != null) {
            if (antATC.length > 0) {
                for (AvisoADMContas ACT : antATC) {
                    int pos = ClassOf(diaATC, ACT.getRegistro());
                    double dcr = 0;
                    double ddb = 0;
                    double dsd = 0;
                    double acr = 0;
                    double adb = 0;
                    double asd = 0;
                    double stu = 0;
                    if (pos != -1) {
                        dcr = diaATC[pos].getCredito().doubleValue();
                        ddb = diaATC[pos].getDebito().doubleValue();
                        dsd = dcr - ddb;
                    }
                    acr = ACT.getCredito().doubleValue();
                    adb = ACT.getDebito().doubleValue();
                    asd = acr - adb;

                    stu = dsd + asd;

                    String nmCTA = null;
                    try {
                        nmCTA = conn.LerCamposTabela(new String[]{"descricao"}, "adm", "codigo = ?", new Object[][]{{"string", ACT.getRegistro()}})[0][3].toString();
                    } catch (Exception e) {
                    }
                    //System.out.println(nmCTA + ".................... CR: " + dcr + " DB: " + ddb + " SD: " + dsd + " SD.ANT: " + asd + " SD.ATU: " + stu);
                    dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[]{nmCTA, dcr, ddb, dsd, asd, stu});
                    tdcr += dcr;
                    tddb += ddb;
                    tdsd += dsd;
                    tasd += asd;
                    tstu += stu;
                }
            }
        } else {
            for (AvisoADMContas ACT : diaATC) {
                double dcr = 0;double ddb = 0;double dsd = 0;double acr = 0;
                double adb = 0;double asd = 0;double stu = 0;

                dcr = ACT.getCredito().doubleValue();
                ddb = ACT.getDebito().doubleValue();
                dsd = dcr - ddb;

                stu = dsd + asd;

                String nmCTA = null;
                try { nmCTA = conn.LerCamposTabela(new String[]{"descricao"}, "adm", "codigo = ?", new Object[][]{{"string", ACT.getRegistro()}})[0][3].toString(); } catch (Exception e) { }
                //System.out.println(nmCTA + ".................... CR: " + dcr + " DB: " + ddb + " SD: " + dsd + " SD.ANT: " + asd + " SD.ATU: " + stu);
                dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {nmCTA, dcr, ddb, dsd, asd, stu});
                tdcr += dcr; tddb += ddb; tdsd += dsd; tasd += asd; tstu += stu;
            }
        }
        // - FIM Contas Adm

        BigDecimal coll1 = diaADM[0].add(diaADM[1]).add(diaADM[2]).add(diaADM[3]).add(diaADM[4]).add(diaADM[5]).add(BigDecimal.valueOf(tdcr));
        BigDecimal coll2 = new BigDecimal(tddb);
        BigDecimal coll3 = diaADM[0].add(diaADM[1]).add(diaADM[2]).add(diaADM[3]).add(diaADM[4]).add(diaADM[5]).add(BigDecimal.valueOf(tdsd));
        BigDecimal coll4 = antADM[0].add(antADM[1]).add(antADM[2]).add(antADM[3]).add(antADM[4]).add(antADM[5]).add(BigDecimal.valueOf(tasd));
        BigDecimal coll5 = sdaCM.add(sdaMU).add(sdaJU).add(sdaCO).add(sdaEP).add(sdaSG).add(BigDecimal.valueOf(tstu));

        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"", "=========", "=========", "=========", "=========", "========="});
        dadosImpr = FuncoesGlobais.ObjectsAdd(dadosImpr, new Object[] {"TOTAL", coll1.doubleValue(), coll2.doubleValue(), coll3.doubleValue(), coll4.doubleValue(), coll5.doubleValue()});

        Collections dadm = VariaveisGlobais.getAdmDados();
        new Impressao(VariaveisGlobais.usuario).ImprimeRazaoPDF(dadm, dadosImpr, true);
    }
}
