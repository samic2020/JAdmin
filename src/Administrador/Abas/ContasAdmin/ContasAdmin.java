package Administrador.Abas.ContasAdmin;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import javax.rad.genui.UIColor;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ContasAdmin implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private AnchorPane anchorPaneContasAdm;
    @FXML private TableView<ContasAdm> ca_contas;
    @FXML private TableColumn<ContasAdm, Integer> ca_contas_id;
    @FXML private TableColumn<ContasAdm, String> ca_contas_codigo;
    @FXML private TableColumn<ContasAdm, String> ca_contas_descricao;

    @FXML private TableView<ContasAdm> ac_contas;
    @FXML private TableColumn<ContasAdm, Integer> ac_contas_id;
    @FXML private TableColumn<ContasAdm, String> ac_contas_codigo;
    @FXML private TableColumn<ContasAdm, String> ac_contas_descricao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Initialize_ca();
    }

    private void Initialize_ca() {
        populateContasAdm();
        populateAdmContas();
    }

    private void populateAdmContas() {
        List<ContasAdm> data = new ArrayList<ContasAdm>();
        ResultSet rs;
        String qSQL = "SELECT id, codigo, descricao FROM adm_contas ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qdescricao = null;
                int qid = -1;

                try { qid = rs.getInt("id"); } catch (SQLException e) { }
                try { qcodigo = rs.getString("codigo"); } catch (SQLException e) { }
                try { qdescricao = rs.getString("descricao"); } catch (SQLException e) { }

                data.add(new ContasAdm(qid, qcodigo, qdescricao));
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {
        }

        ac_contas_id.setCellValueFactory(new PropertyValueFactory<ContasAdm, Integer>("id"));
        ac_contas_id.setStyle("-fx-alignment: CENTER;");

        ac_contas_codigo.setCellValueFactory(new PropertyValueFactory<ContasAdm, String>("codigo"));
        ac_contas_codigo.setStyle("-fx-alignment: CENTER;");
        ac_contas_codigo.setEditable(false);

        Callback<TableColumn<ContasAdm, String>, TableCell<ContasAdm, String>> cellFactoryDesc = (TableColumn<ContasAdm, String> p) -> new EditingCellTextDesc(60);
        ac_contas_descricao.setCellValueFactory(new PropertyValueFactory<ContasAdm, String>("descricao"));
        ac_contas_descricao.setCellFactory(cellFactoryDesc);
        ac_contas_descricao.setStyle("-fx-alignment: CENTER-LEFT;");
        ac_contas_descricao.setEditable(true);
        ac_contas_descricao.setOnEditCommit((TableColumn.CellEditEvent<ContasAdm, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescricao(t.getNewValue());
                    ContasAdm tbvlinhas = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if (tbvlinhas.getId() > 0) {
                        String uSql = "UPDATE adm_contas SET descricao = '" + tbvlinhas.getDescricao() + "' WHERE id = " + tbvlinhas.getId() + ";";
                        conn.ExecutarComando(uSql);
                    }
                }
        );


        if (!data.isEmpty()) ac_contas.setItems(FXCollections.observableArrayList(data));

        ac_contas.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.INSERT) {
                try {
                    ChamaTela("Lançamento Adm Contas", "/Administrador/amdContasLanc.fxml", "loca.png");
                } catch (Exception ex) {
                }
            } else if (event.getCode() == KeyCode.DELETE) {
                if (true) {
                    String sql = "DELETE FROM Adm_Contas WHERE id = %s;";
                    sql = String.format(sql, ac_contas.getSelectionModel().getSelectedItem().id.get());
                    conn.ExecutarComando(sql);
                }
            }
            populateContasAdm();
        });
    }

    private void populateContasAdm() {
        List<ContasAdm> data = new ArrayList<ContasAdm>();
        ResultSet rs;
        String qSQL = "SELECT id, codigo, descricao FROM adm ORDER BY id;";
        try {
            rs = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                String qcodigo = null, qdescricao = null;
                int qid = -1;

                try {
                    qid = rs.getInt("id");
                } catch (SQLException e) {
                }
                try {
                    qcodigo = rs.getString("codigo");
                } catch (SQLException e) {
                }
                try {
                    qdescricao = rs.getString("descricao");
                } catch (SQLException e) {
                }

                data.add(new ContasAdm(qid, qcodigo, qdescricao));
            }
            DbMain.FecharTabela(rs);
        } catch (SQLException e) {
        }

        ca_contas_id.setCellValueFactory(new PropertyValueFactory<ContasAdm, Integer>("id"));
        ca_contas_id.setStyle("-fx-alignment: CENTER;");

        ca_contas_codigo.setCellValueFactory(new PropertyValueFactory<ContasAdm, String>("codigo"));
        ca_contas_codigo.setStyle("-fx-alignment: CENTER;");
        ca_contas_codigo.setEditable(false);

        Callback<TableColumn<ContasAdm, String>, TableCell<ContasAdm, String>> cellFactoryDesc = (TableColumn<ContasAdm, String> p) -> new EditingCellTextDesc(60);
        ca_contas_descricao.setCellValueFactory(new PropertyValueFactory<ContasAdm, String>("descricao"));
        ca_contas_descricao.setCellFactory(cellFactoryDesc);
        ca_contas_descricao.setStyle("-fx-alignment: CENTER-LEFT;");
        ca_contas_descricao.setEditable(true);
        ca_contas_descricao.setOnEditCommit((TableColumn.CellEditEvent<ContasAdm, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescricao(t.getNewValue());
                    ContasAdm tbvlinhas = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if (tbvlinhas.getId() > 0) {
                        String uSql = "UPDATE adm SET descricao = '" + tbvlinhas.getDescricao() + "' WHERE id = " + tbvlinhas.getId() + ";";
                        conn.ExecutarComando(uSql);
                    }
                }
        );


        if (!data.isEmpty()) ca_contas.setItems(FXCollections.observableArrayList(data));
    }

    private void ChamaTela(String nome, String url, String icone) throws Exception {
        AnchorPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(url));
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
        internalFrame.setTitle(nome.replace("_", ""));
        //internalFrame.setIconImage(UIImage.getImage("/Figuras/prop.png"));
        internalFrame.setClosable(true);


        internalFrame.setBackground(new UIColor(221, 221, 221));
        //internalFrame.setBackground(new UIColor(51,81, 135));

        internalFrame.pack();
        internalFrame.setVisible(true);
    }
}
