package Editor.activetree;

import Funcoes.DbMain;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import exemplos.CapituleFunction;
import exemplos.CondicaoFunction;
import exemplos.ExtensoFunction;
import exemplos.FormatFunction;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionException;
import net.sourceforge.jeval.function.FunctionHelper;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Comparator;

public class ProcessaTexto {
    DbMain conn = VariaveisGlobais.conexao;

    private String l_rgprp;
    private String l_rgimv;
    private String l_contrato;
    private String l_texto;

    public void setL_rgprp(String l_rgprp) {
        this.l_rgprp = l_rgprp;
    }

    public void setL_rgimv(String l_rgimv) {
        this.l_rgimv = l_rgimv;
    }

    public void setL_contrato(String l_contrato) {
        this.l_contrato = l_contrato;
    }

    public void setL_texto(String l_texto) {
        this.l_texto = l_texto;
    }

    InputStream l_targetStream;

    public InputStream getL_targetStream() {
        return l_targetStream;
    }

    public ProcessaTexto() {
        Processa();
    }

    public ProcessaTexto(String rgprp, String rgimv, String contrato, String textoContrato) {
        this.l_rgprp = rgprp;
        this.l_rgimv = rgimv;
        this.l_contrato = contrato;
        this.l_texto = textoContrato;
        Processa();
    }

