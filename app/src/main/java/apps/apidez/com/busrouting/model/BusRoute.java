package apps.apidez.com.busrouting.model;

import java.util.List;

public class BusRoute {
    private String name;
    private String destination;
    private boolean state;
    private List<BusStopLocation> listOfLocations;

    public BusRoute() {
    }

    public BusRoute(String name, String destination,
                    List<BusStopLocation> listOfLocations) {
        this.name = name;
        this.destination = destination;
        this.listOfLocations = listOfLocations;
        state = false;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public List<BusStopLocation> getListOfLocations() {
        return listOfLocations;
    }

    public void setListOfLocations(List<BusStopLocation> listOfLocations) {
        this.listOfLocations = listOfLocations;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
