package Movimento.BloqLocatario;

import Classes.paramEvent;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BloqLocatario implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    boolean bExiste = false;

    private String[] _possibleSuggestionsLcContrato = {};
    private String[] _possibleSuggestionsLcNome = {};
    private String[][] _possibleSuggestionsLc = {};
    private Set<String> possibleSuggestionsLcContrato;
    private Set<String> possibleSuggestionsLcNome;
    private boolean isSearchLcContrato = true;
    private boolean isSearchLcNome = true;

    @FXML private AnchorPane anchorPane;
    @FXML private TextField blqContrato;
    @FXML private TextField blqNome;
    @FXML private TextArea blqMotivo;
    @FXML private Button btnBloquear;
    @FXML private Button btnGravar;
    @FXML private TableView<BloqueioLoca> blqLista;
    @FXML private TableColumn<BloqueioLoca, Integer> blqListaId;
    @FXML private TableColumn<BloqueioLoca, String> blqListaContrato;
    @FXML private TableColumn<BloqueioLoca, String> blqListaNome;
    @FXML private TableColumn<BloqueioLoca, Date> blqListaData;
    @FXML private ProgressBar blqProgress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AutocompleteLc();

        btnGravar.setOnAction(event -> {
            String sql = ""; Object[][] param = null;
            if (bExiste) {
                sql = "UPDATE LocaBloq SET historico = ? WHERE contrato = ?;";
                param = new Object[][] {{"string", blqMotivo.getText().trim()}, {"string", blqContrato}};
            } else {
                sql = "INSERT INTO LocaBloq (contrato, nome, historico, status, data) VALUES (?, ?, ?, ?, ?);";
                param = new Object[][] {
                        {"string", blqContrato.getText()},
                        {"string", blqNome.getText()},
                        {"string", blqMotivo.getText().trim()},
                        {"integer",btnBloquear.getText().trim().toLowerCase().equalsIgnoreCase("BLOQUEAR") ? "1" : "0"},
                        {"date", Dates.toSqlDate(DbMain.getDateTimeServer())}
                };
            }
            conn.ExecutarComando(sql, param);

            //new SimpleThread().start();
            blqContrato.requestFocus();
        });

        btnBloquear.setOnAction(event -> {
            String sql = ""; Object[][] param = null;
            if (bExiste) {
                if (btnBloquear.getText().trim().toLowerCase().equalsIgnoreCase("BLOQUEAR")) {
                    sql = "UPDATE LocaBloq SET status = ?, historico = ? WHERE contrato = ?;";
                    param = new Object[][] {
                            {"integer", 1},
                            {"string", blqMotivo.getText().trim()},
                            {"string", blqContrato.getText()}
                    };
                    btnBloquear.setText("Desbloquear");
                } else {
                    sql = "DELETE FROM LocaBloq WHERE contrato = ?;";
                    param = new Object[][] {{"string", blqContrato.getText()}};
                    btnBloquear.setText("Bloquear");
                }
            } else {
                sql = "INSERT INTO LocaBloq (contrato, nome, historico, status, data) VALUES (?, ?, ?, ?, ?);";
                param = new Object[][] {
                        {"string", blqContrato.getText()},
                        {"string", blqNome.getText()},
                        {"string", blqMotivo.getText().trim()},
                        {"int",1},
                        {"date", Dates.toSqlDate(DbMain.getDateTimeServer())}
                };
                btnBloquear.setText("Desbloquear");
            }
            conn.ExecutarComando(sql, param);

            //new SimpleThread().start();
            blqContrato.requestFocus();
        });

       // new SimpleThread().start();
        Platform.runLater(() -> ListaBloqueados());
        Platform.runLater(() -> blqContrato.requestFocus());
    }

    private void AutocompleteLc() {
        _possibleSuggestionsLcContrato = new String[]{};
        _possibleSuggestionsLcNome = new String[]{};
        _possibleSuggestionsLc = new String[][]{};
        possibleSuggestionsLcContrato = new HashSet<String>();
        possibleSuggestionsLcNome = new HashSet<String>();
        isSearchLcContrato = true;
        isSearchLcNome = true;

        try {
            TextFields.bindAutoCompletion(blqContrato, new HashSet<String>());
            TextFields.bindAutoCompletion(blqNome, new HashSet<String>());
        } catch (Exception e) {}

        ResultSet imv = null;
        String qSQL = null;

        // Locatarios
        qSQL = "SELECT l_contrato contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END AS nome FROM locatarios ORDER BY l_contrato;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                String qcontrato = null, qnome = null;
                try {qcontrato = imv.getString("contrato");} catch (SQLException e) {}
                try {qnome = imv.getString("nome");} catch (SQLException e) {}
                _possibleSuggestionsLcContrato = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcContrato, qcontrato);
                possibleSuggestionsLcContrato = new HashSet<>(Arrays.asList(_possibleSuggestionsLcContrato));

                _possibleSuggestionsLcNome = FuncoesGlobais.ArrayAdd(_possibleSuggestionsLcNome, qnome);
                possibleSuggestionsLcNome = new HashSet<>(Arrays.asList(_possibleSuggestionsLcNome));

                _possibleSuggestionsLc = FuncoesGlobais.ArraysAdd(_possibleSuggestionsLc, new String[] {qcontrato, qnome});
            }
        } catch (SQLException e) {}
        try { DbMain.FecharTabela(imv); } catch (Exception e) {}

        TextFields.bindAutoCompletion(blqContrato, possibleSuggestionsLcContrato);
        TextFields.bindAutoCompletion(blqNome, possibleSuggestionsLcNome);

        blqContrato.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                blqContrato.setText(null);
                blqNome.setText(null);

                //
                blqMotivo.setText("");
                blqMotivo.setDisable(true);
                btnGravar.setDisable(true);
                btnBloquear.setDisable(true);
                btnBloquear.setText("Bloquear");
            } else {
                // out focus
                String pcontrato = null;
                try {pcontrato = blqContrato.getText();} catch (NullPointerException e) {}
                if (pcontrato != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc, 0, blqContrato.getText());
                    if (pos > -1 && isSearchLcContrato) {
                        isSearchLcNome = false;
                        blqNome.setText(_possibleSuggestionsLc[pos][1]);
                        isSearchLcNome = true;
                    }
                } else {
                    isSearchLcContrato = false;
                    isSearchLcNome = true;
                }
            }
        });

        blqNome.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                // on focus
                blqMotivo.setText("");
                blqMotivo.setDisable(true);
                btnGravar.setDisable(true);
                btnBloquear.setDisable(true);
                btnBloquear.setText("Bloquear");
            } else {
                // out focus
                if (blqNome.getText().trim() != null) {
                    int pos = FuncoesGlobais.FindinArrays(_possibleSuggestionsLc, 1, blqNome.getText());
                    String pcontrato = null;
                    try {
                        pcontrato = blqContrato.getText();
                    } catch (NullPointerException e) {
                    }
                    if (pos > -1 && isSearchLcNome && pcontrato == null) {
                        isSearchLcContrato = false;
                        if (!FuncoesGlobais.FindinArraysDup(_possibleSuggestionsLc, 1, blqNome.getText())) {
                            blqContrato.setText(_possibleSuggestionsLc[pos][0]);
                        } else {
                            WindowEnderecos(blqNome.getText());
                        }
                        isSearchLcContrato = true;
                    } else {
                        isSearchLcContrato = true;
                        isSearchLcNome = false;
                    }

                    LerBloqueio(blqContrato.getText());
                    blqMotivo.setDisable(false);
                    blqMotivo.setText("");
                    btnGravar.setDisable(false);
                    btnBloquear.setDisable(false);
                    blqMotivo.requestFocus();
                }
            }
        });
    }

    private void WindowEnderecos(String snome) {
        try {
            AnchorPane root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/Movimento/Alteracao/Enderecos.fxml"));
            } catch (Exception e) {e.printStackTrace();}
            UICustomComponent wrappedRoot = new UICustomComponent(root);

            UIInternalFrame internalFrame = new UIInternalFrame(VariaveisGlobais.desktopPanel);
            internalFrame.setLayout(new UIBorderLayout());
            internalFrame.setModal(true);
            internalFrame.setResizable(false);
            internalFrame.setMaximizable(false);
            internalFrame.add(wrappedRoot, UIBorderLayout.CENTER);
            internalFrame.setTitle("Endereços");
            //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
            internalFrame.setClosable(true);


            internalFrame.setBackground(new UIColor(103,165, 162));
            //internalFrame.setBackground(new UIColor(51,81, 135));

            internalFrame.pack();
            internalFrame.setVisible(true);

            root.fireEvent(new paramEvent(new Object[] {anchorPane, blqNome.getText()}, paramEvent.GET_PARAM));
        } catch (Exception e) {e.printStackTrace();}
    }

    private void LerBloqueio(String contrato) {
        String sql = "SELECT historico, status FROM LocaBloq WHERE contrato = ?;";
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}});

        blqMotivo.setText("");
        bExiste = false;
        try {
            while (rs.next()) {
                String motivo = rs.getString("historico");
                blqMotivo.setText(motivo);
                String btTxt = (rs.getBoolean("status") ? "Desbloquear" : "Bloquear");
                btnBloquear.setText(btTxt);
                bExiste = true;
            }
        } catch (Exception ex) {}
        DbMain.FecharTabela(rs);
    }

    private void ListaBloqueados() {
        List<BloqueioLoca> data = new ArrayList<BloqueioLoca>();
        String sql = "SELECT id, contrato, nome, data FROM LocaBloq ORDER BY Lower(nome);";
        blqLista.getItems().clear();
        ResultSet rs = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        try {
            //blqProgress.setValue(0);
            int eof = DbMain.RecordCount(rs); int pos = 1;
            rs.beforeFirst();
            while (rs.next()) {
                int br = (pos++ * 100) / eof;
                //jbarra.setValue(br + 1);

                int pId = rs.getInt("id");
                String pcontrato = rs.getString("contrato");
                String pnome = rs.getString("nome");
                Date pdata = rs.getDate("data");

                data.add(new BloqueioLoca(pId, pcontrato, pnome, pdata));
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        DbMain.FecharTabela(rs);

        blqListaId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        blqListaContrato.setCellValueFactory(new PropertyValueFactory<>("Contrato"));
        blqListaNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        blqListaData.setCellValueFactory(new PropertyValueFactory<>("Data"));

        blqLista.setItems(FXCollections.observableArrayList(data));
    }

}
