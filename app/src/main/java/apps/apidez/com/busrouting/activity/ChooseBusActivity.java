package apps.apidez.com.busrouting.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.apidez.com.busrouting.R;
import apps.apidez.com.busrouting.fragment.FragmentDetailRouting;
import apps.apidez.com.busrouting.fragment.FragmentDialogPicker;
import apps.apidez.com.busrouting.fragment.FragmentDropBus;
import apps.apidez.com.busrouting.fragment.FragmentReportResult;
import apps.apidez.com.busrouting.fragment.FragmentRouting;
import apps.apidez.com.busrouting.helper.Constant;
import apps.apidez.com.busrouting.interfaces.OnCallBackFragment;
import apps.apidez.com.busrouting.model.BusRoute;
import apps.apidez.com.busrouting.model.CompleteRoute;

public class ChooseBusActivity extends FragmentActivity
        implements OnCallBackFragment, FragmentDialogPicker.OnPickerCallBack {
    private List<BusRoute> listBusRoute;
    private TextView title;
    private int numBus;
    private FragmentRouting fragmentRouting;
    private boolean checkWarning;
    public TextView getTitleOfFrag() {
        return title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bus);
        if (savedInstanceState == null) {
            fragmentRouting = new FragmentRouting();
            fragmentRouting.setArguments(new Bundle());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragmentRouting)
                    .commit();
        }

        // init the bus route
        listBusRoute = new ArrayList<>();

        // setup
        setUpView();
        setUpListener();
    }

    // add listener when changed fragment
    public void setUpListener() {
        final FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
            }
        });
    }

    // transit to new layout
    public void transitFragment(Fragment frag, boolean backStack) {
        // move to new edit view
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right);
        trans.replace(R.id.container, frag);
        if (backStack) trans.addToBackStack(null);
        trans.commit();
    }

    public void setUpView() {
        // set up views
        title = (TextView) findViewById(R.id.title_top);
        ImageView backBtn = (ImageView) findViewById(R.id.icon_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDropBus(String nameOfBus, String nameOfDes, int numberOfBus, int id) {

        // create fragment
        FragmentDropBus fragment = new FragmentDropBus();

        // push data
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.NUMBER_OF_BUS, numberOfBus);
        bundle.putString(Constant.NAME_OF_BUS, nameOfBus);
        bundle.putString(Constant.NAME_OF_DESTINATION, nameOfDes);
        bundle.putInt(Constant.ID_OF_BUS, id);
        bundle.putInt(Constant.NUMBER_CURRENT_BUS, listBusRoute.size());
        fragment.setArguments(bundle);

        // start transit
        transitFragment(fragment, true);
    }

    @Override
    public void onDetail(int numBus, boolean checkWarning, int id) {
        // update data
        this.numBus = numBus;
        this.checkWarning = checkWarning;

        // create fragment
        FragmentDetailRouting fragment = new FragmentDetailRouting();

        // push data
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ID_OF_BUS, id);
        bundle.putInt(Constant.NUMBER_OF_BUS, numBus);
        bundle.putBoolean(Constant.CHECK_WARNING, checkWarning);
        fragment.setArguments(bundle);

        // start transit
        transitFragment(fragment, true);
    }

    @Override
    public void onUpdateBusRoute(BusRoute busRoute) {
        listBusRoute.add(busRoute);
    }

    @Override
    public void removeBus(int pos) {
        if (pos == listBusRoute.size() - 1)
            listBusRoute.remove(pos);
    }

    @Override
    public void onReport() {
        // create data
        CompleteRoute completeRoute = new CompleteRoute(numBus, checkWarning, listBusRoute);

        // start fragment
        FragmentReportResult frag = new FragmentReportResult();
        frag.setFinalResults(completeRoute);
        transitFragment(frag, true);
    }

    @Override
    public void onPick() {
        // start fragment
        DialogFragment dialog = new FragmentDialogPicker();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onSetNumber(int numberOfBus) {
        fragmentRouting.num = numberOfBus;
        fragmentRouting.numberOfBus.setText(String.valueOf(numberOfBus));
    }
}
