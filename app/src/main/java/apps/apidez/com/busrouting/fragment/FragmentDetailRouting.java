package apps.apidez.com.busrouting.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import apps.apidez.com.busrouting.R;
import apps.apidez.com.busrouting.activity.ChooseBusActivity;
import apps.apidez.com.busrouting.helper.Constant;
import apps.apidez.com.busrouting.helper.UIUtils;
import apps.apidez.com.busrouting.interfaces.OnCallBackFragment;
import apps.apidez.com.busrouting.views.ProgressBarAnimation;

public class FragmentDetailRouting extends Fragment {
    private OnCallBackFragment mCallBack;
    private EditText editNameBus, editNameDes;
    private int numberOfBus;
    private ProgressBar progressBus;
    private boolean stateBus, stateDes;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
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
    public void onResume() {
        super.onResume();
        ((ChooseBusActivity) getActivity()).getTitleOfFrag()
                .setText(getResources().getString(R.string.title_create_bus) + " "
                        + String.valueOf(getArguments().getInt(Constant.ID_OF_BUS)));
    }

    @Override
    public void onPause() {
        super.onPause();
        UIUtils.hideKeyboard(editNameBus);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // root view
        View rootView = inflater.inflate(R.layout.fragment_detail_route, container, false);

        // get data
        numberOfBus = getArguments().getInt(Constant.NUMBER_OF_BUS);

        // setup views
        setUpView(rootView);
        return rootView;
    }

    // init all the animation
    public void setUpView(View view) {
        // next action
        final TextView next = (TextView) view.findViewById(R.id.title_action);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateDes && stateBus) {
                    int id = getArguments().getInt(Constant.ID_OF_BUS);
                    mCallBack.onDropBus(editNameBus.getText().toString(),
                            editNameDes.getText().toString(), numberOfBus, id);
                }
            }
        });

        // title completed
        TextView titleView = (TextView) view.findViewById(R.id.title_completed);
        int currentBus = Constant.getSumBus(getActivity()) - getArguments().getInt(Constant.NUMBER_OF_BUS);
        titleView.setText(currentBus + "/" + Constant.getSumBus(getActivity())
                + " " + getResources().getString(R.string.complete));

        // progress bus1
        progressBus = (ProgressBar) view.findViewById(R.id.progress_bus);
        progressBus.setMax(100);
        int current = 100 - 100 * getArguments().getInt(Constant.NUMBER_OF_BUS)
                / Constant.getSumBus(getActivity());
        int unit = 100 / Constant.getSumBus(getActivity());
        if (current > 0) {
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBus, current - unit, current);
            anim.setDuration(500);
            progressBus.startAnimation(anim);
        }


        // edit text for bus name
        editNameBus = (EditText) view.findViewById(R.id.editBus);
        editNameBus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stateBus = s.length() != 0;
                if (stateDes && stateBus) {
                    next.setBackgroundResource(R.drawable.button_background_submit);
                } else {
                    next.setBackgroundResource(R.drawable.button_background_unactive);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // edit text for destination
        editNameDes = (EditText) view.findViewById(R.id.editDes);
        editNameDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stateDes = s.length() != 0;
                if (stateDes && stateBus) {
                    next.setBackgroundResource(R.drawable.button_background_submit);
                } else {
                    next.setBackgroundResource(R.drawable.button_background_unactive);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}