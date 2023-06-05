package com.lcf.goetia.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CustomView extends LinearLayout implements NestedScrollingParent2 {
    public RecyclerView recyclerView;
    private View topView;
    private int topViewHeight = 0;

    private NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        recyclerView = new RecyclerView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        addView(recyclerView);
    }

    public void setTopView(View topView) {
        this.topView = topView;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        topView.measure(MeasureSpec.makeMeasureSpec(((1 << 30) - 1), MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(((1 << 30) - 1), MeasureSpec.UNSPECIFIED));
//        int measuredHeight = topView.getMeasuredHeight();
//        params.setMargins(0, (int) (-0.5f * measuredHeight), 0, 0);
        topView.setLayoutParams(params);
        addView(topView, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (topView != null) {
            measureChildWithMargins(topView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            topViewHeight = topView.getMeasuredHeight();
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes, int type) {

    }

    @Override
    public void onStopNestedScroll(View child, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(child, type);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        //当子控件处理完后，交给父控件进行处理。
        if (dyUnconsumed < 0) {
            //表示已经向下滑动到头
            scrollBy(0, dyUnconsumed);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {

        //这里不管手势滚动还是fling都处理
        boolean hideTop = dy > 0 && getScrollY() < topViewHeight;
        boolean showTop = dy < 0 && getScrollY() >= 0 && !target.canScrollVertically(-1);
        if (hideTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }

//        if (target instanceof RecyclerView) {
//            RecyclerView rv = (RecyclerView) target;
//            if (dy > 0 && rv.canScrollVertically(1)) {
//                // 如果RecyclerView向上滚动并且可以继续滚动，将滚动事件交给RecyclerView处理
//                scrollBy(0, dy);
//                consumed[1] = dy;
//            }
////        }
    }

//    @Override
//    public void scrollBy(int x, int y) {
//        super.scrollBy(x, y);
//    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > topViewHeight) {
            y = topViewHeight;
        }
        super.scrollTo(x, y);
    }


    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev) || ev.getAction() == MotionEvent.ACTION_MOVE;
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("icv", "parent onTouchEvent : " + event.getAction()/* == MotionEvent.ACTION_MOVE*/);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("icv", "parent onInterceptTouchEvent : " + ev.getAction()/* == MotionEvent.ACTION_MOVE*/);
        return super.onInterceptTouchEvent(ev);
    }

}
