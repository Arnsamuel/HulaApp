package e.aaronsamuel.hulaapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

public class PushUserDb implements Parcelable {
    String userId;
    String name;
    String profilepic;
    boolean attending;

    public PushUserDb() {
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;

        if(o == null)
            return false;

        if (!(o instanceof PushUserDb)) {
            return false;
        }

        PushUserDb user = (PushUserDb) o;

        if(TextUtils.isEmpty(user.userId))
            return false;

        return user.userId.equals(userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    PushUserDb(String userId) {
        this.userId = userId;
    }

    PushUserDb(String userId, String name, String profilepic) {
        this.userId = userId;
        this.name = name;
        this.profilepic = profilepic;
    }

     PushUserDb(Parcel parcel) {
        userId = parcel.readString();
        name = parcel.readString();
        profilepic = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(profilepic);
    }

    public static final Creator<PushUserDb> CREATOR = new Creator<PushUserDb>() {
        public PushUserDb createFromParcel(Parcel source) {
            return new PushUserDb(source);
        }
        public PushUserDb[] newArray(int size) {
            return new PushUserDb[size];
        }
    };

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getProfilepic() {
        return profilepic;
    }
}
