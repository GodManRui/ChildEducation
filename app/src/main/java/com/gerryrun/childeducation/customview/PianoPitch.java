package com.gerryrun.childeducation.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

public class PianoPitch extends View {

    private Context mContext;

    //最多支持5个手指弹奏
    public static final int MAX_FINGERS = 5;
    //设置5个黑色的按键
//    public static final int BLACK_KEYS_COUNT = 5;
    // 7个白色按键
    public static final int WHITE_KEYS_COUNT = 8;

    //黑色按钮的宽比
    public static final float BLACK_TO_WHITE_WIDTH_RATIO = 0.625f;
    //黑色按钮的高比
    public static final float BLACK_TO_WHITE_HEIGHT_RATIO = 0.58f;
    //设置最多支持5个手指弹奏
    private Point[] mFingerPoints = new Point[MAX_FINGERS];
    private int[] mFingerTones = new int[MAX_FINGERS];
    //默认的颜色和按下的颜色(白色按下、黑色按下)
    private Paint mWhiteKeyPaint, mWhiteKeyHitPaint,
            mBlackKeyPaint, mBlackKeyHitPaint;
    //设置音符的按键
    private Paint mCKeyPaint, mCSharpKeyPaint, mDKeyPaint,
            mDSharpKeyPaint, mEKeyPaint, mFKeyPaint,
            mFSharpKeyPaint, mGKeyPaint, mGSharpKeyPaint,
            mAKeyPaint, mASharpKeyPaint, mBKeyPaint;
    private Rect mCKey = new Rect(), mCSharpKey = new Rect(),
            mDKey = new Rect(), mDSharpKey = new Rect(),
            mEKey = new Rect(), mFKey = new Rect(),
            mFSharpKey = new Rect(), mGKey = new Rect(),
            mGSharpKey = new Rect(), mAKey = new Rect(),
            mASharpKey = new Rect(), mBKey = new Rect();


    private MotionEvent.PointerCoords mPointerCoords;

    public PianoPitch(Context context) {
        super(context);
        mContext = context;
    }

