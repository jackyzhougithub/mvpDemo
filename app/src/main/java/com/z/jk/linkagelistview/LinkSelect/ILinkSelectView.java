package com.z.jk.linkagelistview.LinkSelect;

import com.z.jk.linkagelistview.LinkList.LinkListBean;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/3/3.
 * Email zhoudeheng@qccr.com
 */
public interface ILinkSelectView {
    /**
     * 刷新或添加下一个控件的数据
     * @param nextList 下一个控件数据
     * @param currentLinkListViewIndex 当前滑动linklistview的下标
     */
    void refreshNextLinkListView(List<LinkListBean> nextList,int currentLinkListViewIndex);
}
