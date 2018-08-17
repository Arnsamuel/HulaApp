package e.aaronsamuel.hulaapp;

import java.io.Serializable;

public class PushEventDb implements Serializable {
    String eventId;
    String eventTitle;
    String eventDate;
    String eventTime;
    String eventCreator;

    public PushEventDb(){

    }

    public PushEventDb(String eventId, String eventTitle, String eventDate, String eventTime, String eventCreator) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventCreator = eventCreator;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getEventCreator() {
        return eventCreator;
    }
}
