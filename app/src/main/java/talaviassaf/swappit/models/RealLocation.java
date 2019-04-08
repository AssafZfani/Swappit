package talaviassaf.swappit.models;

@SuppressWarnings("unused")

public class RealLocation {

    private String lat, lon;

    RealLocation() {

    }

    public RealLocation(String lat, String lon) {

        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {

        return lat;
    }

    public String getLon() {

        return lon;
    }
}
