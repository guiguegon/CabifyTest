package es.guiguegon.cabify.models;

import es.guiguegon.cabify.utils.LocationUtils;

public class StopBuilder {
    private Marker marker;

    public StopBuilder setMarker(Marker marker) {
        this.marker = marker;
        return this;
    }

    public Stop createStop() {
        double[] loc = new double[] {
                marker.getAddress().getLatitude(), marker.getAddress().getLongitude()
        };
        String name = marker.getAddress().getFeatureName();
        String addr = LocationUtils.getFullAddress(marker);
        String num = "s/n";
        String city = marker.getAddress().getLocality();
        String country = marker.getAddress().getCountryName();
        return new Stop(loc, name, addr, num, city, country);
    }
}