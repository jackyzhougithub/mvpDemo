package com.z.jk.linkagelistview.LinkList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by $ zhoudeheng on 2016/2/29.
 * Email zhoudeheng@qccr.com
 * p 处理了m 的数据？
 */
public class LinkagePresenter implements ILinkageListViewPresenter {
    private Timer mTimer;
    private MyTimerTask mTask;
    private int mViewHeight;
    private int mViewWidth;

    private float mMaxTextSize = 20;
    private float mMinTextSize = 10;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;
    private Paint mPaint;
    private List<LinkListBean> mList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 2.8f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 2;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    ILinkageListView mLinkageView;

    public LinkagePresenter(@NonNull ILinkageListView linkageListView,Paint paint){
        mLinkageView = linkageListView;
        mPaint = paint;
        mTimer = new Timer();
    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    @Override
    public void drawData(@NonNull Canvas canvas) {
        if (!isInit)return;
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mList.get(mCurrentSelected).getName(), x, baseline, mPaint);
        // 绘制上方data
        for (int i = 1; (mCurrentSelected - i) >= 0; i++)
        {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelected + i) < mList.size(); i++)
        {
            drawOtherText(canvas, i, 1);
        }

    }

    @Override
    public void setSelected(int selected) {
        // 如果设置选中的index为0或者1，在函数drawData()中会出现上方少绘制的情况，因此特殊处理
        if (selected == 0) {
            LinkListBean tail = mList.get(mList.size() - 1);
            mList.remove(mList.size() - 1);
            mList.add(0, tail);

            tail = mList.get(mList.size() - 1);
            mList.remove(mList.size() - 1);
            mList.add(0, tail);

            selected = 2;

        } else if (selected == 1) {
            LinkListBean tail = mList.get(mList.size() - 1);
            mList.remove(mList.size() - 1);
            mList.add(0, tail);

            selected = 2;

        } else if (selected == mList.size()-2) { // 如果设置选中的index为最后一个或者最后第二个，在函数drawData()中会出现下方少绘制的情况，因此特殊处理
            LinkListBean head = mList.get(0);
            mList.remove(0);
            mList.add(mList.size(), head);

            selected = mList.size() - 3;
        } else if (selected == mList.size() - 1) {
            LinkListBean head = mList.get(0);
            mList.remove(0);
            mList.add(mList.size(), head);

            head = mList.get(0);
            mList.remove(0);
            mList.add(mList.size(), head);

            selected = mList.size() - 3;
        }

        mCurrentSelected = selected;
        mLinkageView.myInvalidate();
    }

    @Override
    public void onMeasure(int width, int height) {
        mViewHeight = width;
        mViewWidth = height;
        // 按照View的高度计算字体大小,可以提供不同策略
        mMaxTextSize = mViewHeight / 8.0f;
        mMinTextSize = mMaxTextSize / 2f;
        isInit = true;//第一次width 0 没有measure完成
        mLinkageView.myInvalidate();
    }

    @Override
    public void setData(@NonNull List<LinkListBean> list) {
        mList = list;
        mCurrentSelected = list.size()/2;
        mLinkageView.myInvalidate();
    }

    @Override
    public void setColorText(int colorText) {
        mPaint.setColor(colorText);
    }

    @Override
    public LinkListBean getSelectetedItem() {
        if(mList != null){
            return mList.get(mCurrentSelected);
        }
        return null;//避免使用null？
    }

    @Override
    public int getCurrentSelectedItem() {
        return mCurrentSelected;
    }


    /**
     * @param canvas
     * @param position
     *            距离mCurrentSelected的差值
     * @param type
     *            1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(@NonNull Canvas canvas, int position, int type)
    {
        float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveLen);
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mList.get(mCurrentSelected + type * position).getName(),
                (float) (mViewWidth / 2.0), baseline, mPaint);
    }

    /**
     * 抛物线
     *
     * @param zero
     *            零点坐标
     * @param x
     *            偏移量
     * @return scale
     */
    private float parabola(float zero, float x)
    {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    private void doDown(@NonNull MotionEvent event)
    {
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(@NonNull MotionEvent event)
    {

        mMoveLen += (event.getY() - mLastDownY);

        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2)
        {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2)
        {
            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
        }

        mLastDownY = event.getY();
        mLinkageView.myInvalidate();
    }

    private void doUp(@NonNull MotionEvent event)
    {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001)
        {
            mMoveLen = 0;
            return;
        }
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(mUpdateHandler);
        mTimer.schedule(mTask, 0, 10);
    }

    private MyHandler mUpdateHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        WeakReference<LinkagePresenter> mLk;
        public MyHandler(LinkagePresenter lk){
            mLk = new WeakReference<>(lk);
        }
        @Override
        public void handleMessage(Message msg) {
            LinkagePresenter linkageview = mLk.get();
            if(linkageview == null) return;
            if (Math.abs(linkageview.mMoveLen) < SPEED)
            {
                linkageview.mMoveLen = 0;
                if (linkageview.mTask != null)
                {
                    linkageview.mTask.cancel();
                    linkageview.mTask = null;
                    linkageview.mLinkageView.performSelect();
                }
            } else {
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                linkageview.mMoveLen = linkageview.mMoveLen - linkageview.mMoveLen / Math.abs(linkageview.mMoveLen) * SPEED;
            }
            linkageview.mLinkageView.myInvalidate();
        }
    }

    private void moveHeadToTail()
    {
        LinkListBean head = mList.get(0);
        mList.remove(0);
        mList.add(head);
    }

    private void moveTailToHead()
    {
        LinkListBean tail = mList.get(mList.size() - 1);
        mList.remove(mList.size() - 1);
        mList.add(0, tail);
    }


    class MyTimerTask extends TimerTask
    {
        Handler handler;

        public MyTimerTask(Handler handler)
        {
            this.handler = handler;
        }

        @Override
        public void run()
        {
            handler.sendMessage(handler.obtainMessage());
        }

    }
}
