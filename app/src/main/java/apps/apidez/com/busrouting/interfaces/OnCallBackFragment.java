package apps.apidez.com.busrouting.interfaces;

import apps.apidez.com.busrouting.model.BusRoute;

public interface OnCallBackFragment {
    public void onDropBus(String nameOfBus, String nameOfDes, int numberOfBus, int id);
    public void onDetail(int numOfBus, boolean warning, int id);
    public void onUpdateBusRoute(BusRoute busRoute);
    public void removeBus(int pos);
    public void onReport();
    public void onPick();
}
