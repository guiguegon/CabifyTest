package es.guiguegon.cabify.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guille on 12/10/2016.
 */

public class Icon implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() {
        @Override
        public Icon createFromParcel(Parcel in) {
            return new Icon(in);
        }

        @Override
        public Icon[] newArray(int size) {
            return new Icon[size];
        }
    };
    private String regular;

    protected Icon(Parcel in) {
        regular = in.readString();
    }

    public String getRegular() {
        return regular;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(regular);
    }
}