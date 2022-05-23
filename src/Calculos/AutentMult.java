package Calculos;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AutentMult {
    DbMain conn = VariaveisGlobais.conexao;

    public String MontaAutInicial(String rgprp, String rgimv) {
        String retorno = "{" + rgprp + ",null,null,\"\"}, ";
        String sql = "SELECT rgprp_dv FROM dividir WHERE rgprp = '%s' AND rgimv = '%s';";
        sql = String.format(sql, rgprp, rgimv);
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);

        try {
            while (ars.next()) {
                retorno += "{" + ars.getString("rgprp_dv") + ",null,null,\"\"}" + ", ";
            }
            retorno = retorno.substring(0, retorno.length() - 2);
            retorno = "{" + retorno + "}";
        } catch (Exception e) {}
        DbMain.FecharTabela(ars);

        return retorno;
    }

    public Object[][] PegaAutentMult_Mov(String rgprp, String rgimv, String table, String dtvecto, String refer) {
        String sql = "SELECT aut_pag FROM %s WHERE rgprp = '%s' AND rgimv = '%s' AND aut_rec <> 0 AND dtvencimento = '%s' AND referencia = '%s' LIMIT 1;";
        sql = String.format(sql,
                table,
                rgprp,
                rgimv,
                dtvecto,
                refer
                           );
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Object[][] retorno = null; boolean temreg = false;
        try {
            while (ars.next()) {
                temreg = true;
                String[][] vetor = (String[][]) ars.getArray("aut_pag").getArray();
                retorno = Convert2ObjectArrays(vetor);
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(ars);

        if (temreg && retorno == null) retorno = ConvertArrayString2ObjectArrays(MontaAutInicial(rgprp, rgimv));
        return retorno;
    }

    public Object[][] PegaAutentMult_Taxas(String rgprp, String rgimv, String table, String refer) {
        String sql = "SELECT aut_pag FROM %s WHERE rgprp = '%s' AND rgimv = '%s' AND aut_rec <> 0 AND referencia = '%s' LIMIT 1;";
        sql = String.format(sql,
                table,
                rgprp,
                rgimv,
                refer
        );
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Object[][] retorno = null; boolean temreg = false;
        try {
            while (ars.next()) {
                temreg = true;
                String[][] vetor = (String[][]) ars.getArray("aut_pag").getArray();
                retorno = Convert2ObjectArrays(vetor);
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(ars);

        if (temreg && retorno == null) retorno = ConvertArrayString2ObjectArrays(MontaAutInicial(rgprp, rgimv));
        return retorno;
    }

    public String MontaAutInicial_Avi(String registro) {
        String retorno = "{" + registro + ",null, null, null}";
        return retorno;
    }

    public Object[][] PegaAutentMult_Avi(String registro, String table, Integer autrec) {
        String sql = "SELECT aut_pag FROM %s WHERE aut_rec = %s LIMIT 1;";
        sql = String.format(sql,
                table,
                autrec
        );
        ResultSet ars = conn.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        Object[][] retorno = null; boolean temreg = false;
        try {
            while (ars.next()) {
                temreg = true;
                String[][] vetor = (String[][]) ars.getArray("aut_pag").getArray();
                retorno = Convert2ObjectArrays(vetor);
            }
        } catch (Exception e) {}
        DbMain.FecharTabela(ars);

        if (temreg && retorno == null) retorno = ConvertArrayString2ObjectArrays_Avi(MontaAutInicial_Avi(registro));
        return retorno;
    }

    private Object[][] ConvertArrayString2ObjectArrays_Avi(String value) {
        Object[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ObjectsAdd(retorno,
                    new Object[]{
                            Integer.valueOf(vtr[0].trim()),
                            vtr[1].trim(),
                            vtr[2].trim(),
                            vtr[3].trim()
                    });
        }
        return retorno;
    }

    public Object[][] UpgradeAutent_Avi(Object[][] value, String rgprp, int aut, String dtpato, String user) {
        int pos = FuncoesGlobais.FindinObject(value,0,Integer.valueOf(rgprp));
        Object[][] retorno = (Object[][]) value;
        if (pos > -1) {
            retorno[pos][1] = aut;
            retorno[pos][2] = dtpato;
            retorno [pos][3] = user;
        }
        return value;
    }

    private Object[][] Convert2ObjectArrays(String[][] value) {
        Object[][] retorno = {};
        for(String[] vetor : value) {
            retorno = FuncoesGlobais.ObjectsAdd(retorno,
                    new Object[] {
                            Integer.valueOf(vetor[0].trim()),
                            ReturnValueOrNull(vetor[1]),
                            ReturnStringOrNull(vetor[2]),
                            vetor[3]
            });
        }
        return retorno;
    }

    private Object ReturnValueOrNull(Object value) {
        Object retorno = null;
        try {
            retorno = value.toString().trim().equalsIgnoreCase("null") ? null : Integer.valueOf(value.toString().trim());
        } catch (Exception e) {}
        return retorno;
    }

    private Object ReturnStringOrNull(Object value) {
        Object retorno = null;
        try {
            retorno = value.toString().trim().equalsIgnoreCase("null") ? null : value.toString().trim();
        } catch (Exception e) {}
        return retorno;
    }

    private Object[][] ConvertArrayString2ObjectArrays(String value) {
        Object[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ObjectsAdd(retorno,
                    new Object[]{
                            Integer.valueOf(vtr[0].trim()),
                            vtr[1].trim().equalsIgnoreCase("null") ? null : Integer.valueOf(vtr[1].trim()),
                            vtr[2].trim().equalsIgnoreCase("null") ? null : vtr[2].trim(),
                            vtr[3].trim().replace("\"","")
                    });
        }
        return retorno;
    }

    public Object[][] UpgradeAutent(Object[][] value, String rgprp, int aut, String dtpato, String user) {
        int pos = FuncoesGlobais.FindinObject(value,0,Integer.valueOf(rgprp));
        Object[][] retorno = (Object[][]) value;
        if (pos > -1) {
            retorno[pos][1] = aut;
            retorno[pos][2] = dtpato;
            retorno [pos][3] = user;
        }
        return value;
    }

    public String ObjectArrays2String(Object[][] value) {
        String retorno = Arrays.deepToString(value);
        retorno = retorno.replace("[","{");
        retorno = retorno.replace("]","}");
        retorno = retorno.replace(" }","\"\"}");
        return retorno;
    }

    public int CountAutent(Object[][] value) {
        return value.length;
    }

    // ----[Reservas]-------------------------------------------------
    public Object[][] PegaReserva_Mov(ResultSet rs) {
        Object[][] retorno = null; boolean temreg = false;
        try {
            temreg = true;
            String[][] vetor = (String[][]) rs.getArray("reserva").getArray();
            retorno = Convert2ObjectArrays(vetor);
        } catch (Exception e) {}

        if (temreg && retorno == null) retorno = ReservaConvertArrayString2ObjectArrays(ReservaMontaAutInicial());
        return retorno;
    }

    public String ReservaMontaAutInicial() {
        String retorno = "{{" + VariaveisGlobais.usuario + "," + new SimpleDateFormat("dd-MM-yyyy").format(DbMain.getDateTimeServer()) + "}}";
        return retorno;
    }

    public Object[][] ReservaConvertArrayString2ObjectArrays(String value) {
        Object[][] retorno = {};

        // Fase 1 - Remoção dos Bracetes da matriz principal {}
        // Remove bracete inicial '{'
        value = value.substring(1);
        // Remove bracete final '}'
        value = value.substring(0,value.length() - 1);

        // Fase 2 - Converter em array
        String[] value2 = value.replace("{","").substring(0,value.replace("{","").length() - 1).split("},");

        // Fase 3 - Montar array Object[][]
        for (String vetor : value2) {
            String[] vtr = vetor.split(",");
            retorno = FuncoesGlobais.ObjectsAdd(retorno,
                    new Object[]{
                            vtr[0].trim().equalsIgnoreCase("null") ? null : vtr[0].trim(),
                            vtr[1].trim().replace("\"","")
                    });
        }
        return retorno;
    }
}
