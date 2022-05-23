package Funcoes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author wellspinto@gmail.com
 *
 * Rotinas de manipulação de Banco de Dados mySQL
 */
public class DbMain {

    public Connection conn = null;
    private String hostName = "127.0.0.1";
    private String userName = "postgres";
    private String password = "7kf51b"; 
    private String url = null;
    private String jdbcDriver = null;
    private String dataBaseName = null;
    private String dataBasePrefix = null;
    private String dabaBasePort = null;

    private final String mdbConect = "";
    private final String mdbODBC = "";
    
    public DbMain(String host, String user, String passwd, String databasename) {        
        jdbcDriver = "org.postgresql.Driver";
        hostName = host;
        userName = user;
        password = passwd;
        
        dataBaseName = databasename;
        dataBasePrefix = "jdbc:postgresql://";
        dabaBasePort = "5432";

        if ("".equals(host.trim()) && "".equals(user.trim()) && "".equals(passwd.trim()) && !"".equals(databasename.trim())) {
            jdbcDriver = mdbODBC;
            url = mdbConect + databasename.trim();
            userName = "";
            password = "";
        } else {
            url = dataBasePrefix + hostName + ":"+dabaBasePort+"/" + dataBaseName +
                  "?useUnicode=true&characterEncoding=utf8";
        }

        AbrirConexao();
    }
    
    /* Abrir Banco de Dados
     * wellspinto@gmail.com
     * 12/01/2011
     */
    public Connection AbrirConexao(){
        try {
            if (conn == null) {
                Class.forName(jdbcDriver);
                Properties props = new Properties();
                props.setProperty("user",userName);
                props.setProperty("password",password);
                props.setProperty("loginTimeout","5");
                props.setProperty("socketTimeout","5");


                conn = DriverManager.getConnection(url, props);
            } else if (conn.isClosed()) {
                conn = null;
                return AbrirConexao();
            }
        } catch (SQLException | ClassNotFoundException e) {
            //JOptionPane.showMessageDialog(null, "Unidade OffLine!!!\nTente novamente...", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
            //System.exit(0);
            FecharConexao();
            conn = null;
        }
        return conn;
    }

