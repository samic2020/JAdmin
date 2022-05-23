package Relatorios.Controle;

import Funcoes.Dates;
import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import pdfViewer.PdfViewer;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Controle implements Initializable {
    DbMain conn = VariaveisGlobais.conexao;

    @FXML private DatePicker iniPer;
    @FXML private DatePicker fimPer;
    @FXML private Button BtnPreview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iniPer.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        fimPer.setValue(Dates.toLocalDate(Dates.primDataMes(DbMain.getDateTimeServer())));
        BtnPreview.setOnAction(event -> {
            Listar(Dates.toDate(iniPer.getValue()), Dates.toDate(fimPer.getValue()));
        });
    }

    private void Listar(Date ini, Date fim) {
        List<cControle> lista = new ArrayList<>();

        String selectSQL = "select t.id, t.rgprp, t.rgimv, " +
                "(select i.i_end || ', ' || i.i_num || ' ' || i.i_cplto from imoveis i where i.i_rgimv = t.rgimv) endereco, " +
                "(select i.i_bairro from imoveis i where i.i_rgimv = t.rgimv) bairro, t.precampo, c.descricao, " +
                "t.poscampo, t.cota, t.valor, t.dtvencimento, t.dtrecebimento, t.referencia, t.tipo " +
                "from taxas t inner join campos c on c.codigo = t.campo where " +
                "t.extrato = false and t.retencao = false and " +
                "t.dtrecebimento between ? and ?;";
        ResultSet rs = conn.AbrirTabela(selectSQL,ResultSet.CONCUR_READ_ONLY, new Object[][]{
                {"date",Dates.toSqlDate(ini)},
                {"date", Dates.toSqlDate(fim)}
        });
        try {
            while (rs.next()) {
                int tid = -1; try { tid = rs.getInt("id"); } catch (SQLException sqlException) {}
                String trgprp = null; try { trgprp = rs.getString("rgprp"); } catch (SQLException sqlException) {}
                String trgimv = null; try { trgimv = rs.getString("rgimv"); } catch (SQLException sqlException) {}
                String tend = null; try { tend = rs.getString("endereco"); } catch (SQLException sqlException) {}
                String tbairro = null; try { tbairro = rs.getString("bairro"); } catch (SQLException sqlException) {}
                String tprec = null; try { tprec = rs.getString("precampo"); } catch (SQLException sqlException) {}
                String tdesc = null; try { tdesc = rs.getString("descricao"); } catch (SQLException sqlException) {}
                String tposc = null; try { tposc = rs.getString("poscampo"); } catch (SQLException sqlException) {}
                String tcota = null; try { tcota = rs.getString("cota"); } catch (SQLException sqlException) {}
                BigDecimal tvalor = null; try { tvalor = rs.getBigDecimal("valor"); } catch (SQLException sqlException) {}
                java.sql.Date tvenc = null; try { tvenc = rs.getDate("dtvencimento"); } catch (SQLException sqlException) {}
                java.sql.Date trecb = null; try { trecb = rs.getDate("dtrecebimento"); } catch (SQLException sqlException) {}
                String tref = null; try { tref = rs.getString("referencia"); } catch (SQLException sqlException) {}
                String ttipo = null; try { ttipo = rs.getString("tipo"); } catch (SQLException sqlException) {}

                cControle values = new cControle(tid, trgprp, trgimv, tend, tbairro, tprec, tdesc, tposc, tcota, tvalor, tvenc, trecb, tref, ttipo);
                lista.add(values);
            }
        } catch (SQLException e) {e.printStackTrace();}
        try {DbMain.FecharTabela(rs);} catch (Exception ex) {}

        Map parametros = new HashMap();
        parametros.put("dataIni", Dates.DateFormata("dd-MM-yyyy", ini));
        parametros.put("dataFin", Dates.DateFormata("dd-MM-yyyy", fim));

        String pdfName = new PdfViewer().GeraPDFTemp(lista,"Administrativas", parametros);
        new PdfViewer("Preview da Contas Administrativa Automáticas", pdfName);
    }

}
