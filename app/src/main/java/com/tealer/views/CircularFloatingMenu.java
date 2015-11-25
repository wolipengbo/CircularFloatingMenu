package com.tealer.views;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.tealer.circularfloatingmenudemo.R;

/**
 * @author lipengbo
 * @date 2015/11/25
 * @intro 简介
 */
public class CircularFloatingMenu extends RelativeLayout {
    private static final String TAG = "CircularFloatingMenu";
    int mItemCount = 0;
    private OnItemClickListener mOnItemClickListener;
    boolean mIsOpen = false;
    int mRadius;// 弹出Item的半径
    int mDegrees;// Items的扇形大小(角度)
    int mStartDegree;// item从哪个位置开始排列(角度)
    boolean mIsItemClickClose;// item点击时是否缩回去
    View mVMenu;
    View mvBackgroud;
    float mPerDegree;
    boolean mInited = false;
    Interpolator outInterpolator = new OvershootInterpolator();
    Interpolator inInterpolator = new AnticipateInterpolator();
    float defaultRotation = -180;
    float defaultAlpha = 0f;
    public CircularFloatingMenu(Context context) {
        super(context);
    }

    public CircularFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public CircularFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
    }

    /**
     * 设置菜单及菜单项的点击事件监听
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularFloatingMenu);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CircularFloatingMenu_radius, -1);
        if (mRadius == -1) {
            mRadius = getResources().getDimensionPixelSize(R.dimen.CFMDefaultRadius);
        }
        mStartDegree = typedArray.getInteger(R.styleable.CircularFloatingMenu_startDegrees, -1);
        if (mStartDegree == -1) {
            mStartDegree = getResources().getInteger(R.integer.CFMDefaultStartDegrees);
        }
        mDegrees = typedArray.getInteger(R.styleable.CircularFloatingMenu_degrees, -1);
        if (mDegrees == -1) {
            mDegrees = getResources().getInteger(R.integer.CFMDefaultDegrees);
        }
        mIsItemClickClose = typedArray.getBoolean(R.styleable.CircularFloatingMenu_isCloseWhenItemClick, false);
        typedArray.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        if (mInited) {
            return;
        }
        mInited = true;
        mItemCount = getChildCount() - 1;
        Log.w(TAG, "init itemCount:" + mItemCount);
        if (mItemCount < 2) {
            return;
        }
        for (int i = 1; i < mItemCount; i++) {
            if(i!=mItemCount) {
                View item = getChildAt(i);
                item.setTag(i);
                item.setOnClickListener(mClickListener);
                ViewHelper.setAlpha(item, defaultAlpha);
            }
        }
        mPerDegree = mDegrees * 1.0f / (mItemCount - 2);
        mvBackgroud=getChildAt(0);

        ViewHelper.setAlpha(mvBackgroud,0);
        mVMenu = ((FrameLayout)getChildAt(mItemCount)).getChildAt(1);
        mVMenu.setOnClickListener(mMenuClickListener);
    }




    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
            }
            if (mIsItemClickClose) {
                mMenuClickListener.onClick(mVMenu);
            }
        }
    };

    OnClickListener mMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (!mIsOpen) {
                for (int i = 1; i < mItemCount; i++) {
                    int x = (int) (mRadius * Math.cos((mStartDegree + mPerDegree * (i-1)) * Math.PI / 180));
                    int y = (int) (mRadius * Math.sin((mStartDegree + mPerDegree * (i-1)) * Math.PI / 180));
                    View item = getChildAt(i);
                    ViewHelper.setRotation(item, defaultRotation);
                    ViewHelper.setAlpha(item, defaultAlpha);
                    ViewPropertyAnimator.animate(item).translationX(x).translationY(y).rotation(0).alpha(1)
                            .setInterpolator(inInterpolator).setDuration(500).start();
                }
                ViewPropertyAnimator.animate(v).rotation(ViewHelper.getRotation(v) + 90).setDuration(500).start();
                ViewPropertyAnimator.animate(mvBackgroud).alpha(1).setDuration(500).start();
                mvBackgroud.setClickable(true);
                mvBackgroud.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMenuClickListener.onClick(mVMenu);
                    }
                });
            } else {
                for (int i = 1; i < mItemCount; i++) {
                    View item = getChildAt(i);
                    ViewHelper.setRotation(item, defaultRotation);
                    ViewPropertyAnimator.animate(item).translationX(0).translationY(0).rotation(0).alpha(defaultAlpha)
                            .setInterpolator(outInterpolator).setDuration(500).start();
                }
                ViewPropertyAnimator.animate(v).rotation(ViewHelper.getRotation(v)+90).setDuration(500).start();
                ViewPropertyAnimator.animate(mvBackgroud).alpha(0f).setDuration(500).start();
                mvBackgroud.setClickable(false);
            }
            mIsOpen = !mIsOpen;
            if (mOnItemClickListener == null) {
                return;
            }
        }
    };

    public interface OnItemClickListener {
        void onItemClick(View view, int index);
    }
}
