package com.z.jk.linkagelistview.LinkList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/2/29.
 * Email zhoudeheng@qccr.com
 * 仿IOS的滚轮效果的多级联动基础view
 * 原理，drawText
 * 后续是否支持drawimage
 * 该view不可再被继承
 */
public final class LinkageListListView extends View implements ILinkageListView {
    private final String TAG = "LinkageListListView";

    private List<LinkListBean> mList;
    private int mIndex;

    List<IOnSelectListener> mOnSelectListeners;
    ILinkageListViewPresenter mLinkagePresenter;
    public LinkageListListView(Context context,int index) {
        super(context);
        mIndex = index;
        init();
    }

//    public LinkageListListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }

    private void init(){
        mList = new ArrayList<>();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        int colorText = 0x333333;
        paint.setColor(colorText);

        mLinkagePresenter = new LinkagePresenter(this,paint);
    }

    public void setColorText(int colorText) {
        mLinkagePresenter.setColorText(colorText);
    }

    public void setData(@NonNull List<LinkListBean> list){

        mLinkagePresenter.setData(list);
    }

    public LinkListBean getSelectetedItem(){
        return mLinkagePresenter.getSelectetedItem();
    }

    public void addSelectListener(IOnSelectListener listener){
        if(mOnSelectListeners == null){
            mOnSelectListeners = new ArrayList<>();
        }
        mOnSelectListeners.add(listener);
    }

    public void removeSelectListenser(IOnSelectListener listener){
        if(mOnSelectListeners == null || mOnSelectListeners.size() == 0) return;
        mOnSelectListeners.remove(listener);
    }

    public void setSelected(int selected)
    {
        mLinkagePresenter.setSelected(selected);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLinkagePresenter.onMeasure(getMeasuredWidth(),getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // 根据index绘制view
        mLinkagePresenter.drawData(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
       return mLinkagePresenter.onTouchEvent(event);
    }

    @Override
    public void myInvalidate() {
        invalidate();
    }

    @Override
    public void performSelect() {
        if (mOnSelectListeners == null) {
            return;
        }
        for (IOnSelectListener lisenter:mOnSelectListeners
                ) {
//                lisenter.onSelect(mList.get(mLinkagePresenter.getCurrentSelectedItem()).getId(),
//                        mList.get(mLinkagePresenter.getCurrentSelectedItem()).getName());
        }
    }


    public interface IOnSelectListener
    {
        void onSelect(long id,String name);
    }
}
