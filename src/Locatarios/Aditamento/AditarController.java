package Locatarios.Aditamento;

import Classes.paramEvent;
import Editor.activetree.CustomJEditor;
import Editor.activetree.ProcessaTexto;
import Funcoes.*;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AditarController implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    private int id;
    private int idcarteira;
    private String rgprp;
    private String rgimv;
    private String contrato;
    private String dtFim;
    private String dAdito;
    private String dVencto;
    private String vrmensalAdito;
    private String cotaAdito;
    private String refAdito;
    private String docAdito;

    private boolean retorno = false;

    @FXML private AnchorPane anchorPane;
    @FXML private DatePicker dtAdito;
    @FXML private TextField aditoFileName;
    @FXML private Button btnFileName;
    @FXML private Button btnImprimirAdito;
    @FXML private Button btnConsolidarAdito;
    @FXML private Button btnRetornar;

    @FXML private DatePicker dtVencto;
    @FXML private TextField vrmensal;
    @FXML private TextField cpoCota;
    @FXML private TextField cpoRef;

    public void setRgprp(String rgprp) { this.rgprp = rgprp; }
    public void setRgimv(String rgimv) { this.rgimv = rgimv; }
    public void setContrato(String contrato) { this.contrato = contrato; }
    public void setDtFim(String dtFim) { this.dtFim = dtFim; }
    public void setIdcarteira(int idcarteira) { this.idcarteira = idcarteira; }

    public String getdAdito() { return dAdito; }
    public String getdVencto() { return dVencto; }
    public void setdVencto(String dVencto) { this.dVencto = dVencto; }
    public String getVrmensalAdito() { return vrmensalAdito; }
    public void setVrmensalAdito(String vrmensalAdito) { this.vrmensalAdito = vrmensalAdito; }
    public String getCotaAdito() { return cotaAdito; }
    public void setCotaAdito(String cotaAdito) { this.cotaAdito = cotaAdito; }
    public String getRefAdito() { return refAdito; }
    public void setRefAdito(String refAdito) { this.refAdito = refAdito; }

    public boolean isRetorno() { return retorno; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            dtAdito.getEditor().setText("");
            dtVencto.getEditor().setText(dVencto);
            vrmensal.setText(vrmensalAdito);
            cpoCota.setText(cotaAdito);
            cpoRef.setText(refAdito);
            id = -1; docAdito = null;

            String selectSQL = "SELECT id, idcarteira, dtaditamento, mensal, dtvencimento, cota, referencia, doc " +
                    "FROM carteiraa WHERE idcarteira = ?;";
            ResultSet rsc = conn.AbrirTabela(selectSQL, ResultSet.CONCUR_READ_ONLY, new Object[][]{{"int", idcarteira}});
            try {
                while (rsc.next()) {
                    try { id = rsc.getInt("id"); } catch (SQLException e) {}
                    try { idcarteira = rsc.getInt("idcarteira"); } catch (SQLException e) {}
                    try { dAdito = Dates.DateFormata("dd/MM/yyyy", rsc.getDate("dtaditamento")); } catch (SQLException e) {}
                    try { dVencto = Dates.DateFormata("dd/MM/yyyy", rsc.getDate("dtvencimento")); } catch (SQLException e) {}
                    try { vrmensalAdito = LerValor.BigDecimalToCurrency(rsc.getBigDecimal("mensal")); } catch (SQLException e) {}
                    try { cotaAdito = rsc.getString("cota"); } catch (SQLException e) {}
                    try { refAdito = rsc.getString("referencia"); } catch (SQLException e) {}
                    try { docAdito = rsc.getString("doc"); } catch (SQLException e) {}
                }
            } catch (SQLException e) { e.printStackTrace();}
            try {rsc.close();} catch (SQLException e) {}

            if (id > -1) {
                dtAdito.getEditor().setText(dAdito);
                dtVencto.getEditor().setText(dVencto);
                vrmensal.setText(vrmensalAdito);
                cpoCota.setText(cotaAdito);
                cpoRef.setText(refAdito);
                aditoFileName.setText(docAdito);

                btnConsolidarAdito.setDisable(false);
                btnRetornar.setDisable(false);
            }
        });

        // Setar os campos
        MaskFieldUtil.dateRefField(cpoCota);
        MaskFieldUtil.dateRefField(cpoRef);
        MaskFieldUtil.monetaryField(vrmensal);

        btnConsolidarAdito.setDisable(true);
        btnRetornar.setDisable(false);

        btnImprimirAdito.setOnAction(event -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String FileName = aditoFileName.getText().trim();
                    if (!new File(FileName).exists()) {
                        return;
                    }

                    String textoContrato = readFile(new File(FileName));

                    // Chama o Processamento
                    ProcessaTexto processaTexto = new ProcessaTexto(rgprp, rgimv, contrato, textoContrato);
                    InputStream targetStream = processaTexto.getL_targetStream();

                    CustomJEditor editor2 = new CustomJEditor();
                    editor2.set_isPreContrato(true);
                    editor2.postInit();
                    editor2.openDocument(targetStream);
                    editor2.setToTop(true);
                    editor2.setVisible(true);

                    // Salva dados do aditamento
                    String deleteSQL = "DELETE FROM carteiraa WHERE id = ?;";
                    conn.ExecutarComando(deleteSQL, new Object[][] {{"int", id}});

                    String insertSQL = "INSERT INTO carteiraa(idcarteira, dtaditamento, mensal, dtvencimento, " +
                            "cota, referencia, doc) VALUES (?, ?, ?, ?, ?, ?, ?);";
                    Object[][] param = {
                            {"int", idcarteira},
                            {"date", Dates.toSqlDate(Dates.StringtoDate(dtAdito.getEditor().getText(),"dd/MM/yyyy"))},
                            {"decimal", new BigDecimal(LerValor.Number2BigDecimal(vrmensal.getText()))},
                            {"date", Dates.toSqlDate(Dates.StringtoDate(dtVencto.getEditor().getText(),"dd/MM/yyyy"))},
                            {"string", cpoCota.getText()},
                            {"string", cpoRef.getText()},
                            {"string", aditoFileName.getText()}
                    };
                    conn.ExecutarComando(insertSQL, param);

                    btnConsolidarAdito.setDisable(false);
                    btnRetornar.setDisable(false);
                }
            });
        });

        // Setar Botao Imprimir
        BooleanBinding bd = aditoFileName.textProperty().isEmpty();
        btnImprimirAdito.disableProperty().bind(bd);

        btnConsolidarAdito.setOnAction(event -> {
            Alert msg = new Alert(Alert.AlertType.CONFIRMATION, "O Documento foi Impresso Corretamente?", new ButtonType("Sim"), new ButtonType("Não"));
            Optional<ButtonType> result = msg.showAndWait();
            if (result.get().getText().equals("Não")) {
                btnConsolidarAdito.setDisable(true);
                retorno = false;
                return;
            }

            String updateSQL = "UPDATE carteira SET dtaditamento=?, cota=?, mensal=?, dtvencimento=?, referencia=? " +
                    "WHERE id = ?;";
            Object[][] param = {
                    {"date", Dates.toSqlDate(Dates.StringtoDate(dtAdito.getEditor().getText(),"dd/MM/yyyy"))},
                    {"string", cpoCota.getText()},
                    {"decimal", new BigDecimal(LerValor.Number2BigDecimal(vrmensal.getText()))},
                    {"date", Dates.toSqlDate(Dates.StringtoDate(dtVencto.getEditor().getText(),"dd/MM/yyyy"))},
                    {"string", cpoRef.getText()},
                    {"int", idcarteira}
            };
            conn.ExecutarComando(updateSQL, param);

            // Deleta carteiraa
            String deleteSQL = "DELETE FROM carteiraa WHERE id = ?;";
            conn.ExecutarComando(deleteSQL, new Object[][] {{"int", id}});

            retorno = true;
            try {anchorPane.fireEvent(new paramEvent(new String[] {"Aditar"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        });

        btnFileName.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Arquivos de Impressão de contratos");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Arquivo de Texto FXRent", "*.rtf"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                aditoFileName.setText(selectedFile.getAbsolutePath());
            }
        });

        btnRetornar.setOnAction(event -> {
            retorno = false;
            try {anchorPane.fireEvent(new paramEvent(new String[] {"Aditar"},paramEvent.GET_PARAM));} catch (NullPointerException e) {}
        });
    }

    private String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) { ex.printStackTrace(); } finally { try { bufferedReader.close(); } catch (IOException ex) { ex.printStackTrace(); } }

        return stringBuffer.toString();
    }

}

