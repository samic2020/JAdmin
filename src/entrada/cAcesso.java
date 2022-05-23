package entrada;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class cAcesso {
    private SimpleIntegerProperty menu;
    private SimpleIntegerProperty submenu;
    private SimpleStringProperty viea;
    private SimpleBooleanProperty seach;

    public cAcesso(int menu, int submenu, String viea, boolean seach) {
        this.menu = new SimpleIntegerProperty(menu);
        this.submenu = new SimpleIntegerProperty(submenu);
        this.viea = new SimpleStringProperty(viea);
        this.seach = new SimpleBooleanProperty(seach);
    }

    public int getMenu() { return menu.get(); }
    public SimpleIntegerProperty menuProperty() { return menu; }
    public void setMenu(int menu) { this.menu.set(menu); }

    public int getSubmenu() { return submenu.get(); }
    public SimpleIntegerProperty submenuProperty() { return submenu; }
    public void setSubmenu(int submenu) { this.submenu.set(submenu); }

    public String getViea() { return viea.get(); }
    public SimpleStringProperty vieaProperty() { return viea; }
    public void setViea(String viea) { this.viea.set(viea); }

    public boolean isSeach() { return seach.get(); }
    public SimpleBooleanProperty seachProperty() { return seach; }
    public void setSeach(boolean seach) { this.seach.set(seach); }
}
