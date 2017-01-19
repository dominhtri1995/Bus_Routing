package apps.apidez.com.busrouting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import apps.apidez.com.busrouting.R;
import apps.apidez.com.busrouting.model.BusRoute;

public class AdapterBusRouteResults extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BusRoute> mList;
    private Context mContext;
    private View view;

    // constructor
    public AdapterBusRouteResults(Context context, List<BusRoute> list) {
        this.mList = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        // configure the view
        if (convertView == null) {
            view = mInflater.inflate(R.layout.cell_bus_route, parent, false);
        } else {
            view = convertView;
        }

        initView(view, position);
        this.view = view;
        return view;
    }

    private void initView(View view, int position) {
        // fetch data
        final BusRoute busRoute = mList.get(position);

        // update data
        TextView busName = (TextView) view.findViewById(R.id.name_of_bus);
        busName.setText(busRoute.getName());
        TextView busDes = (TextView) view.findViewById(R.id.name_of_des);
        busDes.setText(busRoute.getDestination());

        // layout
        LinearLayout ln = (LinearLayout) view.findViewById(R.id.bus_route_layout);
        if (busRoute.isState()) {
            ln.setBackgroundResource(R.drawable.background_cell_bus_selected);
        } else {
            ln.setBackgroundResource(R.drawable.background_cell_bus_unselected);
        }
    }
}
