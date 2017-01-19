package apps.apidez.com.busrouting.helper;

import android.app.Service;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class UIUtils {
    public static void hideKeyboard(View view) {
        if (view == null)
            return;

        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        view.clearFocus();
    }

    public static void showKeyboard(TextView tv) {
        if (tv == null)
            return;

        InputMethodManager imm = (InputMethodManager) tv.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        // imm.viewClicked(tv);
        tv.requestFocus();
        imm.showSoftInput(tv, InputMethodManager.SHOW_IMPLICIT);
    }
}