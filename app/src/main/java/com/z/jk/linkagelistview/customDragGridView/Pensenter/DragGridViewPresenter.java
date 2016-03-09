package com.z.jk.linkagelistview.customDragGridView.Pensenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.z.jk.linkagelistview.customDragGridView.IDrawGridView;
import com.z.jk.linkagelistview.customDragGridView.data.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by $ zhoudeheng on 2016/3/8.
 * Email zhoudeheng@qccr.com
 */
public class DragGridViewPresenter implements IGridViewPresenter {
    private Context mContext;
    private final IDrawGridView mDrawGridView;
    /**
     * 是否可以拖动,只有长按可以拖动
     */
    private boolean mEnableDrag;
    /**
     * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
     */
    private long mDragResponseMS = 1000;
    /**
     * 状态栏的高度
     */
    private int mStatusHeight;
    /**
     * touch 点击和移动坐标
     */
    private int mDownX;
    private int mDownY;
    private int mMoveX;
    private int mMoveY;
    /**
     * 正在拖拽的position
     */
    private int mDragPosition;
    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;
    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;
    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;
    /**
     * DragGridView距离屏幕左边的偏移量
     */
    private int mOffset2Left;
    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;
    /**
     * DragGridView自动滚动的速度
     */
    private static final int SPEED = 20;

    private long mVibrateTime ;
    /**
     * 刚开始拖拽的item对应的View
     */
    private View mStartDragItemView;
    /**
     * 用于拖拽的镜像，这里直接用一个ImageView
     */
    private ImageView mDragImageView;
    /**
     * 我们拖拽的item对应的Bitmap
     */
    private Bitmap mDragBitmap;
    private boolean mAnimationEnd;

    private WindowManager.LayoutParams mDragViewParams;

    private int mNumColumns;
    private int mColumnWidth;
    private boolean mNumColumnsSet;
    private int mHorizontalSpacing;
    public DragGridViewPresenter(Context context, IDrawGridView drawGridView) {
        mDrawGridView = drawGridView;
        init(context);
    }

    /**
     * 震动器
     */
    private Vibrator mVibrator;


    private void init(Context context) {
        mContext = context;
        mStartDragItemView = null;
        mEnableDrag = false;
        mVibrateTime = 50;
        mAnimationEnd = true;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mStatusHeight = Utils.getStatusHeight(context); //获取状态栏的高度
        if(!mNumColumnsSet){
            mNumColumns = GridView.AUTO_FIT;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dispatchActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                dispatchActionMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongTouchRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
            default:
                break;
        }
        return false;
    }

    private void dispatchActionDown(MotionEvent ev) {
        mDownX = (int) ev.getX();
        mDownY = (int) ev.getY();
        //根据按下的X,Y坐标获取所点击item的position
        mDragPosition = mDrawGridView.myPointToPosition(mDownX, mDownY);
        if (mDragPosition == AdapterView.INVALID_POSITION) {
            return;
        }

        //使用Handler延迟dragResponseMS执行mLongClickRunnable
        mHandler.postDelayed(mLongTouchRunnable, mDragResponseMS);

        //根据position获取该item所对应的View
        mStartDragItemView = mDrawGridView.getChildViewAt(mDragPosition);
        mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
        mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

        mOffset2Top = (int) (ev.getRawY() - mDownY);
        mOffset2Left = (int) (ev.getRawX() - mDownX);

        //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
        mDownScrollBorder = mDrawGridView.getViewHeight() / 5;
        //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
        mUpScrollBorder = mDrawGridView.getViewHeight() * 4 / 5;

        //开启mDragItemView绘图缓存
        mStartDragItemView.setDrawingCacheEnabled(true);
        //获取mDragItemView在缓存中的Bitmap对象
        mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
        //这一步很关键，释放绘图缓存，避免出现重复的镜像
        mStartDragItemView.destroyDrawingCache();
        mStartDragItemView.setDrawingCacheEnabled(false);
    }

