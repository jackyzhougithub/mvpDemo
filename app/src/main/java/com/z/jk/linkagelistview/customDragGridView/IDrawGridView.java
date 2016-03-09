package com.z.jk.linkagelistview.customDragGridView;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by $ zhoudeheng on 2016/3/8.
 * Email zhoudeheng@qccr.com
 * p层回调v
 *
 */
public interface IDrawGridView {
    /**
     * 添加镜像
     */
    void addDragView(ImageView dragView,WindowManager.LayoutParams params);

    /**
     * 移除镜像
     */
    void removeDragView(ImageView dragView);

    /**
     * 更新镜像位置
     */
    void updateDragView(ImageView dragView,WindowManager.LayoutParams params);

    void mySmoothScrollBy(int distance);

    /**
     * 交换item
     * @param dragPosition
     * @param tempPosition
     */
    void switchItem(int dragPosition, int tempPosition);

    /**
     * 根据位坐标获得位置
     * @param x
     * @param y
     * @return
     */
    int myPointToPosition(int x,int y);

    /**
     * 停止拖拽
     * @param dragPos
     */
    void stopDrag(int dragPos);

    /**
     * 获得某个位置的view
     * @param pos
     * @return
     */
    View getChildViewAt(int pos);

    /**
     * 获得view的高度
     * @return
     */
    int getViewHeight();

    /**
     * 隐藏这个位置的view
     * @param pos
     */
    void hideView(int pos);

    int getGridViewPaddingLeft();
    int getGridViewPaddingRight();
}
