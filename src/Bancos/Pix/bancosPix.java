package Bancos.Pix;

import Funcoes.DbMain;
import Funcoes.VariaveisGlobais;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class bancosPix {
    DbMain conn = VariaveisGlobais.conexao;

    private SimpleIntegerProperty id;
    private SimpleStringProperty tipo;
    private SimpleStringProperty chave;
    private SimpleStringProperty banco;
    private SimpleIntegerProperty nnumero;

    public bancosPix() { }

    private bancosPix(int id, String tipo, String chave, String banco, int nnumero) {
        this.id = new SimpleIntegerProperty(id);
        this.tipo = new SimpleStringProperty(tipo);
        this.chave = new SimpleStringProperty(chave);
        this.banco = new SimpleStringProperty(banco);
        this.nnumero = new SimpleIntegerProperty(nnumero);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getTipo() { return tipo.get(); }
    public SimpleStringProperty tipoProperty() { return tipo; }
    public void setTipo(String tipo) { this.tipo.set(tipo); }

    public String getChave() { return chave.get(); }
    public SimpleStringProperty chaveProperty() { return chave; }
    public void setChave(String chave) { this.chave.set(chave); }

    public String getBanco() { return banco.get(); }
    public SimpleStringProperty bancoProperty() { return banco; }
    public void setBanco(String banco) { this.banco.set(banco); }

    public int getNnumero() { return nnumero.get(); }
    public SimpleIntegerProperty nnumeroProperty() { return nnumero; }
    public void setNnumero(int nnumero) { this.nnumero.set(nnumero); }

    @Override
    public String toString() {
        return banco.get();
    }

    public bancosPix[] LerBancos() {
        /**
         * Checa se a tabela existe dentro do banco de dados, caso não exista cia-o.
         */
        BancoPixStructure();

        bancosPix[] retorno = {};
        String selecSQL = "SELECT id, tipo, chave, banco, nnumero FROM bancos_pix ORDER BY id;";
        ResultSet pixbancos = conn.AbrirTabela(selecSQL,ResultSet.CONCUR_READ_ONLY);
        try {
            while (pixbancos.next()) {
                int pid = -1; pid = pixbancos.getInt("id");
                String ptipo = null; ptipo = pixbancos.getString("tipo");
                String pchave = null; pchave = pixbancos.getString("chave");
                String pbanco = null; pbanco = pixbancos.getString("banco");
                int pnnumero = -1; pnnumero = pixbancos.getInt("nnumero");

                retorno = PixsAdd(retorno, new bancosPix(pid, ptipo, pchave, pbanco, pnnumero));
            }
        } catch (SQLException sqlException) {}
        DbMain.FecharTabela(pixbancos);
        return retorno;
    }

    private bancosPix[] PixsAdd(bancosPix[] mArray, bancosPix value) {
        bancosPix[] temp = new bancosPix[mArray.length + 1];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        temp[temp.length - 1] = value;
        return temp;
    }

    public void BancoPixStructure() {
        String createSQL = "CREATE TABLE IF NOT EXISTS bancos_pix (" +
                "id integer NOT NULL DEFAULT nextval('bancos_pix_id_seq'::regclass), " +
                "tipo character varying(10), " +
                "chave character varying(45), " +
                "banco character varying(45), " +
                "nnumero integer DEFAULT 0, " +
                "CONSTRAINT bancos_pix_pkey PRIMARY KEY (id)) " +
                "WITH (OIDS=FALSE); " +
                "ALTER TABLE public.bancos_pix OWNER TO postgres;";
        try { conn.ExecutarComando(createSQL); } catch (Exception e) {}
        return;
    }
}
