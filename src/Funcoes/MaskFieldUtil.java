package Funcoes;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;

import static javafx.scene.input.KeyCode.*;

public abstract class MaskFieldUtil extends TextField {
    private ArrayList<String> patterns;

    public MaskFieldUtil() {}
    
    private static List<KeyCode> ignoreKeyCodes;

    static {
        ignoreKeyCodes = new ArrayList<>();
        Collections.addAll(ignoreKeyCodes, new KeyCode[]{F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12});
    }

    public static void ignoreKeys(final TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (ignoreKeyCodes.contains(keyEvent.getCode())) {
                    keyEvent.consume();
                }
            }
        });
    }

    /**
     * Monta a mascara para Data (dd/MM/yyyy).
     *
     * @param textField TextField
     */
    public static void dateField(final TextField textField) {
        maxField(textField, 10);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() < 11) {
                    String value = textField.getText();
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
                    value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
                    textField.setText(value);
                    positionCaret(textField);
                }
            }
        });
    }

    /**
     * Monta a mascara para Data (MM/yyyy).
     *
     * @param textField TextField
     */
    public static void dateRefField(final TextField textField) {
        maxField(textField, 7);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue == null) return;
                if (newValue.intValue() < 8) {
                    String value = textField.getText();
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{2})(\\d)", "$1/$2");
                    //value = value.replaceFirst("(\\d{2})\\/(\\d{2})(\\d)", "$1/$2/$3");
                    textField.setText(value);
                    positionCaret(textField);
                }
            }
        });
    }

    /**
     * Campo que aceita somente numericos.
     *
     * @param textField TextField
     */
    public static void numericField(final TextField textField) {
        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = textField.getText().charAt(oldValue.intValue());
                    if (!(ch >= '0' && ch <= '9')) {
                        textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                    }
                }
            }
        });
    }

    /**
     * Monta a mascara para Moeda.
     *
     * @param textField TextField
     */
    public static void monetaryField(final TextField textField) {
        textField.setAlignment(Pos.CENTER_RIGHT);
        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String value = textField.getText();
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceAll("([0-9]{1})([0-9]{14})$", "$1.$2");
                value = value.replaceAll("([0-9]{1})([0-9]{11})$", "$1.$2");
                value = value.replaceAll("([0-9]{1})([0-9]{8})$", "$1.$2");
                value = value.replaceAll("([0-9]{1})([0-9]{5})$", "$1.$2");
                value = value.replaceAll("([0-9]{1})([0-9]{2})$", "$1,$2");
                textField.setText(value);
                positionCaret(textField);

                textField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                        if (newValue.length() > 17)
                            textField.setText(oldValue);
                    }
                });
            }
        });

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean fieldChange) {
                if (!fieldChange) {
                    final int length = textField.getText().length();
                    if (length > 0 && length < 3) {
                        textField.setText(textField.getText() + "00");
                    }
                }
            }
        });
    }

    /**
     * Monta as mascara para CPF/CNPJ. A mascara eh exibida somente apos o campo perder o foco.
     *
     * @param textField TextField
     */
    public static void cpfCnpjField(final TextField textField) {

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean fieldChange) {
                String value = textField.getText();
                if (value == null) return;
                if (!fieldChange) {
                    if (textField.getText().length() == 11) {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceFirst("([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})$", "$1.$2.$3-$4");
                    }
                    if (textField.getText().length() == 14) {
                        value = value.replaceAll("[^0-9]", "");
                        value = value.replaceFirst("([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})$", "$1.$2.$3/$4-$5");
                    }
                }
                textField.setText(value);
                if (textField.getText() != value) {
                    textField.setText("");
                    textField.insertText(0, value);
                }

            }
        });

        maxField(textField, 18);
    }

    /**
     * Monta a mascara para os campos CNPJ.
     *
     * @param textField TextField
     */
    public static void cnpjField(final TextField textField) {
        maxField(textField, 18);

        textField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String value = textField.getText();
                if (value == null) return;
                value = value.replaceAll("[^0-9]", "");
                value = value.replaceFirst("(\\d{2})(\\d)", "$1.$2");
                value = value.replaceFirst("(\\d{2})\\.(\\d{3})(\\d)", "$1.$2.$3");
                value = value.replaceFirst("\\.(\\d{3})(\\d)", ".$1/$2");
                value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                textField.setText(value);
                positionCaret(textField);
            }
        });

    }

    public static void cepField(TextField textField) {
        MaskFieldUtil.maxField(textField, 9);
        textField.lengthProperty().addListener((observableValue, number, number2) -> {
                    String value = textField.getText();
                    value = value.replaceAll("[^0-9]", "");
                    value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                    textField.setText(value);
                    MaskFieldUtil.positionCaret(textField);
                }
        );
    }

    public static void foneField(TextField textField) {
        MaskFieldUtil.maxField(textField, 10);
        textField.lengthProperty().addListener((observableValue, number, number2) -> {
                    try {
                        String value = textField.getText();
                        value = value.replaceAll("[^0-9]", "");
                        int tam = value.length();
                        //value = value.replaceFirst("(\\d{2})(\\d)", "($1)$2");
                        value = value.replaceFirst("(\\d{4})(\\d)", "$1-$2");
                        if (tam > 8) {
                            value = value.replaceAll("-", "");
                            value = value.replaceFirst("(\\d{5})(\\d)", "$1-$2");
                        }
                        textField.setText(value);
                        MaskFieldUtil.positionCaret(textField);

                    } catch (Exception ex) {
                    }
                }
        );
    }

    /**
     * Devido ao incremento dos caracteres das mascaras eh necessario que o cursor sempre se posicione no final da string.
     *
     * @param textField TextField
     */
    private static void positionCaret(final TextField textField) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Posiciona o cursor sempre a direita.
                textField.positionCaret(textField.getText().length());
            }
        });
    }

    /**
     * @param textField TextField.
     * @param length    Tamanho do campo.
     */
    public static void maxField(final TextField textField, final Integer length) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (newValue != null) {
                    if (newValue.length() > length)
                        textField.setText(oldValue);
                }
            }
        });
    }

    static public boolean isCpf(String strCPF) {
        int Soma;
        int Resto;
        Soma = 0;
        strCPF = strCPF.replace(".","");
        strCPF = strCPF.replace("-","");
        if (strCPF == "00000000000")
            return false;
        for (int i=1; i<=9; i++)
            Soma = Soma + Integer.parseInt(strCPF.substring(i-1, i)) * (11 - i);
        Resto = (Soma * 10) % 11;
        if ((Resto == 10) || (Resto == 11))
            Resto = 0;
        if (Resto != Integer.parseInt(strCPF.substring(9, 10)) )
            return false;
        Soma = 0;
        for (int i = 1; i <= 10; i++)
            Soma = Soma + Integer.parseInt(strCPF.substring(i-1, i)) * (12 - i);
        Resto = (Soma * 10) % 11;
        if ((Resto == 10) || (Resto == 11))
            Resto = 0;
        if (Resto != Integer.parseInt(strCPF.substring(10, 11) ) )
            return false;
        return true;
    }

