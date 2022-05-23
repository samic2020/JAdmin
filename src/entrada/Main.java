package entrada;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sibvisions.rad.ui.swing.impl.SwingFactory;
import com.sibvisions.util.log.ILogger;
import com.sibvisions.util.log.LoggerFactory;

import javax.rad.genui.UIFactoryManager;
import javax.swing.*;
import java.io.IOException;

public final class Main {
    static public void main(String[] args) throws IOException {
                       
        // entrada.Main com.sibvisions.rad.ui.javafx.impl.JavaFXFactory
        LoggerFactory.setLevel("jAdmin", ILogger.LogLevel.ALL);

        try {
            Class<?> factoryClass = null;

            try {
                // Use provided factory and fall-back to Swing
                factoryClass = Main.class.getClassLoader().loadClass(args[0]);
            } catch (Exception e) {
                LoggerFactory.getInstance(Main.class).info("Defaulting to SwingFactory.", e);

                factoryClass = Main.class.getClassLoader().loadClass("com.sibvisions.rad.ui.swing.impl.SwingFactory");
            }

            if (SwingFactory.class.isAssignableFrom(factoryClass)) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            // This will set and initialize the factory that is used by JVx
            // for creating all the GUI controls (and some other stuff, like
            // threading).
            UIFactoryManager.getFactoryInstance(factoryClass);

            // The invokeAndWait(Runnable) call is necessary so that we are
            // on the UI thread of the specified technology.
            UIFactoryManager.getFactory().invokeAndWait(() -> {
                // Create the main frame.
                LoginFrame frame = new LoginFrame();
                frame.pack();
                frame.setVisible(true);

                // Since we do not know anything technology specific,
                // we only can exit when the main window has closed.
                // Not invoking exit might result in that the application
                // will not exit.
                frame.eventWindowClosed().addListener(pEvent -> {
                    System.exit(0);
                });
            });
        } catch (Exception e) {
            LoggerFactory.getInstance(Main.class).error(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}

// Argumento do Programa
// com.sibvisions.rad.ui.javafx.impl.JavaFXFactory