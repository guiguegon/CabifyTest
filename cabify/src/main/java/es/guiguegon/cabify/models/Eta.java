package es.guiguegon.cabify.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guille on 12/10/2016.
 */

public class Eta implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Eta> CREATOR = new Parcelable.Creator<Eta>() {
        @Override
        public Eta createFromParcel(Parcel in) {
            return new Eta(in);
        }

        @Override
        public Eta[] newArray(int size) {
            return new Eta[size];
        }
    };
    private int min;
    private int max;
    private int formatted;

    protected Eta(Parcel in) {
        min = in.readInt();
        max = in.readInt();
        formatted = in.readInt();
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getFormatted() {
        return formatted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeInt(formatted);
    }
}