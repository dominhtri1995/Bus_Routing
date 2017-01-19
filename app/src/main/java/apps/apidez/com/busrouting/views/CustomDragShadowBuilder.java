package apps.apidez.com.busrouting.views;

import android.graphics.Point;
import android.view.View;

public class CustomDragShadowBuilder extends View.DragShadowBuilder {
    private Point _offset;

    public CustomDragShadowBuilder(View view, Point offset) {
        // Stores the View parameter passed to myDragShadowBuilder.
        super(view);

        // Save the offset :
        _offset = offset;
    }

    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        // Set the shadow size :
        size.set(getView().getWidth(), getView().getHeight());

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(_offset.x, _offset.y);
    }
}
