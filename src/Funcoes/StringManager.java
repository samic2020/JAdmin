package Funcoes;

/**
 * Created by supervisor on 29/06/16.
 */
public class StringManager {
    public StringManager() {}

    public static String Mid(String text, int start) {
        return (text.length() < start + 1) ? "" : text.substring(start, text.length() - start);
    }

    public static String Mid(String text, int start, int end) {
        String part1 = text.substring(start - 1);

        String part2 = "";
        if (end > part1.length()) {
            part2 = part1;
        } else {
            part2 = part1.substring(0, end);
        }
        return part2;
    }

    public static String Left(String text, int length) {
        return (text.length() < length) ? text : text.substring(0, length);
    }

    public static String Right(String text, int length) {
        return (text.length() < length) ? text : text.substring(text.length() - length);
    }

    public static String ConvStr(String cword) {
        if (cword == null || cword.length() == 0) return "";
        String[] words = cword.trim().toLowerCase().split(" ");
        for (int i=0;i<words.length;i++) {
            if (words[i].equals("e") || words[i].equals("de") || words[i].equals("do") || words[i].equals("dos") || words[i].equals("da") || words[i].equals("das")) {
                // nada pq já é minusculo
            } else {
                words[i] = words[i].toUpperCase(); //capitalize(words[i]);
            }
        }
        return FuncoesGlobais.join(words, " ");
    }
}
