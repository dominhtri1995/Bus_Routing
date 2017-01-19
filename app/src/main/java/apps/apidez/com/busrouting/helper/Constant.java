package apps.apidez.com.busrouting.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Constant {
    public static final String NUMBER_OF_BUS = "numberOfBus";
    public static final String CHECK_WARNING = "checkWarning";
    public static final String NAME_OF_BUS = "nameOfBus";
    public static final String NAME_OF_DESTINATION = "nameOfDes";
    public static final String ID_OF_BUS = "busId";
    public static final String NUMBER_CURRENT_BUS = "currentBuses";

    public static final String TAG = "Bus Routing";

    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 900;

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 60;

    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // name
    public static final String SHARE_PREF_NAME = "share_pref_name";

    // save number of bus
    public static synchronized void setSumBus(Context context, int id) {
        SharedPreferences prefs = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt("sum_of_bus", id).apply();
    }

    public static int getSumBus(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        int regId = prefs.getInt("sum_of_bus", 2);
        return regId;
    }
}
