package apps.apidez.com.busrouting.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import apps.apidez.com.busrouting.R;

public class FragmentDialogPicker extends DialogFragment {
    private OnPickerCallBack mListener;
    public interface OnPickerCallBack {
        public void onSetNumber(int numberOfBus);
    }

    public FragmentDialogPicker() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View root = inflater.inflate(R.layout.numberpicker_dialog, null);

        // setup picker
        final NumberPicker picker = (NumberPicker) root.findViewById(R.id.bus_picker);
        picker.setMinValue(1);
        picker.setMaxValue(5);
        picker.setValue(2);

        builder.setView(root)
                // Add action buttons
                .setTitle(R.string.set_number_of_bus)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSetNumber(picker.getValue());
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnPickerCallBack) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
