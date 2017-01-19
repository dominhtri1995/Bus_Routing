package apps.apidez.com.busrouting.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import apps.apidez.com.busrouting.R;
import apps.apidez.com.busrouting.helper.Constant;
import apps.apidez.com.busrouting.helper.UIUtils;
import apps.apidez.com.busrouting.interfaces.OnCallBackFragment;

public class FragmentRouting extends Fragment {
    private OnCallBackFragment mCallBack;
    public TextView numberOfBus;
    private boolean stateBus = true;
    private CheckBox checkWarning;
    private int id = 1;
    public int num = 1;
    private ImageView bigPin, smallPin;

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
        numberOfBus.setText(String.valueOf(num));
    }

    @Override
    public void onPause() {
        super.onPause();
        UIUtils.hideKeyboard(numberOfBus);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_routing_layout, container, false);
        setUpView(rootView);
        return rootView;
    }

    // init all the animation
    public void setUpView(View view) {
        // action next
        final TextView next = (TextView) view.findViewById(R.id.title_action);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateBus) {
                    mCallBack.onDetail(
                            Integer.valueOf(numberOfBus.getText().toString()), checkWarning.isChecked(), id);

                    // store
                    Constant.setSumBus(getActivity(), Integer.valueOf(numberOfBus.getText().toString()));
                }
            }
        });

        // checker
        checkWarning = (CheckBox) view.findViewById(R.id.checkWarning);

        // edit text
        numberOfBus = (TextView) view.findViewById(R.id.numBus);
        numberOfBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onPick();
            }
        });

        // animation
        bigPin = (ImageView) view.findViewById(R.id.big_pin);
        smallPin = (ImageView) view.findViewById(R.id.small_pin);
        playAnimation();
    }

    public void playAnimation() {
        // animation for small pin
        final Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_small_pin);
        animation2.setFillAfter(true);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                smallPin.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // animation for large pin
        final Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_big_pin);
        animation1.setFillAfter(true);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                smallPin.startAnimation(animation2);
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bigPin.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bigPin.startAnimation(animation1);

    }

}