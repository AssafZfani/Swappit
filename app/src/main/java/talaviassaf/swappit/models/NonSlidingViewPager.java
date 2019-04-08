package talaviassaf.swappit.models;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

@SuppressWarnings("EmptyMethod")

public class NonSlidingViewPager extends ViewPager {

    public NonSlidingViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN)
            performClick();

        return false;
    }

    @Override
    public boolean performClick() {

        return super.performClick();
    }
}
