package es.guiguegon.cabify.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Guille on 12/10/2016.
 */

public class VehicleType implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<VehicleType> CREATOR =
            new Parcelable.Creator<VehicleType>() {
                @Override
                public VehicleType createFromParcel(Parcel in) {
                    return new VehicleType(in);
                }

                @Override
                public VehicleType[] newArray(int size) {
                    return new VehicleType[size];
                }
            };
    @SerializedName("_id")
    private String id;
    private String name;
    private String shortName;
    private String description;
    private Icon icons;
    private boolean reservedOnly;
    private boolean asapOnly;
    private String currency;
    private String icon;
    private Eta eta;

    protected VehicleType(Parcel in) {
        id = in.readString();
        name = in.readString();
        shortName = in.readString();
        description = in.readString();
        icons = (Icon) in.readValue(Icon.class.getClassLoader());
        reservedOnly = in.readByte() != 0x00;
        asapOnly = in.readByte() != 0x00;
        currency = in.readString();
        icon = in.readString();
        eta = (Eta) in.readValue(Eta.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReservedOnly() {
        return reservedOnly;
    }

    public boolean isAsapOnly() {
        return asapOnly;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIcon() {
        return icon;
    }

    public Icon getIcons() {
        return icons;
    }

    public Eta getEta() {
        return eta;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(shortName);
        dest.writeString(description);
        dest.writeValue(icons);
        dest.writeByte((byte) (reservedOnly ? 0x01 : 0x00));
        dest.writeByte((byte) (asapOnly ? 0x01 : 0x00));
        dest.writeString(currency);
        dest.writeString(icon);
        dest.writeValue(eta);
    }
}