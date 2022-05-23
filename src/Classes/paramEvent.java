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
public class paramEvent extends Event {
    public static final EventType<paramEvent> GET_PARAM=new EventType<>(ANY,"GET_PARAM");
    
    public Object[] sparam;
    
    public paramEvent(EventType<? extends Event>eventType) { super(eventType); }
    
    public paramEvent(Object[] sparam, EventType<? extends Event>eventType) {
        super(eventType);
        this.sparam = sparam;
    }

}
