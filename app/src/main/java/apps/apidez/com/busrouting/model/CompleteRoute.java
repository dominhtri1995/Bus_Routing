package apps.apidez.com.busrouting.model;

import java.util.List;

public class CompleteRoute {
    private int numOfBus;
    private boolean warning;
    private List<BusRoute> list;

    public CompleteRoute() {
    }

    public CompleteRoute(int numOfBus, boolean warning, List<BusRoute> list) {
        this.numOfBus = numOfBus;
        this.warning = warning;
        this.list = list;
    }

    public int getNumOfBus() {
        return numOfBus;
    }

    public void setNumOfBus(int numOfBus) {
        this.numOfBus = numOfBus;
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public List<BusRoute> getList() {
        return list;
    }

    public void setList(List<BusRoute> list) {
        this.list = list;
    }
}
