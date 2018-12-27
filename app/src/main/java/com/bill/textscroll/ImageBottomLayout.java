package com.bill.textscroll;

import android.content.Context;
import android.graphics.RectF;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Bill on 2017/4/12.
 * 仿网易新闻图片新闻详情底部Layout
 */

public class ImageBottomLayout extends LinearLayout {

    private int minLength;

    private float lastY = 0;
    private int top;
    private int bottom;
    private TextView mContentView;
    private TextView mTitleView;
    private TextView pageIndex;
    private TextView pageSize;
    private int botTop = 0;
    private int botBottom = 0;
    private int maxBottom;

    private boolean isTitleViewPress = false;

    public ImageBottomLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public ImageBottomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ImageBottomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    /**
     * 初始化View
     *
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {
        minLength = (int) context.getResources().getDimension(R.dimen.content_min_height);

        LayoutInflater inflater = LayoutInflater.from(context);
        View mView = inflater.inflate(R.layout.bottom_layout, this, true);

        mTitleView = mView.findViewById(R.id.act_image_title);
        mContentView = mView.findViewById(R.id.bot_content);
        pageIndex = mView.findViewById(R.id.act_img_index);
        pageSize = mView.findViewById(R.id.act_img_size);

        mContentView.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    /**
     * 设置页数
     *
     * @param index
     * @param size
     */
    public void setPage(String index, String size) {
        pageIndex.setText(index);
        pageSize.setText(size);
    }

    /**
     * 设置内容
     *
     * @param content
     */
    public void setContent(final String content) {
        mContentView.setText(content + "\n");

        mContentView.scrollTo(0, 0);

        //添加全局布局侦听器
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                maxBottom = getBottom();

                int minHeight = minLength;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    minHeight = mContentView.getMinHeight();
                }
                botTop = getTop() + (mContentView.getHeight() - minHeight);
                botBottom = getBottom() + (mContentView.getHeight() - minHeight);
                layout(getLeft(), botTop, getRight(), botBottom);

                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 计算指定的 View 在屏幕中的坐标。
     */
    public static RectF calcViewScreenLocation(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }

    /**
     * 分发事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTitleViewPress = false;
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动的距离
                float distanceY = event.getRawY() - lastY;
                boolean isMove = true;
                if (distanceY > 0) {
                    RectF rect = calcViewScreenLocation(mTitleView);
                    boolean isInViewRect = rect.contains(event.getRawX(), event.getRawY());
                    if (isTitleViewPress || isInViewRect) {
                        isTitleViewPress = true;
                        isMove = true;
                    } else if (mContentView.getScrollY() > 0) {
                        isMove = false;
                    }
                }

                if (isMove) {
                    top = (int) (getTop() + distanceY);
                    bottom = (int) (getBottom() + distanceY);
                    if (top < 0) {
                        top = 0;
                        bottom = getHeight();
                    }
                    if (bottom > botBottom) {
                        bottom = botBottom;
                        top = botBottom - getHeight();
                    }

                    if (bottom < maxBottom) {
                        bottom = maxBottom;
                        top = maxBottom - getHeight();
                    }
                }

                layout(getLeft(), top, getRight(), bottom);

                lastY = event.getRawY();


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTitleViewPress = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

}
