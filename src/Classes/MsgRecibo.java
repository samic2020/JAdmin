package Classes;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by supervisor on 24/05/17.
 */
public class MsgRecibo {
    SimpleStringProperty msg;

    public MsgRecibo(String msg) { this.msg = new SimpleStringProperty(msg); }
    public String getMsg() { return msg.get(); }
    public SimpleStringProperty msgProperty() { return msg; }
    public void setMsg(String msg) { this.msg.set(msg); }
}
