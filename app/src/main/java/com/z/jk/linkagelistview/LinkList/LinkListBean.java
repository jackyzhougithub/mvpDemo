package com.z.jk.linkagelistview.LinkList;

import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/2/29.
 * Email zhoudeheng@qccr.com
 * 递归的多级数据
 */
public final class LinkListBean {

    private String mName;
    private long mId;
    private long mImgIndex;//右边图片如箭头，
    List<LinkListBean> mNext;
    public LinkListBean(String name,long id,long imgIndex,List< LinkListBean> list){
        mId = id;
        mName = name;
        mImgIndex = imgIndex;
        mNext = list;
    }

    public String getName() {
        return mName;
    }

    public long getId() {
        return mId;
    }

    public long getImgIndex() {
        return mImgIndex;
    }

    public List<LinkListBean> getNext() {
        return mNext;
    }

}
