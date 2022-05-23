/*
package Movimento.BaixaManual;

import Calculos.AutentMult;
import Calculos.Processa;
import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class BaixaManual implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private TableView<cBaixaManual> baLista;
    @FXML private TableColumn<cBaixaManual, Integer> baId;
    @FXML private TableColumn<cBaixaManual, String> baContrato;
    @FXML private TableColumn<cBaixaManual, String> baNome;
    @FXML private TableColumn<cBaixaManual, String> baBanco;
    @FXML private TableColumn<cBaixaManual, String> baIdentificador;
    @FXML private TableColumn<cBaixaManual, Boolean> baTag;
    @FXML private Button baBaixar;
    @FXML private TableView<cBaixaManual> bxLista;
    @FXML private TableColumn<cBaixaManual, Integer> bxId;
    @FXML private TableColumn<cBaixaManual, String> bxContrato;
    @FXML private TableColumn<cBaixaManual, String> bxNome;
    @FXML private TableColumn<cBaixaManual, String> bxBanco;
    @FXML private TableColumn<cBaixaManual, String> bxIdentificador;
    @FXML private Button bxListar;
    @FXML private ImageView lupa;
    @FXML private ImageView clear;
    @FXML private TextField busca;

*
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lupa.setImage(new Image(getClass().getResourceAsStream("/Figuras/lupa_16x16.png")));
        clear.setImage(new Image(getClass().getResourceAsStream("/Figuras/clear_16x16.png")));

        clear.setOnMouseClicked(event -> {
            busca.setText("");
            busca.requestFocus();
        });

        PopulaBaixar();

        baBaixar.setOnAction(event -> {
            for (cBaixaManual item : baLista.getItems()) {
                if (!item.isTag()) continue;


            }
        });
    }

    private void PopulaBaixar() {
        List<cBaixaManual> data = new ArrayList<cBaixaManual>();
        ResultSet imv;
        String qSQL = "select m.id id, m.rgprp rgprp, m.rgimv rgimv, m.contrato contrato, " +
                "(select case when l.l_fisjur then l.l_f_nome else l.l_j_razao end from locatarios l where l.l_contrato = m.contrato) nome, " +
                "m.banco banco, m.nnumero identificador, m.dtvencimento vencimento from movimento m where (m.aut_rec IS NULL OR m.aut_rec = 0) AND " +
                "(m.nnumero IS NOT NULL OR m.nnumero != '') order by m.contrato;";

        try {
            imv = conn.AbrirTabela(qSQL, ResultSet.CONCUR_READ_ONLY);
            while (imv.next()) {
                int tid = -1; try {tid = imv.getInt("id");} catch (SQLException e) {}
                String trgprp = null; try {trgprp = imv.getString("rgprp");} catch (SQLException e) {}
                String trgimv = null; try {trgimv = imv.getString("rgimv");} catch (SQLException e) {}
                String tcontrato = null; try {tcontrato = imv.getString("contrato");} catch (SQLException e) {}
                String tnome = null; try {tnome = imv.getString("nome");} catch (SQLException e) {}
                String tbanco = null; try {tbanco = imv.getString("banco");} catch (SQLException e) {}
                String tidentificador = null; try {tidentificador = imv.getString("identificador");} catch (SQLException e) {}
                Date tvencto = null; try {tvencto = imv.getDate("vencimento");} catch (SQLException e) {}
                boolean ttag = false;

                data.add(new cBaixaManual(tid, trgprp, trgimv, tcontrato, tnome, tbanco, tidentificador, tvencto, ttag));
            }
            imv.close();
        } catch (SQLException e) {}

        baId.setCellValueFactory(new PropertyValueFactory("id"));
        baContrato.setCellValueFactory(new PropertyValueFactory("contrato"));
        baNome.setCellValueFactory(new PropertyValueFactory("nome"));
        baBanco.setCellValueFactory(new PropertyValueFactory("banco"));
        baIdentificador.setCellValueFactory(new PropertyValueFactory("identificador"));
        baTag.setCellFactory( tc -> new CheckBoxTableCell<>());

        baLista.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        baLista.setItems(FXCollections.observableArrayList(data));
        baLista.setEditable(true);

        setRowSelection();

        FilteredList<cBaixaManual> filteredData = new FilteredList<cBaixaManual>(FXCollections.observableArrayList(data), e -> true);
        busca.setOnKeyReleased(e ->{
            busca.textProperty().addListener((observableValue, oldValue, newValue) ->{
                filteredData.setPredicate((Predicate<? super cBaixaManual>) user->{
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(user.getContrato().contains(newValue)){
                        return true;
                    }else if(user.getNome().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else if(user.getIdentificador().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else if (user.getBanco().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    } else return false;
                });
            });
            SortedList<cBaixaManual> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(baLista.comparatorProperty());
            baLista.setItems(sortedData);
            baLista.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        });

    }

    public void setRowSelection() {
        baLista.getSelectionModel().clearSelection();
        baLista.getSelectionModel().setCellSelectionEnabled(false);
    }

    private void Baixa(String vcto, BigInteger aut, String rgprp, String rgimv) {
        BigInteger aut = conn.PegarAutenticacao();

        // Gravar no caixa
        try {
            String caixaSQL = "INSERT INTO caixa (aut, datahora, logado, operacao, documento, rgprp, rgimv, " +
                    "contrato, valor, lancamentos) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');";
            String lanctos = lancamentos;
            caixaSQL = String.format(caixaSQL,
                    aut,
                    new java.util.Date(),
                    VariaveisGlobais.usuario,
                    "CRE",
                    "REC",
                    rgprp, rgimv, contrato,
                    valor, lanctos
            );
            if (conn.ExecutarComando(caixaSQL) > 0) {
                System.out.println("incluido.");
                Baixa(vct, aut, rgprp, rgimv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pegar o mes de referencia do Vencimento no descdif
        String descdif_refer = Dates.DateFormata("MM/yyyy", Dates.DateAdd(Dates.MES, -1, Dates.StringtoDate(vcto, "dd/MM/yyyy")));

        //
        BigDecimal[] valores = ProcessarCampos(vcto);

        // IRRF
        BigDecimal irenda = valores[3];
        if (irenda.compareTo(BigDecimal.ZERO) == 1) {
            irenda.toPlainString();
        }

        BigDecimal vrIptu = valores[4];

        // Calcula comissao
        BigDecimal baseComissao = valores[0].subtract(valores[2]).add(valores[1]);
        Double Comissao = baseComissao.doubleValue() * (VariaveisGlobais.co / 100);

        // MU, JU, CO, EP
        BigDecimal mu = valores[6];
        BigDecimal ju = valores[7];
        BigDecimal co = valores[8];
        BigDecimal ep = valores[9];

        // Movimento
        String sql = "UPDATE movimento SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', ir = '%s', cm = '%s', mu = '%s', ju = '%s', co = '%s', ep = '%s', aut_pag = '%s' WHERE contrato = '%s' AND dtvencimento = '%s' AND dtrecebimento is null;";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                irenda.toPlainString(),
                Comissao,
                mu.toPlainString(),
                ju.toPlainString(),
                co.toPlainString(),
                ep.toPlainString(),
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.getText().trim(),
                Dates.StringtoString(vcto,"dd/MM/yyyy", "yyyy/MM/dd")
                //descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [movimento]. " + vcto);
        }

        // Desconto Diferença
        sql = "UPDATE descdif SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', aut_pag = '%s' WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.getText().trim(),
                descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [descdif]. " + vcto);
        }

        // Taxas
        sql = "UPDATE taxas SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', aut_pag = '%s' WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.getText().trim(),
                descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [taxas]. " + vcto);
        }

        // Seguros
        sql = "UPDATE seguros SET dtrecebimento = '%s', aut_rec = '%s', usr_rec = '%s', aut_pag = '%s' WHERE contrato = '%s' AND referencia = '%s';";
        sql = String.format(sql,
                Dates.toSqlDate(new java.util.Date()),
                aut,
                VariaveisGlobais.usuario,
                new AutentMult().MontaAutInicial(rgprp, rgimv),
                contrato.getText().trim(),
                descdif_refer.trim()
        );
        if (conn.ExecutarComando(sql) > 0) {
            System.out.println("Atualizando [seguro]. " + vcto);
        }
    }

    public BigDecimal[] ProcessarCampos(String vctos) {
        try {
            this.rgprp = (String) conn.LerCamposTabela(new String[]{"l_rgprp"}, "locatarios", "l_contrato = '" + contrato.getText() + "'")[0][3];
        } catch (Exception e) {
        }
        Processa calc = new Processa(this.rgprp, rgimv.getText(), contrato.getText(), Dates.StringtoDate(vctos, "dd-MM-yyyy"), Dates.StringtoDate(Dates.DatetoString(new java.util.Date()), "dd-MM-yyyy"));

        return new BigDecimal[]{calc.getAluguel(), calc.getDiferenca(), calc.getDescontos(), calc.getIrenda(), calc.getIptu(), calc.getSeguro(), calc.Multa(), calc.Juros(), calc.Correcao(), calc.Expediente()};
    }

}
*/
