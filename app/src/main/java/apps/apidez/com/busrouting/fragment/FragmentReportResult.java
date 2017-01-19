package apps.apidez.com.busrouting.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import apps.apidez.com.busrouting.R;
import apps.apidez.com.busrouting.activity.ChooseBusActivity;
import apps.apidez.com.busrouting.adapter.AdapterBusRouteResults;
import apps.apidez.com.busrouting.helper.Constant;
import apps.apidez.com.busrouting.interfaces.OnCallBackFragment;
import apps.apidez.com.busrouting.model.BusRoute;
import apps.apidez.com.busrouting.model.BusStopLocation;
import apps.apidez.com.busrouting.model.CompleteRoute;

public class FragmentReportResult extends Fragment implements
        GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks {
    // views
    private ListView listResults;
    private TextView shareBtn;
    private MapView mapView;
    private GoogleMap mMap;
    private ImageView indicator, indicatorShow;

    // location client
    private LocationClient mLocationClient;
    private LinearLayout animateTarget;
    private RelativeLayout closeContainer;
    private List<List<Marker>> listOfRouteMarkers = new ArrayList<>();

    // call back
    private OnCallBackFragment mCallBack;
    private boolean stateShow;

    // data
    private CompleteRoute finalResults;

    public void setFinalResults(CompleteRoute finalResults) {
        this.finalResults = finalResults;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getActivity(), this, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        // add points
        addPointsOnMaps();
        showSelectedItem(0, false);

        // reconnect
        connect();

        // set up the title
        ((ChooseBusActivity) getActivity()).getTitleOfFrag()
                .setText(getResources().getString(R.string.title_result));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBack = (OnCallBackFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCallBackFragment");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        addPointsOnMaps();

        // disconnect
        disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_result_layout, container, false);
        initView(rootView, savedInstanceState);
        return rootView;
    }

    // connect the client location
    private void connect() {
        if (mLocationClient != null && !mLocationClient.isConnected()) {
            mLocationClient.connect();
        }
    }

    // disconnect the client
    private void disconnect() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }
    }

    // init the view
    private void initView(final View rootView, Bundle bundle) {
        // list of result
        final AdapterBusRouteResults adapterBusRouteResults
                = new AdapterBusRouteResults(getActivity(), finalResults.getList());
        listResults = (ListView) rootView.findViewById(R.id.list_result);
        if (finalResults.getList().size() >= 2) {
            listResults.getLayoutParams().height
                    = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        }
        listResults.setAdapter(adapterBusRouteResults);
        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < finalResults.getList().size(); i++) {
                    finalResults.getList().get(i).setState(false);
                }
                finalResults.getList().get(position).setState(true);
                adapterBusRouteResults.notifyDataSetInvalidated();
                showSelectedItem(position, true);
            }
        });

        // share button
        shareBtn = (TextView) rootView.findViewById(R.id.share_bus);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back to main layout
                if (stateShow) {
                    getActivity().finish();
                }
            }
        });

        // set up map
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) rootView.findViewById(R.id.map_view);
        mapView.onCreate(bundle);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);
        }

        // indicator
        indicator = (ImageView) rootView.findViewById(R.id.indicator);
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateShow) animateCloseList();
            }
        });

        indicatorShow = (ImageView) rootView.findViewById(R.id.indicator_show);
        indicatorShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stateShow) animateListResult();
            }
        });

        // animating
        closeContainer = (RelativeLayout) rootView.findViewById(R.id.container_close);
        animateTarget = (LinearLayout) rootView.findViewById(R.id.container_list);
        animateListResult();
    }

    // animate the list view
    private void animateListResult() {
        float translate = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics());
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animateTarget, "translationY", translate, 0);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                closeContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        objectAnimator.start();
        stateShow = true;
    }

    // close list result
    private void animateCloseList() {
        float translate = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getResources().getDisplayMetrics());
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animateTarget, "translationY", 0, translate);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                closeContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        objectAnimator.start();
        stateShow = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constant.TAG, "Connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(Constant.TAG, "Disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(Constant.TAG, "Connection Failed");
        if (result.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                result.startResolutionForResult(getActivity(),
                        Constant.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
            Toast.makeText(getActivity(), result.getErrorCode(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // add points on map
    private void addPointsOnMaps() {
        if (mMap != null) {
            List<BusRoute> listBus = finalResults.getList();
            for (int i = 0; i < listBus.size(); i++) {
                BusRoute busRoute = listBus.get(i);
                List<Marker> list = new ArrayList<>();

                // fetch and add markers
                for (int j = 0; j < busRoute.getListOfLocations().size(); j++) {
                    BusStopLocation bus = busRoute.getListOfLocations().get(j);
                    Marker marker = mMap.addMarker(bus.getMarkerOptionUnDraggable());
                    marker.setTitle(busRoute.getName());

                    // add this bus to the route
                    list.add(marker);
                }

                // update list
                listOfRouteMarkers.add(list);
            }
        }
    }

    // highlight selected item
    private void showSelectedItem(int position, boolean animate) {
        for (int i = 0; i < listOfRouteMarkers.size(); i++) {
            List<Marker> route = listOfRouteMarkers.get(i);
            for (int j = 0; j < route.size(); j++) {
                Marker marker = route.get(j);
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin_none));
            }
        }

        List<Marker> route = listOfRouteMarkers.get(position);
        for (int i = 0; i < route.size(); i++) {
            Marker marker = route.get(i);
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin));
        }

        // close list
        Marker location = route.get(route.size() / 2);
        if (animate) {
            animateCloseList();
        }

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(new LatLng(location.getPosition().latitude, location.getPosition().longitude), 15);
        mMap.moveCamera(cameraUpdate);
    }
}