    private void dispatchActionMove(MotionEvent ev) {
        int moveX = (int) ev.getX();
        int moveY = (int) ev.getY();

        //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
        if (!Utils.isTouchInItem(mStartDragItemView, moveX, moveY)) {
            mHandler.removeCallbacks(mLongTouchRunnable);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mEnableDrag || mDragImageView == null) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) ev.getX();
                mMoveY = (int) ev.getY();
                //拖动item
                onDragItem(mMoveX, mMoveY);
                break;
            case MotionEvent.ACTION_UP:
                onStopDrag();
                mEnableDrag = false;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void setVibrate(long milliseconds) {
        mVibrateTime = milliseconds;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNumColumns == GridView.AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(View.MeasureSpec.
                        getSize(widthMeasureSpec) - mDrawGridView.getGridViewPaddingLeft()
                        - mDrawGridView.getGridViewPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1)
                                * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    numFittedColumns = 1;
                }
            } else {
                numFittedColumns = 2;
            }
            mNumColumns = numFittedColumns;
        }
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        mColumnWidth = columnWidth;
    }

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
    }

    @Override
    public void setNumColumns(int numColumns) {
        mNumColumnsSet = true;
        this.mNumColumns = numColumns;
    }

    @Override
    public void setDragResponseMS(long dragResponseMS) {
        mDragResponseMS = dragResponseMS;
    }

    @Override
    public void animateSwitch(int oldPosition, int newPosition) {
        animateReorder(mDragPosition, newPosition);
        mDragPosition = newPosition;
    }


    /**
     * 长按事件，自定义长按时间
     */
    private Runnable mLongTouchRunnable = new Runnable() {
        @Override
        public void run() {
            mEnableDrag = true;
            mVibrator.vibrate(mVibrateTime); //震动一定时间
            mStartDragItemView.setVisibility(View.INVISIBLE);//隐藏该item

            //根据我们按下的点显示item镜像
            createDragImage(mDragBitmap, mDownX, mDownY);
        }
    };

    private Handler mHandler = new Handler();

    /**
     * 创建拖动镜像，加入到窗体
     *
     * @param bitmap
     * @param downX  down相对父控件的X
     * @param downY  down相对父控件的Y
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mDragImageView = new ImageView(mContext);
        mDragImageView.setImageBitmap(bitmap);
        mDragViewParams = getWindowLayoutParams(downX, downY);
        mDrawGridView.addDragView(mDragImageView, mDragViewParams);
    }

    private void removeDragImage() {
        if (mDrawGridView != null) {
            mDrawGridView.removeDragView(mDragImageView);
            mDragImageView = null;
        }
    }

    private void onStopDrag() {
        mDrawGridView.stopDrag(mDragPosition);

        removeDragImage();

    }

    private void onDragItem(int moveX, int moveY) {
        mDragViewParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mDragViewParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mDrawGridView.updateDragView(mDragImageView, mDragViewParams);
        onSwapItem(moveX, moveY);
        mHandler.post(mScrollRunnable);
    }

    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触犯GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;

            if (mMoveY > mUpScrollBorder) {
                scrollY = SPEED;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (mMoveY < mDownScrollBorder) {
                scrollY = -SPEED;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            //当我们的手指到达GridView向上或者向下滚动的偏移量的时候，
            // 可能我们手指没有移动，但是DragGridView在自动的滚动
            //所以我们在这里调用下onSwapItem()方法来交换item
            onSwapItem(mMoveX, mMoveY);
            mDrawGridView.mySmoothScrollBy(scrollY);
        }
    };

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        int tempPosition = mDrawGridView.myPointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition
                && tempPosition != AdapterView.INVALID_POSITION &&
                mAnimationEnd) {
            mDrawGridView.switchItem(mDragPosition, tempPosition);
        }
    }




    /**
     * 暂时写死，如果想要其他效果，请自己定义
     * item的交换动画效果
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;//移动方向
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = mDrawGridView.getChildViewAt(pos);
                Animator animator;
                if ((pos + 1) % mNumColumns == 0) {//列
                    animator = Utils.createTranslationAnimations(view,
                            -view.getWidth() * (mNumColumns - 1), 0,
                            view.getHeight(), 0);

                } else {
                    animator = Utils.createTranslationAnimations(view,
                            view.getWidth(), 0, 0, 0);
                }
                resultList.add(animator);
            }
        } else {
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = mDrawGridView.getChildViewAt(pos);
                Animator animator;
                if ((pos + mNumColumns) % mNumColumns == 0) {
                    animator = Utils.createTranslationAnimations(view,
                            view.getWidth() * (mNumColumns - 1), 0,
                            -view.getHeight(), 0);

                } else {
                    animator = Utils.createTranslationAnimations(view,
                            -view.getWidth(), 0, 0, 0);
                }
                resultList.add(animator);
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(300);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
            }
        });
        resultSet.start();
    }

    private WindowManager.LayoutParams getWindowLayoutParams(int downX, int downY) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = downX - mPoint2ItemLeft + mOffset2Left;
        params.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        params.alpha = 0.45f; //透明度
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        return params;
    }
}
