package com.z.jk.linkagelistview.customDragGridView.Pensenter;

import android.view.MotionEvent;

/**
 * Created by $ zhoudeheng on 2016/3/8.
 * Email zhoudeheng@qccr.com
 */
public interface IGridViewPresenter {
    /**
     * 分发事件
     * @param ev
     * @return
     */
    boolean dispatchTouchEvent(MotionEvent ev);

    /**
     * 处理触摸事件
     * @param ev
     * @return
     */
    boolean onTouchEvent(MotionEvent ev);

    /**
     * 设置震动时间
     * @param milliseconds
     */
    void setVibrate(long milliseconds);

    /**
     * 绘制
     */
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec);
    void setColumnWidth(int columnWidth);
    void setHorizontalSpacing(int horizontalSpacing);
    void setNumColumns(int numColumns);
    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     * @param dragResponseMS
     */
    void setDragResponseMS(long dragResponseMS);

    /**
     * 交换动画
     */
    void animateSwitch(final int oldPosition, final int newPosition);

}
