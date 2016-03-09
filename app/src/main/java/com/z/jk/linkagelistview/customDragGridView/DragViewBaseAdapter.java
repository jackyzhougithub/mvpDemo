package com.z.jk.linkagelistview.customDragGridView;

/**
 * Created by $ zhoudeheng on 2016/3/8.
 * Email zhoudeheng@qccr.com
 * 实现隐藏item
 */
public interface DragViewBaseAdapter {
    /**
     * 重新排列数据
     *
     * @param oldPosition
     * @param newPosition
     */
    void reorderItems(int oldPosition, int newPosition);


    /**
     * 设置某个item隐藏
     *
     * @param hidePosition
     */
    void setHideItem(int hidePosition);
}