    public PianoPitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PianoPitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPointerCoords = new MotionEvent.PointerCoords();
        Arrays.fill(mFingerPoints, null);
        Arrays.fill(mFingerTones, -1);
        setupPaints();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    private void setupPaints() {
        mWhiteKeyPaint = new Paint();
        mWhiteKeyPaint.setStyle(Paint.Style.STROKE);
        mWhiteKeyPaint.setColor(Color.BLACK);
        mWhiteKeyPaint.setStrokeWidth(3);
        mWhiteKeyPaint.setAntiAlias(true);
        mCKeyPaint = mWhiteKeyPaint;
        mDKeyPaint = mWhiteKeyPaint;
        mEKeyPaint = mWhiteKeyPaint;
        mFKeyPaint = mWhiteKeyPaint;
        mGKeyPaint = mWhiteKeyPaint;
        mAKeyPaint = mWhiteKeyPaint;
        mBKeyPaint = mWhiteKeyPaint;

        mWhiteKeyHitPaint = new Paint(mWhiteKeyPaint);
        mWhiteKeyHitPaint.setColor(Color.LTGRAY);
        mWhiteKeyHitPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mBlackKeyPaint = new Paint();
        mBlackKeyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBlackKeyPaint.setColor(Color.BLACK);
        mBlackKeyPaint.setAntiAlias(true);
        mCSharpKeyPaint = mBlackKeyPaint;
        mDSharpKeyPaint = mBlackKeyPaint;
        mFSharpKeyPaint = mBlackKeyPaint;
        mGSharpKeyPaint = mBlackKeyPaint;
        mASharpKeyPaint = mBlackKeyPaint;

        mBlackKeyHitPaint = new Paint(mBlackKeyPaint);
        mBlackKeyHitPaint.setColor(Color.DKGRAY);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //获取屏幕大小
        int width = getWidth();
        int height = getHeight();
        //计算每个按键的大小
        int whiteKeyWidth = width / WHITE_KEYS_COUNT;
        int blackKeyWidth = (int) (whiteKeyWidth * BLACK_TO_WHITE_WIDTH_RATIO);
        int blackKeyHeight = (int) (height * BLACK_TO_WHITE_HEIGHT_RATIO);

        mCKey.set(0 * whiteKeyWidth, 0, 1 * whiteKeyWidth, height);
        mDKey.set(1 * whiteKeyWidth, 0, 2 * whiteKeyWidth, height);
        mEKey.set(2 * whiteKeyWidth, 0, 3 * whiteKeyWidth, height);
        mFKey.set(3 * whiteKeyWidth, 0, 4 * whiteKeyWidth, height);
        mGKey.set(4 * whiteKeyWidth, 0, 5 * whiteKeyWidth, height);
        mAKey.set(5 * whiteKeyWidth, 0, 6 * whiteKeyWidth, height);
        mBKey.set(6 * whiteKeyWidth, 0, 7 * whiteKeyWidth, height);

        mCSharpKey.set(1 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                1 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mDSharpKey.set(2 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                2 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mFSharpKey.set(4 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                4 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mGSharpKey.set(5 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                5 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mASharpKey.set(6 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                6 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制白色按键
        canvas.drawRect(mCKey, mCKeyPaint);
        canvas.drawRect(mDKey, mDKeyPaint);
        canvas.drawRect(mEKey, mEKeyPaint);
        canvas.drawRect(mFKey, mFKeyPaint);
        canvas.drawRect(mGKey, mGKeyPaint);
        canvas.drawRect(mAKey, mAKeyPaint);
        canvas.drawRect(mBKey, mBKeyPaint);

        //绘制黑色按键，这里黑色按键会在白色按键上面
        canvas.drawRect(mCSharpKey, mCSharpKeyPaint);
        canvas.drawRect(mDSharpKey, mDSharpKeyPaint);
        canvas.drawRect(mFSharpKey, mFSharpKeyPaint);
        canvas.drawRect(mGSharpKey, mGSharpKeyPaint);
        canvas.drawRect(mASharpKey, mASharpKeyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {/*
        //记录用了多少个手指头来点击屏幕
        int pointerCount = event.getPointerCount();
        //有多个手指头点击屏幕，在此判断。
        //如果当前手指数量大于设置的最大数量
        int cappedPointerCount = pointerCount > MAX_FINGERS ? MAX_FINGERS : pointerCount;
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int id = event.getPointerId(actionIndex);
        //检查是否收到了手指的按下或者抬起的动作
        if ((action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN)
                && id < MAX_FINGERS) {
            mFingerPoints[id] = new Point((int) event.getX(actionIndex),
                    (int) event.getY(actionIndex));
        } else if ((action == MotionEvent.ACTION_POINTER_UP ||
                action == MotionEvent.ACTION_UP)
                && id < MAX_FINGERS) {
            mFingerPoints[id] = null;
            invalidateKey(mFingerTones[id]);
            mFingerTones[id] = -1;

        }
        for (int i = 0; i < cappedPointerCount; i++) {
            int index = event.findPointerIndex(i);
            if (mFingerPoints[i] != null && index != -1) {
                mFingerPoints[i].set((int) event.getX(index),
                        (int) event.getY(index));
                int tone = getToneForPoint(mFingerPoints[i]);
                if (tone != mFingerTones[i] && tone != -1) {
                    invalidateKey(mFingerTones[i]);
                    mFingerTones[i] = tone;
                    invalidateKey(mFingerTones[i]);
                    if (!isKeyDown(i)) {
                        event.getPointerCoords(index, mPointerCoords);
                        Toast.makeText(mContext, "***" + mFingerTones[i], Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }
        updatePaints();*/
        return true;
    }
}