/*
    static public boolean isCpf(String cpf) {
        if (cpf == null) return true;
        cpf = cpf.replace(".", "");
        cpf = cpf.replace("-", "");

        try{
          Long.parseLong(cpf);
        } catch(NumberFormatException e){
          return false;
        }

        int d1, d2;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;

        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;

        for (int nCount = 1; nCount < cpf.length() - 1; nCount++) {
          digitoCPF = Integer.valueOf(cpf.substring(nCount - 1, nCount)).intValue();

          // multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4
          // e assim por diante.
          d1 = d1 + (11 - nCount) * digitoCPF;

          // para o segundo digito repita o procedimento incluindo o primeiro
          // digito calculado no passo anterior.
          d2 = d2 + (12 - nCount) * digitoCPF;
        }

        // Primeiro resto da divisão por 11.
        resto = (d1 % 11);

        // Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11
        // menos o resultado anterior.
        if (resto < 2)
          digito1 = 0;
        else
          digito1 = 11 - resto;

        d2 += 2 * digito1;

        // Segundo resto da divisão por 11.
        resto = (d2 % 11);

        // Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11
        // menos o resultado anterior.
        if (resto < 2)
          digito2 = 0;
        else
          digito2 = 11 - resto;

        // Digito verificador do CPF que está sendo validado.
        String nDigVerific = cpf.substring(cpf.length() - 2, cpf.length());

        // Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

        // comparar o digito verificador do cpf com o primeiro resto + o segundo
        // resto.
        return nDigVerific.equals(nDigResult);
      }
*/

      /**
       * Realiza a validação de um cnpj
       * 
       * @param cnpj String - o CNPJ pode ser passado no formato 99.999.999/9999-99 ou 99999999999999
       * @return boolean
       */
      static public boolean isCnpj(String cnpj) {
        cnpj = cnpj.replace(".", "");
        cnpj = cnpj.replace("-", "");
        cnpj = cnpj.replace("/", "");

        try{
          Long.parseLong(cnpj);
        } catch(NumberFormatException e){
          return false;
        }

        // considera-se erro CNPJ's formados por uma sequencia de numeros iguais
        if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111")
            || cnpj.equals("22222222222222") || cnpj.equals("33333333333333")
            || cnpj.equals("44444444444444") || cnpj.equals("55555555555555")
            || cnpj.equals("66666666666666") || cnpj.equals("77777777777777")
            || cnpj.equals("88888888888888") || cnpj.equals("99999999999999") || (cnpj.length() != 14))
          return (false);
        char dig13, dig14;
        int sm, i, r, num, peso; // "try" - protege o código para eventuais
                                 // erros de conversao de tipo (int)
        try {
          // Calculo do 1o. Digito Verificador
          sm = 0;
          peso = 2;
          for (i = 11; i >= 0; i--) {
            // converte o i-ésimo caractere do CNPJ em um número: // por
            // exemplo, transforma o caractere '0' no inteiro 0 // (48 eh a
            // posição de '0' na tabela ASCII)
            num = (int) (cnpj.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso + 1;
            if (peso == 10)
              peso = 2;
          }

          r = sm % 11;
          if ((r == 0) || (r == 1))
            dig13 = '0';
          else
            dig13 = (char) ((11 - r) + 48);

          // Calculo do 2o. Digito Verificador
          sm = 0;
          peso = 2;
          for (i = 12; i >= 0; i--) {
            num = (int) (cnpj.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso + 1;
            if (peso == 10)
              peso = 2;
          }
          r = sm % 11;
          if ((r == 0) || (r == 1))
            dig14 = '0';
          else
            dig14 = (char) ((11 - r) + 48);
          // Verifica se os dígitos calculados conferem com os dígitos
          // informados.
            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException erro) {
            return (false);
        }
    }
}