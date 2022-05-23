package entrada.BuscaGlobal;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javax.rad.genui.UIColor;
import javax.rad.genui.UIImage;
import javax.rad.genui.component.UICustomComponent;
import javax.rad.genui.container.UIInternalFrame;
import javax.rad.genui.layout.UIBorderLayout;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BuscaGlobal implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    private Class<?> controller = null;

    @FXML private AnchorPane anchorPane;
    @FXML private Pane panel;
    @FXML private TableView<cBuscaGlobal> buscaLista;
    @FXML private TableColumn<cBuscaGlobal, Integer> buscaId;
    @FXML private TableColumn<cBuscaGlobal, String> buscaRgprp;
    @FXML private TableColumn<cBuscaGlobal, String> buscaRgimv;
    @FXML private TableColumn<cBuscaGlobal, String> buscaContrato;
    @FXML private TableColumn<cBuscaGlobal, String> buscaCpfcnpj;
    @FXML private TableColumn<cBuscaGlobal, String> buscaNome;
    @FXML private TableColumn<cBuscaGlobal, String> buscaFantasia;
    @FXML private TableColumn<cBuscaGlobal, Boolean> buscaAtivo;
    @FXML private RadioButton buscaProprietarios;
    @FXML private ToggleGroup propLoca;
    @FXML private RadioButton buscaLocatarios;
    @FXML private CheckBox buscaQQPosicao;

    private String busca;
    public void setBusca(String busca) {
        this.busca = busca;
        Busca((buscaQQPosicao.isSelected() ? "%" : "") + busca.toUpperCase().trim() + '%');
    }

    private SimpleBooleanProperty Ativa = new SimpleBooleanProperty(true);
    public boolean getAtiva() {
        return Ativa.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buscaQQPosicao.setOnAction(event -> {
            Busca((buscaQQPosicao.isSelected() ? "%" : "") + this.busca.toUpperCase().trim() + '%');
        });
        buscaProprietarios.setOnAction(event -> {
            Busca((buscaQQPosicao.isSelected() ? "%" : "") + this.busca.toUpperCase().trim() + '%');
        });
        buscaLocatarios.setOnAction(event -> {
            Busca((buscaQQPosicao.isSelected() ? "%" : "") + this.busca.toUpperCase().trim() + '%');
        });
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Ativa.set(false);
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
        internalFrame.setClosable(close);
        internalFrame.setBackground(new UIColor(221,221, 221));

        internalFrame.pack();
        internalFrame.setVisible(true);

        controller = loader.getController();
    }

    private String TrocaNome(String value) {
        for (int contas = 0; contas <= VariaveisGlobais.contas_ca.size() - 1; contas++) {
            value = value.replace("[" + VariaveisGlobais.contas_ca.key(contas) + "]", VariaveisGlobais.contas_ca.get(VariaveisGlobais.contas_ca.key(contas)));
        }
        return value;
    }

    private void Busca(String value) {
        String selectSQL = "";
        Object[][] param = null;
        if (buscaProprietarios.isSelected()) {
            // Proprietários
            selectSQL = "SELECT p_id id, p_rgprp rgprp, ''::varchar(6) rgimv, ''::varchar(6) contrato, p_cpfcnpj cpfcnpj, p_nome nome, ''::varchar(60) fantasia, false ativo FROM proprietarios WHERE (exclusao is null) AND p_rgprp::varchar(6) LIKE ? OR Trim(p_cpfcnpj) LIKE ? OR Upper(Trim(p_nome)) LIKE ? ORDER BY Upper(Trim(p_nome));";
            param = new Object[][] {
                    {"string", value},
                    {"string", value},
                    {"string", value}
            };
        } else {
            // Locatários
            selectSQL = "SELECT l_id id, l_rgprp rgprp, l_rgimv rgimv, l_contrato contrato, l_cpfcnpj cpfcnpj, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END nome, CASE WHEN l_fisjur THEN '' ELSE l_j_fantasia END fantasia, l_dtbaixa is not null ativo FROM locatarios WHERE (exclusao is null) AND l_rgprp LIKE ? OR l_rgimv LIKE ? OR l_contrato LIKE ? OR UPPER(TRIM(CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END)) LIKE ? OR UPPER(TRIM(l_j_fantasia)) LIKE ? ORDER BY UPPER(TRIM(CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END));";
            param = new Object[][] {
                    {"string", value},
                    {"string", value},
                    {"string", value},
                    {"string", value},
                    {"string", value}
            };
        }
        ListaBusca(selectSQL, param, buscaProprietarios.isSelected());

    }

    private void ListaBusca(String sql, Object[][] param, boolean prop) {
        buscaLista.getItems().clear();

        List<cBuscaGlobal> data = new ArrayList<>();
        ResultSet imv;
        String qSQL = sql;
        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY, param);
            while (imv.next()) {
                String qrgprp = null, qrgimv = null, qcontrato = null, qcpfcnpj = null, qnome = null, qfantasia = null;
                boolean qativo = false; int qid = -1;
                try {qid = imv.getInt("id");} catch (SQLException e) {}
                try {qrgprp = imv.getString("rgprp");} catch (SQLException e) {}
                try {qrgimv = imv.getString("rgimv");} catch (SQLException e) {}
                try {qcontrato = imv.getString("contrato");} catch (SQLException e) {}
                try {qcpfcnpj = imv.getString("cpfcnpj");} catch (SQLException e) {}
                try {qnome = imv.getString("nome");} catch (SQLException e) {}
                try {qfantasia = imv.getString("fantasia");} catch (SQLException e) {}
                try {qativo = imv.getBoolean("ativo");} catch (SQLException e) {}

                data.add(new cBuscaGlobal(qid, qrgprp, qrgimv, qcontrato, qcpfcnpj, qnome, qfantasia, qativo));
            }
            imv.close();
        } catch (SQLException e) {}

        buscaRgprp.setCellValueFactory(new PropertyValueFactory<>("rgprp"));
        buscaRgprp.setStyle("-fx-alignment: center-right;");

        buscaRgimv.setCellValueFactory(new PropertyValueFactory<>("rgimv"));
        buscaRgimv.setStyle("-fx-alignment: center-right;");

        buscaContrato.setCellValueFactory(new PropertyValueFactory<>("contrato"));
        buscaContrato.setStyle("-fx-alignment: center;");

        buscaCpfcnpj.setCellValueFactory(new PropertyValueFactory<>("cpfcnpj"));
        buscaCpfcnpj.setStyle("-fx-alignment: center;");

        buscaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        buscaNome.setStyle("-fx-alignment: center-left;");
        buscaNome.setCellFactory(column -> {
            return new TableCell<cBuscaGlobal, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        cBuscaGlobal nome = getTableView().getItems().get(getIndex());
                        if (nome.isAtivo()) {
                            setTextFill(Color.RED);
                        } else {
                            setTextFill(Color.BLACK);
                        }
                    }
                }
            };
        });

        buscaFantasia.setCellValueFactory(new PropertyValueFactory<>("fantasia"));
        buscaFantasia.setStyle("-fx-alignment: center-left;");

        buscaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
        buscaAtivo.setStyle("-fx-alignment: center;");

        buscaId.setVisible(false);
        if (prop) {
            buscaRgimv.setVisible(false);
            buscaContrato.setVisible(false);
            buscaFantasia.setVisible(false);
        } else {
            buscaRgimv.setVisible(true);
            buscaContrato.setVisible(true);
            buscaFantasia.setVisible(true);
        }
        buscaAtivo.setVisible(false);

        if (data != null) buscaLista.setItems(FXCollections.observableArrayList(data));
        buscaLista.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        buscaLista.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Acao();
                return;
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                System.out.println("Escape");
            }
        });

        buscaLista.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    Acao();
                }
            }
        });
    }

    private void Acao() {
        if(buscaLista.getSelectionModel().getSelectedItem() != null) {
            System.out.println(buscaLista.getSelectionModel().getSelectedItem().getNome());
            if (buscaProprietarios.isSelected()) {
                try { ChamaTela("Proprietários", "/Proprietarios/Proprietario.fxml", "prop.png", null, false, false); } catch (Exception e) { }
                //((ProprietarioController) controller).MoveToProp(1);
            } else {
                try { ChamaTela("Locatarios", "/Locatarios/Locatario.fxml", "prop.png", null, false, false); } catch (Exception e) { }
            }
        }
    }
}