    /**
    * Fecha a conexão com BD.
    *
    */
    public void FecharConexao() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Abrir Tabela de dados
     */
    public ResultSet AbrirTabela(String sqlString, int iTipo) {
        ResultSet hResult = null;
        Connection connectionSQL = this.conn;
        Statement stm = null;

        try {
            stm = connectionSQL.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, iTipo); 
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            hResult = stm.executeQuery(sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hResult;
    }

    public ResultSet AbrirTabela(String sqlString, int iTipo, Object[][] param) {
        ResultSet hResult = null;
        Connection connectionSQL = this.conn;

        if (param.length <= 0) {
            Statement stm = null;
            try {
                stm = connectionSQL.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, iTipo);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                hResult = stm.executeQuery(sqlString);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            PreparedStatement stm = null;
            try {
                stm = connectionSQL.prepareStatement(sqlString, ResultSet.TYPE_SCROLL_INSENSITIVE, iTipo);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                for (int i = 0; i <= param.length - 1; i++) {
                    if (param[i][0].equals("int")) {
                        stm.setInt(i + 1, (int) param[i][1]);
                    } else if (param[i][0].equals("bigint")) {
                        stm.setObject(i + 1, (BigInteger) param[i][1], Types.BIGINT);
                    } else if (param[i][0].equals("date")) {
                        stm.setDate(i + 1, (Date) param[i][1]);
                    } else if (param[i][0].equals("string")) {
                        stm.setString(i + 1, (String) param[i][1]);
                    } else if (param[i][0].equals("decimal")) {
                        stm.setBigDecimal(i + 1, (BigDecimal) param[i][1]);
                    } else if (param[i][0].equals("boolean")) {
                        stm.setBoolean(i + 1, (Boolean) param[i][1]);
                    } else if (param[i][0].equals("float")) {
                        stm.setFloat(i + 1, (Float) param[i][1]);
                    } else if (param[i][0].equals("double")) {
                        stm.setDouble(i + 1, (Double) param[i][1]);
                    } else if (param[i][0].equals("array")) {
                        stm.setArray(i + 1, (Array) param[i][1]);
                    } else if (param[i][0].equals("int")) {
                        stm.setInt(i + 1, (int) param[i][1]);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                hResult = stm.executeQuery();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return hResult;
    }

    /**
     *
     * @param hResult
     */
    public static void FecharTabela(ResultSet hResult) {
        try {
            hResult.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Abrir Tabela de dados
     */
    public int ExecutarComando(String sqlString) {
        int hRetorno = 0;
        Connection connectionSQL = this.conn;
        Statement stm = null;
        try {
            stm = connectionSQL.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            hRetorno = stm.executeUpdate(sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hRetorno;
    }

    public int ExecutarComando(String sqlString, Object[][] param) {
        int hRetorno = 0;
        Connection connectionSQL = this.conn;

        if (param.length <= 0) {
            Statement stm = null;
            try {
                stm = connectionSQL.createStatement();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                hRetorno = stm.executeUpdate(sqlString);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            PreparedStatement stm = null;
            try {
                stm = connectionSQL.prepareStatement(sqlString);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                for (int i = 0; i <= param.length - 1; i++) {
                    if (param[i][0].equals("int")) {
                        stm.setInt(i + 1, (int) param[i][1]);
                    } else if (param[i][0].equals("bigint")) {
                        stm.setObject(i + 1, (BigInteger) param[i][1], Types.BIGINT);
                    } else if (param[i][0].equals("date")) {
                        stm.setDate(i + 1, (Date) param[i][1]);
                    } else if (param[i][0].equals("string")) {
                        stm.setString(i + 1, (String) param[i][1]);
                    } else if (param[i][0].equals("decimal")) {
                        stm.setBigDecimal(i + 1, (BigDecimal) param[i][1]);
                    } else if (param[i][0].equals("boolean")) {
                        stm.setBoolean(i + 1, (Boolean) param[i][1]);
                    } else if (param[i][0].equals("float")) {
                        stm.setFloat(i + 1, (Float) param[i][1]);
                    } else if (param[i][0].equals("double")) {
                        stm.setDouble(i + 1, (Double) param[i][1]);
                    } else if (param[i][0].equals("array")) {
                        stm.setArray(i + 1, (Array) param[i][1]);
                    } else if (param[i][0].equals("int")) {
                        stm.setInt(i + 1, (int) param[i][1]);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                hRetorno = stm.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return hRetorno;
    }

    /**
     * LerParametros
     */
    public String LerParametros(String cVar) throws SQLException {
        String rVar = null;

        ResultSet hResult = AbrirTabela("SELECT campo, conteudo, tipo FROM PARAMETROS WHERE LOWER(TRIM(campo)) = '" + cVar.toLowerCase().trim() + "';", ResultSet.CONCUR_READ_ONLY);

        if (hResult.first()) {
            rVar = hResult.getString("conteudo");
        }

        return rVar;
    }

    /**
     * GravarParametros
     */
    public boolean GravarParametros(String cVar[]) throws SQLException {
        boolean rVar = false;
        boolean bInsert = false;
        String sql = "";

        bInsert = (LerParametros(cVar[0]) == null);
        if (bInsert) {
            sql = "INSERT INTO PARAMETROS (campo, conteudo, tipo) VALUES ('" + cVar[0] + "','" + cVar[1] + "','" + cVar[2] + "')";
        } else {
            sql = "UPDATE PARAMETROS SET CONTEUDO = '" + cVar[1] + "' WHERE CAMPO = '" + cVar[0] + "';";
        }

        rVar = (ExecutarComando(sql)) > 0;
        return rVar;
    }

    public boolean GravarMultiParametros(String cVar[][]) throws SQLException {
        boolean bInsert = false;
        int i = 0; int nVar = 0;

        for (i=0;i<=cVar.length - 1;i++) {
            String sql = "";

            if (!"".equals(cVar[i][0])) {
                bInsert = (LerParametros(cVar[i][0]) == null);
                if (bInsert) {
                    sql = "INSERT INTO PARAMETROS (campo, tipo, conteudo) VALUES ('" + cVar[i][0] + "','" + cVar[i][1] + "','" + cVar[i][2] + "')";
                } else {
                    sql = "UPDATE PARAMETROS SET CONTEUDO = '" + cVar[i][2] + "' WHERE CAMPO = '" + cVar[i][0] + "';";
                }

                nVar += ExecutarComando(sql);
            }
        }
        return (nVar > 0);
    }

    public static int RecordCount(ResultSet hrs) {
        
        int retorno = 0;
        try {
            int pos = hrs.getRow();
            hrs.last();
            retorno = hrs.getRow();
            hrs.beforeFirst();
            if (pos > 0) hrs.absolute(pos);
        } catch (SQLException e) {retorno = 0;}
        return retorno;
    }    
    
    public boolean ExistTable(String TableName) throws SQLException {
        ResultSet tbl = AbrirTabela("select * from pg_tables where schemaname='public' and tablename LIKE '" + TableName + "';", ResultSet.CONCUR_READ_ONLY);
        tbl.last();
        boolean retorno = tbl.getRow() > 0;
        tbl.beforeFirst();
        FecharTabela(tbl);
        return retorno;
    }
    
    public void Auditor(String cVelho, String cNovo) {
        if (!ExisteTabelaAuditor()) return;
        
        try {
            ExecutarComando("INSERT INTO auditor (usuario, datahora, origem, maquina, velho, novo) VALUES ('" +
            VariaveisGlobais.usuario + "','" + Dates.DateFormata("yyyy-MM-dd hh:mm:ss", new java.util.Date()) +
            "','" + VariaveisGlobais.cargo + "','" + VariaveisGlobais.unidade + "','" +
            cVelho.toUpperCase() + "','" + cNovo.toUpperCase() + "')");
        } catch (Exception err) {}        
    }

    public Object[][] LerCamposTabela(String[] aCampos, String tbNome, String sWhere) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos + " FROM " + tbNome + " WHERE " + sWhere;
        ResultSet tmpResult = AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY);
        ResultSetMetaData md = tmpResult.getMetaData();
        Object[][] vRetorno = new Object[aCampos.length][4];
        int i = 0;

        if(tmpResult.first()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);

                // Trabala field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                }
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }

                // Debug
                //System.out.println(vRetorno[i][0] + "::" + vRetorno[i][1]);

                try {
                    switch (md.getColumnType(i + 1)) {
                        case Types.VARCHAR:
                            if (tmpResult.getString(variavel) != null) {
                                vRetorno[i][3] = tmpResult.getString(variavel);
                            } else vRetorno[i][3] = "";
                            break;
                        case Types.DATE:
                            vRetorno[i][3] = tmpResult.getDate(variavel);
                            break;
                        case Types.BOOLEAN:
                            vRetorno[i][3] = tmpResult.getBoolean(variavel);
                            break;
                        case Types.DECIMAL:
                            vRetorno[i][3] = tmpResult.getBigDecimal(variavel);
                            break;
                        case Types.INTEGER:
                            vRetorno[i][3] = tmpResult.getInt(variavel);
                            break;
                        default:
                            vRetorno[i][3] = tmpResult.getString(variavel);
                    }

                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        } else {
            vRetorno = null;
        }

        FecharTabela(tmpResult);

        return vRetorno;
    }

    public Object[][] LerCamposTabela(String[] aCampos, String tbNome, String sWhere, Object[][] param) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos + " FROM " + tbNome + " WHERE " + sWhere + " LIMIT 1";
        ResultSet tmpResult = AbrirTabela(sSql, ResultSet.CONCUR_READ_ONLY, param);
        ResultSetMetaData md = tmpResult.getMetaData();
        Object[][] vRetorno = new Object[aCampos.length][4];
        int i = 0;

        while (tmpResult.next()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);

                // Trabala field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                }
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }

                // Debug
                //System.out.println(vRetorno[i][0] + "::" + vRetorno[i][1]);

                try {
                    switch (md.getColumnType(i + 1)) {
                        case Types.VARCHAR:
                            if (tmpResult.getString(variavel) != null) {
                                vRetorno[i][3] = tmpResult.getString(variavel);
                            } else vRetorno[i][3] = "";
                            break;
                        case Types.DATE:
                            vRetorno[i][3] = tmpResult.getDate(variavel);
                            break;
                        case Types.BOOLEAN:
                            vRetorno[i][3] = tmpResult.getBoolean(variavel);
                            break;
                        case Types.DECIMAL:
                            vRetorno[i][3] = tmpResult.getBigDecimal(variavel);
                            break;
                        case Types.INTEGER:
                            vRetorno[i][3] = tmpResult.getInt(variavel);
                            break;
                        default:
                            vRetorno[i][3] = tmpResult.getString(variavel);
                    }

                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        }
        FecharTabela(tmpResult);

        return vRetorno;
    }

    public boolean ExisteTabelaBloquetos() {
        boolean ret = true;
        String sql = "CREATE TABLE `bloquetos` (\n" +
                "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                "  `contrato` varchar(6) DEFAULT NULL,\n" +
                "  `nome` varchar(70) DEFAULT NULL,\n" +
                "  `vencimento` datetime DEFAULT NULL,\n" +
                "  `valor` varchar(15) DEFAULT NULL,\n" +
                "  `nnumero` varchar(20) DEFAULT NULL,\n" +
                "  `remessa` varchar(1) DEFAULT 'N',\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1";

        try {
            if (!ExistTable("bloquetos")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}

        return ret;
    }

    private boolean ExisteTabelaAuditor() {
        boolean ret = true;
        String sql = "CREATE TABLE auditor " +
                "(" +
                "  id serial NOT NULL," +
                "  usuario character varying(25) NOT NULL," +
                "  datahora date," +
                "  velho character varying(255)," +
                "  novo character varying(255)," +
                "  origem character varying(30) NOT NULL," +
                "  maquina character varying(60) NOT NULL," +
                "  CONSTRAINT aditor_pkey PRIMARY KEY (id)" +
                ")" +
                "WITH (" +
                "  OIDS=FALSE " +
                "); " +
                "ALTER TABLE auditor" +
                "  OWNER TO postgres;";

        try {
            if (!ExistTable("auditor")) {
                ExecutarComando(sql);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public BigInteger PegarAutenticacao() {
        BigInteger aut = new BigInteger("0");
        ResultSet db = AbrirTabela("SELECT nextval('caixa_aut') AS aut;", ResultSet.CONCUR_READ_ONLY);
        try {
            if (db.next()) {
                aut = new BigInteger(db.getString("aut"));
            }
        } catch (Exception e) {}
        return aut;
    }

    public static String GeraLancamentosArray(String[][] lanctos) {
        String tArray = "";
        for (String[] valores : lanctos) {
            if (valores[0].equalsIgnoreCase("") && valores[1].equalsIgnoreCase("")) {
                // DN
                tArray += "{\"DN\"," + valores[5].replace(".","").replace(",",".") + ",\"\",\"\",\"\",\"\"},";
            } else if (valores[0].equalsIgnoreCase("") && !valores[1].equalsIgnoreCase("")) {
                // CH
                tArray += "{\"CH\"," + valores[5].replace(".","").replace(",",".") + ",\"" +  valores[3] + "\"," + "\"" + valores[2] + "\"," +
                         "\"" + valores[1] +"\",\"" + valores[4] + "\"},";
            } else {
                // BC
                tArray += "{\"BC\"," + valores[5].replace(".","").replace(",",".") + ",\"" + valores[0] + "\",\"\",\"\",\"\"},";
            }

/*
            System.out.println("Banco: " + valores[0]);
            System.out.println("Numero Banco: " + valores[1]);
            System.out.println("Agência: " + valores[2]);
            System.out.println("NCheque: " + valores[3]);
            System.out.println("Dt.Cheque: " + valores[4]);
            System.out.println("Vr.Lançado: " + valores[5]);
*/
        }
        return "{" + tArray.substring(0, tArray.length() - 1) + "}";
    }

    public static String GeraLctosArray(String[][] lanctos) {
        String retorno = "{";
        for (String[] item : lanctos) {
            String sItem = "{";
            for (int i = 0; i<= item.length - 1; i++) {
                if (i == 0) {
                    sItem += "\"" + item[i] + "\"" + ", ";
                } else {
                    String isEmpty = item[i].trim().equalsIgnoreCase("") ? "\"" : "";
                    sItem +=  isEmpty + item[i] + isEmpty + ", ";
                }
            }
            retorno += sItem.substring(0, sItem.length() - 2) + "}, ";
        }
        return retorno.substring(0, retorno.length() - 2) + "}";
    }

    public static Timestamp getDateTimeServer() {
//        return new Timestamp(System.currentTimeMillis());
        Timestamp datahora = null;
        String sql = "select current_timestamp datahora;";
        ResultSet rs = null;
        try {
            rs = VariaveisGlobais.conexao.AbrirTabela(sql, ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {}
        try {
            while (rs.next()) {
                datahora = rs.getTimestamp("datahora");
            }
        } catch (SQLException e) {datahora = new Timestamp(System.currentTimeMillis());}
        DbMain.FecharTabela(rs);
        return datahora;
    }
}
