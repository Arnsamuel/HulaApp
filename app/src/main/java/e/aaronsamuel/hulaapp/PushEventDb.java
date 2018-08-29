package e.aaronsamuel.hulaapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PushEventDb implements Parcelable {
    String eventId;
    String eventTitle;
    String eventDate;
    String eventTime;
    String eventCreator;
    String eventCreatorId;
    String eventLocation;
    String eventCoor;
    PushGroupDb eventGroup;

    public PushEventDb(){

    }

    public PushEventDb(Parcel parcel) {
        eventId = parcel.readString();
        eventTitle = parcel.readString();
        eventDate = parcel.readString();
        eventTime = parcel.readString();
        eventCreator = parcel.readString();
        eventCreatorId = parcel.readString();
        eventLocation = parcel.readString();
        eventCoor = parcel.readString();

        eventGroup = parcel.readParcelable(PushGroupDb.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventTitle);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
        dest.writeString(eventCreator);
        dest.writeString(eventCreatorId);
        dest.writeString(eventLocation);
        dest.writeString(eventCoor);

        dest.writeParcelable(eventGroup, flags);
    }

    public static final Creator<PushEventDb> CREATOR = new Creator<PushEventDb>() {
        public PushEventDb createFromParcel(Parcel source) {
            return new PushEventDb(source);
        }
        public PushEventDb[] newArray(int size) {
            return new PushEventDb[size];
        }
    };

    public PushEventDb(String eventId, String eventTitle, String eventDate, String eventTime, String eventCreator, String eventCreatorId, String eventLocation, String eventCoor, PushGroupDb eventGroup) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventCreator = eventCreator;
        this.eventCreatorId = eventCreatorId;
        this.eventLocation = eventLocation;
        this.eventCoor = eventCoor;
        this.eventGroup = eventGroup;
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

    public String getEventCreatorId() {
        return eventCreatorId;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventCoor() {
        return eventCoor;
    }

    public PushGroupDb getEventGroup() {
        return eventGroup;
    }
}
