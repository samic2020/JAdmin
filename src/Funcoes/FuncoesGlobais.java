/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import Classes.Taxas;
import Gerencia.divSec;

import javax.rad.genui.menu.UIMenu;
import java.text.Normalizer;
import java.util.Collections;
import java.util.*;
import java.util.regex.Pattern;
import samic.serversamic.Consulta;

/**
 *
 * @author supervisor
 */
public class FuncoesGlobais {

    public FuncoesGlobais() { }

    public static int FindinArrays(String[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            if (marray[i][coluna] != null) {
                if (marray[i][coluna].contains(oque)) {
                    achei = true;
                    break;
                }
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static boolean FindinArraysDup(String[][] marray, int coluna, String oque) {
        if (marray.length == 0) {return false;}
        int nocours = 0;
        for (int i=0;i<marray.length;i++) {
            if (marray[i][coluna].contains(oque)) {
                nocours++;
            }
        }
        return nocours > 1;
    }

    public static String[] ArrayAdd(String[] mArray, String value) {
        String[] temp = new String[mArray.length+1];

        System.arraycopy(mArray,0,temp,0,mArray.length);

        temp[mArray.length] = value;

        return temp;
    }

    public static String[] ArrayDel(String[] array, int index) {
            ArrayList list = CreateStringList(array);
            list.remove(index);
            return ConvertToStringArray(list);
    }

    public static String[][] ArraysAdd(String[][] mArray, String[] value) {
        String[][] temp = new String[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }

    public static String[][] ArrayDel(String[][] array, int index) {
        String[][] newArray = {};
        for (int i = 0; i <= array.length -1; i++) {
            if (i != index) {
                newArray = ArraysAdd(newArray,array[i]);
            }
        }
        return newArray;
    }

    public static UIMenu[] MenuArrayAdd(Object[] mArray, UIMenu value) {
        UIMenu[] temp = new UIMenu[mArray.length+1];

        System.arraycopy(mArray,0,temp,0,mArray.length);

        temp[mArray.length] = value;

        return temp;
    }


    public static int ClassIndexOf(Object[] value, Object search) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= value.length - 1; i++) {
            if (((Taxas) value[i]).getCodigo().equalsIgnoreCase((String)search)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;

    }


    public static int ObjIndexOf(Object aString[], Object sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].equals(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int FindLike(String aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (aString[i].contains(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static Object[][] ObjectsAdd(Object[][] mArray, Object[] value) {
        Object[][] temp = new Object[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }

    public static int FindinObject(Object[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray == null) return retorno;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            if (marray[i][coluna].equals(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int FindinObject(Object[][] marray, int coluna, Object oque) {
        int retorno = -1, i = 0;
        if (marray == null) return retorno;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            if (oque instanceof String) {
                if (marray[i][coluna] == oque) {
                    achei = true;
                    break;
                }
            } else if (oque instanceof Integer) {
                if (marray[i][coluna].equals(oque)) {
                    achei = true;
                    break;
                }
            } else if (oque instanceof Object) {
                if (marray[i][coluna] == String.valueOf(oque)) {
                    achei = true;
                    break;
                }
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static Object[] ObjectsAdd(Object[] mArray, Object value) {
        Object[] temp = new Object[mArray.length+1];
        System.arraycopy(mArray,0,temp,0,mArray.length);
        temp[mArray.length] = value;
        return temp;
    }

    public static Object[][] ObjectDel(Object[] array, int index) {
        Object[][] list = {};
        for (int i = 0; i <= array.length - 1; i++) {
            if (i != index) list = FuncoesGlobais.ObjectsAdd(list, (Object[])array[i]);
        }
        return list;
    }

    public static Object[] ObjectsRemoveDup(Object[] array) {
        ArrayList<Object> al = new ArrayList<Object>();
        for (Object ar : array) al.add(ar);

        for(int i=0;i<al.size();i++){
            for(int j=i+1;j<al.size();j++){
                if(al.get(i).equals(al.get(j))){
                    al.remove(j);
                    j--;
                }
            }
        }

        return al.toArray();
    }

    public static Object[] ObjectsOrdenaData(Object[] Vetor) {
        ArrayList<Date> arrayList = new ArrayList<Date>();
        for (Object ar : Vetor) arrayList.add((Date)ar);

        Collections.sort(arrayList, new Comparator<Date>() {
            @Override
            public int compare(Date s1, Date s2) {
                // compare the two strings if they lexicographically equal
                return s1.compareTo(s2);

            }
        });

        return arrayList.toArray();
    }

    public static ArrayList<String> CreateStringList(String ... values)
    {
        ArrayList<String> results = new ArrayList<String>();
        Collections.addAll(results, values);
        return results;
    }

    public static Object[] OrdenaMatriz(Object[] vetor) {
        ArrayList arrayList = new ArrayList();
        for (Object ar : vetor) arrayList.add(ar);

        Collections.sort(arrayList, new Comparator<Date>() {
            @Override
            public int compare(Date s1, Date s2) {
                return s1.compareTo(s2);

            }
        });
        return arrayList.toArray();
    }

    public static List OrdenaList(List vetor) {
        Collections.sort(vetor);
        return vetor;
    }

    public static String[] ConvertToStringArray(ArrayList list)
    {
        return (String[])list.toArray(new String[0]);
    }

    public static String join(String[] s, String delimiter) {
        if (s.length <= 0) return "";
        int i = 0;
        String sRet = "";
        for (i=0;i<=s.length - 1;i++) {
            sRet += s[i] + delimiter;
        }
        return sRet.substring(0, sRet.length() - delimiter.length());
    }

    public static String StrZero(String valor, int Tam) {
        String tmpValor = valor.replace(".0", "").replace(" ", "").replace(",", "");
        int i = 0; String zeros = Repete("0", Tam);
        String part1 = zeros + tmpValor;

        return part1.substring(part1.length() - Tam);
    }

    public static String IntToStrZero(int NumInteiro, int Tamanho) {
        String sNumInteiro = String.valueOf(NumInteiro);
        return StrZero(sNumInteiro,Tamanho);
    }

    public static String Repete(String texto, int length) {
        StringBuffer retorno = new StringBuffer();
        for (int i=1; i<=length;i++) {
            retorno.append(texto);
        }
        return retorno.toString();
    }

    public static String myLetra(String cword) {
        String iLetras = "à;è;ì;ò;ù;ã;õ;â;ê;î;ô;û;á;é;í;ó;ú;ä;ë;ï;ö;ü;ç;À;È;Ì;Ò;Ù;Ã;Õ;Â;Ê;Î;Ô;Û;Á;É;Í;Ó;Ú;Ä;Ë;Ï;Ö;Ü;Ç;:;¹;²;³;£;¢;¬;{;ª;º;°";
        String oLetras = "a;e;i;o;u;a;o;a;e;i;o;u;a;e;i;o;u;a;e;i;o;u;c;A;E;I;O;U;A;O;A;E;I;O;U;A;E;I;O;U;A;E;I;O;U;C; ;1;2;3;F;C; ; ;a;o;o";
        
        String[] aiLetras = iLetras.split(";"); String[] aoLetras = oLetras.split(";");
        for (int i=0;i<aiLetras.length;i++) {
            cword = cword.replace(aiLetras[i], aoLetras[i]);
        }
        
        return cword;
    }

    public static String Space(int value) {
        String espaco = "";
        for (int i=1;i<=value;i++) {
            espaco += " ";
        }
        return espaco;
    }

    static public String rmvLetras(String value) {
        String ret = "";
        for (int i=0; i<value.length();i++) {
            char letra = value.charAt(i);
            if (value.substring(i, i + 1).equalsIgnoreCase(":")) {
                //
            } else if ((int)letra < 48 || (int)letra > 57) {
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    static public String fmtNumero(String value) {
        String numero = "000000000000000";
        value = value.substring(0, value.indexOf(",") + 3);
        String saida = (numero + rmvNumero(value)).trim();
        return saida.substring(saida.length() - 15);
    }

    public static String rmvNumero(String value) {
        if (value == null) return "";
        String ret = "";
        for (int i=0;i<value.length();i++) {
            if (value.substring(i, i + 1).equalsIgnoreCase(".") || 
                value.substring(i, i + 1).equalsIgnoreCase("/") || 
                value.substring(i, i + 1).equalsIgnoreCase("-") || 
                value.substring(i, i + 1).equalsIgnoreCase(",") || 
                value.substring(i, i + 1).equalsIgnoreCase(" ") ||
                value.substring(i, i + 1).equalsIgnoreCase(")") || 
                value.substring(i, i + 1).equalsIgnoreCase("(")) {
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    static public String Valor4Boleta(String valor) {
        String valor1 = "0000000000" + valor.replace(" ", "").replace(",", "").replace(".", "").replace("-", "");
        return valor1.substring(valor1.length() - 10, valor1.length());
    }

    static public String addCota(String cota) {
        String ret = null;
        if (cota.length() == 5) {
            // Cota 99/99
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,5);
            if (checkCota(cota)) {
                ret = "01/" + part2;
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) + 1),2) + "/" + part2;
            }
        } else {
            // Parcela 99/9999
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,7);
            if (checkCota(cota)) {
                ret = "01/" + FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part2) + 1),4);
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) + 1),2) + "/" + part2;
            }
        }
        return ret;
    }

    static public String addCota(String cota, String limite) {
        String ret = null;
        if (cota.length() == 5) {
            // Cota 99/99
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3, 5);
            if (checkCota(cota)) {
                ret = "01/" + (limite == null ? limite : part2);
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) + 1),2) + "/" + part2;
            }
        } else {
            // Parcela 99/9999
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,7);
            if (checkCota(cota)) {
                ret = "01/" + FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part2) + 1),4);
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) + 1),2) + "/" + part2;
            }
        }
        return ret;
    }

    static public boolean checkCota(String cota) {
        boolean ret = false;
        if (cota.length() == 5) {
            // Cota 99/99
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,5);
            if (part1.equalsIgnoreCase(part2)) ret = true;
        } else {
            // Parcela 99/9999
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,7);
            if (part1.equalsIgnoreCase("12")) ret = true;
        }
        return ret;
    }

    static public String subCota(String cota) {
        String ret = null;
        if (cota.length() == 5) {
            // Cota 99/99
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,5);
            if (checkCotaSub(cota)) {
                ret = "01/" + part2;
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) - 1),2) + "/" + part2;
            }
        } else {
            // Parcela 99/9999
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,7);
            if (checkCotaSub(cota)) {
                ret = "01/" + FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part2) - 1),4);
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) - 1),2) + "/" + part2;
            }
        }
        return ret;
    }

    static public String subCota(String cota, String limite) {
        String ret = null;
        if (cota.length() == 5) {
            // Cota 99/99
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3, 5);
            if (checkCotaSub(cota)) {
                ret = "01/" + (limite == null ? limite : part2);
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) - 1),2) + "/" + part2;
            }
        } else {
            // Parcela 99/9999
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,7);
            if (checkCotaSub(cota)) {
                ret = "01/" + FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part2) - 1),4);
            } else {
                ret = FuncoesGlobais.StrZero(String.valueOf(Integer.valueOf(part1) - 1),2) + "/" + part2;
            }
        }
        return ret;
    }

    static public boolean checkCotaSub(String cota) {
        boolean ret = false;
        if (cota.length() == 5) {
            // Cota 99/99
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,5);
            if (part1.equalsIgnoreCase("01")) ret = true;
        } else {
            // Parcela 99/9999
            String part1 = cota.substring(0,2);
            String part2 = cota.substring(3,7);
            if (part1.equalsIgnoreCase("01")) ret = true;
        }
        return ret;
    }

    public static int FindinList(List<?> marray, String oque) {
        int retorno = -1, i = 0;
        if (marray == null) return retorno;
        if (marray.size() == 0) {return retorno;}
        boolean achei = false; Object[] tarray = marray.toArray();
        for (i=0;i<tarray.length;i++) {
            if (((divSec)tarray[i]).getRgimv() != null) {
                if (((divSec) tarray[i]).getRgimv().equalsIgnoreCase(oque)) {
                    achei = true;
                    break;
                }
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int IndexOf(Object aString[], String sOque) {
        int i =  0;
        boolean achei = false;
        int retorno = -1;

        for (i=0; i <= aString.length - 1; i++) {
            if (((String)aString[i]).contains(sOque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static String LimpaCpfCnpj(String value) {
        if (value == null) return "";
        String retvalue = "";
        for (Character o : value.toCharArray()) {
            if (Character.isDigit(o)) retvalue += o;
        }
        return retvalue;
    }

    public static String FormatCpfCnpj(String value) {
        String retvalue = "";
        value = LimpaCpfCnpj(value);
        if (value.length() == 11) {
            // CPF
            retvalue = value.substring(0, 3) + "." + value.substring(3, 6) + "." + value.substring(6, 9) + "-" + value.substring(9, 11);
        } else {
            // CNPJ
            retvalue = value.substring(0, 2) + "." + value.substring(2, 5) + "." + value.substring(5, 8) + "." + value.substring(8, 12) + "-" + value.substring(12, 14);
        }
        return retvalue;
    }

    public static Consulta[] ArrayConsultaAdd(Consulta[] mArray, Consulta value) {
        Consulta[] temp = new Consulta[mArray.length+1];
        System.arraycopy(mArray,0,temp,0,mArray.length);
        temp[mArray.length] = value;
        return temp;
    }

    public static int FindNinObjects(Object[][] marray, int[] coluna, String[] oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            boolean aonde1 = true;
            for (int n=0;n<coluna.length;n++) {
                aonde1 =  aonde1 && marray[i][coluna[n]].equals(oque[n]) ;
            }

            if (aonde1) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static int FindinObjects(Object[][] marray, int coluna, String oque) {
        int retorno = -1, i = 0;
        if (marray.length == 0) {return retorno;}
        boolean achei = false;
        for (i=0;i<marray.length;i++) {
            String aonde = String.valueOf(marray[i][coluna]);
            if (aonde.contains(oque)) {
                achei = true;
                break;
            }
        }
        if (achei) retorno = i;
        return retorno;
    }

    public static String Subst(String Variavel, String[] Conteudos) {
        String retorno = Variavel;
        if (Conteudos.length > 0) {
            for (int i=0;i<Conteudos.length;i++) {
                retorno = retorno.replace("&" + String.valueOf(i + 1).trim() + ".", Conteudos[i]);
            }
        }

        return retorno;
    }

    public static int SeekNObjects(Object[] marray, int coluna, String oque) {
        int pos = -1; boolean bachei = false;
        for (Object obj : marray) {
            pos++;
            if (((Object[])obj)[coluna].toString().equals(oque)) {
                bachei = true;
                break;
            }
        }
        if (!bachei) pos = -1;
        return pos;
    }
}

