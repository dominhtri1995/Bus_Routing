package apps.apidez.com.busrouting.model;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import apps.apidez.com.busrouting.R;

public class BusStopLocation {
    private String id;
    private double latitude;
    private double longitude;

    public BusStopLocation(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // get marker option
    public MarkerOptions getMarkerOption() {
        return new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin))
                .position(new LatLng(latitude, longitude))
                .draggable(true);
    }

    public MarkerOptions getMarkerOptionUnDraggable() {
        return new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin))
                .position(new LatLng(latitude, longitude))
                .draggable(false);
    }
}
