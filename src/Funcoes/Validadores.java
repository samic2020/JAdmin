package Funcoes;

public class Validadores {

    public String ValidaInscricao(String value) {
        String Retorno = "";
        value = value.replace(".","").replace("-","");
        Retorno = CalcDig11Inscricao(value);
        return StringManager.Left(value,2) + "." + StringManager.Mid(value, 3,3) + "." + StringManager.Mid(value,6,2) + "-" + Retorno;
    }

    public String CalcDig11Inscricao(String cadeia) {
        int total= 0; int mult = 2;
        for (int i=1; i<=cadeia.length();i++) {
            total += Integer.valueOf(cadeia.substring(cadeia.length() - i,(cadeia.length() + 1) - i)) * mult;
            mult++;
            if (mult > 9) mult = 2;
        }
        int soma = total;
        int resto = (soma % 11);
        if (resto < 2) {
            resto = 0;
        } else {
            resto = 11 - resto;
        }
        return String.valueOf(resto);
    }

}
