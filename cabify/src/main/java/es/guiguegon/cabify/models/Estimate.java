package es.guiguegon.cabify.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guille on 12/10/2016.
 */
public class Estimate implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Estimate> CREATOR = new Parcelable.Creator<Estimate>() {
        @Override
        public Estimate createFromParcel(Parcel in) {
            return new Estimate(in);
        }

        @Override
        public Estimate[] newArray(int size) {
            return new Estimate[size];
        }
    };
    private VehicleType vehicleType;
    private int totalPrice;
    private String priceFormatted;
    private String currency;
    private String currencySymbol;

    protected Estimate(Parcel in) {
        vehicleType = (VehicleType) in.readValue(VehicleType.class.getClassLoader());
        totalPrice = in.readInt();
        priceFormatted = in.readString();
        currency = in.readString();
        currencySymbol = in.readString();
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getPriceFormatted() {
        return priceFormatted;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(vehicleType);
        dest.writeInt(totalPrice);
        dest.writeString(priceFormatted);
        dest.writeString(currency);
        dest.writeString(currencySymbol);
    }
}