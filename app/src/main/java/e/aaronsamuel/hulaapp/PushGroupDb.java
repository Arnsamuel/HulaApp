package e.aaronsamuel.hulaapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PushGroupDb implements Parcelable {
    String groupId;
    String groupTitle;
    String groupCreator;
    String groupCreatorId;

    ArrayList<PushUserDb> groupMember;

    public PushGroupDb() {

    }

    public PushGroupDb(Parcel parcel) {
        groupId = parcel.readString();
        groupTitle = parcel.readString();
        groupCreator = parcel.readString();
        groupCreatorId = parcel.readString();

        if(this.groupMember == null)
            this.groupMember = new ArrayList<>();

        parcel.readTypedList(this.groupMember, PushUserDb.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(groupTitle);
        dest.writeString(groupCreator);
        dest.writeString(groupCreatorId);

        dest.writeTypedList(groupMember);
    }

    public static final Creator<PushGroupDb> CREATOR = new Creator<PushGroupDb>() {
        public PushGroupDb createFromParcel(Parcel source) {
            return new PushGroupDb(source);
        }
        public PushGroupDb[] newArray(int size) {
            return new PushGroupDb[size];
        }
    };

    public PushGroupDb (String groupId, String groupTitle , String groupCreator, String groupCreatorId, List groupMember) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupCreator = groupCreator;
        this.groupCreatorId = groupCreatorId;
        this.groupMember = new ArrayList<>(groupMember);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public String getGroupCreatorId() {
        return groupCreatorId;
    }

    public List getGroupMember() {
        return groupMember;
    }
}