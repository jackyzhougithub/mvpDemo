package com.z.jk.linkagelistview.LinkSelect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.z.jk.linkagelistview.LinkList.LinkListBean;
import com.z.jk.linkagelistview.LinkList.LinkageListListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/2/29.
 * Email zhoudeheng@qccr.com
 */
public final class LinkSelectView extends LinearLayout implements ILinkSelectView {
    private boolean isInit = false;
    private int mViewHeight;
    private int mViewWidth;
    private List<LinkListBean> mList;
    private Context mContext;
    private static final int FIRSTITEMINDEX = 0;
    private List<Integer> mSelectedIds;

    public LinkSelectView(Context context) {
        super(context);
        init(context);
    }

    public LinkSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInit) {
            mViewHeight = getMeasuredHeight();
            mViewWidth = getMeasuredWidth();
        }
        isInit = true;

    }

    private void init(Context context) {
        mContext = context;
        this.setGravity(Gravity.CENTER);
        this.setOrientation(HORIZONTAL);
    }

    private void initView() {
        mSelectedIds = new ArrayList<>();
        addLinkListView(FIRSTITEMINDEX,mList);
    }

    private void addLinkListView(final int index,List<LinkListBean> list){
        LinkageListListView listView = new LinkageListListView(mContext,index);
        listView.setData(list);
        listView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,1.0f));

        LinkageListListView.IOnSelectListener listener = new LinkageListListView.IOnSelectListener() {
            @Override
            public void onSelect(long id, String name) {
                //List<LinkListBean> list = mList.get()
            }
        };
        listView.addSelectListener(listener);
        this.addView(listView);
    }



    public void setData(@NonNull List<LinkListBean> list, int level) {
        mList = list;
        initView();
    }

    @Override
    public void refreshNextLinkListView(List<LinkListBean> nextList, int currentLinkListViewIndex) {
        int count = getChildCount();//当前有几个linklistview
        if(count <= currentLinkListViewIndex ){
            //需要添加view
            addLinkListView(currentLinkListViewIndex+1,nextList);
        }else {
            //从新设置该view的数据
            View view = getChildAt(currentLinkListViewIndex);
            if(view instanceof LinkageListListView){
                LinkageListListView listView = (LinkageListListView) view;
                listView.setData(nextList);
            }
        }
    }
}
