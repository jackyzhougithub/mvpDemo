package com.z.jk.linkagelistview.LinkList;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/3/3.
 * Email zhoudeheng@qccr.com
 */
public class LinkModule{
    private List<LinkListBean> mList;
    private void refreshNext(List<LinkListBean> mCurrentList,int currentItemPos){
        List<LinkListBean> list = mCurrentList.get(currentItemPos).getNext();
        if(list == null) return;
        refreshNext(list,list.size()/2);

    }
}