    public void Processa() {
        Object[][] dbTabFields = {};
        String mField = ""; boolean getCaracter = false;
        String oldcaracter = "";
        for (int ch=0; ch < this.l_texto.length(); ch++) {
            String caracter = this.l_texto.substring(ch,ch+1);
            if (caracter.equalsIgnoreCase("$")) {
                // Abre Campo
                getCaracter = false;
                mField = caracter;
                oldcaracter = caracter;
                continue;
            }
            if (oldcaracter.equalsIgnoreCase("$") && caracter.equalsIgnoreCase("<")) {
                // Abre Campo
                getCaracter = true;
                mField += caracter;
                oldcaracter = "";
                continue;
            }
            if (caracter.equalsIgnoreCase(">")) {
                getCaracter = false;
                mField += caracter;
                oldcaracter = caracter;
                continue;
            }
            if (oldcaracter.equalsIgnoreCase(">") && caracter.equalsIgnoreCase("[")) {
                getCaracter = true;
                mField += caracter;
                oldcaracter = caracter;
                continue;
            }
            if (oldcaracter.equalsIgnoreCase("[") && caracter.equalsIgnoreCase("]")) {
                getCaracter = false;
                mField += caracter;

                String _table = ""; String _field = ""; int _pos = -1;
                _table = mField.substring(2, mField.indexOf("."));
                _field = mField.substring(mField.indexOf(".") + 1, mField.indexOf(">"));
                if (mField.indexOf("[") > -1) {
                    String value = mField.substring(mField.indexOf("[") + 1);
                    value = value.substring(0,value.length() - 1);
                    _pos = Integer.valueOf(value);
                }

                int _posSubArray = FuncoesGlobais.FindinObject(dbTabFields,0,_table);
                if (_posSubArray == -1) {
                    Object[][] _subFieldValue = {{_field, _pos}};
                    Object[] _subField = {_table, _subFieldValue};
                    dbTabFields = FuncoesGlobais.ObjectsAdd(dbTabFields, _subField);
                } else {
                    Object[] _subField = (Object[]) dbTabFields[_posSubArray];
                    Object[] _subFieldValue = (Object[]) _subField[1];
                    _subFieldValue = FuncoesGlobais.ObjectsAdd(_subFieldValue, new Object[]{_field, _pos});
                    _subField[1] = _subFieldValue;
                    dbTabFields[_posSubArray] = _subField;
                }
                //System.out.println(mField);
                mField = ""; oldcaracter = "";
                continue;
            }

            if (oldcaracter.equalsIgnoreCase(">") && !caracter.equalsIgnoreCase("[")) {
                getCaracter = false;

                String _table = ""; String _field = ""; int _pos = -1;
                int _dotpos = mField.indexOf(".");
                if (_dotpos > -1) {
                    _table = mField.substring(2, _dotpos);
                    _field = mField.substring(mField.indexOf(".") + 1, mField.indexOf(">"));
                    if (mField.indexOf("[") > -1) {
                        String value = mField.substring(mField.indexOf("[") + 1);
                        value = value.substring(0, value.length() - 1);
                        _pos = Integer.valueOf(value);
                    }

                    int _posSubArray = FuncoesGlobais.FindinObject(dbTabFields, 0, _table);
                    if (_posSubArray == -1) {
                        Object[][] _subFieldValue = {{_field, _pos}};
                        Object[] _subField = {_table, _subFieldValue};
                        dbTabFields = FuncoesGlobais.ObjectsAdd(dbTabFields, _subField);
                    } else {
                        Object[] _subField = (Object[]) dbTabFields[_posSubArray];
                        Object[] _subFieldValue = (Object[]) _subField[1];
                        _subFieldValue = FuncoesGlobais.ObjectsAdd(_subFieldValue, new Object[]{_field, _pos});
                        _subField[1] = _subFieldValue;
                        dbTabFields[_posSubArray] = _subField;
                    }
                }
                //System.out.println(mField);
                mField = ""; oldcaracter = "";
                continue;
            }
            if (getCaracter) mField += caracter;
        }

        Arrays.sort(dbTabFields, (Comparator<Object[]>) (o1, o2) -> {
            final String tabela1 = (String)o1[0];
            final String tabela2 = (String)o2[0];
            return tabela1.compareTo(tabela2);
        });

        String[][] erros = {};
        for (Object[] stbl : dbTabFields) {
            Object[] tbl = stbl;
            String tsql = ""; boolean isProgField = true;
            if (((String)tbl[0]).toUpperCase().contains("PROPRIETARIOS")) {
                tsql = "SELECT * FROM proprietarios WHERE p_rgprp = '%s';";
                tsql = String.format(tsql, this.l_rgprp);
            } else if (((String)tbl[0]).toUpperCase().contains("IMOVEIS")) {
                tsql = "SELECT * FROM imoveis WHERE i_rgimv = '%s';";
                tsql = String.format(tsql, this.l_rgimv);
            } else if (((String)tbl[0]).toUpperCase().contains("LOCATARIOS")) {
                tsql = "SELECT * FROM prelocatarios WHERE l_contrato = '%s';";
                tsql = String.format(tsql, this.l_contrato);
            } else if (((String)tbl[0]).toUpperCase().contains("CARTEIRA")) {
                tsql = "SELECT * FROM carteira WHERE contrato = '%s';";
                tsql = String.format(tsql, this.l_contrato);
            } else if (((String)tbl[0]).toUpperCase().contains("FIADOR")) {
                tsql = "SELECT * FROM fiadores WHERE f_contrato = '%s';";
                tsql = String.format(tsql, this.l_contrato);
            } else if(((String)tbl[0]).toUpperCase().contains("SOCIOS")) {
                tsql = "SELECT * FROM socios WHERE s_contrato= '%s';";
                tsql = String.format(tsql, this.l_contrato);
            } else if (((String)tbl[0]).toUpperCase().contains("ADCLOCATARIOS")) {
                tsql = "SELECT * FROM adclocatarios WHERE l_contrato= '%s' ORDER BY l_id, l_idloca;";
                tsql = String.format(tsql, this.l_contrato);
            }

            Object sftbl = tbl[1];
            Object[] _campos = (Object[]) sftbl;
            for (Object _field : _campos) {
                Object[] aaa = (Object[]) _field;
                String _f1 = (String) aaa[0];
                int _f2 = (int) aaa[1];
                ResultSet rs = conn.AbrirTabela(tsql, ResultSet.CONCUR_READ_ONLY);
                try {
                    int _indice = 0;
                    while (rs.next()) {
                        String _regex = "$<" + (String) tbl[0] + "." + _f1 + ">";
                        if (_f2 == -1) {
                            this.l_texto = this.l_texto.replace(_regex, rs.getString(_f1));
                            this.l_texto = this.l_texto.replace(_regex.substring(1), rs.getString(_f1));
                        } else {
                            if (_indice == Integer.valueOf(_f2)) {
                                this.l_texto = this.l_texto.replace(_regex + "[" + _f2 + "]", rs.getString(_f1));
                                this.l_texto = this.l_texto.replace(_regex.substring(1) + "[" + _f2 + "]", rs.getString(_f1));
                            }
                            _indice += 1;
                        }
                    }
                } catch (Exception sqlException) {
                    sqlException.printStackTrace();
                }
                DbMain.FecharTabela(rs);
            }
        }

        Object[] dbMacroFields = {};
        String macroField = ""; getCaracter = false;
        int parenteses = -1; String caracter = "";
        for (int ch=0; ch < this.l_texto.length(); ch++) {
            caracter = this.l_texto.substring(ch, ch + 1);
            if (caracter.equalsIgnoreCase("$")) {
                getCaracter = true;
                macroField = "";
                parenteses = -1;
                //continue;
            }

            if (caracter.equalsIgnoreCase("(")) {
                if (parenteses == -1) parenteses = 0;
                parenteses += 1;
            }
            if (caracter.equalsIgnoreCase(")")) {
                parenteses -= 1;
            }
            if (parenteses == 0) {
                macroField += caracter;
                getCaracter = false;

                Object _field = macroField;
                if (_field.toString().trim().length() != 0)
                    dbMacroFields = FuncoesGlobais.ObjectsAdd(dbMacroFields, _field);
                //macroField = "";
                oldcaracter = "";
                parenteses = -1;
                continue;
            }
            if (getCaracter) macroField += caracter;
            oldcaracter = caracter;
        }

        Arrays.sort(dbMacroFields, (Comparator<Object>) (o1, o2) -> {
            final String tabela1 = (String)o1;
            final String tabela2 = (String)o2;
            return tabela1.compareTo(tabela2);
        });

        Evaluator evaluator = new Evaluator();
        evaluator.putFunction(new CapituleFunction());
        evaluator.putFunction(new ExtensoFunction());
        evaluator.putFunction(new FormatFunction());
        evaluator.putFunction(new CondicaoFunction());

        String[] Erros = {};
        for (Object macro : dbMacroFields) {
            try {
                String newField = null;
                newField = evaluator.evaluate(macro.toString().replace("$",""));
                newField = FunctionHelper.trimAndRemoveQuoteChars(newField, evaluator.getQuoteCharacter());
                if (newField != null) this.l_texto = this.l_texto.replace((String)macro, newField);
            } catch (EvaluationException e) {
                Erros = FuncoesGlobais.ArrayAdd(Erros, "Erro na expressão " + macro.toString() + "\n");
                //e.printStackTrace();
            } catch (FunctionException e) {
                e.printStackTrace();
            }
        }
        evaluator.clearFunctions();

        if (Erros.length > 0) {
            String msage = "";
            for (String s : Erros) {
                msage += s;
            }
            JOptionPane.showMessageDialog(null, msage, "Aten��o!!!", JOptionPane.INFORMATION_MESSAGE);
        }

        l_targetStream = IOUtils.toInputStream(this.l_texto);
    }
}
