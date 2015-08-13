package com.example.catdog.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by MyeongJun on 2015. 8. 9..
 */
public class CustomMapView extends View {
    public CustomMapView(Context context) {
        super(context);
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =0;
        switch (heightMode){
            case MeasureSpec.UNSPECIFIED: heightSize = heightMeasureSpec;break;
            case MeasureSpec.AT_MOST: heightSize = MeasureSpec.getSize(heightMeasureSpec);break;
            case MeasureSpec.EXACTLY: heightSize = MeasureSpec.getSize(heightMeasureSpec);break;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =0;
        switch (widthMode){
            case MeasureSpec.UNSPECIFIED: widthSize = widthMeasureSpec;break;
            case MeasureSpec.AT_MOST: widthSize = MeasureSpec.getSize(widthMeasureSpec);break;
            case MeasureSpec.EXACTLY: widthSize = MeasureSpec.getSize(widthMeasureSpec);break;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void onDraw(Canvas canvas){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        canvas.drawColor(Color.BLUE);
    }
}
