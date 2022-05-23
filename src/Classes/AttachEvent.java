package Classes;

import javafx.event.Event;
import javafx.event.EventType;

public class AttachEvent extends Event {
        public static final EventType<Classes.AttachEvent> GET_ATTACH=new EventType<>(ANY,"GET_ATTACH");

        public Object[] sparam;

        public AttachEvent(EventType<? extends Event>eventType) { super(eventType); }

        public AttachEvent(Object[] sparam, EventType<? extends Event>eventType) {
            super(eventType);
            this.sparam = sparam;
        }
}
