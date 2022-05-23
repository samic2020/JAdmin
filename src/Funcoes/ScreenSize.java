package Funcoes;

import java.awt.*;

/**
 * Created by supervisor on 07/11/16.
 */
public class ScreenSize {
    public static int getHeight() {
        int y;
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        y = (int) d.getHeight();
        return y;
    }
    public static int getWidth() {
        int x;
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        x = (int) d.getWidth();
        return x;
    }
}
