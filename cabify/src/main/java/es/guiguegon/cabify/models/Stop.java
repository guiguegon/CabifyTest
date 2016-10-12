package es.guiguegon.cabify.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guillermoguerrero on 15/1/16.
 */
public class Stop implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };
    private double[] loc;
    private String name;
    private String addr;
    private String num;
    private String city;
    private String country;

    Stop(double[] loc, String name, String addr, String num, String city, String country) {
        this.loc = loc;
        this.name = name;
        this.addr = addr;
        this.num = num;
        this.city = city;
        this.country = country;
    }

    protected Stop(Parcel in) {
        name = in.readString();
        addr = in.readString();
        num = in.readString();
        city = in.readString();
        country = in.readString();
        in.readDoubleArray(loc);
    }

    public double[] getLoc() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public String getNum() {
        return num;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(addr);
        dest.writeString(num);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeDoubleArray(loc);
    }
}