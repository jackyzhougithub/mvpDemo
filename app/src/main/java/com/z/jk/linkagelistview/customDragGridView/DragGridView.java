package com.z.jk.linkagelistview.customDragGridView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.z.jk.linkagelistview.customDragGridView.Pensenter.DragGridViewPresenter;
import com.z.jk.linkagelistview.customDragGridView.Pensenter.IGridViewPresenter;

/**
 * Created by $ zhoudeheng on 2016/3/8.
 * Email zhoudeheng@qccr.com
 * 1，长按item隐藏，添加镜像
 * 2，移动确定位置
 * 3，镜像如果到屏幕下方，gridview整体上移
 * 4，交换，刷新
 */
public class DragGridView extends GridView implements IDrawGridView {
    private static final int SCROLL_DURATION = 10;
    private WindowManager mWindowManager;
    private IGridViewPresenter mGridViewPresenter;
    private DragViewBaseAdapter mDragAdapter;

    private int mNumColumns;
    private int mColumnWidth;
    private int mHorizontalSpacing;
    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mGridViewPresenter = new DragGridViewPresenter(context,this);
        setDefaultValue();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if(adapter instanceof DragViewBaseAdapter){
            mDragAdapter = (DragViewBaseAdapter) adapter;
        }else {
            throw new IllegalStateException("the adapter must be implements DragViewBaseAdapter");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGridViewPresenter.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mGridViewPresenter.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public void addDragView(ImageView dragView,WindowManager.LayoutParams params) {
        mWindowManager.addView(dragView, params);
    }

    @Override
    public void removeDragView(ImageView dragView) {
        mWindowManager.removeView(dragView);
    }

    @Override
    public void updateDragView(ImageView dragView, WindowManager.LayoutParams params) {
        mWindowManager.updateViewLayout(dragView, params);
    }

    @Override
    public void mySmoothScrollBy(int distance) {
        smoothScrollBy(distance, SCROLL_DURATION);
    }

    @Override
    public void switchItem(final int dragPosition, final int tempPosition) {
        mDragAdapter.reorderItems(dragPosition,tempPosition);
        mDragAdapter.setHideItem(tempPosition);
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                mGridViewPresenter.animateSwitch(dragPosition, tempPosition);
                return true;
            }
        });

    }

    /**
     * 若设置为AUTO_FIT，计算有多少列
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mGridViewPresenter.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int myPointToPosition(int x, int y) {
        return pointToPosition(x,y);
    }

    @Override
    public void stopDrag(int dragPos) {
        View view = getChildAt(dragPos - getFirstVisiblePosition());
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
        mDragAdapter.setHideItem(-1);
    }



    @Override
    public View getChildViewAt(int pos) {
        return getChildAt(pos - getFirstVisiblePosition());
    }

    @Override
    public int getViewHeight() {
        return getHeight();
    }

    @Override
    public void hideView(int pos) {
        if(mDragAdapter != null){
            mDragAdapter.setHideItem(pos);
        }
    }

    @Override
    public int getGridViewPaddingLeft() {
        return getPaddingLeft();
    }

    @Override
    public int getGridViewPaddingRight() {
        return getPaddingRight();
    }

    /**
     * 将gridview一些xml默认的值赋给P
     */
    private void setDefaultValue(){
        mGridViewPresenter.setHorizontalSpacing(mHorizontalSpacing);
        mGridViewPresenter.setNumColumns(mNumColumns);
        mGridViewPresenter.setColumnWidth(mColumnWidth);
    }

    /**
     * super 时执行，此时 mGridViewPresenter = null
     * @param numColumns
     */
    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumns = numColumns;
        if(mGridViewPresenter != null) {
            mGridViewPresenter.setNumColumns(numColumns);
        }
    }


    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
        if(mGridViewPresenter != null) {
            mGridViewPresenter.setColumnWidth(columnWidth);
        }
    }

    @Override
    public void setHorizontalSpacing(final int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        mHorizontalSpacing = horizontalSpacing;
        if(mGridViewPresenter != null) {
            mGridViewPresenter.setHorizontalSpacing(horizontalSpacing);
        }

    }

    /**
     * 设置震动时常
     */
    public void setVibrate(long milliseconds){
        mGridViewPresenter.setVibrate(milliseconds);
    }

    public void setDragResponseMS(long dragResponseMS) {
        mGridViewPresenter.setDragResponseMS(dragResponseMS);
    }
}
