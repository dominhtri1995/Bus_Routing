package apps.apidez.com.busrouting.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.IntentSender;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import apps.apidez.com.busrouting.R;
import apps.apidez.com.busrouting.activity.ChooseBusActivity;
import apps.apidez.com.busrouting.helper.Constant;
import apps.apidez.com.busrouting.interfaces.OnCallBackFragment;
import apps.apidez.com.busrouting.model.BusRoute;
import apps.apidez.com.busrouting.model.BusStopLocation;
import apps.apidez.com.busrouting.views.CustomDragShadowBuilder;

public class FragmentDropBus extends Fragment implements
        GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        LocationListener, View.OnDragListener {
    private OnCallBackFragment mCallBack;
    private GoogleMap mMap;
    private MapView mapView;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;

    private TextView submitBtn;
    private ImageView busPin;
    private ImageView recycleBin;
    private TextView messageText;

    private CameraPosition cp;
    private List<BusStopLocation> lists;

    private boolean animate = false;
    private int countBus = 0;
    private int countDrop = 0;
    private boolean showData = true;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // init client and map
        lists = new ArrayList<>();
        mLocationClient = new LocationClient(getActivity(), this, this);
        initLocationRequest();
        countBus = getArguments().getInt(Constant.NUMBER_OF_BUS);
    }

    // connect the client location
    private void connect() {
        if (mLocationClient != null && !mLocationClient.isConnected()) {
            mLocationClient.connect();
        }
    }

    // request listener
    private void requestUpdateLocation() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            if (!animate) {
                mLocationClient.requestLocationUpdates(mLocationRequest, this);
            }
        }
    }

    // remove update
    private void removeUpdateLocation() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
    }

    // disconnect the client
    private void disconnect() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }
    }

    // init location request
    public void initLocationRequest() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();

        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the update interval to 10 seconds
        mLocationRequest.setInterval(Constant.UPDATE_INTERVAL);

        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(Constant.FASTEST_INTERVAL);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_drop_bus_layout, container, false);
        initView(rootView, bundle);
        return rootView;
    }

    // init the view
    private void initView(final View rootView, Bundle bundle) {
        // recycle
        recycleBin = (ImageView) rootView.findViewById(R.id.recycle);

        // message text
        messageText = (TextView) rootView.findViewById(R.id.message);

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

        // button action
        submitBtn = (TextView) rootView.findViewById(R.id.title_action);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBus();
                createBusRouteAndSubmit();
            }
        });

        // implement bus pin
        busPin = (ImageView) rootView.findViewById(R.id.bus_pin);
        busPin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // send drag data
                        ClipData data =
                                ClipData.newPlainText("DragData", (String) v.getTag());

                        // shadow builder
                        CustomDragShadowBuilder builder =
                                new CustomDragShadowBuilder(v, new Point(v.getWidth() / 2, v.getHeight()));

                        // start dragging
                        v.startDrag(data, builder, v, 0);
                        break;
                }
                return true;

            }
        });

        // drag surface
        RelativeLayout rel = (RelativeLayout) rootView.findViewById(R.id.container);
        rel.setOnDragListener(this);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mapView.getMap();
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);

            // add points
            addMapPoints();
        }

        // drag the marker
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            int x, y;

            @Override
            public void onMarkerDragStart(Marker marker) {
                recycleBin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Point point = mMap.getProjection().toScreenLocation(marker.getPosition());
                x = point.x;
                y = point.y;
                if (recycleBin.getX() - recycleBin.getWidth() / 2 < x
                        && x < recycleBin.getX() + recycleBin.getWidth() * 3 / 2
                        && recycleBin.getY() - recycleBin.getHeight() / 2 < y
                        && y < recycleBin.getY() + recycleBin.getHeight() * 3 / 2) {
                    recycleBin.setBackgroundResource(R.drawable.recycle_open);
                } else {
                    recycleBin.setBackgroundResource(R.drawable.recycle);
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Point point = mMap.getProjection().toScreenLocation(marker.getPosition());
                x = point.x;
                y = point.y;
                if (recycleBin.getX() < x && x < recycleBin.getX() + recycleBin.getWidth()
                        && recycleBin.getY() < y && y < recycleBin.getY() + recycleBin.getHeight()) {
                    recycleBin.setBackgroundResource(R.drawable.recycle_open);

                    // removing
                    marker.remove();
                    removeMarker(marker.getId());
                    removeDrop();
                } else {
                    updateMarker(marker);
                }
                recycleBin.setBackgroundResource(R.drawable.recycle);
                recycleBin.setVisibility(View.INVISIBLE);
            }
        });

        // create pin
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
//                mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin))
//                        .position(latLng));
//                createAndAddNewMarker(latLng);
            }
        });

        // show data
        if (!showData) {
            rootView.findViewById(R.id.use_last_data).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.use_last_data).setVisibility(View.VISIBLE);
        }

        // yes no button for old data
        TextView yesBtn = (TextView) rootView.findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.use_last_data).setVisibility(View.GONE);
                showData = false;
            }
        });

        TextView noBtn = (TextView) rootView.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.use_last_data).setVisibility(View.GONE);
                showData = false;
            }
        });

    }

    // remove marker
    private void removeMarker(String id) {
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId().equals(id)) {
                lists.remove(i);
                break;
            }
        }
    }

    // continue to the next bus
    private void nextBus() {
        if (countDrop >= 3) {
            if (countBus > 1) {
                int id = getArguments().getInt(Constant.ID_OF_BUS);
                mCallBack.onDetail(--countBus, getArguments().getBoolean(Constant.CHECK_WARNING), id + 1);
            } else {
                mCallBack.onReport();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // remove bus if needed
        mCallBack.removeBus(getArguments().getInt(Constant.NUMBER_CURRENT_BUS));

        // back to count bus
        countBus = getArguments().getInt(Constant.NUMBER_OF_BUS);
        if (countDrop >= 3) {
            submitBtn.setBackgroundResource(R.drawable.button_background_submit);
        }

        // resume the map
        mapView.onResume();
        addMapPoints();

        // reconnect
        connect();

        // get back the camera
        if (cp != null)
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

        // set up the title
        ((ChooseBusActivity) getActivity()).getTitleOfFrag()
                .setText(getResources().getString(R.string.title_drop_pin) + " "
                        + String.valueOf(getArguments().getInt(Constant.ID_OF_BUS)));
    }

    @Override
    public void onPause() {
        super.onPause();

        // get camera position
        cp = mMap.getCameraPosition();

        // pause the map
        mapView.onPause();

        // disconnect the client
        disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(Constant.TAG, "Connected");
        requestUpdateLocation();
    }

    @Override
    public void onDisconnected() {
        Log.d(Constant.TAG, "Disconnected");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!animate) {
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.
                    newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
            mMap.moveCamera(cameraUpdate);
            animate = true;

            // remove update
            removeUpdateLocation();
        }
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

    // run animation
    private void runAnimNewPin() {
        busPin.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pin_new_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                busPin.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                messageText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        busPin.startAnimation(animation);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d(Constant.TAG, "STARTED");
                runAnimNewPin();
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d(Constant.TAG, "ENTERED");
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d(Constant.TAG, "ENDED");
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d(Constant.TAG, "EXITED");
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.d(Constant.TAG, "DRAG LOCATION");
                break;
            case DragEvent.ACTION_DROP:
                Log.d(Constant.TAG, "DROPPED");
                LatLng point = mMap.getProjection().fromScreenLocation(
                        new Point(x, y - busPin.getHeight() / 3));
                createAndAddNewMarker(point);

                break;
        }
        return true;
    }

    // add drop()
    public void addDrop() {
        countDrop++;
        if (countDrop >= 3) {
            submitBtn.setBackgroundResource(R.drawable.button_background_submit);
        }
        updateMessage();
    }

    // remove drop
    private void removeDrop() {
        countDrop--;
        if (countDrop < 3) {
            submitBtn.setBackgroundResource(R.drawable.button_background_unactive);
        }
        updateMessage();
    }

    // update message
    private void updateMessage() {
        // message pop up
        if (countDrop == 2) {
            messageText.setText(getResources().getString(R.string.drag_one_more));
        } else if (countDrop == 1) {
            messageText.setText(getResources().getString(R.string.drag_two_more));
        } else if (countDrop == 0) {
            messageText.setText(getResources().getString(R.string.ask_for_drag));
        } else {
            messageText.setText(getResources().getString(R.string.drag_more));
        }
    }

    // add the points back
    private void addMapPoints() {
        if (mMap != null) {
            mMap.clear();
            for (int i = 0; i < lists.size(); i++) {
                BusStopLocation marker = lists.get(i);
                mMap.addMarker(marker.getMarkerOption());
            }
        }
    }

    // update marker
    private void updateMarker(Marker marker) {
        BusStopLocation location = null;
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId().equals(marker.getId())) {
                location = lists.get(i);
                break;
            }
        }
        if (location != null) {
            location.setLatitude(marker.getPosition().latitude);
            location.setLongitude(marker.getPosition().longitude);
        }
    }

    // create new marker on map
    private void createAndAddNewMarker(LatLng point) {
        if (mMap != null) {
            // create new marker on mad
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin))
                    .draggable(true));

            // create model
            BusStopLocation model = new BusStopLocation(marker.getId(), point.latitude, point.longitude);
            lists.add(model);
            addDrop();
        }
    }

    // create bus route and submit
    private void createBusRouteAndSubmit() {
        String nameOfBus = getArguments().getString(Constant.NAME_OF_BUS);
        String destination = getArguments().getString(Constant.NAME_OF_DESTINATION);
        BusRoute route = new BusRoute(nameOfBus, destination, lists);
        mCallBack.onUpdateBusRoute(route);
    }
}