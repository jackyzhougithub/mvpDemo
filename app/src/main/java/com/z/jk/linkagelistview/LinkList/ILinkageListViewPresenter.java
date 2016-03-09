package com.z.jk.linkagelistview.LinkList;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/2/29.
 * Email zhoudeheng@qccr.com
 */
public interface ILinkageListViewPresenter {
    boolean onTouchEvent(MotionEvent event);
    void drawData(Canvas canvas);
    void setSelected(int selected);
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec);
    void setData(@NonNull List<LinkListBean> list);
    void setColorText(int colorText);
    LinkListBean getSelectetedItem();
    int getCurrentSelectedItem();
}
