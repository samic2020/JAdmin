package Classes;

import java.math.BigDecimal;

/**
 * Created by supervisor on 05/09/16.
 */
public class ir {
    int id;
    String mesano;
    BigDecimal indice;

    public ir(int id, String mesano, BigDecimal indice) {
        this.id = id;
        this.mesano = mesano;
        this.indice = indice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMesano() { return mesano; }
    public void setMesano(String mesano) { this.mesano = mesano; }

    public BigDecimal getIndice() { return indice; }
    public void setIndice(BigDecimal indice) { this.indice = indice; }

    @Override
    public String toString() {
        return "ir{" +
                "id=" + id +
                ", mesano='" + mesano + '\'' +
                ", indice=" + indice +
                '}';
    }
}
