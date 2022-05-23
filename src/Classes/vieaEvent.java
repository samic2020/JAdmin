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
public class vieaEvent  extends Event {
    public static final EventType<vieaEvent> GET_VIEA=new EventType<>(ANY,"GET_VIEA");
    
    public String sviea;
    
    public vieaEvent(EventType<? extends Event>eventType) { super(eventType); }
    
    public vieaEvent(String sviea, EventType<? extends Event>eventType) {
        super(eventType);
        this.sviea = sviea;
    }

}
