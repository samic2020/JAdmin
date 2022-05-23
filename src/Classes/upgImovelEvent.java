/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author supervisor
 */
public class upgImovelEvent extends Event {
    public static final EventType<upgImovelEvent> GET_UPG =new EventType<>(ANY,"GET_UPG");

    public String supg;

    public upgImovelEvent(EventType<? extends Event>eventType) { super(eventType); }

    public upgImovelEvent(String supg, EventType<? extends Event>eventType) {
        super(eventType);
        this.supg = supg;
    }

}
