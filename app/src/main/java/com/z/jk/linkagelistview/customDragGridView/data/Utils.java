package com.z.jk.linkagelistview.customDragGridView.data;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

/**
 * Created by $ zhoudeheng on 2016/3/8.
 * Email zhoudeheng@qccr.com
 */
public class Utils {
    private static final String TAG = "Utils";
    private Utils(){
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                Log.e(TAG,"getStatusHeight"+e.toString());//如果+频繁使用用StringBuilder替换
            }
        }
        return statusHeight;
    }

    /**
     * 是否点击在GridView的item上面
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    public static boolean isTouchInItem(View dragView, int x, int y){
        if(dragView == null){
            return false;
        }
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if((x < leftOffset || x > leftOffset + dragView.getWidth())
                ||(y < topOffset || y > topOffset + dragView.getHeight())){
            return false;
        }

        return true;
    }

    /**
     * 创建移动动画
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    public static AnimatorSet createTranslationAnimations(View view, float startX,
                                                    float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }
}
