package Classes;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

/**
 * Created by supervisor on 18/05/17.
 */
public class iRecibo {
    public static class CorpoRecibo {
        SimpleStringProperty descr;
        SimpleStringProperty copa;
        SimpleObjectProperty<BigDecimal> vlr;

        public CorpoRecibo(String descr, String copa, BigDecimal vlr) {
            this.descr = new SimpleStringProperty(descr);
            this.copa = new SimpleStringProperty(copa);
            this.vlr = new SimpleObjectProperty<BigDecimal>(vlr);
        }

        public String getDescr() { return descr.get(); }
        public SimpleStringProperty descrProperty() { return descr; }
        public void setDescr(String descr) { this.descr.set(descr); }

        public String getCopa() { return copa.get(); }
        public SimpleStringProperty copaProperty() { return copa; }
        public void setCopa(String copa) { this.copa.set(copa); }

        public BigDecimal getVlr() { return vlr.get(); }
        public SimpleObjectProperty<BigDecimal> vlrProperty() { return vlr; }
        public void setVlr(BigDecimal vlr) { this.vlr.set(vlr); }
    }
}
