/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Relatorios.Ficha;

import Calculos.Multas;
import Funcoes.DbMain;
import static Funcoes.StringManager.Left;
import Funcoes.VariaveisGlobais;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import pdfViewer.PdfViewer;

/**
 *
 * @author Samic
 */
public class Ficha implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;
    
    @FXML private ListView<classFicha> listaLoca;
    @FXML private Button btPrint;
    @FXML private TextField busca;
    @FXML private Label clear;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //clear.setStyle("-fx-background-image: url('C:\\Users\\Samic\\Pictures\\clear.png')");
        clear.setOnMouseClicked((event) -> { busca.setText(""); });
        FillFichas();
        
        btPrint.setOnAction((event) -> {
            List<classFicha> rows = new ArrayList<>();
            for (classFicha item : listaLoca.getSelectionModel().getSelectedItems()) {
                rows.add(new classFicha(
                        item.getRgprp(), 
                        item.getNome_prop(), 
                        item.getCpfcnpj_prop(), 
                        item.getRgimv(), 
                        item.getContrato(), 
                        item.getNome_loca(), 
                        item.getCpfcnpj_loca(), 
                        item.getEnd_imovel(), 
                        item.getTel_prop(), 
                        item.getNome_fiador1(), 
                        item.getTel_fiador1(), 
                        item.getNome_fiador2(), 
                        item.getTel_fiador2(), 
                        item.getObs_loca(), 
                        item.getDtinicio_cart(), 
                        item.getDttermino_cart(), 
                        item.getDtadito_cart(), 
                        item.getMulta(), 
                        item.getComissao()));
            }
            String pdfName = new PdfViewer().GeraPDFTemp(rows,"Ficha", null);
            new PdfViewer("Preview da Ficha", pdfName);            
        });
        
        Platform.runLater(() -> { busca.requestFocus(); });
    }
    
    private void FillFichas() {
        List<classFicha> rows = new ArrayList<>();
        
        String locaSQL = "SELECT l_rgprp, l_rgimv, l_contrato, CASE WHEN l_fisjur THEN l_f_nome ELSE l_j_razao END l_nome, l_cpfcnpj, l_msg, Upper(l_tipoimovel) l_tipo FROM locatarios WHERE (exclusao::varchar != '') IS NOT TRUE ORDER BY CASE WHEN l_fisjur THEN Upper(l_f_nome) ELSE Upper(l_j_razao) END;";
        ResultSet rs = conn.AbrirTabela(locaSQL, ResultSet.CONCUR_READ_ONLY);
        try {
            while (rs.next()) {
                String rgprp = null; try { rgprp = rs.getString("l_rgprp"); } catch (SQLException e) {}
                String rgimv = null; try { rgimv = rs.getString("l_rgimv"); } catch (SQLException e) {}
                String contrato = null; try { contrato = rs.getString("l_contrato"); } catch (SQLException e) {}
                String nomeloca = null; try { nomeloca = rs.getString("l_nome"); } catch (SQLException e) {}
                String cpfcnpjloca = null; try { cpfcnpjloca = rs.getString("l_cpfcnpj"); } catch (SQLException e) {}
                String obsloca = null; try {obsloca = rs.getString("l_msg"); } catch (SQLException e) {}
                String tipoimv = "COM"; try { tipoimv = Left(rs.getString("l_tipo"), 3); } catch (SQLException e) {}
                
                String nomeprop = null; String cpfcnpjprop = null; String telprop = null;
                String propSQL = "SELECT p_nome, p_cpfcnpj, p_tel FROM proprietarios WHERE p_rgprp::varchar = ? LIMIT 1;";
                ResultSet prs = conn.AbrirTabela(propSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", rgprp}});
                while (prs.next()) {
                    try { nomeprop = prs.getString("p_nome"); } catch (SQLException e) {}
                    try { cpfcnpjprop = prs.getString("p_cpfcnpj"); } catch (SQLException e) {}
                    try { telprop = prs.getString("p_tel"); } catch (SQLException e) {}
                }
                prs.close();
                
                String endimv = null; String imvSQL = "SELECT Trim(i_end) || ', ' || trim(i_num) || CASE WHEN Trim(i_cplto) = '' THEN '' ELSE ' - ' || Trim(i_cplto) END || ' / ' || Trim(i_bairro) ender FROM imoveis WHERE i_rgprp = ? AND i_rgimv = ? LIMIT 1;";
                ResultSet irs = conn.AbrirTabela(imvSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", rgprp}, {"string", rgimv}});
                while (irs.next()) {
                    try { endimv = irs.getString("ender"); } catch (SQLException e) {}
                }
                irs.close();
                
                String nomefia1 = null; String telfia1 = null; String nomefia2 = null; String telfia2 = null;
                String fiaSQL = "SELECT CASE WHEN f_fisjur THEN f_f_nome ELSE f_j_razao END f_nome, CASE WHEN f_fisjur THEN f_f_tel ELSE f_j_tel END f_tel FROM fiadores WHERE f_contrato = ? LIMIT 2;";
                ResultSet frs = conn.AbrirTabela(fiaSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}});
                int pos = 1;
                while (frs.next()) {
                    if (pos == 1) {
                        try { nomefia1 = frs.getString("f_nome"); } catch (SQLException e) {}
                        try { telfia1 = frs.getString("f_tel"); } catch (SQLException e) {}
                    } else {
                        try { nomefia2 = frs.getString("f_nome"); } catch (SQLException e) {}
                        try { telfia2 = frs.getString("f_tel"); } catch (SQLException e) {}
                    }
                    pos += 1;
                }
                frs.close();
                
                String dtinicio = null; String dtfim = null; String dtadito = null;
                String cartSQL = "SELECT TO_CHAR(dtinicio, 'DD-MM-YYYY') dtinicio, TO_CHAR(dtfim, 'DD-MM-YYYY') dtfim, TO_CHAR(dtaditamento, 'DD-MM-YYYY') dtaditamento FROM carteira WHERE contrato = ? LIMIT 1;";
                ResultSet crs = conn.AbrirTabela(cartSQL, ResultSet.CONCUR_READ_ONLY, new Object[][] {{"string", contrato}});
                
                while (crs.next()) {
                    try { dtinicio = crs.getString("dtinicio"); } catch (SQLException e) {}
                    try { dtfim = crs.getString("dtfim"); } catch (SQLException e) {}
                    try { dtadito = crs.getString("dtaditamento"); } catch (SQLException e) {}
                }
                crs.close();
                
                new Multas(rgprp, rgimv);
                double multa = 0;
                if (tipoimv.equalsIgnoreCase("RES")) {
                    multa = VariaveisGlobais.mu_res;
                } else {
                    multa = VariaveisGlobais.mu_com;
                }
                
                double comissao = VariaveisGlobais.co;
                
                rows.add(new classFicha(rgprp, nomeprop, cpfcnpjprop, rgimv, contrato, nomeloca, cpfcnpjloca, endimv, telprop, nomefia1, telfia1, nomefia2, telfia2, obsloca, dtinicio, dtfim, dtadito, multa, comissao));
            }
        } catch (Exception e) {}
        try { rs.close(); } catch (SQLException e) {}
        
        ObservableList<classFicha> data = FXCollections.observableArrayList(rows);
        if (!rows.isEmpty()) {
            listaLoca.setItems(data);
        }
        listaLoca.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        FilteredList<classFicha> filteredData = new FilteredList<>(data, e -> true);
        busca.setOnKeyReleased(e ->{
            busca.textProperty().addListener((observableValue, oldValue, newValue) ->{
                filteredData.setPredicate((Predicate<? super classFicha>) user->{
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if(user.getContrato().contains(newValue)){
                        return true;
                    }else if(user.getNome_loca().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }else {
                        boolean ret = true;
                        if (user.getCpfcnpj_loca() != null) {
                            ret = user.getCpfcnpj_loca().toLowerCase().contains(lowerCaseFilter);
                        } else ret = false;
                        return ret;
                    }
                });
            });
            SortedList<classFicha> sortedData = new SortedList<>(filteredData);
            listaLoca.setItems(sortedData);
        });
    }
}
